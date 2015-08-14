/*
 * EditPreferencesAction.java
 *
 * Created on 16. Februar 2007, 15:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.viewActions;

import java.awt.event.ActionEvent;
import org.exmaralda.exakt.exmaraldaSearch.swing.EditPreferencesPanel;
import org.exmaralda.exakt.search.swing.AbstractOKCancelDialog;
import java.util.prefs.Preferences;

/**
 *
 * @author thomas
 */
public class BrowsingModeAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    /** Creates a new instance of EditPreferencesAction */
    public BrowsingModeAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().startBrowser();        
    }
    
    
}
