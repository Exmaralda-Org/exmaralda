/*
 * EditEventAction.java
 *
 * Created on 18. Juni 2003, 11:38
 */

package org.exmaralda.partitureditor.partiture.eventActions;

import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class FindNextEventAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditEventAction */
    public FindNextEventAction(PartitureTableWithActions t) {
        super("Find next event", t);
    }
    
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("findNextEventAction!");
        findEvent();
    }
    
    private void findEvent(){
        table.commitEdit(true);
        table.findEvent();
    }
    
    
}
