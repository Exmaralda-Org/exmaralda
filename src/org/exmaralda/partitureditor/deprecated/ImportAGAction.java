/*
 * ImportTASXAction.java
 *
 * Created on 17. Juni 2003, 09:54
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ImportAGDialog;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;

/**
 * Imports a TASX file
 * Menu: File --> Import --> Import TASX
 * @author  thomas
 */
public class ImportAGAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ImportTASXAction */
    public ImportAGAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("AG...", icon, t);                                
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("importAGAction!");
        table.commitEdit(true);
        importAG();
        table.setFrameEndPosition(-2);        
    }
    
    private void importAG(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        ImportAGDialog dialog = new ImportAGDialog(table.homeDirectory);
        if (dialog.importAG(table.parent)){
            BasicTranscription newTranscription = dialog.getTranscription();
            table.stratify(newTranscription);
            table.cleanup(newTranscription);
            table.setRowHidden(JCTableEnum.ALLCELLS, false);
            table.getModel().setTranscription(newTranscription);
            table.setFilename("untitled.xml");
            table.transcriptionChanged=true;
        }
        table.linkPanelDialog.getLinkPanel().emptyContents();
        //table.reexportHTMLAction.setEnabled(false);
        table.reconfigureAutoSaveThread();
        table.setupMedia();
       
    }
    
    
}
