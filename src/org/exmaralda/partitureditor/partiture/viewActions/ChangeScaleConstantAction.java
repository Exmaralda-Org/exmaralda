/*
 * ChangeScaleConstantAction.java
 *
 * Created on 17. Juni 2003, 15:23
 */

package org.exmaralda.partitureditor.partiture.viewActions;

import org.exmaralda.partitureditor.jexmaraldaswing.EditScaleConstantDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 *
 * @author  thomas
 */

public class ChangeScaleConstantAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ChangeScaleConstantAction */
    public ChangeScaleConstantAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Change scale constant...", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("changeScaleConstantAction!");
        table.commitEdit(true);
        changeScaleConstant();        
    }
    
    private void changeScaleConstant(){
        EditScaleConstantDialog dialog = new EditScaleConstantDialog(table.parent,true,table.scaleConstant);
        if (dialog.editScaleConstant()){
            table.scaleConstant = dialog.getScaleConstant();
            table.getModel().fireFormatReset();
        }
    }
    
    
}
