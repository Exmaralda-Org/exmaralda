/*
 * this.java
 *
 * Created on 1. Juli 2003, 15:03
 */

package org.exmaralda.partitureditor.partiture.menus;

import javax.swing.*;
import org.exmaralda.partitureditor.partiture.*;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import org.exmaralda.common.helpers.Internationalizer;

/**
 *
 * @author  thomas
 */
public class EditMenu extends AbstractTableMenu {
    
    private final JMenuItem undoMenuItem;

    private final JMenuItem copyTextMenuItem;
    private final JMenuItem copyHTMLMenuItem; // #338
    private final JMenuItem copyStructureMenuItem; // #471
    private final JMenuItem pasteMenuItem;
    private final JMenuItem pasteStructureMenuItem; // #471
    private final JMenuItem cutMenuItem;

    private final JMenuItem searchInEventsMenuItem;
    private final JMenuItem findNextMenuItem;
    private final JMenuItem replaceInEventsMenuItem;
    private final JMenuItem lowerUpperCaseMenuItem;

    private final JMenuItem exaktSearchMenuItem;

    
    //private final javax.swing.JMenu selectionMenu;


    /** Creates a new instance of EditMenu
     * @param t */
    public EditMenu(PartitureTableWithActions t) {
        super(t);
        
        this.setText("Edit");
        this.setMnemonic(java.awt.event.KeyEvent.VK_E);
        
        /*selectionMenu = new javax.swing.JMenu();
        selectionMenu.setText(Internationalizer.getString("Selection"));
        selectionMenu.setToolTipText("Operations for the current selection");*/

        
        undoMenuItem = this.add(table.undoAction);
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undoMenuItem.setToolTipText("Undo the last action");
        
        addSeparator();

        // edit menu
        copyTextMenuItem = this.add(table.copyTextAction);
        copyTextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        
        copyHTMLMenuItem = this.add(table.copyHTMLAction);
        copyHTMLMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK+ActionEvent.SHIFT_MASK));
        copyHTMLMenuItem.setToolTipText("Copy the current selection as HTML");
        
        copyStructureMenuItem = this.add(table.copyStructureAction);
        copyStructureMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK+ActionEvent.ALT_MASK));
        copyHTMLMenuItem.setToolTipText("Copy the current selection as a transcript structure");

        pasteMenuItem = this.add(table.pasteAction);
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        
        pasteStructureMenuItem = this.add(table.pasteStructureAction);
        pasteStructureMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK+ActionEvent.ALT_MASK));
        pasteStructureMenuItem.setToolTipText("Paste the current transcript structure from the clipboard");

        cutMenuItem = this.add(table.cutAction);
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        
        this.add(new javax.swing.JSeparator());
        //-------------------------------------------------

        searchInEventsMenuItem = this.add(table.searchInEventsAction);
        searchInEventsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        searchInEventsMenuItem.setToolTipText("Search for text in events");
        
        findNextMenuItem = this.add(table.findNextAction);
        findNextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        findNextMenuItem.setToolTipText("Find the next item for the last search in events");

        replaceInEventsMenuItem = this.add(table.replaceInEventsAction);
        replaceInEventsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
        replaceInEventsMenuItem.setToolTipText("Search and replace text in events");
        
        lowerUpperCaseMenuItem = this.add(table.lowerUpperCaseAction);
        replaceInEventsMenuItem.setToolTipText("Replace upper with lower case letters or vice versa");

        add(table.gotoAction).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));

        this.add(new javax.swing.JSeparator());
        //-------------------------------------------------

        exaktSearchMenuItem = this.add(table.exaktSearchAction);
        exaktSearchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.SHIFT_MASK + ActionEvent.CTRL_MASK));
        exaktSearchMenuItem.setToolTipText("Use EXAKT interface to do a search on this transcription");
                
        this.add(new javax.swing.JSeparator());
        //-------------------------------------------------
        add(table.selectionToNewAction).setToolTipText("Make a new transcription from the current selection");
        add(table.leftPartToNewAction).setToolTipText("Make a new transcription out of the part on the left of the cursor");
        add(table.rightPartToNewAction).setToolTipText("Make a new transcription out of the part on the right of the cursor");
        /*selectionMenu.add(new javax.swing.JSeparator());
        selectionMenu.add(table.selectionToRTFAction).setToolTipText("Output the current selection as an RTF file (for MS Word)");
        selectionMenu.add(table.selectionToHTMLAction).setToolTipText("Output the current selection as an HTML file (for a browser)");
        selectionMenu.add(table.printSelectionAction).setToolTipText("");*/
        
        //this.add(selectionMenu);
        
        this.addSeparator();
        //-------------------------------------------------
        

        add(table.editPreferencesAction).setToolTipText("Change settings and preferences for the Partitur Editor");
        add(table.editPartiturParametersAction).setToolTipText("Change settings and preferences for outputting and formatting partiturs");
    }

    public void setUndoText(String text){
        if (text==null){
            undoMenuItem.setText(Internationalizer.getString("Undo"));
        } else {
            undoMenuItem.setText(Internationalizer.getString("Undo") + ": " + text);
        }
    }
    
}
