/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk;

import java.io.File;
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
 * @author Schmidt
 */
public class CorrectSiglen {

    String[] TRANSCRIPTIONS = {"06", "11", "12", "13", "14", "15", "16", "17"};
    String[][] CORRECTIONS = {
        {"DA", "DK"},
        {"DI", "PL"},
        {"JA", "JZ"},
        {"MA", "MT"},
        {"PA", "PL"},
        {"SI", "SK"},
        {"ST", "SK"},
    };
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new CorrectSiglen().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(CorrectSiglen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CorrectSiglen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        for (String T : TRANSCRIPTIONS){
            File f = new File(new File("Z:\\FOLK_Transkripte_normalisiert\\DGD-Kennungen"), "FOLK_E_00021_SE_01_T_" + T + "_DF_01.fln");
            Document doc = FileIO.readDocumentFromLocalFile(f);
            System.out.println(f.getAbsolutePath() + " read.");
            for (String[] C : CORRECTIONS){
                String OLD = C[0];
                String NEW = C[1];
                Element speaker = (Element) XPath.selectSingleNode(doc, "//speaker[@speaker-id='" + OLD + "']");
                if (speaker!=null){
                    speaker.setAttribute("speaker-id", NEW);
                } 
                // =======================
                List l = XPath.selectNodes(doc, "//contribution[@speaker-reference='" + OLD + "']");
                System.out.println(l.size() + " contributions for speaker " + OLD);
                for (Object o : l){
                    Element contribution = (Element)o;
                    contribution.setAttribute("speaker-reference", NEW);
                }
            }
            FileIO.writeDocumentToLocalFile(f, doc);
        }
    }
}
