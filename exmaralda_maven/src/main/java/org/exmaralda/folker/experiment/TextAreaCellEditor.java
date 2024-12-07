/*
 * TextAreaEditor.java
 *
 * Created on 14. Maerz 2008, 15:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

// Code from http://www.roseindia.net/javatutorials/JTable_in_JDK.shtml


package org.exmaralda.folker.experiment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TextAreaCellEditor extends DefaultCellEditor implements java.awt.event.KeyListener {

    public TextAreaCellEditor() {
    super(new JTextField());

    final JTextPane textArea = new JTextPane();
    /*final JTextArea textArea = new JTextArea();
    textArea.setWrapStyleWord(true);
    textArea.setLineWrap(true);   */   
    textArea.addKeyListener(this);

    
    setClickCountToStart(1);

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setBorder(null);
    editorComponent = scrollPane;

    delegate = new DefaultCellEditor.EditorDelegate() {
      public void setValue(Object value) {
        textArea.setText((value != null) ? value.toString() : "");
      }
      public Object getCellEditorValue() {
        return textArea.getText();
      }
    };
    
  }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        // catch enter key and make it validate the cell editing
        if (e.getKeyCode() == KeyEvent.VK_ENTER){
            delegate.stopCellEditing();
            e.consume();
        }
    }
}
