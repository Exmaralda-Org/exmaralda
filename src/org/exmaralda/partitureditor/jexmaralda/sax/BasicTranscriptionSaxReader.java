package org.exmaralda.partitureditor.jexmaralda.sax;

import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.jexmaralda.*;

import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;  

/*
 * BasicTranscriptionSaxReader.java
 *
 * Created on 7. Maerz 2001, 09:55
 */



/**
 *
 * @author  Thomas
 * @version 
 */
public class BasicTranscriptionSaxReader extends Object {

    private String rootElementName = "";
    
    /** Creates new BasicTranscriptionSaxReader */
    public BasicTranscriptionSaxReader() {
    }
    
    

    /** reads transcription from XML file and returns it */
    public BasicTranscription readFromFile(String inputFileName) throws SAXException {
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
            BasicTranscriptionSaxHandler handler = new BasicTranscriptionSaxHandler();
            xmlReader.setContentHandler(handler);
            // Set an ErrorHandler before parsing
            //xmlReader.setErrorHandler(new MyErrorHandler(System.err));
            // Tell the XMLReader to parse the XML document
            xmlReader.parse(new File(inputFileName).toURL().toString());
            return handler.getTranscription();
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
    /** reads transcription from XML file and returns it */
    public BasicTranscription readFromFile(String inputFileName, boolean readOnlyHead) throws SAXException {
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
            BasicTranscriptionSaxHandler handler = new BasicTranscriptionSaxHandler(readOnlyHead);
            xmlReader.setContentHandler(handler);
            // Set an ErrorHandler before parsing
            //xmlReader.setErrorHandler(new MyErrorHandler(System.err));
            // Tell the XMLReader to parse the XML document
            xmlReader.parse(new File(inputFileName).toURL().toString());
            rootElementName = handler.getRootElementName();
            return handler.getTranscription();
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


    /** reads transcription from XML string and returns it 
     *
     * added by JTM, 23.11.2001
     */
    
    public BasicTranscription readFromString(String inputString) throws SAXException {
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
            BasicTranscriptionSaxHandler handler = new BasicTranscriptionSaxHandler();
            xmlReader.setContentHandler(handler);
            // Set an ErrorHandler before parsing
            //xmlReader.setErrorHandler(new MyErrorHandler(System.err));
            // Tell the XMLReader to parse the XML document
            StringReader isr = new StringReader(inputString);
            xmlReader.parse(new org.xml.sax.InputSource(isr));
            return handler.getTranscription();
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

    public String getRootElementName() {
        return rootElementName;
    }

    
}