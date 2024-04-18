/*
 * CompleteTimelineAction.java
 *
 * Created on 19. Juni 2003, 10:20
 */

package org.exmaralda.partitureditor.partiture.timelineActions;

import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaraldaswing.ModifyAbsoluteTimesDialog;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class ShiftAbsoluteTimesAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of CompleteTimelineAction
     * @param t */
    public ShiftAbsoluteTimesAction(PartitureTableWithActions t) {
        super("Modify absolute times...", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("ShiftAbsoluteTimesAction!");
        table.commitEdit(true);
        shiftTimes();
        table.transcriptionChanged = true;        
    }
    
    private void shiftTimes(){
        /*String amountString = javax.swing.JOptionPane.showInputDialog("Enter time value", "0.0");
        if (amountString==null){
            // cancelled
            return;
        }
        try {
            double amount = Double.parseDouble(amountString);
            table.getModel().shiftTimes(amount);
        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), "Invalid number format");
            ex.printStackTrace();
        }*/
        // changed 15-03-2012
        Timeline tl = table.getModel().getTranscription().getBody().getCommonTimeline();
        ModifyAbsoluteTimesDialog dialog = new ModifyAbsoluteTimesDialog(tl, table.selectionStartCol, table.parent, true);
        dialog.setLocationRelativeTo(table);
        dialog.setVisible(true);
        if (dialog.approved){
            if (dialog.getModificationType()==ModifyAbsoluteTimesDialog.SHIFT_MODIFICATION){
                double shift = dialog.getShiftAmount();
                System.out.println("Shifting by: " + shift);
                table.getModel().shiftTimes(shift);     
                table.status("Shifted times by " + Double.toString(shift));                
            } else if (dialog.getModificationType()==ModifyAbsoluteTimesDialog.SCALE_MODIFICATION){
                double scale = dialog.getScaleAmount();
                table.getModel().scaleTimes(scale);             
                table.status("Scaled times by " + Double.toString(scale));                
            }
        }
    }
    
    
}
