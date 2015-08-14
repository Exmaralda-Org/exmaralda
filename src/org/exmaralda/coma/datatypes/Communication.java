package org.exmaralda.coma.datatypes;

import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

public class Communication extends ComaDatatype {

	public static Preferences prefs = Ui.prefs;

	String id; // att

	String name; // att

	// ** elements

	Description description;

	Setting setting;

	Vector<Recording> recordings;

	Vector<Transcription> transcriptions;

	public Vector<Transcription> getTranscriptions() {
		return transcriptions;
	}

	public void setTranscriptions(Vector<Transcription> transcriptions) {
		this.transcriptions = transcriptions;
	}

	TreeSet<File> files;

	Location location;

	Vector<Language> languages;

	public Communication(Element e, Coma c) {
		super(e, c);
		datatype = "Communication";
		refresh();
	}

	@Override
	public void refresh() {
		location = null;
		recordings = new Vector<Recording>();
		languages = new Vector<Language>();
		transcriptions = new Vector<Transcription>();
		files = new TreeSet<File>();
		id = el.getAttributeValue("Id");
		name = el.getAttributeValue("Name");
		description = new Description(el.getChild("Description"), coma);
		List<Element> recs = el.getChildren("Recording");
		for (Element eli : recs) {
			recordings.add(new Recording(eli, coma));
		}
		Element locs = el.getChild("Location");
		if (locs != null)
			location = new Location(locs, coma);

		if (el.getChild("Setting") != null)
			setting = new Setting(el.getChild("Setting"), coma);

		List<Element> langs = el.getChildren("Language");
		for (Element eli : langs) {
			languages.add(new Language(eli, coma));
		}

		List<Element> fs = el.getChildren("File");
		for (Element f : fs) {
			files.add(new File(f, coma));
		}

		List<Element> trans = el.getChildren("Transcription");
		for (Element tr : trans) {
			transcriptions.add(new Transcription(tr, coma));
		}
	}

