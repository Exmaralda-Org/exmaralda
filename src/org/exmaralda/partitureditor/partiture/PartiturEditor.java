/*
 * PartiturEditor.java
 *
 * Created on 21. August 2001, 15:32
 *	Last change:  TS    6 Nov 2002   11:30 am
 *                    TS    16 June 2003    Restructured package. Put Actions into
 *                                          separate classes
 *                    TS    09 March 2004   Restructured again. Put toolbar and menu
 *                                       &   into separate classes
 */

package org.exmaralda.partitureditor.partiture;

import com.apple.eawt.ApplicationEvent;
import java.io.IOException;
import javax.swing.*;
import org.exmaralda.exakt.search.swing.KWICTableEvent;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import org.exmaralda.exakt.search.AnnotationSearchResult;
import org.exmaralda.partitureditor.partiture.menus.*;
import org.exmaralda.partitureditor.partiture.toolbars.*;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.common.helpers.Internationalizer;




// *************************************************************************************



//=========================================================
/**
 * The JFrame that makes up the main application window of the partitur editor
 * contains the menu bar, the toolbars and the PartiturTableWithActions, 
 * i.e. the actual partitur.
 * Implements PartiturTableListener so that it can react to a change of filename
 * (this will cause the frame title to update)
 * @author  Thomas
 * @version
 */

