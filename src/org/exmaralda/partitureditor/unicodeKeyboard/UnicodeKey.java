/*
 * UnicodeKey.java
 *
 * Created on 1. August 2003, 14:44
 */

package org.exmaralda.partitureditor.unicodeKeyboard;

/**
 *
 * @author  thomas
 */
public class UnicodeKey {
    
    public String content = "";
    public String description = "";       
    public java.awt.Font font = new java.awt.Font("Arial Unicode MS", java.awt.Font.PLAIN, 10);
    
    /** Creates a new instance of UnicodeKey */
    public UnicodeKey() {
    }
    
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<key>");
            sb.append("<content>");
                sb.append(content);
            sb.append("</content>");
            sb.append("<description>");
                sb.append(description);
            sb.append("</description>");
            sb.append("<font ");
                sb.append("name=\"" + font.getFontName() + "\" ");
                String facevalue="plain";
                if (font.getStyle()==java.awt.Font.BOLD) facevalue="bold";
                if (font.getStyle()==java.awt.Font.ITALIC) facevalue="italic";
                sb.append("face=\"" + facevalue + "\" ");
                sb.append("size=\"" + Integer.toString(font.getSize()) + "\">");
            sb.append("</font>");
        sb.append("</key>");
        return sb.toString();
    }
    
}
