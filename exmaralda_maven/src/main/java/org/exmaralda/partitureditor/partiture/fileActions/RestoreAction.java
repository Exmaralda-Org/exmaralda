/*
 * RestoreAction.java
 *
 * Created on 16. Juni 2003, 16:51
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import com.klg.jclass.table.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class RestoreAction extends  org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RestoreAction */
    public RestoreAction(PartitureTableWithActions t) {
        super("Restore", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        table.commitEdit(true);
        System.out.println("restoreAction!");
        restoreTranscription();
        table.transcriptionChanged = false;
        table.clearUndo();
    }

    private void restoreTranscription(){
        javax.swing.JOptionPane askDialog = new javax.swing.JOptionPane();
        int confirmation = askDialog.showConfirmDialog( table,
                "Are you sure you want to restore the last saved version?\n (All changes since last save will be lost) ",
                "Question",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null);
        if (confirmation==javax.swing.JOptionPane.YES_OPTION) {
            try {
                BasicTranscription restoredTranscription = new BasicTranscription(table.getFilename());
                // ... stratify the new transcription
                table.stratify(restoredTranscription);
                // ... make hidden tiers reappear...
                table.setRowHidden(JCTableEnum.ALLCELLS, false);
                table.getModel().setTranscription(restoredTranscription);
                table.setupMedia();
                table.setupPraatPanel();
                table.setFrameEndPosition(-2);        
                table.getModel().fireRowLabelsFormatChanged();
                table.status("Transcription " + table.getFilename() + " restored");
                
                //restoreAction.setEnabled(false);
            } catch (Exception e){
                javax.swing.JOptionPane.showMessageDialog(table.parent,
                    "Transcription could not be restored: " + e.getLocalizedMessage(), "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
}
