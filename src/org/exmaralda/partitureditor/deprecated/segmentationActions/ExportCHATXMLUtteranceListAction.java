/*
 * ExportCHATHTMLUtteranceListAction.java
 *
 * Created on 23. Juli 2003, 12:57
 */

package org.exmaralda.partitureditor.deprecated.segmentationActions;

import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.exmaralda.partitureditor.jexmaraldaswing.SpeakerContributionNavigationDialog;
import org.exmaralda.partitureditor.deprecated.SaveListTranscriptionAsDialog;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.fsm.*;
import java.util.*;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class ExportCHATXMLUtteranceListAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportCHATHTMLUtteranceListAction */
    public ExportCHATXMLUtteranceListAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Utterance list (XML)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportCHATXMLUtteranceListAction!");
        table.commitEdit(true);
        exportCHATHTMLUtteranceList();     
    }
    
    private void exportCHATHTMLUtteranceList() {
        try {
            BasicTranscription bt = table.getModel().getTranscription().makeCopy();
            org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation hs = new org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation();
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
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (FSMException ex) {
            ex.printStackTrace();
        }
    }    
    
}
