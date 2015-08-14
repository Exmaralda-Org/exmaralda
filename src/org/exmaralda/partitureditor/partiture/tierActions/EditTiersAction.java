/*
 * EditTierAction.java
 *
 * Created on 17. Juni 2003, 14:29
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.jexmaraldaswing.EditTiersDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class EditTiersAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditTierAction */
    public EditTiersAction(PartitureTableWithActions t) {
        super("Edit tiers...", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editTiersAction!");
        table.commitEdit(true);
        editTiers();
        table.transcriptionChanged = true;        
    }
    
    private void editTiers(){
        EditTiersDialog dialog = new EditTiersDialog(table.parent, true, table.getModel().getTranscription());
        if (dialog.editTiers()){
            if (table.undoEnabled){
                UndoInformation undoInfo = new UndoInformation(table, "Edit tiers");
                undoInfo.memorizeTranscription(table);
                table.addUndo(undoInfo);
            }
            table.getModel().editTiers(dialog.getTranscription());
        }
    }
    
    
}
