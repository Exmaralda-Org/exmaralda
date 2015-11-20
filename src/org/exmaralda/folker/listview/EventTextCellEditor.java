/*
 * EventTextCellEditor.java
 *
 * Created on 7. Mai 2008, 17:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 *
 * @author thomas
 */
public class EventTextCellEditor extends javax.swing.DefaultCellEditor {
    
    Font font = null;
    String[][] keyBindings = null;

    /** Creates a new instance of EventTextCellEditor
     * @param regexPattern */
    public EventTextCellEditor(String regexPattern) {
        super(new RegularExpressionTextField(regexPattern));
        setClickCountToStart(1);
    }

    public void setFont(Font f){
        font = f;
    }
    
    public void setKeyBindings(String[][] kb){
        keyBindings = kb;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (font!=null){
            c.setFont(font);
        }
        bindKeys(c);
        return c;
    }
    
    void bindKeys(Component c){
        if (keyBindings==null) return;
        final JTextField textField = (JTextField)c;
        for (String[] keyBinding : keyBindings){
            String keyStrokeAndKey = keyBinding[0];
            final String textToInsert = keyBinding[1];
            KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeAndKey);
            textField.getInputMap().put(keyStroke, keyStrokeAndKey);
            textField.getActionMap().put(keyStrokeAndKey, new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    textField.replaceSelection(textToInsert);
                }                
            });        
        }
        
    }



}
