/*
 * SegmentedTranscriptionSaxHandler.java
 *
 * Created on 6. August 2002, 12:28
 */

package org.exmaralda.partitureditor.jexmaralda.sax;

import org.exmaralda.partitureditor.jexmaralda.*;
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
public class SegmentedTranscriptionSaxHandler extends org.xml.sax.helpers.DefaultHandler {

    private SegmentedTranscription transcription;
    private Vector openElements;
    private String currentPCData;
    private String currentAttributeName;
    private String currentType;
    private String currentUDValue;
    private UDInformationHashtable currentUDInformation;
    private Speaker currentSpeaker;
    
    private SegmentedTier currentTier;
    private AbstractSegmentVector currentSegmentVector;
    private TimelineFork currentTimelineFork;
    private TimedAnnotation currentTA;
    private Vector segmentStack;
    
    /** Creates new SegmentedTranscriptionSaxHandler */
    public SegmentedTranscriptionSaxHandler() {
        openElements = new Vector();
    }
    
    public SegmentedTranscription getTranscription(){
        return transcription;
    }
    
    public void startDocument(){
      System.out.println("started reading document...");
      transcription = new SegmentedTranscription();
      currentPCData = new String();
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endDocument(){
        System.out.println("document read.");
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void startElement(String namespaceURI, String localName, String name, Attributes atts ) {        
//        System.out.println("element " + name + " open.");
        openElements.addElement(name);
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {
            case SAXUtilities.UD_META_INFORMATION   :   currentUDInformation = new UDInformationHashtable(); 
                                                        break;
            case SAXUtilities.REFERENCED_FILE       :   transcription.getHead().getMetaInformation().addReferencedFile(atts.getValue(0));
                                                        break;
            case SAXUtilities.UD_INFORMATION        :   currentAttributeName = new String(atts.getValue("attribute-name"));
                                                        currentUDValue = new String();
                                                        break;
            case SAXUtilities.SPEAKER               :   currentSpeaker = new Speaker();
                                                        currentSpeaker.setID(atts.getValue("id"));
                                                        break;
            case SAXUtilities.SEX                   :   currentSpeaker.setSex(atts.getValue("value").charAt(0));
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

            case SAXUtilities.TLI                   :   TimelineItem tli = new TimelineItem();
                                                        tli.setID(atts.getValue("id"));
                                                        try{tli.setTime(new Double(atts.getValue("time")).doubleValue());} catch (Throwable t) {/*thrown if no abs time is present*/}
                                                        tli.setBookmark(atts.getValue("bookmark"));
                                                        try{
                                                            if (((String)(openElements.elementAt(openElements.size()-2))).equals("common-timeline")){
                                                                transcription.getBody().getCommonTimeline().addTimelineItem(tli);
                                                            } else {
                                                                currentTimelineFork.addTimelineItem(tli);
                                                            }
                                                        } catch (JexmaraldaException je) {}
                                                        break;
            case SAXUtilities.SEGMENTED_TIER        :   currentTier = new SegmentedTier();
                                                        currentTier.setID(atts.getValue("id"));
                                                        currentTier.setCategory(atts.getValue("category"));
                                                        currentTier.setType(atts.getValue("type"));
                                                        if (atts.getIndex("speaker")>=0){
                                                            currentTier.setSpeaker(atts.getValue("speaker"));
                                                        }
                                                        break;
            case SAXUtilities.TIMELINE_FORK         :   currentTimelineFork = new TimelineFork();
                                                        currentTimelineFork.setStart(atts.getValue("start"));
                                                        currentTimelineFork.setEnd(atts.getValue("end"));
                                                        break;
            case SAXUtilities.SEGMENTATION          :   currentSegmentVector = new Segmentation();
                                                        currentSegmentVector.setName(atts.getValue("name"));
                                                        currentSegmentVector.setTierReference(atts.getValue("tierref"));
                                                        segmentStack = new Vector();
                                                        break;
            case SAXUtilities.ANNOTATION            :   currentSegmentVector = new Annotation();
                                                        currentSegmentVector.setName(atts.getValue("name"));
                                                        currentSegmentVector.setTierReference(atts.getValue("tierref"));
                                                        break;
            case SAXUtilities.TS                    :   TimedSegment ts = new TimedSegment();
                                                        currentPCData = new String();
                                                        ts.setName(atts.getValue("n"));
                                                        if (atts.getIndex("id")>=0){
                                                            ts.setID(atts.getValue("id"));
                                                        }
                                                        ts.setStart(atts.getValue("s"));
                                                        ts.setEnd(atts.getValue("e"));
                                                        segmentStack.addElement(ts);
                                                        break;
            case SAXUtilities.ATS                   :   AtomicTimedSegment ats = new AtomicTimedSegment();                                            
                                                        currentPCData = new String();
                                                        ats.setName(atts.getValue("n"));
                                                        if (atts.getIndex("id")>=0){
                                                            ats.setID(atts.getValue("id"));
                                                        }
                                                        ats.setStart(atts.getValue("s"));
                                                        ats.setEnd(atts.getValue("e"));
                                                        segmentStack.addElement(ats);
                                                        break;                                                        
            case SAXUtilities.NTS                   :   NonTimedSegment nts = new NonTimedSegment();                                            
                                                        currentPCData = new String();
                                                        nts.setName(atts.getValue("n"));
                                                        if (atts.getIndex("id")>=0){
                                                            nts.setID(atts.getValue("id"));
                                                        }
                                                        segmentStack.addElement(nts);
                                                        break;                                                        
            case SAXUtilities.TA                    :   currentTA = new TimedAnnotation();
                                                        currentPCData = new String();
                                                        if (atts.getIndex("n")>=0){
                                                            currentTA.setID(atts.getValue("n"));
                                                        }
                                                        if (atts.getIndex("id")>=0){
                                                            currentTA.setID(atts.getValue("id"));
                                                        }
                                                        if (atts.getIndex("medium")>=0){
                                                            currentTA.setMedium(atts.getValue("medium"));
                                                        }
                                                        if (atts.getIndex("url")>=0){
                                                            currentTA.setURL(atts.getValue("url"));
                                                        }
                                                        if (atts.getIndex("ref-id")>=0){
                                                            currentTA.setRefID(atts.getValue("ref-id"));
                                                        }
                                                        currentTA.setStart(atts.getValue("s"));
                                                        currentTA.setEnd(atts.getValue("e"));
                                                        break;
            case SAXUtilities.CONVERSION_INFO       :   transcription.setConversionInfo(new ConversionInfo());
                                                        break;
            case SAXUtilities.CONVERSION_TLI        :   if (atts.getIndex("id")>=0){                                                            
                                                            String tid = atts.getValue("id");
                                                            TimelineItem newTli = new TimelineItem();
                                                            newTli.setID(tid);
                                                            try{
                                                                transcription.getConversionInfo().getTimeline().addTimelineItem(newTli);
                                                            } catch (JexmaraldaException je){
                                                                // do nothing
                                                            }
                                                        }
                                                        break;
            case SAXUtilities.CONVERSION_TIER       :   String s1 = atts.getValue("segmented-tier-id");
                                                        String s2 = atts.getValue("name");
                                                        String s3 = atts.getValue("category");
                                                        String s4 = atts.getValue("display-name");
                                                        String s5 = atts.getValue("type");
                                                        String[] info = {s1,s2,s3,s4,s5};
                                                        transcription.getConversionInfo().addTierConversionInfo(info);
                                                        break;
        }
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endElement(String namespaceURI, String localName, String name){               
        openElements.removeElementAt(openElements.size()-1);
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {
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
                                                        
            case SAXUtilities.SEGMENTED_TIER         :  try {transcription.getBody().addTier(currentTier);}
                                                        catch (JexmaraldaException je) {}
                                                        break;
            case SAXUtilities.TIMELINE_FORK          :  currentTier.addTimelineFork(currentTimelineFork);
                                                        break;
            case SAXUtilities.SEGMENTATION          :   currentTier.addElement(currentSegmentVector);
                                                        break;
            case SAXUtilities.ANNOTATION            :   currentTier.addElement(currentSegmentVector);
                                                        break;
            case SAXUtilities.TS                    :   TimedSegment ts = ((TimedSegment)(segmentStack.lastElement()));
                                                        if (currentPCData.length()>0){
                                                            ts.setDescription(currentPCData);
                                                        }
                                                        currentPCData = new String();
                                                        int stackSize = segmentStack.size();
                                                        if (stackSize==1){
                                                            ((Segmentation)currentSegmentVector).addSegment(ts);
                                                        } else if (segmentStack.elementAt(stackSize-2) instanceof TimedSegment){
                                                            ((TimedSegment)(segmentStack.elementAt(stackSize-2))).add(ts);
                                                        }
                                                        segmentStack.removeElementAt(stackSize-1);
                                                        break;
            case SAXUtilities.ATS                    :  AtomicTimedSegment ats = ((AtomicTimedSegment)(segmentStack.lastElement()));
                                                        ats.setDescription(currentPCData);
                                                        currentPCData = new String();
                                                        int stackSize2 = segmentStack.size();
                                                        if (stackSize2==1){
                                                            ((Segmentation)currentSegmentVector).addSegment(ats);
                                                        } else if (segmentStack.elementAt(stackSize2-2) instanceof TimedSegment){
                                                            ((TimedSegment)(segmentStack.elementAt(stackSize2-2))).add(ats);
                                                        }
                                                        segmentStack.removeElementAt(stackSize2-1);
                                                        break;
            case SAXUtilities.NTS                    :  NonTimedSegment nts = ((NonTimedSegment)(segmentStack.lastElement()));
                                                        nts.setDescription(currentPCData);
                                                        currentPCData = new String();
                                                        int stackSize3 = segmentStack.size();
                                                        if (stackSize3==1){
                                                            // this is an error!!
                                                        } else if (segmentStack.elementAt(stackSize3-2) instanceof TimedSegment){
                                                            ((TimedSegment)(segmentStack.elementAt(stackSize3-2))).add(nts);
                                                        }
                                                        segmentStack.removeElementAt(stackSize3-1);
                                                        break;
            case SAXUtilities.TA                    :   currentTA.setDescription(currentPCData);
                                                        currentPCData = new String();
                                                        currentSegmentVector.addElement(currentTA);
                                                        break;
        }           
    }
        
    public void characters (char buff[], int offset, int length){
        StringBuffer newData = new StringBuffer();
        for (int pos=offset; pos<offset+length; pos++){
            if ((buff[pos]!='\n') && (buff[pos]!='\t')){
                newData.append(buff[pos]); //ignore newline and tabs
            }
        }
        int id = SAXUtilities.getIDForElementName(((String)openElements.lastElement()));
        if (id==SAXUtilities.UD_INFORMATION){
            currentUDValue+=newData.substring(0);
        } else {
            currentPCData+=newData.substring(0);
        }
    } // end characters
            
// ----------------------------------------------------------------------------------------------------------- 
    public void error (SAXParseException e) throws SAXParseException {
        System.out.println("Error: " + e.getMessage());
        throw e;
    }
    
    

// ----------------------------------------------------------------------------------------------------------- 
    public void setDocumentLocator (Locator l){
    }


// ----------------------------------------------------------------------------------------------------------- 
    public void ignorableWhitespace (char buf [], int offset, int len)
    throws SAXException {
        // do nothing
    }
   
    

}
