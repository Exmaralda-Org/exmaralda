/*
 * this.java
 *
 * Created on 1. Juli 2003, 15:11
 */

package org.exmaralda.partitureditor.partiture.menus;

import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class CLARINMenu extends AbstractTableMenu {
    

    /** Creates a new instance of EventMenu
     * @param t */
    public CLARINMenu(PartitureTableWithActions t) {
        super(t);

        // changed 04-11-2021, issue #285
        //this.setText("CLARIN");
        this.setText("Web Services");
        
        add(table.webMAUSAction);
        add(table.webMAUSFineAlignmentAction);
        add(table.webLichtAction);
    }
    
}
