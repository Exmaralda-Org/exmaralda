/*
 * this.java
 *
 * Created on 1. Juli 2003, 15:11
 */

package org.exmaralda.partitureditor.partiture.menus;

import javax.swing.*;
import org.exmaralda.partitureditor.partiture.*;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

/**
 *
 * @author  thomas
 */
public class EventMenu extends AbstractTableMenu {
    
    private JMenuItem shiftRightMenuItem;
    private JMenuItem shiftLeftMenuItem;
    private JMenuItem mergeMenuItem;
    private JMenuItem splitMenuItem;
    //private JMenuItem doubleSplitMenuItem;
    private JMenuItem deleteEventMenuItem;
    private JMenuItem extendRightMenuItem;
    private JMenuItem extendLeftMenuItem;
    private JMenuItem shrinkRightMenuItem;
    private JMenuItem shrinkLeftMenuItem;
    private JMenuItem moveRightMenuItem;
    private JMenuItem moveLeftMenuItem;
    private JMenuItem findNextEventMenuItem;

    /** Creates a new instance of EventMenu */
    public EventMenu(PartitureTableWithActions t) {
        super(t);

        this.setText("Event");
        this.setMnemonic(java.awt.event.KeyEvent.VK_N);
        
        add(table.editEventAction);
        deleteEventMenuItem = this.add(table.deleteEventAction);
        deleteEventMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        addSeparator();

        shiftRightMenuItem  = this.add(table.shiftRightAction);
        shiftRightMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        
        shiftLeftMenuItem  = this.add(table.shiftLeftAction);
        shiftLeftMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        addSeparator();

        mergeMenuItem  = this.add(table.mergeAction);
        mergeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        splitMenuItem  = this.add(table.splitAction);
        splitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        splitMenuItem  = this.add(table.doubleSplitAction);
        splitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        addSeparator();


        extendRightMenuItem = this.add(table.extendRightAction);
        extendRightMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, java.awt.event.InputEvent.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        extendLeftMenuItem  = this.add(table.extendLeftAction);
        extendLeftMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, java.awt.event.InputEvent.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        shrinkRightMenuItem  = this.add(table.shrinkRightAction);
        shrinkRightMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, java.awt.event.InputEvent.ALT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      
        
        shrinkLeftMenuItem  = this.add(table.shrinkLeftAction);
        shrinkLeftMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, java.awt.event.InputEvent.ALT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      

        moveRightMenuItem  = this.add(table.moveRightAction);
        moveRightMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      

        moveLeftMenuItem  = this.add(table.moveLeftAction);
        moveLeftMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      

        addSeparator();
                
        findNextEventMenuItem  = this.add(table.findNextEventAction);
        findNextEventMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        addSeparator();

        add(table.insertPauseAction);
        //findNextEventMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, java.awt.event.InputEvent.ALT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
    
}
