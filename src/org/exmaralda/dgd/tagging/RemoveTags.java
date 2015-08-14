/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.tagging;

import java.io.File;
import java.io.FilenameFilter;
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
public class RemoveTags {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if ((args.length!=2)){
            System.out.println("usage: removeTags inputDirectory ouputDirectory");
            System.exit(1);
        }
        try {
            new RemoveTags().doit(args);
        } catch (IOException ex) {
            Logger.getLogger(RemoveTags.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(RemoveTags.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit(String[] args) throws IOException, JDOMException {
        String inputDir = args[0];
        String outputDir = args[1];
        File in = new File(inputDir);
        File out = new File(outputDir); 
        out.mkdir();
        for (File f : out.listFiles()){
            f.delete();
        }        
        File[] transcriptFiles = in.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".fln"));
            }               
        });
        
        
        System.out.println("Found " + transcriptFiles.length + " OrthoNormal transcripts. ");
        for (File transcript : transcriptFiles){
            System.out.println("Removing tags in " + transcript.getName());
            Document d = FileIO.readDocumentFromLocalFile(transcript.getAbsolutePath());
            List l = XPath.newInstance("//w").selectNodes(d);
            for (Object o : l){
                Element e = (Element)o;
                e.removeAttribute("n");
                e.removeAttribute("pos");
                e.removeAttribute("lemma");
            }
            FileIO.writeDocumentToLocalFile(new File(out,transcript.getName()).getAbsolutePath(), d);
        }
        
        
    }
}
