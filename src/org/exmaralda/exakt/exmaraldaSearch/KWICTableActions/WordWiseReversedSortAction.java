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

/**
 *
 * @author thomas
 */
public class WordWiseReversedSortAction extends AbstractKWICTableAction {
    
    /** Creates a new instance of WordWiseReversedSortAction
     * @param t
     * @param title */
    public WordWiseReversedSortAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("*** WORD WISE REVERSED SORT ACTION");
        
    }
    
}
