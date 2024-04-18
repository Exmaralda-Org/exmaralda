/*
 * RemoveInterpolatedTimesAction.java
 *
 * Created on 19. Juni 2003, 10:17
 */

package org.exmaralda.partitureditor.partiture.timelineActions;

import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class RemoveTimesAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RemoveInterpolatedTimesAction
     * @param t */
    public RemoveTimesAction(PartitureTableWithActions t) {
        super("Remove times", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("removeTimesAction!");
        table.commitEdit(true);
        int value = JOptionPane.showConfirmDialog(table, "Are you sure?", "Remove all absolute time values", JOptionPane.YES_NO_OPTION);
        if (value==JOptionPane.NO_OPTION) return;
        removeTimes();
        table.transcriptionChanged = true;        
    }
    
    
    private void removeTimes(){
        table.getModel().removeTimes();
        table.status("Removed absolute times from timeline");
        
    }
    
    
    
}
