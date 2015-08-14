/**
 *
 */
package org.exmaralda.coma.root;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.exmaralda.coma.datatypes.ComaDatatype;
import org.exmaralda.coma.datatypes.ComaTemplate;
import org.exmaralda.coma.filters.ComaFilter;
import org.exmaralda.coma.helpers.ComaTemplates;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/** @author woerner */
public class ComaData {
	private static final int MAX_RECENT_FILES = 5;

	public static final String[] TRANSCRIPTION_FORMATS = new String[] { "xml",
			"exb", "exs", "tei", "exa" };
	public static Preferences prefs = Ui.prefs;
	HashMap<String, ComaTemplate> oldTemplates;
	private HashSet<Element> basket;

	private HashSet<String> roleTypes;

	private HashMap<String, ComaFilter> cfilters;

	private HashMap<String, ComaFilter> pfilters;

	private HashMap<String, ComaFilter> cFilterPresets;

	private HashMap<String, ComaFilter> sFilterPresets;

	private HashMap<String, String> hiddenKeys;

	private Vector<File> recentFiles;

	// private Element selectedCorpus;

	String defaultTemplateName = "";

	private Element dataElement;

	private Document document;

	private Element rootElement;

	private File openFile = new File("");

	private String uniqueSpeakerDistinction;

	private int schemaVersion = 0;

	private ComaTemplates templates;

	private File templateFile;

	private String searchTerm;

	private HashMap<String, Element> selectedCommunications = new HashMap<String, Element>();
	private HashMap<String, Element> selectedPersons = new HashMap<String, Element>();
	// waren Element-Vektoren
	private Coma coma;

	private String filterChanged;

	public ComaData(Coma c) {
		coma = c;
		oldTemplates = new HashMap<String, ComaTemplate>();
		basket = new HashSet<Element>();
		cfilters = new HashMap<String, ComaFilter>();
		pfilters = new HashMap<String, ComaFilter>();
		templates = new ComaTemplates();
		uniqueSpeakerDistinction = "";
		openFile = new File("");
		initPresets();
		createRecentFiles();
	}

	private void initPresets() {
		cFilterPresets = new HashMap<String, ComaFilter>();
		cFilterPresets.put(Ui.getText("filter.emptyFilter"), new ComaFilter(
				coma, "//Corpus/CorpusData/Communication[1>0]"));
		cFilterPresets
				.put(Ui.getText("filter.commsWithTranscriptions"),
						new ComaFilter(coma,
								"//Corpus/CorpusData/Communication[count(Transcription)>0]"));
		cFilterPresets
				.put(Ui.getText("filter.commsWithRecordings"), new ComaFilter(
						coma,
						"//Corpus/CorpusData/Communication[count(Recording)>0]"));
		sFilterPresets = new HashMap<String, ComaFilter>();
		sFilterPresets.put(Ui.getText("filter.emptyFilter"), new ComaFilter(
				coma, "//Corpus/CorpusData/Speaker[1>0]"));
	}

	private void createRecentFiles() {
		recentFiles = new Vector<File>();
		String allFiles = prefs.get("recentFiles", "");
		String[] files = allFiles.split("\n");
		for (String s : files) {
			if (s.length() > 0) {
				if (recentFiles.size() < MAX_RECENT_FILES) {
					recentFiles.add(new File(s));
				}
			}
		}
	}

	// RECENT FILES (moved from coma.helpers.RecentFiles.java)
	public Vector<File> getRecentFiles() {
		return recentFiles;
	}

	public void removeRecentFile(File f) {
		recentFiles.remove(f);
		recentFilesChanged();
	}

	private void recentFilesChanged() {
		String filesString = "";
		for (File f : recentFiles) {
			filesString += f.getAbsolutePath() + "\n";
		}
		prefs.put("recentFiles", filesString);
	}

	public void clearRecentFiles() {
		recentFiles.clear();
		recentFilesChanged();
	}

	private void setRecentFile(File f) {
		if (f != null)
			if (f.exists()) {
				prefs.put("recentDir", f.getParent());
				if (recentFiles.contains(f)) {
					recentFiles.remove(f);
				}
				recentFiles.add(0, f);

			}
		if (recentFiles.size() > MAX_RECENT_FILES) {
			recentFiles.remove(recentFiles.size() - 1);
		}
		recentFilesChanged();

	}

	// templates for description-panels

	public ComaTemplates getTemplates() {
		return templates;
	}

