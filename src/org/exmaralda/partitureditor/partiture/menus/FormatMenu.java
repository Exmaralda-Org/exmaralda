/*
 * FormatMenu.java
 *
 * Created on 1. Juli 2003, 14:13
 */

package org.exmaralda.partitureditor.partiture.menus;

import javax.swing.JMenu;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.partiture.formatActions.ApplyBuiltInFormatStylesheetAction;


/**
 *
 * @author  thomas
 */
public class FormatMenu extends AbstractTableMenu {
    
    String[][] builtInStylesheets = {
        {"Default", "/org/exmaralda/partitureditor/jexmaralda/xsl/FormatTable4BasicTranscription.xsl"},
        {"DULKO", "/org/exmaralda/partitureditor/jexmaralda/xsl/FormatTable4BasicTranscription.xsl"}
    };
    
    public JMenu builtInStylesheetsMenu;
    /** Creates a new instance of FormatMenu
     * @param t */
    public FormatMenu(PartitureTableWithActions t) {
        super(t);
        
        this.setText("Format");
        this.setToolTipText("Methoden zum Bearbeiten der Formatierungseigenschaften");
        this.setMnemonic(java.awt.event.KeyEvent.VK_R);
        
        add(table.applyFormatStylesheetAction);
        
        addSeparator();

        builtInStylesheetsMenu = new JMenu("Built-in stylesheets");
        add(builtInStylesheetsMenu);
        
        for (String[] pair : this.builtInStylesheets){
            ApplyBuiltInFormatStylesheetAction abifssa =
                    new ApplyBuiltInFormatStylesheetAction(t, pair[0], pair[1]);
            builtInStylesheetsMenu.add(abifssa);
        }
        
        
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
