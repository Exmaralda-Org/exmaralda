/*
 * DeleteEventAction.java
 *
 * Created on 18. Juni 2003, 11:34
 */

package org.exmaralda.partitureditor.partiture.eventActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;


/**
 *
 * @author  thomas
 */
public class DeleteEventAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    public boolean safetyCheck = false;

    /** Creates a new instance of DeleteEventAction */
    public DeleteEventAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Remove", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control D"));            
    }
    
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("deleteEventAction!");
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
        deleteEvent();
        table.transcriptionChanged = true;        
    }
    
    private void deleteEvent(){
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(table.selectionStartCol);
            int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol));
            //System.out.println("upper " + upper + " lower " + lower);
            UndoInformation undoInfo = new UndoInformation(table, "Remove event");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo delete event");
            // end undo information
        }
        table.getModel().deleteEvent(table.selectionStartRow, table.selectionStartCol);
    }
        
}
