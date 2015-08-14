package org.exmaralda.coma.datatypes;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import org.apache.commons.io.FilenameUtils;
import org.exmaralda.coma.helpers.ComaDateTime;
import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.DurationHelper;
import org.exmaralda.partitureditor.sound.RecordingPropertiesCalculator;
import org.jdom.Element;

public class Recording extends ComaDatatype {
	String id; // att

	String name;

	Description description;

	Vector<Media> media = new Vector<Media>();

	Date recordingDateTime;

	long recordingDuration;

	Availability availability;

	public Recording(Element e, Coma c) {
		super(e, c);
		datatype = "Recording";
		refresh();
	}

	@Override
	public void refresh() {
		if (el.getChild("Description") != null) {
			description = new Description(el.getChild("Description"), coma);
		} else {
			el.addContent(new Element("Description"));
			description = new Description(el.getChild("Description"), coma);

		}
		List<Element> ell = el.getChildren();
		for (Element myElm : ell) {

			if (myElm.getName().equals("Name"))
				name = myElm.getText();

			if (myElm.getName().equals("Media")) {
				media.add(new Media(myElm, coma));
			}
			if (myElm.getName().equals("RecordingDateTime"))
				recordingDateTime = ComaDateTime.dateFromXSDateTime(myElm
						.getText());
			//
			if (myElm.getName().equals("RecordingDuration"))
				recordingDuration = new Long(myElm.getText());
			if (myElm.getName().equals("Availability"))
				availability = new Availability(myElm, coma);
		}

	}

	@Override
	public String toHTML(boolean onlyRows) {

		String html = "";
		html += "<tr><td class='recording' colspan='2'>Recording: "
				+ (name != null ? name : "")
				+ (recordingDuration > 0 ? " ("
						+ DurationHelper.getDurationString(recordingDuration,
								DurationHelper.MILLISECONDS) + "; "
						+ getColumnHTML("" + recordingDuration) + "ms)" : "")
				+ getDeleteHTML() + "</td></tr>";
		html += description.toHTML(true);
		for (Media md : media) {
			html += md.toHTML(true);
		}
		html = (!onlyRows) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;
	}

	public void add(String string) {
		System.out.println("add " + string);
	}

	public static void create(Coma coma, Communication communication) {
		if (coma.isSaved()) {
			JFileChooser fc = new JFileChooser();
			JCheckBox sepRecCheckBox = new JCheckBox(Ui.getText("option.seperateRecordings"));
			fc.setAccessory(sepRecCheckBox);

			if (new File(Coma.prefs.get("recentRecordingDirectory", "null"))
					.exists()) {
				fc.setCurrentDirectory(new File(Coma.prefs.get(
						"recentRecordingDirectory", "/")));

			} else {
				fc.setCurrentDirectory(coma.getData().getOpenFile()
						.getParentFile());
			}
			fc.setMultiSelectionEnabled(true);
			int dialogStatus = fc.showOpenDialog(coma);

			if (dialogStatus == 0) {
				boolean pathSet = false;
				String recRelPath;
				Element c = communication.el;
				HashSet<File> filesToAdd = new HashSet<File>();
				for (File recFile : fc.getSelectedFiles()) {
					filesToAdd.add(recFile);
					if (recFile.exists()) {
						if (!pathSet) {
							Coma.prefs.put("recentRecordingDirectory",
									recFile.getParent());
							pathSet = true;
						}
						recRelPath = coma.getRelativePath(null, recFile);
						if (c.getChild("Recording") != null) {
							for (Element r : (List<Element>) c
									.getChildren("Recording")) {
								if (r.getChild("Media") != null) {
									if (r.getChild("Media").getChild("NSLink") != null) {
										String rF = r.getChild("Media")
												.getChildText("NSLink");
										if (rF.equals(recRelPath)) {
											filesToAdd.remove(recFile);
										}
									}
								}
							}
						}
					} else {
						filesToAdd.remove(recFile);
					}
				}
				if (filesToAdd.size() > 0) {
					if (sepRecCheckBox.isSelected()) { // jede datei in eine eigene Recording
						HashSet<String>recordingNames = new HashSet<String>();
						int nameCount=0;
						for (File f : filesToAdd) {
							nameCount++;
							Element newRec = new Element("Recording");
							String elementName = FilenameUtils
									.removeExtension(filesToAdd
											.iterator().next()
											.getName());
							if (recordingNames.contains(elementName)) {
								elementName+=""+nameCount;
							}
							recordingNames.add(elementName);
							newRec.addContent(new Element("Name")
									.setText(elementName));
							newRec.setAttribute("Id", "R" + new GUID().makeID());
							Element newMedia = new Element("Media");
							newMedia.setAttribute("Id",
									"M" + new GUID().makeID());
							newMedia.addContent(new Element("Description"));
							newMedia.getChild("Description").addContent(
									new Element("Key"));
							newMedia.getChild("Description").getChild("Key")
									.setAttribute("Name", "Type");
							newMedia.getChild("Description").getChild("Key")
									.setText("Digital");
							newMedia.addContent(new Element("NSLink"));
							recRelPath = coma.getRelativePath(null, f);
							if (newRec.getChildren("RecordingDuration").size() == 0) {
								if (f.getName().endsWith(".wav")) {
									double dur = RecordingPropertiesCalculator
											.getRecordingDuration(f);
									if (dur >= 0) {
										newRec.addContent(new Element(
												"RecordingDuration").setText(Long
												.toString((long) (Math
														.round(dur * 1000.0)))));
									}
								}
							}


							newMedia.getChild("NSLink").setText(recRelPath);
							newRec.addContent(newMedia);
							c.addContent(newRec);
							communication.refresh();
							coma.updateValueDisplay();
							coma.xmlChanged();

						}

					} else { // alle dateien in die selbe recording
						Element newRec = new Element("Recording");
						newRec.addContent(new Element("Name")
								.setText(FilenameUtils
										.removeExtension(filesToAdd.iterator()
												.next().getName())));
						newRec.setAttribute("Id", "R" + new GUID().makeID());
						for (File f : filesToAdd) {
							Element newMedia = new Element("Media");
							newMedia.setAttribute("Id",
									"M" + new GUID().makeID());
							newMedia.addContent(new Element("Description"));
							newMedia.getChild("Description").addContent(
									new Element("Key"));
							newMedia.getChild("Description").getChild("Key")
									.setAttribute("Name", "Type");
							newMedia.getChild("Description").getChild("Key")
									.setText("Digital");
							newMedia.addContent(new Element("NSLink"));
							recRelPath = coma.getRelativePath(null, f);

							newMedia.getChild("NSLink").setText(recRelPath);
							if (newRec.getChildren("RecordingDuration").size() == 0) {
								if (f.getName().endsWith(".wav")) {
									double dur = RecordingPropertiesCalculator
											.getRecordingDuration(f);
									if (dur >= 0) {
										newRec.addContent(new Element(
												"RecordingDuration").setText(Long
												.toString((long) (Math
														.round(dur * 1000.0)))));
									}
								}
							}
							newRec.addContent(newMedia);
						}
						c.addContent(newRec);
						communication.refresh();
						coma.updateValueDisplay();
						coma.xmlChanged();
					}

				}

			}
			System.out.println("CREATE!");
		}
	}

	public String getColumnHTML(String display) {
		String columnHTML = "<a href=\"#\" id=\"column:" + getXPath()
				+ "/RecordingDuration\" title=\"2nd column\">";
		columnHTML += ComaHTML.hilightSearchResult(display, coma.getData()
				.getSearchTerm())
				+ "</a>";
		return columnHTML;
	}

}
