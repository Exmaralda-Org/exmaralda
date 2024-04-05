/*
 * RemoveInterpolatedTimesAction.java
 *
 * Created on 19. Juni 2003, 10:17
 */

package org.exmaralda.partitureditor.partiture.timelineActions;

import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class RemoveInterpolatedTimesAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RemoveInterpolatedTimesAction
     * @param t */
    public RemoveInterpolatedTimesAction(PartitureTableWithActions t) {
        super("Remove interpolated times", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("removeInterpolatedTimesAction!");
        table.commitEdit(true);
        removeInterpolatedTimes();
        table.transcriptionChanged = true;        
    }
    
    
    private void removeInterpolatedTimes(){
        table.getModel().removeInterpolatedTimes();
    }
    
    
    
}
