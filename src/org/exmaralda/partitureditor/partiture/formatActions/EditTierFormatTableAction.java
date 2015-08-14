/*
 * EditTierFormatTableAction.java
 *
 * Created on 19. Juni 2003, 10:56
 */

package org.exmaralda.partitureditor.partiture.formatActions;

import org.exmaralda.partitureditor.jexmaraldaswing.EditTierFormatTableDialog;
import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.TierDescriptions;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 *
 * @author  thomas
 */
public class EditTierFormatTableAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditTierFormatTableAction */
    public EditTierFormatTableAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Edit format table...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editTierFormatTableAction!");
        table.commitEdit(true);
        editTierFormatTable();        
    }
    
    private void editTierFormatTable(){
        TierFormatTable tft = table.getModel().getTierFormatTable();
        EditTierFormatTableDialog dialog = new EditTierFormatTableDialog(table.parent, true, tft, new TierDescriptions(table.getModel().getTranscription()));
        if (dialog.editTierFormatTable(false)){
            table.getModel().setTierFormatTable(dialog.getTierFormatTable());
            table.formatChanged = true;            
        }
    }
    
    
    
}
