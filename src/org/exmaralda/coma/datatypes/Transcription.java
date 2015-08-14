package org.exmaralda.coma.datatypes;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.JFileChooser;

import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.coma.importer.ExmaraldaPartitur;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.ExmaraldaFileFilter;
import org.jdom.Element;

public class Transcription extends ComaDatatype {
	String name;
	String id; // att
	// String fileStore; // ?!?
	String filename;
	private URI nsLink;
	Description description;
	Availability availability;

	public Transcription(Element e, Coma c) {
		super(e, c);
		datatype = "Transcription";
		refresh();
	}

	public void refresh() {
		if (el.getChild("Description") != null) {
			description = new Description(el.getChild("Description"), coma);
		}
		List<Element> ell = el.getChildren();
		for (Element myElm : ell) {

			if (myElm.getName().equals("Name"))
				name = myElm.getText();

			if (myElm.getName().equals("Filename")) {
				filename = myElm.getText();
			}
			if (myElm.getName().equals("NSLink")) {
				try {
					nsLink = new URI(myElm.getText());
				} catch (URISyntaxException e) {
					nsLink = null;
					System.err.println("malformed uri for transcription in "
							+ coma.getData().getOpenFile().getName() + ": "
							+ myElm.getText());
				}
			}
			if (myElm.getName().equals("Availability")) {
				availability = new Availability(myElm, coma);
			}
		}
		if (availability == null) {
			availability = new Availability(new Element("Availability"), coma);

		}

	}

	public static void create(Coma c, Communication parent) {
		System.out.println("creating transcription!");
		JFileChooser fc = new JFileChooser();
		if (new File(Coma.prefs.get("recentTranscriptionDirectory", "null"))
				.exists()) {
			fc.setCurrentDirectory(new File(Coma.prefs.get(
					"recentTranscriptionDirectory", "/")));

		} else {
			fc.setCurrentDirectory(new File(Coma.prefs.get("recentDir", "/")));

		}
		fc.addChoosableFileFilter(new ExmaraldaFileFilter(
				"EXMaRALDA-Transcriptions", c.getData().TRANSCRIPTION_FORMATS,
				true));
		fc.setMultiSelectionEnabled(true);
		int dialogStatus = fc.showOpenDialog(c);
		if (dialogStatus == 0) {
			for (int i = 0; i < fc.getSelectedFiles().length; i++) {
				if (i == 0) {
					Ui.prefs.put("recentTranscriptionDirectory",
							fc.getSelectedFiles()[i].getParent());
				}
				System.out.println("1");
				ExmaraldaPartitur myPartitur = new ExmaraldaPartitur(
						fc.getSelectedFiles()[i], Coma.prefs.getBoolean(
								"prefs.writeCIDWhenAssigning", true));
				if (myPartitur.isValid()) {
					System.out.println("2");
					Element newTrans = new Element("Transcription");
					newTrans.setAttribute("Id", new GUID().makeID());
					newTrans.addContent(new Element("Name"));
					if (c.prefs.get("prefs.nameTranscriptionsAfter",
							Ui.getText("communication")).equals(
							Ui.getText("communication"))) {
						newTrans.getChild("Name").setText(
								parent.getName() + (i > 0 ? i : ""));
					} else {
						newTrans.getChild("Name").setText(
								fc.getSelectedFiles()[i].getName()
										+ (i > 0 ? i : ""));
					}
					newTrans.addContent(new Element("Filename"));
					newTrans.getChild("Filename").setText(
							fc.getSelectedFiles()[i].getName());
					newTrans.addContent(new Element("NSLink"));
					newTrans.getChild("NSLink").setText(
							c.getRelativePath(null, fc.getSelectedFiles()[i]));
					newTrans.addContent(new Element("Description"));

					Element typeKey = new Element("Key");
					typeKey.setAttribute("Name", "segmented");
					typeKey.setText((myPartitur.isSegmented() ? "true"
							: "false"));
					newTrans.getChild("Description").addContent(typeKey);
					newTrans.getChild("Description").addContent(
							myPartitur.getSegmentElements());
					newTrans.addContent(new Element("Availability"));
					newTrans.getChild("Availability").addContent(
							new Element("Available"));
					newTrans.getChild("Availability")
							.getChild("Available")
							.setText(
									c.prefs.get("newTranscriptionsAvailable",
											"false"));
					newTrans.getChild("Availability").addContent(
							new Element("ObtainingInformation"));
					int myIndex;
					if (parent.el.getChild("Recording") != null) {
						myIndex = parent.el.getContent().indexOf(
								parent.el.getChild("Recording")) + 1;
					} else {
						myIndex = parent.el.getContent().indexOf(
								parent.el.getChild("Setting")) + 1;
					}
					parent.el.addContent(myIndex, newTrans);
					parent.refresh();
					c.updateValueDisplay();
					c.xmlChanged();
				} else {
					// not valid
				}
			}
		}

	}

	public String toHTML(boolean onlyRows) {
		// is segmented?
		String segmented = "";
		if (description.getDataAsTreeMap() != null) {
			if (description.getDataAsTreeMap().get("segmented") != null) {
				segmented = ((description.getDataAsTreeMap().get("segmented"))
						.equals("true")) ? "Segmented " : "Basic ";
			}
		}

		String html = "<tr><td colspan='2' class='transcription'>" + segmented
				+ Ui.getText("transcription") + ": " + name +

				getDeleteHTML() + "</td></tr>";
		html += description.toHTML(true);
		// html += availability.toHTML(true);
		html += "<tr><td>File: </td><td><a href='"
				+ nsLink
				+ "'>"
				+ (nsLink.toString().length() > 20 ? "(...)"
						+ nsLink.toString().substring(
								nsLink.toString().length() - 20) : nsLink
						.toString()) + "</a></td></tr>";
		return (!onlyRows) ? ComaHTML.tableWithStylesStart(html) : html;
	}

	@Override
	public void add(String whichType) {
		if (whichType.equals("Description")) {
			Description.create(coma, this);
		}
	}

	public void setNsLink(URI nsLink) {
		this.nsLink = nsLink;
	}

	public URI getNsLink() {
		return nsLink;
	}

}
