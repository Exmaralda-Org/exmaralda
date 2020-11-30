/*
 * ShowAllTiersAction.java
 *
 * Created on 17. Juni 2003, 15:03
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class ShowAllTiersAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ShowAllTiersAction */
    public ShowAllTiersAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Show all tiers", icon, t); 
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("showAllTiersAction!");
        table.commitEdit(true);
        table.showAllTiers(); 
        for (int i=0; i<table.getModel().getTranscription().getBody().getNumberOfTiers(); i++){
            table.getModel().getTranscription().getBody().getTierAt(i).getUDTierInformation().removeAttribute("exmaralda:hidden");
        }
    }
    
    
    
}
