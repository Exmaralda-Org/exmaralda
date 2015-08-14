/*
 * FileMenu.java
 *
 * Created on 9. Februar 2007, 11:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.editActions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;


/**
 *
 * @author thomas
 */
public class EditMenu extends javax.swing.JMenu {
    
    EXAKT exaktFrame;
    
    private JMenuItem copyMenuItem;
    
    /** Creates a new instance of FileMenu */
    public EditMenu(EXAKT ef) {
        setText("Edit");
        exaktFrame = ef;

        copyMenuItem = new JMenuItem("Copy selection");
        copyMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                copy(e);
            }
        });
        add(copyMenuItem);
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        this.add(new javax.swing.JSeparator());
        add(exaktFrame.editPreferencesAction);
        add(exaktFrame.getPartitur().editPartiturParametersAction);
    }

    void copy(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().getKWICTable().copyAction.actionPerformed(e);
    }

    
}
