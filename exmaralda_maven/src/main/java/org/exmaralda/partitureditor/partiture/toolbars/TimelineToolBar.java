/*
 * TimelineToolBar.java
 *
 * Created on 1. Juli 2003, 16:17
 */

package org.exmaralda.partitureditor.partiture.toolbars;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.*;

/**
 *
 * @author  thomas
 */
public class TimelineToolBar extends AbstractTableToolBar {
    
    private JButton editTimelineItemButton;
    private JButton insertTimelineItemButton;
    private JButton removeGapButton;
    private JButton removeUnusedTimelineItemsButton;
    
    /** Creates a new instance of TimelineToolBar */
    public TimelineToolBar(PartitureTableWithActions t) {

        super(t, "Timeline");
        
        
        this.setPreferredSize(new java.awt.Dimension(150,30));
        this.setMaximumSize(new java.awt.Dimension(150,30));

        editTimelineItemButton = this.add(table.editTimelineItemAction);
        editTimelineItemButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        insertTimelineItemButton = this.add(table.insertTimelineItemAction);
        insertTimelineItemButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        removeGapButton = this.add(table.removeGapAction);
        removeGapButton.setPreferredSize(new java.awt.Dimension(24,24));

        removeUnusedTimelineItemsButton = this.add(table.removeUnusedTimelineItemsAction);
        removeUnusedTimelineItemsButton.setPreferredSize(new java.awt.Dimension(24,24));

        

        setToolTipTexts();
        
    }
    
    
}
