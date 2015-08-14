/*
 * LeftPartToNewAction.java
 *
 * Created on 17. Juni 2003, 13:05
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import com.klg.jclass.table.*;

/**
 *
 * @author  thomas
 */
public class LeftPartToNewAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of LeftPartToNewAction */
    public LeftPartToNewAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Left part to new", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("leftPartToNewAction!");
        table.commitEdit(true);
        leftPartToNew();
        table.transcriptionChanged = true;
        table.setFrameEndPosition(-2);
    }
    
    private void leftPartToNew(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        BasicTranscription newTranscription;        
        newTranscription = table.getModel().getPartOfTranscription(table.getIndicesOfVisibleRows(),0,table.selectionStartCol, true);
        table.setRowHidden(JCTableEnum.ALLCELLS, false);

        // added and changed 02-12-2009
        TierFormatTable tft = table.getModel().getTierFormatTable();
        table.getModel().setTranscriptionAndTierFormatTable(newTranscription, tft);
        //table.getModel().setTranscription(newTranscription);

        table.setFilename("untitled.exb");
        table.linkPanelDialog.getLinkPanel().emptyContents();
        table.largeTextField.setText("");
        //table.reexportHTMLAction.setEnabled(false);
        table.restoreAction.setEnabled(false);
        table.clearUndo();
    }
    
    
}
