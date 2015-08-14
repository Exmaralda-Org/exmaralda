package org.exmaralda.tagger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public class TaggerFrame extends JFrame implements ChangeListener,
		ActionListener, TaggerEventListener, HyperlinkListener {
	static Preferences prefs = Preferences.userRoot().node(
			"org.sfb538.z2tagger");

	private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);

	private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	private static String[] columnNames;

	private javax.swing.JPanel jContentPane = null;

	private TEITagger tagger;

	public JTextPane sentenceOutputPane = null;

	private JButton rwdButton = null;

	private JButton fwdButton = null;

	private JTextPane textInfoPane;

	private JPanel buttonPane;

	private JPanel filePanel;

	private JMenuBar menu;

	private JTextField outputFileTextField;

	private JTextField wordlistFileTextField;

	private JButton chooseWordlistFileButton;

	private JButton chooseOutputFileButton;

	private JButton chooseInputFileButton;

	private JButton startTaggingButton;

	private JSlider progress;

	private JTextField progressTextField;

	private JButton editWordlistButton;

	private JScrollPane sentencePane;

	private String myValue;

	private JToolBar toolBar;

	private JTextField beforeTextField;

	private JTextField afterTextField;

	private JPanel textInfoHolderPane;

	private boolean updateSlider;

	private boolean initialized;

	private JCheckBox tagAllCheckBox;

	private JMenuItem saveAsTaggedItem;

	private JPanel parentInfoPanel;

	private DefaultTableModel parentInfoModel;

	private DefaultTableModel currentInfoModel;

	private JCheckBox moveNext;

	private JCheckBox showTag;

	private JCheckBox showAtt;

	private JCheckBox ignoreTagged;

	private JPanel oPanel;

	private JSplitPane infoOptionsPanel;

	private JCheckBox showFirst;

	private JCheckBox ignoreFirst;

	private JCheckBox showLast;

	private JCheckBox ignoreLast;

	private JPanel newAttributePanel;

	private JTextField newAtt;

	private JTextField newVal;

	private JScrollPane textInfoScrollPane;

	private JPanel buttons;

	private JTabbedPane settingsTP;

	private JPanel wlPanel;

	private JTextPane wordlist;

	private JPanel toPanel;

	private JButton wlChangeButton;

	private JTextField searchedTag;

	private JButton toAcceptBtn;

	private JTextPane attsToSearch;

	private JMenuItem recentTEI;

	private JMenuItem recentWL;

	private JTextPane attsValueToSearch;

	private JSplitPane rightPanes;

	private JSplitPane leftPanes;

	private MyTableModel optionsTM;

	private JLabel statusDisplay;

	private JCheckBox wlActiveCheckbox;
	public static String os = (System.getProperty("mrj.version") == null ? "win"
			: "mac");

	public TaggerFrame() {
		super();
		System.out.println(System.getProperty("java.version"));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			System.setProperty("apple.awt.brushMetalLook", "true");
		} catch (Exception e) {
			System.err.println("failed setting SystemLookAndFeel");
		}
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exit(false);
			}
		});
		tagger = new TEITagger();
		tagger.addTaggerEventListener(this);
		initialize();
	}

	public TaggerFrame(TEITagger t) {
		super();
		if (t == null) {
			tagger = new TEITagger(null, null);
		} else {
			tagger = t;
		}
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exit(false);
			}
		});
		initialize();
	}

	private void initialize() {
		columnNames = new String[] { "attribute", "value" };
		this.setTitle("[z2tagger]");
		this.setSize(prefs.getInt("windowWidth", 700), prefs.getInt(
				"windowHeight", 400));
		this.setLocation(prefs.getInt("windowPosX", 10), prefs.getInt(
				"windowPosY", 10));
		this.setContentPane(getJContentPane());
		this.setJMenuBar(getMenus());
		initialized = true;
	}

	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
		}
		jContentPane.setLayout(new BorderLayout());
		leftPanes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				getParentInfoPanel(), getSentencePane());
		rightPanes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanes,
				getInfoOptionsPane());
		jContentPane.add(rightPanes, BorderLayout.CENTER);
		leftPanes.setDividerLocation(prefs.getInt("lpDivider", 100));
		rightPanes.setDividerLocation(prefs.getInt("rpDivider", 100));
		jContentPane.add(getButtonPanel(), BorderLayout.PAGE_END);
		jContentPane.add(getToolBar(), BorderLayout.NORTH);
		return jContentPane;
	}

	private JPanel getParentInfoPanel() {
		if (parentInfoPanel == null) {
			parentInfoPanel = new JPanel();
			parentInfoPanel.setLayout(new BoxLayout(parentInfoPanel,
					BoxLayout.Y_AXIS));
		}
		currentInfoModel = new DefaultTableModel();
		parentInfoModel = new DefaultTableModel();
		JTable parentInfo = new JTable(parentInfoModel);
		JTable currentInfo = new JTable(currentInfoModel);
		parentInfoModel.setColumnCount(2);
		currentInfoModel.setColumnCount(2);
		parentInfoModel.setColumnIdentifiers(columnNames);
		currentInfoModel.setColumnIdentifiers(columnNames);
		currentInfo.setEnabled(true);
		parentInfo.setEnabled(true);
		JScrollPane currentScroll = new JScrollPane(currentInfo);
		currentScroll.setBorder(BorderFactory.createTitledBorder("found tag"));
		JScrollPane parentScroll = new JScrollPane(parentInfo);
		parentScroll.setBorder(BorderFactory.createTitledBorder("parent tag"));
		parentInfoPanel.add(currentScroll);
		newAttributePanel = new JPanel();
		newAttributePanel.setLayout(new BoxLayout(newAttributePanel,
				BoxLayout.X_AXIS));
		newAttributePanel.setBorder(BorderFactory
				.createTitledBorder("add attribute"));
		newAtt = new JTextField();
		newVal = new JTextField();
		JButton addAtt = new JButton("add");
		addAtt.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setNewAtt();
			}
		});
		newAttributePanel.add(newAtt);
		newAttributePanel.add(newVal);
		newAttributePanel.add(addAtt);
		resize(newAttributePanel, 220, 50);
		parentInfoPanel.add(newAttributePanel);
		parentInfoPanel.add(parentScroll);
		parentInfo.setPreferredScrollableViewportSize(new Dimension(200, 70));
		currentInfo.setPreferredScrollableViewportSize(new Dimension(200, 70));
		return parentInfoPanel;
	}

	/**
	 * resets the UI components since something in the tagger has changed
	 */
	private void repaintControls() {
		// textInfoPane.setText(tagger.getHTMLHeader());
		progress.setMinimum(0);
		progress.setMaximum(tagger.getSearchCount());
		buttons.removeAll();
		buttons.add(getRwdButton());
		Vector options = tagger.getOptions();
		if (options != null) {
			Iterator i = options.iterator();
			int count = 0;
			while (i.hasNext()) {
				System.out.println("AN OPTION!");
				TagOption myOption = (TagOption) i.next();
				myValue = myOption.getAttName() + "=" + myOption.getNewVal();
				JButton myButton = new JButton(myValue);
				if (myOption.getKey().length() > 0) {
					char myKey = myOption.getKey().charAt(0);
					myButton.setMnemonic(myKey);
				}
				// JPanel buttonBackground = new JPanel();
				// buttonBackground.setBackground(bg)
				myButton.setBackground(Helpers.colors[count]);
				myButton.setOpaque(true);
				count++;
				myButton.setActionCommand("" + options.indexOf(myOption));
				myButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						tagIt(((JButton) e.getSource()).getActionCommand());
					}
				});
				buttons.add(myButton);
			}
		}
		buttons.add(getFwdButton());
		tagAllCheckBox = new JCheckBox();
		buttons.add(tagAllCheckBox);
		buttons.add(new JLabel("tag all"));
		this.setVisible(true);
	}

	/**
	 * 
	 */
	protected void setNewAtt() {
		tagger.newAtt(newAtt.getText(), newVal.getText());
		newAtt.setText("");
		newVal.setText("");
		showCurrentSearchItem();
	}

	/**
	 * 
	 */
	private void showCurrentSearchItem() {
		String searchdisplay = tagger.outputCurrentForm();
		sentenceOutputPane.setText(searchdisplay);
		parentInfoModel
				.setDataVector(tagger.getParentAttributes(), columnNames);
		currentInfoModel.setDataVector(tagger.getCurrentAttributes(),
				columnNames);
		showProgress();
	}

	private JPanel getButtonPanel() {
		if (buttonPane == null) {
			buttonPane = new JPanel();
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));
		}
		buttons = new JPanel();
		buttons.setBorder(BorderFactory.createTitledBorder("actions"));
		buttons.add(new JLabel("no search results yet"));
		JPanel progressP = new JPanel();
		progressP.setLayout(new BoxLayout(progressP, BoxLayout.Y_AXIS));
		progressP.setBorder(BorderFactory.createTitledBorder("progress"));
		progress = new JSlider();
		progress.addChangeListener(this);
		progress.setMinimum(0);
		progress.setMaximum(tagger.getSearchCount());
		progressP.add(progress);
		progressTextField = new JTextField();
		progressTextField.setHorizontalAlignment(JTextField.CENTER);
		progressTextField.setBackground(java.awt.SystemColor.control);
		progressTextField.addActionListener(this);
		progressP.add(progressTextField);
		buttonPane.add(progressP);
		buttonPane.add(buttons);
		return buttonPane;
	}

	/**
	 * Exits the program
	 * 
	 * @param b
	 *            (deprecated, can be deleted)
	 */
	private void exit(boolean b) {
		if (tagger.isDocChanged()) {
			Object[] options = { "Yes", "No, thanks!" };
			int n = JOptionPane.showOptionDialog(this,
					"Would you like to save the changes?", "Document changed",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, options, options[0]);
			if (n == JOptionPane.YES_OPTION) {
				saveDoc();
			}
		}
		if (!b) { // keine neue Session
			prefs.putInt("windowWidth", this.getWidth());
			prefs.putInt("windowHeight", this.getHeight());
			prefs.putInt("windowPosX", this.getX());
			prefs.putInt("windowPosY", this.getY());
			prefs.putInt("lpDivider", leftPanes.getDividerLocation());
			prefs.putInt("rpDivider", rightPanes.getDividerLocation());
			System.exit(0);
		}
	}

	private JMenuBar getMenus() {
		if (menu == null) {
			menu = new JMenuBar();
		}
		// file menu
		JMenu file = new JMenu("File");
		JMenuItem openTEIFile = new JMenuItem("open TEI-file");
		openTEIFile.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				openTeiFile(false);
			}
		});
		recentTEI = new JMenuItem("open TEI-file: "
				+ prefs.get("recentTEI", ""));
		recentTEI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				openTeiFile(true);
			}
		});
		JMenuItem openWordlistFile = new JMenuItem("open tagging options");
		openWordlistFile.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				openWordlistFile(false);
			}
		});
		recentWL = new JMenuItem("open tagging options: "
				+ prefs.get("recentWL", ""));
		recentWL.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				openWordlistFile(true);
			}
		});
		JMenuItem attachItem = new JMenuItem("attach stylesheet");
		attachItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				File file = chooseFile("attach stylesheet", true, "xsl");

				String name = file.getPath();
				if (!name.endsWith(".xsl")) {
					name += ".xsl";
				}
				tagger.setXSL(name);
				if (tagger != null) {
					// textInfoPane.setText(tagger.getHTMLHeader());
				}
				saveDoc();
			}
		});
		JMenuItem saveItem = new JMenuItem("save TEI-File");
		saveItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				saveDoc();
			}
		});
		JMenuItem saveAsItem = new JMenuItem("save TEI-File as...");
		saveAsItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new CFileFilter("XML-Files",
						new String[] { "xml" }, true));
				int returnVal = fc.showSaveDialog(filePanel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					String name = file.getPath();
					if (!name.endsWith(".xml")) {
						name += ".xml";
					}
					tagger.save(name);
				}
			}
		});
		JMenuItem saveWLItem = new JMenuItem("save tagging-options");
		saveWLItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				saveOptions(false);
			}
		});
		JMenuItem saveWLAsItem = new JMenuItem("save tagging-options as...");
		saveWLAsItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				saveOptions(true);
			}
		});
		file.add(openTEIFile);
		if (prefs.get("recentTEI", null) != null) {
			file.add(recentTEI);
		}
		file.add(recentTEI);
		file.add(openWordlistFile);
		if (prefs.get("recentWL", null) != null) {
			file.add(recentWL);
		}
		file.addSeparator();
		file.add(attachItem);
		file.addSeparator();
		file.add(saveItem);
		file.add(saveAsItem);
		file.addSeparator();
		file.add(saveWLItem);
		file.add(saveWLAsItem);
		// file.add(saveAsTaggedItem);
		// tools menu
		JMenu tools = new JMenu("tools");
		JMenuItem doWords = new JMenuItem("chop words");
		doWords.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doWords();
			}
		});
		tools.add(doWords);
		JMenuItem number = new JMenuItem("number items");
		number.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				numberItems();
			}
		});
		tools.add(number);
		JMenuItem setAtt = new JMenuItem("set Attribute");
		setAtt.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setAtt();
			}
		});
		tools.add(setAtt);
		menu.add(file);
		menu.add(tools);
		return menu;
	}

	protected File chooseFile(String title, boolean loadSave,
			final String extension) {
		File file = null;
		if (os.equals("mac")) {
			// use the native file dialog on the mac
			FileDialog dialog = new FileDialog((Frame) this, title,
					(loadSave ? FileDialog.LOAD : FileDialog.SAVE));
			dialog.setFilenameFilter(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith("." + extension);
				}
			});
			dialog.show();
			if (dialog.getFile() != null)
				file = new File(dialog.getDirectory() + dialog.getFile());
		} else {

			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(prefs.get("recentDir", null)));
			fc.setFileFilter(new CFileFilter(extension + "-files",
					new String[] { extension }, true));
			int returnVal;
			if (loadSave) {
				returnVal = fc.showOpenDialog(this);
			} else {
				returnVal = fc.showSaveDialog(this);
			}
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
			}
		}

		return file;
	}

	/**
	 * 
	 */
	protected void saveOptions(boolean saveas) {
		if ((tagger.getOptions().size() > 0)
				|| (tagger.getXpath().length() > 0)) {
			if ((tagger.getWordlistFileName() != null) && (!saveas)) {
				tagger.saveOptions(tagger.getWordlistFileName());
			} else {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new CFileFilter("XML-Files",
						new String[] { "xml" }, true));
				int returnVal = fc.showSaveDialog(filePanel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					String name = file.getPath();
					if (!name.endsWith(".xml")) {
						name += ".xml";
					}
					tagger.saveOptions(name);
				}
			}
		} else {
			JOptionPane.showMessageDialog(this,
					"There are neither options nor forms to save.");
		}
	}

	/**
	 * tool to set attribute values
	 */
	protected void setAtt() {
		if (tagger.isXmlLoaded()) {
			String s = (String) JOptionPane.showInputDialog(this,
					"Enter tag,attribute,value to be set\n"
							+ "(i.e.: w,pos,u to set type=u for all w-tags,\n"
							+ " w,pos,  to delete all w-Attributes)\n"
							+ "OLD VALUES WILL BE OVERRIDDEN!",
					"set attributes", JOptionPane.PLAIN_MESSAGE, null, null,
					prefs.get("setAttDefault", "w,pos,u"));
			if ((s != null) && (s.length() > 0)) {
				prefs.put("setAttDefault", s);
				tagger.toolsSetAtt(s);
			}
		} else {
			JOptionPane.showMessageDialog(this,
					"No XML loaded. Load a TEI-File first.");
		}
	}

	/**
	 * tool to numbers tags
	 */
	protected void numberItems() {
		if (tagger.isXmlLoaded()) {
			String s = (String) JOptionPane
					.showInputDialog(
							this,
							"Enter tag,attribute,starting number\n"
									+ "(i.e.: w,n,010 to number n-attributes in w-tags,\n starting with 10, using 3-digit-numbers)",
							"number words", JOptionPane.PLAIN_MESSAGE, null,
							null, prefs.get("numberItemsDefault", "s,n,1"));
			if ((s != null) && (s.length() > 0)) {
				prefs.put("numberItemsDefault", s);
				tagger.number(s);
			}
		} else {
			JOptionPane.showMessageDialog(this,
					"No XML loaded. Load a TEI-File first.");
		}
	}

	/**
	 * tokenizes document through a FSM
	 */
	protected void doWords() {
		if (tagger.isXmlLoaded()) {
			final JDialog dialog = new JDialog(this, "Tokenizer", true);
			dialog.getContentPane().setLayout(
					new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
			dialog.getContentPane().add(new JLabel("Select FSM:"));
			final JTextField fsmChooser = new JTextField(prefs.get(
					"currentFSM", ""));
			fsmChooser.setPreferredSize(new Dimension(200, 20));
			JButton fcButton = new JButton("...");
			fcButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					fc
							.setCurrentDirectory(new File(prefs.get(
									"recentDir", ".")));
					fc.setFileFilter(new CFileFilter("XML-File",
							new String[] { "xml" }, true));
					int returnVal = fc.showOpenDialog(filePanel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						// ((JTextField)
						// ((JButton)e.getSource()).getParent().getComponent(0)).setText(fc.getSelectedFile().getPath());
						fsmChooser.setText(fc.getSelectedFile().getPath());
					}
				}
			});
			JPanel holder = new JPanel();
			holder.add(fsmChooser);
			holder.add(fcButton);
			dialog.getContentPane().add(holder);
			JPanel holder2 = new JPanel();
			JButton okB = new JButton("OK");
			okB.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					prefs.put("currentFSM", fsmChooser.getText());
					tagger.chopWords(fsmChooser.getText());
					dialog.dispose();
				}
			});
			JButton cancelB = new JButton("cancel");
			cancelB.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dialog.dispose();
				}
			});
			holder2.add(okB);
			holder2.add(cancelB);
			dialog.getContentPane().add(holder2);
			dialog.pack();
			dialog.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this,
					"No XML loaded. Load a TEI-File first.");
		}
	}

	/**
	 * opens the wordlist file (now: tagset)
	 * 
	 * @param b
	 *            opens the recent file from prefs if true
	 * 
	 */
	protected void openWordlistFile(boolean openRecent) {
		File file = null;
		boolean open = false;
		if (!openRecent) {
			System.out.println("openWordlistFile");
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(prefs.get("recentDir", ".")));
			fc.setFileFilter(new CFileFilter("XML-File",
					new String[] { "xml" }, true));
			int returnVal = fc.showOpenDialog(filePanel);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				open = true;
			}
		} else {
			file = new File(prefs.get("recentWL", null));
			open = true;
		}
		if (open) {
			String result = tagger.getWordlistFile(file);
			if (result.equals("ok")) {
				prefs.put("recentDir", file.getParent());
				prefs.put("recentWL", file.getPath());
			} else {
				JOptionPane.showMessageDialog(this, result, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * opens the TEI-XML-file
	 * 
	 * @param b
	 *            opens the recent file from prefs if true
	 * 
	 */
	protected void openTeiFile(boolean openRecent) {
		File file = null;
		boolean open = false;
		if (!openRecent) {
			System.out.println("openTEIFile");
			file = chooseFile("open TEI file", true, "xml");
			if (file != null) {
				open = true;
			}
		} else {
			file = new File(prefs.get("recentTEI", null));
			open = true;
		}
		if (open) {
			String result = tagger.setTEIFile(file);
			if (result.equals("ok")) {
				prefs.put("recentDir", file.getParent());
				prefs.put("recentTEI", file.getPath());
			} else {
				JOptionPane.showMessageDialog(this, result, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} // nix
	}

	private JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
			toolBar.setFloatable(false);
			toolBar.setRollover(true);
		}
		statusDisplay = new JLabel("OK");
		toolBar.add(statusDisplay);
		return toolBar;
	}

	/**
	 * @param resizes
	 *            a JComponent
	 * @param i
	 *            x-size
	 * @param j
	 *            y-size
	 */
	private void resize(JComponent c, int w, int h) {
		Dimension d = new Dimension(w, h);
		c.setPreferredSize(d);
		c.setMinimumSize(d);
		c.setMaximumSize(d);
	}

	/**
	 * changes the display surrounding search results
	 * 
	 */
	private void changeSurrounding() {
		tagger.setItemsBefore((new Integer(beforeTextField.getText()))
				.intValue());
		prefs.put("before", beforeTextField.getText());
		tagger
				.setItemsAfter((new Integer(afterTextField.getText()))
						.intValue());
		prefs.put("after", afterTextField.getText());
	}

	/**
	 * saves the TEI-XML-file
	 * 
	 */
	private void saveDoc() {
		tagger.save(null);
	}

	/**
	 * saves the TEI-XML-File with name+"_tagged"
	 * 
	 */
	private void saveDocAsModified() {
		tagger.save(tagger.getModifiedFileName(true));
	}

	/**
	 * tags an element
	 * 
	 * @param option
	 *            holds all options
	 */
	private void tagIt(String option) {
		if (tagAllCheckBox.isSelected()) {
			tagger.setIndex(0);
			tagger.tag(option);
			while (showNextSearchitem()) {
				tagger.tag(myValue);
			}
		} else {
			tagger.tag(option);
			if (moveNext.isSelected()) {
				showNextSearchitem();
			} else {
				int tempIndex = tagger.getIndex();
				tagger.setIndex(tagger.getIndex() - 1);
				showNextSearchitem();
				if (tagger.getIndex() > tempIndex) {
					showPreviousSearchItem();
				}
			}
		}
	}

	private JScrollPane getSentencePane() {
		if (sentencePane == null) {
			sentenceOutputPane = new JTextPane();
			sentenceOutputPane.addHyperlinkListener(this);
			sentencePane = new JScrollPane(sentenceOutputPane);
		}
		HTMLEditorKit htmlKit = new HTMLEditorKit();
		StyleSheet css = htmlKit.getStyleSheet();
		Style style = css.getStyle(StyleContext.DEFAULT_STYLE);
		css.addRule("body { color : black }");
		css.addRule("body { font-size : 12pt; }");
		css
				.addRule("body { font-family : \"Arial Unicode MS\", Arial, sans-serif; }");
		css.addRule("a {text-decoration: none; }");
		css.addRule("a.hover {text-decoration: none; }");
		sentenceOutputPane.setEditorKit(htmlKit);
		sentenceOutputPane.setEditable(false);
		sentencePane
				.setBorder(BorderFactory.createTitledBorder("Handled item"));
		/*
		 * if (tagger.getSearchCount() < 1) { sentenceOutputPane .setText("<b>No
		 * search results / wrong file format</b>"); }
		 */
		return sentencePane;
	}

	/**
	 * contains TEIHeader, Display Options, Wordlist
	 * 
	 * @return
	 */
	private JComponent getInfoOptionsPane() {
		textInfoPane = new JTextPane();
		textInfoScrollPane = new JScrollPane(textInfoPane);
		textInfoScrollPane.setPreferredSize(new Dimension(200, 200));
		textInfoScrollPane.setBorder(BorderFactory
				.createTitledBorder("teiHeader"));
		// resize(textInfoScrollPane, 300, 300);
		HTMLEditorKit htmlKit = new HTMLEditorKit();
		textInfoPane.setEditorKit(htmlKit);
		textInfoPane.setEditable(false);
		// display options
		settingsTP = new JTabbedPane();
		oPanel = new JPanel();
		oPanel.setLayout(new BoxLayout(oPanel, BoxLayout.Y_AXIS));
		moveNext = new JCheckBox("auto move next");
		moveNext.setSelected(true);
		showTag = new JCheckBox("tags");
		showTag.setSelected(false);
		showTag.addActionListener(this);
		showFirst = new JCheckBox("first result");
		showFirst.addActionListener(this);
		ignoreFirst = new JCheckBox("first result");
		ignoreFirst.addActionListener(this);
		showLast = new JCheckBox("last result");
		showLast.addActionListener(this);
		ignoreLast = new JCheckBox("last result");
		ignoreLast.addActionListener(this);
		showAtt = new JCheckBox("attributes");
		showAtt.setSelected(false);
		showAtt.addActionListener(this);
		ignoreTagged = new JCheckBox("tagged");
		ignoreTagged.setSelected(false);
		ignoreTagged.addActionListener(this);
		oPanel.add(new JLabel("navigation:"), null);
		oPanel.add(moveNext);
		oPanel.add(new JLabel("ignore:"));
		oPanel.add(new JLabel(""));
		oPanel.add(ignoreFirst);
		oPanel.add(ignoreLast);
		oPanel.add(new JLabel("show only:"));
		oPanel.add(showFirst);
		oPanel.add(showLast);
		oPanel.add(new JLabel("show inline:"));
		oPanel.add(showTag);
		oPanel.add(showAtt);
		beforeTextField = new JTextField(prefs.get("before", "2"));
		resize(beforeTextField, 40, 20);
		// beforeTextField.setMaximumSize(new Dimension(40, 20));
		afterTextField = new JTextField(prefs.get("after", "2"));
		resize(afterTextField, 40, 20);
		// changeSurrounding();
		JButton changeSurroundingButton = new JButton("change");
		changeSurroundingButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						changeSurrounding();
					}
				});
		JPanel itB = new JPanel();
		itB.setLayout(new BoxLayout(itB, BoxLayout.X_AXIS));
		itB.add(new JLabel("items before:"));
		itB.add(beforeTextField);
		itB.setAlignmentX(0);
		oPanel.add(itB);
		JPanel itA = new JPanel();
		itA.setLayout(new BoxLayout(itA, BoxLayout.X_AXIS));
		itA.add(new JLabel("items behind:"));
		itA.add(afterTextField);
		itA.setAlignmentX(0);
		oPanel.add(itA);
		oPanel.add(changeSurroundingButton);
		// wordlist
		wlPanel = new JPanel();
		wlPanel.setLayout(new BorderLayout());
		StyleContext sc = new StyleContext();
		Style plain = sc.addStyle("Plain", null);
		StyleConstants.setFontFamily(plain, "Arial Unicode MS");
		StyleConstants.setAlignment(plain, StyleConstants.ALIGN_LEFT);
		wordlist = new JTextPane();
		wordlist.setLogicalStyle(plain);
		JScrollPane wordlistScroller = new JScrollPane(wordlist);
		wlPanel.add(wordlistScroller, BorderLayout.CENTER);
		wlActiveCheckbox = new JCheckBox("don't use wordlist");
		wlActiveCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				wordlist.setEnabled(!wlActiveCheckbox.isSelected());
				wlChangeButton.setEnabled(!wlActiveCheckbox.isSelected());
				tagger.setWordlistEnabled(!wlActiveCheckbox.isSelected());
			}
		});
		wlPanel.add(wlActiveCheckbox, BorderLayout.NORTH);
		wlChangeButton = new JButton("accept changes");
		wlChangeButton.setActionCommand("wordlistChanged");
		wlChangeButton.addActionListener(this);
		wlPanel.add(wlChangeButton, BorderLayout.SOUTH);
		// tag and options
		toPanel = new JPanel();
		toPanel.setLayout(new BoxLayout(toPanel, BoxLayout.Y_AXIS));
		toPanel.add(new JLabel("tag what (XPath):"));
		toPanel.add(searchedTag = new JTextField());

		toPanel.add(new JLabel("tag options:"));
		optionsTM = new MyTableModel();
		JTable optionsTable = new JTable(optionsTM);
		optionsTM.addRow();
		toPanel.add(new JScrollPane(optionsTable));
		JScrollPane toScroller = new JScrollPane(toPanel);
		toPanel.add(toAcceptBtn = new JButton("accept changes"));
		toAcceptBtn.setActionCommand("acceptToChanges");
		toAcceptBtn.addActionListener(this);
		// put it together
		settingsTP.add("Display options", oPanel);
		settingsTP.add("Wordlist", wlPanel);
		settingsTP.add("tagoptions", toPanel);
		settingsTP.add("header",textInfoScrollPane);
		return settingsTP;
