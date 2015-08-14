/*
 * ExportTEITranscriptionAction.java
 *
 * Created on 12. August 2004, 18:02
 */

package org.exmaralda.partitureditor.deprecated.segmentationActions;

import org.exmaralda.partitureditor.deprecated.ExportTEITranscriptionDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;


/**
 *
 * @author  thomas
 */
public class ExportTEITranscriptionAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExportTEITranscriptionAction */
    public ExportTEITranscriptionAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("TEI transcription (XML)", icon, t);  
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        System.out.println("exportTEITranscriptionAction!");
        table.commitEdit(true);
        exportTEITranscription();        
    }
    
    private void exportTEITranscription(){
        ExportTEITranscriptionDialog dialog = new ExportTEITranscriptionDialog(table.homeDirectory, table.getModel().getTranscription());
        boolean success = dialog.exportTEI(table.parent);
    }
    
    
}
