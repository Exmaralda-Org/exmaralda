/*
 * FileMenu.java
 *
 * Created on 9. Februar 2007, 11:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.viewActions;

import javax.swing.JCheckBoxMenuItem;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;


/**
 *
 * @author thomas
 */
public class ViewMenu extends javax.swing.JMenu {
    
    EXAKT exaktFrame;
    public JCheckBoxMenuItem showMediaPanelCheckBoxMenuItem;
    public JCheckBoxMenuItem showLinkPanelCheckBoxMenuItem;
    
    
    /** Creates a new instance of FileMenu */
    public ViewMenu(EXAKT ef) {
        setText("View");
        exaktFrame = ef;

        add(exaktFrame.browsingModeAction);
        showMediaPanelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showMediaPanelCheckBoxMenuItem.setSelected(false);
        showMediaPanelCheckBoxMenuItem.setText("Show video panel");
        showMediaPanelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showMediaPanelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        add(showMediaPanelCheckBoxMenuItem);

        showLinkPanelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showLinkPanelCheckBoxMenuItem.setSelected(false);
        showLinkPanelCheckBoxMenuItem.setText("Show link panel");
        showLinkPanelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLinkPanelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        add(showLinkPanelCheckBoxMenuItem);
    }


    private void showMediaPanelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        exaktFrame.getPartitur().mediaPanelDialog.setVisible(showMediaPanelCheckBoxMenuItem.isSelected());
    }

    private void showLinkPanelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        exaktFrame.getPartitur().linkPanelDialog.setVisible(showLinkPanelCheckBoxMenuItem.isSelected());
    }

}
