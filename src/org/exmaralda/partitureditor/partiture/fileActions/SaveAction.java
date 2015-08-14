/*
 * SaveAction.java
 *
 * Created on 16. Juni 2003, 17:05
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.SaveBasicTranscriptionAsDialog;
import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

/**
 * Saves the transcription under its current file name
 * Menu: File --> Save
 * @author  thomas
 */
public class SaveAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SaveAction */
    public SaveAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Save", icon, t);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                        
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        table.commitEdit(true);
        System.out.println("saveAction!");
        save();
    }

    private void save(){
        if (!table.getFilename().equals("untitled.exb")){
            try{
                if (table.saveTierFormatTable){
                    table.getModel().getTranscription().writeXMLToFile(table.getFilename(),"none", table.getModel().getTierFormatTable());
                } else {
                    table.getModel().getTranscription().writeXMLToFile(table.getFilename(),"none");                    
                }
                table.transcriptionChanged = false;        
                table.status("Transcription " + table.getFilename() + " saved");
            } catch (Throwable t){
                saveTranscription();
            }
        }
        else {
            saveTranscription();
        }
    }
    
    private void saveTranscription(){
        SaveBasicTranscriptionAsDialog dialog = new SaveBasicTranscriptionAsDialog(table.homeDirectory, table.getModel().getTranscription());
        boolean success = dialog.saveTranscriptionAs(table.parent);
        table.transcriptionChanged = !success;        
        table.restoreAction.setEnabled(true);
        if (success){
            table.setFilename(dialog.getFilename());
            table.homeDirectory = dialog.getFilename();
            table.status("Transcription " + dialog.getFilename() + " saved");
        }
    }
    
    
}
