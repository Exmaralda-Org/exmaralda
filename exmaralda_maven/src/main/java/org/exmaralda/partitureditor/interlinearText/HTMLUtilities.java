/*
 * HTMLUtilities.java
 *
 * Created on 4. Maerz 2002, 11:48
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class HTMLUtilities {

    /** Creates new HTMLUtilities */
    public HTMLUtilities() {
    }
    
    /** replaces characters critical in HTML in the checkString 
     * by the appropriate escape sequences */
    public static String toHTMLString(String checkString){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<checkString.length(); pos++){
            char symbol = checkString.charAt(pos);
            if (symbol==' '){sb.append("&nbsp;");}
            else if (symbol=='<'){sb.append("&lt;");}
            else if (symbol=='>'){sb.append("&gt;");}
            else if (symbol=='&'){sb.append("&amp;");}
            else if (symbol=='"'){sb.append("&quot;");}
            else {sb.append(symbol);}
        }
        return sb.toString();
    }   
    
    /** returns a HTML(CSS) representation of the specified color */
    static String toHTMLColorString(java.awt.Color color){
        String result;
        String r = Integer.toString(color.getRed());
        String g = Integer.toString(color.getGreen());
        String b = Integer.toString(color.getBlue());
        result="rgb("+ r + "," + g + "," + b + ")";
        return result;
    }
    
    static String toHTMLColorString(String colorName){
        String result;
        try{
            int r = Integer.parseInt(colorName.substring(2,4),16);
            int g = Integer.parseInt(colorName.substring(5,7),16);
            int b = Integer.parseInt(colorName.substring(8),16);
            result="rgb("+ r + "," + g + "," + b + ")";
        } catch (NumberFormatException nfe){
            result="rgb(0,0,0)";
        }
        return result;
    }
    
    /** returns a HTML(CSS) representation of the specified borders
     * in the given String, 't' stands for top, 'b' for bottom, 
     * 'l' for left and 'r' for right */
    static String toHTMLBorderString(String borders, String style){
        StringBuffer sb = new StringBuffer();

        if (borders.indexOf('l')>=0){
            //sb.append("border-left-color:" + toHTMLColorString(color) + ";");
            sb.append("border-left-style:" + style + ";");
            sb.append("border-left-width:1px;");
        }
        if (borders.indexOf('r')>=0){
            //sb.append("border-right-color:" + toHTMLColorString(color) + ";");
            sb.append("border-right-style:" + style + ";");
            sb.append("border-right-width:1px;");
        } 
        if (borders.indexOf('t')>=0){
            //sb.append("border-top-color:" + toHTMLColorString(color) + ";");
            sb.append("border-top-style:" + style + ";");
            sb.append("border-top-width:1px;");
        } 
        if (borders.indexOf('b')>=0){
            //sb.append("border-top-color:" + toHTMLColorString(color) + ";");
            sb.append("border-bottom-style:" + style + ";");
            sb.append("border-bottom-width:1px;");
        }                
        
        return sb.toString();
    }
    
    static String makeJavaScriptFunctions(HTMLParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("<script type=\"text/javascript\">"); 
        sb.append("function openOnClickWindow(anchor)");
        sb.append("{window.open(\"" + param.onClickTarget + "\" + \"#\" + anchor" + ",");
        sb.append("\"Info\"");
        sb.append(",\"width=300,height=100,menubar=no,toolbar=no,resizable=yes,scrollbars=yes,top=0,left=0,dependent=yes\"");
        sb.append(")");
        sb.append("}");
        sb.append("</script>");
        return sb.toString();
    }
    

}
