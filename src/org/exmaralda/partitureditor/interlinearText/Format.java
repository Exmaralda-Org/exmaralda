/*
 * Format.java
 *
 * Created on 26. Februar 2002, 10:19
 */

package org.exmaralda.partitureditor.interlinearText;

import java.util.*;
/**
 *
 * @author  thomas.schmidt@uni-hamburg.de
 * @version 1.0.2.
 */
public class Format extends java.util.Properties implements XMLElement, HTMLable{

    private String id;
    
    /** Creates new Format */
    public Format() {
        // set defaults
        this.setProperty("chunk-border","");
        this.setProperty("chunk-border-color","#R00G00B00");
        this.setProperty("chunk-border-style","solid");
        this.setProperty("font-color","#R00G00B00");
        this.setProperty("bg-color","#RFFGFFBFF");
        this.setProperty("font-size","10");
        this.setProperty("font-face","Plain");
        this.setProperty("font-name","Times New Roman");
        this.setProperty("text-alignment","Left");
        this.setProperty("row-height-calculation","Generous");
        this.setProperty("fixed-row-height","10");
    }
    
    /** returns a copy of this object */
    public Format makeCopy(){
        Format result = (Format)this.clone();
        result.setID(this.getID());
        return result;
    }
    
    /** adds the properties in the given format to this format */
    public void augment(Format f){
        for (Enumeration e = f.elements() ; e.hasMoreElements() ;) {
           String propertyName = (String)e.nextElement();
           String propertyValue = getProperty(propertyName);
           this.setProperty(propertyName, propertyValue);
        }
    }
    
    /** returns the id of this format */
    public String getID(){
        return id;
    }
    
    /** sets the id of this format to the specified value */
    public void setID(String i){
        id = i;
    }
    
    /** returns a font corresponding to the properties of this format */
    public java.awt.Font makeFont(){
        String fontName = getProperty("font-name");
        int fontStyle = java.awt.Font.PLAIN;
        if (getProperty("font-face").equalsIgnoreCase("BOLD")){
            fontStyle = java.awt.Font.BOLD;
        } else if (getProperty("font-face").equalsIgnoreCase("ITALIC")){
            fontStyle = java.awt.Font.ITALIC;
        }
        int fontSize = (Integer.valueOf(getProperty("font-size"))).intValue();
        return new java.awt.Font(fontName,fontStyle,fontSize);
    }
    
    /** returns a color corresponding to the specified property */
    public java.awt.Color makeColor(String propertyName){
        String colorName = getProperty(propertyName);
        try {
            // color is of the form #RxxGxxBxx where xx is a hexadecimal number
            int r = Integer.parseInt(colorName.substring(2,4),16);
            int g = Integer.parseInt(colorName.substring(5,7),16);
            int b = Integer.parseInt(colorName.substring(8),16);
            return new java.awt.Color(r,g,b);
        } catch (Exception e){
            return java.awt.Color.white;        
        }
    }
    
    /** returns the height in pixels of the font in this format */
    public int getHeight(){
        if (this.getProperty("row-height-calculation","Generous").equals("Fixed")){
            return Short.parseShort(getProperty("fixed-row-height","10"));
        }
        if (this.getProperty("row-height-calculation","Generous").equals("Miserly")){
            return 0;
        }
        java.awt.Font font = makeFont();
        javax.swing.JTextField dummyTextField = new javax.swing.JTextField();
        return dummyTextField.getFontMetrics(font).getHeight();
    }
    
    /** returns the height in pixels of the font in this format */
    public int getDescent(){
        if (this.getProperty("row-height-calculation","Generous").equals("Miserly")){
            return 0;
        }
        java.awt.Font font = makeFont();
        javax.swing.JTextField dummyTextField = new javax.swing.JTextField();
        return dummyTextField.getFontMetrics(font).getDescent();
    }
    

