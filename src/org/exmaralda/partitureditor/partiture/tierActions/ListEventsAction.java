/*
 * EditTierAction.java
 *
 * Created on 17. Juni 2003, 14:29
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaraldaswing.ListEventsTableDialog;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;

// changed for #382


/**
 *
 * @author  thomas
 */
public class ListEventsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditTierAction
     * @param t */
    public ListEventsAction(PartitureTableWithActions t) {
        super("List events...", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("listEventsAction!");
        table.commitEdit(true);
        listEvents();
        table.transcriptionChanged = true;        
    }
    
    private void listEvents(){
        Tier tier = table.getModel().getTranscription().getBody().getTierAt(table.selectionStartRow);
        //ListEventsDialog dialog = new ListEventsDialog(table.parent, false, tier);
        ListEventsTableDialog dialog = new ListEventsTableDialog(table.parent, false, table.getModel().getTranscription(), tier);
        dialog.setLocationRelativeTo(table);
        dialog.addSearchResultListener(table);
        dialog.setVisible(true);
        
    }
    
    
}
