/*
 * MakeTimelineConsistentAction.java
 *
 * Created on 19. Juni 2003, 10:17
 */

package org.exmaralda.partitureditor.partiture.timelineActions;

import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class MakeTimelineConsistentAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of MakeTimelineConsistentAction
     * @param t */
    public MakeTimelineConsistentAction(PartitureTableWithActions t) {
        super("Make timeline consistent", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("makeTimelineConsistentAction!");
        table.commitEdit(true);
        makeTimelineConsistent();
        table.transcriptionChanged = true;        
    }
    
    private void makeTimelineConsistent(){
        if (table.undoEnabled){
            UndoInformation undoInfo = new UndoInformation(table, "Make timeline consistent");
            undoInfo.memorizeTranscription(table);
            table.addUndo(undoInfo);
        }
        int count = table.getModel().makeTimelineConsistent();
        String message = "Removed absolute time for " + Integer.toString(count) + " inconcistent timeline items.";
        JOptionPane.showMessageDialog(table, message, "Make timeline consistent", JOptionPane.INFORMATION_MESSAGE);
        table.status(message);
        
    }
    
    
    
}
