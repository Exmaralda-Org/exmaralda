/*
 * EditPreferencesAction.java
 *
 * Created on 16. Februar 2007, 15:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.editActions;

import java.awt.event.ActionEvent;
import org.exmaralda.exakt.exmaraldaSearch.swing.EditPreferencesPanel;
import org.exmaralda.exakt.search.swing.AbstractOKCancelDialog;
import java.util.prefs.Preferences;

/**
 *
 * @author thomas
 */
public class EditPreferencesAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    /** Creates a new instance of EditPreferencesAction */
    public EditPreferencesAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        EditPreferencesPanel p = new EditPreferencesPanel();
        AbstractOKCancelDialog dialog = new AbstractOKCancelDialog(exaktFrame,true,p);
        dialog.setTitle("Edit preferences");
        dialog.setVisible(true);
        if (dialog.isApproved()){
            Preferences prefs = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT");
            prefs.put("xsl-partitur-tool",p.getPartiturInToolStylesheet());
            prefs.put("xsl-partitur-output",p.getPartiturOutputStylesheet());
            prefs.put("xsl-concordance-output",p.getConcordanceOutputStylesheet());            
            prefs.put("xsl-segmented-tool",p.getSegmentedOutputStylesheet());        
            prefs.put("kwic-table-font-name",p.getKwicTableFont());
            prefs.putInt("kwic-table-font-size",p.getKwicTableFontSize());
            prefs.putInt("max-search-results",p.getMaxSearchResults());
            prefs.putInt("kwic-context-limit",p.getKWICContextLimit());
            prefs.putInt("full-display-limit",p.getFullDisplayContextLimit());            
        }
    }
    
    
}
