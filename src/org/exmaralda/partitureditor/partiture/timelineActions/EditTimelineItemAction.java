/*
 * EditTimelineItemAction.java
 *
 * Created on 19. Juni 2003, 10:19
 */

package org.exmaralda.partitureditor.partiture.timelineActions;


import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;
import org.exmaralda.partitureditor.praatPanel.*;

/**
 *
 * @author  thomas
 */
public class EditTimelineItemAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditTimelineItemAction */
    public EditTimelineItemAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Edit timeline item...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editTimelineItemAction!");
        table.commitEdit(true);
        editTimelineItem();
        table.transcriptionChanged = true;        
    }
    
    private void editTimelineItem(){
        TimelineItem timelineItem = table.getModel().getTimelineItem(table.selectionStartCol);
        EditTimelineItemDialog dialog = new EditTimelineItemDialog(table.parent, true, timelineItem);
        if (dialog.editTimelineItem()){
            if (table.undoEnabled){
                // Undo information
                UndoInformation undoInfo = new UndoInformation(table, "Edit timeline item");
                undoInfo.memorizeTime(timelineItem, timelineItem.getTime());
                table.addUndo(undoInfo);
                // end undo information

            }
            TimelineItem tli = dialog.getTimelineItem();
            table.getModel().editTimelineItem(table.selectionStartCol, tli );
            PraatPanelEvent ppe = new PraatPanelEvent(tli.getTime());
            table.mediaPanelDialog.setStartTime(tli.getTime());
        }
    }
    
    
    
}
