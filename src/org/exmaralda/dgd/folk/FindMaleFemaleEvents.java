/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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
public class FindMaleFemaleEvents {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new FindMaleFemaleEvents().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(FindMaleFemaleEvents.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FindMaleFemaleEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        String DIRECTORY = "O:\\xml\\events\\Werkstatt\\FOLK";
        File[] files = new File(DIRECTORY).listFiles();
        for (File file : files){
            Document doc = FileIO.readDocumentFromLocalFile(file);
            String beschreibung = ((Element)XPath.selectSingleNode(doc, "//Basisdaten/Beschreibung")).getTextNormalize();
            List l = XPath.newInstance("//Geschlecht").selectNodes(doc);
            HashSet<String> sexes = new HashSet<String>();
            for (Object o : l){
                Element e = (Element)o;
                sexes.add(e.getText());
            }
            if (sexes.size()>1) continue;
            for (String sex : sexes){
                System.out.print(sex + ";");
            }
            System.out.print(file.getName()+";" + beschreibung + ";");
            System.out.println("");
        }
    }
}
