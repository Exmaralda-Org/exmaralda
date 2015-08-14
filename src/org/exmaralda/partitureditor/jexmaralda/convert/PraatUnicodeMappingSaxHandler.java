/*
 * PraatUnicodeMappingSaxHandler.java
 *
 * Created on 6. Februar 2004, 15:59
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
//import com.sun.xml.parser.Resolver;

/**
 *
 * @author  thomas
 */
public class PraatUnicodeMappingSaxHandler extends org.xml.sax.helpers.DefaultHandler {
    
    PraatUnicodeMapping pum;
    
    /** Creates a new instance of PraatUnicodeMappingSaxHandler */
    public PraatUnicodeMappingSaxHandler() {
        pum = new PraatUnicodeMapping();
    }
    
    PraatUnicodeMapping getPraatUnicodeMapping(){
        return pum;
    }
    
// ----------------------------------------------------------------------------------------------------------- 
    public void startDocument(){
      System.out.println("started reading document...");
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endDocument(){
        System.out.println("document read.");
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void startElement(String namespaceURI, String localName, String name, Attributes atts ) {
        //System.out.println("Opening element " + name);
        if (name.equals("mapping")){
            String praat = atts.getValue("praat");
            String unicode = atts.getValue("unicode");
            pum.praatUnicode.put(praat, unicode);
            pum.unicodePraat.put(unicode,praat);
        }
    }

    public void endElement(String namespaceURI, String localName, String name){      
    }    
    
    public void characters (char buff[], int offset, int length){
    } // end characters

// ----------------------------------------------------------------------------------------------------------- 
    public void error (SAXParseException e)
    throws SAXParseException
    {
        System.out.println("Error: " + e.getMessage());
        throw e;
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void setDocumentLocator (Locator l)
    {
    }


// ----------------------------------------------------------------------------------------------------------- 
    public void ignorableWhitespace (char buf [], int offset, int len)
    throws SAXException
    {
        // do nothing
    }    
}
