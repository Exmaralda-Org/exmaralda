/**
 *
 */
package org.exmaralda.teide.renderers;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author woerner
 *
 */
public class FileListRenderer extends JLabel implements ListCellRenderer {

	public FileListRenderer() {
		setOpaque(true);
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList arg0,
			Object displayObject, int index, boolean isSelected, boolean arg4) {
		File theFile = (File) displayObject;
		setText(theFile.getName());
		if (isSelected) {
			setBackground(Color.orange);
		} else {
			setBackground(Color.WHITE);
		}
		return this;
	}

}
