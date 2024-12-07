/*
 * Created on 28.09.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.util.List;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 * coma2/org.sfb538.coma2/CommunicationTableModel.java
 * 
 * @author woerner
 * 
 */
public class RecordingTableModel extends AbstractTableModel {
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#fireTableCellUpdated(int, int)
	 */
	@Override
	public void fireTableCellUpdated(int arg0, int arg1) {
		// TODO Auto-generated method stub
		super.fireTableCellUpdated(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.AbstractTableModel#fireTableChanged(javax.swing.event
	 * .TableModelEvent)
	 */
	@Override
	public void fireTableChanged(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		super.fireTableChanged(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#fireTableDataChanged()
	 */
	@Override
	public void fireTableDataChanged() {
		// TODO Auto-generated method stub
		super.fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#fireTableRowsDeleted(int, int)
	 */
	@Override
	public void fireTableRowsDeleted(int arg0, int arg1) {
		// TODO Auto-generated method stub
		super.fireTableRowsDeleted(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#fireTableRowsInserted(int, int)
	 */
	@Override
	public void fireTableRowsInserted(int arg0, int arg1) {
		// TODO Auto-generated method stub
		super.fireTableRowsInserted(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#fireTableRowsUpdated(int, int)
	 */
	@Override
	public void fireTableRowsUpdated(int arg0, int arg1) {
		// TODO Auto-generated method stub
		super.fireTableRowsUpdated(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#fireTableStructureChanged()
	 */
	@Override
	public void fireTableStructureChanged() {
		// TODO Auto-generated method stub
		super.fireTableStructureChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object obj, int row, int col) {
		data.set(((row * 3) + col), obj);
		fireTableCellUpdated(row, col);
	}

	/**
	 *
	 */
	// public Coma coma;
	private Vector columnNames;

	private Vector data;

	private String secondColumn;

	private XPath commXPath;

	public RecordingTableModel() {
		super();
		data = new Vector();
		columnNames = new Vector();
		columnNames.add("Element");
		columnNames.add("Name");
		columnNames.add("Type");
		try {
			commXPath = XPath.newInstance("Filename");
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public String getColumnName(int col) {
		return columnNames.get(col).toString();
	}

	@Override
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * PersonTables should only be editable for boolean values (for
	 * communication-assignments)
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		// return (getValueAt(row, col) instanceof Boolean);
		return false;
	}

	public void setColumnName(int col, String name) {
		columnNames.set(col, name);
	}

	public int getColumnCount() {
		if (columnNames != null) {
			return columnNames.size();
		} else {
			return 0;
		}
	}

	/*
	 * sets data for whole rows
	 */
	public void setColumnValue(int col, Object o) {
		for (int i = 0; i < getRowCount(); i++) {
			data.set(i * 3 + col, o);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		if ((data == null) || (getColumnCount() == 0)) {
			return 0;
		} else {
			return data.size() / getColumnCount();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col) {
		return data.elementAt((row * getColumnCount()) + col);
	}

	/*
	 * sets the number of rows, works only for (rowCount==0) or
	 * (rowCount>actualRowCount)
	 */
	public void setRowCount(int rowCount) {
		if (rowCount == 0) {
			data.clear();
			fireTableDataChanged();
		} else if (getRowCount() != rowCount) {
			for (int i = getRowCount(); i < rowCount; i++) {
				data.add(new Object());
				data.add(new Object());
			}
			fireTableRowsInserted(rowCount, rowCount);
		}
	}

	/**
	 * @param objects
	 */
	public void addRow(org.jdom.Element elm) {
		String strOut;
		// data.add(elm.getAttributeValue("Name"));
		data.add(elm);
		if (elm.getChild("Name") == null) {
			if (elm.getChild("Media").getChild("Filename") == null) {
				data.add("unnamed, no media file.");
			} else {
				data.add("file: "
						+ elm.getChild("Media").getChild("Filename").getText()
						+ " (unnamed)");

			}

		} else {
			data.add(elm.getChild("Name").getText());
		}
		try {
			List results = commXPath.selectNodes(elm);
			if (results.size() > 0) {
				strOut = ((Element) results.get(0)).getText();
			} else {
				strOut = "(?)";
			}
			data.add(strOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int size = getRowCount();
		fireTableRowsInserted(size - 1, size - 1);
	}

	public void setRow(org.jdom.Element elm, int row) {
		String strOut;
		data.set(row * 3, elm);
		data.set((row * 3) + 1, elm.getChild("Sigle").getText());
		try {
			List results = commXPath.selectNodes(elm);
			if (results.size() > 0) {
				strOut = ((Element) results.get(0)).getText();
			} else {
				strOut = "(?)";
			}
			data.set((row * 3) + 2, strOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fireTableRowsUpdated(row, row);
	}

	public void removeRow(org.jdom.Element elm) {
		int pos = data.indexOf(elm);
		data.remove(pos);
		data.remove(pos);
		data.remove(pos);
		data.remove(pos);
		fireTableRowsDeleted(pos / 3, pos / 3);
	}

	/**
	 * @param text
	 */
	public boolean setSecondColumn(String text, boolean blnXpath) {
		String xPathString;
		if (!blnXpath) {
			xPathString = "Description/Key[@Name='" + text + "']";
		} else {
			xPathString = text;
		}
		try {
			commXPath = XPath.newInstance(xPathString);
			return true;
		} catch (JDOMException e) {
			e.printStackTrace();
			return false;
		}
		// secondColumn = text;
	}
}