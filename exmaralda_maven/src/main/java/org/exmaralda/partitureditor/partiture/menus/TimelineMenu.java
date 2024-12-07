/*
 * this.java
 *
 * Created on 1. Juli 2003, 15:13
 */

package org.exmaralda.partitureditor.partiture.menus;

import javax.swing.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.common.helpers.Internationalizer;

/**
 *
 * @author  thomas
 */
public class TimelineMenu extends AbstractTableMenu {
    
    public JCheckBoxMenuItem timelineModeCheckBoxMenuItem;
    public JCheckBoxMenuItem playbackModeCheckBoxMenuItem;
    

    /** Creates a new instance of TimelineMenu */
    public TimelineMenu(PartitureTableWithActions t) {
        super(t);
        
        this.setText("Timeline");
        this.setMnemonic(java.awt.event.KeyEvent.VK_L);

        add(table.editTimelineItemAction);
        add(table.insertTimelineItemAction);
        addSeparator();
        add(table.removeGapAction);
        add(table.removeAllGapsAction);
        add(table.removeUnusedTimelineItemsAction);
        add(table.makeTimelineConsistentAction);
        add(table.smoothTimelineAction);
        add(table.anchorTimelineItemAction);
        addSeparator();
        add(table.completeTimelineAction);
        add(table.removeInterpolatedTimesAction);
        add(table.removeTimesAction);
        add(table.confirmTimelineItemAction);
        addSeparator();
        add(table.shiftAbsoluteTimesAction);
        addSeparator();
        timelineModeCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        timelineModeCheckBoxMenuItem.setSelected(false);
        timelineModeCheckBoxMenuItem.setText(Internationalizer.getString("Fine tuning mode"));
        timelineModeCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timelineModeCheckBoxMenuItemActionPerformed(evt);
            }
        });
        add(timelineModeCheckBoxMenuItem);
        
        add(table.easyAlignmentAction);


        addSeparator();

        add(table.addBookmarkAction);
        add(table.bookmarksAction);
    }
    
   private void timelineModeCheckBoxMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
       table.toggleTimelineMode();
   }
        
}
