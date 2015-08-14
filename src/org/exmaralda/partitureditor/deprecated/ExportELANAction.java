/*
 * ExportELANAction.java
 *
 * Created on 13. November 2003, 12:33
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.InputELANMediaDialog;
import org.exmaralda.partitureditor.deprecated.ExportEAFFileDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;


/**
 *
 * @author  thomas
 */
public class ExportELANAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExportELANAction */
    public ExportELANAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("ELAN...", icon, t);   
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportELANAction!");
        table.commitEdit(true);
        exportELAN();        
    }
    
    private void exportELAN(){
        InputELANMediaDialog mediaDialog = new InputELANMediaDialog(table.parent, true, table.getModel().getTranscription());
        boolean goon = mediaDialog.getMediaInfo();
        if (!goon) return;
        ExportEAFFileDialog dialog = new ExportEAFFileDialog(table.homeDirectory, table.getModel().getTranscription());
        boolean success = dialog.exportELAN(table.parent);
    }
}
    