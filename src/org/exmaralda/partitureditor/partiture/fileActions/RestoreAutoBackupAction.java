/*
 * OpenAction.java
 *
 * Created on 16. Juni 2003, 16:37
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.OpenBasicTranscriptionDialog;
import com.klg.jclass.table.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.io.File;
import org.exmaralda.partitureditor.jexmaraldaswing.RestoreAutoBackupDialog;
import org.xml.sax.SAXException;


/**
 * Opens a basic transcription for the editor
 * Menu: File --> Open
 * @author  thomas
 */
public class RestoreAutoBackupAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    

    /** Creates a new instance of OpenAction */
    public RestoreAutoBackupAction(PartitureTableWithActions t) {
        // call super class, set name and icon of the action,
        // set the pointer to the partitur
        super("Restore Auto Backup...", t);
    }
    
    /** implements the abstract method of the superclass */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        // finish any editing actions that might still be in progress on the partitur
        table.commitEdit(true);
        // write a message to stdout
        System.out.println("RestoreAutoBackupAction!");
        // perform the actual code for this action
        restoreAutoBackup();
        // reset the frame end position of the partitur
        table.setFrameEndPosition(-2);
        table.clearUndo();
        table.clearSearchResult();
        table.makeColumnVisible(0);
    }
    
    private void restoreAutoBackup(){
        boolean proceed = true;
        // check if the user wants to save changes
        if (table.transcriptionChanged){proceed = table.checkSave();}
        // if user has cancelled or something's gone wrong with saving, stop here
        if (!proceed) return;
        // Create a dialog for the user to select the file he wants to open
        RestoreAutoBackupDialog dialog = new RestoreAutoBackupDialog(table.parent, true, table);
        dialog.setLocationRelativeTo(table);
        // tell the dialog to show itself and open the transcription the user selects
        // If the user hasn't cancelled and nothing has gone wrong with opening the selected file...
        dialog.setVisible(true);
        
        if (!dialog.restore) return;
        
        File file = dialog.getSelectedFile();
        
        try {
            BasicTranscription restoredTranscription = new BasicTranscription(file.getAbsolutePath());
            // ... stratify the new transcription
            table.stratify(restoredTranscription);
            // ... make hidden tiers reappear...
            table.setRowHidden(JCTableEnum.ALLCELLS, false);
            table.getModel().setTranscription(restoredTranscription);
            table.setupMedia();
            table.setupPraatPanel();
            table.setFrameEndPosition(-2);        
            table.getModel().fireRowLabelsFormatChanged();
            table.status("Transcription backup " + file.getAbsolutePath() + " restored ");

            //restoreAction.setEnabled(false);
        } catch (JexmaraldaException | SAXException e){
            javax.swing.JOptionPane.showMessageDialog(table.parent,
                "Transcription could not be restored: " + e.getLocalizedMessage(), "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        
        

        //table.reexportHTMLAction.setEnabled(false);
        table.restoreAction.setEnabled(true);
        table.reconfigureAutoSaveThread();

    }
    
}
