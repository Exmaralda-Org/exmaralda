/*
 * WordWiseReversedSortAction.java
 *
 * Created on 19. Februar 2007, 13:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import org.exmaralda.exakt.exmaraldaSearch.swing.AdvancedSortDialog;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICTable;

/**
 *
 * @author thomas
 */
public class AdvancedSortAction extends AbstractKWICTableAction {
    
    /** Creates a new instance of WordWiseReversedSortAction
     * @param t
     * @param title */
    public AdvancedSortAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AdvancedSortDialog advancedSortDialog = new AdvancedSortDialog((Frame) table.getTopLevelAncestor(), true, table);
        advancedSortDialog.setLocationRelativeTo(table);
        advancedSortDialog.setVisible(true);
        if (!advancedSortDialog.approved){
            return;
        }
        
    }
    
}
