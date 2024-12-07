/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.regex;



/**
 *
 * @author thomas
 */
public class RegexLibraryTreeModel extends javax.swing.tree.DefaultTreeModel {

    public RegexLibraryTreeModel(RegexLibraryTreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }

    public RegexLibraryTreeModel(RegexLibraryTreeNode root) {
        super(root);
    }

}
