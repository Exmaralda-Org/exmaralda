/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.tagging;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.tagging.SextantOrthonormalIntegrator;
import org.exmaralda.tagging.TreeTaggableOrthonormalTranscription;
import org.exmaralda.tagging.TreeTagger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class RepairOriginals {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new RepairOriginals().doit("C:\\Users\\Schmidt\\Desktop\\FOLK-Normal\\transcripts\\0-Originale");
        } catch (IOException ex) {
            Logger.getLogger(RepairOriginals.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(RepairOriginals.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit(String dir) throws IOException, JDOMException {
        File in = new File(dir);
        File[] transcriptFiles = in.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".fln"));
            }               
        });
        System.out.println("Found " + transcriptFiles.length + " OrthoNormal transcripts. ");
        for (File transcript : transcriptFiles){
            System.out.println("Repairing " + transcript.getName());
            Document doc = FileIO.readDocumentFromLocalFile(transcript);
            for (Object o : XPath.selectNodes(doc, "//w")){
                Element w = (Element)o;
                w.removeAttribute("lemma");
                w.removeAttribute("pos");
            }
            FileIO.writeDocumentToLocalFile(transcript, doc);
        }
        
        
    }
}
