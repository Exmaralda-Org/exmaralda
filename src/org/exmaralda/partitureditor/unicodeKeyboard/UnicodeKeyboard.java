/*
 * UnicodeKeyboard.java
 *
 * Created on 1. August 2003, 14:41
 */

package org.exmaralda.partitureditor.unicodeKeyboard;

import java.util.*;

/**
 *
 * @author  thomas
 */
public class UnicodeKeyboard extends Vector{
    
    public String name = "";
    
    /** Creates a new instance of UnicodeKeyboard */
    public UnicodeKeyboard() {
    }
    
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<unicode-keyboard name=\"" + name + "\">");
        for (int pos=0; pos<this.size(); pos++){
            UnicodeKey key = (UnicodeKey)(elementAt(pos));
            sb.append(key.toXML());
        }
        sb.append("</unicode-keyboard>");
        return sb.toString();
    }
    
    public String[] getKeyMap(){
        String[] result = new String[this.size()];
        for (int pos=0; pos<this.size(); pos++){
            UnicodeKey key = (UnicodeKey)(elementAt(pos));
            result[pos] = key.content;
        }
        return result;
    }
    
}

