/*
 * ExportHTMLTurnListAction.java
 *
 * Created on 17. Juni 2003, 15:55
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import org.exmaralda.partitureditor.deprecated.ExportHTMLTurnListFileDialog;
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
public class ExportHTMLSpeakerContributionListAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportHTMLTurnListAction */
    public ExportHTMLSpeakerContributionListAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Export segment chain list (HTML)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportHTMLSegmentChainListAction!");
        table.commitEdit(true);
        exportHTMLSpeakerContributionList();        
    }
    
    private void exportHTMLSpeakerContributionList(){
         BasicTranscription bt = table.getModel().getTranscription().makeCopy();
         SegmentedTranscription st = bt.toSegmentedTranscription();
         SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION);
         ListTranscription lt = st.toListTranscription(info);
         lt.getBody().sort();
         /*if (!lt.getBody().sort()){
            showListCouldNotBeCompletelyOrderedMessage();
         }*/
         ExportHTMLTurnListFileDialog dialog = new ExportHTMLTurnListFileDialog(lt, table.getModel().getTierFormatTable(), table.htmlDirectory);
         boolean success = dialog.saveHTML(table);
         table.htmlDirectory = dialog.getFilename();
    }
    
    
}
