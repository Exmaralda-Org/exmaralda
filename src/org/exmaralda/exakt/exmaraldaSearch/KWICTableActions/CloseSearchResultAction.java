/*
 * CloseSearchResultAction.java
 *
 * Created on 12. Juni 2007, 17:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;

/**
 *
 * @author thomas
 */
public class CloseSearchResultAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    /** Creates a new instance of CloseSearchResultAction */
    public CloseSearchResultAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        exaktFrame.closeActiveSearchPanel();
    }
    
}
