/*
 * Created on 03.01.2005 by woerner
 */
package org.exmaralda.coma.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.exmaralda.coma.dialogs.ComaError;
import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.coma.root.ComaXMLOutputter;
import org.exmaralda.coma.root.Ui;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * coma2/org.sfb538.coma2.importers/ExmaraldaPartitur.java
 * 
 * @author woerner
 * 
 */
public class ExmaraldaPartitur {
	public boolean changed;

	private String id;

	private List speakers;

	private boolean segmented;

	private boolean valid;

	private boolean doesExist;

	private File xmlFile;

	private String mediaFile;

	private String projectName;

	private String transcriptionName;

	private String comment;

	private String transcriptionConvention;

	private HashMap description;

	private HashMap segmentCount;

	private List segmentElements;

	// private SAXBuilder builder = new SAXBuilder(
	// "org.apache.xerces.parsers.SAXParser");

	private boolean saved = false;

	private String rootElementName;

	private boolean writeId;

	private Vector<String> mediaFiles;

	private Document partitur;

	/**
	 * @return Returns the mediaFile.
	 */
	public Vector<String> getMediaFiles() {
		return mediaFiles;
	}

	public String getMediaFileDeprecated() {
		return mediaFile;
	}

	/**
	 * @param mediaFile
	 *            The mediaFile to set.
	 */
	public String setMediaFile(String newMediaFile) {
		String oldMedia = mediaFile;

		return oldMedia;
		// this.mediaFile = mediaFile;
	}

	public ExmaraldaPartitur(File inputFile, boolean updateFile) {
		xmlFile = inputFile;
		writeId = updateFile;
		if (xmlFile.canRead()) {
			setValues();
		}
	}

	/**
	 * @param addId
	 * 
	 */
	private void setValues() {
		changed = false;
		Element metaInformation;
		List udmeta;
		Element myDesc;
		Element spkTable;
		Iterator uI;
		description = new HashMap();
		segmentCount = new HashMap();
		try {
			SAXBuilder builder = new SAXBuilder(
					"org.apache.xerces.parsers.SAXParser");

			partitur = builder.build(xmlFile);
			Element root = partitur.getRootElement();
			rootElementName = root.getName();
			if (root.getName().endsWith("-transcription")) {
				valid = true;
				if (root.getAttributeValue("Id") == null) {
					if (writeId) {
						// hierher! zackzack!
						id = "CID" + new GUID().makeID();
						root.setAttribute("Id", id);
						ComaXMLOutputter outputter = new ComaXMLOutputter();
						try {
							FileOutputStream out = new FileOutputStream(xmlFile);
							outputter.output(partitur, out);
							out.close();
							saved = true;
						} catch (IOException ie) {
							// saved = true;
						}

					} else {
						id = "temporaryId_" + new GUID().makeID();

					}
				} else {
					root.getAttributeValue("Id");
				}

				segmented = root.getName().equals("segmented-transcription");

				metaInformation = root.getChild("head").getChild(
						"meta-information");

				projectName = metaInformation.getChild("project-name")
						.getText();

				transcriptionName = metaInformation.getChild(
						"transcription-name").getText();

				comment = metaInformation.getChild("comment").getText();

				List<Element> mf = metaInformation
						.getChildren("referenced-file");

				mediaFiles = new Vector<String>();

				for (Element me : mf) {
					if (me.getAttributeValue("url").length() > 0) {
						mediaFiles.add(me.getAttributeValue("url"));
					}
				}
				if (metaInformation.getChild("referenced-file")!=null) {
					mediaFile = (metaInformation.getChild("referenced-file")
							.getAttributeValue("url").length() > 0) ? metaInformation
							.getChild("referenced-file").getAttributeValue(
									"url") : null;
				} else {
					mediaFile = null;
				}
				transcriptionConvention = metaInformation.getChild(
						"transcription-convention").getText();

				udmeta = metaInformation.getChild("ud-meta-information")
						.getChildren();
				uI = udmeta.iterator();
				while (uI.hasNext()) { // ud-information elements
					myDesc = (Element) uI.next();
					if ((myDesc.getAttributeValue("attribute-name")
							.startsWith("#"))) {
						segmentCount.put(
								myDesc.getAttributeValue("attribute-name")
										.substring(2), myDesc.getText());
					} else {
						description.put(
								myDesc.getAttributeValue("attribute-name"),
								myDesc.getText());

					}
				}

				spkTable = (Element) root.getChild("head")
						.getChild("speakertable").clone();
				speakers = spkTable.getChildren();

			} else {
				valid = false;
			}
			// partitur = null;
			builder = null;
		} catch (Exception e) {
			new ComaError("Error in " + xmlFile, xmlFile.getName()
					+ Ui.getText("err.invalidTranscription"), e.toString());
			e.printStackTrace();
			valid = false;
		}
	}

	/**
	 * @return Returns the exists.
	 */
	public boolean exists() {
		return doesExist;
	}

	/**
	 * @return Returns whether segmented or not.
	 */
	public boolean isSegmented() {
		return segmented;
	}

	/**
	 * @return Returns the valid.
	 */
	public boolean isValid() {
		return valid;
	}

	public HashMap getSegmentCount() {

		return segmentCount;
	}

	/**
	 * @return Returns the comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return Returns the convention.
	 */
	public String getTranscriptionConvention() {
		return transcriptionConvention;
	}

	/**
	 * @return Returns the description.
	 */
	public HashMap getDescription() {
		return description;
	}

	/**
	 * @return Returns the projectName.
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return Returns the transcriptionName.
	 */
	public String getTranscriptionName() {
		return transcriptionName;
	}

	/**
	 * @return Returns the xmlFile.
	 */
	public File getXmlFile() {
		return xmlFile;
	}

	/**
	 * @return Returns the speakers.
	 */
	public List getSpeakers() {
		return speakers;
	}

	public HashSet<String> getAllUsedLanguages() {
		HashSet<String> ul = new HashSet<String>();
		Iterator spi = speakers.iterator();
		while (spi.hasNext()) {
			Element spkr = (Element) spi.next();
			Element usd = spkr.getChild("languages-used");
			if (usd.getChildren().size() > 0) {
				Iterator lu = usd.getChildren().iterator();
				while (lu.hasNext()) {
					ul.add(((Element) lu.next()).getAttributeValue("lang"));
				}

			}
		}
		return ul;
	}

	public List getSegmentElements() {
		Vector segElmts = new Vector();

		for (Iterator it = segmentCount.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			Element myElement = new Element("Key");
			myElement.setAttribute("Name", "# " + key);
			myElement.setText(value.toString());
			segElmts.add(myElement);
		}

		segmentElements = segElmts;
		return segmentElements;
	}

	public String getId() {
		return id;
	}

	public String getRootElementName() {
		return rootElementName;
	}

	public Document getDocument() {
		return partitur;
	}

	public void rewrite() {
		ComaXMLOutputter outputter = new ComaXMLOutputter();
		try {
			FileOutputStream out = new FileOutputStream(xmlFile);
			outputter.output(partitur, out);
			out.close();
			saved = true;
		} catch (IOException ie) {
			// saved = true;
		}
	}
}