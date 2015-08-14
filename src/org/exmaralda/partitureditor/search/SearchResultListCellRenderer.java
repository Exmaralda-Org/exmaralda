/*
 * SearchResultListCellRenderer.java
 *
 * Created on 24. Juni 2003, 11:24
 */

package org.exmaralda.partitureditor.search;

import javax.swing.*;


/**
 *
 * @author  thomas
 */
public class SearchResultListCellRenderer implements ListCellRenderer {
    
    /** Creates a new instance of SearchResultListCellRenderer */
    public SearchResultListCellRenderer() {
    }
    
    public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         java.awt.Font arialUnicodeMS = new java.awt.Font("Arial Unicode MS", java.awt.Font.PLAIN, 10);
         java.awt.Color bc = java.awt.Color.white;
         if (isSelected) {bc = java.awt.Color.lightGray;}
         
         JPanel result = new JPanel();
         result.setLayout(new javax.swing.BoxLayout(result, javax.swing.BoxLayout.X_AXIS));
         result.setBackground(java.awt.Color.white);
         result.setOpaque(true);
         EventSearchResult esr = (EventSearchResult)value;
         //System.out.println(esr.toString());
         
         JLabel tierNameLabel = new JLabel();
         tierNameLabel.setFont(arialUnicodeMS);
         tierNameLabel.setForeground(java.awt.Color.blue);
         tierNameLabel.setOpaque(true);
         tierNameLabel.setBackground(bc);
         tierNameLabel.setText(" " + esr.tierName + "   ");         

         JLabel beforeLabel = new JLabel();
         beforeLabel.setFont(arialUnicodeMS);
         beforeLabel.setForeground(java.awt.Color.black);
         beforeLabel.setOpaque(true);
         beforeLabel.setBackground(bc);
         beforeLabel.setText(esr.event.getDescription().substring(0,esr.offset));

         JLabel searchLabel = new JLabel();
         searchLabel.setFont(arialUnicodeMS);
         searchLabel.setForeground(java.awt.Color.red);
         searchLabel.setOpaque(true);
         searchLabel.setBackground(bc);
         searchLabel.setText(esr.event.getDescription().substring(esr.offset, esr.offset+esr.length));
         searchLabel.setBorder(new javax.swing.border.LineBorder(java.awt.Color.darkGray));
         
         JLabel afterLabel = new JLabel();
         afterLabel.setFont(arialUnicodeMS);
         afterLabel.setForeground(java.awt.Color.black);
         afterLabel.setOpaque(true);
         afterLabel.setBackground(bc);
         afterLabel.setText(esr.event.getDescription().substring(esr.offset+esr.length));

         result.add(tierNameLabel);
         result.add(beforeLabel);
         result.add(searchLabel);
         result.add(afterLabel);
         
         return result;        
    }
    
}
