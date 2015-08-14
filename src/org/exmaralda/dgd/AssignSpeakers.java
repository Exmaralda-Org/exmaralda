/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

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
public class AssignSpeakers {

    File metadataDirectory;
    File transcriptionDirectory;
    static Document logDocument;

    public AssignSpeakers(File metadataDirectory, File transcriptionDirectory) {
        this.metadataDirectory = metadataDirectory;
        this.transcriptionDirectory = transcriptionDirectory;
        Element rootElement = new Element("speakerAssignments");
        logDocument = new Document(rootElement);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String metadataPath = "O:\\xml\\events\\Werkstatt\\FOLK";
            String transcriptPath = "C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\3";
            String logPath = "C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\FOLK_Speaker_Assignment3.xml";
            AssignSpeakers as = new AssignSpeakers(new File(metadataPath), new File(transcriptPath));
            as.doit();
            FileIO.writeDocumentToLocalFile(new File(logPath), logDocument);
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
            Document transcriptionDocument = FileIO.readDocumentFromLocalFile(transcriptionFile);
            //PF--_E_00008_SE_01_T_01_DF_01.fln
            String speechEventKennung = transcriptionFile.getName().substring(0,18);
            String transcriptKennung = transcriptionFile.getName().substring(0, transcriptionFile.getName().indexOf("."));            
            HashMap<String, String> speakersMap = mapSpeakers(transcriptionDocument, metaDataDocument, speechEventKennung, transcriptKennung);
            
            List existingReferences = XPath.newInstance("//contribution/@speaker-dgd-id").selectNodes(transcriptionDocument);
            for (Object e : existingReferences){
                Attribute a = (Attribute)e;
                a.detach();
            }
            
            List contributions = XPath.newInstance("//contribution[@speaker-reference]").selectNodes(transcriptionDocument);
            for (Object o : contributions){
                Element contribution = (Element)o;
                String speakerID = contribution.getAttributeValue("speaker-reference").trim();
                //System.out.println("Looking up " + speakerID);
                String speakerKennung = speakersMap.get(speakerID);
                //contribution.setAttribute("speaker-dgd-id", speakerKennung);
            }
            //FileIO.writeDocumentToLocalFile(transcriptionFile, transcriptionDocument);
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
