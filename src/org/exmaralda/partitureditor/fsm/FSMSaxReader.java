/*
 * FSMSaxReader.java
 *
 * Created on 29. Juli 2002, 16:07
 */

package org.exmaralda.partitureditor.fsm;

import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.jexmaralda.*;

import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;  

/**
 *
 * @author  Thomas
 * @version 
 */
public class FSMSaxReader {

    /** Creates new FSMSaxReader */
    public FSMSaxReader() {
    }

    /** reads finite state machine from XML file and returns it */
    public FiniteStateMachine readFromFile(String inputFileName) throws SAXException {
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
            FSMSaxHandler handler = new FSMSaxHandler();
            xmlReader.setContentHandler(handler);
            // Set an ErrorHandler before parsing
            //xmlReader.setErrorHandler(new MyErrorHandler(System.err));
            // Tell the XMLReader to parse the XML document
            xmlReader.parse(new File(inputFileName).toURL().toString());            
            return handler.getFSM();
        }
        catch(SAXException se){
            throw (se);
        }
        catch (IOException e) {            
            System.out.println(e.getMessage());
            throw new SAXException ("I/O error", e);
        }
        catch (ParserConfigurationException pce) {
            throw new SAXException ("Parser Configuration error", pce);
        }        
    }

    /** reads finite state machine from XML string and returns it */    
    public FiniteStateMachine readFromString(String inputString) throws SAXException {
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
            FSMSaxHandler handler = new FSMSaxHandler();
            xmlReader.setContentHandler(handler);
            // Set an ErrorHandler before parsing
            //xmlReader.setErrorHandler(new MyErrorHandler(System.err));
            // Tell the XMLReader to parse the XML document
            StringReader isr = new StringReader(inputString);
            xmlReader.parse(new org.xml.sax.InputSource(isr));
            return handler.getFSM();
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
    
    public FiniteStateMachine readFromStream(java.io.InputStream is) throws SAXException {
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
            FSMSaxHandler handler = new FSMSaxHandler();
            xmlReader.setContentHandler(handler);
            // Set an ErrorHandler before parsing
            //xmlReader.setErrorHandler(new MyErrorHandler(System.err));
            // Tell the XMLReader to parse the XML document
            xmlReader.parse(new org.xml.sax.InputSource(is));            
            return handler.getFSM();
        }
        catch(SAXException se){
            throw (se);
        }
        catch (IOException e) {            
            System.out.println(e.getMessage());
            throw new SAXException ("I/O error", e);
        }
        catch (ParserConfigurationException pce) {
            throw new SAXException ("Parser Configuration error", pce);
        }        
    }
    
        
}
