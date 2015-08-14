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
import org.exmaralda.exakt.exmaraldaSearch.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;
import org.exmaralda.exakt.tokenlist.AbstractTokenList;


/**
 *
 * @author thomas
 */
public class WordListListCellRenderer extends javax.swing.DefaultListCellRenderer {
    
    JPanel result = new JPanel();
    JLabel nameLabel = new JLabel();
    JLabel countLabel1 = new JLabel();

    /** Creates a new instance of COMACorpusListCellRenderer */
    public WordListListCellRenderer() {
    }

    /*public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component retValue;        
        COMACorpus corpus = (COMACorpus)(value);
        String corpusDescription = corpus.getCorpusName();        
        retValue = super.getListCellRendererComponent(list, corpusDescription, index, isSelected, cellHasFocus);
        ((JLabel)retValue).setToolTipText(corpus.getCorpusPath());
        return retValue;
    }*/
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         AbstractTokenList tokenList = (AbstractTokenList)(value);

         java.awt.Color bc = java.awt.Color.white;
         if (isSelected) {bc = java.awt.Color.lightGray;}
                  
         result.removeAll();
         result.setLayout(new javax.swing.BoxLayout(result, javax.swing.BoxLayout.Y_AXIS));
         result.setBackground(bc);
         result.setOpaque(true);
         
         nameLabel.setFont(nameLabel.getFont().deriveFont(java.awt.Font.BOLD));
         nameLabel.setForeground(java.awt.Color.BLUE);
         nameLabel.setOpaque(true);
         nameLabel.setBackground(bc);
         nameLabel.setText(tokenList.getName());

         
         int count1 = tokenList.getNumberOfTokens();
         String countString1 = Integer.toString(count1) + " types";
         int count2 = tokenList.getTotalTokenCount();
         String countString2 = Integer.toString(count2) + " tokens";
         countLabel1.setForeground(java.awt.Color.DARK_GRAY);
         countLabel1.setOpaque(true);
         countLabel1.setBackground(bc);
         countLabel1.setText("   " + countString1 + "   " + countString2);

         result.add(nameLabel);
         result.add(countLabel1);
         result.validate();
         
         return result;   
    }

    
}
