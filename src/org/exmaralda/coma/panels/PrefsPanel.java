/*
 * Created on 02.11.2004 by woerner
 */
package org.exmaralda.coma.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.exmaralda.coma.resources.ResourceHandler;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.ExmaraldaFileFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * coma2/org.sfb538.coma2.panels/MyPrefsPanel.java
 * 
 * @author woerner
 * 
 */
public class PrefsPanel extends JPanel implements ActionListener {

	private JScrollPane jScrollPane = null;

	private JPanel optionsPanel = null;

	private Coma coma;

	private ButtonGroup langButtonGroup;

	private Preferences prefs = Ui.prefs;

	private JCheckBox completeBasket;

	private JPanel dirPanel = null;

	private JButton jexmaraldaDirBtn;

	private JFileChooser fc;

	private JCheckBox copySchema;

	private JCheckBox deleteConfirm;

	private JButton selectTemplatesFileButton;

	private JLabel logdirLabel;

	private JButton selectLogdirButton;

	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getOptionsPanel());
		}
		return jScrollPane;
	}

	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel();
		}
		optionsPanel.removeAll();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		// Sprachen
		optionsPanel.add(new BoldLabel(Ui.getText("languageFile")));
		addLanguages();
		// Filter
		optionsPanel.add(new BoldLabel(Ui.getText("filter")));
		optionsPanel.add(new PrefsCheckBox("prefs.newFiltersActive", true));
		optionsPanel.add(new PrefsCheckBox("prefs.newFiltersAdd", false));
		// Userinterface
		optionsPanel.add(new BoldLabel(Ui.getText("systemSpecificPrefs")));
		// optionsPanel.add(new PrefsCheckBox("cmd.debugPanel", false));
		optionsPanel.add(getDeleteConfirm());
		optionsPanel.add(new PrefsCheckBox("prefs.useNimbusLookAndFeel", false));
		optionsPanel.add(new PrefsRadioGroup("prefs.nameTranscriptionsAfter",
				new String[] { "communication", "file" }, "communication"));

		// Transcription handling
		optionsPanel.add(new BoldLabel(Ui.getText("transcription")));
		optionsPanel.add(new PrefsCheckBox(
				"cmd.copyOnlySegmentedTranscriptions", true));

		// Template-Handling
		optionsPanel.add(new BoldLabel(Ui.getText("prefs.templateHandling")));
		optionsPanel.add(new PrefsCheckBox("cmd.autoLoadTemplates", false));
		selectTemplatesFileButton = new JButton();
		String t = prefs.get("defaultTemplatesFile", "");
		if (new File(t).exists()) {
			selectTemplatesFileButton.setText(t);
		} else {
			selectTemplatesFileButton.setText(Ui
					.getText("cmd.selectDefaultTemplateFile"));
		}
		selectTemplatesFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectDefaultTemplatesFile();
			}

		});

		optionsPanel.add(selectTemplatesFileButton);
		optionsPanel.add(new PrefsCheckBox("cmd.autoSaveTemplates", false));

		// Transcription-Handling
		optionsPanel.add(new BoldLabel(Ui
				.getText("prefs.transcriptionHandling")));
		optionsPanel
				.add(new PrefsCheckBox("prefs.writeCIDsWhenUpdating", true));
		optionsPanel
				.add(new PrefsCheckBox("prefs.writeCIDWhenAssigning", true));

		// auto-update
		optionsPanel
				.add(new PrefsRadioGroup("prefs.updateCheckTimespan",
						new String[] { "timespanNever", "timespanDaily",
								"timespanWeekly", "timespanMonthly" },
						"timespanNever"));
