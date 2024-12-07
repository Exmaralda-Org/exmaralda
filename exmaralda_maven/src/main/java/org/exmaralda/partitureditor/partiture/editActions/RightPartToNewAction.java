/*
 * RightPartToNewAction.java
 *
 * Created on 17. Juni 2003, 13:09
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import com.klg.jclass.table.*;

/**
 *
 * @author  thomas
 */
public class RightPartToNewAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of RightPartToNewAction */
    public RightPartToNewAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Right part to new", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("rightPartToNewAction!");
        table.commitEdit(true);
        rightPartToNew();
        table.transcriptionChanged = true;
        table.setFrameEndPosition(-2);
    }

    private void rightPartToNew(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        BasicTranscription newTranscription;        
        newTranscription = table.getModel().getPartOfTranscription(table.getIndicesOfVisibleRows(), table.selectionStartCol, table.getModel().getNumColumns()-1, true);
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
