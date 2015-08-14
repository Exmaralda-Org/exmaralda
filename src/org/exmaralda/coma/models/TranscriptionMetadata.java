/*
 * Created on 03.01.2005 by woerner
 */
package org.exmaralda.coma.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.coma.root.ComaXMLOutputter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * coma2/org.sfb538.coma2.importers/ExmaraldaPartitur.java
 * 
 * @author woerner
 * 
 */
public class TranscriptionMetadata {

	private String id;

	private String fileName;

	private HashMap<String, HashMap<String, String>> speakers;

	private HashMap<String, String> metadata;

	private boolean valid;

	private HashSet<String> mediaFiles;
	// private String mediaFile;

	private boolean segmented;

	private String transcriptionName;

	private HashMap<String, String> machineTags;

	/**
	 * @return Returns the mediaFile.
	 */
	public HashSet<String> getMediaFiles() {
		return mediaFiles;
	}

	/**
	 * @param mediaFile
	 *            The mediaFile to set.
	 */
	public void setMediaFiles(HashSet<String> files) {
		mediaFiles = files;
		// this.mediaFile = mediaFile;
	}

	public void addMediaFile(String filename) {
		mediaFiles.add(filename);
	}

	public boolean removeMediaFile(String filename) {
		if (mediaFiles.contains(filename)) {
			return false;
		} else {
			mediaFiles.remove(filename);
			return true;
		}

	}

	public TranscriptionMetadata(File inputFile, boolean makeId) {
		if (inputFile.exists()) {
			setValues(inputFile, makeId);
		}
	}

	/**
	 * @param addId
	 * 
	 */
	private void setValues(File xmlFile, boolean makeId) {
		fileName = xmlFile.getPath();
		mediaFiles = new HashSet<String>();
		metadata = new HashMap<String, String>();
		speakers = new HashMap<String, HashMap<String, String>>();
		machineTags = new HashMap<String, String>();
		Element metaInformation;
		Element spkTable;
		try {
			SAXBuilder builder = new SAXBuilder(
					"org.apache.xerces.parsers.SAXParser");

			Document partitur = builder.build(xmlFile);
			Element root = partitur.getRootElement();
			if ((root.getName().equals("basic-transcription"))
					|| (root.getName().equals("segmented-transcription"))) {
				valid = true;
				if (root.getAttributeValue("Id") == null) {
					id = "CID" + new GUID().makeID();
					if (makeId) {
						root.setAttribute("Id", id);
						ComaXMLOutputter outputter = new ComaXMLOutputter();
						try {
							FileOutputStream out = new FileOutputStream(xmlFile);
							outputter.output(partitur, out);
							out.close();
						} catch (IOException ie) {
							// saved = true;
						}
					}
					// attribut machen
					// speichern
				} else {
					id = root.getAttributeValue("Id");
				}
				segmented = root.getName().equals("segmented-transcription");
				metaInformation = root.getChild("head").getChild(
						"meta-information");
				metadata.put("project-name",
						metaInformation.getChild("project-name").getText()
								.trim());
				metadata.put("transcription-name",
						metaInformation.getChild("transcription-name")
								.getText());
				metadata.put("comment", metaInformation.getChild("comment")
						.getText());
				metadata.put("transcription-convention", metaInformation
						.getChild("transcription-convention").getText().trim());
				for (Element e : (List<Element>) metaInformation
						.getChildren("referenced-file")) {
					if (e.getAttributeValue("url").length() > 0) {
						mediaFiles.add(e.getAttributeValue("url"));
					}
				}
				for (Element e : (List<Element>) metaInformation.getChild(
						"ud-meta-information").getChildren()) {
					if ((e.getAttributeValue("attribute-name").startsWith("#"))) {
						machineTags.put(e.getAttributeValue("attribute-name")
								.substring(2), e.getText().trim());
					} else {
						metadata.put(
								"ud_" + e.getAttributeValue("attribute-name"),
								e.getText().trim());
					}
				}
				spkTable = (Element) root.getChild("head").getChild(
						"speakertable");
				for (Element s : (List<Element>) spkTable.getChildren()) {
					String id = "SID" + new GUID().makeID();
					speakers.put(id, new HashMap<String, String>());
					speakers.get(id).put("id", s.getAttributeValue("id"));
					speakers.get(id).put("@abbreviation",
							s.getChildText("abbreviation"));
					speakers.get(id).put(
							"@sex",
							(s.getChild("sex").getAttributeValue("value")
									.equals("m") ? "male" : "female"));
					speakers.get(id).put("@abbreviation",
							s.getChildText("abbreviation"));
					int count = 0;
					for (Element ul : (List<Element>) s.getChild(
							"languages-used").getChildren()) {
						count++;
						metadata.put("@language-used-" + count,
								ul.getAttributeValue("lang"));
					}
					count = 0;
					for (Element l1e : (List<Element>) s.getChild("l1")
							.getChildren()) {
						speakers.get(id).put("@l1" + count,
								l1e.getAttributeValue("lang"));
						count++;
					}
					count = 0;
					for (Element l2e : (List<Element>) s.getChild("l2")
							.getChildren()) {
						speakers.get(id).put("@l2" + count,
								l2e.getAttributeValue("lang"));

						count++;
					}

					for (Element udi : (List<Element>) s.getChild(
							"ud-speaker-information").getChildren()) {
						speakers.get(id).put(
								"ud_"
										+ udi.getAttributeValue(
												"attribute-name").trim(),
								udi.getText());

						// }
					}
					if (s.getChild("comment").getText().length() > 0) {
						speakers.get(id).put("comment",
								s.getChild("comment").getText());
					}
				}
			} else {
				valid = false;
			}
			partitur = null;
			builder = null;
		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("Could not parse " + xmlFile.getPath()
					+ " - not an EXMARaLDA-Transcription.");
			valid = false;
		}
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

	/**
	 * @return Returns the transcriptionName.
	 */
	public String getTranscriptionName() {
		return transcriptionName;
	}

	public String getId() {
		return id;
	}

	public String getFileName() {
		return fileName;
	}

	public HashMap<String, HashMap<String, String>> getSpeakers() {
		return speakers;
	}

	public HashMap<String, String> getMetadata() {
		return metadata;
	}

	public HashMap<String, String> getMachineTags() {
		return machineTags;
	}
}