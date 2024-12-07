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
public class EditMetaInformationAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditMetaInformationAction */
    public EditMetaInformationAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Meta information...", icon, t);                
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editMetaInformationAction!");
        table.commitEdit(true);
        editMetaInformation();
    }
    
    private void editMetaInformation(){
        BasicTranscription transcription = table.getModel().getTranscription();
        EditMetaInformationDialog dialog = new EditMetaInformationDialog(table.parent, true, transcription.getHead().getMetaInformation());
        dialog.setCurrentFilename(table.getFilename());
        if (dialog.editMetaInformation()){
            transcription.getHead().setMetaInformation(dialog.getMetaInformation());
            table.setupMedia();
            table.setupPraatPanel();
            table.transcriptionChanged = true;
            table.status("Meta information edited");
        }
    }
    
    
}
