/*
 * FileMenu.java
 *
 * Created on 9. Februar 2007, 11:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.regexActions;

import java.awt.event.WindowEvent;
import org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import javax.swing.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;


/**
 *
 * @author thomas
 */
public class RegexMenu extends javax.swing.JMenu implements WindowListener {
    
    EXAKT exaktFrame;

    JCheckBoxMenuItem regexLibraryDialogCheckBoxMenuItem;
    
  
    
    
    /** Creates a new instance of FileMenu */
    public RegexMenu(EXAKT ef) {
        setText("RegEx");
        exaktFrame = ef;
        
        exaktFrame.regexLibraryDialog.addWindowListener(this);

        regexLibraryDialogCheckBoxMenuItem = new JCheckBoxMenuItem("Regex Library Dialog");
        regexLibraryDialogCheckBoxMenuItem.setSelected(exaktFrame.regexLibraryDialog.isShowing());
        regexLibraryDialogCheckBoxMenuItem.addActionListener(new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                toggleRegexDialog();
            }
        });
        this.add(regexLibraryDialogCheckBoxMenuItem);

        add(new AddEntryAction(exaktFrame));
    }

    void toggleRegexDialog(){
        exaktFrame.regexLibraryDialog.setVisible(!(exaktFrame.regexLibraryDialog.isShowing()));
        regexLibraryDialogCheckBoxMenuItem.setSelected(exaktFrame.regexLibraryDialog.isShowing());
    }

    void selectAll(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().getKWICTable().selectAllAction.actionPerformed(e);
    }

    public void windowOpened(WindowEvent e) {
        regexLibraryDialogCheckBoxMenuItem.setSelected(exaktFrame.regexLibraryDialog.isShowing());
    }

    public void windowClosing(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
        regexLibraryDialogCheckBoxMenuItem.setSelected(exaktFrame.regexLibraryDialog.isShowing());
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
    
    
}
