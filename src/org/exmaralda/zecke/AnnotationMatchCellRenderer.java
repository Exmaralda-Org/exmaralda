/*
 * AnnotationMatchCellRenderer.java
 *
 * Created on 6. Juni 2005, 14:51
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.exmaralda.zecke;

import javax.swing.*;

/**
 *
 * @author thomas
 */
public class AnnotationMatchCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
    
    /** Creates a new instance of AnnotationMatchCellRenderer */
    public AnnotationMatchCellRenderer() {        
    }
    
    public java.awt.Component getTableCellRendererComponent(JTable table,
                                                            Object v,
                                                            boolean isSelected,
                                                            boolean hasFocus,
                                                            int row,
                                                            int column) {
        String[] value = (String[])v;
        
        java.awt.Font arialUnicodeMS = new java.awt.Font("Arial Unicode MS", java.awt.Font.PLAIN, 10);
        java.awt.Color bc = java.awt.Color.white;
        if (isSelected) {bc = java.awt.Color.lightGray;}

        JPanel result = new JPanel();
        result.setLayout(new javax.swing.BoxLayout(result, javax.swing.BoxLayout.X_AXIS));
        result.setBackground(java.awt.Color.white);
        result.setOpaque(true);
        

        JLabel leftContextLabel = new JLabel();
        leftContextLabel.setFont(arialUnicodeMS);
        leftContextLabel.setForeground(java.awt.Color.black);
        leftContextLabel.setOpaque(true);
        leftContextLabel.setBackground(bc);
        leftContextLabel.setText(value[0]);         

        JLabel matchLabel = new JLabel();
        matchLabel.setFont(arialUnicodeMS);
        matchLabel.setForeground(java.awt.Color.blue);
        matchLabel.setOpaque(true);
        matchLabel.setBackground(bc);
        matchLabel.setText(value[1]);
        //matchLabel.setBorder(new javax.swing.border.LineBorder(java.awt.Color.darkGray));

        JLabel rightContextLabel = new JLabel();
        rightContextLabel.setFont(arialUnicodeMS);
        rightContextLabel.setForeground(java.awt.Color.black);
        rightContextLabel.setOpaque(true);
        rightContextLabel.setBackground(bc);
        rightContextLabel.setText(value[2]);
        
        result.add(leftContextLabel);
        result.add(matchLabel);
        result.add(rightContextLabel);
        
        return result;
    }
    
    
    
}
