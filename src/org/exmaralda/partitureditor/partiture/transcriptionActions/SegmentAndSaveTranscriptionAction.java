/*
 * ExportSegmentedTranscriptionAction.java
 *
 * Created on 17. Juni 2003, 17:00
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.io.File;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.SaveSegmentedTranscriptionAsDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.xml.sax.SAXException;

/**
 *
 * @author  thomas
 */
public class SegmentAndSaveTranscriptionAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportSegmentedTranscriptionAction
     * @param t */
    public SegmentAndSaveTranscriptionAction(PartitureTableWithActions t) {
        super("Export segmented transcription...", t);
    }
    
    @Override
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
            if (!(table.getFilename().equals("untitled.exb"))){
                File exbFile = new File(table.getFilename());
                File exsFile = new File(exbFile.getParentFile(), exbFile.getName().replaceAll("\\.exb", ".exs"));
                dialog.setCurrentDirectory(exsFile);
                dialog.setFilename(exsFile.getName());                
            }
            dialog.saveTranscriptionAs(table);
        } catch (FSMException | SAXException ex) {
            int optionChosen = JOptionPane
                    .showConfirmDialog(table, "Segmentation Error(s):\n " + ex.getLocalizedMessage() + "\nEdit errors?",
                    "Segmentation Error(s)", JOptionPane.OK_CANCEL_OPTION);
            if (optionChosen==JOptionPane.OK_OPTION){
                table.getSegmentationErrorsAction.actionPerformed(null);
            }
        }
    }
    
    
}
