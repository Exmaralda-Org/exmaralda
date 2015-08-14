package org.exmaralda.coma.render;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.exmaralda.coma.root.IconFactory;

public class ConnectionCellRenderer extends DefaultTableCellRenderer implements
		TableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int col) {
		Component comp;
		if (value instanceof Boolean) {
			if ((Boolean) value) {
				comp = new JLabel(IconFactory
						.createImageIcon("bueroklammer16.png"));
			} else {
				comp = new JLabel(" ");
			}
		} else {
			comp = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, col);
		}
//		if (isSelected == false && hasFocus == false) {
//			((JComponent) comp).setOpaque(true); // if comp is a JLabel
//			if ((Boolean) table.getValueAt(row, 0)) {
//				// if ((row % 2) == 1) { // you can specify arbitrary row
//				comp.setBackground(Color.LIGHT_GRAY);
//			} else {
//				comp.setBackground(Color.white);
//			}
//		}
		return comp;
	}
}
