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
public class SIN2GAT {

    static String FILENAME = "Z:\\SiN-Datenspende\\OFL-WAR01\\OFL-WAR01-F.flk";
    static String OUTNAME = "Z:\\SiN-Datenspende\\OFL-WAR01\\OFL-WAR01-F_ToLower.flk";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new SIN2GAT().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(SIN2GAT.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SIN2GAT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        Document d = FileIO.readDocumentFromLocalFile(new File(FILENAME));
        List l = XPath.newInstance("//segment").selectNodes(d);
        for (Object o : l){
            Element segment = (Element)o;
            String text = segment.getText();
            segment.setText(text.toLowerCase().replaceAll("\\. *$", " ").replace("^ +", ""));
        }
        FileIO.writeDocumentToLocalFile(new File(OUTNAME), d);
    }
}
