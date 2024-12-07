/*
 * CHATConverter.java
 *
 * Created on 16. Juli 2003, 12:44
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
 * @author  thomas
 */
public class XCHATConverter implements javax.xml.transform.ErrorListener {
    
    static Document document;
    static final String CHAT2SIMPLECHAT_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/CHAT2SimpleCHAT.xsl";
    static final String SIMPLECHAT2BASICTRANSCRIPTION_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/SimpleCHAT2BasicTranscription.xsl";
    
    /** Creates a new instance of CHATConverter */
    public XCHATConverter() {
    }
    
    public BasicTranscription readXCHATFromFile(String filename) throws JexmaraldaException, SAXException, 
                                                                              ParserConfigurationException, 
                                                                              IOException, TransformerConfigurationException, 
                                                                              TransformerException  {
        
        StylesheetFactory ssf = new StylesheetFactory();
        
        String simplifiedCHAT = ssf.applyInternalStylesheetToExternalXMLFile(CHAT2SIMPLECHAT_STYLESHEET, filename);
        String basicTrans = ssf.applyInternalStylesheetToString(SIMPLECHAT2BASICTRANSCRIPTION_STYLESHEET, simplifiedCHAT);
        
        // convert the ouput stream to a string and return it
        BasicTranscription bt = new BasicTranscription();
        bt.BasicTranscriptionFromString(basicTrans);

        return bt;
    }    
    
    public void error(javax.xml.transform.TransformerException transformerException) throws javax.xml.transform.TransformerException {
        System.out.println(transformerException.getMessageAndLocation());
    }
    
    public void fatalError(javax.xml.transform.TransformerException transformerException) throws javax.xml.transform.TransformerException {
        System.out.println(transformerException.getMessageAndLocation());
    }
    
    public void warning(javax.xml.transform.TransformerException transformerException) throws javax.xml.transform.TransformerException {
        System.out.println(transformerException.getMessageAndLocation());
    }
    
}
