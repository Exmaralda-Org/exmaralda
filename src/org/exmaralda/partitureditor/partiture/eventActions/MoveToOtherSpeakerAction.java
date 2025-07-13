/*
 * MoveLeftAction.java
 *
 * Created on 18. Juni 2003, 11:37
 */

package org.exmaralda.partitureditor.partiture.eventActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaraldaswing.SingleSpeakerSelectionDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
// 24-06-2016 MuM-Multi new 
public class MoveToOtherSpeakerAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of MoveLeftAction
     * @param t */
    public MoveToOtherSpeakerAction(PartitureTableWithActions t) {
        super("Move to other speaker...", t);
    }
    
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("moveToOtherSpeakerAction!");
        moveToOtherSpeaker();
        table.transcriptionChanged = true;        
    }
    
    private void moveToOtherSpeaker(){
        table.commitEdit(true);
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(Math.max(0,table.selectionStartCol-1));
            int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol));
            //System.out.println("upper " + upper + " lower " + lower);
            UndoInformation undoInfo = new UndoInformation(table, "Move to other speaker");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo move left");
            // end undo information
        }
        // Check 0: is it a block selection at all?
        if (table.selectionStartCol<0 || table.selectionStartRow<0){
            JOptionPane.showMessageDialog(table, "Cannot move the current selection to another speaker");
            return;
        }
        // Check 1: is it a speaker block at all?
        Tier firstTier = table.getModel().getTier(table.selectionStartRow);
        String firstSpeaker = firstTier.getSpeaker();
        if (firstSpeaker==null){
            JOptionPane.showMessageDialog(table, "Cannot move the current selection to another speaker:\nThe first tier is not assigned to a speaker");
            return;            
        }
        for (int i=table.selectionStartRow; i<=table.selectionEndRow; i++){
            String thisSpeaker = table.getModel().getTier(i).getSpeaker();
            if (thisSpeaker==null){
                JOptionPane.showMessageDialog(table, "Cannot move the current selection to another speaker:\nAt least one of the selected tiers is not assigned to a speaker");
                return;            
            }
            if ((!thisSpeaker.equals(firstSpeaker))){
                JOptionPane.showMessageDialog(table, "Cannot move the current selection to another speaker:\nNot all selected tiers are assigned to the same speaker");
                return;                            
            }
        }
        
        // Find possible target positions
        String[] tiersOfSameCategory = table.getModel().getTranscription().getBody().getTiersOfCategory(firstTier.getCategory());
        List<String> candidateStarters = new ArrayList<>();
        for (String tierID : tiersOfSameCategory){
            try {
                if (firstTier.getID().equals(tierID)) continue;
                Tier candidateTier = table.getModel().getTranscription().getBody().getTierWithID(tierID);
                if (candidateTier.getSpeaker()==null) continue;
                candidateStarters.add(tierID);
            } catch (JexmaraldaException ex) {
                Logger.getLogger(MoveToOtherSpeakerAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.println("candidateStarters: " + String.join(" / ", candidateStarters));
        
        int heightOfSelection = table.selectionEndRow - table.selectionStartRow + 1;
        List<String> candidateStartersConsolidated = new ArrayList<>();
        for (String tierID : candidateStarters){
            int startPosition = table.getModel().getTranscription().getBody().lookupID(tierID);
            boolean doneBreak = false;
            for (int j=0; j<heightOfSelection; j++){
                Tier sourceTier = table.getModel().getTier(table.selectionStartRow + j);                
                if (startPosition + j > table.getModel().getNumRows() -1){
                    System.out.println("Reason 1: " + sourceTier.getID());
                    doneBreak = true;
                    break;
                }
                Tier targetTier = table.getModel().getTier(startPosition + j);                
                if (
                        (targetTier.getSpeaker()==null) ||
                        (!targetTier.getCategory().equals(sourceTier.getCategory())) ||
                        (!targetTier.getType().equals(sourceTier.getType()))
                   ){
                    System.out.println("Reason 2: " + sourceTier.getID());
                    doneBreak = true;
                    break;                    
                }
            }
            if (!doneBreak){
                candidateStartersConsolidated.add(tierID);
            }
        }
        
        if (candidateStartersConsolidated.isEmpty()){
            JOptionPane.showMessageDialog(table, "Cannot move the current selection to another speaker:\nNo target with same tier structure found");            
            return;
        }
        
        List<String> candidateStartersConsolidated2 = new ArrayList<>();
        String startTLI = table.getModel().getTimelineItem(table.selectionStartCol).getID();
        String endTLI = table.getModel().getTimelineItem(table.selectionEndCol + 1).getID();
            
        System.out.println("candidateStartersConsolidated: " + String.join(" / ", candidateStartersConsolidated));
        
        
        for (String tierID : candidateStartersConsolidated){
            int startPosition = table.getModel().getTranscription().getBody().lookupID(tierID);
            boolean doneBreak = false;
            for (int j=0; j<heightOfSelection; j++){
                Tier targetTier = table.getModel().getTier(startPosition + j);                
                //Vector<Event> eventsBetween = targetTier.getEventsBetween(table.getModel().getTranscription().getBody().getCommonTimeline(), startTLI, endTLI);
                Vector<Event> eventsBetween = targetTier.getEventsIntersecting(table.getModel().getTranscription().getBody().getCommonTimeline(), startTLI, endTLI);
                if (!eventsBetween.isEmpty()){
                    doneBreak = true;
                    break;                                        
                }
            }
            if (!doneBreak){
                candidateStartersConsolidated2.add(tierID);
            }
        }
        
        if (candidateStartersConsolidated2.isEmpty()){
            JOptionPane.showMessageDialog(table, "Cannot move the current selection to another speaker:\nTarget(s) with same tier structure found, but none of them is empty. ");            
            return;
        }
        
        System.out.println("candidateStartersConsolidated2: " + String.join(" / ", candidateStartersConsolidated2));
        
        List<String> speakerIDs = new ArrayList<>();
        for (String tierID : candidateStartersConsolidated2){
            try {
                speakerIDs.add(table.getModel().getTranscription().getBody().getTierWithID(tierID).getSpeaker());
            } catch (JexmaraldaException ex) {
                Logger.getLogger(MoveToOtherSpeakerAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        SingleSpeakerSelectionDialog speakerSelectionDialog =
                new SingleSpeakerSelectionDialog(table.getModel().getTranscription(), speakerIDs, table.parent, true);
        
        speakerSelectionDialog.setLocationRelativeTo(table);
        speakerSelectionDialog.setExplanationText("<html>Please choose the <b>speaker</b><br/> to whose tiers the selection will be moved</html>");
        
        speakerSelectionDialog.setVisible(true);
        if (speakerSelectionDialog.getReturnStatus()==SingleSpeakerSelectionDialog.RET_CANCEL) return;
        String targetSpeakerID = speakerSelectionDialog.getSelectedSpeakerID();
        
        
        //JOptionPane.showMessageDialog(table, "Speaker: " + targetSpeakerID);
        
        int index = speakerIDs.indexOf(targetSpeakerID);
        String selectedTargetTierID = candidateStartersConsolidated2.get(index);
        int startPosition = table.getModel().getTranscription().getBody().lookupID(selectedTargetTierID);

        /*JOptionPane.showMessageDialog(table, "Speaker: " + targetSpeakerID
        + "\nselectedTargetTierID: " + selectedTargetTierID
        + "\nstartPosition: " + startPosition
        + "\nheightOfSelection: " + heightOfSelection
        );*/

        
        for (int j=0; j<heightOfSelection; j++){
            Tier sourceTier = table.getModel().getTier(table.selectionStartRow + j);                
            Tier targetTier = table.getModel().getTier(startPosition + j);
            
            try {
                table.getModel().moveToTier(sourceTier.getID(), targetTier.getID(), startTLI, endTLI);
            } catch (JexmaraldaException ex) {
                Logger.getLogger(MoveToOtherSpeakerAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
}
