/**
 *
 */
package org.exmaralda.coma.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.exmaralda.coma.datatypes.Description;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

/**
 * @author woerner
 * 
 */
public class DescriptionTableModel extends AbstractTableModel implements
		TableModelListener {
	public Coma coma;

	private Vector<String> columnNames;

	private Vector<String> data;

	private boolean directChanges;

	private Description description;

	private HashSet<String> lockedKeys;

	/**
	 * @param coma2
	 * @param description
	 */

	public DescriptionTableModel(Coma c, Description description) {
		this(c, description, false);
	}

	public DescriptionTableModel(Coma c, Description d, boolean dc) {
		lockedKeys = new HashSet<String>();
		coma = c;
		description = d;
		directChanges = dc;
		columnNames = new Vector();
		columnNames.add(Ui.getText("parameter"));
		columnNames.add(Ui.getText("value"));
		columnNames.add("...");
		data = makeData(description);
		// data = description.getData();
		this.addTableModelListener(this);
	}

	public DescriptionTableModel(Coma c) {
		lockedKeys = new HashSet<String>();
		coma = c;
		description = null;
		columnNames = new Vector();
		columnNames.add(Ui.getText("parameter"));
		columnNames.add(Ui.getText("value"));
		columnNames.add("...");
		data = new Vector<String>();
		data.add("");
		data.add("");
		data.add("...");
		this.addTableModelListener(this);
	}

	public void updateData(Description d) {
		makeData(d);
	}

	/**
	 * @param description
	 * @param t
	 * @return
	 */
	private Vector<String> makeData(Description description) {
		Vector<String> tv = new Vector<String>();
		TreeMap<String, String> tempData = new TreeMap();
		tempData.putAll(description.getDataAsTreeMap());
		for (Entry<String, String> es : tempData.entrySet()) {
			tv.add(es.getKey());
			tv.add(es.getValue());
			tv.add("...");
		}
		tv.add("");
		tv.add("");
		tv.add("...");
		return tv;
	}

	public TreeMap<String, String> getData() {
		TreeMap<String, String> tempData = new TreeMap<String, String>();
		for (int i = 0; i < getRowCount(); i++) {
			if ((getValueAt(i, 1) == null) || (getValueAt(i, 1).equals(""))) {
				//
			} else {
				tempData.put(getValueAt(i, 0), getValueAt(i, 1));
			}
		}
		return tempData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columnNames.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		if (data == null) {
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
	public String getValueAt(int row, int col) {
		return data.elementAt((row * getColumnCount()) + col);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.swing.event.TableModelListener#tableChanged(javax.swing.event.
	 * TableModelEvent)
	 */

	/**
	 * 
	 */
	public void addEmptyRow() {
		data.add(new String(""));
		data.add(new String(""));
		data.add(new String("..."));
		int size = getRowCount();
		fireTableRowsInserted(size - 1, size - 1);
	}

	public void addRow(String key, String value, boolean caseSensitive) {
		HashMap<String, String> temp = getDataAsHashMap();
		if (temp.containsKey(key)) {
			if (value != null) {
				temp.put(key, value);
			}
		} else {
			temp.put(key, (value == null ? "" : value));
		}
		data.clear();
		for (String k : temp.keySet()) {
			if (k.equals("") == false) {

				data.add(k);
				data.add(temp.get(k));
				data.add("...");
			}
		}

		int size = getRowCount();
		fireTableRowsInserted(size - 1, size - 1);
	}

	public void removeRow(int row) {
		data.remove(row * 2);
		data.remove(row * 2);
		data.remove(row * 2);
		int size = getRowCount();
		fireTableRowsDeleted(row, row);
	}

	@Override
	public String getColumnName(int col) {
		return columnNames.get(col);
	}

	/**
	 * gets the description-keys (first column) as a vector
	 * 
	 * @return
	 */
	public Vector<String> getKeys() {
		Vector<String> keys = new Vector<String>();
		int count = 0;
		for (String key : data) {
			if (count % 3 == 0) {
				keys.add(key);

			}
			count++;
		}
		return keys;
	}

	public HashMap<String, String> getDataAsHashMap() {
		HashMap<String, String> dataHM = new HashMap<String, String>();
		for (int i = 0; i < data.size(); i = i + 3) {
			dataHM.put(data.get(i), data.get(i + 1));
		}
		return dataHM;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {

		data.setElementAt((String) value, (row * getColumnCount()) + col);
		fireTableCellUpdated(row, col);
	}

	@Override
	public Class<String> getColumnClass(int c) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		if (lockedKeys.contains(getValueAt(row, 0))) {
			return false;
		} else if (getValueAt(row, 0).startsWith("#")) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.swing.event.TableModelListener#tableChanged(javax.swing.event.
	 * TableModelEvent)
	 */
	public void tableChanged(TableModelEvent arg0) {
		if (getValueAt(getRowCount() - 1, 0).length() > 0
				&& getValueAt(getRowCount() - 1, 1).length() > 0) {
			addEmptyRow();
		}
		if (directChanges) {
			description.setContent(getData());
		}
		// TODO Auto-generated method stub

	}

	/**
	 * @param largeKey
	 * @param text
	 */
	public void setValueForKey(String largeKey, String text) {
		for (int i = 0; i < data.size(); i = i + 2) {
			if (data.get(i).equals(largeKey)) {
				data.set(i + 1, text);
				break;
			}
		}
	}

	/**
	 * @param description
	 */
	public void setDescription(Description d) {
		data = makeData(d);
		fireTableDataChanged();
	}

	/**
	 * fills the table with key/values from a template-description
	 * 
	 * @param td
	 *            the template-Description
	 * @param overwriteValues
	 *            overwrites values for keys that exist in the description
	 * @param overwritesEverything
	 *            deletes the old description-data and leaves only the template
	 */
	public void pushTemplate(Description td, boolean overwriteValues,
			boolean overwritesEverything) {
		TreeMap<String, String> tempData = new TreeMap();
		tempData.putAll(td.getDataAsTreeMap());
		if (overwritesEverything) {
			tempData.putAll(td.getDataAsTreeMap());

		} else {
			if (overwriteValues) {
				tempData.putAll(td.getDataAsTreeMap());
				tempData.putAll(getDataAsHashMap());
			} else {
				tempData.putAll(getDataAsHashMap());
				tempData.putAll(td.getDataAsTreeMap());
			}
		}
		data.clear();
		for (Entry<String, String> es : tempData.entrySet()) {
			data.add(es.getKey());
			data.add(es.getValue());
		}
		data.add("");
		data.add("");
		data.add("...");

		fireTableDataChanged();
	}

	/**
	 * @param templateElement
	 */
	public void setDataFromElement(Element de) {
		System.out.println(de);
		TreeMap<String, String> tm = new TreeMap<String, String>();
		Vector<String> tv = new Vector<String>();
		if (de.getChildren() != null) {
			for (Element e : (List<Element>) de.getChildren()) {
				tm.put(e.getAttributeValue("Name"), e.getText());
			}
			for (Entry<String, String> es : tm.entrySet()) {
				tv.add(es.getKey());
				tv.add(es.getValue());
				tv.add("...");
			}
		}
		tv.add("");
		tv.add("");
		tv.add("...");

		data = tv;
		fireTableDataChanged();

	}

	public void setLockedKeys(HashSet lockedKeys) {
		this.lockedKeys = lockedKeys;
	}

	public HashSet getLockedKeys() {
		return lockedKeys;
	}
}
