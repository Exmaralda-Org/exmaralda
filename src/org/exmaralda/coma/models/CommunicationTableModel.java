package org.exmaralda.coma.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

public class CommunicationTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 3231532725746262143L;

	private XPath columnXPath;

	// ??
	@Override
	public Class<?> getColumnClass(int c) {
		switch (c) {
		case 0:
			return Element.class;
		case 1:
			return String.class;
		case 2:
			return Boolean.class;
		case 3:
			return String.class;
		case 4:
			return String.class;
		default:
			return String.class;
		}
		// return getValueAt(0, c).getClass();

	}

	public CommunicationTableModel() {
		super(new String[] { "Element", "Id", "S", "Name", "Var" }, 0);
		try {
			columnXPath = XPath
					.newInstance("Description/Key[@Name='undefined']");
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}

	public String getXPath() {
		return columnXPath.getXPath();
	}

	/**
	 * adds a row from a JDOM-Element
	 * 
	 * @param elm
	 */
	public Vector addRow(org.jdom.Element elm) {
		Vector tmp = element2rowVector(elm);
		addRow(tmp);
		return tmp;
	}

	/**
	 * make a row-vector from an element
	 * 
	 * @param elm
	 * @return the vector
	 */
	private Vector element2rowVector(Element elm) {
		String strOut = "?";
		try {
			List results = columnXPath.selectNodes(elm);
			if (results.size() > 0) {
				strOut = "";
				for (Object e : results) {
					if (e.getClass().equals(Element.class)) {
						strOut += ((Element) e).getText() + "; ";
					} else if (e.getClass().equals(Attribute.class)) {
						strOut += ((Attribute) e).getValue() + "; ";
					}
				}
				if (strOut.length() > 2)
					strOut = strOut.substring(0, strOut.length() - 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Vector tmp = new Vector();
		tmp.add(elm);
		tmp.add(elm.getAttributeValue("Id"));
		tmp.add(new Boolean(false));
		tmp.add(elm.getAttributeValue("Name"));
		tmp.add(strOut);
		return tmp;
	}

	public void changeRow(Element elm, int row) {
		dataVector.set(row, element2rowVector(elm));
	}

	/**
	 * Sets the xpath for what to display in the 2nd column
	 * 
	 * @param xpath
	 * @return
	 */
	public boolean setSecondColumn(String xpath) {
		xpath = xpath.substring(xpath.indexOf("Communication") + 14);
		try {
			columnXPath = XPath.newInstance(xpath);
			return true;
		} catch (JDOMException e) {
			e.printStackTrace();
			return false;
		}
		// secondColumn = text;
	}

	/**
	 * returns the postion of a newly created datavector
	 * 
	 * @param v
	 */
	public int getRow(Vector v) {
		return dataVector.indexOf(v);
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 4) {
			List<Element> results;
			try {
				results = columnXPath
						.selectNodes((Element) ((Vector) dataVector.get(row))
								.get(0));
				if ((!(columnXPath.getXPath().startsWith("Location")))
						&& (columnXPath.getXPath().contains("Description"))
						&& (!columnXPath.getXPath().contains("# "))) {
					return true;
				}
				// }
			} catch (JDOMException err) {
				err.printStackTrace();
			}
		}
		return false;
	}

	public void selectCommsWithRoles(Set<String> ids) {
		for (Vector row : (Vector<Vector>) dataVector) {
			row.set(2, false);
			if (ids.contains(row.get(1))) {
				row.set(2,true);
			}
		}
		fireTableDataChanged();

	}

	public void selectComms(Set<String> ids) {
		for (Vector row : (Vector<Vector>) dataVector) {
			row.set(2, false);
			Element comm = (Element) row.get(0);
			if (comm.getChild("Setting") != null) {
				if (comm.getChild("Setting").getChildren("Person").size() > 0) {
					for (Element pChild : (List<Element>) comm.getChild(
							"Setting").getChildren("Person")) {
						if (ids.contains(pChild.getText())) {
							row.set(2, true);
						}
					}
				}
			}
		}
		fireTableDataChanged();

	}

	public void selectCommsbyElements(HashSet<Element> elements) {
		for (Vector row : (Vector<Vector>) dataVector) {
			row.set(2, false);
			Element comm = (Element) row.get(0);
			if (elements.contains(comm)) {
				row.set(2, true);

			}

		}
		fireTableDataChanged();

	}

}
