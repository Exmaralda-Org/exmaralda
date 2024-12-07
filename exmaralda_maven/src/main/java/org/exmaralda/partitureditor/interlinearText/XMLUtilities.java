/*
 * XMLUtilities.java
 *
 * Created on 25. Juli 2002, 11:17
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class XMLUtilities {

    /** Creates new XMLUtilities */
    public XMLUtilities() {
    }
    
    /** replaces characters critical in HTML in the checkString 
     * by the appropriate escape sequences */
    public static String toXMLString(String checkString){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<checkString.length(); pos++){
            char symbol = checkString.charAt(pos);
            if (symbol=='<'){sb.append("&lt;");}
            else if (symbol=='>'){sb.append("&gt;");}
            else if (symbol=='&'){sb.append("&amp;");}
            else if (symbol=='"'){sb.append("&quot;");}
            else if (symbol=='\u0003') {
                /* do nothing - not allowed in XML */
                System.out.println("XML String conversion: End of text symbol (#0003) encountered");
            }
            else if (symbol=='\u0005') {
                /* do nothing - not allowed in XML */
                System.out.println("XML String conversion: Enquiry symbol (#0005) encountered");
            }
            else {sb.append(symbol);}
        }
        return sb.toString();
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
    
    static String makeXMLComment(String c){
        return "<!--" + c + "-->";
    }
    
    

}
