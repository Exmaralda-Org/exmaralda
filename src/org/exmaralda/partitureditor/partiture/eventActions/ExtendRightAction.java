/*
 * ExtendRightAction.java
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
public class ExtendRightAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExtendRightAction
     * @param t
     * @param icon */
    public ExtendRightAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Extend to the right", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift RIGHT"));          
    }
    
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("extendRightAction!");
        extendRight();
        table.transcriptionChanged = true;        
    }
    
    private void extendRight(){
        table.commitEdit(true);
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(table.selectionStartCol);
            int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol)+1);
            UndoInformation undoInfo = new UndoInformation(table, "Extend right");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo extend right");
            // end undo information
        }
        table.getModel().extendRight(table.selectionStartRow, table.selectionStartCol);
        table.status("Right extended event [" + table.selectionStartRow + "/" + table.selectionStartCol + "]");        
        
    }
    
    
}
