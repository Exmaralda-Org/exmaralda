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
import org.exmaralda.exakt.exmaraldaSearch.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;


/**
 *
 * @author thomas
 */
public class SelectHighlightedAction extends AbstractKWICTableAction {
    
    /** Creates a new instance of WordWiseReversedSortAction */
    public SelectHighlightedAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    public void actionPerformed(ActionEvent e) {
        int[] selectedRows = table.getSelectedRows();
        int[] modelRows = new int[selectedRows.length];
        for (int i=0; i<selectedRows.length; i++){
            modelRows[i] = ((COMAKWICTableSorter)(table.getModel())).modelIndex(selectedRows[i]);
        }
        table.getWrappedModel().selectRange(modelRows);
        table.setCellEditors();
    }
    
}
