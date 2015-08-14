/*
 * TranscriptionSearchResultItem.java
 *
 * Created on 17. Juni 2004, 12:07
 */

package org.exmaralda.zecke;

import org.exmaralda.partitureditor.jexmaralda.AtomicTimedSegment;
import org.exmaralda.partitureditor.jexmaralda.*;
/**
 *
 * @author  thomas
 */
public class DescriptionSearchResultItem extends AbstractSearchResultItem {
    
    public AtomicTimedSegment segment;
    public String category;
    
    /** Creates a new instance of TranscriptionSearchResultItem */
    public DescriptionSearchResultItem() {
    }
    
    public DescriptionSearchResultItem(   String tn, String tp, 
                                            String tID, String sID, String sAbb,
                                            AtomicTimedSegment ats, int start, int length, String c){
        transcriptionName = tn;
        transcriptionPath = tp;
        tierID = tID;
        speakerID = sID;
        speakerAbb = sAbb;
        segment = ats;
        matchStart = start;
        matchLength = length;
        category = c;
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
        return segment.getStart();
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
        sb.append("<category>" + category + "</category>");
        sb.append("</match>");
        
        return sb.toString();
    }
    
}
