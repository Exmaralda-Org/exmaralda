/*
 * CleanupAction.java
 *
 * Created on 19. August 2003, 16:46
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.TierFormat;
import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class CleanupAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of CleanupAction */
    public CleanupAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Clean up...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("cleanupAction!");
        cleanup();                
    }
    
    private void cleanup(){
        CleanupDialog dialog = new CleanupDialog(table.parent, true);
        if (dialog.editCleanupParameters()){
            if (table.undoEnabled){
                UndoInformation undoInfo = new UndoInformation(table, "Cleanup");
                undoInfo.memorizeTranscription(table);
                table.addUndo(undoInfo);
            }
            for (int pos=0; pos<table.getModel().getTranscription().getBody().getNumberOfTiers(); pos++){
                if (dialog.removeEmptyEvents()){table.getModel().getTranscription().getBody().getTierAt(pos).removeEmptyEvents();}
                if (dialog.bridgeGaps()){table.getModel().getTranscription().getBody().getTierAt(pos).bridgeGaps(dialog.getMaxDiff()/1000, table.getModel().getTranscription().getBody().getCommonTimeline());}
            }
            if (dialog.smoothTimeline()){
                table.getModel().smoothTimeline(dialog.getThreshhold() / 1000.0);
            }
            if (dialog.removeUnusedTLI()){table.getModel().getTranscription().getBody().removeUnusedTimelineItems();}
            if (dialog.removeGaps()){table.getModel().getTranscription().getBody().removeAllGaps();}
            if (dialog.normalizeIDs()){
                // added 24-09-2009: tier format table mappings must be taken care of
                String[] originaltierids = table.getModel().getTranscription().getBody().getAllTierIDs();
                table.getModel().getTranscription().normalize();
                String[] newtierids = table.getModel().getTranscription().getBody().getAllTierIDs();
                TierFormatTable tft = table.getModel().getTierFormatTable();
                int count = 0;
                for (String originalID : originaltierids){
                    try {
                        TierFormat tf = tft.getTierFormatForTier(originalID);
                        tf.setTierref(newtierids[count]);
                        count++;
                    } catch (JexmaraldaException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }        
        //table.resetData();
        //table.getModel().fireDataReset();
        table.getModel().setTranscription(table.getModel().getTranscription());
    }
    
}
