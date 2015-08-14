/*
 * ExportXMLSpeakerContributionList.java
 *
 * Created on 9. Juli 2003, 11:45
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import org.exmaralda.partitureditor.deprecated.SaveListTranscriptionAsDialog;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  thomas
 */
public class ExportXMLSpeakerContributionListAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportXMLSpeakerContributionList */
    public ExportXMLSpeakerContributionListAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Export segment chain list (XML)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportXMLSegmentChainListAction!");
        table.commitEdit(true);
        exportXMLSpeakerContributionList();        
    }
    
    private void exportXMLSpeakerContributionList(){
         BasicTranscription bt = table.getModel().getTranscription().makeCopy();
         SegmentedTranscription st = bt.toSegmentedTranscription();
         SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION);
         ListTranscription lt = st.toListTranscription(info);
         lt.getBody().sort();
         /*if (!lt.getBody().sort()){
            showListCouldNotBeCompletelyOrderedMessage();
         }*/
         SaveListTranscriptionAsDialog dialog = new SaveListTranscriptionAsDialog(table.filename, lt);
         boolean success = dialog.saveTranscriptionAs(table);
    }
    
    
}
