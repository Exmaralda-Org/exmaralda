/**
 *
 */
package org.exmaralda.coma.models;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 * @author woerner
 *
 */
public class AssignmentTableModel extends DefaultTableModel {

	/**
	 *
	 */
	public AssignmentTableModel() {
	}

	/**
	 * @param rowCount
	 * @param columnCount
	 */
	public AssignmentTableModel(int rowCount, int columnCount) {
		super(rowCount, columnCount);

	}

	/**
	 * @param columnNames
	 * @param rowCount
	 */
	public AssignmentTableModel(Vector columnNames, int rowCount) {
		super(columnNames, rowCount);

	}

	/**
	 * @param columnNames
	 * @param rowCount
	 */
	public AssignmentTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);

	}

	/**
	 * @param data
	 * @param columnNames
	 */
	public AssignmentTableModel(Vector data, Vector columnNames) {
		super(data, columnNames);

	}

	/**
	 * @param data
	 * @param columnNames
	 */
	public AssignmentTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);

	}

	@Override
	public void addColumn(Object columnName, Object[] columnData) {
		super.addColumn(columnName, columnData);
	}

	@Override
	public void addColumn(Object columnName, Vector columnData) {
		super.addColumn(columnName, columnData);
	}

	@Override
	public void addColumn(Object columnName) {
		super.addColumn(columnName);
	}

	@Override
	public void addRow(Object[] rowData) {
		super.addRow(rowData);
	}

	@Override
	public void addRow(Vector rowData) {
		super.addRow(rowData);
	}

	@Override
	public int getColumnCount() {
		return super.getColumnCount();
	}

	@Override
	public String getColumnName(int column) {
		return super.getColumnName(column);
	}

	@Override
	public Vector getDataVector() {
		return super.getDataVector();
	}

	@Override
	public int getRowCount() {
		return super.getRowCount();
	}

	@Override
	public Object getValueAt(int row, int column) {

		return super.getValueAt(row, column);
	}

	@Override
	public void insertRow(int row, Object[] rowData) {
		super.insertRow(row, rowData);
	}

	@Override
	public void insertRow(int row, Vector rowData) {
		super.insertRow(row, rowData);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		System.out.println(column);
		if (column == 1) {
			return false;
		}
		return true;

	}

	@Override
	public void moveRow(int start, int end, int to) {
		super.moveRow(start, end, to);
	}

	@Override
	public void newDataAvailable(TableModelEvent event) {
		super.newDataAvailable(event);
	}

	@Override
	public void newRowsAdded(TableModelEvent e) {
		super.newRowsAdded(e);
	}

	@Override
	public void removeRow(int row) {
		super.removeRow(row);
	}

	@Override
	public void rowsRemoved(TableModelEvent event) {
		super.rowsRemoved(event);
	}

	@Override
	public void setColumnCount(int columnCount) {
		super.setColumnCount(columnCount);
	}

	@Override
	public void setColumnIdentifiers(Object[] newIdentifiers) {
		super.setColumnIdentifiers(newIdentifiers);
	}

	@Override
	public void setColumnIdentifiers(Vector columnIdentifiers) {
		super.setColumnIdentifiers(columnIdentifiers);
	}

	@Override
	public void setDataVector(Object[][] dataVector, Object[] columnIdentifiers) {
		super.setDataVector(dataVector, columnIdentifiers);
	}

	@Override
	public void setDataVector(Vector dataVector, Vector columnIdentifiers) {
		super.setDataVector(dataVector, columnIdentifiers);
	}

	@Override
	public void setNumRows(int rowCount) {
		super.setNumRows(rowCount);
	}

	@Override
	public void setRowCount(int rowCount) {
		super.setRowCount(rowCount);
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		super.setValueAt(value, row, column);
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
		return (columnIndex == 0) ? Boolean.class : String.class;
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
	public void removeTableModelListener(TableModelListener l) {
		super.removeTableModelListener(l);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}


	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * 
	 */
	public void clear() {
		dataVector.clear();
		fireTableDataChanged();
	}

	public HashMap<String, String> getMeta(String whatMeta) {
		HashMap<String, String> meta = new HashMap<String, String>();
		for (int i = 0; i < dataVector.size(); i++) {
			Vector<Object> v = (Vector) dataVector.get(i);
			if ((Boolean) v.get(0)) {
				if (((String) v.get(3)).equals(whatMeta)) {
					meta.put((String) v.get(1), (String) v.get(2));
				}
			}
		}
		return meta;
	}
}
