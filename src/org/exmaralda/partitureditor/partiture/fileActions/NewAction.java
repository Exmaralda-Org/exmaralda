/*
 * NewAction.java
 *
 * Created on 16. Juni 2003, 16:23
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;



/**
 * Creates a new transcription
 * Menu: File --> New
 * @author  thomas
 */
public class NewAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    

    /** Creates a new instance of NewAction */
    public NewAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("New...", icon, t);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));        
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        table.commitEdit(true);
        System.out.println("newAction!");
        newTranscription();
        table.transcriptionChanged = false;
        table.clearUndo();
        table.clearSearchResult();
        table.setFrameEndPosition(-2);
    }
    
    private void newTranscription(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        table.getModel().resetTranscription();
        table.showAllTiers();
        table.setFilename("untitled.exb");
        table.linkPanelDialog.getLinkPanel().emptyContents();
        table.largeTextField.setText("");
        //table.reexportHTMLAction.setEnabled(false);
        table.restoreAction.setEnabled(false);
        table.reconfigureAutoSaveThread();
        table.setupMedia();
        table.status("New transcription");
    }
    
    
}
