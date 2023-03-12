/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author thomas
 */
public class KWICTableCellRenderer extends DefaultTableCellRenderer {

    Color defaultForeground;
    
    public static Color STRIPED_COLOR = new java.awt.Color(245, 245, 245);
    public static Color ANNOTATION_COLOR = new java.awt.Color(0,128,64);
    
    public KWICTableCellRenderer(Color df) {
        super();
        defaultForeground = df;
    }

    
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Object o = table.getModel().getValueAt(row, 1);
        boolean thisOneIsSelected = ((Boolean)o);
        if (!thisOneIsSelected){
            c.setForeground(Color.GRAY);            
        } else {
            if (isSelected && defaultForeground.equals(ANNOTATION_COLOR)){
                c.setForeground(Color.WHITE);
            } else {
                c.setForeground(defaultForeground);            
            }
        }
        
        if (row%2==0 && !(isSelected)){
            c.setBackground(STRIPED_COLOR);
        }
        //setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));        
        return c;
    }
    
    
    
}
