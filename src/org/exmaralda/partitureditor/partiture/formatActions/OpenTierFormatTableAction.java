/*
 * OpenTierFormatTableAction.java
 *
 * Created on 19. Juni 2003, 10:56
 */

package org.exmaralda.partitureditor.partiture.formatActions;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.OpenTierFormatTableDialog;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */

public class OpenTierFormatTableAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of OpenTierFormatTableAction */
    public OpenTierFormatTableAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Open format table...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("openTierFormatTableAction!");
        table.commitEdit(true);
        openTierFormatTable();        
    }
    
    private void openTierFormatTable(){
        boolean proceed = true;
        if (!proceed) return;
        OpenTierFormatTableDialog dialog = new OpenTierFormatTableDialog(table.filename);
        if (dialog.openTierFormatTable(table.parent)){
            table.getModel().setTierFormatTable(dialog.getTierFormatTable());
            table.formatChanged = false;
        }
    }
    
    
    
}
