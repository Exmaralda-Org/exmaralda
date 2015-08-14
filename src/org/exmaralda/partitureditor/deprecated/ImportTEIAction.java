/*
 * ImportTEIAction.java
 *
 * Created on 12. August 2004, 17:42
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ImportTEIFileDialog;
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
public class ImportTEIAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ImportTEIAction */
    public ImportTEIAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("TEI...", icon, t);                                
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("importTEIAction!");
        table.commitEdit(true);
        importTEI();
        table.setFrameEndPosition(-2);        
    }
    
    private void importTEI(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        ImportTEIFileDialog dialog = new ImportTEIFileDialog(table.homeDirectory);
        if (dialog.importTEI(table.parent)){
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
