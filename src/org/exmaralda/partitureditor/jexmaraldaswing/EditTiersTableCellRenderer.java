/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Thomas_Schmidt
 */
public class EditTiersTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
        if (value==null && (!isSelected)){
            component.setBackground(Color.LIGHT_GRAY);
        } else {
            component.setBackground(new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).getBackground());
        } 
        if ("### ERROR".equals(value)){
            component.setForeground(Color.red);
        } else {
            component.setForeground(new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).getForeground());            
        }
        if (table.getValueAt(row, 3).equals("t")){
            component.setFont(component.getFont().deriveFont(Font.BOLD));
        }
        if (column==3){
            JLabel label = (JLabel)component;
            if ("t".equals(value)){
                label.setText("T(ranscription)");
            } else if ("a".equals(value)){
                label.setText("A(nnotation)");
            } else if ("d".equals(value)){
                label.setText("D(escription)");                
            }
            
        }
        return component;
    }
    
    
    
}
