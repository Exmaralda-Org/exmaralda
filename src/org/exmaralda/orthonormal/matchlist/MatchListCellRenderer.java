/*
 * COMACorpusListCellRenderer.java
 *
 * Created on 17. Januar 2007, 15:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.matchlist;

import java.awt.Component;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;


/**
 *
 * @author thomas
 */
public class MatchListCellRenderer extends javax.swing.DefaultListCellRenderer {
    
    JPanel result = new JPanel();
    JLabel leftContextLabel = new JLabel();
    JLabel matchLabel = new JLabel();
    JLabel rightContextLabel = new JLabel();

    /** Creates a new instance of COMACorpusListCellRenderer */
    public MatchListCellRenderer() {
    }

    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         Element element = (Element)value;

         java.awt.Color bc = java.awt.Color.white;
         if (isSelected) {bc = java.awt.Color.lightGray;}
                  
         result.removeAll();
         //transcriptionPanel.removeAll();;
         result.setLayout(new java.awt.FlowLayout());
         //result.setAlignmentX(Component.LEFT_ALIGNMENT);
         //transcriptionPanel.setLayout(new javax.swing.BoxLayout(transcriptionPanel, javax.swing.BoxLayout.X_AXIS));                          
         //transcriptionPanel.setMaximumSize(new Dimension(200,transcriptionPanel.getMaximumSize().height));
         result.setBackground(bc);
         result.setOpaque(true);
         
         boolean isDone = "yes".equals(element.getAttributeValue("done"));
         
         matchLabel.setFont(matchLabel.getFont().deriveFont(java.awt.Font.BOLD).deriveFont(10.0f));
         //matchLabel.setForeground(java.awt.Color.BLUE);
         matchLabel.setOpaque(true);
         matchLabel.setBackground(bc);
         matchLabel.setText(getMatchText(element));         
         if (isDone){
             matchLabel.setForeground(java.awt.Color.GRAY);
         } else {
             matchLabel.setForeground(java.awt.Color.BLACK);             
         }
         

         //leftContextLabel.setForeground(java.awt.Color.DARK_GRAY);
         leftContextLabel.setOpaque(true);
         leftContextLabel.setBackground(bc);
         leftContextLabel.setFont(leftContextLabel.getFont().deriveFont(10.0f));
         leftContextLabel.setText(getLeftContext(element));
         if (isDone){
             leftContextLabel.setForeground(java.awt.Color.GRAY);
         } else {
             leftContextLabel.setForeground(java.awt.Color.BLACK);             
         }

         //rightContextLabel.setForeground(java.awt.Color.DARK_GRAY);
         rightContextLabel.setOpaque(true);
         rightContextLabel.setBackground(bc);
         rightContextLabel.setFont(rightContextLabel.getFont().deriveFont(10.0f));
         rightContextLabel.setText(getRightContext(element));
         if (isDone){
             rightContextLabel.setForeground(java.awt.Color.GRAY);
         } else {
             rightContextLabel.setForeground(java.awt.Color.BLACK);             
         }
         
         
         result.add(leftContextLabel);
         result.add(matchLabel);
         result.add(rightContextLabel);
         
         result.setToolTipText(element.getAttributeValue("transcript"));
         //transcriptionPanel.add(idLabel);
         //result.add(textLabel);
         //result.add(transcriptionPanel);
         //result.add(timeLabel);
         result.validate();
         
         return result;   
    }

    private String getMatchText(Element element) {
        try {
            String matchID = element.getAttributeValue("match");
            String result = "";
            Element w = (Element) XPath.selectSingleNode(element, "descendant::w[@id='" + matchID + "']");
            result+=WordUtilities.getWordText(w) + " ";
            return result;
        } catch (JDOMException ex) {
            Logger.getLogger(MatchListCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR" + ex.getLocalizedMessage();
        }
    }
    
    private String getLeftContext(Element element){
        try {
            String matchID = element.getAttributeValue("match");
            String result = "";
            List l = XPath.selectNodes(element, "descendant::w");
            for (Object o : l){
                Element w = (Element)o;
                if (w.getAttributeValue("id").equals(matchID)) break;
                result+=WordUtilities.getWordText(w) + " ";
            }
            if (result.length()>30){
                result = "..." + result.substring(result.length()-30);
            }
            return result;
        } catch (JDOMException ex) {
            Logger.getLogger(MatchListCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR" + ex.getLocalizedMessage();
        }
        
    }

    private String getRightContext(Element element){
        try {
            String matchID = element.getAttributeValue("match");
            String result = "";
            List l = XPath.selectNodes(element, "descendant::w");
            boolean in = false;
            for (Object o : l){
                Element w = (Element)o;
                if (in){
                    result+=WordUtilities.getWordText(w) + " ";
                }
                in = in || w.getAttributeValue("id").equals(matchID);
            }
            if (result.length()>30){
                result = result.substring(0,30) + "...";
            }
            return result;
        } catch (JDOMException ex) {
            Logger.getLogger(MatchListCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR" + ex.getLocalizedMessage();
        }
        
    }
    
}
