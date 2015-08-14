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
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class SpeakerNamesInTranscriptions {

    File metadataDirectory;
    File transcriptionDirectory;
    static Document logDocument;

    public SpeakerNamesInTranscriptions(File metadataDirectory, File transcriptionDirectory) {
        this.metadataDirectory = metadataDirectory;
        this.transcriptionDirectory = transcriptionDirectory;
        Element rootElement = new Element("speakerAssignments");
        logDocument = new Document(rootElement);
    }
    
    String speakerMetadataPath = "O:\\xml\\speakers\\final\\FOLK";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String metadataPath = "O:\\xml\\events\\Werkstatt\\FOLK";
            String transcriptPath = "C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\3";
            String logPath = "C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\FOLK_Speaker_Assignment3.xml";
            SpeakerNamesInTranscriptions as = new SpeakerNamesInTranscriptions(new File(metadataPath), new File(transcriptPath));
            as.doit();
        } catch (JDOMException ex) {
            Logger.getLogger(AssignSpeakers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AssignSpeakers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void doit() throws JDOMException, IOException{
        Document metaDataDocument = getMetaDataDocument();
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
            //PF--_E_00008_SE_01_T_01_DF_01.fln
            String speechEventKennung = transcriptionFile.getName().substring(0,18);
            String transcriptKennung = transcriptionFile.getName().substring(0, transcriptionFile.getName().indexOf("."));            
            HashMap<String, String> speakersMap = mapSpeakers(transcriptionDocument, metaDataDocument, speechEventKennung, transcriptKennung);
            
            List l = XPath.selectNodes(transcriptionDocument, "//speaker");
            for (Object o : l){
                Element s = (Element)o;
                String id = s.getAttributeValue("speaker-id");
                String kennung = speakersMap.get(id);
                String currentName = s.getChildText("name");
                if (kennung!=null && !(kennung.equals("???"))){
                    File speakerMetaFile = new File(new File(speakerMetadataPath), kennung + ".xml");
                    Document speakerDoc = FileIO.readDocumentFromLocalFile(speakerMetaFile);
                    String pseudo = ((Element)XPath.selectSingleNode(speakerDoc, "//Pseudonym")).getTextNormalize();
                    System.out.println(id + " / " + kennung + " / " + currentName + " --> " + pseudo);
                    s.getChild("name").setText(pseudo);
                } else {
                    System.out.println("Not found");
                    System.out.println(id + " / " + kennung + " / " + currentName + " --> ");
                    s.getChild("name").setText("");
                }
            }
            
            FileIO.writeDocumentToLocalFile(transcriptionFile, transcriptionDocument);
            
        }
    }

    private Document getMetaDataDocument() throws JDOMException, IOException {
        TabulateEventMetadata tem = new TabulateEventMetadata(metadataDirectory.getAbsolutePath());
        tem.doit();
        return tem.getResult();
    }

    private HashMap<String, String> mapSpeakers(Document transcriptionDocument, Document metaDataDocument, 
                                                String speechEventKennung, String transcriptKennung) throws JDOMException {
        HashMap<String, String> ids2Kennung = new HashMap<String,String>();
        List speakers = XPath.newInstance("//speaker").selectNodes(transcriptionDocument);
        for (Object o : speakers){
            Element speaker = (Element)o;
            String speakerID = speaker.getAttributeValue("speaker-id").trim();
            //<Sprecher Kennung="FOLK_S_00001" SE-Kennung="FOLK_E_00001_SE_01" sigle="ML"/>
            String xp = "//Sprecher[@SE-Kennung='" + speechEventKennung + "' and @sigle='" + speakerID + "']";
            if (transcriptKennung.startsWith("HL")){
                xp = "//Sprecher[@SE-Kennung='" + speechEventKennung + "' and contains(@sigle,'" + speakerID + "')]";                
            }
            Element metaSprecher = (Element) XPath.newInstance(xp).selectSingleNode(metaDataDocument);
            Element logElement = new Element("assignment");
            logElement.setAttribute("speaker-id", speakerID);
            logElement.setAttribute("transcript-id", transcriptKennung);
            if (metaSprecher!=null && metaSprecher.getAttributeValue("Kennung")!=null){
                speaker.setAttribute("dgd-id", metaSprecher.getAttributeValue("Kennung"));
                logElement.setAttribute("dgd-id", metaSprecher.getAttributeValue("Kennung"));
            } else {
                speaker.setAttribute("dgd-id", "???");
                logElement.setAttribute("dgd-id", "NOT_FOUND");
                System.out.println("Not found: " + transcriptKennung + " / " + speakerID);
            }
            ids2Kennung.put(speakerID, speaker.getAttributeValue("dgd-id"));
            logDocument.getRootElement().addContent(logElement);
        }
        return ids2Kennung;
    }
    
    
}
