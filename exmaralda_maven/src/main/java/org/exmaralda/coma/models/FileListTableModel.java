/**
 *
 */
package org.exmaralda.coma.models;

import java.io.File;
import java.util.EventListener;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 * @author woerner
 * 
 */
public class FileListTableModel extends DefaultTableModel {

	/**
	 *
	 */
	public FileListTableModel() {
	}

	/**
	 * @param rowCount
	 * @param columnCount
	 */
	public FileListTableModel(int rowCount, int columnCount) {
		super(rowCount, columnCount);

	}

	public Vector<File> getSelectedFiles() {
		Vector sFile = new Vector<File>();
		for (int i = 0; i < dataVector.size(); i++) {
			Vector v = (Vector) dataVector.get(i);
			// changed by the evil TS, 19-01-2009
			if (((Boolean) v.get(0)).booleanValue()) {
				sFile.add((File) v.get(1));
			}
		}
		return sFile;
	}

	/**
	 * @param columnNames
	 * @param rowCount
	 */
	public FileListTableModel(Vector columnNames, int rowCount) {
		super(columnNames, rowCount);

	}

	/**
	 * @param columnNames
	 * @param rowCount
	 */
	public FileListTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);

	}

	/**
	 * @param data
	 * @param columnNames
	 */
	public FileListTableModel(Vector data, Vector columnNames) {
		super(data, columnNames);

	}

	/**
	 * @param data
	 * @param columnNames
	 */
	public FileListTableModel(Object[][] data, Object[] columnNames) {
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
		if (column == 1) {
			return (((File) super.getValueAt(row, column)).getName() + ((((File) super
					.getValueAt(row, column)).isDirectory()) ? " (Dir)" : ""));

		} else {
			return super.getValueAt(row, column);
		}
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
		return (column == 0);
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
		switch (columnIndex) {
			case 1 :
				return String.class;
			case 2 :
				return String.class;
			default :
				return Boolean.class;
		}
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
	 * @param b
	 * 
	 */
	public void selectBasic(boolean b) {
		for (int i = 0; i < getRowCount(); i++) {
			if (((Boolean) getValueAt(i, 3)) == false) {
				setValueAt(b, i, 0);
			}
		}
	}

	public void selectSegmented(boolean b) {
		for (int i = 0; i < getRowCount(); i++) {
			if (((Boolean) getValueAt(i, 3)) == true) {
				setValueAt(b, i, 0);
			}
		}
	}

	// public void deselectAll() {
	// for (int i = 0; i < getRowCount(); i++) {
	// setValueAt(false, i, 0);
	//
	// }
	// }
}
