/*
 * MoveTierUpAction.java
 *
 * Created on 17. Juni 2003, 14:50
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;
/**
 *
 * @author  thomas
 */
public class MoveTierUpAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of MoveTierUpAction */
    public MoveTierUpAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Move tier upwards", icon, t);        
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control UP"));
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("moveTierUpAction!");
        table.commitEdit(true);
        moveTierUp();
        table.transcriptionChanged = true;        
    }
    
    private void moveTierUp(){
        if (table.undoEnabled){
            UndoInformation undoInfo = new UndoInformation(table, "Move tier");
            undoInfo.restoreType = UndoInformation.RESTORE_TIER_ORDER;
            undoInfo.restoreObject = table.getModel().getTranscription().getBody().getAllTierIDs();
            table.addUndo(undoInfo);
        }
        table.getModel().moveTierUp(table.selectionStartRow);
    }
    
    
}
