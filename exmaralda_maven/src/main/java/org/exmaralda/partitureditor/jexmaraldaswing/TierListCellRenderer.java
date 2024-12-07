/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;

/**
 *
 * @author bernd
 */
public class TierListCellRenderer implements ListCellRenderer {

    DefaultListCellRenderer dflcr = new DefaultListCellRenderer();
    
    BasicTranscription transcription;
    
    public TierListCellRenderer(BasicTranscription bt) {
        transcription = bt;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Tier tier = (Tier)value;
        JLabel resultLabel = (JLabel) dflcr.getListCellRendererComponent(list, tier.getDisplayName(), index, isSelected, cellHasFocus);
        
        String displayName = tier.getDisplayName();
        if (displayName==null){
            displayName = tier.getDescription(transcription.getHead().getSpeakertable());
        }
        
        String speakerLabel = "---";
        if (tier.getSpeaker()!=null){
            try {
                speakerLabel = transcription.getHead().getSpeakertable().getSpeakerWithID(tier.getSpeaker()).getAbbreviation();
            } catch (JexmaraldaException ex) {
                Logger.getLogger(TierListCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                        
        String html = "<html>";
        if (tier.getType().equals("t")) html+="<b>";
        html+="<span style=\"font-size:14pt;";
        html+="\">";
        html+=displayName;
        String color = "gray";
        if (isSelected) color="lightGray";
        html+="</span>";
        if (tier.getType().equals("t")) html+="</b>";        
        html+="<br/><span style=\"color:" + color + "; font-size:10pt;\">";
        html+=speakerLabel + " [" + tier.getSpeaker() + "]" + " / " + tier.getCategory() + " [" + tier.getID() + "]";
        html+="</span></html>";
        
        Border outerBorder = BorderFactory.createMatteBorder(1, 0, 1, 0, Color.GRAY);
        Border innerBorder = BorderFactory.createEmptyBorder(5,5,5,5);
        Border combinedBorder = BorderFactory.createCompoundBorder(outerBorder, innerBorder);
        resultLabel.setBorder(combinedBorder);    
        resultLabel.setText(html);
        return resultLabel;
        
    }
}
    
