/*
 * EXAKT.java
 *
 * Created on 17. Januar 2007, 15:10
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import org.exmaralda.exakt.exmaraldaSearch.viewActions.BrowsingModeAction;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import java.io.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.*;
import java.util.prefs.*;
import java.util.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.sax.*;
import org.exmaralda.partitureditor.jexmaralda.convert.*;
import java.awt.Cursor;
import java.net.URL;
import org.jdom.*;
import org.jdom.transform.*;
import javax.swing.JOptionPane;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.common.helpers.RelativePath;
import org.exmaralda.exakt.search.*;
import org.exmaralda.exakt.exmaraldaSearch.*;
import org.exmaralda.exakt.search.swing.*;
import org.exmaralda.exakt.exmaraldaSearch.fileActions.*;
import org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.*;
import org.exmaralda.exakt.exmaraldaSearch.editActions.*;
import org.exmaralda.exakt.exmaraldaSearch.regexActions.RegexMenu;
import org.exmaralda.exakt.exmaraldaSearch.viewActions.ViewMenu;
import org.exmaralda.exakt.regex.RegexLibraryDialog;
import org.exmaralda.exakt.tokenlist.AbstractTokenList;
import org.exmaralda.exakt.tokenlist.DBTokenList;
import org.exmaralda.exakt.tokenlist.HashtableTokenList;
import org.exmaralda.exakt.tokenlist.WordlistDialog;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.exmaralda.partitureditor.listTable.ListTable;
import org.exmaralda.partitureditor.listTable.ListTableModel;
import org.xml.sax.SAXException;






/**
 *
 * @author  thomas
 */
