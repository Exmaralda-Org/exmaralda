/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.regex;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;

/**
 *
 * @author thomas
 */
public class RegexLibraryTreeCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer {

    javax.swing.ImageIcon folderIcon;
    javax.swing.ImageIcon entryIcon;
    javax.swing.ImageIcon libraryIcon;

    public RegexLibraryTreeCellRenderer() {
        folderIcon =
                new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/places/folder.png"));
        entryIcon =
                new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/apps/preferences-desktop-font.png"));
        libraryIcon =
                new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/actions/address-book-new.png"));

    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        RegexLibraryTreeNode node = (RegexLibraryTreeNode)value;
        //String labelText = "<html>" + node.getDescription() + "</html>";
        String labelText = node.getDescription();
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, labelText, sel, expanded, leaf, row, hasFocus);
        label.setToolTipText(node.getDescription());
        if (node.getName().equals("folder")){
            label.setIcon(folderIcon);
        } else if (node.getName().equals("entry")){
            label.setIcon(entryIcon);
            label.setForeground(Color.BLUE);
        } else if (node.getName().equals("regex-library")){
            label.setIcon(libraryIcon);
        } else {
        }
        return label;
    }



}
