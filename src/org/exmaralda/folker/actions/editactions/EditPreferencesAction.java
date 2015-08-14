/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.editactions;

import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.gui.EditPreferencesPanel;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.PreferencesUtilities;


/**
 *
 * @author thomas
 */
public class EditPreferencesAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public EditPreferencesAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** EditPreferencesAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        ac.commitEdit();
        JDialog dialog = new JDialog(ac.getFrame(), true);
        EditPreferencesPanel editPreferencesPanel = new EditPreferencesPanel((org.exmaralda.common.ExmaraldaApplication)(ac.getFrame()));
        dialog.setTitle(FOLKERInternationalizer.getString("dialog.preferences"));
        dialog.getContentPane().add(editPreferencesPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(ac.getFrame());
        dialog.setVisible(true);        
        
        editPreferencesPanel.commitValues();

        int parseLevel = Integer.parseInt(PreferencesUtilities.getProperty("parse-level", "2"));
        ac.setParseLevel(parseLevel);
        ac.contributionTextPane.MAKE_LINE_BREAK_AFTER_BOUNDARY_SYMBOLS=(parseLevel==3);        
        ac.TIME_BETWEEN_LOOPS = Integer.parseInt(PreferencesUtilities.getProperty("loop-time", "500"));
        ac.updateInfoPanel();

        ac.setFont();
        
    }
    
}
