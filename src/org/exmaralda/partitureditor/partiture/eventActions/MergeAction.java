/*
 * MergeAction.java
 *
 * Created on 18. Juni 2003, 11:38
 */

package org.exmaralda.partitureditor.partiture.eventActions;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class MergeAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of MergeAction */
    public MergeAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Merge", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 1"));            
    }
    
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("mergeAction!");
        merge();
        table.transcriptionChanged = true;        
    }
    
    private void merge(){
        //table.getModel().merge(table.selectionStartRow, table.selectionStartCol, table.selectionEndCol);
        int startCol = table.selectionStartCol;
        int endCol = table.selectionEndCol;
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(startCol);
            int upper = table.getModel().upper(endCol);
            //System.out.println("upper " + upper + " lower " + lower);
            UndoInformation undoInfo = new UndoInformation(table, "Merge");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo merge");
            // end undo information
        }
        table.getModel().merge(table.selectionStartRow, table.selectionEndRow, table.selectionStartCol, table.selectionEndCol);
        int r = table.selectionStartRow;
        int c = table.selectionStartCol;
        if (table.AUTO_REMOVE_UNUSED_TLI){
            table.getModel().removeUnusedTimelineItems(startCol, endCol);
            if ((r<table.getNumRows()) && (c<table.getNumColumns())){
                table.setNewSelection(r, c, false);
            }
        }
    }
    
}
