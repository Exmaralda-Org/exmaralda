package org.exmaralda.partitureditor.jexmaralda;

import java.awt.*;
import java.util.*;
/*
 * StringUtilities.java
 *
 * Created on 2. Februar 2001, 15:53
 */


/* Revision History
 *  0   02-Feb-2001 Creation 
 *  1   22-Oct-2001 Changed some details in getPixelWidthForPaperFormat() to achieve better RTF-Export

 *  n+1 05-Oct-2005 Changed the method toXMLString() - quotes are also escaped now
 */

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */

public class StringUtilities extends Object {

    // ********************************************
    // ********** CONSTRUCTORS  *******************
    // ********************************************

    static final String CONTROL_REGEX = "[\\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\u0008\\u0009"
            + "\\u000A\\u000B\\u000C\\u000D\\u000E\\u000F\\u0010\\u0011\\u0012\\u0013\\u0014\\u0015"
            + "\\u0016\\u0017\\u0018\\u0019\\u001A\\u001B\\u001C\\u001D\\u001E\\u001F]";

    
    /** Creates new StringUtilities */
    StringUtilities() {
    }   
    
    // ********************************************
    // ********** OUTPUT CONVERSIONS  *************
    // ********************************************

    /** makes a CDATA 'envelope' for checkString if checkString contains critical characters like
    *   'less than' or ampersand */
    static String checkCDATA(String checkString){
        if (checkString==null) {return null;}
        // replace non-XML-control-characters by simple space
        checkString = replaceControlCharacters(checkString);
        if ((contains(checkString,'<')) ||
            (contains(checkString,'>')) ||
            (contains(checkString,'&')) ||
            checkString.equals(" ")){
                return new String("<![CDATA[" + checkString + "]]>");
            } else {
                return checkString;
            }            
    }
    
    public static String replaceControlCharacters(String checkString){
        return checkString.replaceAll(CONTROL_REGEX, " ");
    }
    
    /** replaces reserved symbols by the corresponding entities 
     *  preferrably to be used with attribute content, not with PCDATA */
    public static String toXMLString(String checkString){
        StringBuffer result = new StringBuffer();
        for (int pos=0; pos<checkString.length(); pos++){
            char c = checkString.charAt(pos);
            if (c=='<') {result.append("&lt;");}
            else if (c=='>') {result.append("&gt;");}
            else if (c=='&') {result.append("&amp;");}
            // added on 05 October 2005, Thomas, ICSI
            // quotes need to be escaped, too!
    	    else if (c=='"') {result.append("&quot;");}
            // added on 07 June 2011
            // apostrophes need to be escaped, too!
    	    //else if (c=='\'') {result.append("&apos;");}
            else {result.append(c);}            
        }
        return result.toString();
    }

    static String checkHTML(String checkString){
        return checkHTML(checkString,true);
    }
    
    static String checkHTML(String checkString, boolean nonbreak){
        String result = new String();
        for (int pos=0; pos<checkString.length(); pos++){
            char symbol = checkString.charAt(pos);
            if (symbol==' ' && nonbreak){result+="&nbsp;";}
            else if (symbol=='<'){result+="&lt;";}
            else if (symbol=='>'){result+="&gt;";}
            else if (symbol=='&'){result+="&amp;";}
            else if (symbol=='"'){result+="&quot;";}
            else {result+=symbol;}
        }
        return result;
    }
    
    static String toANSI(String string){
        String result = new String();
        for (int pos=0; pos<string.length(); pos++){
            char symbol = string.charAt(pos);
            if (symbol=='{'){
                result+="\\{";
            }
            else if (symbol=='}'){
                result+="\\}";
            }
            else if (symbol=='\\'){
                result+="\\\\";
            }            
            else if (symbol<=127){
                result+=symbol;
            }
            else if (symbol<=255){
                String hex = "\\'" + Integer.toHexString(symbol).toLowerCase();
                result+=hex;
                }
            else {
                result+="\\u" + new Integer(symbol).toString() + " ";
            }
        }
        return result;
    }

    // ********************************************
    // ********** STRING ARRAY UTILITIES  *********
    // ********************************************

    /** returns true if checkString contains checkChar, false otherwise */
    static boolean contains(String checkString, char checkChar){
        if (checkString.indexOf(checkChar)>=0){return true;}
        return false;
    }

    static boolean stringArrayContains(String[] array, String searchString){
        for (int pos=0; pos<array.length; pos++){
            if (array[pos].equals(searchString)) {return true;}            
        }
        return false;
    }
    
    static String[] stringVectorToArray(Vector source){
        String[] result = new String[source.size()];
        for (int pos=0; pos<result.length; pos++){
          result[pos]=(String)source.elementAt(pos);
        }
        return result;
    }
    
