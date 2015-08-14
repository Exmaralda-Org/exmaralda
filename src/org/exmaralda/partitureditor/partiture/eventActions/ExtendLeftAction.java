/*
 * ExtendLeftAction.java
 *
 * Created on 18. Juni 2003, 11:35
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
public class ExtendLeftAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExtendLeftAction */
    public ExtendLeftAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Extend to the left", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift LEFT"));            
    }
    
        
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("extendLeftAction!");
        extendLeft();
        table.transcriptionChanged = true;        
    }
    
    private void extendLeft(){
        table.commitEdit(true);
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(Math.max(0,table.selectionStartCol-1));
            int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol));
            //System.out.println("upper " + upper + " lower " + lower);
            UndoInformation undoInfo = new UndoInformation(table, "Extend left");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo extend left");
            // end undo information
        }
        table.getModel().extendLeft(table.selectionStartRow, table.selectionStartCol);
    }
    
    
}