public class PartiturEditor extends javax.swing.JFrame 
                        implements  javax.swing.event.ChangeListener, 
                                    java.awt.event.WindowListener, 
                                    PartitureTableListener,
                                    org.exmaralda.common.ExmaraldaApplication,
                                    org.exmaralda.exakt.search.swing.KWICTableListener
                                    { 
    
                         
    /** the table component containing the partitur */
    private PartitureTableWithActions table;
    /** the user settings */
    /** changed in version 1.3.3. to use java 1.4 properties */
    //private java.util.Properties settings;
    private java.util.prefs.Preferences settings;
    
        
    /** the main menu bar */
    public PartiturMenuBar menuBar;
    
    /** layout container for the toolbar */
    public ToolBarPanel toolBarPanel;
    
    /** layout container for the main component (i.e. the partitur) */
    private JPanel mainPanel;
    /** layout container for the text field on top of the partitur */
    public JPanel largeTextFieldPanel;
    /** JSlider for changing the font size in the large text field */
    private JSlider changeLargeTextFieldFontSizeSlider;
    /** scroll pane for the text field on top of the partitur */
    private JScrollPane largeTextFieldScrollPane;

    /** the exit action (in the file menu) */
    private javax.swing.AbstractAction exitAction;
    /** the exit menu item (in the file menu) */
    private JMenuItem exitMenuItem;
    
    // added on 12-Feb-2004 because Annette does not like
    // staring into the hole at the right edge of the screen
    /** buffers on the sides of the JFrame */
    private JPanel rightBuffer;
    private JPanel leftBuffer;

    // added on 09-March-2007
    private StatusPanel statusPanel;
    // added on 11-October-2011
    public PartiturEditorInfoPanel infoPanel;

    PartiturTimelinePanel partiturTimelinePanel;
    org.exmaralda.folker.timeview.TimelineViewer timelineViewer = new org.exmaralda.folker.timeview.TimelineViewer();
    public PartiturEditorTimeviewPlayerControl controller;
  
    /** Creates new form PartiturEditor */
    public PartiturEditor() {
        
        new StartupSplashScreen(this);
        
        org.exmaralda.common.Logger.initialiseLogger(this);
                                
        // initialize the table
        table = new PartitureTableWithActions(this);
        table.keyboardDialog.addWindowListener(this);
        table.linkPanelDialog.addWindowListener(this);
        table.mediaPanelDialog.addWindowListener(this);
        table.praatPanel.addWindowListener(this);
        table.annotationDialog.addWindowListener(this);
        table.ipaPanel.addWindowListener(this);
             
        // init the other GUI components      
        initComponents();
                                           
        table.addPartitureTableListener(this);
        
        pack();
                
        System.out.println("Application initialized.");
                       
        loadSettings();
        menuBar.transcriptionMenu.segmentationLabel.setText(" Segmentation (" + table.preferredSegmentation + ")");
        menuBar.transcriptionMenu.insertHIATUtteranceNumbersMenuItem.setVisible(table.preferredSegmentation.equals("HIAT"));
        
        // init the exit action and add it to the file menu
        initExit();

        menuBar.viewMenu.useDifferentEmptyColorCheckBoxMenuItem.setSelected(table.diffBgCol);
        menuBar.add(new org.exmaralda.common.application.HelpMenu(Internationalizer.getString("Help"), this));

        
        table.getModel().resetTranscription();
        
      
        // if this is a MAC OS: init the MAC OS X specific actions
        String os = System.getProperty("os.name").substring(0,3);
        if (os.equalsIgnoreCase("mac")) {
            // changed 09-06-2009
            setupMacOSXApplicationListener();
        }

        // register shortcuts for media playback
        // registerKeyStrokes();

    }
    
    /** initialises the GUI components */
    private void initComponents() {
                      
        mainPanel = new javax.swing.JPanel();
        
        largeTextFieldPanel = new javax.swing.JPanel();
        changeLargeTextFieldFontSizeSlider = new JSlider();
        largeTextFieldScrollPane = new javax.swing.JScrollPane(table.largeTextField);
        
        menuBar = new PartiturMenuBar(table);
        toolBarPanel = new ToolBarPanel(table);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("EXMARaLDA Partitur-Editor " + getVersion() +  " [untitled.exb]");
        setIconImage(getIconImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(null);
            }
        });
                       
        largeTextFieldPanel.setLayout(new javax.swing.BoxLayout(largeTextFieldPanel, javax.swing.BoxLayout.X_AXIS));
        largeTextFieldPanel.setMaximumSize(new java.awt.Dimension(10000,60));
        largeTextFieldPanel.setPreferredSize(new java.awt.Dimension(10000,60));
        largeTextFieldPanel.setMinimumSize(new java.awt.Dimension(10,10));
        changeLargeTextFieldFontSizeSlider.setMinimum(6);
        changeLargeTextFieldFontSizeSlider.setMaximum(24);
        changeLargeTextFieldFontSizeSlider.setValue(10);
        changeLargeTextFieldFontSizeSlider.setOrientation(javax.swing.SwingConstants.VERTICAL);
        changeLargeTextFieldFontSizeSlider.setToolTipText("Change font size");
        changeLargeTextFieldFontSizeSlider.addChangeListener(
            new javax.swing.event.ChangeListener(){
            @Override
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    int value = changeLargeTextFieldFontSizeSlider.getValue();
                    table.largeTextField.setFont(table.largeTextField.getFont().deriveFont(value + 0.0f));
                }                    
            });
        largeTextFieldPanel.add(largeTextFieldScrollPane);
        largeTextFieldPanel.add(changeLargeTextFieldFontSizeSlider);
        largeTextFieldPanel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 10, 0));
        
        
        // initialize the progress bar
        table.progressBar.addChangeListener(this);
        table.progressBar.setAlignmentX(0.5F);
        table.progressBar.setAlignmentY(0.5F);

        mainPanel.setLayout(new java.awt.BorderLayout());
        partiturTimelinePanel = new PartiturTimelinePanel(largeTextFieldPanel, timelineViewer, table);
        partiturTimelinePanel.setTimeViewVisible(false);
        
        mainPanel.add(partiturTimelinePanel, java.awt.BorderLayout.CENTER);

        rightBuffer = new javax.swing.JPanel();
        rightBuffer.setPreferredSize(new java.awt.Dimension(20,1));
        leftBuffer = new javax.swing.JPanel();
        leftBuffer.setPreferredSize(new java.awt.Dimension(20,1));

        statusPanel = new StatusPanel();
        table.statusPanel = statusPanel;
        infoPanel = new PartiturEditorInfoPanel();
        table.infoPanel = infoPanel;
        
        getContentPane().add(toolBarPanel, java.awt.BorderLayout.NORTH);
        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(rightBuffer, java.awt.BorderLayout.EAST);
        getContentPane().add(leftBuffer, java.awt.BorderLayout.WEST);
        infoPanel.add(statusPanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(infoPanel, java.awt.BorderLayout.SOUTH);
        
        setJMenuBar(menuBar);

        statusPanel.setStatus("Partitur-Editor started");
    }
    
  
    /** Exit the Application */
    private boolean exitForm(java.awt.event.WindowEvent evt) {
        boolean proceed = table.exit();
        if (proceed){
            storeSettings();
            System.out.println("Application terminated. ");
            System.out.println(new java.util.Date().toString());
            System.out.println("________________________");
            System.exit(0);
        }
        return proceed;
    }
    
    /**
     * @param args the command line arguments
     * either no parameter is passed - then the application is started with an empty transcription
     * or the name of a basic-transcription XML-document is passed, then the application is started with this
     * transcription open
     */
    public static void main(final String args[]) {
        System.out.println("java.library.path=" + System.getProperty("java.library.path"));
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
        }           
        catch (Exception e) {
            e.printStackTrace();        
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PartiturEditor pe = new PartiturEditor();
                pe.setVisible(true);
                if (args.length>0){
                    try{
                        BasicTranscription bt = new BasicTranscription(args[0]);
                        pe.table.stratify(bt);
                        pe.table.getModel().setTranscription(bt);
                        pe.table.setFilename(args[0]);
                        pe.table.setupMedia();
                        pe.table.setupPraatPanel();
                        pe.table.homeDirectory = args[0];
                        // added 31-08-2012
                        pe.table.reconfigureAutoSaveThread();
                        
                        //pe.table.transcriptionChanged = true;
                    } catch (Exception e){
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(pe, e.getLocalizedMessage());
                    }
                }
            }
        });
    }
        
    
   /** initializes the exit action */
   private void initExit(){

       exitAction = new javax.swing.AbstractAction(Internationalizer.getString("Exit")){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                exitForm(null);
            }
        };
        String os = System.getProperty("os.name").substring(0,3);
        if (!(os.equalsIgnoreCase("mac"))) {
            menuBar.fileMenu.addSeparator();
            exitMenuItem = menuBar.fileMenu.add(this.exitAction);
            exitMenuItem.setToolTipText("Programm beenden");
        }
    }
    
   /** listener method for the EXAKT search dialog */ 
    @Override
   public void processEvent(KWICTableEvent ev) {
        if (ev.getType() == KWICTableEvent.DOUBLE_CLICK){
            try {
                table.commitEdit(true);
                org.exmaralda.exakt.search.SimpleSearchResult searchResult = (org.exmaralda.exakt.search.SimpleSearchResult) (ev.getSelectedSearchResult());
                BasicTranscription bt = table.getModel().getTranscription();
                org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription st = table.getModel().getTranscription().toSegmentedTranscription();
                String tierID = searchResult.getAdditionalData()[0];
                org.exmaralda.partitureditor.jexmaralda.Tier tier = bt.getBody().getTierWithID(tierID);
                String timeID = searchResult.getAdditionalData()[2];

                // changed 26-05-2009
                if (!(searchResult instanceof AnnotationSearchResult)){
                    // find the exact startpoint
                    int count = 0;
                    int s = searchResult.getOriginalMatchStart();
                    while ((tier.containsEventAtStartPoint(timeID)) && (count + tier.getEventAtStartPoint(timeID).getDescription().length() <= s)) {
                        count += tier.getEventAtStartPoint(timeID).getDescription().length();
                        timeID = tier.getEventAtStartPoint(timeID).getEnd();
                    }
                }

                String timeID2 = st.getBody().getCommonTimelineMatch(timeID);

                table.makeVisible(tierID, timeID2);
            } catch (JexmaraldaException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage());
                ex.printStackTrace();
            }
        }
   }

    /** forces immediate update of the progress bar */ 
    @Override
   public void stateChanged(final javax.swing.event.ChangeEvent p1) {
        java.awt.Rectangle barRectangle = table.progressBar.getBounds();
        barRectangle.x = 0;
        barRectangle.y = 0;
        table.progressBar.paintImmediately(barRectangle);
    }
    
    /** returns the EXMARaLDA icon associated with this application */
    @Override
    public java.awt.Image getIconImage(){
        return new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/partitureditor2.png")).getImage();
    }
    
    /** reacts to changes in the partitur table:
     *  a change of filename (FILENAME_CHANGED) or
     *  a change of media time (MEDIA_TIME_CHANGED) or
     *  an addition / removal to / from the undo history */
    @Override
    public void partitureTablePropertyChanged(PartitureTableEvent e) {
        if (e.getID()==PartitureTableEvent.FILENAME_CHANGED){
            this.setTitle("EXMARaLDA Partitur-Editor " + getVersion() + " [" + (String)e.getInfo()+ "]");
            table.recentFiles.insertElementAt((String)e.getInfo(), 0);
            //menuBar.fileMenu.setupOpenRecentMenu(table.recentFiles);            
        } else if (e.getID()==PartitureTableEvent.MEDIA_TIME_CHANGED){
            Double[] times = (Double[])(e.getInfo());
            this.processMediaTimeChanged(times[0].doubleValue(), times[1].doubleValue());
        } else if (e.getID()==PartitureTableEvent.UNDO_CHANGED){
            String text = (String)(e.getInfo());
            menuBar.editMenu.setUndoText(text);
        }
    }
    
    /** loads the user settings */
    private void loadSettings(){
        try {
            // change in version 1.3.3. (05-12-2006)
            
            settings = java.util.prefs.Preferences.userRoot().node(getPreferencesNode());

            String os = System.getProperty("os.name").substring(0,3);
            String defaultPlayer = "JMF-Player";
            if (os.equalsIgnoreCase("mac")){
                defaultPlayer = "ELAN-Quicktime-Player";
            } else if (os.equalsIgnoreCase("win")){
                defaultPlayer = "JDS-Player";
            }
            System.out.println("Default player: " + defaultPlayer);


            String playerType = settings.get("PlayerType", defaultPlayer);
            boolean playerTypeConfirmed = settings.getBoolean("PlayerTypeConfirmed", false);
            if ((!playerTypeConfirmed) && (!playerType.equals(defaultPlayer))){
                String message = "Your player preference is set to " + playerType + ".\n"
                        + "This is not the recommended default player for your system.\n"
                        + "The recommended default player for your system is " + defaultPlayer + ".\n"
                        + "Do you want to switch to the recommended default player?";
                int optionChosen = JOptionPane.showConfirmDialog(rootPane, message);
                if (optionChosen == JOptionPane.YES_OPTION){
                    settings.put("PlayerType", defaultPlayer);
                    JOptionPane.showMessageDialog(rootPane, "Please restart the Partitur-Editor \nfor the changes to take effect.");
                }
                settings.putBoolean("PlayerTypeConfirmed", true);
            }



            table.setSettings(getPreferencesNode());

            // menus
            menuBar.fileMenu.setupOpenRecentMenu(table.recentFiles);
            menuBar.viewMenu.showKeyboardCheckBoxMenuItem.setSelected(settings.get("SHOW-Keyboard","TRUE").equalsIgnoreCase("TRUE"));
            menuBar.viewMenu.showLinkPanelCheckBoxMenuItem.setSelected(settings.get("SHOW-LinkPanel","TRUE").equalsIgnoreCase("TRUE"));
            menuBar.viewMenu.showMediaPanelCheckBoxMenuItem.setSelected(settings.get("SHOW-MediaPanel","TRUE").equalsIgnoreCase("TRUE"));
            menuBar.viewMenu.showPraatPanelCheckBoxMenuItem.setSelected(settings.get("SHOW-PraatPanel","FALSE").equalsIgnoreCase("TRUE"));
            menuBar.viewMenu.showAnnotationPanelCheckBoxMenuItem.setSelected(settings.get("SHOW-AnnotationPanel","FALSE").equalsIgnoreCase("TRUE"));
            menuBar.viewMenu.showIPAPanelCheckBoxMenuItem.setSelected(settings.get("SHOW-IPAPanel","FALSE").equalsIgnoreCase("TRUE"));

            boolean showLTF = settings.get("SHOW-LargeTextField","TRUE").equalsIgnoreCase("TRUE");
            largeTextFieldPanel.setVisible(showLTF);
            menuBar.viewMenu.showLargeTextFieldCheckBoxMenuItem.setSelected(showLTF);

            menuBar.sfb538Menu.setVisible(settings.get("SHOW-SFB538Menu","FALSE").equalsIgnoreCase("TRUE"));
            menuBar.sinMenu.setVisible(settings.get("SHOW-SiNMenu","FALSE").equalsIgnoreCase("TRUE"));
            menuBar.odtstdMenu.setVisible(settings.get("SHOW-ODTSTDMenu","FALSE").equalsIgnoreCase("TRUE"));

            System.out.println("Settings read and set.");

            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            try {
                // changed to accomodate a presumed bug on OS X where the returned ScreenSize is wayyyyy tooooo laaaaaarge
                int x = Integer.parseInt(settings.get("XSize","600"));
                int y = Integer.parseInt(settings.get("YSize","400"));
                int xSize = Math.min(screenSize.width, x);
                int ySize = Math.min(screenSize.height, y);
                setSize(xSize,ySize);
                
                int x2 = Integer.parseInt(settings.get("XPosition","0"));
                int y2 = Integer.parseInt(settings.get("YPosition","0"));
                int xPosition = Math.min(screenSize.width-20,Math.max(0,x2));
                int yPosition = Math.min(screenSize.height-20,Math.max(0,y2));
                setLocation(xPosition,yPosition);
            } catch (NumberFormatException nfe){
                nfe.printStackTrace();
            }

            partiturTimelinePanel.loadSettings(getPreferencesNode());

            table.praatPanel.praatControl.configure(this);


        } catch (Exception e){
            System.out.println("Settings could not be read");
            e.printStackTrace();
        }
    }
    
    /** stores the user settings */
    private void storeSettings(){
        partiturTimelinePanel.storeSettings(getPreferencesNode());
        table.getSettings(getPreferencesNode());
        settings.put("XSize", Integer.toString(this.getWidth()));
        settings.put("YSize", Integer.toString(this.getHeight()));
        settings.put("XPosition", Integer.toString(this.getLocation().x));
        settings.put("YPosition", Integer.toString(this.getLocation().y));

        settings.put("SHOW-LargeTextField", Boolean.toString(largeTextFieldPanel.isShowing()));


        settings.put("SHOW-SFB538Menu", Boolean.toString(menuBar.sfb538Menu.isShowing()));
        settings.put("SHOW-SiNMenu", Boolean.toString(menuBar.sinMenu.isShowing()));
        settings.put("SHOW-ODTSTDMenu", Boolean.toString(menuBar.odtstdMenu.isShowing()));

        System.out.println("Settings stored.");        
    }
    
    
    /** handles window closing events
     *  source can be either this JFrame
     * or one of the panels (i.e. keyboard, link, segmentation or audio panel)
     * if the latter, takes care of updating the CheckBoxMenuItems in the view menu accordingly */
    @Override
    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
        Object source = windowEvent.getSource();
        if (source==table.keyboardDialog){
            menuBar.viewMenu.showKeyboardCheckBoxMenuItem.setSelected(false);
        } else if (source==table.linkPanelDialog){
            menuBar.viewMenu.showLinkPanelCheckBoxMenuItem.setSelected(false);
        } else if (source==table.mediaPanelDialog){
            menuBar.viewMenu.showMediaPanelCheckBoxMenuItem.setSelected(false);        
        } /*else if (source==table.segmentationPanel){
            //menuBar.viewMenu.showSegmentationPanelCheckBoxMenuItem.setSelected(false);
        } */else if (source==table.praatPanel){
            menuBar.viewMenu.showPraatPanelCheckBoxMenuItem.setSelected(false);
        } else if (source==table.annotationDialog){
            menuBar.viewMenu.showAnnotationPanelCheckBoxMenuItem.setSelected(false);
        } else if (source==table.ipaPanel){
            menuBar.viewMenu.showIPAPanelCheckBoxMenuItem.setSelected(false);
        } else { // i.e. source is this
        }
    }
    
    /** does nothing, required by the WindowListener interface */
    @Override
    public void windowOpened(java.awt.event.WindowEvent windowEvent) {
    }
    
    /** does nothing, required by the WindowListener interface */
    @Override
    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
    }
    
    /** does nothing, required by the WindowListener interface */
    @Override
    public void windowActivated(java.awt.event.WindowEvent windowEvent) {
    }
    
    /** does nothing, required by the WindowListener interface */
    @Override
    public void windowDeiconified(java.awt.event.WindowEvent windowEvent) {
    }
    
    /** does nothing, required by the WindowListener interface */
    @Override
    public void windowDeactivated(java.awt.event.WindowEvent windowEvent) {
    }
    
    /** does nothing, required by the WindowListener interface */
    @Override
    public void windowIconified(java.awt.event.WindowEvent windowEvent) {
    }
    
    //*******************************************************************
    //*************** METHODS FROM ExmaraldaApplication ******************
    //*******************************************************************
    
    /** returns the version string */
    @Override
    public String getVersion(){
        return org.exmaralda.common.EXMARaLDAConstants.PARTITUREDITOR_VERSION;
    }
    
    /** returns the application name */
    @Override
    public String getApplicationName(){
        return "Partitur-Editor";
    }
    
    @Override
    public String getPreferencesNode() {
        return "org.sfb538.exmaralda.PartiturEditor";
    }   
    
    @Override
    public ImageIcon getWelcomeScreen() {
        return new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/SplashScreen.png"));
    }
    
    @Override
    public void resetSettings(){
        System.out.println("Resetting settings: " + getPreferencesNode());
        try {
            java.util.prefs.Preferences.userRoot().node(getPreferencesNode()).clear();                
            JOptionPane.showMessageDialog(table, "Preferences reset.\nRestart the editor.");
            loadSettings();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, "Problem resetting preferences:\n" + ex.getLocalizedMessage());
        }        
    }
    
    
    //*******************************************************************
    //*******************************************************************
    
    
    

    // ----- Acess methods for DIDA Extensions --
    /** returns the partitur */
    public PartitureTableWithActions getPartitur(){
        return table;
    }
    
    /** called whenever the media time has changed through
     *  a user selection (this can be overridden by the DIDA extension 
     *  to communicate with XWaves or PRAAT) */
    public void processMediaTimeChanged(double startTime, double endTime){
    }
    
    /* MAC OS X specific actions (added in version 1.2.7., 08-01-04 */
    public void about(){
        System.out.println("MAC OS X : about");
        new org.exmaralda.common.application.AboutAction("", this).actionPerformed(null);
        //table.about();
    }
    
    public void handlePrefs(){
        System.out.println("MAC OS X : handlePrefs");
        table.editPreferencesAction.actionPerformed(null);
    }
    
    public boolean exit(){
        System.out.println("MAC OS X : quit");
        return exitForm(null);
    }
    
    void registerKeyStrokes(){
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getRootPane().getActionMap();
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),"F1_Pressed");
        am.put("F1_Pressed", table.mediaPanelDialog.playAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),"F2_Pressed");
        am.put("F2_Pressed", table.mediaPanelDialog.pauseAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),"F3_Pressed");
        am.put("F3_Pressed", table.mediaPanelDialog.stopAction);

        Object[][] generalAssignments = {
            {"control SPACE", "playSelection", controller.playSelectionAction},
            {"control shift SPACE", "playLastSecondOfSelection", controller.playLastSecondOfSelectionAction},
            {"control F4", "play", controller.playAction},
            {"control F5", "pause", controller.pauseAction},
            {"control F6", "stop", controller.stopAction},

            {"alt shift LEFT", "decreaseSelectionStart", controller.decreaseSelectionStartAction},
            {"alt shift RIGHT", "increaseSelectionStart", controller.increaseSelectionStartAction},
            {"alt LEFT", "decreaseSelectionEnd", controller.decreaseSelectionEndAction},
            {"alt RIGHT", "increaseSelectionEnd", controller.increaseSelectionEndAction},

            {"control shift S", "shiftSelection", controller.shiftSelectionAction},
        };
        controller.processAssignments(generalAssignments, partiturTimelinePanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        controller.processAssignments(generalAssignments, partiturTimelinePanel, JComponent.WHEN_IN_FOCUSED_WINDOW);

    }

    public void setupMedia() {
        BasicTranscription bt = table.getModel().getTranscription();
        String rf = bt.getHead().getMetaInformation().getReferencedFile();
        if (rf.length()>1){
            String wf = bt.getHead().getMetaInformation().getReferencedFile("wav");
            try {
                if (wf!=null){
                    try{
                        timelineViewer = new org.exmaralda.folker.timeview.WaveFormViewer();
                        timelineViewer.setPixelsPerSecond(150.0);
                        table.getModel().setPixelsPerSecond(150.0);
                        timelineViewer.setSoundFile(wf);
                    } catch (IOException ioe){
                        // added 09-06-2009: fallback onto first media file
                        // if the WAV file cannot be opened
                        System.out.println("One goes wrong, other one may go right");
                        ioe.printStackTrace();
                        timelineViewer = new org.exmaralda.folker.timeview.TimelineViewer();
                        timelineViewer.setPixelsPerSecond(150.0);
                        table.getModel().setPixelsPerSecond(150.0);
                        timelineViewer.setSoundFile(rf);
                    }
                } else {
                    timelineViewer = new org.exmaralda.folker.timeview.TimelineViewer();
                    timelineViewer.setPixelsPerSecond(150.0);
                    table.getModel().setPixelsPerSecond(150.0);
                    timelineViewer.setSoundFile(rf);
                }
                partiturTimelinePanel.setTimelineViewer(timelineViewer);
                partiturTimelinePanel.setTimeViewVisible(true);
                menuBar.viewMenu.setProportionButtonsVisible(true);
                timelineViewer.addTimeSelectionListener(partiturTimelinePanel);
                timelineViewer.resetDragBoundaries();
                controller = new PartiturEditorTimeviewPlayerControl(this, timelineViewer, getPartitur(), getPartitur().player);
                controller.zoomToggleButton = this.partiturTimelinePanel.zoomToggleButton;
                registerKeyStrokes();
                partiturTimelinePanel.assignActions(controller);
                partiturTimelinePanel.assignKeyStrokes(controller);
            } catch (IOException ex) {
                ex.printStackTrace();
                String message = "There was a problem opening\n" + rf +"\n\n" + "Error message:\n";
                String errmess = ex.getLocalizedMessage();
                if (errmess.length()>50){
                    int index = errmess.length()/2;
                    errmess = errmess.substring(0,index)+"\n" + errmess.substring(index);
                }
                message+=errmess + "\n\n";
                message+="The media file may not be at the specified location\n";
                message+="or it may be in some format not supported by the current configuration.\n";
                message+="Try one of the following:\n";
                message+="1) Edit the recordings associated with this transcription\n";
                message+="2) Use a different media player (Edit > Preferences > Media)\n\n";
                String[] options = {"Edit recordings", "Ignore"};
                int optionChosen = JOptionPane.showOptionDialog(this, message, "Waveform viewer: Problem opening Media",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/22x22/mimetypes/video-x-generic.png")), 
                        options, "Edit recordings");
                if (optionChosen==JOptionPane.YES_OPTION){
                    table.editRecordingsAction.actionPerformed(null);
                } else {
                    timelineViewer = new org.exmaralda.folker.timeview.TimelineViewer();
                    partiturTimelinePanel.setTimelineViewer(timelineViewer);
                    partiturTimelinePanel.setTimeViewVisible(false);
                    menuBar.viewMenu.setProportionButtonsVisible(false);
                }
            }
        } else {
            timelineViewer = new org.exmaralda.folker.timeview.TimelineViewer();
            partiturTimelinePanel.setTimelineViewer(timelineViewer);
            partiturTimelinePanel.setTimeViewVisible(false);
            menuBar.viewMenu.setProportionButtonsVisible(false);
        }
    }


    /** added 09-06-2009: replaces older mac app handler in inner class */
    private void setupMacOSXApplicationListener() {
        final com.apple.eawt.Application application = com.apple.eawt.Application.getApplication();
        application.setEnabledAboutMenu(true); // damit ein "Ueber " Menue erscheint
        application.addPreferencesMenuItem(); // "Einstellen..." Dialog
        application.setEnabledPreferencesMenu(true); // diesen Dialog auch
        application.addApplicationListener(new com.apple.eawt.ApplicationListener() {

            @Override
            public void handleAbout(com.apple.eawt.ApplicationEvent ae) {
                about();
                ae.setHandled(true); // habe fertig...
            }

                // app wird ueber den finder geoeffnet. wie auch sonst.
                // (lies: total unn�tz!)
            @Override
            public void handleOpenApplication(ApplicationEvent ae) {
            }

            @Override
            public void handlePreferences(ApplicationEvent ae) {
                handlePrefs();
                ae.setHandled(true);
            }

            @Override
            public void handlePrintFile(ApplicationEvent ae) {
                System.out.println("Drucken?!");
                table.printAction.actionPerformed(null);
                ae.setHandled(true);
            }

            @Override
            public void handleQuit(ApplicationEvent ae) {
                boolean reallyQuit = exit();
                ae.setHandled(reallyQuit); // da wird wohl nichts mehr draus!
            }

            // coma laeuft bereits und jemand startet es nochmal
            @Override
            public void handleReOpenApplication(ApplicationEvent ae) {
                System.out.println("Laeuft schon");
                // will ich mehrere instanzen? nein
                //JOptionPane.showMessageDialog(new JFrame(), "You already have an instance\nof the EXMARaLDA Partitur-Editor running.");
                ae.setHandled(true);
            }

            // anwendung laeuft schon, dokument wird über den finder geöffnet
            @Override
            public void handleOpenFile(ApplicationEvent ae) {
                try{
                    boolean proceed = true;
                    if (table.transcriptionChanged){
                        proceed = table.checkSave();
                    }
                    if (!proceed) return;
                    BasicTranscription bt = new BasicTranscription(ae.getFilename());
                    table.getModel().setTranscription(bt);
                    table.setFilename(ae.getFilename());
                    table.setupMedia();
                    table.homeDirectory = ae.getFilename();
                    //pe.table.transcriptionChanged = true;
                } catch (Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(table.getTopLevelAncestor(), e.getLocalizedMessage());
                }
            }
          });
	}

    public void setTimeViewBuffer(boolean proportional) {
        if (proportional){
            partiturTimelinePanel.setBuffer(this.table.getColumnPixelWidth(-1));
        } else {
            partiturTimelinePanel.setBuffer(0);
        }
    }
    


}

class MacSpecials implements com.apple.mrj.MRJQuitHandler, com.apple.mrj.MRJPrefsHandler, com.apple.mrj.MRJAboutHandler {

    PartiturEditor pe;

    public MacSpecials(PartiturEditor theEditor) {
		pe = theEditor;
		// Set up the Application Menu
		com.apple.mrj.MRJApplicationUtils.registerAboutHandler(this);
		com.apple.mrj.MRJApplicationUtils.registerPrefsHandler(this);
		com.apple.mrj.MRJApplicationUtils.registerQuitHandler(this);
	}
	
    @Override
    public void handleAbout() {
		pe.about();
	}
	
    @Override
    public void handlePrefs() {
        pe.handlePrefs();                
	}
	
    @Override
    public void handleQuit() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                     pe.exit();
                }
            });
            throw new IllegalStateException("Let the quit handler do it");
	}
}