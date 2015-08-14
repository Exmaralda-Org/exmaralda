package org.exmaralda.folker.listview;

import java.awt.Component;
import javax.swing.JTable;
import java.util.*;
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
public class CountingTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
    
    
    /** Creates a new instance of TimeCellRenderer */
    public CountingTableCellRenderer() {
    }
    

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component retValue;
        
        retValue = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        retValue.setBackground(java.awt.Color.LIGHT_GRAY);
        ((JLabel)(retValue)).setVerticalAlignment(SwingConstants.TOP);
        return retValue;
    }
    
}
