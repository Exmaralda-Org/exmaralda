/*
 * TranscriptionSearchResultItem.java
 *
 * Created on 17. Juni 2004, 12:07
 */

package org.exmaralda.zecke;

import org.exmaralda.partitureditor.jexmaralda.Describable;
import org.exmaralda.partitureditor.jexmaralda.Timeable;
import org.exmaralda.partitureditor.jexmaralda.TimedSegment;
import org.exmaralda.partitureditor.jexmaralda.TimedAnnotation;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;
/**
 *
 * @author  thomas
 */
public class AnnotationSearchResultItem extends AbstractSearchResultItem {
    
    public TimedSegment segment;
    public TimedAnnotation annotation;
    public int annotationMatchStart;
    public int annotationMatchLength;
    public String category;
    
    /** Creates a new instance of TranscriptionSearchResultItem */
    public AnnotationSearchResultItem() {
    }
    
    public AnnotationSearchResultItem(   String tn, String tp, 
                                            String tID, String sID, String sAbb,
                                            TimedSegment ts,  
                                            TimedAnnotation a, int aStart, int aLength,
                                            String c){
        transcriptionName = tn;
        transcriptionPath = tp;
        tierID = tID;
        speakerID = sID;
        speakerAbb = sAbb;
        
        segment = ts;
        
        annotation = a;
        annotationMatchStart = aStart;
        annotationMatchLength = aLength;
        
        category = c;

        calculateMatchStart();
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
    
    public String getLeftAnnotationMatchContext(){
        return annotation.getDescription().substring(0, annotationMatchStart);        
    }
    
    public String getAnnotationMatch(){
        return annotation.getDescription().substring(annotationMatchStart, annotationMatchStart + annotationMatchLength);
    }
    
    public String getRightAnnotationMatchContext(){
        return annotation.getDescription().substring(annotationMatchStart + annotationMatchLength);        
    }
    
    public String getTliID(){
        return annotation.getStart();
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
        sb.append("<annotation>" + annotation.toXML() + "</annotation>");
        sb.append("<annotation-match-start>" + Integer.toString(annotationMatchStart) + "</annotation-match-start>");
        sb.append("<annotation-match-length>" + Integer.toString(annotationMatchLength) + "</annotation-match-length>");
        sb.append("<category>" + category + "</category>");
        sb.append("</match>");
        
        return sb.toString();
    }
    
    void calculateMatchStart(){
        Vector leaves = segment.getLeaves();
        int leafCount = 0;
        boolean foundMatchStart = false;
        boolean foundMatchEnd = false;
        matchStart = 0;
        int matchEnd = 0;
        for (int pos=0; pos<leaves.size(); pos++){
            Object o = leaves.elementAt(pos);
            if (o instanceof Timeable){
                Timeable t = (Timeable)o;
                if (t.getStart().equals(annotation.getStart())){
                    foundMatchStart = true;
                }
            }
            if (!foundMatchStart) {
                matchStart += (((Describable)o).getDescription()).length();
            }
            if (!foundMatchEnd) {
                matchEnd += (((Describable)o).getDescription()).length();
            }
            if (o instanceof Timeable){
                Timeable t = (Timeable)o;
                if (t.getEnd().equals(annotation.getEnd())){
                    foundMatchEnd = true;
                }
            }            
        }
        matchLength = matchEnd - matchStart;        
    }
    
    
}
