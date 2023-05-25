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
    
    private final JMenuItem shiftRightMenuItem;
    private final JMenuItem shiftLeftMenuItem;
    private final JMenuItem mergeMenuItem;
    private final JMenuItem splitMenuItem;
    private final JMenuItem doubleSplitMenuItem;
    private final JMenuItem deleteEventMenuItem;
    private final JMenuItem deleteEventsMenuItem;
    private final JMenuItem extendRightMenuItem;
    private final JMenuItem extendLeftMenuItem;
    private final JMenuItem shrinkRightMenuItem;
    private final JMenuItem shrinkLeftMenuItem;
    private final JMenuItem moveRightMenuItem;
    private final JMenuItem moveLeftMenuItem;
    private final JMenuItem findNextEventMenuItem;
    private final JMenuItem splitLongEventMenuItem;

    /** Creates a new instance of EventMenu
     * @param t */
    public EventMenu(PartitureTableWithActions t) {
        super(t);

        this.setText("Event");
        this.setMnemonic(java.awt.event.KeyEvent.VK_N);
        
        add(table.editEventAction).setToolTipText("Open a separate dialog to edit the current event");
        
        deleteEventMenuItem = this.add(table.deleteEventAction);
        deleteEventMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        deleteEventMenuItem.setToolTipText("Delete the current event");
        
        deleteEventsMenuItem = this.add(table.deleteEventsAction);
        //deleteEventsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        deleteEventsMenuItem.setToolTipText("Delete the currently selected events");

        addSeparator();

        shiftRightMenuItem  = this.add(table.shiftRightAction);
        shiftRightMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        shiftRightMenuItem.setToolTipText("Move characters to the right of the cursor to the adjacent event on the right");
        
        shiftLeftMenuItem  = this.add(table.shiftLeftAction);
        shiftLeftMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        shiftLeftMenuItem.setToolTipText("Move characters to the left of the cursor to the adjacent event on the left");

        addSeparator();

        mergeMenuItem  = this.add(table.mergeAction);
        mergeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mergeMenuItem.setToolTipText("Combine the currently selected events into one event");

        splitMenuItem  = this.add(table.splitAction);
        splitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        splitMenuItem.setToolTipText("Make two events from the current event, splitting it at the cursor position");

        doubleSplitMenuItem  = this.add(table.doubleSplitAction);
        doubleSplitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        doubleSplitMenuItem.setToolTipText("Make three events from the current event, splitting it at the start and end of the text selection");

        splitLongEventMenuItem  = this.add(table.splitLongEventAction);
        splitLongEventMenuItem.setToolTipText("Open a dialog for placing the cursor in a long event so you can split it");

        addSeparator();


        extendRightMenuItem = this.add(table.extendRightAction);
        extendRightMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, java.awt.event.InputEvent.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        extendRightMenuItem.setToolTipText("Extend the current event to include the adjacent interval on the right");
        
        extendLeftMenuItem  = this.add(table.extendLeftAction);
        extendLeftMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, java.awt.event.InputEvent.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        extendLeftMenuItem.setToolTipText("Extend the current event to include the adjacent interval on the left");

        shrinkRightMenuItem  = this.add(table.shrinkRightAction);
        shrinkRightMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, java.awt.event.InputEvent.ALT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      
        shrinkRightMenuItem.setToolTipText("Let the current event end one time point earlier");
        
        shrinkLeftMenuItem  = this.add(table.shrinkLeftAction);
        shrinkLeftMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, java.awt.event.InputEvent.ALT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      
        shrinkLeftMenuItem.setToolTipText("Let the current event start one time point later");

        moveRightMenuItem  = this.add(table.moveRightAction);
        moveRightMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      
        moveRightMenuItem.setToolTipText("Move the current event one time interval to the right");
        
        moveLeftMenuItem  = this.add(table.moveLeftAction);
        moveLeftMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      
        moveLeftMenuItem.setToolTipText("Move the current event one time interval to the left");

        addSeparator();
                
        findNextEventMenuItem  = this.add(table.findNextEventAction);
        findNextEventMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        findNextEventMenuItem.setToolTipText("Find the next event in the current tier");
        
        addSeparator();

        add(table.insertPauseAction).setToolTipText("Insert a pause for the length of the current selection");
        
        //findNextEventMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, java.awt.event.InputEvent.ALT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
    
}
