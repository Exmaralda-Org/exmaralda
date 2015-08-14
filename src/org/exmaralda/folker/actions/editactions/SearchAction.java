/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.editactions;

import java.awt.Toolkit;
import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.gui.SearchPanel;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;


/**
 *
 * @author thomas
 */
public class SearchAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public SearchAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** SearchAction ***]");
        ((ApplicationControl)(applicationControl)).searchDialog.setTitle(FOLKERInternationalizer.getString("dialog.search"));
        ((ApplicationControl)(applicationControl)).searchPanel.setMode(SearchPanel.SEARCH_MODE);
        ((ApplicationControl)(applicationControl)).searchDialog.setVisible(true);
    }
    
}
