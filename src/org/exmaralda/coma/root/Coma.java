package org.exmaralda.coma.root;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.io.FilenameUtils;
import org.exmaralda.coma.actions.SaveAction;
import org.exmaralda.coma.datatypes.ComaDatatype;
import org.exmaralda.coma.datatypes.Speaker;
import org.exmaralda.coma.dialogs.RoleDialog;
import org.exmaralda.coma.dialogs.SpeakerSelector;
import org.exmaralda.coma.dialogs.UpdateDialogPane;
import org.exmaralda.coma.filters.ComaFilter;
import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.coma.importer.ExmaraldaPartitur;
import org.exmaralda.coma.panels.BasketPanel;
import org.exmaralda.coma.panels.CorpusPanel;
import org.exmaralda.coma.panels.DataPanel;
import org.exmaralda.coma.panels.PrefsPanel;
import org.exmaralda.coma.resources.ResourceHandler;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.helpers.DurationHelper;
import org.exmaralda.common.helpers.ExmaraldaFileFilter;
import org.exmaralda.partitureditor.sound.RecordingPropertiesCalculator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.apple.eawt.ApplicationEvent;
import java.util.prefs.BackingStoreException;

/**
 * Coma.java<br>
 * The container for everything happening.
 * coma2/org.sfb538.coma2/Coma.java
 *
 * @author woerner
 */
/**
 * @author woerner
 * 
 */
public class Coma extends JFrame implements ChangeListener,
		ExmaraldaApplication, PropertyChangeListener {

	/**
     *
     *
     */
	private static final long serialVersionUID = 1L;
	private ComaData data;
	private HashMap<String, ComaDatatype> editableItems = new HashMap<String, ComaDatatype>();
	public static Preferences prefs = Ui.prefs;
	public static final String FILE_SCHEME = "file://";
	private HashMap<String, Element> speakerIndex = new HashMap<String, Element>();
	private HashMap<String, Element> commIndex = new HashMap<String, Element>();
	// private HashSet<Element> basketHS;
	public Element nowSpeaker;
	private Document basket;
	public boolean xmlChanged = false;
	private ComaMenuBar menuBar;
	// private JWindowsTabbedPane elementTabbedPane;
	private JTabbedPane elementTabbedPane;
	private final String[] allTabs = { Ui.getText("communication"),
			Ui.getText("data"), Ui.getText("basket") };
	private CorpusPanel corpusPanel;
	public DataPanel dataPanel;
	public BasketPanel basketPanel;
	private JTextField corpusField;
	private int counter;
	public boolean filterSet;
	public Element speakerElmt;
	private Element missingElement;
	protected JTextField descName;
	private String version = "0.0";
	private Coma coma;
	private boolean allDone = false;
	protected File fileToOpen = null;
	private ProgressMonitor progressMonitor;
	private RefreshTranscriptionTask task;
	private RefreshRecordingsTask rTask;
	public static String os = (System.getProperty("mrj.version") == null ? "win"
			: "mac");
	private List<Element> transcriptions;
	private String revision;
	private List<Element> rCommunications;
	public int added;
	private boolean debugging;
	public String err;
	private boolean removeOldRecordings;
	private int recordingsRemoved;

	public Coma(String inputFile, String version, String revision,
			boolean logging) {
		super("Coma " + version);
		System.out.println(">>" + version);
		coma = this;
		data = new ComaData(coma);
		if (os.equals("mac")) {
			setupMacOSXApplicationListener();
			setupMacLookAndFeelTweaks();
		}
		this.setMinimumSize(new Dimension(800, 500));
		coma.setVersion(version);
		coma.setRevision(revision);
		if (logging) {
			System.out.println("logger enabled. logging to: "
					+ prefs.get("LOG-Directory", "unknown"));
			org.exmaralda.common.Logger.initialiseLogger(this);
		} else {
			System.out.println("logging disabled!");
			System.out.println(getData());
		}
		System.out.println("eh?");
		String className = this.getClass().getName().replace('.', '/');
		String classJar = this.getClass()
				.getResource("/" + className + ".class").toString();
		debugging = (!classJar.startsWith("jar:"));
		try {
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name", "Coma");
		} catch (Exception e) {
			status(e.toString());
		}
		getRootPane().addNotify();
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent evt) {
				quit();
			}
		});
		// comaDoc = new ComaDocument();
		data.setDocument(new Document(new Element("Corpus")));
		createUI();
		pack();

		setSize(prefs.getInt("winWidth", 900), prefs.getInt("winHeigth", 600));
		setLocation(prefs.getInt("winX", 20), prefs.getInt("winY", 50));
