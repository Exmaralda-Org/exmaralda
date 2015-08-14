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


/**
 * Opens a basic transcription for the editor
 * Menu: File --> Open
 * @author  thomas
 */
public class OpenAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    

    /** Creates a new instance of OpenAction */
    public OpenAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        // call super class, set name and icon of the action,
        // set the pointer to the partitur
        super("Open...", icon, t);
        // Associate this action with the key stroke Strg + O
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                
    }
    
    /** implements the abstract method of the superclass */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        // finish any editing actions that might still be in progress on the partitur
        table.commitEdit(true);
        // write a message to stdout
        System.out.println("openAction!");
        // perform the actual code for this action
        openTranscription();
        // reset the frame end position of the partitur
        table.setFrameEndPosition(-2);
        table.clearUndo();
        table.clearSearchResult();
        table.makeColumnVisible(0);
    }
    
    private void openTranscription(){
        boolean proceed = true;
        // check if the user wants to save changes
        if (table.transcriptionChanged){proceed = table.checkSave();}
        // if user has cancelled or something's gone wrong with saving, stop here
        if (!proceed) return;
        // Create a dialog for the user to select the file he wants to open
        OpenBasicTranscriptionDialog dialog = new OpenBasicTranscriptionDialog(table.homeDirectory);
        // tell the dialog to show itself and open the transcription the user selects
        // If the user hasn't cancelled and nothing has gone wrong with opening the selected file...
        if (dialog.openTranscription(table.parent)){
            // ... get the newly read transcription from the dialog...
            BasicTranscription newTranscription = dialog.getTranscription();
            // ... stratify the new transcription
            table.stratify(newTranscription);
            // ... make hidden tiers reappear...
            table.setRowHidden(JCTableEnum.ALLCELLS, false);

            //added 16-08-2012
            table.saveTierFormatTable = newTranscription.getTierFormatTable()!=null;
            
            // ...set the transcription for the partitur to the newly read transcription...
            table.getModel().setTranscription(newTranscription);
            
            // ...update the filename...
            table.homeDirectory = dialog.getFilename();
            table.setFilename(dialog.getFilename());
            // ...if the other directories have not yet been set: set them, too...
            if ((table.rtfDirectory==null) || (table.rtfDirectory.length()==0))   {table.rtfDirectory = table.homeDirectory;}
            if ((table.htmlDirectory==null) || (table.htmlDirectory.length()==0))  {table.htmlDirectory = table.homeDirectory;}
            if ((table.txtDirectory==null) || (table.txtDirectory.length()==0))   {table.txtDirectory = table.homeDirectory;}
            
            // ... empty the link panel of its contents...
            table.linkPanelDialog.getLinkPanel().emptyContents();
            // ... and set its directory ...
            table.linkPanelDialog.getLinkPanel().setDirectory(table.homeDirectory);
            
            // make the changes to the media panel (i.e. set the media file if there is one)
            table.setupMedia();
            // make the changes to the praat panel (version 1.3. and later)
            table.setupPraatPanel();
            table.transcriptionChanged = false;
            table.formatChanged = false;
            

            table.status("Transcription " + dialog.getFilename() + " opened");
            
        }

        //table.reexportHTMLAction.setEnabled(false);
        table.restoreAction.setEnabled(true);
        table.reconfigureAutoSaveThread();

    }
    
}
