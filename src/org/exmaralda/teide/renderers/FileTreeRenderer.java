/**
 *
 */
package org.exmaralda.teide.renderers;

import java.awt.Component;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author woerner
 *
 */
public class FileTreeRenderer extends DefaultTreeCellRenderer {

	public FileTreeRenderer() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (((DefaultMutableTreeNode) value).getUserObject() != null) {
			File theFile = (File) ((DefaultMutableTreeNode) value)
					.getUserObject();
			super.getTreeCellRendererComponent(tree, theFile.getName(), sel,
					expanded, leaf, row, hasFocus);

		} else
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);

		return this;
	}

}