	public String toHTML(boolean onlyRows) {
		String html = "<tr><td colspan='2' class='communication'>Communication "
				+ name
				+ " "
				+ getEditHTML()
				+ "</td></tr>"
				+ description.toHTML(true);
		html += ComaHTML.SEPARATOR;

		if (location != null) {
			html += location.toHTML(true);
		} else {
			html += "<tr><td class='location' colspan='2'>No Location "
					+ getAddHTML("Location") + "</td></tr>";
		}
		html += ComaHTML.SEPARATOR;
		// if (language.size() > 0) {
		html += "<tr><td class='language' colspan='2'>";
		switch (languages.size()) {
		case 0:
			html += "No Languages";
			break;
		case 1:
			html += "Languages";
			break;
		default:
			html += languages.size() + " Languages";
		}

		html += getAddHTML("Language") + "</td></tr>";
		// }
		for (Language l : languages) {
			html += l.toHTML(true);
		}
		html += ComaHTML.SEPARATOR;

		html += (setting != null) ? setting.toHTML(true, false)
				+ ComaHTML.SEPARATOR : "";
		// there's nothing useful in there!
		// treat speakers seperately

		html = (!onlyRows) ? ComaHTML.completeTable(html, "communication",
				"communication32.png") : html;
		// hier ist der erste Teil rum.

		if (recordings.size() > 0) {
			String rhtml = "<tr><td class='recording' colspan='2'>"
					+ recordings.size()
					+ (recordings.size() > 1 ? " Recordings" : " Recording")
					+ getAddHTML("Recording") + "</td></tr>";

			for (Recording r : recordings) {
				rhtml += r.toHTML(true);
			}
			html += ComaHTML.completeTable(rhtml, "recording", "reel32.png");
		} else {
			String rhtml = "<tr><td class='recording'>No Recordings"
					+ getAddHTML("Recording") + "</td></tr>";
			html += ComaHTML.completeTable(rhtml, "recording", "reel32.png");
		}
		html += ComaHTML.TABLE_SEPARATOR;
		if (transcriptions.size() > 0) {
			String thtml = "<tr><td class='transcription' colspan='2'>"
					+ transcriptions.size()
					+ (transcriptions.size() > 1 ? " Transcriptions"
							: " Transcription") + getAddHTML("Transcription")
					+ "</td></tr>";

			for (Transcription t : transcriptions) {
				thtml += t.toHTML(true);
			}
			html += ComaHTML.completeTable(thtml, "transcription", "edit.png");
		} else {
			String thtml = "<tr><td class='transcription' colspan='2'>No Transcriptions"
					+ getAddHTML("Transcription") + "</td></tr>";
			html += ComaHTML.completeTable(thtml, "recording", "edit.png");

		}
		html += ComaHTML.TABLE_SEPARATOR;
		if (files.size() == 0) {
			String thtml = "<tr><td class='file' colspan='2'>No attached files"
					+ getAddHTML("File") + "</td></tr>";
			html += ComaHTML.completeTable(thtml, "file", "file.png");

		} else {
			String thtml = "<tr><td class='file' colspan='2'>" + files.size()
					+ (files.size() > 1 ? " attached files" : " attached file")
					+ getAddHTML("File") + "</td></tr>";

			for (File f : files) {
				thtml += f.toHTML(true);
			}
			html += ComaHTML.completeTable(thtml, "file", "file.png");

		}
		String shtml = "";
		String sp = "";
		if (prefs.getBoolean("menu.viewMenu.showSpeakers", false)) {
			if (coma.hasRoles()) {
				for (Element se : coma.getData().getAssignedSpeakers(this.getElement()).values()) {
					Speaker s = new Speaker(coma,se,false);
					sp = (location != null) ? s.toHTML(
							location.getPeriod(), true) : s.toHTML(true);
					sp = (!onlyRows) ? ComaHTML.completeTable(sp,
							"speaker", "speaker.png") : sp;
					shtml += sp;
				}
			} else {

				for (Speaker s : setting.getSpeakers()) {
					if (s != null) {
						sp = (location != null) ? s.toHTML(
								location.getPeriod(), true) : s.toHTML(true);
						sp = (!onlyRows) ? ComaHTML.completeTable(sp,
								"speaker", "speaker.png") : sp;
						shtml += sp;
					} else {
						// sprecher existiert nicht. k�nnte man l�schen. oder
						// sich
						// beschweren.
					}
				}
			}
		}
		html += shtml;
		return ComaHTML.htmlBracket(html);

	}

	//
	// public String toCSV() {
	// String csv = "Communication" + name + "\n";
	// csv += description.toCSV();
	// if (location != null) {
	// csv += location.toCSV();
	// }
	// for (Language l : languages) {
	// csv += l.toCSV();
	// }
	//
	// csv += (setting != null) ? setting.toCSV() : "";
	//
	// if (recordings.size() > 0) {
	// for (Recording r : recordings) {
	// csv += r.toCSV();
	// }
	// }
	// if (transcriptions.size() > 0) {
	// for (Transcription t : transcriptions) {
	// csv += t.toCSV();
	// }
	// }
	// if (files.size() > 0) {
	// for (File f : files) {
	// csv += f.toCSV();
	// }
	// }
	// return csv;
	//
	// }

	@Override
	public void edit() {
		String s = JOptionPane.showInputDialog(coma, Ui.getText("name"), name);
		if (s != null) {
			if (s.length() > 0 && !name.equals(s)) {
				el.setAttribute("Name", s);
				name = s;
				coma.dataPanel.updateSelectedElement();
				coma.updateValueDisplay();
				coma.xmlChanged();
			}
		}
	}

	@Override
	public void add(String whichType) {
		if (whichType.equals("Language")) {
			Language.create(coma, this);
		}
		if (whichType.equals("Location")) {
			Location.create(coma, this);
		}
		if (whichType.equals("Transcription")) {
			Transcription.create(coma, this);
		}
		if (whichType.equals("Recording")) {
			Recording.create(coma, this);
		}
		if (whichType.equals("File")) {
			File.create(coma, this);
		}
	}

	public String getName() {
		return name;
	}

	public TreeSet<File> getFiles() {
		return files;
	}

	public String toString() {
		return "Communication: "+name;
	}
}
