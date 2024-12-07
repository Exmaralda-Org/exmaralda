/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.coma.render;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author thomas
 */
public class CategoryPlusTypesTableCellRenderer implements javax.swing.table.TableCellRenderer {

    DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
    
    public CategoryPlusTypesTableCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = dtcr.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        switch (column){
            case 0 :
                c.setForeground(Color.BLUE);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                break;
            case 1 :
                c.setForeground(Color.BLACK);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                break;
            case 2 :
                break;
        }
        return c;

    }

}
