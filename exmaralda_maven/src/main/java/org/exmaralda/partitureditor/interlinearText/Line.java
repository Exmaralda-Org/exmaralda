/*
 * Line.java
 *
 * Created on 26. Februar 2002, 10:44
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class Line extends AbstractRunVector implements XMLElement, ItElement, HTMLable, RTFable, WordMLable {

    private java.util.Vector anchors;
    private boolean keepTogetherWithNext = false;
    
    /** Creates new Line */
    public Line() {
        anchors = new java.util.Vector();
    }
    
    public Line(boolean ktwn){
        this();
        keepTogetherWithNext=ktwn;
    }
    
    /** Creates new Line with one run with the specified text*/
    public Line(String text) {
        this();
        Run run = new Run();
        run.setText(text);
        this.addRun(run);
    }    
    
    /** Creates new Line with one run with the specified text*/
    public Line(String text, boolean ktwn) {
        this();
        Run run = new Run();
        run.setText(text);
        this.addRun(run);
        keepTogetherWithNext=ktwn;
    }    

    /** returns an array containing all anchors of this line */
    public String[] getAnchorsAsArray(){
        String[] result = new String[anchors.size()];
        for (int pos=0; pos<anchors.size(); pos++){
            result[pos]=(String)anchors.elementAt(pos);
        }
        return result;
    }
    
    /** adds an anchor to this line */
    public void addAnchor(String anchor){
        anchors.addElement(anchor);
    }
        
   
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<line formatref=\"");
        buffer.append(getFormat().getID());
        buffer.append("\">");
        if (hasUdInformation())buffer.append(getUdInformation().toXML());
        if (anchors.size()>0){
            for (int pos=0; pos<anchors.size(); pos++){
                buffer.append("<anchor>" + (String)(anchors.elementAt(pos)) + "</anchor>");
            }
        }        
        for (int pos=0; pos<anchors.size(); pos++){
            buffer.append("<anchor>");
            buffer.append((String)anchors.elementAt(pos));
            buffer.append("</anchor>");
        }
        buffer.append(super.toXML());
        buffer.append("</line>");
        return buffer.toString();        
    }
    
    /** always returns false */
    public boolean isItBundle() {
        return false;
    }    
    
    /** trims this line according to the given parameters */
    public java.util.Vector trim(BreakParameters param) {
        java.util.Vector result = new java.util.Vector();
        result.add(this);
        return result;
    }
    
    /** writes an HTML representation of this object to the specified file output stream */
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }
    
    /** returns a HTML representation of this object */
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        if (!containsOnlyWhiteSpace()){
            sb.append("<p class=\"");
            sb.append(getFormat().getID());
            sb.append("\">\n");
            sb.append(super.toHTML(param));
            sb.append("</p>\n");        
        }
        return sb.toString();                        
    }
    
    public String toRTF(RTFParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("\\pard");    // reset to defaults
        // Removed for version 1.3.3, 09-Octo-2006
        /*if (param.isFirstParagraph){
            sb.append("\\pagebb");
        }*/        
        // added for version 1.3.3, 09-Oct-2006
        sb.append("\\keepn");       // Keep paragraph with the next paragraph        
        if (param.saveSpace){
            sb.append("\\fi-500");
        }
        sb.append(super.toRTF(param));
        sb.append("\\par");
        return sb.toString();                                
    }
    
    public boolean keepTogetherWithNext() {
        return keepTogetherWithNext;
    }
    
    public void print(java.awt.Graphics2D graphics, PrintParameters param) {
        java.awt.Shape shape = null;
        param.resetCurrentX();
        if (param.saveSpace){
            shape = graphics.getClip();
            graphics.setClip(-25,Math.round(param.currentY),200,400);
            param.currentX-=25;
        }
        param.currentY+=getHeight();
        super.print(graphics, param);
        if (param.saveSpace){
            graphics.setClip(shape);
        }
    }
    
    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<w:p>"); // paragraph
        sb.append("<w:pPr>");   // paragraph properties
        if (param.saveSpace) {
            sb.append("<w:ind w:hanging=\"500\"/>");
        }
        sb.append("</w:pPr>");   
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            Run r = getRunAt(pos);
            sb.append(WordMLUtilities.rtEnvelope(r.getText(), r.getFormat().getID()));
        }
        sb.append("</w:p>");
        return sb.toString();                        
    }
    


}
