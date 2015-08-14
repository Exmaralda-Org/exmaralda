/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.tagging;

import java.io.IOException;
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
public class RepairNonProbabilities {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new RepairNonProbabilities().doit("C:\\Users\\Schmidt\\FOLK_E_00001_SE_01_T_01_DF_01SW_110512.fln");
        } catch (IOException ex) {
            Logger.getLogger(RepairNonProbabilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(RepairNonProbabilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit(String file) throws IOException, JDOMException {
        System.out.println("Repairing " + file);
        Document doc = FileIO.readDocumentFromLocalFile(file);
        for (Object o : XPath.selectNodes(doc, "//w")){
                Element w = (Element)o;
                w.setAttribute("p-pos", "0.99");
        }
        FileIO.writeDocumentToLocalFile("C:\\Users\\Schmidt\\FOLK_E_00001_SE_01_T_01_DF_01SW_110512_Repariert_TS.fln", doc);
    }
        
        
}