//		infoOptionsPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
//				textInfoScrollPane, settingsTP);
//		return infoOptionsPanel;
	}

	private JButton getRwdButton() {
		if (rwdButton == null) {
			rwdButton = new JButton("<<");
			rwdButton.setMnemonic(KeyEvent.VK_LEFT);
			rwdButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showPreviousSearchItem();
				}
			});
		}
		if (tagger == null) {
			rwdButton.setEnabled(false);
		}
		return rwdButton;
	}

	private boolean showNextSearchitem() {
		String searchdisplay = tagger.outputNextForm();
		sentenceOutputPane.setText(searchdisplay);
		parentInfoModel
				.setDataVector(tagger.getParentAttributes(), columnNames);
		currentInfoModel.setDataVector(tagger.getCurrentAttributes(),
				columnNames);
		showProgress();
		return (!searchdisplay.equals("no (more) results."));
	}

	private boolean showPreviousSearchItem() {
		String searchdisplay = tagger.outputPreviousForm();
		sentenceOutputPane.setText(searchdisplay);
		parentInfoModel
				.setDataVector(tagger.getParentAttributes(), columnNames);
		currentInfoModel.setDataVector(tagger.getCurrentAttributes(),
				columnNames);
		showProgress();
		return (!searchdisplay.equals("no (more) results."));
	}

	private void showProgress() {
		progress.setValue(tagger.getIndex());
		System.out.println(tagger.getIndex());
		progressTextField.setText(tagger.getIndex() + "/"
				+ tagger.getSearchCount());
		updateSlider = false;
	}

	private JButton getFwdButton() {
		if (fwdButton == null) {
			fwdButton = new JButton(">>");
			fwdButton.setMnemonic(KeyEvent.VK_RIGHT);
			fwdButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showNextSearchitem();
				}
			});
		}
		if (tagger.getResultCount() < 1) {
			fwdButton.setEnabled(false);
		}
		return fwdButton;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
	 * )
	 */
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (initialized == true) {
			progressTextField.setText("" + source.getValue());
			if (!source.getValueIsAdjusting()) {
				int sliderIndex = (int) source.getValue();
				if (sliderIndex != tagger.getIndex()) {
					jumpToIndex(sliderIndex);
				}
			}
		}
	}

	private void jumpToIndex(int sliderIndex) {
		if ((sliderIndex > 0) && (sliderIndex < tagger.getSearchCount())) {
			boolean once = true;
			while ((tagger.getIndex() > sliderIndex) && (tagger.getIndex() > 0)) {
				int oldIndex = tagger.getIndex();
				tagger.getPreviousForm();
				if (oldIndex == tagger.getIndex()) {
					if (once == false) {
						break;
					} else {
						once = false;
					}
				}
			}
			while ((tagger.getIndex() < sliderIndex)
					&& (tagger.getIndex() < tagger.getSearchCount())) {
				int oldIndex = tagger.getIndex();
				tagger.getNextForm();
				if (oldIndex == tagger.getIndex()) {
					break;
				}
			}
			if (!showNextSearchitem()) {
				showPreviousSearchItem();
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == progressTextField) {
			jumpToIndex(new Integer(progressTextField.getText()).intValue());
		} else if (e.getSource().getClass() == javax.swing.JCheckBox.class) {
			tagger.updateOptions(showAtt.isSelected(), showTag.isSelected(),
					ignoreTagged.isSelected(), showFirst.isSelected());
		} else if (e.getActionCommand().equals("wordlistChanged")) {
			tagger.updateWordlist(wordlist.getText());
		} else if (e.getActionCommand().equals("acceptToChanges")) {
			searchedTag.setBackground(Color.WHITE);
			adjustTagOptions();
		}
	}

	/**
	 * 
	 */
	private void adjustTagOptions() {
		boolean changes = false;
		if ((searchedTag.getText()).length() > 0) {
			tagger.changeTag(searchedTag.getText(), false);
		} else {
			System.out.println("I didnt change a thing!"
					+ searchedTag.getText());
		}
		tagger.clearOptions();
		for (int i = 0; i < optionsTM.getRowCount(); i++) {
			if ((((String) optionsTM.getValueAt(i, 0)).length() > 0)
					&& ((String) optionsTM.getValueAt(i, 2)).length() > 0) {
				tagger.addOption(new TagOption((String) optionsTM.getValueAt(i,
						0), (String) optionsTM.getValueAt(i, 1),
						(String) optionsTM.getValueAt(i, 2), (String) optionsTM
								.getValueAt(i, 3), ((Boolean) optionsTM
								.getValueAt(i, 4)).booleanValue()));
			}
		}
		tagger.optionsChanged();
	} /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * z2tagger.TaggerEventListener#TaggerEventHappened(z2tagger.TaggerEvent)
	 */

	public void TaggerEventHappened(TaggerEvent t) {
		System.out.println(t.getMessage());
		if (t.getMessage().startsWith("xmlLoaded:")) {
			this.setTitle(t.getMessage().substring(10));
			textInfoPane.setText(tagger.getHTMLHeader());
		} else if (t.getMessage().equals("resultsExist")) {
			System.out.println("resultsExist");
			repaintControls();
		} else if (t.getMessage().equals("hasNamespace")) {
			this.setTitle(this.getTitle() + "(with namespace)");
			// searchedTag
			// .setBorder(BorderFactory
			// .createTitledBorder("document has a namespace! please add 'z2:' before every element"));
			// repaintControls();
		} else if (t.getMessage().equals("hasNoNamespace")) {
			// searchedTag.setBorder(BorderFactory
			// .createTitledBorder("document has no namespace"));
			// repaintControls();

		} else if (t.getMessage().equals("wordlistLoaded")) {
			wordlist.setText(tagger.getWordlist());
			optionsTM.fill(tagger.getOptions());
			searchedTag.setText(tagger.getXpath());
			settingsTP.setSelectedIndex(1);
		} else if (t.getMessage().startsWith("ERR:")) {
			statusDisplay.setText("Error: " + t.getMessage().substring(4));
			toolBar.setBackground(Color.RED);
			if (t.getMessage().equals("ERR:invalidXPath")) {
				searchedTag.setBackground(Color.RED);
			}
		} else if (t.getMessage().equals("XPathValid")) {
			searchedTag.setBackground(Color.GREEN);
		} else if (t.getMessage().startsWith("OK:")) {
			statusDisplay.setText("Success: " + t.getMessage().substring(3));
			toolBar.setBackground(Color.GREEN);
		} else if (t.getMessage().startsWith("STATUS:")) {
			statusDisplay.setText(t.getMessage().substring(7));
			toolBar.setBackground(SystemColor.control);
		} else if (t.getMessage().equals("WAIT")) {
			setCursor(waitCursor);
		} else if (t.getMessage().equals("RESUME")) {
			setCursor(defaultCursor);
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			AttributeSet atts;
			atts = e.getSourceElement().getAttributes();
			atts = (AttributeSet) atts
					.getAttribute(javax.swing.text.html.HTML.Tag.A);
			String theId = (String) atts
					.getAttribute(javax.swing.text.html.HTML.Attribute.ID);
			XMLOutputter out = new XMLOutputter();
			try {
				showElement(tagger.getElementForId(theId));
				out.output(tagger.getElementForId(theId), System.out);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// System.out.println(tagger.getElementForId(theId).getContent());
		} else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
			AttributeSet atts;
			atts = e.getSourceElement().getAttributes();
			atts = (AttributeSet) atts
					.getAttribute(javax.swing.text.html.HTML.Tag.A);
			String theId = (String) atts
					.getAttribute(javax.swing.text.html.HTML.Attribute.ID);
			XMLOutputter out = new XMLOutputter();
			statusDisplay.setText(out.outputString(tagger
					.getElementForId(theId)));
			toolBar.setBackground(SystemColor.window);
		} else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
			statusDisplay.setText("Idle");
			toolBar.setBackground(SystemColor.control);
		}
	}

	/**
	 * @param elementForId
	 */
	private void showElement(Element e) {
		String searchdisplay = tagger.outputForm(e);
		sentenceOutputPane.setText(searchdisplay);
		parentInfoModel
				.setDataVector(tagger.getParentAttributes(), columnNames);
		currentInfoModel.setDataVector(tagger.getCurrentAttributes(),
				columnNames);
		showProgress();
		// return (!searchdisplay.equals("no (more) results."));
		// TODO Auto-generated method stub
	}
}

