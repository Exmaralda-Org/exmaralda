/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.hzsk.copilot;


import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.jdom.*;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public abstract class AbstractCorpusProcessor {
    
    public static String CORPUS_FILENAME = "S:\\Korpora\\DOLMETSCHEN-SSHC\\Publikation\\0.1\\new.coma";
    public static String CORPUS_BASEDIRECTORY = "S:\\Korpora\\DOLMETSCHEN-SSHC\\Publikation\\0.1\\";
    //public static String CORPUS_FILENAME = "T:\\TP-E5\\SKOBI-ORDNER-AKTUELL\\CoMa_SKOBI_Datenbank.xml";
    //public static String CORPUS_BASEDIRECTORY = "T:\\TP-E5\\SKOBI-ORDNER-AKTUELL";
    public static String SEGMENTED_FILE_XPATH = "//Transcription[Description/Key[@Name='segmented']/text()='true']/NSLink";
    public static String BASIC_FILE_XPATH = "//Transcription[Description/Key[@Name='segmented']/text()='false']/NSLink";
    
    public String currentFilename;
    
    public Document corpus;
    public Element currentElement;
    
    /** Creates a new instance of AbstractCorpusProcessor */
    public AbstractCorpusProcessor() {
    }
    
    public void doIt() throws IOException, JDOMException, SAXException, JexmaraldaException {
        doIt(true);
    }

    public void doIt(boolean segmented) throws IOException, JDOMException, SAXException, JexmaraldaException {
        corpus = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromLocalFile(CORPUS_FILENAME);
        System.out.println(CORPUS_FILENAME + " read successfully.");
        XPath xpath = null;
        if (segmented){
            xpath = XPath.newInstance(SEGMENTED_FILE_XPATH);
        } else {
            xpath = XPath.newInstance(BASIC_FILE_XPATH);            
        }
        List transcriptionList = xpath.selectNodes(corpus);
        System.out.println(transcriptionList.size() + " segmented transcriptions in the corpus.");
        for (int pos=0; pos<transcriptionList.size(); pos++){
            Element nslink = (Element)(transcriptionList.get(pos));
            currentElement = nslink;
            String fullTranscriptionName = CORPUS_BASEDIRECTORY + "\\" + nslink.getText();
            //fullTranscriptionName = fullTranscriptionName.replaceAll("%20/","/");
            System.out.println("Reading " + fullTranscriptionName + "...");
            if (segmented) {
                org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader reader = new org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader();
                SegmentedTranscription st = reader.readFromFile(fullTranscriptionName);
                System.out.println(fullTranscriptionName + " read successfully.");
                currentFilename = fullTranscriptionName;
                processTranscription(st);
            } else {
                BasicTranscription bt = new BasicTranscription(fullTranscriptionName);
                System.out.println(fullTranscriptionName + " read successfully.");
                currentFilename = fullTranscriptionName;
                processTranscription(bt);                
            }
        }
    }
    

    public abstract void processTranscription(SegmentedTranscription st);
    
    public abstract void processTranscription(BasicTranscription bt);
}
