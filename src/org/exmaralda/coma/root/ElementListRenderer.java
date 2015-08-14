package org.exmaralda.coma.root;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jdom.Element;

public class ElementListRenderer extends DefaultListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ElementListRenderer() {
		setOpaque(false);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(TOP);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		if (value.getClass() == org.jdom.Element.class) {
			Element myElement = (Element) value;
			if (myElement.getChild("Filename") != null) {
				setText(myElement.getChild("Filename").getValue());
			} else {
				setText(myElement.toString());
			}
			if (isSelected) {
				setForeground(Color.RED);
			}

		} else {
			setText("Baeh!");
		}

		// TODO Auto-generated method stub
		return this;
	}

}
