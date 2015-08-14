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

    private JMenuItem shiftRightMenuItem;
    private JMenuItem shiftLeftMenuItem;
    private JMenuItem splitMenuItem;
    private JMenuItem doubleSplitMenuItem;
    private JMenuItem deleteMenuItem;
    private JMenuItem copyMenuItem;
    private JMenuItem pasteMenuItem;
    private JMenuItem cutMenuItem;
    private JMenuItem underlineMenuItem;
    private JMenuItem editEventPropertiesMenuItem;

    private Component firstSeparator;
    private Component lastSeparator;
    
    /** Creates a new instance of EventPopupMenu */
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

        this.add(new javax.swing.JPopupMenu.Separator());
        
        copyMenuItem  = this.add(table.copyTextAction);      
        copyMenuItem.setIcon(null);
        copyMenuItem.setAccelerator(null);
        copyMenuItem.setText(Internationalizer.getString("Copy"));
        
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
