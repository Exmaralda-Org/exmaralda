/*
 * Test.java
 *
 * Created on 1. August 2003, 15:28
 */

package org.exmaralda.partitureditor.unicodeKeyboard;

/**
 *
 * @author  thomas
 */
public class Test {
    
    /** Creates a new instance of Test */
    public Test() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Throwable {
        UnicodeKeyboardSaxReader reader = new UnicodeKeyboardSaxReader();
        UnicodeKeyboard keyboard = reader.readFromFile("D:\\Java\\de\\uni-hamburg\\rrz\\jexmaralda\\unicodeKeyboard\\Charsets\\HIAT.xml");
        System.out.println(keyboard.toXML());
        
    }
    
}
