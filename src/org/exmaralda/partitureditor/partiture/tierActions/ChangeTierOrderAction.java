/*
 * ChangeTierOrderAction.java
 *
 * Created on 17. Juni 2003, 14:54
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.jexmaraldaswing.EditTierOrderDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class ChangeTierOrderAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ChangeTierOrderAction */
    public ChangeTierOrderAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Change tier order...", icon, t);     
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("changeTierOrderAction!");
        table.commitEdit(true);
        changeTierOrder();
        table.transcriptionChanged = true;        
    }

    private void changeTierOrder(){
        EditTierOrderDialog dialog = new EditTierOrderDialog(table.parent, true, table.getModel().getTranscription());
        if (dialog.editTierOrder()){
            if (table.undoEnabled){
                UndoInformation undoInfo = new UndoInformation(table, "Change tier order");
                undoInfo.restoreType = UndoInformation.RESTORE_TIER_ORDER;
                undoInfo.restoreObject = table.getModel().getTranscription().getBody().getAllTierIDs();
                table.addUndo(undoInfo);
            }
            table.getModel().changeTierOrder(dialog.getTierOrder());
        }
    }
    
}
