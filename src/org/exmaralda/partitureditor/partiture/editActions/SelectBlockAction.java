/*
 * ChopAudioAction.java
 *
 * Created on 22. April 2005, 14:38
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.partiture.*;
/**
 *
 * @author  thomas
 */
public class SelectBlockAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    

    /** Creates a new instance of ChopAudioAction */
    public SelectBlockAction(PartitureTableWithActions t) {
        super("Select block", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("SelectBlock");
        table.commitEdit(true);
        table.selectBlock();
    }
    
    
}
