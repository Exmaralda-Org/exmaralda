/*
 * MoveLeftAction.java
 *
 * Created on 18. Juni 2003, 11:37
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
public class MoveLeftAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of MoveLeftAction
     * @param t
     * @param icon */
    public MoveLeftAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Move to the left", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control LEFT"));            
    }
    
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("moveLeftAction!");
        moveLeft();
        table.transcriptionChanged = true;        
    }
    
    private void moveLeft(){
        table.commitEdit(true);
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(Math.max(0,table.selectionStartCol-1));
            int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol));
            //System.out.println("upper " + upper + " lower " + lower);
            UndoInformation undoInfo = new UndoInformation(table, "Move left");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo move left");
            // end undo information
        }
        //table.getModel().moveLeft(table.selectionStartRow, table.selectionStartCol);
        // changed 19-04-2024 for #467
        table.getModel().moveLeft(table.selectionStartRow, table.selectionEndRow, table.selectionStartCol, table.selectionEndCol);
        table.status("Moved event left [" + table.selectionStartRow + "-" + table.selectionEndRow + "/" + table.selectionStartCol + "-" + table.selectionEndCol + "]");
        
    }
    
    
}
