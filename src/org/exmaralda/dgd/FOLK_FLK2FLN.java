/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class FOLK_FLK2FLN {

    
    public FOLK_FLK2FLN(String in, String out) throws JDOMException, IOException {
        File[] flns = new File(in).listFiles();
        for (File f : flns){
           System.out.println("Reading " + f.getAbsolutePath());
           Document d = FileIO.readDocumentFromLocalFile(f);
           d.getRootElement().setAttribute("id", new GUID().makeID());
           Iterator i = d.getDescendants(new ElementFilter("w"));
           int count = 0;
           while (i.hasNext()){
               count++;
               Element wordElement = (Element)(i.next());
               wordElement.setAttribute("id", "w" + Integer.toString(count));
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
            FOLK_FLK2FLN assignFOLKAudio = new FOLK_FLK2FLN(
                                                      "C:\\Users\\Schmidt\\Desktop\\FOLK_Werkstatt\\1-FLN", 
                                                      "C:\\Users\\Schmidt\\Desktop\\FOLK_Werkstatt\\1a-FLN"
                                                      );
        } catch (JDOMException ex) {
            Logger.getLogger(FOLK_FLK2FLN.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FOLK_FLK2FLN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
