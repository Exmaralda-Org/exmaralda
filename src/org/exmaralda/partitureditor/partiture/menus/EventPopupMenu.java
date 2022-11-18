/*
 * EventPopupMenu.java
 *
 * Created on 1. August 2003, 13:28
 */

package org.exmaralda.partitureditor.partiture.menus;

import java.awt.Component;
import javax.swing.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.common.helpers.Internationalizer;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;


/**
 *
 * @author  thomas
 */
public class EventPopupMenu extends javax.swing.JPopupMenu {
    
    PartitureTableWithActions table;

    private final JMenuItem shiftRightMenuItem;
    private final JMenuItem shiftLeftMenuItem;
    private final JMenuItem splitMenuItem;
    private final JMenuItem doubleSplitMenuItem;
    private final JMenuItem deleteMenuItem;
    
    //public final JMenu moveMenu;
    
    // 24-06-2016 MuM-Multi new 
    private final JMenuItem moveDownLeftMenuItem;
    private final JMenuItem moveDownRightMenuItem;
    
    
    private final JMenuItem copyMenuItem;
    private final JMenuItem copyHTMLMenuItem; // #338
    private final JMenuItem pasteMenuItem;
    private final JMenuItem cutMenuItem;
    private final JMenuItem underlineMenuItem;
    private final JMenuItem editEventPropertiesMenuItem;
    
    // 26-07-2016 New - issue #11
    private final JMenuItem nextEventMenuItem;

    private final Component firstSeparator;
    private final Component lastSeparator;
    
    /** Creates a new instance of EventPopupMenu
     * @param t */
    public EventPopupMenu(PartitureTableWithActions t) {
        table=t;
        
        shiftRightMenuItem  = this.add(table.shiftRightAction);      
        shiftRightMenuItem.setIcon(null);
        shiftRightMenuItem.setAccelerator(null);
        shiftRightMenuItem.setText(Internationalizer.getString("Chars right"));
        
        shiftLeftMenuItem  = this.add(table.shiftLeftAction);
        shiftLeftMenuItem.setIcon(null);
        shiftLeftMenuItem.setAccelerator(null);
        shiftLeftMenuItem.setText(Internationalizer.getString("Chars left"));

        splitMenuItem  = this.add(table.splitAction);
        splitMenuItem.setIcon(null);
        splitMenuItem.setAccelerator(null);
        splitMenuItem.setText(Internationalizer.getString("Split"));
        
        doubleSplitMenuItem  = this.add(table.doubleSplitAction);
        doubleSplitMenuItem.setIcon(null);
        doubleSplitMenuItem.setAccelerator(null);
        doubleSplitMenuItem.setText(Internationalizer.getString("Double split"));

        deleteMenuItem  = this.add(table.deleteEventAction);
        deleteMenuItem.setIcon(null);
        deleteMenuItem.setAccelerator(null);
        deleteMenuItem.setText(Internationalizer.getString("Remove"));

        add(new javax.swing.JPopupMenu.Separator());
        
        // 26-07-2016 new
        //moveMenu = new JMenu();
        //moveMenu.setText("Move to tier...");
        add(table.moveMenu);
        
        // 24-06-2016 MuM-Multi new 
        moveDownLeftMenuItem = this.add(table.moveDownLeftAction);
        moveDownRightMenuItem = this.add(table.moveDownRightAction);
        
        add(new javax.swing.JPopupMenu.Separator());

        copyMenuItem  = this.add(table.copyTextAction);      
        copyMenuItem.setIcon(null);
        copyMenuItem.setAccelerator(null);
        copyMenuItem.setText(Internationalizer.getString("Copy"));
        
        copyHTMLMenuItem  = this.add(table.copyHTMLAction);      
        copyHTMLMenuItem.setIcon(null);
        copyHTMLMenuItem.setAccelerator(null);
        copyHTMLMenuItem.setText(Internationalizer.getString("Copy HTML"));


        pasteMenuItem  = this.add(table.pasteAction);
        pasteMenuItem.setIcon(null);
        pasteMenuItem.setAccelerator(null);
        pasteMenuItem.setText(Internationalizer.getString("Paste"));

        cutMenuItem  = this.add(table.cutAction);
        cutMenuItem.setIcon(null);
        cutMenuItem.setAccelerator(null);
        cutMenuItem.setText(Internationalizer.getString("Cut"));

        lastSeparator = add(new javax.swing.JPopupMenu.Separator());
        
        underlineMenuItem  = this.add(table.underlineAction);
        underlineMenuItem.setIcon(null);
        underlineMenuItem.setAccelerator(null);
        underlineMenuItem.setText(Internationalizer.getString("Underline"));

        add(new javax.swing.JPopupMenu.Separator());
        
        nextEventMenuItem  = add(table.findNextEventAction);

        firstSeparator = add(new javax.swing.JPopupMenu.Separator());

        editEventPropertiesMenuItem  = this.add(table.editEventAction);
        editEventPropertiesMenuItem.setIcon(null);
        editEventPropertiesMenuItem.setAccelerator(null);
        editEventPropertiesMenuItem.setText(Internationalizer.getString("Event properties"));



        this.pack();
    }

    public void configureForFolker(){
        editEventPropertiesMenuItem.setVisible(false);
        firstSeparator.setVisible(false);
        lastSeparator.setVisible(false);
        underlineMenuItem.setVisible(false);

        shiftRightMenuItem.setText(FOLKERInternationalizer.getString("event_menu.charsright"));
        shiftLeftMenuItem.setText(FOLKERInternationalizer.getString("event_menu.charsleft"));
        splitMenuItem.setText(FOLKERInternationalizer.getString("event_menu.split"));
        doubleSplitMenuItem.setText(FOLKERInternationalizer.getString("event_menu.doublesplit"));
        deleteMenuItem.setText(FOLKERInternationalizer.getString("event_menu.delete"));
        copyMenuItem.setText(FOLKERInternationalizer.getString("edit_menu.copy"));
        pasteMenuItem.setText(FOLKERInternationalizer.getString("edit_menu.paste"));
        cutMenuItem.setText(FOLKERInternationalizer.getString("edit_menu.cut"));

    }
    
    
    
}
