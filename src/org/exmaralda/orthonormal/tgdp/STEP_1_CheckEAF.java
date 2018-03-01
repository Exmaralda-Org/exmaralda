/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.tgdp;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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
public class STEP_1_CheckEAF extends AbstractEAFProcessor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            STEP_1_CheckEAF x = new STEP_1_CheckEAF();
            x.doit();
            FileIO.writeDocumentToLocalFile(new File("F:\\Dropbox\\IDS\\AGD\\Sprachinseln\\GOLD_STANDARD\\TGDP\\interviews\\TierInventory.xml"), x.result);
            for (String type : x.alltypes){
                System.out.println(type);
            }
                
        } catch (IOException ex) {
            Logger.getLogger(STEP_1_CheckEAF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    HashSet<String> alltypes = new HashSet<String>();
    Document result = new Document(new Element("TGDP-Tiers"));

    @Override
    public void processFile(File eafFile) throws IOException {
        Element fileElement = new Element("transcription");
        fileElement.setAttribute("file", eafFile.getAbsolutePath());
        result.getRootElement().addContent(fileElement);
        try {
            org.jdom.Document doc = FileIO.readDocumentFromLocalFile(eafFile);
            java.util.List tiers = XPath.selectNodes(doc, "//TIER");
            System.out.println(tiers.size() + " tiers");
            for (Object o : tiers){
                /*<TIER DEFAULT_LOCALE="en" LINGUISTIC_TYPE_REF="Default" PARTICIPANT="001"
                    TIER_ID="Interviewer 1">*/
                Element e = (Element)o;
                e.removeContent();
                e.detach();
                fileElement.addContent(e);
                //alltypes.add(e.getAttributeValue("LINGUISTIC_TYPE_REF"));
                System.out.println(e.getAttributeValue("TIER_ID") + " --- " + e.getAttributeValue("PARENT_REF"));
            }
        } catch (JDOMException ex) {
            throw new IOException(ex);
        }
            
        
    }
    
}
