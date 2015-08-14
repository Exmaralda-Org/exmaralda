/*
 * COMACorpusListCellRenderer.java
 *
 * Created on 17. Januar 2007, 15:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.matchlist;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;
import org.exmaralda.exakt.exmaraldaSearch.*;
import org.exmaralda.folker.utilities.Constants;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.jdom.Element;


/**
 *
 * @author thomas
 */
public class MatchListCellRenderer extends javax.swing.DefaultListCellRenderer {
    
    JPanel result = new JPanel();
    JLabel transcriptionLabel = new JLabel();
    JLabel textLabel = new JLabel();
    JLabel timeLabel = new JLabel();
    JLabel idLabel = new JLabel();
    //JPanel transcriptionPanel = new JPanel();

    /** Creates a new instance of COMACorpusListCellRenderer */
    public MatchListCellRenderer() {
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
         Element element = (Element)value;

         java.awt.Color bc = java.awt.Color.white;
         if (isSelected) {bc = java.awt.Color.lightGray;}
                  
         result.removeAll();
         //transcriptionPanel.removeAll();;
         result.setLayout(new javax.swing.BoxLayout(result, javax.swing.BoxLayout.Y_AXIS));
         result.setAlignmentX(Component.LEFT_ALIGNMENT);
         //transcriptionPanel.setLayout(new javax.swing.BoxLayout(transcriptionPanel, javax.swing.BoxLayout.X_AXIS));                          
         //transcriptionPanel.setMaximumSize(new Dimension(200,transcriptionPanel.getMaximumSize().height));
         result.setBackground(bc);
         result.setOpaque(true);
         
         
         transcriptionLabel.setFont(transcriptionLabel.getFont().deriveFont(java.awt.Font.BOLD));
         transcriptionLabel.setForeground(java.awt.Color.BLUE);
         transcriptionLabel.setOpaque(true);
         transcriptionLabel.setBackground(bc);
         if ("yes".equals(element.getAttributeValue("done"))){
             transcriptionLabel.setText("\u2713 " + element.getAttributeValue("tra"));
             
         } else {
            transcriptionLabel.setText(element.getAttributeValue("tra"));
         }
         

         textLabel.setForeground(java.awt.Color.black);
         textLabel.setOpaque(true);
         textLabel.setBackground(bc);
         textLabel.setFont(textLabel.getFont().deriveFont(10.0f));
         textLabel.setText(element.getAttributeValue("speaker") + ": " + element.getText());

         timeLabel.setForeground(java.awt.Color.DARK_GRAY);
         timeLabel.setOpaque(true);
         timeLabel.setBackground(bc);
         timeLabel.setFont(timeLabel.getFont().deriveFont(java.awt.Font.BOLD).deriveFont(10.0f));
         timeLabel.setText(TimeStringFormatter.formatMiliseconds(Double.parseDouble(element.getAttributeValue("time"))*1000.0,2));
         

         idLabel.setForeground(java.awt.Color.DARK_GRAY);
         idLabel.setOpaque(true);
         idLabel.setBackground(bc);
         idLabel.setFont(idLabel.getFont().deriveFont(java.awt.Font.BOLD).deriveFont(10.0f));
         idLabel.setText(element.getAttributeValue("speaker"));

         
         
         result.add(transcriptionLabel);
         //transcriptionPanel.add(idLabel);
         result.add(textLabel);
         //result.add(transcriptionPanel);
         result.add(timeLabel);
         result.validate();
         
         return result;   
    }

    
}
