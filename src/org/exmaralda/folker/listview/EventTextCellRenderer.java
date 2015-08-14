package org.exmaralda.folker.listview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.exmaralda.folker.data.Timepoint;
/*
 * TimeCellRenderer.java
 *
 * Created on 14. Maerz 2008, 16:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author thomas
 */
public class EventTextCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
    
    Font font = null;

    /** Creates a new instance of TimeCellRenderer */
    public EventTextCellRenderer() {
    }

    @Override
    public void setFont(Font f){
        super.setFont(f);
        font = f;
    }

    

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component retValue;
        
        retValue = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (font!=null){
            retValue.setFont(font);
        }
        

        return retValue;
    }

    
}
