/*
 * EditSpeakertableAction.java
 *
 * Created on 17. Juni 2003, 09:49
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import org.exmaralda.partitureditor.jexmaraldaswing.EditSpeakerTableDialog;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 *
 * opens a dialog for editing the speakertable
 * Menu: File --> Edit speakertable
 * @author  thomas
 */
public class EditSpeakertableAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditSpeakertableAction */
    public EditSpeakertableAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Speakertable...", icon, t);                        
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editSpeakertableAction!");
        table.commitEdit(true);
        editSpeakertable();
    }
    
    private void editSpeakertable(){
        BasicTranscription transcription = table.getModel().getTranscription();
        int[] tiersWithAutoDisplayName = transcription.getTierNumbersWithAutoDisplayName();
        EditSpeakerTableDialog dialog = new EditSpeakerTableDialog(table.parent,true, transcription.getHead().getSpeakertable());
        if (dialog.editSpeakertable()){
            transcription.getHead().setSpeakertable(dialog.getSpeakertable());
            table.getModel().getTranscription().checkSpeakers();
            transcription.makeAutoDisplayName(tiersWithAutoDisplayName);
            table.getModel().fireRowLabelsChanged();
            table.transcriptionChanged = true;
            table.status("Speaker table edited");
        }
    }
    
    
}
