/*
 * SplitAction.java
 *
 * Created on 18. Juni 2003, 11:38
 */

package org.exmaralda.partitureditor.partiture.eventActions;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaraldaswing.SplitLongEventDialog;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */

// 22-08-2017: for issue #120

public class SplitLongEventAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SplitAction
     * @param t */
    public SplitLongEventAction(PartitureTableWithActions t) {
        super("Split long event...", t);
    }
    
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("splitLongEventAction!");
        splitLongEvent();
        table.transcriptionChanged = true;        
    }
    
    private void splitLongEvent(){
        table.commitEdit(true);
        SplitLongEventDialog dialog = new SplitLongEventDialog(table.parent, true);
        Tier tier = table.getModel().getTranscription().getBody().getTierAt(table.selectionStartRow);
        String tli = table.getModel().getTranscription().getBody().getCommonTimeline().getTimelineItemAt(table.selectionStartCol).getID();
        try {
            Event event = tier.getEventAtStartPoint(tli);
            String eventText = event.getDescription();
            dialog.setText(eventText);
            dialog.setLocationRelativeTo(table);
            dialog.setVisible(true);
            if (dialog.approved){
                if (table.undoEnabled){
                // Undo information
                    int lower = table.getModel().lower(table.selectionStartCol);
                    int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol));
                    //System.out.println("upper " + upper + " lower " + lower);
                    UndoInformation undoInfo = new UndoInformation(table, "Split");
                    undoInfo.memorizeRegion(table, lower, upper);
                    table.addUndo(undoInfo);
                    //System.out.println("Added undo split");
                    // end undo information
                }
                int splitPosition = dialog.getCursorPosition();
                table.getModel().split(table.selectionStartRow, table.selectionStartCol, splitPosition, table.parent);                
            }

        } catch (JexmaraldaException ex) {
            Logger.getLogger(SplitLongEventAction.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
}
