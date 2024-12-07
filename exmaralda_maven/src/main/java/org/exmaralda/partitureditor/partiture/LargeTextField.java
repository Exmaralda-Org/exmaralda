/*
 * LargeTextField.java
 *
 * Created on 22. Juli 2003, 17:58
 */

package org.exmaralda.partitureditor.partiture;

import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
/**
 * The class which the large text field at the top of the editor GUI
 * (between toolbars and partitur) is an instance of
 * @author  thomas
 */
public class LargeTextField extends javax.swing.JTextArea 
        implements org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardListener,
        CaretListener {
    
    javax.swing.AbstractAction enterPressedAction;
    PartitureTable table;
    
    /** Creates a new instance of LargeTextField
     * @param t */
    public LargeTextField(PartitureTable t) {
        table = t;
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        
        enterPressedAction =  new javax.swing.AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                enterPressed();
            }
        };
        getInputMap().put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0), "enterPressed");
        getActionMap().put("enterPressed", enterPressedAction);

        // added 14-08-2017
        //addCaretListener(this);
        
        setFont(new java.awt.Font("Arial Unicode MS",java.awt.Font.PLAIN, 10));
        //setBackground(java.awt.Color.lightGray);
    }
    
    @Override
    public void performUnicodeKeyboardAction(org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardEvent event) {
        this.replaceSelection(event.getText());
        this.getTopLevelAncestor().requestFocus();
        this.requestFocus();         
    }
    
    private void enterPressed(){
        //System.out.println("ENTER Pressed!");
        table.commitEdit(true);
    }

    @Override
    // added 14-08-2017
    public void caretUpdate(CaretEvent e) {
        int dot = e.getDot();
        int mark = e.getMark();
        if (dot==mark){
            JTextField textField = (JTextField) table.getEditingComponent();
            if (textField!=null){
                textField.setCaretPosition(dot);
            }
            
        }
    }
    
    
}

