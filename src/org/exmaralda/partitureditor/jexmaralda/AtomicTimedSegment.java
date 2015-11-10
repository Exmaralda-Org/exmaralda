/*
 * AtomicTimedSegment.java
 *
 * Created on 1. August 2002, 14:29
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class AtomicTimedSegment extends AbstractSegment implements Timeable, XMLable {

    String start;
    String end;
    
    /** Creates new AtomicTimedSegment */
    public AtomicTimedSegment() {
    }

    @Override
    public AbstractSegment makeCopy() {
        AtomicTimedSegment result = new AtomicTimedSegment();
        result.setStart(this.getStart());
        result.setEnd(this.getEnd());
        result.setDescription(this.getDescription());
        return result;        
    }

    @Override
    public void setEnd(String e) {
        end = e;
    }
      
    @Override
    public String getEnd() {
        return end;
    }
    
    @Override
    public void setStart(String s) {
        start = s;
    }
    
    @Override
    public String getStart() {
        return start;
    }

    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        String [][] atts = {{"n", getName()},
                                {"id", getID()},
                                {"s", getStart()},
                                {"e", getEnd()},
                                {"medium", getMedium()},
                                {"url", getURL()}};
        sb.append(StringUtilities.makeXMLOpenElement("ats", atts));
        sb.append(StringUtilities.checkCDATA(getDescription()));
        sb.append(StringUtilities.makeXMLCloseElement("ats"));
        return sb.toString();        
    }
    
    @Override
    public boolean isTimed() {
        return ((start!=null) && (end!=null) && (start.length()>0) && (end.length()>0));
    }
    
    @Override
    public void timeUp() {
        if (getParent()==null) {return;}
        TimedSegment parent = (TimedSegment)getParent();
        for (int pos=0; pos<parent.getChildCount(); pos++){
            if (parent.getChildAt(pos) instanceof Timeable){
                if (parent.getChildAt(pos)==this){
                    parent.setStart(this.getStart());
                }
                break;
            }
        }
        for (int pos=parent.getChildCount()-1; pos>=0; pos--){
            if (parent.getChildAt(pos) instanceof Timeable){
                if (parent.getChildAt(pos)==this){
                    parent.setEnd(this.getEnd());
                }
                break;
            }
        }        
        parent.timeUp();        
    }
    
    @Override
    public Hashtable indexTLIs(){
        Hashtable result = new Hashtable();
        result.put(getStart(),this);
        result.put(getEnd(),this);
        String[] thisOne = {getStart(), getEnd()};
        result.put(thisOne, this);
        return result;
    }
    
    
}
