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
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;

/**
 *
 * @author thomas
 */
class WordListTableCellRenderer implements javax.swing.table.TableCellRenderer {

    DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
    SegmentedTranscription transcription;
    
    public WordListTableCellRenderer(SegmentedTranscription st) {
        transcription = st;
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
        }
        return c;

    }

}
