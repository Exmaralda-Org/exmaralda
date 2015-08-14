/*
 * FormatMenu.java
 *
 * Created on 1. Juli 2003, 14:13
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
public class FormatMenu extends AbstractTableMenu {
    
    
    /** Creates a new instance of FormatMenu */
    public FormatMenu(PartitureTableWithActions t) {
        super(t);
        
        this.setText("Format");
        this.setToolTipText("Methoden zum Bearbeiten der Formatierungseigenschaften");
        this.setMnemonic(java.awt.event.KeyEvent.VK_R);
        
        add(table.applyFormatStylesheetAction);
        
        addSeparator();
        
        add(table.openTierFormatTableAction);
        add(table.saveTierFormatTableAction);
        
        addSeparator();

        add(table.editTierFormatTableAction);
        
        add(table.editTierFormatAction);
        //editTierFormatMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,  + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        add(table.editRowLabelFormatAction);
        
        add(table.editColumnLabelFormatAction);
        
        add(table.editTimelineItemFormatAction);
        
        addSeparator();
        
        add(table.setFrameEndAction);
        
        add(table.reformatAction);        
        
        addSeparator();
        
        add(table.underlineAction);
    }
    
}
