/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.alignment;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class PrepareBW_BBAW {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new PrepareBW_BBAW().doit();
        } catch (Exception ex) {
            Logger.getLogger(PrepareBW_BBAW.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    String IN_DIRECTORY = "C:\\Users\\Schmidt\\Dropbox\\IDS\\AGD\\BW\\2-BBAW-NO_NS";
    String OUT_DIRECTORY = "C:\\Users\\Schmidt\\Dropbox\\IDS\\AGD\\BW\\3-BBAW-SPLIT";

    private void doit() throws JDOMException, IOException {
        File in = new File(IN_DIRECTORY);
        File out = new File(OUT_DIRECTORY);
        out.mkdir();
        File[] in_files = in.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }            
        });
        
        for (File f : in_files){
            System.out.println("Processing " + f.getName());
            Document doc = FileIO.readDocumentFromLocalFile(f);            
            
            List l = XPath.newInstance("//text/body/div[1]/u").selectNodes(doc);          
            System.out.println(l.size() + " utterances. ");
            for (Object o : l){
                Element u = (Element)o;
                List l2 = u.removeContent();
                Element seg = new Element("seg");
                int count=0;
                int remaining = l2.size();
                for (Object o2 : l2){
                    Content c = (Content)o2;
                    if ((c instanceof Element) && (((Element)c).getName().equals("pause")) && count>20 && remaining>20){
                        u.addContent(seg);
                        seg = new Element("seg");
                        count=0;
                    }
                    seg.addContent(c);
                    count++;
                    remaining--;
                }
                u.addContent(seg);
            }
            
            File out_file = new File(out,f.getName());
            FileIO.writeDocumentToLocalFile(out_file, doc);
        }
        
    }
    
    
    
}
