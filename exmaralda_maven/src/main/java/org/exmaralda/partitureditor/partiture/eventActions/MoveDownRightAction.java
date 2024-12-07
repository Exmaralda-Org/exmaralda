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
// 24-06-2016 MuM-Multi new 
public class MoveDownRightAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of MoveLeftAction */
    public MoveDownRightAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Move down right", icon, t);
    }
    
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("moveDownRightAction!");
        moveDownRight();
        table.transcriptionChanged = true;        
    }
    
    private void moveDownRight(){
        table.commitEdit(true);
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(Math.max(0,table.selectionStartCol-1));
            int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol));
            //System.out.println("upper " + upper + " lower " + lower);
            UndoInformation undoInfo = new UndoInformation(table, "Move down right");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo move left");
            // end undo information
        }
        table.getModel().moveDownRight(table.selectionStartRow, table.selectionStartCol);
    }
    
    
}
