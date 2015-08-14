/*
 * EditTierAction.java
 *
 * Created on 17. Juni 2003, 14:29
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.jexmaraldaswing.EditTierDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class EditTierAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditTierAction */
    public EditTierAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Tier properties...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editTierAction!");
        table.commitEdit(true);
        editTier();
        table.transcriptionChanged = true;        
    }
    
    private void editTier(){
        EditTierDialog dialog = new EditTierDialog(table.parent, true, table.getModel().getTranscription(), table.selectionStartRow);
        if (dialog.editTier()){
            if (table.undoEnabled){
                Tier tier =  table.getModel().getTier(table.selectionStartRow).makeCopy();
                tier.removeAllElements();
                UndoInformation undoInfo = new UndoInformation(table, "Edit tier");
                undoInfo.restoreType = UndoInformation.RESTORE_TIER_PROPERTIES;
                undoInfo.restoreObject = tier;
                table.addUndo(undoInfo);
            }
            table.getModel().editTier(table.selectionStartRow, dialog.getTier());
        }
    }
    
    
}
