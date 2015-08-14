package org.exmaralda.partitureditor.jexmaralda;

import java.io.*;
import java.net.URI;
/*
 * Event.java
 *
 * Created on 5. Maerz 2001, 11:06
 */



/**
 * Class corresponding to an event in an EXMARaLDA Basic Transcription
 * @author  Thomas
 * @version 
 */

public class Event extends Object implements Timeable, Describable, Linkable {

    private String start;
    private String end;
    private String description;
    private String medium;
    private String URL;
    private UDInformationHashtable udEventInformation; 
    
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new Event */
    public Event() {
        start = new String();
        end = new String();
        description = new String();
        medium = new String("none");
        URL = new String();
        udEventInformation = new UDInformationHashtable();
    }

    /** Creates new Event with start point s, end point e and description d*/
    public Event(String s, String e, String d) {
        start = s;
        end = e;
        description = d;
        medium = "none";
        URL = new String();
        udEventInformation = new UDInformationHashtable();
    }

    /** Creates new Event with start point s, end point e, description d, medium m and URL u*/
    public Event(String s, String e, String d, String m, String u) {
        start = s;
        end = e;
        description = d;
        medium = m;
        URL = new String(u);
        udEventInformation = new UDInformationHashtable();
    }

    /** returns a copy of this event */
    public Event makeCopy() {
        Event newEvent = new Event(this.getStart(), this.getEnd(), this.getDescription(), this.getMedium(), this.getURL());
        newEvent.setUDEventInformation(this.getUDEventInformation().makeCopy());
        return newEvent;
    }
    
    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    /** returns the id of the start point of this event */
    public String getStart(){
        return start;
    }
    
    /** sets the start point to the specified value */
    public void setStart(String s){
        start = new String(s);
    }
    
    /** returns the id of the end point of this event */
    public String getEnd(){
        return end;
    }
    
    /** sets the end point of this event to the specified value */
    public void setEnd(String e){
        end = new String(e);
    }
    
    /** returns the description of this event */
    public String getDescription(){
        return description;
    }
    
    /** sets the description of this event to the specified value */
    public void setDescription(String d){
        description = new String(d);
    }

    /** returns the link medium of this event */
    public String getMedium(){
        return medium;
    }
    
    /** sets the link medium of this event to the specified value */
    public void setMedium(String m){
        medium = m;
    }
    
    /** returns the link URL of this event */
    public String getURL(){
        return URL;
    }
    
    /** sets the link URL of this event to the specified value */
    public void setURL(String u){
        URL = u;
    }
    
    public boolean relativizeLink(String relativeToWhat){
        if ((URL==null) || (URL.length()==0)) return false;
        try{
            URI uri1 = new File(URL).toURI();
            URI uri2 = new File(relativeToWhat).getParentFile().toURI();
            URI relativeURI = uri2.relativize(uri1);
            URL = relativeURI.toString();            
        } catch (Exception e){
            System.out.println("Error relativizing " + URL);
            e.printStackTrace();
            // do nothing
            return false;
        }
        return true;
    }
    
    public boolean resolveLink(String relativeToWhat){
        if ((URL==null) || (URL.length()==0)) return false;
        try{
            if (new File(URL).isAbsolute()) return false;
            URI uri2 = new File(relativeToWhat).getParentFile().toURI();
            URI absoluteURI = uri2.resolve(URL);
            URL = new File(absoluteURI).getAbsolutePath();
        } catch (Exception e){
            System.out.println("Error resolving " + URL);
            e.printStackTrace();
            // do nothing
            return false;
        }
        return true;
    }
    
    
    public UDInformationHashtable getUDEventInformation(){
        return udEventInformation;
    }
    
    public void setUDEventInformation(UDInformationHashtable info){
        udEventInformation = info;
    }

    // ********************************************
    // ********** BASIC MANIPULATION **************
    // ********************************************

    /** checks the well-formedness of this event, i.e.
     * everything that is not covered by the DTD */    
    public void check (Timeline timeline) throws JexmaraldaException{
        if (timeline.before(getEnd(), getStart())){
            throw new JexmaraldaException(52, "End time before start time : " + getStart() + " " + getEnd() );}            
    }
    
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns the event as an XML element &lt;event&gt; as
     *  specified in the corresponding dtd */
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<event ");
        sb.append("start=\"" + getStart() +"\" ");
        sb.append("end=\"" + getEnd() + "\"");
        if (!medium.equals("none")){
            sb.append(" medium=\"" + getMedium() + "\" ");
            sb.append("url=\"" + getURL() + "\"");
        }
        sb.append(">");
        sb.append(udEventInformation.toXML());
        sb.append(StringUtilities.checkCDATA(getDescription()));
        sb.append("</event>\n");
        return sb.toString();        
    }
    
    /** writes an XML element &lt;event&gt; to the specified
     *  file output stream */
    public void writeXML(FileOutputStream fos) throws IOException {
        fos.write(toXML().getBytes("UTF-8"));
    }

    // ********************************************
    // ******************** CONVERSIONS ***********
    // ********************************************
        
    /** converts this event to a timed segment, i.e. to an object
     * corresponding to an &lt;ts&gt; element in an EXMARaLDA segmented 
     * transcription */
    TimedSegment toTimedSegment(){
       TimedSegment ts = new TimedSegment();
       ts.setStart(this.getStart());
       ts.setEnd(this.getEnd());
       ts.setName("e");
       ts.setDescription(this.getDescription());
       if (!getMedium().equals("none")){       
           ts.setMedium(this.getMedium());
           ts.setURL(this.getURL());
       }
       return ts;
   }
   
    /** converts this event to an atomic timed segment, i.e. to an object
     * corresponding to an &lt;ats&gt; element in an EXMARaLDA segmented 
     * transcription */
    AtomicTimedSegment toAtomicTimedSegment(){
       AtomicTimedSegment ats = new AtomicTimedSegment();
       ats.setStart(this.getStart());
       ats.setEnd(this.getEnd());
       ats.setName("e");
       ats.setDescription(this.getDescription());
       if (!getMedium().equals("none")){       
           ats.setMedium(this.getMedium());
           ats.setURL(this.getURL());
       }
       return ats;
   }
   
    /** converts this event to a timed annotation, i.e. to an object
     * corresponding to an &lt;ta&gt; element in an EXMARaLDA segmented 
     * transcription */
    TimedAnnotation toTimedAnnotation(){
       TimedAnnotation ta = new TimedAnnotation();
       ta.setStart(this.getStart());
       ta.setEnd(this.getEnd());
       ta.setDescription(this.getDescription());
       // 10-12-2010: set links
       ta.setMedium(this.getMedium());
       ta.setURL(this.getURL());
       return ta;
   }

    
    /** returns true iff this event has been assigned a start and an end point */
    public boolean isTimed() {
        return ((start!=null) && (end!=null) && (start.length()>0) && (end.length()>0));        
    }
    
    public void timeUp() {
    }

    public java.util.Hashtable indexTLIs() {
        java.util.Hashtable result = new java.util.Hashtable();
        result.put(getStart(),this);
        result.put(getEnd(),this);
        String[] thisOne = {getStart(), getEnd()};
        result.put(thisOne, this);
        return result;        
    }
    
}