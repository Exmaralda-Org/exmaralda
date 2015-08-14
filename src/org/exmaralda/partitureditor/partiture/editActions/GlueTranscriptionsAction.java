/*
 * GlueTranscriptionsAction.java
 *
 * Created on 1. Juni 2004, 12:37
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.OpenBasicTranscriptionDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.PattexmaraldaDialog;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.ELANConverter;

/**
 *
 * @author  thomas
 */

public class GlueTranscriptionsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of GlueTranscriptionsAction */
    public GlueTranscriptionsAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Glue transcriptions...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("GlueTranscriptionsAction");
        table.commitEdit(true);
        glueTranscriptions();        
    }
    
    private void glueTranscriptions(){
        // Create a dialog for the user to select the file he wants to open
        OpenBasicTranscriptionDialog dialog = new OpenBasicTranscriptionDialog(table.homeDirectory);
        // tell the dialog to show itself and open the transcription the user selects
        // If the user hasn't cancelled and nothing has gone wrong with opening the selected file...
        if (dialog.openTranscription(table.parent)){
            // ... get the newly read transcription from the dialog...
            BasicTranscription newTranscription = dialog.getTranscription();
            PattexmaraldaDialog glueDialog = new PattexmaraldaDialog(table.parent, true, 
                                                                     table.getModel().getTranscription(), 
                                                                     newTranscription);
            
            if (glueDialog.glueTranscriptions()){
                if (glueDialog.getTierIDMappings().length<1) return;
                try{
                    table.getModel().getTranscription().glue(
                            newTranscription,
                            glueDialog.getTierIDMappings(),
                            glueDialog.getMergeTimelines());
                    if (glueDialog.getMergeTimelines()){
                        new ELANConverter().normalize(table.getModel().getTranscription());
                    }
                    table.stratify(table.getModel().getTranscription());
                    table.resetData();
                    table.transcriptionChanged = true;
                } catch (JexmaraldaException je){
                    je.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(table.parent, "Glue failed : " + je.getMessage());
                }
            }
        }
        
    }
        
    
}
