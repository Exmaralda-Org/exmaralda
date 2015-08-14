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
public class FindNextAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    

    /** Creates a new instance of ChopAudioAction */
    public FindNextAction(PartitureTableWithActions t) {
        super("Find next", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("FindNextAction");
        table.commitEdit(true);
        table.findNext();
    }
    
    
}
