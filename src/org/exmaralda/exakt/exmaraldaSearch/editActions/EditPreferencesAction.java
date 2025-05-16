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
    
    /** Creates a new instance of EditPreferencesAction
     * @param ef
     * @param title
     * @param icon */
    public EditPreferencesAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EditPreferencesPanel editPreferencesPanel = new EditPreferencesPanel();
        AbstractOKCancelDialog dialog = new AbstractOKCancelDialog(exaktFrame,true,editPreferencesPanel);
        dialog.setTitle("Edit preferences");
        dialog.setVisible(true);
        if (dialog.isApproved()){
            Preferences prefs = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT");
            prefs.put("xsl-partitur-tool",editPreferencesPanel.getPartiturInToolStylesheet());
            prefs.put("xsl-partitur-output",editPreferencesPanel.getPartiturOutputStylesheet());
            prefs.put("xsl-concordance-output",editPreferencesPanel.getConcordanceOutputStylesheet());            
            prefs.put("xsl-segmented-tool",editPreferencesPanel.getSegmentedOutputStylesheet());        
            prefs.put("kwic-table-font-name",editPreferencesPanel.getKwicTableFont());
            prefs.putInt("kwic-table-font-size",editPreferencesPanel.getKwicTableFontSize());
            prefs.putInt("max-search-results",editPreferencesPanel.getMaxSearchResults());
            prefs.putInt("kwic-context-limit",editPreferencesPanel.getKWICContextLimit());
            prefs.putInt("full-display-limit",editPreferencesPanel.getFullDisplayContextLimit()); 
            prefs.putBoolean("delete-unfiltered", editPreferencesPanel.getDeleteUnfiltered());
        }
    }
    
    
}
