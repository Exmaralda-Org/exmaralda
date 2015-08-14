/*
 * TranscriptionSearchResultItem.java
 *
 * Created on 17. Juni 2004, 12:07
 */

package org.exmaralda.zecke;

import org.exmaralda.partitureditor.jexmaralda.TimedSegment;
import org.exmaralda.partitureditor.jexmaralda.*;
/**
 *
 * @author  thomas
 */
public class TranscriptionSearchResultItem extends AbstractSearchResultItem {
    
    public TimedSegment segment;
    
    /** Creates a new instance of TranscriptionSearchResultItem */
    public TranscriptionSearchResultItem() {
    }
    
    public TranscriptionSearchResultItem(   String tn, String tp, 
                                            String tID, String sID, String sAbb,
                                            TimedSegment ts, int start, int length){
        transcriptionName = tn;
        transcriptionPath = tp;
        tierID = tID;
        speakerID = sID;
        speakerAbb = sAbb;
        segment = ts;
        matchStart = start;
        matchLength = length;
    }
    
    public TranscriptionSearchResultItem(   String tn, String tp, 
                                            String tID, String sID, String sAbb,
                                            TimedSegment ts, int start, int length, String[][] metaData){
        transcriptionName = tn;
        transcriptionPath = tp;
        tierID = tID;
        speakerID = sID;
        speakerAbb = sAbb;
        segment = ts;
        matchStart = start;
        matchLength = length;
        additionalMetaData = metaData;
    }
    
    
    public String getLeftContext(){
        return segment.getDescription().substring(0, matchStart);
    }
    
    public String getMatch(){
        return segment.getDescription().substring(matchStart, matchStart + matchLength);
    }
    
    public String getRightContext(){
        return segment.getDescription().substring(matchStart + matchLength);        
    }
    
    public String getTliID(){
        return segment.getTLIByCharacterOffset(matchStart);
    }
    
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<match>");
        sb.append("<transcription-name>" + XMLUtilities.toXMLString(transcriptionName) + "</transcription-name>");
        sb.append("<transcription-path>" + XMLUtilities.toXMLString(transcriptionPath) + "</transcription-path>");
        sb.append("<tierID>" + XMLUtilities.toXMLString(tierID) + "</tierID>");
        sb.append("<speakerID>" + XMLUtilities.toXMLString(speakerID) + "</speakerID>");
        sb.append("<speaker-abbreviation>" + XMLUtilities.toXMLString(speakerAbb) + "</speaker-abbreviation>");
        sb.append("<segment>" + segment.toXML() + "</segment>");
        sb.append("<start>" + Integer.toString(matchStart) + "</start>");
        sb.append("<length>" + Integer.toString(matchLength) + "</length>");
        sb.append("<additional-meta-data>");
        if (additionalMetaData!=null){
            for (int pos=0; pos<this.additionalMetaData.length; pos++){
                sb.append("<meta-data key=\"" + additionalMetaData[pos][0] +"\">");
                sb.append(additionalMetaData[pos][1]);
                sb.append("</meta-data>");
            }
        }
        sb.append("</additional-meta-data>");
        sb.append("</match>");
        
        return sb.toString();
    }
    
}
