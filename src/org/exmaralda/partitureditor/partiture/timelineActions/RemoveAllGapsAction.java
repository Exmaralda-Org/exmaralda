/*
 * RemoveAllGapsAction.java
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
public class RemoveAllGapsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RemoveAllGapsAction */
    public RemoveAllGapsAction(PartitureTableWithActions t) {
        super("Remove all gaps", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("removeAllGapsAction!");
        table.commitEdit(true);
        removeAllGaps();
        table.transcriptionChanged = true;
    }
    
    private void removeAllGaps(){
        if (table.undoEnabled){
            UndoInformation undoInfo = new UndoInformation(table, "Remove all gaps");
            undoInfo.memorizeTranscription(table);
            table.addUndo(undoInfo);
        }
        table.getModel().removeAllGaps();
    }
    
    
    
}
