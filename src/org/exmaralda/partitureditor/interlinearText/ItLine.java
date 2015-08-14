/*
 * ItLine.java
 *
 * Created on 26. Februar 2002, 12:22
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ItLine extends java.util.Vector implements XMLElement, Formattable, UdInformable, HTMLable{

    public static final short NB_TIME = 0;
    public static final short NB_NOTIME = 1;
    public static final short NB_DEP = 2;
    public static final short NB_LNK = 3;
    public static final short B = 4;
    public static final short IMG = 5;
    public static String[] BREAK_TYPE_STRINGS = {"NB_TIME","NB_NOTIME","NB_DEP","NB_LNK","B","IMG"};
    
    private ItLabel label;
    private Format format;
    private UdInformation udInfo;
    private short breakType;
    private java.util.Hashtable starts;
    private java.util.Hashtable ends;
    
    /** Creates new ItLine */
    public ItLine() {
        starts = new java.util.Hashtable();
        ends = new java.util.Hashtable();
    }

    /** returns the break type of this line */
    public short getBreakType(){
        return breakType;
    }
    
    /** sets the break type of this line */
    public void setBreakType(short bt){
        breakType = bt;
    }
    
    /** returns the label of this line (if it has one...) */
    public ItLabel getLabel(){
        return label;
    }
    
    /** sets the label of this line to the specified value */
    public void setLabel(ItLabel l){
        label=l;
    }
    
    /** returns true iff the line has a label */
    public boolean hasLabel(){
        return (label!=null);
    }
    
    /** returns the format of this object  */
    public Format getFormat() {
        return format;
    }
    
    /** writes a string representing this object in XML to the specified output stream */
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException {
        fo.write(toXML().getBytes("UTF-8"));
    }
    
    /** returns the width of this object in pixels  */
    public double getWidth() {
        return -1;
    }
    
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<it-line breaktype=\"");
        buffer.append(BREAK_TYPE_STRINGS[getBreakType()]);
        if (hasFormat()){
            buffer.append("\" formatref=\"");
            buffer.append(getFormat().getID());
        }
        buffer.append("\">");
        if (hasUdInformation()) buffer.append(getUdInformation().toXML());
        if (hasLabel()) buffer.append(getLabel().toXML());
        for (int pos=0; pos<getNumberOfItChunks(); pos++){
            buffer.append(getItChunkAt(pos).toXML());
        }
        buffer.append("</it-line>");        
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
        //System.out.println("Setting " + f.getID());
        format = f;
    }
    
    /** returns true iff the line has ud information */
    public boolean hasUdInformation() {
        return (udInfo!=null);        
    }
    
    /** sets the ud info to the specified value */
    public void setUdInformation(UdInformation udi) {
        udInfo = udi;
    }
    
    /** returns the ud information of this line */
    public UdInformation getUdInformation() {
        return udInfo;
    }
    
    /** returns the number of it chunks contained in this line */
    public int getNumberOfItChunks(){
        return size();
    }
    
    /** returns the it chunk at the specified position */
    public ItChunk getItChunkAt(int pos){
        return (ItChunk)elementAt(pos);
    }

    /** adds the given it chunk to this line */
    public void addItChunk(ItChunk itc){
        addElement(itc);
        // update the start points hashtable accordingly
        starts.put(itc.getStart().getID(),new Integer(getNumberOfItChunks()-1));  
        if (itc.getEnd()!=null){
            // update the end points hashtable accordingly
            ends.put(itc.getEnd().getID(),new Integer(getNumberOfItChunks()-1));  
        }
        if (!itc.hasFormat()){
            // if the chunk doesn't have a format, make it inherit this line's format
            itc.setFormat(this.getFormat());
            itc.propagateFormat();
        }                
    }
    
    /** returns the height of this object in pixels  */
    public double getHeight() {
        //System.out.println(getFormat().toXML());
        if (this.getFormat().getProperty("row-height-calculation","Generous").equals("Fixed")){
            return Short.parseShort(getFormat().getProperty("fixed-row-height","10"));
        }
        double result = 0;
        if (this.getFormat().getProperty("row-height-calculation","Generous").equals("Generous")){
            if (hasLabel()) result = getLabel().getHeight();
        }
        for (int pos=0; pos<getNumberOfItChunks();pos++){
            result = Math.max(result, getItChunkAt(pos).getHeight());
        }
        return result;
    }
    
    
    public double getDescent() {
        double result = 0;
        if (hasLabel()) result = getLabel().getDescent();
        for (int pos=0; pos<getNumberOfItChunks();pos++){
            result = Math.max(result, getItChunkAt(pos).getDescent());
        }
        return result;
    }

    /** returns true iff this line has been assigned a format */
    public boolean hasFormat() {
        return (getFormat()!=null);        
    }
    
    /** propagates this object's format to its children that don't have formats  */
    public void propagateFormat() {
        for (int pos=0; pos<getNumberOfItChunks(); pos++){
            if(!getItChunkAt(pos).hasFormat()){
                getItChunkAt(pos).setFormat(this.getFormat());
            }
        }
    }
    
    /** returns true iff this line has an it chunk with a
     * start point with the specified id */
    public boolean hasItChunkStartingAtSyncPoint(String id){
        return starts.containsKey(id);
    }
    
    /** returns true iff this line has an it chunk with an
     * end point with the specified id */
    public boolean hasItChunkEndingAtSyncPoint(String id){
        return ends.containsKey(id);
    }

    /** returns the id chunk with the specified start point */
    public ItChunk getItChunkStartingAtSyncPoint(String id){
        return getItChunkAt(lookupStartID(id));
    }
    
    /** returns the id chunk with the specified end point */
    public ItChunk getItChunkEndingAtSyncPoint(String id){
        return getItChunkAt(lookupEndID(id));
    }

    /** removes the id chunk with the specified start point */
    public void removeItChunkStartingAtSyncPoint(String id){
        removeElementAt(lookupStartID(id));
        updatePositions();
    }
    
    /** updates the position hashtable */
    private void updatePositions(){
        starts.clear();
        ends.clear();
        for (int pos=0; pos<getNumberOfItChunks(); pos++){
            starts.put(getItChunkAt(pos).getStart().getID(), new Integer(pos));
            ends.put(getItChunkAt(pos).getEnd().getID(), new Integer(pos));
        }       
    }
    
    private int lookupStartID(String id){
        Integer pos = (Integer)starts.get(id);
        return pos.intValue();
    }
    
    private int lookupEndID(String id){
        Integer pos = (Integer)ends.get(id);
        return pos.intValue();
    }

    /** cuts a piece from this line that fits into the space
     * determined by the given parameters (criticalSync and pixelsThatFit)
     * and returns that piece */
    public ItLine cut(BreakParameters param,SyncPoints syncPoints,int criticalSync,double pixelsThatFit, double pixelsThatDontFit){
        ItLine result=new ItLine();
        result.setLabel(this.getLabel());
        result.setFormat(this.getFormat());
        for (int pos=0; pos<=criticalSync; pos++){
            if (hasItChunkStartingAtSyncPoint(syncPoints.getSyncPointAt(pos).getID())){
                ItChunk itc = getItChunkStartingAtSyncPoint(syncPoints.getSyncPointAt(pos).getID());
                if (syncPoints.isBefore(itc.getEnd(), syncPoints.getSyncPointAt(criticalSync+1))){  
                    // i.e. this chunk ends before or at the critical syncPoint
                    // so it can be added to the cut line and be removed from the original line
                    result.addItChunk(itc);
                    this.removeItChunkStartingAtSyncPoint(itc.getStart().getID());
                } else {
                    // i.e. this chunk ends after the critical syncPoint
                    // so maybe it has to be cut
                    // three possibilities:
                    // 1: there are more pixels available than the tolerance
                    // --> just cut the chunk at the appropriate position
                    if (pixelsThatFit>param.getTolerance()){
                        ItChunk newItChunk = itc.cut(getBreakType(), param, syncPoints, criticalSync, pixelsThatFit, pixelsThatDontFit);                    
                        param.cutChunks.addElement(itc);
                        result.addItChunk(newItChunk);
                    // 2: there are fewer pixels available than the tolerance, but the chunk starts before the critical sync
                    // --> cut it, but only up to the critical sync, i.e. set pixelsThatFit to 0
                    } else if (syncPoints.isBefore(itc.getStart(),syncPoints.getSyncPointAt(criticalSync))){
                        ItChunk newItChunk = itc.cut(getBreakType(), param, syncPoints, criticalSync, 0, pixelsThatDontFit);                    
                        param.cutChunks.addElement(itc);
                        result.addItChunk(newItChunk);                        
                    } else {
                        // this is just to make sure that the correct start point will be assigned
                        param.cutChunks.addElement(itc);
                    }                    
                }   
            }
        }
        updatePositions(); //necessary because start point of last element may have changed
        return result;
    }
    
    /** assigns appropriate end points to all it chunks that don't have end points */
    void implyEnds(SyncPoints syncPoints){
        for (int pos2=0; pos2<syncPoints.getNumberOfSyncPoints(); pos2++){
            SyncPoint sp = syncPoints.getSyncPointAt(pos2);
            if ((hasItChunkStartingAtSyncPoint(sp.getID())) && 
                (getItChunkStartingAtSyncPoint(sp.getID()).getEnd()==null)){
                int nextStart=pos2+1;
                while ((nextStart<syncPoints.getNumberOfSyncPoints()-1) && 
                       (!hasItChunkStartingAtSyncPoint(syncPoints.getSyncPointAt(nextStart).getID()))){
                           nextStart++;
                }
                getItChunkStartingAtSyncPoint(sp.getID()).setEnd(syncPoints.getSyncPointAt(nextStart));                    
            }
        }
        updatePositions();
    }

    /** writes an HTML representation of this object to the specified file output stream */
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }    
    
    /** returns a HTML representation of this object */
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<tr>\n");
        param.rowHeight = Math.round(this.getHeight());
        sb.append(getLabel().toHTML(param));
        for (int pos=0; pos<param.syncPoints.getNumberOfSyncPoints()-1; pos++){
            SyncPoint sp = param.syncPoints.getSyncPointAt(pos);
            if (hasItChunkStartingAtSyncPoint(sp.getID())){
                ItChunk itc = getItChunkStartingAtSyncPoint(sp.getID());               
                pos+=param.syncPoints.span(itc)-1;
                param.isLastChunk = (pos>=param.syncPoints.getNumberOfSyncPoints()-2);
                sb.append(itc.toHTML(param));
            } else {    // empty cell
                //sb.append("<td");
                sb.append("<td height=\"" + Long.toString(param.rowHeight) + "px\"");
                param.isLastChunk = (pos>=param.syncPoints.getNumberOfSyncPoints()-2);                
                if ((param.drawFrame) && (!param.isOutside)){
                    sb.append(" style=\"");
                    if ((param.isLastLine) && (param.frame.indexOf('b')>=0)){
                        sb.append(" border-bottom-color:" + HTMLUtilities.toHTMLColorString(param.frameColor) + ";");
                        sb.append(HTMLUtilities.toHTMLBorderString("b", param.frameStyle));
                    }
                    if ((param.isFirstLine) && (!param.includeSyncPoints) && (param.frame.indexOf('t')>=0)){
                        sb.append(" border-top-color:" + HTMLUtilities.toHTMLColorString(param.frameColor) + ";");
                        sb.append(HTMLUtilities.toHTMLBorderString("t", param.frameStyle));
                    }
                    if ((param.isLastChunk) && (param.frame.indexOf('r')>=0))  {
                        sb.append(" border-right-color:" + HTMLUtilities.toHTMLColorString(param.frameColor) + ";");
                        sb.append(HTMLUtilities.toHTMLBorderString("r", param.frameStyle));
                    }
                    sb.append("\"");
                }                                
                // added for version 1.3.3., 21-Nov-2006
                if (param.isLastChunk && param.smoothRightBoundary){
                    sb.append(" width=\"100%\"");
                }
                sb.append("></td>");
            }
        }        
        sb.append("\t</tr>");
        return sb.toString();        
    }
    
    String toRTFTableRowDefinition(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        //sb.append("\\trowd\\trgaph70\\trleft-70");  // defaults / space betw. table rows / position of left edge
        // 20-10-2008: changed left indent to zero because it looketh shite
        sb.append("\\trowd\\trgaph0");  // defaults / space betw. table rows / position of left edge
        sb.append("\\trftsWidth1"); // no preferred row width
        // added 26-04-2004 / take 2
        if (!getFormat().getProperty("row-height-calculation").equalsIgnoreCase("Generous")){
            long rowHeight = Math.round(this.getHeight()*20); // height in twips
            sb.append("\\trrh-" + Long.toString(rowHeight));
        }
        sb.append("\\trpaddl0\\trpaddr0\\trpaddfl3\\trpaddfr3");  // default left/right cell margin / unit for these is twips
        sb.append("\\trpaddb0\\trpaddt0\\trpaddfb3\\trpaddft3");  // default bottom/top cell margin / unit for these is twips
        //sb.append("\\trspdl0\\trspdr0\\trspdfl3\\trspdfr3");    // default left/right cell spacing / unit for these is twips
        //sb.append("\\trspdb0\\trspdt0\\trspdfb3\\trspdft3");    // default bottom/top cell spacing / unit for these is twips
        param.labelWidth = param.syncPoints.getSyncPointAt(0).getOffset();
        sb.append(getLabel().toRTFTableCellDefinition(param));
        for (int pos=0; pos<param.syncPoints.getNumberOfSyncPoints()-1; pos++){
            SyncPoint sp = param.syncPoints.getSyncPointAt(pos);
            param.isFirstChunk = false;
            if (hasItChunkStartingAtSyncPoint(sp.getID())){
                ItChunk itc = getItChunkStartingAtSyncPoint(sp.getID());               
                pos+=param.syncPoints.span(itc)-1;
                param.isLastChunk = (pos>=param.syncPoints.getNumberOfSyncPoints()-2);
                sb.append(itc.toRTFTableCellDefinition(param));
            } else {    // empty cell
                sb.append("\\clvertalt");   // cell align top        
                param.isLastChunk = (pos+1>=param.syncPoints.getNumberOfSyncPoints()-1);
                sb.append(RTFUtilities.toRTFBorderDefinition(param.emptyFormat, param));  // border definition
                sb.append("\\cltxlrtb");    // text flow left to right and top to bottom
                sb.append("\\clftsWidth3"); // cell width is in twips
                SyncPoint nextSp = param.syncPoints.getSyncPointAt(pos+1);                
                sb.append("\\clwWidth" + Long.toString(Math.round((nextSp.getOffset() - sp.getOffset())*20)));    // preferred Cell Width (in twips!)
                sb.append("\\cellx" + Long.toString(Math.round((nextSp.getOffset())*20))); // position of right edge (in twips!)                
            }
        }
        return sb.toString();
    }
    
    String toRTFTableRowContent(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("\\pard\\plain\\ql\\li0\\ri0");   //reset to defaults / ??? / left-aligned / left/right indent
        // Removed for version 1.3.3, 09-Octo-2006
        /*if (param.isFirstParagraph){
            sb.append("\\pagebb");
            param.isFirstParagraph=false;
        }*/
        sb.append("\\widctlpar\\intbl");            //widow/orphan control on / paragraph is in table (!)
        // added for version 1.3.3, 09-Oct-2006
        if (!param.isLastLine){
            sb.append("\\keepn");       // Keep paragraph with the next paragraph        
        }
        sb.append("{");
        sb.append(getLabel().toRTFTableCellContent(param));
        for (int pos=0; pos<param.syncPoints.getNumberOfSyncPoints()-1; pos++){
            SyncPoint sp = param.syncPoints.getSyncPointAt(pos);
            if (hasItChunkStartingAtSyncPoint(sp.getID())){
                ItChunk itc = getItChunkStartingAtSyncPoint(sp.getID());               
                pos+=param.syncPoints.span(itc)-1;
                param.isLastChunk = (pos>=param.syncPoints.getNumberOfSyncPoints()-2);
                sb.append(itc.toRTFTableCellContent(param));
            } else {    // empty cell
                sb.append("{}");
                sb.append("\\cell");
            }
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
        if (!hasUdInformation()) {udInfo = new UdInformation();}
        udInfo.setProperty(propertyName, propertyValue);
    }

    public String toSVG(SVGParameters param, double y){
        StringBuffer sb = new StringBuffer();
        double h = getHeight();
        sb.append(XMLUtilities.makeXMLComment("row height: " + getHeight()));
        String labelBorder = SVGUtilities.drawBorder(getLabel().getFormat().getProperty("chunk-border"), 
                                                     getLabel().getFormat().getProperty("chunk-border-style"), 
                                                     getLabel().getFormat().getProperty("chunk-border-color"), 
                                                     getLabel().getFormat().getProperty("bg-color"),
                                                     10,y-h+getDescent(),param.syncPoints.getSyncPointAt(0).getOffset()-12,h);
        sb.append(XMLUtilities.makeXMLComment("label border"));
        sb.append(labelBorder);
        String labelSVG = getLabel().toSVG(param, 11, y );
        sb.append(XMLUtilities.makeXMLComment("label"));
        sb.append(labelSVG);
        for (int pos=0; pos<param.syncPoints.getNumberOfSyncPoints()-1; pos++){
            SyncPoint sp = param.syncPoints.getSyncPointAt(pos);
            if (hasItChunkStartingAtSyncPoint(sp.getID())){
                ItChunk currentItChunk = getItChunkStartingAtSyncPoint(sp.getID());
                double x = currentItChunk.getStart().getOffset();
                double w = currentItChunk.getEnd().getOffset() - currentItChunk.getStart().getOffset();
                format = currentItChunk.getFormat();                            
                String border = SVGUtilities.drawBorder(format.getProperty("chunk-border"), 
                                                        format.getProperty("chunk-border-style"), 
                                                        format.getProperty("chunk-border-color"), 
                                                        format.getProperty("bg-color"),
                                                        x,y-h+getDescent(),w,h);
                sb.append(XMLUtilities.makeXMLComment("chunk border"));
                sb.append(border);
                sb.append(XMLUtilities.makeXMLComment("chunk"));
                String chunkSVG = currentItChunk.toSVG(param, x, y );
                sb.append(chunkSVG);
            }            
        }                                   
        return sb.toString();
    }
    
    public void print(java.awt.Graphics2D graphics, PrintParameters param) {
        param.resetCurrentX();
        param.currentY+=getHeight();
        double y = param.currentY + getDescent() - getHeight();
        double h = getHeight();
        double x = param.getPaperMeasure("margin:left", OutputParameters.PX_UNIT);
        double w = param.syncPoints.getSyncPointAt(0).getOffset();
        java.awt.geom.Rectangle2D.Double r = new java.awt.geom.Rectangle2D.Double(x,y,w,h);
        Format format = getLabel().getFormat();
        PrintUtilities.drawBorder(graphics, r, 
                                  format.getProperty("chunk-border"), 
                                  format.getProperty("chunk-border-style"), 
                                  format.makeColor("chunk-border-color"), 
                                  format.makeColor("bg-color"));
        getLabel().print(graphics, param);
        for (int pos=0; pos<param.syncPoints.getNumberOfSyncPoints()-1; pos++){
            SyncPoint sp = param.syncPoints.getSyncPointAt(pos);
            if (hasItChunkStartingAtSyncPoint(sp.getID())){
                ItChunk currentItChunk = getItChunkStartingAtSyncPoint(sp.getID());
                x = param.getPaperMeasure("margin:left", OutputParameters.PX_UNIT) + currentItChunk.getStart().getOffset();
                w = currentItChunk.getEnd().getOffset() - currentItChunk.getStart().getOffset();
                r = new java.awt.geom.Rectangle2D.Double(x,y,w,h);
                format = currentItChunk.getFormat();
                PrintUtilities.drawBorder(graphics, r, 
                                          format.getProperty("chunk-border"), 
                                          format.getProperty("chunk-border-style"), 
                                          format.makeColor("chunk-border-color"), 
                                          format.makeColor("bg-color"));
                currentItChunk.print(graphics, param);
            } else {    // empty cell
            }
        }
    }
    
    /** returns a HTML representation of this object */
    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<w:tr>");    // table row
        sb.append("<w:trPr>");    // table row properties
        String hrule = "exact";
        if (getFormat().getProperty("row-height-calculation").equalsIgnoreCase("Generous")){
            hrule = "at-least";
        }
        sb.append("<w:trHeight w:h-rule=\"" + hrule + "\" ");
        sb.append("w:val=\"" + Math.round(getHeight()*20) + "\"/>");
        sb.append("</w:trPr>");           
        sb.append(getLabel().toWordML(param));
        param.isFirstChunk = false;            
        for (int pos=0; pos<param.syncPoints.getNumberOfSyncPoints()-1; pos++){
            SyncPoint sp = param.syncPoints.getSyncPointAt(pos);
            if (hasItChunkStartingAtSyncPoint(sp.getID())){
                ItChunk itc = getItChunkStartingAtSyncPoint(sp.getID());               
                pos+=param.syncPoints.span(itc)-1;
                param.isLastChunk = (pos>=param.syncPoints.getNumberOfSyncPoints()-2);
                sb.append(itc.toWordML(param));
            } else {    // empty cell
                sb.append("<w:tc>");    // table cell
                sb.append("<w:tcPr>");    // table cell properties
                param.isLastChunk = (pos+1>=param.syncPoints.getNumberOfSyncPoints()-1);
                sb.append(WordMLUtilities.toWordMLBorderDefinition(param.emptyFormat, param));  // border definition
                sb.append("</w:tcPr>");
                sb.append(WordMLUtilities.prtEnvelope(""));
                sb.append("</w:tc>");
            }
        }        
        sb.append("</w:tr>");
        return sb.toString();        
    }
    
    
    
}
