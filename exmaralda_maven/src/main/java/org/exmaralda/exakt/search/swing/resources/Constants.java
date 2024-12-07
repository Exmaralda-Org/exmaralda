/*
 * Constants.java
 *
 * Created on 29. Januar 2007, 16:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search.swing.resources;

/**
 *
 * @author thomas
 */
public class Constants {
    
    
    public static String[][] STANDARD_REGEX =
    {
        {"0-1", "Once or not at all", "?"},
        {"0-n", "Zero or more times", "*"},
        {"1-n", "One or more times", "+"},
        {"OR", "One or more times", "+"},
        
    };
    
/*        {"Word boundary", "A word boundary", "."},
        {"Any", "Any character", "."},
        {"ABC", "Any ASCII upper case character (A,B,C,...,Z)", "[A-Z]"},
        {"abc", "Any ASCII lower case character (a,b,c,...,z)", "[a-z]"},
        {"AaBb", "Any ASCII case character (A,a,B,b,...Z,z)", "[A-Za-z]"},
        {"Space", "Any whitespace character (Space, Tab, Newline, etc.)", "\\s"},
        {"Digit", "Any digit (0,1,2,...,9)", "\\d"},
        {"Non-Digit", "Anything but a digit", "\\D"},*/
    /** Creates a new instance of Constants */
    public Constants() {
    }
    
}
