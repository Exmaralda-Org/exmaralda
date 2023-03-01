/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import static org.exmaralda.tagging.TreeTaggableOrthonormalTranscription.XPATH_NO_XY;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class MakeTrainingFile {

    static String INPUT_DIRECTORY = "Z:\\TAGGING\\TRAINING\\kleiner_Goldstandard";
    static String OUTPUT_FILE = "Z:\\TAGGING\\TRAINING\\trainingdata.txt";
    
    File inputDirectory;
    File outputFile;
    public String xpathToTokens = XPATH_NO_XY;
    
    public MakeTrainingFile(String in, String out) {
        inputDirectory = new File(in);
        outputFile = new File(out);
    }
    
    public void setXPathToTokens(String xp){
        xpathToTokens = xp;
    }
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MakeTrainingFile mtl = new MakeTrainingFile(INPUT_DIRECTORY, OUTPUT_FILE);
        if (args.length==0){
            // DEFAULT
        } else if(args.length==2){
            mtl = new MakeTrainingFile(args[0], args[1]);            
        } else if (args.length==3) {
            mtl = new MakeTrainingFile(args[0], args[1]);
            if ("ALL".equals(args[2])){
                mtl.setXPathToTokens(TreeTaggableOrthonormalTranscription.XPATH_ALL_WORDS_AND_PUNCTUATION);
            }
            if ("NO_DUMMIES".equals(args[2])){
                mtl.setXPathToTokens(TreeTaggableOrthonormalTranscription.XPATH_NO_DUMMIES);
            }            
        }
            
        try {
            mtl.doit();
        } catch (JDOMException ex) {
            Logger.getLogger(MakeTrainingFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MakeTrainingFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        StringBuilder misMatches = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        File[] files = inputDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".fln");
            }            
        });
        for (File f : files){
            System.out.println("Reading " + f.getAbsolutePath());
            Document doc = FileIO.readDocumentFromLocalFile(f);
            // changed 09-12-2015
            List contributions = XPath.selectNodes(doc,"//contribution[" + xpathToTokens + "]");
            for (Object o : contributions){
                Element contribution = (Element)o;
                // changed 09-12-2015
                //List tokens = XPath.selectNodes(contribution, "descendant::w");
                List tokens = XPath.selectNodes(contribution, xpathToTokens);
                for (Object o2 : tokens){
                    Element token = (Element)o2;
                    String n = token.getAttributeValue("n");
                    if (n==null){
                        // 01-03-2023, issue #340
                        n = WordUtilities.getWordText(token, true);
                    }
                    String pos = token.getAttributeValue("pos");
                    String lemma = token.getAttributeValue("lemma");

                    String[] ns = n.split(" ");
                    String[] poss = pos.split(" ");
                    String[] lemmas = lemma.split(" ");

                    if (!(ns.length==poss.length && poss.length==lemmas.length)){
                        misMatches.append(f.getName()).append(IOUtilities.elementToString(token)).append("\n");
                    } else {
                        for (int i=0; i<ns.length; i++){
                            sb.append(ns[i]);
                            sb.append("\t");
                            sb.append(poss[i]);
                            sb.append("\n");
                        }
                    }
                }
                sb.append(".\t$.\n");
                
            }
        }
        
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(sb.toString().getBytes());       
        fos.close();         
        
        System.out.println("Output written to " + outputFile);
        
        if (misMatches.length()>0){
            System.out.println("========================");
            System.out.println("Mismatches:");
            System.out.println(misMatches.toString());
        }
        

        
    }

}
