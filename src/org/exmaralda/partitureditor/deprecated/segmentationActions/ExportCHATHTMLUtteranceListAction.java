/*
 * ExportCHATHTMLUtteranceListAction.java
 *
 * Created on 23. Juli 2003, 12:57
 */

package org.exmaralda.partitureditor.deprecated.segmentationActions;

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
import org.exmaralda.partitureditor.deprecated.ExportHTMLTurnListFileDialog;
import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class ExportCHATHTMLUtteranceListAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportCHATHTMLUtteranceListAction */
    public ExportCHATHTMLUtteranceListAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Utterance list (HTML)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportHIATHTMLUtteranceListAction!");
        table.commitEdit(true);
        exportCHATHTMLUtteranceList();     
    }
    
    private void exportCHATHTMLUtteranceList() {
        try {
            BasicTranscription bt = table.getModel().getTranscription().makeCopy();
            org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation cs = new org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation();
            ListTranscription lt = cs.BasicToUtteranceList(bt);
            if (!cs.lastSortWasComplete){
               showListCouldNotBeCompletelyOrderedMessage();
            }
            ExportHTMLTurnListFileDialog dialog = new ExportHTMLTurnListFileDialog(lt, table.getModel().getTierFormatTable(), table.htmlDirectory);
            boolean success = dialog.saveHTML(table);
            table.htmlDirectory = dialog.getFilename();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (FSMException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
    }    
    
}
