/*
 * EditRowLabelFormatAction.java
 *
 * Created on 19. Juni 2003, 10:52
 */

package org.exmaralda.partitureditor.partiture.formatActions;

import org.exmaralda.partitureditor.jexmaraldaswing.EditTierFormatDialog;
import org.exmaralda.partitureditor.jexmaralda.TierFormat;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 *
 * @author  thomas
 */
public class EditRowLabelFormatAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditRowLabelFormatAction */
    public EditRowLabelFormatAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Format tier labels...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editRowLabelFormatAction!");
        table.commitEdit(true);
        formatRowLabel();        
    }
    
    private void formatRowLabel(){
        TierFormat format = table.getModel().getRowLabelFormat();
        EditTierFormatDialog dialog = new EditTierFormatDialog(table.parent, true, format);
        if (dialog.editTierFormat()){
            table.getModel().setRowLabelFormat(dialog.getTierFormat());
            table.formatChanged = true;            
        }
    }
    
        
    
}
