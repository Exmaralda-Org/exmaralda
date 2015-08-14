/*
 * CompleteTimelineAction.java
 *
 * Created on 19. Juni 2003, 10:20
 */

package org.exmaralda.partitureditor.partiture.timelineActions;

import javax.swing.Action;
import javax.swing.KeyStroke;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 *
 * @author  thomas
 */
public class CompleteTimelineAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of CompleteTimelineAction */
    public CompleteTimelineAction(PartitureTableWithActions t) {
        super("Interpolate timeline...", t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift I"));                    
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("completeTimelineAction!");
        table.commitEdit(true);
        completeTimeline();
        table.transcriptionChanged = true;        
    }
    
    private void completeTimeline(){
        InterpolationDialog dialog = new InterpolationDialog(table.parent, true);
        dialog.setLocationRelativeTo(table);
        dialog.setVisible(true);
        if (dialog.approved){
            boolean linear = dialog.getLinearInterpolation();
            table.getModel().completeTimeline(linear);
        }
    }
    
    
}
