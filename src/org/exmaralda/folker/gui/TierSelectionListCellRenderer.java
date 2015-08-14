/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.folker.gui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Tier;

/**
 *
 * @author thomas
 */
public class TierSelectionListCellRenderer extends javax.swing.DefaultListCellRenderer {

    BasicTranscription transcription;

    public TierSelectionListCellRenderer(BasicTranscription bt) {
        transcription = bt;
    }


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof Tier){
            Tier tier = (Tier)value;
            int pos = transcription.getBody().lookupID(tier.getID());
            String text = Integer.toString(pos+1) + " - " + tier.getDisplayName();
            String tooltip = tier.getDescription(transcription.getHead().getSpeakertable());
            Component c = super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            ((JLabel)c).setToolTipText(tooltip);
            return c;
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }



}
