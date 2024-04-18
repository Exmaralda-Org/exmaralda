/*
 * DeleteEventAction.java
 *
 * Created on 18. Juni 2003, 11:34
 */

package org.exmaralda.partitureditor.partiture.eventActions;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;


/**
 *
 * @author  thomas
 */

// 25-05-2023 : issue #389
public class DeleteEventsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    public boolean safetyCheck = false;

    /** Creates a new instance of DeleteEventAction
     * @param t
     * @param icon */
    public DeleteEventsAction(PartitureTableWithActions t) {
        super("Remove events", t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift D"));            
    }
    
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("deleteEventsAction!");
        table.commitEdit(true);
        if (safetyCheck){
            int retValue = JOptionPane.showConfirmDialog(table, 
                    FOLKERInternationalizer.getString("option.sure"),
                    FOLKERInternationalizer.getString("segmentactions.removeSegment"),
                    JOptionPane.YES_NO_OPTION);
            if (retValue==JOptionPane.NO_OPTION){
                return;
            }
        }
        deleteEvents();
        table.transcriptionChanged = true;        
    }
    
    private void deleteEvents(){
        if (table.undoEnabled){
            // Undo information
            // need a loop here
            int lower = table.getModel().lower(table.selectionStartCol);
            int upper = table.getModel().upper(table.selectionEndCol 
                    + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol));
            //System.out.println("upper " + upper + " lower " + lower);
            UndoInformation undoInfo = new UndoInformation(table, "Remove event");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo delete event");
            // end undo information
        }
        table.getModel()
                .deleteEvents(
                        table.selectionStartRow, 
                        table.selectionEndRow, 
                        table.selectionStartCol,
                        table.selectionEndCol
                );
        table.status("Deleted events [" + table.selectionStartRow + "-" + table.selectionEndRow +  "/" + table.selectionStartCol + "-" + table.selectionEndCol + "]");
        
    }
        
}
