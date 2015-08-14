/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class TranscriptRecordingMapFOLK {

    Hashtable<String, String> transcript2recording = new Hashtable<String,String>();
    String directory;
    
    public TranscriptRecordingMapFOLK(String directory) {
        Element re = new Element("metadata");
        this.directory = directory;
    }
    
    void doit() throws JDOMException, IOException{
        File dir = new File(directory);
        File[] files = dir.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                //ZW--_E_05654.xml
                return name.matches("[A-Z][A-Z]([A-Z]|-){2}_E_\\d{5}\\.xml");
            }
            
        });
        
        for (File f : files){
            System.out.println("Reading " + f.getAbsolutePath());
            try {
                Document xmlDoc = FileIO.readDocumentFromLocalFile(f);
                if (!(xmlDoc.getRootElement().getName().equals("Ereignis"))) {
                    continue;
                }
                List transcripts = XPath.newInstance("//Transkript").selectNodes(xmlDoc);
                for (Object o : transcripts){
                    Element transcript = (Element)o;
                    String transcriptID = transcript.getAttributeValue("Kennung");
                    // <Relation_zu_SE-Aufnahme Kennung_SE-Aufnahme="FOLK_E_00011_SE_01_A_02">
                    String recordingID = transcript.getChild("Basisdaten").getChild("Relation_zu_SE-Aufnahme").getAttributeValue("Kennung_SE-Aufnahme");
                    System.out.println(transcriptID + " --> " + recordingID);
                    transcript2recording.put(transcriptID, recordingID);
                }
            } catch (FileNotFoundException fnfe){
                System.out.println("File does not exist");
            }
            
        }
    }

        
    
    static String INPUT_DIRECTORY = "O:\\xml\\events\\Werkstatt\\folk";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            TranscriptRecordingMapFOLK tabulator = new TranscriptRecordingMapFOLK(INPUT_DIRECTORY);
            tabulator.doit();
        } catch (JDOMException ex) {
            Logger.getLogger(TranscriptRecordingMapFOLK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TranscriptRecordingMapFOLK.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }

}
