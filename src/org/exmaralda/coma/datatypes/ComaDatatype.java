/**
 *
 */
package org.exmaralda.coma.datatypes;

import javax.swing.JOptionPane;

import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.root.Coma;
import org.jdom.Element;

/**
 * @author woerner
 * 
 */
public abstract class ComaDatatype {

	String datatype = "generic";

	String editableId;

	String xPath;

	Coma coma;

	Element el;

	public ComaDatatype(Element e, Coma c) {
		coma = c;
		el = e;
		editableId = coma.addEditableItem(this);
	}

	public void refresh() {
		// implement where necessary
	}

	public String toHTML(boolean withHeader) {
		String html = "";
		html = (withHeader) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;
	}

	public String toHTML(boolean withHeader, int displayMode) {
		String html = "";
		html = (withHeader) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;
	}

	public String getEditHTML() {
		String editableHTML = "<a href=\"#\" id=\"edit:" + editableId + "\">";
		editableHTML += "<image src=\""
				+ ComaHTML.getImagePath("accessories-text-editor-16.png")
				+ "\" border=\"0\" alt=\"edit\"/>";
		// editableHTML+=" [EDIT] </a>";
		editableHTML += "</a>";

		return editableHTML;

	}

	public String getAddHTML(String dt) {
		String addHTML = "<a href=\"#\" id=\"add." + dt + ":" + editableId
				+ "\">";
		addHTML += "<image src=\"" + ComaHTML.getImagePath("list-add-16.png")
				+ "\" border=\"0\" alt=\"edit\"/>";
		addHTML += "</a>";
		return addHTML;

	}

	public String getDeleteHTML() {
		String deleteHTML = "<a href=\"#\" id=\"dele:" + editableId + "\">";
		deleteHTML += "<image src=\""
				+ ComaHTML.getImagePath("list-remove-16.png")
				+ "\" border=\"0\" alt=\"delete\"/>";
		// editableHTML+=" [EDIT] </a>";
		deleteHTML += "</a>";

		return deleteHTML;

	}

	public String getColumnHTML() {
		return getColumnHTML(null);

	}

	public String getColumnHTML(String fromElement) {
		String filterHTML = "<a href=\"#\" id=\"column:"
				+ getXPath(fromElement) + "\">";
		filterHTML += "<image src=\"" + ComaHTML.getImagePath("2ndcol.gif")
				+ "\" border=\"0\" alt=\"2. column\"/>";
		// editableHTML+=" [EDIT] </a>";
		filterHTML += "</a>";
		return filterHTML;
	};

	public String getFilterHTML(String xPath) {
		String filterXPath = getXPath() + xPath;
		String filterHTML = "<a href=\"#\" id=\"filter:" + filterXPath + "\">";
		filterHTML += "<image src=\"" + ComaHTML.getImagePath("filter.gif")
				+ "\" border=\"0\" alt=\"delete\"/>";
		// editableHTML+=" [EDIT] </a>";
		filterHTML += "</a>";
		return filterHTML;
	}
	
	

	/*
	 * same as getFilterHTML, but with a string instead of the filter-icon.
	 */
	public String getFilterHTML(String xPath, String dispString) {
		String filterXPath = getXPath() + xPath;
		String filterHTML = "<a href=\"#\" id=\"filter:" + filterXPath + "\">"
				+ dispString + "</a>";
		return filterHTML;
	}

	public Element getElement() {
		return el;
	}

	public String getXPath() {
		xPath = "";
		Element pEl = el;
		while (pEl != null) {
			xPath = "/" + pEl.getName() + xPath;
			pEl = pEl.getParentElement();
		}
		return "/" + xPath;
	}

	public Coma getComa() {
		return coma;
	}

	public String getFilterXPath() {
		// implement elsewhere!
		return null;
	}

	public String getXPath(String fromElement) {
		coma.status("____" + fromElement);
		if (fromElement == null) {
			return getXPath();
		} else {
			xPath = "";
			Element pEl = el;
			coma.status(fromElement + "=" + pEl.getName() + "?");
			while (!pEl.getName().equals(fromElement)) {
				// coma.status(fromElement + "=" + pEl.getName() + "?");
				xPath = "/" + pEl.getName() + xPath;
				pEl = pEl.getParentElement();
			}
			// coma.status(xPath);
			return xPath;
		}
	}

	/**
	 * 
	 */
	public void edit() {
		// To be implemented inside the subclasses
	}

	public void delete() {
		if (Coma.prefs.getBoolean("deleteConfirm", true)) {
			int n = JOptionPane.showConfirmDialog(coma, "Delete " + datatype,
					"Confirm", JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				el.getParent().removeContent(el);
			}
		} else {
			el.getParent().removeContent(el);
		}
		coma.xmlChanged();
		coma.updateValueDisplay();
	}

	public String getDatatype() {
		return datatype;
	}

	/**
	 * @param string
	 */
	public void add(String string) {
		// to be implemented in datatype-subclasses
	}

	public void showTargets() {
		// to be implemented in datatype-subclasses (speaker)
	}

	public String getRoleCount() {
		return null;
		// to be implemented in datatype-subclasses (speaker)
	}
}
