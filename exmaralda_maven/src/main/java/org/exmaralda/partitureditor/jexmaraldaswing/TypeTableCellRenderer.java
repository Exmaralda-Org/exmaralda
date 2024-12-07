/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author thomas
 */
class TypeTableCellRenderer implements javax.swing.table.TableCellRenderer {

    DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
    
    public TypeTableCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = dtcr.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        switch (column){
            case 0 :
                c.setForeground(Color.BLUE);
                break;
            case 1 :
                c.setForeground(Color.BLACK);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                break;
            case 2 :
                String sourceValue = (String) table.getModel().getValueAt(table.convertRowIndexToModel(row), 0);
                if (sourceValue.equals(value)){
                    c.setForeground(Color.LIGHT_GRAY);
                } else {
                    c.setForeground(Color.red);
                }
        }
        return c;

    }

}
