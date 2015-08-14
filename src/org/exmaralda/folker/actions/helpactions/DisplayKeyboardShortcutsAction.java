/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.helpactions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.HTMLDisplayDialog;
import org.exmaralda.folker.utilities.PreferencesUtilities;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class DisplayKeyboardShortcutsAction extends AbstractApplicationAction {
    
    
    HTMLDisplayDialog dialog;
    boolean loaded = false;

    /** Creates a new instance of OpenAction */
    public DisplayKeyboardShortcutsAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
        dialog = new HTMLDisplayDialog(ac.getFrame(), false);
        dialog.setTitle(FOLKERInternationalizer.getString("dialog.keyboard"));
        dialog.setSize(280, 600);
        dialog.setPreferredSize(dialog.getSize());
        java.awt.Dimension dialogSize = dialog.getPreferredSize();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation(screenSize.width - dialogSize.width, 0);
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** DisplayKeyboardShortcutsAction ***]");
        if (!loaded){
            try {
                boolean useControl = PreferencesUtilities.getBooleanProperty("use-control", false);
                String lang = PreferencesUtilities.getProperty("language", "en");
                String theText = "";
                if (lang.equals("de")){
                    if (useControl){
                        theText = "/org/exmaralda/folker/utilities/KeyboardShortcutsWithCtrl.html";
                    } else {
                        theText = "/org/exmaralda/folker/utilities/KeyboardShortcuts.html";                    
                    }
                } else if (lang.equals("fr")){
                    if (useControl){
                        theText = "/org/exmaralda/folker/utilities/KeyboardShortcutsWithCtrl_FR.html";
                    } else {
                        theText = "/org/exmaralda/folker/utilities/KeyboardShortcuts_FR.html";
                    }
                } else {
                    if (useControl){
                        theText = "/org/exmaralda/folker/utilities/KeyboardShortcutsWithCtrl_EN.html";
                    } else {
                        theText = "/org/exmaralda/folker/utilities/KeyboardShortcuts_EN.html";
                    }
                }
                dialog.loadInternalResource(theText);
                loaded = true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getLocalizedMessage());
            }
        }
        dialog.setVisible(true);
        
    }
    
}
