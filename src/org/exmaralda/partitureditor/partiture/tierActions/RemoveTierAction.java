/*
 * RemoveTierAction.java
 *
 * Created on 17. Juni 2003, 14:46
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class RemoveTierAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RemoveTierAction */
    public RemoveTierAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Remove tier...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("removeTierAction!");
        table.commitEdit(true);
        removeTier();
        table.transcriptionChanged = true;
    }
    
    private void removeTier(){
        javax.swing.JOptionPane askDialog = new javax.swing.JOptionPane();
        int confirmation = askDialog.showConfirmDialog( table,
            "Are you sure you want to remove the selected tier(s)? ",
            "Question",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.QUESTION_MESSAGE,
            null);
        if (confirmation==javax.swing.JOptionPane.YES_OPTION) {
            boolean aSeriesOfRowsIsSelected = ((table.selectionStartRow != table.selectionEndRow) && (table.selectionStartRow != -1) && (table.selectionEndRow != -1));
            if (table.undoEnabled){
                UndoInformation undoInfo = new UndoInformation(table, "Remove tier");
                undoInfo.memorizeTranscription(table);
                table.addUndo(undoInfo);
            }
            if (!aSeriesOfRowsIsSelected){
                int row = table.selectionStartRow;
                table.getModel().removeTier(row);
                if (row <= table.getFrameEndPosition()){
                    table.setFrameEndPosition(table.getFrameEndPosition()-1);
                    table.getModel().fireRowLabelsFormatChanged();                    
                }
            }
            else {
                int firstRow = table.selectionStartRow;
                int lastRow = table.selectionEndRow;
                table.getModel().removeTiers(firstRow, lastRow);
                if (firstRow <= table.getFrameEndPosition()){
                    table.setFrameEndPosition(table.getFrameEndPosition() - (Math.min(table.getFrameEndPosition(), lastRow) - firstRow +1));
                    table.getModel().fireRowLabelsFormatChanged();                                        
                }
            }
        }
    }
    
    
}
