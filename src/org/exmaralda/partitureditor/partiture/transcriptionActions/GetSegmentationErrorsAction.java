/*
 * GetHIATSegmentationErrorsAction.java
 *
 * Created on 15. Februar 2005, 13:33
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.partiture.*;


/**
 *
 * @author  thomas
 */
public class GetSegmentationErrorsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of GetHIATSegmentationErrorsAction */
    public GetSegmentationErrorsAction(PartitureTableWithActions t) {
        super("Segmentation errors...", t);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("getSegmentationErrors!");
        table.commitEdit(true);
        getSegmentationErrors();          
    }
    
    private void getSegmentationErrors() {
        org.exmaralda.partitureditor.jexmaralda.segment.swing.EditSegmentationErrorsDialog dialog = 
            new org.exmaralda.partitureditor.jexmaralda.segment.swing.EditSegmentationErrorsDialog(table.parent, false);
        dialog.setupDialog( table.getModel().getTranscription(),
                            AbstractSegmentation.getSegmentationCode(table.preferredSegmentation),
                            table.getFSMForPreferredSegmentation());
        dialog.addSearchResultListener(table);
        dialog.show();     
        //dialog.removeAllListeners();
    }
    
    
}
