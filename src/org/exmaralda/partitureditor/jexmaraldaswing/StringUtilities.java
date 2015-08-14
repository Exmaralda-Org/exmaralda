package org.exmaralda.partitureditor.jexmaraldaswing;

import java.awt.*;
import java.util.*;
/*
 * StringUtilities.java
 *
 * Created on 2. Februar 2001, 15:53
 */


/* Revision History
 *  0   02-Feb-2001 Creation 
 */

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */

class StringUtilities extends Object {

    // ********************************************
    // ********** CONSTRUCTORS  *******************
    // ********************************************

    /** Creates new StringUtilities */
    StringUtilities() {
    }   
    
    // ********************************************
    // ********** OUTPUT CONVERSIONS  *************
    // ********************************************

    /** makes a CDATA 'envelope' for checkString if checkString contains critical characters like
    *   'less than' or ampersand */
    static String checkCDATA(String checkString){
        if ((contains(checkString,'<')) ||
            (contains(checkString,'>')) ||
            (contains(checkString,'&'))){
                return new String("<![CDATA[" + checkString + "]]>");
            } else {
                return checkString;
            }            
    }

    static String checkHTML(String checkString){
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
    
    static String toANSI(String string){
        String result = new String();
        for (int pos=0; pos<string.length(); pos++){
            char symbol = string.charAt(pos);
            if (symbol<=127){
                result+=symbol;
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
    // ********** STRING SEGMENTATION UTILITIES  **
    // ********************************************
    
    
/*    static int findSegmentEndSymbol(String checkString, String[] segmentEndSymbols, String symbolFound){
        int result = -1;
        int maxLength = 0;
        for (int pos=0; pos<segmentEndSymbols.length; pos++){
            String ses = segmentEndSymbols[pos];
            int index = checkString.indexOf(ses);
            if ((index!=-1) && ((index < result) || (result==-1))){
                result=index + ses.length();;
                symbolFound = ses;
                maxLength=ses.length();
            }
            else if ((index!=-1) && ((index == result)||(result==-1)) && (ses.length() > maxLength)){
                result=index + ses.length();;
                symbolFound = ses;
                maxLength=ses.length();
            }
        }        
        return result;
    }

    static boolean wordEndsWithUtteranceEndSymbol(String word,String[] utteranceEndSymbols){
        for (int pos=0; pos<utteranceEndSymbols.length; pos++){
            String ses = utteranceEndSymbols[pos];
            if (word.endsWith(ses)){return true;}
        }
        return false;
    }
        
    
    static void removeWordEndSymbol(Segment word, String[] wordEndSymbols, String[] utteranceEndSymbols){
        String[] allEndSymbols = mergeStringArrays(wordEndSymbols, utteranceEndSymbols);
        int max = 0;
        for (int pos=0; pos<allEndSymbols.length; pos++){
            String ses = allEndSymbols[pos];
            if ((word.getDescription().endsWith(ses)) && (ses.length()>max)){max=ses.length();}
        }        
        word.setDescription(word.getDescription().substring(0,word.getDescription().length()-max));
    }

    
    // ********************************************
    // ********** FONT UTILITIES  *****************
    // ********************************************

    static String getBestDefaultFont(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] allFonts = ge.getAvailableFontFamilyNames();
        if (stringArrayContains(allFonts, "Arial Unicode MS")){
            return new String("Arial Unicode MS");}
        if (stringArrayContains(allFonts, "Lucida Sans Unicode")){
            return new String("Lucida Sans Unicode");}
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
        return new String("<!DOCTYPE basic-transcription SYSTEM \"" + pathToDTD + "\\basic-transcription.dtd\">\n");
    }
        
    static String makeXMLDoctypeSegmentedTranscription(String pathToDTD){
        if (pathToDTD.length()==0){return StringConstants.XML_DOCTYPE_SEGMENTED_TRANSCRIPTION;}
        else if (pathToDTD.equals("public")){return StringConstants.XML_PUBLIC_DOCTYPE_SEGMENTED_TRANSCRIPTION;}
        return new String("<!DOCTYPE segmented-transcription SYSTEM \"" + pathToDTD + "\\segmented-transcription.dtd\">\n");
    }

    static String makeXMLDoctypeListTranscription(String pathToDTD){
        if (pathToDTD.length()==0){return StringConstants.XML_DOCTYPE_LIST_TRANSCRIPTION;}
        else if (pathToDTD.equals("public")){return StringConstants.XML_PUBLIC_DOCTYPE_LIST_TRANSCRIPTION;}
        return new String("<!DOCTYPE list-transcription SYSTEM \"" + pathToDTD + "\\list-transcription.dtd\">\n");
    }

    static String makeXMLDoctypePartiturTableTranscription(String pathToDTD){
        if (pathToDTD.length()==0){return StringConstants.XML_DOCTYPE_PARTITUR_TABLE_TRANSCRIPTION;}
        return new String("<!DOCTYPE partitur-table-transcription SYSTEM \"" + pathToDTD + "\\partitur-table-transcription.dtd\">\n");
    }

    static String makeXMLDoctypeTierFormatTable(String pathToDTD){
        if (pathToDTD.length()==0){return StringConstants.XML_DOCTYPE_TIERFORMAT_TABLE;}
        else if (pathToDTD.equals("public")){return StringConstants.XML_PUBLIC_DOCTYPE_TIERFORMAT_TABLE;}
        return new String("<!DOCTYPE tierformat-table SYSTEM \"" + pathToDTD + "\\tierformat-table.dtd\">\n");
    }

    static String makeXMLDoctypeWordUtteranceSegmentationInfoTable(String pathToDTD){
        if (pathToDTD.length()==0){return StringConstants.XML_DOCTYPE_WORD_UTTERANCE_SEGMENTATION_INFO_TABLE;}
        return new String("<!DOCTYPE word-utterance-segmentation-info-table SYSTEM \"" + pathToDTD + "\\word-utterance-segmentation-info-table.dtd\">\n");
    }

    static String makeXMLDoctypeSegmentedToBasicConversionInfo(String pathToDTD){
        if (pathToDTD.length()==0){return StringConstants.XML_DOCTYPE_SEGMENTED_TO_BASIC_CONVERSION_INFO;}
        return new String("<!DOCTYPE segmented-to-basic-conversion-info SYSTEM \"" + pathToDTD + "\\segmented-to-basic-conversion-info.dtd\">\n");
    }

    static String makeXMLDoctypeSegmentedToListConversionInfo(String pathToDTD){
        if (pathToDTD.length()==0){return StringConstants.XML_DOCTYPE_SEGMENTED_TO_LIST_CONVERSION_INFO;}
        return new String("<!DOCTYPE segmented-to-list-conversion-info SYSTEM \"" + pathToDTD + "\\segmented-to-list-conversion-info.dtd\">\n");
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
            return (11906-2*1418)/20;
        }
        else if (paperFormat.equals("DINA4_HORIZONTAL")){
            return (16840-2*1418)/20;
        }
        else if (paperFormat.equals("DINA3_VERTICAL")){
            return (16840-2*1418)/20;
        }
        else if (paperFormat.equals("DINA3_HORIZONTAL")){
            return (23814-2*1418)/20;
        }
        return (11906-2*1418)/20;
    }
    
    // ********************************************
    // ********** UTILITIES for Tier Formats ******
    // ********************************************

    static void addStandardTierFormats(TierFormatTable tft){
        String bestDefaultFont = getBestDefaultFont();
        TierFormat clf = new TierFormat("COLUMN-LABEL", "Plain", 5, "Left", "black", "white", bestDefaultFont);
        TierFormat rlf = new TierFormat("ROW-LABEL", "Bold", 10, "Left", "black", "white", bestDefaultFont);
        TierFormat subrlf = new TierFormat("SUB-ROW-LABEL", "Plain", 8, "Right", "black", "white", bestDefaultFont);
        TierFormat empty = new TierFormat("EMPTY", "Plain", 2, "Left", "white", "white", bestDefaultFont);
        TierFormat emptyEditor = new TierFormat("EMPTY_EDITOR", "Plain", 2, "Left", "white", "lightGray", bestDefaultFont);
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
    }*/

    // ********************************************
    // ********** SIMPLE EXMARALDA UTILITIES ******
    // ********************************************

/*    static String trim(String source){
        String trimmed = new Stringsource.trim();
        if (result.charAt(result.indexOf(trimmed)+trimmed.length())==' '){trimmed+=" ";}
        return trimmed;
        
    }*/
    
    /*static String getSpeaker(String line) throws JexmaraldaException{
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
    }*/
    
}