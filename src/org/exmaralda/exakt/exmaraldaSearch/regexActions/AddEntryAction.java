/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.regexActions;

import java.awt.event.ActionEvent;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanel;
import org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT;
import org.exmaralda.exakt.regex.AddRegexDialog;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class AddEntryAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {

    
    public AddEntryAction(EXAKT exaktFrame) {
        super(exaktFrame, "Add to library...");
    }

    public void actionPerformed(ActionEvent e) {
        COMAKWICSearchPanel cksp = exaktFrame.getActiveSearchPanel();
        if (cksp==null) return;
        Element entry = cksp.getRegexLibraryEntry();

        AddRegexDialog dialog = new AddRegexDialog(exaktFrame, true);
        dialog.entryPanel.setEntry(entry);
        dialog.setLocationRelativeTo(exaktFrame.regexLibraryDialog);
        dialog.setVisible(true);
        if (!dialog.changed) return;

        Element modifiedEntry = dialog.entryPanel.getEntry();
        exaktFrame.regexLibraryDialog.addEntry(modifiedEntry);
        exaktFrame.regexLibraryDialog.setVisible(true);
    }

}
