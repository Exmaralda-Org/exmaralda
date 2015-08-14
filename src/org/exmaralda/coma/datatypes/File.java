package org.exmaralda.coma.datatypes;

import java.net.URLConnection;
import java.util.List;

import javax.swing.JFileChooser;

import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.coma.helpers.URIHelper;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

public class File extends ComaDatatype implements Comparable<File> {

	String id;
	String filename;
	String mimetype;
	String relPath;
	String absPath;
	Description description;

	public File(Element e, Coma c) {
		super(e, c);
		datatype = "File";
		refresh();
	}

	@Override
	public void refresh() {
		if (el.getChild("Description") == null) {
			el.addContent(new Element("Description"));
			description = new Description(el.getChild("Description"), coma);
		} else {
			description = new Description(el.getChild("Description"), coma);
		}
		for (Element elm : (List<Element>) el.getContent(new ElementFilter())) {

			if (elm.getName().equals("filename"))
				filename = elm.getText();
			if (elm.getName().equals("mimetype"))
				mimetype = elm.getText();
			if (elm.getName().equals("relPath"))
				relPath = elm.getText();
			if (elm.getName().equals("absPath"))
				absPath = elm.getText();
			if (elm.getName().equals("Description"))
				description = new Description(elm, coma);
		}
	}

	// String mimeType = fileNameMap.getContentTypeFor("alert.gif");

	public String toHTML(boolean onlyRows) {
		String html = "<tr><td colspan='2' class='file'>";
		// if ((filename != null) && (nSLink != null)) {
		html += "File: <a href='"
				+ relPath
				// + URIHelper.getURI(coma.getData().getOpenFile(), absPath)
				// .getPath()
				+ "'>"
				+ ComaHTML.hilightSearchResult(filename, coma.getData()
						.getSearchTerm()) + "</a>" + getDeleteHTML();
		html += "<tr><td>Mimetype</td><td>" + mimetype + "</td></tr>";
		html += description.toHTML(true);
		html = (!onlyRows) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;
	}

	public static void create(Coma coma, Communication communication) {
		if (coma.isSaved()) {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new java.io.File(Coma.prefs.get(
					"recentFileDirectory", "/")));

			if (communication.getTranscriptions().size() > 0) {
				if (communication.getTranscriptions().get(0).getNsLink() != null) {
					System.out.println(communication.getTranscriptions().get(0)
							.getNsLink());

					try {
						if (URIHelper.getFile(

								coma.getData().getOpenFile(),
								communication.getTranscriptions().get(0)
										.getNsLink().getPath()).getParentFile()
								.exists()) {
							fc.setCurrentDirectory(

							URIHelper.getFile(
									coma.getData().getOpenFile(),
									communication.getTranscriptions().get(0)
											.getNsLink().getPath()).getParentFile()

							);

						}
					} catch (NullPointerException e) {
						coma.error(Ui.getText("err.transNotBelowCorpus"),null);
						System.err.println("");
					}
				}
			}

			fc.setMultiSelectionEnabled(true);
			int dialogStatus = fc.showOpenDialog(coma);

			if (dialogStatus == 0) {
				boolean pathSet = false;
				for (java.io.File theFile : fc.getSelectedFiles()) {
					if (theFile.exists()) {
						if (!pathSet) {
							Coma.prefs.put("recentFileDirectory", theFile
									.getParent());
							pathSet = true;
						}
						String filRelPath = coma.getRelativePath(null, theFile);
						String mimet = (URLConnection.getFileNameMap()
								.getContentTypeFor(theFile.getPath()) != null ? URLConnection
								.getFileNameMap().getContentTypeFor(
										theFile.getPath())
								: "unknown");
						System.out.println(mimet);
						Element c = communication.el;
						boolean addFile = true;
						if (c.getChild("File") != null) {
							for (Element f : (List<Element>) c
									.getChildren("File")) {
								if (f.getChildText("absPath").equals(
										theFile.getPath())) {
									addFile = false;
								}
							}
						}
						if (addFile) {
							Element newFile = new Element("File");
							newFile.setAttribute("Id", "F"
									+ new GUID().makeID());
							newFile.addContent(new Element("filename")
									.setText(theFile.getName()));
							newFile.addContent(new Element("mimetype")
									.setText(mimet));
							newFile.addContent(new Element("relPath")
									.setText(filRelPath));
							newFile.addContent(new Element("absPath")
									.setText(theFile.getAbsolutePath()));
							c.addContent(newFile);
							communication.refresh();
							coma.updateValueDisplay();
							coma.xmlChanged();
						}
					}
				}

			}
		}

	}

	@Override
	public int compareTo(File o) {
		return this.filename.compareTo(o.filename);
	}


}
