/*
 * ExportAGAction.java
 *
 * Created on 17. Juni 2003, 11:00
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ExportAGDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;

/**
 *
 * exports the current transcription to an Atlas Interchange Format (AIF) File
 * Menu: File --> Export --> Export AIF
 * @author  thomas
 */
public class ExportAGAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExportAGAction */
    public ExportAGAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("AG...", icon, t);  
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportAGAction!");
        table.commitEdit(true);
        exportAG();        
    }
    
    private void exportAG(){
        ExportAGDialog dialog = new ExportAGDialog(table.homeDirectory, table.getModel().getTranscription().makeCopy());
        boolean success = dialog.exportAG(table.parent);
    }
    
    
}
