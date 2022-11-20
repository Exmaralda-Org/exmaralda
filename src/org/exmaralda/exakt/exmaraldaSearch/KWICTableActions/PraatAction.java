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
import org.exmaralda.exakt.exmaraldaSearch.swing.*;

/**
 *
 * @author thomas
 */
public class PraatAction extends AbstractKWICTableAction {
    
    
    /** Creates a new instance of WordWiseReversedSortAction
     * @param t
     * @param title */
    public PraatAction(COMAKWICTable t, String title) {
        super(t,title);
        //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        table.firePraatRequest();        
    }


    
}
