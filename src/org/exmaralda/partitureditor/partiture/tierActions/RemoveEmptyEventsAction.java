/*
 * RemoveEmptyEventsAction.java
 *
 * Created on 17. Juni 2003, 15:07
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class RemoveEmptyEventsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RemoveEmptyEventsAction */
    public RemoveEmptyEventsAction(PartitureTableWithActions t) {
        super("Remove empty events", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("removeEmptyEventsAction!");
        table.commitEdit(true);
        removeEmptyEvents();
    }

    private void removeEmptyEvents(){
        if (table.undoEnabled){
            UndoInformation undoInfo = new UndoInformation(table, "Remove empty events");
            undoInfo.memorizeTranscription(table);
            table.addUndo(undoInfo);
        }
        table.getModel().removeEmptyEvents(table.selectionStartRow);
    }
    
}
