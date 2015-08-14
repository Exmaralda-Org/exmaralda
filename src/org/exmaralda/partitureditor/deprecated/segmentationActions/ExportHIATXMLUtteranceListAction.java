/*
 * ExportHIATXMLUtteranceListAction.java
 *
 * Created on 9. Juli 2003, 10:54
 */

package org.exmaralda.partitureditor.deprecated.segmentationActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.fsm.*;
import java.util.*;
import org.exmaralda.partitureditor.deprecated.SaveListTranscriptionAsDialog;
import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class ExportHIATXMLUtteranceListAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportHIATXMLUtteranceListAction */
    public ExportHIATXMLUtteranceListAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Utterance list (XML)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportHIATHTMLUtteranceListAction!");
        table.commitEdit(true);
        exportHIATXMLUtteranceList();             
    }
    
    private void exportHIATXMLUtteranceList() {
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
             SaveListTranscriptionAsDialog dialog = new SaveListTranscriptionAsDialog(table.filename, lt);
             boolean success = dialog.saveTranscriptionAs(table);
         } catch (SAXException se){
            processSAXException(se);
         } catch (FSMException fsme){
            processFSMException(fsme);
         } 
    }
    
    
}