// yes?
		SplashScreen about = new SplashScreen(this,true);

		if (inputFile.length() > 0) {
			openComaXML(new File(inputFile));
			System.out.println("opening file " + inputFile);
		}
		if ((new File(prefs.get("defaultTemplatesFile", "")).exists())
				&& (prefs.getBoolean("cmd.autoLoadTemplates", false))) {
			if (!data.getTemplates().loadTemplates(
					new File(prefs.get("defaultTemplatesFile", "")))) {
				JOptionPane.showMessageDialog(this,
						Ui.getText("err.invalidTemplatesFile"));
				prefs.putBoolean("cmd.autoLoadTemplates", false);
				prefs.remove("defaultTemplatesFile");

			}
		}
		// ADDED BY THE EVIL TS 06-03-2009
		try {
			setIconImage(IconFactory.createImageIcon("coma64px.png").getImage());
		} catch (Throwable t) {
			System.out.println("Dann setzen wir halt kein Icon");
			t.printStackTrace();
		}
		setVisible(true);
		// auto-update-check
		String ucp = prefs.get("prefs.updateCheckTimespan", "timespanNever");
		long updateCheckTimespan = DurationHelper.timeSpans().get(ucp);
		long updateLastChecked = prefs.getLong("updateLastChecked", 0);
		long now = System.currentTimeMillis();
		if (updateCheckTimespan > 0) {
			System.out
					.println("next update check in "
							+ ((updateLastChecked + updateCheckTimespan - now) / 3600000)
							+ " hours");
			if (updateLastChecked + updateCheckTimespan < now) {
				System.out.println("auto-checking for updates...("
						+ getVersion() + "),(" + getRevision() + ")");
				System.out.println();
				try {
					UpdateDialogPane udp = new UpdateDialogPane(this,
							getVersion());
					if (udp.isUpdateAvailable()) {
						System.out.println("update available.");
						udp.setLocationRelativeTo(this);
						udp.setVisible(true);
					} else {
						udp.dispose();
						System.out.println("no update available.");
					}
				} catch (Exception e) {
					System.out.println("checking for updates failed. "
							+ e.getMessage());

				}
			}
		} else {
			Ui.prefs.putLong("updateLastChecked", 0);
			System.out.println("automatic update checks disabled");
		}
		xmlSaved(); // nothing has changed yet!
	}

	public boolean isDebugging() {
		return debugging;
	}

	public String getRevision() {
		return revision;
	}

	private void setRevision(String revision) {
		this.revision = revision;

	}

	/**
	 * creates the main program window
	 * 
	 */
	private void createUI() {
		getContentPane().setLayout(new BorderLayout());
		// elementTabbedPane = new JWindowsTabbedPane();

		elementTabbedPane = new JTabbedPane();
		elementTabbedPane.addChangeListener(this);
		dataPanel = new DataPanel(this);
		corpusPanel = new CorpusPanel(this);
		// personPanel = new PersonPanel(this);
		// createPersonPanel();
		basketPanel = new BasketPanel(this);
		JPanel prefsPanel = new PrefsPanel(this);
		// toolBar = new ComaToolBar("File", this);
		menuBar = new ComaMenuBar(this);
		this.setJMenuBar(menuBar);
		// getContentPane().add(toolBar, BorderLayout.NORTH);
		elementTabbedPane
				.addTab(Ui.getText("corpus"),
						IconFactory.createImageIcon("mail-attachment.png"),
						corpusPanel);
		elementTabbedPane.addTab(Ui.getText("data"),
				IconFactory.createImageIcon("adressbook.png"), dataPanel);
		elementTabbedPane.addTab(Ui.getText("basket"),
				IconFactory.createImageIcon("package.png"), basketPanel);
		// elementTabbedPane.addTab(Ui.getText("cmd.prefs"),
		// IconFactory.createImageIcon("preferences-system.png"),
		// prefsPanel);
		getContentPane().add(elementTabbedPane, BorderLayout.CENTER);
		toggleTab(allTabs, false);
		newCorpus();
	}

	public final void stateChanged(ChangeEvent e) {
		if (elementTabbedPane.getTitleAt(elementTabbedPane.getSelectedIndex()) == Ui
				.getText("data")) {
			dataPanel.updateLists();
			dataPanel.validate();
		} else if (elementTabbedPane.getTitleAt(elementTabbedPane
				.getSelectedIndex()) == Ui.getText("person")) {
			// personPanel.updateLists();
		} else if (elementTabbedPane.getTitleAt(
				elementTabbedPane.getSelectedIndex()).startsWith(
				Ui.getText("basket"))) {
			basketPanel.updateDisplay();
		}
	}

	/** checks whether changes needs to be saved, then exits the program */
	public void quit() {
		if (changesChecked()) {
			prefs.put("winWidth", "" + this.getWidth());
			prefs.put("winHeigth", "" + this.getHeight());
			prefs.put("winX", "" + this.getX());
			prefs.put("winY", "" + this.getY());
			if ((prefs.getBoolean("cmd.autoSaveTemplates", false))
					&& (new File(prefs.get("defaultTemplatesFile", ""))
							.exists())) {
				data.getTemplates().saveTemplates(false, this);
			}
			System.out.println("Coma ended gracefully");
			System.exit(0);
		}
	}

	/** increases a counter used on various occasions */
	public String count() {
		counter++;
		return "" + counter;
	}

	public boolean setCorpusField(String text) {
		if (corpusField.getText() != null) {
			corpusField.setText(corpusField.getText() + "\n" + text);
		} else {
			corpusField.setText(text);
		}
		return true;
	}

	/**
	 * enables or disables a tab in the elementTabbedPane
	 * 
	 * @param tabName
	 *            actual name of the tab to en-/disable
	 * @param onOff
	 *            true enables, false disables
	 * @return success (false: tab didn't exist)
	 */
	public boolean toggleTab(String tabName, boolean onOff) {
		int tabIndex = elementTabbedPane.indexOfTab(tabName);
		if (tabIndex != -1) {
			elementTabbedPane.setEnabledAt(tabIndex, onOff);
			return true;
		} else {
			return false;
		}
	}

	public boolean switchToTab(String tabName) {
		int myIndex = elementTabbedPane.indexOfTab(tabName);
		if (myIndex > -1) {
			elementTabbedPane.setSelectedIndex(elementTabbedPane
					.indexOfTab(tabName));
			return true;
		}
		return false;
	}

	/**
	 * enables or disables tabs in the elementTabbedPane
	 * 
	 * @param tabNames
	 *            String array with the actual names of the tabs to en-/disable
	 * @param onOff
	 *            true enables, false disables
	 * @return success (false: tab didn't exist)
	 */
	public boolean toggleTab(String[] tabNames, boolean onOff) {
		boolean result = true;
		for (int i = 0; i < tabNames.length; i++) {
			if (toggleTab(tabNames[i], onOff) == false) {
				result = false;
			}
		}
		return result;
	}

	/**
	 * Writes information to the statusbar
	 * 
	 * @param obj
	 *            the object that's to be examined <br>
	 *            if the object == NULL, "STATUS: NULL" will be returned.<br>
	 *            if the obj.toString() starts with "@", it will only be output
	 *            in debug-mode.<br>
	 * @param newLine
	 *            adds a newline character if true
	 * @return success (always true)
	 */
	public boolean status(Object obj, boolean newLine) {
		String text = (obj == null ? "STATUS: NULL" : obj.toString());
		System.out.println("Status: " + text);
		return true;
	}

	public boolean status(Object obj) {
		return status(obj, true);
	}

	public boolean status(double z) {
		return status("" + z, true);
	}

	public boolean status(int z) {
		return status("" + z, true);
	}

	/**
	 * Shows an error dialog
	 * 
	 * @param title
	 *            the window title to display. "error" if null.
	 * @param err
	 *            the error message
	 */
	public void error(String title, String err) {
		JOptionPane.showMessageDialog(null, (title == null ? "error" : title),
				err, JOptionPane.ERROR_MESSAGE);
	}

	public Document createEmptyCorpus(String name) {
		Element newCorp = new Element("Corpus");
		Document ec = new Document(newCorp);
		Namespace xsi = Namespace.getNamespace("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		newCorp.setAttribute("Name", name);
		newCorp.setAttribute("Id", new GUID().makeID());
		newCorp.setAttribute("noNamespaceSchemaLocation",
				"http://www.exmaralda.org/xml/comacorpus.xsd", xsi);
		newCorp.setAttribute("uniqueSpeakerDistinction",
				"//speaker/abbreviation");
		// TODO SchemaVersion
		// newCorp.setAttribute("schemaVersion", "2");

		newCorp.addContent(new Element("DBNode"));
		return ec;
	}

	public void newCorpus() {
		elementTabbedPane.setSelectedIndex(0);
		Namespace xsi = Namespace.getNamespace("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		Element newCorp = new Element("Corpus");
		data.removecfilter(null);
		data.removepfilter(null);
		newCorp.setAttribute("Name", "unnamed corpus");
		newCorp.setAttribute("Id", new GUID().makeID());
		newCorp.setAttribute("noNamespaceSchemaLocation",
				"http://www.exmaralda.org/xml/comacorpus.xsd", xsi);
		newCorp.setAttribute("uniqueSpeakerDistinction",
				"//speaker/abbreviation");
		// TODO: schemaVersion
		// newCorp.setAttribute("schemaVersion", "2");
		newCorp.addContent(new Element("DBNode"));
		newCorp.addContent(new Element("CorpusData"));
		data.setRootElement(newCorp);
		data.setOpenFile(null);
		data.setUniqueSpeakerDistinction(data.getRootElement()
				.getAttributeValue("uniqueSpeakerDistinction"));
		corpusPanel.showValues();
		dataPanel.reset();
		toggleTab(true, true);
		allDone = true;
		if (fileToOpen != null) {
			openComaXML(fileToOpen);
		}
	}

	public Document getComaDocument(File xmlFile) {
		if (xmlFile != null) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			URL url = new ResourceHandler().schemaURL();
			SAXBuilder builder = new SAXBuilder(
					"org.apache.xerces.parsers.SAXParser");
			builder.setFeature(
					"http://apache.org/xml/features/validation/schema", true);
			if (url.toString().indexOf(".jar!") > 0) {
				builder.setProperty(
						"http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
						"jar:" + url.getFile());
			} else {
				builder.setProperty(
						"http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
						url.getFile());
			}
			builder.setEntityResolver(new SchemaLoader());
			boolean parsed = false;
			Document tempDoc = null;
			if (xmlFile.exists() != false) {
				try {
					tempDoc = builder.build(xmlFile);
					parsed = true;
					this.setCursor(Cursor.DEFAULT_CURSOR);
					return tempDoc;
				} catch (Exception e) {
					status(e.getMessage());
					status("xml operation failed: ", false);
					JOptionPane.showMessageDialog(
							this,
							xmlFile.getName() + " "
									+ Ui.getText("err.invalidComaDocument")
									+ "\n" + e.getMessage(),
							Ui.getText("XMLerror"), JOptionPane.ERROR_MESSAGE);
				}
			} else {
				status("File not Found!");
				error(xmlFile.getName() + " not found.", "File not found error");
				data.removeRecentFile(xmlFile);
			}
		}
		this.setCursor(Cursor.DEFAULT_CURSOR);
		return null;
	}

	/**
	 * Opens and parses a coma document
	 * 
	 * @param xmlFile
	 *            the java.io.File to open
	 */
	/**
	 * @param xmlFile
	 * @return
	 */
	public boolean openComaXML(File xmlFile) {
		System.out.println("open");
		Document tempDoc = getComaDocument(xmlFile);

		if (tempDoc != null) {
			data.setDocument(tempDoc);
			data.setOpenFile(xmlFile);
			// updateMenuBar();
			// if (hasSubcorpora()) {
			// System.out.println("has!");
			// data.removeSubcorpora();
			// corpusPanel.showValues();
			// dataPanel.reset();
			// elementTabbedPane.setSelectedIndex(0);
			// } else {
			// corpusPanel.showValues();
			// dataPanel.reset();
			// elementTabbedPane.setSelectedIndex(0);
			// }
		}
		corpusPanel.showValues();
		dataPanel.reset();
		elementTabbedPane.setSelectedIndex(0);
		resetAllTabs();
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		return (tempDoc != null);
	}

	// private boolean hasSubcorpora() {
	// XPath spx;
	// List<Element> subcorpora = null;
	// try {
	// spx = XPath.newInstance("//Corpus'");
	// subcorpora = spx.selectNodes(data.getRootElement());
	// } catch (JDOMException e) {
	// System.out.println("ERROR: checking for subcorpora failed.");
	// e.printStackTrace();
	// }
	// return (subcorpora.size() > 1);
	// }

	/**
	 * @param description
	 *            Description Element from where the key should be read
	 * @param keyname
	 *            Name of the key to get the value from
	 * @return returns the value of the key
	 */
	public String getDescriptionValue(Element description, String keyname) {
		List keys = description.getChildren();
		Iterator i = keys.iterator();
		while (i.hasNext()) {
			Element myKey = (Element) i.next();
			if (myKey.getAttributeValue("Name").equals(keyname)) {
				return myKey.getText();
			}
		}
		return null;
	}

	/**
	 * @param description
	 *            Description Element in whicht to set the key
	 * @param keyname
	 *            name of the key
	 * @param keyvalue
	 *            value to set
	 * @return key's value before the change. null if it didn't exist
	 */
	public String setDescriptionValue(Element description, String keyname,
			String keyvalue) {
		List keys = description.getChildren();
		Iterator i = keys.iterator();
		while (i.hasNext()) {
			Element myKey = (Element) i.next();
			if (myKey.getAttributeValue("Name").equals(keyname)) {
				String keyToReturn = myKey.getText();
				myKey.setText(keyvalue);
				return keyToReturn;
			}
		}
		Element myKey = new Element("Key");
		myKey.setAttribute("Name", keyname);
		myKey.setText(keyvalue);
		description.addContent(myKey);
		return null;
	}

	/**
	 * selects the corpus-tab and deletes all old information in the other tabs
	 */
	private void resetAllTabs() {
		data.clearBasket();
		basketUpdated();
		elementTabbedPane.setSelectedIndex(1);
	}

	public void sizeIt(JComponent c, int width, int heigth) {
		Dimension myDimension = new Dimension(width, heigth);
		c.setMaximumSize(myDimension);
		c.setMinimumSize(myDimension);
		c.setPreferredSize(myDimension);
	}

	public void btnSize(JButton[] btn, int width, int heigth) {
		for (int i = 0; i < btn.length; i++) {
			sizeIt(btn[i], width, heigth);
			btn[i].setHorizontalAlignment(JButton.LEFT);
			btn[i].setToolTipText(btn[i].getLabel());
		}
	}

	// private JPanel createRecordingPanel() {
	// JPanel panel = new JPanel();
	// panel.setLayout(new BorderLayout());
	// recordingText = new JTextArea("Juhuu");
	// panel.add(recordingText, BorderLayout.CENTER);
	// return panel;
	// }
	// public Document getDocument() {
	// return comaDoc;
	// }
	private void setVersion(String v) {
		version = v;
	}

	public void newSpeaker() {
	}

	public void addDescriptionToLocation(Element elmt) {
		missingElement = new Element("Description");
		elmt.addContent(missingElement);
	}

	public void updateValueDisplay() {
		String activeTabName = elementTabbedPane.getTitleAt(elementTabbedPane
				.getSelectedIndex());
		if (activeTabName == Ui.getText("data")) {
			dataPanel.updateLists(true);
			// dataPanel.showSelectedValues();
		}
	}

	// public void dumpNode(Element e) {
	// Format format = Format.getPrettyFormat();
	// XMLOutputter outputter = new XMLOutputter(format);
	// }

	/**
	 * en/disables all Tabs
	 * 
	 * @param b
	 *            allTabs?
	 * @param c
	 *            enable/disable
	 */
	public void toggleTab(boolean b, boolean c) {
		if (b = true) {
			toggleTab(allTabs, c);
		}
	}

	/**
	 * @param newComm
	 */
	public void newCommunication(Element newComm) {
		data.disableCommFilters();
		dataPanel.updateLists();
		System.out.println("wie bin ich denn HIERHER gekommen?!");
	}

	public void xmlChanged() {
		xmlChanged = true;
		this.getRootPane().putClientProperty("Window.documentModified",
				Boolean.TRUE);
	}

	public void addToBasket(Element e) {
		HashSet<Element> transcriptionsToAdd = new HashSet<Element>();
		if (e.getName().equals("Communication")) {
			List<Element> transcriptions = e.getChildren("Transcription");
			for (Element te : transcriptions) {
				if (prefs.getBoolean("cmd.copyOnlySegmentedTranscriptions",
						true)) {
					if (coma.getDescriptionValue(te.getChild("Description"),
							"segmented") != null) {
						if (coma.getDescriptionValue(
								te.getChild("Description"), "segmented")
								.equals("true")) {
							transcriptionsToAdd.add(te);
						}
					}
				} else {
					transcriptionsToAdd.add(te);
				}
			}
		} else if (e.getName().equals("Transcription")) {
			if (prefs.getBoolean("cmd.copyOnlySegmentedTranscriptions", true)) {
				if (coma.getDescriptionValue(e.getChild("Description"),
						"segmented") != null) {
					if (coma.getDescriptionValue(e.getChild("Description"),
							"segmented").equals("true")) {
						transcriptionsToAdd.add(e);
					}
				}
			}
		}
		for (Element myT : transcriptionsToAdd) {
			data.addToBasket(myT);
		}
		basketUpdated();
	}

	/**
	 * @return
	 */
	// public HashSet<Element> getBasketHS() {
	// return basketHS;
	// }
	/**
	 * builds a new corpus (JDOM-Document) from the transcriptions in the basket
	 * 
	 * @return new JDOM Document containing the corpus
	 */
	public boolean addElementToBasket(Element elm) {
		HashSet myPersons = new HashSet();
		if (basket == null) {
			basket = createEmptyCorpus("basket");
			basket.getRootElement().addContent(new Element("CorpusData"));
		}
		Element basketRoot = basket.getRootElement().getChild("CorpusData");
		if (elm.getName() == "Transcription") {
			Element nowComm = (Element) elm.getParent();
			basketRoot.addContent(nowComm.detach());
			status("@communication " + nowComm.getAttributeValue("Name")
					+ "/Person:");
			Iterator pI = nowComm.getDescendants(new ElementFilter("Person"));
			while (pI.hasNext()) {
				String pID = ((Element) pI.next()).getText();
				status(pID + ",", false);
				if (!myPersons.contains(pID)) {
					myPersons.add(pID);
				}
			}
			if (myPersons.size() > 0) {
				HashSet sElms = new HashSet();
				Iterator sI = data.getRootElement().getDescendants(
						new ElementFilter("Speaker"));
				while (sI.hasNext()) {
					Element nowSp = (Element) sI.next();
					String spID = nowSp.getAttributeValue("Id");
					if (myPersons.contains(spID)) {
						addSpeakerToCorpusData(basketRoot, nowSp);
					}
				}
			}
			status("");
		} else if (elm.getName() == "Communication") {
		} else if (elm.getName() == "Speaker") {
		}
		return false;
	}

	/**
	 * @param basketRoot
	 * @param mySpeaker
	 */
	private void addSpeakerToCorpusData(Element cd, Element spk) {
		cd.addContent(spk);
	}

	public Document buildCorpusFromBasket() {
		HashSet<String> myPersons = new HashSet<String>();
		HashSet<String> myCommunications = new HashSet<String>();
		Element newCorp = new Element("Corpus");
		Document basketDoc = new Document(newCorp);
		if (data.getBasket().size() < 1) {
			return null;
		}
		if (elementTabbedPane.getSelectedIndex() > 0) {
			elementTabbedPane.setSelectedIndex(0);
		}
		Namespace xsi = Namespace.getNamespace("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		newCorp.setAttribute("Name", "corpusBasket from "
				+ data.getRootElement().getAttributeValue("Name"));
		newCorp.setAttribute("Id", new GUID().makeID());
		newCorp.setAttribute("noNamespaceSchemaLocation",
				"http://www.exmaralda.org/xml/comacorpus.xsd", xsi);
		newCorp.setAttribute("uniqueSpeakerDistinction", data.getRootElement()
				.getAttributeValue("uniqueSpeakerDistinction"));
		newCorp.addContent(new Element("DBNode"));
		Element cd = new Element("CorpusData");
		newCorp.addContent(cd);
		Iterator bi = data.getBasket().iterator();
		while (bi.hasNext()) {
			Element nowComm = (Element) ((Element) bi.next()).getParent();
			if (!myCommunications.contains(nowComm.getAttributeValue("Id"))) {
				myCommunications.add(nowComm.getAttributeValue("Id"));
				cd.addContent((Element) nowComm.clone());
				Iterator pI = nowComm
						.getDescendants(new ElementFilter("Person"));
				while (pI.hasNext()) {
					String pID = ((Element) pI.next()).getText();
					if (!myPersons.contains(pID)) {
						myPersons.add(pID);
					}
				}
			}
		}
		if (myPersons.size() > 0) {
			HashSet<Element> sElms = new HashSet<Element>();
			Iterator sI = data.getRootElement().getDescendants(
					new ElementFilter("Speaker"));
			while (sI.hasNext()) {
				Element nowSp = (Element) sI.next();
				String spID = nowSp.getAttributeValue("Id");
				if (myPersons.contains(spID)) {
					sElms.add(nowSp);
				}
			}
			for (Element e : sElms) {
				Element cc = (Element) e.clone();
				cd.addContent(cc);
			}
		}
		return basketDoc;
	}

	public String absolutized(String relPathString) {
		URI comaDocParent = data.getOpenFile().getParentFile().toURI();
		URI abs = comaDocParent.resolve(relPathString);
		return new File(abs).getAbsolutePath();
	}

	/**
	 * returns a relative path starting from the document's location
	 * 
	 * @param absPathString
	 * @return
	 */
	public String relativized(String absPathString) {
		URI comaDocParent = data.getOpenFile().getParentFile().toURI();
		URI abs;
		try {
			abs = new URI(absPathString);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return absPathString;
		}
		URI relURL = comaDocParent.relativize(abs);
		return relURL.toString();
	}

	public String getRelativePath(File from, File to) {
		if (from == null) {
			from = data.getOpenFile().getParentFile();
		}
		URI fromURI = from.toURI();
		URI toURI = to.toURI();
		URI relativeURI = fromURI.relativize(toURI);
		return relativeURI.toString();
	}

	/**
	 * @return
	 */
        @Override
	public String getVersion() {
		return version;
	}

	public HashMap<String, Element> getSpeakerIndex() {
		return speakerIndex;
	}

	public void setSpeakerIndex(HashMap<String, Element> speakerIndex) {
		this.speakerIndex = speakerIndex;
	}

	public HashMap<String, Element> getCommIndex() {
		return commIndex;
	}

	public void setCommIndex(HashMap<String, Element> commIndex) {
		this.commIndex = commIndex;
	}

	/**
	 * updates the basket-tab with the number of transcriptions in the basket
	 * 
	 */
	public void basketUpdated() {
		elementTabbedPane.setTitleAt(2, Ui.getText("basket") + " ("
				+ data.getBasket().size() + ")");
	}

	public void resetEditableItems() {
		editableItems.clear();
	}

	public String addEditableItem(ComaDatatype d) {
		String id = new GUID().makeID();
		editableItems.put(id, d);
		return id;
	}

	public ComaDatatype getEditableElement(String theId) {
		return editableItems.get(theId);
	}

	public ComaData getData() {
		return data;
	}

	/**
	 * @param id
	 */
	public void setCommFilter(String xp, boolean fromPreset) {
		if (fromPreset) {
			data.addRawCFilter(xp);
		} else {
			data.addcfilter(xp);
		}
		data.setFilterChanged("comm");
		dataPanel.updateLists();
	}

	/**
	 * @param id
	 */
	public void setSpeakerFilter(String xp, boolean fromPreset) {
		if (fromPreset) {
			data.addRawPFilter(xp);
		} else {
			data.addpfilter(xp);

		}
		data.setFilterChanged("spk");
		dataPanel.updateLists();
	}

	/**
	 * @param id
	 */
	public void setComm2ndColumn(String id) {
		dataPanel.updateRenderer(id, true);
	}

	public void handleAboutComa() {
		SplashScreen about = new SplashScreen(this,false);
	}

	public void handlePrefs() {
		this.switchToTab(Ui.getText("cmd.prefs"));
	}

	public void handleQuit() {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				quit();
			}
		});
		throw new IllegalStateException("Let the quit handler do it");
	}

	/**
     *
     */
	public void xmlSaved() {
		xmlChanged = false;
		this.getRootPane().putClientProperty("Window.documentModified",
				Boolean.FALSE);
	}

	public void setOpenFile(File selectedFile) {
		data.setOpenFile(selectedFile);
		updateMenuBar();
		updateTitle();
		xmlSaved();
	}

	public void updateMenuBar() {
		menuBar.updateFileMenu();
	}

	public void updateTitle() {

		this.setTitle("EXMARaLDA Coma | "
				+ ((data.getOpenFile() != null) ? data.getOpenFile().getName()
						: "new file") + " | Corpus: "
				+ data.getRootElement().getAttributeValue("Name"));
	}

	@Override
	public String getApplicationName() {
		return "Corpus Manager (Coma)";
	}

	@Override
	public String getPreferencesNode() {
		return "org.exmaralda.coma";
	}
        
        @Override
        public void resetSettings(){
            try {
                java.util.prefs.Preferences.userRoot().node(getPreferencesNode()).clear();                
                JOptionPane.showMessageDialog(rootPane, "Preferences reset.\nRestart the editor.");
            } catch (BackingStoreException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(rootPane, "Problem resetting preferences:\n" + ex.getLocalizedMessage());
            }        
        }
        

	@Override
	public ImageIcon getWelcomeScreen() {
		return IconFactory.createImageIcon("splash.png");
	}

	private void setupMacOSXApplicationListener() {
		try {
			final com.apple.eawt.Application application = com.apple.eawt.Application
					.getApplication();
			application.setEnabledAboutMenu(true); // damit ein "Über " Menü
			// erscheint
			application.addPreferencesMenuItem(); // "Einstellen..." Dialog
			application.setEnabledPreferencesMenu(true); // diesen Dialog auch
			// freischalten
			application
					.addApplicationListener(new com.apple.eawt.ApplicationListener() {

						public void handleAbout(
								com.apple.eawt.ApplicationEvent ae) {
							handleAboutComa();
							ae.setHandled(true); // habe fertig...
						}

						// coma wird über den finder geöffnet. wie auch sonst.
						// (lies: total unnütz!)
						public void handleOpenApplication(ApplicationEvent ae) {
						}

						public void handlePreferences(ApplicationEvent ae) {
							handlePrefs();
							ae.setHandled(true);
						}

						public void handlePrintFile(ApplicationEvent ae) {
							System.out.println("Drucken?!");
							// weiss nicht, wo man das tatsächlich auslösen kann
							ae.setHandled(true);
						}

						public void handleQuit(ApplicationEvent ae) {
							quit();
							ae.setHandled(true); // da wird wohl nichts mehr
							// draus!
						}

						// coma läuft bereits und jemand startet es nochmal
						public void handleReOpenApplication(ApplicationEvent ae) {
							// völlig blödsinniger handler.
						}

						// anwendung läuft schon, dokument wird über den finder
						// geöffnet
						public void handleOpenFile(ApplicationEvent ae) {
							if (!allDone) {
								fileToOpen = new File(ae.getFilename());
							} else {
								openComaXML(new File(ae.getFilename()));
							}
						}
					});
		} catch (Throwable e) {
			// bummsti. nicht schlimm.
		}
	}

	private void setupMacLookAndFeelTweaks() {
		// Font for OptionPanes
		String css = "<head>" + "<style type=\"text/css\">"
				+ "b { font: 13pt \"Lucida Grande\" }"
				+ "p { font: 11pt \"Lucida Grande\"; margin-top: 8px }"
				+ "</style>" + "</head>";
		UIManager.put("OptionPane.css", css);
		// Icon for OptionPanes
		ImageIcon appIcon = IconFactory.createImageIcon("coma64px.png");
		UIManager.put("OptionPane.errorIcon", appIcon);
		UIManager.put("OptionPane.informationIcon", appIcon);
		UIManager.put("OptionPane.questionIcon", appIcon);
		UIManager.put("OptionPane.warningIcon", appIcon);
	}

	public boolean isSaved() {
		if (!data.getOpenFile().exists()) {
			JOptionPane
					.showMessageDialog(this, Ui.getText("err.notSavedTitle"));
			return false;
		} else {
			return true;
		}
	}

	public void importSpeaker(File f) {
		File file = null;
		if (f != null) {
			file = f;
		} else {
			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new ExmaraldaFileFilter("Coma-File",
					new String[] { "coma" }, true));
			int dialogStatus = fc.showOpenDialog(coma);
			file = fc.getSelectedFile();
		}
		Document tempDoc = getComaDocument(file);
		if (tempDoc != null) {
			SpeakerSelector sps = new SpeakerSelector(this, tempDoc);
			sps.setLocationRelativeTo(this);
			sps.setVisible(true);
			if (sps.getSpeakers().size() > 0) {
				data.addSpeakers(sps.getSpeakers());
			}
			corpusPanel.showValues();
			dataPanel.reset();
			elementTabbedPane.setSelectedIndex(0);
		}
		return;

	}

	public void refreshTranscriptionStats() {
		if (data.getOpenFile().exists()) {
			transcriptions = null;
			XPath trx;
			try {
				trx = XPath.newInstance("//Transcription");
				transcriptions = trx.selectNodes(data.getDataElement());
			} catch (JDOMException err) {
				err.printStackTrace();
			}
			progressMonitor = new ProgressMonitor(this,
					Ui.getText("countingSegments"), "", 0, 100);
			progressMonitor.setProgress(0);
			task = new RefreshTranscriptionTask();
			task.addPropertyChangeListener(this);
			task.execute();
		} else {
			JOptionPane.showMessageDialog(this,
					Ui.getText("err.noCorpusLoaded"));
		}
	}

	public void updateRecordings() {
		if (data.getOpenFile().exists()) {
			int n = JOptionPane.showConfirmDialog(this,
					Ui.getText("option.removeFirst"),
					Ui.getText("option.removeFirstTitle"),
					JOptionPane.YES_NO_OPTION);
			removeOldRecordings = (n == JOptionPane.YES_OPTION);
			recordingsRemoved = 0;
			if (removeOldRecordings) {
				HashSet<Element> recsToRemove = new HashSet<Element>();
				Iterator<Element> ri = data.getDataElement().getDescendants(
						new ElementFilter("Recording"));
				while (ri.hasNext()) {
					recsToRemove.add(ri.next());
				}
				for (Element re : recsToRemove) {
					re.getParent().removeContent(re);
					recordingsRemoved++;
				}
			}

			rCommunications = data.getDataElement()
					.getChildren("Communication");
			progressMonitor = new ProgressMonitor(this,
					Ui.getText("updatingRecordings"), "", 0, 100);
			progressMonitor.setProgress(0);
			rTask = new RefreshRecordingsTask();
			rTask.addPropertyChangeListener(this);
			rTask.execute();
		} else {
			JOptionPane.showMessageDialog(this,
					Ui.getText("err.noCorpusLoaded"));
		}
	}

	class RefreshRecordingsTask extends SwingWorker<Void, Void> {

		@Override
		public void done() {
			progressMonitor.setProgress(0);
			progressMonitor.close();
			JOptionPane.showMessageDialog(
					coma,
					(recordingsRemoved > 0 ? (recordingsRemoved + " "
							+ Ui.getText("msg.recordingsRemoved") + "\n") : "")
							+ added
							+ " "
							+ Ui.getText("msg.recordingsAdded")
							+ "." + err);
		}

		@Override
		protected Void doInBackground() throws Exception {
			int trcount = 0;
			int progress = 0;
			added = 0;
			err = "";
			setProgress(0);
			File theFile = null;
			ExmaraldaPartitur myPartitur = null;
			for (Element comm : rCommunications) {
				if (isCancelled()) {
					break;
				}
				progress = Math.round((100 * (trcount / (float) rCommunications
						.size())));
				trcount++;
				if (progress == 100) {
					progress = 99;
				}
				setProgress(progress);
				if (comm.getChildren("Transcription").size() == 0) {
					continue;
				}
				List<Element> transkriptionen = (List<Element>) comm
						.getChildren("Transcription");

				HashSet<File> existingFiles = new HashSet<File>();
				HashSet<File> filesToAdd = new HashSet<File>();
				List<Element> recs = comm.getChildren("Recording");
				Element newRec = null;
				for (Element transe : transkriptionen) {
					File transcriptionFile = new File(coma.absolutized(transe
							.getChild("NSLink").getText()));
					if (!(transcriptionFile.exists())) {
						continue;
					}
					myPartitur = new ExmaraldaPartitur(transcriptionFile,
							Coma.prefs.getBoolean(
									"prefs.writeCIDsWhenUpdating", true));
					if (myPartitur.getMediaFiles().size() == 0) {
						continue;
					}
					for (String mediaFile : myPartitur.getMediaFiles()) {
						theFile = new File("");
						try {
							URI trDocParent = transcriptionFile.getParentFile()
									.toURI();
							URI mediaAbs = trDocParent.resolve(mediaFile);
							theFile = new File(mediaAbs);
							filesToAdd.add(theFile);
						} catch (Exception e) {
							err += "\n" + transe.getChildText("Name")
									+ " references " + mediaFile
									+ " (invalid);";
						}
						if (!(theFile.exists())) {
							filesToAdd.remove(theFile);
							continue;
						}
						for (Element rec : recs) {
							List<Element> medias = rec.getChildren("Media");
							for (Element media : medias) {
								if (media.getChild("NSLink") == null) {
									continue;
								}
								URI comaParent = coma.getData().getOpenFile()
										.getParentFile().toURI();
								URI coMedAbs = comaParent.resolve(media
										.getChildText("NSLink"));
								File comaFile = new File(coMedAbs);
								if (comaFile.equals(theFile)) {
									if (!removeOldRecordings) {
										filesToAdd.remove(theFile);
									}
								}
							}
						}
					}
					if (filesToAdd.size() > 0) {
						newRec = new Element("Recording");
						String elementName = FilenameUtils
								.removeExtension(filesToAdd.iterator().next()
										.getName());
						newRec.addContent(new Element("Name")
								.setText(elementName));
						newRec.setAttribute("Id", "R" + new GUID().makeID());
						for (File f : filesToAdd) {
							if (!(existingFiles.contains(f))) {
								Element newMedia = new Element("Media");
								newMedia.setAttribute("Id",
										"M" + new GUID().makeID());
								Element newDescType = new Element("Key");
								newDescType.setAttribute("Name", "Type");
								newDescType.setText("digital");
								Element newDesc = new Element("Description");
								newDesc.addContent(newDescType);
								newMedia.addContent(newDesc);
								Element newNSLink = new Element("NSLink");
								newNSLink
										.setText(coma.getRelativePath(null, f));
								newMedia.addContent(newNSLink);
								newRec.addContent(newMedia);
								if (newRec.getChildren("RecordingDuration")
										.size() == 0) {
									if (f.getName().endsWith(".wav")) {
										double dur = RecordingPropertiesCalculator
												.getRecordingDuration(f);
										if (dur >= 0) {
											newRec.addContent(new Element(
													"RecordingDuration").setText(Long.toString((long) (Math
													.round(dur * 1000.0)))));
										}
									}
								}
								existingFiles.add(f);
							}
						}
					}
				}
				if (newRec != null) {
					comm.addContent(newRec);
					added++;
				}
			}
			if (added > 0) {
				coma.xmlChanged();
			}
			return null;
		}
	}

	class RefreshTranscriptionTask extends SwingWorker<Void, Void> {

		@Override
		public void done() {
			// Toolkit.getDefaultToolkit().beep();
			progressMonitor.setProgress(0);
			progressMonitor.close();
			System.out.println("task done()");
		}

		@Override
		public Void doInBackground() {
			int trcount = 0;
			int progress = 0;
			setProgress(0);
			for (Element element : transcriptions) {
				if (isCancelled()) {
					break;
				}
				progress = Math.round((100 / (float) transcriptions.size())
						* trcount);
				trcount++;
				if (progress == 100) {
					progress = 99;
				}
				setProgress(progress);
				if (element.getChild("Filename") != null) {
					ExmaraldaPartitur myPartiture = null;
					int count = 1;
					myPartiture = new ExmaraldaPartitur(new File(
							coma.absolutized(element.getChild("NSLink")
									.getText())), coma.prefs.getBoolean(
							"prefs.writeCIDsWhenUpdating", true));
					while (!myPartiture.isValid() && count < 10) {
						// System.out.print(count + ",");
						myPartiture = new ExmaraldaPartitur(new File(
								coma.absolutized(element.getChild("NSLink")
										.getText())), coma.prefs.getBoolean(
								"prefs.writeCIDsWhenUpdating", true));
						count++;
					}
					if (myPartiture.isSegmented()) {
						List descKeys;
						Element myKey;
						HashMap segments = myPartiture.getSegmentCount();
						Iterator sI = segments.entrySet().iterator();
						boolean set = false;
						while (sI.hasNext()) {
							Map.Entry entry = (Map.Entry) sI.next();
							Object key = entry.getKey();
							Object value = entry.getValue();
							descKeys = element.getChild("Description")
									.getChildren();
							Iterator keyI = descKeys.iterator();
							while (keyI.hasNext()) {
								myKey = (Element) keyI.next();
								if (myKey.getAttributeValue("Name").equals(
										"# " + key)) {
									myKey.setText(value.toString());
									set = true;
								}
							}
							if (!set) {
								Element newKey = new Element("Key");
								newKey.setAttribute("Name", "# " + key);
								newKey.setText(value.toString());
								element.getChild("Description").addContent(
										newKey);
							}
							set = false;
						}
					}
				}
				// System.out.println(trcount + "/" + transcriptions.size());
			}
			coma.xmlChanged();
			return null;
		}
	}

	/**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			String message = progress + "%";

			progressMonitor.setNote(message);
			if (evt.getSource() == task) {
				if (progressMonitor.isCanceled() || task.isDone()) {
					if (progressMonitor.isCanceled()) {
						task.cancel(true);
						System.out.println("task cancelled");
					} else {
						System.out.println("task completed");
					}
				}

			} else {
				if (progressMonitor.isCanceled() || rTask.isDone()) {
					if (progressMonitor.isCanceled()) {
						rTask.cancel(true);
						System.out.println("rTask cancelled");
					} else {
						System.out.println("rTask completed");
					}
				}
			}
		}

	}

	public void refreshDisplay(boolean b) {
		dataPanel.updateLists(b);
	}

	public boolean changesChecked() {
		if (xmlChanged) {
			int n = JOptionPane.showConfirmDialog(coma,
					Ui.getText("q.saveChanges"), Ui.getText("unsavedChanges"),
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				return (new SaveAction(coma).save());
			} else if (n == JOptionPane.NO_OPTION) {
				return true;
			}
			return false;

		} else {
			return true;
		}

	}

	public void selectRoleTargets(String id) {
		editableItems.get(id).showTargets();
	}

	public void saveFilter(ComaFilter f) {
		System.out.println(f.getXPath() + " soll ein preset werden");
		if (f.getFilterType() == ComaFilter.COMMTYPE) {
			if (data.getcFilterPresets().containsValue(f))
				return;
			{ // schon da?
				boolean exists = true;
				String s = "";
				while (exists) {
					exists = false;
					s = (String) JOptionPane.showInputDialog(this,
							"Wie soll er heissen?", "Filter preset",
							JOptionPane.PLAIN_MESSAGE, null, null,
							f.getDisplayName());
					if (data.getcFilterPresets().containsKey(s))
						exists = true;
				}

				if (s.length() < 1)
					return;
				data.getcFilterPresets().put(s, f);
			}
		} else {
			if (data.getsFilterPresets().containsValue(f))
				return;
			{ // schon da?
				boolean exists = true;
				String s = "";
				while (exists) {
					exists = false;
					s = (String) JOptionPane.showInputDialog(this,
							"Wie soll er heissen?", "Filter preset",
							JOptionPane.PLAIN_MESSAGE, null, null,
							f.getDisplayName());
					if (data.getsFilterPresets().containsKey(s))
						exists = true;
				}

				if (s.length() < 1)
					return;
				data.getsFilterPresets().put(s, f);
			}

		}
	}

	/**
	 * @param removeSettings
	 */
	public void introduceRoles(boolean removeSettings) {
		if (data.getDocument() != null) {
			if (data.getRootElement().getDescendants(new ElementFilter("role"))
					.hasNext()) {
				JOptionPane.showMessageDialog(this,
						"There seem to be roles in this document already.",
						"Ouch!", JOptionPane.WARNING_MESSAGE);
				return;
			} else {
				System.out.println("no roles anywhere");
				Iterator sI = data.getRootElement().getDescendants(
						new ElementFilter("Setting"));
				Vector<Element> settings = new Vector<Element>();
				while (sI.hasNext()) {

					Element setting = (Element) sI.next();
					settings.add(setting);
					for (Element e : (List<Element>) setting
							.getChildren("Person")) {
						XPath spx;
						try {
							spx = XPath.newInstance("//Speaker[@Id='"
									+ e.getText() + "']");
							Element speaker = (Element) spx
									.selectSingleNode(data.getRootElement());
							Element role = new Element("role");
							role.setAttribute("Type", "Participant");
							role.setAttribute("target", setting
									.getParentElement().getAttributeValue("Id"));
							speaker.addContent(role);
						} catch (JDOMException jde) {
							System.out.println("argh!");
						}

					}

				}
				if (removeSettings) {
					System.out.println("killing settings");
					for (Element e : settings) {
						// TODO: Rescue descriptions into the comm-description
						// TODO: Make settings optional
						e.getParent().removeContent(e);
					}
				}
				JOptionPane.showMessageDialog(this, "Roll over, Coma!",
						"Done.", JOptionPane.INFORMATION_MESSAGE);

			}

		} else {
			return;
		}
	}

	public int getSchemaVersion() {
		System.out.println("SchemaVersion:" + data.getSchemaVersion());
		return data.getSchemaVersion();
	}

	public void setSchemaVersion(String attributeValue) {
		data.setSchemaVersion(attributeValue);
	}

	public boolean hasRoles() {
		return (data.getSchemaVersion() > 0);
	}

	/**
	 * display html in a dialog
	 * 
	 * @param html
	 */
	public void showInfoDialog(String html) {
		JDialog d = new JDialog(); // nicht modal
		JTextPane t = new JTextPane();
		t.setContentType("text/plain");
		t.setEditorKit(new HTMLEditorKit());
		t.setText(html);
		d.add(new JScrollPane(t));
		d.setSize(600, 500);
		d.setLocationRelativeTo(this);
		d.setVisible(true);
	}

	public void editRoles(String id) {
		System.out.println("edit roles for " + editableItems.get(id) + " ("
				+ editableItems.get(id).getRoleCount() + " roles)");
		RoleDialog dialog = new RoleDialog(this,
				(Speaker) editableItems.get(id));
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

}
