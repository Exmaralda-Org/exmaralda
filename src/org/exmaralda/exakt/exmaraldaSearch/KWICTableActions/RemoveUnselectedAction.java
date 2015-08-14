/*
 * WordWiseReversedSortAction.java
 *
 * Created on 19. Februar 2007, 13:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICTable;
import javax.swing.JOptionPane;

/**
 *
 * @author thomas
 */
public class RemoveUnselectedAction extends AbstractKWICTableAction {
    
    /** Creates a new instance of WordWiseReversedSortAction */
    public RemoveUnselectedAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    public void actionPerformed(ActionEvent e) {
        String message = "Are you sure you want to remove unselected search results?";
        int returnValue =  JOptionPane.showConfirmDialog(table.getTopLevelAncestor(), message, "Remove unselected search results", JOptionPane.YES_NO_OPTION);
        if (returnValue==JOptionPane.OK_OPTION){
            table.getWrappedModel().removeUnselected();
            table.setCellEditors();
        }
    }
    
}
