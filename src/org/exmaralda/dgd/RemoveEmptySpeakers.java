/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class RemoveEmptySpeakers {

    
    public RemoveEmptySpeakers(String in, String out) throws JDOMException, IOException {
        File[] flns = new File(in).listFiles();
        for (File f : flns){
           System.out.println("Reading " + f.getAbsolutePath());
           Document d = FileIO.readDocumentFromLocalFile(f);
           List l = XPath.newInstance("//contribution[@speaker-reference=\"\"]").selectNodes(d);
           for (Object o : l){
               Element e = (Element)o;
               e.removeAttribute("speaker-reference");
               e.removeAttribute("speaker-dgd-id");
               
               System.out.println("Founf onr");
           }
           
           l = XPath.newInstance("//speaker[@speaker-id=\"\"]").selectNodes(d);
           for (Object o : l){
               Element e = (Element)o;
               e.detach();
           }
           
            
           
           
           File outFile = new File(new File(out), f.getName());
           FileIO.writeDocumentToLocalFile(outFile, d);
        }
    }


    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            RemoveEmptySpeakers assignFOLKAudio = new RemoveEmptySpeakers(
                                                      "C:\\Users\\Schmidt\\Desktop\\FOLK_Werkstatt\\7", 
                                                      "C:\\Users\\Schmidt\\Desktop\\FOLK_Werkstatt\\8"
                                                      );
        } catch (JDOMException ex) {
            Logger.getLogger(RemoveEmptySpeakers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RemoveEmptySpeakers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
