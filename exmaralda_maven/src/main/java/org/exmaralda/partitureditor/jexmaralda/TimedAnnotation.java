/*
 * TimedAnnotation.java
 *
 * Created on 2. August 2002, 14:22
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class TimedAnnotation implements Identifiable, Timeable, Describable, XMLable {

    String start;
    String end;
    String description;    
    String name;
    String id;
    String refID;

    private String medium = "none";
    private String URL;


    /** Creates new TimedAnnotation */
    public TimedAnnotation() {
    }

    public void setEnd(String e) {
        end = e;
    }
    
    public void setID(String i) {
        id = i;
    }
    
    public String getID() {
        return id;
    }
    
    public void setRefID(String i) {
        refID = i;
    }

    public String getRefID() {
        return refID;
    }

    public String getStart() {
        return start;
    }
    
    public void setName(String n) {
        name = n;
    }
    
    public String getEnd() {
        return end;
    }
    
    public void setStart(String s) {
        start = s;
    }
    
    public String getName() {
        return name;
    }
    
    public void setDescription(String d) {
        description = d;
    }
    
    public String getDescription() {
        return description;
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

    
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        if (refID==null){
            if (medium.equals("none")){
                String [][] atts = {{"n", getName()},
                                        {"id", getID()},
                                        {"s", getStart()},
                                        {"e", getEnd()}};
                sb.append(StringUtilities.makeXMLOpenElement("ta", atts));
            } else {
                String [][] atts = {{"n", getName()},
                                    {"id", getID()},
                                    {"s", getStart()},
                                    {"e", getEnd()},
                                    {"medium", getMedium()},
                                    {"url", getURL()},
                };
                sb.append(StringUtilities.makeXMLOpenElement("ta", atts));
            }
        } else {
            if (medium.equals("none")){
                String [][] atts = {{"n", getName()},
                                        {"id", getID()},
                                        {"ref-id", getRefID()},
                                        {"s", getStart()},
                                        {"e", getEnd()}};
                sb.append(StringUtilities.makeXMLOpenElement("ta", atts));
            } else {
                String [][] atts = {{"n", getName()},
                                    {"id", getID()},
                                    {"ref-id", getRefID()},
                                    {"s", getStart()},
                                    {"e", getEnd()},
                                    {"medium", getMedium()},
                                    {"url", getURL()},
                };
                sb.append(StringUtilities.makeXMLOpenElement("ta", atts));
            }
        }
        sb.append(StringUtilities.checkCDATA(getDescription()));
        sb.append(StringUtilities.makeXMLCloseElement("ta"));
        return sb.toString();                
    }
    
    public boolean isTimed() {
        return ((start!=null) && (end!=null) && (start.length()>0) && (end.length()>0));        
    }
    
    public void timeUp() {
    }
    
    public Identifiable makeCopy() {
        TimedAnnotation result = new TimedAnnotation();
        result.setDescription(this.getDescription());
        result.setStart(this.getStart());
        result.setEnd(this.getEnd());
        result.setName(this.getName());
        result.setID(this.getID());
        result.setMedium(this.getMedium());
        result.setURL(this.getURL());
        return result;
    }

    public java.util.Hashtable indexTLIs() {
        Hashtable result = new Hashtable();
        result.put(getStart(),this);
        result.put(getEnd(),this);
        String[] thisOne = {getStart(), getEnd()};
        result.put(thisOne, this);
        return result;
    }
    
}
