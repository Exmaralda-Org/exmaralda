/*
 * CHATSegmentation.java
 *
 * Created on 23. Juli 2003, 12:59
 */

package org.exmaralda.partitureditor.jexmaralda.segment;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.fsm.*;
import java.util.*;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class StadtspracheSegmentation {
    
    
    //String SPLIT_EXPRESSION =   "((\\(\\(.+\\)\\))[^\\p{Lu}\\p{Ll}-]*|[^\\p{Lu}\\p{Ll}-]+)";
    String SPLIT_EXPRESSION =   "[\\p{L}-]+|((\\(\\([^\\)]+\\)\\)))";
    java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile(SPLIT_EXPRESSION);
    
    /** Creates a new instance of StadtspracheSegmentation */
    public StadtspracheSegmentation() {
    }
    
    public BasicTranscription wordSegmentation( BasicTranscription bt,
                                                String startTLI,
                                                String endTLI,
                                                String tierID) throws JexmaraldaException{
        return wordSegmentation(bt,startTLI,endTLI,tierID,true);
    }

    public BasicTranscription wordSegmentation( BasicTranscription bt, 
                                                String startTLI, 
                                                String endTLI, 
                                                String tierID,
                                                boolean addVariableTier) throws JexmaraldaException{
        
         BasicTranscription result = bt.getPartOfTranscription(bt.getBody().getAllTierIDs(), startTLI, endTLI);
         Tier tier = result.getBody().getTierWithID(tierID);
         Vector<Event> toBeAdded = new Vector<Event>();
         for (int pos=0; pos<tier.getNumberOfEvents(); pos++){
             Event event = tier.getEventAt(pos);
             //tier.removeEventAtStartPoint(event.getStart());
             int startPos = result.getBody().getCommonTimeline().lookupID(event.getStart());
             int endPos = result.getBody().getCommonTimeline().lookupID(event.getEnd());
             if ((endPos-startPos)>1){
                 throw new JexmaraldaException("Es gibt Ereignisse, die mehr als ein Intervall umfassen.");
             }
             String desc = event.getDescription();
             java.util.regex.Matcher matcher = PATTERN.matcher(desc);
             //int lastStartIndex = 0;
             String lastEndTLI = event.getStart();
             while (matcher.find()){
                 int start = matcher.start();
                 int end = matcher.end();
                 if (end<desc.length()) end++;
                 while ((end<desc.length()) && !(Character.isLetter(desc.charAt(end)))) end++;
                 String token = desc.substring(start,end);
                 if (token.endsWith("((")) token=token.substring(0, token.length()-2);
                 Event newEvent = new Event();
                 newEvent.setStart(lastEndTLI);
                 if (end<desc.length()-1){
                    String newTLIID = result.getBody().getCommonTimeline().insertTimelineItemAfter(lastEndTLI);
                    newEvent.setEnd(newTLIID);
                    lastEndTLI = newTLIID;
                 } else{
                     newEvent.setEnd(event.getEnd());
                 }                 
                 newEvent.setDescription(token);
                 toBeAdded.add(newEvent);
                 //tier.addEvent(newEvent);
                 //lastStartIndex = end;
             }             
         }
         
         tier.removeAllEvents();
         for (Event e : toBeAdded){
             tier.addEvent(e);
         }         
         
         if (addVariableTier){
             String newTierID = result.getBody().getFreeID();
             Tier newTier = new Tier();
             newTier.setSpeaker(tier.getSpeaker());
             newTier.setCategory("variable");
             newTier.setType("a");
             newTier.setID(newTierID);
             newTier.setDisplayName(newTier.getDescription(result.getHead().getSpeakertable()));
             result.getBody().insertTierAt(newTier, result.getBody().lookupID(tier.getID())+1);

             UDInformationHashtable udinfo = result.getHead().getMetaInformation().getUDMetaInformation();
             Calendar cal = Calendar.getInstance(TimeZone.getDefault());
             String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
             java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
             sdf.setTimeZone(TimeZone.getDefault());
             udinfo.setAttribute("Entnahme:Datum", sdf.format(cal.getTime()));
             udinfo.setAttribute("Entnahme:Start", startTLI);
             udinfo.setAttribute("Entnahme:Ende", endTLI);
             udinfo.setAttribute("Entnahme:Spur", tierID);
         }
         
         return result;
    }
    
    

    
}
