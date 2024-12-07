/*
 * SAXUtilities.java
 *
 * Created on 1. August 2003, 14:47
 */

package org.exmaralda.partitureditor.unicodeKeyboard;

/**
 *
 * @author  thomas
 */
public class SAXUtilities {
    
    static final int UNICODE_KEYBOARD = 0;
    static final int KEY = 1;
    static final int CONTENT = 2;
    static final int DESCRIPTION = 3;
    static final int FONT = 4;
    static final int UNKNOWN = 100;
    
    
    /** Creates a new instance of SAXUtilities */
    public SAXUtilities() {
    }
    
    static int getIDForElementName(String elementName){
        if (elementName.equals("unicode-keyboard")){return UNICODE_KEYBOARD;}
        else if (elementName.equals("key")){return KEY;}
        else if (elementName.equals("content")){return CONTENT;}
        else if (elementName.equals("description")){return DESCRIPTION;}
        else if (elementName.equals("font")){return FONT;}
        else return UNKNOWN;
    }    
    
}
