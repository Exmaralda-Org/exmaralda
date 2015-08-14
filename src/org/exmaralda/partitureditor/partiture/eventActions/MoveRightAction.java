/*
 * MoveRightAction.java
 *
 * Created on 18. Juni 2003, 11:36
 */

package org.exmaralda.partitureditor.partiture.eventActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class MoveRightAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of MoveRightAction */
    public MoveRightAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Move to the right", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control RIGHT"));            
    }
    
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("moveRightAction!");
        moveRight();
        table.transcriptionChanged = true;        
    }
    
    private void moveRight(){
        table.commitEdit(true);
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(table.selectionStartCol);
            int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol)+1);
            UndoInformation undoInfo = new UndoInformation(table, "Move right");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo move right");
            // end undo information
        }
        table.getModel().moveRight(table.selectionStartRow, table.selectionStartCol);
    }
    
    
}
