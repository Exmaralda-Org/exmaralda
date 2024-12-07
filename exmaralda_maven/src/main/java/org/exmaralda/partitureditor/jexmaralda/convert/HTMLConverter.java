/*
 * HTMLConverter.java
 *
 * Created on 23. Juli 2003, 13:56
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
public class HTMLConverter {
    
    static final String HEAD2HTML_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/Head2HTML_en.xsl";
    
    /** Creates a new instance of HTMLConverter */
    public HTMLConverter() {
    }
    
    public String HeadToHTML(Head head)throws JexmaraldaException, SAXException, 
                                              ParserConfigurationException, 
                                              IOException, TransformerConfigurationException, 
                                              TransformerException  {
        

        String headString = new StylesheetFactory().applyInternalStylesheetToString(HEAD2HTML_STYLESHEET, head.toXML());
        return headString;
    }
    
    public String HeadToHTML(Head head, String externalStylesheetName)  throws JexmaraldaException, SAXException, 
                                                                        ParserConfigurationException, 
                                                                        IOException, TransformerConfigurationException, 
                                                                        TransformerException  {
        

        String headString = new StylesheetFactory().applyExternalStylesheetToString(externalStylesheetName, head.toXML());
        return headString;
    }

}
