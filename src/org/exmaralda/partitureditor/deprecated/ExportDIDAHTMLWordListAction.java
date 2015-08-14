/*
 * ExportDIDAHTMLWordListAction.java
 *
 * Created on 12. Maerz 2004, 10:23
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import org.exmaralda.partitureditor.deprecated.ExportHTMLWordListFileDialog;
import org.exmaralda.partitureditor.jexmaralda.SegmentList;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
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
public class ExportDIDAHTMLWordListAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportDIDAHTMLWordListAction */
    public ExportDIDAHTMLWordListAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Word list (HTML)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportDIDAHTMLWordListAction!");
        table.commitEdit(true);
        exportDIDAHTMLWordList();        
    }
    
    private void exportDIDAHTMLWordList(){
        
        // convert Basic Transcription to Interlinear Text
        org.exmaralda.partitureditor.interlinearText.InterlinearText it =
            ItConverter.BasicTranscriptionToInterlinearText(table.getModel().getTranscription(), table.getModel().getTierFormatTable());
        System.out.println("Transcript converted to interlinear text.");
        if (table.getFrameEndPosition()>=0){((org.exmaralda.partitureditor.interlinearText.ItBundle)it.getItElementAt(0)).frameEndPosition=table.getFrameEndPosition();}
        if (table.htmlParameters.prependAdditionalInformation){
            table.htmlParameters.additionalStuff = table.getModel().getTranscription().getHead().toHTML();
        }

         //FSMSaxReader sr = new FSMSaxReader();
         try{
             BasicTranscription bt = table.getModel().getTranscription().makeCopy();
             // convert Basic Transcription to Segmented Transcription
             SegmentedTranscription st = new org.exmaralda.partitureditor.jexmaralda.segment.DIDASegmentation(table.didaFSM).BasicToSegmented(bt);             
             // put all the words into a list
             SegmentList words = st.makeSegmentList("DIDA:w");
             org.exmaralda.partitureditor.jexmaralda.segment.DIDASegmentation.cleanWordList(words);

             // give the word list, the interlinear text and the break parameters to the dialog
             // which will take care of the rest
             ExportHTMLWordListFileDialog dialog = new ExportHTMLWordListFileDialog(words, it, table.htmlParameters, table.htmlDirectory);
             boolean success = dialog.saveHTML(table);
             table.htmlDirectory = dialog.getFilename();
             
         } catch (SAXException se){
            //processSAXException(se);
         } catch (FSMException fsme){
            //processFSMException(fsme);
         }         
    }
    
    
}
