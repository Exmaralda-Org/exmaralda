/*
 * EventListTranscriptionXMLReader.java
 *
 * Created on 9. Mai 2008, 15:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.io;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.data.EventListTranscription;
import org.jdom.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.*;
import java.io.*;
import org.exmaralda.folker.data.AbstractParser;
import org.exmaralda.folker.data.TranscriptionHead;
import org.xml.sax.SAXException;


/**
 *
 * @author thomas
 */
public class EventListTranscriptionXMLReaderWriter {
    
    public static String EXMARALDA2FOLKER_STYLESHEET = "/org/exmaralda/folker/data/exmaralda2folker.xsl";
    public static String FOLKER2EXMARALDA_STYLESHEET = "/org/exmaralda/folker/data/folker2exmaralda.xsl";
    
    /** Creates a new instance of EventListTranscriptionXMLReader */
    public EventListTranscriptionXMLReaderWriter() {
    }
    
    // 08-06-2009: using a stylesheet for this is very slow
    // replaced the existing method with a faster method
    public static Document toJDOMDocumentOld(EventListTranscription elt, File file)
            throws SAXException, JDOMException, IOException,
            ParserConfigurationException, TransformerConfigurationException, TransformerException{

        BasicTranscription bt = EventListTranscriptionConverter.exportBasicTranscription(elt);
        bt.getHead().getMetaInformation().relativizeReferencedFile(file.getAbsolutePath());
        StylesheetFactory sf = new StylesheetFactory(true);
        String resultString = sf.applyInternalStylesheetToString(EXMARALDA2FOLKER_STYLESHEET, bt.toXML());        
        Document doc = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString(resultString);        
        return doc;

    }

    public static Document toJDOMDocument(EventListTranscription elt, File file){
        return toJDOMDocument(elt, file, null);
    }
    
    public static Document toJDOMDocument(EventListTranscription elt, File file, TranscriptionHead transcriptionHead){
        Document result = new Document();
        Element root = elt.toJDOMElement(file, transcriptionHead);
        result.setRootElement(root);
        return result;
    }
    
    public static void writeXML(EventListTranscription elt, File file, AbstractParser ap, int parseLevel) throws
            SAXException, IOException, JDOMException,
            ParserConfigurationException, TransformerConfigurationException, TransformerException {
        writeXML(elt, file, ap, parseLevel, null);
    }

    public static void writeXML(EventListTranscription elt, File file, AbstractParser ap, int parseLevel, TranscriptionHead transcriptionHead) throws
            SAXException, IOException, JDOMException,
            ParserConfigurationException, TransformerConfigurationException, TransformerException {
        elt.updateContributions();
        org.jdom.Document doc = toJDOMDocument(elt, file, transcriptionHead);
        // TODO: take care of parse level 3
        /*for (int level=1; level<=parseLevel; level++){
            ap.parseDocument(doc, level);
        }*/
        if (parseLevel>0){
            ap.parseDocument(doc, 1);
            if (parseLevel>1){
                ap.parseDocument(doc, parseLevel);
            }
        }
                
        org.exmaralda.common.jdomutilities.IOUtilities.writeDocumentToLocalFile(file.getAbsolutePath(), doc);
    }
    
    public static EventListTranscription readXML(File file) throws JDOMException, IOException, SAXException,
                                                                    ParserConfigurationException, TransformerConfigurationException,
                                                                    TransformerException, JexmaraldaException, IllegalArgumentException {
        return readXML(file,50);
        
    }

     public static EventListTranscription readXML(File file, int timelineTolerance) throws JDOMException, IOException, SAXException,
                                                                    ParserConfigurationException, TransformerConfigurationException,
                                                                    TransformerException, JexmaraldaException, IllegalArgumentException {
        Document doc = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromLocalFile(file.getAbsolutePath());
        String docString = org.exmaralda.common.jdomutilities.IOUtilities.documentToString(doc);
        StylesheetFactory sf = new StylesheetFactory(true);
        String resultString = sf.applyInternalStylesheetToString(FOLKER2EXMARALDA_STYLESHEET, docString);        
        BasicTranscription bt = new BasicTranscription();
        bt.BasicTranscriptionFromString(resultString);
        if (!new File(bt.getHead().getMetaInformation().getReferencedFile()).isAbsolute()){
            // changed 19-06-2012: NEW METHOD
            bt.getHead().getMetaInformation().resolveReferencedFile(file.getAbsolutePath(), MetaInformation.NEW_METHOD);
        }
        EventListTranscription result = EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt, timelineTolerance);
        return result;        
    }
     
     public static EventListTranscription readXML(Document doc, int timelineTolerance) throws JDOMException, IOException, SAXException,
                                                                    ParserConfigurationException, TransformerConfigurationException,
                                                                    TransformerException, JexmaraldaException, IllegalArgumentException {
         
        String docString = org.exmaralda.common.jdomutilities.IOUtilities.documentToString(doc);
        StylesheetFactory sf = new StylesheetFactory(true);
        String resultString = sf.applyInternalStylesheetToString(FOLKER2EXMARALDA_STYLESHEET, docString);        
        BasicTranscription bt = new BasicTranscription();
        bt.BasicTranscriptionFromString(resultString);
        // no resolving file when it comes from a doc...
        //if (!new File(bt.getHead().getMetaInformation().getReferencedFile()).isAbsolute()){
            // changed 19-06-2012: NEW METHOD
        //    bt.getHead().getMetaInformation().resolveReferencedFile(file.getAbsolutePath(), MetaInformation.NEW_METHOD);
        //}
        EventListTranscription result = EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt, timelineTolerance);
        return result;        
     }

    public static BasicTranscription readXMLAsBasicTranscription(File file) throws JDOMException, IOException, SAXException,
                                                                            ParserConfigurationException, TransformerConfigurationException,
                                                                            TransformerException, JexmaraldaException{
        Document doc = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromLocalFile(file.getAbsolutePath());
        String docString = org.exmaralda.common.jdomutilities.IOUtilities.documentToString(doc);
        StylesheetFactory sf = new StylesheetFactory(true);
        String resultString = sf.applyInternalStylesheetToString(FOLKER2EXMARALDA_STYLESHEET, docString);
        BasicTranscription bt = new BasicTranscription();
        bt.BasicTranscriptionFromString(resultString);
        if (!new File(bt.getHead().getMetaInformation().getReferencedFile()).isAbsolute()){
            bt.getHead().getMetaInformation().resolveReferencedFile(file.getAbsolutePath(), MetaInformation.NEW_METHOD);
        }
        return bt;
    }

    public static BasicTranscription readXMLAsBasicTranscription(Document doc) throws JDOMException, IOException, SAXException,
                                                                            ParserConfigurationException, TransformerConfigurationException,
                                                                            TransformerException, JexmaraldaException{
        String docString = org.exmaralda.common.jdomutilities.IOUtilities.documentToString(doc);
        StylesheetFactory sf = new StylesheetFactory(true);
        String resultString = sf.applyInternalStylesheetToString(FOLKER2EXMARALDA_STYLESHEET, docString);
        BasicTranscription bt = new BasicTranscription();
        bt.BasicTranscriptionFromString(resultString);
        return bt;
    }
    
    
    
}
