/*
 * exSyncSaxHandler.java
 *
 * Created on 11. April 2002, 11:47
 */

package org.exmaralda.partitureditor.exSync.sax;

import org.exmaralda.partitureditor.exSync.*;
import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
//import com.sun.xml.parser.Resolver;
import org.exmaralda.partitureditor.jexmaralda.sax.SAXUtilities;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ExSyncDocumentSaxHandler extends org.xml.sax.helpers.DefaultHandler {

    static final short EX_SYNC = 0;
    static final short TRACK = 1;
    static final short NAME = 2;
    static final short FONT = 3;
    static final short NO_ENTRIES = 4;
    static final short SYNC_TABS = 5;
    static final short SYNC_TAB = 6;
    static final short TEXT = 7;
  
    private ExSyncDocument document;
    private Vector openElements;
    private String currentPCData;

    private Track currentTrack;
    private SyncTabs currentSyncTabs;
    private SyncTab currentSyncTab;
    
    /** Creates new exSyncSaxHandler */
    public ExSyncDocumentSaxHandler() {
        openElements = new Vector();
        currentPCData = new String();                
    }
    
    public ExSyncDocument getDocument(){
        return document;
    }
    
    public void startDocument(){
      System.out.println("started reading document...");
    }

    public void endDocument(){
        System.out.println("document read.");
    }

    public void startElement(String namespaceURI, String localName, String name, Attributes atts ) {
        openElements.addElement(name);
        int id = getIDForElementName(name);
        switch (id) {
            case EX_SYNC   :    document = new ExSyncDocument();
                                break;
            case TRACK     :    currentTrack = new Track();
                                break;
            case SYNC_TABS :    currentSyncTabs = new SyncTabs();
                                break;
            case SYNC_TAB  :    currentSyncTab = new SyncTab();
                                currentSyncTab.id = atts.getValue("id");
                                currentSyncTab.offset = Integer.valueOf(atts.getValue("offset")).intValue();
                                break;                                
        }

    }
    
    public void endElement(String namespaceURI, String localName, String name){               
        openElements.removeElementAt(openElements.size()-1);
        int id = getIDForElementName(name);
        switch (id) {
            case TRACK       :    document.addTrack(currentTrack);
                                  break;
            case SYNC_TABS   :    currentTrack.syncTabs = currentSyncTabs;
                                  break;
            case SYNC_TAB    :    currentSyncTabs.addSyncTab(currentSyncTab);
                                  break; 
            case NAME        :    currentTrack.name = currentPCData;
                                  break;                               
            case FONT        :    currentTrack.font = currentPCData;
                                  break;
            case NO_ENTRIES  :    currentTrack.noEntries = Integer.valueOf(currentPCData).intValue();
                                  break;
            case TEXT        :    currentTrack.text = currentPCData;
        }
        currentPCData = new String();

    }
    
    public void characters (char buff[], int offset, int length){
        String newData = new String();
        int currentElement = getIDForElementName((String)openElements.lastElement());
        for (int pos=offset; pos<offset+length; pos++){
            if (((buff[pos]!='\n') && (buff[pos]!='\t')) || (currentElement==TEXT)){ //ignore newline and tabs except in text
                newData+=buff[pos]; 
            }
        }
        currentPCData+=newData;
    } // end characters
            

    public void error (SAXParseException e) throws SAXParseException {
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
   

    

    private int getIDForElementName(String elementName){
        if (elementName.equals("exSync")){return EX_SYNC;}
        else if (elementName.equals("track")){return TRACK;}
        else if (elementName.equals("name")){return NAME;}
        else if (elementName.equals("font")){return FONT;}
        else if (elementName.equals("noEntries")){return NO_ENTRIES;}
        else if (elementName.equals("syncTabs")){return SYNC_TABS;}
        else if (elementName.equals("syncTab")){return SYNC_TAB;}
        else if (elementName.equals("text")){return TEXT;}
        return -1;
    }
    
}
