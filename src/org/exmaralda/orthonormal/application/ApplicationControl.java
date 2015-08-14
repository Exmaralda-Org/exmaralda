/*
 * ApplicationControl.java
 *
 * Created on 9. Mai 2008, 14:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.data.*;
import org.exmaralda.folker.listview.*;
import org.exmaralda.folker.utilities.Constants;
import org.exmaralda.orthonormal.lexicon.LexiconException;
import org.exmaralda.partitureditor.sound.*;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.PatternSyntaxException;
import javax.swing.table.TableColumn;

import org.bounce.text.xml.XMLEditorKit;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.actions.fileactions.OpenRecentAction;

import org.exmaralda.folker.actions.fileactions.OutputAction;
import org.exmaralda.orthonormal.gui.WordLabel;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.exmaralda.orthonormal.gui.EditContributionDialog;
import org.exmaralda.orthonormal.gui.EditPreferencesDialog;
import org.exmaralda.orthonormal.gui.SaveLexiconDialog;
import org.exmaralda.orthonormal.gui.TaggingDialog;
import org.exmaralda.orthonormal.gui.WordListTableCellRenderer;
import org.exmaralda.orthonormal.gui.WordListTableModel;
import org.exmaralda.orthonormal.gui.WordListTableRowSorter;
import org.exmaralda.orthonormal.gui.WordNormalizationDialog;
import org.exmaralda.orthonormal.lexicon.AutoNormalizer;
import org.exmaralda.orthonormal.lexicon.DerewoWordlist;
import org.exmaralda.orthonormal.lexicon.LexiconInterface;
import org.exmaralda.orthonormal.lexicon.RDBLexicon;
import org.exmaralda.orthonormal.lexicon.SimpleXMLFileLexicon;
import org.exmaralda.orthonormal.lexicon.XMLLexicon;
import org.exmaralda.orthonormal.matchlist.MatchListDialog2;
import org.exmaralda.orthonormal.matchlist.MatchListListener;
import org.exmaralda.orthonormal.utilities.PreferencesUtilities;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;


/**
 *
 * @author thomas
 */
