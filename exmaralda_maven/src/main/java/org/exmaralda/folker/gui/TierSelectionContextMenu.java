/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.folker.gui;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;

/**
 *
 * @author thomas
 */
public class TierSelectionContextMenu extends javax.swing.JPopupMenu {

    String selectedTierID = null;

    public TierSelectionContextMenu(PartitureTableWithActions p, final String id1, final String id2, final int col1, final int col2) {
        super("Select a tier");
        final PartitureTableWithActions partitur = p;
        final BasicTranscription bt = p.getModel().getTranscription();
        Timeline tl = bt.getBody().getCommonTimeline();
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            final Tier tier = bt.getBody().getTierAt(pos);
            int intersectorsCount = tier.getEventsIntersecting(tl, id1, id2).size();
            if (intersectorsCount==0){
                String text = tier.getDisplayName();
                if (text.trim().length()==0){
                    text = tier.getDescription(bt.getHead().getSpeakertable());                    
                }
                text = Integer.toString(pos) + " - " + text;
                JMenuItem mi = new JMenuItem(text);
                mi.addActionListener(new ActionListener(){
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        System.out.println("****" + tier.getID());
                        org.exmaralda.partitureditor.jexmaralda.Event newEvent = new org.exmaralda.partitureditor.jexmaralda.Event();
                        newEvent.setStart(id1);
                        newEvent.setEnd(id2);
                        tier.addEvent(newEvent);

                        int row = bt.getBody().lookupID(tier.getID());
                        partitur.getModel().fireEventAdded(row, col1, col2);
                        partitur.setNewSelection(row, col1, true);
                    }
                });
                add(mi);
            }
        }
        addSeparator();
        JMenuItem doNothingMI = new JMenuItem("--- none ---");
        doNothingMI.addActionListener(new ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("**** do nothing ***");
                partitur.setNewSelection(-1,-1, col1, col2-1);
            }
        });
        add(doNothingMI);
    }

    public String getSelectedTierID(){
        return selectedTierID;
    }



}
