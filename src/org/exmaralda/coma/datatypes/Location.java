package org.exmaralda.coma.datatypes;

import java.util.Iterator;

import org.exmaralda.coma.dialogs.EditLocationDialog;
import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.root.Coma;
import org.jdom.Element;

public class Location extends ComaDatatype implements Comparable<Location> {

	private String type; // attribute

	private String street;

	private String city;

	private String postalCode;

	private String country;

	private Period period;

	private Description description;

	public Location(Element l, Coma c) {
		super(l, c);
		datatype = "Location";
		refresh();

	}

	private String getFilterXPath(String n, String v) {
		return getXPath() + "[" + n + "=\'" + v + "\']";
	}

	// private String getLocalFilterHTML(String n, String v) {
	//
	// String filterHTML = "<a href=\"#\" id=\"filter:" + getFilterXPath(n, v)
	// + "\">";
	// filterHTML += "<image src=\"" + ComaHTML.getImagePath("filter.gif")
	// + "\" border=\"0\" alt=\"delete\"/>";
	// // editableHTML+=" [EDIT] </a>";
	// filterHTML += "</a>";
	// return filterHTML;
	// }

	private String getFilterHTML(String n, String v, String display) {

		String filterHTML = "<a href=\"#\" id=\"filter:" + getFilterXPath(n, v)
				+ "\">";
		filterHTML += ComaHTML.hilightSearchResult(display, coma.getData()
				.getSearchTerm());
		filterHTML += "</a>";
		return filterHTML;
	}

	@Override
	public String getColumnHTML(String field) {
		String columnHTML = "<a href=\"#\" id=\"column:" + getXPath() + "/"
				+ field + "\">";
		columnHTML += "<image valign='middle' src=\""
				+ ComaHTML.getImagePath("2ndcol.gif")
				+ "\" border=\"0\" alt=\"delete\"/></a>";
		return columnHTML;
	}

	public String getColumnHTML(String field, String display) {
		String columnHTML = "<a href=\"#\" id=\"column:" + getXPath() + "/"
				+ field + "\">";
		columnHTML += ComaHTML.hilightSearchResult(display, coma.getData()
				.getSearchTerm());
		return columnHTML;
	}

	@Override
	public void refresh() {
		type = el.getAttributeValue("Type");
		if (el.getChild("Description") == null) {
			//
		} else {
			description = new Description(el.getChild("Description"), coma);
		}
		Iterator i = el.getChildren().iterator();
		while (i.hasNext()) {
			Element myElm = (Element) i.next();
			if (myElm.getName().equals("Period")) {
				period = new Period(myElm, coma);
			} else if (myElm.getName().equals("Description")) {
				description = new Description(myElm, coma);
			} else {
				if (myElm.getName() == "Street")
					street = myElm.getText();
				if (myElm.getName() == "City")
					city = myElm.getText();
				if (myElm.getName() == "PostalCode")
					postalCode = myElm.getText();
				if (myElm.getName() == "Country")
					country = myElm.getText();
			}
		}

	}

	public String toHTML(boolean onlyRows, Period commDate) {

		String html = "";
		if (type != null) {
			html += "<tr><td class='location' colspan='2'>"
					+ getColumnHTML("@Type")
					+ getFilterHTML("@Type", type, type) + " (Location)"
					+ getEditHTML() + getDeleteHTML() + " </td></tr>";
		} else {
			html += "<tr><td class='location' colspan='2'>Location"
					+ getEditHTML() + getDeleteHTML() + "</td></tr>";
		}
		html += (street != null) ? "<tr><td>"
				+ getColumnHTML("Street", "Street") + "</td><td>"
				+ getFilterHTML("Street", street, street) + "</td></tr>" : "";
		html += (city != null) ? "<tr><td>" + getColumnHTML("City", "City")
				+ "</td><td>" + getFilterHTML("City", city, city)
				+ "</td></tr>" : "";
		html += (postalCode != null) ? "<tr><td>"
				+ getColumnHTML("PostalCode", "PostalCode") + "</td><td>"
				+ getFilterHTML("PostalCode", postalCode, postalCode)
				+ "</td></tr>" : "";
		html += (country != null) ? "<tr><td>"
				+ getColumnHTML("Country", "Country") + "</td><td>"
				+ getFilterHTML("Country", country, country) + "</td></tr>"
				: "";

		html += (period != null) ? period.toHTML(true, commDate) : "";
		if (description == null) {
			html += "<tr><td class='newblock' colspan='2'>Description (Location)"
					+ getAddHTML("Description") + "</td></tr>";
		} else {
			html += description.toHTML(true);
		}

		html = (!onlyRows) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;

	}

	@Override
	public String toHTML(boolean onlyRows) {
		return toHTML(onlyRows, null);
	}

	public Period specialDate(String key, String value) {
		if (description.hasPair(key, value)) {
			return period;
		}
		return null;
	}

	public static void create(Coma c, ComaDatatype parent) {
		Element l = new Element("Location");
		parent.getElement().addContent(l);
		parent.refresh();
		EditLocationDialog eld = new EditLocationDialog(c, l, true);
	}

	public Period getPeriod() {
		return period;
	}

	@Override
	public void edit() {
		EditLocationDialog edl = new EditLocationDialog(coma, el, false);

	}

	@Override
	public void add(String whichType) {
		if (whichType.equals("Description")) {
			Description.create(coma, this);
		}
	}

	@Override
	public int compareTo(Location o) {
		try {
			return (this.period.getPeriodStart().compareTo(o.period
					.getPeriodStart()));
		} catch (Exception e) {
			return 0;
		}
		// if (this.type != null && o.type != null) {
		// return this.type.compareTo(o.type);
		// } else {
		// return 0;
		// }
	}


}
