/*
 * SplitAction.java
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
public class SplitAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SplitAction
     * @param t
     * @param icon */
    public SplitAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Split", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 2"));            
    }
    
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("splitAction!");
        split();
        table.transcriptionChanged = true;        
    }
    
    private void split(){
        int pos = table.getCharPos();
        table.commitEdit(true);
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
        table.getModel().split(table.selectionStartRow, table.selectionStartCol, pos, table.parent);
        table.status("Split event [" + table.selectionStartRow + "/" + table.selectionStartCol + "]");
        
    }
    
    
}
