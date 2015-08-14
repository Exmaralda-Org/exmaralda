/*
 * this.java
 *
 * Created on 1. Juli 2003, 15:03
 */

package org.exmaralda.partitureditor.partiture.menus;

import javax.swing.*;
import org.exmaralda.partitureditor.partiture.*;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import org.exmaralda.common.helpers.Internationalizer;

/**
 *
 * @author  thomas
 */
public class EditMenu extends AbstractTableMenu {
    
    private JMenuItem undoMenuItem;

    private JMenuItem copyTextMenuItem;
    private JMenuItem pasteMenuItem;
    private JMenuItem cutMenuItem;

    private JMenuItem searchInEventsMenuItem;
    private JMenuItem findNextMenuItem;
    private JMenuItem replaceInEventsMenuItem;

    private JMenuItem exaktSearchMenuItem;

    
    private javax.swing.JMenu selectionMenu;


    /** Creates a new instance of EditMenu */
    public EditMenu(PartitureTableWithActions t) {
        super(t);
        
        this.setText("Edit");
        this.setMnemonic(java.awt.event.KeyEvent.VK_E);
        
        selectionMenu = new javax.swing.JMenu();
        selectionMenu.setText(Internationalizer.getString("Selection"));

        
        undoMenuItem = this.add(table.undoAction);
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        addSeparator();

        // edit menu
        copyTextMenuItem = this.add(table.copyTextAction);
        copyTextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        
        pasteMenuItem = this.add(table.pasteAction);
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        
        cutMenuItem = this.add(table.cutAction);
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        
        this.add(new javax.swing.JSeparator());
        //-------------------------------------------------

        searchInEventsMenuItem = this.add(table.searchInEventsAction);
        searchInEventsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        
        findNextMenuItem = this.add(table.findNextAction);
        findNextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        replaceInEventsMenuItem = this.add(table.replaceInEventsAction);
        replaceInEventsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        add(table.gotoAction).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        this.add(new javax.swing.JSeparator());
        //-------------------------------------------------

        exaktSearchMenuItem = this.add(table.exaktSearchAction);
        exaktSearchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        this.add(new javax.swing.JSeparator());
        //-------------------------------------------------
        selectionMenu.add(table.selectionToNewAction);
        selectionMenu.add(table.leftPartToNewAction);
        selectionMenu.add(table.rightPartToNewAction);
        selectionMenu.add(new javax.swing.JSeparator());
        selectionMenu.add(table.selectionToRTFAction);
        selectionMenu.add(table.selectionToHTMLAction);
        selectionMenu.add(table.printSelectionAction);
        
        this.add(selectionMenu);
        
        this.addSeparator();
        //-------------------------------------------------
        

        add(table.editPreferencesAction);
        add(table.editPartiturParametersAction);
    }

    public void setUndoText(String text){
        if (text==null){
            undoMenuItem.setText(Internationalizer.getString("Undo"));
        } else {
            undoMenuItem.setText(Internationalizer.getString("Undo") + ": " + text);
        }
    }
    
}
