/*
 * UnicodeTextField.java
 *
 * Created on 13. Juni 2003, 13:48
 */

package org.exmaralda.partitureditor.unicodeKeyboard;

/**
 *
 * @author  thomas
 */
public class UnicodeTextField extends javax.swing.JTextField implements UnicodeKeyboardListener {
    
    /** Creates a new instance of UnicodeTextField */
    public UnicodeTextField() {
        super();
        this.setFont(new java.awt.Font("Arial Unicode MS", java.awt.Font.PLAIN , 12));
    }
    
    public void performUnicodeKeyboardAction(org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardEvent event) {
        this.replaceSelection(event.getText());
        this.requestFocus(); 
    }

    public void requestFocus() {
        super.requestFocus();
    }
    
}