	public void setDefaultTemplateName(String t) {
		if (oldTemplates.containsKey(t))
			defaultTemplateName = t;

	}

	public ComaTemplate getTemplate(String s) {
		String key = (s == null ? defaultTemplateName : s);
		return oldTemplates.get(key);
	}

	/**
	 * @param text
	 * @param keys
	 */
	public void addTemplate(String name, Vector<String> keys) {
		oldTemplates.put(name, new ComaTemplate(name, null, keys));

	}

	public HashMap<String, ComaFilter> getCfilters() {
		return cfilters;
	}

	public void setCfilters(HashMap<String, ComaFilter> cfilters) {
		this.cfilters = cfilters;
	}

	public HashMap<String, ComaFilter> getPfilters() {
		return pfilters;
	}

	public void addRawCFilter(String xpath) {
		ComaFilter filter = new ComaFilter(coma, xpath);
		cfilters.put(filter.getXPath(), filter);
	}

	public void addRawPFilter(String xpath) {
		ComaFilter filter = new ComaFilter(coma, xpath);
		pfilters.put(filter.getXPath(), filter);

	}

	public void addcfilter(String xpath) {

		String newXPath = xpath.substring(0,
				xpath.indexOf("Communication") + 13)
				+ "["
				+ xpath.substring((xpath.indexOf("Communication") + 14)) + "]";
		System.out.println("-->" + newXPath);
		ComaFilter filter = new ComaFilter(coma, (newXPath));
		cfilters.put(filter.getXPath(), filter);
	}

	public void addpfilter(String xpath) {

		String newXPath = xpath.substring(0, xpath.indexOf("Speaker") + 7)
				+ "[" + xpath.substring((xpath.indexOf("Speaker") + 8)) + "]";
		ComaFilter filter = new ComaFilter(coma, (newXPath));
		pfilters.put(filter.getXPath(), filter);
	}

	public void removepfilter(String xpath) {
		if (xpath == null) {
			pfilters.clear();
		} else {
			pfilters.remove(xpath);
		}
	}

	public void removecfilter(String xpath) {
		if (xpath == null) {
			cfilters.clear();
		} else {
			cfilters.remove(xpath);

		}
	}

	public void removeSearchFilters() {
		for (String xp : cfilters.keySet()) {
			if (xp.contains(searchTerm)) {
				removecfilter(xp);
			}
		}
		for (String xp : pfilters.keySet()) {
			if (xp.contains(searchTerm)) {
				removepfilter(xp);
			}
		}
	}

	/** @param string */

	/**
	 * Returns a java.util.list of Communications for a Speaker
	 * 
	 * @param speakerElmt
	 * @return
	 */
	public HashSet<String> getSpeakerIDs(Vector<Element> speakers) {
		HashSet<String> ids = new HashSet<String>();
		for (Element speaker : speakers) {
			ids.add(speaker.getAttributeValue("Id"));

		}
		return ids;
	}

	/**
	 * Returns a java.util.list of Communications for a Speaker
	 * 
	 * @param speakerElmt
	 * @return
	 */
	public List<Element> getCommunicationsForSpeaker(Element speakerElmt,
			boolean filtered) {
		HashSet<Element> commlist = new HashSet<Element>();
		String speakerId = speakerElmt.getAttributeValue("Id");
		List<Element> allComms = (filtered ? filterComms() : dataElement
				.getChildren("Communication"));
		for (Element el : allComms) {
			List<Element> spks = el.getChild("Setting").getChildren("Person");
			HashMap<String, Element> spkTmp = new HashMap<String, Element>();
			for (Element sp : spks) {
				if (sp.getText().equals(speakerId)) {
					commlist.add(el);

				}

			}

		}
		return (new Vector<Element>(commlist).subList(0, commlist.size()));
		// return List.(commList);
	}

	public List<Element> filterComms() {
		List<Element> allComms = dataElement.getChildren("Communication");
		Vector<Element> independentList = new Vector<Element>();
		independentList.addAll(allComms);
		int fcount = 0;
		for (ComaFilter filter : getCfilters().values()) {
			if (filter.isEnabled()) {
				try {
					XPath xp = XPath.newInstance(filter.isInverted() ? filter
							.getXPathInverted() : filter.getXPath());
					List nl = xp.selectNodes(dataElement);
					if ((filter.isIncluding()) && (fcount > 0)) {
						independentList.addAll(nl);
					} else {
						independentList.retainAll(nl);
					}
				} catch (JDOMException err) {
					err.printStackTrace();
				}
			}
			fcount++;
		}
		return independentList;
	}

