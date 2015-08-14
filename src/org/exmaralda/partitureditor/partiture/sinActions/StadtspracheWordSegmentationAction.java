/*
 * ExportHTMLUtteranceListAction.java
 *
 * Created on 17. Juni 2003, 16:00
 */

package org.exmaralda.partitureditor.partiture.sinActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;


/**
 *
 * @author  thomas
 */
public class StadtspracheWordSegmentationAction extends AbstractFSMSegmentationAction {
    
    
    /** Creates a new instance of ExportHTMLUtteranceListAction */
    public StadtspracheWordSegmentationAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Wort-Segmentierung", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("StadtsprachWordSegmentation!");
        table.commitEdit(true);
        wordSegmentation();        
    }
    
    private void wordSegmentation() {
        int start = table.selectionStartCol;
        int end = Math.min(table.getModel().getNumColumns()-1, table.selectionEndCol+1);
        
        String startTLI = table.getModel().getTimelineItem(start).getID();
        String endTLI = table.getModel().getTimelineItem(end).getID();

        String tierID;
        if (table.selectionStartRow>=0){
            tierID = table.getModel().getTier(table.selectionStartRow).getID();
        } else {
            SingleTierSelectionDialog sstd = new SingleTierSelectionDialog(table.getModel().getTranscription(), table.parent, true);
            sstd.setVisible(true);
            if (sstd.getReturnStatus()==SingleTierSelectionDialog.RET_CANCEL) return;
            tierID = sstd.getSelectedTierID();
        }


        boolean proceed=true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        org.exmaralda.partitureditor.jexmaralda.segment.StadtspracheSegmentation segmentor = new org.exmaralda.partitureditor.jexmaralda.segment.StadtspracheSegmentation();
        BasicTranscription segmentedSelection;
        try {
            segmentedSelection = segmentor.wordSegmentation(table.getModel().getTranscription(), startTLI, endTLI, tierID);
        } catch (JexmaraldaException ex) {
            javax.swing.JOptionPane.showMessageDialog(table.parent, ex.getLocalizedMessage());
            return;
        }            
        table.showAllTiers();
        table.getModel().setTranscription(segmentedSelection);
        table.setFilename("untitled.exb");
        table.linkPanelDialog.getLinkPanel().emptyContents();
        table.largeTextField.setText("");
        //table.reexportHTMLAction.setEnabled(false);
        table.restoreAction.setEnabled(false);        
        
        
    }
    
    
}
