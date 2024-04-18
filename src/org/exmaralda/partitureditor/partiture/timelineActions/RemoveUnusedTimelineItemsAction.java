/*
 * RemoveUnusedTimelineItemsAction.java
 *
 * Created on 19. Juni 2003, 10:18
 */

package org.exmaralda.partitureditor.partiture.timelineActions;

import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class RemoveUnusedTimelineItemsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RemoveUnusedTimelineItemsAction
     * @param t
     * @param icon */
    public RemoveUnusedTimelineItemsAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Remove unused timeline items", icon, t);
    }
    
    @Override
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
        int countRemoved = table.getModel().removeUnusedTimelineItems();
        String message = "Removed " + Integer.toString(countRemoved) + " unused timeline items.";
        JOptionPane.showMessageDialog(table, message, "Remove unused timeline items", JOptionPane.INFORMATION_MESSAGE);
        if ((r<table.getNumRows()) && (c<table.getNumColumns())){
            table.setNewSelection(r, c, false);
        }
        table.status(message);

    }
    
    
    
}
