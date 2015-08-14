/*
 * TEISaxHandler.java
 *
 * Created on 11. August 2004, 16:17
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
//import com.sun.xml.parser.Resolver;

/**
 *
 * @author  thomas
 */
public class TEISaxHandler extends org.xml.sax.helpers.DefaultHandler {
    
    static final short TEI2 = 0;
    static final short TEI_HEADER = 1;
    static final short PROFILE_DESC = 2;
    static final short PARTIC_DESC = 3;
    static final short PERSON = 4;
    static final short TEXT = 5;
    static final short TIMELINE = 6;
    static final short WHEN = 7;
    static final short U = 8;
    static final short DIV = 9;
    static final short ANCHOR = 10;
    static final short PROSODY = 11;
    static final short PAUSE = 12;
    static final short EVENT = 13;
    static final short VOCAL = 14;
    static final short KINESIC = 15;
    static final short OTHER = 99;
    
    boolean insideSegmentDIV = false;
    String currentSpeakerID;
    String currentUStart;
    String currentUEnd;
    
    String currentEventDescription = new String();;
    Event currentEvent;
    Tier currentTier;
    
    BasicTranscription transcription;
    
    /** Creates a new instance of TEISaxHandler */
    public TEISaxHandler() {
        transcription = new BasicTranscription();
    }
    
