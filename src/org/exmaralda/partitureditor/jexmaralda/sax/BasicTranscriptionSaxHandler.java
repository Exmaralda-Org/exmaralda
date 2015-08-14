package org.exmaralda.partitureditor.jexmaralda.sax;

import org.exmaralda.partitureditor.jexmaralda.*;

/*
 * BasicTranscriptionSaxHandler.java
 *
 * Created on 7. Maerz 2001, 09:57
 */
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
class BasicTranscriptionSaxHandler extends org.xml.sax.helpers.DefaultHandler {

    private BasicTranscription transcription;
    private Vector openElements;
    private String currentPCData;
    private String currentAttributeName;
    private String currentUDValue;
    private UDInformationHashtable currentUDInformation;
    private Speaker currentSpeaker;
    private Tier currentTier;
    private Event currentEvent;
    
    boolean readOnlyHead = false;
    boolean outsideHead = false;
    String rootElementName = "";
    
    TierFormatTableSaxHandler tierFormatTableSaxHandler = new TierFormatTableSaxHandler();
    boolean insideTierFormatTable = false;
    

    /** Creates new BasicTranscriptionSaxHandler */
    public BasicTranscriptionSaxHandler() {
     openElements = new Vector();
     currentPCData = new String();
    }

    /** Creates new BasicTranscriptionSaxHandler */
    public BasicTranscriptionSaxHandler(boolean roh) {
     openElements = new Vector();
     currentPCData = new String();
     readOnlyHead = roh;
    }

    public BasicTranscription getTranscription(){
        return transcription;
    }
        
    public String getRootElementName(){
        return rootElementName;
    }
    
