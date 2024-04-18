/*
 * RemoveAllGapsAction.java
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
public class RemoveAllGapsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RemoveAllGapsAction
     * @param t */
    public RemoveAllGapsAction(PartitureTableWithActions t) {
        super("Remove all gaps", t);
    }
    
    @Override
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
        int count = table.getModel().removeAllGaps();
        String message = "Removed " + Integer.toString(count) + " gaps.";
        JOptionPane.showMessageDialog(table, message, "Remove all gaps", JOptionPane.INFORMATION_MESSAGE);
        table.status(message);
        
    }
    
    
    
}
