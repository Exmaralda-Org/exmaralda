package org.exmaralda.coma.datatypes;

import java.net.URI;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Iterator;

import org.exmaralda.coma.helpers.ComaDateTime;
import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.root.Coma;
import org.jdom.Element;

public class AsocFile extends ComaDatatype {
	String id;

	Description description;

	String fileStore;

	String filename;

	String nSLink;

	Date lastBackup;

	public AsocFile(Element e, Coma c) {
		super(e, c);
		datatype = "Media";
		refresh();
	}

	@Override
	public void refresh() {
		if (el.getChild("Description") == null) {
			//
		} else {
			description = new Description(el.getChild("Description"), coma);
		}
		Iterator i = el.getChildren().iterator();
		while (i.hasNext()) {
			Element myElm = (Element) i.next();
			if (myElm.getName().equals("FileStore"))
				fileStore = myElm.getText();
			if (myElm.getName().equals("Filename"))
				filename = myElm.getText();
			if (myElm.getName().equals("NSLink"))
				nSLink = myElm.getText();
			if (myElm.getName().equals("LastBackup"))
				lastBackup = ComaDateTime.dateFromXSDateTime(myElm.getText());
		}

	}

	// private String getFilterHTML(String n, String v) {
	//
	// String filterHTML = "<a href=\"#\" id=\"filter:" + getFilterXPath(n, v)
	// + "\">";
	// filterHTML += "<image src=\"" + ComaHTML.getImagePath("filter.gif")
	// + "\" border=\"0\" alt=\"delete\"/>";
	// // editableHTML+=" [EDIT] </a>";
	// filterHTML += "</a>";
	// return filterHTML;
	// }

	private String getFilterXPath(String n, String v) {
		return getXPath() + "[" + n + "=\'" + v + "\']";
	}

	@Override
	public String toHTML(boolean onlyRows) {

		String html = "<tr><td colspan='2'>";
		if ((filename != null) && (nSLink != null)) {
			html += "File: <a href='"
					+ nSLink
					+ "'>"
					+ ComaHTML.hilightSearchResult(filename, coma.getData()
							.getSearchTerm()) + "</a>";
		} else if ((filename != null) && (nSLink == null)) {
			html += "File: "
					+ ComaHTML.hilightSearchResult(filename, coma.getData()
							.getSearchTerm());
		} else {
			try {
				html += "File: <a href='" + nSLink + "'>"
						+ URLDecoder.decode(new URI(nSLink).toString())
						+ "</a>";
			} catch (Exception e) {
				html += "File: " + nSLink + " (?)";
			}
		}
		html = (!onlyRows) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;
	}
}
