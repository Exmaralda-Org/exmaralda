/*
 * StringUtilities.java
 *
 * Created on 6. Juni 2005, 09:04
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.exmaralda.zecke;

/**
 *
 * @author thomas
 */
public class StringUtilities {
    
    /** Creates a new instance of StringUtilities */
    public StringUtilities() {
    }
    
    static String checkHTML(String checkString){
        String result = new String();
        for (int pos=0; pos<checkString.length(); pos++){
            char symbol = checkString.charAt(pos);
            if (symbol=='<'){result+="&lt;";}
            else if (symbol=='>'){result+="&gt;";}
            else if (symbol=='&'){result+="&amp;";}
            else if (symbol=='"'){result+="&quot;";}
            else {result+=symbol;}
        }
        return result;
    }
    
    
}
