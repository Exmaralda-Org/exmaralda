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


/**
 *
 * @author thomas
 */
public class COMACorpusListCellRenderer extends javax.swing.DefaultListCellRenderer {
    
    JPanel result = new JPanel();
    JLabel nameLabel = new JLabel();
    JLabel filenameLabel = new JLabel();
    JLabel countLabel1 = new JLabel();
    JLabel countLabel2 = new JLabel();

    /** Creates a new instance of COMACorpusListCellRenderer */
    public COMACorpusListCellRenderer() {
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
         COMACorpusInterface corpus = (COMACorpusInterface)(value);

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
         nameLabel.setText(corpus.getCorpusName());

         if (corpus instanceof COMACorpus){
            nameLabel.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/devices/computer.png")));
         } else if (corpus instanceof COMARemoteCorpus){
            nameLabel.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/categories/applications-internet.png")));
         } else if (corpus instanceof COMADBCorpus){
            // TODO: this is the DB corpus case
            nameLabel.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/org/exmaralda/folker/tangoicons/othericons/database.png")));
         }
         
         String cp = corpus.getCorpusPath();
         filenameLabel.setToolTipText(cp);
         result.setToolTipText(cp);
         if (cp.length()>25){
             cp = cp.substring(0,7) + "..." + cp.substring(cp.length()-19);
         }
         filenameLabel.setForeground(java.awt.Color.black);
         filenameLabel.setOpaque(true);
         filenameLabel.setBackground(bc);
         filenameLabel.setText("   " + cp);      

         int count1 = corpus.getNumberOfTranscriptions();
         String countString1 = Integer.toString(count1) + " transcriptions";
         countLabel1.setForeground(java.awt.Color.DARK_GRAY);
         countLabel1.setOpaque(true);
         countLabel1.setBackground(bc);
         countLabel1.setText("   " + countString1);

         int count2 = corpus.getNumberOfSegmentChains();
         String countString2 = Integer.toString(count2) + " segment chains";
         countLabel2.setForeground(java.awt.Color.DARK_GRAY);
         countLabel2.setOpaque(true);
         countLabel2.setBackground(bc);
         countLabel2.setText("   " + countString2);

         
         result.add(nameLabel);
         result.add(filenameLabel);
         result.add(countLabel1);
         result.add(countLabel2);
         result.validate();
         
         return result;   
    }

    
}
