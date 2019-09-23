
package org.exmaralda.partitureditor.partiture.menus;

import java.awt.Color;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  bau2401
 */
public class InelMenu extends AbstractTableMenu {
    

    /** Creates a new instance of EventMenu */
    public InelMenu(PartitureTableWithActions t) {
        super(t);

        this.setText("INEL");
        this.setForeground(Color.BLUE);
        
        add(table.importActionInel);
        //add(table.stadtspracheWordSegmentationAction);
        //add(table.stadtspracheTierSegmentationAction);
    }
    
}
