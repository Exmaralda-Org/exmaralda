/*
 * this.java
 *
 * Created on 1. Juli 2003, 15:08
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
public class TierMenu extends AbstractTableMenu {
    
    private final JMenuItem addTierMenuItem;
    private final JMenuItem insertTierMenuItem;
    private final JMenuItem moveTierUpMenuItem;
    private final JMenuItem hideTierMenuItem;

    /** Creates a new instance of TierMenu */
    public TierMenu(PartitureTableWithActions t) {
        super(t);

        this.setText("Tier");
        this.setMnemonic(java.awt.event.KeyEvent.VK_T);
        
        add(table.editTierAction);
        add(table.editTiersAction);

        addSeparator();
        
        add(table.listEventsAction); // issue #316
        add(table.typesAction); // issue #295
        
        addSeparator();
        
        addTierMenuItem = add(table.addTierAction);
        addTierMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      
        
        insertTierMenuItem  = add(table.insertTierAction);
        insertTierMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      

        add(table.removeTierAction);
        addSeparator();

        moveTierUpMenuItem  = add(table.moveTierUpAction);
        moveTierUpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      
        add(table.changeTierOrderAction);

        addSeparator();
        
        hideTierMenuItem  = this.add(table.hideTierAction);
        hideTierMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, java.awt.event.InputEvent.ALT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));      
        add(table.showAllTiersAction);

        addSeparator();

        add(table.removeEmptyEventsAction);

        
    }
    
}
