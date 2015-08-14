/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.annotation;


/**
 *
 * @author thomas
 */
public class AnnotationSetTreeModel extends javax.swing.tree.DefaultTreeModel {

    public AnnotationSetTreeModel(Category root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }

    public AnnotationSetTreeModel(Category root) {
        super(root);
    }

}
