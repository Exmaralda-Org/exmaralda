/*
 * Output.java
 *
 * Created on 29. Juli 2002, 15:20
 */

package org.exmaralda.partitureditor.fsm;

/**
 *
 * @author  Thomas
 * @version 
 */
public class Output {

    String prefix;
    String suffix;
    boolean outputOriginal;

    /** Creates new Output */
    public Output(String p, String s, boolean oo) {
        prefix = p;
        suffix = s;
        outputOriginal = oo;
    }
    
    public String process(char c){
        return process(c, true);
    }

    public String process(char c, boolean escapeXML){
        StringBuffer sb = new StringBuffer();
        if (prefix!=null) {sb.append(prefix);}
        if (outputOriginal) {
            if (escapeXML){
                sb.append(toXMLString(c));
            } else {
                sb.append(String.valueOf(c));
            }
        }
        if (suffix!=null) {sb.append(suffix);}
        return sb.toString();
    }

    static String toXMLString(char c){
        if (c=='<'){return "&lt;";}
        else if (c=='>'){return "&gt;";}
        else if (c=='&'){return "&amp;";}
        else if (c=='"'){return "&quot;";}
        else {return String.valueOf(c);}
    }           
    

}
