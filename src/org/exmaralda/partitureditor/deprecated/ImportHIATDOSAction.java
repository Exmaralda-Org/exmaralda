/*
 * ImportHIATDOS.java
 *
 * Created on 17. Juni 2003, 10:20
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;
import org.exmaralda.partitureditor.exHIATDOS.*;
import org.exmaralda.partitureditor.exHIATDOS.swing.*;

/**
 * Imports a HIAT-DOS file into the editor
 * Menu: File --> Import --> Import HIAT-DOS
 * @author  thomas
 */
public class ImportHIATDOSAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ImportHIATDOS */
    public ImportHIATDOSAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("\"Import\" HIAT-DOS...", icon, t);                                                
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("importHIATDOSAction!");
        table.commitEdit(true);
        importHIATDOS();
        table.setFrameEndPosition(-2);        
    }
    
    private void importHIATDOS(){
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;
        ImportHIATDOSDialog dialog = new ImportHIATDOSDialog(table.parent, true);
        if (dialog.importHIATDOS()){
            TierFormatTable tft = new TierFormatTable(dialog.getTranscription());
            String[] ids = tft.getAllTierIDs();
            for (int pos=0; pos<ids.length; pos++){
                try{
                    tft.getTierFormatForTier(ids[pos]).setProperty("font:size", "10"); 
                    tft.getTierFormatForTier(ids[pos]).setProperty("font:name","Courier New");                   
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
