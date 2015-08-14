/**
 *
 */
package org.exmaralda.teide.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.MenuBar;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.exmaralda.teide.Teide;
import org.exmaralda.teide.HTML.TeideHTML;
import org.exmaralda.teide.file.TeideFileWriter;
import org.exmaralda.teide.renderers.FileListRenderer;
import org.exmaralda.teide.xml.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.xml.sax.SAXException;

/**
 * @author woerner
 * 
 */
public class TeideUI extends JFrame implements WindowListener {
	private static final int STATUS_IDLE = 0;

	private static final int STATUS_PROCESSING = 1;

	private static final int STATUS_OK = 2;

	private static final int STATUS_ERROR = 3;

	private static final int STATUS_NOT_TRANSFORMED = 4;

	private TeideData data;

	private HashMap<File, Vector<File>> filesToTransform;

	//	private Vector<File> filesToTransform;

	private JTextPane displayPane;

	private JPanel fileSelectPanel;

	private SAXBuilder builder;

	private JButton fileSelectButton;

	private File rootDir;

	public static Preferences prefs = Preferences.userRoot().node("org.exmaralda.teide");

	private Element root;

	private Namespace ns;

	private Document xml;

	private Element teiHeader;

	private int count;

	private File xmlFile;

	private JPanel optionsPanel;

	private JCheckBox onlyHighest;

	private JCheckBox onlyXML;

	private FileTree fileTree;

	private DefaultMutableTreeNode rootNode;

	private JPanel stylesheetPanel;

	private JComboBox styleSelector;

	private JButton styleChooser;

	private MenuBar myMenuBar;

	private JButton openInBrowserButton;

	private HtmlPanel htmlPanel;

	private DocumentBuilderImpl dbuilder;

	private HTMLDisplayer displayPanel;

	private JButton styleRemover;

	private JLabel jLabel_IL2;

	private JButton styleListSaver;

	private XMLDisplayer xmlDisplayer;

	private JPanel fileOptionsPanel;

	private JCheckBox checkRoot;

	private JTextField rootTextfield;

	private TEIDEFileFilter tff;

	private JPanel topPanel;

	private JPanel htmlControlPanel;

	private JButton copyHTMLtoClipboardButton;

	private JLabel htmlStatusLabel;

	private JButton saveHTMLButton;

	private String output;

	private String color;

	private File curFile = new File("");

	private JButton refreshHTMLButton;

	private String pH;

	private File corpDir;

	private TeideUI teide;

	/**
	 * @throws HeadlessException
	 */
	public TeideUI() throws HeadlessException {
		super("TEIDE (TEI Document Explorer)");
		teide = this;
		builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
		builder.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
		builder.setFeature("http://xml.org/sax/features/external-general-entities", true);

		data = new TeideData();
		createUI();

	}

