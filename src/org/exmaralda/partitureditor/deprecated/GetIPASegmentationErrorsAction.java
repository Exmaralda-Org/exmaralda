/*
 * GetIPASegmentationErrors.java
 *
 * Created on 23. Juni 2005, 10:00
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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
public class GetIPASegmentationErrorsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of GetHIATSegmentationErrorsAction */
    public GetIPASegmentationErrorsAction(PartitureTableWithActions t) {
        super("Edit segmentation errors...", t); 
    }

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("getIPASegmentationErrors!");
        table.commitEdit(true);
        getIPASegmentationErrors();          
    }
    
    private void getIPASegmentationErrors() {
        org.exmaralda.partitureditor.jexmaralda.segment.swing.EditSegmentationErrorsDialog dialog = 
            new org.exmaralda.partitureditor.jexmaralda.segment.swing.EditSegmentationErrorsDialog(table.parent, false);
        dialog.setupDialog(table.getModel().getTranscription(), AbstractSegmentation.IPA_SEGMENTATION, table.ipaFSM);
        dialog.addSearchResultListener(table);
        dialog.show();     
        //dialog.removeAllListeners();
    }
    
}