//		optionsPanel.add(new PrefsCheckBox("prefs.updateCheckForPreviews",
//				false));

		// logging
		optionsPanel.add(new BoldLabel(Ui.getText("prefs.loggingDir")));
		selectLogdirButton = new JButton(prefs.get("LOG-Directory", System
				.getProperty("user.home")));
		selectLogdirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectLoggingDir();
			}
		});
		optionsPanel.add(selectLogdirButton);
		optionsPanel.add(new BoldLabel(Ui.getText("cmd.prefs")));
		JButton resetPrefsButton = new JButton(Ui.getText("prefs.resetPrefs"));
		resetPrefsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshPanel();
			}
		});
		optionsPanel.add(resetPrefsButton);

		return optionsPanel;
	}

	protected void selectLoggingDir() {
		JFileChooser fc = new JFileChooser(new File(prefs.get("LOG-Directory",
				System.getProperty("user.home"))), null);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int dialogStatus = fc.showDialog(coma, Ui.getText("select"));
		if (dialogStatus == 0) {
			selectLogdirButton.setText(fc.getSelectedFile().getPath());
			prefs.put("LOG-Directory", fc.getSelectedFile().getPath());
			boolean doit = true;

		}
	}

	protected void refreshPanel() {
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			System.err.println("Couldn't-care-less-exception fired...");
		}
		optionsPanel = getOptionsPanel();
		jScrollPane.setViewportView(optionsPanel);
		this.repaint();
	}

	private void selectDefaultTemplatesFile() {
		fc = new JFileChooser();
		fc.addChoosableFileFilter(new ExmaraldaFileFilter("Coma-Templates",
				new String[] { "ctf" }, true));
		int dialogStatus = fc.showOpenDialog(coma);
		if (dialogStatus == 0) {
			prefs.put("defaultTemplatesFile", fc.getSelectedFile().getPath());
			selectTemplatesFileButton.setText(fc.getSelectedFile().getPath());
		}
	}

	private void addLanguages() {
		URL languagesURL = new ResourceHandler().getResource("languages.xml");
		SAXBuilder builder = new SAXBuilder(
				"org.apache.xerces.parsers.SAXParser");
		builder.setFeature("http://apache.org/xml/features/validation/schema",
				false);
		try {
			Document langDoc = builder.build(languagesURL);
			Element rootElm = langDoc.getRootElement();
			langButtonGroup = new ButtonGroup();
			JRadioButton[] langButtons = new JRadioButton[15];
			List languages = rootElm.getChild("LanguageNames").getChildren();
			Iterator li = languages.iterator();
			int i = 0;
			while (li.hasNext()) {
				Element myLanguage = (Element) li.next();
				String langName = myLanguage.getAttributeValue("name");
				String langCode = myLanguage.getAttributeValue("code");
				langButtons[i] = new JRadioButton(langName);
				langButtons[i].setActionCommand("uiLanguage:" + langCode);
				langButtons[i].addActionListener(this);
				langButtons[i].setFont(new java.awt.Font("Dialog",
						java.awt.Font.PLAIN, 12));
				if (prefs.get("uiLanguage", "...").equals(langCode)) {
					langButtons[i].setSelected(true);
				}
				langButtonGroup.add(langButtons[i]);
				optionsPanel.add(langButtons[i]);
				i++;
			}
			if (langButtonGroup.getSelection() == null) {
				langButtons[0].setSelected(true);
			}
		} catch (Exception e) {
			coma.status("@" + e);
		}
	}

	/**
	 * This method initializes jRadioButton1
	 * 
	 * @return javax.swing.JRadioButton
	 */

	private JCheckBox getDeleteConfirm() {
		if (deleteConfirm == null) {
			deleteConfirm = new JCheckBox();
			deleteConfirm.setText(Ui.getText("option.deleteConfirm"));
			deleteConfirm.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			deleteConfirm.setActionCommand("deleteConfirm");
			deleteConfirm.setEnabled(true);
			deleteConfirm.addActionListener(this);
			deleteConfirm.setSelected(prefs.getBoolean("deleteConfirm", true));
		}
		return deleteConfirm;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getDirPanel() {
		if (dirPanel == null) {
			dirPanel = new JPanel();
			dirPanel.setLayout(new BoxLayout(dirPanel, BoxLayout.Y_AXIS));
			dirPanel
					.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			jexmaraldaDirBtn = new JButton(prefs.get("path.jexmaralda", "..."));
			jexmaraldaDirBtn.setActionCommand("chooseJexmaraldaDir");
			jexmaraldaDirBtn.addActionListener(this);
			dirPanel.add(new JLabel(Ui.getText("pathToJexmaralda")));
			dirPanel.add(jexmaraldaDirBtn, null);
		}
		return dirPanel;
	}

	/**
	 * This is the default constructor
	 */
	public PrefsPanel(Coma c) {
		super();
		coma = c;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(300, 200);
		this.add(getOptionsPanel(),java.awt.BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent evt) {
		coma.status("@" + evt.getActionCommand());
		if (Ui.getText("OK").equals(evt.getActionCommand())) {
			coma.status("OK");
		}
		if (evt.getActionCommand().startsWith("uiLanguage:")) {
			prefs.put("uiLanguage", evt.getActionCommand().substring(11));
		}
		if (evt.getActionCommand().startsWith("nameNewTranscriptionsCommName:")) {
			prefs.put("nameNewTranscriptionsCommName", evt.getActionCommand()
					.substring(30));
		}
		if (evt.getActionCommand().equals("completeBasket")) {
			prefs.putBoolean("completeBasket", (completeBasket.isSelected()));
		}
		if (evt.getActionCommand().equals("deleteConfirm")) {
			prefs.putBoolean("deleteConfirm", (deleteConfirm.isSelected()));
		}
		if (evt.getActionCommand().equals("copySchema")) {
			prefs.putBoolean("copySchema", (copySchema.isSelected()));
		}
		if (evt.getActionCommand().equals("chooseJexmaraldaDir")) {
			fc = new JFileChooser();
			fc.addChoosableFileFilter(new MyFilter());
			coma.status("@chooseJexmaraldaDir");
			if (fc.showOpenDialog(coma) == JFileChooser.APPROVE_OPTION) {
				prefs.put("path.jexmaralda", fc.getSelectedFile().getPath());
				jexmaraldaDirBtn.setText(fc.getSelectedFile().getPath());
				System.out.println("getSelectedFile() : "
						+ fc.getSelectedFile());
			} else {
				System.out.println("No Selection ");
			}
		}
	}

	class MyFilter extends javax.swing.filechooser.FileFilter {
		@Override
		public boolean accept(File file) {
			String filename = file.getName();
			if ((filename.endsWith(".jar")) | file.isDirectory()) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "*.jar";
		}
	}

	class BoldLabel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public BoldLabel(String text) {
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(new JLabel(" "));
			JLabel myLabel = new JLabel(text);
			myLabel
					.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
			this.add(myLabel);
		}
	}

	class PrefsCheckBox extends JCheckBox {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String cmd;

		public PrefsCheckBox(String command, boolean fallback) {
			super(Ui.getText(command));
			cmd = command;
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					prefs.putBoolean(((PrefsCheckBox) e.getSource()).cmd,
							((PrefsCheckBox) e.getSource()).isSelected());
				}
			});
			this.setEnabled(true);
			this.setSelected(prefs.getBoolean(command, fallback));
		}
	}

	class PrefsRadioGroup extends JPanel {

		public PrefsRadioGroup(String preference, String[] option,
				String defaultoption) {
			super();
			String defopt = (prefs.get(preference, null) == null) ? defaultoption
					: prefs.get(preference, null);
			String p = Ui.getText(preference);
			String d = Ui.getText(defopt);
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(new BoldLabel(p));
			ButtonGroup myGroup = new ButtonGroup();
			int count = 0;
			JRadioButton[] button = new JRadioButton[20];
			for (String s : option) {
				String o = Ui.getText(s);
				button[count] = new JRadioButton(o);
				if (defopt.equals(s))
					button[count].setSelected(true);
				button[count].setActionCommand(preference + "=" + s);
				button[count].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						prefs.put(e.getActionCommand().split("=")[0], e
								.getActionCommand().split("=")[1]);
					}
				});
				myGroup.add(button[count]);
				this.add(button[count]);
				count++;
			}
		}
	}
}