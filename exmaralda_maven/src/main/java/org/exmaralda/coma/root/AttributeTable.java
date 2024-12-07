/*
 * Created on 10.03.2004 by woerner
 */
package org.exmaralda.coma.root;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * coma2/org.sfb538.coma2/PersonAttributeTable.java
 * 
 * @author woerner
 */
public class AttributeTable extends JTable implements TableModelListener {
	private ComaTableModel tableModel;
	public AttributeTable(TableModel arg0) {
		super();
		setModel(arg0);
		this.getModel().addTableModelListener(this);
	}
	// make sure to get the right cell renderers
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		TableColumn tableColumn = getColumnModel().getColumn(column);
		TableCellRenderer renderer = tableColumn.getCellRenderer();
		if (renderer == null) {
			Class c = getColumnClass(column);
			if (c.equals(Object.class)) {
				Object o = getValueAt(row, column);
				if (o != null)
					c = getValueAt(row, column).getClass();
			}
			renderer = getDefaultRenderer(c);
		}
		return renderer;
	}
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		TableColumn tableColumn = getColumnModel().getColumn(column);
		TableCellEditor editor = tableColumn.getCellEditor();
		if (editor == null) {
			Class c = getColumnClass(column);
			if (c.equals(Object.class)) {
				Object o = getValueAt(row, column);
				if (o != null)
					c = getValueAt(row, column).getClass();
			}
			editor = getDefaultEditor(c);
		}
		return editor;
	}
}
