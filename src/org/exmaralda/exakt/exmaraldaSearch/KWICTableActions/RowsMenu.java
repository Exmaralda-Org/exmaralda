/*
 * FileMenu.java
 *
 * Created on 9. Februar 2007, 11:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;


/**
 *
 * @author thomas
 */
public class RowsMenu extends javax.swing.JMenu {
    
    EXAKT exaktFrame;
    
    private JMenuItem sampleMenuItem;
    private JMenuItem selectAllMenuItem;
    private JMenuItem deselectAllMenuItem;
    private JMenuItem selectHighlightedMenuItem;
    private JMenuItem deselectHighlightedMenuItem;
    private JMenuItem removeUnselectedMenuItem;
   
    
    
    
    /** Creates a new instance of FileMenu */
    public RowsMenu(EXAKT ef) {
        setText("Rows");
        exaktFrame = ef;

        removeUnselectedMenuItem = new JMenuItem("Remove unselected");
        removeUnselectedMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                removeUnselected(e);
            }
        });
        this.add(removeUnselectedMenuItem);

        this.addSeparator();

        sampleMenuItem = new JMenuItem("Sample...");
        sampleMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                sample(e);
            }
        });
        this.add(sampleMenuItem);

        selectAllMenuItem = new JMenuItem("Select all");
        selectAllMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                selectAll(e);
            }
        });
        this.add(selectAllMenuItem);

        deselectAllMenuItem = new JMenuItem("Deselect all");
        deselectAllMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                deselectAll(e);
            }
        });
        this.add(deselectAllMenuItem);

        selectHighlightedMenuItem = new JMenuItem("Select highlighted");
        selectHighlightedMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                selectHighlighted(e);
            }
        });
        this.add(selectHighlightedMenuItem);

        deselectHighlightedMenuItem = new JMenuItem("Deselect highlighted");
        deselectHighlightedMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                deselectHighlighted(e);
            }
        });
        this.add(deselectHighlightedMenuItem);


    }

    void sample(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().getKWICTable().sampleAction.actionPerformed(e);
    }

    void selectAll(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().getKWICTable().selectAllAction.actionPerformed(e);
    }

    void deselectAll(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().getKWICTable().deselectAllAction.actionPerformed(e);
    }

    void selectHighlighted(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().getKWICTable().selectHighlightedAction.actionPerformed(e);
    }

    void deselectHighlighted(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().getKWICTable().deselectHighlightedAction.actionPerformed(e);
    }

    void removeUnselected(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().getKWICTable().removeUnselectedAction.actionPerformed(e);
    }


    public void enableMenuItems(boolean enabled){

        selectAllMenuItem.setEnabled(enabled);
        deselectAllMenuItem.setEnabled(enabled);
        selectHighlightedMenuItem.setEnabled(enabled);
        deselectHighlightedMenuItem.setEnabled(enabled);
        removeUnselectedMenuItem.setEnabled(enabled);

    }


    
    
}
