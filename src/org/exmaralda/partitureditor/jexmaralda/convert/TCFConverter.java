/*
 * TEIConverter.java
 *
 * Created on 12. August 2004, 17:24
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.*;

import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.*;

import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.SAXException;

import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.common.corpusbuild.TEIMerger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.cGATMinimalSegmentation;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

/**
 *
 * @author  thomas
 */
public class TCFConverter {
    
    static String TCF_STYLESHEET_PATH = "/org/exmaralda/partitureditor/jexmaralda/xsl/ISOTEI2TCF.xsl";
    

    
    /** Creates a new instance of TEIConverter */
    public TCFConverter() {
    }
    
    public BasicTranscription readTCFFromFile(String path) { 
        // TODO
        return null;
    }
    
    
    public void writeHIATTCFToFile(BasicTranscription bt, String filename) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {
        writeHIATTCFToFile(bt, filename, "de");
        
    }
    public void writeHIATTCFToFile(BasicTranscription bt, String filename, String language) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {
        writeTCFToFile(bt, filename, language, "HIAT");
    }
    
    public void writeTCFToFile(BasicTranscription bt, String filename, String language, String segmentationName) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {
    
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();        
        System.out.println("started writing document...");
        
        AbstractSegmentation segmentation = null;
        if ("HIAT".equals(segmentationName)){
            segmentation = new HIATSegmentation();
        } else if ("cGAT Minimal".equals(segmentationName)){
            segmentation = new cGATMinimalSegmentation();                                                                      
        } else {
            segmentation = new GenericSegmentation();
        }
        
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);
        System.out.println("Segmented transcription created");
        
        String nameOfDeepSegmentation = "SpeakerContribution_Word";
        if ("HIAT".equals(segmentationName)){
            nameOfDeepSegmentation = "SpeakerContribution_Utterance_Word";
        }
        
        TEIMerger teiMerger = new TEIMerger(true);
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(stdoc, nameOfDeepSegmentation, "SpeakerContribution_Event", true);
        System.out.println("Merged");
        generateWordIDs(teiDoc);
        
        
        StylesheetFactory ssf = new StylesheetFactory(true);
        String tcf = ssf.applyInternalStylesheetToString(TCF_STYLESHEET_PATH, IOUtilities.documentToString(teiDoc));
        Document tcfDoc = FileIO.readDocumentFromString(tcf);
                
        // set the language (not too elegant...)
        XPath xp = XPath.newInstance("//tcf:TextCorpus");
        xp.addNamespace("tcf", "http://www.dspin.de/data/textcorpus");        
        Element textCorpusElement = (Element) xp.selectSingleNode(tcfDoc);
        textCorpusElement.setAttribute("lang", language);
        
        FileIO.writeDocumentToLocalFile(filename, tcfDoc);
        System.out.println("document written.");        
        
    }
    
    public void writeFOLKERTCFToFile(Document flnDoc, String absolutePath) throws SAXException, ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException, JDOMException {
        StylesheetFactory sf = new StylesheetFactory(true);
        
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/folker2isotei.xsl", IOUtilities.documentToString(flnDoc));
        Document teiDoc = IOUtilities.readDocumentFromString(result);
        generateWordIDs(teiDoc);
        
        String tcf = sf.applyInternalStylesheetToString(TCF_STYLESHEET_PATH, IOUtilities.documentToString(teiDoc));
        Document tcfDoc = FileIO.readDocumentFromString(tcf);
        
        IOUtilities.writeDocumentToLocalFile(absolutePath, tcfDoc);                
    }
    
    

    //****************************************************
    //********* private processing methods ***************
    //****************************************************

    private void generateWordIDs(Document document) throws JDOMException{
        XPath wordXPath = XPath.newInstance("//tei:w"); 
        wordXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        
        List words = wordXPath.selectNodes(document);
        int count=0;
        for (Object o : words){
            count++;
            Element word = (Element)o;
            String wordID = "w" + Integer.toString(count);
            //System.out.println("*** " + wordID);
            word.setAttribute("id", wordID, Namespace.XML_NAMESPACE);
        }
        
        // new 02-12-2014
        XPath pcXPath = XPath.newInstance("//tei:pc"); 
        pcXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        
        List pcs = pcXPath.selectNodes(document);
        count=0;
        for (Object o : pcs){
            count++;
            Element pc = (Element)o;
            String pcID = "pc" + Integer.toString(count);
            //System.out.println("*** " + wordID);
            pc.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
        }
    }

    

    
}
