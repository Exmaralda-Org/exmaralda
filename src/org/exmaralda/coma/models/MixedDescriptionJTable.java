package org.exmaralda.coma.models;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class MixedDescriptionJTable extends JTable {
	MixedDescriptionTableModel tm;

	public MixedDescriptionJTable(MixedDescriptionTableModel t) {
		super(t);
		tm = t;
	}

	@Override
	public TableCellEditor getCellEditor(int row, int col) {

		if (col == 1) {
			String[] tmp = (String[]) tm.getValueAt(row, col);
			JComboBox cb = new JComboBox(tmp);
			cb.setEditable(true);
			DefaultCellEditor ed = new DefaultCellEditor(cb);
			return ed;
		} else {
			JTextField tf = new JTextField();
			DefaultCellEditor ed = new DefaultCellEditor(tf);
			return ed;
		}
	}

}
