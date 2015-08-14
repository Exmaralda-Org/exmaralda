/*
 * ImportPraatAction.java
 *
 * Created on 17. Juni 2003, 09:59
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ImportPraatDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;

/**
 * Imports a Praat TextGrid into the editor
 * Menu: File --> Import --> Import Praat TextGrid
 * @author  thomas
 */
public class ImportPraatAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ImportPraatAction */
    public ImportPraatAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Praat TextGrid...", icon, t);                                        
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("importPraatAction!");
        table.commitEdit(true);
        importPraat();
        table.setFrameEndPosition(-2);        
    }
    
    private void importPraat(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        ImportPraatDialog dialog = new ImportPraatDialog(table.homeDirectory);
        if (dialog.importPraat(table.parent)){
            table.setRowHidden(JCTableEnum.ALLCELLS, false);
            table.getModel().setTranscription(dialog.getTranscription());
            table.setFilename("untitled.xml");
            table.transcriptionChanged=true;
        }
        table.linkPanelDialog.getLinkPanel().emptyContents();
        //table.reexportHTMLAction.setEnabled(false);
        table.reconfigureAutoSaveThread();
        table.setupMedia();
       
    }
    
    
}
