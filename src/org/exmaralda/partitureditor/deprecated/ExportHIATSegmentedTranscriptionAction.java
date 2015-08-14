/*
 * ExportSegmentedTranscriptionAction.java
 *
 * Created on 17. Juni 2003, 17:00
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.SaveSegmentedTranscriptionAsDialog;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
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
public class ExportHIATSegmentedTranscriptionAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportSegmentedTranscriptionAction */
    public ExportHIATSegmentedTranscriptionAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Segmented transcription (XML)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportHIATSegmentedTranscriptionAction!");
        table.commitEdit(true);
        exportHIATSegmentedTranscription();        
    }
    
    private void exportHIATSegmentedTranscription(){
         BasicTranscription bt = table.getModel().getTranscription().makeCopy();
         try{
             SegmentedTranscription st = new org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation(table.hiatFSM).BasicToSegmented(bt);
             org.exmaralda.partitureditor.jexmaralda.segment.SegmentCountForMetaInformation.count(st);
             SaveSegmentedTranscriptionAsDialog dialog = new SaveSegmentedTranscriptionAsDialog(table.homeDirectory, st);
             boolean success = dialog.saveTranscriptionAs(table);             
         } catch (SAXException se){
            //processSAXException(se);
         } catch (FSMException fsme){
            //processFSMException(fsme);
         }         
    }
    
    
}
