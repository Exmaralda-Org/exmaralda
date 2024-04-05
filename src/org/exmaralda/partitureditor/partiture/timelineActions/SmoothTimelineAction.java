/*
 * RemoveAllGapsAction.java
 *
 * Created on 19. Juni 2003, 10:18
 */

package org.exmaralda.partitureditor.partiture.timelineActions;

import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class SmoothTimelineAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RemoveAllGapsAction
     * @param t */
    public SmoothTimelineAction(PartitureTableWithActions t) {
        super("Smooth timeline...", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("smoothTimelineAction!");
        table.commitEdit(true);
        double threshhold = 0.01;
        // TODO: INSERT DIALOG FOR ASKING ABOUT THRESHHOLD
        String amountString = javax.swing.JOptionPane.showInputDialog("Enter threshhold (in seconds)", "0.01");
        try {
            threshhold = Math.abs(Double.parseDouble(amountString));
            smoothTimeline(threshhold);
            table.transcriptionChanged = true;
        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), "Invalid number format");
            System.out.println(ex.getLocalizedMessage());
        }

    }
    
    private void smoothTimeline(double threshhold){
        table.getModel().smoothTimeline(threshhold);
    }
    
    
    
}
