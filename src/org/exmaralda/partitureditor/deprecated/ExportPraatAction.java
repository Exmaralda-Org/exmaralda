/*
 * ExportPraatAction.java
 *
 * Created on 17. Juni 2003, 11:06
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ExportPraatDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;

/**
 * Exports the current transcription as a Praat Text Grid
 * Menu: File --> Export --> Export Praat TextGrid
 * @author  thomas
 */
public class ExportPraatAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExportPraatAction */
    public ExportPraatAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Praat TextGrid...", icon, t);  
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportPraatAction!");
        table.commitEdit(true);
        exportPraat();        
    }
    
    private void exportPraat(){
        // BUG IN 1.3.!!!! Fixed 25-11-2004. Must give dialog a copy of the transcription,
        // not the transcription itself!!! Bloody hell!
        ExportPraatDialog dialog = new ExportPraatDialog(table.homeDirectory, table.getModel().getTranscription().makeCopy());
        boolean success = dialog.exportPraat(table.parent);
    }
    
    
}
