/*
 * Run.java
 *
 * Created on 26. Februar 2002, 10:57
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class Run implements Formattable, XMLElement, HTMLable {

    private Format format;
    private String text;
    
    /** Creates new Run */
    public Run() {
    }

    /** returns the format of this object  */
    public Format getFormat() {
        return format;
    }
    
    /** returns the text of this run */
    public String getText(){
        return text;
    }
    
    /** sets the text of this run to the specified value */
    public void setText(String t){
        text = t;
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
    
    /** returns the width of this object in pixels  */
    public double getWidth() {
        if (getText().length()==0) {return 0;}
        java.awt.font.TextLayout textLayout = new java.awt.font.TextLayout(getText(), getFormat().makeFont(), new java.awt.font.FontRenderContext(null, false, true));
        return textLayout.getAdvance();        
    }    
    
    /** writes a string representing this object in XML to the specified output stream */
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException {
        fo.write(toXML().getBytes("UTF-8"));
    }
    
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        if (hasFormat()){
            buffer.append("<run formatref=\"");
            buffer.append(getFormat().getID());
            buffer.append("\">");
        } else {
            buffer.append("<run>");
        }
        buffer.append(XMLUtilities.toXMLString(getText()));
        buffer.append("</run>");
        return buffer.toString();
    }
    
    /** returns the height of this object in pixels  */
    public double getHeight() {
        if (getText().length()==0) {return 0;}
        if (getFormat().getProperty("row-height-calculation","Generous").equals("Miserly")){
            java.awt.font.TextLayout textLayout = new java.awt.font.TextLayout(getText(), getFormat().makeFont(), new java.awt.font.FontRenderContext(null, false, true));
            return (textLayout.getAscent() + textLayout.getDescent());
            //return textLayout.getBounds().getHeight();
        }
        return getFormat().getHeight();
    }
    
    public double getDescent() {
        if (getText().length()==0) {return 0;}
        if (getFormat().getProperty("row-height-calculation","Generous").equals("Miserly")){
            java.awt.font.TextLayout textLayout = new java.awt.font.TextLayout(getText(), getFormat().makeFont(), new java.awt.font.FontRenderContext(null, false, true));
            return textLayout.getDescent();
        }
        return getFormat().getDescent();
    }    

    /** returns true if this run has been assigned a format */
    public boolean hasFormat() {
        return (getFormat()!=null);        
    }
    
    /** propagates this object's format to its children that don't have formats  */
    public void propagateFormat() {
        // Run can't have children (is not fertile hohoho)
    }
    
    /** cuts a piece from the beginning of this run so that it fits into the space 
     * specified by pixelsThatFit and returns this piece */
    public Run cut(short breakType, BreakParameters param, double pixelsThatFit, double pixelsThatDontFit){
        Run result = new Run();
        result.setFormat(this.getFormat());
        java.awt.Font font = getFormat().makeFont();
        java.awt.font.FontRenderContext frc = new java.awt.font.FontRenderContext(null, false, true);        
        int cutPosition = 0;
        int lastWordEnd = -1;
        for (int pos=1; pos<getText().length();pos++){
            String text = getText().substring(0, pos);
            java.awt.font.TextLayout textLayout = new java.awt.font.TextLayout(text, font, frc);
            cutPosition = pos;
            if (textLayout.getAdvance()>=pixelsThatFit) {
                // i.e. if this text does not fit into the available space anymore
                // then the position before this one must have been the right one to cut the run at
                cutPosition--;
                break;
            }
            if (param.isWordBoundary(getText().charAt(pos))){
                // keep the last occurence of a word boundary in memory
                lastWordEnd = pos;
            }
        }
        if (param.respectWordBoundariesForBreakType(breakType)){
            // i.e. if word boundaries are to be respected
            // then the last occurence of a word boundary is the right place to cut the run at
            // unless it would mean that more than 30% of the total break width would go unused (added for 1.2.4. to fix bug 113)
            boolean ignoreRespectWordBoundaries = ((lastWordEnd==-1) && (pixelsThatFit>0.3*param.width));
            if ((lastWordEnd+1<=cutPosition) && (!ignoreRespectWordBoundaries)){
                cutPosition = lastWordEnd+1;
            }
        }
        // put the part that fits into the available space into the result run
        result.setText(this.getText().substring(0,cutPosition));
        // and leave the rest in this one
        this.setText(this.getText().substring(cutPosition));       
        return result;
    }
    

    /** writes an HTML representation of this object to the specified file output stream */
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }
    
    /** returns a HTML representation of this object */
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<span class=\"");
        sb.append(getFormat().getID());
        sb.append("\">");
        sb.append(HTMLUtilities.toHTMLString(getText()));
        sb.append("</span>");                
        return sb.toString();        
    }
    
    public String toRTF(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("\\plain");    //reset to defaults
        String fontNo = Integer.toString(param.getFontMapping(getFormat().getProperty("font-name")));
        String fontSize = Integer.toString(Integer.parseInt(getFormat().getProperty("font-size"))*2);
        String fontColor = Integer.toString(param.getColorMapping(getFormat().getProperty("font-color")));
        if (getWidth()>0 && param.chunkSizeIsCritical && (!param.useClFitText)){
            String textWidth = Long.toString(Math.round(getWidth())*20);
            sb.append("\\fittext" + textWidth); // the width as calculated by JAVA (is not 100% the same as for RTF...)
        }
        String fontFace=getFormat().getProperty("font-face");
        sb.append(" ");
        sb.append(RTFUtilities.toEscapedRTFString(getText(), fontNo, fontSize, fontColor, fontFace));
        return sb.toString();        
    }
    
    public String toSVG(SVGParameters param, double x, double y){
        StringBuffer sb = new StringBuffer();
        if (getText().length()>0){  // necessary, otherwise MAC OS X will throw an illegal argument exception???
            String fontStyleName = "font-style";
            String fontStyleProperty = "normal";
            if (getFormat().getProperty("font-face").equalsIgnoreCase("Italic")){
                fontStyleProperty = "italic";
            } else if (getFormat().getProperty("font-face").equalsIgnoreCase("Bold")){
                fontStyleName = "font-weight";
                fontStyleProperty = "bold";
            }
            String[][] attributes = {{"x", Double.toString(x)},
                                     {"y", Double.toString(y)}, // + this.getHeight() - this.getDescent())},
                                     {"fill", SVGUtilities.toSVGColor(getFormat().getProperty("font-color"))},
                                     {"font-family", getFormat().getProperty("font-name")},
                                     {fontStyleName, fontStyleProperty},                                     
                                     {"font-size", getFormat().getProperty("font-size") + "px"}};                                     
                                     //{"class", getFormat().getID()}};
            sb.append(XMLUtilities.makeXMLOpenElement("text", attributes));
            sb.append(XMLUtilities.toXMLString(getText()));
            sb.append(XMLUtilities.makeXMLCloseElement("text"));
        }        
        return sb.toString();
    }
    
    public void print(java.awt.Graphics2D graphics, PrintParameters param){
        if (getText().length()>0){  // necessary, otherwise MAC OS X will throw an illegal argument exception???
            graphics.setFont(getFormat().makeFont());
            graphics.setColor(getFormat().makeColor("font-color"));
            graphics.drawString(getText(),param.currentX, param.currentY);
            param.currentX+=getWidth();
        }
    }
    
    /** returns a HTML representation of this object */
    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append(WordMLUtilities.prtEnvelope(getText(), getFormat().getID()));
        return sb.toString();        
    }
    
    
    
}
