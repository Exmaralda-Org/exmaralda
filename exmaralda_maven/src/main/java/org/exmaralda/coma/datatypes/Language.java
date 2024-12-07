package org.exmaralda.coma.datatypes;

import org.exmaralda.coma.dialogs.EditLanguageDialog;
import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.helpers.ISOLanguageCodeHelper;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

public class Language extends ComaDatatype {
	/*
	 * <xs:complexType name="LanguageType"> <xs:annotation>
	 * <xs:documentation>type for storing languages</xs:documentation>
	 * </xs:annotation> <xs:all> <xs:element name="LanguageCode"
	 * type="xs:string"/> <xs:element name="Description" type="DescriptionType"
	 * minOccurs="0"/> </xs:all> </xs:complexType>
	 */
	private String languageCode;

	private String type;

	private Description description;

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public Language(Element l, Coma c) {
		super(l, c);
		datatype = "Language";
		languageCode = el.getChildText("LanguageCode");
		type = el.getAttributeValue("Type");
		if (l.getChild("Description") == null) {
			el.addContent(new Element("Description"));
		}
		description = new Description(l.getChild("Description"), coma);
	}

	public Language(Coma c) {
		super(new Element("Language"), c);

	}

	@Override
	public String toHTML(boolean onlyRows) {
		String html = "<tr><td class='language' colspan='2'>";
		if (type != null) {
			html += getColumnHTML("@Type") + getFilterHTML("@Type", type, type)
					+ " (Language)";
		} else {
			html += "Language ";
		}
		html += getEditHTML() + "&nbsp;" + getDeleteHTML() + "</td></tr>";

		// + (type != null ? type : "Language ") + getEditHTML()
		// + "&nbsp;" + getDeleteHTML() + "</td></tr>";

		// if (type != null) {
		// html += "<tr><td class='location' colspan='2'>"
		// + getColumnHTML("@Type")
		// + getFilterHTML("@Type", type, type) + " (Location)"
		// + getEditHTML() + getDeleteHTML() + " </td></tr>";
		// } else {
		// html += "<tr><td class='location' colspan='2'>Location"
		// + getEditHTML() + getDeleteHTML() + "</td></tr>";
		// }

		html += (languageCode != null) ? "<tr><td>"
				+ getColumnHTML("/LanguageCode", "LanguageCode") + "</td><td>"
				+ getLanguageFilterHTML(languageCode)
				// + ComaHTML.languageLink(this)

				+ (Ui.prefs.getBoolean("menu.viewMenu.showLangNames", false) ? " ("
						+ ISOLanguageCodeHelper.getLanguageName(languageCode
								.toLowerCase()) + ")"
						: "") + "</td></tr>"
				: "";
		// html+=period.toHTML(true);

		html += (description != null) ? description.toHTML(true) : "";
		html = (!onlyRows) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;
	}

	@Override
	public void edit() {
		EditLanguageDialog edl = new EditLanguageDialog(coma, el, false);
	}

	public String getColumnHTML(String pathExtension, String display) {
		String cHTML = "<a href=\"#\" id=\"column:" + getXPath()
				+ pathExtension + "\">";
		cHTML += display;
		cHTML += "</a>";
		return cHTML;
	}

	public static void create(Coma c, ComaDatatype parent) {
		Element l = new Element("Language");
		l.addContent(new Element("LanguageCode").setText("???"));
		l.addContent(new Element("Description"));
		parent.getElement().addContent(l);
		parent.refresh();
		EditLanguageDialog eld = new EditLanguageDialog(c, l, true);
	}

	@Override
	public String toString() {

		return "Language: " + languageCode;
	}

	private String getLanguageFilterHTML(String display) {

		String filterHTML = "<a href=\"#\" id=\"filter:" + getFilterXPath()
				+ "\">";
		filterHTML += ComaHTML.hilightSearchResult(display, coma.getData()
				.getSearchTerm());
		filterHTML += "</a>";
		return filterHTML;
	}

	@Override
	public String getFilterXPath() {
		return getXPath() + "[LanguageCode=\'" + languageCode + "\']";
	}

	@Override
	public void add(String whichType) {
		if (whichType.equals("Description")) {
			Description.create(coma, this);
		}
	}

	private String getFilterHTML(String n, String v, String display) {

		String filterHTML = "<a href=\"#\" id=\"filter:" + getFilterXPath(n, v)
				+ "\">";
		filterHTML += ComaHTML.hilightSearchResult(display, coma.getData()
				.getSearchTerm());
		filterHTML += "</a>";
		return filterHTML;
	}

	private String getFilterXPath(String n, String v) {
		return getXPath() + "[" + n + "=\'" + v + "\']";
	}

	public String getColumnHTML(String field) {
		String columnHTML = "<a href=\"#\" id=\"column:" + getXPath() + "/"
				+ field + "\">";
		columnHTML += "<image valign='middle' src=\""
				+ ComaHTML.getImagePath("2ndcol.gif")
				+ "\" border=\"0\" alt=\"delete\"/></a>";
		return columnHTML;
	}

}