    /** writes a string representing this object in XML to the specified output stream */
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException {
        fo.write(toXML().getBytes("UTF-8"));
    }
    
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<format id=\"");
        buffer.append(getID());
        buffer.append("\">");
        for (Enumeration e = propertyNames() ; e.hasMoreElements() ;) {
             String propertyName = (String)e.nextElement();
             String propertyValue = getProperty(propertyName);
             buffer.append("<property name=\"");
             buffer.append(propertyName);
             buffer.append("\">");
             buffer.append(propertyValue);
             buffer.append("</property>");             
        }        
        buffer.append("</format>");
        return buffer.toString();
    }
    
    /** writes an HTML representation of this object to the specified file output stream */
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }
    
    /** returns a HTML representation of this object */
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        //StringBuffer body = new StringBuffer();
       
        // style for table cells
        sb.append ("td." + getID() + "{\n");
        sb.append("\t\t background-color: ");
        sb.append(HTMLUtilities.toHTMLColorString(makeColor("bg-color")));
        sb.append(";\n");
        sb.append("\t\t border-color: ");
        sb.append(HTMLUtilities.toHTMLColorString(makeColor("chunk-border-color")));
        sb.append(";\n");
        sb.append(HTMLUtilities.toHTMLBorderString(getProperty("chunk-border"),getProperty("chunk-border-style")));
        sb.append("}\n");        

        // style for runs
        sb.append ("span." + getID() + "{\n");
        sb.append("\t\t font-family: \"" + getProperty("font-name") + "\";\n");
        sb.append("\t\t font-size: " + getProperty("font-size") + "pt;\n");
        sb.append("\t\t font-style: ");
        if (getProperty("font-face").equalsIgnoreCase("Italic")){sb.append("italic;");}
        else {sb.append("normal;");}
        sb.append("\n\t\t font-weight: ");
        if (getProperty("font-face").equalsIgnoreCase("Bold")){sb.append("bold;");}
        else {sb.append("normal;");}
        sb.append("\n\t\t color: ");
        //System.out.println(toXML());
        sb.append(HTMLUtilities.toHTMLColorString(getProperty("font-color")));
        sb.append(";\n"); 
        sb.append("}\n");        

        return sb.toString();                
    }
    

    
    /** returns an entry for the font table, e.g. 
     * {\f58\fmodern\fcharset128\fprq2 Arial Unicode MS;} */
    public String toRTFFont(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        if (param.getFontMapping(getProperty("font-name"))<0){
            int fontNo = param.addFontMapping(getProperty("font-name"));
            sb.append("{\\f");
            sb.append(Integer.toString(fontNo));
            sb.append("\\fnil");
            sb.append("\\fcharset0");
            // change 26-04-2004, take 1
            //sb.append("\\fcharset1");
            sb.append(" " + getProperty("font-name"));
            sb.append(";}");
        }
        return sb.toString();
    }
    
    public String toRTFColor(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        if (param.getColorMapping(getProperty("font-color"))<0){
            java.awt.Color c = makeColor("font-color");
            sb.append("\\red" + Integer.toString(c.getRed()));
            sb.append("\\green" + Integer.toString(c.getGreen()));
            sb.append("\\blue" + Integer.toString(c.getBlue()));
            sb.append(";");
            param.addColorMapping(getProperty("font-color"));
        }
        if (param.getColorMapping(getProperty("bg-color"))<0){
            java.awt.Color c = makeColor("bg-color");
            sb.append("\\red" + Integer.toString(c.getRed()));
            sb.append("\\green" + Integer.toString(c.getGreen()));
            sb.append("\\blue" + Integer.toString(c.getBlue()));
            sb.append(";");
            param.addColorMapping(getProperty("bg-color"));
        }
        if (param.getColorMapping(getProperty("chunk-border-color"))<0){
            java.awt.Color c = makeColor("chunk-border-color");
            sb.append("\\red" + Integer.toString(c.getRed()));
            sb.append("\\green" + Integer.toString(c.getGreen()));
            sb.append("\\blue" + Integer.toString(c.getBlue()));
            sb.append(";");
            param.addColorMapping(getProperty("chunk-border-color"));
        }
        return sb.toString();
    }
    
    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();        
        sb.append("<w:style "); // style element
        sb.append("w:type=\"character\" "); // type attribute
        sb.append("w:styleId=\"" + getID() + "\">");
        sb.append("<w:name w:val=\"" + getID() + "\"/>");
        sb.append("<w:basedOn w:val=\"Standard\"/>");
        sb.append("<w:rPr>");   // run properties
        sb.append("<w:rFonts " + WordMLUtilities.fontAttributes(getProperty("font-name")) + "/>");
        sb.append("<wx:font wx:val=\"" + getProperty("font-name") + "\"/>");
        if (getProperty("font-face").equalsIgnoreCase("bold")){ sb.append("<w:b/>");}
        if (getProperty("font-face").equalsIgnoreCase("italic")){ sb.append("<w:i/>");}
        sb.append("<w:sz w:val=\"" + Integer.parseInt(getProperty("font-size"))*2 + "\"/>");
        sb.append("<w:sz-cs w:val=\"" + Integer.parseInt(getProperty("font-size"))*2 + "\"/>");
        sb.append("<w:color w:val=\"" + WordMLUtilities.toColorString(getProperty("font-color")) + "\"/>");
        sb.append("</w:rPr>");
        sb.append("</w:style>");
        return sb.toString();                
    }
    
    
}