class MyTableModel extends AbstractTableModel {
	private String[] columnNames = { "att.name", "old value(regex)",
			"new value(string)", "shortcut", "replace" };

	private Vector data = new Vector();

	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * @param options
	 */
	public void fill(Vector options) {
		clear();
		for (int i = 0; i < options.size(); i++) {
			data.add(((TagOption) options.get(i)).getAttName());
			data.add(((TagOption) options.get(i)).getReplaceVal());
			data.add(((TagOption) options.get(i)).getNewVal());
			data.add(((TagOption) options.get(i)).getKey());
			data.add(new Boolean(((TagOption) options.get(i)).isReplace()));
			fireTableRowsInserted(getRowCount() + 1, getRowCount() + 1);
		}
		// TODO Auto-generated method stub
	}

	public void clear() {
		data.clear();
	}

	public int getRowCount() {
		return data.size() / getColumnCount();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data.get((row * getColumnCount()) + col);
	}

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public void addRow() {
		data.add("");
		data.add("");
		data.add("");
		data.add("");
		data.add(new Boolean(false));
		fireTableRowsInserted(getRowCount() + 1, getRowCount() + 1);
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		return true;
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		data.set((row * getColumnCount()) + col, value);
		fireTableCellUpdated(row, col);
		if (row + 1 == getRowCount()) {
			addRow();
		}
	}
}