public class EXAKT extends javax.swing.JFrame 
        implements  org.exmaralda.exakt.search.SearchListenerInterface, 
                    org.exmaralda.exakt.search.swing.KWICTableListener,
                    javax.swing.event.ChangeListener,
                    javax.swing.event.ListSelectionListener,
                    org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanelListener,
                    org.exmaralda.common.ExmaraldaApplication,
                    MouseListener {
    

    /** ch-ch-ch-changes! */
    
    String PATH_TO_STYLESHEET = "/org/exmaralda/exakt/resources/FormatTable4Partitur.xsl";
    String SEGMENTED_STYLESHEET = "/org/exmaralda/exakt/resources/ST2SCList.xsl";
    
    private static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
    private static final Cursor ORDINARY_CURSOR = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    
    COMACorpusListInternalFrame corpusListInternalFrame;

    public RegexLibraryDialog regexLibraryDialog;
    
    DefaultListModel corpusListModel = new DefaultListModel();
    public COMAKWICSearchPanelListModel concordanceListModel = new COMAKWICSearchPanelListModel();
    DefaultListModel wordListListModel = new DefaultListModel();
    
    String progressString;
    int currentProgress;
    boolean isOpening = false;
    public final static int UPDATE_TIME = 100;
    javax.swing.Timer timer;
    Vector<String[]> recentCorpora = new Vector<String[]>();
    
    PartitureTableWithActions partitur;
    String lastLoadedPartitur = "";
    
    public Hashtable<COMAKWICSearchPanel,Integer> panelIndex = new Hashtable<COMAKWICSearchPanel,Integer>();
    
    public OpenCorpusAction openCorpusAction;
    public OpenRemoteCorpusAction openRemoteCorpusAction;
    public OpenDBCorpusAction openDBCorpusAction;
    public GenerateCorpusAction generateCorpusAction;
    public GenerateFOLKERCorpusAction generateFolkerCorpusAction;
    public GenerateCHATCorpusAction generateCHATCorpusAction;
    public GenerateEAFCorpusAction generateEAFCorpusAction;
    public GenerateTranscriberCorpusAction generateTranscriberCorpusAction;
    
    public NewSearchPanelAction newSearchPanelAction;
    public SaveSearchResultAsAction saveSearchResultAsAction;
    public SaveSearchResultAction saveSearchResultAction;
    public OpenSearchResultAction openSearchResultAction;
    public AppendSearchResultAction appendSearchResultAction;
    public CloseSearchResultAction closeSearchResultAction;

    public NewWordlistAction newWordlistAction;
    
    public BrowsingModeAction browsingModeAction;
    public EditPreferencesAction editPreferencesAction;
    
    private File lastSearchResultPath;
    private File lastCorpusPath;
    
    FileMenu fileMenu;
    EditMenu editMenu;
    ViewMenu viewMenu;
    ConcordanceMenu concordanceMenu;
    ColumnsMenu columnsMenu;
    RowsMenu rowsMenu;
    RegexMenu regexMenu;

    KWICTableEvent lastKWICTableEvent = null;
    
    /** Creates new form EXAKT */
    public EXAKT() {
        new StartupSplashScreen(this);        
        
        org.exmaralda.common.Logger.initialiseLogger(this);

        initActions();
        partitur = new PartitureTableWithActions(this);        
        initComponents();

        regexLibraryDialog = new RegexLibraryDialog(this, false);
        regexLibraryDialog.setLocationRelativeTo(this);
        //regexLibraryDialog.setVisible(true);

        try {
            setIconImage(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/exakt_small.png")).getImage());
        } catch (Throwable t){
            System.out.println("Dann setzen wir halt kein Icon");
            t.printStackTrace();
        }

        initMenuBar();
        initPartitur();
        readSettings();

        fileMenu.setupOpenRecentMenu(recentCorpora);
        initExit();


        corpusList.setCellRenderer(new COMACorpusListCellRenderer());
        corpusList.setModel(corpusListModel);
        concordanceList.setCellRenderer((new COMAKWICSearchPanelListCellRenderer()));
        concordanceList.setModel(concordanceListModel);
        wordListList.setCellRenderer(new WordListListCellRenderer());
        wordListList.setModel(wordListListModel);

        timer = new javax.swing.Timer(UPDATE_TIME, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (!isOpening) return;
                progressBar.setValue(currentProgress);
                progressBar.setString(progressString);
            }
        });
        timer.start();
        
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(new java.awt.Dimension(screenSize.width, (int)Math.round(screenSize.height*0.9)));
        this.setPreferredSize(new java.awt.Dimension(screenSize.width, (int)Math.round(screenSize.height*0.9)));
        outerSplitPane.setDividerLocation((int)Math.round(0.7*0.9*screenSize.height));
        innerSplitPane.setDividerLocation((int)Math.round(0.2*screenSize.width));
        
        sendPartiturToBrowserButton.setText(null);
        changeScaleConstantButton.setText(null);
        exportRTFPartiturButton.setText(null);
        
        //partitur.mediaPanelDialog.setVisible(true);
        partitur.mediaPanelDialog.minimize(true);
        partitur.mediaPanelDialog.setAlwaysOnTop(true);
        HTMLViewPanel.setVisible(false);
        listViewPanel.setVisible(false);
        
        concordanceList.getSelectionModel().addListSelectionListener(this);
        corpusList.getSelectionModel().addListSelectionListener(this);
        tabbedPane.addChangeListener(this);
        corpusList.addMouseListener(this);
        wordListList.addMouseListener(this);
       
        menuBar.add(new org.exmaralda.common.application.HelpMenu("Help", this));
        
        setTitle("EXMARaLDA EXAKT " + getVersion());

        // if this is a MAC OS: init the MAC OS X specific actions
        String os = System.getProperty("os.name").substring(0,3);
        if (os.equalsIgnoreCase("mac")) {
            try {
            	new MacSpecials(this);
            }
            catch(Exception e) {
                System.out.println("Failed to init MAC OS X specific actions.");
                System.out.println(e.getLocalizedMessage());
            }
        }
        
        status("EXAKT started.");

                
    }
    
    
    public COMAKWICSearchPanel getActiveSearchPanel(){
        int index = tabbedPane.getSelectedIndex();
        if (index<0) return null;
        return (COMAKWICSearchPanel)(tabbedPane.getSelectedComponent());
    }
    
    public void closeActiveSearchPanel(){
        int index = tabbedPane.getSelectedIndex();
        if (index<0) return;
        int retValue = javax.swing.JOptionPane.showConfirmDialog(this, 
                "Are you sure?", 
                "Close concordance", 
                javax.swing.JOptionPane.YES_NO_OPTION);
        if (retValue!=javax.swing.JOptionPane.OK_OPTION) return;
        java.awt.Component c = tabbedPane.getSelectedComponent();
        tabbedPane.remove(c);
        concordanceListModel.removeElement(c);
        concordanceList.setSelectedValue(getActiveSearchPanel(),true);
        status("Concordance closed.");
    }

    private void initActions(){
        openCorpusAction = new OpenCorpusAction(this, "Open corpus...", null);
        openRemoteCorpusAction = new OpenRemoteCorpusAction(this, "Open remote corpus...", null);
        openDBCorpusAction = new OpenDBCorpusAction(this, "Open database corpus...", null);
        generateCorpusAction = new GenerateCorpusAction(this, "Generate corpus...", null);
        generateFolkerCorpusAction = new GenerateFOLKERCorpusAction(this, "FOLKER corpus...", null);
        generateCHATCorpusAction = new GenerateCHATCorpusAction(this, "CHAT corpus...", null);
        generateEAFCorpusAction = new GenerateEAFCorpusAction(this, "EAF corpus...", null);
        generateTranscriberCorpusAction = new GenerateTranscriberCorpusAction(this, "Transcriber corpus...", null);
        
        newSearchPanelAction = new NewSearchPanelAction(this, "New concordance", null);
        saveSearchResultAsAction = new SaveSearchResultAsAction(this, "Save concordance as...", null);
        saveSearchResultAction = new SaveSearchResultAction(this, "Save concordance", null);
        openSearchResultAction = new OpenSearchResultAction(this, "Open concordance...", null);
        appendSearchResultAction = new AppendSearchResultAction(this, "Append concordance...", null);
        closeSearchResultAction = new CloseSearchResultAction(this, "Close concordance", null);

        newWordlistAction = new NewWordlistAction(this, "New wordlist", null);

        editPreferencesAction = new EditPreferencesAction(this, "EXAKT preferences...", null);
        
        browsingModeAction = new BrowsingModeAction(this, "Browsing mode...", null);

        newSearchPanelAction.setEnabled(false);
        saveSearchResultAsAction.setEnabled(false);
        saveSearchResultAction.setEnabled(false);
        openSearchResultAction.setEnabled(false);
        appendSearchResultAction.setEnabled(false);
        closeSearchResultAction.setEnabled(false);
    }
    
    private void initMenuBar(){
        fileMenu = new FileMenu(this);
        menuBar.add(fileMenu);
        editMenu = new EditMenu(this);
        menuBar.add(editMenu);

        viewMenu = new ViewMenu(this);
        menuBar.add(viewMenu);

        concordanceMenu = new ConcordanceMenu(this);
        menuBar.add(concordanceMenu);

        columnsMenu = new ColumnsMenu(this);
        menuBar.add(columnsMenu);

        rowsMenu = new RowsMenu(this);
        menuBar.add(rowsMenu);

        regexMenu = new RegexMenu(this);
        menuBar.add(regexMenu);
    }
    
    private void initPartitur(){
        getPartitur().setLocked(true);
        getPartitur().scaleConstant = -1;
        getPartitur().setCellBorderWidth(0);
        getPartitur().setSettings(getPreferencesNode());
        partiturPanel.add(getPartitur());   
        getPartitur().mediaPanelDialog.setVisible(false);
    }
    
    void readSettings() {
        System.out.println("Reading settings");
        Preferences prefs = java.util.prefs.Preferences.userRoot().node(getPreferencesNode());
        int pos = 0;
        while (!(prefs.get("Corpus-Name" + Integer.toString(pos), "***").equals("***"))){
            String cn = prefs.get("Corpus-Name" + Integer.toString(pos),"");
            String cp = prefs.get("Corpus-Path" + Integer.toString(pos),"");
            pos++;
            String[] s = {cn,cp};
            recentCorpora.add(s);            
        }

        
        String lcp = prefs.get("Last-Corpus-Path", System.getProperties().getProperty("user.dir"));
        setLastCorpusPath(new File(lcp));

        String lsp = prefs.get("Last-Search-Result-Path", System.getProperties().getProperty("user.dir"));
        setLastSearchResultPath(new File(lsp));

    }
    
    void writeSettings(){
        System.out.println("Writing settings");
        Preferences prefs = java.util.prefs.Preferences.userRoot().node(getPreferencesNode());
        HashSet<String> doneWith = new HashSet<String>();
        for (int pos=0; pos<corpusListModel.size(); pos++){
            Object o = corpusListModel.elementAt(pos);
            COMACorpusInterface c = (COMACorpusInterface)o;
            // change 30-05-2011: Name can (but should not) be null
            String name = "[unnamed corpus]";
            if (c.getCorpusName()!=null){
                name = c.getCorpusName();
            }
            prefs.put("Corpus-Name" + Integer.toString(pos), name);
            prefs.put("Corpus-Path" + Integer.toString(pos), c.getCorpusPath());
            doneWith.add(c.getCorpusName());
        }        
        int pos=corpusListModel.size();
        for (String[] r : recentCorpora){
            String cn = r[0];
            if (!doneWith.contains(cn)){
                prefs.put("Corpus-Name" + Integer.toString(pos), cn);
                prefs.put("Corpus-Path" + Integer.toString(pos), r[1]);
                pos++;
            }
        }
        
        prefs.put("Last-Corpus-Path", getLastCorpusPath().getAbsolutePath());
        prefs.put("Last-Search-Result-Path", getLastSearchResultPath().getAbsolutePath());
    }
    
   /** initializes the exit action */
   private void initExit(){
        javax.swing.AbstractAction exitAction = 
                new javax.swing.AbstractAction(org.exmaralda.common.helpers.Internationalizer.getString("Exit")){
            public void actionPerformed(java.awt.event.ActionEvent e) {
                exitForm(null);
            }
        };
        fileMenu.addSeparator();
        fileMenu.add(exitAction);
    }
   
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {
        // TODO: check if there's something to be checked
        boolean proceed = true;
        try {
            regexLibraryDialog.saveUserLibrary();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not save user regex library:\n" + ex.getLocalizedMessage());
        }

        if (proceed){
            getPartitur().getSettings(getPreferencesNode());
            writeSettings();        
            System.out.println("Application terminated. ");
            System.out.println("____________________________________________________________________________");
            System.exit(0);
        }
    }
   
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        viewChooserButtonGroup = new javax.swing.ButtonGroup();
        outerSplitPane = new javax.swing.JSplitPane();
        innerSplitPane = new javax.swing.JSplitPane();
        thingsPanel = new javax.swing.JPanel();
        leftSideSplitPane = new javax.swing.JSplitPane();
        corpusListPanel = new javax.swing.JPanel();
        corpusListScrollPane = new javax.swing.JScrollPane();
        corpusList = new javax.swing.JList();
        progressBarPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        concWordSplitPane = new javax.swing.JSplitPane();
        concordanceListPanel = new javax.swing.JPanel();
        concordanceListScrollPane = new javax.swing.JScrollPane();
        concordanceList = new javax.swing.JList();
        wordListPanel = new javax.swing.JPanel();
        wordListListScrollPane = new javax.swing.JScrollPane();
        wordListList = new javax.swing.JList();
        tabbedPane = new javax.swing.JTabbedPane();
        viewPanel = new javax.swing.JPanel();
        viewChooserPanel = new javax.swing.JPanel();
        partiturViewRadioButton = new javax.swing.JRadioButton();
        listViewRadioButton = new javax.swing.JRadioButton();
        segmentedAndXSLViewRadioButton = new javax.swing.JRadioButton();
        viewCenterPanel = new javax.swing.JPanel();
        partiturViewPanel = new javax.swing.JPanel();
        partiturPanel = new javax.swing.JPanel();
        partiturButtonPanel = new javax.swing.JPanel();
        mediaButtonsPanel = new javax.swing.JPanel();
        playButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        visualizeButtonsPanel = new javax.swing.JPanel();
        sendPartiturToBrowserButton = new javax.swing.JButton();
        exportRTFPartiturButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        changeScaleConstantButton = new javax.swing.JButton();
        listViewPanel = new javax.swing.JPanel();
        listScrollPane = new javax.swing.JScrollPane();
        listTable = new org.exmaralda.partitureditor.listTable.ListTable();
        listButtonPanel = new javax.swing.JPanel();
        HTMLViewPanel = new javax.swing.JPanel();
        HTMLViewScrollPane = new javax.swing.JScrollPane();
        HTMLEditorPane = new javax.swing.JEditorPane();
        HTMLButtonPanel = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        openCorpusButton = new javax.swing.JButton();
        generateCorpusButton = new javax.swing.JButton();
        separator3 = new javax.swing.JToolBar.Separator();
        newConcordanceButton = new javax.swing.JButton();
        separator2 = new javax.swing.JToolBar.Separator();
        preferencesButton = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("EXAKT (PV8 0.4)");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        outerSplitPane.setDividerSize(10);
        outerSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        outerSplitPane.setOneTouchExpandable(true);

        innerSplitPane.setDividerSize(10);
        innerSplitPane.setOneTouchExpandable(true);

        thingsPanel.setLayout(new java.awt.BorderLayout());

        leftSideSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        leftSideSplitPane.setMinimumSize(new java.awt.Dimension(0, 0));
        leftSideSplitPane.setPreferredSize(new java.awt.Dimension(150, 190));

        corpusListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Corpora"));
        corpusListPanel.setLayout(new java.awt.BorderLayout());

        corpusList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        corpusListScrollPane.setViewportView(corpusList);

        corpusListPanel.add(corpusListScrollPane, java.awt.BorderLayout.CENTER);

        progressBarPanel.setLayout(new javax.swing.BoxLayout(progressBarPanel, javax.swing.BoxLayout.LINE_AXIS));

        progressBar.setStringPainted(true);
        progressBarPanel.add(progressBar);

        corpusListPanel.add(progressBarPanel, java.awt.BorderLayout.SOUTH);

        leftSideSplitPane.setLeftComponent(corpusListPanel);

        concWordSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        concordanceListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Concordances"));
        concordanceListPanel.setLayout(new java.awt.BorderLayout());

        concordanceList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        concordanceListScrollPane.setViewportView(concordanceList);

        concordanceListPanel.add(concordanceListScrollPane, java.awt.BorderLayout.CENTER);

        concWordSplitPane.setRightComponent(concordanceListPanel);

        wordListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Word lists"));
        wordListPanel.setLayout(new java.awt.BorderLayout());

        wordListList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        wordListListScrollPane.setViewportView(wordListList);

        wordListPanel.add(wordListListScrollPane, java.awt.BorderLayout.CENTER);

        concWordSplitPane.setLeftComponent(wordListPanel);

        leftSideSplitPane.setRightComponent(concWordSplitPane);

        thingsPanel.add(leftSideSplitPane, java.awt.BorderLayout.CENTER);

        innerSplitPane.setLeftComponent(thingsPanel);

        tabbedPane.setBackground(new java.awt.Color(102, 51, 0));
        innerSplitPane.setRightComponent(tabbedPane);

        outerSplitPane.setLeftComponent(innerSplitPane);

        viewPanel.setLayout(new java.awt.BorderLayout());

        viewChooserButtonGroup.add(partiturViewRadioButton);
        partiturViewRadioButton.setSelected(true);
        partiturViewRadioButton.setText("Partitur");
        partiturViewRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        partiturViewRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        partiturViewRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                partiturViewRadioButtonActionPerformed(evt);
            }
        });
        viewChooserPanel.add(partiturViewRadioButton);

        viewChooserButtonGroup.add(listViewRadioButton);
        listViewRadioButton.setText("List");
        listViewRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listViewRadioButtonActionPerformed(evt);
            }
        });
        viewChooserPanel.add(listViewRadioButton);

        viewChooserButtonGroup.add(segmentedAndXSLViewRadioButton);
        segmentedAndXSLViewRadioButton.setText("HTML");
        segmentedAndXSLViewRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        segmentedAndXSLViewRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        segmentedAndXSLViewRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segmentedAndXSLViewRadioButtonActionPerformed(evt);
            }
        });
        viewChooserPanel.add(segmentedAndXSLViewRadioButton);

        viewPanel.add(viewChooserPanel, java.awt.BorderLayout.SOUTH);

        viewCenterPanel.setLayout(new javax.swing.BoxLayout(viewCenterPanel, javax.swing.BoxLayout.LINE_AXIS));

        partiturViewPanel.setLayout(new java.awt.BorderLayout());

        partiturPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Partitur"));
        partiturPanel.setLayout(new javax.swing.BoxLayout(partiturPanel, javax.swing.BoxLayout.LINE_AXIS));
        partiturViewPanel.add(partiturPanel, java.awt.BorderLayout.CENTER);

        partiturButtonPanel.setLayout(new javax.swing.BoxLayout(partiturButtonPanel, javax.swing.BoxLayout.Y_AXIS));

        mediaButtonsPanel.setLayout(new javax.swing.BoxLayout(mediaButtonsPanel, javax.swing.BoxLayout.LINE_AXIS));

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/media-playback-start.png"))); // NOI18N
        playButton.setToolTipText("Play");
        playButton.setEnabled(false);
        playButton.setMaximumSize(new java.awt.Dimension(28, 28));
        playButton.setMinimumSize(new java.awt.Dimension(28, 28));
        playButton.setPreferredSize(new java.awt.Dimension(28, 28));
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });
        mediaButtonsPanel.add(playButton);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/media-playback-stop.png"))); // NOI18N
        stopButton.setToolTipText("Stop");
        stopButton.setEnabled(false);
        stopButton.setMaximumSize(new java.awt.Dimension(28, 28));
        stopButton.setMinimumSize(new java.awt.Dimension(28, 28));
        stopButton.setPreferredSize(new java.awt.Dimension(28, 28));
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        mediaButtonsPanel.add(stopButton);

        partiturButtonPanel.add(mediaButtonsPanel);

        visualizeButtonsPanel.setLayout(new javax.swing.BoxLayout(visualizeButtonsPanel, javax.swing.BoxLayout.LINE_AXIS));

        sendPartiturToBrowserButton.setAction(partitur.sendHTMLPartitureToBrowserAction);
        sendPartiturToBrowserButton.setToolTipText("Open partitur in browser");
        sendPartiturToBrowserButton.setMaximumSize(new java.awt.Dimension(28, 28));
        sendPartiturToBrowserButton.setMinimumSize(new java.awt.Dimension(28, 28));
        sendPartiturToBrowserButton.setPreferredSize(new java.awt.Dimension(28, 28));
        visualizeButtonsPanel.add(sendPartiturToBrowserButton);

        exportRTFPartiturButton.setAction(partitur.outputAction);
        exportRTFPartiturButton.setMaximumSize(new java.awt.Dimension(28, 28));
        exportRTFPartiturButton.setMinimumSize(new java.awt.Dimension(28, 28));
        exportRTFPartiturButton.setPreferredSize(new java.awt.Dimension(28, 28));
        visualizeButtonsPanel.add(exportRTFPartiturButton);

        partiturButtonPanel.add(visualizeButtonsPanel);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        changeScaleConstantButton.setAction(partitur.changeScaleConstantAction);
        changeScaleConstantButton.setMaximumSize(new java.awt.Dimension(28, 28));
        changeScaleConstantButton.setMinimumSize(new java.awt.Dimension(28, 28));
        changeScaleConstantButton.setPreferredSize(new java.awt.Dimension(28, 28));
        jPanel2.add(changeScaleConstantButton);

        partiturButtonPanel.add(jPanel2);

        partiturViewPanel.add(partiturButtonPanel, java.awt.BorderLayout.WEST);

        viewCenterPanel.add(partiturViewPanel);

        listViewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("List"));
        listViewPanel.setLayout(new java.awt.BorderLayout());

        listTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        listScrollPane.setViewportView(listTable);

        listViewPanel.add(listScrollPane, java.awt.BorderLayout.CENTER);

        listButtonPanel.setLayout(new javax.swing.BoxLayout(listButtonPanel, javax.swing.BoxLayout.Y_AXIS));
        listViewPanel.add(listButtonPanel, java.awt.BorderLayout.WEST);

        viewCenterPanel.add(listViewPanel);

        HTMLViewPanel.setLayout(new java.awt.BorderLayout());

        HTMLViewScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("HTML View"));

        HTMLEditorPane.setContentType("text/html"); // NOI18N
        HTMLEditorPane.setEditable(false);
        HTMLEditorPane.setPreferredSize(new java.awt.Dimension(1000, 1000));
        HTMLViewScrollPane.setViewportView(HTMLEditorPane);

        HTMLViewPanel.add(HTMLViewScrollPane, java.awt.BorderLayout.CENTER);

        HTMLButtonPanel.setLayout(new javax.swing.BoxLayout(HTMLButtonPanel, javax.swing.BoxLayout.LINE_AXIS));
        HTMLViewPanel.add(HTMLButtonPanel, java.awt.BorderLayout.WEST);

        viewCenterPanel.add(HTMLViewPanel);

        viewPanel.add(viewCenterPanel, java.awt.BorderLayout.CENTER);

        outerSplitPane.setRightComponent(viewPanel);

        getContentPane().add(outerSplitPane, java.awt.BorderLayout.CENTER);

        openCorpusButton.setAction(openCorpusAction);
        openCorpusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/Open.gif"))); // NOI18N
        openCorpusButton.setText(null);
        openCorpusButton.setToolTipText("Open a corpus");
        toolBar.add(openCorpusButton);

        generateCorpusButton.setAction(generateCorpusAction);
        generateCorpusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/corpuscreator24.png"))); // NOI18N
        generateCorpusButton.setText(null);
        generateCorpusButton.setToolTipText("Generate a corpus");
        generateCorpusButton.setFocusable(false);
        generateCorpusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        generateCorpusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(generateCorpusButton);
        toolBar.add(separator3);

        newConcordanceButton.setAction(newSearchPanelAction);
        newConcordanceButton.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        newConcordanceButton.setText("<html>\n\t<span style=\"color:black\">A</span>\n\t<span style=\"color:red\">B</span>\n\t<span style=\"color:black\">C</span>\n</html>");
        newConcordanceButton.setToolTipText("New concordance");
        newConcordanceButton.setActionCommand("<html><span style=\"color:black\">A</span><span style=\"color:red\">B</span><span style=\"color:black\">C</span></html>");
        newConcordanceButton.setFocusable(false);
        newConcordanceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newConcordanceButton.setMaximumSize(new java.awt.Dimension(31, 31));
        newConcordanceButton.setMinimumSize(new java.awt.Dimension(31, 31));
        newConcordanceButton.setPreferredSize(new java.awt.Dimension(31, 31));
        newConcordanceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(newConcordanceButton);
        toolBar.add(separator2);

        preferencesButton.setAction(editPreferencesAction);
        preferencesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/Preferences.gif"))); // NOI18N
        preferencesButton.setText(null);
        preferencesButton.setToolTipText("EXAKT preferences...");
        toolBar.add(preferencesButton);

        getContentPane().add(toolBar, java.awt.BorderLayout.NORTH);

        statusPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        statusPanel.setLayout(new javax.swing.BoxLayout(statusPanel, javax.swing.BoxLayout.LINE_AXIS));

        statusLabel.setText("jLabel1");
        statusPanel.add(statusLabel);

        getContentPane().add(statusPanel, java.awt.BorderLayout.PAGE_END);
        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void segmentedAndXSLViewRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentedAndXSLViewRadioButtonActionPerformed
        partiturViewPanel.setVisible(partiturViewRadioButton.isSelected());
        HTMLViewPanel.setVisible(segmentedAndXSLViewRadioButton.isSelected());
        listViewPanel.setVisible(listViewRadioButton.isSelected());
        if (lastKWICTableEvent!=null){
            showHTML(lastKWICTableEvent);
        }

    }//GEN-LAST:event_segmentedAndXSLViewRadioButtonActionPerformed

    private void partiturViewRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_partiturViewRadioButtonActionPerformed
        partiturViewPanel.setVisible(partiturViewRadioButton.isSelected());
        HTMLViewPanel.setVisible(segmentedAndXSLViewRadioButton.isSelected());
        listViewPanel.setVisible(listViewRadioButton.isSelected());
        if (lastKWICTableEvent!=null){
            showPartitur(lastKWICTableEvent);
        }
    }//GEN-LAST:event_partiturViewRadioButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        partitur.mediaPanelDialog.doStop();
        playButton.setEnabled(true);
        stopButton.setEnabled(false);
    }//GEN-LAST:event_stopButtonActionPerformed

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        playButton.setEnabled(false);
        stopButton.setEnabled(true);     
        if (partitur.mediaPanelDialog.isVideo()){
            partitur.mediaPanelDialog.setVisible(true);
        }
        partitur.mediaPanelDialog.doPlay();
    }//GEN-LAST:event_playButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        exitForm(evt);
    }//GEN-LAST:event_formWindowClosing

    private void listViewRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listViewRadioButtonActionPerformed
        partiturViewPanel.setVisible(partiturViewRadioButton.isSelected());
        HTMLViewPanel.setVisible(segmentedAndXSLViewRadioButton.isSelected());
        listViewPanel.setVisible(listViewRadioButton.isSelected());
        if (lastKWICTableEvent!=null){
            showList(lastKWICTableEvent);
        }
    }//GEN-LAST:event_listViewRadioButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        boolean thisIsAMac = System.getProperty("os.name").substring(0,3).equalsIgnoreCase("mac");
        try{
            if (thisIsAMac){
                System.out.println("Setting Quaqua L&F");
                Set includes = new HashSet();
                includes.add("ColorChooser");
                includes.add("FileChooser");
                includes.add("Component");
                includes.add("Browser");
                includes.add("Tree");
                includes.add("SplitPane");
                ch.randelshofer.quaqua.QuaquaManager.setIncludedUIs(includes);
                UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
            } else {
                System.out.println("Setting system L&F : " + javax.swing.UIManager.getSystemLookAndFeelClassName());
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
          e.printStackTrace();        
        }
        EXAKT ex = new EXAKT();
        if (args.length>0){
            try{
                ex.doOpen(new File(args[0]));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        ex.setVisible(true);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel HTMLButtonPanel;
    private javax.swing.JEditorPane HTMLEditorPane;
    private javax.swing.JPanel HTMLViewPanel;
    private javax.swing.JScrollPane HTMLViewScrollPane;
    private javax.swing.JButton changeScaleConstantButton;
    private javax.swing.JSplitPane concWordSplitPane;
    public javax.swing.JList concordanceList;
    private javax.swing.JPanel concordanceListPanel;
    private javax.swing.JScrollPane concordanceListScrollPane;
    public javax.swing.JList corpusList;
    private javax.swing.JPanel corpusListPanel;
    private javax.swing.JScrollPane corpusListScrollPane;
    private javax.swing.JButton exportRTFPartiturButton;
    private javax.swing.JButton generateCorpusButton;
    private javax.swing.JSplitPane innerSplitPane;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane leftSideSplitPane;
    private javax.swing.JPanel listButtonPanel;
    private javax.swing.JScrollPane listScrollPane;
    private javax.swing.JTable listTable;
    private javax.swing.JPanel listViewPanel;
    private javax.swing.JRadioButton listViewRadioButton;
    private javax.swing.JPanel mediaButtonsPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton newConcordanceButton;
    private javax.swing.JButton openCorpusButton;
    private javax.swing.JSplitPane outerSplitPane;
    private javax.swing.JPanel partiturButtonPanel;
    private javax.swing.JPanel partiturPanel;
    private javax.swing.JPanel partiturViewPanel;
    private javax.swing.JRadioButton partiturViewRadioButton;
    private javax.swing.JButton playButton;
    private javax.swing.JButton preferencesButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel progressBarPanel;
    private javax.swing.JRadioButton segmentedAndXSLViewRadioButton;
    private javax.swing.JButton sendPartiturToBrowserButton;
    private javax.swing.JToolBar.Separator separator2;
    private javax.swing.JToolBar.Separator separator3;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton stopButton;
    public javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel thingsPanel;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel viewCenterPanel;
    private javax.swing.ButtonGroup viewChooserButtonGroup;
    private javax.swing.JPanel viewChooserPanel;
    private javax.swing.JPanel viewPanel;
    private javax.swing.JPanel visualizeButtonsPanel;
    private javax.swing.JList wordListList;
    private javax.swing.JScrollPane wordListListScrollPane;
    private javax.swing.JPanel wordListPanel;
    // End of variables declaration//GEN-END:variables
    
    //private COMACorpus corpusToBeAdded = null;
    private COMACorpusInterface corpusToBeAdded = null;
    
    void updateCorpusList(){
        corpusListModel.addElement(corpusToBeAdded);
        isOpening = false;
        progressBar.setValue(100);
        progressBar.setString("Done.");
        corpusList.setSelectedIndex(corpusList.getModel().getSize()-1);
        // added 21-01-2010: automatically make a new concordance for each newly opened corpus
        newSearchPanelAction.actionPerformed(null);

        if (corpusToBeAdded.isWordSegmented()){
            int value = JOptionPane.showConfirmDialog(this,
                    "The corpus contains a word segmentation.\nDo you want to create a word list?",
                    "Word list",
                    JOptionPane.YES_NO_OPTION);
            if (value==JOptionPane.YES_OPTION){
                readWordList(corpusToBeAdded);
            }
        }
    }


    public void readWordList(COMACorpusInterface corpus){
        final ProgressBarDialog pbd = new ProgressBarDialog(this, false);
        pbd.setTitle("Reading word list from " + corpus.getCorpusName());
        pbd.setLocationRelativeTo(this);
        pbd.setVisible(true);

        final AbstractTokenList l;
        if (corpus instanceof COMADBCorpus){
            l = new DBTokenList();
            pbd.progressBar.setIndeterminate(true);
            pbd.progressBar.setString("Getting tokens from database...");
        } else {
            l = new HashtableTokenList();
            ((HashtableTokenList)l).addSearchListener(pbd);
        }
        final COMACorpusInterface finalCorpus = corpus;
        final Runnable doUpdateWordListList = new Runnable() {
             public void run() {
                 updateWordListList();
             }
         };
        Thread openThread = new Thread(){
            @Override
            public void run(){
                try {
                    l.readWordsFromExmaraldaCorpus(finalCorpus);
                    wordlistToBeAdded = l;
                    pbd.setVisible(false);
                    try {
                        javax.swing.SwingUtilities.invokeAndWait(doUpdateWordListList);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    setCursor(ORDINARY_CURSOR);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showErrorDialog(ex);
                    progressBar.setValue(100);
                    progressBar.setString("Error");
                    setCursor(ORDINARY_CURSOR);
                }
            }
        };
        openThread.start();
    }

    private AbstractTokenList wordlistToBeAdded = null;

    void updateWordListList(){
        this.wordListListModel.addElement(wordlistToBeAdded);
        status("Wordlist added.");
        
    }
    
    final Runnable doUpdateCorpusList = new Runnable() {
         public void run() {
             updateCorpusList();
         }
     };
    

    public void doOpen(File f){
        setCursor(WAIT_CURSOR);
        final File file = f;
        final COMACorpus corpus = new COMACorpus();
        corpus.addSearchListener(this);
        isOpening = true;
        Thread openThread = new Thread(){
            @Override
            public void run(){
                try {
                    corpus.readCorpus(file);
                    corpusToBeAdded = corpus;
                    try {
                        javax.swing.SwingUtilities.invokeAndWait(doUpdateCorpusList);
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    setCursor(ORDINARY_CURSOR);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showErrorDialog(ex);
                    progressBar.setValue(100);
                    progressBar.setString("Error");
                    setCursor(ORDINARY_CURSOR);
                }
            }
        };
        openThread.start();        
        //javax.swing.SwingUtilities.invokeLater(openThread);
    }

    public void doOpen(URL u){
        setCursor(WAIT_CURSOR);
        final URL url = u;
        final COMARemoteCorpus corpus = new COMARemoteCorpus();
        corpus.addSearchListener(this);
        isOpening = true;
        Thread openThread = new Thread(){
            @Override
            public void run(){
                try {
                    corpus.readCorpus(url);
                    corpusToBeAdded = corpus;
                    try {
                        javax.swing.SwingUtilities.invokeAndWait(doUpdateCorpusList);
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    setCursor(ORDINARY_CURSOR);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showErrorDialog(ex);
                    progressBar.setValue(100);
                    progressBar.setString("Error");
                    setCursor(ORDINARY_CURSOR);
                } 
            }
        };
        openThread.start();
        //javax.swing.SwingUtilities.invokeLater(openThread);
    }

    public void doOpenDB(final String corpusName, final String connection, final String usr, final String pwd){
        setCursor(WAIT_CURSOR);
        final COMADBCorpus corpus = new COMADBCorpus();
        corpus.addSearchListener(this);
        isOpening = true;
        Thread openThread = new Thread(){
            @Override
            public void run(){
                try {
                    corpus.readCorpus(corpusName, connection, usr, pwd);
                    corpusToBeAdded = corpus;
                    try {
                        javax.swing.SwingUtilities.invokeAndWait(doUpdateCorpusList);
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    setCursor(ORDINARY_CURSOR);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showErrorDialog(ex);
                    progressBar.setValue(100);
                    progressBar.setString("Error");
                    setCursor(ORDINARY_CURSOR);
                }
            }
        };
        openThread.start();
    }


    public void showErrorDialog(Exception ex){
        JOptionPane.showMessageDialog(this, ex.getMessage());        
    }

    public void processSearchEvent(SearchEvent se) {
        switch(se.getType()){
            case SearchEvent.CORPUS_INIT_PROGRESS : 
                progressString = (String)(se.getData());
                progressString = progressString.substring(Math.max(0,progressString.length()-40));
                currentProgress = (int)Math.round(se.getProgress()*100);        
                break;
            case SearchEvent.SEARCH_COMPLETED :
                System.out.println("Search completed!");
                break;
        }
    }

    public void processEvent(KWICTableEvent ev) {
        if (ev.getType() == KWICTableEvent.DOUBLE_CLICK){
            if (partiturViewRadioButton.isSelected()){
                // update partiture
                showPartitur(ev);
            } else if (segmentedAndXSLViewRadioButton.isSelected()) {
                // update HTML view
                showHTML(ev);
            } else if (listViewRadioButton.isSelected()) {
                // update HTML view
                showList(ev);
            }
        }
    }

    public void processEvent(COMAKWICSearchPanelEvent ev) {
        if (ev.getType()==COMAKWICSearchPanelEvent.NEW_SEARCH_RESULT){
            SearchResultList srl = ev.getSearchResultList();
            String title = "No results";
            if (srl.size()>0) title = srl.elementAt(0).getMatchTextAsString();
            COMAKWICSearchPanel panel = (COMAKWICSearchPanel)(ev.getParentComponent());
            int index = panelIndex.get(panel).intValue();
            tabbedPane.setTitleAt(index, panel.getCorpus().getCorpusName() + " (" + srl.size() + " results)");
            tabbedPane.setToolTipTextAt(index, title);   
            concordanceListModel.fireContentsChanged(panel,0,concordanceListModel.getSize());
        }
    }
    
    private SegmentedTranscription getTranscriptionForSearchResult(SearchResultInterface sri) throws SAXException{
        String filename = sri.getSearchableSegmentLocator().getCorpusComponentFilename();
        SegmentedTranscriptionSaxReader str = new SegmentedTranscriptionSaxReader();
        SegmentedTranscription st = null;
        COMACorpusInterface activeCorpus = getActiveSearchPanel().getCorpus();
        if (activeCorpus instanceof COMACorpus){
            st = str.readFromFile(filename);
            status(filename + " read.");
        } else if (activeCorpus instanceof COMARemoteCorpus) {
            //System.out.println("Full filename: " + filename);
            st = str.readFromURL(filename);
            status(filename + " read.");
        } else if (activeCorpus instanceof COMADBCorpus){
            //System.out.println("Corpus: " + activeCorpus.getCorpusPath());
            //System.out.println("Filename: " + filename);
            String fullFilename = RelativePath.resolveRelativePath(filename,activeCorpus.getCorpusPath());
            //System.out.println("Full filename: " + fullFilename);
            st = str.readFromURL(fullFilename);
            //st = str.readFromFile(fullFilename);
            status(fullFilename + " read.");
        }
        return st;
        
    }

    public void showList(KWICTableEvent event) {
        lastKWICTableEvent = event;
        // added 07-05-2009
        if (partitur.player!=null){
            partitur.player.stopPlayback();
        }
        SimpleSearchResult searchResult = (SimpleSearchResult)(event.getSelectedSearchResult());
        setCursor(WAIT_CURSOR);
        int dl = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT").getInt("full-display-limit", 50);
        //System.out.println("FILENAME: " + filename);
        try {
            SegmentedTranscription st = getTranscriptionForSearchResult(searchResult);
            SegmentedToListInfo stl = new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION);
            ListTranscription lt = st.toListTranscription(stl);
            lt.getBody().sort();
            listTable.setModel(new ListTableModel(lt));

            final String tierID = searchResult.getAdditionalData()[0];
            final String timeID = searchResult.getAdditionalData()[2];
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    ((ListTable)listTable).makeVisible(tierID, timeID);
                }
            });

        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage());
        }
        setCursor(ORDINARY_CURSOR);
    }

    public void showPartitur(KWICTableEvent event) {
        lastKWICTableEvent = event;
        // added 07-05-2009
        if (partitur.player!=null){
            partitur.player.stopPlayback();
        }
        setCursor(WAIT_CURSOR);
        int dl = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT").getInt("full-display-limit", 50);
        SimpleSearchResult searchResult = (SimpleSearchResult)(event.getSelectedSearchResult());
        try{
            SegmentedTranscription st = getTranscriptionForSearchResult(searchResult);
            BasicTranscription bt = st.toBasicTranscription();
            String tierID = searchResult.getAdditionalData()[0];
            Tier tier = bt.getBody().getTierWithID(tierID);
            String timeID = searchResult.getAdditionalData()[2];
            
            if (!(searchResult instanceof AnnotationSearchResult)){
                // find the exact startpoint
                int count = 0;
                int s = searchResult.getOriginalMatchStart();
                while (count + tier.getEventAtStartPoint(timeID).getDescription().length() <= s){
                    count+=tier.getEventAtStartPoint(timeID).getDescription().length();
                    timeID = tier.getEventAtStartPoint(timeID).getEnd();
                }                            
            }
            
            String timeID2 = st.getBody().getCommonTimelineMatch(timeID);
            
            if (dl>0){
                int index = bt.getBody().getCommonTimeline().lookupID(timeID2);
                int startIndex = Math.max(index-dl,0);
                String startTLI = bt.getBody().getCommonTimeline().getTimelineItemAt(startIndex).getID();
                int endIndex = Math.min(index+dl,bt.getBody().getCommonTimeline().getNumberOfTimelineItems()-1);
                String endTLI = bt.getBody().getCommonTimeline().getTimelineItemAt(endIndex).getID();
                bt = bt.getPartOfTranscription(bt.getBody().getAllTierIDs(), startTLI, endTLI);
            }
            if ((dl>0) || (!lastLoadedPartitur.equals(searchResult.getSearchableSegmentLocator().getCorpusComponentFilename()))){
                StylesheetFactory sf = new StylesheetFactory();
                String formatString = "";
                String pathToPartiturStylesheet = java.util.prefs.Preferences.
                                                    userRoot().node("org.sfb538.exmaralda.EXAKT").
                                                    get("xsl-partitur-tool","");
                if (pathToPartiturStylesheet.length()<=0){    
                    formatString = sf.applyInternalStylesheetToString(PATH_TO_STYLESHEET, bt.toXML());
                } else {
                    try{
                        formatString = sf.applyExternalStylesheetToString(pathToPartiturStylesheet, bt.toXML());
                    } catch (Exception e){
                        formatString = sf.applyInternalStylesheetToString(PATH_TO_STYLESHEET, bt.toXML());                        
                    }
                }
                TierFormatTable tft = new TierFormatTable();
                tft.TierFormatTableFromString(formatString);                
                partitur.getModel().setTranscriptionAndTierFormatTable(bt, tft);
                boolean success = partitur.setupMedia();
                playButton.setEnabled(success);
                stopButton.setEnabled(false);
                getPartitur().getModel().setTranscriptionAndTierFormatTable(bt,tft);
                //getPartitur().setPlaybackMode(true);
            }
            System.out.println(tierID +  "  "  + timeID2);
            getPartitur().makeVisible(tierID, timeID2);
            lastLoadedPartitur = searchResult.getSearchableSegmentLocator().getCorpusComponentFilename();
        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage());
        }
        setCursor(ORDINARY_CURSOR);        
    }
    
    public void showHTML(KWICTableEvent event) {    
        lastKWICTableEvent = event;
        setCursor(WAIT_CURSOR);
        SimpleSearchResult searchResult = (SimpleSearchResult)(event.getSelectedSearchResult());
        String filename = (String)(searchResult.getSearchableSegmentLocator().getCorpusComponentFilename());
        Document xmlDocument;
        try {
            if (getActiveSearchPanel().getCorpus() instanceof COMACorpus){
                xmlDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(new File(filename));                
            } else {
                xmlDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromURL(filename);                                
            }
            String reference = searchResult.getAdditionalData()[2];
            org.exmaralda.exakt.utilities.FileIO.reduceSegmentedTranscription(xmlDocument,reference);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        } catch (JDOMException ex) {
            ex.printStackTrace();
            return;
        }
        
        String pathToXSL = java.util.prefs.Preferences.
                                            userRoot().node("org.sfb538.exmaralda.EXAKT").
                                            get("xsl-segmented-tool","");                                                    
        boolean failed = false;
        
        Document transformedDocument = null;
        
        if (pathToXSL.length()>0){
            try {
                // try to use external stylesheet
                org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory ssf =
                        new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);
                String result = 
                        ssf.applyExternalStylesheetToString(pathToXSL, org.exmaralda.exakt.utilities.FileIO.getDocumentAsString(xmlDocument));
                System.out.println(result);
                transformedDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromString(result);                
            } catch (Exception ex) {
                ex.printStackTrace();
                String message = "There is a problem with " + pathToXSL + ": \n";
                message += ex.getMessage() + "\n";
                message += "Using default stylesheet instead.";
                failed = true;
                javax.swing.JOptionPane.showMessageDialog(this, message);                
            }
        }
         if ((failed) || (pathToXSL.length()<=0)){    
            try {
                java.io.InputStream is = getClass().getResourceAsStream(SEGMENTED_STYLESHEET);
                XSLTransformer transformer = new XSLTransformer(is);
                transformedDocument = transformer.transform(xmlDocument);
            } catch (XSLTransformException ex) {
                ex.printStackTrace();
                return;
            }
        }
        try {
            String htmlString = org.exmaralda.exakt.utilities.FileIO.getDocumentAsString(transformedDocument, true);
            HTMLEditorPane.setText(htmlString);
            HTMLEditorPane.setCaretPosition(0);
            final String reference = searchResult.getAdditionalData()[2];
            HTMLEditorPane.scrollToReference(reference);
            HTMLEditorPane.scrollToReference("#" + reference);
            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                            HTMLEditorPane.scrollToReference(reference);
                    }
            });     
            setCursor(ORDINARY_CURSOR);                        
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    

    public PartitureTableWithActions getPartitur() {
        return partitur;
    }

    public File getLastSearchResultPath() {
        return lastSearchResultPath;
    }

    public void setLastSearchResultPath(File lastSearchResultPath) {
        this.lastSearchResultPath = lastSearchResultPath;
    }
    
    /** returns the icon associated with this application */
    public java.awt.Image getIconImage(){
        return new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/exakt.png")).getImage();
    }
    

    public File getLastCorpusPath() {
        return lastCorpusPath;
    }

    public void setLastCorpusPath(File lastCorpusPath) {
        this.lastCorpusPath = lastCorpusPath;
    }

    boolean waitASecond = false;
    
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        if (e.getSource()==concordanceList.getSelectionModel()){
            Object o = concordanceList.getSelectedValue();
            java.awt.Component c = (java.awt.Component)o;
            if (tabbedPane.indexOfComponent(c)>=0){
                waitASecond = true;
                tabbedPane.setSelectedComponent(c);
                waitASecond = false;                
            }
            boolean somethingIsSelected = (concordanceList.getSelectedIndex()>-1);
            saveSearchResultAsAction.setEnabled(somethingIsSelected);
            saveSearchResultAction.setEnabled(somethingIsSelected);
            appendSearchResultAction.setEnabled(somethingIsSelected);
            closeSearchResultAction.setEnabled(somethingIsSelected);      
            
            concordanceMenu.enableMenuItems(somethingIsSelected);
            columnsMenu.enableMenuItems(somethingIsSelected);
        } else if (e.getSource()==corpusList.getSelectionModel()){
            boolean somethingIsSelected = (corpusList.getSelectedIndex()>-1);
            newSearchPanelAction.setEnabled(somethingIsSelected);
            openSearchResultAction.setEnabled(somethingIsSelected);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (waitASecond) return;
        if (e.getSource()==tabbedPane){
            concordanceList.setSelectedValue(getActiveSearchPanel(),true);
        }
    }

    @Override
    public String getVersion() {
        return org.exmaralda.common.EXMARaLDAConstants.EXAKT_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "EXAKT";
    }

    @Override
    public String getPreferencesNode() {
        return "org.sfb538.exmaralda.EXAKT";
    }
    
    @Override
    public ImageIcon getWelcomeScreen() {
        return new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/SplashScreen.png"));
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
    


    /* MAC OS X specific actions (added in version 0.6., 07-05-09 */
    public void about(){
        System.out.println("MAC OS X : about");
        new org.exmaralda.common.application.AboutAction("", this).actionPerformed(null);
    }

    public void handlePrefs(){
        System.out.println("MAC OS X : handlePrefs");
        editPreferencesAction.actionPerformed(null);
    }

    public void exit(){
        System.out.println("MAC OS X : quit");
        exitForm(null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount()==2){
            if (e.getSource()==corpusList){
                newSearchPanelAction.actionPerformed(null);
            } else if (e.getSource()==wordListList){
                if (wordListList.getSelectedValue()==null) return;
                AbstractTokenList tl = (AbstractTokenList)(wordListList.getSelectedValue());
                WordlistDialog dialog = new WordlistDialog(new javax.swing.JFrame(), false, tl);
                dialog.setLocationRelativeTo(this);
                dialog.setTitle(tl.getName());
                
                dialog.setVisible(true);
            }
        }
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void status(String message){
        statusLabel.setText(message);
    }


    
    
}
class MacSpecials implements com.apple.mrj.MRJQuitHandler, com.apple.mrj.MRJPrefsHandler, com.apple.mrj.MRJAboutHandler {

    EXAKT exakt;

    public MacSpecials(EXAKT theEditor) {
		exakt = theEditor;
		// Set up the Application Menu
		com.apple.mrj.MRJApplicationUtils.registerAboutHandler(this);
		com.apple.mrj.MRJApplicationUtils.registerPrefsHandler(this);
		com.apple.mrj.MRJApplicationUtils.registerQuitHandler(this);
	}

    public void handleAbout() {
		exakt.about();
	}

    public void handlePrefs() {
        exakt.handlePrefs();
	}

    public void handleQuit() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				exakt.exit();
			}
		});
		throw new IllegalStateException("Let the quit handler do it");
	}
}