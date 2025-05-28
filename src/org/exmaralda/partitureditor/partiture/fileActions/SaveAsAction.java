/*
 * SaveAsAction.java
 *
 * Created on 17. Juni 2003, 09:12
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import java.io.File;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.SaveBasicTranscriptionAsDialog;
import org.exmaralda.partitureditor.partiture.*;

/**
 * Saves the transcription under a new filename
 * Menu: File --> Save As
 * @author  thomas
 */
public class SaveAsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SaveAsAction
     * @param t
     * @param icon */
    public SaveAsAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Save as...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        table.commitEdit(true);
        System.out.println("saveAsAction!");
        saveTranscription();
    }
    
    private void saveTranscription(){
        // 28-05-2025 issue #527
        String startDirectory = table.homeDirectory;
        if (table.getFilename().equals("untitled.exb")){
            String referencedFile = table.getModel().getTranscription().getHead().getMetaInformation().getReferencedFile();
            if (referencedFile!=null && referencedFile.length()>0){
                int index = new File(referencedFile).getName().lastIndexOf(".");
                if (index<0){
                    index = new File(referencedFile).getName().length();
                }
                File suggestedFile = new File(new File(referencedFile).getParentFile(), new File(referencedFile).getName().substring(0, index) + ".exb");
                System.out.println("Suggested filename " + suggestedFile.getAbsolutePath());
                if (suggestedFile.getParentFile().exists() && (!suggestedFile.exists())){
                    startDirectory = suggestedFile.getAbsolutePath();
                }
            }
        }
        SaveBasicTranscriptionAsDialog dialog = new SaveBasicTranscriptionAsDialog(startDirectory, table.getModel().getTranscription(), table.getModel().getTierFormatTable());
        dialog.setSaveTierFormatTable(table.saveTierFormatTable);
        dialog.prettyPrint = table.prettyPrint;
        boolean success = dialog.saveTranscriptionAs(table.parent);
        table.transcriptionChanged = !success;        
        table.restoreAction.setEnabled(true);
        if (success) {
            table.setFilename(dialog.getFilename());
            table.homeDirectory = dialog.getFilename();
            table.saveTierFormatTable = dialog.isSaveTierFormatTable();
            table.status("Transcription " + dialog.getFilename() + " saved");
        }
    }
    
}
