/*
 * editTierFormatAction.java
 *
 * Created on 19. Juni 2003, 10:44
 */

package org.exmaralda.partitureditor.partiture.formatActions;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
/**
 *
 * @author  thomas
 */
public class EditTierFormatAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of editTierFormatAction */
    public EditTierFormatAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Format tier...", icon, t);
        //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F"));        
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editTierFormatAction!");
        table.commitEdit(true);
        formatTier();        
    }
    
    private void formatTier(){
        TierFormat format = table.getModel().getFormat(table.selectionStartRow);
        EditTierFormatDialog dialog = new EditTierFormatDialog(table.parent, true, format);
        if (dialog.editTierFormat()){
            boolean aSeriesOfRowsIsSelected = ((table.selectionStartRow != table.selectionEndRow) && (table.selectionStartRow != -1) && (table.selectionEndRow != -1));
            if (!aSeriesOfRowsIsSelected){
                table.getModel().setFormat(table.selectionStartRow, dialog.getTierFormat());
            } else {
                table.getModel().setFormats(table.selectionStartRow, table.selectionEndRow, dialog.getTierFormat());
            }
            table.formatChanged = true;            
        }
    }
        
    
}
