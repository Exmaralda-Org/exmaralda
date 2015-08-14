/*
 * TimedSegmentSaxReader.java
 *
 * Created on 6. August 2002, 15:24
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.sax.*;

import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;  
/**
 *
 * @author  Thomas
 * @version 
 */
public class TimedSegmentSaxReader {


    /** Creates new TimedSegmentSaxReader */
    public TimedSegmentSaxReader() {
    }
    
    public TimedSegment readFromString(String inputString) throws SAXException {
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
            TimedSegmentSaxHandler handler = new TimedSegmentSaxHandler();
            xmlReader.setContentHandler(handler);
            // Set an ErrorHandler before parsing
            //xmlReader.setErrorHandler(new MyErrorHandler(System.err));
            // Tell the XMLReader to parse the XML document
            StringReader isr = new StringReader(inputString);
            xmlReader.parse(new org.xml.sax.InputSource(isr));
            return handler.getTimedSegment();
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
