/*
 * SaveAsAction.java
 *
 * Created on 17. Juni 2003, 09:12
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.SaveBasicTranscriptionAsDialog;
import org.exmaralda.partitureditor.partiture.*;

/**
 * Saves the transcription under a new filename
 * Menu: File --> Save As
 * @author  thomas
 */
public class SaveAsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SaveAsAction */
    public SaveAsAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Save as...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        table.commitEdit(true);
        System.out.println("saveAsAction!");
        saveTranscription();
    }
    
    private void saveTranscription(){
        SaveBasicTranscriptionAsDialog dialog = new SaveBasicTranscriptionAsDialog(table.homeDirectory, table.getModel().getTranscription(), table.getModel().getTierFormatTable());
        dialog.setSaveTierFormatTable(table.saveTierFormatTable);
        boolean success = dialog.saveTranscriptionAs(table.parent);
        table.transcriptionChanged = !success;        
        table.restoreAction.setEnabled(true);
        if (success) {
            table.setFilename(dialog.getFilename());
            table.homeDirectory = dialog.getFilename();
            table.saveTierFormatTable = dialog.isSaveTierFormatTable();
            table.status("Transcription " + dialog.getFilename() + " saved");
        }
    }
    
}
