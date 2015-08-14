/*
 * UnicodeKeyboardSaxHandler.java
 *
 * Created on 1. August 2003, 14:45
 */

package org.exmaralda.partitureditor.unicodeKeyboard;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
//import com.sun.xml.parser.Resolver;

/**
 *
 * @author  thomas
 */
public class UnicodeKeyboardSaxHandler extends org.xml.sax.helpers.DefaultHandler {
    
    UnicodeKeyboard keyboard;
    UnicodeKey currentKey;
    String currentPCData;
    java.awt.Font currentFont;
    
    /** Creates a new instance of UnicodeKeyboardSaxHandler */
    public UnicodeKeyboardSaxHandler() {
        currentPCData = new String();                        
    }

    public UnicodeKeyboard getUnicodeKeyboard(){
        return keyboard;
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
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {
            case SAXUtilities.UNICODE_KEYBOARD : keyboard = new UnicodeKeyboard();
                                                 keyboard.name = atts.getValue("name");
                                                 break;
            case SAXUtilities.KEY              : currentKey = new UnicodeKey();
                                                 break;
            case SAXUtilities.CONTENT          : break;
            case SAXUtilities.DESCRIPTION      : break;
            case SAXUtilities.FONT             : currentFont = new java.awt.Font("Arial Unicode MS", java.awt.Font.PLAIN, 10);
                                                 if (atts.getIndex("name")>=0){
                                                    String font = atts.getValue("name");
                                                    currentFont = new java.awt.Font(font, currentFont.getStyle(), currentFont.getSize());
                                                 }
                                                 if (atts.getIndex("face")>=0){
                                                    String face = atts.getValue("face");
                                                    int facevalue = java.awt.Font.PLAIN;
                                                    if (face.equals("bold")){
                                                        facevalue = java.awt.Font.BOLD;
                                                    } else if (face.equals("italic")){
                                                        facevalue = java.awt.Font.ITALIC;
                                                    }
                                                    currentFont = new java.awt.Font(currentFont.getName(), facevalue, currentFont.getSize());
                                                 }
                                                 if (atts.getIndex("size")>=0){
                                                     try{
                                                        int size = Integer.parseInt(atts.getValue("size"));
                                                        currentFont = new java.awt.Font(currentFont.getName(), currentFont.getStyle(), size);
                                                     } catch (NumberFormatException nfe){}
                                                 }
                                                 break;
        }
        currentPCData = new String();
    }

    public void endElement(String namespaceURI, String localName, String name){      
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {
            case SAXUtilities.UNICODE_KEYBOARD : break;                                                 
            case SAXUtilities.KEY              : keyboard.addElement(currentKey);
                                                 break;
            case SAXUtilities.CONTENT          : currentKey.content=currentPCData;
                                                 break;
            case SAXUtilities.DESCRIPTION      : currentKey.description=currentPCData;
                                                 break;
            case SAXUtilities.FONT             : currentKey.font=currentFont;
                                                 break;
        }
    }    
    
    public void characters (char buff[], int offset, int length){
        String newData = new String();
        for (int pos=offset; pos<offset+length; pos++){
            if ((buff[pos]!='\n') && (buff[pos]!='\t')){
                newData+=buff[pos]; //ignore newline and tabs
            }
        }
        currentPCData+=newData;
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
