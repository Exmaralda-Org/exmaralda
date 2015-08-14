package org.exmaralda.partitureditor.jexmaralda.sax;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
//import com.sun.xml.parser.Resolver;
import org.exmaralda.partitureditor.jexmaralda.sax.SAXUtilities;

/*
 * TierFormatTableSaxHandler.java
 *
 * Created on 23. April 2001, 11:40
 */



/**
 *
 * @author  Thomas
 * @version 
 */
class TierFormatTableSaxHandler extends org.xml.sax.helpers.DefaultHandler {

    private org.exmaralda.partitureditor.jexmaralda.TierFormatTable tierFormatTable;
    private Vector openElements;
    private String currentPCData;
    private org.exmaralda.partitureditor.jexmaralda.TierFormat currentTierFormat;
    private String currentPropertyName;
    private String currentPropertyValue;
    private boolean fontHasBeenSet = false;
    
    /** Creates new TierFormatTableSaxHandler */
    public TierFormatTableSaxHandler() {
        openElements = new Vector();
        currentPCData = new String();        
        currentPropertyValue = new String();
    }
    
    public org.exmaralda.partitureditor.jexmaralda.TierFormatTable getTierFormatTable(){
        return tierFormatTable;
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
        openElements.addElement(name);
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {
            case SAXUtilities.TIER_FORMAT_TABLE     :   tierFormatTable = new org.exmaralda.partitureditor.jexmaralda.TierFormatTable();
                                                        break;
            case SAXUtilities.REFERENCED_FILE       :   tierFormatTable.addReferencedFile(atts.getValue(0));
                                                        break;
            case SAXUtilities.TIER_FORMAT           :   currentTierFormat = new org.exmaralda.partitureditor.jexmaralda.TierFormat();
                                                        fontHasBeenSet = false;
                                                        //System.out.println("Here I REALLY go again with " + atts.toString());
                                                        if (atts.getIndex("tierref")>=0)
                                                            {currentTierFormat.setTierref(atts.getValue("tierref"));}
                                                        if (atts.getIndex("style-name")>=0)
                                                            {currentTierFormat.setStyleName(atts.getValue("style-name"));}
                                                        if (atts.getIndex("size")>=0)
                                                            {currentTierFormat.setSize(new Integer(atts.getValue("size")).intValue());}
                                                        if (atts.getIndex("alignment-name")>=0)
                                                            {currentTierFormat.setAlignmentName(atts.getValue("alignment-name"));}
                                                        if (atts.getIndex("textcolor-name")>=0)
                                                            {currentTierFormat.setTextcolorName(atts.getValue("textcolor-name"));}
                                                        if (atts.getIndex("bgcolor-name")>=0)
                                                            {currentTierFormat.setBgcolorName(atts.getValue("bgcolor-name"));}
                                                        break;
            case SAXUtilities.PROPERTY              :   currentPropertyName = atts.getValue("name");
                                                        break;
            case SAXUtilities.TIMELINE_ITEM_FORMAT  :   org.exmaralda.partitureditor.jexmaralda.TimelineItemFormat tlif = new org.exmaralda.partitureditor.jexmaralda.TimelineItemFormat();
                                                        if (atts.getIndex("show-every-nth-numbering")>=0)
                                                            {tlif.setNthNumbering(Short.parseShort(atts.getValue("show-every-nth-numbering")));}
                                                        if (atts.getIndex("show-every-nth-absolute")>=0)
                                                            {tlif.setNthAbsolute(Short.parseShort(atts.getValue("show-every-nth-absolute")));}
                                                        if (atts.getIndex("show-every-nth-absolute")>=0)
                                                            {tlif.setAbsoluteTimeFormat(atts.getValue("absolute-time-format"));}
                                                        if (atts.getIndex("miliseconds-digits")>=0)
                                                            {tlif.setMilisecondsDigits(Short.parseShort(atts.getValue("miliseconds-digits")));}
                                                        tierFormatTable.setTimelineItemFormat(tlif);
                                                        break;    
        }                                                
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endElement(String namespaceURI, String localName, String name){               
        if (((String)openElements.lastElement()).equals(name)){
            openElements.removeElementAt(openElements.size()-1);
        }
        else {
            System.out.println("Fehler");
        }
        if (name.equals("tier-format")){
            if (!fontHasBeenSet){currentTierFormat.setFontName(currentPCData);}
            currentPCData = new String();        
            try {tierFormatTable.addTierFormat(currentTierFormat);}
            catch (org.exmaralda.partitureditor.jexmaralda.JexmaraldaException je) {}
        } else if (name.equals("property")){
            currentTierFormat.setProperty(currentPropertyName,currentPropertyValue);
            if (currentPropertyName.equals("font-name")){fontHasBeenSet=true;}
            currentPropertyValue = new String();
        }
    }
    
// ----------------------------------------------------------------------------------------------------------- 
    public void characters (char buff[], int offset, int length){
        String newData = new String();
        for (int pos=offset; pos<offset+length; pos++){
            if ((buff[pos]!='\n') && (buff[pos]!='\t')){
                newData+=buff[pos]; //ignore newline and tabs
            }
        }
        int id = SAXUtilities.getIDForElementName(((String)openElements.lastElement()));
        if (id==SAXUtilities.PROPERTY){
            currentPropertyValue+=newData;
        } else {
            currentPCData+=newData;
        }
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