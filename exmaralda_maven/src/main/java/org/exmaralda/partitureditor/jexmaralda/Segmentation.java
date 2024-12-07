/*
 * Segmentation.java
 *
 * Created on 1. August 2002, 14:41
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class Segmentation extends AbstractSegmentVector implements XMLable {

    
    /** Creates new Segmentation */
    public Segmentation() {
    }
          
    public Timeable getSegmentAtStartPoint(String start) throws JexmaraldaException{
        for (int pos=0; pos<size(); pos++){
            Timeable t = (Timeable)(elementAt(pos));
            if (t.getStart().equals(start)){
                return t;
            }
        }
        throw new JexmaraldaException(112, "No segment starting at " + start);
    }
    
    
    public SegmentList getAllSegmentsWithName(String name){
        SegmentList result = new SegmentList();
        for (int pos=0; pos<getNumberOfSegments(); pos++){
            Identifiable ts = (Identifiable)elementAt(pos);
            if (ts.getName().equals(name)){
                result.addElement(ts);                
            }
            if (ts instanceof TimedSegment){
                result.addAll(((TimedSegment)(ts)).getAllSegmentsWithName(name)); 
            }
        }
        return result;
    }
    
    public HashSet getAllSegmentNames(){
        HashSet names = new HashSet();
        for (int pos=0; pos<getNumberOfSegments(); pos++){
            Identifiable ts = (Identifiable)elementAt(pos);
            names.add(ts.getName());                
            if (ts instanceof TimedSegment){
                names.addAll(((TimedSegment)(ts)).getAllSegmentNames()); 
            }
        }        
        return names;
    }
        
    
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        String [][] atts = {{"name", getName()}, {"tierref", getTierReference()}};
        sb.append(StringUtilities.makeXMLOpenElement("segmentation", atts));
        for (int pos=0; pos<getNumberOfSegments(); pos++){
            sb.append(((XMLable)elementAt(pos)).toXML());
        }
        sb.append(StringUtilities.makeXMLCloseElement("segmentation"));
        return sb.toString();        
    }    
    
    
}
