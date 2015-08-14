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


/**
 *
 * @author thomas
 */
public class CopyAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public CopyAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                        
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** CopyAction ***]");
        try {
            ApplicationControl ac = (ApplicationControl) applicationControl;
            ac.copySelectionToClipboard();
        } catch (Exception ex) {
            applicationControl.displayException(ex);
        }
        
    }
    
}
