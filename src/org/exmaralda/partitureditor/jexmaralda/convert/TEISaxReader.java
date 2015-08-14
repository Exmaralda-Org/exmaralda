/*
 * TEISaxReader.java
 *
 * Created on 11. August 2004, 17:14
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.jexmaralda.*;

import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;  

/**
 *
 * @author  thomas
 */
public class TEISaxReader {
    
    /** Creates a new instance of TEISaxReader */
    public TEISaxReader() {
    }
    
    /** reads finite state machine from XML file and returns it */
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
            TEISaxHandler handler = new TEISaxHandler();
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
    
    public static void main(String[] args){
        TEISaxReader reader = new TEISaxReader();
        try{ 
            BasicTranscription bt = reader.readFromFile("d:\\TEI\\in.xml");
            bt.writeXMLToFile("d:\\TEI\\out.xml", "none");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    
}
