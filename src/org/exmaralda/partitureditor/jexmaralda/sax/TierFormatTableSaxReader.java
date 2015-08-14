package org.exmaralda.partitureditor.jexmaralda.sax;

import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.jexmaralda.*;

import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;  

/*
 * TierFormatTableSaxReader.java
 *
 * Created on 23. April 2001, 11:23
 */



/**
 *
 * @author  Thomas
 * @version 
 */
public class TierFormatTableSaxReader extends Object {

    /** Creates new TierFormatTableSaxReader */
    public TierFormatTableSaxReader() {
    }

    public org.exmaralda.partitureditor.jexmaralda.TierFormatTable readFromFile(String inputFileName) throws SAXException {
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
            TierFormatTableSaxHandler handler = new TierFormatTableSaxHandler();
            xmlReader.setContentHandler(handler);
            // Set an ErrorHandler before parsing
            //xmlReader.setErrorHandler(new MyErrorHandler(System.err));
            // Tell the XMLReader to parse the XML document
            xmlReader.parse(new File(inputFileName).toURL().toString());
            return handler.getTierFormatTable();
        }
        catch(SAXException se){
            throw (se);
        }
        catch (IOException e) {
            throw new SAXException ("I/O error", e);
        }
        catch (ParserConfigurationException pce) {
            throw new SAXException ("Parser Configuration error", pce);
        }
    }    
    
    public org.exmaralda.partitureditor.jexmaralda.TierFormatTable readFromString(String inputString) throws SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        try{
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            XMLReader xmlReader = null;
            // Create a JAXP SAXParser
            SAXParser saxParser = spf.newSAXParser();
            // Get the encapsulated SAX XMLReader
            xmlReader = saxParser.getXMLReader();
            // Set the ContentHandler of the XMLReader
            TierFormatTableSaxHandler handler = new TierFormatTableSaxHandler();
            xmlReader.setContentHandler(handler);
            // Tell the XMLReader to parse the XML document
            StringReader isr = new StringReader(inputString);
            xmlReader.parse(new org.xml.sax.InputSource(isr));
            return handler.getTierFormatTable();
        }
        catch(SAXException se){
            throw (se);
        }
        catch (IOException e) {            
            throw new SAXException ("I/O error", e);
        }
        catch (ParserConfigurationException pce) {
            throw new SAXException ("Parser Configuration error", pce);
        }
    }        
    
}