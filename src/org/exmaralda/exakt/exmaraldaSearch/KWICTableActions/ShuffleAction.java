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
import javax.swing.JOptionPane;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICTable;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMASearchResultListTableModel;

/**
 *
 * @author thomas
 */
public class ShuffleAction extends AbstractKWICTableAction {
    
    /** Creates a new instance of WordWiseReversedSortAction
     * @param t
     * @param title */
    public ShuffleAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        COMASearchResultListTableModel model = table.getWrappedModel();
        table.getWrappedModel().shuffle();
        table.setCellEditors();
        table.adjustColumns();
        ((ExmaraldaApplication)(table.getTopLevelAncestor())).status("Search result shuffled. ");
        
        
    }
    
}
