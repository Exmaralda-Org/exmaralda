/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.annotation;

import java.awt.Component;
import javax.swing.JTree;

/**
 *
 * @author thomas
 */
public class CategoryTreeCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Category category = (Category)value;
        String labelText = "<html>" + category.getName();
        if (category.getTag()!=null){
            labelText += ": <b>" + category.getTag() + "</b>";
            if (category.keyboardShortcut!=null){
                labelText+=" <i>[" + category.keyboardShortcut + "]</i>";
            }
        }
        labelText+="</html>";
        Component c = super.getTreeCellRendererComponent(tree, labelText, sel, expanded, leaf, row, hasFocus);
        return c;
    }



}
