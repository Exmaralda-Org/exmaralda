/*
 * ExportSegmentedTranscriptionAction.java
 *
 * Created on 17. Juni 2003, 17:00
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.SaveSegmentedTranscriptionAsDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;

/**
 *
 * @author  thomas
 */
public class SegmentAndSaveTranscriptionAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportSegmentedTranscriptionAction */
    public SegmentAndSaveTranscriptionAction(PartitureTableWithActions t) {
        super("Export segmented transcription...", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportSegmentedTranscriptionAction!");
        table.commitEdit(true);
        exportSegmentedTranscription();        
    }
    
    private void exportSegmentedTranscription(){
        try {
            BasicTranscription bt = table.getModel().getTranscription().makeCopy();
            AbstractSegmentation as = table.getAbstractSegmentation();
            SegmentedTranscription st = as.BasicToSegmented(bt);
            st.setEXBSource(table.filename);
            org.exmaralda.partitureditor.jexmaralda.segment.SegmentCountForMetaInformation.count(st);
            SaveSegmentedTranscriptionAsDialog dialog = new SaveSegmentedTranscriptionAsDialog(table.homeDirectory, st);
            dialog.saveTranscriptionAs(table);
        } catch (Exception ex) {
            int optionChosen = JOptionPane
                    .showConfirmDialog(table, "Segmentation Error(s):\n " + ex.getLocalizedMessage() + "\nEdit errors?",
                    "Segmentation Error(s)", JOptionPane.OK_CANCEL_OPTION);
            if (optionChosen==JOptionPane.OK_OPTION){
                table.getSegmentationErrorsAction.actionPerformed(null);
            }
        }
    }
    
    
}
