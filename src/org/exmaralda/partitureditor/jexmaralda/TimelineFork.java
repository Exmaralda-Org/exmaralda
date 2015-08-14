/*
 * TimelineFork.java
 *
 * Created on 2. August 2002, 14:00
 */

package org.exmaralda.partitureditor.jexmaralda;

/**
 *
 * @author  Thomas
 * @version 
 */
public class TimelineFork extends Timeline implements Timeable {


    String start;
    String end;
    
    /** Creates new TimelineFork */
    public TimelineFork() {
    }

    public void setEnd(String e) {
        end = e;
    }
    
    public String getStart() {
        return start;
    }
    
    public String getEnd() {
        return end;
    }
    
    public void setStart(String s) {
        start = s;
    }
    
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        String [][] atts = {{"start", getStart()}, {"end", getEnd()}};
        sb.append(StringUtilities.makeXMLOpenElement("timeline-fork", atts));
        sb.append(super.toXML());
        sb.append(StringUtilities.makeXMLCloseElement("timeline-fork"));
        return sb.toString();        
    }    
    
    public boolean isTimed() {
        return ((start!=null) && (end!=null) && (start.length()>0) && (end.length()>0));
    }
    
    public void timeUp() {
    }

    public java.util.Hashtable indexTLIs() {
        return null;
    }
    
}
