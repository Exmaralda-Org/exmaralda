/*
 * ExportGATTranscriptAction.java
 *
 * Created on 22. Maerz 2004, 15:45
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import java.io.File;
import org.exmaralda.partitureditor.jexmaraldaswing.SpeakerContributionNavigationDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.EventNavigationDialog;
import org.exmaralda.partitureditor.deprecated.ExportGATTranscriptDialog;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.segment.GATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
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
public class ExportGATTranscriptAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportGATTranscriptAction */
    public ExportGATTranscriptAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("GAT transcript (TXT)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportGATTranscriptAction!");
        table.commitEdit(true);
        exportGATTranscript(null);
    }
    
    public void exportGATTranscript(File file){
         BasicTranscription bt = table.getModel().getTranscription().makeCopy();
         try{
             // check whether events in T-tiers do not span more than one interval
             Vector eventsThatAreNot = new Vector();
             boolean everythingAllRight = bt.getBody().areAllEventsOneIntervalLong(eventsThatAreNot);
             if (!everythingAllRight){
                 String message = "There are events in tiers of type 't' spanning more than one interval.";                  
                 int usersChoice = showOrderingProblemDialog(message);
                 switch (usersChoice){
                     //case CANCEL_OPTION : return; // cancel the exportAction
                     //case CONTINUE_OPTION : break; // do nothing, i.e. continue
                     /*case EDIT_OPTION :
                         EventNavigationDialog dialog =
                            new EventNavigationDialog(table.parent, false, eventsThatAreNot, table);
                         dialog.show();
                         return;*/
                 }                 
             }             
             
             // segment the basic transcription and transform it into a list transcription
             GATSegmentation segmenter = new org.exmaralda.partitureditor.jexmaralda.segment.GATSegmentation(table.gatFSM);
             ListTranscription lt = segmenter.BasicToIntonationUnitList(bt);
             
             // check whether the list transcription can be fully ordered
             Vector scsThatAreNot = new Vector();
             boolean everythingOK = lt.getBody().areAllSpeakerContributionsInTheCommonTimeline(scsThatAreNot);
             if (!everythingOK){
                 String message = "The list cannot be completely ordered.";                                  
                 int usersChoice = showOrderingProblemDialog(message);
                 switch (usersChoice){
                     //case CANCEL_OPTION : return; // cancel the exportAction
                     //case CONTINUE_OPTION : break; // do nothing, i.e. continue
                     /*case EDIT_OPTION :
                         SpeakerContributionNavigationDialog dialog =
                            new SpeakerContributionNavigationDialog(table.parent, false, scsThatAreNot, segmenter.beforeAugment, table);
                         dialog.show();
                         return;*/
                 }
             }
             
             // convert the list transcription into a GAT-like text file and prompt the user to save it
             String text = segmenter.toText(lt);
             ExportGATTranscriptDialog dialog = new ExportGATTranscriptDialog(text);
             if (file==null){
                 boolean success = dialog.saveTranscript(table.parent);
             }
         } catch (SAXException se){
            //processSAXException(se);
         } catch (FSMException fsme){
            //processFSMException(fsme);
         } catch (JexmaraldaException je){
            javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
            errorDialog.showMessageDialog(table,
                je.getMessage(),
                "Jexmaralda Error",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);         
         }
    }

    public int showOrderingProblemDialog(String message) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
