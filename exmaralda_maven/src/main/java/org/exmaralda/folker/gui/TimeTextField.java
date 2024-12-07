/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.folker.gui;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.exmaralda.folker.utilities.TimeStringFormatter;

/**
 *
 * @author thomas
 */
public class TimeTextField extends JTextField implements DocumentListener {

    public TimeTextField() {
        getDocument().addDocumentListener(this);
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

    private void check() {
        try {
            TimeStringFormatter.parseString(getText());
            setForeground(Color.BLACK);
        } catch (Exception ex) {
            setForeground(Color.RED);
            //ex.printStackTrace();
        }
    }




}
