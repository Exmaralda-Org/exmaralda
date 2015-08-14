/*
 * Created on 19.02.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * coma2/org.sfb538.coma2/ComaTableModel.java *  * @author woerner
 */

public class ComaTableModel extends AbstractTableModel {

	private Vector columnNames;


	private Vector data;

	private String name;



	public ComaTableModel() {
		this(null);
	}

	/**
	 * @param string
	 */
	public ComaTableModel(String string) {
		name = string;
		columnNames = new Vector();
		columnNames.add(Ui.getText("parameter"));
		columnNames.add(Ui.getText("value"));
		data = new Vector();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		boolean isEditable = true;
		/**
		 * Allow editing only in Value-column, except the Attribute-column is
		 * empty
		 */
		if (col == 0) {
			isEditable = (((String) getValueAt(row, 0)).length() == 0);
		} else {
			isEditable = (!(((String) getValueAt(row, 0)).length() == 0));
		}
		/**
		 * disable editing for "Id" and "Parent" Attributes, since they are
		 * handled by the program
		 */
		if (isEditable == true) {
			if (getValueAt(row, 0) == "Id" | getValueAt(row, 0) == "Parent") {
				isEditable = false;
			}
		}
		return isEditable;
	}

	public int getColumnCount() {
		return columnNames.size();
	}

	public int getRowCount() {
		if (data == null) {
			return 0;
		} else {
			return data.size() / getColumnCount();
		}
	}

	public void setRowCount(int rowCount) {
		if (rowCount == 0) {
			data.clear();
		} else {
			System.err.println(
				"Bitte ComaTableModel.setRowCount(int rowCount) für rowCount>0 implementieren!");
			System.exit(1);
		}

	}
	
	public void addRow(Object[] values) {
		data.add(values[0]);
		data.add(values[1]);
		int size = getRowCount();
		fireTableRowsInserted(size - 1, size - 1);
	}

	public void removeRow(int row) {
		data.remove(row * 2);
		data.remove(row * 2);
		int size = getRowCount();
		fireTableRowsDeleted(size - 1, size - 1);
	}

	@Override
	public String getColumnName(int col) {
		return (String) columnNames.get(col);
	}

	public Object getValueAt(int row, int col) {
//		if (data.elementAt((row * getColumnCount()) + col).getClass().getName()			== "javax.swing.JButton") {
			return data.elementAt((row * getColumnCount()) + col);
//		} else { 
//			return data.elementAt((row * getColumnCount()) + col).toString();
//		}
	}

	public void setColumnName(int col, String name) {
		columnNames.set(col, name);
	}
	@Override
	public Class getColumnClass(int c) {
	return Object.class;	
//		return getValueAt(0, c).getClass();
	}

	
	
	
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		data.setElementAt(value, (row * getColumnCount()) + col);
		fireTableCellUpdated(row, col);
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *                  The name to set.
	 */
	public void setName(String newName) {
		name = newName;
	}

}