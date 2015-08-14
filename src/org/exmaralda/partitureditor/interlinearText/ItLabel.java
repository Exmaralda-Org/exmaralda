/*
 * ItLabel.java
 *
 * Created on 26. Februar 2002, 14:54
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ItLabel extends AbstractRunVector implements HTMLable{

    /** Creates new ItLabel */
    public ItLabel() {
    }

    
    /** returns the XML representation of this object */
    public String toXML(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("<it-label formatref=\"");
        buffer.append(getFormat().getID());
        buffer.append("\">");
        buffer.append(super.toXML());
        buffer.append("</it-label>");
        return buffer.toString();        
    }
    
    /** writes an HTML representation of this object to the specified file output stream */
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }    
    
    /** returns a HTML representation of this object */
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<td height=\"" + Long.toString(param.rowHeight) + "px\" class=\"");
        sb.append(getFormat().getID());
        sb.append("\"");
        //sb.append(" style=\"");
        //sb.append(" height:" + Long.toString(param.rowHeight) + "px;");
        //sb.append(" overflow:hidden;");
        if ((param.drawFrame) && (!param.isOutside)){
            sb.append(" style=\"");
            if ((param.frame.indexOf('l')>=0)){
                sb.append(" border-left-color:rgb(0,0,0);");
                sb.append(HTMLUtilities.toHTMLBorderString("l","solid"));  
            }
            if ((param.isFirstLine) && (!param.includeSyncPoints) && (param.frame.indexOf('t')>=0)){
                sb.append(" border-top-color:" + HTMLUtilities.toHTMLColorString(param.frameColor) + ";");
                sb.append(HTMLUtilities.toHTMLBorderString("t", param.frameStyle));
            }
            if (param.isLastLine && param.frame.indexOf('b')>=0){
                sb.append(" border-bottom-color:" + HTMLUtilities.toHTMLColorString(param.frameColor) + ";");
                sb.append(HTMLUtilities.toHTMLBorderString("b", param.frameStyle));
            }
            sb.append("\"");
        }                        
        //sb.append("\"");
        if (param.useJavaScript && hasUdInformation()){
            if (getUdInformation().getProperty("javascript:onClickAnchor")!=null){
                sb.append(" onclick=\"");
                sb.append("openOnClickWindow('" + getUdInformation().getProperty("javascript:onClickAnchor") + "')\"");
            }
        }
        sb.append(">");
        sb.append(super.toHTML(param));
        // added for version 1.3.3., 21-Nov-2006
        // removed because it causes trouble with formatting of akz-tiers in HIAT, 12-Jun-2012
        //sb.append("&nbsp;&nbsp;");
        sb.append("</td>");                
        return sb.toString();                
    }
    
    public String toRTFTableCellDefinition(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("\\clvertalt");   // cell align top        
        param.isLastChunk=false;
        param.isFirstChunk = true;
        sb.append(RTFUtilities.toRTFBorderDefinition(getFormat(), param));  // border definition
        sb.append("\\clcbpat" + Integer.toString(param.getColorMapping(getFormat().getProperty("bg-color")))); // background color of the background pattern (sic!)
        sb.append("\\cltxlrtb");    // text flow left to right and top to bottom
        sb.append("\\clftsWidth3"); // cell width is in twips
        sb.append("\\clwWidth" + Long.toString(Math.round(param.labelWidth*20)));    // preferred Cell Width (in twips!)
        sb.append("\\cellx" + Long.toString(Math.round(param.labelWidth*20))); // position of right edge (in twips!)
        return sb.toString();
    }
    
    public String toRTFTableCellContent(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append(super.toRTF(param));
        sb.append("\\cell");
        sb.append("}");
        return sb.toString();
    }    
    
    public void print(java.awt.Graphics2D graphics, PrintParameters param) {
        param.resetCurrentX();
        super.print(graphics, param);
    }    
    
    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<w:tc>"); // table cell
        sb.append("<w:tcPr>"); // table cell properties
        param.isLastChunk=false;
        param.isFirstChunk = true;
        sb.append(WordMLUtilities.toWordMLBorderDefinition(getFormat(), param));  // border definition
        sb.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\""); // backgorund colour...
        sb.append(WordMLUtilities.toColorString(getFormat().getProperty("bg-color")) +"\"/>");   // ...backgorund colour
        sb.append("</w:tcPr>");                
        sb.append(super.toWordML(param));
        sb.append("</w:tc>");                
        return sb.toString();                
    }
    
}
