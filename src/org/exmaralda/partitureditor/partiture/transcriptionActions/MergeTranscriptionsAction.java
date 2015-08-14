/*
 * GlueTranscriptionsAction.java
 *
 * Created on 1. Juni 2004, 12:37
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.OpenBasicTranscriptionDialog;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */

public class MergeTranscriptionsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of GlueTranscriptionsAction */
    public MergeTranscriptionsAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Merge transcriptions...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("MergeTranscriptionsAction");
        table.commitEdit(true);
        mergeTranscriptions();        
    }
    
    private void mergeTranscriptions(){
        // Create a dialog for the user to select the file he wants to open
        OpenBasicTranscriptionDialog dialog = new OpenBasicTranscriptionDialog(table.homeDirectory);
        // tell the dialog to show itself and open the transcription the user selects
        // If the user hasn't cancelled and nothing has gone wrong with opening the selected file...
        if (dialog.openTranscription(table.parent)){
            // ... get the newly read transcription from the dialog...
            BasicTranscription newTranscription = dialog.getTranscription();
            try {
                table.getModel().getTranscription().merge(newTranscription);
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(table.parent, "Merge failed : " + ex.getMessage());                
            }
            table.stratify(table.getModel().getTranscription());
            table.resetData();
            table.transcriptionChanged = true;
        }
        
    }
        
    
}