    static String[] mergeStringArrays(String[] a1, String[] a2){
        String[] result = new String[a1.length + a2.length];
        int all=0;
        for (int pos=0; pos<a1.length; pos++){
            result[all]=a1[pos];
            all++;
        }
        for (int pos=0; pos<a2.length; pos++){
            result[all]=a2[pos];
            all++;
        }
        return result;
    }

    static void addStringArrayToVector(String[] array, Vector vector){
        for (int pos=0; pos<array.length; pos++){
            vector.addElement(array[pos]);
        }
    }


    
    // ********************************************
    // ********** FONT UTILITIES  *****************
    // ********************************************

    static String getBestDefaultFont(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] allFonts = ge.getAvailableFontFamilyNames();
        if (stringArrayContains(allFonts, "Times New Roman")){
            return new String("Times New Roman");}
        if (stringArrayContains(allFonts, "Arial")){
            return new String("Arial");}
        if (stringArrayContains(allFonts, "Helvetica")){
            return new String("Helvetica");}
        return new String("Dialog");
    }
    
    static void initialiseFonts(){
        // this is necessary, otherwise the fonts are not correctly initialised
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] AllFonts = ge.getAvailableFontFamilyNames();
    }
     
    static int getCutPosition (String string, Font font, long pixelWidth, boolean respectWords){
          int width=0;
          int lastSeparator=0;
          for (int pos=0; pos < string.length(); pos++){
             char symbol = string.charAt(pos);
             if ((symbol==' ') || (symbol=='-') || (symbol=='\'') || (symbol=='.')){ lastSeparator = pos;}
             if (width + stringWidth(font, symbol) > pixelWidth) {
                if (respectWords && (lastSeparator > 0)) { return lastSeparator + 1; }
                else if (respectWords && (lastSeparator == 0) && (pixelWidth<=100)) {return 0;}
                else    { return pos; }
             } else {
               width+= stringWidth(font, symbol);
             }
         }
         return string.length();
    }    
  
    static double stringWidth(java.awt.Font font, char ch){
        String text = new Character(ch).toString(); 
        java.awt.font.TextLayout textLayout = new java.awt.font.TextLayout(text, font, new java.awt.font.FontRenderContext(null, false, true));
        return textLayout.getAdvance();
    }

    static double stringWidth(java.awt.Font font, String text){
        if (text.length()==0) {return 0;}
        java.awt.font.TextLayout textLayout = new java.awt.font.TextLayout(text, font, new java.awt.font.FontRenderContext(null, false, true));
        return textLayout.getAdvance();
    }
    
    static double stringWidth(TierFormat tf, String text){
        if (text.length()==0) {return 0;}
        java.awt.Font font = tf.getFont();
        java.awt.font.TextLayout textLayout = new java.awt.font.TextLayout(text, font, new java.awt.font.FontRenderContext(null, false, true));
        return textLayout.getAdvance();
    }

    // ********************************************
    // ********** UTILITIES for filenames *********
    // ********************************************

    static String makeXMLDoctypeBasicTranscription(String pathToDTD){
        if (pathToDTD.length()==0){return StringConstants.XML_DOCTYPE_BASIC_TRANSCRIPTION;}
        else if (pathToDTD.equals("public")){return StringConstants.XML_PUBLIC_DOCTYPE_BASIC_TRANSCRIPTION;}
        else if (pathToDTD.equals("none")){return new String();}
        return new String("<!DOCTYPE basic-transcription SYSTEM \"" + pathToDTD + "\\basic-transcription.dtd\">\n");
    }
        
    static String makeXMLDoctypeSegmentedTranscription(String pathToDTD){
        if (pathToDTD.length()==0){return StringConstants.XML_DOCTYPE_SEGMENTED_TRANSCRIPTION;}
        else if (pathToDTD.equals("public")){return StringConstants.XML_PUBLIC_DOCTYPE_SEGMENTED_TRANSCRIPTION;}
        else if (pathToDTD.equals("none")){return new String();}
        return new String("<!DOCTYPE segmented-transcription SYSTEM \"" + pathToDTD + "\\segmented-transcription.dtd\">\n");
    }

    static String makeXMLDoctypeListTranscription(String pathToDTD){
        if (pathToDTD.length()==0){return StringConstants.XML_DOCTYPE_LIST_TRANSCRIPTION;}
        else if (pathToDTD.equals("public")){return StringConstants.XML_PUBLIC_DOCTYPE_LIST_TRANSCRIPTION;}
        else if (pathToDTD.equals("none")){return new String();}
        return new String("<!DOCTYPE list-transcription SYSTEM \"" + pathToDTD + "\\list-transcription.dtd\">\n");
    }


    static String makeXMLDoctypeTierFormatTable(String pathToDTD){
        if (pathToDTD.length()==0){return StringConstants.XML_DOCTYPE_TIERFORMAT_TABLE;}
        else if (pathToDTD.equals("public")){return StringConstants.XML_PUBLIC_DOCTYPE_TIERFORMAT_TABLE;}
        else if (pathToDTD.equals("none")){return new String();}
        return new String("<!DOCTYPE tierformat-table SYSTEM \"" + pathToDTD + "\\tierformat-table.dtd\">\n");
    }


    // ********************************************
    // ********** UTILITIES for RTF Export ********
    // ********************************************

    static String rtfPaperFormat(int[] paperFormat){      
        String paperw = new Integer(paperFormat[0]).toString();
        String paperh = new Integer(paperFormat[1]).toString();
        String margl = new Integer(paperFormat[2]).toString();
        String margr = new Integer(paperFormat[3]).toString();
        String margt = new Integer(paperFormat[4]).toString();
        String margb = new Integer(paperFormat[5]).toString();
        String result = new String( "\\paperw" + paperw +
                                    "\\paperh" + paperh +
                                    "\\margl" + margl +
                                    "\\margr" + margr +
                                    "\\margt" + margt +
                                    "\\margb" + margb);
        return result;
    }
    
    static String rtfPaperFormat(String paperFormat){
        if (paperFormat.equals("DINA4_VERTICAL")){
            int[] pf = {11906,16838,1418,1418,1418,1134};
            return rtfPaperFormat(pf);
        }
        else if (paperFormat.equals("DINA4_HORIZONTAL")){
            int[] pf = {16840,11907,1134,1418,1418,1418};
            return rtfPaperFormat(pf);
        }
        else if (paperFormat.equals("DINA3_VERTICAL")){
            int[] pf = {16840,23814,1418,1418,1418,1134};
            return rtfPaperFormat(pf);
        }
        else if (paperFormat.equals("DINA3_HORIZONTAL")){
            int[] pf = {23814,16840,1134,1418,1418,1418};
            return rtfPaperFormat(pf);
        }
        int[] pf = {11906,16838,1418,1418,1418,1134};
        return rtfPaperFormat(pf);
   }

    static double getPixelWidthForPaperFormat(String paperFormat){
        if (paperFormat.equals("DINA4_VERTICAL")){            
            /* Debugging 2210a*/
            // 22-10-2001: vorher: 
            // return (11906-2*1418)/20;
            // neu !!!!!!
            return (11906-2*1418)/20-40;
            // ende neu
        }
        else if (paperFormat.equals("DINA4_HORIZONTAL")){
            /* Debugging 2210a*/
            // 22-10-2001: vorher: 
            // return (16840-2*1418)/20;
            // neu !!!!!!
            return (16840-2*1418)/20-40;
            // ende neu
        }
        else if (paperFormat.equals("DINA3_VERTICAL")){
            /* Debugging 2210a*/
            // 22-10-2001: vorher: 
            // return (16840-2*1418)/20;
            // neu !!!!!!
            return (16840-2*1418)/20-40;
            // ende neu
        }
        else if (paperFormat.equals("DINA3_HORIZONTAL")){
            /* Debugging 2210a*/
            // 22-10-2001: vorher: 
            // return (23814-2*1418)/20
            // neu !!!!!!
            return (23814-2*1418)/20-40;
            // ende neu
        }
        /* Debugging 2210a*/
        // 22-10-2001: vorher: 
        // return (11906-2*1418)/20;
        // neu !!!!!!
        return (11906-2*1418)/20-40;
    }
    
    // ********************************************
    // ********** UTILITIES for Tier Formats ******
    // ********************************************

    static void addStandardTierFormats(TierFormatTable tft){
        String bestDefaultFont = getBestDefaultFont();
        TierFormat clf = new TierFormat("COLUMN-LABEL", "Plain", 7, "Left", "black", "lightGray", bestDefaultFont);
        TierFormat rlf = new TierFormat("ROW-LABEL", "Bold", 10, "Left", "black", "lightGray", bestDefaultFont);
        TierFormat subrlf = new TierFormat("SUB-ROW-LABEL", "Plain", 8, "Right", "black", "white", bestDefaultFont);
        TierFormat empty = new TierFormat("EMPTY", "Plain", 2, "Left", "white", "white", bestDefaultFont);
        TierFormat emptyEditor = new TierFormat("EMPTY-EDITOR", "Plain", 2, "Left", "white", "lightGray", bestDefaultFont);
        try {
            tft.addTierFormat(clf);
            tft.addTierFormat(rlf);
            tft.addTierFormat(subrlf);
            tft.addTierFormat(empty);
            tft.addTierFormat(emptyEditor);
        }
        catch (JexmaraldaException je){}
    }
    
    static void addSegmentedTranscriptionTierFormats(TierFormatTable tft){
        String bestDefaultFont = getBestDefaultFont();
        TierFormat ctl = new TierFormat("CTL", "Bold", 6, "Left", "black", "red", bestDefaultFont);
        TierFormat itl = new TierFormat("ITL", "Bold", 6, "Left", "black", "yellow", bestDefaultFont);
        try {
            tft.addTierFormat(ctl);
            tft.addTierFormat(itl);
        }
        catch (JexmaraldaException je){}    
    }

    // ********************************************
    // ********** SIMPLE EXMARALDA UTILITIES ******
    // ********************************************

