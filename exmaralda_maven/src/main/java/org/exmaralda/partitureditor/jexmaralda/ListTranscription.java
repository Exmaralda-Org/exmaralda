/*
 * ListTranscription.java
 *
 * Created on 27. August 2002, 15:08
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.io.*;
/**
 *
 * @author  Thomas
 */
public class ListTranscription extends AbstractTranscription {
    
    private ListBody body;
    private static String INTERNAL_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/ListBody2HTML.xsl";
    private static String SIMPLE_TEXT_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/ListBody2TXT.xsl";
    
    /** Creates a new instance of ListTranscription */
    public ListTranscription() {
        body = new ListBody();
    }
    
    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    /** returns the body of the transcription */
    public ListBody getBody(){
        return body;
    }
    
    /** sets the body of the transcription to the specified value */
    public void setBody(ListBody b){
        body = b;
    }
    
    /** returns the transcription as an XML element &lt;basic-transcription&gt; as
     *  specified in the corresponding dtd */
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<list-transcription>\n");
        sb.append(super.toXML());
        sb.append(getBody().toXML());
        sb.append("</list-transcription>\n");
        return sb.toString();
    }
    
    
    /** writes an XML file with the specified file name and the specified path to
     *  the dtd */
    public void writeXMLToFile(String filename, String pathToDTD) throws IOException {
        // changed 11-05-2010: new method can also produce relative paths that
        // go via parent folders, i.e. including one or more ..s
        getHead().getMetaInformation().relativizeReferencedFile(filename, MetaInformation.NEW_METHOD);
        // change rolled back, can't find a decent method to resolve such a path
        // 13-08-2010: change reintroduced
        //getHead().getMetaInformation().relativizeReferencedFile(filename);
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        fos.write(StringConstants.XML_HEADER.getBytes("UTF-8"));        
        fos.write(StringUtilities.makeXMLDoctypeListTranscription(pathToDTD).getBytes("UTF-8"));
        fos.write(StringConstants.XML_COPYRIGHT_COMMENT.getBytes("UTF-8"));
        fos.write(toXML().getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
    }
    
    public String toHTML(TierFormatTable tft) {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
        sb.append(tft.toCSS());
        sb.append("</head>");
        sb.append("<body>");
        sb.append(getBody().toHTML(getHead().getSpeakertable()));
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
    
    public void writeHTMLToFile(String filename, TierFormatTable tft) throws IOException {
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        fos.write(toHTML(tft).getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
    }
    
    public boolean writeHTMLToFile(String filename, String pathToStylesheet) throws IOException {
        org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory ssf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory();
        boolean externalStylesheetCouldBeApplied = true;
        String output = new String();
        try{
            output = ssf.applyExternalStylesheetToString(pathToStylesheet, this.toXML());
        } catch (Exception e){
            externalStylesheetCouldBeApplied = false;
            try {
                output = ssf.applyInternalStylesheetToString(INTERNAL_STYLESHEET, this.toXML());
            } catch (Exception e2){
                throw new IOException(e2.getLocalizedMessage());
            }
        }
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        fos.write(output.getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
        return externalStylesheetCouldBeApplied;
    }
    
    public void writeSimpleTextToFile(String filename) throws IOException{
        org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory ssf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory();
        String output = new String();
        try {
            output = ssf.applyInternalStylesheetToString(SIMPLE_TEXT_STYLESHEET, this.toXML());
        } catch (Exception e2){
            throw new IOException(e2.getLocalizedMessage());
        }
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        fos.write(output.getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
        
    }
    
}
