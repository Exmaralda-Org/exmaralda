package org.exmaralda.partitureditor.jexmaralda;

/*
 * TimelineItem.java
 *
 * Created on 6. Februar 2001, 12:06
 */



/* Revision History
 *  0   06-Feb-2001 Creation according to revision 0 of 'exmaralda-time-transcription.dtd'
 *                  and 'exmaralda-segment-transcription.dtd'
 */

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */

public class TimelineItem extends Object {

    private String id;
    private double time;
    private String type;
    private String bookmark;
    
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new TimeLineItem */
    public TimelineItem() {
        super();
        id = new String();
        time = -1.0;
        type = "unsp";
    }
    
    /** Creates new TimeLineItem with id i*/
    public TimelineItem(String i){
        super();
        id = new String(i);
        time = -0.1;
        type = "unsp";
    }
    
    /** Creates new TimeLineItem with id i and time t*/
    public TimelineItem(String i, double t){
        super();
        id = new String(i);
        time = t;
        type = "unsp";
    }

    /** returns a copy of this timeline item */
    public TimelineItem makeCopy(){
        TimelineItem result = new TimelineItem(this.getID(), this.getTime());
        result.setType(this.getType());
        result.setBookmark(this.getBookmark());
        return result;
    }
    
    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    /** returns ID */
    public String getID(){
        return id;
    }
    
    /** returns time as double value */
    public double getTime(){
        return time;
    }
    
    public String getType(){
        return type;
    }
    
    /** returns time as string */
    public String getTimeAsString(){
        return ((new Double(time)).toString());
    }
    
    /** returns time as string with soundsoviel Nachkommastellen*/
    public String getTimeAsString(int afterComma){
        String result = (new Double(time)).toString();
        if (result.indexOf('.')>0){
            result = result.substring(0,Math.min(result.length()-1,result.indexOf('.')+afterComma+1));
        }
        return result;
    }

    /** sets id to the value of i */
    public void setID (String i){
        id = i;
    }
    
    /** sets time to the value of t */
    public void setTime (double t){
        time = t;
    }
    
    public void setType (String t){
        type = t;
    }
    
    public void setBookmark (String bm){
        if ((bm!=null) && (bm.length()>0)){
            bookmark = bm;
        } else {
            bookmark = null;            
        }
    }
    
    public String getBookmark (){
        return bookmark;
    }
    
    public String getDescription(int count){
        return getDescription(count, new TimelineItemFormat());
    }
    
    public String getDescription(int count, TimelineItemFormat tlif){
        StringBuffer result = new StringBuffer();
        if ((tlif.getNthNumbering()>0) && ((count)%tlif.getNthNumbering()==0)){
            result.append(Integer.toString(count));
        }
        if ((getTime()>=0) && (tlif.getNthAbsolute()>0) && ((count)%tlif.getNthAbsolute()==0)){
            if (result.length()>0) {result.append(" ");}
            if (tlif.getAbsoluteTimeFormat().equals("decimal")){
                result.append("[" + getTimeAsString(tlif.getMilisecondsDigits()) + "]");
            } else {
                /*String timeFormat = "hh:mm:ss.";
                if (getTime()<60) {timeFormat="ss";}
                else if (getTime()<3600) {timeFormat="mm:ss";}
                else if (getTime()<36000) {timeFormat="h:mm:ss";}
                if (tlif.getMilisecondsDigits()>0) timeFormat+=".";
                for (int i=0; i<tlif.getMilisecondsDigits(); i++) {timeFormat+="x";}
                result.append("[" + TimeUtilities.makeTimeString(getTime(), timeFormat) + "]");*/
                // changed 01-12-2008
                result.append("[");
                //String text = org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(getTime()*1000.0, 1, true);
                // changed 26-02-2010
                String text = org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(getTime()*1000.0, tlif.getMilisecondsDigits(), true);
                result.append(text);
                if (getType().equals("intp")){
                    result.append("*");
                }
                result.append("]");

            }
        }
        return result.toString();
    }
    
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns an XML-element &lt;tli&gt; */
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<tli id=\"" + id + "\"");
        if (getTime()>= 0){
            sb.append(" time=\"" + getTimeAsString() +"\"");
            if ((getType().length()>0) && (!getType().equals("unsp"))){
                sb.append(" type=\"" + getType() +"\"");
            }
        }
        if ((getBookmark()!=null) && getBookmark().length()>0){
            sb.append(" bookmark=\"" + StringUtilities.toXMLString(getBookmark()) +"\"");
        }
        sb.append("/>\n");
        return sb.toString();
    }
    
    /** returns an XML-element &lt;tpr&gt; */
    public String toTimepointReferenceXML(){
        String result = new String();
        result+="<tpr id=\"" + id + "\"";
        result+="/>\n";
        return result;
    }

    /** returns an XML-element &lt;timepoint&gt; */
    public String toTimepointXML(){
        String result = new String();
        result+="<timepoint id=\"" + id + "\"";
        if (getTime()>= 0){
            result+=" time=\"" + getTimeAsString() +"\"";
        }
        result+="/>\n";
        return result;
    }
    


}