/*    static String trim(String source){
        String trimmed = new Stringsource.trim();
        if (result.charAt(result.indexOf(trimmed)+trimmed.length())==' '){trimmed+=" ";}
        return trimmed;
        
    }*/
    
    static String getSpeaker(String line) throws JexmaraldaException{
        int index = line.indexOf(':');
        if (index < 0) {throw new JexmaraldaException(40,"Simple Exmaralda Parse Error - No Speaker: " + line);}
        return line.substring(0, index);
    }
    
    static String getDependentDescription(String line) throws JexmaraldaException{
        int startIndex = line.indexOf("[");
        int endIndex = line.indexOf("]");
        if ((startIndex==-1) && (endIndex==-1)) {return new String();}
        startIndex++;
        if ((!(startIndex<endIndex)) || (startIndex==0) || (endIndex==-1)){
            throw new JexmaraldaException(41,"Simple Exmaralda Parse Error - [] don't match: " + line);
        }
        String result = line.substring(startIndex,endIndex);
        String trimmed = result.trim();
        if (((result.indexOf(trimmed)+trimmed.length()) < result.length()) && (result.charAt(result.indexOf(trimmed)+trimmed.length())==' ')){trimmed+=" ";}
        return trimmed;
    }
    
    static String getAnnotationDescription(String line) throws JexmaraldaException{
        int startIndex = line.indexOf("{");
        int endIndex = line.indexOf("}");
        if ((startIndex==-1) && (endIndex==-1)) {return new String();}
        startIndex++;
        if ((!(startIndex<endIndex)) || (startIndex==0) || (endIndex==-1)){
            throw new JexmaraldaException(42,"Simple Exmaralda Parse Error - {} don't match: " + line);
        }
        String result = line.substring(startIndex,endIndex);
        String trimmed = result.trim();
        if (((result.indexOf(trimmed)+trimmed.length()) < result.length()) && (result.charAt(result.indexOf(trimmed)+trimmed.length())==' ')){trimmed+=" ";}
        return trimmed;
    }

    static String getMainDescription(String line) throws JexmaraldaException{
        int startIndex = Math.max(line.indexOf("]"), line.indexOf(":"));
        int endIndex = line.indexOf("{");
        if (endIndex == -1) {endIndex = line.length();}
        if (startIndex == -1) {
            throw new JexmaraldaException(43,"Simple Exmaralda Parse Error - " + line);
        }
        startIndex++;        
        String result = line.substring(startIndex,endIndex);
        String trimmed = result.trim();
        if (((result.indexOf(trimmed)+trimmed.length()) < result.length()) && (result.charAt(result.indexOf(trimmed)+trimmed.length())==' ')){trimmed+=" ";}
        return trimmed;
    }
    
    static String makeXMLOpenElement(String elementName, String[][] attributes){
        StringBuffer sb = new StringBuffer();
        sb.append("<");
        sb.append(elementName);
        if (attributes != null){
            for (int pos=0; pos<attributes.length; pos++){
                if (attributes[pos][1]!=null){
                    sb.append(" ");
                    sb.append(attributes[pos][0]);
                    sb.append("=\"");
                    sb.append(toXMLString(attributes[pos][1]));
                    sb.append("\"");
                }
            }
        }
        sb.append(">");
        return sb.toString();
    }

    static String makeXMLCloseElement(String elementName){
        StringBuffer sb = new StringBuffer();
        sb.append("</");
        sb.append(elementName);
        sb.append(">");
        return sb.toString();
    }
    
    public static String stripXMLElements (String text){
        StringBuffer sb = new StringBuffer();
        boolean on = true;
        for (int pos=0; pos<text.length(); pos++){
            char c = text.charAt(pos);
            if (c=='<') on=false;
            if (c=='>') {on=true;}
            else if (on) {sb.append(c);}
        }
        return sb.toString();
    }
    
    
}