	/**
	 * 
	 */
	private void createUI() {
		this.setLocation(prefs.getInt("posX", 10), prefs.getInt("posY", 10));
		this.setPreferredSize(new Dimension(prefs.getInt("sizeX", 800), prefs.getInt("sizeY", 600)));
		addWindowListener(this);
		this.getContentPane().setLayout(new BorderLayout());
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(0.25);
		this.setJMenuBar(new TEIDEMenuBar(this));
		this.getContentPane().add(splitPane, BorderLayout.CENTER);

		fileTree = new FileTree(prefs.get("lastRootDir", ""));
		rootDir = fileTree.getRootDir();
		tff = new TEIDEFileFilter();
		tff.setOnlyHighest(prefs.getBoolean("onlyHighest", false));
		tff.setOnlyXML(prefs.getBoolean("onlyXML", false));
		fileTree.setFileFilter(tff);
		fileTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				showFile((File) node.getUserObject());
			}
		});
		//		JScrollPane fileListScrollPane = new JScrollPane(getFileTree());
		JScrollPane fileListScrollPane = new JScrollPane(fileTree);
		fileListScrollPane.setPreferredSize(new Dimension(120, 600));
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(getFileOptionsPanel());
		leftPanel.add(getFileSelectPanel());
		leftPanel.add(fileListScrollPane);
		leftPanel.add(getStylesheetsPanel());
		splitPane.add(leftPanel, JSplitPane.LEFT);

		JSplitPane sp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getTopPanel(), getXMLDisplayer());
		sp2.setOneTouchExpandable(true);

		splitPane.add(sp2, JSplitPane.RIGHT);
		this.getContentPane().setPreferredSize(new Dimension(600, 600));
		JLabel adLabel = new JLabel("http://www.exmaralda.org/teide", JLabel.RIGHT);
		adLabel.setOpaque(true);
		adLabel.setBackground(Color.DARK_GRAY);
		adLabel.setForeground(Color.WHITE);
		this.getContentPane().add(adLabel, BorderLayout.SOUTH);
		this.pack();
		sp2.setDividerLocation(1.0);

	}

	/**
	 * @return
	 */
	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel(new BorderLayout());
			topPanel.add(getHTMLControlPanel(), BorderLayout.NORTH);
			topPanel.add(getDisplayPanel(), BorderLayout.CENTER);
			topPanel.setBorder(BorderFactory.createTitledBorder(Loc.getText("title.transformedResult")));
		}

		return topPanel;
	}

	/**
	 * @return
	 */
	private JPanel getHTMLControlPanel() {
		if (htmlControlPanel == null) {
			htmlControlPanel = new JPanel();
			htmlControlPanel.setLayout(new BoxLayout(htmlControlPanel, BoxLayout.X_AXIS));

			htmlControlPanel.add(getOpenInBrowserButton());
			htmlControlPanel.add(getCopyHTMLtoClipboardButton());
			htmlControlPanel.add(getSaveHTMLButton());
			htmlControlPanel.add(getRefreshHTMLButton());
			htmlControlPanel.add(Box.createHorizontalGlue());
			htmlControlPanel.add(getHTMLStatusLabel());
		}
		return htmlControlPanel;
	}

	/**
	 * @return
	 */
	private Component getHTMLStatusLabel() {
		if (htmlStatusLabel == null) {
			htmlStatusLabel = new JLabel("");
			htmlStatusLabel.setOpaque(true);
			status(STATUS_IDLE);
		}
		return htmlStatusLabel;
	}

	/**
	 * @return
	 */
	private Component getCopyHTMLtoClipboardButton() {
		if (copyHTMLtoClipboardButton == null) {
			copyHTMLtoClipboardButton = new JButton(Loc.getText("command.copyResultToClipboard"));
			copyHTMLtoClipboardButton.putClientProperty("JButton.buttonType", "segmented");
			copyHTMLtoClipboardButton.putClientProperty("JButton.segmentPosition", "middle");

			copyHTMLtoClipboardButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setClipboard(displayPanel.getText());
				}

			});

		}
		return copyHTMLtoClipboardButton;
	}

	private Component getSaveHTMLButton() {
		if (saveHTMLButton == null) {
			saveHTMLButton = new JButton(Loc.getText("command.saveResult"));
			saveHTMLButton.putClientProperty("JButton.buttonType", "segmented");
			saveHTMLButton.putClientProperty("JButton.segmentPosition", "last");

			saveHTMLButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					saveHTML();
				}

			});

		}
		saveHTMLButton.setEnabled(false);
		return saveHTMLButton;
	}

	private Component getRefreshHTMLButton() {
		if (refreshHTMLButton == null) {

			refreshHTMLButton = new JButton(Loc.getText("command.refreshResult"), createImageIcon("resources/icons/refresh16.png", ""));
			refreshHTMLButton.putClientProperty("JButton.buttonType", "segmented");
			refreshHTMLButton.putClientProperty("JButton.segmentPosition", "only");
			refreshHTMLButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					refreshHTML();
				}

			});

		}
		return refreshHTMLButton;
	}

	protected static ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = Teide.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * 
	 */
	protected void refreshHTML() {
		showFile(xmlFile);
	}

	private void saveHTML() {
		String textToSave = displayPanel.getText();
		String filename;
		if (((File) styleSelector.getSelectedItem()).exists()) {
			filename = curFile.getName().substring(0, curFile.getName().toUpperCase().indexOf(".XML")) + " (" + ((File) styleSelector.getSelectedItem()).getName() + ")";

		} else {
			filename = curFile.getName().substring(0, curFile.getName().toUpperCase().indexOf(".XML")) + " (" + styleSelector.getSelectedItem() + ")";
		}
		filename = filename.replaceAll(File.separator, "_");
		filename = filename.replaceAll(File.pathSeparator, "_");
		TeideFileWriter writer = new TeideFileWriter(new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + File.separator + filename + ".html"));
		writer.selectFile(this.getRootPane());
		writer.write(textToSave);

	}

	/**
	 * @return
	 */
	private XMLDisplayer getXMLDisplayer() {
		if (xmlDisplayer == null) {
			xmlDisplayer = new XMLDisplayer();
		}
		xmlDisplayer.setBorder(BorderFactory.createTitledBorder("XML source"));
		return xmlDisplayer;

	}

	/**
	 * @return
	 */
	private MenuBar getMyMenuBar() {
		if (myMenuBar == null) {
			myMenuBar = new MenuBar();
		}

		return myMenuBar;
	}

	/**
	 * @return
	 */
	private JPanel getFileOptionsPanel() {
		if (fileOptionsPanel == null) {
			fileOptionsPanel = new JPanel();
			fileOptionsPanel.setLayout(new GridLayout(0, 1));
			fileOptionsPanel.setBorder(BorderFactory.createTitledBorder(Loc.getText("title.showfiles")));
			fileOptionsPanel.add(getOnlyXML());
			fileOptionsPanel.add(getOnlyHighest());
			fileOptionsPanel.add(getCheckRoot());
			fileOptionsPanel.add(getRootTextfield());
			for (Component c : fileOptionsPanel.getComponents()) {
				((JComponent) c).setAlignmentX(0);
			}
		}
		return fileOptionsPanel;
	}

	/**
	 * @return
	 */
	private JTextField getRootTextfield() {
		if (rootTextfield == null) {
			rootTextfield = new JTextField();

			rootTextfield.setText(prefs.get("rootValue", "TEI.2"));
		}
		return rootTextfield;
	}

	private JPanel getStylesheetsPanel() {
		if (stylesheetPanel == null) {
			stylesheetPanel = new JPanel();
			stylesheetPanel.setLayout(new GridLayout(0, 1));
			//			stylesheetPanel.setLayout(new BoxLayout(stylesheetPanel,
			//					BoxLayout.Y_AXIS));
			//			stylesheetPanel.setVisible(false);
			stylesheetPanel.setBorder(BorderFactory.createTitledBorder(Loc.getText("title.stylesheets")));
			stylesheetPanel.add(new JLabel("Stylesheets..."));
			stylesheetPanel.add(getJLabel_IL2());
			stylesheetPanel.add(getStyleSelector());
			stylesheetPanel.add(new JLabel("hinzufügen/entfernen"));
			Box layoutBox = Box.createHorizontalBox();
			layoutBox.add(getStyleChooser());
			layoutBox.add(getStyleRemover());
			layoutBox.add(getStyleListSaver());
			stylesheetPanel.add(layoutBox);
			stylesheetPanel.add(getOpenInBrowserButton());
			stylesheetPanel.add(Box.createVerticalGlue());
			for (Component c : stylesheetPanel.getComponents()) {
				((JComponent) c).setAlignmentX(0);
			}
		}
		return stylesheetPanel;
	}

	/**
	 * @return
	 */
	private JButton getStyleListSaver() {
		if (styleListSaver == null) {
			styleListSaver = new JButton("Liste Speichern");
			styleListSaver.putClientProperty("JButton.buttonType", "segmented");
			styleListSaver.putClientProperty("JButton.segmentPosition", "last");
			styleListSaver.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					saveStylesList();
				}
			});
		}
		return styleListSaver;
	}

	/**
	 * 
	 */
	protected void saveStylesList() {
		if (styleSelector.getItemCount() > 1) {
			String toPut = "";
			for (int i = 1; i < styleSelector.getItemCount(); i++) {
				toPut += ((File) styleSelector.getItemAt(i)).getAbsolutePath() + File.pathSeparator;
			}
			prefs.put("teiWalker.recentSheets", toPut);
		}
	}

	/**
	 * @return
	 */
	private JButton getOpenInBrowserButton() {
		if (openInBrowserButton == null) {
			openInBrowserButton = new JButton("im Browser öffnen");
			openInBrowserButton.putClientProperty("JButton.buttonType", "segmented");
			openInBrowserButton.putClientProperty("JButton.segmentPosition", "first");

			openInBrowserButton.setEnabled(false);
			openInBrowserButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					openInBrowser();
				}

			});
		}
		return openInBrowserButton;
	}

	private void openInBrowser() {
		try {

			FileOutputStream fos = new FileOutputStream(System.getProperty("java.io.tmpdir") + "teideFile.html", false);
			fos.write(convertToHTML().getBytes("UTF-8"));
			fos.close();
			Desktop.getDesktop().browse(new File(System.getProperty("java.io.tmpdir") + "teideFile.html").toURI());
		} catch (Exception e) {
			System.err.println(e.toString());
			//System.exit(1);
		}

	}

	/**
	 * @return
	 */
	private JButton getStyleChooser() {
		if (styleChooser == null) {
			styleChooser = new JButton("+");
			styleChooser.putClientProperty("JButton.buttonType", "segmented");
			styleChooser.putClientProperty("JButton.segmentPosition", "first");

			styleChooser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					selectNewStylesheet();
				}
			});
		}
		return styleChooser;
	}

	private JButton getStyleRemover() {
		if (styleRemover == null) {
			styleRemover = new JButton("-");
			styleRemover.putClientProperty("JButton.buttonType", "segmented");
			styleRemover.putClientProperty("JButton.segmentPosition", "middle");

			styleRemover.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					removeStylesheet();
				}
			});
		}
		return styleRemover;
	}

	private void removeStylesheet() {
		int i = styleSelector.getSelectedIndex();
		if (i > 0) {
			//		if (((File) styleSelector.getSelectedItem()).exists()) {
			styleSelector.setSelectedIndex(0);
			styleSelector.removeItemAt(i);
		}
	}

	private void selectNewStylesheet() {
		JFileChooser fc = new JFileChooser(prefs.get("teiWalker.RecentStylesheetDir", "/"), null);
		//		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int dialogStatus = fc.showOpenDialog(this);
		if (dialogStatus == 0) {
			File stylesheetFile = fc.getSelectedFile();
			prefs.put("teiWalker.RecentStylesheetDir", stylesheetFile.getParentFile().getPath());
			styleSelector.addItem(stylesheetFile);
			styleSelector.setSelectedItem(stylesheetFile);
		}
	}

	/**
	 * @return
	 */
	private JComboBox getStyleSelector() {
		if (styleSelector == null) {
			Vector<File> sheets = new Vector<File>();
			sheets.add(new File("Intern (teiHeader"));
			String recentSheets = prefs.get("teiWalker.recentSheets", "");
			if (recentSheets.length() > 1) {
				for (String aFile : recentSheets.split(File.pathSeparator)) {
					if (new File(aFile).exists()) {
						sheets.add(new File(aFile));

					}
				}
			}
			styleSelector = new JComboBox(sheets);
			Dimension maximumSize = styleSelector.getPreferredSize();
			maximumSize.width = Short.MAX_VALUE;
			styleSelector.setMaximumSize(maximumSize);

			styleSelector.setRenderer(new FileListRenderer());
			styleSelector.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (xmlFile != null) {
						showFile(xmlFile);
					}
				}
			});

		}
		return styleSelector;
	}

	/**
	 * @return
	 */
	private JCheckBox getOnlyHighest() {
		if (onlyHighest == null) {
			onlyHighest = new JCheckBox("Nur höchste Version anzeigen");
			onlyHighest.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					tff.setOnlyHighest(onlyHighest.isSelected());
					fileTree.setFileFilter(tff);
				}
			});
			onlyHighest.setSelected(prefs.getBoolean("onlyHighest", false));
		}
		return onlyHighest;
	}

	private JCheckBox getOnlyXML() {
		if (onlyXML == null) {
			onlyXML = new JCheckBox("Nur XML");
			onlyXML.setSelected(prefs.getBoolean("onlyXML", true));
			onlyXML.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					tff.setOnlyXML(onlyXML.isSelected());
					fileTree.setFileFilter(tff);
				}
			});
		}
		return onlyXML;
	}

	private JCheckBox getCheckRoot() {
		if (checkRoot == null) {
			checkRoot = new JCheckBox("Document-Typ prüfen");

			checkRoot.setSelected(prefs.getBoolean("checkRoot", true));
			checkRoot.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					prefs.putBoolean("checkRoot", checkRoot.isSelected());
				}
			});

		}
		return checkRoot;
	}

	/**
	 * @return
	 */
	/*
	 * private JTree getFileTree() { if (fileTree == null) { rootNode = new
	 * DefaultMutableTreeNode(); fileTree = new JTree(rootNode);
	 * fileTree.setCellRenderer(new FileTreeRenderer());
	 * fileTree.addTreeSelectionListener(new TreeSelectionListener() { public
	 * void valueChanged(TreeSelectionEvent e) { DefaultMutableTreeNode node =
	 * (DefaultMutableTreeNode) e .getPath().getLastPathComponent();
	 * showFile((File) node.getUserObject()); } }); } return fileTree; }
	 */

	private String convertToHTML(File tf, File inputFile) throws TeideException {
		String html = "";
		StylesheetFactory sf = new StylesheetFactory(true);
		try {
			if (tf.exists()) {
				try {
					html = sf.applyExternalStylesheetToExternalXMLFile(tf.getAbsolutePath(), inputFile.getPath());
					openInBrowserButton.setEnabled(true);
				} catch (Exception e) {
					html = "<html><body><pre>" + e.getLocalizedMessage() + "</pre></body></html>";
					throw new TeideException(e.getLocalizedMessage());
				}
			} else {
				html = sf.applyInternalStylesheetToExternalXMLFile("/org/exmaralda/teide/resources/teiHeader.xsl", inputFile.getPath());
			}
			status(STATUS_OK);

		} catch (TransformerConfigurationException err) {
			//error compiling stylesheet
			html = "<h2>" + inputFile.getPath() + "</h2><p style='background-color: #ffff00;'>Failed to compile the stylesheet " + tf.getName() + ". It will be removed." + "</p>";
			removeStylesheet();

		} catch (SAXException err) {
			err.printStackTrace();
		} catch (ParserConfigurationException err) {
			err.printStackTrace();
		} catch (IOException err) {
			err.printStackTrace();
		} catch (TransformerException err) {
			err.printStackTrace();
		}
		return html;
	}

	private String convertToHTML(int selectedSheet, File inputFile) throws TeideException {
		String html = "";
		StylesheetFactory sf = new StylesheetFactory(true);
		File tf = (File) styleSelector.getItemAt(selectedSheet);
		return convertToHTML(tf, inputFile);
	}

	private String convertToHTML() throws TeideException {
		return convertToHTML(styleSelector.getSelectedIndex(), xmlFile);
	}

	/**
	 * @return
	 */
	private JPanel getFileSelectPanel() {
		if (fileSelectPanel == null) {
			fileSelectPanel = new JPanel(new BorderLayout());
			{
				fileSelectButton = new JButton("Wurzelverzeichnis auswählen");
				fileSelectPanel.add(fileSelectButton);
				fileSelectButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						selectRootFolder();
					}
				});
			}
		}
		return fileSelectPanel;
	}

	/**
	 * @return
	 */
	private HTMLDisplayer getDisplayPanel() {
		if (displayPanel == null) {
			displayPanel = new HTMLDisplayer(HTMLDisplayer.TYPE_COBRA);
			displayPanel.setSize(new Dimension(500, 500));
			displayPanel.setText("<html><body>TEIDE 0.5</body></html>");
		}
		return displayPanel;
	}

	protected void selectRootFolder() {
		JFileChooser fc = new JFileChooser(prefs.get("teiWalker.RecentDir", "/"), null);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int dialogStatus = fc.showOpenDialog(this);
		if (dialogStatus == 0) {
			rootDir = fc.getSelectedFile();
			fileSelectButton.setText((rootDir.getAbsolutePath() == rootDir.getAbsolutePath().substring(rootDir.getAbsolutePath().lastIndexOf(File.separator)) ? rootDir.getAbsolutePath() : "..." + rootDir.getAbsolutePath().substring(rootDir.getAbsolutePath().lastIndexOf(File.separator))));
			fileSelectButton.setToolTipText(rootDir.getAbsolutePath());

			prefs.put("teiWalker.RecentDir", rootDir.getPath());
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			selectFiles(rootDir);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private void selectFiles(File rootFile) {
		fileTree.setHome(rootFile);
	}

	/**
	 * @param file
	 * @return
	 */
	/*
	 * private boolean doWeWantThatFile(File file) { boolean weWantIt = true;
	 * if (file.getName().startsWith(".")) weWantIt = false; if
	 * (onlyXML.isSelected()) if (!file.getName().endsWith(".xml")) return
	 * false; if (file.getName().startsWith(".")) return false; else if
	 * (onlyHighest.isSelected()) { if (file.getName().indexOf(".") > 2) {
	 * String myName = file.getName(); int lastDot = myName.lastIndexOf(".");
	 * TreeSet<String> brothers = new TreeSet<String>(); for (String brother :
	 * file.getParentFile().list()) { if (brother.length() == myName.length()) {
	 * if (brother.substring(0, lastDot - 2).equals( myName.substring(0,
	 * lastDot - 2))) { brothers.add(brother); } } } String highest =
	 * brothers.last(); if (!highest.equals(myName)) { weWantIt = false; } }
	 * else { weWantIt = false; } } return weWantIt; }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	private void showFile(final File f) {
		if (f != null) {
			curFile = f;
			this.setCursor(Cursor.WAIT_CURSOR);
			openInBrowserButton.setEnabled(false);
			status(STATUS_IDLE);

			if (f.isDirectory()) {
				display("<h2>Directory: " + f.getName() + "</h2>");
				status(STATUS_NOT_TRANSFORMED);

			} else {
				if (!f.getName().endsWith(".xml")) {
					//				displayPane.setText("<h2>Not an XML-File: " + f.getName()
					//						+ "</h2>");
					status(STATUS_ERROR);
					display("<h2>Not an XML-File: " + f.getName() + "</h2>");
				} else {
					status(STATUS_PROCESSING);

					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							transformXML(f);
						}
					});
				}
			}
			this.setCursor(Cursor.DEFAULT_CURSOR);
		}
	}

	/**
	 * @param f
	 */
	private void transformXML(File f) {
		xmlFile = f;
		try {
			xml = builder.build(xmlFile);
			root = xml.getRootElement();
			ns = root.getNamespace();
			if (checkRoot.isSelected()) {
				if (!root.getName().matches(rootTextfield.getText())) {
					String html = "<h2 style='background: #aaaa00;'>" + xmlFile.getName() + "</h2>";
					html += "<p> - doesn't match the document-type. It's root-element is called '" + root.getName() + "'</p>";
					display(html);
					status(STATUS_NOT_TRANSFORMED);

				} else {
					xml2HTML();
				}
			} else {
				xml2HTML();
			}
			status(STATUS_OK);
			if (xmlDisplayer.showXML()) {
				Format format = Format.getRawFormat();
				XMLOutputter outputter = new XMLOutputter(format);
				xmlDisplayer.setText(outputter.outputString(root));
			}

		} catch (JDOMException err) {
			try {
				displayError("contains invalid XML:<br/>" + err.getLocalizedMessage() + xmlFile.toURL());
			} catch (MalformedURLException err1) {
				err1.printStackTrace();
			}
			status(STATUS_NOT_TRANSFORMED);
			err.printStackTrace();

		} catch (IOException err) {
			err.printStackTrace();
			displayError("file doesn't exist (anymore?`)");
			status(STATUS_NOT_TRANSFORMED);

		}
	}

	/**
	 * @param idle2
	 */
	private void status(int statuscode) {
		switch (statuscode) {
		case STATUS_IDLE: {
			output = "idle";
			color = "cccccc";
			break;
		}
		case STATUS_OK: {
			output = "ok";
			color = "99ff99";
			saveHTMLButton.setEnabled(true);
			break;
		}
		case STATUS_ERROR: {
			output = "error";
			color = "ff9999";
			saveHTMLButton.setEnabled(false);
			break;
		}
		case STATUS_PROCESSING: {
			output = "processing";
			color = "ffff00";
			break;
		}
		case STATUS_NOT_TRANSFORMED: {
			output = "nothing to transform";
			color = "cccccc";
			break;
		}
		default: {
			output = "idle";
			color = "cccccc";
			break;
		}
		}
		String curFileName = "";
		if (curFile.exists()) {
			curFileName = curFile.getName();
		} else {
			curFileName = "n/a";
		}
		output = curFileName + " ->" + ((File) styleSelector.getSelectedItem()).getName() + ")" + ": " + output;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				htmlStatusLabel.setText(output);
				htmlStatusLabel.setBackground(Color.decode("0x" + color));
			}
		});
		htmlStatusLabel.setText(output);
		htmlStatusLabel.setBackground(Color.decode("0x" + color));
	}

	/**
	 * @param string
	 */
	private void displayError(String string) {
		String html = "<h2>" + xmlFile.getPath() + "</h2><p style='background-color: #aaaa00;'>" + string + "</p>";
		display(html);
		//		displayPane.setText(html);
	}

	/**
	 * @return
	 */
	private void xml2HTML() {
		try {
			display(convertToHTML());
		} catch (TeideException err) {
			display("error");
		}
		//		displayPane.setText(convertToHtml());
		//		displayPane.setCaretPosition(0);
		File tf = (File) styleSelector.getSelectedObjects()[0];
		if (tf.exists())
			openInBrowserButton.setEnabled(true);
	}

	private void display(String html) {
		displayPanel.setText(html);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed(WindowEvent e) {
		handleQuit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent e) {
		handleQuit();
	}

	/**
	 * 
	 */
	private void handleQuit() {
		System.out.println("HandlingQuit");
		prefs.put("lastRootDir", rootDir.getPath());
		prefs.putBoolean("onlyXML", onlyXML.isSelected());
		prefs.putBoolean("onlyHighest", onlyHighest.isSelected());
		prefs.putBoolean("checkRoot", checkRoot.isSelected());
		prefs.put("rootValue", rootTextfield.getText());
		prefs.putInt("sizeX", this.getSize().width);
		prefs.putInt("sizeY", this.getSize().height);
		prefs.putInt("posX", this.getX());
		prefs.putInt("posY", this.getY());
		System.exit(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	public void windowDeactivated(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	public void windowDeiconified(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent e) {
	}

	public static void setClipboard(String str) {
		StringSelection ss = new StringSelection(str);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		System.out.println(str);
	}

	private JLabel getJLabel_IL2() {
		if (jLabel_IL2 == null) {
			jLabel_IL2 = new JLabel("stylesheet wählen");
			jLabel_IL2.setText("stylesheet wählen");
			jLabel_IL2.setHorizontalAlignment(SwingConstants.LEFT);
		}
		return jLabel_IL2;
	}

	/**
	 * create a HTML-Display of the Corpus with prerendered visualisiations
	 */

	Runnable publishCorpusRunnable = new Runnable() {

		private int p;

		private HashMap<File, String> miniStylesheetMap;

		public void run() {
			long startTime = System.currentTimeMillis();

			DateFormat dateFormat = new SimpleDateFormat("dd_hhmmss");
			Date date = new Date();
			corpDir = new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + File.separator + dateFormat.format(date));
			boolean success = corpDir.mkdir();

			//			filesToTransform = new Vector<File>();
			filesToTransform = new HashMap<File, Vector<File>>();
			if (miniStylesheet != null) {
				miniStylesheetMap = new HashMap<File, String>();
			}
			progress.setDeterminable(false);
			progress.addStatusText("<h3>" + Loc.getText("status.countingFiles") + "</h3>", true);
			progress.setTask(Loc.getText("status.countingFiles") + "(1/3)");

			handleFile(rootDir);

			if (styleSelector.getItemCount() > 1) {
				progress.setDeterminable(true);
				progress.setMin(0);
				progress.setMax(filesToTransform.size());
				progress.setTask(Loc.getText("status.transforming") + "(2/3)");
				progress.addStatusText("<h3>Transforming " + filesToTransform.size() + " files through " + (styleSelector.getItemCount() - 1) + (((styleSelector.getItemCount() - 1) > 1) ? "stylesheets" : "stylesheet") + "</h3>", true);
				int count = 0;
				for (File xmlInputFile : filesToTransform.keySet()) {
					count++;
					if (xmlInputFile.isDirectory()) {
						progress.addStatusText("[" + xmlInputFile.getName() + "]", false);
					} else {
						// ministylesheet f�r Dateibeschreibung transformieren
						if (miniStylesheet != null) {
							String myResult;
							try {
								miniStylesheetMap.put(xmlInputFile, convertToHTML(miniStylesheet, xmlInputFile));
							} catch (TeideException err) {
								miniStylesheetMap.put(xmlInputFile, "");
								err.printStackTrace();
							}

						}
						// stylesheets aus liste transformieren
						for (int i = 1; i < styleSelector.getItemCount(); i++) {
							File xslFile = (File) styleSelector.getItemAt(i);
							if (xslFile.exists()) {
								progress.addStatusText(xmlInputFile.getName() + "->" + styleSelector.getItemAt(i).toString() + ": ", false);
								String transResult;
								try {
									transResult = convertToHTML(i, xmlInputFile);
									String transformedFilename = xmlInputFile.getParent() + File.separator + xmlInputFile.getName().substring(0, xmlInputFile.getName().toUpperCase().indexOf(".XML")) + xslFile.getName().substring(0, xslFile.getName().toUpperCase().indexOf(".XSL")) + ".html";
									TeideFileWriter writer = new TeideFileWriter(new File(transformedFilename));
									writer.write(transResult);
									progress.addStatusText("<span style='color:green'>OK</span>", true);
									filesToTransform.get(xmlInputFile).add(new File(transformedFilename));

								} catch (TeideException err) {
									progress.addStatusText("<span style='color:red; font-weight: bold;'>" + err.getMessage() + "</span>", true);
								}
							} else {
								progress.addStatusText("<span style='color:yellow'>doesn't match</span>", true);
							}
						}
						progress.setProgress(count);
					}
				}
			}
			// alles geschrieben. jetzt durchsuchen wir den kram nochmal und machen sch�nes html!

			pH = "<html><head><title>Korpus</title><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />" + TeideHTML.scriptString() + TeideHTML.styleString() + "</head><body>";

			progress.setMin(0);
			progress.setMax(filesToTransform.size());
			p = 0;
			progress.setProgress(0);
			progress.setTask(Loc.getText("status.writingIndex") + "(3/3)");

			count = 1000;

			delEmptyDirs(corpDir);
			doIndexFile(corpDir);

			pH += "</body></html>";
			progress.setMin(0);
			progress.setMax(filesToTransform.size());
			progress.setProgress(0);
			progress.setTask(Loc.getText("status.writingIndex") + "(3/3)");
			TeideFileWriter writer = new TeideFileWriter(new File(corpDir.getPath() + File.separator + "index.html"));
			writer.write(pH);
			long time = System.currentTimeMillis() - startTime;
			progress.addStatusText("Elapsed time: " + time / 1000 + " seconds.", true);
			progress.done();

		}

		private void delEmptyDirs(File f) {
			if (f.isDirectory()) {
				if (f.listFiles().length > 0) {
					for (File fc : f.listFiles()) {
						delEmptyDirs(fc);
					}
				} else {
					File fp = f.getParentFile();
					f.delete();
					delEmptyDirs(fp);
				}
			}
		}

		//		        progressBar.setValue(i);

		private void doIndexFile(File f) {
			count++;
			if (f.isDirectory()) {
				if (f.listFiles().length > 0) {
					pH += "<div class=\"notab\" id=\"SP" + count + "\"><a class=\"dirLink\" onclick=\"switchMenu('DIV" + count + "', '" + "SP" + count + "','false');\">" + f.getName() + "</a></div>" + "<div class=\"tabbody\" id=\"DIV" + count + "\" style=\"display:none;\">";
					;
					for (File fc : f.listFiles()) {
						doIndexFile(fc);
					}
					pH += "</div>";
				}
			} else {
				if (filesToTransform.keySet().contains(f)) { // ausgangsdatei
					pH += "<div class=\"content\">";
					progress.setProgress(p += 1);
					if (miniStylesheet != null) {

						pH += miniStylesheetMap.get(f);
					}
					pH += "<p><strong><a href='" + URLDecoder.decode(getRelativePath(corpDir, f).toString()) + "'>" + f.getName() + "</a></strong>";
					for (File tf : filesToTransform.get(f)) {
						pH += " <br/><a href='" + URLDecoder.decode(getRelativePath(corpDir, tf).toString()) + "'>"

						+ tf.getName().substring(f.getName().lastIndexOf(".")

						, tf.getName().toUpperCase().indexOf(".HTML"))

						+ "</a> ";
					}
					pH += "</p></div>";
				}
			}

		}
	};

	private ProgressDialog progress;

	private File miniStylesheet;

	public void publishCorpus() {
		JFileChooser fc = new JFileChooser(prefs.get("teiWalker.RecentStylesheetDir", "/"), null);
		//		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setDialogTitle(Loc.getText("message.selectMiniStylesheet"));
		int dialogStatus = fc.showOpenDialog(this);

		if (dialogStatus == 0) {
			miniStylesheet = fc.getSelectedFile();
		} else {
			miniStylesheet = null;
		}

		// start the publishing
		progress = new ProgressDialog(teide, Loc.getText("command.publishCorpus"), 0, 100);
		progress.setVisible(true);

		Thread thread = new Thread(publishCorpusRunnable);
		thread.start();

	}

	/**
	 * @param f
	 */
	private void handleFile(File f) {
		if (f.isDirectory()) {
			filesToTransform.put(new File(corpDir + File.separator + getRelativePath(rootDir, f.getParentFile()) + f.getName()), new Vector<File>());

			String parDirString = URLDecoder.decode((corpDir + File.separator + getRelativePath(rootDir, f)));
			if (parDirString.endsWith(File.separator)) {
				parDirString = parDirString.substring(0, parDirString.length() - 1); // letzter separator weg
			}
			File parDir = new File(parDirString);
			if (!parDir.exists()) {
				boolean mkDirResult = parDir.mkdir();

			}
			for (File ff : f.listFiles()) {
				handleFile(ff);
			}

		} else {
			if (tff.accept(f)) {
				boolean accept = true;

				if (checkRoot.isSelected()) {
					try {
						xml = builder.build(f);
						root = xml.getRootElement();
						ns = root.getNamespace();
						if (!root.getName().matches(rootTextfield.getText())) {
							progress.addStatusText(f.getName() + "  <span style='color:grey'>root-elemt doesn't match</span>", true);
							accept = false;
						}
					} catch (Exception e) {
						progress.addStatusText(f.getName() + " <span style='color:red'>xml-error</span>", true);
						accept = false;
					}
				}
				if (accept) {
					copyFile(f.getPath(), corpDir + File.separator + getRelativePath(rootDir, f.getParentFile()) + f.getName());

					filesToTransform.put(new File(corpDir + File.separator + getRelativePath(rootDir, f.getParentFile()) + f.getName()), new Vector<File>());
					progress.addStatusText(f.getName() + " <span style='color:green'>will be tranformed</span>", true);

				} else {
					progress.addStatusText(f.getName() + " <span style='color:red'>xml-error</span>", true);
				}
			}
		}
	}

	private static void copyFile(String src, String dest) {
		try {
			File inputFile = new File(src);
			File outputFile = new File(dest);

			FileInputStream in = new FileInputStream(inputFile);
			FileOutputStream out = new FileOutputStream(outputFile);

			FileChannel inc = in.getChannel();
			FileChannel outc = out.getChannel();

			inc.transferTo(0, inc.size(), outc);

			inc.close();
			outc.close();

			in.close();
			out.close();
			//		System.out.println("Y:" + src + "->" + dest);
		} catch (Exception e) {
			//	System.err.println("N:" + src + "->" + dest				+ e.getLocalizedMessage());

			// fuck!

		}
	}

	/**
	 * der tolle relativierer
	 * 
	 * @param from
	 *              ursprung
	 * @param to
	 *              die zu relativierende datei
	 * @return
	 */
	public URI getRelativePath(File from, File to) {

		URI fromURI = from.toURI();
		URI toURI = to.toURI();
		URI relativeURI = fromURI.relativize(toURI);
		return relativeURI;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent e) {
	}

	/**
	 * 
	 */
	public void publishCorpusX() {
		Vector<File> stylesheetVector = new Vector<File>();
		if (styleSelector.getItemCount() > 1) {
			for (int i = 1; i < styleSelector.getItemCount(); i++) {
				stylesheetVector.add(((File) styleSelector.getItemAt(i)));
			}

			PublishWizard pw = new PublishWizard(tff, rootDir, stylesheetVector);
			pw.pack();
			pw.setVisible(true);
		}
	}
}