public final class ApplicationControl implements  ListSelectionListener,
                                            TableModelListener,
                                            PlayableListener,
                                            ChangeListener,
                                            ComponentListener,
                                            AdjustmentListener,
                                            ContributionTextPaneListener,
                                            WindowListener,
                                            MouseListener,
                                            DocumentListener,
                                            MatchListListener
                                            /*CellEditorListener*/ {
    
        
    boolean IS_SAVING_IN_BACKGROUND = false;   
    ApplicationFrame applicationFrame;   

    public Playable player;

    NormalizedFolkerTranscription nft;

    NormalizedContributionListTableModel contributionListTableModel;
    NormalizedContributionListTable contributionListTable;

    public WordListTableModel wordListTableModel;
    public WordListTableRowSorter wordListTableRowSorter;
    
    public org.exmaralda.orthonormal.actions.playeractions.PlaySelectionAction playSelectionAction;
    public org.exmaralda.orthonormal.actions.playeractions.PlayAction playAction;
    public org.exmaralda.orthonormal.actions.playeractions.StopAction stopAction;
    // ---------------------------
    org.exmaralda.orthonormal.actions.fileactions.OpenAction openAction;
    org.exmaralda.orthonormal.actions.fileactions.SaveAction saveAction;
    org.exmaralda.orthonormal.actions.fileactions.SaveAsAction saveAsAction;
    org.exmaralda.orthonormal.actions.fileactions.ExportAction exportAction;
    org.exmaralda.orthonormal.actions.fileactions.OutputAction outputAction;
    org.exmaralda.orthonormal.actions.fileactions.ImportCMCAction importCMCAction;    
    org.exmaralda.orthonormal.actions.fileactions.ExportCMCAction exportCMCAction;    
    org.exmaralda.orthonormal.actions.fileactions.EditRecordingAction editRecordingAction;
    org.exmaralda.orthonormal.actions.fileactions.ExitAction exitAction;
    // ---------------------------
    org.exmaralda.orthonormal.actions.editActions.EditPreferencesAction editPreferencesAction;
    //org.exmaralda.orthonormal.actions.editActions.UpdateRDBLexiconAction updateRDBLexiconAction;
    org.exmaralda.orthonormal.actions.editActions.SaveLexiconAction saveLexiconAction;
    
    public String currentFilePath = null;
    public String currentMediaPath = null;
    Vector<File> recentFiles = new Vector<File>();

    public boolean DOCUMENT_CHANGED = false;

    public LexiconInterface lexicon;
    public AutoNormalizer autoNormalizer;

    String LEXICON_PATH = "T:\\TP-Z2\\GAT\\orthonormal\\test-lexicon.xml";
    
    public static int NORMALIZATION_MODE = 0;
    public static int TAGGING_MODE = 1;
    public static int XML_MODE = 2;
    public static int CORRECTION_MODE = 2;
    
    int mode = NORMALIZATION_MODE;
    boolean autoAdvance = false;
    public double maximumTagProbability = 0.98;
    public double criticalTagProbability = 0.9;
    
    public MatchListDialog2 matchListDialog;
    
    /** Creates a new instance of ApplicationControl */
    public ApplicationControl(ApplicationFrame af)  {
        applicationFrame = af;
        applicationFrame.addComponentListener(this);
        applicationFrame.addWindowListener(this);
        
        contributionListTable = new NormalizedContributionListTable();
        contributionListTable.getSelectionModel().addListSelectionListener(this);

        applicationFrame.wordTable.addMouseListener(this);
        applicationFrame.playerSlider.addChangeListener(this);
        applicationFrame.filterTextField.getDocument().addDocumentListener(this);
                
        
        matchListDialog = new MatchListDialog2(af, false);
        matchListDialog.setApplicationControl(this);
        matchListDialog.setLocationRelativeTo(af);
        //matchListDialog.setVisible(true);
        
        
        initPlayer();      
        player.addPlayableListener(this);

        initActions();
        setGeneralDocumentActionsEnabled(false);
        setupLexicon();
    }



    public void setupLexicon() {
        System.out.println("Setting up lexicon");
        String type = PreferencesUtilities.getProperty("lexicon-type", "xml");
        
        
        boolean success = false;
        String errorMessage = "";
        
        while (!success){        
            if ("xml".equals(type)){
                // built-in XML lexicon
                lexicon = new XMLLexicon();
                try {
                    lexicon.read(null);
                    success=true;
                    status("Internal lexicon read successfully.");
                } catch (IOException ex) {
                    ex.printStackTrace();;
                    errorMessage = "Interne XML-Lexikondatei konnte nicht gelesen werden:\n" + ex.getLocalizedMessage();
                }            
            } else if ("xml-local".equals(type)){
                // local lexicon on the file system
                lexicon = new XMLLexicon();
                LEXICON_PATH = PreferencesUtilities.getProperty("lexicon-path", "");
                if (LEXICON_PATH.length()!=0){
                    File lexiconFile = new File(LEXICON_PATH);
                    if (!(lexiconFile.exists())){
                        try {
                            lexicon.write(lexiconFile);
                            //lexiconFile.createNewFile();
                            success=true;
                            status("Local lexicon created as " + lexiconFile.getAbsolutePath() + ".");                            
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            errorMessage = "XML-Lexikondatei " + lexiconFile.getAbsolutePath() + "\nkonnte nicht angelegt werden.";
                        }
                   } else {
                        try {
                            lexicon.read(lexiconFile);
                            success = true;
                            status("Local lexicon " + lexiconFile.getAbsolutePath() + " read.");                                                        
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            errorMessage = "XML-Lexikondatei " + lexiconFile.getAbsolutePath() + "\nkonnte nicht gelesen werden.";
                        }
                   }
                } else {
                    errorMessage = "Kein Pfad für XML-Lexikondatei angegeben.";
                }            
            }

            if (!(success)){
                status(errorMessage);
                JOptionPane.showMessageDialog(applicationFrame, errorMessage + "\nBitte ändern Sie die Einstellungen für das Lexikon.");
                EditPreferencesDialog dialog = new EditPreferencesDialog(applicationFrame, true);
                dialog.setLocationRelativeTo(applicationFrame);
                dialog.setVisible(true);
                type = PreferencesUtilities.getProperty("lexicon-type", "xml");
            }
        }
        
        /*boolean success = false;
        String errorMessage = "";
        while (!success){

            if (type.equals("xml")){
                lexicon = new SimpleXMLFileLexicon();

                } else {
                    errorMessage = "Kein Pfad für XML-Lexikondatei angegeben.";
                }
            } else if (type.equals("db")){
                try {
                    String RDB_URL = PreferencesUtilities.getProperty("rdb-url", "");
                    String RDB_USERNAME = PreferencesUtilities.getProperty("rdb-username", "");
                    String RDB_PASSWORD = PreferencesUtilities.getProperty("rdb-password", "");
                    String[] CONNECTION_PARAMETERS = {RDB_URL, RDB_USERNAME, RDB_PASSWORD};
                    lexicon = new RDBLexicon();
                    // jdbc:mysql://localhost:3306/orthonormal
                    lexicon.read(CONNECTION_PARAMETERS);
                    success = true;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    errorMessage = "Keine Verbindung zur Datenbank möglich.";
                }
            }
            if (!(success)){
                JOptionPane.showMessageDialog(applicationFrame, errorMessage + "\nBitte ändern Sie die Einstellungen für das Lexikon.");
                EditPreferencesDialog dialog = new EditPreferencesDialog(applicationFrame, true);
                dialog.setLocationRelativeTo(applicationFrame);
                dialog.setVisible(true);
            }
        
        * }*/
        autoNormalizer = new AutoNormalizer(lexicon);
        //applicationFrame.autoButton.setEnabled(lexicon instanceof SimpleXMLFileLexicon);
        System.out.println("Lexicon setup successfully.");

    }

    public int getMode(){
        return mode;
    }
    
    public void writeLexicon(){
        if (lexicon instanceof SimpleXMLFileLexicon){
            try {
                lexicon.write(new File(LEXICON_PATH));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<String> queryLexicon(String lemma){
        try {
            return lexicon.getCandidateForms(lemma);
        } catch (LexiconException ex) {
            ex.printStackTrace();
            displayException(ex);
        }
        return new Vector<String>();
    }

    public List<String> queryLexicon(Element[] wordElements){
        HashSet<String> forms = new HashSet<String>();
        for (Element word : wordElements){
            forms.add(word.getText());
        }
        Vector<String> result = new Vector<String>();
        for (String form : forms){
            try {
                result.addAll(lexicon.getCandidateForms(form));
            } catch (LexiconException ex) {
                ex.printStackTrace();
                displayException(ex);
            }
        }
        return result;
    }


    
    public JFrame getFrame(){
        return applicationFrame;
    }


    void initActions(){

        org.exmaralda.folker.utilities.Constants c = new org.exmaralda.folker.utilities.Constants();

        playSelectionAction = new org.exmaralda.orthonormal.actions.playeractions.PlaySelectionAction(this, "[*]", c.getIcon(Constants.PLAY_SELECTION_ICON));
        playAction = new org.exmaralda.orthonormal.actions.playeractions.PlayAction(this, "" , c.getIcon(Constants.PLAY_ICON));
        stopAction = new org.exmaralda.orthonormal.actions.playeractions.StopAction(this, "", c.getIcon(Constants.STOP_ICON));

        openAction = new org.exmaralda.orthonormal.actions.fileactions.OpenAction(this, "Öffnen...", c.getIcon(Constants.OPEN_ICON));
        saveAction = new org.exmaralda.orthonormal.actions.fileactions.SaveAction(this, "Speichern", c.getIcon(Constants.SAVE_ICON));
        saveAsAction = new org.exmaralda.orthonormal.actions.fileactions.SaveAsAction(this, "Speichern unter...", c.getIcon(Constants.SAVE_AS_ICON));
        exportAction = new org.exmaralda.orthonormal.actions.fileactions.ExportAction(this, "Export...", c.getIcon(Constants.EXPORT_ICON));        
        outputAction = new org.exmaralda.orthonormal.actions.fileactions.OutputAction(this, "Ausgabe...", c.getIcon(Constants.OUTPUT_ICON));
        importCMCAction = new org.exmaralda.orthonormal.actions.fileactions.ImportCMCAction(this, "CMC-Datei importieren...", c.getIcon(Constants.IMPORT_ICON));
        exportCMCAction = new org.exmaralda.orthonormal.actions.fileactions.ExportCMCAction(this, "CMC-Datei exportieren...", c.getIcon(Constants.EXPORT_ICON));
        editRecordingAction = new org.exmaralda.orthonormal.actions.fileactions.EditRecordingAction(this, "Aufnahme...", c.getIcon(Constants.EDIT_RECORDING_ICON));
        exitAction = new org.exmaralda.orthonormal.actions.fileactions.ExitAction(this, "Beenden", null);

        editPreferencesAction = new org.exmaralda.orthonormal.actions.editActions.EditPreferencesAction(this, "Voreinstellungen...", c.getIcon(Constants.EDIT_PREFERENCES_ICON));
        //updateRDBLexiconAction = new org.exmaralda.orthonormal.actions.editActions.UpdateRDBLexiconAction(this, "Datenbank-Lexikon aktualisieren...", null);
        saveLexiconAction = new org.exmaralda.orthonormal.actions.editActions.SaveLexiconAction(this, "Lexikon speichern...", null);

    }
    
    void assignActions(){
        JMenuItem jmi2 = applicationFrame.fileMenu.add(openAction);
        jmi2.setAccelerator(KeyStroke.getKeyStroke("control O"));
        JMenuItem jmi3 = applicationFrame.fileMenu.add(saveAction);
        jmi3.setAccelerator(KeyStroke.getKeyStroke("control S"));
        applicationFrame.fileMenu.add(saveAsAction);
        applicationFrame.fileMenu.addSeparator();
        applicationFrame.fileMenu.add(exportAction);
        applicationFrame.fileMenu.add(outputAction);
        
        applicationFrame.fileMenu.addSeparator();
        JMenu cmcDataMenu = new JMenu("CMC-Daten");
        cmcDataMenu.add(importCMCAction);
        cmcDataMenu.add(exportCMCAction);
        applicationFrame.fileMenu.add(cmcDataMenu);
        
        
        applicationFrame.fileMenu.addSeparator();
        applicationFrame.fileMenu.add(editRecordingAction);
        applicationFrame.fileMenu.addSeparator();

        applicationFrame.editMenu.add(editPreferencesAction);
        applicationFrame.editMenu.addSeparator();
        //applicationFrame.editMenu.add(updateRDBLexiconAction);
        applicationFrame.editMenu.add(saveLexiconAction);

        playSelectionAction.setEnabled(false);
        applicationFrame.playSelectionButton.setAction(playSelectionAction);
        applicationFrame.playSelectionButton.setToolTipText("Auswahl abspielen");

        playAction.setEnabled(false);
        applicationFrame.playButton.setAction(playAction);
        applicationFrame.playButton.setToolTipText("Abspielen");

        stopAction.setEnabled(false);
        applicationFrame.stopButton.setAction(stopAction);
        applicationFrame.stopButton.setToolTipText("Stop");

        applicationFrame.applicationToolBar.add(openAction).setToolTipText("Öffnen...");
        applicationFrame.applicationToolBar.add(saveAction).setToolTipText("Speichern");
        applicationFrame.applicationToolBar.add(saveAsAction).setToolTipText("Speichern unter...");
        applicationFrame.applicationToolBar.add(outputAction).setToolTipText("Ausgabe...");
        applicationFrame.applicationToolBar.add(new JToolBar.Separator());
        applicationFrame.applicationToolBar.add(editPreferencesAction).setToolTipText("Voreinstellungen bearbeiten...");

    }


 /***************** PLAYING MEDIA ******************************/

    public static int PLAYER_NO_SOUND = -1;
    public static int PLAYER_IDLE = 0;
    public static int PLAYER_PLAYING = 1;
    public static int PLAYER_HALTED = 2;

    public int playerState = PLAYER_IDLE;

    public double selectionStart = -1;
    public double selectionEnd = -1;

    public void playSelection(){
        if (playerState==PLAYER_PLAYING) return;
        player.setStartTime(selectionStart/1000.0);
        player.setEndTime(selectionEnd/1000.0);
        player.startPlayback();
        playerState=PLAYER_PLAYING;
    }

    public void play(){
        if (playerState==PLAYER_PLAYING) return;
        int pos = applicationFrame.playerSlider.getValue() / 1000;
        player.setStartTime(pos);
        player.setEndTime(player.getTotalLength());
        player.startPlayback();
        playerState=PLAYER_PLAYING;
    }

    /** stops the current playback */
    public void stop(){
        player.stopPlayback();
        playerState=PLAYER_IDLE;
         
    }

    void assignKeyboardShortcuts(){
        // changed 19-05-2009
        boolean useControl = PreferencesUtilities.getBooleanProperty("use-control", false);
        String modifier = "";
        if (useControl) modifier = "control ";

        Object[][] generalAssignments = {
            {modifier + "F3", "playSelection", playSelectionAction},
            {modifier + "F4", "play", playAction},
            {modifier + "F6", "stop", stopAction},

        };
        processAssignments(generalAssignments, applicationFrame.mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        processAssignments(generalAssignments, applicationFrame.mainPanel, JComponent.WHEN_IN_FOCUSED_WINDOW);

    }

    public void processAssignments(Object[][] assignments, JComponent c, int condition) {
        for (Object[] assignment : assignments) {
            String keyName = (String) (assignment[0]);
            String actionName = (String) (assignment[1]);
            //System.out.println(keyName + " " + actionName);
            AbstractAction action = (AbstractAction) (assignment[2]);
            c.getInputMap(condition).put(KeyStroke.getKeyStroke(keyName), actionName);
            c.getActionMap().put(actionName, action);
        }
    }


    
    public void setGeneralDocumentActionsEnabled(boolean enabled){
        saveAction.setEnabled(enabled);
        saveAsAction.setEnabled(enabled);
        outputAction.setEnabled(enabled);
        exportAction.setEnabled(enabled);
        editRecordingAction.setEnabled(enabled);
        contributionListTable.setEnabled(enabled);
    }
    
    public String getDefaultPlayer(){
        // changed 13-04-2015
        String defaultPlayer = "BAS-Audio-Player";
        /*String defaultPlayer = "JMF-Player";
        String os = System.getProperty("os.name").substring(0,3);
        if (os.equalsIgnoreCase("mac")){
            defaultPlayer="ELAN-Quicktime-Player";
        } else if (os.equalsIgnoreCase("win")){
            defaultPlayer="DirectShow-Player";
        }*/
        return defaultPlayer;
    }


    public void initPlayer(){
        
        // set the default player according to os
        String defaultPlayer = getDefaultPlayer();
        player = new BASAudioPlayer();
        
        // read preferred player from preferences
        //String playerType = java.util.prefs.Preferences.userRoot().node(applicationFrame.getPreferencesNode()).get("PlayerType", defaultPlayer);
        //System.out.println("Player: " + playerType);
        // make sure that there is no contradiction between preferred player and os
        //String os = System.getProperty("os.name").substring(0,3);
        /*if (playerType.equals("DirectShow-Player") && os.equalsIgnoreCase("mac")){
            playerType = "ELAN-Quicktime-Player";
        }
        if (playerType.equals("ELAN-Quicktime-Player") && os.equalsIgnoreCase("win")){
            playerType = "DirectShow-Player";
        }

        if (playerType.equals("JMF-Player")) {
            player = new JMFPlayer();
        } else if (playerType.equals("DirectShow-Player")) {
            player = new ELANDSPlayer();
        } else if (playerType.equals("ELAN-Quicktime-Player")) {
            player = new ELANQTPlayer();
        } else if (playerType.equals("Quicktime-Player")) {
            try {
                player = new QuicktimePlayer();
            } catch (Throwable ex) {
                ex.printStackTrace();
                player = new JMFPlayer();
            }
        } else if (playerType.equals("Praat-Player")) {
            try {
                player = new PraatPlayer(new File("c:\\programme\\praat\\praatcon.exe"));
            } catch (IOException ex) {
                ex.printStackTrace();
                player = new JMFPlayer();
            }
        } else {
            player = new JMFPlayer();            
        }*/
        //java.util.prefs.Preferences.userRoot().node(applicationFrame.getPreferencesNode()).put("PlayerType", playerType);
    }

 
    /***************** GETTING TRANSCRIPTION AND MEDIA ******************************/ 
 
        
    
    public void openTranscriptionFile(File f){

        String mediaPath;
        String newFilename = f.getAbsolutePath();
        try {
            if (f.getName().toLowerCase().endsWith("fln")){
                    nft = org.exmaralda.orthonormal.io.XMLReaderWriter.readNormalizedFolkerTranscription(f, true);
            } else {
                    nft = org.exmaralda.orthonormal.io.XMLReaderWriter.readFolkerTranscription(f);
            }
        } catch (JDOMException ex) {
            ex.printStackTrace();    
            displayException(ex);
            return;
        } catch (IOException ex) {
            ex.printStackTrace();    
            displayException(ex);
            return;
        } catch (IllegalArgumentException ex){
            ex.printStackTrace();    
            displayException(ex);
            return;            
        }

        mediaPath = nft.getMediaPath();            

        boolean mediaSet = false;
        while (!mediaSet){
            try {
                setMedia(mediaPath);
                mediaSet = true;
            } catch (IOException ex) {
                ex.printStackTrace();
                mediaPath = displayRecordingNotFoundDialog(mediaPath, ex);
                nft.setMediaPath(mediaPath);
            }
        }


        /*if (!(mediaSet)){
            return;
        }*/

        if (newFilename.toLowerCase().endsWith("flk")){
            //newFilename = newFilename.substring(0, newFilename.lastIndexOf("."))  + ".fln";
            newFilename = null;
            String message = f.getName() + " wurde gelesen und mit IDs versehen.\n";
            message+="Das Dokument ist auf Stufe " + Integer.toString(nft.getHighestParseLevel()) + " geparst.\n";
            message+="Soll die Datei automatisch normalisiert werden?";
            //message+="Neuer Dateiname ist \n";
            //message+= newFilename;
            //JOptionPane.showMessageDialog(applicationFrame, message);
            int choice = JOptionPane.showConfirmDialog(applicationFrame, message, "Auto-Normalisierung", JOptionPane.YES_NO_OPTION);
            if (choice==JOptionPane.YES_OPTION){
                    final ProgressBarDialog pbd = new ProgressBarDialog(applicationFrame, false);
                    pbd.setTitle("Auto normalizing");
                    pbd.setLocationRelativeTo(applicationFrame);
                    pbd.enableTimeEstimate(true);
                    pbd.setVisible(true);
                    autoNormalizer.addSearchListener(pbd);
                    final Runnable setTrans = new Runnable() {
                    @Override
                            public void run() {
                                try {
                                    // new 19-12-2012
                                    DerewoWordlist derewoWordlist = new DerewoWordlist();
                                    derewoWordlist.checkNormalizedFolkerTranscription(nft.getDocument());
                                } catch (IOException ex) {
                                    Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                setTranscription(nft);
                                setCurrentFilePath(null);

                                reset();            
                            }
                    };
                    Thread normalizeThread = new Thread() {
                            @Override
                            public void run() {
                                    try {
                                        int count = autoNormalizer.normalize(nft.getDocument());
                                        pbd.setVisible(false);
                                        status(Integer.toString(count) + " Wörter automatisch normalisiert. ");
                                        javax.swing.SwingUtilities.invokeAndWait(setTrans);                                        
                                        //status("Transkription " + f.getAbsolutePath() + " geöffnet.");
                                        //notifyAll();
                                    } catch (Exception ex) {
                                            ex.printStackTrace();
                                    }
                            }
                    };        
                    normalizeThread.start();
                    status("Transkription " + f.getAbsolutePath() + " geöffnet.");
                    return;
            }
        } else {
            File recentFile = new File(newFilename);
            if ((recentFiles.isEmpty()) || (!recentFiles.elementAt(0).equals(recentFile))) {
                recentFiles.remove(recentFile);
                recentFiles.add(0, recentFile);
            }
            try {
                lexicon.update(nft.getDocument());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(applicationFrame, "Could not update lexicon:\n" + ex.getMessage());
                Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        try {
            // new 19-12-2012
            DerewoWordlist derewoWordlist = new DerewoWordlist();
            derewoWordlist.checkNormalizedFolkerTranscription(nft.getDocument());
        } catch (IOException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setTranscription(nft);
        setCurrentFilePath(newFilename);

        reset();            
        status("Transkription " + f.getAbsolutePath() + " geöffnet.");
    }
    
    public void saveTranscriptionFileAs(File f, boolean checkOverwrite){
        try {
            // check if the file exists
            // if yes, ask for user confirmation
            if ((checkOverwrite) && (f.exists())) {
                int retVal = JOptionPane.showConfirmDialog(applicationFrame, "Datei existiert. Überschreiben?", "Bestätigung", JOptionPane.YES_NO_OPTION);
                if (retVal != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            // add the file to the list of recently used files
            if ((recentFiles.isEmpty()) || (!recentFiles.elementAt(0).equals(f))) {
                recentFiles.remove(f);
                recentFiles.add(0, f);
            }

            Document d = nft.getDocument();       
            
            // added 19-12-2012: remove attributes indicating suspiciousness
            // removed again - user may continue to work after saving, you trottel!
            /*for (Object o : XPath.selectNodes(d, "//@i")){
                ((Attribute)o).detach();
            }*/
            
            System.out.println("Start saving...");
            setBookmark(d, contributionListTable.getSelectedRow());
            IOUtilities.writeDocumentToLocalFile(f.getAbsolutePath(), d);
            //currentFilePath = f.getAbsolutePath();
            setCurrentFilePath(f.getAbsolutePath());
            DOCUMENT_CHANGED = false;
            contributionListTableModel.DOCUMENT_CHANGED = false;
            System.out.println("Done saving...");
            status("Datei als " + f.getAbsolutePath() + " gespeichert. ");
        } catch (IOException ex) {
            displayException(ex);
            status("Fehler beim Speichern von " + f.getAbsolutePath() + ".");
            ex.printStackTrace();
        }
    
    }
    
    private void setBookmark(Document d, int selectedRow){
        Element head = d.getRootElement().getChild("head");
        //head.removeChildren("bookmark");
        if (selectedRow>0){
            Element bookmark = new Element("bookmark");
            bookmark.setAttribute("view", "contribution");
            bookmark.setAttribute("position", Integer.toString(selectedRow));
            bookmark.setText("last edit");
            head.addContent(bookmark);
        }
    }
    
    private void gotoBookmark(Document document, String bookmarkName) {
        try {
            List l = XPath.selectNodes(document, "//bookmark[@view='contribution' and text()='last edit']");
            if (!(l.isEmpty())){
                Element bookmark = (Element)(l.get(l.size()-1));
                int row = Integer.parseInt(bookmark.getAttributeValue("position"));
                if (row < contributionListTable.getModel().getRowCount()){
                    contributionListTable.getSelectionModel().clearSelection();
                    contributionListTable.getSelectionModel().setSelectionInterval(row, row);
                    contributionListTable.scrollRectToVisible(contributionListTable.getCellRect(Math.min(row+2, contributionListTable.getModel().getRowCount()-1), 0, true));
                }            
            }
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
    }
    
    
    
    public void setMedia(String path) throws IOException{
        File tryFile = new File(path);
        String defaultAudioPath = org.exmaralda.folker.utilities.PreferencesUtilities.getProperty("default-audio-path", "");
        String tryPath = path;
        if ((!(tryFile.exists())) && defaultAudioPath!=null && defaultAudioPath.length()>0){
            String name = tryFile.getName();
            File otherTryFile = new File(new File(defaultAudioPath), name);
            if (otherTryFile.exists()){
                tryPath = otherTryFile.getAbsolutePath();
            }
        } 
        //timeViewer.setPixelsPerSecond(10.0);
        player.setSoundFile(tryPath);            
        playerState=PLAYER_IDLE;         
        currentMediaPath = path;

        applicationFrame.playerSlider.setMinimum(0);
        applicationFrame.playerSlider.setMaximum((int)Math.round(player.getTotalLength()*1000.0));


    }
    
    public void setTranscription(NormalizedFolkerTranscription nft){
        contributionListTableModel = new NormalizedContributionListTableModel(nft);
        //contributionListTableModel.setCheckRegex(checkRegex2);
        contributionListTable.setModel(contributionListTableModel);        
        contributionListTableModel.addTableModelListener(this);

        wordListTableModel = new WordListTableModel(nft.getWords());
        applicationFrame.wordTable.setModel(wordListTableModel);
        setWordListCellRenderers();
        wordListTableRowSorter = new WordListTableRowSorter(wordListTableModel);
        applicationFrame.wordTable.setRowSorter(wordListTableRowSorter);

        if (nft.getNumberOfContributions()>0){
            contributionListTable.getSelectionModel().setSelectionInterval(0, 0);
        }
        
        gotoBookmark(nft.getDocument(), "last edit");

    }
    
    void setWordListCellRenderers(){
        applicationFrame.wordTable.getColumnModel().getColumn(0).setCellRenderer(new WordListTableCellRenderer());
        applicationFrame.wordTable.getColumnModel().getColumn(1).setCellRenderer(new WordListTableCellRenderer());
        if (applicationFrame.wordTable.getModel().getColumnCount()>2){
            applicationFrame.wordTable.getColumnModel().getColumn(2).setCellRenderer(new WordListTableCellRenderer());
            applicationFrame.wordTable.getColumnModel().getColumn(3).setCellRenderer(new WordListTableCellRenderer());
            applicationFrame.wordTable.getColumnModel().getColumn(4).setCellRenderer(new WordListTableCellRenderer());
        }        
    }

    public NormalizedFolkerTranscription getTranscription() {
        return nft;
    }

    
    public void reset(){
        setGeneralDocumentActionsEnabled(true);        
    }
        

    public boolean checkSave(){
        // TODO: make a REAL check whether or not the transcription has actually changed
        boolean thereWereChanges = this.DOCUMENT_CHANGED;
        //boolean thereWereChanges = DOCUMENT_CHANGED || eventListTableModel.DOCUMENT_CHANGED || contributionListTableModel.DOCUMENT_CHANGED || partitur.transcriptionChanged;
        //if ((getTranscription().getNumberOfEvents()>0) && thereWereChanges){
        if (thereWereChanges){
            int retValue = JOptionPane.showConfirmDialog(applicationFrame, 
                    "Änderungen an aktueller Transkription speichern?", 
                    "Sicherheitsabfrage", JOptionPane.YES_NO_CANCEL_OPTION);
            if (retValue==JOptionPane.CANCEL_OPTION){
                return false;
            }
            if (retValue==JOptionPane.YES_OPTION){
                saveAction.actionPerformed(null);
            }
            return ((retValue==JOptionPane.YES_OPTION) || (retValue==JOptionPane.NO_OPTION));
        }
        return true;
    }

    
    public void storeSettings(){
        System.out.println("Storing settings");
        // window size
        PreferencesUtilities.setProperty("window-size-x", Integer.toString(applicationFrame.getWidth()));        
        PreferencesUtilities.setProperty("window-size-y", Integer.toString(applicationFrame.getHeight()));        
        // window location
        PreferencesUtilities.setProperty("window-location-x", Integer.toString(applicationFrame.getLocationOnScreen().x));        
        PreferencesUtilities.setProperty("window-location-y", Integer.toString(applicationFrame.getLocationOnScreen().y));        
        // divider locations
        PreferencesUtilities.setProperty("divider-location", Integer.toString(applicationFrame.mainSplitPane.getDividerLocation()));
        PreferencesUtilities.setProperty("outer-divider-location", Integer.toString(applicationFrame.outerSplitPane.getDividerLocation()));
        // pixels per second in time viewer
        // recent files
        String recentFileString = "";
        for (int pos=0; pos<Math.min(4,recentFiles.size()); pos++){
            recentFileString+=recentFiles.elementAt(pos).getAbsolutePath()+"#";
        }
        PreferencesUtilities.setProperty("recent-files", recentFileString);

    }
    
    public void retrieveSettings(){
        System.out.println("Retrieving settings");
        // window size
        int windowSizeX = Integer.parseInt(PreferencesUtilities.getProperty("window-size-x", Integer.toString(applicationFrame.getWidth())));
        int windowSizeY = Integer.parseInt(PreferencesUtilities.getProperty("window-size-y", Integer.toString(applicationFrame.getHeight())));
        applicationFrame.setSize(new Dimension(Math.max(200, windowSizeX), Math.max(100,windowSizeY)));
        // window location
        int windowLocationX = Integer.parseInt(PreferencesUtilities.getProperty("window-location-x", "0"));
        int windowLocationY = Integer.parseInt(PreferencesUtilities.getProperty("window-location-y", "0"));
        applicationFrame.setLocation(Math.max(0,windowLocationX), Math.max(0,windowLocationY));
        // divider location
        int dividerLocation = Integer.parseInt(PreferencesUtilities.getProperty("divider-location", "200"));
        applicationFrame.mainSplitPane.setDividerLocation(dividerLocation);
        // outer divider location (main edit field / word list)
        int preset = Math.max(windowSizeX - 400, windowSizeX * 2 / 3);
        int outerDividerLocation = Integer.parseInt(PreferencesUtilities.getProperty("outer-divider-location", Integer.toString(preset)));
        applicationFrame.outerSplitPane.setDividerLocation(outerDividerLocation);


        // recent files
        String recentFileString = PreferencesUtilities.getProperty("recent-files", "");
        if (recentFileString.length()>0){
            String[] fileNames = recentFileString.split("#");
            for (String fileName : fileNames){
                System.out.println("#### " + fileName);
                File f = new File(fileName);
                recentFiles.add(f);
                OpenRecentAction opra = new OpenRecentAction(this, f);
                applicationFrame.fileMenu.add(opra).setToolTipText(f.getAbsolutePath());
            }
        }

        applicationFrame.fileMenu.addSeparator();
        applicationFrame.fileMenu.add(exitAction);


    }

    
    public void exitApplication(){
        if (!checkSave()) return;
        System.out.println("Application terminated");
        System.out.println("-----------------------");
        storeSettings();
        writeLexicon();
        System.exit(0);
    }
    
    
/***************** OTHER ******************************/ 


    public void copySelectionToClipboard() throws SAXException, JDOMException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
            int[] selectedContributions = contributionListTable.getSelectedRows();
            EventListTranscription elt2 = null; // getTranscription().partForContributions(selectedContributions);
            String STYLESHEET2 = OutputAction.CONTRIBUTIONS2HTML_STYLESHEET;
            Document transcriptionDoc2 = EventListTranscriptionXMLReaderWriter.toJDOMDocument(elt2, new File(currentFilePath));
            transcriptionDoc2.getRootElement().setAttribute("count-start", Integer.toString(contributionListTable.getSelectedRow()));
            String docString2 = org.exmaralda.common.jdomutilities.IOUtilities.documentToString(transcriptionDoc2);
            //System.out.println(docString2);
            StylesheetFactory sf2 = new StylesheetFactory(true);
            String resultString2 = sf2.applyInternalStylesheetToString(STYLESHEET2, docString2);
            org.exmaralda.exakt.utilities.HTMLSelection html2 = new org.exmaralda.exakt.utilities.HTMLSelection(resultString2);
            this.getFrame().getToolkit().getSystemClipboard().setContents(html2,null);
            status("Auswahl in Zwischenablage kopiert. ");

    }


    
    public void setCurrentFilePath(String cfp) {
        currentFilePath = cfp;
        String appname = applicationFrame.getApplicationName() + " " + applicationFrame.getVersion();
        if (cfp==null){
            applicationFrame.setTitle(appname + " [Neue Transkription]");
        } else {
            applicationFrame.setTitle(appname +  " [" + cfp + "]");
        }
    }


    static final String TIME_NOW = "HH:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(TIME_NOW);

    public void status(String m){
        Calendar cal = Calendar.getInstance();
        String message = "[" + sdf.format(cal.getTime()) + "] " + m;
        applicationFrame.statusMessageLabel.setText(message);
    }
        
    public void displayException(Exception e){
        String title = "Fehler";
        String message = e.getLocalizedMessage();
        JOptionPane.showMessageDialog(getFrame(), message, title, JOptionPane.ERROR_MESSAGE);
        status(title + ": " + message);
        e.printStackTrace();
    }


    public String displayRecordingNotFoundDialog(String mediaPath, Exception ex){
        String message = "Fehler beim Lesen der Aufnahme \n" + mediaPath +".\nFehlermeldung:\n" + ex.getLocalizedMessage()
                + "\nBitte ordnen Sie die Aufnahme neu zu.";
        JOptionPane.showMessageDialog(applicationFrame, message);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Aufnahme zuordnen");
        fileChooser.setFileFilter(new org.exmaralda.folker.utilities.WaveFileFilter());
        fileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));
        int retValue = fileChooser.showOpenDialog(getFrame());
        if (retValue==JFileChooser.CANCEL_OPTION) return "";
        String newMediaPath = fileChooser.getSelectedFile().getAbsolutePath();
        DOCUMENT_CHANGED = true;
        return newMediaPath;
    }
    
        
    /** process changes in the selection of the contribution view */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if ((e!=null) && (e.getValueIsAdjusting())) return;
        // the selected contribution(s) in the contribution view have changed
        int firstSelectedRow = contributionListTable.getSelectedRow();
        if (firstSelectedRow<0) return;

        Element contribution = nft.getContributionAt(firstSelectedRow);
        String sID = contribution.getAttributeValue("start-reference");
        String eID = contribution.getAttributeValue("end-reference");
        Timepoint minTimepoint = nft.getTimeForId(sID);
        Timepoint maxTimepoint = nft.getTimeForId(eID);

        // change the start and end times for the player
        selectionStart = minTimepoint.getTime();
        selectionEnd = maxTimepoint.getTime();
        playSelectionAction.setEnabled(selectionStart!=selectionEnd);

        //applicationFrame.playerSlider.setMinimum((int)Math.round(selectionStart));
        //applicationFrame.playerSlider.setMaximum((int)Math.round(selectionEnd));
        applicationFrame.playerSlider.setValue((int)Math.round(minTimepoint.getTime()));
        applicationFrame.startLabel.setText(TimeStringFormatter.formatMiliseconds(selectionStart, 2));
        applicationFrame.stopLabel.setText(TimeStringFormatter.formatMiliseconds(selectionEnd, 2));
        

        setupEditor(contribution);
    }

    void setupEditor(final Element contribution){
        commitEdit();
        // maybe this could be optimized by keeping words in an index in the nft
        /*Iterator i = contribution.getDescendants(new ElementFilter("w"));
        while (i.hasNext()){
            Element word = (Element)(i.next());
            WordLabel wordLabel = new WordLabel(word, this);
            applicationFrame.editPanel.add(wordLabel);
        }*/
        //long before = System.currentTimeMillis();
        if (mode==XML_MODE){
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            
            final JEditorPane xmlEditorPane = new JEditorPane();
            XMLEditorKit xmlEditorKit = new XMLEditorKit(true); 
            xmlEditorKit.setWrapStyleWord(true);      
            xmlEditorPane.setFont(xmlEditorPane.getFont().deriveFont(11.0f));
            xmlEditorPane.setEditorKit(xmlEditorKit);
            xmlEditorPane.setText(IOUtilities.elementToString(contribution, contribution.getContentSize()<100));
            
            //xmlEditorPane.setSize( new Dimension(1000, Short.MAX_VALUE));
            //Dimension preferredSize = xmlEditorPane.getPreferredSize();
            xmlEditorPane.setPreferredSize(new Dimension(800, 400));
            
            JButton okButton = new JButton("Ändern");
            okButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    String editedXMLText = xmlEditorPane.getText();
                    try {
                        Element editedContribution = IOUtilities.readElementFromString(editedXMLText);
                        contribution.setContent(editedContribution.removeContent());
                        nft.reindexContribution(contribution);
                        wordListTableModel.fireTableDataChanged();
                        contributionListTableModel.fireTableCellUpdated(contributionListTable.getSelectedRow(), 4);
                    } catch (JDOMException ex) {
                        Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(xmlEditorPane, ex.getLocalizedMessage());
                    } catch (IOException ex) {
                        Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(xmlEditorPane, ex.getLocalizedMessage());
                    }
                }               
            });
            
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(xmlEditorPane);
            applicationFrame.editPanel.setLayout(new BorderLayout());
            applicationFrame.editPanel.add(scrollPane, java.awt.BorderLayout.CENTER);
            applicationFrame.editPanel.add(okButton, java.awt.BorderLayout.NORTH);
            //applicationFrame.editPanel.add(panel);
                      
            applicationFrame.editPanel.revalidate();
            applicationFrame.editPanel.repaint();
            applicationFrame.editPanel.scrollRectToVisible(new Rectangle(0,0,1,1));            
        } else if (mode==CORRECTION_MODE) {
            //ContributionTextPane contributionTextPane = new ContributionTextPane();
            //contributionTextPane.setContribution(contribution);
            // TODO
        } else {
            FlowLayout fl = new FlowLayout();
            fl.setAlignment(FlowLayout.LEFT);
            applicationFrame.editPanel.setLayout(fl);
            for (Element word : nft.getWordsForContribution(contribution)){
                WordLabel wordLabel = new WordLabel(word, this);
                applicationFrame.editPanel.add(wordLabel);
            }
            applicationFrame.editPanel.revalidate();
            applicationFrame.editPanel.repaint();
            applicationFrame.editPanel.scrollRectToVisible(new Rectangle(0,0,1,1));
            if (autoAdvance){
                if (applicationFrame.editPanel.getComponentCount()>0){
                    WordLabel firstWordLabel = (WordLabel) applicationFrame.editPanel.getComponent(0);
                    firstWordLabel.mouseClicked(null);
                }
            }
        }
        //long after = System.currentTimeMillis();
        //System.out.println("Setup time: " + (after-before)/1000);
    }

    void commitEdit(){
        applicationFrame.editPanel.removeAll();
    }


    /** process change events fired by either the event or the contribution view */
    @Override
    public void tableChanged(TableModelEvent e) {
    }

    @Override
    public void processPlayableEvent(PlayableEvent e) {
        int type = e.getType();
        switch (type){
            case PlayableEvent.SOUNDFILE_SET :
                status("Audiodatei geladen.");
                break;                
            case PlayableEvent.PLAYBACK_STARTED :                 
                status("Playback gestartet.");
                playSelectionAction.setEnabled(false);
                stopAction.setEnabled(true);
                break;
            case PlayableEvent.PLAYBACK_STOPPED : 
                playSelectionAction.setEnabled(selectionStart!=selectionEnd);
                stopAction.setEnabled(false);
                status("Playback gestoppt.");
                playerState=PLAYER_IDLE;
                break;
            case PlayableEvent.PLAYBACK_HALTED :
                status("Playback angehalten.");
                break;                
            case PlayableEvent.PLAYBACK_RESUMED :
                status("Playback fortgesetzt.");
                break;
            case PlayableEvent.POSITION_UPDATE :
                double pos = e.getPosition();
                applicationFrame.playerSlider.setValue((int)Math.round(pos*1000.0));
                break;
            default: break;
        }
    }    

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource()==applicationFrame.playerSlider){
            double pos = applicationFrame.playerSlider.getValue();
            applicationFrame.positionLabel.setText(TimeStringFormatter.formatMiliseconds(pos, 2));
            playAction.setEnabled((pos>=0));
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {        
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }


    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
    }

    @Override
    public void validateContribution() {        
        int row = contributionListTable.getSelectedRow();
        if (row<0) return;
        contributionListTableModel.fireTableCellUpdated(row,3);
    }


    
    @Override
    public void processTimepoint(Timepoint tp) {
        if (playerState!=ApplicationControl.PLAYER_IDLE){
            stop();
        }
        double time = tp.getTime();
        //System.out.println("Playing time " + time);
        selectionStart = time;
        playSelection();
    }
    
    @Override
    public void processTimepoint(Timepoint tp, MouseEvent e) {
        if (playerState!=ApplicationControl.PLAYER_IDLE){
            stop();
        } 
        double start = tp.getTime();
        if (!(e.isShiftDown())){            
            selectionStart = start;
            //System.out.println("Playing *time " + start);
            if (selectionEnd <= selectionStart){
                selectionEnd = player.getTotalLength();
            }
            playSelection();
        } else {
            selectionStart = start;
            Timepoint tpEnd = tp.getTimeline().getNextTimepoint(tp);
            double end = this.player.getTotalLength();
            if (tpEnd!=null){
                end = tpEnd.getTime();
            }
            selectionEnd = end;
            //System.out.println("Playing time " + start + " / " + end);
            playSelection();
        }
    }
    
    
    
    // WindowListener interface methods
    @Override
    public void windowOpened(WindowEvent e) {
        // TODO
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (e.getSource()==this.applicationFrame){
            exitApplication();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
        if (e.getSource()==this.applicationFrame){
        }
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO
    }

    @Override
    public void windowActivated(WindowEvent e) {
        // TODO
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getSource()==applicationFrame.wordTable) && (e.getClickCount()==2)){
            int row = applicationFrame.wordTable.rowAtPoint(e.getPoint());
            if (row>=0){
                int mRow = applicationFrame.wordTable.convertRowIndexToModel(row);
                final Element thisWordElement = (Element) wordListTableModel.getValueAt(mRow,0);
                final int contributionNo = nft.getContributionIndex(thisWordElement);
                contributionListTable.getSelectionModel().setSelectionInterval(contributionNo, contributionNo);
                SwingUtilities.invokeLater(new Runnable(){
                        @Override
                        public void run() {
                            contributionListTable.scrollRectToVisible(contributionListTable.getCellRect(contributionNo, 0, true));
                            findWord(thisWordElement);
                        }
                    }
                );
            }
        }
    }

    void findWord(Element wordElement){
        for (Component c : applicationFrame.editPanel.getComponents()){
            if (!(c instanceof WordLabel)) continue;
            WordLabel wl = (WordLabel)c;
            if (wl.getWordElement()==wordElement){
                wl.highlight();
                break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {showWordTablePopup(e);}
    @Override
    public void mouseReleased(MouseEvent e) {showWordTablePopup(e);}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    void showWordTablePopup(MouseEvent e){
        if (e.getSource()==applicationFrame.wordTable){
            if (!e.isPopupTrigger()) return;
            if (applicationFrame.wordTable.getSelectedRowCount()<=0) return;
            Element[] wordElements = new Element[applicationFrame.wordTable.getSelectedRowCount()];
            int i=0;
            for (int row : applicationFrame.wordTable.getSelectedRows()){
                int mRow = applicationFrame.wordTable.convertRowIndexToModel(row);
                Element thisWordElement = (Element) wordListTableModel.getValueAt(mRow,0);
                wordElements[i] = thisWordElement;
                i++;
            }
            // changed 03-09-2014
            if (this.getMode()==ApplicationControl.NORMALIZATION_MODE) {
                WordNormalizationDialog dialog = new WordNormalizationDialog(applicationFrame, true, wordElements[0], queryLexicon(wordElements));
                dialog.setLocation(e.getLocationOnScreen());
                dialog.setVisible(true);
                if (!dialog.escaped){
                    String normalizedForm = dialog.getNormalizedForm();
                    String newWordForm = WordUtilities.getWordText(dialog.getWordElement());
                    
                    for (int row : applicationFrame.wordTable.getSelectedRows()){
                        int mRow = applicationFrame.wordTable.convertRowIndexToModel(row);
                        Element thisWordElement = (Element) wordListTableModel.getValueAt(mRow,0);
                        // changed 03-09-2014
                        //String form = thisWordElement.getText();
                        String form = WordUtilities.getWordText(thisWordElement);
                        
                        String lemma = form;
                        if (!(normalizedForm.equals(form))){
                            thisWordElement.setAttribute("n", dialog.getNormalizedForm());
                            lemma = normalizedForm;
                        } else {
                            thisWordElement.removeAttribute("n");
                        }
                        
                        // added 03-09-2014
                        if (!(newWordForm.equals(form)) && (thisWordElement.getChildren().isEmpty())){
                            // the user has also changed the word form!
                            // only do this if the word does not contain any child elements
                            thisWordElement.setText(newWordForm);
                        }
                        
                        wordListTableModel.fireTableCellUpdated(mRow, 0);
                        wordListTableModel.fireTableCellUpdated(mRow, 1);
                        if (lexicon instanceof SimpleXMLFileLexicon){
                            try {
                                lexicon.put(form, lemma, getTranscription().getID(), thisWordElement.getAttributeValue("id"));
                            } catch (LexiconException ex) {
                                ex.printStackTrace();
                                displayException(ex);
                            }
                        }    
                        updateContribution();
                        valueChanged(null);
                    }
                    //setWord();
                    DOCUMENT_CHANGED = true;
                }
            // added 03-09-2014
            } else if (this.getMode()==ApplicationControl.TAGGING_MODE) {
                TaggingDialog dialog = new TaggingDialog(applicationFrame, true, wordElements[0]);
                dialog.setLocation(e.getLocationOnScreen());
                dialog.setVisible(true);
                if (!dialog.escaped){
                    String lemma = dialog.getLemma();
                    String pos = dialog.getPOS();
                    for (int row : applicationFrame.wordTable.getSelectedRows()){
                        int mRow = applicationFrame.wordTable.convertRowIndexToModel(row);
                        Element thisWordElement = (Element) wordListTableModel.getValueAt(mRow,0);
                        thisWordElement.setAttribute("lemma", lemma);
                        thisWordElement.setAttribute("pos", pos);
                        wordListTableModel.fireTableCellUpdated(mRow, 2);
                        wordListTableModel.fireTableCellUpdated(mRow, 3);
                        updateContribution();
                        valueChanged(null);
                    }
                    //setWord();
                    DOCUMENT_CHANGED = true;
                }                
            }
        }
    }


    void autoNormalize() throws LexiconException {
        int firstSelectedRow = contributionListTable.getSelectedRow();
        if (firstSelectedRow<0) return;
        Element contribution = nft.getContributionAt(firstSelectedRow);
        int count = autoNormalizer.normalize(contribution);
        wordListTableModel.fireTableDataChanged();
        setupEditor(contribution);
        status(Integer.toString(count) + " Wörter automatisch normalisiert. ");
    }
    
    private void autoNormalizeAll() {
    }
    

    public void updateContribution() {
        contributionListTableModel.fireTableCellUpdated(contributionListTable.getSelectedRow(), 4);
    }

    public void nextWord(WordLabel aThis) {        
        if(!autoAdvance) return;
        applicationFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));        
        aThis.setBackground(Color.WHITE);
        Element e = aThis.getWordElement();
        Element c;
        try {
            c = (Element) XPath.newInstance("ancestor::contribution").selectSingleNode(e);
            int i = nft.getWordsForContribution(c).indexOf(e);
            int cc = applicationFrame.editPanel.getComponentCount();
            if (i+1<cc){
                WordLabel nextWordLabel = (WordLabel) applicationFrame.editPanel.getComponent(i+1);
                nextWordLabel.setBackground(Color.yellow);
                nextWordLabel.mouseClicked(null);
            } else {
                // this is the last word in the current contribution
                int r = contributionListTable.getSelectedRow();
                if (r>=nft.getNumberOfContributions()-1) return;
                while ((r+1 < nft.getNumberOfContributions()) && (nft.getWordsForContribution(nft.getContributionAt(r+1)).size()<=0)){
                    r++;
                }
                if (!(r+1<nft.getNumberOfContributions())) return;
                //Element nextC = nft.getContributionAt(r+1);
                contributionListTable.getSelectionModel().clearSelection();
                contributionListTable.getSelectionModel().setSelectionInterval(r+1, r+1);
            }
        applicationFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));        
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
    }

    public void updateRDBLexicon() throws SQLException, JDOMException, LexiconException {
        JPasswordField jpf = new JPasswordField();
        int returnValue = JOptionPane.showConfirmDialog(applicationFrame, jpf, "Passwort:", JOptionPane.OK_CANCEL_OPTION);
        if (returnValue==JOptionPane.CANCEL_OPTION) return;
        String enteredPW = "";
        for (char c : jpf.getPassword()){
            enteredPW+=c;
        }
        if (!("REKLOF".equals(enteredPW))){
            JOptionPane.showMessageDialog(applicationFrame, "Passwort nicht korrekt");
        }
        if (!(lexicon instanceof RDBLexicon)){
            JOptionPane.showMessageDialog(applicationFrame, "Kein RDB-Lexikon");            
        }
        applicationFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));                
        RDBLexicon rdbl = (RDBLexicon)lexicon;
        rdbl.update(getTranscription().getDocument());
        applicationFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));                
        JOptionPane.showMessageDialog(applicationFrame, "Lexikon aktualisiert!");            
    }
    
    public void saveLexicon() {
        SaveLexiconDialog sld = new SaveLexiconDialog(applicationFrame,true);
        sld.setFilename(LEXICON_PATH);
        sld.setLexicon(lexicon); 
        sld.setLocationRelativeTo(applicationFrame);
        sld.setVisible(true);
    }
    

    void filter(String filterText, int column) {
        RowFilter<WordListTableModel, Object> rowFilter = null;
        //If current expression doesn't parse, don't update.
        try {
            rowFilter = RowFilter.regexFilter(filterText, column);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        wordListTableRowSorter.setRowFilter(rowFilter);
    }
    
    void filterSplitWords(boolean filter){
        if (filter){
            RowFilter<WordListTableModel, Object> rowFilter = new RowFilter <WordListTableModel, Object>(){
                @Override
                public boolean include(RowFilter.Entry<? extends WordListTableModel, ? extends Object> entry) {
                    Element word = (Element) entry.getValue(0);
                    return (word.getChild("time")!=null);
                }                
            };
            wordListTableRowSorter.setRowFilter(rowFilter);
        } else {
            wordListTableRowSorter.setRowFilter(null);
        }
    }

    void filterOutOfVocabularyWords(boolean filter){
        if (filter){
            RowFilter<WordListTableModel, Object> rowFilter = new RowFilter <WordListTableModel, Object>(){
                @Override
                public boolean include(RowFilter.Entry<? extends WordListTableModel, ? extends Object> entry) {
                    Element word = (Element) entry.getValue(0);
                    return ("n".equals(word.getAttributeValue("i")));
                }                
            };
            wordListTableRowSorter.setRowFilter(rowFilter);
        } else {
            wordListTableRowSorter.setRowFilter(null);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        checkFilterRegex();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        checkFilterRegex();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        checkFilterRegex();
    }

    private void checkFilterRegex() {
        String input = applicationFrame.filterTextField.getText();
        try {
            java.util.regex.Pattern.compile(input);
            applicationFrame.filterTextField.setForeground(Color.BLACK);
            applicationFrame.filterTextField.setToolTipText("Regulärer Ausdruck OK");
            applicationFrame.filterButton.setEnabled(true);
        } catch (PatternSyntaxException pse){
            applicationFrame.filterTextField.setForeground(Color.RED);
            applicationFrame.filterButton.setEnabled(false);
            applicationFrame.filterTextField.setToolTipText(pse.getLocalizedMessage());            
        }
    }

    
    void editContribution() throws JexmaraldaException {
        try {
            int firstSelectedRow = contributionListTable.getSelectedRow();
            if (firstSelectedRow<0) return;

            //EventListTranscription elt = EventListTranscriptionXMLReaderWriter.readXML(getTranscription().getDocument(), 0);
            //elt.updateContributions();
            
            
            //Contribution c = elt.getContributionAt(firstSelectedRow);
            Element originalContributionElement = getTranscription().getContributionAt(firstSelectedRow);
            // CLEANUP - 01-12-2014
            String badTimes = "//contribution/descendant::time[(not(following-sibling::*) and not(following-sibling::text())) or following-sibling::node()[1][self::time]]";
            List bt = XPath.selectNodes(originalContributionElement, badTimes);
            for (Object o : bt){
                Element t = (Element)o;
                t.detach();
            }
            // END CLEANUP
            
            
            //System.out.println("--------- INPUT -----------------");
            //System.out.println("1: " + IOUtilities.elementToString(originalContributionElement));
            // changed 20-01-2014            
            EventListTranscription elt = Contribution.getTranscriptionForContributionFromOrthoNormalDocument(getTranscription().getDocument(), firstSelectedRow);
            //System.out.println("2: " + IOUtilities.elementToString(elt.getContributionAt(0).toJDOMElement(elt.getTimeline())));
            HashMap<String,String> originalNormalizations = new HashMap<String, String>();
            List l1 = XPath.selectNodes(originalContributionElement, "descendant::w[@n]");
            for (Object o : l1){
                Element w = (Element)o;
                String word = WordUtilities.getWordText(w);
                String normalization = w.getAttributeValue("n");
                originalNormalizations.put(word, normalization);
            }

            HashMap<String,String> originalLemmas = new HashMap<String, String>();
            HashMap<String,String> originalPOS = new HashMap<String, String>();
            List l2 = XPath.selectNodes(originalContributionElement, "descendant::w[@lemma and @pos]");
            for (Object o : l2){
                Element w = (Element)o;
                String word = WordUtilities.getWordText(w);
                String lemma = w.getAttributeValue("lemma");
                String pos = w.getAttributeValue("pos");
                originalLemmas.put(word, lemma);
                originalPOS.put(word, pos);
            }
            
            
            int parseLevel = Integer.parseInt(originalContributionElement.getAttributeValue("parse-level"));
            
            EditContributionDialog dialog = new EditContributionDialog(applicationFrame, this, true);
            dialog.setContribution(elt.getContributionAt(0));
            dialog.setLocationRelativeTo(applicationFrame.getContentPane());
            dialog.addContributionTextPaneListener(this);
            dialog.setVisible(true);            
            
            player.stopPlayback();
            
            if(!dialog.ok) return;
            
            applicationFrame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            //Contribution modifiedContribution = dialog.getContribution();
                       
            GATParser parser = new GATParser();
                    
            Document eltDocument = EventListTranscriptionXMLReaderWriter.toJDOMDocument(elt, null);
            parser.parseDocument(eltDocument, 1);
            parser.parseDocument(eltDocument, parseLevel);
            
            //System.out.println("***********************");
            //System.out.println(IOUtilities.documentToString(eltDocument));
            
            
            Element modifiedContributionElement = 
                    (Element) XPath.selectNodes(eltDocument, "//contribution").get(0);
            
            

            int newParseLevel = Integer.parseInt(modifiedContributionElement.getAttributeValue("parse-level"));
            if (!(parseLevel==newParseLevel)){
                applicationFrame.getContentPane().setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(applicationFrame, "Der bearbeitete Beitrag konnte nicht auf dem selben \n"
                        + "Parse-Level geparst werden wie der Ausgangsbeitrag.\n"
                        + "Die Änderungen wurden nicht angenommen.");
                return;
            }
            
            //modifiedContributionElement should now be correct, but it contains wrong timeline IDs
            //take the original one and insert them into the new one
            modifiedContributionElement.setAttribute("start-reference", originalContributionElement.getAttributeValue("start-reference"));
            modifiedContributionElement.setAttribute("end-reference", originalContributionElement.getAttributeValue("end-reference"));
            List tModified = XPath.selectNodes(modifiedContributionElement, "descendant::time");
            List tOriginal = XPath.selectNodes(originalContributionElement, "descendant::time");
            if (tModified.size()!=tOriginal.size()){
                applicationFrame.getContentPane().setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(applicationFrame, "Es gab Probleme beim Zuordnen von Zeitpunkten. \n"
                        + "Die Änderungen wurden nicht angenommen.");
                return;                
            }
            for (int i=0; i<tModified.size(); i++){
                Element eModified = (Element) tModified.get(i);
                Element eOriginal = (Element) tOriginal.get(i);
                eModified.setAttribute("timepoint-reference", eOriginal.getAttributeValue("timepoint-reference"));
                
            }
            
           
            // make new IDs for words
            List l = XPath.selectNodes(modifiedContributionElement, "descendant::w");
            int i=1;
            HashSet<String> newIDs = new HashSet<String>();
            for (Object o : l){
                Element w = (Element)o;
                String testID = "w1";                
                while (getTranscription().hasWordID(testID)||newIDs.contains(testID)){
                    i++;
                    testID = "w" + Integer.toString(i);
                }
                w.setAttribute("id", testID);
                newIDs.add(testID);
                i++;
            }
            System.out.println("*************************");
            //System.out.println(IOUtilities.elementToString(modifiedContributionElement));
            // reinsert original normalizations
            autoNormalizer.normalize(modifiedContributionElement, originalNormalizations);
            
            // reinsert original lemmatisation and POS-tagging
            if (!originalLemmas.isEmpty()){
                for (Object o : l){
                    Element w = (Element)o;
                    String word = WordUtilities.getWordText(w);
                    if (originalLemmas.containsKey(word)){
                        w.setAttribute("lemma", originalLemmas.get(word));
                    } else {
                        w.setAttribute("lemma", word);
                    }                    
                    if (originalPOS.containsKey(word)){
                        w.setAttribute("pos", originalPOS.get(word));
                    } else {
                        w.setAttribute("pos", "---");
                    }                    
                }                
            }
            
            getTranscription().unindexContribution(originalContributionElement);
            originalContributionElement.setContent(modifiedContributionElement.removeContent());            
            getTranscription().reindexContribution(originalContributionElement);
            
            System.out.println("--------- RESULT -----------------");
            System.out.println(IOUtilities.elementToString(originalContributionElement));
            
            updateContribution();
            valueChanged(null);         
            
            wordListTableModel = new WordListTableModel(nft.getWords());
            applicationFrame.wordTable.setModel(wordListTableModel);
            setWordListCellRenderers();
            wordListTableRowSorter = new WordListTableRowSorter(wordListTableModel);
            applicationFrame.wordTable.setRowSorter(wordListTableRowSorter);
            
            
            applicationFrame.getContentPane().setCursor(Cursor.getDefaultCursor());
            
            this.DOCUMENT_CHANGED = true;
        } catch (JDOMException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    void changeSpeaker() {
        int firstSelectedRow = contributionListTable.getSelectedRow();
        if (firstSelectedRow<0) return;
        Element originalContributionElement = getTranscription().getContributionAt(firstSelectedRow);
        String currentSpeakerID = originalContributionElement.getAttributeValue("speaker-reference");
        try {
            Vector<Speaker> speakerlist = new Vector<Speaker>();
            List l = XPath.selectNodes(nft.getDocument(), "//speaker");
            Speaker currentSpeaker = null;
            for (Object o: l){
                String id = ((Element)o).getAttributeValue("speaker-id");
                Speaker s = nft.getSpeakerForId(id);
                speakerlist.addElement(s);
                if (id.equals(currentSpeakerID)){
                    currentSpeaker = s;
                }
            }
            JComboBox comboBox = new JComboBox();
            DefaultComboBoxModel model = new DefaultComboBoxModel(speakerlist);    
            comboBox.setModel(model);
            comboBox.setSelectedItem(currentSpeaker);
            comboBox.setRenderer(new SpeakerListCellRenderer());
            int accept = JOptionPane.showConfirmDialog(applicationFrame, comboBox, "Neue Sprecherzuordnung",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (accept==JOptionPane.OK_OPTION){
                Speaker s = (Speaker) comboBox.getSelectedItem();
                String id = s.getIdentifier();
                originalContributionElement.setAttribute("speaker-reference", id);
                contributionListTableModel.fireTableCellUpdated(firstSelectedRow, 3);
            }
        } catch (JDOMException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processMatchListEvent(File workingDirectory, Element contribution) {
        try {
            String filename = contribution.getAttributeValue("transcript");
            System.out.println(workingDirectory.getAbsolutePath());;
            System.out.println(IOUtilities.elementToString(contribution));
            File f = new File(workingDirectory, filename);
            if (currentFilePath==null || (!(f.equals(new File(currentFilePath))))){
                if (!checkSave()) return;
                openTranscriptionFile(f);
            }
            
            String wordID = contribution.getAttributeValue("match");
            final Element w = (Element) XPath.selectSingleNode(nft.getDocument(), "//w[@id='" + wordID + "']");
            Element c = (Element) XPath.selectSingleNode(w, "ancestor::contribution");
            final int position = c.getParentElement().getChildren("contribution").indexOf(c); // - 4;
            
            System.out.println("Scrolling to " + position);
            
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    contributionListTable.scrollRectToVisible(contributionListTable.getCellRect(position, 0, true));
                    contributionListTable.getSelectionModel().setSelectionInterval(position, position);
                    findWord(w);
                }
                
            });
            
            
            
            
        } catch (JDOMException ex) {
            JOptionPane.showMessageDialog(applicationFrame, ex);
        }
        
        
    }

    void toggleMatchList(boolean showOrDont) {
        matchListDialog.setVisible(showOrDont);
        applicationFrame.showMatchListCheckBoxMenuItem.setSelected(matchListDialog.isShowing());
        
    }

    /**************************/
    /* CMC DATA Functionality */
    /**************************/
    
    String TRANSFORM_CMC_XSL = "/org/exmaralda/orthonormal/chat/ChatXML2FLN.xsl";
    
    public void importCMCFile(File f) {
        try {
            // remove DTD reference because it is broken
            String cleanedUp = removeEmptyLinesAndDTD(f.getAbsolutePath());
            
            // transform to FLN using the stylesheet
            StylesheetFactory ssf = new StylesheetFactory(true);
            String transformed = ssf.applyInternalStylesheetToString(TRANSFORM_CMC_XSL, cleanedUp);
            nft = org.exmaralda.orthonormal.io.XMLReaderWriter.readNormalizedFolkerTranscription(transformed); 
            
            // check if the document contains at least one normalisation
            if (XPath.selectSingleNode(nft.getDocument(), "//w[@n]")==null){
                int choice = JOptionPane.showConfirmDialog(applicationFrame, 
                        "No normalisations found in document.\nApply automatic normalisation?", "Auto normalise", JOptionPane.YES_NO_OPTION);
                if (choice==JOptionPane.YES_OPTION){
                    DerewoWordlist derewoWordlist = new DerewoWordlist();
                    int count1 = derewoWordlist.checkNormalizedFolkerTranscription(nft.getDocument());
                    int count2 = autoNormalizer.normalize(nft.getDocument());
                    String message = Integer.toString(count1) + " out of vocabulary items according to DeReWo.\n"
                            + Integer.toString(count2) + " forms normalised via lexicon.\n";
                    JOptionPane.showMessageDialog(applicationFrame, message);
                }
            }
                        
            // set the document for the editor
            setTranscription(nft);
            String newFilename = null;
            setCurrentFilePath(newFilename);

            //reset();        
            setupForCMCEditing();
            status("CMC-Datei " + f.getAbsolutePath() + " importiert.");
        } catch (Exception ex) {
            ex.printStackTrace();
            displayException(ex);
        }
                
    }
    
    public void exportCMCFile(File f) {
        try {
            Document editedDocument = getTranscription().getDocument();
            String cleanedUp = removeEmptyLinesAndDTD(f.getAbsolutePath());
            Document targetDocument = FileIO.readDocumentFromString(cleanedUp);
            
            List editedTokens = XPath.selectNodes(editedDocument, "//w");
            HashMap<String,Element> ids2editedTokens = new HashMap<String, Element>();
            for (Object o : editedTokens){
                Element e = (Element)o;
                String id = e.getAttributeValue("id");
                ids2editedTokens.put(id, e);
            }
                        
            List targetTokens = XPath.selectNodes(targetDocument, "//messageBody/descendant::token");
            for (Object o : targetTokens){
                Element t = (Element)o;
                String id = t.getAttributeValue("id");
                Element e = ids2editedTokens.get(id);
                
                if (e.getAttributeValue("n")!=null){
                    t.setAttribute("n", e.getAttributeValue("n"));                    
                } else {
                    t.removeAttribute("n");
                }
                t.setAttribute("lemma", e.getAttributeValue("lemma"));
                t.setAttribute("pos", e.getAttributeValue("pos"));
                t.setAttribute("i", e.getAttributeValue("i"));
            }
            
            FileIO.writeDocumentToLocalFile(f, targetDocument, "ISO-8859-1");
            status("CMC-Datei " + f.getAbsolutePath() + " exportiert.");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            displayException(ex);
        } 
        

        
    }

    
    private String removeEmptyLinesAndDTD(String filename) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        StringBuilder result = new StringBuilder();
        FileInputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
        BufferedReader br = new BufferedReader(isr);
        String nextLine="";
        while ((nextLine = br.readLine()) != null){
            if ((nextLine.trim().length()>0) && (!(nextLine.contains("<!DOCTYPE")))){
                result.append(nextLine);
            } else if (nextLine.contains("<!DOCTYPE")){
                //<!DOCTYPE logfile SYSTEM "../../../DTD/chatkorpus.dtd">
                result.append(nextLine.replaceAll("<!DOCTYPE[^>]+>", ""));
            }
        }
        br.close();
        return result.toString();
    }

    private void setupForCMCEditing() {
        applicationFrame.playerControlsPanel.setVisible(false);
        applicationFrame.xmlRadioButton.setEnabled(true); 
        contributionListTable.setEnabled(true);
        TableColumn column1 = contributionListTable.getColumnModel().getColumn(1);
        TableColumn column2 = contributionListTable.getColumnModel().getColumn(2);
        contributionListTable.getColumnModel().removeColumn(column1);
        contributionListTable.getColumnModel().removeColumn(column2);
    }
    








    
    
    

    
}
