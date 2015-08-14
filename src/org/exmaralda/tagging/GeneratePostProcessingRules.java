/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
public class GeneratePostProcessingRules {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {    
            try {
                new GeneratePostProcessingRules().doit();
            } catch (JDOMException ex) {
                Logger.getLogger(GeneratePostProcessingRules.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(GeneratePostProcessingRules.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, JDOMException {
        String TXT_DIR = "C:\\EXMARaLDA_FRESHEST\\src\\org\\exmaralda\\tagging\\closedlists";
        Element root = new Element("rules");
        Document result = new Document(root);
        File[] txt_files = new File(TXT_DIR).listFiles();
        for (File f : txt_files){
            System.out.println("Processing " + f.getName());
            String tag = f.getName().substring(0,f.getName().indexOf("."));
            /* <rule>
                <!-- Häsitationspartikel -->
                <match field="n">äh</match>
                <set field="pos">PTKHES</set>
            </rule> */
            Element rule = new Element("rule");
            Element match = new Element("match");
            match.setAttribute("field", "n");
            Element set = new Element("set");
            set.setAttribute("field", "pos");
            set.setText(tag);
            rule.addContent(match);
            rule.addContent(set);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String regex = "(";
            String nextLine = new String();
            while ((nextLine = br.readLine()) != null) {
                // ignore multiwords and starred entries
                if (nextLine.trim().length()>0 && (!(nextLine.trim().contains(" ") || nextLine.contains("*")))){
                    regex+=nextLine.trim() + "|";
                }
            }            
            regex = regex.substring(0, regex.length()-1);
            regex+=")";
            match.setText(regex);
            if (!(")".equals(regex))){
                root.addContent(rule);
            }
            br.close();
            
        }
        String STABLE = "C:\\EXMARaLDA_FRESHEST\\src\\org\\exmaralda\\tagging\\PostProcessingRulesFOLK_STABLE.xml";
        Document stableRulesDoc = FileIO.readDocumentFromLocalFile(STABLE);
        List c = stableRulesDoc.getRootElement().removeContent();
        root.addContent(c);
        
        String OUTPUT = "C:\\EXMARaLDA_FRESHEST\\src\\org\\exmaralda\\tagging\\PostProcessingRulesFOLK.xml";
        FileIO.writeDocumentToLocalFile(OUTPUT, result);
    }
}
