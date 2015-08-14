/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.wizard.folkercorpuswizard;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.exmaralda.folker.listview.CheckTableCellRenderer;

/**
 *
 * @author thomas
 */
public class FileSelectionTableCellRenderer extends CheckTableCellRenderer {

    DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (column==0){
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        } else if (column==1){
            JLabel l = (JLabel)(adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));
            File file = (File)value;
            l.setText(file.getName());
            l.setToolTipText(file.getAbsolutePath());
            boolean isChosen = ((Boolean)(table.getValueAt(row, 0))).booleanValue();
            if (!isChosen) {
                l.setForeground(Color.GRAY);
            }
            else {
                l.setForeground(Color.BLACK);
            }
            return l;
        }
        return adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

}
