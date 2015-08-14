/*
 * SaveTierFormatTableAsAction.java
 *
 * Created on 19. Juni 2003, 10:57
 */

package org.exmaralda.partitureditor.partiture.formatActions;

import org.exmaralda.partitureditor.deprecated.*;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.SaveTierFormatTableAsDialog;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */

public class SaveTierFormatTableAsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SaveTierFormatTableAsAction */
    public SaveTierFormatTableAsAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Save format table as...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("saveTierFormatTableAsAction!");
        table.commitEdit(true);
        saveTierFormatTableAs();        
    }
    
    private void saveTierFormatTableAs(){
        SaveTierFormatTableAsDialog dialog = new SaveTierFormatTableAsDialog(table.filename, table.getModel().getTierFormatTable());
        boolean success = dialog.saveTierFormatTableAs(table.parent);
        table.formatChanged = !success;
        if (success){
            //table.formatDirectory = dialog.getFilename();
            //table.setFormatFilename(dialog.getFilename());
        }
    }
    
    
    
}
