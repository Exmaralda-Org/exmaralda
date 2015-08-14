/*
 * AbstractCorpusProcessor.java
 *
 * Created on 10. Oktober 2006, 17:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

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
public class CorpusSextantIntegrator {
    
    public static String CORPUS_FILENAME = "S:\\TP-Z2\\DATEN\\MAPTASK\\0.2\\MAPTASK.coma";
    public static String CORPUS_BASEDIRECTORY = "S:\\TP-Z2\\DATEN\\MAPTASK\\Map_Task_Aufnahmen";
    public static String SEGMENTED_FILE_XPATH = "//Transcription[Description/Key[@Name='segmented']/text()='true']/NSLink";
    
    public String currentFilename;    
    public Document corpus;
    public Element currentElement;
    
    /** Creates a new instance of AbstractCorpusProcessor */
    public CorpusSextantIntegrator(String cfn) {
        CORPUS_FILENAME = cfn;
        CORPUS_BASEDIRECTORY = new File(cfn).getParent();
    }
    

    public void doIt() throws IOException, JDOMException, SAXException, JexmaraldaException {
        corpus = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromLocalFile(CORPUS_FILENAME);
        System.out.println(CORPUS_FILENAME + " read successfully.");
        XPath xpath = null;
        xpath = XPath.newInstance(SEGMENTED_FILE_XPATH);
        List transcriptionList = xpath.selectNodes(corpus);
        System.out.println(transcriptionList.size() + " segmented transcriptions in the corpus.");
        for (int pos=0; pos<transcriptionList.size(); pos++){
            Element nslink = (Element)(transcriptionList.get(pos));
            currentElement = nslink;
            String fullTranscriptionName = CORPUS_BASEDIRECTORY + "\\" + nslink.getText();
            //fullTranscriptionName = fullTranscriptionName.replaceAll("%20/","/");
            System.out.println("Reading " + fullTranscriptionName + "...");
            org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader reader = new org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader();
            SegmentedTranscription st = reader.readFromFile(fullTranscriptionName);
            System.out.println(fullTranscriptionName + " read successfully.");
            currentFilename = fullTranscriptionName;
            processTranscription(st);
        }
    }
    

    public void processTranscription(SegmentedTranscription st) throws JDOMException, IOException{
        String standoffFilename = currentFilename.replaceAll("\\.exs", ".esa");
        SextantIntegrator integrator = new SextantIntegrator(currentFilename);
        System.out.println("Integrating " + standoffFilename + " into " + currentFilename + "...");
        integrator.integrate(standoffFilename);
        integrator.writeDocument(currentFilename);
    }

    public static void main(String[] args){
        String corpus = CorpusSextantIntegrator.CORPUS_FILENAME;
        if (args.length>0){
            corpus = args[0];
        }
        try {
            CorpusSextantIntegrator csi = new CorpusSextantIntegrator(corpus);
            csi.doIt();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
    }
    
}
