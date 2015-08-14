/*
 * ExportIPAWordListAction.java
 *
 * Created on 27. Juni 2005, 09:08
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import org.exmaralda.partitureditor.deprecated.ExportTextWordListFileDialog;
import org.exmaralda.partitureditor.jexmaralda.SegmentList;
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
 * @author thomas
 */
public class ExportIPASyllableListAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportDIDAHTMLWordListAction */
    public ExportIPASyllableListAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Syllable list (HTML)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportIPASyllableListAction!");
        table.commitEdit(true);
        exportIPASyllableList();        
    }
    
    private void exportIPASyllableList(){
        
         try{
             BasicTranscription bt = table.getModel().getTranscription().makeCopy();
             // convert Basic Transcription to Segmented Transcription
             SegmentedTranscription st = new org.exmaralda.partitureditor.jexmaralda.segment.IPASegmentation(table.ipaFSM).BasicToSegmented(bt);             
             // put all the words into a list
             SegmentList words = st.makeSegmentList("IPA:sl");

             // give the word list, the interlinear text and the break parameters to the dialog
             // which will take care of the rest
             ExportTextWordListFileDialog dialog = 
                     new ExportTextWordListFileDialog(words, table.htmlDirectory, bt.getBody().getCommonTimeline());
             boolean success = dialog.saveText(table.parent);
             table.htmlDirectory = dialog.getFilename();
             
         } catch (SAXException se){
            //processSAXException(se);
         } catch (FSMException fsme){
            //processFSMException(fsme);
         }         
    }
    
    
}
