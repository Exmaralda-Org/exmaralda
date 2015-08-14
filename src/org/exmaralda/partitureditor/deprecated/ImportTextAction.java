/*
 * ImportSimpleExmaraldaAction.java
 *
 * Created on 17. Juni 2003, 11:08
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ImportTextDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;

/**
 * imports a text file 
 * Menu: File --> Import --> Import Text
 * @author  thomas
 */
public class ImportTextAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ImportSimpleExmaraldaAction */
    public ImportTextAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Text...", icon, t);       
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("importTextAction!");
        table.commitEdit(true);
        importText();
        table.setFrameEndPosition(-2);        
    }
    
    private void importText(){
        ImportTextDialog dialog = new ImportTextDialog(table.txtDirectory);
        if (dialog.importTranscription(table.parent)){
            table.setRowHidden(JCTableEnum.ALLCELLS, false);
            table.getModel().setTranscription(dialog.getTranscription());
            table.setFilename("untitled.xml");
            table.transcriptionChanged = true;
            table.txtDirectory = dialog.getFilename();            
        }
        table.reconfigureAutoSaveThread();
        table.setupMedia();
    }
    
    
}
