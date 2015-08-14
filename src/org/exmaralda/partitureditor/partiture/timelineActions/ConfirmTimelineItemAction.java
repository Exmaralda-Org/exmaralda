/*
 * EditTimelineItemAction.java
 *
 * Created on 19. Juni 2003, 10:19
 */

package org.exmaralda.partitureditor.partiture.timelineActions;


import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.praatPanel.*;

/**
 *
 * @author  thomas
 */
public class ConfirmTimelineItemAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ConfirmTimelineItemAction */
    public ConfirmTimelineItemAction(PartitureTableWithActions t) {
        super("Confirm timeline item(s)",  t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("confirmTimelineItemAction!");
        table.commitEdit(true);
        confirmTimelineItem();
        table.transcriptionChanged = true;        
    }
    
    private void confirmTimelineItem(){
        table.getModel().confirmTimelineItems(table.selectionStartCol, table.selectionEndCol);
    }
    
    
    
}
