/*
 * ExportHTMLUtteranceListAction.java
 *
 * Created on 17. Juni 2003, 16:00
 */

package org.exmaralda.partitureditor.deprecated.segmentationActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.fsm.*;
import java.util.*;
import org.exmaralda.partitureditor.deprecated.ExportHTMLTurnListFileDialog;
import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.xml.sax.*;


/**
 *
 * @author  thomas
 */
public class ExportHIATHTMLUtteranceListAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportHTMLUtteranceListAction */
    public ExportHIATHTMLUtteranceListAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Utterance list (HTML)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportHIATHTMLUtteranceListAction!");
        table.commitEdit(true);
        exportHIATHTMLUtteranceList();        
    }
    
    private void exportHIATHTMLUtteranceList() {
         try{
             BasicTranscription bt = table.getModel().getTranscription().makeCopy();
             org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation hs = new org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation(table.hiatFSM);
             ListTranscription lt = hs.BasicToUtteranceList(bt);
             Vector scsThatAreNot = new Vector();
             boolean everythingOK = lt.getBody().areAllSpeakerContributionsInTheCommonTimeline(scsThatAreNot);
             if (!everythingOK){
                 String message = "The list cannot be completely ordered.";                                  
                 int usersChoice = showOrderingProblemDialog(message);
                 switch (usersChoice){
                     case CANCEL_OPTION : return; // cancel the exportAction
                     case CONTINUE_OPTION : break; // do nothing, i.e. continue
                     case EDIT_OPTION : 
                         SpeakerContributionNavigationDialog dialog =
                            new SpeakerContributionNavigationDialog(table.parent, false, scsThatAreNot, hs.beforeAugment, table);
                         dialog.show();
                         return;
                 }
             }
             ExportHTMLTurnListFileDialog dialog = new ExportHTMLTurnListFileDialog(lt, table.getModel().getTierFormatTable(), table.htmlDirectory);
             if (table.HIATUtteranceList2HTMLStylesheet.length()>0){
                 dialog.saveHTMLWithStylesheet(table, table.HIATUtteranceList2HTMLStylesheet);
             } else {
                dialog.saveHTML(table);
             }
             table.htmlDirectory = dialog.getFilename();
         } catch (SAXException se){
            processSAXException(se);
         } catch (FSMException fsme){
            processFSMException(fsme);
         } 
    }
    
    
}
