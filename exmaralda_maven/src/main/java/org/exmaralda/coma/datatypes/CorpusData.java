package org.exmaralda.coma.datatypes;

import java.util.Vector;

import org.exmaralda.coma.root.Coma;
import org.jdom.Element;

public class CorpusData {
	private Element jdomElement;

	private String id;
	
	Coma coma;

	private Description description;
	
	private Vector<Communication> communication;
	
	private Vector<Speaker> speaker;
	
	private Vector<CorpusData> corpus;
	
	private boolean hasData;
	
	private String editableId;
/**
	public CorpusData(Element commElmt, Coma c) {
		if (commElmt == null) {
			//debug
		}
		jdomElement = commElmt;
		coma = c;
		editableId = coma.addEditableItem(commElmt);
		location = null;
		recording = new Vector<Recording>();
		language = new Vector<Language>();
		id = commElmt.getAttributeValue("Id");
		name = commElmt.getAttributeValue("Name");
		description = new Description(commElmt.getChild("Description"), coma);
		List recs = commElmt.getChildren("Recording");
		Iterator i = recs.iterator();
		while (i.hasNext()) {
			recording.add(new Recording((Element) i.next(), coma));
		}
		Element locs = commElmt.getChild("Location");
		if (locs != null)
			location = new Location(locs, coma);

		setting = new Setting(commElmt.getChild("Setting"), c);

		List langs = commElmt.getChildren("Language");
		i = langs.iterator();
		while (i.hasNext()) {
			language.add(new Language((Element) i.next(), coma));
		}

	}

	public Element getElement() {
		return jdomElement;
	}
	
	public Element toElement() {
		Element e = new Element("Communication").setAttribute("Name", name);
		e.setAttribute("Id", id);
		e.addContent(description.toElement());
		e.addContent(setting.toElement());
		for (Recording r : recording) {
			e.addContent(r.toElement());
		}
		//		e.addContent()
		return e;
	}

	public String toHTML(boolean onlyRows, boolean withSpeakers) {
		String html = "";
		html += "<tr><td>Name</td><td>" + name + "</td></tr>";
		html += "<tr><td>Id</td><td>" + id + "</td></tr>";
		html += description.toHTML(true, true);
		if (location != null)
			html += location.toHTML(true);

		if (language.size() > 0) {
			html += "<tr><td class='newblock' colspan='2'>Language(s)</td></tr>";
		}
		for (Language l : language) {
			html += l.toHTML(true);
		}

		html += (setting != null) ? setting.toHTML(true, false) : "";
		// treat speakers seperately
		if (withSpeakers) {
			for (Speaker s : setting.getSpeakers()) {
				html += (location != null) ? s.toHTML(location.getPeriod(),
						true) : s.toHTML(true);

			}
		}
		html = (!onlyRows) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;

	}**/
}
