/*
 * CleanupAction.java
 *
 * Created on 19. August 2003, 16:46
 */

package org.exmaralda.partitureditor.partiture.editActions;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
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
    
    /** Creates a new instance of CleanupAction
     * @param t
     * @param icon */
    public CleanupAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Clean up...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("cleanupAction!");
        cleanup();                
    }
    
    private void cleanup(){
        CleanupDialog dialog = new CleanupDialog(table.parent, true);

        int howManyUnusedSpeakers = 0;
        int howManyEmptyEvents = 0;
        int howManyGapsBridged = 0;
        int howManyUnusedTimelineItems = 0;
        int howManySmoothed = 0;
        int howManyGapsRemoved = 0;

        if (dialog.editCleanupParameters()){
            if (table.undoEnabled){
                UndoInformation undoInfo = new UndoInformation(table, "Cleanup");
                undoInfo.memorizeTranscription(table);
                table.addUndo(undoInfo);
            }
            
            
            
            if (dialog.removeUnusedSpeakers()){
                try {
                    howManyUnusedSpeakers = table.getModel().getTranscription().removeUnusedSpeakers();
                } catch (JexmaraldaException ex) {
                    Logger.getLogger(CleanupAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
            for (int pos=0; pos<table.getModel().getTranscription().getBody().getNumberOfTiers(); pos++){
                if (dialog.removeEmptyEvents()){
                    int howMany = table.getModel().getTranscription().getBody().getTierAt(pos).removeEmptyEvents();
                    howManyEmptyEvents+=howMany;
                }
                if (dialog.bridgeGaps()){
                    int howMany = table.getModel().getTranscription().getBody().getTierAt(pos).bridgeGaps(dialog.getMaxDiff()/1000, table.getModel().getTranscription().getBody().getCommonTimeline());
                    howManyGapsBridged+=howMany;
                }
            }
            if (dialog.smoothTimeline()){
                howManySmoothed = table.getModel().smoothTimeline(dialog.getThreshhold() / 1000.0);
            }
            if (dialog.removeUnusedTLI()){
                howManyUnusedTimelineItems = table.getModel().getTranscription().getBody().removeUnusedTimelineItems();
            }
            if (dialog.removeGaps()){
                howManyGapsRemoved = table.getModel().getTranscription().getBody().removeAllGaps();
            }
            if (dialog.normalizeIDs()){
                // added 24-09-2009: tier format table mappings must be taken care of
                // changed 31-03-2017: perform this on a copy, may be safer issue #67
                BasicTranscription copyBT = table.getModel().getTranscription().makeCopy();
                String[] originaltierids = copyBT.getBody().getAllTierIDs();
                copyBT.normalize();
                String[] newtierids = copyBT.getBody().getAllTierIDs();
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
                table.getModel().setTranscription(copyBT);
                return;
            }
        }        
        //table.resetData();
        //table.getModel().fireDataReset();
        //table.getModel().setTranscription(table.getModel().getTranscription());
        

        String message = "<html>";
        message+= Integer.toString(howManyUnusedSpeakers) + " unused speakers removed. <br/>";
        message+= Integer.toString(howManyEmptyEvents) + " empty events removed. <br/>";
        message+= Integer.toString(howManyGapsBridged) + " gaps bridged. <br/>";
        message+= Integer.toString(howManyUnusedTimelineItems) + " unused timeline items removed. <br/>";
        message+= Integer.toString(howManySmoothed) + " timeline items smoothed. <br/>";
        message+= Integer.toString(howManyGapsRemoved) + " gaps removed. <br/>";
        message+="</html>";
        
        JOptionPane.showMessageDialog(table, message, "Clenup performed", JOptionPane.INFORMATION_MESSAGE);
        
        table.getModel().setTranscription(table.getModel().getTranscription());
        table.status("Cleanup performed. ");
    }
    
}
