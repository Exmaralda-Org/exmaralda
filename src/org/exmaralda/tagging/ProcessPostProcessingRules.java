/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.corpusbuild.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class ProcessPostProcessingRules {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {    
            try {
                new ProcessPostProcessingRules().doit();
            } catch (JDOMException ex) {
                Logger.getLogger(ProcessPostProcessingRules.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(ProcessPostProcessingRules.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, JDOMException {
        HashSet<String> allTags = new HashSet<String>();
        HashSet<String> doubleTags = new HashSet<String>();
        String TXT_DIR = "C:\\EXMARaLDA_GIT\\exmaralda\\src\\org\\exmaralda\\tagging\\closedlists";
        Element root = new Element("rules");
        File[] txt_files = new File(TXT_DIR).listFiles();
        for (File f : txt_files){
            //System.out.println("Processing " + f.getName());
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String nextLine = "";
            while ((nextLine = br.readLine()) != null) {
                // ignore multiwords and starred entries
                if (nextLine.trim().length()==0 || nextLine.trim().contains(" ")){
                } else {
                    String word = nextLine.trim();
                    if (word.contains("*")){
                        word = word.replaceAll("\\*", "");
                    }
                    if (allTags.contains(word)){
                        System.out.print("\"" + word + "\",");
                        doubleTags.add(word);
                    } 
                    allTags.add(word);   
                    
                }
            }            
            br.close();
            
        }
        
        for (File f : txt_files){
            //System.out.println("Processing " + f.getName());
            ArrayList<String> allLines = new ArrayList<String>();
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String nextLine = "";
            while ((nextLine = br.readLine()) != null) {
                if (nextLine.trim().length()==0){
                    // ignore
                } else if (nextLine.trim().contains(" ") || nextLine.contains("*")) {
                    allLines.add(nextLine);
                } else {
                    String word = nextLine.trim();
                    if (doubleTags.contains(word)){
                        allLines.add("*" + word);
                    } else {
                        allLines.add(word);
                    }                    
                }                
            }
            br.close();
            
            FileOutputStream fos = new FileOutputStream(f);
            for (String line : allLines){
                fos.write(line.getBytes());
                fos.write("\n".getBytes());
            }
            fos.close();
            System.out.println("document written.");
        }
            
            

        
        String STABLE = "C:\\EXMARaLDA_GIT\\exmaralda\\src\\org\\exmaralda\\tagging\\PostProcessingRulesFOLK_STABLE.xml";
        Document stableRulesDoc = FileIO.readDocumentFromLocalFile(STABLE);
        List c = stableRulesDoc.getRootElement().removeContent();
        root.addContent(c);
        
        String OUTPUT = "C:\\EXMARaLDA_GIT\\exmaralda\\src\\org\\exmaralda\\tagging\\PostProcessingRulesFOLK.xml";
        //FileIO.writeDocumentToLocalFile(OUTPUT, result);
    }
}
