/*
 * ShrinkLeftAction.java
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
public class ShrinkLeftAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ShrinkLeftAction */
    public ShrinkLeftAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Shrink on the left", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control alt LEFT"));            
    }
    
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("shrinkLeftAction!");
        shrinkLeft();
        table.transcriptionChanged = true;                
    }
    
    private void shrinkLeft(){
        table.commitEdit(true);
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(Math.max(0,table.selectionStartCol-1));
            int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol));
            //System.out.println("upper " + upper + " lower " + lower);
            UndoInformation undoInfo = new UndoInformation(table, "Shrink left");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo shrink left");
            // end undo information
        }
        table.getModel().shrinkLeft(table.selectionStartRow, table.selectionStartCol);
    }
    
    
}