    public BasicTranscription getTranscription(){
        return transcription;
    }
    
// ----------------------------------------------------------------------------------------------------------- 
    public void startDocument(){
      System.out.println("started reading TEI document...");
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endDocument(){
        System.out.println("TEI document read.");
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void startElement(String namespaceURI, String localName, String name, Attributes atts ) {
        //System.out.println("Opening element " + name);
        int id = getIDForElementName(name);
        switch (id) {
            case PERSON     :   String personID = atts.getValue("id");
                                Speaker newSpeaker = new Speaker();
                                newSpeaker.setID(personID);
                                newSpeaker.setAbbreviation(personID);
                                try{
                                    transcription.getHead().getSpeakertable().addSpeaker(newSpeaker);
                                } catch (JexmaraldaException je){je.printStackTrace();}
                                break;
            case WHEN       :   String tliID = atts.getValue("id");
                                TimelineItem tli = new TimelineItem();
                                tli.setID(tliID);
                                String absoluteTime = atts.getValue("absolute");
                                if (absoluteTime != null){
                                    try{
                                        double time = Double.parseDouble(absoluteTime);
                                        tli.setTime(time);
                                    } catch (NumberFormatException nfe) {}
                                }
                                try{
                                    transcription.getBody().getCommonTimeline().addTimelineItem(tli);
                                } catch (JexmaraldaException je){je.printStackTrace();}
                                break;
            case U         :    currentSpeakerID = atts.getValue("who");
                                currentUStart = atts.getValue("start");
                                currentUEnd = atts.getValue("end");
                                break;
            case DIV       :    String type = atts.getValue("type");
                                if (type.equals("segmental")){
                                    insideSegmentDIV = true;
                                    try{
                                        currentTier = transcription.getBody().getTierWithID(currentSpeakerID + "_V");
                                    } catch (JexmaraldaException je){je.printStackTrace();}
                                    currentEventDescription = "";
                                    currentEvent = new Event();
                                    currentEvent.setStart(currentUStart);
                                } else if (type.equals("prosody")){
                                    try{
                                        currentTier = transcription.getBody().getTierWithID(currentSpeakerID + "_P");
                                    } catch (JexmaraldaException je){je.printStackTrace();}
                                } 
                                break;
            case ANCHOR     :   String synch = atts.getValue("synch");
                                currentEvent.setEnd(synch);
                                currentEvent.setDescription(currentEventDescription);
                                currentTier.addEvent(currentEvent);
                                currentEventDescription = "";
                                currentEvent = new Event();
                                currentEvent.setStart(synch);
                                break;                                
            case PROSODY    :   String start = atts.getValue("start");
                                String end = atts.getValue("end");
                                String description = atts.getValue("feature") + ": " + atts.getValue("desc");
                                Event newProsodyEvent = new Event(start, end, description);
                                currentTier.addEvent(newProsodyEvent);
                                break;
            case VOCAL      :
            case EVENT      :
            case KINESIC    :   String openMark = getOpenMark(id);
                                String closeMark = getCloseMark(id);
                                if (insideSegmentDIV){
                                    currentEventDescription+=openMark;
                                    currentEventDescription+=atts.getValue("desc");
                                    currentEventDescription+=closeMark;
                                } else {
                                    currentSpeakerID = atts.getValue("who");
                                    try{
                                        if (currentSpeakerID!=null){
                                            currentTier = transcription.getBody().getTierWithID(currentSpeakerID + "_E");
                                        } else {
                                            currentTier = transcription.getBody().getTierWithID("_E");
                                        }
                                    } catch (JexmaraldaException je){je.printStackTrace();}
                                    String start2 = atts.getValue("start");
                                    String end2 = atts.getValue("end");
                                    String description2 = openMark + atts.getValue("desc") + closeMark;
                                    Event newDescriptionEvent = new Event(start2, end2, description2);
                                    currentTier.addEvent(newDescriptionEvent);
                                    System.out.println(newDescriptionEvent.toXML() + " added to " + currentTier.toXML());
                                }                                    
                                break;
            case PAUSE    :     String pauseDescription = "";
                                if (atts.getValue("dur")!=null){
                                    pauseDescription = atts.getValue("dur");
                                } else if (atts.getValue("type")!=null){
                                    pauseDescription = atts.getValue("type");
                                } else if (atts.getValue("desc")!=null){
                                    pauseDescription = atts.getValue("desc");
                                }
                                if (insideSegmentDIV){
                                    currentEventDescription+= "<" + pauseDescription + ">";
                                } else {
                                    currentSpeakerID = atts.getValue("who");
                                    try{
                                        if (currentSpeakerID!=null){
                                            currentTier = transcription.getBody().getTierWithID(currentSpeakerID + "_E");
                                        } else {
                                            currentTier = transcription.getBody().getTierWithID("_E");
                                        }
                                    } catch (JexmaraldaException je){je.printStackTrace();}
                                    String start3 = atts.getValue("start");
                                    String end3 = atts.getValue("end");
                                    String description3 = "<" + pauseDescription + ">";
                                    Event newPauseEvent = new Event(start3, end3, description3);
                                    currentTier.addEvent(newPauseEvent);
                                }                                    
                                break;

        }
    }

    public void endElement(String namespaceURI, String localName, String name){      
        //System.out.println("Closing element " + name);
        int id = getIDForElementName(name);
        switch (id) {
            case PARTIC_DESC    :   makeTiers();
                                    break;
            case DIV            :   if (insideSegmentDIV){
                                        currentEvent.setEnd(currentUEnd);
                                        currentEvent.setDescription(currentEventDescription);
                                        currentTier.addEvent(currentEvent);
                                        insideSegmentDIV = false;
                                    }
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
        currentEventDescription+=newData;
        //System.out.println("Desc: " + currentEventDescription);
    } // end characters

// ----------------------------------------------------------------------------------------------------------- 
    public void error (SAXParseException e) throws SAXParseException {
        System.out.println("Error: " + e.getMessage());
        throw e;
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void setDocumentLocator (Locator l) {
    }


// ----------------------------------------------------------------------------------------------------------- 
    public void ignorableWhitespace (char buf [], int offset, int len) throws SAXException {
        // do nothing
    }
    

    private short getIDForElementName(String elementName){
        if (elementName.equals("TEI.2")) return TEI2;
        if (elementName.equals("teiHeader")) return TEI_HEADER;
        if (elementName.equals("profileDesc")) return PROFILE_DESC;
        if (elementName.equals("particDesc")) return PARTIC_DESC;
        if (elementName.equals("person")) return PERSON;
        if (elementName.equals("text")) return TEXT;
        if (elementName.equals("timeline")) return TIMELINE;
        if (elementName.equals("when")) return WHEN;
        if (elementName.equals("u")) return U;
        if (elementName.equals("div")) return DIV;
        if (elementName.equals("anchor")) return ANCHOR;
        if (elementName.equals("vocal")) return VOCAL;
        if (elementName.equals("event")) return EVENT;
        if (elementName.equals("kinesic")) return KINESIC;
        if (elementName.equals("pause")) return PAUSE;
        if (elementName.equals("prosody")) return PROSODY;
        return OTHER;
    }
    
    private void makeTiers(){
        
        System.out.println("Making tiers");
        String[] speakerIDs = transcription.getHead().getSpeakertable().getAllSpeakerIDs();
        for (int pos=0; pos<speakerIDs.length; pos++){
            //isctd
            String speakerID = speakerIDs[pos];
            Tier prosodyTier = new Tier(speakerID + "_P", speakerID , "p" , "a" , speakerID + " [p]");
            Tier verbalTier = new Tier(speakerID + "_V", speakerID , "v" , "t" , speakerID + " [v]");
            Tier eventTier = new Tier(speakerID + "_E", speakerID , "e" , "d" , speakerID + " [e]");
            try{
                transcription.getBody().addTier(prosodyTier);
                transcription.getBody().addTier(verbalTier);
                transcription.getBody().addTier(eventTier);
            } catch (JexmaraldaException je){je.printStackTrace();}
        }
        Tier noSpeakerTier = new Tier("_E", null, "e", "d", " [e]");
        try{
            transcription.getBody().addTier(noSpeakerTier);
        } catch (JexmaraldaException je){}
    }
    
    private String getOpenMark(int tagID){
        if (tagID==VOCAL) return "[";
        if (tagID==EVENT) return "{";
        if (tagID==KINESIC) return "(";
        return "";
    }
    
    private String getCloseMark(int tagID){
        if (tagID==VOCAL) return "]";
        if (tagID==EVENT) return "}";
        if (tagID==KINESIC) return ")";
        return "";
    }
}
