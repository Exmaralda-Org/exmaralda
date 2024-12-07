/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.common.helpers;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author thomas
 */
public class TimeTextField extends JTextField implements DocumentListener {
    
    public static String CHECK_REGEX = "-?((\\d{0,3}:)?([012345][0123456789]:)?([012345][0123456789])(\\.\\d+)|\\d+(\\.\\d+)?)";

    public TimeTextField(String text) {
        super(text);
        getDocument().addDocumentListener(this);
    }
    
    void check(){
        if (getText().matches(CHECK_REGEX)){
            setForeground(Color.BLACK);
            setToolTipText("Time format OK.");
        } else {
            setForeground(Color.RED);
            setToolTipText("Time format error.");
        }
    }
    

    public void insertUpdate(DocumentEvent e) {
        check();
    }

    public void removeUpdate(DocumentEvent e) {
        check();
    }

    public void changedUpdate(DocumentEvent e) {
        check();
    }
    
    
    
}
