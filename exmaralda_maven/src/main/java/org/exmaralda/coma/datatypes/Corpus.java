package org.exmaralda.coma.datatypes;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.root.Coma;
import org.jdom.Element;

public class Corpus extends ComaDatatype {
	private Element jdomElement;

	private String id;
	
	Coma coma;
	
	private String name;

	private Vector<Communication> communication;
	
	private Vector<Speaker> speaker;
	
	private Vector<Corpus> corpus;
	
	private boolean hasData;
	
	
	//** elements

	Description description;

	Setting setting;

	Vector<Recording> recording;

	Vector<Transcription> transcription;

	Location location;

	Vector<Language> language;

	private String editableId;

	public Corpus(Element commElmt, Coma c) {
		super(commElmt,c);
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

	@Override
	public Element getElement() {
		return jdomElement;
	}
	

	public String toHTML(boolean onlyRows, boolean withSpeakers) {
		String html = "";
		html += "<tr><td>Name</td><td>" + name + "</td></tr>";
		html += "<tr><td>Id</td><td>" + id + "</td></tr>";
		html += description.toHTML(true);
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

	}
}
