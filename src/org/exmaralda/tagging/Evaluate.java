/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class Evaluate {

    File evaluationDataDirectory;
    File testDataDirectory;
    File outputDirectory;
    
    StylesheetFactory ssf = new StylesheetFactory(true);
    
    String[][] stylesheets = {
        {"Evaluate.xsl", "evaluate"},
        {"Evaluate_By_Corrected_Tag.xsl", "evaluate_by_tag"}, 
        {"Evaluate_By_Tag.xsl", "evaluate_by_corrected_tag"}, 
        {"Evaluate_Lemma.xsl", "evaluate_lemma"},
        {"List.xsl", "list"},
        {"Merged2HTML.xsl", "display"}
    };
    

    public Evaluate(File evalutationDataDirectory, File testDataDirectory, File outputDirectory) {
        this.evaluationDataDirectory = evalutationDataDirectory;
        this.testDataDirectory = testDataDirectory;
        this.outputDirectory = outputDirectory;
        outputDirectory.mkdir();
    }
    
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length!=3){
            System.out.println("Usage: Evaluate evaluation-data-directory test-data-directory output-directory");
            System.exit(0);
        }
        File edd = new File(args[0]);
        File tdd = new File(args[1]);
        File opd = new File(args[2]);
        Evaluate evaluate = new Evaluate(edd,tdd,opd);
        try {
            try {
                evaluate.doit();
            } catch (SAXException ex) {
                Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JDOMException ex) {
            Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        if (!evaluationDataDirectory.exists()){
            System.out.println(evaluationDataDirectory.getAbsolutePath() + " does not exist.");
            System.exit(1);
        }
        if (!testDataDirectory.exists()){
            System.out.println(testDataDirectory.getAbsolutePath() + " does not exist.");
            System.exit(1);
        }
        outputDirectory.mkdir();
        
        File[] evaluationFiles = evaluationDataDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".fln"));
            }               
        });
        
        HashMap<String,HashMap<String,Integer>> matrix = new HashMap<String,HashMap<String,Integer>>(); 
        Document overviewDocument = new Document(new Element("overview"));
        for (File evaluationFile : evaluationFiles){
            System.out.println("************************************************");
            System.out.println("Evaluating " + evaluationFile.getAbsolutePath());
            //FOLK_E_00124_SE_01_T_02_DF_01_S_1xxxx.fln
            final String prefix = evaluationFile.getName().substring(0,33);
            File[] testFileCandidates = testDataDirectory.listFiles(new FilenameFilter(){
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".fln") && name.startsWith(prefix);
                }                
            });
            if (testFileCandidates.length!=1){
                System.out.println("   No corresponding file found for " + prefix);
                System.out.println("************************************************");
                continue;
            }
            File testFile = testFileCandidates[0];
                System.out.println("   Corresponding file is " + testFile.getName());
            Document originalDoc = FileIO.readDocumentFromLocalFile(testFile);
            Document correctedDoc = FileIO.readDocumentFromLocalFile(evaluationFile);
            Document mergedDoc = new Merge().merge(originalDoc, correctedDoc);
            //pos / pos_c
            List l = XPath.selectNodes(mergedDoc, "//w");
            for (Object o : l){
                Element w = (Element)o;
                String allPos = w.getAttributeValue("pos");
                String allCorrectedPos = w.getAttributeValue("pos_c");
                if (allPos==null) continue;
                if (allCorrectedPos==null) continue;
                String[] posTokens = allPos.split(" ");
                String[] correctedPosTokens = allCorrectedPos.split(" ");
                int count=0;
                for (String pos : posTokens){
                    if (count>=correctedPosTokens.length) {
                        System.out.println("   Mismatch:" + w.getAttributeValue("id") + " " + allPos + " / " + allCorrectedPos);
                        continue;
                    }
                    String correctedPos = correctedPosTokens[count];
                    if (!(matrix.containsKey(pos))){
                        HashMap<String, Integer> thisOne = new HashMap<String, Integer>();
                        thisOne.put(correctedPos, 0);
                        matrix.put(pos, thisOne);
                    }
                    HashMap<String, Integer> thisOne = matrix.get(pos);
                    if (!thisOne.containsKey(correctedPos)){
                        thisOne.put(correctedPos, 1);
                    } else {
                        thisOne.put(correctedPos, thisOne.get(correctedPos)+1);
                    }
                    count++;
                }
            }
            
            
            String xml = IOUtilities.documentToString(mergedDoc);
            System.out.println("   Documents merged.");
            
            //Wörter insgesamt: <xsl:value-of select="count(//w)"/><br/>
            //Nicht vergleichbar: <xsl:value-of select="count(//w[not(@pos_c)])"/> (= <xsl:value-of select="count(//w[not(@pos_c)]) div count(//w) * 100"/>%)<br/>
            //Übereinstimmung: <xsl:value-of select="count(//w[@pos_c and @pos=@pos_c])"/> (= <xsl:value-of select="count(//w[@pos_c and @pos=@pos_c]) div count(//w[@pos_c]) * 100"/>%)<br/>
            //Abweichung <xsl:value-of select="count(//w[@pos_c and not(@pos=@pos_c)])"/> (= <xsl:value-of select="count(//w[@pos_c and not(@pos=@pos_c)]) div count(//w[@pos_c]) * 100"/>%)<br/> 
            //Übereinstimmung: (Superkategorie): <xsl:value-of select="count(//w[@pos_c and @super=@super_c])"/> (= <xsl:value-of select="count(//w[@pos_c and @super=@super_c]) div count(//w[@pos_c]) * 100"/>%)<br/>
            //Abweichung (Superkategorie): <xsl:value-of select="count(//w[@pos_c and not(@super=@super_c)])"/> (= <xsl:value-of select="count(//w[@pos_c and not(@super=@super_c)]) div count(//w[@pos_c]) * 100"/>%) 
            int totalWords = XPath.selectNodes(mergedDoc, "//w").size();
            int nonComparableWords = XPath.selectNodes(mergedDoc, "//w[not(@pos_c)]").size();
            int agreement = XPath.selectNodes(mergedDoc, "//w[@pos_c and @pos=@pos_c]").size();
            int disagreement = XPath.selectNodes(mergedDoc, "//w[@pos_c and not(@pos=@pos_c)]").size();
            int superAgreement = XPath.selectNodes(mergedDoc, "//w[@pos_c and @super=@super_c]").size();
            int superDisagreement = XPath.selectNodes(mergedDoc, "//w[@pos_c and not(@super=@super_c)]").size();
            Element thisOverview = new Element("file");
            thisOverview.setAttribute("name", testFile.getName());
            thisOverview.setAttribute("total-words", Integer.toString(totalWords));
            thisOverview.setAttribute("non-compare", Integer.toString(nonComparableWords));
            thisOverview.setAttribute("agree", Integer.toString(agreement));
            thisOverview.setAttribute("disagree", Integer.toString(disagreement));
            thisOverview.setAttribute("super-agree", Integer.toString(superAgreement));
            thisOverview.setAttribute("super-disagree", Integer.toString(superDisagreement));
            overviewDocument.getRootElement().addContent(thisOverview);
            
            for (String[] ss : stylesheets){
                System.out.println("   Applying stylesheet " + ss[1]);
                String html = ssf.applyInternalStylesheetToString("/org/exmaralda/tagging/" + ss[0], xml);
                // FOLK_E_00001_SE_01_T_01_DF_01
                //String number = testFile.getName().substring(7, 12);
                String number = testFile.getName();
                File out = new File(outputDirectory, number + "_" + ss[1] + ".html");
                FileOutputStream fos = new FileOutputStream(out);
                fos.write(html.getBytes("UTF-8"));
                fos.close();                
                System.out.println("   Output written to " + out.getName());
            }
            System.out.println("************************************************");
        }
        
        Element root = new Element("matrix");
        root.setAttribute("source", evaluationDataDirectory.getAbsolutePath());
        root.setAttribute("target", testDataDirectory.getAbsolutePath());
        Document matrixDocument = new Document(root);
        for (String pos : matrix.keySet()){
            Element source = new Element("source");
            source.setAttribute("pos", pos);
            HashMap<String, Integer> thisOne = matrix.get(pos);
            for (String pos_c : thisOne.keySet()){
                Element target = new Element("target");
                target.setAttribute("pos", pos_c);
                target.setAttribute("count", thisOne.get(pos_c).toString());       
                source.addContent(target);
            }
            root.addContent(source);
        }
        FileIO.writeDocumentToLocalFile(new File(outputDirectory, "matrix.xml"), matrixDocument);
        FileIO.writeDocumentToLocalFile(new File(outputDirectory, "overview.xml"), overviewDocument);
        
        String html = ssf.applyInternalStylesheetToString("/org/exmaralda/tagging/Matrix2HTML.xsl", IOUtilities.documentToString(matrixDocument));
        File out = new File(outputDirectory, "matrix.html");
        FileOutputStream fos = new FileOutputStream(out);
        fos.write(html.getBytes("UTF-8"));       
        fos.close();                
        
        String txt = ssf.applyInternalStylesheetToString("/org/exmaralda/tagging/Matrix2TXT.xsl", IOUtilities.documentToString(matrixDocument));
        File out2 = new File(outputDirectory, "matrix.txt");
        FileOutputStream fos2 = new FileOutputStream(out2);
        fos2.write(txt.getBytes("UTF-8"));       
        fos2.close();                
    }
}
