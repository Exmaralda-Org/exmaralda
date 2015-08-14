/**
 *
 * 
 * 
 * "Smokey, my friend, you are entering a world of pain."
 * 
 * 								(Walter Sobchak)
 */

package org.exmaralda.coma.helpers;

// should be org.exmaralda.coma.handicaps

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.exmaralda.coma.models.AssignmentTableModel;
import org.exmaralda.coma.models.CCWSpeakerModel;
import org.exmaralda.coma.models.FileListTableModel;
import org.exmaralda.coma.models.TranscriptionMetadata;
import org.exmaralda.coma.root.ComaXMLOutputter;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.corpusbuild.TranscriptionSegmentor;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.exakt.search.SearchEvent;
import org.jdesktop.swingworker.SwingWorker;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class CCW extends javax.swing.JDialog implements PropertyChangeListener,
		TableModelListener, RowSorterListener {

	/**
	 * CCW steht für "Coma Creator Wizard". Falls jemand fragt. Frage: Wofür
	 * steht eigentlich CCW?
	 */
	private static final long serialVersionUID = 1L;

	private static final int FILE_NAME_SELECTION = 1;

	private static final int TRANSCRIPTION_SELECTION = 2;

	private static final int SEGMENTATION = 3;

	private static final int METADATA = 4;

	private static final int SPEAKERS = 5;

	private static final int SUMMARY = 6;

	private JPanel cardPanel;

	private JPanel ButtonPanel;

	private JButton cancelButton;

	private JButton prevButton;

	private JPanel card1;

	private JPanel card6;

	private JLabel jLabel6;

	private JLabel jLabel5;

	private JLabel jLabel4;

	private JScrollPane jScrollPane3;

	private JTextArea summary;

	private JScrollPane jScrollPane2;

	private JTable assignmentTable;

	private JComboBox segSelector;

	private JLabel segSelectLabel;

	private JLabel doSegLabel;

	private JCheckBox segmentTranscriptionsCheckbox;

	private JPanel main3;

	private JScrollPane jScrollPane1;

	private JTable transcriptionTable;

	private JButton selectComaFileButton;

	private JLabel comaFilenameLabel;

	private JLabel comaFileLabel;

	private JTextField corpusNameTextfield;

	private JLabel corpusNameLabel;

	private JPanel main1;

	private JLabel title6;

	private JLabel title5;

	private JLabel title4;

	private JLabel title3;

	private JLabel title2;

	private JLabel title1;

	private JLabel jLabel3;

	private JLabel jLabel2;

	private JLabel jLabel1;

	private JPanel stepsPanel;

	private JPanel card4;

	private JPanel card3;

	private JPanel card2;

	private JButton finishButton;

	private JButton nextButton;

	private JPanel card5;

	private int cardShowing;

	private int maxCards = 6;

	private int oldCard;

	private Vector<JLabel> labels;

	private JCheckBox errorListCheckbox;

	private JTextField segTargetOption2TextField;

	private JRadioButton segTargetOption2;

	private JRadioButton segTargetOption1;

	private JRadioButton segErrorOption3;

	private JRadioButton segErrorOption2;

	private JRadioButton segErrorOption1;

	private JLabel onErrorLabel;

	private JLabel Zielort;

	private JLabel suffixLabel;

	private JTextField segSuffixTextfield;

	private JButton deselectButton;

	private JCheckBox selectBasicButton;

	private JCheckBox selectSegmentedButton;

	private JPanel card2ButtonsPanel;

	private ProgressMonitor progressMonitor;

	public HashSet<File> files;

	private File comaFile;

	private countFilesTask countTask;
	public static final Preferences prefs = Preferences.userRoot().node(
			"org.exmaralda.coma");

	private HashMap<File, TranscriptionMetadata> transcriptions = new HashMap<File, TranscriptionMetadata>();

	private HashMap<String, HashMap<String, String>> speakerMetadata = new HashMap<String, HashMap<String, String>>(); // sprechermetadaten

	private FileListTableModel transcriptionTableModel;

	private HashMap<String, String> metadataFields = new HashMap<String, String>();

	private AssignmentTableModel assignmentTableModel;

	private CCWSpeakerModel speakerTableModel;

	private JComboBox speakerDistinctionCombo;

	private HashSet<String> speakerMetaFields;

	public File resultFile = null;

	private String speakerDistinction;

	private JTable speakerTable;

	private Vector<JComponent> segmentationComponents;

	private JComboBox commNameCombo;

	private JComboBox speakerPseudoCombo;

	private HashMap<String, String> actualDescription;

	private boolean transTableChanged = false;

	private boolean closeOnFinish;

	private HashMap<File, File> segmentedTranscriptions;

	private JLabel numberOfTranscriptionsLabel;

	private HashMap<File, File> fromSegmentation = new HashMap<File, File>();

	private HashMap<String, String> recMeta = new HashMap<String, String>();

	private HashMap<String, String> transMeta = new HashMap<String, String>();

	private HashMap<String, String> commMeta = new HashMap<String, String>();

	/**
	 * Auto-generated main method to display this JDialog
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				CCW inst = new CCW(frame);
				inst.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				inst.setVisible(true);
			}
		});
	}

	public CCW(JFrame frame, boolean closeOnFinish) {
		super(frame, Ui.getText("cmd.ImportTranscriptions"));
		this.closeOnFinish = closeOnFinish;
		initGUI();
	}

	public CCW(JFrame frame) {
		super(frame, Ui.getText("cmd.ImportTranscriptions"));
		initGUI();
	}

	private void initGUI() {
		try {
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			cardShowing = 1;
			oldCard = 1;
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			this.setModal(true);

			ButtonPanel = new JPanel();
			BoxLayout ButtonPanelLayout = new BoxLayout(ButtonPanel,
					javax.swing.BoxLayout.X_AXIS);
			ButtonPanel.setLayout(ButtonPanelLayout);
			getContentPane().add(ButtonPanel, BorderLayout.SOUTH);
			// ButtonPanel.setBorder(BorderFactory.createLineBorder(Color.black));

			cancelButton = new JButton();
			ButtonPanel.add(Box.createHorizontalGlue());
			ButtonPanel.add(cancelButton);
			cancelButton.setText(Ui.getText("cancel"));
			cancelButton.putClientProperty("JButton.buttonType", "segmented");
			cancelButton.putClientProperty("JButton.segmentPosition", "only");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					cancel();
				}
			});

			prevButton = new JButton();
			ButtonPanel.add(prevButton);
			prevButton.setText("< " + Ui.getText("prev"));
			prevButton.setEnabled(false);
			prevButton.putClientProperty("JButton.buttonType", "segmented");
			prevButton.putClientProperty("JButton.segmentPosition", "first");
			prevButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					changeCard(false);
				}
			});

			nextButton = new JButton();
			ButtonPanel.add(nextButton);
			nextButton.setText(Ui.getText("next") + " >");
			nextButton.putClientProperty("JButton.buttonType", "segmented");
			nextButton.putClientProperty("JButton.segmentPosition", "last");
			nextButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					changeCard(true);
				}
			});

			finishButton = new JButton();
			ButtonPanel.add(finishButton);
			finishButton.setText(Ui.getText("finish"));
			finishButton.putClientProperty("JButton.buttonType", "segmented");
			finishButton.putClientProperty("JButton.segmentPosition", "only");
			finishButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					finish();
				}
			});

			cardPanel = new JPanel();
			CardLayout cardPanelLayout = new CardLayout();
			cardPanel.setLayout(cardPanelLayout);
			cardPanel.setBorder(BorderFactory.createRaisedBevelBorder());
			getContentPane().add(cardPanel, BorderLayout.CENTER);

			initDirSelectionCard();
			initTranscriptionCard();
			initMetadataCard();
			initSegmentationCard();
			initSpeakerCard();
			initSummaryCard();

			// wizard-schritte an der seite

			stepsPanel = new JPanel();
			getContentPane().add(stepsPanel, BorderLayout.WEST);
			BoxLayout jPanel2Layout = new BoxLayout(stepsPanel,
					javax.swing.BoxLayout.Y_AXIS);
			// stepsPanel.setBackground(new java.awt.Color(255, 255, 255));
			// stepsPanel.setFont(new java.awt.Font("Dialog", 1, 12));
			// stepsPanel.setPreferredSize(new java.awt.Dimension(150, 380));
			stepsPanel.setLayout(jPanel2Layout);
			// stepsPanel.setSize(150, 380);

			jLabel1 = new JLabel();
			stepsPanel.add(jLabel1);
			stepsPanel.add(new JLabel(" "));
			jLabel1.setText("1. " + Ui.getText("ccw.fileName"));
			jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));

			jLabel2 = new JLabel();
			stepsPanel.add(jLabel2);
			jLabel2.setText("2. " + Ui.getText("transcriptions"));
			stepsPanel.add(new JLabel(" "));

			jLabel3 = new JLabel();
			stepsPanel.add(jLabel3);
			jLabel3.setText("3. " + Ui.getText("segmentation"));
			stepsPanel.add(new JLabel(" "));

			jLabel4 = new JLabel();
			stepsPanel.add(jLabel4);
			jLabel4.setText("4. " + Ui.getText("assignMetadata"));
			stepsPanel.add(new JLabel(" "));

			jLabel5 = new JLabel();
			stepsPanel.add(jLabel5);
			jLabel5.setText("5. " + Ui.getText("speakers"));
			stepsPanel.add(new JLabel(" "));

			jLabel6 = new JLabel();
			stepsPanel.add(jLabel6);
			jLabel6.setText("6. " + Ui.getText("summary"));
			stepsPanel.add(new JLabel(" "));
			this.pack();
			stepsPanel.setPreferredSize(new Dimension(
					stepsPanel.getWidth() + 10, stepsPanel.getHeight()));

			labels = new Vector<JLabel>();
			labels.add(jLabel1);
			labels.add(jLabel2);
			labels.add(jLabel3);
			labels.add(jLabel4);
			labels.add(jLabel5);
			labels.add(jLabel6);

			this.setSize(886, 430);
			((CardLayout) cardPanel.getLayout()).show(cardPanel, "card1");
			buttonCheck();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void initMetadataCard() {
		card4 = new JPanel();
		cardPanel.add(card4, "card4");
		BorderLayout jPanel1Layout2 = new BorderLayout();
		card4.setLayout(jPanel1Layout2);

		title4 = new JLabel();
		card4.add(title4, BorderLayout.NORTH);
		title4.setText(Ui.getText("assignMetadata"));
		title4.setBackground(new java.awt.Color(255, 255, 255));
		title4.setFont(new java.awt.Font("Dialog", 1, 16));
		title4.setOpaque(true);

		jScrollPane2 = new JScrollPane();
		card4.add(jScrollPane2, BorderLayout.CENTER);
		jScrollPane2.setPreferredSize(new java.awt.Dimension(736, 360));

		assignmentTableModel = new AssignmentTableModel();
		assignmentTableModel.addColumn("use");
		assignmentTableModel.addColumn("field");
		assignmentTableModel.addColumn("target name");
		assignmentTableModel.addColumn("target");
		assignmentTable = new JTable(assignmentTableModel);

		jScrollPane2.setViewportView(assignmentTable);

		JLabel commNameLabel = new JLabel(Ui.getText("commNameFrom"));
		commNameCombo = new JComboBox();
		commNameCombo.addItem("one file -> one communication");
		JPanel commNamePanel = new JPanel();
		commNamePanel.setLayout(new BoxLayout(commNamePanel, BoxLayout.X_AXIS));
		commNamePanel.add(commNameLabel);
		commNamePanel.add(commNameCombo);
		card4.add(commNamePanel, BorderLayout.SOUTH);

	}

	/**
	 * 
	 */
	private void initTranscriptionCard() {
		card2 = new JPanel();
		cardPanel.add(card2, "card2");
		BorderLayout jPanel1Layout1 = new BorderLayout();
		card2.setLayout(jPanel1Layout1);
		title2 = new JLabel();
		card2.add(title2, BorderLayout.NORTH);
		title2.setText(Ui.getText("ccw.selectTranscriptions"));
		title2.setBackground(new java.awt.Color(255, 255, 255));
		title2.setFont(new java.awt.Font("Dialog", 1, 16));
		title2.setOpaque(true);
		jScrollPane1 = new JScrollPane();
		card2.add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.setPreferredSize(new java.awt.Dimension(736, 360));

		transcriptionTableModel = new FileListTableModel();
		transcriptionTableModel.addColumn("X");
		transcriptionTableModel.addColumn(Ui.getText("file"));
		transcriptionTableModel.addColumn("Seg");
		transcriptionTable = new JTable();
		jScrollPane1.setViewportView(transcriptionTable);
		transcriptionTable.setModel(transcriptionTableModel);
		transcriptionTable.getModel().addTableModelListener(this);

		TableRowSorter rs = new TableRowSorter<TableModel>(
				transcriptionTableModel);
		rs.addRowSorterListener(this);

		transcriptionTable.setRowSorter(rs);

		card2ButtonsPanel = new JPanel();
		BoxLayout card2ButtonsPanelLayout = new BoxLayout(card2ButtonsPanel,
				javax.swing.BoxLayout.X_AXIS);
		card2ButtonsPanel.setLayout(card2ButtonsPanelLayout);
		card2.add(card2ButtonsPanel, BorderLayout.SOUTH);

		selectSegmentedButton = new JCheckBox(
				Ui.getText("cmd.setSegmentedFlags"), true);
		card2ButtonsPanel.add(selectSegmentedButton);
		selectSegmentedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (selectSegmentedButton.isSelected()) {
					transcriptionTableModel.selectSegmented(true);

				} else {
					transcriptionTableModel.selectSegmented(false);

				}
				buttonCheck();
			}
		});

		selectBasicButton = new JCheckBox(Ui.getText("cmd.setBasicFlags"), true);
		card2ButtonsPanel.add(selectBasicButton);
		selectBasicButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (selectBasicButton.isSelected()) {
					transcriptionTableModel.selectBasic(true);

				} else {
					transcriptionTableModel.selectBasic(false);

				}
				buttonCheck();

			}
		});
	}

	/**
	 * 
	 */
	private void initSpeakerCard() {
		card5 = new JPanel();
		BorderLayout card5Layout = new BorderLayout();
		card5.setLayout(card5Layout);
		cardPanel.add(card5, "card5");

		title5 = new JLabel();
		card5.add(title5, BorderLayout.NORTH);
		title5.setText(Ui.getText("speakers"));
		title5.setBackground(new java.awt.Color(255, 255, 255));
		title5.setFont(new java.awt.Font("Dialog", 1, 16));
		title5.setOpaque(true);

		speakerTableModel = new CCWSpeakerModel();
		speakerTableModel.addColumn("X");
		speakerTableModel.addColumn("sigle");
		speakerTable = new JTable(speakerTableModel);

		card5.add(new JScrollPane(speakerTable), BorderLayout.CENTER);
		JPanel sdp = new JPanel();
		sdp.setLayout(new BoxLayout(sdp, BoxLayout.X_AXIS));
		speakerDistinctionCombo = new JComboBox();
		speakerDistinctionCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String distinction = (String) cb.getSelectedItem();
				speakerDistinctionChanged(distinction);
			}
		});
		speakerPseudoCombo = new JComboBox();

		sdp.add(new JLabel("unique speaker distinction:"));
		sdp.add(speakerDistinctionCombo);
		sdp.add(new JLabel("pseudo/name:"));
		sdp.add(speakerPseudoCombo);
		card5.add(sdp, BorderLayout.SOUTH);
	}

	/**
	 * the card for corpusname and directory-selection
	 */
	private void initDirSelectionCard() {
		card1 = new JPanel();
		cardPanel.add(card1, "card1");
		card1.setLayout(new BorderLayout());

		title1 = new JLabel();
		card1.add(title1, BorderLayout.NORTH);
		title1.setText(Ui.getText("ccw.corpusName") + ", "
				+ Ui.getText("ccw.fileName"));
		title1.setFont(new java.awt.Font("Dialog", 1, 16));
		title1.setOpaque(true);
		title1.setBackground(new java.awt.Color(255, 255, 255));

		numberOfTranscriptionsLabel = new JLabel();
		numberOfTranscriptionsLabel.setEnabled(false);

		main1 = new JPanel();
		main1.setLayout(new GridLayout(0, 2));
		JPanel layouter = new JPanel(new BorderLayout());
		card1.add(layouter, BorderLayout.CENTER);
		layouter.add(main1, BorderLayout.NORTH);
		main1.add(new JLabel(""));
		main1.add(new JLabel(""));
		corpusNameLabel = new JLabel();
		main1.add(corpusNameLabel);
		corpusNameLabel.setText(Ui.getText("ccw.corpusName"));

		corpusNameTextfield = new JTextField();
		main1.add(corpusNameTextfield);
		corpusNameTextfield.setText(Ui.getText("unnamed"));
		main1.add(new JLabel(""));
		main1.add(new JLabel(""));

		comaFileLabel = new JLabel();
		main1.add(comaFileLabel);
		comaFileLabel.setText(Ui.getText("ccw.fileName"));

		selectComaFileButton = new JButton();
		main1.add(selectComaFileButton);
		selectComaFileButton.setText(Ui.getText("browse"));
		selectComaFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectComaFile();
			}
		});
		main1.add(new JLabel(""));
		main1.add(new JLabel(""));
		main1.add(new JLabel(""));
		main1.add(numberOfTranscriptionsLabel);
	}

	/**
	 * 
	 */
	private void initSummaryCard() {
		card6 = new JPanel();
		cardPanel.add(card6, "card6");
		BorderLayout jPanel6Layout = new BorderLayout();
		card6.setLayout(jPanel6Layout);

		title6 = new JLabel();
		card6.add(title6, BorderLayout.NORTH);
		title6.setText(Ui.getText("summary"));
		title6.setBackground(new java.awt.Color(255, 255, 255));
		title6.setFont(new java.awt.Font("Dialog", 1, 16));
		title6.setOpaque(true);

		jScrollPane3 = new JScrollPane();
		card6.add(jScrollPane3, BorderLayout.CENTER);
		jScrollPane3.setPreferredSize(new java.awt.Dimension(736, 360));

		summary = new JTextArea();
		jScrollPane3.setViewportView(summary);
		summary.setText("jTextArea1");

	}

	public void tableChanged(TableModelEvent e) {
		transTableChanged = true;
		nextButton
				.setEnabled(transcriptionTableModel.getSelectedFiles().size() > 0);
	}

	/**
	 * 
	 */
	protected void finish() {
		// added by the evil TS, modified by the even more evil KW
		if (segmentTranscriptionsCheckbox.isSelected()) {
			doSegmentation();
			System.out.println("------ DoSegmentation returned.");
		} else {
			// the ev'lishness ends here
			outputAndClose();
		}
	}

	public void outputAndClose() {
		ComaXMLOutputter outputter = new ComaXMLOutputter();
		Document coma = getComaDocument();
		FileOutputStream out;
		try {
			out = new FileOutputStream(comaFile);
			outputter.output(coma, out);
			out.close();
			resultFile = comaFile;
		} catch (Exception err) {
			err.printStackTrace();
			resultFile = null;
		}
		if (closeOnFinish) {
			this.dispose();
		} else {
			finishButton.setEnabled(false);
		}
	}

	/**
	 * 
	 */
	protected void cancel() {
		this.dispose();
	}

	/**
	 * 
	 */
	private void buttonCheck() {
		nextButton.setEnabled(false);
		prevButton.setEnabled(false);
		finishButton.setEnabled(false);
		switch (cardShowing) {
		case FILE_NAME_SELECTION:
			prevButton.setEnabled(false);
			if (transcriptions.size() > 0) {
				nextButton.setEnabled(true);
			}
			break;
		case TRANSCRIPTION_SELECTION:
			prevButton.setEnabled(true);
			if (transcriptionTableModel.getSelectedFiles().size() > 0) {
				nextButton.setEnabled(true);
			}
			break;
		case SEGMENTATION:
			prevButton.setEnabled(true);
			nextButton.setEnabled(transcriptions.size() > 0);
			break;
		case METADATA:
			prevButton.setEnabled(true);
			nextButton.setEnabled(true);
			break;
		case SPEAKERS:

			prevButton.setEnabled(true);
			if (speakerTableModel.getSelectedSpeakers().size() > 0) {
				nextButton.setEnabled(true);
			}
			break;
		case SUMMARY:

			prevButton.setEnabled(true);
			finishButton.setEnabled(true);
			break;
		}
	}

	/**
	 * 
	 */
	private void changeCard(boolean plusminus) {
		oldCard = cardShowing;
		cardShowing += (plusminus ? 1 : -1);
		buttonCheck();
		cardCheck(cardShowing);
		String cardToShow = "card" + cardShowing;
		((CardLayout) cardPanel.getLayout()).show(cardPanel, cardToShow);
		unbold(oldCard - 1);
		bold(cardShowing - 1);
	}

	/**
	 * updates the display on the card to show before it is actually shown
	 */
	private void cardCheck(int card) {
		switch (card) {
		case TRANSCRIPTION_SELECTION:
			Vector<Vector<Object>> aussen = new Vector<Vector<Object>>();
			Vector<String> header = new Vector<String>();
			header.add("Include");
			header.add("file");
			header.add("path");
			header.add("segmented");
			for (File f : transcriptions.keySet()) {
				Vector<Object> innen = new Vector<Object>();
				innen.add(new Boolean(true));
				innen.add(f);
				innen.add(f.getParent());
				innen.add(transcriptions.get(f).isSegmented());
				aussen.add(innen);
			}
			transcriptionTableModel.setDataVector(aussen, header);
			transcriptionTableModel.fireTableDataChanged();
			transcriptionTable.getColumnModel().getColumn(0).setMinWidth(40);
			transcriptionTable.getColumnModel().getColumn(0).setMaxWidth(40);
			transcriptionTable.getColumnModel().getColumn(3).setMinWidth(40);
			transcriptionTable.getColumnModel().getColumn(3).setMaxWidth(40);
			transcriptionTable.getTableHeader().repaint();
			buttonCheck();

			break;
		case METADATA: // metadaten-zuordnung
			if ((assignmentTableModel.getRowCount() == 0)
					|| (transTableChanged == true)) {
				transTableChanged = false;
				assignmentTableModel.clear();
				for (File f : transcriptionTableModel.getSelectedFiles()) {
					TranscriptionMetadata transmeta = transcriptions.get(f);
					HashMap<String, String> meta = transmeta.getMetadata();
					for (String name : meta.keySet()) {
						metadataFields.put(name, "11" + name);
					}
				}
				TreeSet<String> ts = new TreeSet<String>(
						metadataFields.keySet());
				for (String key : ts) {
					Vector rowToAdd = new Vector();
					rowToAdd.add(true);
					rowToAdd.add(key);
					rowToAdd.add((key.startsWith("ud_") ? key.substring(3)
							: key));
					rowToAdd.add("communication");
					assignmentTableModel.addRow(rowToAdd);
					commNameCombo.addItem(key);
				}
				if (metadataFields.keySet().contains("transcription-name")) {
					commNameCombo.setSelectedItem("transcription-name");
				}
				JComboBox comboBox = new JComboBox();
				comboBox.addItem("communication");
				comboBox.addItem("transcription");
				comboBox.addItem("recording");
				TableColumn col = assignmentTable.getColumnModel().getColumn(3);
				col.setCellEditor(new DefaultCellEditor(comboBox));
				assignmentTable.getColumnModel().getColumn(0).setMinWidth(40);
				assignmentTable.getColumnModel().getColumn(0).setMaxWidth(40);
				assignmentTable.getTableHeader().repaint();
			}
			break;
		case SPEAKERS:
			if (speakerMetaFields == null) {
				speakerDistinction = "@abbreviation";
				speakerMetaFields = new HashSet<String>();
				for (File f : transcriptionTableModel.getSelectedFiles()) {
					TranscriptionMetadata transmeta = transcriptions.get(f);
					for (String s : transmeta.getSpeakers().keySet()) {
						HashMap<String, String> speakerMeta = transmeta
								.getSpeakers().get(s);
						for (String t : speakerMeta.keySet()) {
							speakerMetaFields.add(t);
						}
					}
				}
				for (String s : new TreeSet<String>(speakerMetaFields)) {
					if (!(s.equals("@sex"))) {
						speakerDistinctionCombo.addItem(s);
					}
					speakerPseudoCombo.addItem(s);
				}
				speakerDistinctionChanged("@abbreviation");
			}
			break;
		case SUMMARY:
			String st = "";
			st += "Creating corpus " + corpusNameTextfield.getText()
					+ " in file " + comaFile.getPath() + "\n";
			st += "Transcriptions selected: "
					+ transcriptionTableModel.getSelectedFiles().size() + "\n";
			st += "\nMappings: \nCommunications:\n";
			for (String s : assignmentTableModel.getMeta("communication")
					.keySet()) {
				st += s + "->"
						+ assignmentTableModel.getMeta("communication").get(s)
						+ "\n";
			}
			st += "\nTranscriptions:\n";
			for (String s : assignmentTableModel.getMeta("transcription")
					.keySet()) {
				st += s + "->"
						+ assignmentTableModel.getMeta("transcription").get(s)
						+ "\n";
			}
			st += "\nRecordings:\n";
			for (String s : assignmentTableModel.getMeta("recording").keySet()) {
				st += s + "->"
						+ assignmentTableModel.getMeta("recording").get(s)
						+ "\n";
			}
			st += "\nUnique speakers selected:"
					+ speakerTableModel.getSelectedSpeakers().size() + "\n";
			st += "\nSegmentation options:\n";

			summary.setText(st);
			break;

		}

	}

	public String getRelativePath(File from, File to) {
		if (from == null) {
			from = comaFile;
		}
		if (to == null) {
			return null;
		}
		URI fromURI = from.getParentFile().toURI();
		URI toURI = to.toURI();
		URI relativeURI = fromURI.relativize(toURI);
		return relativeURI.toString();

	}

	/**
	 * 
	 */
	private void speakerDistinctionChanged(String distinction) {
		speakerDistinction = distinction;
		HashSet<String> uniqueSpeakers = new HashSet<String>();
		for (File f : transcriptionTableModel.getSelectedFiles()) {
			TranscriptionMetadata transmeta = transcriptions.get(f);
			for (String s : transmeta.getSpeakers().keySet()) {
				// HashMap<String, String> speakerMeta = transmeta.getSpeakers()
				// .get(s);
				uniqueSpeakers.add(transmeta.getSpeakers().get(s)
						.get(distinction));
			}
		}
		Vector<String> ident = new Vector<String>();
		ident.add("use");
		ident.add(distinction);
		speakerTableModel.setDataVector(new Vector(), ident);
		if (uniqueSpeakers.size() > 0) {
			TreeSet<String> ts = new TreeSet<String>();
			try { // sowas macht man m?glicherweise nicht, aber jetzt ist's mir
					// grad egal! ist ja nicht meine schuld, wenn das doofe
					// treeset keine nullen vergleichen kann
				ts.addAll(uniqueSpeakers);
				for (String s : /* new TreeSet<String>( */ts)/* ) */{
					Vector sv = new Vector();
					if (s == null) {
						sv.add(false);
					} else {
						sv.add(s.length() > 0);
					}
					sv.add(s);
					speakerTableModel.addRow(sv);
				}
			} catch (Exception e) {
				for (String s : uniqueSpeakers) {
					Vector sv = new Vector();
					if (s == null) {
						sv.add(false);

					} else {
						sv.add(s.length() > 0);
					}
					sv.add(s);
					speakerTableModel.addRow(sv);
				}
			}
		}
		speakerTable.getColumnModel().getColumn(0).setMinWidth(40);
		speakerTable.getColumnModel().getColumn(0).setMaxWidth(40);
		speakerTable.getTableHeader().repaint();

		buttonCheck();
	}

	private Document getComaDocument() {
		Element newCorp = new Element("Corpus");
		Document comaCorpus = new Document(newCorp);
		Namespace xsi = Namespace.getNamespace("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		comaCorpus.setRootElement(newCorp);
		newCorp.setAttribute("Name", corpusNameTextfield.getText());
		newCorp.setAttribute("Id", new GUID().makeID());
		newCorp.setAttribute("noNamespaceSchemaLocation",
				"http://www.exmaralda.org/xml/comacorpus.xsd", xsi);
		String sdx = "";
		System.out.println(speakerDistinction);
		if (speakerDistinction.startsWith("ud_")) {
			sdx = "//speaker/ud-speaker-information/ud-information[@attribute-name=\""
					+ speakerDistinction.substring(3) + "\"]";
		} else if (speakerDistinction.startsWith("@")) {
			sdx = "//speaker/" + speakerDistinction.substring(1);
		} else {
			// TODO HIER STIMMT WAS NICHT!
			sdx = "//speaker/" + speakerDistinction;
		}
		System.out.println(sdx);

		newCorp.setAttribute("uniqueSpeakerDistinction", sdx);
		Element corpusDataElement = new Element("CorpusData");
		newCorp.addContent(corpusDataElement);
		// schritt 1: communications anhand von schl?ssel bestimmen
		HashMap<String, Vector<File>> comms = new HashMap<String, Vector<File>>();
		int count = 0;
		Vector<File> workFiles = transcriptionTableModel.getSelectedFiles();
		System.out.println("***** " + workFiles.size() + " workFiles");
		if (segmentedTranscriptions != null) {
			// workFiles.addAll(Arrays.asList(segmentedTranscriptions));
			fromSegmentation = new HashMap<File, File>();
			for (File f : segmentedTranscriptions.keySet()) {
				File keyFile = new File(new TranscriptionMetadata(f, false)
						.getMachineTags().get("EXB-SOURCE"));
				fromSegmentation.put(keyFile, f);
			}
		}
		System.out.println("***** " + fromSegmentation.size()
				+ " fromSegmentation");

		for (File f : workFiles) {
			count++;
			if (commNameCombo.getSelectedItem().equals(
					"one file -> one communication")) {
				Vector<File> v = new Vector<File>();
				v.add(f);
				if (fromSegmentation.get(f) != null) {
					v.add(fromSegmentation.get(f));
				}
				comms.put("unnamed" + count, v);
			} else {
				Object selectedCommName = commNameCombo.getSelectedItem();
				String key = transcriptions.get(f).getMetadata()
						.get(selectedCommName);
				String cName = (key == null) ? "unnamed" + count
						: transcriptions.get(f).getMetadata()
								.get(selectedCommName);
				if (comms.get(cName) == null) {
					Vector<File> v = new Vector<File>();
					v.add(f);
					// NEU
					if (fromSegmentation.get(f) != null) {
						v.add(fromSegmentation.get(f));
					}
					// NICHT MEHR SO NEU
					comms.put(cName, v);
				} else {
					comms.get(cName).add(f);
					if (fromSegmentation.get(f) != null) {
						comms.get(cName).add(fromSegmentation.get(f));
					}
				}
			}
		}
		// schritt 2: sprecher (speakers) anhand von schluesseln bestimmen und
		// comms (commSpeakers) zuordnen
		HashMap<String, Vector<String>> speakers = new HashMap<String, Vector<String>>();
		HashMap<String, HashSet<String>> commSpeakers = new HashMap<String, HashSet<String>>();
		HashMap<String, String> speakerNames = new HashMap<String, String>();
		for (String c : comms.keySet()) {
			commSpeakers.put(c, new HashSet<String>());
			for (File f : comms.get(c)) {
				// for (File f : transcriptions.keySet()) { // jede einzelne
				// transkription
				for (String sp : transcriptions.get(f).getSpeakers().keySet()) { // jeder
					// einzelne
					// sprecher
					String spName = transcriptions.get(f).getSpeakers().get(sp)
							.get(speakerDistinctionCombo.getSelectedItem());

					if (speakers.get(spName) == null) {
						speakerNames.put(spName, "S" + new GUID().makeID());
						Vector<String> v = new Vector<String>();
						v.add(sp);
						speakers.put(spName, v);
					} else {
						speakers.get(spName).add(sp);
					}
					commSpeakers.get(c).add(spName);
				}
			}
		}

		HashMap<String, Integer> commNames = new HashMap<String, Integer>();
		commMeta = assignmentTableModel.getMeta("communication");
		recMeta = assignmentTableModel.getMeta("recording");
		transMeta = assignmentTableModel.getMeta("transcription");
		// Comms machen
		for (String c : comms.keySet()) {
			HashSet<String> commLanguages = new HashSet<String>();

			Element commElement = new Element("Communication");
			commElement.setAttribute("Name", c);
			commElement.setAttribute("Id", "C" + new GUID().makeID());
			Element commDescriptionElement = new Element("Description");
			if (commMeta.size() > 0) {
				actualDescription = new HashMap<String, String>();
				for (File trFile : comms.get(c)) {
					TranscriptionMetadata cMeta = transcriptions.get(trFile);

					//
					for (String metaKey : cMeta.getMetadata().keySet()) {
						if (metaKey.startsWith("@language-used"))
							commLanguages.add(cMeta.getMetadata().get(metaKey));

						if (commMeta.containsKey(metaKey)) {
							actualDescription.put(commMeta.get(metaKey), cMeta
									.getMetadata().get(metaKey));
						}
					}
				}
				for (String k : new TreeMap<String, String>(actualDescription)
						.keySet()) { // sortiert!
					Element key = new Element("Key");
					key.setAttribute("Name", k);
					key.setText(actualDescription.get(k));
					commDescriptionElement.addContent(key);
				}

			}

			commElement.addContent(commDescriptionElement);
			Element se = new Element("Setting");
			//

			for (String sp : commSpeakers.get(c)) {
				// for (String trspk : speakers.get(sp)) {
				// for (String spM : speakerMetadata.get(trspk).keySet()) {
				// // metadaten
				// // f�r
				// // diesen
				// // sprecher
				// if (speakerMetadata.get(trspk) != null) { // nur, wenn
				// // der
				// // sprecher
				// // metadaten
				// // hat
				//
				// if (spM.startsWith("@language-used")) {
				// commLanguages.add(speakerMetadata.get(trspk)
				// .get(spM));
				// }
				// }
				// }
				// }
				Element pe = new Element("Person");
				pe.setText(speakerNames.get(sp));
				se.addContent(pe);
			}
			commElement.addContent(se);
			// recordings zuordnen
			HashMap<String, HashSet<File>> mediaFiles = new HashMap<String, HashSet<File>>();
			Element recDescriptionElement = new Element("Description");
			for (File f : comms.get(c)) {
				if (transcriptions.get(f).getMediaFiles().size() > 0) {
					for (String s : transcriptions.get(f).getMediaFiles()) {
						if (mediaFiles.get(s) == null) {
							mediaFiles.put(s, new HashSet<File>());
						}
						mediaFiles.get(s).add(f);
						System.out.println(s);
					}
				}

				actualDescription = new HashMap<String, String>();
				if (recMeta.size() > 0) {
					TranscriptionMetadata cMeta = transcriptions.get(f);
					if (cMeta != null) {
						for (String metaKey : cMeta.getMetadata().keySet()) {
							if (recMeta.containsKey(metaKey)) {
								actualDescription.put(recMeta.get(metaKey),
										cMeta.getMetadata().get(metaKey));
							}
						}
					}
				}
			}
			for (String k : new TreeMap<String, String>(actualDescription)
					.keySet()) { // sortiert!
				Element key = new Element("Key");
				key.setAttribute("Name", k);
				key.setText(actualDescription.get(k));
				recDescriptionElement.addContent(key);
			}
			if (mediaFiles.size() > 0) {
				Element rec = new Element("Recording");
				rec.setAttribute("Id", "R" + new GUID().makeID());
				File nf = new File(mediaFiles.keySet().iterator().next());
				int whereDot = nf.getName().lastIndexOf('.');
				if (whereDot > 0 && whereDot <= nf.getName().length() - 2) {
					rec.addContent(new Element("Name").setText(nf.getName()
							.substring(0, whereDot)));
				} else {
					rec.addContent(new Element("Name").setText(nf.getName()));
				}
				for (String mf : mediaFiles.keySet()) {
					HashSet<String> relPaths = new HashSet<String>();
					HashSet<File> absFiles = new HashSet<File>();
					for (File trf : mediaFiles.get(mf)) {
						// System.out.println("#" + mf + "#");
						File actualMediaFile = null;
						if (new File(mf).exists()) { // is absolute
							actualMediaFile = new File(mf);
						} else if (new File(trf.getParent() + File.separator
								+ mf).exists()) { // is relative
							actualMediaFile = new File(trf.getParent()
									+ File.separator + mf);
						}

						// System.out.println(comaFile + " " + actualMediaFile);
						String relativePath = getRelativePath(comaFile,
								actualMediaFile);
						if ((!relPaths.contains(relativePath))
								&& (!absFiles.contains(actualMediaFile))) {
							Element media = new Element("Media");
							media.setAttribute("Id", "M" + new GUID().makeID());
							Element newDescType = new Element("Key");
							newDescType.setAttribute("Name", "Type");
							newDescType.setText("digital");
							Element newDesc = new Element("Description");
							newDesc.addContent(newDescType);
							media.addContent(newDesc);
							Element newNSLink = new Element("NSLink");
							newNSLink.setText((relativePath == null ? mf
									: relativePath));
							media.addContent(newNSLink);
							rec.addContent(media);
							relPaths.add(relativePath);
							absFiles.add(actualMediaFile);
						}
					}
				}
				rec.addContent(recDescriptionElement);
				commElement.addContent(rec);
			}

			// added by the evil TS

			// end addition by the evil TS

			// transkriptionen zuordnen
			for (File f : comms.get(c)) {
				Element tr = new Element("Transcription");
				tr.setAttribute("Id", transcriptions.get(f).getId());
				tr.addContent(new Element("Name").setText(f.getName()
						.substring(0, f.getName().lastIndexOf("."))));
				tr.addContent(new Element("Filename").setText(f.getName()));
				tr.addContent(new Element("NSLink").setText(getRelativePath(
						null, f)));
				Element transDescription = new Element("Description");
				tr.addContent(transDescription);
				Element k = new Element("Key");
				k.setAttribute("Name", "segmented");
				k.setText(transcriptions.get(f).isSegmented() ? "true"
						: "false");
				transDescription.addContent(k);
				for (String mtk : transcriptions.get(f).getMachineTags()
						.keySet()) {
					Element key = new Element("Key");
					key.setAttribute("Name", "# " + mtk);
					key.setText(transcriptions.get(f).getMachineTags().get(mtk));
					transDescription.addContent(key);
				}

				if (transMeta.size() > 0) {
					actualDescription = new HashMap<String, String>();
					TranscriptionMetadata cMeta = transcriptions.get(f);
					for (String metaKey : cMeta.getMetadata().keySet()) {
						if (transMeta.containsKey(metaKey)) {
							actualDescription.put(transMeta.get(metaKey), cMeta
									.getMetadata().get(metaKey));
						}
					}
					for (String ks : actualDescription.keySet()) {
						Element key = new Element("Key");
						key.setAttribute("Name", ks);
						key.setText(actualDescription.get(ks));
						transDescription.addContent(key);
					}

				}

				tr.addContent(new Element("Availability"));
				tr.getChild("Availability").addContent(
						new Element("Available").setText("false"));
				tr.getChild("Availability").addContent(
						new Element("ObtainingInformation"));
				commElement.addContent(tr);
			}

			for (String l : commLanguages) {
				Element lang = new Element("Language");
				lang.addContent(new Element("LanguageCode").setText(l));
				commElement.addContent(lang);
			}
			corpusDataElement.addContent(commElement);
		}
		// spks machen
		// sprecher extrahieren

		for (String sp : speakers.keySet()) {
			HashMap<String, String> myMeta = new HashMap<String, String>(); // Metadaten
			HashSet<String> langs = new HashSet<String>();
			HashMap<String, Integer> myMetaCount = new HashMap<String, Integer>();
			String sexValue = "male";
			HashMap<String, String> langHashMap = new HashMap<String, String>();
			Vector<Element> languages = new Vector<Element>();
			int l1c = 0;
			int l2c = 0;
			for (String spS : speakers.get(sp)) { // alle einzelnen sprecher aus
				for (String spM : speakerMetadata.get(spS).keySet()) {
					if (speakerMetadata.get(spS) != null) {
						sexValue = speakerMetadata.get(spS).get("@sex");
						if (spM.equals("@abbreviation")) {
							myMeta.put(spM, speakerMetadata.get(spS).get(spM));

						}
						if (spM.startsWith("@l1")) {
							if (langHashMap.containsKey("l1")) {
								// l1 gibt es schon!
								if (!(langHashMap.get("l1")
										.equals(speakerMetadata.get(spS).get(
												spM)))) {
									Element lang = new Element("Language");
									langs.add(speakerMetadata.get(spS).get(spM));
									lang.addContent(new Element("LanguageCode")
											.setText(speakerMetadata.get(spS)
													.get(spM)));
									lang.addContent(new Element("Description"));
									lang.setAttribute("Type", "L1(" + l1c + ")");
									l1c++;
									languages.add(lang);
								}
							} else {
								langHashMap.put("l1", speakerMetadata.get(spS)
										.get(spM));
								langs.add(speakerMetadata.get(spS).get(spM));

								Element lang = new Element("Language");
								lang.addContent(new Element("LanguageCode")
										.setText(speakerMetadata.get(spS).get(
												spM)));
								lang.addContent(new Element("Description"));
								lang.setAttribute("Type", "L1");
								languages.add(lang);
							}
						}
						if (spM.startsWith("@l2")) {

							if (langHashMap.containsKey("l2")) {
								if (!langs.contains(speakerMetadata.get(spS)
										.get(spM))) {
									langHashMap.put("l2",
											speakerMetadata.get(spS).get(spM));
									langs.add(speakerMetadata.get(spS).get(spM));
									Element lang = new Element("Language");
									lang.addContent(new Element("LanguageCode")
											.setText(speakerMetadata.get(spS)
													.get(spM)));
									lang.addContent(new Element("Description"));
									lang.setAttribute("Type", "L2(" + l2c + ")");

									l2c++;
									languages.add(lang);
								}
							} else {
								langHashMap.put("l2", speakerMetadata.get(spS)
										.get(spM));
								langs.add(speakerMetadata.get(spS).get(spM));
								Element lang = new Element("Language");
								lang.addContent(new Element("LanguageCode")
										.setText(speakerMetadata.get(spS).get(
												spM)));
								lang.addContent(new Element("Description"));
								lang.setAttribute("Type", "L2");
								languages.add(lang);
							}
						}

						if (speakerMetadata.get(spS).get(spM) != null) { // nur,  wenn der schlüssel auch einen wert hat
							if (!(spM.startsWith("@"))) {

								if (myMetaCount.get(spM) == null) { // diesen schlüssel gibt es noch nicht
									myMetaCount.put(spM, 0);
									myMeta.put(spM, speakerMetadata.get(spS)
											.get(spM));
								} else { // den schl�ssel gibt es schon
									boolean difValue = true;
									if ((myMeta.get(spM).equals(speakerMetadata
											.get(spS).get(spM)))) {
										difValue = false;
									}
									for (int i = 0; i < myMetaCount.get(spM); i++) {
										if ((myMeta.get(spM + i)
												.equals(speakerMetadata
														.get(spS).get(spM)))) {
											difValue = false;
										}
									}
									if (difValue) { // neuer wert
										myMeta.put(
												spM + myMetaCount.get(spM),
												speakerMetadata.get(spS).get(
														spM));
										myMetaCount.put(spM,
												myMetaCount.get(spM) + 1);
									}
								}
							}
						}
					}
				}
			}
			Element spe = new Element("Speaker");
			spe.setAttribute("Id", speakerNames.get(sp));
			spe.addContent(new Element("Sigle").setText(sp));
			spe.addContent(new Element("Pseudo").setText(myMeta
					.get(speakerPseudoCombo.getSelectedItem())));
			spe.addContent(new Element("Sex").setText(sexValue));
			// spe.addContent(new Element("Location"));
			Element spD = new Element("Description");
			for (String ms : myMeta.keySet()) {
				Element spDK = new Element("Key");
				spDK.setAttribute("Name",
						(ms.startsWith("ud_") ? ms.substring(3) : ms));
				spDK.setText(myMeta.get(ms));
				spD.addContent(spDK);
			}
			spe.addContent(spD);
			for (Element lang : languages) {
				spe.addContent(lang);
			}
			corpusDataElement.addContent(spe);
		}
		return comaCorpus;
	}

	/**
	 * bolds the jlabel for the corresponding card
	 * 
	 * @param c
	 *            the index of the JLabel in labels to bold
	 */
	private void bold(int c) {
		JLabel l = labels.get(c);
		Font f = l.getFont();
		l.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
	}

	/**
	 * unbolds the jlabel for the corresponding card
	 * 
	 * @param c
	 *            the index of the JLabel in labels to unbold
	 */
	private void unbold(int c) {
		JLabel l = labels.get(c);
		Font f = l.getFont();
		l.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
	}

	/**
	 * opens a JFilechooser to specify the location and name of the Coma-File to
	 * generate
	 */
	protected void selectComaFile() {
		JFileChooser fc = new JFileChooser(new File(
				prefs.get("ccwdir", FileSystemView.getFileSystemView()
						.getHomeDirectory().getPath())), null);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setSelectedFile(new File(corpusNameTextfield.getText() + ".coma"));
		int dialogStatus = fc.showSaveDialog(this);
		if (dialogStatus == 0) {
			boolean doit = true;
			if (fc.getSelectedFile().exists()) {
				Object[] options = { Ui.getText("yes"), Ui.getText("no") };
				int n = JOptionPane
						.showOptionDialog(this, Ui.getText("fileexists"),
								"File exists", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[1]);
				if (n == 1) {
					doit = false;
				}
			}
			if (doit) {
				comaFile = fc.getSelectedFile();
				prefs.put("ccwdir", comaFile.getParentFile().getPath());

				selectComaFileButton.setText(fc.getSelectedFile().getPath());
				selectTranscriptions();
			}
		}

	}

	/**
	 * creates a list of all transcription files inside the selected directory
	 */
	private void selectTranscriptions() {
		files = new HashSet<File>();

		progressMonitor = new ProgressMonitor(this,
				Ui.getText("ccw.searching"), "", 0, 100);
		progressMonitor.setProgress(0);
		progressMonitor.setMillisToDecideToPopup(0);
		countTask = new countFilesTask();
		countTask.addPropertyChangeListener(this);
		countTask.execute();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			if (progress >= 100)
				progress = 99;
			progressMonitor.setProgress(progress);
			String message = progress + "% (" + transcriptions.size()
					+ " found)";
			progressMonitor.setNote(message);
			if (evt.getSource() == countFilesTask.class) {

				if (progressMonitor.isCanceled() || countTask.isDone()) {
					if (progressMonitor.isCanceled()) {
						countTask.cancel(true);
						System.out.println("was cancelled");
					} else {
						System.out.println("else kling");
						//
					}
				}
			}
		}

	}

	class countFilesTask extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() {
			progressMonitor.setNote("getting xml-files");
			setProgress(0);
			countFiles(comaFile.getParentFile());
			progressMonitor.setNote("finding transcriptions");
			setProgress(0);
			getTranscriptions();
			return null;
		}

		/**
		 * 
		 */
		private void getTranscriptions() {
			float count = 0;
			transcriptions.clear();
			for (File f : files) {
				count++;
				int pro = Math.round((100 / (float) files.size()) * count);
				TranscriptionMetadata t = new TranscriptionMetadata(f, false);
				if (t.isValid()) {
					transcriptions.put(f, t);
					for (String myS : t.getSpeakers().keySet()) {
						speakerMetadata.put(myS, t.getSpeakers().get(myS));
					}
				} else {
					// invalid
				}
				setProgress(pro);

			}
		}

		private void countFiles(File rootFile) {
			File[] theFiles = rootFile.listFiles();
			setProgress(1);
			for (int i = 0; i < theFiles.length; i++) {
				if (theFiles[i].isDirectory()) {
					countFiles(theFiles[i]);
				} else {
					if ((theFiles[i].getName().toLowerCase().endsWith(".xml"))
							|| (theFiles[i].getName().toLowerCase()
									.endsWith(".exb") || (theFiles[i].getName()
									.toLowerCase().endsWith(".exs")))) {
						files.add(theFiles[i]);
					}
				}
			}
		}

		public void done() {
			progressMonitor.setProgress(0);
			System.out.println("done");
			progressMonitor.close();
			transcriptionsCounted();
		}

	}

	/**
	 * 
	 */
	public void transcriptionsCounted() {
		if (transcriptions.size() > 0) {
			numberOfTranscriptionsLabel.setText(transcriptions.size()
					+ " Transcriptions");
			buttonCheck();
		} else {
			JOptionPane.showMessageDialog(this,
					"Keine EXMARaLDA-Transkriptionen gefunden.", "Fehler",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	// ************************* SEGMENTATION CODE *********************
	// WRITTEN BZW. MOVED BY THE EVIL TS, 05-08-2008

	ButtonGroup group1 = new ButtonGroup();

	ButtonGroup group2 = new ButtonGroup();

	private void initSegmentationCard() {
		card3 = new JPanel();
		cardPanel.add(card3, "card3");
		BorderLayout jPanel1Layout = new BorderLayout();
		card3.setLayout(jPanel1Layout);

		title3 = new JLabel();
		card3.add(title3, BorderLayout.NORTH);
		title3.setText(Ui.getText("segmentation"));
		title3.setBackground(new java.awt.Color(255, 255, 255));
		title3.setFont(new java.awt.Font("Dialog", 1, 16));
		title3.setOpaque(true);

		main3 = new JPanel();
		main3.setLayout(new GridLayout(0, 2));
		JPanel layouter = new JPanel(new BorderLayout());
		layouter.add(main3, BorderLayout.NORTH);
		// layouter.setBorder(BorderFactory.createLineBorder(Color.black));
		card3.add(layouter, BorderLayout.CENTER);

		doSegLabel = new JLabel(Ui.getText("cmd.segmentTranscriptions"));
		segmentTranscriptionsCheckbox = new JCheckBox();
		main3.add(doSegLabel);
		main3.add(segmentTranscriptionsCheckbox);

		segSelectLabel = new JLabel(Ui.getText("ccw.algorithm"));
		String[] segAlgs = { "default", "HIAT", "DIDA", "CHAT", "GAT", "IPA",
				"GENERIC", "cGAT_MINIMAL", "CHAT_MINIMAL" };
		ComboBoxModel segSelectorModel = new DefaultComboBoxModel(segAlgs);
		segSelector = new JComboBox();
		segSelector.setModel(segSelectorModel);
		segSelector.setEnabled(false);
		segSelector.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				segTargetOption2TextField.setText((String) ((JComboBox) e
						.getSource()).getSelectedItem() + "-segmented");
			}
		});
		main3.add(segSelectLabel);
		main3.add(segSelector);

		// fehlerbehandlung
		onErrorLabel = new JLabel(Ui.getText("ccw.onError"));
		segErrorOption1 = new JRadioButton("..." + Ui.getText("cancel"));
		segErrorOption2 = new JRadioButton("..." + Ui.getText("skip"));
		segErrorOption3 = new JRadioButton("..." + Ui.getText("ccw.useDefault"));
		errorListCheckbox = new JCheckBox(Ui.getText("ccw.writeErrorList"));

		main3.add(onErrorLabel);
		main3.add(segErrorOption1);
		main3.add(new JLabel(""));
		main3.add(segErrorOption2);
		main3.add(new JLabel(""));
		main3.add(segErrorOption3);
		main3.add(new JLabel(""));
		main3.add(errorListCheckbox);
		errorListCheckbox.setSelected(true);
		segErrorOption2.setSelected(true);
		group2.add(segErrorOption1);
		group2.add(segErrorOption2);
		group2.add(segErrorOption3);
		segErrorOption3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (((JRadioButton) evt.getSource()).isSelected()) {
					errorListCheckbox.setSelected(false);
				} else {
					errorListCheckbox.setSelected(true);

				}
			}
		});

		// zielort
		JLabel zielortLabel = new JLabel(Ui.getText("ccw.target"));
		segTargetOption1 = new JRadioButton(Ui.getText("ccw.sameDir"));
		segTargetOption1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (segSuffixTextfield.getText().length() == 0) {
					segSuffixTextfield.setText("_s");
				}
			}
		});
		segTargetOption2 = new JRadioButton(Ui.getText("ccw.newDir"));
		segTargetOption2TextField = new JTextField();
		segTargetOption2TextField.setText("segmentedTranscriptions");
		group1.add(segTargetOption1);
		group1.add(segTargetOption2);
		JPanel segTargetOption2Panel = new JPanel();
		segTargetOption2Panel.setLayout(new BorderLayout());
		segTargetOption2Panel.add(segTargetOption2, BorderLayout.WEST);
		segTargetOption2Panel.add(segTargetOption2TextField,
				BorderLayout.CENTER);
		main3.add(zielortLabel);
		main3.add(segTargetOption1);
		main3.add(new JLabel(""));
		main3.add(segTargetOption2Panel);
		segTargetOption1.setSelected(true);

		segSuffixTextfield = new JTextField();
		segSuffixTextfield.setText("_s");
		suffixLabel = new JLabel(Ui.getText("ccw.suffix"));
		main3.add(suffixLabel);
		main3.add(segSuffixTextfield);

		segmentationComponents = new Vector<JComponent>();
		segmentationComponents.add(segSelector);
		segmentationComponents.add(segTargetOption1);
		segmentationComponents.add(segTargetOption2);
		segmentationComponents.add(segErrorOption1);
		segmentationComponents.add(segErrorOption2);
		segmentationComponents.add(segErrorOption3);
		segmentationComponents.add(segSuffixTextfield);
		segmentationComponents.add(errorListCheckbox);

		segmentTranscriptionsCheckbox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				for (JComponent c : segmentationComponents) {

					c.setEnabled(segmentTranscriptionsCheckbox.isSelected());
				}
			}
		});
		for (JComponent c : segmentationComponents) {

			c.setEnabled(false);
		}

	}

	private void doSegmentation() {
		finishButton.setEnabled(false);

		// changed by the evil TS, 19-01-2009
		// File[] all = (File[]) (transcriptions.keySet().toArray(new File[0]));

		Vector<File> workFiles = transcriptionTableModel.getSelectedFiles();

		Vector<File> basicTr = new Vector<File>();
		// for (File tr : all) {
		for (File tr : workFiles) {
			if (!transcriptions.get(tr).isSegmented()) {
				basicTr.add(tr);
			}
		}
		org.exmaralda.common.corpusbuild.TranscriptionSegmentor segmentor = new org.exmaralda.common.corpusbuild.TranscriptionSegmentor(
				(File[]) (basicTr.toArray(new File[0])));

		// the index of the selected segmentation algorithm
		int sa = segSelector.getSelectedIndex();
		// the selected suffix
		String suff = segSuffixTextfield.getText();
		// the target directory for segmented transcriptions (null if segmented
		// transcriptions do shall be writteth to the same directory as the
		// basic transcription)
		File td = null;
		if (segTargetOption2.isSelected()) {
			td = new java.io.File(comaFile.getParent(),
					segTargetOption2TextField.getText());
		}
		// the method for error handling
		int eh = TranscriptionSegmentor.ERRORS_CANCEL;
		if (segErrorOption2.isSelected())
			eh = TranscriptionSegmentor.ERRORS_IGNORE;
		if (segErrorOption3.isSelected())
			eh = TranscriptionSegmentor.ERRORS_FAILSAFE;
		// whether or not to write an error list
		boolean we = errorListCheckbox.isSelected();
		// the file in which to write the error list
		File ep = new java.io.File(comaFile.getParent(),
				"SegmentationErrors.xml");

		// CHANGES BY THE VERY EVIL AND DANGEROUS TS
		// ON 07-APRIL-2010 AFTER J.C. : LET THERE
		// BE A PROGRESS DIALOG LEST IT DOTH LOOK LIKE YE OLDE SHYTE
		// CHANGES NOT REALISED : THE VERY EVIL AND DANGEROUS TS
		// IS TOO STUPID
		// TRIED AGAIN ON 15-04-2010
		final TranscriptionSegmentor theSegmentor = segmentor;
		final int f_sa = sa;
		final String f_suff = suff;
		final File f_td = td;
		final int f_eh = eh;
		final boolean f_we = we;
		final File f_ep = ep;
		final ProgressBarDialog pbd = new ProgressBarDialog(
				(JFrame) (getParent()), false);
		pbd.setLocationRelativeTo(this);
		pbd.setTitle("Segmentation... ");
		pbd.setAlwaysOnTop(true);
		theSegmentor.addSearchListener(pbd);
		pbd.setVisible(true);
		Thread segmentThread = new Thread() {
			@Override
			public void run() {
				try {
					theSegmentor.doSegmentation(f_sa, f_suff, f_td, f_eh, f_we,
							f_ep);
					System.out.println("------ segmentation done.");
					segmentedTranscriptions = theSegmentor
							.getSegmentedTranscriptions();
					System.out.println("CCW: " + segmentedTranscriptions.size()
							+ " segmented transcriptions returned.");

					pbd.setVisible(true);
					pbd.setTitle("Metadata processing...");
					int count = 0;
					for (File f : segmentedTranscriptions.keySet()) {
						System.out.println("[" + System.currentTimeMillis()
								+ "]" + f.getAbsolutePath());

						SearchEvent se = new SearchEvent(
								SearchEvent.SEARCH_PROGRESS_CHANGED,
								(((double) count) / ((double) segmentedTranscriptions
										.size())), "Getting metadata for "
										+ f.getName());
						pbd.processSearchEvent(se);

						TranscriptionMetadata tm = new TranscriptionMetadata(f,
								true);
						transcriptions.put(f, tm);
						for (String myS : tm.getSpeakers().keySet()) {
							speakerMetadata.put(myS, tm.getSpeakers().get(myS));
						}
						count++;
					}
					pbd.setVisible(false);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							outputAndClose();
						}
					});
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		segmentThread.start();
		System.out.println("------ Thread started.");

	}

	@Override
	public void sorterChanged(RowSorterEvent arg0) {
		// yawn

	}
}
