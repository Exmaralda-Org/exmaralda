/*
 * SyncPoints.java
 *
 * Created on 26. Februar 2002, 15:02
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class SyncPoints extends java.util.Vector implements XMLElement, Formattable, UdInformable, HTMLable {

    private UdInformation udInfo;
    private Format format;
    private java.util.Hashtable positions;
    
    /** Creates new SyncPoints */
    public SyncPoints() {
        positions = new java.util.Hashtable();
    }

    /** returns the number of syncPoint objects included in this object */
    public int getNumberOfSyncPoints(){
        return size();
    }
    
    /** returns the sync point at the specified position */
    public SyncPoint getSyncPointAt(int pos){
        return (SyncPoint)elementAt(pos);
    }
    
    /** returns the syncPoint before the given one */
    public SyncPoint getSyncPointBefore(SyncPoint sp){
        return getSyncPointAt(lookupID(sp.getID())-1);
    }
    
    /** returns the syncPoint before the given one */
    public SyncPoint getSyncPointAfter(SyncPoint sp){
        return getSyncPointAt(lookupID(sp.getID())+1);
    }

    /** returns the syncPoint with the specified id */
    public SyncPoint getSyncPointWithID(String id){
        return getSyncPointAt(lookupID(id));
    }
    
    private int lookupID(String id){
        Integer pos = (Integer)positions.get(id);
        if (pos==null) {
            System.out.println("The evil man is " + id);
        }
        return pos.intValue();
    }
    
    
    /** updates the position hashtable */
    private void updatePositions(){
        positions.clear();
        for (int pos=0; pos<getNumberOfSyncPoints(); pos++){
            positions.put(getSyncPointAt(pos).getID(), new Integer(pos));
        }       
    }
    
    /** adds the given sync point */
    public void addSyncPoint(SyncPoint sp){
        addElement(sp);
        positions.put(sp.getID(),new Integer(getNumberOfSyncPoints()-1));  
        if (!sp.hasFormat()){
            sp.setFormat(this.getFormat());
        }                
    }

    
    public boolean isLastSyncPoint(SyncPoint sp){
        return (this.indexOf(sp) == this.getNumberOfSyncPoints()-1);
    }
    /** returns the span of the given it chunk,
     * i.e. the number of sync points it spans across */
    public int span(ItChunk itc){        
        if (itc.getEnd()==null) return (getNumberOfSyncPoints()-lookupID(itc.getStart().getID()));
        return (lookupID(itc.getEnd().getID())-lookupID(itc.getStart().getID()));
    }
    
    /** augments the format by the properties in the given format  */
    public void augmentFormat(Format f) {
        Format newFormat = getFormat().makeCopy();
        newFormat.augment(f);
        setFormat(f);                
    }
    
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        if (hasFormat()){
            buffer.append("<sync-points formatref=\"");
            buffer.append(getFormat().getID());
            buffer.append("\">");
        } else {
            buffer.append("<sync-points>");
        }
        if (hasUdInformation()) buffer.append(getUdInformation().toXML());
        for (int pos=0; pos<getNumberOfSyncPoints(); pos++){
            buffer.append(getSyncPointAt(pos).toXML());
        }
        buffer.append("</sync-points>");
        return buffer.toString();                
    }
    
    /** writes a string representing this object in XML to the specified output stream */
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException {
        fo.write(toXML().getBytes("UTF-8"));
    }
    
    /** returns the width of this object in pixels  */
    public double getWidth() {
        return -1;
    }
    
    /** returns the difference of offsets of the chunk */
    public double getCombinedWidth(ItChunk itc){
        return getCombinedWidth(itc.getStart(), itc.getEnd());
    }
 
    /** returns the difference of offsets of the sync points */
    public double getCombinedWidth(SyncPoint sp1, SyncPoint sp2){
        return (sp2.getOffset()-sp1.getOffset());
    }
    
    /** sets the format of this object  */
    public void setFormat(Format f) {
        format = f;
    }
    
    /** returns the format of this object  */
    public Format getFormat() {
        return format;
    }
    
    /** returns true iff this object has ud info */
    public boolean hasUdInformation() {
        return (udInfo!=null);
    }
    
    /** sets the ud info */
    public void setUdInformation(UdInformation udi) {
        udInfo = udi;
    }
    
    /** returns the ud info */
    public UdInformation getUdInformation() {
        return udInfo;
    }
    
    /** returns the height of this object in pixels  */
    public double getHeight() {
        double result=0;
        for (int pos=0; pos<getNumberOfSyncPoints();pos++){
            result = Math.max(result,getSyncPointAt(pos).getHeight());
        }
        return result;
    }
    
    public double getDescent() {
        double result=0;
        for (int pos=0; pos<getNumberOfSyncPoints();pos++){
            result = Math.max(result,getSyncPointAt(pos).getDescent());
        }
        return result;
    }
    
    public boolean hasFormat() {
        return (getFormat()!=null);        
    }
    
    /** propagates this object's format to its children that don't have formats  */
    public void propagateFormat() {
        for (int pos=0; pos<getNumberOfSyncPoints(); pos++){
            if (!getSyncPointAt(pos).hasFormat()){
                getSyncPointAt(pos).setFormat(this.getFormat());
            }
        }        
    }
    
    /** returns true iff sp1 is before sp2 in this obejct */
    public boolean isBefore(SyncPoint sp1, SyncPoint sp2){
        //System.out.println("Comparing " + sp1.getID() + " and " + sp2.getID());
        return (lookupID(sp1.getID())<lookupID(sp2.getID()));
    }
    
    public SyncPoints cut(BreakParameters param, int criticalSync, double pixelsThatFit, double pixelsThatDontFit){
        SyncPoints result = new SyncPoints();
        result.setFormat(this.getFormat());
        for (int pos=0; pos<=criticalSync; pos++){
            // add all syncPoints until the critical one to the result
            result.addSyncPoint(getSyncPointAt(pos));
        }
        // then make a copy of the critical syncPoint
        SyncPoint newSyncPoint = getSyncPointAt(criticalSync).makeCopy();
        // and change its text
        // to indicate that it is now just a continuation of another syncPoint
        // if there are more pixels available than the tolerance
        if (pixelsThatFit>param.getTolerance()){             
            newSyncPoint.setText("..");        
        }
        // change the offset of the new syncPoint
        // because it is now the first of the remaining ones
        newSyncPoint.setOffset(getSyncPointAt(0).getOffset());

        // the text of the critical syncPoint in the result may take up more space than the available pixels
        // not sure how to deal with this intelligently, for the moment: just delete the text if this situation occurs...
        if (result.getSyncPointAt(criticalSync).getWidth()>pixelsThatFit){
            result.getSyncPointAt(criticalSync).setText("");
        }
        // remove all syncPoints until the critical one (they have been added to the result)
        for (int pos=0; pos<=criticalSync; pos++){
            removeElementAt(0);
        }
        // insert the copy of the original critical sync point as the first of the remaining syncPoints
        this.insertElementAt(newSyncPoint,0);        
        // beacause syncPoints have been removed and added , the position hashtable must be updated
        updatePositions();
        return result;
    }
    
    /** writes an HTML representation of this object to the specified file output stream */
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }
    
    /** returns a HTML representation of this object */
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<tr");
        sb.append(">");
        
        sb.append("<td");
        sb.append(" class=\"EMTPY\"");
        sb.append(" style=\"");
        if ((param.drawFrame) && (!param.putSyncPointsOutside) && (param.frame.indexOf('t')>=0)){
            sb.append(" border-top-color:" + HTMLUtilities.toHTMLColorString(param.frameColor) + ";");
            sb.append(HTMLUtilities.toHTMLBorderString("t",param.frameStyle));
        }
        if ((param.drawFrame) && (!param.putSyncPointsOutside) && (param.frame.indexOf('l')>=0)){
            sb.append(" border-left-color:" + HTMLUtilities.toHTMLColorString(param.frameColor) + ";");
            sb.append(HTMLUtilities.toHTMLBorderString("l",param.frameStyle));
        }
        else if ((param.drawFrame) && (param.putSyncPointsOutside) && (param.frame.indexOf('t')>=0)){
            sb.append(" border-bottom-color:" + HTMLUtilities.toHTMLColorString(param.frameColor) + ";");
            sb.append(HTMLUtilities.toHTMLBorderString("b",param.frameStyle));
            //sb.append("\"");
        }
        sb.append("\"");
        sb.append(">");

        sb.append("</td>");

        for (int pos=0; pos<getNumberOfSyncPoints()-1; pos++){
            SyncPoint sp = getSyncPointAt(pos);
            param.isLastChunk=(pos==getNumberOfSyncPoints()-2);
            sb.append(sp.toHTML(param));
        }
        sb.append("</tr>\n");                    
        return sb.toString();        
    }
    
    String toRTFTableRowDefinition(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        //sb.append("\\trowd\\trgaph0\\trleft-70");  // defaults / space betw. table rows / position of left edge
        // 20-10-2008: changed left indent to zero because it looketh shite
        sb.append("\\trowd\\trgaph0");  // defaults / space betw. table rows / position of left edge
        sb.append("\\trftsWidth1"); // no preferred row width
        sb.append("\\trpaddl0\\trpaddr0\\trpaddfl3\\trpaddfr3");  // default left/right cell margin / units for these is twips

        // empty cell
        sb.append("\\clvertalt");   // cell align top        
        param.isFirstChunk=!param.putSyncPointsOutside;
        param.isLastChunk=false;
        param.isLastLine=false;
        param.isFirstLine = !param.putSyncPointsOutside;
        sb.append(RTFUtilities.toRTFBorderDefinition(param.emptyFormat, param));  // border definition
        sb.append("\\cltxlrtb");    // text flow left to right and top to bottom
        sb.append("\\clftsWidth3"); // cell width is in twips
        sb.append("\\clwWidth" + Long.toString(Math.round(getSyncPointAt(0).getOffset()*20)));    // preferred Cell Width (in twips!)
        sb.append("\\cellx" + Long.toString(Math.round(getSyncPointAt(0).getOffset()*20))); // position of right edge (in twips!)          

        param.isFirstChunk=false;
        for (int pos=0; pos<getNumberOfSyncPoints()-1; pos++){
            param.isLastChunk=!param.putSyncPointsOutside && pos==getNumberOfSyncPoints()-2;
            SyncPoint sp = getSyncPointAt(pos);
            param.nextSyncPoint = getSyncPointAt(pos+1);
            sb.append(sp.toRTFTableCellDefinition(param));
        }
        return sb.toString();
    }
    
    String toRTFTableRowContent(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("\\pard\\plain\\ql\\li0\\ri0");   //reset to defaults / ??? / left-aligned / left/right indent
        // Removed for version 1.3.3, 09-Octo-2006
        /*if (param.isFirstParagraph){
            sb.append("\\pagebb");
        }*/
        sb.append("\\widctlpar\\intbl");            //widow/orphan control on / paragraph is in table (!)
        // added for version 1.3.3, 09-Oct-2006
        sb.append("\\keepn");       // Keep paragraph with the next paragraph        
        sb.append("{");
        // empty cell
        sb.append("{\\fs4 }");
        sb.append("\\cell");
        for (int pos=0; pos<getNumberOfSyncPoints()-1; pos++){
            SyncPoint sp = getSyncPointAt(pos);
            param.nextSyncPoint = getSyncPointAt(pos+1);
            sb.append(sp.toRTFTableCellContent(param));
        }
        sb.append("}");
        return sb.toString();
    }
    
    public String toRTFTableRow(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append(toRTFTableRowDefinition(param));
        sb.append(toRTFTableRowContent(param));
        sb.append("\\row");
        return sb.toString();
    }
    
    
    public void addUdInformation(String propertyName, String propertyValue) {
        udInfo.setProperty(propertyName, propertyValue);
    }

    public String toSVG(SVGParameters param){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<getNumberOfSyncPoints()-1; pos++){
            SyncPoint sp = getSyncPointAt(pos);
            param.nextSyncPoint = getSyncPointAt(pos+1);
            double x = sp.getOffset();
            double w = param.nextSyncPoint.getOffset() - sp.getOffset();
            double h = this.getHeight();
            String border = SVGUtilities.drawBorder(getFormat().getProperty("chunk-border"), 
                                                         getFormat().getProperty("chunk-border-style"), 
                                                         getFormat().getProperty("chunk-border-color"), 
                                                         getFormat().getProperty("bg-color"),
                                                         x,10,w,h);
            sb.append(XMLUtilities.makeXMLComment("syncPoint border"));
            sb.append(border);
            String spSVG = sp.toSVG(param, x);
            sb.append(spSVG);
        }
        return sb.toString();
    }
    
    public void print(java.awt.Graphics2D graphics, PrintParameters param) {
        param.currentY+=getHeight();
        double y = param.currentY + getDescent() - getHeight();
        double h = getHeight();
        for (int pos=0; pos<getNumberOfSyncPoints()-1; pos++){
            SyncPoint sp = getSyncPointAt(pos);
            param.nextSyncPoint = getSyncPointAt(pos+1);
            double x = param.getPaperMeasure("margin:left", OutputParameters.PX_UNIT) + sp.getOffset();
            double w = param.nextSyncPoint.getOffset() - sp.getOffset();
            java.awt.geom.Rectangle2D.Double r = new java.awt.geom.Rectangle2D.Double(x,y,w,h);
            Format format = sp.getFormat();
            PrintUtilities.drawBorder(graphics, r, 
                                      format.getProperty("chunk-border"), 
                                      format.getProperty("chunk-border-style"), 
                                      format.makeColor("chunk-border-color"), 
                                      format.makeColor("bg-color"));
            sp.print(graphics,param);
        }
    }

    /** returns a HTML representation of this object */
    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();

        // empty cell left upper corner
        sb.append("<w:tr>");    // table row 
        sb.append("<w:trPr>");    // table row properties
        sb.append("<w:trHeight w:h-rule=\"exact\" ");
        sb.append("w:val=\"" + Math.round(getHeight()*20) + "\"/>");
        sb.append("</w:trPr>");           
        sb.append("<w:tc>");    // table cell
        sb.append("<w:tcPr>");    // table cell properties
        param.isFirstChunk=!param.putSyncPointsOutside;
        param.isLastChunk=false;
        param.isLastLine=false;
        param.isFirstLine = !param.putSyncPointsOutside;
        sb.append(WordMLUtilities.toWordMLBorderDefinition(param.emptyFormat, param));  // border definition
        sb.append("</w:tcPr>");      
        sb.append(WordMLUtilities.prtEnvelope(""));
        sb.append("</w:tc>");      
        
        // go through the syncPoints
        for (int pos=0; pos<getNumberOfSyncPoints()-1; pos++){
            SyncPoint sp = getSyncPointAt(pos);
            param.isLastChunk=(pos==getNumberOfSyncPoints()-2);
            sb.append(sp.toWordML(param));
        }
        sb.append("</w:tr>");                    
        return sb.toString();        
    }
    
    public String toColumnDefinition(){
        StringBuffer sb = new StringBuffer();
        double lastOffset = 0;
        for (int pos=0; pos<getNumberOfSyncPoints(); pos++){
            SyncPoint sp = getSyncPointAt(pos);
            long colWidth = Math.round((sp.getOffset() - lastOffset) * 20);
            lastOffset = sp.getOffset();
            sb.append("<w:gridCol w:w=\"" + Long.toString(colWidth) + "\"/>");
        }
        return sb.toString();
    }
    
}
