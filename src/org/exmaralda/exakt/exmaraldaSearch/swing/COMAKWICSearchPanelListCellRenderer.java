/*
 * COMACorpusListCellRenderer.java
 *
 * Created on 17. Januar 2007, 15:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.awt.Component;
import javax.swing.*;
import org.exmaralda.exakt.search.SearchResultList;

/**
 *
 * @author thomas
 */
public class COMAKWICSearchPanelListCellRenderer extends javax.swing.DefaultListCellRenderer {
    
    JPanel result = new JPanel();
    JLabel nameLabel = new JLabel();
    JLabel corpusLabel = new JLabel();
    JLabel countLabel1 = new JLabel();
    JLabel countLabel2 = new JLabel();

    /** Creates a new instance of COMACorpusListCellRenderer */
    public COMAKWICSearchPanelListCellRenderer() {
    }

    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         COMAKWICSearchPanel panel = (COMAKWICSearchPanel)(value);
         SearchResultList srl = panel.getSearchResultList();
         
         java.awt.Color bc = java.awt.Color.white;
         if (isSelected) {bc = java.awt.Color.lightGray;}
                  
         result.removeAll();
         result.setLayout(new javax.swing.BoxLayout(result, javax.swing.BoxLayout.Y_AXIS));
         result.setBackground(bc);
         result.setOpaque(true);
         
         String name = "New concordance";
         if (srl.size()>0){
             name = "";
             boolean start = true;
             for (Object o : srl.getTypes()){
                 String type = (String)o;
                 if (name.length()<30){
                     if (!start){
                         name+=", ";
                     }
                     name+=type;
                     start=false;                     
                 }
             }
         }
         
         nameLabel.setFont(nameLabel.getFont().deriveFont(java.awt.Font.BOLD));
         nameLabel.setForeground(new java.awt.Color(0,128,64));
         nameLabel.setOpaque(true);
         nameLabel.setBackground(bc);
         nameLabel.setText(name);   
         
         corpusLabel.setForeground(java.awt.Color.BLUE);
         corpusLabel.setOpaque(true);
         corpusLabel.setBackground(bc);
         corpusLabel.setText("   "  + panel.getCorpus().getCorpusName());      

         int count1 = srl.size();
         String countString1 = Integer.toString(count1) + " tokens";
         countLabel1.setForeground(java.awt.Color.DARK_GRAY);
         countLabel1.setOpaque(true);
         countLabel1.setBackground(bc);
         countLabel1.setText("   " + countString1);

         int count2 = srl.getTypes().size();
         String countString2 = Integer.toString(count2) + " types";
         countLabel2.setForeground(java.awt.Color.DARK_GRAY);
         countLabel2.setOpaque(true);
         countLabel2.setBackground(bc);
         countLabel2.setText("   " + countString2);

         
         result.add(nameLabel);
         result.add(corpusLabel);
         result.add(countLabel1);
         result.add(countLabel2);
         result.validate();
         
         return result;   
    }

    
}
