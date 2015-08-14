/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class CompareDirectories {

    static String MANUAL_DIRECTORY = "C:\\Users\\Schmidt\\Desktop\\FOLK\\FOLK-Normal\\transcripts\\0a-Training_Evaluation\\Evaluation";
    static String AUTO_DIRECTORY = "C:\\Users\\Schmidt\\Desktop\\FOLK\\FOLK-Normal\\transcripts\\0a-Training_Evaluation\\Test";
    public static String OUTPUT_PATH = "C:\\Users\\Schmidt\\Desktop\\FOLK\\FOLK-Normal\\Compare_Threshhold_5.xml";

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new CompareDirectories().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(CompareDirectories.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CompareDirectories.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        Element rootElement = new Element("auto-normalization-evaluation");
        Document outputDocument = new Document(rootElement);
        File[] flns = new File(MANUAL_DIRECTORY).listFiles(ResetDatabaseLexicon.FLN_FILTER);
        for (File f : flns){
           System.out.println("Reading " + f.getAbsolutePath());
           Document manual_doc = FileIO.readDocumentFromLocalFile(f);
           File otherFile = new File(new File(AUTO_DIRECTORY), f.getName());
           System.out.println("Comparing to " + otherFile.getAbsolutePath());
           Document auto_doc = FileIO.readDocumentFromLocalFile(otherFile);

           List normalizedWords = XPath.newInstance("//w[@n]").selectNodes(manual_doc);
           int countAll = normalizedWords.size();
           int countNormalized = 0;
           int countCorrect = 0;
           for (Object o : normalizedWords){
               Element manual_w = (Element)o;
               String id = manual_w.getAttributeValue("id");
               Element auto_w = (Element)(XPath.newInstance("//w[@id='" + id + "']").selectSingleNode(auto_doc));
               System.out.println(manual_w.getAttributeValue("n") + "\t" + auto_w.getAttributeValue("n"));
               if (auto_w.getAttribute("n")!=null){
                   countNormalized++;
                   if (auto_w.getAttributeValue("n").equals(manual_w.getAttributeValue("n"))){
                       countCorrect++;
                   }
               }
               auto_w.setAttribute("n_correct", manual_w.getAttributeValue("n"));
           }
           
           FileIO.writeDocumentToLocalFile(otherFile, auto_doc);

           normalizedWords = XPath.newInstance("//w[@n]").selectNodes(auto_doc);
           int countAllInTarget = normalizedWords.size();
           int countNormalizedOnlyInTarget = 0;
           for (Object o : normalizedWords){
               Element auto_w = (Element)o;
               String id = auto_w.getAttributeValue("id");
               Element manual_w = (Element)(XPath.newInstance("//w[@id='" + id + "']").selectSingleNode(manual_doc));
               if (manual_w.getAttribute("n")==null){
                   countNormalizedOnlyInTarget++;
               }
           }
           
           
           Element thisEvaluation = new Element("evaluation");
           rootElement.addContent(thisEvaluation);
           thisEvaluation.setAttribute("normalizedWordsInSource", Integer.toString(countAll));
           thisEvaluation.setAttribute("normalizedWordsInTarget", Integer.toString(countAllInTarget));
           thisEvaluation.setAttribute("normalizedInTarget", Integer.toString(countNormalized));
           thisEvaluation.setAttribute("normalizedCorrectlyInTarget", Integer.toString(countCorrect));
           thisEvaluation.setAttribute("normalizedOnlyInTarget", Integer.toString(countNormalizedOnlyInTarget));
           
        }
        
       FileIO.writeDocumentToLocalFile(OUTPUT_PATH, outputDocument); 
        
        
    }

}
