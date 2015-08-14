package org.exmaralda.partitureditor.jexmaralda.segment.swing;

import java.awt.*;
import java.util.*;
/*
 * StringUtilities.java
 *
 * Created on 2. Februar 2001, 15:53
 */



class StringUtilities extends Object {

    // ********************************************
    // ********** CONSTRUCTORS  *******************
    // ********************************************

    /** Creates new StringUtilities */
    StringUtilities() {
    }   
    
    public static String stripXMLElements (String text){
        StringBuffer sb = new StringBuffer();
        boolean on = true;
        for (int pos=0; pos<text.length(); pos++){
            char c = text.charAt(pos);
            if (c=='<') on=false;
            if (c=='>') {on=true;}
            else if (on) {sb.append(c);}
        }
        return sb.toString();
    }
    
    
}