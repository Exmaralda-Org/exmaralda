/*
 * ImportTASXAction.java
 *
 * Created on 17. Juni 2003, 09:54
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ImportTASXDialog;
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
public class ImportTASXAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ImportTASXAction */
    public ImportTASXAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("TASX...", icon, t);                                
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("importTASXAction!");
        table.commitEdit(true);
        importTASX();
        table.setFrameEndPosition(-2);        
    }
    
    private void importTASX(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        ImportTASXDialog dialog = new ImportTASXDialog(table.homeDirectory);
        if (dialog.importTASX(table.parent)){
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
