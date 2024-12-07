package org.exmaralda.coma.helpers;

import java.util.prefs.Preferences;

import org.apache.commons.lang.StringEscapeUtils;
import org.exmaralda.coma.datatypes.Language;
import org.exmaralda.coma.resources.ResourceHandler;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class ComaHTML {
	private static Preferences prefs = Ui.prefs;

	private String styleString = "<style>cite.attr {font-size: 8px; color: #666633; font-style: normal}</style>";

	public static final String ENTHOLOGUE_LOOKUP = "http://www.ethnologue.com/show_language.asp?code=";

	public static final String ENTHOLOGUE_INDEX = "http://www.ethnologue.com/family_index.asp";

	public static final String DATAROW1_COLOR = "#CCCCCC";

	public static final String DATAROW2_COLOR = "#EEEEEE";

	public static final String DATAROW3_COLOR = "#DDDDDD";

	public static final String SEPARATOR = "<tr><td colspan=2 style='background-color:#000000; font-size: 3px;'>&nbsp;</td></tr>";

	public static final String TABLE_SEPARATOR = "<table><tr><td style='background-color:#000000; font-size: 3px;'>&nbsp;</td></tr></table>";

	public static String htmlBracket(String html) {
		String bracket = "<html>" + styleString() + "<body>" + html
				+ "</body></html>";
		return bracket;

	}

	public static String tableWithStylesStart(String html) {
		String tableHTML = "<html>" + styleString() + "<body>";
		tableHTML += "<table>" + html + "</table></body></html>";
		return tableHTML;
	}

	public static String tableWithStylesStart(String html, String styleClass,
			String imagename) {
		String tableHTML = "<html>" + styleString() + "<body>";
		tableHTML += "<table><tr><td class=''" + styleClass + "'><img src=\""
				+ getImagePath(imagename) + "\"/></td><td width='100%'>";
		tableHTML += "<table>" + html + "</table>";
		tableHTML += "</td></tr></table></body></html>";
		return tableHTML;
	}

	public static String completeTable(String html, String styleClass,
			String imagename) {
		String tableHTML = "<table><tr><td class='" + styleClass
				+ "' valign='top'><img src=\"" + getImagePath(imagename)
				+ "\"/></td><td width='100%'>";
		tableHTML += "<table>" + html + "</table>";
		tableHTML += "</td></tr></table>";
		return tableHTML;
	}

	private static final String styleString() {
		String style = "<head>";
		try {
			style += StreamToString.convertStreamToString(new ResourceHandler()
					.printStylesheetStream());
			style += StreamToString.convertStreamToString(new ResourceHandler()
					.screenStylesheetStream(prefs.get(
							"menu.viewMenu.colorMode", "colored")));

			;
			style += "</head>";
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("html will be css-less because:"
					+ e.getLocalizedMessage());
		}
		return style;
	}

	public static String lookupLangcodeURL(String languageCode) {
		return ENTHOLOGUE_LOOKUP + languageCode.substring(0, 3);
	}

	public static String languageLink(Language language) {
		String ll = "<a id=\"href\" href=\""
				+ ENTHOLOGUE_LOOKUP
				+ (language.getLanguageCode() + "   ").toLowerCase().substring(
						0, 3) + "\">(O)</a>";
		return ll;
	}

	public static String getImagePath(String imageName) {
		return new ResourceHandler().image(imageName).toString();
	}

	public static String dumpXML(Element e) {
		Format format = Format.getRawFormat();
		XMLOutputter outputter = new XMLOutputter(format);
		String html = "";
		html += outputter.outputString(e);
		return html;
	}

	public static String hilightSearchResult(String in, String search) {
		if (search != null) {
			if (search.length() > 0) {
				return StringEscapeUtils.escapeHtml(in).replace(
						search,
						"<span style='background-color:#FF9999';>" + search
								+ "</span>");
			}
		}
		return in;
	}

}