    public void startDocument(){
      System.out.println("started reading document...");
      transcription = new BasicTranscription();
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endDocument(){
        System.out.println("document read.");
        transcription.setTierFormatTable(tierFormatTableSaxHandler.getTierFormatTable());
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void startElement(String namespaceURI, String localName, String name, Attributes atts ) {
        if (SAXUtilities.getIDForElementName(name)==SAXUtilities.TIER_FORMAT_TABLE){
            insideTierFormatTable = true;
        }
        if (insideTierFormatTable){
            // pass it on to the other handler
            tierFormatTableSaxHandler.startElement(namespaceURI, localName, name, atts);
            return;
        }
        if ((rootElementName.length()==0) && (openElements.size()<=0)){
            rootElementName = name;
        }
        if (!(rootElementName.equals("basic-transcription"))) return;
        if (readOnlyHead && outsideHead) return;
        openElements.addElement(name);
        currentPCData = new String();           
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {
            case SAXUtilities.UD_META_INFORMATION   :   currentUDInformation = new UDInformationHashtable(); 
                                                        break;
            case SAXUtilities.REFERENCED_FILE       :   transcription.getHead().getMetaInformation().addReferencedFile(atts.getValue("url"));
                                                        break;
            case SAXUtilities.UD_INFORMATION        :   currentAttributeName = new String(atts.getValue("attribute-name"));
                                                        currentUDValue = new String();
                                                        break;
            case SAXUtilities.SPEAKER               :   currentSpeaker = new Speaker();
                                                        currentSpeaker.setID(atts.getValue("id"));
                                                        break;
            case SAXUtilities.SEX                   :   if (atts.getValue("value").length()>0){
                                                            currentSpeaker.setSex(atts.getValue("value").charAt(0));
                                                        } else {
                                                            currentSpeaker.setSex('u');
                                                        }
                                                        break;
            case SAXUtilities.LANGUAGE              :   int id2 = SAXUtilities.getIDForElementName((String)openElements.elementAt(openElements.size()-2));
                                                        String language = new String();
                                                        if (atts.getIndex("xml:lang")>=0){
                                                            language = atts.getValue("xml:lang");}
                                                        if (atts.getIndex("lang")>=0){
                                                            language = atts.getValue("lang");}
                                                        switch (id2) {
                                                            case SAXUtilities.LANGUAGES_USED : currentSpeaker.getLanguagesUsed().addLanguage(language); break;
                                                            case SAXUtilities.L1             : currentSpeaker.getL1().addLanguage(language); break;
                                                            case SAXUtilities.L2             : currentSpeaker.getL2().addLanguage(language); break;
                                                        }
                                                        break;
            case SAXUtilities.TIER                  :   currentTier = new Tier();
                                                        currentTier.setID(atts.getValue("id"));
                                                        currentTier.setCategory(atts.getValue("category"));
                                                        currentTier.setType(atts.getValue("type"));
                                                        if (atts.getIndex("speaker")>=0){
                                                            currentTier.setSpeaker(atts.getValue("speaker"));
                                                        }
                                                        if (atts.getIndex("display-name")>=0){
                                                            currentTier.setDisplayName(atts.getValue("display-name"));
                                                        }
                                                        break;
            case SAXUtilities.EVENT                 :   currentEvent = new Event();
                                                        currentEvent.setStart(atts.getValue("start"));
                                                        currentEvent.setEnd(atts.getValue("end"));
                                                        if (atts.getLength()==4){
                                                            currentEvent.setMedium(atts.getValue("medium"));
                                                            currentEvent.setURL(atts.getValue("url"));
                                                        }
                                                        break;
            case SAXUtilities.TLI                   :   TimelineItem tli = new TimelineItem();
                                                        tli.setID(atts.getValue("id"));
                                                        // changed 25-02-2009
                                                        if (atts.getValue("type")!=null){
                                                            tli.setType(atts.getValue("type"));
                                                        }

                                                        try{tli.setTime(new Double(atts.getValue("time")).doubleValue());} catch (Throwable t) {/*thrown if no abs time is present*/}
                                                        tli.setBookmark(atts.getValue("bookmark"));
                                                        try{transcription.getBody().getCommonTimeline().addTimelineItem(tli);} catch (JexmaraldaException je) {}
                                                        break;
        }
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endElement(String namespaceURI, String localName, String name){               
        if (!(rootElementName.equals("basic-transcription"))) return;
        if (insideTierFormatTable){
            // pass it on to the other handler
            tierFormatTableSaxHandler.endElement(namespaceURI, localName, name);
            if (SAXUtilities.getIDForElementName(name)==SAXUtilities.TIER_FORMAT_TABLE){
                insideTierFormatTable = false;
            }
            return;
        }
        if (readOnlyHead && outsideHead) return;
        openElements.removeElementAt(openElements.size()-1);
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {            
            case SAXUtilities.HEAD                  :   outsideHead=true;
                                                        break;
            case SAXUtilities.UD_META_INFORMATION   :   transcription.getHead().getMetaInformation().setUDMetaInformation(currentUDInformation.makeCopy());
                                                        currentUDInformation.clear();
                                                        break;
            case SAXUtilities.PROJECT_NAME          :   transcription.getHead().getMetaInformation().setProjectName(currentPCData);
                                                        currentPCData = new String();
                                                        break;
            case SAXUtilities.TRANSCRIPTION_NAME    :   transcription.getHead().getMetaInformation().setTranscriptionName(currentPCData);
                                                        currentPCData = new String();        
                                                        break;
            case SAXUtilities.UD_INFORMATION        :   currentUDInformation.setAttribute(currentAttributeName,currentUDValue);
                                                        break;
            case SAXUtilities.COMMENT               :   int id2 = SAXUtilities.getIDForElementName(((String)openElements.lastElement()));
                                                        if (id2 == SAXUtilities.META_INFORMATION){
                                                            transcription.getHead().getMetaInformation().setComment(currentPCData);
                                                        } else if (id2 == SAXUtilities.SPEAKER){
                                                            currentSpeaker.setComment(currentPCData);
                                                        }
                                                        currentPCData = new String();                        
                                                        break;
            case SAXUtilities.TRANSCRIPTION_CONVENTION :  transcription.getHead().getMetaInformation().setTranscriptionConvention(currentPCData);
                                                        currentPCData = new String();                        
                                                        break;
            case SAXUtilities.SPEAKER               :   try {transcription.getHead().getSpeakertable().addSpeaker(currentSpeaker); } 
                                                        catch (JexmaraldaException je) {je.printStackTrace();}
                                                        break;
            case SAXUtilities.ABBREVIATION          :   currentSpeaker.setAbbreviation(currentPCData); 
                                                        currentPCData = new String();
                                                        break;
            case SAXUtilities.UD_SPEAKER_INFORMATION :  currentSpeaker.setUDSpeakerInformation(currentUDInformation.makeCopy()); 
                                                        currentUDInformation.clear();
                                                        break;
            case SAXUtilities.UD_TIER_INFORMATION   :   currentTier.setUDTierInformation(currentUDInformation.makeCopy());
                                                        currentUDInformation.clear();
                                                        break;
            case SAXUtilities.TIER                  :   if (currentTier.getDisplayName()==null){
                                                            currentTier.setDisplayName(currentTier.getDescription(transcription.getHead().getSpeakertable()));
                                                        }
                                                        try {transcription.getBody().addTier(currentTier); } 
                                                        catch (JexmaraldaException je) {}
                                                        break;
            case SAXUtilities.EVENT                 :   currentEvent.setDescription(currentPCData);
                                                        currentEvent.setUDEventInformation(currentUDInformation.makeCopy());
                                                        currentTier.addEvent(currentEvent);
                                                        currentUDInformation.clear();
                                                        currentPCData = new String();           
        }           
    }
        
    public void characters (char buff[], int offset, int length){
        if (!(rootElementName.equals("basic-transcription"))) return;
        if (readOnlyHead && outsideHead) return;
        if (insideTierFormatTable){
            // pass it on to the other handler
            tierFormatTableSaxHandler.characters(buff, offset, length);
        }
        String newData = new String();
        for (int pos=offset; pos<offset+length; pos++){
            if ((buff[pos]!='\n') && (buff[pos]!='\t')){
                newData+=buff[pos]; //ignore newline and tabs
            }
        }
        int id = SAXUtilities.getIDForElementName(((String)openElements.lastElement()));
        if (id==SAXUtilities.UD_INFORMATION){
            currentUDValue+=newData;
        } else {
            currentPCData+=newData;
        }
    } // end characters
            
// ----------------------------------------------------------------------------------------------------------- 
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
   


}