package org.exmaralda.coma.models;

import java.lang.reflect.Array;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;


public class MixedDescriptionTableModel extends AbstractTableModel {

	private String[] columnNames = new String[]{"Key", "Value"};
	private HashMap<String, HashSet<String>> data = new HashMap<String, HashSet<String>>();
	public MixedDescriptionTableModel(HashMap<String, HashSet<String>> desc) {
		data = desc;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int y, int x) {
		Object[] keyArray = this.data.keySet().toArray();

		if (x == 0) {
			return keyArray[y].toString();
		} else if (x == 1) {
			HashSet<String> tmp = data.get(keyArray[y]);
			return tmp.toArray(new String[0]);
		}

		return null;
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		super.addTableModelListener(l);
	}

	@Override
	public int findColumn(String columnName) {
		return super.findColumn(columnName);
	}

	@Override
	public void fireTableCellUpdated(int row, int column) {
		super.fireTableCellUpdated(row, column);
	}

	@Override
	public void fireTableChanged(TableModelEvent e) {
		super.fireTableChanged(e);
	}

	@Override
	public void fireTableDataChanged() {
		super.fireTableDataChanged();
	}

	@Override
	public void fireTableRowsDeleted(int firstRow, int lastRow) {
		super.fireTableRowsDeleted(firstRow, lastRow);
	}

	@Override
	public void fireTableRowsInserted(int firstRow, int lastRow) {
		super.fireTableRowsInserted(firstRow, lastRow);
	}

	@Override
	public void fireTableRowsUpdated(int firstRow, int lastRow) {
		super.fireTableRowsUpdated(firstRow, lastRow);
	}

	@Override
	public void fireTableStructureChanged() {
		super.fireTableStructureChanged();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return (columnIndex == 0 ? String.class : Array.class);
	}

	@Override
	public String getColumnName(int column) {
		return super.getColumnName(column);
	}

	@Override
	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		return super.getListeners(listenerType);
	}

	@Override
	public TableModelListener[] getTableModelListeners() {
		return super.getTableModelListeners();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex==1) {
			return true;
		} else {
			Object[] keyArray = this.data.keySet().toArray();
			System.out.println(keyArray[rowIndex].toString().length());
			if (keyArray[rowIndex].toString().length()==0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		super.removeTableModelListener(l);
	}

	@Override
	public void setValueAt(Object aValue, int row, int col) {
		Object[] keyArray = this.data.keySet().toArray();
		if (col==0) {
			data.put(aValue.toString(), new HashSet<String>());
		} else {
			data.get(keyArray[row]).add(aValue.toString());
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public void addEmptyRow() {
		data.put("", new HashSet<String>());
		fireTableDataChanged();
	}

}
