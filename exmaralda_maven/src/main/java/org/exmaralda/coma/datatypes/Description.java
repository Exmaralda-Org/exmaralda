package org.exmaralda.coma.datatypes;

import java.awt.Component;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.JFrame;

import org.exmaralda.coma.dialogs.EditDescriptionDialog;
import org.exmaralda.coma.root.Coma;
import org.jdom.Element;

/**
 * @author woerner
 * 
 */
public class Description extends ComaDatatype {
	private TreeSet<Key> keys;

	private String parentType;

	public Description(Element d, Coma c) {
		super(d, c);
		keys = new TreeSet<Key>();
		parentType = el.getParentElement().getName();
		if (el.getChildren() != null) {
			Iterator i = el.getChildren().iterator();
			while (i.hasNext()) {
				keys.add(new Key((Element) i.next(), coma));
			}
		}
	}

	/**
	 * Returns the JDOM-Element-representation of the Description.
	 * 
	 * @return a JDOM-Element representation of the description
	 *         (DescriptionType)
	 */

	/**
	 * 
	 */
	public Description(Coma c) {
		super(new Element("Description"), c);
		keys = new TreeSet<Key>();
		editableId = coma.addEditableItem(this);
	}

	public Description(Coma c, ComaDatatype parent) {
		super(new Element("Description"), c);
		keys = new TreeSet<Key>();

		parent.el.addContent(el);
		parent.refresh();
		editableId = coma.addEditableItem(this);
	}

	/*************************************************************************
	 * public Element toElement() { Element e = new Element("Description"); for
	 * (Iterator i = key.entrySet().iterator(); i.hasNext();) { Element k = new
	 * Element("Key"); Map.Entry entry = (Map.Entry) i.next();
	 * k.setAttribute("Name", (String) entry.getKey()); k.setText((String)
	 * entry.getValue()); } return e; }
	 ************************************************************************/

	public Component descriptionComponent() {
		return new JFrame();
	}

	@Override
	public String toHTML(boolean rowsOnly) {
		String html = "<tr><td class='description' colspan='2'>Description ("
				+ parentType + ")" + getEditHTML() + "</td></tr>";
		if (keys.size() > 0) {
			boolean oddEven = true;
			for (Key k : keys) {
				oddEven = !oddEven;
				html += k.toHTML(false, oddEven);
			}
		} else {
			html += "";
		}

		if (!rowsOnly) {
			html = "<table width='400' border='1'>" + html + "</table>";
		}
		return html;
	}

	/**
	 * in case there is no description in the parent datatype
	 * 
	 * @param pt
	 * @return
	 */
	public static String toHTML(ComaDatatype pt) {
		String html = "<tr><td class='newblock' colspan='2'>Description ("
				+ pt.datatype + ")";
		return html;

	}

	public TreeSet<Key> getData() {
		return keys;
	}

	public TreeMap<String, String> getDataAsTreeMap() {
		TreeMap<String, String> temp = new TreeMap<String, String>();
		for (Key k : keys) {
			temp.put(k.getName(), k.getValue());
		}
		return temp;
	}

	@Override
	public void edit() {
		EditDescriptionDialog edd = new EditDescriptionDialog(coma, this);
		coma.updateValueDisplay();
	}

	/**
	 * @param data
	 */
	public void setContent(TreeMap<String, String> data) {
		el.removeContent();
		keys.clear();
		for (Entry<String, String> e : data.entrySet()) {
			Element te = new Element("Key").setText(e.getValue()).setAttribute(
					"Name", e.getKey());
			el.addContent(te);
			keys.add(new Key(te, coma));
		}
	}

	@Override
	public String toString() {
		return "Description (" + keys.size() + " Keys)";
	}

	/**
	 * @param coma
	 * @param language
	 */
	public static void create(Coma c, ComaDatatype parent) {
		Element ds = new Element("Description");
		parent.getElement().addContent(ds);
		parent.refresh();
		EditDescriptionDialog edd = new EditDescriptionDialog(c,
				new Description(ds, c));

	}

	@Deprecated
	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean hasPair(String key, String value) {
		return false;
	}

	public String toCSV() {
		String csv = "";
		if (keys.size() > 0) {
			for (Key k : keys) {
				csv += k.toCSV();
			}
		}
		return csv;
	}

}
