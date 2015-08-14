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
import javax.swing.JTable;

/**
 *
 * @author thomas
 */
public class EventTextCellEditor extends javax.swing.DefaultCellEditor {
    
    Font font = null;

    /** Creates a new instance of EventTextCellEditor */
    public EventTextCellEditor(String regexPattern) {
        super(new RegularExpressionTextField(regexPattern));
        setClickCountToStart(1);
    }

    public void setFont(Font f){
        font = f;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (font!=null){
            c.setFont(font);
        }
        return c;
    }



}
