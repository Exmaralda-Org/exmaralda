/*
 * WordMLUtilities.java
 *
 * Created on 19. April 2004, 13:34
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  thomas
 */
public class WordMLUtilities {
    
    public static String WORD_DOCUMENT_ELEMENT = 
                                      "<w:wordDocument" 
                                    + " xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\""
                                    + " xmlns:v=\"urn:schemas-microsoft-com:vml\""
                                    + " xmlns:w10=\"urn:schemas-microsoft-com:office:word\""
                                    + " xmlns:sl=\"http://schemas.microsoft.com/schemaLibrary/2003/core\""
                                    + " xmlns:aml=\"http://schemas.microsoft.com/aml/2001/core\""
                                    + " xmlns:wx=\"http://schemas.microsoft.com/office/word/2003/auxHint\""
                                    + " xmlns:o=\"urn:schemas-microsoft-com:office:office\""
                                    + " xmlns:dt=\"uuid:C2F41010-65B3-11d1-A29F-00AA00C14882\""
                                    + " w:macrosPresent=\"no\""
                                    + " w:embeddedObjPresent=\"no\""
                                    + " w:ocxPresent=\"no\""
                                    + " xml:space=\"preserve\">";
    
 
    public static String DEFAULT_FONTS_ELEMENT = 
                                            "<w:defaultFonts "
                                        +   "w:ascii=\"Times New Roman\" "
                                        +   "w:fareast=\"Times New Roman\" "
                                        +   "w:h-ansi=\"Times New Roman\" "
                                        +   "w:cs=\"Times New Roman\"/>";
    
    /** Creates a new instance of WordMLUtilities */
    public WordMLUtilities() {
    }
    
    public static String fontAttributes(String fontname){
        return ("w:ascii=\"" + fontname + "\" w:fareast=\"" + fontname + "\" w:h-ansi=\"" + fontname + "\" w:cs=\"" + fontname + "\"");
    }
    
    public static String prtEnvelope(String text){
        return prtEnvelope(text, "");
    }
    
    public static String prtEnvelope(String text, String style){
        StringBuffer sb = new StringBuffer();
        sb.append("<w:p>"); // paragraph
        sb.append("<w:r>"); // run
        sb.append("<w:rPr>");  // run properties
        sb.append("<w:rStyle w:val=\"" + style + "\"/>");
        sb.append("</w:rPr>");  // run properties
        sb.append("<w:t>"); // text
        sb.append(text);
        sb.append("</w:t></w:r></w:p>");
        return sb.toString();
    }

    public static String rtEnvelope(String text, String style){
        StringBuffer sb = new StringBuffer();
        sb.append("<w:r>"); // run
        sb.append("<w:rPr>");  // run properties
        sb.append("<w:rStyle w:val=\"" + style + "\"/>");
        sb.append("</w:rPr>");  // run properties
        sb.append("<w:t>"); // text
        sb.append(text);
        sb.append("</w:t></w:r>");
        return sb.toString();
    }
    
    static String toWordMLBorderStyleString(String borderStyle){
        if (borderStyle.equalsIgnoreCase("Solid")){   
            return "single"; // solid border
        } else if (borderStyle.equalsIgnoreCase("Dashed")){   
            return "dashed"; // dashed border
        } else {    // dotted border
            return "dotted"; // dashed border
        }
    }
    
    static String toColorString(String colorString){
        //#R00G00B00
        String result =     colorString.substring(2,4)
                          + colorString.substring(5,7)
                          + colorString.substring(8,10);
        return result;
    }

