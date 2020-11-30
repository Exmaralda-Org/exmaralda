/*
 * HideTierAction.java
 *
 * Created on 17. Juni 2003, 14:58
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;

/**
 *
 * @author  thomas
 */
public class HideTierAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of HideTierAction
     * @param t */
    public HideTierAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Hide tier", icon, t); 
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control alt H"));        
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("hideTierAction!");
        table.commitEdit(true);
        hideTier();        
    }
    
    private void hideTier(){
        for (int row=table.selectionStartRow; row<=table.selectionEndRow; row++){
            table.hideRow(row);            
        }
        table.getModel().fireAreaChanged(0, table.getModel().getNumColumns());
    }
    
    
}
