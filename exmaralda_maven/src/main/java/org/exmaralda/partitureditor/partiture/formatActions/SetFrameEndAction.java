/*
 * SetFrameEndAction.java
 *
 * Created on 19. Juni 2003, 10:58
 */

package org.exmaralda.partitureditor.partiture.formatActions;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 *
 * @author  thomas
 */

public class SetFrameEndAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SetFrameEndAction */
    public SetFrameEndAction(PartitureTableWithActions t) {
        super("Set frame end", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("setFramEndAction!");
        setFrameEnd();        
    }
    
    private void setFrameEnd(){
        table.setFrameEndPosition(table.selectionStartRow);
    }
    
    
    
}
