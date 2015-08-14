/*
 * CorpusElementListCellRenderer.java
 *
 * Created on 7. Juni 2005, 13:07
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.exmaralda.zecke;

/**
 *
 * @author thomas
 */
public class CorpusElementListCellRenderer implements javax.swing.ListCellRenderer {
    
    /** Creates a new instance of CorpusElementListCellRenderer */
    public CorpusElementListCellRenderer() {
    }

    public java.awt.Component getListCellRendererComponent( javax.swing.JList jList, 
                                                            Object obj, 
                                                            int index, 
                                                            boolean isSelected, 
                                                            boolean cellHasFocus) {
         
        CorpusElement ce = (CorpusElement)obj;
        
         java.awt.Font arialUnicodeMS = new java.awt.Font("Arial Unicode MS", java.awt.Font.PLAIN, 14);
         java.awt.Color bc = java.awt.Color.white;
         if (isSelected) {bc = java.awt.Color.lightGray;}
                  
         javax.swing.JLabel result = new javax.swing.JLabel();
         result.setText(ce.transcriptionName);
         result.setToolTipText(ce.transcriptionPath);
         result.setBackground(bc);
         result.setOpaque(true);
         
         return result;
        
    }
    
}
