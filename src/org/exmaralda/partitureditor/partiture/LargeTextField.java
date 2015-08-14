/*
 * LargeTextField.java
 *
 * Created on 22. Juli 2003, 17:58
 */

package org.exmaralda.partitureditor.partiture;

import javax.swing.KeyStroke;
/**
 * The class which the large text field at the top of the editor GUI
 * (between toolbars and partitur) is an instance of
 * @author  thomas
 */
public class LargeTextField extends javax.swing.JTextArea implements org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardListener{
    
    javax.swing.AbstractAction enterPressedAction;
    PartitureTable table;
    
    /** Creates a new instance of LargeTextField */
    public LargeTextField(PartitureTable t) {
        table = t;
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        
        enterPressedAction =  new javax.swing.AbstractAction(){
                                public void actionPerformed(java.awt.event.ActionEvent e){
                                    enterPressed();
                                }
                            };
        getInputMap().put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0), "enterPressed");
        getActionMap().put("enterPressed", enterPressedAction);

                                    
        setFont(new java.awt.Font("Arial Unicode MS",java.awt.Font.PLAIN, 10));
        //setBackground(java.awt.Color.lightGray);
    }
    
    public void performUnicodeKeyboardAction(org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardEvent event) {
        this.replaceSelection(event.getText());
        this.getTopLevelAncestor().requestFocus();
        this.requestFocus();         
    }
    
    private void enterPressed(){
        //System.out.println("ENTER Pressed!");
        table.commitEdit(true);
    }
    
    
}

