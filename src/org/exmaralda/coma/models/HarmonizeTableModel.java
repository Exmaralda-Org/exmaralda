package org.exmaralda.coma.models;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.exmaralda.coma.root.Ui;

public class HarmonizeTableModel extends DefaultTableModel {

	public HarmonizeTableModel(Map<String, String> m) {
		super(new String[] { Ui.getText("old"), Ui.getText("new") }, 0);
		for (String k : m.keySet()) {
			addRow(new Object[] { k, m.get(k) });
		}
		setRowCount(m.size());

	}

	@Override
	public void addColumn(Object columnName, Object[] columnData) {
		// TODO Auto-generated method stub
		super.addColumn(columnName, columnData);
	}

	@Override
	public void addColumn(Object columnName, Vector columnData) {
		// TODO Auto-generated method stub
		super.addColumn(columnName, columnData);
	}

	@Override
	public void addColumn(Object columnName) {
		// TODO Auto-generated method stub
		super.addColumn(columnName);
	}

	@Override
	public void addRow(Object[] rowData) {
		// TODO Auto-generated method stub
		super.addRow(rowData);
	}

	@Override
	public void addRow(Vector rowData) {
		// TODO Auto-generated method stub
		super.addRow(rowData);
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return super.getColumnCount();
	}

	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return super.getColumnName(column);
	}

	@Override
	public Vector getDataVector() {
		// TODO Auto-generated method stub
		return super.getDataVector();
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return super.getRowCount();
	}

	@Override
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		return super.getValueAt(row, column);
	}

	@Override
	public void insertRow(int row, Object[] rowData) {
		// TODO Auto-generated method stub
		super.insertRow(row, rowData);
	}

	@Override
	public void insertRow(int row, Vector rowData) {
		// TODO Auto-generated method stub
		super.insertRow(row, rowData);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return (column == 1);
	}

	@Override
	public void moveRow(int start, int end, int to) {
		// TODO Auto-generated method stub
		super.moveRow(start, end, to);
	}

	@Override
	public void newDataAvailable(TableModelEvent event) {
		// TODO Auto-generated method stub
		super.newDataAvailable(event);
	}

	@Override
	public void newRowsAdded(TableModelEvent e) {
		// TODO Auto-generated method stub
		super.newRowsAdded(e);
	}

	@Override
	public void removeRow(int row) {
		// TODO Auto-generated method stub
		super.removeRow(row);
	}

	@Override
	public void rowsRemoved(TableModelEvent event) {
		// TODO Auto-generated method stub
		super.rowsRemoved(event);
	}

	@Override
	public void setColumnCount(int columnCount) {
		// TODO Auto-generated method stub
		super.setColumnCount(columnCount);
	}

	@Override
	public void setColumnIdentifiers(Object[] newIdentifiers) {
		// TODO Auto-generated method stub
		super.setColumnIdentifiers(newIdentifiers);
	}

	@Override
	public void setColumnIdentifiers(Vector columnIdentifiers) {
		// TODO Auto-generated method stub
		super.setColumnIdentifiers(columnIdentifiers);
	}

	@Override
	public void setDataVector(Object[][] dataVector, Object[] columnIdentifiers) {
		// TODO Auto-generated method stub
		super.setDataVector(dataVector, columnIdentifiers);
	}

	@Override
	public void setDataVector(Vector dataVector, Vector columnIdentifiers) {
		// TODO Auto-generated method stub
		super.setDataVector(dataVector, columnIdentifiers);
	}

	@Override
	public void setNumRows(int rowCount) {
		// TODO Auto-generated method stub
		super.setNumRows(rowCount);
	}

	@Override
	public void setRowCount(int rowCount) {
		// TODO Auto-generated method stub
		super.setRowCount(rowCount);
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		// TODO Auto-generated method stub
		super.setValueAt(aValue, row, column);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		super.addTableModelListener(l);
	}

	@Override
	public int findColumn(String columnName) {
		// TODO Auto-generated method stub
		return super.findColumn(columnName);
	}

	@Override
	public void fireTableCellUpdated(int row, int column) {
		// TODO Auto-generated method stub
		super.fireTableCellUpdated(row, column);
	}

	@Override
	public void fireTableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		super.fireTableChanged(e);
	}

	@Override
	public void fireTableDataChanged() {
		// TODO Auto-generated method stub
		super.fireTableDataChanged();
	}

	@Override
	public void fireTableRowsDeleted(int firstRow, int lastRow) {
		// TODO Auto-generated method stub
		super.fireTableRowsDeleted(firstRow, lastRow);
	}

	@Override
	public void fireTableRowsInserted(int firstRow, int lastRow) {
		// TODO Auto-generated method stub
		super.fireTableRowsInserted(firstRow, lastRow);
	}

	@Override
	public void fireTableRowsUpdated(int firstRow, int lastRow) {
		// TODO Auto-generated method stub
		super.fireTableRowsUpdated(firstRow, lastRow);
	}

	@Override
	public void fireTableStructureChanged() {
		// TODO Auto-generated method stub
		super.fireTableStructureChanged();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		return super.getColumnClass(columnIndex);
	}

	@Override
	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		// TODO Auto-generated method stub
		return super.getListeners(listenerType);
	}

	@Override
	public TableModelListener[] getTableModelListeners() {
		// TODO Auto-generated method stub
		return super.getTableModelListeners();
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		super.removeTableModelListener(l);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	public HashMap<String, String> getDataAsMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		for (Object v : super.getDataVector()) {
			Object[] rowVector = ((Vector) v).toArray();
			map.put((String) rowVector[0], (String) rowVector[1]);
		}
		return map;
	}

	public void setData(TreeMap<String, String> keyMap) {
		System.out.println(keyMap.size());
		super.dataVector.clear();
		for (String k : keyMap.keySet()) {
			addRow(new Object[] { k, keyMap.get(k) });
		}
		setRowCount(keyMap.size());
	}

}
