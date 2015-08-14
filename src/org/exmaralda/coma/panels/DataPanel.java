/*
 *
 */
package org.exmaralda.coma.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.html.HTMLEditorKit;

import org.exmaralda.coma.datatypes.Communication;
import org.exmaralda.coma.datatypes.Speaker;
import org.exmaralda.coma.dialogs.EditSingleRoleDialog;
import org.exmaralda.coma.filters.ComaFilter;
import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.coma.helpers.XMLEditorKit;
import org.exmaralda.coma.helpers.XPathHelper;
import org.exmaralda.coma.models.CommunicationTableModel;
import org.exmaralda.coma.models.SpeakerTableModel;
import org.exmaralda.coma.render.ConnectionCellRenderer;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaData;
import org.exmaralda.coma.root.ComaListRenderer;
import org.exmaralda.coma.root.IconFactory;
import org.exmaralda.coma.root.RecordingTableModel;
import org.exmaralda.coma.root.TranscriptionTableModel;
import org.exmaralda.coma.root.Ui;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 * coma2/org.sfb538.coma2.panels/DataPanel.java
 * 
 * @author woerner
 */
public class DataPanel extends JPanel implements ActionListener,
		TableModelListener, /* ChangeListener */
		RowSorterListener, ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -154433669541633220L;

	static final int NOTHING = 0;

	static final int COMMUNICATION = 1;

	static final int SPEAKER = 2;

	static final int TRANSCRIPTION = 3;

	static final int RECORDING = 4;

	private int lastSelected = NOTHING;

	private Coma coma;

	private ComaData data;

	private Element missingElement;

	private int myIndex;

	private CommunicationTableModel commTableModel;

	private SpeakerTableModel speakerTableModel;

	private ComaListRenderer commJListRenderer;

	protected JTextField descName;

	private JButton delCommBtn;

	private JTable commTable;

	private TranscriptionTableModel transcriptionTableModel;

	private JTable transcriptionTable;

	private JFileChooser fc;

	static Preferences prefs = Ui.prefs;

	private Element transElmt;

	private RecordingTableModel recordingTableModel;

	private JTable recordingTable;

	private JButton transcriptionToolsButton;

	private JPanel speakerPanel;

	private JTextField spkXPath;

	private JTextField commFilterTextfield;

	private JPopupMenu transcriptionToolPopup;

	private JButton commToBasketButton;

	private Element popupKey;

	protected JPopupMenu commTablePopup;

	private JScrollPane commScrollPane;

	private JTabbedPane perTransRec;

	private boolean layoutingPanel;

	private JTable newCommAttTable;

	private JList transcriptionDescriptionList;

	private JDialog countSegmentDialog;

	private JPanel attributes;

	private JEditorPane valueDisplayPanel;

	private JPanel searchPanel;

	private JCheckBox showSpeakers;

	private JScrollPane commAttributesHTMLScroller;

	private JList commFilterList;

	private TitledBorder commBorder;

	private JPanel commFilters;

	private JPanel communicationPanel;

	private JLabel commCountLabel;

	private HTMLEditorKit kit;

	private XMLEditorKit dkit;

	private JButton personToBasketBtn;

	private JPanel speakerButtons;

	private JTable speakerTable;


	private JButton addSpeakerButton;

	private JButton removeSpeakerButton;

	private JButton removeTranscriptionButton;

	private JButton transcriptionToBasketButton;

	private JButton openInPEButton;

	private JLabel speakerCountLabel;

	private JPanel speakerFilters;

	private Vector<Element> recordingElements;

	private JButton removeSpeakerFiltersButton;

	private JButton removeCFiltersButton;

	// private JButton toTheRescueButton;

	private int[] commRowIndices;

	private int[] speakerRowIndices;

	private JPopupMenu commFilterPopupMenu;

	private JButton cFilterBtn;

	private JButton assignButton;

	private JButton unassignButton;

	private JButton editRolesButton;

	private SelectionListener speakerTableSelectionListener;

	private SelectionListener communicationTableSelectionListener;

	private JPopupMenu recordingToolPopup;

	private JButton recordingToolsButton;

	private JButton cloneSpeakerButton;

	private JButton cloneCommButton;

	private JButton countSegBtn;

	public String lastSelectedTable;

	private JEditorPane recDisplayPanel;

	private HTMLEditorKit rkit;

	private JButton printValuesButton;

	private JCheckBox showSegments;

	private JButton addCommBtn;

	private JPanel assignPanel;

	private JTextField searchTextField;

	private JButton searchButton;

	private TableRowSorter<TableModel> sRowSorter;

	private TableRowSorter<TableModel> rowSorter;

	private JPanel speakerTopPanel;

	private Container commTopPanel;

	private JPanel communicationTopPanel;

	private JPanel attributesTopPanel;

	private JButton sFilterBtn;

	private JPopupMenu spkFilterPopupMenu;

	private JButton searchResetButton;

	private JPopupMenu popup;

	public DataPanel(Coma c) {
		super();
		coma = c;
		data = c.getData();
		layoutPanel();
	}

	private JPopupMenu createTranscriptionToolMenu() {
		if (transcriptionToolPopup == null) {
			transcriptionToolPopup = new JPopupMenu();
			String popItems[] = { Ui.getText("cmd.setSegmentedFlags"),
					Ui.getText("cmd.updateTranscriptionDescriptions") };
			JMenuItem entry[] = new JMenuItem[popItems.length];
			for (int i = 0; i < popItems.length; i++) {
				transcriptionToolPopup
						.add(entry[i] = new JMenuItem(popItems[i]));
				entry[i].setActionCommand(popItems[i]);
				entry[i].addActionListener(this);
			}
		}
		return transcriptionToolPopup;
	}

	private JPopupMenu createRecordingToolMenu() {
		if (recordingToolPopup == null) {
			recordingToolPopup = new JPopupMenu();
			String popItems[] = { Ui.getText("cmd.extractRecordings") };
			JMenuItem entry[] = new JMenuItem[popItems.length];
			for (int i = 0; i < popItems.length; i++) {
				recordingToolPopup.add(entry[i] = new JMenuItem(popItems[i]));
				entry[i].setActionCommand(popItems[i]);
				entry[i].addActionListener(this);
			}
		}
		return recordingToolPopup;
	}

	private JPopupMenu createCommFilterPopupMenu() {
		if (commFilterPopupMenu == null) {
			commFilterPopupMenu = new JPopupMenu();
			for (String fName : coma.getData().getcFilterPresets().keySet()) {
				JMenuItem entry = new JMenuItem(fName);
				entry.setActionCommand("CFilter:" + fName);
				entry.addActionListener(this);
				commFilterPopupMenu.add(entry);

			}
		}
		return commFilterPopupMenu;
	}

	private JPopupMenu createSpkFilterPopupMenu() {
		if (spkFilterPopupMenu == null) {
			spkFilterPopupMenu = new JPopupMenu();
			for (String fName : coma.getData().getsFilterPresets().keySet()) {
				JMenuItem entry = new JMenuItem(fName);
				entry.setActionCommand("SFilter:" + fName);
				entry.addActionListener(this);
				spkFilterPopupMenu.add(entry);

			}
		}
		return spkFilterPopupMenu;
	}

	/**
	 * @param uiString
	 * @param iconName
	 * @param actionCommand
	 * @param enabled
	 * @param clientProperties
	 * @return clearCheckboxButton = createButton("cmd.assignCommSpeaker",
	 *         "edit-clear.png", "assignCommSpeaker", true, new String[] {
	 *         "JButton.buttonType=segmented", "JButton.segmentPosition=left"
	 *         });
	 */
	private JButton createButton(String uiString, String iconName,
			String actionCommand, boolean enabled, String[] clientProperties) {
		Icon imgIcon = IconFactory.createImageIcon(iconName);
		JButton tmpButton = new JButton(imgIcon);
		tmpButton.setToolTipText(Ui.getText(uiString));
		tmpButton.setActionCommand(actionCommand);
		tmpButton.addActionListener(this);
		tmpButton.setEnabled(enabled);
		if (clientProperties != null) {
			for (String property : clientProperties) {
				String[] keyVal = property.split("=");
				tmpButton.putClientProperty(keyVal[0], keyVal[1]);
			}
		}
		tmpButton.setPreferredSize(new Dimension(48, 48));
		tmpButton.setMinimumSize(new Dimension(48, 48));
		tmpButton.setMaximumSize(new Dimension(48, 48));
		return tmpButton;
	}

	private JButton createButton(String uiString, String iconName,
			String actionCommand, boolean enabled) {
		return createButton(uiString, iconName, actionCommand, enabled, null);
	}

	private void layoutPanel() {
		layoutingPanel = true;
		this.setLayout(new BorderLayout());
		perTransRec = new JTabbedPane();

		// communications
		communicationPanel = new JPanel(new BorderLayout()); // holding comms
		communicationTopPanel = new JPanel(new BorderLayout());
		speakerPanel = new JPanel(new BorderLayout());
		speakerTopPanel = new JPanel(new BorderLayout());
		speakerPanel.add(speakerTopPanel, BorderLayout.PAGE_START);
		speakerTableModel = new SpeakerTableModel(coma.getData()
				.getUniqueSpeakerDistinction());

		speakerTableModel.addTableModelListener(this);
		speakerTable = new JTable(speakerTableModel);
		speakerTable.putClientProperty("Quaqua.Table.style", "striped");
		sRowSorter = new TableRowSorter<TableModel>(speakerTableModel);
		sRowSorter.addRowSorterListener(this);
		sRowSorter.setSortsOnUpdates(false);

		speakerTable.setRowSorter(sRowSorter);
		speakerTable.removeColumn(speakerTable.getColumnModel().getColumn(0)); // Element-Column
		speakerTable.removeColumn(speakerTable.getColumnModel().getColumn(0)); // ID-Column
		speakerTable.setDefaultRenderer(Object.class,
				new ConnectionCellRenderer());
		speakerTable.setDefaultRenderer(Boolean.class,
				new ConnectionCellRenderer());
		speakerTableSelectionListener = new SelectionListener(speakerTable);
		speakerTable.getSelectionModel().addListSelectionListener(
				speakerTableSelectionListener);
		speakerTable.setGridColor(Color.LIGHT_GRAY);

		// speakerTable.getSelectionModel().addListSelectionListener(this);
		speakerTable.getColumnModel().getColumn(0).setMinWidth(25);
		speakerTable.getColumnModel().getColumn(0).setMaxWidth(25);

		// **

		addSpeakerButton = createButton("cmd.AddPerson", "list-add.png",
				"addSpeaker", true, new String[] {
						"JButton.buttonType=segmented",
						"JButton.segmentPosition=first" });

		removeSpeakerButton = createButton("cmd.RemovePerson",
				"list-remove.png", "removeSpeaker", false, new String[] {
						"JButton.buttonType=segmented",
						"JButton.segmentPosition=middle" });

		cloneSpeakerButton = createButton("cmd.ClonePerson", "clone.png",
				"cloneSpeaker", false, new String[] {
						"JButton.buttonType=segmented",
						"JButton.segmentPosition=middle" });

		personToBasketBtn = createButton("cmd.filteredToBasket", "cart.png",
				"personToBasket", false, new String[] {
						"JButton.buttonType=segmented",
						"JButton.segmentPosition=last" });

		speakerButtons = new JPanel();
		speakerButtons
				.setLayout(new BoxLayout(speakerButtons, BoxLayout.X_AXIS));

		speakerButtons.add(Box.createHorizontalGlue());

		speakerButtons.add(addSpeakerButton);
		speakerButtons.add(removeSpeakerButton);
		speakerButtons.add(cloneSpeakerButton);
		speakerButtons.add(personToBasketBtn);
		speakerButtons.add(Box.createHorizontalGlue());

		speakerPanel.add(new JScrollPane(speakerTable), BorderLayout.CENTER);

		speakerPanel.add(speakerButtons, BorderLayout.SOUTH);

		// speaker filter panel
		JPanel speakerFilterPanel = new JPanel(new BorderLayout());
		JPanel speakerFilterButtons = new JPanel();
		speakerFilterButtons.setLayout(new BoxLayout(speakerFilterButtons,
				BoxLayout.X_AXIS));
		speakerCountLabel = new JLabel(Ui.getText("label.showing"));
		speakerCountLabel.setFont(new Font("Dialog", 1, 18));
		speakerCountLabel.setHorizontalAlignment(JLabel.CENTER);

		removeSpeakerFiltersButton = new JButton(
				Ui.getText("cmd.removeAllFilters"));
		sFilterBtn = new JButton(IconFactory.createImageIcon("folder.png"));
		sFilterBtn.setToolTipText(Ui.getText("templates"));
		sFilterBtn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				JPopupMenu popup = createSpkFilterPopupMenu();
				popup.show(sFilterBtn, me.getX(), me.getY());
			}
		});
		sFilterBtn.setEnabled(true);
		removeSpeakerFiltersButton.setActionCommand("resetSpeakerFilter");
		removeSpeakerFiltersButton.addActionListener(this);
		speakerFilterButtons.add(sFilterBtn);
		speakerFilterButtons.add(removeSpeakerFiltersButton);
		speakerTopPanel.add(speakerCountLabel);
		speakerFilterPanel.setBorder(BorderFactory.createTitledBorder(Ui
				.getText("filter")));

		speakerFilters = new JPanel();
		speakerFilters
				.setLayout(new BoxLayout(speakerFilters, BoxLayout.Y_AXIS));
		speakerFilterPanel.add(speakerFilters, BorderLayout.CENTER);
		speakerFilterPanel.add(speakerFilterButtons, BorderLayout.SOUTH);

		speakerTopPanel.add(speakerFilterPanel, BorderLayout.PAGE_END);
		perTransRec.addTab(Ui.getText("speakers"),
				IconFactory.createImageIcon("speaker.png"), speakerPanel);

		JPanel commButtons = new JPanel(); // the this holding the add/remove
		// buttons
		// northPanel holds xPathPanel and FilterPanel
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

		// communication filter panel
		JPanel commFilterPanel = new JPanel(new BorderLayout());
		JPanel commFilterButtons = new JPanel();
		commFilterButtons.setLayout(new BoxLayout(commFilterButtons,
				BoxLayout.X_AXIS));
		commCountLabel = new JLabel(Ui.getText("label.showing"));
		commCountLabel.setFont(new Font("Dialog", 1, 18));
		commCountLabel.setHorizontalAlignment(JLabel.CENTER);
		cFilterBtn = new JButton(IconFactory.createImageIcon("folder.png"));
		cFilterBtn.setToolTipText(Ui.getText("templates"));
		cFilterBtn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				JPopupMenu popup = createCommFilterPopupMenu();
				popup.show(cFilterBtn, me.getX(), me.getY());
			}
		});
		cFilterBtn.setEnabled(true);
		removeCFiltersButton = new JButton(Ui.getText("cmd.removeAllFilters"));

		removeCFiltersButton.setActionCommand("resetCommFilter");
		removeCFiltersButton.addActionListener(this);
		commFilterButtons.add(cFilterBtn);
		commFilterButtons.add(removeCFiltersButton);
		communicationTopPanel.add(commCountLabel, BorderLayout.PAGE_START);
		commFilterPanel.setBorder(BorderFactory.createTitledBorder(Ui
				.getText("filter")));
		commFilterTextfield = new JTextField();

		commFilters = new JPanel();
		commFilters.setLayout(new BoxLayout(commFilters, BoxLayout.Y_AXIS));
		commFilterPanel.add(commFilters, BorderLayout.CENTER);
		commFilterPanel.add(commFilterButtons, BorderLayout.SOUTH);

		commButtons.setLayout(new BoxLayout(commButtons, BoxLayout.X_AXIS));
		JScrollPane newCommAttributes = new JScrollPane(newCommAttTable);
		newCommAttributes.setBorder(BorderFactory.createTitledBorder(Ui
				.getText("data(new)")));
		commJListRenderer = new ComaListRenderer();
		commTableModel = new CommunicationTableModel();
		commTable = new JTable(commTableModel);
		commTable.putClientProperty("Quaqua.Table.style", "striped");
		communicationTableSelectionListener = new SelectionListener(commTable);
		commTable.getSelectionModel().addListSelectionListener(
				communicationTableSelectionListener);
		commTableModel.addTableModelListener(this);
		commTable
				.setDefaultRenderer(Object.class, new ConnectionCellRenderer());
		commTable.setDefaultRenderer(Boolean.class,
				new ConnectionCellRenderer());
		commTable.setGridColor(Color.LIGHT_GRAY);

		rowSorter = new TableRowSorter<TableModel>(commTableModel);
		rowSorter.addRowSorterListener(this);
		rowSorter.setSortsOnUpdates(false);
		commTable.setRowSorter(rowSorter);

		commTable.removeColumn(commTable.getColumnModel().getColumn(0));
		commTable.removeColumn(commTable.getColumnModel().getColumn(0));
		commTable.getColumnModel().getColumn(0).setMinWidth(25);
		commTable.getColumnModel().getColumn(0).setMaxWidth(25);

		// popup for commTable
		descName = new JTextField(commJListRenderer.getDescXPath());
		JButton xPathSendButton = new JButton(Ui.getText("cmd.showXPath"));
		JButton descSendButton = new JButton(Ui.getText("cmd.showDescPart"));
		// xPaththis.add(descName);
		JPanel xPaththisBtns = new JPanel();
		xPaththisBtns.setLayout(new BoxLayout(xPaththisBtns, BoxLayout.X_AXIS));
		xPaththisBtns.add(xPathSendButton);
		xPathSendButton.setActionCommand("xpathUpdate");
		xPaththisBtns.add(descSendButton);
		// xPaththis.add(xPaththisBtns);
		descSendButton.setActionCommand("descUpdate");
		descSendButton.addActionListener(this);
		commScrollPane = new JScrollPane(commTable);
		coma.sizeIt(commScrollPane, 300, 1);
		communicationPanel.add(commScrollPane, BorderLayout.CENTER);
		addCommBtn = createButton("cmd.addCommunication", "list-add.png",
				"addCommunication", true, new String[] {
						"JButton.buttonType=segmented",
						"JButton.segmentPosition=first" });

		delCommBtn = createButton("cmd.DelCommunication", "list-remove.png",
				"delCommunication", false, new String[] {
						"JButton.buttonType=segmented",
						"JButton.segmentPosition=middle" });

		cloneCommButton = createButton("cmd.cloneCommunication",
				"communication-clone32.png", "cloneCommunication", false,
				new String[] { "JButton.buttonType=segmented",
						"JButton.segmentPosition=middle" });

		commToBasketButton = createButton("cmd.commToBasket", "cart.png",
				"commToBasket", true, new String[] {
						"JButton.buttonType=segmented",
						"JButton.segmentPosition=middle" });

		countSegBtn = createButton("cmd.CountSegments",
				"accessories-calculator.png", "countSegments", false,
				new String[] { "JButton.buttonType=segmented",
						"JButton.segmentPosition=last" });

		/*
		 * toTheRescueButton = createButton("cmd.ToTheRescue", "rescue.png",
		 * "toTheRescue", true, new String[] { "JButton.buttonType=segmented",
		 * "JButton.segmentPosition=last" });
		 */
		printValuesButton = createButton("cmd.printValues", "printpreview.png",
				"printValues", true, new String[] {
						"JButton.buttonType=segmented",
						"JButton.segmentPosition=first" });
		commButtons.add(Box.createHorizontalGlue());
		commButtons.add(addCommBtn);
		commButtons.add(delCommBtn);
		commButtons.add(cloneCommButton);
		commButtons.add(commToBasketButton);
		commButtons.add(countSegBtn);
		commButtons.add(Box.createHorizontalGlue());

		// werkzeuge hierher!
		communicationPanel.add(commButtons, BorderLayout.SOUTH);
		valueDisplayPanel = new JEditorPane();
		valueDisplayPanel.setEditable(false);
		kit = new HTMLEditorKit();
		dkit = new XMLEditorKit();
		dkit.setLineWrappingEnabled(true);
		dkit.setWrapStyleWord(true);

		valueDisplayPanel.setEditorKit(kit);
		valueDisplayPanel.addHyperlinkListener(new ComaHyperlinkListener(coma));
		attributes = new JPanel();
		attributes.setLayout(new BorderLayout());
		attributesTopPanel = new JPanel(new BorderLayout());
		JLabel metadataLabel = new JLabel(Ui.getText("metadata"));
		metadataLabel.setFont(new Font("Dialog", 1, 18));
		metadataLabel.setHorizontalAlignment(JLabel.CENTER);

		attributesTopPanel.add(metadataLabel, BorderLayout.PAGE_START);
		attributesTopPanel.add(getSearchPanel(), BorderLayout.PAGE_END);
		attributes.add(attributesTopPanel, BorderLayout.PAGE_START);
		attributes.add(getAssignPanel(), BorderLayout.PAGE_END);
		// attributes.add(commAttributesTablePanel, BorderLayout.SOUTH);
		commAttributesHTMLScroller = new JScrollPane(valueDisplayPanel);
		attributes.add(commAttributesHTMLScroller, BorderLayout.CENTER);
		// *******************************************************************************
		JSplitPane spRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				attributes, speakerPanel);
		spRight.setResizeWeight(1.0);
		spRight.setOneTouchExpandable(true);
		spRight.setDividerSize(8);
		JSplitPane spLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				communicationPanel, spRight);
		spLeft.setOneTouchExpandable(true);
		spLeft.setResizeWeight(0.0);
		spLeft.setDividerSize(8);
		spRight.setDividerLocation(0.8);
		spLeft.setDividerLocation(0.5);
		attributes.setSize(800, 200);
		this.add(spLeft, BorderLayout.CENTER);
		northPanel.add(commFilterPanel);
		communicationTopPanel.add(northPanel, BorderLayout.PAGE_END);
		communicationPanel.add(communicationTopPanel, BorderLayout.PAGE_START);
		layoutingPanel = false;
	}

	private JPanel getSearchPanel() {
		if (searchPanel == null) {
			searchPanel = new JPanel();
		}
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		searchPanel.setBorder(BorderFactory.createTitledBorder(Ui
				.getText("cmd.search")));
		showSpeakers = new JCheckBox(Ui.getText("cmd.showSpeakers"));
		showSpeakers.setSelected(prefs.getBoolean("showSpeakers", false));
		showSpeakers.setActionCommand("showSpeakers");
		showSpeakers.addActionListener(this);

		showSegments = new JCheckBox(Ui.getText("cmd.showSegmentCounts"));
		showSegments.setSelected(prefs.getBoolean("showSegmentCounts", false));
		showSegments.setActionCommand("showSegmentCounts");
		showSegments.addActionListener(this);

		searchTextField = new JTextField();

		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
		searchPanel.add(searchTextField);
		searchButton = new JButton(Ui.getText("cmd.search"));
		searchButton.setActionCommand("find");
		searchButton.addActionListener(this);
		searchPanel.add(searchButton);
		searchResetButton = new JButton(Ui.getText("cmd.reset"));
		searchResetButton.setActionCommand("resetSearch");
		searchResetButton.addActionListener(this);
		searchPanel.add(searchResetButton);
		// checkBoxPanel.add(showSpeakers);
		// checkBoxPanel.add(showSegments);

		// attributesControlPanel.add(checkBoxPanel);

		searchPanel.add(Box.createHorizontalGlue());
		searchPanel.add(printValuesButton);
		// attributesControlPanel.add(transcriptionToolsButton);
		// searchPanel.add(toTheRescueButton);

		return searchPanel;
	}

	private JPanel getAssignPanel() {
		if (assignPanel == null) {
			assignPanel = new JPanel();
		}
		assignPanel.setLayout(new BoxLayout(assignPanel, BoxLayout.X_AXIS));

		// attributesControlPanel.add(transcriptionToolsButton);
		// assignPanel.add(toTheRescueButton);
		assignButton = createButton("cmd.assignCommSpeaker", "link.png",
				"assignCommSpeaker", false, new String[] {
						"JButton.buttonType=segmented",
						"JButton.segmentPosition=middle" });

		unassignButton = createButton("cmd.unassignCommSpeaker", "unlink.png",
				"unassignCommSpeaker", false, new String[] {
						"JButton.buttonType=segmented",
						"JButton.segmentPosition=last" });
		/*
		 * editRolesButton = createButton("cmd.editRoles", "role32.png",
		 * 
		 * "editRoles", false, new String[] { "JButton.buttonType=segmented",
		 * "JButton.segmentPosition=last" });
		 */
		assignPanel.add(Box.createHorizontalGlue());
		assignPanel.add(printValuesButton);
		assignPanel.add(assignButton);
		assignPanel.add(unassignButton);
		/*
		 * if (coma.isDebugging()) assignPanel.add(editRolesButton);
		 */
		assignPanel.add(Box.createHorizontalGlue());

		return assignPanel;
	}

	/**
	 * 
	 */
	private void showMultiSpeakerValues() {
		valueDisplayPanel.setEditable(false);
		valueDisplayPanel.setText("<h2>" + data.getSelectedPersons().size()
				+ " " + Ui.getText("speakersSelected"));

	}

	public void showCommunicationValues() {
		showCommunicationValues(0);
	}

	/** wenn alles in die fritten geht.... */
	/*
	 * public void toTheRescue() { if (valueDisplayPanel.getText() != null) {
	 * resetCommFilter(); resetSpeakerFilter(); valueDisplayPanel
	 * .setText(ComaHTML .htmlBracket(
	 * "<h2>Panic Switch activated.</h2><p>Sorry for the inconvenience!</p>"));
	 * coma.pack(); } }
	 */

	public void showCommunicationValues(int assignedSpeakers) {
		if (data.getSelectedCommunications().size() > 0) {
			if (data.getSelectedCommunications().size() > 1) {
				valueDisplayPanel.setEditable(false);
				valueDisplayPanel.setText(ComaHTML.htmlBracket("<h2>"
						+ data.getSelectedCommunications().size() + " "
						+ Ui.getText("communicationsSelected") + ",<br/>"
						+ assignedSpeakers + " "
						+ Ui.getText("assignedSpeakers")));
			} else {
				showComm();

				if (data.getSelectedCommunications().values().iterator().next()
						.getChild("Description") == null) {
					missingElement = new Element("Description");
					data.getSelectedCommunications().values().iterator().next()
							.addContent(0, missingElement);
				}
				if (data.getSelectedCommunications().values().iterator().next()
						.getChild("Setting") == null) {
					missingElement = new Element("Setting");
					data.getSelectedCommunications().values().iterator().next()
							.addContent(1, missingElement);
				} else {
					Element setting = data.getSelectedCommunications().values()
							.iterator().next().getChild("Setting");
					List persons = setting.getChildren("Person");
					HashSet personIds = new HashSet();
					if (persons.size() > 0) {
						Iterator it = persons.iterator();
						while (it.hasNext()) {
							Element person = (Element) it.next();
							personIds.add(person.getText());
						}
					}
				}
				myIndex = data
						.getSelectedCommunications()
						.values()
						.iterator()
						.next()
						.getContent()
						.indexOf(
								data.getSelectedCommunications().values()
										.iterator().next().getChild("Setting"));
			}
		}

	}

	/**
	 * 
	 */
	private void showComm() {
		valueDisplayPanel.setEditable(false);

		coma.resetEditableItems();

		// hier gehts ab!
		// data.getSelectedCommunications().values().iterator().next();
		Communication c = new Communication(data.getSelectedCommunications()
				.values().iterator().next(), coma);
		// Communication c = new Communication(data.getSelectedCommunications()
		// .get(0), coma);
		valueDisplayPanel.setText(c.toHTML(false));
		valueDisplayPanel.setCaretPosition(0);
	}

	/**
	 * 
	 */
	private void showPerson() {
		valueDisplayPanel.setEditable(false);
		coma.resetEditableItems();
		Speaker s = new Speaker(coma, data.getSelectedPersons().values()
				.iterator().next(), false);
		valueDisplayPanel.setText(s.toHTML(false));
		valueDisplayPanel.setCaretPosition(0);
	}

	/**
	 * changes the Name-Attribute of the Communication
	 * 
	 * @param newName
	 */

	public void changeCommName(String newName) {
		data.getSelectedCommunications().values().iterator().next()
				.setAttribute("Name", newName);
		updateLists(true);
	}

	/**
	 * updates the list of communications to display other values in the second
	 * column
	 * 
	 * @param isXPath
	 *            true if text in descName is a xPath-expression
	 */
	public void updateRenderer(String parmText, boolean isXPath) {
		// !!! removeCommSelection();
		if (parmText.contains("/CorpusData/Communication/")) {
			commTableModel.setSecondColumn(parmText);
			TableColumn tc = commTable.getColumnModel().getColumn(2);
			tc.setHeaderValue(XPathHelper.getInnerCondition(parmText));
			commTable.getTableHeader().repaint();
		} else if (parmText.contains("/CorpusData/Speaker/")) {
			speakerTableModel.setSecondColumn(parmText);
			TableColumn tc = speakerTable.getColumnModel().getColumn(2);
			tc.setHeaderValue(XPathHelper.getInnerCondition(parmText));
			speakerTable.getTableHeader().repaint();

		}

		updateLists(true);
	}

	/**
	 * 
	 */

	public void updateLists(boolean redoSelection) {
		int[] cidx = commTable.getSelectedRows();
		int[] sidx = speakerTable.getSelectedRows();

		coma.setCursor(Cursor.WAIT_CURSOR);
		commTableModel.setRowCount(0);
		speakerTableModel.setRowCount(0);
		List comms = coma.getData().filterComms();
		int ccount = 0;
		for (Object c : comms) {
			ccount++;
			Element child = (Element) c;
			coma.getCommIndex().put(child.getAttributeValue("Id"), child);
			commTableModel.addRow(child);
		}
		commCountLabel.setText(Ui.getText("communications") + " (" + ccount
				+ ")");
		commFilters.removeAll();
		for (ComaFilter filter : coma.getData().getCfilters().values()) {
			filter.addChangeListener(this);
			commFilters.add(filter.getPanel());
		}
		commFilters.revalidate();
		removeCFiltersButton
				.setEnabled((coma.getData().getCfilters().size() > 0));
		List spks = coma.getData().filterSpeakers();
		int scount = 0;
		for (Object s : spks) {
			scount++;
			Element child = (Element) s;
			coma.getSpeakerIndex().put(child.getAttributeValue("Id"), child);
			speakerTableModel.addRow(child);
			// personTableModel.addRow(child);
		}
		speakerCountLabel.setText(Ui.getText("speakers") + " (" + scount + ")");
		speakerFilters.removeAll();
		for (ComaFilter filter : coma.getData().getPfilters().values()) {
			filter.addChangeListener(this);
			speakerFilters.add(filter.getPanel());
		}
		speakerFilters.revalidate();
		removeSpeakerFiltersButton.setEnabled((coma.getData().getPfilters()
				.size() > 0));

		if (redoSelection && (lastSelectedTable != null)) {
			valueDisplayPanel.setText("");
			if (lastSelectedTable.equals("commTable")) {
				for (int i : sidx) {
					speakerTable.getSelectionModel().addSelectionInterval(i, i);
				}
				if (!data.getFilterChanged().equals("comm")) {
					for (int i : cidx) {
						commTable.getSelectionModel()
								.addSelectionInterval(i, i);
					}
				}
			} else {
				for (int i : cidx) {
					commTable.getSelectionModel().addSelectionInterval(i, i);
				}
				if (!data.getFilterChanged().equals("spk")) {
					for (int i : sidx) {
						speakerTable.getSelectionModel().addSelectionInterval(
								i, i);
					}
				}

			}

		}
		data.setFilterChanged("");
		coma.setCursor(Cursor.getDefaultCursor());

	}

	public void updateLists() {
		updateLists(false);
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		coma.status("action '" + cmd + "' performed!");
		if (cmd.equals("descUpdate")) {
			updateRenderer(descName.getText(), false);
		} else if (cmd.equals("xpathUpdate")) {
			updateRenderer(descName.getText(), true);
		} else if (cmd.equals("editRoles")) {
			editRoles();
		} else if (cmd.equals("xpathSpkUpdate")) {
			updateSpeakerRenderer(true);
		} else if (cmd.equals("descSpkUpdate")) {
			updateSpeakerRenderer(false);
		} else if (cmd.equals("addCommunication")) {
			addCommunication();
		} else if (cmd.equals("delCommunication")) {
			delCommunication();
		} else if (cmd.equals("cloneCommunication")) {
			cloneCommunication();
		} else if (cmd.equals("addSpeaker")) {
			addSpeaker();
		} else if (cmd.equals("removeSpeaker")) {
			removeSpeaker();
		} else if (cmd.equals("cloneSpeaker")) {
			cloneSpeaker();
		} else if (cmd.equals("remTranscription")) {
			remTranscription();
		} else if (cmd.equals("countSegments")) {
			countSegments();
		} else if (cmd.equals("printValues")) {
			printValues();
			/*
			 * possibly maybe not used anymore?! } else if } else if
			 * (cmd.equals("toTheRescue")) { toTheRescue();
			 * (cmd.equals("addRecording")) { // addRecording();
			 * System.out.println("how did I get here");
			 * JOptionPane.showMessageDialog(coma, "how did I get here?!");
			 */
		} else if (cmd.equals("removeRecording")) {
			removeRecording();
		} else if (cmd.equals("personToBasket")) {
			personToBasket();
		} else if (cmd.equals("playRecording")) {
			playRecording();
		} else if (cmd.equals("assignCommSpeaker")) {
			assignCommsToSpeakers(true);
		} else if (cmd.equals("unassignCommSpeaker")) {
			assignCommsToSpeakers(false);
		} else if (cmd.equals("showSpeakers")) {
			prefs.putBoolean("showSpeakers", showSpeakers.isSelected());
			updateSelectedElement();
		} else if (cmd.equals("showSegmentCounts")) {
			prefs.putBoolean("showSegmentCounts", showSegments.isSelected());
			updateSelectedElement();
		} else if (cmd.equals("find")) {
			find(searchTextField.getText());
		} else if (cmd.equals("resetSearch")) {
			resetSearch();
		} else if (cmd.equals("resetCommFilter")) {
			resetCommFilter();
		} else if (cmd.equals("resetSpeakerFilter")) {
			resetSpeakerFilter();
		} else if (cmd.equals("addToBasket")) {
			if (transcriptionTable.getSelectedRowCount() > 0) {
				addToBasket();
			}
		} else if (cmd.equals("cmd.checkTranscriptionPaths")) {
			//

		} else if (cmd.startsWith("CFilter:")) {
			System.out.println(cmd.substring(8));
			coma.setCommFilter(
					coma.getData().getcFilterPresets().get(cmd.substring(8))
							.getXPath(), true);
		} else if (cmd.startsWith("SFilter:")) {
			System.out.println(cmd.substring(8));
			coma.setSpeakerFilter(
					coma.getData().getsFilterPresets().get(cmd.substring(8))
							.getXPath(), true);
		} else if (cmd.equals("commToBasket")) {
			commToBasket();
		} else if (cmd.equals("removeCFilter")) {
			int r = commFilterList.getMinSelectionIndex();
			coma.getData().removecfilter(
					(String) commFilterList.getModel().getElementAt(r));
			updateLists(true);
		}

	}

	/**
	 * edit the roles (read: type & description) a speaker / speakers have in
	 * the communication
	 */

	private void editRoles() {
		EditSingleRoleDialog srd = new EditSingleRoleDialog(coma, this);
		srd.setVisible(true);
		if (srd.returnValue) {

		}
	}

	private void find(String text) {
		if (text.length() > 0) {
			data.addRawCFilter("//Communication[node()[contains(.,'" + text
					+ "')]]");
			data.addRawPFilter("//Speaker[node()[contains(.,'" + text + "')]]");
			data.setSearchTerm(text);
		} else {
			data.setSearchTerm("");
		}
		updateLists();
	}

	private void resetSearch() {
		data.removeSearchFilters();
		data.setSearchTerm("");
		updateLists();
	}

	/** move all transcriptions from selected communications to basket */
	private void commToBasket() {
		coma.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		for (Element cElement : data.getSelectedCommunications().values()) {
			coma.addToBasket(cElement);
		}
		coma.setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * tut alle transkriptionen, an denen die ausgewählten sprecher teilgenommen
	 * haben, in den korb.
	 */
	private void personToBasket() {
		coma.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		HashSet<String> personIds = new HashSet<String>();

		for (Element pElement : data.getSelectedPersons().values()) {
			personIds.add(pElement.getAttributeValue("Id"));
			// System.out.println(pElement.getAttributeValue("Id"));
		}
		List<Element> myComms = data.getDataElement().getChildren(
				"Communication");
		for (Element c : myComms) {
			List<Element> mySpks = c.getChild("Setting").getChildren("Person");
			for (Element mySpk : mySpks) {
				if (personIds.contains(mySpk.getText())) {
					coma.addToBasket(c);
				}
			}
		}
		coma.setCursor(Cursor.getDefaultCursor());
	}

	/** (un)assigns the selected speakers to the selected communications */
	private void assignCommsToSpeakers(boolean assign) { // true to assign,
		// false to unassign
		if ((data.getSelectedCommunications().size() > 0)
				&& (data.getSelectedPersons().size() > 0)) {

			if (coma.getSchemaVersion() > 0) {
				for (String commId : data.getSelectedCommunications().keySet()) {
					for (Element speakerElement : data.getSelectedPersons()
							.values()) {
						Vector<Element> toDelete = new Vector<Element>();
						boolean rolled = false;
						for (Element role : (List<Element>) speakerElement
								.getChildren("role")) {
							if (commId.equals(role.getAttributeValue("target"))) {
								rolled = true;
								toDelete.add(role);
							}
						}
						if (!assign) {
							if (toDelete.size() > 0) {
								for (Element r : toDelete) {
									speakerElement.removeContent(r);
								}
							}
						} else {
							if (rolled == false) {
								speakerElement.addContent(new Element("role")
										.setAttribute("target", commId));
							}
						}
					}
				}

				for (Element communicationElement : data
						.getSelectedCommunications().values()) {

				}
				for (Element speakerElement : data.getSelectedPersons()
						.values()) {
					HashMap<String, Element> existingRoles = new HashMap<String, Element>();
					for (Element role : (List<Element>) speakerElement
							.getChildren("role")) {
						existingRoles.put(role.getAttributeValue("target"),
								role);

					}

				}

			} else {

				for (Element communicationElement : data
						.getSelectedCommunications().values()) {
					HashSet<String> assignedPersonStrings = new HashSet<String>();
					Element setting = communicationElement.getChild("Setting");
					List<Element> persons = setting.getChildren("Person");
					Iterator i = persons.iterator();
					while (i.hasNext()) {
						Element person = (Element) i.next();
						assignedPersonStrings.add(person.getText());
					}
					for (Element pElement : data.getSelectedPersons().values()) {
						if (assignedPersonStrings.contains(pElement
								.getAttributeValue("Id"))) {
							if (!assign) {
								assignedPersonStrings.remove(pElement
										.getAttributeValue("Id"));
							}
						} else {
							if (assign) {
								assignedPersonStrings.add(pElement
										.getAttributeValue("Id"));
							}
						}
					}
					setting.removeChildren("Person");
					for (String s : assignedPersonStrings) {
						setting.addContent(new Element("Person").setText(s));
					}
				}
			}
		}
		updateLists(true);
		coma.xmlChanged();
	}

	/** clone selected speaker */
	private void cloneSpeaker() {
		Element spC = data.getSelectedPersons().values().iterator().next();
		Element cloned = (Element) spC.clone();
		cloned.setAttribute("Id", "S" + new GUID().makeID());
		cloned.getChild("Sigle").setText(spC.getChildText("Sigle") + "_cloned");
		data.getDataElement().addContent(cloned);
		data.disableSpeakerFilters();
		updateLists();
	}

	/** remove selected speaker(s) */
	private void removeSpeaker() {
		coma.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		for (Element pe : data.getSelectedPersons().values()) {
			String personId = pe.getAttributeValue("Id");
			XPath spx;
			try {
				spx = XPath.newInstance("//Communication//Person[text()='"
						+ personId + "']");
				List<Element> elementsToRemove = spx.selectNodes(data
						.getDataElement());
				for (Element de : elementsToRemove) {
					de.getParent().removeContent(de);
				}
			} catch (JDOMException err) {
				err.printStackTrace();
			}
			pe.getParent().removeContent(pe);

		}
		coma.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		updateLists();
		coma.xmlChanged = true;
	}

	/**
	 * l�scht Sprecherverkn�pfungen mit einer bestimmten ID (meistens, weil der
	 * Sprecher nicht mehr existiert)
	 * 
	 * @param id
	 */

	public void deadSpeaker(String id) {
		XPath spx;
		try {
			spx = XPath.newInstance("//Communication//Person[text()='" + id
					+ "'");
			List<Element> elementsToRemove = spx.selectNodes(data
					.getDataElement());
			for (Element de : elementsToRemove) {
				de.getParent().removeContent(de);
			}
		} catch (JDOMException err) {
			err.printStackTrace();
		}
		coma.xmlChanged = true;
	}

	/**
	 * 
	 */
	private void addSpeaker() {
		Element newSpk = new Element("Speaker");
		newSpk.setAttribute("Id", new GUID().makeID());
		newSpk.addContent(new Element("Sigle").setText("NEW"));
		newSpk.addContent(new Element("Pseudo").setText("New Speaker"));
		newSpk.addContent(new Element("KnownHuman").setText("true"));
		newSpk.addContent(new Element("Sex").setText("female"));
		newSpk.addContent(new Element("Description"));
		data.getDataElement().addContent(newSpk);
		data.disableSpeakerFilters();
		updateLists();

	}

	/**
	 * 
	 */
	private void playRecording() {
		if (recordingElements.size() == 1) {
			String fileToOpen = (coma.absolutized(recordingElements.get(0)
					.getChild("Media").getChild("NSLink").getText()));
			try {
				Desktop.getDesktop().open(new File(fileToOpen));

			} catch (IOException err) {
				coma.error(fileToOpen, "Desktop Error");
			}
		}
	}

	private void countSegments() {
		coma.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		int a;
		int segTranscriptionCount = 0; // number of transcriptions
		HashMap segments = new HashMap();
		for (Element myComm : data.getSelectedCommunications().values()) {
			List transcriptions = myComm.getChildren("Transcription");
			// coma.status(transcriptions.size());
			Iterator it = transcriptions.iterator();
			while (it.hasNext()) {
				Element myTranscription = (Element) it.next();
				if (coma.getDescriptionValue(
						myTranscription.getChild("Description"), "segmented") != null) {
					if (coma.getDescriptionValue(
							myTranscription.getChild("Description"),
							"segmented").equals("true")) {
						segTranscriptionCount++;
						List descriptionList = myTranscription.getChild(
								"Description").getChildren();
						Iterator trLiIt = descriptionList.iterator();
						while (trLiIt.hasNext()) {
							Element trKey = (Element) trLiIt.next();
							String key = trKey.getAttributeValue("Name");
							if (key.startsWith("# ")) {
								if (isParsableToInt(trKey.getValue())) {

									if (segments.get(key.substring(2)) == null) {
										a = 0;
									} else {
										a = ((Integer) segments.get(key
												.substring(2))).intValue();
									}
									a = a
											+ new Integer(trKey.getText())
													.intValue();
									segments.put(trKey
											.getAttributeValue("Name")
											.substring(2), new Integer(a));
								}
							}
						}
					}
				}
			}

		}
		if (segTranscriptionCount > 0) {
			countSegmentDialog = new JDialog(coma, "SegmentCount", true);
			countSegmentDialog.setLayout(new BorderLayout());
			// dialog.setSize(400, 10);
			countSegmentDialog
					.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			DefaultTableModel countTableModel = new DefaultTableModel();
			countTableModel.addColumn("Segments", segments.keySet().toArray());
			countTableModel.addColumn("Count", segments.values().toArray());
			countTableModel.addRow(new String[] { "Transcriptions",
					(new Integer(segTranscriptionCount)).toString() });
			JTable segmentTable = new JTable(countTableModel);
			countSegmentDialog.add(segmentTable, BorderLayout.CENTER);
			countSegmentDialog.add(new JButton(new AbstractAction("OK", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					countSegmentDialog.dispose();
				};
			}), BorderLayout.SOUTH);
			countSegmentDialog.pack();
			countSegmentDialog.setLocationRelativeTo(coma);
			coma.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			countSegmentDialog.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(coma,
					"No countable segment information found.");
		}

	}

	public void resetCommFilter() {
		coma.getData().removecfilter(null);
		updateLists(true);
		// commTableModel.resetFilter();
	}

	public void resetSpeakerFilter() {
		coma.getData().removepfilter(null);
		updateLists(true);
		// commTableModel.resetFilter();
	}

	public void setCommFilter(String xpath) {
		commTableModel.setSecondColumn(xpath);
	}

	private void updateSpeakerRenderer(boolean b) {
		speakerTableModel.setSecondColumn(spkXPath.getText());
		TableColumn tc = speakerTable.getColumnModel().getColumn(3);
		tc.setHeaderValue((b) ? "XPath" : spkXPath.getText());
		speakerTable.getTableHeader().repaint();
		updateLists();
	}

	/**
	 * 
	 */
	/*
	 * private void addRecording() { if (data.getOpenFile().exists()) { if
	 * (data.getSelectedCommunications().size() > 0) {
	 * 
	 * fc = new JFileChooser(); fc.setCurrentDirectory(new File(Coma.prefs.get(
	 * "recentRecordingDirectory", "/"))); fc.setAccessory(new
	 * ChangeTranscriptionAccessory()); fc.setMultiSelectionEnabled(true); int
	 * dialogStatus = fc.showOpenDialog(this); if (dialogStatus == 0) { boolean
	 * pathSet = false; for (File recFile : fc.getSelectedFiles()) { if
	 * (recFile.exists()) { if (!pathSet) {
	 * Coma.prefs.put("recentRecordingDirectory", recFile.getParent()); pathSet
	 * = true; } String recRelPath = coma.getRelativePath(null, recFile);
	 * 
	 * for (Element c : data.getSelectedCommunications() .values()) { boolean
	 * addRec = true; if (c.getChild("Recording") != null) { for (Element r :
	 * (List<Element>) c .getChildren("Recording")) { if (r.getChild("NSLink")
	 * != null) { String rF = r .getChildText("NSLink"); if
	 * (rF.equals(recRelPath)) { addRec = false; } } } } if (addRec) { Element
	 * newRec = new Element("Recording"); newRec.addContent(new Element("Name")
	 * .setText(org.apache.commons.io.FilenameUtils .removeExtension(recFile
	 * .getName()))); System.out .println("I'm here! " + recFile.getName() +
	 * " -> " + org.apache.commons.io.FilenameUtils .removeExtension(recFile
	 * .getName())); newRec.setAttribute("Id", "R" + new GUID().makeID());
	 * Element newMedia = new Element("Media"); newMedia.setAttribute("Id", "M"
	 * + new GUID().makeID()); newMedia.addContent(new Element( "Description"));
	 * newMedia.getChild("Description") .addContent(new Element("Key"));
	 * newMedia.getChild("Description") .getChild("Key") .setAttribute("Name",
	 * "Type"); newMedia.getChild("Description")
	 * .getChild("Key").setText("Digital"); newMedia.addContent(new
	 * Element("NSLink")); newMedia.getChild("NSLink").setText( recRelPath);
	 * newRec.addContent(newMedia); c.addContent(newRec);
	 * recordingTableModel.addRow(newRec); coma.xmlChanged(); if
	 * (((ChangeTranscriptionAccessory) fc .getAccessory()).isSelected()) {
	 * 
	 * } } } } } } } } else { coma.error(Ui.getText("err.notSaved"),
	 * Ui.getText("err.notSavedTitle"));
	 * 
	 * } }
	 */
	private void removeRecording() {
		if (recordingTable.getSelectedRowCount() > 0) {
			HashSet<Element> recsToDelete = new HashSet();
			for (int i : recordingTable.getSelectedRows()) {

				Element rec = (Element) recordingTableModel.getValueAt(i, 0);
				recsToDelete.add(rec);
			}
			for (Element e : recsToDelete) {
				recordingTableModel.removeRow(e);
				e.getParent().removeContent(e);
			}
			coma.xmlChanged();
		}
	}

	/**
	 * 
	 */
	private void remTranscription() {
		if (data.getSelectedCommunications() != null) {
			for (int i = 0; i < transcriptionTable.getSelectedRowCount(); i++) {
				transElmt = (Element) transcriptionTable.getModel().getValueAt(
						transcriptionTable.getSelectedRows()[i], 0);
				data.getSelectedCommunications().values().iterator().next()
						.removeContent(transElmt);
			}
			coma.xmlChanged();
			showCommunicationValues();
		}
	}

	/**
	 * 
	 */
	private void addToBasket() {
		coma.status("@add to basket");
		for (int i = 0; i < transcriptionTable.getSelectedRowCount(); i++) {
			transElmt = (Element) transcriptionTable.getModel().getValueAt(
					transcriptionTable.getSelectedRows()[i], 0);
			coma.addToBasket(transElmt);
		}
	}

	private void addCommunication() {
		Element elmt = data.getDataElement();
		int myIndex = 0;
		if (elmt.getChildren("Speaker").size() > 0) {
			myIndex = elmt.getContent().indexOf(elmt.getChild("Speaker"));
		} else {
			myIndex = elmt.getContentSize();
		}
		Element newComm = new Element("Communication");
		String myId = new GUID().makeID();
		newComm.setAttribute("Id", myId);
		newComm.setAttribute("Name", "unnamed");
		newComm.addContent(new Element("Description"));
		newComm.addContent(new Element("Setting"));
		elmt.addContent(myIndex, newComm);
		Vector tmpVector = commTableModel.addRow(newComm);
		coma.getData().disableCommFilters();
		// removeCommSelection();
		commTable.getSelectionModel().setSelectionInterval(
				commTable.convertRowIndexToView(commTableModel
						.getRow(tmpVector)),
				commTable.convertRowIndexToView(commTableModel
						.getRow(tmpVector)));
		commTableModel.getRow(tmpVector);
		commTable.scrollRectToVisible(commTable.getCellRect(
				commTable.getSelectedRow(), 1, true));
		coma.xmlChanged();
	}

	/** deletes the selected communication(s) */
	private void delCommunication() {
		for (Element e : data.getSelectedCommunications().values()) {
			e.getParent().removeContent(e);
		}
		// .iterator().next())
		// }
		//
		// // commTableModel.removeRow(commElmt);
		// for (int i = 0; i < data.getSelectedCommunications().size(); i++) {
		// data.getSelectedCommunications().get(i).getParent()
		// .removeContent(data.getSelectedCommunications().get(i));
		// }
		coma.xmlChanged();
		updateLists();
	}

	private void cloneCommunication() {
		Element cloned = (Element) data.getSelectedCommunications().values()
				.iterator().next().clone();
		cloned.setAttribute("Id", "C" + new GUID().makeID());
		cloned.setAttribute("Name", cloned.getAttributeValue("Name")
				+ "_cloned");
		data.getDataElement().addContent(cloned);
		data.disableCommFilters();
		coma.xmlChanged();
		updateLists();
	}

	class TranscriptionSelectionHandler implements ListSelectionListener {
		private List<Element> transKeys;

		public void valueChanged(ListSelectionEvent e) {

			ListSelectionModel lsm = (ListSelectionModel) e.getSource();

			if (lsm.isSelectionEmpty()) {
				removeTranscriptionButton.setEnabled(false);
				transcriptionToBasketButton.setEnabled(false);
				openInPEButton.setEnabled(false);
				coma.status(" <none>");
				transcriptionDescriptionList.setListData(new String[0]);
			} else {
				// Find out which indexes are selected.
				int minIndex = lsm.getMinSelectionIndex();
				int maxIndex = lsm.getMaxSelectionIndex();
				removeTranscriptionButton.setEnabled(true);
				transcriptionToBasketButton.setEnabled(true);
				if (minIndex == maxIndex) {
					// if (prefs.get("path.jexmaralda", "").length() > 0) {
					// openInPEButton.setEnabled(true);
					// }
					// display the transcription's description-values
					transElmt = (Element) transcriptionTable.getModel()
							.getValueAt(
									transcriptionTable.getSelectedRows()[0], 0);
					// haha!
					transKeys = transElmt.getChild("Description").getChildren();
					Vector<String> transcriptionInfo = new Vector();
					transcriptionInfo
							.add(Ui.getText("fileLocation")
									+ ": "
									+ ((transElmt.getChildText("NSLink") == null) ? "unknown"
											: transElmt.getChildText("NSLink")));
					for (Element myKey : transKeys) {
						transcriptionInfo.add(myKey.getAttributeValue("Name")
								+ "=" + myKey.getText());
					}
					transcriptionDescriptionList.setListData(transcriptionInfo
							.toArray());
				} else {
					openInPEButton.setEnabled(false);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
	 * )
	 */
	public void stateChanged(ChangeEvent arg0) {
		if (!layoutingPanel) {
			if (arg0.toString().contains("FilterChanged")) {
				coma.status("fired!!");
				updateLists();
			} else {
				prefs.putInt("perTransRecPaneSelected",
						perTransRec.getSelectedIndex());
			}
		}
	}

	public void selectTab(int tabToSelect) {
		perTransRec.setSelectedIndex(tabToSelect);
	}

	public void sorterChanged(RowSorterEvent arg0) {
		commTable.scrollRectToVisible(commTable.getCellRect(
				commTable.getSelectedRow(), 1, true));

	}

	/** @param rowIndices */
	private void commSelected(int[] rowIndices) {
		data.getSelectedCommunications().clear();
		int maxRow = 0;
		for (int rowIndex : rowIndices) {
			if (rowIndex > maxRow)
				maxRow = rowIndex;
		}
		if ((rowIndices.length == 0) | (maxRow >= commTable.getRowCount())) {
			delCommBtn.setEnabled(false);
			cloneCommButton.setEnabled(false);
			countSegBtn.setEnabled(false);
		} else {
			lastSelected = COMMUNICATION;
			commRowIndices = rowIndices;
			// buttons
			delCommBtn.setEnabled(true);
			if (rowIndices.length == 1)
				cloneCommButton.setEnabled(true);
			else
				cloneCommButton.setEnabled(false);

			countSegBtn.setEnabled(true);
			HashSet<String> spkSelect = new HashSet<String>();
			for (int selection : rowIndices) {

				Element cE = (Element) commTable.getModel().getValueAt(
						commTable.convertRowIndexToModel(selection), 0);
				data.getSelectedCommunications().put(
						cE.getAttributeValue("Id"), cE);
				if (cE.getChild("Setting") != null) {
					if (cE.getChild("Setting").getChildren("Person").size() > 0) {

						for (Object o : cE.getChild("Setting").getChildren(
								"Person")) {
							spkSelect.add(((Element) o).getText());
						}
					}
				}
			}
			setSpeakerCheckboxes(spkSelect);
			// speakerTableModel.selectSpeakers(spkSelect);
			showCommunicationValues(spkSelect.size());
		}
	}

	private void speakerSelected(int[] rowIndices) {
		data.getSelectedPersons().clear();
		int maxRow = 0;
		for (int rowIndex : rowIndices) {
			if (rowIndex > maxRow)
				maxRow = rowIndex;
		}
		if ((rowIndices.length == 0) | (maxRow >= speakerTable.getRowCount())) { // no
																					// or
																					// invalid
																					// selection
			removeSpeakerButton.setEnabled(false);
			cloneSpeakerButton.setEnabled(false);
		} else {
			lastSelected = SPEAKER;
			speakerRowIndices = rowIndices;
			removeSpeakerButton.setEnabled(true);
			personToBasketBtn.setEnabled(true);
			for (int selection : rowIndices) {
				Element p = (Element) speakerTable.getModel().getValueAt(
						speakerTable.convertRowIndexToModel(selection), 0);
				data.getSelectedPersons().put(p.getAttributeValue("Id"), p);
			}
			if (data.getSelectedPersons().size() > 1) {
				cloneSpeakerButton.setEnabled(false);
				showMultiSpeakerValues();
			} else {
				// removeCommSelection();
				try {
					showPerson();
				} catch (Exception exceptionFromHell) {
					// das sollte eigentlich nun wirklich nicht mehr
					// passieren!
					coma.error("Error from hell!",
							exceptionFromHell.getLocalizedMessage());
					coma.status("DataPanel:something went terribly wrong! Let's see...");
					coma.status(exceptionFromHell.toString());
					exceptionFromHell.printStackTrace();
					updateLists();
				}
				cloneSpeakerButton.setEnabled(true);
				removeSpeakerButton.setEnabled(true);
			}
			setCommunicationCheckboxes();
		}
	}

	/** saves and restores rowselection and checks assigned communications */
	private void setCommunicationCheckboxes() {
		if (commTable.getRowCount() > 0) {
			// get commSelections
			int[] rowIndices = commTable.getSelectedRows();
			for (int i = 0; i < rowIndices.length; i++) {
				rowIndices[i] = commTable.convertRowIndexToModel(rowIndices[i]);
			}
			if (coma.hasRoles()) {
				commTableModel.selectCommsWithRoles(coma.getData()
						.getCommIdsForSelectedSpeakers());
				// TODO: commTableModel.selectCommsWithRoles(ids der
				// kommunikationen
				// (hashset));
			} else {
				commTableModel.selectComms(coma.getData().getSelectedPersons()
						.keySet());
			}
			commTable.getSelectionModel().removeListSelectionListener(
					communicationTableSelectionListener);
			for (int i : rowIndices) {
				commTable.addRowSelectionInterval(
						commTable.convertRowIndexToView(i),
						commTable.convertRowIndexToView(i));
				Element c = (Element) commTable.getModel().getValueAt(i, 0);
				data.getSelectedCommunications().put(c.getAttributeValue("Id"),
						c);

			}
			commTable.getSelectionModel().addListSelectionListener(
					communicationTableSelectionListener);
		}
	}

	/**
	 * saves and restores rowselection and checks assigned speakers
	 * 
	 * @param spkSelect
	 */
	private void setSpeakerCheckboxes(HashSet<String> spkSelect) {
		if (speakerTable.getRowCount() > 0) {
			// get speakerSelections
			int[] rowIndices = speakerTable.getSelectedRows();
			for (int i = 0; i < rowIndices.length; i++) {
				rowIndices[i] = speakerTable
						.convertRowIndexToModel(rowIndices[i]);
			}
			speakerTableModel.selectSpeakers(spkSelect);
			speakerTable.getSelectionModel().removeListSelectionListener(
					speakerTableSelectionListener);

			for (int i : rowIndices) {
				speakerTable.addRowSelectionInterval(
						speakerTable.convertRowIndexToView(i),
						speakerTable.convertRowIndexToView(i));
				Element p = (Element) speakerTable.getModel().getValueAt(i, 0);
				data.getSelectedPersons().put(p.getAttributeValue("Id"), p);
			}
			speakerTable.getSelectionModel().addListSelectionListener(
					speakerTableSelectionListener);
		}
	}

	public class SelectionListener implements ListSelectionListener {
		JTable table;

		SelectionListener(JTable table) {
			this.table = table;
		}

		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				if (e.getSource() == table.getSelectionModel()
						&& table.getRowSelectionAllowed()) {
					// Column selection changed
					int[] rowIndices = table.getSelectedRows();
					if (table.getRowCount() > 0) {
						if (table == speakerTable) {
							speakerSelected(rowIndices);
							lastSelectedTable = "speakerTable";
							speakerCountLabel.setForeground(Color.BLUE);
							commCountLabel.setForeground(Color.BLACK);
						}
						if (table == commTable) {
							commSelected(rowIndices);
							lastSelectedTable = "commTable";
							speakerCountLabel.setForeground(Color.BLACK);
							commCountLabel.setForeground(Color.BLUE);
						}
					}
				}
			}
			if ((speakerTable.getSelectedRow() > -1)
					&& commTable.getSelectedRow() > -1) {
				assignButton.setEnabled(true);
				unassignButton.setEnabled(true);
				// TODO Multi-Selection editing - ja ja deine Mudder!
				if ((speakerTable.getSelectedRowCount() == 1)
						&& commTable.getSelectedRowCount() == 1) {
					// editRolesButton.setEnabled(true);
				}
			} else {
				assignButton.setEnabled(false);
				unassignButton.setEnabled(false);
				// editRolesButton.setEnabled(false);
			}
		}
	}

	/**
	 * 
	 */
	public void updateSelectedElement() {
		if (lastSelected == COMMUNICATION) {
			if ((data.getSelectedCommunications().size() == 1)
					&& (commRowIndices.length == 1)) {
				commTableModel.changeRow(data.getSelectedCommunications()
						.values().iterator().next(), commRowIndices[0]);
			}
		}

		if (lastSelected == SPEAKER) {
			if ((data.getSelectedPersons().size() == 1)
					&& (speakerRowIndices.length == 1)) {
				speakerTableModel.changeRow(data.getSelectedPersons().values()
						.iterator().next(), speakerRowIndices[0]);
			}
		}
	}

	/**
	 * updates the display of the selected element (because it probably changed)
	 */
	public void showSelectedValues() {
		if (lastSelected == COMMUNICATION) {
			showCommunicationValues();
		}
		if (lastSelected == SPEAKER) {
			showPerson();
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			if (e.getColumn() == 4) {
				if ((TableModel) e.getSource() == commTableModel) {
					XPath xp;
					try {
						xp = XPath.newInstance(commTableModel.getXPath());

						String v = (String) commTableModel.getValueAt(
								e.getFirstRow(), e.getColumn());
						List results = xp.selectNodes(data
								.getSelectedCommunications().values()
								.iterator().next());

						if (results.size() == 0) {
							XPath xp2 = XPath.newInstance(commTableModel
									.getXPath().substring(
											0,
											commTableModel.getXPath().indexOf(
													"/")));
							List results2 = xp2.selectNodes(data
									.getSelectedCommunications().values()
									.iterator().next());

							if (results2.size() > 0) {
								Element elx = (Element) results2.get(0);
								elx.addContent(new Element("Key").setAttribute(
										"Name",
										commTableModel.getXPath().substring(
												commTableModel.getXPath()
														.indexOf("'") + 1,
												commTableModel.getXPath()
														.lastIndexOf("'"))));
							}
							results = xp.selectNodes(data
									.getSelectedCommunications().values()
									.iterator().next());
						}
						if (results.size() > 0) {
							if (results.get(0).getClass().equals(Element.class)) {
								((Element) results.get(0)).setText(v);
							} else if (results.get(0).getClass()
									.equals(Attribute.class)) {
								((Attribute) results.get(0)).setValue(v);
							}
							coma.xmlChanged();
							updateLists(true);
						} else {
							System.out.println("hä?");
						}

					} catch (JDOMException err) {
						err.printStackTrace();
					}
				} else {
					XPath xp;
					try {
						xp = XPath.newInstance(speakerTableModel.getXPath());
						String v = (String) speakerTableModel.getValueAt(
								e.getFirstRow(), e.getColumn());
						List results = xp.selectNodes(data.getSelectedPersons()
								.values().iterator().next());
						if (results.size() == 0) {
							XPath xp2 = XPath.newInstance(speakerTableModel
									.getXPath().substring(
											0,
											speakerTableModel.getXPath()
													.indexOf("/")));
							List results2 = xp2.selectNodes(data
									.getSelectedPersons().values().iterator()
									.next());

							if (results2.size() > 0) {
								Element elx = (Element) results2.get(0);
								elx.addContent(new Element("Key").setAttribute(
										"Name",
										speakerTableModel.getXPath().substring(
												speakerTableModel.getXPath()
														.indexOf("'") + 1,
												speakerTableModel.getXPath()
														.lastIndexOf("'"))));
							}
							results = xp.selectNodes(data.getSelectedPersons()
									.values().iterator().next());

						}
						if (results.size() > 0) {
							if (results.get(0).getClass().equals(Element.class)) {
								((Element) results.get(0)).setText(v);
							} else if (results.get(0).getClass()
									.equals(Attribute.class)) {
								((Attribute) results.get(0)).setValue(v);
							}
							coma.xmlChanged();
							updateLists(true);
						} else {
							coma.status("Element doesn't exist!");
						}
					} catch (JDOMException err) {
						err.printStackTrace();
					}
				}
			}
		}
	}

	/** empties all displays. called for new, empty corpus. */
	public void reset() {
		speakerTableModel.setRowCount(0);
		commTableModel.setRowCount(0);
		valueDisplayPanel.setText("");
	}

	private void printValues() {
		File temp;
		try {
			temp = File.createTempFile("print", ".html");
			temp.deleteOnExit();
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(temp), "UTF8"));
			out.write(lastSelected == COMMUNICATION ? new Communication(data
					.getSelectedCommunications().values().iterator().next(),
					coma).toHTML(false) : new Speaker(coma, data
					.getSelectedPersons().get(0), false).toHTML(false));
			out.close();
			Desktop.getDesktop().open(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void unSelectAll() {
		commTable.removeRowSelectionInterval(0, commTable.getRowCount());
		speakerTable.removeRowSelectionInterval(0, speakerTable.getRowCount());
		updateLists();
	}

	public void setSelectionFromElements(HashSet<Element> elements) {
		System.out.println(elements.size());
		commTableModel.selectCommsbyElements(elements);
		speakerTableModel.selectSpeakersbyElements(elements);
	}

	public boolean isParsableToInt(String i) {
		try {
			Integer.parseInt(i);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
}