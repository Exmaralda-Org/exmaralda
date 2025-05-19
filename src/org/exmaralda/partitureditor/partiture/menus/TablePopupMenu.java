/*
 * TablePopupMenu.java
 *
 * Created on 16. September 2003, 17:44
 */

package org.exmaralda.partitureditor.partiture.menus;

import javax.swing.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.common.helpers.Internationalizer;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;

/**
 *
 * @author  thomas
 */
public class TablePopupMenu extends javax.swing.JPopupMenu {
    
    PartitureTableWithActions table;

    private final JMenuItem listEventsMenuItem;
    private final JMenuItem editTierPropertiesMenuItem;
    private final JMenuItem insertTierMenuItem;
    private final JMenuItem deleteTierMenuItem;
    private final JMenuItem hideTierMenuItem;
    private final JMenuItem tierUpMenuItem;
    private final JMenuItem editTimelineItemMenuItem;
    private final JMenuItem insertTimelineItemMenuItem;
    private final JMenuItem confirmTimelineItemMenuItem;
    private final JMenuItem removeGapMenuItem;
    private final JMenuItem addBookmarkMenuItem;
    private final JMenuItem formatTierMenuItem;
    private JMenuItem copyMenuItem;
    private JMenuItem copyHTMLMenuItem;
    private JMenuItem selectBlockMenuItem;
        
    
    /** Creates a new instance of TablePopupMenu
     * @param t */
    public TablePopupMenu(PartitureTableWithActions t) {
        
        table=t;
        
        editTierPropertiesMenuItem  = this.add(table.editTierAction);      
        editTierPropertiesMenuItem.setIcon(null);
        editTierPropertiesMenuItem.setAccelerator(null);
        editTierPropertiesMenuItem.setText(Internationalizer.getString("Tier properties"));

        listEventsMenuItem  = this.add(table.listEventsAction);      
        listEventsMenuItem.setIcon(null);
        listEventsMenuItem.setAccelerator(null);
        listEventsMenuItem.setText(Internationalizer.getString("List events"));

        this.add(new javax.swing.JPopupMenu.Separator());
       
        insertTierMenuItem  = this.add(table.insertTierAction);      
        insertTierMenuItem.setIcon(null);
        insertTierMenuItem.setAccelerator(null);
        insertTierMenuItem.setText(Internationalizer.getString("Insert tier"));
        
        deleteTierMenuItem  = this.add(table.removeTierAction);
        deleteTierMenuItem.setIcon(null);
        deleteTierMenuItem.setAccelerator(null);
        deleteTierMenuItem.setText(Internationalizer.getString("Remove tier"));

        hideTierMenuItem  = this.add(table.hideTierAction);
        hideTierMenuItem.setIcon(null);
        hideTierMenuItem.setAccelerator(null);
        hideTierMenuItem.setText(Internationalizer.getString("Hide tier"));

        tierUpMenuItem  = this.add(table.moveTierUpAction);
        tierUpMenuItem.setIcon(null);
        tierUpMenuItem.setAccelerator(null);
        tierUpMenuItem.setText(Internationalizer.getString("Move tier up"));

        this.add(new javax.swing.JPopupMenu.Separator());
        
        editTimelineItemMenuItem  = this.add(table.editTimelineItemAction);
        editTimelineItemMenuItem.setIcon(null);
        editTimelineItemMenuItem.setAccelerator(null);
        editTimelineItemMenuItem.setText(Internationalizer.getString("Edit timeline item"));
        
        insertTimelineItemMenuItem  = this.add(table.insertTimelineItemAction);
        insertTimelineItemMenuItem.setIcon(null);
        insertTimelineItemMenuItem.setAccelerator(null);
        insertTimelineItemMenuItem.setText(Internationalizer.getString("Insert timeline item"));

        confirmTimelineItemMenuItem  = this.add(table.confirmTimelineItemAction);
        confirmTimelineItemMenuItem.setIcon(null);
        confirmTimelineItemMenuItem.setAccelerator(null);
        confirmTimelineItemMenuItem.setText(Internationalizer.getString("Confirm timeline item(s)"));

        removeGapMenuItem  = this.add(table.removeGapAction);
        removeGapMenuItem.setIcon(null);
        removeGapMenuItem.setAccelerator(null);
        removeGapMenuItem.setText(Internationalizer.getString("Remove gap"));

        addBookmarkMenuItem  = this.add(table.addBookmarkAction);
        addBookmarkMenuItem.setIcon(null);
        addBookmarkMenuItem.setAccelerator(null);
        addBookmarkMenuItem.setText(Internationalizer.getString("Add bookmark"));

        this.add(new javax.swing.JPopupMenu.Separator());

        formatTierMenuItem = this.add(table.editTierFormatAction);
        formatTierMenuItem.setIcon(null);
        formatTierMenuItem.setAccelerator(null);
        formatTierMenuItem.setText(Internationalizer.getString("Tier format"));
             
        this.add(new javax.swing.JPopupMenu.Separator());

        copyMenuItem = this.add(table.copyTextAction);
        copyMenuItem.setIcon(null);
        copyMenuItem.setAccelerator(null);
        copyMenuItem.setText(Internationalizer.getString("Copy"));

        copyHTMLMenuItem = this.add(table.copyHTMLAction);
        copyHTMLMenuItem.setIcon(null);
        copyHTMLMenuItem.setAccelerator(null);
        copyHTMLMenuItem.setText(Internationalizer.getString("Copy HTML"));


        this.add(new javax.swing.JPopupMenu.Separator());
        
        //this.add(table.moveMenu);
        
        selectBlockMenuItem = this.add(table.selectBlockAction);

        this.pack();
    }
    
    public void configure(boolean partiturIsLocked){
        editTierPropertiesMenuItem.setVisible(!partiturIsLocked);
        insertTierMenuItem.setVisible(!partiturIsLocked);
        tierUpMenuItem.setVisible(!partiturIsLocked);
        editTimelineItemMenuItem.setVisible(!partiturIsLocked);
        insertTimelineItemMenuItem.setVisible(!partiturIsLocked);
        removeGapMenuItem.setVisible(!partiturIsLocked);
        addBookmarkMenuItem.setVisible(!partiturIsLocked);
        formatTierMenuItem.setVisible(!partiturIsLocked);
        deleteTierMenuItem.setVisible(!partiturIsLocked);
    }

    public void configureForFolker(){
        removeAll();
        copyMenuItem = this.add(table.copyTextAction);
        copyMenuItem.setIcon(null);
        copyMenuItem.setAccelerator(null);
        copyMenuItem.setText(FOLKERInternationalizer.getString("edit_menu.copy"));

    }
    
}
