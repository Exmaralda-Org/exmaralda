/*
 * AbstractSegmentVector.java
 *
 * Created on 2. August 2002, 14:13
 */

package org.exmaralda.partitureditor.jexmaralda;


import java.util.*;

/**
 *
 * @author  Thomas
 * @version 
 */
/** superclass of Segmentation and Annotation */
/** consists of a number of identifiable objects, i.e. TimedSegments, AtomicTimedSegments, TimedAnnotations etc. */

public class AbstractSegmentVector extends Vector {

    Hashtable id2Position;
    Hashtable start2Position;
    String name;
    String tierReference;

    /** Creates new AbstractSegmentVector */
    public AbstractSegmentVector() {
        id2Position = new Hashtable();
        start2Position = new Hashtable();
        name = new String();        
    }
    
    public String getName(){
        return name;
    }

    public void setName(String n){
        name = n;
    }
    
    public String getTierReference(){
        return tierReference;
    }
    
    public void setTierReference(String t){
        tierReference = t;
    }
    
    
    public int getNumberOfSegments(){
        return size();
    }
    
    public void addSegment(TimedSegment ts){
        this.addElement(ts);
        if (ts.getID()!=null){
            id2Position.put(ts.getID(), new Integer(size()-1));
        }
        start2Position.put(ts.getStart(), new Integer(size()-1));
    }

    public void addSegment(AtomicTimedSegment ats){
        this.addElement(ats);
        if (ats.getID()!=null){
            id2Position.put(ats.getID(), new Integer(size()-1));
        }
        start2Position.put(ats.getStart(), new Integer(size()-1));
    }

    public void addSegment(TimedAnnotation ta){
        this.addElement(ta);
        if (ta.getID()!=null){
            id2Position.put(ta.getID(), new Integer(size()-1));
        }
        start2Position.put(ta.getStart(), new Integer(size()-1));
    }
    
    
    public Identifiable getSegmentWithID(String id){
        if (id2Position.containsKey(id)){
            return (TimedSegment)elementAt(((Integer)id2Position.get(id)).intValue());
        } else {
            for (int pos=0; pos<getNumberOfSegments(); pos++){
                if (elementAt(pos) instanceof TimedSegment){
                    TimedSegment ts = (TimedSegment)elementAt(pos);
                    Identifiable i = ts.getSegmentWithID(id);
                    if (i!=null) {
                        return i;
                    }
                }
            }
        }
        return null;
    }
    
    // replaced by public AbstractSegmentVector getSegments(Timeline tl, Hashtable TLIMapping, String start, String end)
    // in Version 1.2.5.
    /*public AbstractSegmentVector getSegments(Timeline tl, String start, String end){
        AbstractSegmentVector result = new Segmentation();
        result.setName(this.getName());
        for (int pos=0; pos<size(); pos++){
            Timeable t = (Timeable)elementAt(pos);
            int s1 = tl.lookupID(t.getStart()); 
            int e1 = tl.lookupID(t.getEnd());
            int s2 = tl.lookupID(start);
            int e2 = tl.lookupID(end);
            if (((s1<=s2) && (e1>s2)) ||    //---(---
                ((s1>=s2) && (e1<=e2)) ||   //---()---
                ((s1<e2) &&  (e1>=e2)) ||   //---)---
                ((s1<=s2) && (e2<=e1)))     //(-------)
                 {
                    if (t instanceof TimedSegment) {result.addSegment((TimedSegment)t);}
                    else if (t instanceof AtomicTimedSegment) {result.addSegment((AtomicTimedSegment)t);}
                    else if (t instanceof TimedAnnotation) {result.addSegment((TimedAnnotation)t);}
                 }            
        }
        return result;
    } */       
   
    
    public AbstractSegmentVector getSegments(Timeline tl, Hashtable TLIMapping, String start, String end){
        AbstractSegmentVector result = new Segmentation();
        result.setName(this.getName());
        for (int pos=0; pos<size(); pos++){
            Timeable t = (Timeable)elementAt(pos);
            
            String mappedStart = (String)(TLIMapping.get(t.getStart()));
            String mappedEnd = (String)(TLIMapping.get(t.getEnd()));
            
            int s1 = tl.lookupID(mappedStart); 
            int e1 = tl.lookupID(mappedEnd);

            // changed 26-08-2010
            // start and end may also not be in the common timeline
            int s2 = tl.lookupID(start);
            int e2 = tl.lookupID(end);
            //int s2 = tl.lookupID((String)(TLIMapping.get(start)));
            //int e2 = tl.lookupID((String)(TLIMapping.get(end)));
            
            if (((s1<=s2) && (e1>s2)) ||    //---(---
                ((s1>=s2) && (e1<=e2)) ||   //---()---
                ((s1<e2) &&  (e1>=e2)) ||   //---)---
                ((s1<=s2) && (e2<=e1)))     //(-------)
                 {
                    if (t instanceof TimedSegment) {result.addSegment((TimedSegment)t);}
                    else if (t instanceof AtomicTimedSegment) {result.addSegment((AtomicTimedSegment)t);}
                    else if (t instanceof TimedAnnotation) {result.addSegment((TimedAnnotation)t);}
                 }            
        }
        return result;
    }        
    
    public Timeable getParentSegment(Timeline tl, String start, String end){
        int s2 = tl.lookupID(start);
        int e2 = tl.lookupID(end);
        for (int pos=0; pos<size(); pos++){
            Timeable t = (Timeable)elementAt(pos);
            int s1 = tl.lookupID(t.getStart()); 
            int e1 = tl.lookupID(t.getEnd());
            if (((s1<=s2) && (e1>s2)) ||    //---(---
                ((s1>=s2) && (e1<=e2)) ||   //---()---
                ((s1<e2) &&  (e1>=e2)) ||   //---)---
                ((s1<=s2) && (e2<=e1)))     //(-------)
                 {
                    return t;
                 }            
        }
        return null;
    }
    
    
    

}
