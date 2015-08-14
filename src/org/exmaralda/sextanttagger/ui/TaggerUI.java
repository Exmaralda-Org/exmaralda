package org.exmaralda.sextanttagger.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.html.HTMLEditorKit;

import org.exmaralda.sextanttagger.application.TaggerApplication;
import org.exmaralda.sextanttagger.resources.ResourceHandler;
import org.exmaralda.sextanttagger.types.TagOption;

public class TaggerUI extends javax.swing.JFrame
		implements
			KeyEventDispatcher,
			ChangeListener {
	private JPanel selectionPanel;
	private HashMap<String, TagOption> keyBindings;

	private JLabel spkLabel;
	private JPanel navPanel;
	private JPanel annotationPanel;
	private JComboBox keyCombo;
	private JSplitPane optionsSplit;
	private JCheckBox changeTranscriptionsRadio;
	private JLabel keyLabel;
	private JPanel jPanel2;
	private JScrollPane jScrollPane2;
	private JTable infoTable;
	private JTabbedPane tagsPanel;
	private JSeparator sep1;
	private JButton fwdTag;
	private JButton rwdTag;
	private JPanel optionsPanel;
	private JButton rwdShow;
	private JButton fwdShow;
	private JButton addTagButton;
	private JMenuItem openStandoffAnnotationMenuItem;
	private JMenuItem saveStandoffMenuItem;
	private JMenuItem saveTranscriptionMenuItem;
	private JMenuItem loadTranscriptionMenuItem;
	private JMenu fileMenu;
	private JMenuBar menuBar;
	private JScrollPane jScrollPane1;
	private JEditorPane taggingArea;
	private JPanel displayPanel;
	private JLabel navLabel;
	private JCheckBox autoAdvanceCheckbox;
	private JPanel infoPanel;
	private JLabel tagLabel;
	private JLabel disLabel;
	private JComboBox DisplaySegmentSelect;
	private JComboBox AnnotationSegmentSelect;
	private JComboBox SpeakerSelect;
	private JPanel DisplayPanel;
	private TaggerApplication app;
	private JButton rwdxButton;
	private DefaultTableModel infoTableModel;
	private JMenuItem saveTagsetMenuItem;
	private JMenuItem openTagsetMenuItem;
	private JButton openTagButton;
	private JPanel addLoadSavePanel;
	private JButton saveTagButton;
	private JSlider segSlider;
	private JSlider tagSlider;
	// private JLabel tagPosLabel;
	// private JLabel segPosLabel;
	private JPanel segNavPanel;
	private JPanel tagNavPanel;
	private JLabel segNameLabel;
	private JLabel tagNameLabel;
	// added by evil TS - 01-03-2011
	private JMenu editMenu;
	private JMenuItem exaktSearchMenuItem;
	// end add evil TS
	private JMenu taggerMenu;
	private JMenuItem stanfordMenuItem;
	private JLabel transcriptionLabel;
	private JLabel transcriptionPLabel;
	protected boolean showTabs;
	private JMenuItem saveAnnotatedTranscriptionMenuItem;
	private String showSegment;
	private String tagSegment;
	private JMenuItem visualizeTranscriptionMenuItem;
	private JMenu helpMenu;
	private HashMap<JPanel, String> keyMapper;

	public TaggerUI() {
		super();
		initGUI();
	}

	public TaggerUI(TaggerApplication app2) {
		super();
		app = app2;
		initGUI();
	}

	private void initGUI() {
		KeyboardFocusManager kbfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		kbfm.addKeyEventDispatcher(this);
		keyBindings = new HashMap<String, TagOption>();
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("");
			this.setPreferredSize(new java.awt.Dimension(700, 500));
			{
				menuBar = new JMenuBar();
				setJMenuBar(menuBar);

				fileMenu = new JMenu();
				menuBar.add(fileMenu);
				fileMenu.setText("File");
				loadTranscriptionMenuItem = new JMenuItem();
				fileMenu.add(loadTranscriptionMenuItem);
				loadTranscriptionMenuItem.setText("Open Transcription");
				loadTranscriptionMenuItem
						.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								app.openTranscription(null);
							}
						});
				saveTranscriptionMenuItem = new JMenuItem();
				if (app.getPrefs().get("lastOpenedTranscription", "").length() > 0) {
					final File f = new File(app.getPrefs().get(
							"lastOpenedTranscription", ""));
					if (f.exists()) {
						JMenuItem loadLastTranscriptionMenuItem = new JMenuItem(
								"Open Transcription: " + f.getName());
						loadLastTranscriptionMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										app.openTranscription(f);
									}
								});
						fileMenu.add(loadLastTranscriptionMenuItem);
					}
				}
				fileMenu.add(saveTranscriptionMenuItem);
				fileMenu.addSeparator();
				saveTranscriptionMenuItem.setText("Save Transcription");
				saveTranscriptionMenuItem.setEnabled(false);
				openStandoffAnnotationMenuItem = new JMenuItem();
				fileMenu.add(openStandoffAnnotationMenuItem);
				openStandoffAnnotationMenuItem
						.setText("Open Standoff-Annotation");
				// openStandoffAnnotationMenuItem.setEnabled(false);

				openStandoffAnnotationMenuItem
						.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								app.openESA(null);
							}
						});
				saveStandoffMenuItem = new JMenuItem();
				fileMenu.add(saveStandoffMenuItem);
				saveStandoffMenuItem.setText("Save Standoff-Annotation");
				saveStandoffMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						app.saveESA(null);
					}
				});

				saveAnnotatedTranscriptionMenuItem = new JMenuItem();
				fileMenu.add(saveAnnotatedTranscriptionMenuItem);
				saveAnnotatedTranscriptionMenuItem
						.setText("Save annotated Transcription...");
				saveAnnotatedTranscriptionMenuItem
						.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								app.saveEXA(null);
							}
						});
				fileMenu.addSeparator();
				visualizeTranscriptionMenuItem = new JMenuItem();
				fileMenu.add(visualizeTranscriptionMenuItem);
				visualizeTranscriptionMenuItem
						.setText("Visualize Transcription");
				visualizeTranscriptionMenuItem
						.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								app.visualize(null);
							}
						});
				fileMenu.addSeparator();

				openTagsetMenuItem = new JMenuItem();
				fileMenu.add(openTagsetMenuItem);
				openTagsetMenuItem.setText("Open Tagset");
				openTagsetMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						app.openTagset(null);
					}
				});

				if (app.getPrefs().get("lastOpenedTagset", "").length() > 0) {
					final File f = new File(app.getPrefs().get(
							"lastOpenedTagset", ""));
					if (f.exists()) {
						JMenuItem loadLastTagsetMenuItem = new JMenuItem(
								"Open Tagset: " + f.getName());
						loadLastTagsetMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										app.openTagset(f);
									}
								});
						fileMenu.add(loadLastTagsetMenuItem);
					}
				}
				saveTagsetMenuItem = new JMenuItem();
				fileMenu.add(saveTagsetMenuItem);
				saveTagsetMenuItem.setText("Save Tagset");
				saveTagsetMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						app.saveTagset(null);
					}
				});

				// added by evil TS - 01-03-2011
				editMenu = new JMenu("Edit");
				exaktSearchMenuItem = new JMenuItem("EXAKT Search...");
				exaktSearchMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							app.exaktSearch();
						} catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(selectionPanel,
									ex.getLocalizedMessage());
						}
					}
				});
				editMenu.add(exaktSearchMenuItem);
				menuBar.add(editMenu);
				// end add evil TS

				taggerMenu = new JMenu("Taggers");
				stanfordMenuItem = new JMenuItem("Stanford POS Tagger...");
				stanfordMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						app.testStanfordTagging();
					}
				});
				stanfordMenuItem.setEnabled(false);
				taggerMenu.add(stanfordMenuItem);
				// menuBar.add(taggerMenu);

				helpMenu = new JMenu("Help");
				JMenuItem aboutMenuItem = new JMenuItem("About Sextant");
				aboutMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						app.about();
					}
				});
				helpMenu.add(aboutMenuItem);
				JMenuItem webHelpMenuItem = new JMenuItem("Help (on the Web)");
				webHelpMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						app.help();
					}
				});
				helpMenu.add(webHelpMenuItem);
				menuBar.add(helpMenu);
			}
			{
				JPanel topPanel = new JPanel();
				topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
				selectionPanel = new JPanel();
				getContentPane().add(topPanel, BorderLayout.NORTH);
				selectionPanel.setBorder(BorderFactory
						.createTitledBorder("tier & segments"));

				transcriptionLabel = new JLabel("no transcription loaded");
				transcriptionLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD,
						14));
				transcriptionLabel.setBorder(BorderFactory
						.createTitledBorder("source file"));

				topPanel.add(transcriptionLabel);
				topPanel.add(selectionPanel);
				selectionPanel.add(Box.createHorizontalGlue());

				spkLabel = new JLabel();
				selectionPanel.add(spkLabel);
				spkLabel.setText("Speaker:");

				{
					ComboBoxModel SpeakerSelectModel = new DefaultComboBoxModel();
					SpeakerSelect = new JComboBox();
					selectionPanel.add(SpeakerSelect);
					SpeakerSelect.setModel(SpeakerSelectModel);
					SpeakerSelect.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JComboBox cb = (JComboBox) evt.getSource();
							if (cb.getItemCount() > 0) {
								String speaker = (String) cb.getSelectedItem();
								app.setSpeaker(speaker);
							}
						}
					});
				}
				{
					disLabel = new JLabel();
					selectionPanel.add(disLabel);
					disLabel.setText("Display:");
				}
				{
					ComboBoxModel DisplaySegmentSelectModel = new DefaultComboBoxModel();
					DisplaySegmentSelect = new JComboBox();
					selectionPanel.add(DisplaySegmentSelect);
					DisplaySegmentSelect.setModel(DisplaySegmentSelectModel);
					DisplaySegmentSelect.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								JComboBox cb = (JComboBox) e.getSource();
								if (cb.getItemCount() > 0) {
									String showSegment = (String) cb
											.getSelectedItem();
									app.setShowSegment(showSegment);
								}
							}
						}

					});
					// DisplaySegmentSelect
					// .addActionListener(new ActionListener() {
					// public void actionPerformed(ActionEvent evt) {
					// System.out.println("Display dropdown changed");
					// JComboBox cb = (JComboBox) evt.getSource();
					// if (cb.getItemCount() > 0) {
					// String showSegment = (String) cb
					// .getSelectedItem();
					// app.setShowSegment(showSegment);
					// }
					// }
					// });
				}

				tagLabel = new JLabel();
				selectionPanel.add(tagLabel);
				tagLabel.setText("Tag:");

				{
					ComboBoxModel AnnotationSegmentSelectModel = new DefaultComboBoxModel();
					AnnotationSegmentSelect = new JComboBox();
					selectionPanel.add(AnnotationSegmentSelect);
					AnnotationSegmentSelect
							.setModel(AnnotationSegmentSelectModel);
					AnnotationSegmentSelect
							.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									JComboBox cb = (JComboBox) evt.getSource();
									if (cb.getItemCount() > 0) {
										String tagSegment = (String) cb
												.getSelectedItem();
										app.setTagSegment(tagSegment);
									}
								}
							});
				}
			}

			infoPanel = new JPanel();
			BoxLayout ululiugliLayout = new BoxLayout(infoPanel,
					javax.swing.BoxLayout.Y_AXIS);
			infoPanel.setLayout(ululiugliLayout);
			getContentPane().add(infoPanel, BorderLayout.WEST);
			infoPanel.setAlignmentX(0.0f);
			// START >> jPanel2
			// START >> optionsSplit
			optionsSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			infoPanel.add(optionsSplit);
			optionsSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
			optionsSplit.setPreferredSize(new java.awt.Dimension(253, 334));

			jPanel2 = new JPanel();
			optionsSplit.add(jPanel2, JSplitPane.RIGHT);
			BoxLayout jPanel2Layout = new BoxLayout(jPanel2,
					javax.swing.BoxLayout.Y_AXIS);
			jPanel2.setLayout(jPanel2Layout);
			jPanel2.setBorder(BorderFactory.createTitledBorder(null, "tagged",
					TitledBorder.LEADING, TitledBorder.TOP));

			jScrollPane2 = new JScrollPane();
			jPanel2.add(jScrollPane2);

			infoTable = new JTable();
			jScrollPane2.setViewportView(infoTable);
			infoTableModel = new DefaultTableModel(new String[][]{{"", ""}},
					new String[]{"key", "value"});
			infoTable.setModel(infoTableModel);
			infoTable.setRowSorter(new TableRowSorter(infoTableModel));
			infoTable.setGridColor(new java.awt.Color(207, 207, 207));

			optionsPanel = new JPanel();
			optionsSplit.add(optionsPanel, JSplitPane.LEFT);
			BoxLayout optionsPanelLayout = new BoxLayout(optionsPanel,
					javax.swing.BoxLayout.Y_AXIS);
			optionsPanel.setLayout(optionsPanelLayout);
			optionsPanel.setBorder(BorderFactory.createTitledBorder("options"));
			optionsPanel.setAlignmentX(0.0f);

			keyLabel = new JLabel();
			optionsPanel.add(keyLabel);
			keyLabel.setText("show colors for key:");

			keyCombo = new JComboBox(new String[]{" "});
			optionsPanel.add(keyCombo);
			ComboBoxModel keyComboModel = new DefaultComboBoxModel();
			keyCombo.setModel(keyComboModel);
			keyCombo.setAlignmentX(0.0f);
			keyCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.refresh();
				}
			});

			autoAdvanceCheckbox = new JCheckBox();
			optionsPanel.add(autoAdvanceCheckbox);
			autoAdvanceCheckbox.setText("Auto-advance");
			autoAdvanceCheckbox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.getPrefs().putBoolean("autoAdvance",
							autoAdvanceCheckbox.isSelected());
				}
			});
			autoAdvanceCheckbox.setSelected(app.getPrefs().getBoolean(
					"autoAdvance", false));

			// START >> changeTranscriptionsRadio
			changeTranscriptionsRadio = new JCheckBox();
			optionsPanel.add(changeTranscriptionsRadio);
			changeTranscriptionsRadio.setText("change transcription headers");
			changeTranscriptionsRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.getPrefs().putBoolean("changeHeaders",
							changeTranscriptionsRadio.isSelected());
				}
			});
			changeTranscriptionsRadio.setSelected(app.getPrefs().getBoolean(
					"changeHeaders", false));

			annotationPanel = new JPanel();
			BoxLayout saefLayout = new BoxLayout(annotationPanel,
					javax.swing.BoxLayout.Y_AXIS);
			annotationPanel.setLayout(saefLayout);
			getContentPane().add(annotationPanel, BorderLayout.EAST);
			annotationPanel.setBorder(BorderFactory
					.createTitledBorder("annotation"));
			addLoadSavePanel = new JPanel();
			addLoadSavePanel.setLayout(new BoxLayout(addLoadSavePanel,
					BoxLayout.X_AXIS));
			annotationPanel.add(addLoadSavePanel);
			addTagButton = new JButton();
			addLoadSavePanel.add(addTagButton);
			addTagButton.setText("add");
			addTagButton.putClientProperty("JButton.buttonType", "segmented");
			addTagButton.putClientProperty("JButton.segmentPosition", "first");

			addTagButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.addTaggingOption();
				}
			});

			openTagButton = new JButton();
			addLoadSavePanel.add(openTagButton);
			openTagButton.setText("load");
			openTagButton.putClientProperty("JButton.buttonType", "segmented");
			openTagButton
					.putClientProperty("JButton.segmentPosition", "middle");

			openTagButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.openTagset(null);
				}
			});

			saveTagButton = new JButton();
			addLoadSavePanel.add(saveTagButton);
			saveTagButton.setText("save");
			saveTagButton.putClientProperty("JButton.buttonType", "segmented");
			saveTagButton.putClientProperty("JButton.segmentPosition", "last");

			saveTagButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.saveTagset(null);
				}
			});

			final JCheckBox toggle = new JCheckBox("show tabs");
			toggle.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent evt) {
					showTabs = toggle.isSelected();
					updateTagOptions();
				}
			});
			addLoadSavePanel.add(toggle);
			annotationPanel.add(new JSeparator());

			tagsPanel = new JTabbedPane();
			annotationPanel.add(tagsPanel);
			/*
			 * navpanel
			 */
			navPanel = new JPanel();
			navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));
			segNavPanel = new JPanel();
			segNavPanel.setLayout(new BoxLayout(segNavPanel, BoxLayout.X_AXIS));
			segNavPanel.setBorder(BorderFactory
					.createTitledBorder((String) null));
			tagNavPanel = new JPanel();
			tagNavPanel.setLayout(new BoxLayout(tagNavPanel, BoxLayout.X_AXIS));
			tagNavPanel.setBorder(BorderFactory
					.createTitledBorder((String) null));
			getContentPane().add(navPanel, BorderLayout.SOUTH);
			navPanel.setBorder(BorderFactory.createTitledBorder("navigation"));
			navPanel.add(segNavPanel);
			// navPanel.add(Box.createHorizontalGlue());
			navPanel.add(tagNavPanel);

			rwdShow = new JButton(new ImageIcon(
					new ResourceHandler().image("rwd.png")));
			rwdShow.putClientProperty("JButton.buttonType", "bevel");

			rwdShow.setText("");
			rwdShow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.rwdShow(false);
				}
			});

			rwdTag = new JButton(new ImageIcon(
					new ResourceHandler().image("rwd.png")));
			rwdTag.putClientProperty("JButton.buttonType", "bevel");
			navPanel.add(rwdTag);
			rwdTag.setText("");
			rwdTag.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.rwdTag();
				}
			});
			segSlider = new JSlider(JSlider.HORIZONTAL, 0, 0, 0);
			segSlider.addChangeListener(this);
			tagSlider = new JSlider(JSlider.HORIZONTAL, 0, 0, 0);
			tagSlider.addChangeListener(this);
			// segPosLabel = new JLabel("0/0");
			// tagPosLabel = new JLabel("0/0");
			navLabel = new JLabel();

			fwdTag = new JButton(new ImageIcon(
					new ResourceHandler().image("fwd.png")));
			fwdTag.putClientProperty("JButton.buttonType", "bevel");

			navPanel.add(fwdTag);
			fwdTag.setText("");
			fwdTag.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.fwdTag();
				}
			});

			fwdShow = new JButton(new ImageIcon(
					new ResourceHandler().image("fwd.png")));
			fwdShow.putClientProperty("JButton.buttonType", "bevel");

			navPanel.add(fwdShow);
			fwdShow.setText("");
			fwdShow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					app.fwdShow();
				}
			});
			segNameLabel = new JLabel("");
			tagNameLabel = new JLabel("");

			segNavPanel.add(rwdShow);
			segNavPanel.add(segNameLabel);
			segNavPanel.add(segSlider);
			// segNavPanel.add(segPosLabel);
			segNavPanel.add(fwdShow);
			tagNavPanel.add(rwdTag);
			tagNavPanel.add(tagNameLabel);
			tagNavPanel.add(tagSlider);
			// tagNavPanel.add(tagPosLabel);
			tagNavPanel.add(fwdTag);

			/*
			 * displaypanel
			 */
			displayPanel = new JPanel();
			BorderLayout displayPanelLayout = new BorderLayout();
			displayPanel.setLayout(displayPanelLayout);
			getContentPane().add(displayPanel, BorderLayout.CENTER);

			jScrollPane1 = new JScrollPane();
			displayPanel.add(jScrollPane1, BorderLayout.CENTER);
			taggingArea = new JEditorPane();
			taggingArea.setEditorKit(new HTMLEditorKit());
			jScrollPane1.setViewportView(taggingArea);
			taggingArea.setText("");
			taggingArea.setEditable(false);

			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateHeader() {
		setTitle(app.getApplicationName());
	}

	public void setApp(TaggerApplication a) {
		app = a;
		setTitle(app.getApplicationName());
	}

	public void updateDropdowns() {
		SpeakerSelect.removeAllItems();
		DisplaySegmentSelect.removeAllItems();
		AnnotationSegmentSelect.removeAllItems();
		SpeakerSelect.addItem("any speaker");
		for (String key : app.getSpeakers().keySet()) {
			SpeakerSelect.addItem(key);
		}
		for (String key : app.getSegments()) {
			DisplaySegmentSelect.addItem(key);
			AnnotationSegmentSelect.addItem(key);
		}
	}

	public void updateDisplay() {
		taggingArea.setText(app.getDisplayString());
		segSlider.removeChangeListener(this);
		tagSlider.removeChangeListener(this);
		segSlider.setMinimum(0);
		segSlider.setMaximum(app.getShowSegments().size());

		Hashtable<Integer, JComponent> sLabelTable = new Hashtable<Integer, JComponent>();
		sLabelTable.put(new Integer(0), new JLabel("0"));
		sLabelTable.put(new Integer(app.getShowSegments().size() / 2),
				new JLabel(showSegment));
		sLabelTable.put(new Integer(app.getShowSegments().size()), new JLabel(
				"" + app.getShowSegments().size()));
		segSlider.setLabelTable(sLabelTable);
		segSlider.setPaintLabels(true);
		segSlider.setValue(app.getSegmentShowing());

		// segPosLabel.setText((app.getSegmentShowing() + 1) + "/"
		// + app.getShowSegments().size());
		if ((app.getTaggables().size()) < 1) {
			tagSlider.setEnabled(false);
			// tagPosLabel.setText("none");
		} else {
			tagSlider.setEnabled(true);
			tagSlider.setMinimum(0);
			tagSlider.setMaximum(app.getTaggables().size());
			Hashtable<Integer, JComponent> tLabelTable = new Hashtable<Integer, JComponent>();
			tLabelTable.put(new Integer(0), new JLabel("0"));
			tLabelTable.put(new Integer(app.getTaggables().size() / 2),
					new JLabel(tagSegment));
			tLabelTable.put(new Integer(app.getTaggables().size()), new JLabel(
					"" + app.getTaggables().size()));
			tagSlider.setLabelTable(tLabelTable);
			tagSlider.setPaintLabels(true);
			tagSlider.setValue(app.getTagShowing());
		}
		segSlider.addChangeListener(this);
		tagSlider.addChangeListener(this);
	}

	public void updateTagOptions() {
		tagsPanel.removeAll();
		int count = 0;
		TreeSet<TagOption> ts = new TreeSet<TagOption>(app.getTagOptions());
		TagOption last = (ts.size() > 1 ? ts.first() : new TagOption());
		JPanel opPanel = new JPanel();
		opPanel.setLayout(new BoxLayout(opPanel, javax.swing.BoxLayout.Y_AXIS));
		tagsPanel.addTab(showTabs ? last.getKey() : "options", new JScrollPane(
				opPanel));
		keyMapper = new HashMap<JPanel, String>();
		boolean first = true;
		for (TagOption to : ts) {
			if (first && !showTabs)
				opPanel.add(getShowHideButton(to.getKey()));
			first = false;
			if (((!(ts.first().equals(to))))
					&& (!(to.getKey().equals(last.getKey())))) {
				if (showTabs) {
					JPanel rp = app.getRemovePanel(last);
					// rp.setAlignmentX(Component.LEFT_ALIGNMENT);
					opPanel.add(rp);
					keyMapper.put(rp, last.getKey());

					opPanel = new JPanel();
					opPanel.setLayout(new BoxLayout(opPanel,
							javax.swing.BoxLayout.Y_AXIS));

					tagsPanel.addTab(showTabs ? to.getKey() : "options",
							new JScrollPane(opPanel));

				} else {
					JPanel rp = app.getRemovePanel(last);
					opPanel.add(rp);
					keyMapper.put(rp, last.getKey());
					opPanel.add(getShowHideButton(to.getKey()));
				}

			}
			JPanel op = app.getOptionPanel(to);
			opPanel.add(op);
			keyMapper.put(op, to.getKey());
			keyBindings.put(to.getShortcut(), to);
			last = to;
		}
		opPanel.add(app.getRemovePanel(last));
		opPanel.revalidate();
		keyCombo.removeAllItems();
		for (String k : app.getKeys()) {
			keyCombo.addItem(k);
		}

		tagsPanel.revalidate();
	}

	private JPanel getShowHideButton(String key) {
		JPanel p = new JPanel();
		JCheckBox jb = new JCheckBox(key);
		jb.setSelected(true);
		jb.setActionCommand(key);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				for (JPanel l : keyMapper.keySet()) {
					if (keyMapper.get(l).equals(
							((JCheckBox) evt.getSource()).getActionCommand())) {
						l.setVisible(((JCheckBox) evt.getSource()).isSelected());
					}
				}
			}
		});
		jb.setAlignmentX(Component.LEFT_ALIGNMENT);
		p.add(jb);
		p.add(Box.createHorizontalGlue());
		return p;
	}

	public void updateInfoTable(HashSet<TagOption> options) {
		while (infoTableModel.getRowCount() > 0) {
			infoTableModel.removeRow(0);
		}
		for (TagOption to : options) {

			Vector<String> d = new Vector<String>();
			d.add(to.getKey());
			d.add(to.getValue());
			infoTableModel.addRow(d);
		}
	}

	public String getSelectedKey() {
		return (String) keyCombo.getSelectedItem();
	}

	public boolean getAutoAdvance() {
		return autoAdvanceCheckbox.isSelected();
	}

	public JCheckBox getChangeTranscriptionsRadio() {
		return changeTranscriptionsRadio;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_PRESSED) {
			if (app.isShowingDialog()) {
				app.getDialog().keyPressed(e);
			} else {
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (e.isShiftDown()) {
						app.fwdShow();
					} else {
						app.fwdTag();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (e.isShiftDown()) {
						app.rwdShow(false);
					} else {
						app.rwdTag();
					}

				} else {
					String code = e.getKeyCode() + ";" + e.getModifiers();

					if (keyBindings.keySet().contains(code)) {
						app.tagElement(keyBindings.get(code));
					}
				}
			}
		}
		// e.consume();
		return false;
	}

	public void labelNavigation(String showSegment, String tagSegment) {
		this.showSegment = showSegment;
		this.tagSegment = tagSegment;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			int pos = (int) source.getValue();
			if (pos > 0) {
				if (source == tagSlider) {
					app.setTagindex(pos - 1);
				} else {
					app.setSegmentShowing(pos - 1);
				}
			}
		}

	}

	public void setSourceFile(File file) {
		transcriptionLabel.setText(file.getName());

	}

	public void setTagSegment(String t) {
		if (!AnnotationSegmentSelect.getSelectedItem().equals(t)) {
			AnnotationSegmentSelect.setSelectedItem(t);
		}

	}

	// should be implemented by the evil KW
	public void jumpToSegmentChain(String id) {
		// muss man vermutlich abfangen. Annahmen, Annahmen.
		DisplaySegmentSelect.setSelectedItem("sc");
		app.setShowSegment("sc");
		app.setTagSegment(tagSegment);
		if (app.getIdMap().containsKey(id)) {
			if (app.getShowSegments().contains(app.getIdMap().get(id))) {
				app.setSegmentShowing(app.getShowSegments().indexOf(
						app.getIdMap().get(id)));
				app.setTagindex(0);
			}
		}

		// JOptionPane
		// .showMessageDialog(
		// selectionPanel,
		// "Now Ocean should wryte code so that\nthe application jumpeth to\n segment chayne with ID "
		// + id);
	}

}
