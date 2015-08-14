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
    

    /** Creates a new instance of EventMenu */
    public CLARINMenu(PartitureTableWithActions t) {
        super(t);

        this.setText("CLARIN");
        
        add(table.webMAUSAction);
        add(table.webLichtAction);
    }
    
}
