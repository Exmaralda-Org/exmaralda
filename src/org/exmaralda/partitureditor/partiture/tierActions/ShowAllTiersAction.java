/*
 * ShowAllTiersAction.java
 *
 * Created on 17. Juni 2003, 15:03
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

/**
 *
 * @author  thomas
 */
public class ShowAllTiersAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ShowAllTiersAction */
    public ShowAllTiersAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Show all tiers", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("showAllTiersAction!");
        table.commitEdit(true);
        table.showAllTiers();        
    }
    
    
    
}
