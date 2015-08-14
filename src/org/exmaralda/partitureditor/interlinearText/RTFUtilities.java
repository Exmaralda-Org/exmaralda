/*
 * Class.java
 *
 * Created on 8. Maerz 2002, 16:14
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class RTFUtilities {

    public static final String RTF_HEAD = "{\\rtf1\\ansi\\ansicpg1252\\uc1 \\deff0\\deflang1031\\deflangfe1031";
    // this is the crucial flag for correct rendering of UNICODE chars in Arial Unicode MS (Don' ask why...)
    //public static final String RTF_LNBRKRULE = "\\lnbrkrule";   
    public static final String RTF_OTHER = "\\deftab708\\widowctrl\\ftnbj\\aenddoc\\hyphhotz425\\noxlattoyen\\expshrtn\\noultrlspc\\dntblnsbdb\\nospaceforul\\formshade\\horzdoc\\dghspace180\\dgvspace180\\dghorigin1701\\dgvorigin1984\\dghshow0\\dgvshow0\\jexpand\\viewkind1\\viewscale75\\pgbrdrhead\\pgbrdrfoot\\nolnhtadjtbl\\lnbrkrule \\fet0\\sectd \\linex0\\endnhere\\sectdefaultcl"; 
    public static final short LB_RUN = -2;
    public static final short HB_RUN = -3;
    public static final short DB_RUN = -4;
    
    /** Creates new RTFUtilities */
    public RTFUtilities() {
    }
    
    private static String toRTFFontProperties(String fontSize, String fontColor, String fontFace){
        StringBuffer sb = new StringBuffer();
        sb.append("\\fs" + fontSize);   // font size (in half points!)
        sb.append("\\cf" + fontColor); // font color (reference to the color table)
        if (fontFace.equalsIgnoreCase("Bold")){sb.append("\\b");}   // turn on bold
        if (fontFace.equalsIgnoreCase("Italic")){sb.append("\\i");} // turn on italic
        return sb.toString();
    }

    // added 01-02-2010: used for escaping strings inside metadata
    public static String toEscapedRTFString(String original){
        StringBuffer currentRun = new StringBuffer();
        for (int pos=0; pos<original.length(); pos++){
            char c = original.charAt(pos);
            if ((c>=0) && (c<=127)){        // low byte run
                switch (c){
                    case '\\' : currentRun.append("\\\\"); break;   // replace back slash by two back slashes
                    case '{'  : currentRun.append("\\{");  break;   // replace opening curly bracket by back slash plus ocb
                    case '}'  : currentRun.append("\\}");  break;   // replace closing curly bracket by back slash plus ccb
                    default   : currentRun.append(c); break;        // c is proper ANSI character
                }
            } else if ((c>=128) && (c<=255)){        // high byte run
                String hex = "\\'" + Integer.toHexString(c).toLowerCase();
                currentRun.append(hex);
            } else {    // double byte run
                currentRun.append("\\u" + new Integer(c).toString());
                currentRun.append("\\'3f");
            }
        }
        return currentRun.toString();
    }

    public static String toEscapedRTFString(String original, String fontNo, String fontSize, String fontColor, String fontFace){
        if (original.length()==0) {return new String();}
        StringBuffer sb = new StringBuffer();
        short currentRunType = 0;
        StringBuffer currentRun = new StringBuffer();
        for (int pos=0; pos<original.length(); pos++){
            char c = original.charAt(pos);
            if ((c>=0) && (c<=127)){        // low byte run
                if (currentRunType!=LB_RUN && pos!=0){
                    currentRun.append("}");
                    sb.append(currentRun.toString());
                }
                if (currentRunType!=LB_RUN){
                    currentRun=new StringBuffer();
                    currentRun.append("{");
                    currentRun.append("\\f" + fontNo);
                    currentRun.append(toRTFFontProperties(fontSize,fontColor,fontFace) + " ");
                }
                currentRunType = LB_RUN;
                switch (c){
                    case '\\' : currentRun.append("\\\\"); break;   // replace back slash by two back slashes
                    case '{'  : currentRun.append("\\{");  break;   // replace opening curly bracket by back slash plus ocb
                    case '}'  : currentRun.append("\\}");  break;   // replace closing curly bracket by back slash plus ccb
                    default   : currentRun.append(c); break;        // c is proper ANSI character
                                
                }
            } else if ((c>=128) && (c<=255)){        // high byte run
                if (currentRunType!=HB_RUN && pos!=0){
                    currentRun.append("}");
                    sb.append(currentRun.toString());
                }
                if (currentRunType!=HB_RUN){
                    currentRun=new StringBuffer();
                    currentRun.append("{");
                    currentRun.append("\\f" + fontNo);
                    // added 26-04-2004 / take 1
                    currentRun.append("\\loch\\af" + fontNo);
                    currentRun.append("\\hich\\af" + fontNo);                    
                    currentRun.append("\\dbch\\f" + fontNo);       
                    currentRun.append(toRTFFontProperties(fontSize,fontColor,fontFace) + " ");
                }
                currentRunType = HB_RUN;
                String hex = "\\'" + Integer.toHexString(c).toLowerCase();
                currentRun.append(hex);
            } else {    // double byte run
                if (currentRunType!=DB_RUN && pos!=0){
                    currentRun.append("}");
                    sb.append(currentRun.toString());
                }
                if (currentRunType!=DB_RUN){
                    currentRun=new StringBuffer();
                    currentRun.append("{");

                    currentRun.append("\\loch\\af" + fontNo);
                    currentRun.append("\\hich\\af" + fontNo);                    
                    currentRun.append("\\dbch\\f" + fontNo);       
                    currentRun.append(toRTFFontProperties(fontSize,fontColor,fontFace) + " ");
                }
                currentRunType = DB_RUN;
                currentRun.append("\\u" + new Integer(c).toString());
                currentRun.append("\\'3f");
            }
        }
        currentRun.append("}");
        sb.append(currentRun.toString());
        return sb.toString();
    }
    
    static String toRTFBorderStyleString(String borderStyle){
        if (borderStyle.equalsIgnoreCase("Solid")){   
            return "\\brdrs"; // solid border
        } else if (borderStyle.equalsIgnoreCase("Dashed")){   
            return "\\brdrdash"; // dashed border
        } else {    // dotted border
            return "\\brdrdot"; // dashed border
        }
    }
    
    public static String toRTFBorderDefinition(Format format, RTFParameters param){

        StringBuffer sb = new StringBuffer();
        // left border
        sb.append("\\clbrdrl");
        if (param.drawFrame && param.isFirstChunk && param.frame.indexOf('l')>=0 && (!param.isOutside)){
            sb.append(toRTFBorderStyleString(param.frameStyle));
            param.addColorMapping(param.frameColor);
            sb.append("\\brdrw10"); // border width in twips
            sb.append("\\brdrcf" + param.getColorMapping(param.frameColor)); // black            
        }
        else if (format.getProperty("chunk-border").indexOf('l')<0){    // no left border
            sb.append("\\brdrnone");
        } else {    // set border style and color
            sb.append(toRTFBorderStyleString(format.getProperty("chunk-border-style")));
            sb.append("\\brdrw10"); // border width in twips
            sb.append("\\brdrcf" + param.getColorMapping(format.getProperty("chunk-border-color")));
        }
        
        // right border
        sb.append("\\clbrdrr");
        if (param.drawFrame && param.isLastChunk && param.frame.indexOf('r')>=0 && (!param.isOutside)){
            sb.append(toRTFBorderStyleString(param.frameStyle));
            param.addColorMapping(param.frameColor);
            sb.append("\\brdrw10"); // border width in twips
            sb.append("\\brdrcf" + param.getColorMapping(param.frameColor));            
        }
        else if (format.getProperty("chunk-border").indexOf('r')<0){    // no right border
            sb.append("\\brdrnone");
        } else {    // set border style and color
            sb.append(toRTFBorderStyleString(format.getProperty("chunk-border-style")));
            sb.append("\\brdrw10"); // border width in twips
            sb.append("\\brdrcf" + param.getColorMapping(format.getProperty("chunk-border-color")));
        }

        // top border
        sb.append("\\clbrdrt");
        if (param.drawFrame && param.isFirstLine && param.frame.indexOf('t')>=0){
            sb.append(toRTFBorderStyleString(param.frameStyle));
            param.addColorMapping(param.frameColor);
            sb.append("\\brdrw10"); // border width in twips
            sb.append("\\brdrcf" + param.getColorMapping(param.frameColor));             
        }
        else if (format.getProperty("chunk-border").indexOf('t')<0){    // no top border
            sb.append("\\brdrnone");
        } else {    // set border style and color
            sb.append(toRTFBorderStyleString(format.getProperty("chunk-border-style")));
            sb.append("\\brdrw10"); // border width in twips
            sb.append("\\brdrcf" + param.getColorMapping(format.getProperty("chunk-border-color")));
        }

        // bottom border
        sb.append("\\clbrdrb");
        if (param.drawFrame && param.isLastLine && param.frame.indexOf('b')>=0){
            sb.append(toRTFBorderStyleString(param.frameStyle));
            param.addColorMapping(param.frameColor);
            sb.append("\\brdrw10"); // border width in twips
            sb.append("\\brdrcf" + param.getColorMapping(param.frameColor));             
        }
        else if (format.getProperty("chunk-border").indexOf('b')<0){    // no bottom border
            sb.append("\\brdrnone");
        } else {    // set border style and color
            sb.append(toRTFBorderStyleString(format.getProperty("chunk-border-style")));
            sb.append("\\brdrw10"); // border width in twips
            sb.append("\\brdrcf" + param.getColorMapping(format.getProperty("chunk-border-color")));
        }
        return sb.toString();
    }
    

}
