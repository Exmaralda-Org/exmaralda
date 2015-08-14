/*
 * RemoveGapAction.java
 *
 * Created on 19. Juni 2003, 10:18
 */

package org.exmaralda.partitureditor.partiture.timelineActions;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class RemoveGapAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RemoveGapAction */
    public RemoveGapAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Remove gap", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("removeGapAction!");
        table.commitEdit(true);
        removeGap();
        table.transcriptionChanged = true;
    }
    
    private void removeGap(){
        if (table.undoEnabled){
            UndoInformation undoInfo = new UndoInformation(table, "Remove gap");
            undoInfo.memorizeTranscription(table);
            table.addUndo(undoInfo);
        }
        table.getModel().removeGap(table.selectionStartCol);
    }
    
    
}
