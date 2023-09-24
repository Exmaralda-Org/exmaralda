/*
 * EditMetaInformationAction.java
 *
 * Created on 17. Juni 2003, 09:44
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.awt.Container;
import javax.swing.JFrame;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
/**
 *
 * opens a dialog for editing the meta information 
 * Menu: File --> Edit meta information
 * @author  thomas
 */
public class EditRecordingsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditMetaInformationAction
     * @param t */
    public EditRecordingsAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Recordings...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editRecordingsAction!");
        table.commitEdit(true);
        editRecordings();
    }
    
    private void editRecordings(){
        BasicTranscription transcription = table.getModel().getTranscription();
        JFrame frame = null;
        Container c = table.getTopLevelAncestor();
        if (c instanceof JFrame){
            frame = (JFrame)c;
        }
        EditReferencedFilesDialog dialog = new EditReferencedFilesDialog(frame, true, transcription.getHead().getMetaInformation().getReferencedFiles());
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