	public List<Element> filterSpeakers() {
		List<Element> allComms = dataElement.getChildren("Speaker");
		Vector<Element> independentList = new Vector<Element>();
		independentList.addAll(allComms);
		int fcount = 0;
		for (ComaFilter filter : getPfilters().values()) {
			if (filter.isEnabled()) {
				try {
					XPath xp = XPath.newInstance(filter.isInverted() ? filter
							.getXPathInverted() : filter.getXPath());
					List nl = xp.selectNodes(dataElement);
					if ((filter.isIncluding()) && (fcount > 0)) {
						independentList.addAll(nl);
					} else {
						independentList.retainAll(nl);
					}
				} catch (JDOMException err) {
					err.printStackTrace();
				}
			}
			fcount++;
		}
		return independentList;
	}

	public HashMap<String, Element> getAssignedSpeakers(
			Element communicationElement) {
		HashMap<String, Element> spks = new HashMap<String, Element>();
		XPath idx;
		try {
			idx = XPath.newInstance("//Speaker[role/@target='"
					+ communicationElement.getAttributeValue("Id") + "']");
			List<Element> roleElements = idx.selectNodes(getDataElement());
			for (Element e : roleElements) {
				spks.put(e.getAttributeValue("Id"), e);
			}
		} catch (JDOMException err) {
			err.printStackTrace();
		}
		return spks;
	}

	/** @param elmt */
	// public void setSelectedCorpus(Element elmt) {
	// selectedCorpus = elmt;
	// dataElement = selectedCorpus.getChild("CorpusData");
	// }

	public Element getDataElement() {
		return dataElement;
	}

	// public Element getSelectedCorpus() {
	// return selectedCorpus;
	// }

	/**
	 * sets all communication-filters inactive so a new communication gets
	 * displayed in any case
	 */
	public void disableCommFilters() {
		for (ComaFilter f : getCfilters().values()) {
			f.setEnabled(false);
		}

	}

	/**
	 * sets all speaker-filters inactive so a new communication gets displayed
	 * in any case
	 */
	public void disableSpeakerFilters() {
		for (ComaFilter f : getPfilters().values()) {
			f.setEnabled(false);
		}
	}

	/** @return */
	public String getUniqueSpeakerDistinction() {
		return uniqueSpeakerDistinction;
	}

	/** @param attributeValue */
	public void setUniqueSpeakerDistinction(String attributeValue) {
		uniqueSpeakerDistinction = attributeValue;

	}

	/** @param e */
	public void setDataElement(Element e) {
		dataElement = e;

	}

	public File getOpenFile() {
		return (openFile == null) ? new File("") : openFile;
	}

	public void setOpenFile(File openFile) {
		this.openFile = openFile;
		setRecentFile(openFile);

	}

	/** @param selectedFile */
	public void setTemplateFile(File f) {
		templates.setFile(f);
		templateFile = f;
	}

	// public String getSelectedCorpusName() {
	// return ((getSelectedCorpus() == null)
	// ? "no corpus selected"
	// : getSelectedCorpus().getAttributeValue("Name"));
	// }

	public Element getRootElement() {
		return document.getRootElement();
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document d) {
		document = d;
		setSchemaVersion(0);
	}

	public HashMap<String, String> getHiddenKeys() {
		if (hiddenKeys == null) {
			hiddenKeys = new HashMap<String, String>();
			if (getRootElement().getChildren("coma").size() > 0) {
				for (Element e : (List<Element>) getRootElement()
						.getChild("coma").getChild("hidden").getChildren()) {
					hiddenKeys.put(e.getAttributeValue("Name"), e.getValue());

				}
			}
		}
		return hiddenKeys;
	}

	public void setRootElement(Element re) {
		document.setRootElement(re);

	}

	public String getCorpusName() {
		return document.getRootElement().getAttributeValue("Name");

	}

