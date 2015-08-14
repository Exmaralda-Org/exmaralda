/*
 * reformatAction.java
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
public class ReformatAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of reformatAction */
    public ReformatAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Reformat", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("reformatAction!");
        table.commitEdit(true);
        reformat();        
    }
    
    private void reformat(){
        table.resetFormat(true);
    }
       
    
    
}
