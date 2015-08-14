package org.exmaralda.sextanttagger.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.helpers.ColorHelper;
import org.exmaralda.common.helpers.ExmaraldaFileFilter;
import org.exmaralda.exakt.search.swing.KWICTableEvent;
import org.exmaralda.exakt.search.swing.KWICTableListener;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader;
import org.exmaralda.sextanttagger.types.TagOption;
import org.exmaralda.sextanttagger.types.TagOptionDialog;
import org.exmaralda.sextanttagger.ui.TaggerUI;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

public class TaggerApplication
		implements
			ExmaraldaApplication,
			KWICTableListener {
	public static final int EXMARALDA_SEGMENTED_TRANSCRIPTION = 0;
	public static final int TEI_TEXT = 1;
	private int resourceType;
	private Preferences prefs;
	boolean transcriptionLoaded = false;

	public TaggerApplication() {
		super();
		org.exmaralda.common.Logger.initialiseLogger(this);
		exsFile = null;
		exsDoc = null;
		speakers = new HashMap<String, String>();
		segments = new HashSet<String>();
		tagSegments = new HashMap();
		taggables = new Vector<Element>();
		showSegments = Arrays.asList(new Element("null"));
		tagOptions = new HashSet<TagOption>();
		prefs = Preferences.userRoot().node(getPreferencesNode());
	}

	public Preferences getPrefs() {
		return prefs;
	}

	TaggerUI ui;
	private File exsFile;
	private Document exsDoc;
	private HashMap<String, String> speakers;
	private HashSet<String> segments;
	private String showSegment;
	private XPath showXPath;
	private String tagSegment;
	private XPath tagXPath;
	private List<Element> showSegments;
	private String display;
	private String displayString = "";
	private String speaker = ".*";
	private int segmentIndex = 0;
	private HashMap<Element, HashSet<TagOption>> tagSegments;
	private int tagIndex = 0;
	private Element segmentShowing;
	private Vector<Element> taggables;

	public Vector<Element> getTaggables() {
		return taggables;
	}

	private Vector<Element> showables;
	private HashSet<TagOption> tagOptions;
	private Object elementToTag;
	private boolean showingDialog;
	private TagOptionDialog d;
	private File sourceFile = new File("");
	private Namespace xlink = Namespace.getNamespace("xlink",
			"http://www.w3.org/1999/xlink");
	private Namespace xsi;
	private Namespace xml;
	private HashMap<String, Element> idMap;

	public HashSet<TagOption> getTagOptions() {
		return tagOptions;
	}

	// public int getIndexForOption(TagOption option) {
	// for (String s : tagOptions.keySet()) {
	// if (tagOptions.get(s).equals(option)) {
	// return tagOptions.get(s).getIndex();
	// }
	// }
	// return -1;
	// }

	public Document getExsDoc() {
		return exsDoc;
	}

	public File getExsFile() {
		return exsFile;
	}

	public HashMap<String, String> getSpeakers() {
		return speakers;
	}

	public HashSet<String> getSegments() {
		return segments;
	}

	@Override
	public String getApplicationName() {
		return "Sextant Tagger";
	}

	@Override
	public String getPreferencesNode() {
		return "org.exmaralda.sextant";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	@Override
	public ImageIcon getWelcomeScreen() {
		return null;
	}
        
        @Override
        public void resetSettings(){
            try {
                java.util.prefs.Preferences.userRoot().node(getPreferencesNode()).clear();                
                JOptionPane.showMessageDialog(null, "Preferences reset.\nRestart the editor.");
            } catch (BackingStoreException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Problem resetting preferences:\n" + ex.getLocalizedMessage());
            }        
        }
        

	public void openTranscription(File f) {
		ui.setCursor(Cursor.WAIT_CURSOR);
		File file = (File) null;
		if ((f == null) || (!f.exists())) {
			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new ExmaraldaFileFilter(
					"EXMARaLDA Segmented Transcriptions", new String[]{"exs",
							"xml", "exa"}, true));
			int dialogStatus = fc.showOpenDialog(ui);
			if (dialogStatus == 0) {
				file = fc.getSelectedFile();

			}

		} else {
			file = f;
		}
		if (file != null) {
			Document doc = getDocumentFromXMLFile(file);
			if (doc != null) {
				Element root = doc.getRootElement();
				ui.setSourceFile(file);
				sourceFile = file;
				exsFile = file;
				exsDoc = doc;
				if (root.getName().equals("segmented-transcription")) {
					resourceType = EXMARALDA_SEGMENTED_TRANSCRIPTION;
					identifySegments();
					idMap = createIdMap();
				} else if (root.getName().equals("TEI")) {
					removeNamespace(exsDoc);
					resourceType = TEI_TEXT;
					identifySegments();
				}
				ui.updateHeader();
				ui.updateDropdowns();
				prefs.put("lastOpenedTranscription", file.getAbsolutePath());
				if (root.getChild("annotation") != null) {
					doThings(root.getChild("annotation"), null);
					// TODO doThings umbenennen, gucken, ob ein Tagset drinsteckt.
					System.out.println("annotated!");
				}
			}
		}
		ui.setCursor(Cursor.DEFAULT_CURSOR);

	}

	private HashMap<String, Element> createIdMap() {
		idMap = new HashMap<String, Element>();
		try {
			XPath x = XPath.newInstance("//ts");
			for (Element e : (List<Element>) x.selectNodes(exsDoc
					.getRootElement())) {
				idMap.put(e.getAttributeValue("id"), e);
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idMap;
	}
	private void identifySegments() {
		speakers.clear();
		segments.clear();
		if (resourceType == EXMARALDA_SEGMENTED_TRANSCRIPTION) {
			try {
				XPath x = XPath.newInstance("//speaker");
				for (Element e : (List<Element>) x.selectNodes(exsDoc
						.getRootElement())) {
					speakers.put(e.getChild("abbreviation").getText(),
							e.getAttributeValue("id"));
				}
				x = XPath.newInstance("//ts");
				for (Element e : (List<Element>) x.selectNodes(exsDoc
						.getRootElement())) {
					segments.add(e.getAttributeValue("n"));
				}
				ui.updateDropdowns();

			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (resourceType == TEI_TEXT) {
			try {
				// XPath x = XPath.newInstance("//speaker");
				// for (Element e : (List<Element>) x.selectNodes(exsDoc
				// .getRootElement())) {
				// speakers.put(e.getChild("abbreviation").getText(), e
				// .getAttributeValue("id"));
				// }
				XPath x = XPath.newInstance("//text//node()");
				x.addNamespace(exsDoc.getRootElement().getNamespace());
				for (Content e : (List<Content>) x.selectNodes(exsDoc
						.getRootElement())) {
					if (e.getClass().equals(Element.class)) {
						segments.add(((Element) e).getName());
					}
				}
				ui.updateDropdowns();

			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Document getDocumentFromXMLFile(File f) {
		Document doc = null;
		SAXBuilder parser = new SAXBuilder();
		try {
			doc = parser.build(f);
		} catch (JDOMException e) {
			System.err.println("failed building XML from input file");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("failed opening input file");
		}
		return doc;
	}

	public void setUi(TaggerUI f) {
		ui = f;
		// TODO Auto-generated method stub

	}

	public void setShowSegment(String s) {
		showSegment = s;
		valuesChanged();
		// TODO Auto-generated method stub

	}

	private void valuesChanged() {
		if (resourceType == EXMARALDA_SEGMENTED_TRANSCRIPTION) {
			try {
				String s = "//segmented-tier[@speaker"
						+ (speaker.length() > 0 ? "='" + speakers.get(speaker)
								+ "']" : "]") + "//ts[@n='" + showSegment
						+ "']";
				showXPath = XPath.newInstance(s);
				String t = s + "//ts[@n='" + tagSegment + "']";
				showSegments = showXPath.selectNodes(exsDoc);
				tagIndex = segmentIndex = 0;
				if (showSegments.size() > 0) {
					segmentShowing = showSegments.get(segmentIndex);
					tagXPath = XPath.newInstance(t);
					tagSegments.clear();
					List<Element> l = tagXPath.selectNodes(exsDoc);
					for (Element e : l) {
						tagSegments.put(e, new HashSet<TagOption>());
					}
					showSegment(false);
					ui.labelNavigation(showSegment, tagSegment);
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			}
		} else if (resourceType == TEI_TEXT) {
			try {
				String s = "//text/" + showSegment;
				showXPath = XPath.newInstance(s);
				String t = s + "//" + tagSegment;
				showSegments = showXPath.selectNodes(exsDoc);
				tagIndex = segmentIndex = 0;
				if (showSegments.size() > 0) {
					segmentShowing = showSegments.get(segmentIndex);
					tagXPath = XPath.newInstance(t);
					tagSegments.clear();
					List<Element> l = tagXPath.selectNodes(exsDoc);
					for (Element e : l) {
						tagSegments.put(e, new HashSet<TagOption>());
					}
					showSegment(false);
					ui.labelNavigation(showSegment, tagSegment);
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			}
		}

	}

	private void showSegment(boolean fromRwdTag) {
		if (exsDoc != null) {
			if (showSegments.size() > 0) {
				taggables = new Vector<Element>();
				showables = new Vector<Element>();
				segmentShowing = showSegments.get(segmentIndex);
				Iterator<Element> i = segmentShowing
						.getDescendants(new ElementFilter());
				while (i.hasNext()) {
					Element e = i.next();
					showables.add(e);
					if (tagSegments.keySet().contains(e)) {
						taggables.add(e);
					}
				}
				if (fromRwdTag) {
					tagIndex = taggables.size() - 1;
				}
				displayString = "["
						+ segmentShowing.getAttributeValue("id")
						+ "|"
						+ segmentShowing.getAttributeValue("s")
						+ "-"
						+ segmentShowing.getAttributeValue("e")
						+ "]<br/><p style='font: 18px sans-serif;'><strong>"
						+ (resourceType == EXMARALDA_SEGMENTED_TRANSCRIPTION
								? getSpeaker(segmentShowing)
								: "") + ":</strong> ";
				elementToTag = null;
				for (Element e : showables) {
					displayString += "<span style='";
					if (taggables.contains(e)) {
						if (tagSegments.get(e).size() > 0) {
							HashMap<String, TagOption> keys = new HashMap<String, TagOption>();
							for (TagOption to : tagSegments.get(e)) {
								keys.put(to.getKey(), to);
							}
							if (keys.keySet().contains(ui.getSelectedKey())) {
								// String ms = ui.getSelectedKey()
								// + "="
								// + tagSegments.get(e).get(
								// ui.getSelectedKey());
								int ind = 0;

								displayString += "color:"
										+ ColorHelper
												.getHTMLColor(keys.get(
														ui.getSelectedKey())
														.getColor()) + "; ";
							}

						}
						if (taggables.get(tagIndex) == e) { // taggable element!
							displayString += "text-decoration: underline;";
							elementToTag = e;
							ui.updateInfoTable(tagSegments.get(e));
						}
					} else {
						displayString += "color: #aaaaaa;";
					}
					displayString += "'>" + e.getText();
					if ((e.getName().equals("nts")) && (e.getText() == null)) {
						displayString += " ";

					}
					displayString += "</span>";
				}
				displayString += "</p>";

			} else {
				displayString = "<h2>No segments to show</h2>";
			}
			ui.updateDisplay();
		}
	}

	private String getSpeaker(Element segmentShowing2) {
		Element e = segmentShowing2;
		while (e.getAttributeValue("speaker") == null) {
			e = e.getParentElement();
		}
		return (e.getAttributeValue("speaker") == null ? "" : e
				.getAttributeValue("speaker"));
	}

	public int getTagShowing() {
		return tagIndex;
	}

	public void setSegmentShowing(int pos) {
		segmentIndex = pos;
		tagIndex = 0;
		showSegment(false);
	}

	public void setTagindex(int pos) {
		tagIndex = pos;
		showSegment(false);

	}

	public List getShowSegments() {
		return showSegments;
	}

	public String getTagSegment() {
		return tagSegment;
	}

	public int getSegmentShowing() {
		return segmentIndex;
	}

	public HashMap getTagSegments() {
		return tagSegments;
	}

	public String getDisplayString() {
		return displayString;
	}

	public void setSpeaker(String s) {
		speaker = (s.equals("any speaker") ? "" : s);
		valuesChanged();
	}

	public void setTagSegment(String t) {
		tagSegment = t;
		ui.setTagSegment(t);
		valuesChanged();

	}

	public void fwdShow() {
		if (segmentIndex < showSegments.size() - 1) {
			segmentIndex++;
			tagIndex = 0;
			if (segmentIndex == showSegments.size() - 1) {
				// disable fwd-button
			}
			showSegment(false);
		}
	}

	public void rwdShow(boolean fromRwdTag) {
		if (segmentIndex > 0) {
			segmentIndex--;
			tagIndex = 0;
			System.out.println("nulled");
			if (segmentIndex == 0) {
				// disable rwd-button
			}
			showSegment(fromRwdTag);
		}
	}

	public void rwdTag() {
		if (tagIndex > 0) {
			tagIndex--;
			showSegment(false);
		} else {
			rwdShow(true);
		}
	}

	public void fwdTag() {
		if (tagIndex < taggables.size() - 1) {
			tagIndex++;
			showSegment(false);
		} else {
			tagIndex = 0;
			fwdShow();
		}
	}

	public void editTaggingOption(TagOption o, boolean newOption) {
		d = new TagOptionDialog(ui, o);
		setShowingDialog(true);
		d.setLocationRelativeTo(ui);

		d.setVisible(true);
		setShowingDialog(false);
		if (d.isCancelled()) {
			return;
		} else {
			if (d.getOption().getKey().length() == 0) {
				if (newOption) {
					tagOptions.remove(o);
					return;
				} else {
					int ans1 = JOptionPane.showConfirmDialog(
							null,
							"Remove all annotations\n" + o.getKey() + "="
									+ o.getValue() + "?",
							"Remove annotations?",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (ans1 == JOptionPane.CANCEL_OPTION) {
						return;
					} else if (ans1 == JOptionPane.NO_OPTION) {
						tagOptions.remove(o);
						ui.updateTagOptions();
						return;
					} else {
						for (HashSet<TagOption> h : tagSegments.values()) {
							if (h.contains(o)) {
								h.remove(o);
							}
						}
						tagOptions.remove(o);
						ui.updateTagOptions();
						return;
					}
				}
			}
			if (o.equals(d.getOption())) {
				o.setColor(d.getOption().getColor());
				o.setShortcut(d.getOption().getShortcut());
			} else {
				if (!newOption) {
					int ans = JOptionPane.showConfirmDialog(null,
							"Change all elements tagged\n" + o.getKey() + "="
									+ o.getValue() + "\nto\n"
									+ d.getOption().getKey() + "="
									+ d.getOption().getValue() + "?",
							"Change annotations?",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (ans == JOptionPane.YES_OPTION
							|| ans == JOptionPane.NO_OPTION) {
						if (ans == JOptionPane.YES_OPTION) {
							for (HashSet<TagOption> h : tagSegments.values()) {
								if (h.contains(o)) {
									h.remove(o);
									h.add(d.getOption());
								}
							}
						}
						tagOptions.remove(o);
						o = d.getOption();
						tagOptions.add(o);

					}
				} else {
					tagOptions.remove(o);
					o = d.getOption();
					tagOptions.add(o);

				}
			}

			ui.updateTagOptions();
		}

	}

	public void addTaggingOption() {
		TagOption nto = new TagOption();
		tagOptions.add(nto);
		editTaggingOption(nto, true);
	}

	public void tagElement(TagOption o) {
		if (elementToTag != null) {
			HashSet<TagOption> removers = new HashSet<TagOption>();
			for (TagOption to : tagSegments.get(elementToTag)) {
				if (o.getKey().equals(to.getKey())) {
					removers.add(to);
				}
			}
			for (TagOption to : removers) {
				tagSegments.get(elementToTag).remove(to);
			}
			tagSegments.get(elementToTag).add(o);
			if (ui.getAutoAdvance()) {
				fwdTag();
			} else {
				showSegment(false);
			}
		}
	}
	private void removeTag(TagOption o) {
		HashSet<TagOption> removers = new HashSet<TagOption>();
		for (TagOption to : tagSegments.get(elementToTag)) {
			if (o.getKey().equals(to.getKey())) {
				removers.add(to);
			}
		}
		for (TagOption to : removers) {
			tagSegments.get(elementToTag).remove(to);
		}
		if (ui.getAutoAdvance()) {
			fwdTag();
		} else {
			showSegment(false);
		}

	}

	public void saveESA(File file) {
		File f = null;
		if (file == null) {

			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new ExmaraldaFileFilter(
					"EXMARaLDA Standoff Annotation", new String[]{"esa"}, true));
			int dialogStatus = fc.showSaveDialog(ui);
			if (dialogStatus == 0) {
				f = fc.getSelectedFile();

			}
			if (f != null) {
				if (!f.getName().endsWith(".exa")) {
					f = new File(f.getAbsolutePath() + ".exa");
				}
				file = f;
			} else {
				return;
			}

			Format format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			XMLOutputter op = new XMLOutputter();
			op.setFormat(format);
			try {
				FileOutputStream out = new FileOutputStream(file);
				op.output(getSextantDocument(true), out);
				out.close();

				if (ui.getChangeTranscriptionsRadio().isSelected()) {
					Element mde = exsDoc.getRootElement().getChild("head")
							.getChild("meta-information")
							.getChild("ud-meta-information");
					Element afe = new Element("ud-information");
					afe.setAttribute("attribute-name", "@sextant-annotation");
					afe.setText(file.getName());
					mde.addContent(afe);
					out = new FileOutputStream(exsFile);
					op.output(exsDoc, out);
					out.close();

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * SEXTANT PROTOTYPE!
	 * 
	 * @param fullHeader
	 * 
	 * @return a sextant-document containing the annotations for the first tag
	 */
	public Document getSextantDocument(boolean fullHeader) {
		xsi = Namespace.getNamespace("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		xml = Namespace.getNamespace("xml",
				"http://www.w3.org/XML/1998/namespace");

		// root element, namespaces and attributes
		Element r = new Element("annotation");
		r.addNamespaceDeclaration(xlink);
		r.addNamespaceDeclaration(xsi);
		r.addNamespaceDeclaration(xml);
		r.setAttribute("noNamespaceSchemaLocation",
				"http://xml.exmaralda.org/sextant.xsd", xsi);

		//
		String tag = "pos";
		//

		r.setAttribute("base", exsFile.getName(), xml);
		r.setAttribute("target", exsFile.getName());
		r.setAttribute("id", exsFile.getName() + "_" + tag);
		r.setAttribute("targetId",
				(exsDoc.getRootElement().getAttributeValue("id") != null)
						? exsDoc.getRootElement().getAttributeValue("id")
						: "n/a");
		r.setAttribute("type", tag + "-annotation from sextant tagger");
		Document nd = new Document(r);
		Element desc = new Element("description");
		Element k = new Element("Key");
		k.setAttribute("Name", "created");
		k.setText(new Date().toLocaleString());
		desc.addContent(k);
		k = new Element("Key");
		k.setAttribute("Name", "showSegment");
		k.setText(showSegment);
		desc.addContent(k);
		k = new Element("Key");
		k.setAttribute("Name", "tagSegment");
		k.setText(tagSegment);
		desc.addContent(k);
		r.addContent(desc);
		int annCount = 0;
		for (Element e : tagSegments.keySet()) {
			if (tagSegments.get(e).size() > 0) {
				Element ann = new Element("ann");
				Attribute target = new Attribute("href", "#"
						+ e.getAttributeValue("id"));
				target.setNamespace(xlink);
				ann.setAttribute(target);
				ann.setAttribute("id", exsFile.getName().replace(".", "_")
						+ "_" + tag + "_" + annCount);
				Element fs = new Element("fs");
				ann.addContent(fs);
				for (TagOption to : tagSegments.get(e)) {
					Element f = new Element("f");
					f.setAttribute("name", to.getKey());
					f.addContent(new Element("symbol").setAttribute("value",
							to.getValue()));
					fs.addContent(f);
				}
				r.addContent(ann);
				annCount++;
			}
		}
		return nd;
	}

	public int getOptionsCount() {
		return tagOptions.size();
	}

	public HashSet<String> getKeys() {
		HashSet<String> keys = new HashSet<String>();
		for (TagOption to : tagOptions) {
			keys.add(to.getKey());
		}
		return keys;
	}

	public void refresh() {
		showSegment(false);
	}

	public Document getTagsetDocument() {
		Document tsDoc = new Document();
		Element r = new Element("sextant-tagset");
		tsDoc.setRootElement(r);
		// for (String k : tagOptions) {
		for (TagOption to : tagOptions) {
			String key = to.getKey();
			String value = to.getValue();
			Element fs = new Element("fs");
			Element f = new Element("f");
			f.setAttribute("name", key);
			Element s = new Element("symbol");
			s.setAttribute("value", value);
			f.addContent(s);
			Element sf = new Element("f");
			sf.setAttribute("name", "sextantShortcut");
			Element sfs = new Element("symbol");
			sfs.setAttribute("value", to.getShortcut());
			sf.addContent(sfs);
			Element cf = new Element("f");
			cf.setAttribute("name", "sextantColor");
			Element cfs = new Element("symbol");
			cfs.setAttribute("value", "" + to.getColor().getRGB());
			cf.addContent(cfs);

			fs.addContent(f);
			fs.addContent(sf);
			fs.addContent(cf);
			r.addContent(fs);
		}
		return tsDoc;
	}
	public void saveTagset(Object object) {
		Document tsDoc = getTagsetDocument();
		File file;
		File f = null;
		HashMap<String, String> filetypes = new HashMap();
		filetypes.put("tagset", "Sextant-Tagset");
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new ExmaraldaFileFilter("Sextant tagset",
				new String[]{"tagset"}, true));
		int dialogStatus = fc.showSaveDialog(ui);
		if (dialogStatus == 0) {
			f = fc.getSelectedFile();

		}

		if (f != null) {
			file = f;
			if (!file.getName().toLowerCase().endsWith(".tagset")) {
				file = new File(file.getAbsolutePath() + ".tagset");
			}
			if (file.exists()) {
				if (!overwriteFileDialog(file)) {
					return;
				}
			}
		} else {
			return;
		}
		Format format = Format.getRawFormat();
		format.setEncoding("UTF-8");
		XMLOutputter op = new XMLOutputter();
		op.setFormat(format);
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			op.output(tsDoc, out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean overwriteFileDialog(File file) {
		int ans = JOptionPane.showConfirmDialog(null, "" + file.getName()
				+ " exists. Overwrite?", "Save Over Existing File",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (ans == JOptionPane.OK_OPTION)
			return true;

		return false;
	}

	public void openESA(File fi) {
		File file = null;
		if (fi == null) {

			JFileChooser fc = new JFileChooser(prefs.get("recentDir", null));
			fc.addChoosableFileFilter(new ExmaraldaFileFilter(
					"EXMARaLDA Standoff-Annotation", new String[]{"esa"}, true));
			int dialogStatus = fc.showOpenDialog(ui);
			if (dialogStatus == 0) {
				file = fc.getSelectedFile();
				prefs.put("recentDir", file.getParent());

			}

		} else {
			file = fi;
		}
		if (file != null) {
			Document doc = getDocumentFromXMLFile(file);
			if (doc != null) {
				Element root = doc.getRootElement();
				if (root.getName().equals("annotation")) {
					if (!root.getAttributeValue("target").equals(
							sourceFile.getName())) {
						int ans = JOptionPane
								.showConfirmDialog(
										null,
										""
												+ file.getName()
												+ " annotates "
												+ root.getAttributeValue("target")
												+ ".\nDo you want to open the annotated file?",
										"Open annotated file?",
										JOptionPane.OK_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE);
						if (ans == JOptionPane.OK_OPTION) {
							openTranscription(new File(file.getParent()
									+ File.separator
									+ root.getAttributeValue("target")));
						}

					}

					doThings(root, file);
				}

			} else {
				JOptionPane.showMessageDialog(ui, file.getName()
						+ " is not a standoff annotation file.");
				return;

			}
		}

	}

	private void doThings(Element annotationRoot, File file) {
		Element root = annotationRoot;
		for (Element k : (List<Element>) root.getChild("description")
				.getChildren("Key")) {
			if (k.getAttributeValue("Name").equals("tagSegment")) {
				setTagSegment(k.getText());
			}
			if (k.getAttributeValue("Name").equals("showSegment")) {
				setShowSegment(k.getText());
			}
		}
		if (tagSegments.size() > 0) {
			int found = 0;
			int notFound = 0;
			HashMap<String, Element> idMap = new HashMap<String, Element>();
			for (Element e : tagSegments.keySet()) {
				idMap.put(e.getAttributeValue("id"), e);
			}
			for (Element annE : (List<Element>) root.getChildren("ann")) {
				for (Element fe : (List<Element>) annE.getChildren("fs")) {

					for (Element f : (List<Element>) fe.getChildren("f")) {
						TagOption to = new TagOption(f);
						if (!tagOptions.contains(to))
							tagOptions.add(to);
						if (idMap.containsKey(annE.getAttributeValue("href",
								xlink).substring(1))) {
							if (tagSegments.get(
									idMap.get(annE.getAttributeValue("href",
											xlink).substring(1))).size() == 0) {
								tagSegments.put(
										idMap.get(annE.getAttributeValue(
												"href", xlink).substring(1)),
										new HashSet<TagOption>());

							}
							for (TagOption too : tagOptions) {
								if (to.equals(too)) {
									tagSegments
											.get(idMap
													.get(annE
															.getAttributeValue(
																	"href",
																	xlink)
															.substring(1)))
											.add(too);
								}
							}

							found++;
						} else {
							notFound++;
						}
					}
				}

			}
			if (notFound > 0) {
				JOptionPane
						.showMessageDialog(
								ui,
								file.getName()
										+ " contains annotations that don't\nmatch any segment in "
										+ sourceFile.getName() + ".\n"
										+ "Discarded " + notFound
										+ " annotations!",
								"Annotation mismatch!",
								JOptionPane.WARNING_MESSAGE);
			}
			ui.updateTagOptions();

		}

	}

	public void openTagset(File fi) {
		File file = null;
		if (fi == null) {

			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new ExmaraldaFileFilter(
					"Sextant-Tagset",
					new String[]{"xml","tagset"}, true));
			int dialogStatus = fc.showOpenDialog(ui);
			if (dialogStatus == 0) {
				file = fc.getSelectedFile();

			}

		} else {
			file = fi;
		}
		if (file != null) {
			Document doc = getDocumentFromXMLFile(file);
			if (doc != null) {
				//
				Element root = doc.getRootElement();
				if (root.getName().equals("sextant-tagset")) {
					HashSet<TagOption> newOptions = new HashSet<TagOption>();
					for (Element fs : (List<Element>) root.getChildren()) {
						TagOption to = new TagOption(fs,true);
						newOptions.add(to);
					}
					for (TagOption old : tagOptions) {
						if (newOptions.contains(old)) {
							for (TagOption neo : newOptions) {
								if (old.equals(neo)) {
									old.setColor(neo.getColor());
									old.setShortcut(neo.getShortcut());
								}
							}
						} else {
							tagOptions.remove(old);
						}
					}
					for (TagOption neo : newOptions) {
						if (!tagOptions.contains(neo)) {
							tagOptions.add(neo);
						}
					}
					ui.updateTagOptions();
					prefs.put("lastOpenedTagset", file.getAbsolutePath());
				}
			}
		}

	}

	public void setShowingDialog(boolean showingDialog) {
		this.showingDialog = showingDialog;
	}

	public boolean isShowingDialog() {
		return showingDialog;
	}

	public TagOptionDialog getDialog() {
		return d;
	}

	public void removeTagOption(String k) {
		tagOptions.remove(k);
	}

	public JPanel getRemovePanel(final TagOption last) {
		JPanel p = new JPanel();
		JButton remButton = new JButton("Remove Annotation for "
				+ last.getKey());
		remButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				removeTag(last);
			}

		});
		p.add(remButton);
		p.add(Box.createHorizontalGlue());
		return p;
	}

	public JPanel getOptionPanel(final TagOption po) {
		if (po != null) {
			JPanel p = new JPanel();
			p.setOpaque(true);
			p.setBackground(po.getColor());
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			JButton editButton = new JButton("?");
			editButton.putClientProperty("JButton.buttonType", "segmented");
			editButton.putClientProperty("JButton.segmentPosition", "first");
			editButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					editTaggingOption(po, false);
				}
			});
			p.add(editButton);
			JButton b = new JButton(po.getKey() + "=" + po.getValue());
			b.putClientProperty("JButton.buttonType", "segmented");
			b.putClientProperty("JButton.segmentPosition", "last");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					tagElement(po);
				}
			});

			p.add(b);
			JLabel shortcutLabel = new JLabel(" ");
			shortcutLabel.setOpaque(false);
			shortcutLabel.setForeground(new Color(255 - po.getColor().getRed(),
					255 - po.getColor().getGreen(), 255 - po.getColor()
							.getBlue()));
			int modifier = new Integer(po.getShortcut().split(";")[1])
					.intValue();
			int skey = new Integer(po.getShortcut().split(";")[0]).intValue();
			if (modifier + skey > 0) {
				if (modifier < 1) {
					shortcutLabel.setText(KeyEvent.getKeyText(skey));
				} else {
					shortcutLabel.setText(KeyEvent
							.getKeyModifiersText(modifier)
							+ "+"
							+ KeyEvent.getKeyText(skey));
				}
			}
			p.add(Box.createHorizontalGlue());
			p.add(shortcutLabel);
			return p;
		} else {
			return null;
		}
	}

	public void testStanfordTagging() {
		// TaggerConfig tf;
		// try {
		// // MaxentTagger tagger = new MaxentTagger();
		// // tf = new TaggerConfig(null);
		// // // tf.
		// // tagger.tagFromXML(tf);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	private void removeNamespace(Document doc2) {
		Iterator i = doc2.getRootElement().getDescendants();
		while (i.hasNext()) {
			Content c = (Content) i.next();
			if (c.getClass().equals(Element.class)) {
				((Element) c).setNamespace(Namespace.NO_NAMESPACE);
				for (Attribute a : (List<Attribute>) ((Element) c)
						.getAttributes()) {
					a.setNamespace(Namespace.NO_NAMESPACE);
				}
			}

		}

	}

	public void saveEXA(File file) {
		boolean saveTagset = false;
		File f = null;
		if ((file == null) || (!file.exists())) {
			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new ExmaraldaFileFilter(
					"Annotated EXMARaLDA Transcription", new String[]{"exa"},
					true));
			fc.setAccessory(new SaveTagsetAccessory());
			int dialogStatus = fc.showSaveDialog(ui);
			if (dialogStatus == 0) {
				f = fc.getSelectedFile();
				saveTagset = (((SaveTagsetAccessory) fc.getAccessory())
						.getSaveTagset());
			}

			if (f != null) {
				if (!f.getName().endsWith(".exa")) {
					f = new File(f.getAbsolutePath() + ".exa");
				}
				file = f;
			} else {
				return;
			}

		} else {
			f = file;
		}

		Element annotationElmt = (Element) getSextantDocument(true)
				.getRootElement().clone();
		Document annotatedEXSdocument = (Document) exsDoc.clone();
		annotatedEXSdocument.getRootElement().addContent(annotationElmt);
		if (saveTagset) {
			annotatedEXSdocument.getRootElement().addContent(
					(Element) getTagsetDocument().getRootElement().clone());
		}
		Format format = Format.getRawFormat();
		format.setEncoding("UTF-8");
		XMLOutputter op = new XMLOutputter();
		op.setFormat(format);
		FileOutputStream out;
		try {
			out = new FileOutputStream(f);
			op.output(annotatedEXSdocument, out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// added by evil TS - 01-03-2011
	public void exaktSearch() throws SAXException, IOException,
			JexmaraldaException, ParserConfigurationException,
			TransformerConfigurationException, TransformerException,
			JDOMException {
		SegmentedTranscriptionSaxReader saxReader = new SegmentedTranscriptionSaxReader();
		SegmentedTranscription st = saxReader.readFromFile(exsFile
				.getAbsolutePath());
		System.out.println("Segmented transcription read from "
				+ exsFile.getAbsolutePath());
		File segFile = File.createTempFile("EXMARaLDA_s", ".xml");
		segFile.deleteOnExit();
		st.writeXMLToFile(segFile.getAbsolutePath(), "none");
		System.out.println("Segmented transcription written to "
				+ segFile.getAbsolutePath());

		// create COMA corpus and write it to temp file
		File corpusFile = File.createTempFile("EXMARaLDA_corpus", ".xml");
		corpusFile.deleteOnExit();
		org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory();
		String comaString = sf
				.applyInternalStylesheetToString(
						"/org/exmaralda/partitureditor/jexmaralda/xsl/BasicTranscription2Coma.xsl",
						st.toBasicTranscription().toXML());
		Document comaDocument = org.exmaralda.exakt.utilities.FileIO
				.readDocumentFromString(comaString);
		List l = XPath.newInstance("//*[@id='fillinfilename']").selectNodes(
				comaDocument);
		for (Object o : l) {
			Element e = (Element) o;
			e.setText(segFile.getName());
			e.removeAttribute("id");
		}
		org.exmaralda.exakt.utilities.FileIO.writeDocumentToLocalFile(
				corpusFile, comaDocument);
		System.out.println("Temporary corpus file written to "
				+ corpusFile.getAbsolutePath());

		// setup the EXAKT panel
		final org.exmaralda.exakt.exmaraldaSearch.COMACorpus comaCorpus = new org.exmaralda.exakt.exmaraldaSearch.COMACorpus();
		comaCorpus.readCorpus(corpusFile);

		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				displayKwicPanel(comaCorpus);
			}
		});

	}

	private void displayKwicPanel(
			org.exmaralda.exakt.exmaraldaSearch.COMACorpus comaCorpus) {
		org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanel kwicPanel = new org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanel(
				comaCorpus);
		kwicPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		kwicPanel.setSize(new java.awt.Dimension(800, 600));
		kwicPanel.splitPane.setDividerLocation((int) Math.round(0.8 * 600.0));

		kwicPanel.addKWICTableListener(this);

		// put the EXAKT panel in a dialog and display it
		JDialog dialog = new JDialog((JFrame) (ui), false);
		dialog.setTitle("EXAKT search");
		dialog.getContentPane().add(kwicPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(ui);
		dialog.setVisible(true);
	}

	public void processEvent(KWICTableEvent ev) {
		if (ev.getType() != KWICTableEvent.DOUBLE_CLICK) {
			return;
		}
		System.out.println("Received KWICTableEvent");
		org.exmaralda.exakt.search.SimpleSearchResult searchResult = (org.exmaralda.exakt.search.SimpleSearchResult) (ev
				.getSelectedSearchResult());
		String xpath = (String) (searchResult.getSearchableSegmentLocator()
				.getSearchableSegmentLocator());
		// segmentation[@name='SpeakerContribution_Event']/ts)[14]
		// this is pfusch, but it will work for Kerem
		xpath = xpath.replace("SpeakerContribution_Event",
				"SpeakerContribution_Utterance_Word");
		try {
			Element sc = (Element) (XPath.newInstance(xpath)
					.selectSingleNode(exsDoc));
			String id = sc.getAttributeValue("id");
			ui.jumpToSegmentChain(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public HashMap<String, Element> getIdMap() {
		return idMap;
	}

	public void visualize(Object object) {

	}

	public void about() {
		SplashScreen about = new SplashScreen(ui, false);

	}

	public void help() {
		try {
			Desktop.getDesktop()
					.browse(new URI(
							"http://www.exmaralda.org/sextant/sextanttagger.pdf"));
		} catch (IOException err) {
			err.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public class SaveTagsetAccessory extends JPanel {

		private JCheckBox saveTagsetCB;

		public SaveTagsetAccessory() {
			setLayout(new BorderLayout());
			JPanel p = new JPanel();
			saveTagsetCB = new JCheckBox("Save Tagset in File");
			// saveTagsetCB.addItemListener(new ItemListener() {
			//
			// public void itemStateChanged(ItemEvent e) {
			// if (e.getStateChange() == ItemEvent.SELECTED) {
			// System.out.println("checkbox is selected");
			// } else {
			// System.out.println("checkbox is NOT selected");
			// }
			// }
			// });
			p.add(saveTagsetCB);
			add(p, BorderLayout.CENTER);
		}
		public boolean getSaveTagset() {
			return saveTagsetCB.isSelected();
		}
	}
	

}
