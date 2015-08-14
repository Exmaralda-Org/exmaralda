/*
 * SyncPoint.java
 *
 * Created on 26. Februar 2002, 11:56
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class SyncPoint implements XMLElement, Formattable, HTMLable {

    private String id;
    /** an offset that has not yet been calculated, has a negative value */
    private double offset = -1;
    private Format format;
    private String text;
    
    /** Creates new SyncPoint */
    public SyncPoint() {
        text = new String();
        format = new Format();
    }

    /** returns a copy of this object */
    public SyncPoint makeCopy(){
        SyncPoint result = new SyncPoint();
        result.setID(this.getID());
        result.setFormat(this.getFormat());
        result.setOffset(this.getOffset());
        result.setText(this.getText());
        return result;
    }
    
    /** returns the id of this syncPoint */
    public String getID(){
        return id;
    }
    
    /** sets the ID of this syncPoint to the specified value */
    public void setID(String i){
        id = i;
    }
    
    /** returns the offset of this syncPoint */
    public double getOffset(){
        return offset;
    }
    
    /** sets the offset to the specified value */
    public void setOffset(double os){
        offset = os;
    }
        
    /** returns the text of this syncPoint */
    public String getText(){
        return text;
    }
    
    /** sets the text of this syncPoint to the specified value */
    public void setText(String t){        
        text = t;
    }
    
    /** returns the format of this object  */
    public Format getFormat() {
        return format;
    }
    
    /** writes a string representing this object in XML to the specified output stream */
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException {
    }
    
    /** returns the width of this object in pixels  */
    public double getWidth() {
        if (getText().length()==0) {return 0;}
        java.awt.font.TextLayout textLayout = new java.awt.font.TextLayout(getText(), getFormat().makeFont(), new java.awt.font.FontRenderContext(null, false, true));
        return textLayout.getAdvance();                
    }
    
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<sync-point id=\"");
        buffer.append(getID());
        buffer.append("\" offset=\"");
        buffer.append(Double.toString(getOffset()));
        if (hasFormat()){
            buffer.append("\" formatref=\"");
            buffer.append(getFormat().getID());
        }
        buffer.append("\">");
        buffer.append(getText());
        buffer.append("</sync-point>");
        return buffer.toString();        
    }
    
    /** augments the format by the properties in the given format  */
    public void augmentFormat(Format f) {
        Format newFormat = getFormat().makeCopy();
        newFormat.augment(f);
        setFormat(f);                
    }
    
    /** sets the format of this object  */
    public void setFormat(Format f) {
        format = f;
    }
    
    /** returns the height of this object in pixels  */
    public double getHeight() {
        return getFormat().getHeight();
    }
    
    public double getDescent() {
        return getFormat().getDescent();
    }    
    
    /** returns true iff this object has been assigned a format */
    public boolean hasFormat() {
        return (getFormat()!=null);        
    }
    
    /** propagates this object's format to its children that don't have formats  */
    public void propagateFormat() {
        // syncPoint doesn't have children
    }
    
    /** writes an HTML representation of this object to the specified file output stream */
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }
    
    /** returns a HTML representation of this object */
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<td nowrap=\"nowrap\" ");
        sb.append("colspan=\"1\" ");
        sb.append("class=\"" + getFormat().getID() + "\"");

        if ((param.drawFrame) && (!param.putSyncPointsOutside)){
            sb.append(" style=\"");
            if (param.frame.indexOf('t')>=0){
                sb.append(" border-top-color:" + HTMLUtilities.toHTMLColorString(param.frameColor) + ";");
                sb.append(HTMLUtilities.toHTMLBorderString("t",param.frameStyle));
            }
            if ((param.isLastChunk) && (param.frame.indexOf('r')>=0)){
                sb.append(" border-right-color:" + HTMLUtilities.toHTMLColorString(param.frameColor) + ";");
                sb.append(HTMLUtilities.toHTMLBorderString("r",param.frameStyle));
            }
            sb.append("\"");
        }        
        else if ((param.drawFrame) && (param.putSyncPointsOutside) && (param.frame.indexOf('t')>=0)){
            sb.append(" style=\"");
            sb.append(" border-bottom-color:" + HTMLUtilities.toHTMLColorString(param.frameColor) + ";");
            sb.append(HTMLUtilities.toHTMLBorderString("b",param.frameStyle));
            sb.append("\"");
        }        
        
        
        sb.append(">");
        sb.append("<span class=\"" + getFormat().getID() + "\">");
        sb.append(HTMLUtilities.toHTMLString(getText()));
        sb.append("</span>");
        sb.append("</td>\n");                
        return sb.toString();                
    }
    
    public String toRTFTableCellDefinition(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("\\clvertalt");   // cell align top        
        sb.append(RTFUtilities.toRTFBorderDefinition(getFormat(), param));  // border definition
        sb.append("\\clcbpat" + Integer.toString(param.getColorMapping(getFormat().getProperty("bg-color")))); // background color of the background pattern (sic!)
        sb.append("\\cltxlrtb");    // text flow left to right and top to bottom
        sb.append("\\clftsWidth3"); // cell width is in twips
        sb.append("\\clwWidth" + Long.toString(Math.round(param.nextSyncPoint.getOffset() - this.getOffset())*20));    // preferred Cell Width (in twips!)
        sb.append("\\cellx" + Long.toString(Math.round((param.nextSyncPoint.getOffset())*20))); // position of right edge (in twips!)
        return sb.toString();
    }
    
    public String toRTFTableCellContent(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\\plain");    //reset to defaults
        String fontNo = Integer.toString(param.getFontMapping(getFormat().getProperty("font-name")));
        //sb.append("\\f" + fontNo);    // font (reference to font table)
        String fontSize = Integer.toString(Integer.parseInt(getFormat().getProperty("font-size"))*2);
        //sb.append("\\fs" + fontSize);   // font size (in half points!)
        String fontColor = Integer.toString(param.getColorMapping(getFormat().getProperty("font-color")));
        //sb.append("\\cf" + fontColor); // font color (reference to the color table)
        String fontFace=getFormat().getProperty("font-face");
        //if (fontFace.equalsIgnoreCase("Bold")){sb.append("\\b");}   // turn on bold
        //if (fontFace.equalsIgnoreCase("Italic")){sb.append("\\i");} // turn on italic
        if (getWidth()>0){
            String textWidth = Long.toString(Math.round(getWidth())*20);
            sb.append("\\fittext" + textWidth); // the width as calculated by JAVA (is not 100% the same as for RTF...)
        }
        sb.append(" ");
        sb.append(RTFUtilities.toEscapedRTFString(getText(),fontNo, fontSize, fontColor, fontFace));        
        sb.append("\\cell");
        sb.append("}");
        return sb.toString();
    }       
    
    public String toSVG(SVGParameters param, double x){
        StringBuffer sb = new StringBuffer();        
        
        String fontStyleName = "font-style";
        String fontStyleProperty = "normal";
        if (getFormat().getProperty("font-face").equalsIgnoreCase("Italic")){
            fontStyleProperty = "italic";
        } else if (getFormat().getProperty("font-face").equalsIgnoreCase("Bold")){
            fontStyleName = "font-weight";
            fontStyleProperty = "bold";
        }
        String[][] attributes = {{"x", Double.toString(x)},
                                 {"y", Double.toString(10 + this.getHeight())}, // + this.getHeight() - this.getDescent())},
                                 {"fill", SVGUtilities.toSVGColor(getFormat().getProperty("font-color"))},
                                 {"font-family", getFormat().getProperty("font-name")},
                                 {fontStyleName, fontStyleProperty},                                     
                                 {"font-size", getFormat().getProperty("font-size") + "px"}};                                     
                                 //{"class", getFormat().getID()}};
        sb.append(XMLUtilities.makeXMLOpenElement("text", attributes));
        sb.append(this.getText());
        sb.append(XMLUtilities.makeXMLCloseElement("text"));        
        return sb.toString();
    }
    
    public void print(java.awt.Graphics2D graphics, PrintParameters param) {
        param.resetCurrentX();
        param.currentX+=this.getOffset();
        graphics.setFont(getFormat().makeFont());
        graphics.setColor(getFormat().makeColor("font-color"));
        if (getText().length()>0){ // necessary because otherwise MAC OS X will throw an error
            graphics.drawString(getText(),param.currentX, param.currentY);        
        }
    }

    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<w:tc>"); // table cell
        sb.append("<w:tcPr>"); // table Cell Properties
        sb.append(WordMLUtilities.toWordMLBorderDefinition(getFormat(), param));  // border definition
        sb.append("</w:tcPr>");
        sb.append(WordMLUtilities.prtEnvelope(getText(), getFormat().getID()));
        sb.append("</w:tc>");    
        return sb.toString();                
    }
    
    
}
