package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
import java.io.*;
import org.xml.sax.*;


/*
 * TierFormatTable.java
 *
 * Created on 23. April 2001, 10:47
 */



/**
 *
 * @author  Thomas
 * @version 
 */
public class TierFormatTable extends Hashtable {

    private Vector referencedFiles;
    private TimelineItemFormat timelineItemFormat;
    
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new TierFormatTable */
    public TierFormatTable() {
        super();
        referencedFiles = new Vector();
        timelineItemFormat = new TimelineItemFormat();
    }
    
    /** Creates new Tier format table from the specified file */
    public TierFormatTable (String inputFileName) throws SAXException{
        super();
        referencedFiles = new Vector();
        timelineItemFormat = new TimelineItemFormat();
        org.exmaralda.partitureditor.jexmaralda.sax.TierFormatTableSaxReader reader = new org.exmaralda.partitureditor.jexmaralda.sax.TierFormatTableSaxReader();
        TierFormatTable t = new TierFormatTable();
        t = reader.readFromFile(inputFileName);
        for (Enumeration e = t.keys(); e.hasMoreElements(); ){
            String id = (String)e.nextElement();
            try {addTierFormat(t.getTierFormatForTier(id));}
            catch (JexmaraldaException je) {
                System.out.println("SKANDAL!!!");
                je.printStackTrace();
            }
        }
        String[] files = t.getAllReferencedFiles();
        for (int pos=0; pos<files.length; pos++){
            addReferencedFile(files[pos]);
        }
        setTimelineItemFormat(t.getTimelineItemFormat());
        replaceOldAttributeNames();
    }
    
    public void TierFormatTableFromString (String inputString) throws SAXException {
        org.exmaralda.partitureditor.jexmaralda.sax.TierFormatTableSaxReader reader = new org.exmaralda.partitureditor.jexmaralda.sax.TierFormatTableSaxReader();
        TierFormatTable t = new TierFormatTable();
        t = reader.readFromString(inputString);
        this.clear();
        for (Enumeration e = t.keys(); e.hasMoreElements(); ){
            String id = (String)e.nextElement();
            try {addTierFormat(t.getTierFormatForTier(id));}
            catch (JexmaraldaException je) {}
        }
        String[] files = t.getAllReferencedFiles();
        for (int pos=0; pos<files.length; pos++){
            addReferencedFile(files[pos]);
        }
        setTimelineItemFormat(t.getTimelineItemFormat());
        replaceOldAttributeNames();
    }    

    public TierFormatTable(BasicTranscription transcription){
        super();
        referencedFiles = new Vector();
        timelineItemFormat = new TimelineItemFormat();
        BasicBody body = (BasicBody)transcription.getBody();
        for (int pos=0; pos<body.getNumberOfTiers(); pos++){
            Tier t = body.getTierAt(pos);
            TierFormat tf = new TierFormat(t.getType(), t.getID());
            try{addTierFormat(tf);} catch (JexmaraldaException je){}
        }
        StringUtilities.addStandardTierFormats(this);        
    }

