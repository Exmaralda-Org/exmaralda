/*
 * InsertTimelineItemAction.java
 *
 * Created on 19. Juni 2003, 10:16
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
public class InsertTimelineItemAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of InsertTimelineItemAction */
    public InsertTimelineItemAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Insert timeline item", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("insertTimelineItemAction!");
        table.commitEdit(true);
        insertTimelineItem();
        table.transcriptionChanged = true;
    }
    
    private void insertTimelineItem(){
        if (table.undoEnabled){
            UndoInformation undoInfo = new UndoInformation(table, "Insert timeline item");
            undoInfo.memorizeTranscription(table);
            table.addUndo(undoInfo);
        }
        table.getModel().insertTimelineItem(table.selectionStartCol);
    }
    
    
}