    public static String toWordMLBorderDefinition(Format format, WordMLParameters param){

        /*<w:tcBorders>
            <w:top w:val="single" w:sz="4" wx:bdrwidth="10" w:space="0" w:color="000000"/>
            <w:left w:val="single" w:sz="4" wx:bdrwidth="10" w:space="0" w:color="000000"/>
            <w:bottom w:val="single" w:sz="4" wx:bdrwidth="10" w:space="0" w:color="000000"/>
            <w:right w:val="nil"/>
        </w:tcBorders>*/

        StringBuffer sb = new StringBuffer();
        sb.append("<w:tcBorders>");

        // top border
        sb.append("<w:top ");
        if (param.drawFrame && param.isFirstLine && param.frame.indexOf('t')>=0){
            sb.append("w:val=\"" + toWordMLBorderStyleString(param.frameStyle) + "\" ");
            sb.append("w:sz=\"4\" "); 
            sb.append("wx:brdrwidth=\"10\" "); // border width in twips
            sb.append("w:space=\"0\" ");
            sb.append("w:color=\"" + toColorString(param.frameColor) + "\""); // black            
        }
        else if (format.getProperty("chunk-border").indexOf('t')<0){    // no top border
            sb.append("w:val=\"nil\"");
        } else {    // set border style and color
            sb.append("w:val=\"" + toWordMLBorderStyleString(format.getProperty("chunk-border-style")) + "\" ");
            sb.append("w:sz=\"4\" "); 
            sb.append("wx:brdrwidth=\"10\" "); // border width in twips
            sb.append("w:space=\"0\" ");
            sb.append("w:color=\"" + format.getProperty("chunk-border-color") + "\""); // black            
        }
        sb.append("/>");

        // left border
        sb.append("<w:left ");
        if (param.drawFrame && param.isFirstChunk && param.frame.indexOf('l')>=0 && (!param.isOutside)){
            sb.append("w:val=\"" + toWordMLBorderStyleString(param.frameStyle) + "\" ");
            sb.append("w:sz=\"4\" "); 
            sb.append("wx:brdrwidth=\"10\" "); // border width in twips
            sb.append("w:space=\"0\" ");
            sb.append("w:color=\"" + toColorString(param.frameColor) + "\""); // black            
        }
        else if (format.getProperty("chunk-border").indexOf('l')<0){    // no left border
            sb.append("w:val=\"nil\"");
        } else {    // set border style and color
            sb.append("w:val=\"" + toWordMLBorderStyleString(format.getProperty("chunk-border-style")) + "\" ");
            sb.append("w:sz=\"4\" "); 
            sb.append("wx:brdrwidth=\"10\" "); // border width in twips
            sb.append("w:space=\"0\" ");
            sb.append("w:color=\"" + format.getProperty("chunk-border-color") + "\""); // black            
        }
        sb.append("/>");
        
        // bottom border
        sb.append("<w:bottom ");
        if (param.drawFrame && param.isLastLine && param.frame.indexOf('b')>=0){
            sb.append("w:val=\"" + toWordMLBorderStyleString(param.frameStyle) + "\" ");
            sb.append("w:sz=\"4\" "); 
            sb.append("wx:brdrwidth=\"10\" "); // border width in twips
            sb.append("w:space=\"0\" ");
            sb.append("w:color=\"" + toColorString(param.frameColor) + "\""); // black            
        }
        else if (format.getProperty("chunk-border").indexOf('b')<0){    // no bottom border
            sb.append("w:val=\"nil\"");
        } else {    // set border style and color
            sb.append("w:val=\"" + toWordMLBorderStyleString(format.getProperty("chunk-border-style")) + "\" ");
            sb.append("w:sz=\"4\" "); 
            sb.append("wx:brdrwidth=\"10\" "); // border width in twips
            sb.append("w:space=\"0\" ");
            sb.append("w:color=\"" + format.getProperty("chunk-border-color") + "\""); // black            
        }
        sb.append("/>");

        // right border
        sb.append("<w:right ");
        if (param.drawFrame && param.isLastChunk && param.frame.indexOf('r')>=0 && (!param.isOutside)){
            sb.append("w:val=\"" + toWordMLBorderStyleString(param.frameStyle) + "\" ");
            sb.append("w:sz=\"4\" "); 
            sb.append("wx:brdrwidth=\"10\" "); // border width in twips
            sb.append("w:space=\"0\" ");
            sb.append("w:color=\"" + toColorString(param.frameColor) + "\""); // black            
        }
        else if (format.getProperty("chunk-border").indexOf('r')<0){    // no right border
            sb.append("w:val=\"nil\"");
        } else {    // set border style and color
            sb.append("w:val=\"" + toWordMLBorderStyleString(format.getProperty("chunk-border-style")) + "\" ");
            sb.append("w:sz=\"4\" "); 
            sb.append("wx:brdrwidth=\"10\" "); // border width in twips
            sb.append("w:space=\"0\" ");
            sb.append("w:color=\"" + format.getProperty("chunk-border-color") + "\""); // black            
        }
        sb.append("/>");

        sb.append("</w:tcBorders>");
        return sb.toString();
    }
    

}
