/*
 * ImportSimpleExmaraldaAction.java
 *
 * Created on 17. Juni 2003, 11:08
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ImportBasicFromSimpleDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;

/**
 * imports a Simple EXMARaLDA text file 
 * Menu: File --> Import --> Import Simple EXMARaLDA
 * @author  thomas
 */
public class ImportSimpleExmaraldaAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ImportSimpleExmaraldaAction */
    public ImportSimpleExmaraldaAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Simple EXMARaLDA...", icon, t);       
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("importSimpleExmaraldaAction!");
        table.commitEdit(true);
        importSimpleExmaralda();
        table.setFrameEndPosition(-2);        
    }
    
    private void importSimpleExmaralda(){
        ImportBasicFromSimpleDialog dialog = new ImportBasicFromSimpleDialog(table.txtDirectory);
        if (dialog.importTranscription(table.parent, table.progressBar)){
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