    public static TierFormatTable makeTierFormatTableForFolker(BasicTranscription transcription){
        TierFormatTable result = new TierFormatTable(transcription);
        try {
            for (String tierID : result.getAllTierIDs()){
                    result.getTierFormatForTier(tierID).setFontName("Courier New");
                    result.getTierFormatForTier(tierID).setSize(10);
                    if ((transcription.getBody().containsTierWithID(tierID)) && (transcription.getBody().getTierWithID(tierID).getSpeaker()==null)){
                        result.getTierFormatForTier(tierID).setBgcolorName("lightGray");
                    }
            }
            result.getTierFormatForTier("ROW-LABEL").setBgcolorName("white");

            result.getTierFormatForTier("COLUMN-LABEL").setBgcolorName("white");
            result.getTierFormatForTier("COLUMN-LABEL").setSize(8);
            result.getTierFormatForTier("COLUMN-LABEL").setTextcolorName("gray");
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
        // added 26-02-2010
        result.timelineItemFormat.setMilisecondsDigits((short)0);
        result.timelineItemFormat.setNthAbsolute((short)1);
        result.timelineItemFormat.setNthNumbering((short)0);

        return result;
    }

    public static TierFormatTable makeTierFormatTableForDGD2(BasicTranscription transcription){
        TierFormatTable result = new TierFormatTable(transcription);
        try {
            for (String tierID : result.getAllTierIDs()){
                    result.getTierFormatForTier(tierID).setFontName("Arial");
                    result.getTierFormatForTier(tierID).setSize(8);
                    if ((transcription.getBody().containsTierWithID(tierID)) && (transcription.getBody().getTierWithID(tierID).getSpeaker()==null)){
                        result.getTierFormatForTier(tierID).setBgcolorName("lightGray");
                    }
            }
            result.getTierFormatForTier("ROW-LABEL").setBgcolorName("white");

            result.getTierFormatForTier("COLUMN-LABEL").setBgcolorName("white");
            result.getTierFormatForTier("COLUMN-LABEL").setSize(8);
            result.getTierFormatForTier("COLUMN-LABEL").setTextcolorName("gray");
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
        // added 26-02-2010
        result.timelineItemFormat.setMilisecondsDigits((short)0);
        result.timelineItemFormat.setNthAbsolute((short)1);
        result.timelineItemFormat.setNthNumbering((short)0);

        return result;
    }

    public TierFormatTable(BasicTranscription transcription, String defaultFont){
        super();
        referencedFiles = new Vector();
        timelineItemFormat = new TimelineItemFormat();
        BasicBody body = (BasicBody)transcription.getBody();
        for (int pos=0; pos<body.getNumberOfTiers(); pos++){
            Tier t = body.getTierAt(pos);
            TierFormat tf = new TierFormat(t.getType(), t.getID(), defaultFont);
            try{addTierFormat(tf);} catch (JexmaraldaException je){}
        }
        StringUtilities.addStandardTierFormats(this);        
    }
    

    public TierFormatTable makeCopy(){
        TierFormatTable result = new TierFormatTable();
        String[] files = this.getAllReferencedFiles();
        for (int pos=0; pos<files.length; pos++){
            result.addReferencedFile(new String(files[pos]));
        }
        for (Enumeration e = keys(); e.hasMoreElements(); ){
            String id = (String)e.nextElement();
            try {
                TierFormat tf = this.getTierFormatForTier(id);
                result.addTierFormat(tf.makeCopy());
            }
            catch (JexmaraldaException je) {}
        }
        return result;
    }
    // ********************************************
    // ********** BASIC MANIPULATION **************
    // ********************************************

    public int getNumberOfTierFormats(){
        return size();
    }
    
    public String[] getAllTierIDs(){
        Vector resultVector = new Vector();
        for (Enumeration e = this.keys(); e.hasMoreElements(); ){
           String tierID = new String((String)e.nextElement());
           resultVector.addElement(tierID);
        }        
        return StringUtilities.stringVectorToArray(resultVector);
        
    }
    
    public void setTierFormat (TierFormat tf){
        put(tf.getTierref(),tf);
    }

    public void addTierFormat (TierFormat tf) throws JexmaraldaException {
        if (containsKey(tf.getTierref())){
            throw new JexmaraldaException(30, new String ("Table already contains tier format for tier " + tf.getTierref()));}
        put(tf.getTierref(),tf);
    }
    
    public boolean containsTierFormatForTier(String tierref) {
        if (containsKey(tierref)){
            return true;
        }
        return false;    
    }

    public TierFormat getTierFormatForTier(String tierref) throws JexmaraldaException {
        if (!containsKey(tierref)){
            throw new JexmaraldaException(31, new String ("No such tier id - " + tierref));}
        return (TierFormat)get(tierref);    
    }

    public void addReferencedFile(String url){
        referencedFiles.addElement(url);
    }
    
    public String[] getAllReferencedFiles(){
        return StringUtilities.stringVectorToArray(referencedFiles);
    }
    
    public TimelineItemFormat getTimelineItemFormat(){
        return timelineItemFormat;
    }

    public void setTimelineItemFormat(TimelineItemFormat tlif){
        timelineItemFormat = tlif;
    }
    
    
    /** makes all formats left-aligned (required for trim until further notice) */
    void replaceAlignment(){
        for (Enumeration e = keys(); e.hasMoreElements(); ){
            String id = (String)e.nextElement();
            try {
                TierFormat tf = this.getTierFormatForTier(id);
                tf.setAlignmentName("Left");
            }
            catch (JexmaraldaException je) {}
        }
    }
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************    
    
    String toXML(boolean includeFileReferences){
        StringBuffer result = new StringBuffer();
        result.append("<tierformat-table>");
        String[] files = getAllReferencedFiles();
        if (includeFileReferences) {
            for (int pos=0; pos<files.length; pos++){
                result.append("<referenced-file url=\"" + files[pos] + "\"/>\n");
            }
        }
        result.append(timelineItemFormat.toXML());
        for (Enumeration e = keys(); e.hasMoreElements(); ){
            String id = (String)e.nextElement();
            try {
                TierFormat tf = getTierFormatForTier(id);
                result.append(tf.toXML());
            }
            catch (JexmaraldaException je) {}
        }
        result.append("</tierformat-table>");
        return result.toString();            
    }
        
    public String toXML(){
        return toXML(true);
    }
    
    void writeXML(FileOutputStream fos, boolean includeFileReferences) throws IOException {
        fos.write(toXML(includeFileReferences).getBytes("UTF-8"));
    }

    public void writeXML(FileOutputStream fos) throws IOException{
        writeXML(fos, true);
    }
    
    /** writes an XML file with the specified file name and the specified path to
     *  the dtd */
    public void writeXMLToFile(String filename, String pathToDTD)throws IOException {
        try {
            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(new File(filename));
            fos.write(StringConstants.XML_HEADER.getBytes("UTF-8"));
            fos.write(StringUtilities.makeXMLDoctypeTierFormatTable(pathToDTD).getBytes("UTF-8"));
            fos.write(StringConstants.XML_COPYRIGHT_COMMENT.getBytes("UTF-8"));
            writeXML(fos);
            fos.close();
            System.out.println("document written.");
        }
        catch (IOException ioe){
            throw (ioe);
        }           
    }
    
    // ********************************************
    // ********** HTML CONVERSION *****************
    // ********************************************

    public String toCSS(){
        String result = new String();
        result+="<STYLE>";
        for (Enumeration e = keys(); e.hasMoreElements(); ){
           String id = (String)e.nextElement();
           result += "span." + id + "{\n";
           TierFormat tierFormat = (TierFormat)get(id);
           result += tierFormat.toCSS();
           result += "}\n";
        }
        result+="</STYLE>";
        return result;
    }
    
    public String toTDCSS(){
        StringBuffer sb = new StringBuffer();
        for (Enumeration e = keys(); e.hasMoreElements(); ){
           String id = (String)e.nextElement();
           String text = new String("td." + id + "{\n");
           sb.append(text);
           TierFormat tierFormat = (TierFormat)get(id);
           sb.append(tierFormat.toCSS());
           text = new String("}\n");
           sb.append(text);
        }
        return sb.toString();
    }
    
    public void writeCSS(FileOutputStream fos) throws IOException{
        fos.write("<STYLE>".getBytes("UTF-8"));
        for (Enumeration e = keys(); e.hasMoreElements(); ){
           String id = (String)e.nextElement();
           String text = new String("td." + id + "{\n");
           fos.write(text.getBytes("UTF-8"));
           TierFormat tierFormat = (TierFormat)get(id);
           fos.write(tierFormat.toCSS().getBytes("UTF-8"));
           text = new String("}\n");
           fos.write(text.getBytes("UTF-8"));
        }
        fos.write("</STYLE>".getBytes("UTF-8"));
    }

    // ********************************************
    // ********** HTML CONVERSION *****************
    // ********************************************
    
    private void replaceOldAttributeNames(){        
        for (Enumeration e = keys(); e.hasMoreElements(); ){
            String id = (String)e.nextElement();
            try {
                TierFormat tf = getTierFormatForTier(id);
                tf.replaceOldAttributeNames();
            }
            catch (JexmaraldaException je) {}
        }
    }
    
}