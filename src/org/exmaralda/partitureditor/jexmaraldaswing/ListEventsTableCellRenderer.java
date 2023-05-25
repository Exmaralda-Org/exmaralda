/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
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

    final DecimalFormat df = new DecimalFormat("0.00");
    public static Color STRIPED_COLOR = new java.awt.Color(245, 245, 245);

    public ListEventsTableCellRenderer() {
        super();
    }
    
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column==0 || column==1){
            if (column==0) {
                c.setForeground(Color.GREEN.darker().darker());
            } else if (column==1) {
                c.setForeground(Color.RED);
            }
            double dValue = (double)value;
            String timeString = TimeStringFormatter.formatSeconds(dValue, true, 2);
            ((JLabel)c).setText(timeString);
        } else if (column==2){
            c.setForeground(Color.BLUE);
            double dValue = (double)value;
            String formatted = df.format(dValue);
            ((JLabel)c).setText(formatted);
        }
        
        if (row%2==0 && !(isSelected)){
            c.setBackground(STRIPED_COLOR);
        } else {
            c.setBackground(Color.WHITE);
        }
        
        return c;
    }
    
}
