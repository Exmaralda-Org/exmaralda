/*
 * EditTimelineFormatAction.java
 *
 * Created on 19. Juni 2003, 10:53
 */

package org.exmaralda.partitureditor.partiture.formatActions;

import org.exmaralda.partitureditor.jexmaraldaswing.EditTimelineItemFormatDialog;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 *
 * @author  thomas
 */
public class EditTimelineItemFormatAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditTimelineFormatAction */
    public EditTimelineItemFormatAction(PartitureTableWithActions t) {
        super("Format timeline items...", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("EditTimelineItemFormatAction!");
        table.commitEdit(true);
        editTimelineItemFormat();        
    }
    
    private void editTimelineItemFormat(){
        EditTimelineItemFormatDialog dialog = new EditTimelineItemFormatDialog(table.parent, true, table.getModel().getTierFormatTable().getTimelineItemFormat());
        if (dialog.editTimelineItemFormat()){
            table.getModel().setTimelineItemFormat(dialog.getTimelineItemFormat());
            table.formatChanged = true;            
        }
    }
    
    
    
}
