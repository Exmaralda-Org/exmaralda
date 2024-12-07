/*
 * EditSpeakertableAction.java
 *
 * Created on 17. Juni 2003, 09:49
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaraldaswing.EditSpeakerTableDialog;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.partiture.*;

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
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editSpeakertableAction!");
        table.commitEdit(true);
        editSpeakertable();
    }
    
    private void editSpeakertable(){
        BasicTranscription transcription = table.getModel().getTranscription();
        String[] speakerIDsBefore = transcription.getHead().getSpeakertable().getAllSpeakerIDs();
        int[] tiersWithAutoDisplayName = transcription.getTierNumbersWithAutoDisplayName();
        EditSpeakerTableDialog dialog = new EditSpeakerTableDialog(table.parent,true, transcription.getHead().getSpeakertable());
        if (dialog.editSpeakertable()){
            transcription.getHead().setSpeakertable(dialog.getSpeakertable());
            table.getModel().getTranscription().checkSpeakers();
            if (dialog.getAutoAdd()){
                // auto add tiers for new speakers
                HashSet<String> before = new HashSet<String>();
                Collections.addAll(before, speakerIDsBefore);
                HashSet<String> after = new HashSet<String>();
                Collections.addAll(after, dialog.getSpeakertable().getAllSpeakerIDs());
                if (after.removeAll(before)){
                    for (String newID : after){                        
                        Tier newTier = new Tier(transcription.getBody().getFreeID(), newID, "v", "t");
                        String displayName = newTier.getDescription(dialog.getSpeakertable());
                        newTier.setDisplayName(displayName);
                        table.getModel().addTier(newTier);
                        System.out.println("Tier inserted for " + newID);
                    }
                }
            }
            transcription.makeAutoDisplayName(tiersWithAutoDisplayName);
            table.getModel().fireRowLabelsChanged();
            table.transcriptionChanged = true;
            table.status("Speaker table edited");
        }
    }
    
    
}
