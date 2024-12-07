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
import org.jdom.Element;


/**
 *
 * @author thomas
 */
public class CatalogEntryListCellRenderer extends javax.swing.DefaultListCellRenderer {
    
    JPanel result = new JPanel();
    JLabel nameLabel = new JLabel();
    JLabel fullNameLabel = new JLabel();
    JLabel projectNameLabel = new JLabel();
    JLabel countLabel2 = new JLabel();

    /** Creates a new instance of COMACorpusListCellRenderer */
    public CatalogEntryListCellRenderer() {
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         Element resourceElement = (Element)(value);

         java.awt.Color bc = java.awt.Color.white;
         if (isSelected) {bc = java.awt.Color.lightGray;}
                  
         result.removeAll();
         result.setLayout(new javax.swing.BoxLayout(result, javax.swing.BoxLayout.Y_AXIS));
         result.setBackground(bc);
         result.setOpaque(true);
         
         String resourceName = resourceElement.getAttributeValue("name");
         nameLabel.setFont(nameLabel.getFont().deriveFont(java.awt.Font.BOLD).deriveFont(12.0f));
         nameLabel.setForeground(java.awt.Color.BLUE);
         nameLabel.setOpaque(true);
         nameLabel.setBackground(bc);
         nameLabel.setText(resourceName);

         
         String resourcePath = resourceElement.getChild("exmaralda-coma").getAttributeValue("url");
         result.setToolTipText(resourcePath);

         String fullName = resourceElement.getChildText("full-name");
         fullNameLabel.setForeground(java.awt.Color.darkGray);
         fullNameLabel.setOpaque(true);
         fullNameLabel.setBackground(bc);
         fullNameLabel.setText(fullName);

         String projectName = resourceElement.getChildText("project");
         projectNameLabel.setForeground(java.awt.Color.GRAY);
         projectNameLabel.setFont(projectNameLabel.getFont().deriveFont(9.0f));
         projectNameLabel.setOpaque(true);
         projectNameLabel.setBackground(bc);
         projectNameLabel.setText(projectName);

         String countString2 = "";
         countLabel2.setForeground(java.awt.Color.DARK_GRAY);
         countLabel2.setOpaque(true);
         countLabel2.setBackground(bc);
         countLabel2.setText("   " + countString2);

         
         result.add(nameLabel);
         result.add(fullNameLabel);
         result.add(projectNameLabel);
         result.add(countLabel2);
         result.validate();
         
         return result;   
    }

    
}
