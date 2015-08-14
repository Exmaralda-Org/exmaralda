/*
 * Format.java
 *
 * Created on 26. Februar 2002, 10:06
 */

package org.exmaralda.partitureditor.interlinearText;

import java.util.*;

/**
 *
 * @author  thomas.schmidt@uni-hamburg.de
 * @version 1.0.2
 * class for storing formatting information
 */
public class Formats extends Vector implements XMLElement, HTMLable {

    private Hashtable positions;
    Format STANDARD_FORMAT;
    
    /** Creates new Format */
    public Formats() {
        positions = new Hashtable();
        STANDARD_FORMAT = new Format();
        STANDARD_FORMAT.setID("STANDARD");
        addFormat(STANDARD_FORMAT);                
    }

    /** returns the number of formats contained in this Object */
     public int getNumberOfFormats(){
        return size();
    }
    
    /** returns true if there is a format with that id,
     * false otherwise */
    public boolean hasFormatWithID(String id){
        return positions.containsKey(id);
    }
     
    /** returns the format at the specified position */
    public Format getFormatAt(int pos){
        return (Format)elementAt(pos);
    }
    
    /** returns the format with the specified id */
    public Format getFormatWithID(String id){
        return getFormatAt(lookupID(id));
    }
    
    /** adds the specified format 
     * returns true if the format was really new, i.e.
     * if there was not yet a format with that id
     * returns false otherwise */
    public boolean addFormat(Format f){
        if (positions.containsKey(f.getID())){
            setElementAt(f,lookupID(f.getID()));
            return false;
        }
        addElement(f);
        positions.put(f.getID(),new Integer(getNumberOfFormats()-1));
        return true;
    }
    
    /** removes the format at the specified position */
    public void removeFormatAt(int pos){
        positions.remove(getFormatAt(pos).getID());
        removeElementAt(pos);
    }
    
    /** removes the format with the specified id */
    public void removeFormatWithID(String id){
        positions.remove(id);
        removeElementAt(lookupID(id));
    }
    
    private int lookupID(String id){
        Integer pos = (Integer)positions.get(id);
        return pos.intValue();
    }
    
    /** writes a string representing this object in XML to the specified output stream */
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException {
        fo.write("<formats>".getBytes("UTF-8"));    
        for (int pos=0; pos<getNumberOfFormats(); pos++){
            fo.write(getFormatAt(pos).toXML().getBytes("UTF-8"));
        }
        fo.write("</formats>".getBytes("UTF-8"));    
    }
    
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<formats>");
        for (int pos=0; pos<getNumberOfFormats(); pos++){
            buffer.append(getFormatAt(pos).toXML());
        }
        buffer.append("</formats>");
        return buffer.toString();
    }
    
    /** writes an HTML representation of this object to the specified file output stream */
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }
    
    /** returns a HTML representation of this object */
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<style>");
        for (int pos=0; pos<getNumberOfFormats(); pos++){
            sb.append(getFormatAt(pos).toHTML(param));
        }
        sb.append("</style>\n");
        return sb.toString();                
    }
    
    public String toRTFFontTable(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("{\\fonttbl ");
        for (int pos=0; pos<getNumberOfFormats(); pos++){
            sb.append(getFormatAt(pos).toRTFFont(param));
        }
        sb.append("}");
        return sb.toString();                
    }
    
    public String toRTFColorTable(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("{\\colortbl ");        
        try {
            int r = Integer.parseInt(param.frameColor.substring(2,4),16);
            int g = Integer.parseInt(param.frameColor.substring(5,7),16);
            int b = Integer.parseInt(param.frameColor.substring(8),16);        
            sb.append("\\red" + Integer.toString(r));
            sb.append("\\green" + Integer.toString(g));
            sb.append("\\blue" + Integer.toString(b) + ";");
        } catch (NumberFormatException nfe){
            sb.append("\\red0\\green0\\blue0;");
        }
        for (int pos=0; pos<getNumberOfFormats(); pos++){
            sb.append(getFormatAt(pos).toRTFColor(param));
        }
        sb.append("}");
        return sb.toString();                
    }
    
    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();
        
        HashSet allFonts = new HashSet();
        for (int pos=0; pos<getNumberOfFormats(); pos++){
            Format format = getFormatAt(pos);
            allFonts.add(format.getProperty("font-name"));
        }
        sb.append("<w:fonts>");
        sb.append(WordMLUtilities.DEFAULT_FONTS_ELEMENT);
        for (Iterator i = allFonts.iterator(); i.hasNext();){
            String fontName = (String)(i.next());
            sb.append("<w:font w:name=\"" + fontName + "\">");
            sb.append("</w:font>");
        }
        sb.append("</w:fonts>");
        
        sb.append("<w:styles>");    // styles element
        for (int pos=0; pos<getNumberOfFormats(); pos++){
            sb.append(getFormatAt(pos).toWordML(param));
        }
        sb.append("</w:styles>");
        return sb.toString();                
    }
    
}
