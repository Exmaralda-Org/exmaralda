/*
 * RemoveUnusedTimelineItemsAction.java
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
public class RemoveUnusedTimelineItemsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RemoveUnusedTimelineItemsAction */
    public RemoveUnusedTimelineItemsAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Remove unused timeline items", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("removeUnusedTimelineItemsAction!");
        table.commitEdit(true);
        removeUnusedTimelineItems();
        table.transcriptionChanged = true;        
    }
    
    private void removeUnusedTimelineItems(){
        if (table.undoEnabled){
            UndoInformation undoInfo = new UndoInformation(table, "Remove unused timeline items");
            undoInfo.memorizeTranscription(table);
            table.addUndo(undoInfo);
        }
        int r = table.selectionStartRow;
        int c = table.selectionStartCol;
        table.getModel().removeUnusedTimelineItems();
        if ((r<table.getNumRows()) && (c<table.getNumColumns())){
            table.setNewSelection(r, c, false);
        }

    }
    
    
    
}
