/*
 * ExSyncSaxReader.java
 *
 * Created on 11. April 2002, 11:47
 */

package org.exmaralda.partitureditor.exSync.sax;

import java.net.*;
import java.io.*;
import org.xml.sax.*;

import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;  
/**
 *
 * @author  Thomas
 * @version 
 */
public class ExSyncDocumentSaxReader  {

    /** Creates new ExSyncSaxReader */
    public ExSyncDocumentSaxReader() {
    }
    
    public org.exmaralda.partitureditor.exSync.ExSyncDocument readFromFile(String inputFileName) throws SAXException {
        // Create a JAXP SAXParserFactory and configure it
        try{
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            XMLReader xmlReader = null;
            // Create a JAXP SAXParser
            SAXParser saxParser = spf.newSAXParser();
            // Get the encapsulated SAX XMLReader
            xmlReader = saxParser.getXMLReader();
            // Set the ContentHandler of the XMLReader
            ExSyncDocumentSaxHandler handler = new ExSyncDocumentSaxHandler();
            xmlReader.setContentHandler(handler);
            // Set an ErrorHandler before parsing
            //xmlReader.setErrorHandler(new MyErrorHandler(System.err));
            // Tell the XMLReader to parse the XML document
            xmlReader.parse(new File(inputFileName).toURL().toString());
            return handler.getDocument();
        }
        catch(SAXException se){
            throw (se);
        }
        catch (IOException e) {
            throw new SAXException ("I/O error : " + e.getMessage(), e);
        }
        catch (ParserConfigurationException pce) {
            throw new SAXException ("Parser Configuration error", pce);
        }
    }        

}
