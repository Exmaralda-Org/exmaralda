/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.coma.root;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author bernd
 */
public class TestComaDocument {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestComaDocument().doit();
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(TestComaDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        File comaFile = new File("D:\\ZUMULT\\TGDP\\TGDP.coma");
        Document xmlDocument = FileIO.readDocumentFromLocalFile(comaFile);
        Element rootElement = xmlDocument.getRootElement();
        rootElement.detach();
        ComaDocument comaDocument = new ComaDocument(rootElement);
        System.out.println(String.join("\n", comaDocument.getCommunicationIDs()));
        System.out.println("==================");
        System.out.println(String.join("\n", comaDocument.getSpeakerIDs()));
        System.out.println("==================");
        System.out.println(String.join("\n", comaDocument.getTranscriptionIDs()));
        System.out.println("==================");
        
        Map<String, Map<String, String>> speakerMetadataAsMap = comaDocument.getSpeakerMetadataAsMap();
        for (String key : speakerMetadataAsMap.keySet()){
            System.out.println(key);
            for (String key2 : speakerMetadataAsMap.get(key).keySet()){
                System.out.println("\t" + key2 + "\t" + speakerMetadataAsMap.get(key).get(key2));
            }
        }
        
        Map<String, Map<String, String>> communicationMetadataAsMap = comaDocument.getCommunicationMetadataAsMap();
        for (String key : communicationMetadataAsMap.keySet()){
            System.out.println(key);
            for (String key2 : communicationMetadataAsMap.get(key).keySet()){
                System.out.println("\t" + key2 + "\t" + communicationMetadataAsMap.get(key).get(key2));
            }
        }
        
        Map<String, Map<String, String>> transcriptionMetadataAsMap = comaDocument.getTranscriptionMetadataAsMap();
        for (String key : transcriptionMetadataAsMap.keySet()){
            System.out.println(key);
            for (String key2 : transcriptionMetadataAsMap.get(key).keySet()){
                System.out.println("\t" + key2 + "\t" + transcriptionMetadataAsMap.get(key).get(key2));
            }
        }
    }
    
}
