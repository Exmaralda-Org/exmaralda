/*
 * SelectionToNewAction.java
 *
 * Created on 17. Juni 2003, 12:59
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  thomas
 */
public class SelectionToNewAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SelectionToNewAction */
    public SelectionToNewAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Selection to new", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("selectionToNewAction!");
        table.commitEdit(true);
        selectionToNew();
        table.transcriptionChanged = true;
        table.setFrameEndPosition(-2);

    }
    
    private void selectionToNew(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        BasicTranscription newTranscription = table.getCurrentSelectionAsNewTranscription();
        table.showAllTiers();

        // added and changed 02-12-2009
        TierFormatTable tft = table.getModel().getTierFormatTable();
        table.getModel().setTranscriptionAndTierFormatTable(newTranscription, tft);

        //table.getModel().setTranscription(newTranscription);
        table.setFilename("untitled.exb");
        // added 09-06-2009
        table.anchorTimelineItemAction.actionPerformed(null);
        table.linkPanelDialog.getLinkPanel().emptyContents();
        table.largeTextField.setText("");
        //table.reexportHTMLAction.setEnabled(false);
        table.restoreAction.setEnabled(false);
        table.clearUndo();

    }
    
    
}
