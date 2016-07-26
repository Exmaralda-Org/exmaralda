/*
 * MoveLeftAction.java
 *
 * Created on 18. Juni 2003, 11:37
 */

package org.exmaralda.partitureditor.partiture.eventActions;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
// 24-06-2016 MuM-Multi new 
public class MoveEventAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    String sourceTierID;
    String targetTierID;
    String startID;
    
    /** Creates a new instance of MoveLeftAction */
    public MoveEventAction(PartitureTableWithActions t, String sourceTierID, String targetTierID, String startID) throws JexmaraldaException {
        super(t.getModel().getTranscription().getBody().getTierWithID(targetTierID).getDescription(t.getModel().getTranscription().getHead().getSpeakertable()), t);        
        this.sourceTierID = sourceTierID;
        this.targetTierID = targetTierID;
        this.startID = startID;
    }
    
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("moveDownLeftAction!");
        move();
        table.transcriptionChanged = true;        
    }
    
    private void move(){
        table.commitEdit(true);
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(Math.max(0,table.selectionStartCol-1));
            int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol));
            //System.out.println("upper " + upper + " lower " + lower);
            UndoInformation undoInfo = new UndoInformation(table, "Move to tier");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo move left");
            // end undo information
        }
        try {  
            table.getModel().moveToTier(sourceTierID, targetTierID, startID);            
        } catch (JexmaraldaException ex) {
            Logger.getLogger(MoveEventAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
