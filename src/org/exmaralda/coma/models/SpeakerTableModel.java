package org.exmaralda.coma.models;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

public class SpeakerTableModel extends DefaultTableModel {

	private boolean assignmentEnabled = false;

	private static final long serialVersionUID = 3231532725746262143L;

	private XPath columnXPath;

	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 4) {
			List<Element> results;
			try {
				results = columnXPath
						.selectNodes((Element) ((Vector) dataVector.get(row))
								.get(0));
				// if (results.size() > 0) {
				if ((!(columnXPath.getXPath().startsWith("Location")))
						&& (columnXPath.getXPath().contains("Description"))) {
					return true;
				}
				// }
			} catch (JDOMException err) {
				err.printStackTrace();
			}
		}
		return false;
	}

	public String getXPath() {
		return columnXPath.getXPath();
	}

	private XPath distinctionXPath;

	public SpeakerTableModel(String distinction) {
		super(new String[]{"Element", "Id", "S", "Sigle", "Var"}, 0);
		try {
			columnXPath = XPath.newInstance("Pseudo");
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		try {
			distinction = distinction.substring(9);
			distinctionXPath = XPath.newInstance(distinction);
		} catch (Exception e) {
			try {
				distinctionXPath = XPath.newInstance("Sigle");
			} catch (JDOMException err) {
				err.printStackTrace();
			}
		}
	}

	@Override
	public Class<?> getColumnClass(int c) {
		if (c == 0) {
			return Element.class;
		} else if (c == 2) {
			return Boolean.class;
		} else {
			return String.class;
		}
	}

	public int getRow(Vector v) {
		return dataVector.indexOf(v);
	}

	public void changeRow(Element elm, int row) {
		dataVector.set(row, element2rowVector(elm));
	}

	private Vector element2rowVector(Element elm) {

		String strOut = "?";
		boolean add = false;
		try {
			List<Object> results = columnXPath.selectNodes(elm);
			if (results.size() > 0) {
				strOut = "";
				for (Object e : results) {
					if (e.getClass().equals(Attribute.class)) {
						strOut += ((Attribute) e).getValue() + " ;";
					} else if (e.getClass().equals(Element.class)) {
						strOut += ((Element) e).getText() + " ;";
					}
				}
				strOut = strOut.substring(0, strOut.length() - 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String c1 = "?";
		List<Element> res2;
		try {
			res2 = distinctionXPath.selectNodes(elm);
			if (res2.size() > 0) {
				c1 = res2.get(0).getText();
			}
		} catch (JDOMException err) {
			err.printStackTrace();
		}

		Vector<Object> tmp = new Vector<Object>();
		tmp.add(elm); // the element
		tmp.add(elm.getAttributeValue("Id"));
		tmp.add(new Boolean(false)); // for communication-linking
		tmp.add(c1); // the sigle (on display)
		tmp.add(strOut); // display for the 2nd column
		return tmp;
	}

	/**
	 * adds a row from a JDOM-Element
	 * 
	 * @param elm
	 * @return
	 */
	public Vector addRow(org.jdom.Element elm) {
		Vector tmp = element2rowVector(elm);
		addRow(tmp);
		return tmp;
	}

	// set selection for speakers from a Vector of ids
	public void selectSpeakers(HashSet<String> ids) {
		for (Object v : dataVector) {
			Vector tV = (Vector) v;
			tV.set(2, (ids.contains(tV.get(1))));
		}
		fireTableDataChanged();
	}

	/**
	 * Sets the selection from a HashMap of Elements
	 * 
	 * @param elements
	 * @return
	 */
	public void selectSpeakersbyElements(HashSet<Element> elements) {
		for (Vector row : (Vector<Vector>) dataVector) {
			row.set(2, false);
			Element comm = (Element) row.get(0);
			if (elements.contains(comm)) {
				row.set(2, true);

			}

		}
		fireTableDataChanged();

	}

	/**
	 * Sets the xpath for what to display in the 2nd column
	 * 
	 * @param xpath
	 * @return
	 */
	public boolean setSecondColumn(String xpath) {
		xpath = xpath.substring(xpath.lastIndexOf("Speaker") + 8);
		try {
			columnXPath = XPath.newInstance(xpath);
			return true;
		} catch (JDOMException e) {
			e.printStackTrace();
			return false;
		}
	}

}
