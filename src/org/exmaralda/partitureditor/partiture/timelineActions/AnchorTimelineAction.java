/*
 * CompleteTimelineAction.java
 *
 * Created on 19. Juni 2003, 10:20
 */

package org.exmaralda.partitureditor.partiture.timelineActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class AnchorTimelineAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of CompleteTimelineAction */
    public AnchorTimelineAction(PartitureTableWithActions t) {
        super("Anchor timeline", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("anchorTimelineAction!");
        table.commitEdit(true);
        anchorTimeline();
        table.transcriptionChanged = true;        
    }
    
    private void anchorTimeline(){
        if (table.player.getTotalLength()>0){
            if (table.undoEnabled){
                UndoInformation undoInfo = new UndoInformation(table, "Anchor timeline");
                undoInfo.memorizeTranscription(table);
                table.addUndo(undoInfo);
            }
            double first = 0.0;
            double last = table.player.getTotalLength();
            table.getModel().anchorTimeline(first, last);
        }
    }
    
    
}
