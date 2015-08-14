/*
 * ItBundle.java
 *
 * Created on 26. Februar 2002, 15:50
 */

package org.exmaralda.partitureditor.interlinearText;

import java.io.*;
import java.util.Collections;
/**
 *
 * @author  Thomas
 * @version 
 */
public class ItBundle extends java.util.Vector 
        implements XMLElement, Formattable, UdInformable, ItElement, HTMLable, RTFable, WordMLable {

    private UdInformation udInfo;
    private Format format;
    public int frameEndPosition = -2;   // this is the last line INSIDE the frame
    private SyncPoints syncPoints;
    private double minChunkWidth = 10;
    java.util.Vector anchors;
    private double maxOffsetFactor = 1;
    
    /** Creates new ItBundle */
    public ItBundle() {
        anchors = new java.util.Vector();        
        syncPoints = new SyncPoints();
    }

    /** returns a copy of this object */
    public ItBundle makeCopy(){
        ItBundle result = new ItBundle();
        result.setFormat(this.getFormat());
        result.frameEndPosition = this.frameEndPosition;
        result.setSyncPoints(this.getSyncPoints());
        result.setUdInformation(this.getUdInformation());
        for (int pos=0; pos<this.getNumberOfItLines();pos++){
            result.addItLine(this.getItLineAt(pos));
        }
        return result;
    }
    
    /** returns the syncPoints of this bundle */
    public SyncPoints getSyncPoints(){
        return syncPoints;
    }
    
    /** sets the sync points of this bundle */
    public void setSyncPoints(SyncPoints sp){
        syncPoints=sp;
        // make ids of syncPoints anchors of this element
        for (int pos=0; pos<sp.getNumberOfSyncPoints()-1; pos++){
            SyncPoint s = sp.getSyncPointAt(pos);
            anchors.addElement(s.getID());
        }        
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

    /** returns the number of it lines contained in this bundle */
    public int getNumberOfItLines(){
        return size();
    }
    
    /** returns the it line at the specified postition */
    public ItLine getItLineAt(int pos){
        return (ItLine)elementAt(pos);
    }
    
    /** adds the given it line to the bundle */
    public void addItLine(ItLine il){
        addElement(il);
        if (!il.hasFormat()){
            il.setFormat(this.getFormat());
            il.propagateFormat();
        }        
    }
    
    /** returns the height of this object in pixels  */
    public double getHeight() {
        return getHeight(true);
    }
    
    /** returns the height of this bundle in pixels */
    public double getHeight(boolean includeSyncPoints){
        double result=0;
        if (includeSyncPoints) result=getSyncPoints().getHeight();
        for (int pos=0; pos<getNumberOfItLines();pos++){
            result+=getItLineAt(pos).getHeight();
        }
        return result;
    }
    /** returns the format of this object  */
    public Format getFormat() {
        return format;
    }
    
    /** return true iff this bundle has ud info */
    public boolean hasUdInformation() {
        return (udInfo!=null);
    }
    
    /** sets the ud info of this bundle to the specified value */
    public void setUdInformation(UdInformation udi) {
        udInfo = udi;
    }
    
    /** writes a string representing this object in XML to the specified output stream */
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException {
        fo.write(toXML().getBytes("UTF-8"));
    }
    
    /** returns the width of this object in pixels  */
    public double getWidth() {
        return getSyncPoints().getWidth();
    }
    
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        if (hasFormat()){
            buffer.append("<it-bundle formatref=\"");
            buffer.append(getFormat().getID());
            buffer.append("\">");
        } else {
            buffer.append("<it-bundle>");
        }
        if (hasUdInformation()) buffer.append(getUdInformation().toXML());  
        if (anchors.size()>0){
            for (int pos=0; pos<anchors.size(); pos++){
                buffer.append("<anchor>" + (String)(anchors.elementAt(pos)) + "</anchor>");
            }
        }
        buffer.append(getSyncPoints().toXML());
        for (int pos=0;pos<getNumberOfItLines();pos++){
            buffer.append(getItLineAt(pos).toXML());
            if (pos==frameEndPosition) {buffer.append("<frame-end/>");}
        }
        buffer.append("</it-bundle>");
        return buffer.toString();
    }
    
    /** returns the ud info of this bundle */
    public UdInformation getUdInformation() {
        return udInfo;
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
    
    /** always returns true */
    public boolean isItBundle() {
        return true;
    }
    
    /** returns true iff this bundle has been assigned a format  */
    public boolean hasFormat() {
        return (getFormat()!=null);        
    }
    
    /** propagates this object's format to its children that don't have formats  */
    public void propagateFormat() {
        for (int pos=0; pos<getNumberOfItLines(); pos++){
            if (!getItLineAt(pos).hasFormat()){
                getItLineAt(pos).setFormat(this.getFormat());
            }
        }
    }
    
    /** calculates the offsets for syncPoints until maxOffset is reached 
     * returns the position of the first syncPoint with an offset greater than maxOffset
     * returns -1 if there was no syncPoint with an offset greater than offset*/
    public int calculateOffsets(boolean includeSyncPoints, double maxOffset, int additionalLabelSpace){

        double offset=additionalLabelSpace;
        double currentWidth = 0;

        // calculate the width of the labels
        for (int line=0; line<getNumberOfItLines(); line++){
            ItLine itLine = getItLineAt(line);
            if (itLine.hasLabel()){
                currentWidth = Math.max(currentWidth,itLine.getLabel().getWidth()+additionalLabelSpace);
            }
        }
        
        // calculate the offsets on the basis of all itChunks that span only one syncPoint
        int pos=0;
        SyncPoint lastEnd = getSyncPoints().getSyncPointAt(0);
        while ((pos<getSyncPoints().getNumberOfSyncPoints()) && (offset<=maxOffset*maxOffsetFactor)){
            SyncPoint sp = getSyncPoints().getSyncPointAt(pos);
            offset+=currentWidth;
            sp.setOffset(offset);
            currentWidth = 0;
            if (includeSyncPoints) {currentWidth=sp.getWidth();}
            else {currentWidth = minChunkWidth; }
            for (int line=0; line<getNumberOfItLines(); line++){
                ItLine itLine = getItLineAt(line);
                if (itLine.hasItChunkStartingAtSyncPoint(sp.getID())){
                    ItChunk itChunk = itLine.getItChunkStartingAtSyncPoint(sp.getID());
                    if (getSyncPoints().isBefore(lastEnd, itChunk.getEnd())){
                        // keep the last end point in memory
                        lastEnd=itChunk.getEnd();
                    }
                    if (getSyncPoints().span(itChunk)==1){
                        if (itLine.getBreakType()<ItLine.B){
                            currentWidth = Math.max(currentWidth, itChunk.getWidth());
                        } else {
                            currentWidth = Math.max(currentWidth, minChunkWidth);
                        }
                    }
                }
            }
            pos++;
        }

        // if the loop has stopped before the last syncPoint was reached: calculate one more offset
        if (pos<getSyncPoints().getNumberOfSyncPoints()){
            SyncPoint sp = getSyncPoints().getSyncPointAt(pos);
            offset+=currentWidth;
            sp.setOffset(offset);
        }
        
        
        // make sure that all offsets before lastEnd are calculated
        while ((pos<getSyncPoints().getNumberOfSyncPoints()) && 
                (getSyncPoints().isBefore(getSyncPoints().getSyncPointAt(pos),lastEnd))){
                SyncPoint sp = getSyncPoints().getSyncPointAt(pos);
                offset+=currentWidth;
                sp.setOffset(offset);
                currentWidth = 0;
                if (includeSyncPoints) {currentWidth=sp.getWidth();}
                for (int line=0; line<getNumberOfItLines(); line++){
                    ItLine itLine = getItLineAt(line);
                    if (itLine.hasItChunkStartingAtSyncPoint(sp.getID())){
                        ItChunk itChunk = itLine.getItChunkStartingAtSyncPoint(sp.getID());
                        if (getSyncPoints().span(itChunk)==1){
                            if (itLine.getBreakType()<ItLine.B){
                                currentWidth = Math.max(currentWidth, itChunk.getWidth());
                            } else {
                                currentWidth = Math.max(currentWidth, minChunkWidth);
                            }
                        }
                    }
                }
                pos++;            
        }
               
        
        // now check if those itChunks that span more than one syncPoint fit,
        // if they don't, increase the offset of all synPoints from the last syncPoint of its span to the end 
        // of the calculated area appropriately
        
        //  Rückwärts statt vorwärts, wegen Problem unten, weiß nicht, ob's wirklich hilft....
        //  for (int pos2=2; pos2<Math.min(pos,getSyncPoints().getNumberOfSyncPoints()); pos2++){
        for (int pos2=Math.min(pos,getSyncPoints().getNumberOfSyncPoints()-1); pos2>=2; pos2--){
            SyncPoint sp = getSyncPoints().getSyncPointAt(pos2);
            for (int line=0; line<getNumberOfItLines(); line++){
                ItLine itLine = getItLineAt(line);
                if (itLine.hasItChunkEndingAtSyncPoint(sp.getID())){
                    ItChunk itChunk = itLine.getItChunkEndingAtSyncPoint(sp.getID());
                    if (getSyncPoints().span(itChunk)>1){
                        double combinedWidth = syncPoints.getCombinedWidth(itChunk);
                        if ((itLine.getBreakType()<ItLine.B) && (combinedWidth<itChunk.getWidth())){
                            // Something's rotten here???
                            // YES, INDEEDY!!!!! Das wird ein lineares Ungleichungssystem.
                            // Das muss linear optimiert werden, und zwar idealerweise
                            // mit dem Simplexverfahren...
                            double missingPixels = itChunk.getWidth()-combinedWidth;
                            for (int pos3=pos2-1; pos3<Math.min(pos,getSyncPoints().getNumberOfSyncPoints()); pos3++){
                                SyncPoint sp2 = getSyncPoints().getSyncPointAt(pos3);
                                sp2.setOffset(sp2.getOffset()+missingPixels);
                            }
                        }
                    }
                }
            }
        }

        int result = -1;
        for (int pos2=0; pos2<Math.min(pos,getSyncPoints().getNumberOfSyncPoints()); pos2++){
            if (getSyncPoints().getSyncPointAt(pos2).getOffset()>=maxOffset){
                result = pos2;
                break;
            }
        }
        return result;
    }
    
    /** returns the width of the 'column' at the specified position,
     * i.e. the space that the syncPoint at this position has been assigned */
    private double getWidthOfColumn(int col){
        if ((col+1)>=syncPoints.getNumberOfSyncPoints()) return 0;
        return syncPoints.getSyncPointAt(col+1).getOffset()-syncPoints.getSyncPointAt(col).getOffset();
    }
    
    /** cuts a piece from this bundle that fits into the space specified by param
     * and returns that piece */
    public ItBundle cut(BreakParameters param){
        ItBundle result = new ItBundle();
        result.frameEndPosition = this.frameEndPosition;

        boolean includeSyncPoints = param.getIncludeSyncPoints();
        double maxWidth = param.getWidth();                

        int criticalSync = this.calculateOffsets(includeSyncPoints, maxWidth, param.additionalLabelSpace) - 1;  
            // i.e. this is the syncPoint events starting at which must be broken                      

        if (criticalSync==-2){ 
            // i.e. if nothing has to be cut
            // just return a copy of this bundle
            // and "self-destruct"
            result = this.makeCopy();
            this.clear();
            this.setSyncPoints(new SyncPoints());
            return result;
        }
        
        double pixelsThatFit = maxWidth - getSyncPoints().getSyncPointAt(criticalSync).getOffset();
            // i.e. the desired width minus the offset of the critical syncPoint
        double pixelsThatDontFit = getSyncPoints().getSyncPointAt(criticalSync+1).getOffset() - maxWidth;
            // i.e. the offset of the first syncPoint after the critical syncPoint minus the desired width
 
        //System.out.println("criticalSync : " + getSyncPoints().getSyncPointAt(criticalSync).toXML());
        //System.out.println("Pixels that fit : " + pixelsThatFit);
        //System.out.println("Pixels that don't fit " + pixelsThatDontFit);
        param.cutChunks = new java.util.Vector();
        for (int line=0; line<this.getNumberOfItLines(); line++){            
            ItLine newItLine = this.getItLineAt(line).cut(param,getSyncPoints(),criticalSync,pixelsThatFit,pixelsThatDontFit);            
            result.addItLine(newItLine);
        }       
        SyncPoints newSyncPoints = getSyncPoints().cut(param, criticalSync, pixelsThatFit, pixelsThatDontFit);
        if (pixelsThatFit>param.getTolerance()){
            SyncPoint newSyncPoint = new SyncPoint();
            newSyncPoint.setID("END");
            newSyncPoint.setText("");
            newSyncPoint.setOffset(maxWidth);
            newSyncPoints.addSyncPoint(newSyncPoint);
        }
        result.setSyncPoints(newSyncPoints);
        for (int pos=0; pos<param.cutChunks.size(); pos++){
            ((ItChunk)param.cutChunks.elementAt(pos)).setStart(this.getSyncPoints().getSyncPointAt(0));
        }
        result.implyEnds();

        return result;
    }
    
    /** calls implyEnds for all lines */
    public void implyEnds(){
        for (int pos=0; pos<getNumberOfItLines(); pos++){
            ItLine itl = getItLineAt(pos);
            itl.implyEnds(getSyncPoints());
        }
    }
    
    /** removes all lines that do not contain any chunks
     * returns true if at least one such chunk has been removed */
    public boolean removeEmptyLines(){
        boolean result=false;
        for (int line=0; line<getNumberOfItLines(); line++){
            if (getItLineAt(line).getNumberOfItChunks()==0){
                removeElementAt(line);
                result=true;
                if (line<=frameEndPosition){frameEndPosition--;} 
                // i.e. if a line inside the frame has been removed: decrease the frame end position
                line--;      
            }
        }
        return result;
    }
    
    /** trims this bundle according to the given parameters */
    public java.util.Vector trim(BreakParameters param) {
        java.util.Vector result = new java.util.Vector();
        int bundles=0;
        do {
            ItBundle cut = cut(param);
            if (param.removeEmptyLines){
                cut.removeEmptyLines();
            }
            if (param.numberItBundles){
                bundles++;
                result.addElement(new Line("[" + Integer.toString(bundles) + "]", true));
                if (!param.saveSpace){
                    result.addElement(new Line(" ", true));
                }
            }
            if (param.smoothRightBoundary){
                cut.getSyncPoints().getSyncPointAt(cut.getSyncPoints().getNumberOfSyncPoints()-1)
                    .setOffset(param.getWidth()+param.rightMarginBuffer);
            }
            result.addElement(cut);
            if (!param.saveSpace || !param.numberItBundles){
                result.addElement(new Line(" "));
            }
        } while (getSyncPoints().getNumberOfSyncPoints()>0);        
        return result;
    }    
    
    public void glueAdjacentItChunks(boolean glueEmpty, double criticalSizePercentage){
        for (int l=0; l<getNumberOfItLines(); l++){
            ItLine itl = getItLineAt(l);
            for (int pos=0; pos<itl.getNumberOfItChunks(); pos++){
                ItChunk itc = itl.getItChunkAt(pos);
                boolean chunkSizeIsCritical = itc.getWidth()>=criticalSizePercentage*(itc.getEnd().getOffset() - itc.getStart().getOffset());
                if (chunkSizeIsCritical && itl.hasItChunkStartingAtSyncPoint(itc.getEnd().getID())){
                    ItChunk itc2 = itl.getItChunkStartingAtSyncPoint(itc.getEnd().getID());
                    itc.setEnd(itc2.getEnd());
                    for (int i=0; i<itc2.getNumberOfRuns(); i++){
                        itc.addRun(itc2.getRunAt(i));
                    }                    
                    itl.removeItChunkStartingAtSyncPoint(itc2.getStart().getID());
                    pos--;
                    continue;
                } else if (glueEmpty && !itl.hasItChunkStartingAtSyncPoint(itc.getEnd().getID()) && !syncPoints.isLastSyncPoint(itc.getEnd())){
                    itc.setEnd(syncPoints.getSyncPointAfter(itc.getEnd()));
                    pos--;
                    continue;
                }
            }
        }
    }
    /** writes an HTML representation of this object to the specified file output stream */
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }
    
    /** returns a HTML representation of this object */
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        // output anchors 
        if (param.outputAnchors){
            for (int pos=0; pos<anchors.size(); pos++){
                String anchor = (String)anchors.elementAt(pos);
                sb.append("<a name=\"" + anchor + "\"/>");
            }
        }
        sb.append("<table ");
        // added for version 1.3.3., 21-Nov-2006
        if (param.smoothRightBoundary){
            sb.append("width=\"" + Math.round(param.getWidth()*param.stretchFactor) + "px\" ");
        }
        sb.append("style=\"border-collapse:collapse; border-spacing:0px; empty-cells:show\">");
        if (param.includeSyncPoints){
            sb.append(getSyncPoints().toHTML(param));                
        }
        param.syncPoints = getSyncPoints();
        for (int pos=0; pos<this.getNumberOfItLines(); pos++){
            param.isOutside = ((frameEndPosition>=-1) && (pos>frameEndPosition));
            param.isFirstLine = (pos==0);
            param.isLastLine = (((frameEndPosition<-1) && (pos==getNumberOfItLines()-1)) ||
                            ((frameEndPosition>=0) && (pos==frameEndPosition)));
            sb.append(this.getItLineAt(pos).toHTML(param));
        }
        sb.append("</table>");
        return sb.toString();        
    }
    
    public String toRTFTable(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        if (param.includeSyncPoints){
            sb.append(getSyncPoints().toRTFTableRow(param));   
            param.isFirstParagraph = false;
        }
        param.syncPoints = getSyncPoints();
        for (int pos=0; pos<getNumberOfItLines(); pos++){
            param.isOutside = ((frameEndPosition>=-1) && (pos>frameEndPosition));
            param.isFirstLine = (pos==0 && param.putSyncPointsOutside && param.includeSyncPoints) || (pos==0 && !param.includeSyncPoints);
            param.isLastLine = (((frameEndPosition<-1) && (pos==getNumberOfItLines()-1)) ||
                            ((frameEndPosition>=0) && (pos==frameEndPosition)));
            sb.append(this.getItLineAt(pos).toRTFTableRow(param));
        }
        return sb.toString();        
    }
    
    public String toRTF(RTFParameters param) {
        return toRTFTable(param);
    }
    
    public void addUdInformation(String propertyName, String propertyValue) {
        if (!hasUdInformation()) {udInfo = new UdInformation();}
        udInfo.setProperty(propertyName, propertyValue);
    }
    
    public boolean keepTogetherWithNext() {
        return false;
    }
    
    public void writeSVG(String path, SVGParameters param) throws IOException {
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(path));
        fos.write(toSVG(param).getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");                
    }
    
    public String toSVG(SVGParameters param){
        param.syncPoints = getSyncPoints();

        StringBuffer sb = new StringBuffer();
        // Declarations
        sb.append(SVGUtilities.XML_DECLARATION);
        sb.append(SVGUtilities.SVG_DOCTYPE_DECLARATION);
        // root element (defines the coordinate system) 
        String height = Double.toString((getHeight(param.includeSyncPoints) + 20) * param.scaleFactor);
        String width = Double.toString((param.getWidth() + 20)*param.scaleFactor);
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + width + "px\" height=\"" + height + "px\">");
        sb.append("<g transform=\"scale(" + Double.toString(param.scaleFactor) + ")\">");                
        
        //drawGrid(sb, width, height);
        
        double y = 10;
        // output syncPoints
        if (param.includeSyncPoints){
            String syncPointsSVG = getSyncPoints().toSVG(param);
            sb.append(syncPointsSVG);
            y+=getSyncPoints().getHeight();                    
        }
        // output ITLines
        for (int pos=0; pos<this.getNumberOfItLines(); pos++){
            ItLine currentLine = getItLineAt(pos);
            y+=currentLine.getHeight();
            sb.append(XMLUtilities.makeXMLComment("ItLine " + pos));
            String svgLine = currentLine.toSVG(param, y);
            sb.append(svgLine);
        }
        
        // draw frame
        sb.append(XMLUtilities.makeXMLComment("Partitur-Border"));
        double cornerX = 8;
        double w = param.getWidth();
        double cornerY = 10;
        if (param.includeSyncPoints && param.putSyncPointsOutside) {
            cornerY+= getSyncPoints().getHeight();// + getSyncPoints().getDescent();
        }
        double h = 0;
        if (param.includeSyncPoints && !param.putSyncPointsOutside) {
            h+=getSyncPoints().getHeight();
        }
        int lastLineInFrame = frameEndPosition;
        if (frameEndPosition<0){
            lastLineInFrame = getNumberOfItLines()-1;
        }
        for (int line=0; line<lastLineInFrame+1; line++){
            h+=getItLineAt(line).getHeight();
        }
        h+=getItLineAt(lastLineInFrame).getDescent(); // + getItLineAt(lastLineInFrame).getHeight();;
        String frame = SVGUtilities.drawBorder(param.frame, param.frameStyle, param.frameColor, "#RFFGFFBFF",
                                               cornerX, cornerY, w, h);
        sb.append(frame);
        sb.append("</g></svg>");
        return sb.toString();
    }
    
    public void print(java.awt.Graphics2D graphics, PrintParameters param) {
        param.syncPoints = getSyncPoints();

        double memorizeCurrentY = param.currentY;        
        
        if (param.includeSyncPoints){
            getSyncPoints().print(graphics, param);
        }
        for (int line=0; line<getNumberOfItLines(); line++){
            ItLine currentLine = getItLineAt(line);
            currentLine.print(graphics, param);
        }
        
        // draw frame
        double x = param.getPaperMeasure("margin:left",OutputParameters.PX_UNIT);
        double w = param.getPixelWidth();
        double y;
        if (param.includeSyncPoints && param.putSyncPointsOutside) {y=memorizeCurrentY + getSyncPoints().getHeight() + getSyncPoints().getDescent();}
        else {y=memorizeCurrentY;}
        double h = 0;
        if (param.includeSyncPoints && !param.putSyncPointsOutside) {h+=getSyncPoints().getHeight();}
        int lastLineInFrame = frameEndPosition;
        if (frameEndPosition<0){lastLineInFrame = getNumberOfItLines()-1;}
        h+=getItLineAt(lastLineInFrame).getDescent();
        for (int line=0; line<lastLineInFrame+1; line++){
            h+=getItLineAt(line).getHeight();
        }        
        java.awt.geom.Rectangle2D.Double r = new java.awt.geom.Rectangle2D.Double(x,y,w-1,h);
        PrintUtilities.drawBorder(graphics, r, 
                                  param.frame, 
                                  param.frameStyle, 
                                  param.makeFrameColor(), 
                                  java.awt.Color.white);
        
    }
    
    
    public double getDescent() {
        return 0;
    }
    
    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<w:tbl>"); // table element
        sb.append("<w:tblPr>"); // table properties
	sb.append("<w:tblW w:w=\"0\" w:type=\"auto\"/>");
	sb.append("<w:tblLayout w:type=\"Fixed\"/>"); // table is of fixed width
        //<w:tblInd w:w="-65" w:type="dxa"/>
        sb.append("<w:tblCellMar>");
        sb.append("<w:left w:w=\"0\" w:type=\"dxa\"/>");
        sb.append("<w:right w:w=\"0\" w:type=\"dxa\"/>");
        sb.append("</w:tblCellMar>");
        // ...
        sb.append("</w:tblPr>");
        sb.append("<w:tblGrid>"); // column definitions
        sb.append(getSyncPoints().toColumnDefinition());
        sb.append("</w:tblGrid>");
        if (param.includeSyncPoints){
            sb.append(getSyncPoints().toWordML(param));             
        }
        param.syncPoints = getSyncPoints();
        for (int pos=0; pos<this.getNumberOfItLines(); pos++){
            param.isOutside = ((frameEndPosition>=-1) && (pos>frameEndPosition));
            param.isFirstLine = (pos==0);
            param.isLastLine = (((frameEndPosition<-1) && (pos==getNumberOfItLines()-1)) ||
                            ((frameEndPosition>=0) && (pos==frameEndPosition)));
            sb.append(this.getItLineAt(pos).toWordML(param));
        }
        sb.append("</w:tbl>");
        return sb.toString();        
    }
    
    void drawGrid(StringBuffer sb, String width, String height){
        double h = Double.parseDouble(height);
        double w = Double.parseDouble(width);
        for (int x=0; x<w; x+=5){
           String[][] attributes2 = {{"x1", Double.toString(x)},
                                     {"y1", Double.toString(0)},
                                     {"x2", Double.toString(x)},
                                     {"y2", Double.toString(h)},
                                     {"stroke", "#999999"}};
           sb.append(XMLUtilities.makeXMLOpenElement("line", attributes2));
           sb.append(XMLUtilities.makeXMLCloseElement("line"));
        }
        for (int y=0; y<h; y+=5){
           String[][] attributes2 = {{"x1", Double.toString(0)},
                                     {"y1", Double.toString(y)},
                                     {"x2", Double.toString(w)},
                                     {"y2", Double.toString(y)},
                                     {"stroke", "#999999"}};
           sb.append(XMLUtilities.makeXMLOpenElement("line", attributes2));
           sb.append(XMLUtilities.makeXMLCloseElement("line"));
        }
    }
    
    // added 10-10-2011: reorder according to speaker order for compact output in FOLK(ER)
    public void reorder(){
        ItLineComparator comparator = new ItLineComparator(getSyncPoints());
        Collections.sort(this, comparator);        
    }
    
    
}
