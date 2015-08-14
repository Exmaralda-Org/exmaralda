/*
 * AbstractRunVector.java
 *
 * Created on 26. Februar 2002, 10:45
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public abstract class AbstractRunVector extends java.util.Vector implements Formattable, XMLElement, UdInformable, HTMLable {

    private Format format;
    private UdInformation udInfo;
    java.util.Vector links;
    
    /** Creates new AbstractRunVector */
    public AbstractRunVector() {        
        links = new java.util.Vector();
    }
    
    /** adds a link to the links of this object */
    public void addLink(Link link){
        links.addElement(link);
    }
    
    /** returns the links of this chunk */
    public java.util.Vector getLinks(){
        return links;
    }
    
    /** sets the links to the specified value */
    public void setLinks(java.util.Vector l){
        links=l;
    }

    /** returns true iff this chunk has one or more links */
    public boolean hasLinks(){
        return (links.size()>0);
    }
    
    /** returns the first link */
    public Link getFirstLink(){
        return (Link)(links.elementAt(0));
    }
    
        
    public int getNumberOfRuns(){
        return size();
    }
    
    public Run getRunAt(int pos){
        return (Run)elementAt(pos);
    }

    public void addRun(Run r){
        addElement(r);
        if (!r.hasFormat()){
            r.setFormat(this.getFormat());
        }                
    }        

    /** returns the format of this object  */
    public Format getFormat() {
        return format;
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
    
    public double getWidth() {
        double result = 0;
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            result+=getRunAt(pos).getWidth();
        }
        return result;
    }
    
    /** writes a string representing this object in XML to the specified output stream */
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException {
        fo.write(toXML().getBytes("UTF-8"));
    }
    
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        if (hasUdInformation())buffer.append(getUdInformation().toXML());
        for (int pos=0; pos<links.size(); pos++){
            buffer.append(((Link)links.elementAt(pos)).toXML());
        }        
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            buffer.append(getRunAt(pos).toXML());
        }
        return buffer.toString();
    }
    
    public UdInformation getUdInformation(){
        return udInfo;
    }
    
    public void setUdInformation(UdInformation udi){
        udInfo=udi;
    }
    
    public boolean hasUdInformation(){
        return (udInfo!=null);
    }
    
    /** returns the height of this object in pixels  */
    public double getHeight() {
        if (this.getFormat().getProperty("row-height-calculation","Generous").equals("Fixed")){
            return Short.parseShort(getFormat().getProperty("fixed-row-height","10"));
        }
        if ((getFormat().getProperty("row-height-calculation","Generous").equals("Miserly")) && (containsNoText())) return 0;
        double result = this.getFormat().getHeight();
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            result=Math.max(result,getRunAt(pos).getHeight());
        }
        return result;
    }
    
    public double getDescent() {
        if (containsNoText()) return 0;
        double result = this.getFormat().getDescent();
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            result=Math.max(result,getRunAt(pos).getDescent());
        }
        return result;
    }

    public boolean hasFormat() {
        return (getFormat()!=null);
    }
    
    public boolean containsNoText(){
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            if (getRunAt(pos).getText().length()>0){
                return false;
            }
        }
        return true;
    }
    
    public boolean containsOnlyWhiteSpace(){
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            if (getRunAt(pos).getText().trim().length()>0){
                return false;
            }
        }
        return true;
    }
    
    /** propagates this object's format to its children that don't have formats  */
    public void propagateFormat() {
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            if (!getRunAt(pos).hasFormat()){
                getRunAt(pos).setFormat(this.getFormat());
            }
        }        
    }
    
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        if (hasLinks() && param.makeLinks){
            sb.append(getFirstLink().toHTML(param)); 
        }
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            sb.append(getRunAt(pos).toHTML(param));
        }
        if (hasLinks()&& param.makeLinks){
            sb.append("</a>"); 
        }
        return sb.toString();                
    }
    
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }    
    
    public String toRTF(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            sb.append(getRunAt(pos).toRTF(param));
        }
        return sb.toString();                
        
    }
    
        
    public void addUdInformation(String propertyName, String propertyValue) {
        if (!hasUdInformation()) {udInfo = new UdInformation();}
        udInfo.setProperty(propertyName, propertyValue);
    }
    
    public String toSVG(SVGParameters param, double x, double y){
        double currentX = x;
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            Run run = getRunAt(pos);
            sb.append(run.toSVG(param, currentX, y));
            currentX+=run.getWidth();
        }
        return sb.toString();
    }
    
    public void print(java.awt.Graphics2D graphics, PrintParameters param){
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            getRunAt(pos).print(graphics, param);
        }
    }
    
    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();
        if (getNumberOfRuns()==0) {
            sb.append(WordMLUtilities.prtEnvelope(""));
        }
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            sb.append(getRunAt(pos).toWordML(param));
        }
        return sb.toString();                
    }
    
}
