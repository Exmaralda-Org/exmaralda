/*
 * InterlinearText.java
 *
 * Created on 26. Februar 2002, 16:16
 */

package org.exmaralda.partitureditor.interlinearText;

import java.io.*;
import java.awt.*;
import java.awt.print.*;
import java.util.Vector;
import org.xml.sax.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class InterlinearText extends java.util.Vector implements XMLElement, UdInformable, HTMLable, RTFable, Printable{

    private UdInformation udInfo;    
    private Formats formats;
    private java.util.Vector pageBreaks;
    private PrintParameters printParameters = new PrintParameters();

    /** Creates new InterlinearText */
    public InterlinearText() {
        pageBreaks = new java.util.Vector();        
        formats = new Formats();
    }
   
    public InterlinearText(String pathToFile) throws SAXException{
        this();
        org.exmaralda.partitureditor.interlinearText.sax.InterlinearTextSaxReader reader = new org.exmaralda.partitureditor.interlinearText.sax.InterlinearTextSaxReader();
        InterlinearText t = reader.readFromFile(pathToFile);
        this.setFormats(t.getFormats());
        this.setUdInformation(t.getUdInformation());
        this.setPageBreaks(t.getPageBreaks());
        for (int pos=0; pos<t.getNumberOfItElements(); pos++){
            ItElement ite = t.getItElementAt(pos);
            this.addItElement(ite);
        }
    }
    
    public PrintParameters getPrintParameters(){
        return printParameters;
    }

    // added 19-05-2009: insert the specified symbols when there's more than one element at a given syncPoint
    public void markOverlaps(String startChar, String endChar) {
        for (int pos=0; pos<getNumberOfItElements(); pos++){
            ItElement ite = getItElementAt(pos);
            if (ite.isItBundle()){
                ItBundle itb = (ItBundle)ite;
                SyncPoints syncPoints = itb.getSyncPoints();
                for (int pos2=0; pos2<syncPoints.getNumberOfSyncPoints(); pos2++){
                    Vector<ItChunk> startChunks = new Vector<ItChunk>();
                    Vector<ItChunk> endChunks = new Vector<ItChunk>();
                    for (int pos3=0; pos3<itb.getNumberOfItLines(); pos3++){
                        ItLine itl = itb.getItLineAt(pos3);
                        if (itl.hasItChunkStartingAtSyncPoint(syncPoints.getSyncPointAt(pos2).getID())){
                            ItChunk itc = itl.getItChunkStartingAtSyncPoint(syncPoints.getSyncPointAt(pos2).getID());
                            startChunks.add(itc);
                        }
                        if (itl.hasItChunkEndingAtSyncPoint(syncPoints.getSyncPointAt(pos2).getID())){
                            ItChunk itc = itl.getItChunkEndingAtSyncPoint(syncPoints.getSyncPointAt(pos2).getID());
                            endChunks.add(itc);
                        }
                    }
                    if (startChunks.size()>1){
                        for (ItChunk itc : startChunks){
                            itc.getRunAt(0).setText(startChar + itc.getRunAt(0).getText());
                        }
                    }
                    if (endChunks.size()>1){
                        for (ItChunk itc : endChunks){
                            itc.getRunAt(itc.getNumberOfRuns()-1).setText(itc.getRunAt(itc.getNumberOfRuns()-1).getText() + endChar);
                        }
                    }
                }
            }
        }
        
    }
    
    public void setPrintParameters(PrintParameters pp){
        printParameters = pp;
    }
    
    public int getNumberOfItElements() {
        return size();
    }
    
    public ItElement getItElementAt(int pos) {
        return (ItElement)elementAt(pos);
    }
    
    public void addItElement(ItElement ite) {
        addElement(ite);
        if (!((Formattable)ite).hasFormat()){
            ((Formattable)ite).setFormat(getFormats().getFormatWithID("STANDARD"));
            ((Formattable)ite).propagateFormat();
        }
    }
    
    public void insertItElementAt(ItElement ite, int position) {
        insertElementAt(ite, position);
        if (!((Formattable)ite).hasFormat()){
            ((Formattable)ite).setFormat(getFormats().getFormatWithID("STANDARD"));
            ((Formattable)ite).propagateFormat();
        }
    }

    public Formats getFormats() {
        return formats;
    }
    
    public void setFormats(Formats f) {
        formats=f;
    }
    
    public java.util.Vector getPageBreaks(){
        return pageBreaks;
    }
    
    public void setPageBreaks(java.util.Vector pb){
        pageBreaks = pb;
    }
    
    public int[] getPageBreaksAsArray(){
        int[] result=new int[pageBreaks.size()];
        for (int pos=0; pos<pageBreaks.size(); pos++){
            result[pos]=((Integer)pageBreaks.elementAt(pos)).intValue();
        }
        return result;        
    }

    public void addPageBreak(int pos){
        pageBreaks.addElement(new Integer(pos));
    }

    boolean isElementFollowedByPageBreak(int pos){
        return pageBreaks.contains(new Integer(pos));
    }
    
    public boolean hasUdInformation() {
        return (udInfo!=null);
    }
    
    public void setUdInformation(UdInformation udi) {
        udInfo = udi;
    }
    
    /** writes a string representing this object in XML to the specified output stream */
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException {
        fo.write(toXML().getBytes("UTF-8"));
    }
    
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<interlinear-text>");
        buffer.append(getFormats().toXML());
        for (int pos=0; pos<getNumberOfItElements(); pos++){
            if (getItElementAt(pos).isItBundle()){
                buffer.append(((ItBundle)elementAt(pos)).toXML());
            } else {
                buffer.append(((Line)elementAt(pos)).toXML());
            }
            if (this.isElementFollowedByPageBreak(pos)){
                buffer.append("<page-break/>");
            }
        }
        buffer.append("</interlinear-text>");        
        return buffer.toString();
    }
    
    public void writeSVGToFile( SVGParameters param, String filename, 
                                String subfoldername, String svgStem) throws java.io.IOException{
        FileOutputStream fos = new FileOutputStream(new File(filename));
        String head = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head><body><table>";
        fos.write(head.getBytes("UTF-8"));
        int count = 0;
        for (int pos=0; pos<getNumberOfItElements();pos++){
            ItElement e = getItElementAt(pos);
            if (e instanceof ItBundle){
                count++;
                fos.write(("<tr><td valign=\"top\"><b>[" + Integer.toString(count) + "]</b></td>").getBytes("UTF-8"));
                String svgFilename = subfoldername + System.getProperty("file.separator") 
                                    + svgStem + Integer.toString(count) + ".svg";
                String svgPath = new File(filename).getParent() + System.getProperty("file.separator") + svgFilename;
                if (!new File(svgPath).getParentFile().exists()){
                    new File(svgPath).getParentFile().mkdir();
                }
                ItBundle itb = ((ItBundle)e);
                itb.writeSVG(svgPath, param);
                String embed = "<td><embed src=\"" + svgFilename + "\" type=\"image/svg+xml\"";
                double width = (param.getWidth()*param.scaleFactor + 20) *param.scaleFactor;
                embed += " width=\"" + Double.toString(width)  + "\""; 
                double height = (itb.getHeight(param.includeSyncPoints)+20)*param.scaleFactor;
                embed += " height=\"" + Double.toString(height) + "\"/></td></tr>";
                fos.write(embed.getBytes("UTF-8"));
            }
        }
        String foot = "</table></body></html>";        
        fos.write(foot.getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");                
    }
    
    public void writeXMLToFile(String filename) throws java.io.IOException{
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes("UTF-8"));
        writeXML(fos);
        fos.close();
        System.out.println("document written.");        
    }
    
    public UdInformation getUdInformation() {
        return udInfo;
    }
    
    public void trim(BreakParameters param){
        for (int pos=0; pos<getNumberOfItElements(); pos++){
            java.util.Vector trimmed = getItElementAt(pos).trim(param);
            int pos2;
            for (pos2=pos; pos2<pos+trimmed.size(); pos2++){
                this.insertItElementAt((ItElement)trimmed.elementAt(pos2-pos),pos2);
            }
            this.removeElementAt(pos2);
            pos = pos2;            
        }
    }
    
    public void glueAdjacentItChunks(boolean glueEmpty, double criticalSizePercentage){
        for (int pos=0; pos<getNumberOfItElements(); pos++){
            if (getItElementAt(pos).isItBundle()){
                ((ItBundle)getItElementAt(pos)).glueAdjacentItChunks(glueEmpty, criticalSizePercentage);
            }
        }
    }
    
    
    public void calculateOffsets(){
        for (int pos=0; pos<getNumberOfItElements(); pos++){
            if (getItElementAt(pos).isItBundle()){
                ((ItBundle)getItElementAt(pos)).calculateOffsets(true, Integer.MAX_VALUE, 0);
            }
        }
    }
   
    
    public void writeTestRTF(String filename, RTFParameters param) throws IOException{
            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(new File(filename));
            String rtfString = toRTF(param);
            fos.write(rtfString.getBytes());
            fos.close();
            System.out.println("document written.");            
    }
    
    public void copyRTFToClipboard(RTFParameters param){
        String rtfString = toRTF(param);
        RTFSelection ss = new RTFSelection(rtfString);
        new javax.swing.JFrame().getToolkit().getSystemClipboard().setContents(ss,null);        
    }

    public void writeHTMLToFile(String filename, HTMLParameters param) throws java.io.IOException {
        relativizeLinks(filename);
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        writeHTML(fos, param);
        fos.close();
        System.out.println("document written.");
    }
    
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException {
        fos.write(toHTML(param).getBytes("UTF-8"));
    }
    
    public String toHTML(HTMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
        sb.append("<base target=\"" + param.linkTarget + "\"/>");
        if (param.useJavaScript){
            sb.append(HTMLUtilities.makeJavaScriptFunctions(param));
        }
        sb.append(getFormats().toHTML(param));
        sb.append("</head>");
        sb.append("<body>");
        sb.append(param.additionalStuff);
        for (int pos=0; pos < getNumberOfItElements(); pos++){
            sb.append(((HTMLable)elementAt(pos)).toHTML(param));;
        }
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
    
    public String toRTF(RTFParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append(RTFUtilities.RTF_HEAD);
        sb.append(getFormats().toRTFFontTable(param));
        sb.append(getFormats().toRTFColorTable(param));
        sb.append(param.toRTFPaperMeasureString());
        sb.append(RTFUtilities.RTF_OTHER);
        if (param.additionalStuff.length()>0){
            sb.append(param.additionalStuff);
        }
        for (int pos=0; pos<this.getNumberOfItElements(); pos++){
            param.isFirstParagraph = (((param.additionalStuff.length()>0) && pos==0) || pageBreaks.contains(new Integer(pos)));
            sb.append(((RTFable)getItElementAt(pos)).toRTF(param));
        }
        sb.append("}");
        return sb.toString();        
    }
    
    
    public void addUdInformation(String propertyName, String propertyValue) {
        if (!hasUdInformation()) {udInfo = new UdInformation();}
        udInfo.setProperty(propertyName, propertyValue);
    }
    
    /** calculates the page breaks on the basis of the paper height specified in the page output parameters
     * returns the number of pages */
    public int[] calculatePageBreaks(PageOutputParameters pageParameters){
        pageBreaks = new java.util.Vector();
        double currentHeight = 0;
        double availableHeight = pageParameters.getPixelHeight() - pageParameters.pageBreakTolerance;
        int lastKeepTogether = -1;
        for (int currentElementPosition=0; currentElementPosition<getNumberOfItElements(); currentElementPosition++){
            ItElement currentItElement = getItElementAt(currentElementPosition);
            double height = ((Formattable)currentItElement).getHeight();
            if (currentItElement.isItBundle() && !pageParameters.includeSyncPoints){
                height = ((ItBundle)currentItElement).getHeight(false);
            }
            if (height>availableHeight) {return null;}
            if ((currentHeight+height>availableHeight)){ //i.e. this element doesn't fit any more 
                currentHeight=0;
                if (lastKeepTogether>=0){
                    currentElementPosition=lastKeepTogether-1;                    
                }
                pageBreaks.addElement(new Integer(currentElementPosition));
            } else {    // i.e. this element fits
                if (!currentItElement.keepTogetherWithNext()){
                    lastKeepTogether=-1;
                } else if (pageParameters.saveSpace && !currentItElement.isItBundle()){
                     lastKeepTogether=currentElementPosition-1;               
                }
                else {
                    lastKeepTogether=currentElementPosition;
                }
            }
            currentHeight+=height;
        }

        int[] result = new int[pageBreaks.size()];
        for (int pos=0; pos<pageBreaks.size(); pos++){
            result[pos]=((Integer)(pageBreaks.elementAt(pos))).intValue();
        }
        return result;
    }
    
    public int print(java.awt.Graphics graphics, java.awt.print.PageFormat pageFormat, int pageIndex) throws java.awt.print.PrinterException {
        if (pageIndex>pageBreaks.size()){
            return Printable.NO_SUCH_PAGE;
        }
        printParameters.setPaperMeasures(pageFormat);
        printParameters.resetCurrentX();
        printParameters.resetCurrentY();
        return print(graphics, printParameters, pageIndex);
    }
    
    private int print(java.awt.Graphics graphics, PrintParameters param,  int pageIndex) throws java.awt.print.PrinterException {
        Graphics2D g = (Graphics2D)graphics;
        System.out.println("Printing page #" + pageIndex);
        int firstItElementToBePrinted = 0;
        if (pageIndex>0){
            firstItElementToBePrinted = ((Integer)pageBreaks.elementAt(pageIndex-1)).intValue();
        }
        int lastItElementToBePrinted = getNumberOfItElements();        
        if (pageIndex<pageBreaks.size()){
            lastItElementToBePrinted = ((Integer)pageBreaks.elementAt(pageIndex)).intValue();
        }
        for (int pos=firstItElementToBePrinted; pos<lastItElementToBePrinted; pos++){
            ItElement currentElement = getItElementAt(pos);
            currentElement.print(g, param);
        }
        return Printable.PAGE_EXISTS;
    }
    
    String checkHTML(String checkString){
        String result = new String();
        for (int pos=0; pos<checkString.length(); pos++){
            char symbol = checkString.charAt(pos);
            if (symbol==' '){result+="&nbsp;";}
            else if (symbol=='<'){result+="&lt;";}
            else if (symbol=='>'){result+="&gt;";}
            else if (symbol=='&'){result+="&amp;";}
            else if (symbol=='"'){result+="&quot;";}
            else {result+=symbol;}
        }
        return result;
    }
    
    
    public String toWordML(WordMLParameters param) {
        StringBuffer sb = new StringBuffer();
        sb.append(WordMLUtilities.WORD_DOCUMENT_ELEMENT);
        sb.append(getFormats().toWordML(param));
        sb.append("<w:body>");  // body element
        sb.append("<wx:sect>"); // section element
        for (int pos=0; pos < getNumberOfItElements(); pos++){
            sb.append(((WordMLable)elementAt(pos)).toWordML(param));;
        }
        sb.append(param.toWordMLPaperMeasureString());
        sb.append("</wx:sect>");
        sb.append("</w:body>"); 
        sb.append("</w:wordDocument>");
        return sb.toString();
    }
    
    public void relativizeLinks(String relativeToWhat){
        for (int pos=0; pos<getNumberOfItElements(); pos++){
            if (getItElementAt(pos).isItBundle()){
                ItBundle itb = ((ItBundle)getItElementAt(pos));
                for (int pos2=0; pos2<itb.getNumberOfItLines(); pos2++){
                    ItLine itl = itb.getItLineAt(pos2);
                    for (int pos3=0; pos3<itl.getNumberOfItChunks(); pos3++){
                        ItChunk itc = itl.getItChunkAt(pos3);
                        if (itc.hasLinks()){
                            System.out.println(itc.toXML());
                            itc.getFirstLink().relativizeLink(relativeToWhat);
                        }
                    }
                }
            }
        }
        
    }

    public void reorder() {
        for (int pos=0; pos<getNumberOfItElements(); pos++){
            if (getItElementAt(pos).isItBundle()){
                ItBundle itb = ((ItBundle)getItElementAt(pos));
                itb.reorder();
            }
        }
        
    }
    
    
    
    
}

