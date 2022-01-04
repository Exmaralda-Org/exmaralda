/*
 * GlueTranscriptionsAction.java
 *
 * Created on 1. Juni 2004, 12:37
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.OpenBasicTranscriptionDialog;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.TierFormat;
import org.exmaralda.partitureditor.jexmaraldaswing.MergeDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.xml.sax.SAXException;

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
        //OpenBasicTranscriptionDialog dialog = new OpenBasicTranscriptionDialog(table.homeDirectory);
        
        MergeDialog dialog = new MergeDialog(table.parent, true);
        dialog.setLocationRelativeTo(table);
        dialog.setVisible(true);
        // tell the dialog to show itself and open the transcription the user selects
        // If the user hasn't cancelled and nothing has gone wrong with opening the selected file...
        if (dialog.approved){
            // ... get the newly read transcription from the dialog...
            for (File f : dialog.getFilesToBeMerged()){
                try {
                    BasicTranscription newTranscription = new BasicTranscription(f.getAbsolutePath());
                    table.getModel().getTranscription().merge(newTranscription);
                    for (int pos=0; pos<newTranscription.getBody().getNumberOfTiers(); pos++){
                        Tier tier = newTranscription.getBody().getTierAt(pos);
                        table.getModel().getTierFormatTable().addTierFormat(new TierFormat(tier.getType(), tier.getID()));                        
                    }
                } catch (JexmaraldaException | SAXException ex) {
                    Logger.getLogger(MergeTranscriptionsAction.class.getName()).log(Level.SEVERE, null, ex);
                    javax.swing.JOptionPane.showMessageDialog(table.parent, "Merge failed : " + ex.getMessage());                
                }
                
            }
            table.stratify(table.getModel().getTranscription());
            table.resetData();
            table.transcriptionChanged = true;
        }
        
    }
        
    
}
