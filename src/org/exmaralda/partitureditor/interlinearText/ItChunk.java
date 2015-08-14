/*
 * itChunk.java
 *
 * Created on 26. Februar 2002, 11:53
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ItChunk extends AbstractRunVector implements XMLElement, HTMLable {

    private SyncPoint start;
    private SyncPoint end;
    private java.util.Vector links;
    
    /** Creates new itChunk */
    public ItChunk() {
        links = new java.util.Vector();
    }
    
    /** returns the start point of this chunk */
    public SyncPoint getStart(){
        return start;
    }
    
    /** sets the start point of this chunk to the specified value */
    public void setStart(SyncPoint sp){
        start = sp;
    }
    
    /** returns the end point of this chunk */
    public SyncPoint getEnd(){
        return end;
    }
    
    /** sets the end point of this chunk to the specified value */
    public void setEnd(SyncPoint sp){
        end = sp;
    }
    
   
    /** returns a string representing this object in XML  */
    public String toXML(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("<it-chunk start-sync=\"");
        buffer.append(getStart().getID());
        if (getEnd()!=null){
            buffer.append("\" end-sync=\"");
            buffer.append(getEnd().getID());
        }
        if (hasFormat()){
            buffer.append("\" formatref=\"");
            buffer.append(getFormat().getID());
        }
        buffer.append("\">");
        buffer.append(super.toXML());
        buffer.append("</it-chunk>");
        return buffer.toString();        
    }
    
    /** cuts a piece from this chunk that fits into the space
     * determined by the given parameters (criticalSync and pixelsThatFit)
     * and returns that piece */    
    public ItChunk cut(short breakType, BreakParameters param, SyncPoints syncPoints,int criticalSync, double pixelsThatFit,double pixelsThatDontFit){
        ItChunk result = new ItChunk();
        result.setFormat(this.getFormat());
        result.setStart(this.getStart());
        if (this.hasLinks()){
            Link fl = this.getFirstLink();
            result.addLink(new Link(fl.getURL(), fl.getType()));
        }
        //result.setLinks((java.util.Vector)(getLinks().clone()));
        double pixelsBeforeCritical = syncPoints.getSyncPointAt(criticalSync).getOffset() - this.getStart().getOffset();
        // the pixels that are available between the start of this chunk and the critical syncPoint
        // these can be fully used up
        // if this chunk starts at the critical syncPoint, then this value will be 0
        double availablePixels = pixelsBeforeCritical + pixelsThatFit;
        // the pixels available altogether are the sum of the pixels before the
        // critical sync point and the pixels that remain after the critical syncPoint
        double pixels=0;
        for (int pos=0; pos<getNumberOfRuns(); pos++){
            Run run = getRunAt(0);
            if ((pixels + run.getWidth())<=availablePixels){
                // i.e. if this run fully fits into the available space
                // then add it to the cut chunk and remove it from this one
                pixels+=run.getWidth();
                result.addRun(run);
                removeElementAt(pos);                
                // subtract the width of this run from the available pixels
                availablePixels-=run.getWidth();
            } else {
                // i.e. if this run does not fully fit into the available space
                // cut the run, add the cut piece to the new chunk
                // leave the rest in this chunk and quit
                // leaving all possibly remaining runs in this chunk
                Run newRun = run.cut(breakType, param, availablePixels, pixelsThatDontFit);
                result.addRun(newRun);
            }
        }
        //set the start of this chunk to the critical sync point
        setStart(syncPoints.getSyncPointAt(criticalSync));
        return result;
    }

    /** writes an HTML representation of this object to the specified file output stream */
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }
    
    /** returns a HTML representation of this object */
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        if (hasLinks()){
            sb.append(getFirstLink().toHTML(param)); 
        }
        sb.append("<td height=\"" + Long.toString(param.rowHeight) + "px\" nowrap=\"nowrap\" ");
        sb.append("class=\"" + getFormat().getID() + "\" ");
        sb.append("colspan=\"" + param.syncPoints.span(this) + "\" ");
        //sb.append(" style=\"");
        //sb.append(" height:" + Long.toString(param.rowHeight) + "px;");
        //sb.append(" overflow:hidden;");        
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
        //sb.append("\"");
        // added for version 1.3.3., 21-Nov-2006
        if (param.isLastChunk && param.smoothRightBoundary){
            sb.append(" width=\"100%\"");
        }
        sb.append(">");
        sb.append(super.toHTML(param));
        sb.append("</td>");  
        if (hasLinks()){
            sb.append("</a>"); 
        }        
        return sb.toString();                
    }
    
    public String toRTFTableCellDefinition(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("\\clvertalt");   // cell align top        
        sb.append(RTFUtilities.toRTFBorderDefinition(getFormat(), param));  // border definition
        sb.append("\\clcbpat" + Integer.toString(param.getColorMapping(getFormat().getProperty("bg-color")))); // background color of the background pattern (sic!)
        sb.append("\\cltxlrtb");    // text flow left to right and top to bottom
        sb.append("\\clftsWidth3"); // cell width is in twips
        sb.append("\\clwWidth" + Long.toString(Math.round((getEnd().getOffset() - getStart().getOffset())*20)));    // preferred Cell Width (in twips!)
        param.chunkSizeIsCritical = this.getWidth()>=param.criticalSizePercentage*(getEnd().getOffset() - getStart().getOffset());
        if (param.chunkSizeIsCritical && param.useClFitText){
            sb.append("\\clFitText");   // fit text into cell (added for version 1.2.4. because of WORD 2002 (XP))
        }
        sb.append("\\cellx" + Long.toString(Math.round((getEnd().getOffset())*20))); // position of right edge (in twips!)
        return sb.toString();
    }
    
    public String toRTFTableCellContent(RTFParameters param){
        StringBuffer sb = new StringBuffer();
        param.chunkSizeIsCritical = this.getWidth()>=param.criticalSizePercentage*(getEnd().getOffset() - getStart().getOffset());
        sb.append(super.toRTF(param));
        sb.append("\\cell");
        return sb.toString();
    }    
    
    
    public void print(java.awt.Graphics2D graphics, PrintParameters param) {
        param.resetCurrentX();
        param.currentX+=this.getStart().getOffset();
        super.print(graphics, param);
    }
    

    /** returns a HTML representation of this object */
    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<w:tc>"); // table cell
        sb.append("<w:tcPr>"); // table cell properties
        if (param.syncPoints.span(this) != 1){
            sb.append("<w:gridSpan w:val=\"" + param.syncPoints.span(this) + "\"/>");
        }
        param.chunkSizeIsCritical = this.getWidth()>=param.criticalSizePercentage*(getEnd().getOffset() - getStart().getOffset());
        if (param.chunkSizeIsCritical && param.useClFitText){
            sb.append("<w:tcFitText/>");   // fit text into cell (added for version 1.2.4. because of WORD 2002 (XP))
        }
        sb.append(WordMLUtilities.toWordMLBorderDefinition(getFormat(), param));  // border definition        
        sb.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\""); // backgorund colour...
        sb.append(WordMLUtilities.toColorString(getFormat().getProperty("bg-color")) +"\"/>");   // ...backgorund colour

        /*sb.append(  "<w:tcW w:w=\"" 
                    + Long.toString(Math.round((getEnd().getOffset() - getStart().getOffset())*20))
                    + "\" w:type=\"dxa\"/>");    // preferred Cell Width (in twips!)*/ // not necessary...

        sb.append("</w:tcPr>"); 
        sb.append(super.toWordML(param));        
        sb.append("</w:tc>");  
        return sb.toString();                
    }
    
}
