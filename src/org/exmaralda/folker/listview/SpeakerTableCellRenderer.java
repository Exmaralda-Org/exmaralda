/*
 * SpeakerTableCellRenderer.java
 *
 * Created on 7. Mai 2008, 17:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import java.awt.*;
import javax.swing.*;
import org.exmaralda.folker.data.Speaker;
import org.exmaralda.folker.utilities.Constants;

/**
 *
 * @author thomas
 */
public class SpeakerTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
    
    boolean markSpeakerChange = false;
    public boolean isForFOLKER = true;

    /** Creates a new instance of SpeakerTableCellRenderer */
    public SpeakerTableCellRenderer() {
    }
    
    /** Creates a new instance of SpeakerTableCellRenderer */
    public SpeakerTableCellRenderer(boolean msc) {
        markSpeakerChange = msc;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component retValue;
        
        String formatted = "";
        if (value!=null){
            Speaker s = (Speaker)value;
            formatted = s.getIdentifier();
        }
        retValue = super.getTableCellRendererComponent(table, formatted, isSelected, hasFocus, row, column);
        if (value!=null){
            Speaker s = (Speaker)value;                
            ((JLabel)(retValue)).setToolTipText(s.getName());
        }
        retValue.setFont(retValue.getFont().deriveFont(java.awt.Font.BOLD));
        if ((this.markSpeakerChange)){
            if (((row==0) || (table.getModel().getValueAt(row-1, column)!=value))){
                //((JLabel)retValue).setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, java.awt.Color.LIGHT_GRAY));
                retValue.setForeground(java.awt.Color.BLACK);
            } else {
                //retValue.setBackground(java.awt.Color.WHITE);
                retValue.setForeground(java.awt.Color.GRAY);
            }
        }
                
        //if (isForFOLKER){
            //String text = (String) table.getModel().getValueAt(row, column+1);
            //if (text!=null && text.startsWith("[[[") && text.endsWith("]]]")){
                //retValue.setBackground(Color.black);
            //    ((JLabel)(retValue)).setToolTipText("Maskierung");
            //    Icon maskIcon = new Constants().getIcon(Constants.ADD_MASK_ICON);
            //    ((JLabel)(retValue)).setIcon(maskIcon);
            //} else {
            //    ((JLabel)(retValue)).setIcon(null);
            //}
        //}
        
        ((JLabel)(retValue)).setVerticalAlignment(SwingConstants.TOP);
        return retValue;
    }
    
    
}
