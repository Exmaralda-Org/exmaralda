/*
 * ReplaceInEventsAction.java
 *
 * Created on 24. Juni 2003, 09:06
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class ReplaceInEventsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    


    /** Creates a new instance of ReplaceInEventsAction
     * @param t
     * @param icon */
    public ReplaceInEventsAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Replace in events...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("replaceInEventsAction!");
        table.commitEdit(true);
        replaceInEvents();        
    }
    
    private void replaceInEvents(){        
        // this dialog is modal, and it is created anew for each invocation of the action
        org.exmaralda.partitureditor.search.ReplaceDialog dialog = new org.exmaralda.partitureditor.search.ReplaceDialog(table.parent, true, table.externalKeyboardPaths, table.generalPurposeFontName);
        dialog.setTranscription(table.getModel().getTranscription());
        dialog.addSearchResultListener(table);
        dialog.show();     
        dialog.removeAllListeners();
    }
    
}
