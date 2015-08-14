/*
 * SearchInEventsAction.java
 *
 * Created on 17. Juni 2003, 12:55
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.jexmaralda.errorChecker.EditErrorsDialog;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class EditErrorsAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    EditErrorsDialog eed;
    
    /** Creates a new instance of SearchInEventsAction */
    public EditErrorsAction(PartitureTableWithActions t) {
        super("Error list...", t);
        eed = new EditErrorsDialog(table.parent, false);   
        eed.addErrorCheckerListener(t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("EditErrorsAction!");
        table.commitEdit(true);
        editErrors();        
    }
    
    private void editErrors(){
        eed.setLocationRelativeTo(table);
        eed.setVisible(true);
    }
    
    
}
