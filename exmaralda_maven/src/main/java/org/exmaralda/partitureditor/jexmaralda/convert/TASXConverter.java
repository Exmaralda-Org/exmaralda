/*
 * TASXConverter.java
 *
 * Created on 21. November 2001, 18:39
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
 
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

// For write operation
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import org.exmaralda.partitureditor.jexmaralda.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class TASXConverter {

    
    static Document document;
    static final String TASX2EX_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/Tasx2BasicTranscription.xsl";
    static final String EX2TASX_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/BasicTranscription2Tasx.xsl";
    /** Creates new TASXConverter */
    public TASXConverter() {
    }

    private String BasicTranscriptionToTASX(BasicTranscription t) throws SAXException, 
                                                                         IOException, 
                                                                         ParserConfigurationException, 
                                                                         TransformerConfigurationException, 
                                                                         TransformerException {
        
        StylesheetFactory ssf = new StylesheetFactory();        
        String tasxTrans = ssf.applyInternalStylesheetToString(EX2TASX_STYLESHEET, t.toXML());      
        return tasxTrans;
    }
           
    public void writeTASXToFile(BasicTranscription t, String filename) throws SAXException,
                                                                              IOException, 
                                                                              ParserConfigurationException, 
                                                                              TransformerConfigurationException, 
                                                                              TransformerException {
        System.out.println("started writing document...");
        java.io.FileOutputStream fos = new java.io.FileOutputStream(new java.io.File(filename));
        fos.write(BasicTranscriptionToTASX(t).getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
    }
    
    public BasicTranscription readTASXFromFile(String filename) throws JexmaraldaException, 
                                                                       SAXException, 
                                                                       IOException, 
                                                                       ParserConfigurationException, 
                                                                       TransformerConfigurationException, 
                                                                       TransformerException  {
        StylesheetFactory ssf = new StylesheetFactory();        
        String basicTrans = ssf.applyInternalStylesheetToExternalXMLFile(TASX2EX_STYLESHEET, filename);      
        BasicTranscription bt = new BasicTranscription();
        bt.BasicTranscriptionFromString(basicTrans);
        return bt;
    }
    
}
