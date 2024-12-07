/*
 * SearchHistoryListCellRenderer.java
 *
 * Created on 16. Januar 2007, 11:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search.swing;

import java.awt.Component;
import javax.swing.*;

/**
 *
 * @author thomas
 */
public class SearchHistoryListCellRenderer implements javax.swing.ListCellRenderer {
    
    /** Creates a new instance of SearchHistoryListCellRenderer */
    public SearchHistoryListCellRenderer() {
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, 
                                                    boolean isSelected, boolean cellHasFocus) {
        String[] historyItem = (String[])value;
        JPanel returnValue = new JPanel();
        DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
        Component c = dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        java.awt.Color bc = c.getBackground();

        returnValue.setOpaque(true);
        returnValue.setBackground(bc);
        
        returnValue.setLayout(new javax.swing.BoxLayout(returnValue, BoxLayout.X_AXIS));
        JLabel expressionLabel = new JLabel("  " + historyItem[0]);
        expressionLabel.setForeground(java.awt.Color.BLACK);
        JLabel typesLabel = new JLabel(historyItem[1] + "  ");
        typesLabel.setForeground(java.awt.Color.GRAY);
        returnValue.add(expressionLabel);
        returnValue.add(javax.swing.Box.createHorizontalGlue());
        returnValue.add(typesLabel);
        return returnValue;
    }
    
}
