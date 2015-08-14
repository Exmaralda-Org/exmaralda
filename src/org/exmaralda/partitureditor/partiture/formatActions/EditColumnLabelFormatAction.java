/*
 * EditColumnLabelFormatAction.java
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
public class EditColumnLabelFormatAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditColumnLabelFormatAction */
    public EditColumnLabelFormatAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Format timeline...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editColumnLabelFormatAction!");
        table.commitEdit(true);
        formatColumnLabel();        
    }
    
    private void formatColumnLabel(){
        TierFormat format = table.getModel().getColumnLabelFormat();
        EditTierFormatDialog dialog = new EditTierFormatDialog(table.parent, true, format);
        if (dialog.editTierFormat()){
            table.getModel().setColumnLabelFormat(dialog.getTierFormat());
            table.formatChanged = true;
        }
    }
    
    
    
}
