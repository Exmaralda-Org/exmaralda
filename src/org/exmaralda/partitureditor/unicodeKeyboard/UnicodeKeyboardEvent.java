/*
 * UnicodeKeyboardEvent.java
 *
 * Created on 15. November 2001, 11:50
 */

package org.exmaralda.partitureditor.unicodeKeyboard;

/**
 *
 * @author  Thomas
 * @version 
 */
public class UnicodeKeyboardEvent extends java.awt.AWTEvent {

    private String text;
    
    /** Creates new UnicodeKeyboardEvent */
    public UnicodeKeyboardEvent(String t) {
        super(new Object(),0);
        text = t;
    }
    
    public String getText(){
        return text;
    }

}