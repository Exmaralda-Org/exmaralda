/*
 * ExportSegmentedTranscriptionAction.java
 *
 * Created on 9. Juli 2003, 12:07
 */

package org.exmaralda.partitureditor.deprecated.segmentationActions;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.SaveSegmentedTranscriptionAsDialog;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
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

public class ExportSegmentedTranscriptionAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExportSegmentedTranscriptionAction */
    public ExportSegmentedTranscriptionAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Export segmented transcription (XML)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportSegmentedTranscriptionAction!");
        table.commitEdit(true);
        exportSegmentedTranscription();        
    }
    
    private void exportSegmentedTranscription(){
         BasicTranscription bt = table.getModel().getTranscription().makeCopy();
         SegmentedTranscription st = bt.toSegmentedTranscription();
         org.exmaralda.partitureditor.jexmaralda.segment.SegmentCountForMetaInformation.count(st);
         SaveSegmentedTranscriptionAsDialog dialog = new SaveSegmentedTranscriptionAsDialog(table.homeDirectory, st);
         boolean success = dialog.saveTranscriptionAs(table);             
    }
    
}
