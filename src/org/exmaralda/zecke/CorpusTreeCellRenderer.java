/*
 * CorpusTreeCellRenderer.java
 *
 * Created on 10. Juni 2004, 16:48
 */

package org.exmaralda.zecke;

import javax.swing.tree.*;
/**
 *
 * @author  thomas
 */
public class CorpusTreeCellRenderer implements TreeCellRenderer {
    
    /** Creates a new instance of CorpusTreeCellRenderer */
    public CorpusTreeCellRenderer() {
    }
    
    public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        javax.swing.JCheckBox component = new javax.swing.JCheckBox(){
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                componentActionPerformed(evt);
            }        
        };
        component.setText(value.toString());
        component.setEnabled(true);
        
        java.awt.Color backgroundColor;
        java.awt.Color fontColor;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)(value);
        if (node.getChildCount() > 0){
            fontColor = java.awt.Color.RED;
        } else {
            fontColor = java.awt.Color.BLACK;
        }
        if (selected){
            backgroundColor = java.awt.Color.LIGHT_GRAY;
        } else {
            backgroundColor = java.awt.Color.WHITE;
        }
        component.setForeground(fontColor);
        component.setBackground(backgroundColor);
        return component;
    }

    void componentActionPerformed(java.awt.event.ActionEvent evt){
        System.out.println("Angeklickelt");
    }
    
}