	public void removeSubcorpora() {
		System.out.print("removing subcorpora...");
		XPath spx;
		Document newDoc = new Document();
		Element cd = new Element("CorpusData");
		newDoc.setRootElement(new Element("Corpus").addContent(cd));
		newDoc.getRootElement().addContent(new Element("Description"));
		for (Attribute a : (List<Attribute>) getRootElement().getAttributes()) {
			newDoc.getRootElement().setAttribute(a.getName(), a.getValue());
		}
		if (getRootElement().getChild("Description") != null) {
			for (Element delm : (List<Element>) getRootElement().getChild(
					"Description").getChildren()) {
				Element dk = new Element("Key");
				dk.setAttribute("Name", delm.getAttributeValue("Name"));
				dk.setText(delm.getText());
				newDoc.getRootElement().getChild("Description").addContent(dk);

			}
		}
		try {
			XPath xpsc = XPath.newInstance("//Communication|//Speaker");
			List<Element> elements = xpsc.selectNodes(getRootElement());
			for (Element sce : elements) {
				if (sce.getChild("Description") == null) {
					sce.addContent(new Element("Description"));
				}
				Element d = sce.getChild("Description");
				d.addContent(new Element("Key").setAttribute("Name",
						"@Coma-Corpus").setText(
						sce.getParentElement().getParentElement()
								.getAttributeValue("Name")));
				cd.addContent((Element) sce.clone());
			}
			document = newDoc;
			System.out.println("removed.");
			// setRootElement(newDoc.getRootElement());
		} catch (JDOMException e) {
			System.out.println("failed.");
		}

	}

	public void addSpeakers(HashSet<Element> speakers) {
		for (Element e : speakers) {
			dataElement.addContent((Element) e.clone());
		}

	}

	public String getSearchTerm() {
		if ((cfilters.size()) + (pfilters.size()) > 0) {
			return searchTerm;
		} else {
			return "";
		}
	}

	public void setSearchTerm(String text) {
		searchTerm = text;
		// TODO Auto-generated method stub

	}

	/*
	 * clears the basket
	 */
	public void clearBasket() {
		basket.clear();
	}

	/*
	 * adds a transcription to the basket
	 */
	public void addToBasket(Element transcription) {
		basket.add(transcription);
	}

	public HashSet<Element> getBasket() {
		return basket;
	}

	//
	// public void setSelectedCommunications(Vector<Element>
	// selectedCommunications) {
	// this.selectedCommunications = selectedCommunications;
	// }

	public HashMap<String, Element> getSelectedCommunications() {
		return selectedCommunications;
	}

	// public void setSelectedPersons(Vector<Element> selectedPersons) {
	// this.selectedPersons = selectedPersons;
	// }

	public HashMap<String, Element> getSelectedPersons() {
		return selectedPersons;
	}

	public Element getElementById(String target) {
		XPath xp;
		try {
			xp = XPath.newInstance("//.[@Id=\"" + target + "\"]");
			// System.out.println("XPath:" + ".[@Id=\"" + target + "\"]");
			// System.out.println("FOUND "
			// + ((Element) xp.selectSingleNode(dataElement)));
			return (Element) xp.selectSingleNode(dataElement);
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setcFilterPresets(HashMap<String, ComaFilter> cFilterPresets) {
		this.cFilterPresets = cFilterPresets;
	}

	public HashMap<String, ComaFilter> getcFilterPresets() {
		System.out.println(cFilterPresets.size());
		return cFilterPresets;
	}

	public void setsFilterPresets(HashMap<String, ComaFilter> sFilterPresets) {
		this.sFilterPresets = sFilterPresets;
	}

	public HashMap<String, ComaFilter> getsFilterPresets() {
		System.out.println(sFilterPresets.size());
		return sFilterPresets;
	}

	public void setSchemaVersion(int schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public void setSchemaVersion(String versionString) {
		this.schemaVersion = new Integer(versionString);
	}

	public int getSchemaVersion() {
		return schemaVersion;
	}

	public Set<String> getCommIdsForSelectedSpeakers() {
		HashSet<String> ids = new HashSet<String>();
		for (String s : selectedPersons.keySet()) {
			Element person = selectedPersons.get(s);
			if (person.getChild("role") != null) {
				for (Element role : (List<Element>) person.getChildren("role")) {
					ids.add(role.getAttributeValue("target"));
				}
			}
		}

		return ids;
	}

	public String getSelectedCorpusName() {
		return getCorpusName();
	}

	public HashSet<String> getRoleTypes() {
		return roleTypes;
	}

	public void setRoleTypes(HashSet<String> roleTypes) {
		this.roleTypes = roleTypes;
	}

	public HashSet<String> addRoleType(String rt) {
		if (roleTypes == null) {
			roleTypes = new HashSet<String>();
		}
		roleTypes.add(rt);
		return roleTypes;
	}

	public void setFilterChanged(String string) {
		filterChanged=string;
	}
	
	public String getFilterChanged() {
		return filterChanged;
	}
}
