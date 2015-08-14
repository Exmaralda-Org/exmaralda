/*
 * SAXUtilities.java
 *
 * Created on 29. Juli 2002, 15:48
 */

package org.exmaralda.partitureditor.fsm;

/**
 *
 * @author  Thomas
 * @version 
 */
public class SAXUtilities {

    static final int FSM = 0;
    static final int CHAR_SET = 1;
    static final int CHAR = 2;
    static final int START_STATE = 3;
    static final int END_STATE = 4;
    static final int TRANSITIONS = 5;
    static final int TRANSITION = 6;
    static final int INPUT_CHAR = 7;
    static final int INPUT_CHAR_SET = 8;
    static final int INPUT_OTHER = 9;
    static final int INPUT_END = 10;
    static final int TARGET = 11;
    static final int OUTPUT = 12;
    static final int PREFIX = 13;
    static final int SUFFIX = 14;
    static final int FORBIDDEN = 15;
    static final int UNKNOWN = 100;
    
    /** Creates new SAXUtilities */
    public SAXUtilities() {
    }
    
    static int getIDForElementName(String elementName){
        if (elementName.equals("fsm")){return FSM;}
        else if (elementName.equals("char-set")){return CHAR_SET;}
        else if (elementName.equals("char")){return CHAR;}
        else if (elementName.equals("start-state")){return START_STATE;}
        else if (elementName.equals("end-state")){return END_STATE;}
        else if (elementName.equals("transitions")){return TRANSITIONS;}
        else if (elementName.equals("transition")){return TRANSITION;}
        else if (elementName.equals("input-char")){return INPUT_CHAR;}
        else if (elementName.equals("input-char-set")){return INPUT_CHAR_SET;}
        else if (elementName.equals("input-other")){return INPUT_OTHER;}
        else if (elementName.equals("input-end")){return INPUT_END;}
        else if (elementName.equals("target")){return TARGET;}
        else if (elementName.equals("output")){return OUTPUT;}
        else if (elementName.equals("prefix")){return PREFIX;}
        else if (elementName.equals("suffix")){return SUFFIX;}
        else if (elementName.equals("forbidden")){return FORBIDDEN;}
        else return UNKNOWN;
    }

}
