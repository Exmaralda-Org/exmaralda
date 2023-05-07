/*
 * SearchInEventsAction.java
 *
 * Created on 17. Juni 2003, 12:55
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class SearchInEventsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    public org.exmaralda.partitureditor.search.FindDialog dialog;
    
    /** Creates a new instance of SearchInEventsAction
     * @param t
     * @param icon */
    public SearchInEventsAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Search in events...", icon, t);
        // this dialog is non-modal, and it is kept over different actions
        dialog = new org.exmaralda.partitureditor.search.FindDialog(table.parent, false, table.externalKeyboardPaths, table.generalPurposeFontName);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("searchInEventsAction!");
        table.commitEdit(true);
        searchInEvents();        
    }
    
    private void searchInEvents(){
        //org.exmaralda.partitureditor.search.FindDialog dialog = new org.exmaralda.partitureditor.search.FindDialog(table.parent, false, table.externalKeyboardPaths, table.generalPurposeFontName);
        dialog.setTranscription(table.getModel().getTranscription());
        dialog.addSearchResultListener(table);
        dialog.show();             
        //dialog.removeAllListeners();
    }
    
    
}
