/*
 * EditTierAction.java
 *
 * Created on 17. Juni 2003, 14:29
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaraldaswing.TabulateEventsTableDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.TierSelectionDialog;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;

// changed for #382


/**
 *
 * @author  thomas
 */
public class TabulateEventsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditTierAction
     * @param t */
    public TabulateEventsAction(PartitureTableWithActions t) {
        super("Tabulate events...", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            System.out.println("tabulateEventsAction!");
            table.commitEdit(true);
            tabulateEvents();      
            table.transcriptionChanged = true;
        } catch (JexmaraldaException ex) {
            Logger.getLogger(TabulateEventsAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void tabulateEvents() throws JexmaraldaException{
        Tier tier = table.getModel().getTranscription().getBody().getTierAt(table.selectionStartRow);
        //ListEventsDialog dialog = new ListEventsDialog(table.parent, false, tier);
        TierSelectionDialog tierSelectionDialog = new TierSelectionDialog(table.parent, true);
        String[] initTiers = table.getModel().getTranscription().getBody().getTiersOfSpeakerAndType(tier.getSpeaker(), "a");
        boolean selectTiers = tierSelectionDialog.selectTiers(table.getModel().getTranscription(), initTiers);
        if (!selectTiers) return;
        
        String[] selectedTierIDs = tierSelectionDialog.getSelectedTierIDs();
        Tier[] dependentTiers = new Tier[selectedTierIDs.length];
        int i=0;
        for (String tierID : selectedTierIDs){
            System.out.println(tierID);
            dependentTiers[i] = table.getModel().getTranscription().getBody().getTierWithID(tierID);
            i++;
        }
        
        TabulateEventsTableDialog dialog = new TabulateEventsTableDialog(table.parent, false, table.getModel().getTranscription(), 
                tier, dependentTiers);
        table.getModel().addTableDataListener(dialog);
        dialog.addTableDataListener(table);
        dialog.setLocationRelativeTo(table);
        dialog.addSearchResultListener(table);
        dialog.setVisible(true);
        
    }
    
    
}
