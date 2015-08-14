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
public class SinMenu extends AbstractTableMenu {
    

    /** Creates a new instance of EventMenu */
    public SinMenu(PartitureTableWithActions t) {
        super(t);

        this.setText("SiN");
        this.setForeground(Color.BLUE);
        
        add(table.stadtspracheWordSegmentationAction);
        add(table.stadtspracheTierSegmentationAction);
    }
    
}
