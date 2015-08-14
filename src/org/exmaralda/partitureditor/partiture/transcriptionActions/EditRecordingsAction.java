/*
 * EditMetaInformationAction.java
 *
 * Created on 17. Juni 2003, 09:44
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import org.exmaralda.partitureditor.jexmaraldaswing.EditMetaInformationDialog;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
/**
 *
 * opens a dialog for editing the meta information 
 * Menu: File --> Edit meta information
 * @author  thomas
 */
public class EditRecordingsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditMetaInformationAction */
    public EditRecordingsAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Recordings...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editRecordingsAction!");
        table.commitEdit(true);
        editRecordings();
    }
    
    private void editRecordings(){
        BasicTranscription transcription = table.getModel().getTranscription();
        EditReferencedFilesDialog dialog = new EditReferencedFilesDialog(null, true, transcription.getHead().getMetaInformation().getReferencedFiles());
        dialog.defaultDirectory = table.getFilename();
        dialog.setLocationRelativeTo(this.table);
        dialog.setVisible(true);

        transcription.getHead().getMetaInformation().setReferencedFiles(dialog.getReferencedFiles());
        table.setupMedia();
        table.setupPraatPanel();
        table.transcriptionChanged = true;

        table.status("Media file set to " + table.player.getSoundFile());

    }
    
    
}
