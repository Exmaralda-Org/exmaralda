/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk;

import org.exmaralda.dgd.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
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
public class RemoveBookmarks {

    File transcriptionDirectory;

    public RemoveBookmarks(File transcriptionDirectory) {
        this.transcriptionDirectory = transcriptionDirectory;
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String transcriptPath = "C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\3";
            RemoveBookmarks as = new RemoveBookmarks(new File(transcriptPath));
            as.doit();
        } catch (JDOMException ex) {
            Logger.getLogger(AssignSpeakers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AssignSpeakers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void doit() throws JDOMException, IOException{
        File[] transcriptionFiles = transcriptionDirectory.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".fln");
            }
            
        });
        for (File transcriptionFile : transcriptionFiles){
            System.out.println("*********************************************");
            System.out.println(transcriptionFile.getName());
            System.out.println("*********************************************");
            Document transcriptionDocument = FileIO.readDocumentFromLocalFile(transcriptionFile);
            
            transcriptionDocument.getRootElement().getChild("head").removeContent();
            
            FileIO.writeDocumentToLocalFile(transcriptionFile, transcriptionDocument);
            
        }
    }

    
    
}
