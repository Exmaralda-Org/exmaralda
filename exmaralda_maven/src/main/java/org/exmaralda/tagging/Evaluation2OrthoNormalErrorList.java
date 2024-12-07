/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Thomas_Schmidt
 */
public class Evaluation2OrthoNormalErrorList {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new Evaluation2OrthoNormalErrorList().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(Evaluation2OrthoNormalErrorList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Evaluation2OrthoNormalErrorList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //String EVAL_DIR = "Z:\\TAGGING\\GOLD-STANDARD\\ORIGINAL\\train";
    //String TAGGED_DIR = "Z:\\TAGGING\\TRAINING\\20170124_TAGNEW\\TAGGING_OUTPUT\\ORIGINAL_ALL_TRAINING";
    //String OUT = "Z:\\TAGGING\\TRAINING\\20170124_TAGNEW\\Mismatches_ErrorList_TrainingData.xml";

    String EVAL_DIR = "Z:\\TAGGING\\GOLD-STANDARD\\ORIGINAL\\eval";
    String TAGGED_DIR = "Z:\\TAGGING\\TRAINING\\20170124_TAGNEW\\TAGGING_OUTPUT\\ORIGINAL_ALL";
    String OUT = "Z:\\TAGGING\\TRAINING\\20170124_TAGNEW\\Mismatches_ErrorList_EvaluationData.xml";
    
    private void doit() throws JDOMException, IOException {
        File[] evalFiles = new File(EVAL_DIR).listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".fln");
            }            
        });
        
        Document resultDoc = new Document(new Element("match-list"));
        
        for (File evalFile : evalFiles){
            System.out.println("Processing " + evalFile.getAbsolutePath());
            File correspondingFile = new File(new File(TAGGED_DIR), evalFile.getName());
            if (!(correspondingFile.exists())){
                System.out.println(correspondingFile + " does not exist");
                System.exit(1);
            }
            
            Document originalDoc = FileIO.readDocumentFromLocalFile(correspondingFile);
            Document correctedDoc = FileIO.readDocumentFromLocalFile(evalFile);
            Document mergedDoc = new Merge().merge(originalDoc, correctedDoc);
            
            List l = XPath.selectNodes(mergedDoc, "//w[@pos!=@pos_c and not(starts-with(@pos,'V') and starts-with(@pos_c,'V'))]");
            for (Object o : l){
                Element w = (Element)o;
                Element contribution = (Element) XPath.selectSingleNode(w, "ancestor::contribution");
                Element clonedContribution = (Element) contribution.clone();
                clonedContribution.setAttribute("match", w.getAttributeValue("id"));
                clonedContribution.setAttribute("transcript", evalFile.getName());
                clonedContribution.detach();                            
                resultDoc.getRootElement().addContent(clonedContribution);                                    
            }
        }
        
        FileIO.writeDocumentToLocalFile(new File(OUT), resultDoc);
    }
    

    
}
