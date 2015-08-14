/*
 * ImportExSyncAction.java
 *
 * Created on 17. Juni 2003, 10:41
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;
import org.exmaralda.partitureditor.exSync.*;
import org.exmaralda.partitureditor.exSync.swing.*;

/**
 * Imports an exSync file (i.e. a file that has been read out of the 
 * syncWriter using the Apple Script "ExSync" into the partitur editor
 * as a basic transcription
 * Menu: File --> Import --> Import ExSync Data
 * @author  thomas
 */
public class ImportExSyncAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ImportExSyncAction */
    public ImportExSyncAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("\"Import\" ExSync...", icon, t);                                                        
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("importExSyncAction!");
        table.commitEdit(true);
        importExSync();
        table.setFrameEndPosition(-2);        
    }
    
    private void importExSync(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        ImportExSyncDialog dialog = new ImportExSyncDialog(table.homeDirectory);
        if (dialog.importExSync(table.parent)){
            TierFormatTable tft = new TierFormatTable(dialog.getTranscription());
            String[] ids = tft.getAllTierIDs();
            for (int pos=0; pos<ids.length; pos++){
                try{
                    tft.getTierFormatForTier(ids[pos]).setProperty("font:size", "10"); 
                    tft.getTierFormatForTier(ids[pos]).setProperty("font:name","Arial Unicode MS");                   
                } catch (JexmaraldaException je) {}
            }
            table.setRowHidden(JCTableEnum.ALLCELLS, false);
            table.getModel().setTranscriptionAndTierFormatTable(dialog.getTranscription(), tft);
            table.setFilename("untitled.xml");
            table.transcriptionChanged=true;           
        }
        table.reconfigureAutoSaveThread();
        table.setupMedia();
        
    }

    
    
    
    
}
