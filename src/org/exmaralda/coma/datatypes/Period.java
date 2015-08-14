package org.exmaralda.coma.datatypes;

import java.util.Date;

import org.exmaralda.coma.actions.PrefsAction;
import org.exmaralda.coma.helpers.ComaDateTime;
import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.DurationHelper;
import org.jdom.Content;
import org.jdom.Element;

public class Period extends ComaDatatype {

	private Date periodStart;

	private long periodDuration; // in milliseconds

	public Period(Element p, Coma c) {
		super(p, c);
		if (p.getChild("PeriodStart") != null) {
			periodStart = ComaDateTime.dateFromXSDateTime(p
					.getChildText("PeriodStart"));
		} else {
			periodStart = null;
		}

		if (p.getChild("PeriodDuration") != null) {
			try {
				periodDuration = new Long(p.getChildText("PeriodDuration"));
			} catch (NumberFormatException ex) {
				periodDuration = 0;
			}
		}

	}

	public Content toElement() {
		Element p = new Element("Period");
		p.addContent(new Element("PeriodStart").setText(ComaDateTime
				.xsDateTimeFromDate(periodStart)));
		p.addContent(new Element("PeriodDuration").setText(new Long(
				periodDuration).toString()));

		return p;
	}

	public Date getPeriodStart() {
		return periodStart;
	}

	public long getPeriodDuration() {
		return periodDuration;
	}

	public String toHTML(boolean onlyRows, Period commDate) {
		String html = "";
		html += (periodStart != null) ? "<tr><td>"
				+ getColumnHTML("PeriodStart")
				+ "</td>"
				+ "<td>"
				+ getFilterHTML("PeriodStart", ""
						+ ComaDateTime.xsDateTimeFromDate(periodStart),
						periodStart.toLocaleString()) + "</td></tr>" : "";
		if ((periodStart != null) && (commDate != null)) {
			html += "<tr><td>" + Ui.getText("timespan")
					+ "</td><td><image valign='middle' src=\""
					+ ComaHTML.getImagePath("chat_small.png")
					+ "\" border=\"0\" alt=\"delete\"/>";
			// todo!
			html += "" + ComaDateTime.getTimeSpanString(this, commDate, 2);
			html += "</td></tr>";
		}

		if (periodDuration > 0) {
			html += "<tr><td>"
					+ getColumnHTML("PeriodDuration")
					+ "</td><td>"
					+ getFilterHTML("PeriodDuration", "" + periodDuration,
							DurationHelper.getHTMLString(periodDuration, Ui.prefs.getBoolean("menu.viewMenu.showMillis", false)))
					+ "</td></tr>";
		}
		html = (!onlyRows) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;

	}

	@Override
	public String toHTML(boolean onlyRows) {
		return toHTML(onlyRows, null);
	}

	@Override
	public String getColumnHTML(String field) {
		String columnHTML = "<a href=\"#\" id=\"column:" + getXPath() + "/"
				+ field + "\">";
		columnHTML += field + "</a>";
		return columnHTML;
	}

	private String getFilterXPath(String n, String v) {
		return getXPath() + "[" + n + "=\'" + v + "\']";
	}

	private String getFilterHTML(String n, String v, String showString) {

		String filterHTML = "<a href=\"#\" id=\"filter:" + getFilterXPath(n, v)
				+ "\">";
		filterHTML += ComaHTML.hilightSearchResult(showString, coma.getData()
				.getSearchTerm());
		filterHTML += "</a>";
		return filterHTML;
	}

}
