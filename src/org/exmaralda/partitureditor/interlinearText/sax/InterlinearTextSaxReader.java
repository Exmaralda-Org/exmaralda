/*
 * InterlinearTextSaxReader.java
 *
 * Created on 21. Maerz 2002, 16:33
 */

package org.exmaralda.partitureditor.interlinearText.sax;

import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.interlinearText.*;

import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;  

/**
 *
 * @author  Thomas
 * @version 
 */
public class InterlinearTextSaxReader {

    /** Creates new InterlinearTextSaxReader */
    public InterlinearTextSaxReader() {
    }
    
    /** reads transcription from XML file and returns it */
    public InterlinearText readFromFile(String inputFileName) throws SAXException {
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
            InterlinearTextSaxHandler handler = new InterlinearTextSaxHandler();
            xmlReader.setContentHandler(handler);
            // Set an ErrorHandler before parsing
            //xmlReader.setErrorHandler(new MyErrorHandler(System.err));
            // Tell the XMLReader to parse the XML document
            xmlReader.parse(new File(inputFileName).toURL().toString());
            return handler.getInterlinearText();
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
