/*
 * this.java
 *
 * Created on 1. Juli 2003, 15:11
 */

package org.exmaralda.partitureditor.partiture.menus;

import java.awt.Color;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class SFB538Menu extends AbstractTableMenu {
    

    /** Creates a new instance of EventMenu */
    public SFB538Menu(PartitureTableWithActions t) {
        super(t);

        this.setText("SFB 538/632");
        this.setForeground(Color.BLUE);

        add(table.syllableStructureAction);
        add(table.k8MysteryConverterAction);
        add(table.exSyncCleanupAction);
        addSeparator();
        add(table.appendSpaceAction);
    }
    
}
