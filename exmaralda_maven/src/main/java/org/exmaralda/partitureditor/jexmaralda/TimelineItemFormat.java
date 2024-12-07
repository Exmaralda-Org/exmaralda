/*
 * TimeLineItemFormat.java
 *
 * Created on 6. Juni 2002, 14:13
 */

package org.exmaralda.partitureditor.jexmaralda;

/**
 *
 * @author  Thomas
 * @version 
 */
public class TimelineItemFormat {

    private short nthNumbering = 1;
    private short nthAbsolute = 1;
    private String absoluteTimeFormat = "decimal";
    short milisecondsDigits = 1;
    
    /** Creates new TimeLineItemFormat */
    public TimelineItemFormat() {
        nthNumbering = 1;
        nthAbsolute = 1;
        absoluteTimeFormat = "time";
        milisecondsDigits = 1;
    }
    
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<timeline-item-format ");
        sb.append("show-every-nth-numbering=\"" + Short.toString(nthNumbering) + "\" ");
        sb.append("show-every-nth-absolute=\"" + Short.toString(nthAbsolute) + "\" ");
        sb.append("absolute-time-format=\"" + absoluteTimeFormat + "\" ");
        sb.append("miliseconds-digits=\"" + Short.toString(milisecondsDigits) + "\"");
        sb.append("/>");
        return sb.toString();
    }
    
    public short getNthNumbering(){
        return nthNumbering;
    }

    public void setNthNumbering(short n){
        nthNumbering = n;
    }
    
    public void setNthAbsolute(short n){
        nthAbsolute = n;
    }
    
    public short getNthAbsolute(){
        return nthAbsolute;
    }

    public void setAbsoluteTimeFormat(String f){
        absoluteTimeFormat = f;
    }
    
    public String getAbsoluteTimeFormat(){
        return absoluteTimeFormat;
    }

    public void setMilisecondsDigits(short d){
        milisecondsDigits = d;
    }
    
    public short getMilisecondsDigits(){
        return milisecondsDigits;
    }
        

}
