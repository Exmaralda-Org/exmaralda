/**
 * 
 */
package org.exmaralda.coma.datatypes;

import org.apache.commons.lang.StringEscapeUtils;
import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.root.Coma;
import org.jdom.Element;

/** @author woerner */
public class Key extends ComaDatatype implements Comparable {
	String name; // attribute
	String value;
	String language;

	/** @param e
	 * @param c */
	public Key(Element e, Coma c) {
		super(e, c);
		datatype = "Key";
		name = el.getAttributeValue("Name");
		language = el.getAttributeValue("lang");
		value = el.getText();
	}

	public String getValue() {
		return value;
	}

	public String getLanguage() {
		return language;
	}

	public String toHTML(boolean withHeader, boolean oddEven) {
		if (coma.prefs.getBoolean("menu.viewMenu.showMachineTags", false) == false) {
			if (name.startsWith("# ")) {
				return "";
			}
		}
		String html = "<tr class='key" + (oddEven ? "odd" : "even") + "'>";
		html += "<td>";
		if (name.startsWith("# ")) {
			html += getColumnHTML(machineTag(name));
		} else {
			html += getColumnHTML(name);
		}
		html += "</td>";
		html += "<td>";
		if (coma.getData().getHiddenKeys().containsKey(name)) {
			if (coma.getData().getHiddenKeys().get(name).equals(value)) {
				if (!name.startsWith("# ")) {
					html += getFilterHTML("X");
				} else {
					html += "<i>" + getFilterHTML("X") + "</i>";
				}
			} else {
				return "";

			}

		} else {
			if (!name.startsWith("# ")) {
				html += getFilterHTML(value);
			} else {
				html += "<i>" + getFilterHTML(value) + "</i>";
			}
		} // html += ComaHTML.hilightSearchResult(value, coma.getData()
			// .getSearchTerm());
		html += "</td>";
		html += "</tr>";
		return html;
	}

	private String machineTag(String name2) {
		return "<i>" + name2.substring(2) + "</i>";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */

	@Override
	public String getFilterXPath() {
		return getXPath() + "[@Name=\'" + name + "\' and text()=\'"
				+ StringEscapeUtils.escapeXml(value) + "\']";

	}

	@Override
	public String getColumnHTML() {
		String columnHTML = "<a href=\"#\" id=\"column:" + getXPath()
				+ "[@Name=\'" + name + "\']\">";
		columnHTML += "<image valign='middle' src=\""
				+ ComaHTML.getImagePath("2ndcol.gif")
				+ "\" border=\"0\" alt=\"delete\"/></a>";
		return columnHTML;
	}

	public String getColumnHTML(String display) {
		String columnHTML = "<a href=\"#\" id=\"column:" + getXPath()
				+ "[@Name=\'" + name + "\']\" title=\"2nd column\">";
		columnHTML += ComaHTML.hilightSearchResult(display, coma.getData()
				.getSearchTerm())
				+ "</a>";
		return columnHTML;
	}

	@Override
	public String toString() {
		return name + "(" + language + ")" + "=" + value;
	}

	public int compareTo(java.lang.Object arg0) {
		return name.compareTo(arg0.toString());
	}

	public String getFilterHTML() {
		String filterXPath = getXPath() + "";

		String filterHTML = "<a href=\"#\" id=\"filter:" + getFilterXPath()
				+ "\">";
		filterHTML += "<image src=\"" + ComaHTML.getImagePath("filter.gif")
				+ "\" border=\"0\" alt=\"delete\"/>";
		// editableHTML+=" [EDIT] </a>";
		filterHTML += "</a>";
		return filterHTML;
	}

	public String getFilterHTML(String display) {
		String filterXPath = getXPath() + "";

		String filterHTML = "<a href=\"#\" id=\"filter:" + getFilterXPath()
				+ "\" title=\"use as filter\">";
		filterHTML += ComaHTML.hilightSearchResult(display, coma.getData()
				.getSearchTerm());
		filterHTML += "</a>";
		return filterHTML;
	}

	/** @return */
	public String getName() {
		return name;
	}

	public String toCSV() {
		return name + "\t" + value + "\n";
	}

}
