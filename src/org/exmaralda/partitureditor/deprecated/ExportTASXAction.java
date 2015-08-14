/*
 * ExportTASXAction.java
 *
 * Created on 17. Juni 2003, 10:52
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ExportTASXDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;

/**
 * Exports the current transcription as a Time Aligned Signal Exchange (TASX) file
 * Menu: File --> Export --> Export TASX
 * @author  thomas
 */
public class ExportTASXAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExportTASXAction */
    public ExportTASXAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("TASX...", icon, t);   
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportTASXAction!");
        table.commitEdit(true);
        exportTASX();        
    }
    
    private void exportTASX(){
        ExportTASXDialog dialog = new ExportTASXDialog(table.homeDirectory, table.getModel().getTranscription());
        boolean success = dialog.exportTASX(table.parent);
    }
    
    
}
