/*
 * this.java
 *
 * Created on 1. Juli 2003, 15:17
 */

package org.exmaralda.partitureditor.partiture.menus;

import javax.swing.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.common.helpers.Internationalizer;

/**
 *
 * @author  thomas
 */

public class SegmentationMenu extends AbstractTableMenu {
    


    /** Creates a new instance of SegmentationMenu */
    public SegmentationMenu(PartitureTableWithActions t) {
        super(t);
        
        this.setText("Segmentation");        


        
        addSeparator();
        add(new JLabel("HIAT"));
        //add(table.exportHIATHTMLUtteranceListAction);
        //add(table.exportHIATXMLUtteranceListAction);

        addSeparator();
        add(new JLabel("CHAT"));
        //add(table.exportCHATHTMLUtteranceListAction);
    
        
    }
    
}
