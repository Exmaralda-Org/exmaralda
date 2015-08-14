/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class FindZWPseudos extends AbstractSchneiderProcessor {
    
    
    String TRANSCRIPT_PATH = "Y:\\thomas\\ZW_HE";

    String[] PSEUDOS = {"ZWY62",
                        "ZWY27",
                        "ZWY28",
                        "ZWY73",
                        "ZWY52",
                        "ZWV13",
                        "ZW9O7",
                        "ZW9O8",
                        "ZW9O9",
                        "ZW9P7",
                        "ZWL60",
                        "ZWL59",
                        "ZW8O9",
                        "ZW9F3",
                        "ZW983",
                        "ZW7I8",
                        "ZW7J0",
                        "ZW7Q4",
                        "ZW8B0",
                        "ZW7B8",
                        "ZW7B9",
                        "ZW5B1",
                        "ZW5U3",
                        "ZW4L0",
                        "ZW514",
                        "ZWY34",
                        "ZWY33",
                        "ZW498",
                        "ZW2C2",
                        "ZWF82",
                        "ZW2S6",
                        "ZWY29",
                        "ZWY30",
                        "ZW0P0",
                        "ZW0L3",
                        "ZW0M6",
                        "ZWY95",
                        "ZWY97",
                        "ZW0L4",
                        "ZWY89",
                        "HE116"};

    HashSet<String> pseudoSet = new HashSet<String>();
    
    public FindZWPseudos(){
        try {
            for (String p : PSEUDOS){
                pseudoSet.add(p);
            }
            super.getMappings("Y:\\thomas\\ZW_HE\\Meta\\ZW_HE_Metadata.xml");
        } catch (JDOMException ex) {
            Logger.getLogger(FindZWPseudos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FindZWPseudos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            FindZWPseudos aa = new FindZWPseudos();
            aa.doit();
            System.exit(0);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FindZWPseudos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FindZWPseudos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void processFiles() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
    }

    private void doit() throws FileNotFoundException, IOException {
        StringBuffer sb = new StringBuffer();
        for (String p : PSEUDOS){
            System.out.println(p + "-->" + old2new.get(p));
            sb.append("move " + "Y:\\media\\audio\\ZW_DGD_1.0\\umbenannt\\" +  old2new.get(p) + "_SE_01_A_02_DF_01.WAV" + " " 
                    + "Y:\\media\\audio\\ZW_DGD_1.0\\aussortiert2" + System.getProperty("line.separator"));                

        }
        
        this.logFile = new File(new File(TRANSCRIPT_PATH), "rename2.bat");
        this.writeLogToTextFile(sb);
        
    }
    
}
