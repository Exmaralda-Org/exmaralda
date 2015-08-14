/*
 * CheckTableCellRenderer.java
 *
 * Created on 17. April 2008, 09:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 *
 * @author thomas
 */
public class CheckTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
    
    boolean active = true;

    java.awt.Color TRUE_COLOR = java.awt.Color.GREEN;
    String TRUE_SYMBOL = "\u2713";
    java.awt.Color FALSE_COLOR = java.awt.Color.RED;
    String FALSE_SYMBOL = "\u2718";
    java.awt.Color INACTIVE_COLOR = java.awt.Color.GRAY;
    String INACTIVE_SYMBOL = "-";
    
    /** Creates a new instance of CheckTableCellRenderer */
    public CheckTableCellRenderer() {
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component retValue;
        Object adaptedValue = value;
        java.awt.Color color = this.getForeground();
        if (value instanceof Boolean){
            if (!active){
                adaptedValue = INACTIVE_SYMBOL;
                color = INACTIVE_COLOR;
            } else {
                Boolean b = (Boolean)value;
                if (b.booleanValue()){
                    adaptedValue = TRUE_SYMBOL;
                    color = TRUE_COLOR;
                } else {
                    adaptedValue = FALSE_SYMBOL;
                    color = FALSE_COLOR;
                }
            }
        }
        
        retValue = super.getTableCellRendererComponent(table, adaptedValue, isSelected, hasFocus, row, column);
        retValue.setFont(new java.awt.Font("Arial Unicode MS", java.awt.Font.BOLD, 16));
        retValue.setForeground(color);
        ((JLabel)(retValue)).setHorizontalTextPosition(SwingConstants.CENTER);
        ((JLabel)(retValue)).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)(retValue)).setVerticalAlignment(SwingConstants.TOP);
        return retValue;
    }
    
}
