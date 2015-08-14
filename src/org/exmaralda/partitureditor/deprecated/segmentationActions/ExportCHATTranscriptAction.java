/*
 * ExportCHATTranscriptAction.java
 *
 * Created on 23. Maerz 2004, 13:12
 */

package org.exmaralda.partitureditor.deprecated.segmentationActions;

import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.exmaralda.partitureditor.jexmaraldaswing.SpeakerContributionNavigationDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.EventNavigationDialog;
import org.exmaralda.partitureditor.deprecated.ExportGATTranscriptDialog;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.fsm.*;
import org.xml.sax.*;
import java.util.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 *
 * @author  thomas
 */
public class ExportCHATTranscriptAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportCHATTranscriptAction */
    public ExportCHATTranscriptAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("CHAT transcript (CHA)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportCHATTranscriptAction!");
        table.commitEdit(true);
        exportCHATTranscript();        
    }
    
    private void exportCHATTranscript(){
         BasicTranscription bt = table.getModel().getTranscription().makeCopy();
         try{
             // check whether events in T-tiers do not span more than one interval
             Vector eventsThatAreNot = new Vector();
             boolean everythingAllRight = bt.getBody().areAllEventsOneIntervalLong(eventsThatAreNot);
             if (!everythingAllRight){
                 String message = "There are events in tiers of type 't' spanning more than one interval.";                  
                 int usersChoice = showOrderingProblemDialog(message);
                 switch (usersChoice){
                     case CANCEL_OPTION : return; // cancel the exportAction
                     case CONTINUE_OPTION : break; // do nothing, i.e. continue
                     case EDIT_OPTION : 
                         EventNavigationDialog dialog =
                            new EventNavigationDialog(table.parent, false, eventsThatAreNot, table);
                         dialog.show();
                         return;
                 }                 
             }             

             // segment the basic transcription and transform it into a list transcription
             CHATSegmentation segmenter = new org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation(table.chatFSM);
             ListTranscription lt = segmenter.BasicToUtteranceList(bt);

             // check whether the list transcription can be fully ordered
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
                            new SpeakerContributionNavigationDialog(table.parent, false, scsThatAreNot, segmenter.beforeAugment, table);
                         dialog.show();
                         return;
                 }
             }             
             String text = segmenter.toText(lt);
             ExportGATTranscriptDialog dialog = new ExportGATTranscriptDialog(text);
             dialog.setFileFilter(new org.exmaralda.partitureditor.deprecated.CHATFileFilter());
             dialog.setDialogTitle("Set the *.cha file for export");
             dialog.setCurrentDirectory(new java.io.File(table.chatDirectory));
             boolean success = dialog.saveTranscript(table.parent);
             table.chatDirectory = dialog.getSelectedFile().getAbsolutePath();
         } catch (SAXException se){
            processSAXException(se);
         } catch (FSMException fsme){
            processFSMException(fsme);
         } catch (JexmaraldaException je){
            javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
            errorDialog.showMessageDialog(table,
                je.getMessage(),
                "Jexmaralda Error",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);         
         }
    }
    
    
}
