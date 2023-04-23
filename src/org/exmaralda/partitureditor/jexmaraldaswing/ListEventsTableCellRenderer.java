/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.exmaralda.folker.utilities.TimeStringFormatter;

// new for #382

/**
 *
 * @author bernd
 */
public class ListEventsTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column==0 || column==1){
            c.setForeground(Color.BLUE);
            double dValue = (double)value;
            String timeString = TimeStringFormatter.formatSeconds(dValue, true, 2);
            ((JLabel)c).setText(timeString);
        }
        return c;
    }
    
}
