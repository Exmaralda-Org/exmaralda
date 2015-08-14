/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
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
public class FOLKLevel3To2 {

    
    public FOLKLevel3To2(String in, String out) throws JDOMException, IOException {
        File[] flns = new File(in).listFiles();
        for (File f : flns){
           System.out.println("Reading " + f.getAbsolutePath());
           Document doc = FileIO.readDocumentFromLocalFile(f);
           
           List l = XPath.newInstance("//contribution[@parse-level='3']").selectNodes(doc);
           int count = 0;
           for (Object o : l){
               Element c = (Element)o;
               Element line = c.getChild("line");
               c.setAttribute("parse-level", "2");
               line.detach();
               c.addContent(line.removeContent());
               count++;
           }
            System.out.println(count + " contributions degraded");

           File outFile = new File(new File(out), f.getName());
           FileIO.writeDocumentToLocalFile(outFile, doc);
        }
    }


    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            FOLKLevel3To2 assignFOLKAudio = new FOLKLevel3To2(
                                                      "C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\1", 
                                                      "C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\2"
                                                      );
        } catch (JDOMException ex) {
            Logger.getLogger(FOLKLevel3To2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FOLKLevel3To2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
