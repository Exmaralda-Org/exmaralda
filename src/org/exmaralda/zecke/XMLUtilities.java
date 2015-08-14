/*
 * XMLUtilities.java
 *
 * Created on 25. Juli 2002, 11:17
 */

package org.exmaralda.zecke;

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
        if (checkString == null) return new String();
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

}
