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
public class GetDIDASegmentationErrorsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of GetHIATSegmentationErrorsAction */
    public GetDIDASegmentationErrorsAction(PartitureTableWithActions t) {
        super("Edit segmentation errors...", t); 
    }

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("getDIDASegmentationErrors!");
        table.commitEdit(true);
        getDIDASegmentationErrors();          
    }
    
    private void getDIDASegmentationErrors() {
        org.exmaralda.partitureditor.jexmaralda.segment.swing.EditSegmentationErrorsDialog dialog = 
            new org.exmaralda.partitureditor.jexmaralda.segment.swing.EditSegmentationErrorsDialog(table.parent, false);
        dialog.setupDialog(table.getModel().getTranscription(), AbstractSegmentation.DIDA_SEGMENTATION, table.didaFSM);
        dialog.addSearchResultListener(table);
        dialog.show();     
        //dialog.removeAllListeners();
    }
    
    
}
