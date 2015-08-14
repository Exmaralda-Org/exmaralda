/*
 * ImportELANAction.java
 *
 * Created on 12. November 2003, 17:42
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ImportEAFFileDialog;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;


/**
 *
 * @author  thomas
 */
public class ImportELANAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ImportELANAction */
    public ImportELANAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("ELAN...", icon, t);                                
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("importELANAction!");
        table.commitEdit(true);
        importELAN();
        table.setFrameEndPosition(-2);        
    }
    
    private void importELAN(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        ImportEAFFileDialog dialog = new ImportEAFFileDialog(table.homeDirectory);
        dialog.setProgressBar(table.progressBar);
        if (dialog.importELAN(table.parent)){
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
