/**
 *
 */
package org.exmaralda.coma.models;

import java.util.EventListener;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 * @author woerner
 *
 */
public class ComaTableModel extends DefaultTableModel {
/**
	@Override
	public void addColumn(Object arg0, Object[] arg1) {
		// TODO Auto-generated method stub
		super.addColumn(arg0, arg1);
	}

	@Override
	public void addColumn(Object arg0, Vector arg1) {
		// TODO Auto-generated method stub
		super.addColumn(arg0, arg1);
	}

	@Override
	public void addColumn(Object arg0) {
		// TODO Auto-generated method stub
		super.addColumn(arg0);
	}
*/

	@Override
	public void addRow(Object[] arg0) {
		// TODO Auto-generated method stub
		super.addRow(arg0);
	}

	@Override
	public void addRow(Vector arg0) {
		// TODO Auto-generated method stub
		super.addRow(arg0);
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return super.getColumnCount();
	}

	@Override
	public String getColumnName(int arg0) {
		// TODO Auto-generated method stub
		return super.getColumnName(arg0);
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
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return super.getValueAt(arg0, arg1);
	}

	@Override
	public void insertRow(int arg0, Object[] arg1) {
		// TODO Auto-generated method stub
		super.insertRow(arg0, arg1);
	}

	@Override
	public void insertRow(int arg0, Vector arg1) {
		// TODO Auto-generated method stub
		super.insertRow(arg0, arg1);
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return super.isCellEditable(arg0, arg1);
	}

	@Override
	public void moveRow(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		super.moveRow(arg0, arg1, arg2);
	}

	@Override
	public void newDataAvailable(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		super.newDataAvailable(arg0);
	}

	@Override
	public void newRowsAdded(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		super.newRowsAdded(arg0);
	}

	@Override
	public void removeRow(int arg0) {
		// TODO Auto-generated method stub
		super.removeRow(arg0);
	}

	@Override
	public void rowsRemoved(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		super.rowsRemoved(arg0);
	}

	@Override
	public void setColumnCount(int arg0) {
		// TODO Auto-generated method stub
		super.setColumnCount(arg0);
	}

	@Override
	public void setColumnIdentifiers(Object[] arg0) {
		// TODO Auto-generated method stub
		super.setColumnIdentifiers(arg0);
	}

	@Override
	public void setColumnIdentifiers(Vector arg0) {
		// TODO Auto-generated method stub
		super.setColumnIdentifiers(arg0);
	}

	@Override
	public void setDataVector(Object[][] arg0, Object[] arg1) {
		// TODO Auto-generated method stub
		super.setDataVector(arg0, arg1);
	}

	@Override
	public void setDataVector(Vector arg0, Vector arg1) {
		// TODO Auto-generated method stub
		super.setDataVector(arg0, arg1);
	}

	@Override
	public void setNumRows(int arg0) {
		// TODO Auto-generated method stub
		super.setNumRows(arg0);
	}

	@Override
	public void setRowCount(int arg0) {
		// TODO Auto-generated method stub
		super.setRowCount(arg0);
	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		super.setValueAt(arg0, arg1, arg2);
	}

	@Override
	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		super.addTableModelListener(arg0);
	}

	@Override
	public int findColumn(String arg0) {
		// TODO Auto-generated method stub
		return super.findColumn(arg0);
	}

	@Override
	public void fireTableCellUpdated(int arg0, int arg1) {
		// TODO Auto-generated method stub
		super.fireTableCellUpdated(arg0, arg1);
	}

	@Override
	public void fireTableChanged(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		super.fireTableChanged(arg0);
	}

	@Override
	public void fireTableDataChanged() {
		// TODO Auto-generated method stub
		super.fireTableDataChanged();
	}

	@Override
	public void fireTableRowsDeleted(int arg0, int arg1) {
		// TODO Auto-generated method stub
		super.fireTableRowsDeleted(arg0, arg1);
	}

	@Override
	public void fireTableRowsInserted(int arg0, int arg1) {
		// TODO Auto-generated method stub
		super.fireTableRowsInserted(arg0, arg1);
	}

	@Override
	public void fireTableRowsUpdated(int arg0, int arg1) {
		// TODO Auto-generated method stub
		super.fireTableRowsUpdated(arg0, arg1);
	}

	@Override
	public void fireTableStructureChanged() {
		// TODO Auto-generated method stub
		super.fireTableStructureChanged();
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		// TODO Auto-generated method stub
		return super.getColumnClass(arg0);
	}

	@Override
	public <T extends EventListener> T[] getListeners(Class<T> arg0) {
		// TODO Auto-generated method stub
		return super.getListeners(arg0);
	}

	@Override
	public TableModelListener[] getTableModelListeners() {
		// TODO Auto-generated method stub
		return super.getTableModelListeners();
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		super.removeTableModelListener(arg0);
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

	/**
	 *
	 */
	public ComaTableModel() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ComaTableModel(int arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ComaTableModel(Vector arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ComaTableModel(Object[] arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ComaTableModel(Vector arg0, Vector arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ComaTableModel(Object[][] arg0, Object[] arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
