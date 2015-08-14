/*
 * GetHIATSegmentationErrorsAction.java
 *
 * Created on 15. Februar 2005, 13:33
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import org.xml.sax.*;


/**
 *
 * @author  thomas
 */
public class GetHIATSegmentationErrorsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of GetHIATSegmentationErrorsAction */
    public GetHIATSegmentationErrorsAction(PartitureTableWithActions t) {
        super("Edit segmentation errors...", t); 
    }

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("getHIATSegmentationErrors!");
        table.commitEdit(true);
        getHIATSegmentationErrors();          
    }
    
    private void getHIATSegmentationErrors() {
        org.exmaralda.partitureditor.jexmaralda.segment.swing.EditSegmentationErrorsDialog dialog = 
            new org.exmaralda.partitureditor.jexmaralda.segment.swing.EditSegmentationErrorsDialog(table.parent, false);
        dialog.setupDialog(table.getModel().getTranscription(), AbstractSegmentation.HIAT_SEGMENTATION, table.hiatFSM);
        dialog.addSearchResultListener(table);
        dialog.show();     
        //dialog.removeAllListeners();
    }
    
    
}
