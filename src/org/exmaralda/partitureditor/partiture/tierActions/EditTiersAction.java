/*
 * EditTierAction.java
 *
 * Created on 17. Juni 2003, 14:29
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaraldaswing.EditTiersDialogNew;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class EditTiersAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditTierAction
     * @param t */
    public EditTiersAction(PartitureTableWithActions t) {
        super("Edit tiers...", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editTiersAction!");
        table.commitEdit(true);
        editTiers();
        table.transcriptionChanged = true;        
    }
    
    private void editTiers(){
        //EditTiersDialog dialog = new EditTiersDialog(table.parent, true, table.getModel().getTranscription());
        BasicTranscription workingCopy = table.getModel().getTranscription().makeCopy();
        EditTiersDialogNew dialog = new EditTiersDialogNew(table.parent, true, workingCopy);
        dialog.setLocationRelativeTo(table);
        dialog.setTitle("Edit tiers");
        dialog.setVisible(true);
        if (dialog.isChanged()){
            if (table.undoEnabled){
                UndoInformation undoInfo = new UndoInformation(table, "Edit tiers");
                undoInfo.memorizeTranscription(table);
                table.addUndo(undoInfo);
            }
            table.getModel().editTiers(workingCopy);
        }
        /*if (dialog.editTiers()){
            if (table.undoEnabled){
                UndoInformation undoInfo = new UndoInformation(table, "Edit tiers");
                undoInfo.memorizeTranscription(table);
                table.addUndo(undoInfo);
            }
            table.getModel().editTiers(dialog.getTranscription());
        }*/
    }
    
    
}
