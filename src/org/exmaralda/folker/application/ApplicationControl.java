/*
 * ApplicationControl.java
 *
 * Created on 9. Mai 2008, 14:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.data.*;
import org.exmaralda.folker.listview.*;
import org.exmaralda.folker.timeview.*;
import org.exmaralda.folker.utilities.Constants;
import org.exmaralda.partitureditor.sound.*;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.Inet4Address;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.border.EtchedBorder;
import org.exmaralda.folker.actions.fileactions.OpenRecentAction;
import org.exmaralda.folker.actions.fileactions.OutputAction;
import org.exmaralda.folker.gui.MaskDialog;
import org.exmaralda.folker.gui.MaskEntryDialog;
import org.exmaralda.folker.gui.NewLogEntryDialog;
import org.exmaralda.folker.gui.QuickTranscriptionDialog;
import org.exmaralda.folker.gui.VirtualKey;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.folker.matchlist.MatchListDialog;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.PreferencesUtilities;
import org.exmaralda.folker.videopanel.VideoPanel;
import org.exmaralda.masker.MaskFileDialog;
import org.exmaralda.masker.MaskTimeCreator;
import org.exmaralda.masker.Masker;
import org.exmaralda.masker.MaskerProgressDialog;
import org.exmaralda.masker.WavFileException;
import org.exmaralda.partitureditor.interlinearText.PageOutputParameters;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaraldaswing.ModifyAbsoluteTimesDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.partiture.editActions.CopyTextAction;
import org.exmaralda.partitureditor.partiture.eventActions.DeleteEventAction;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;


/**
 *
 * @author thomas
 */
public final class ApplicationControl extends AbstractTimeviewPartiturPlayerControl implements  TimeSelectionListener,
                                                                                    ListSelectionListener,
                                                                                    TableModelListener,
                                                                                    PlayableListener,
                                                                                    ChangeListener,
                                                                                    ComponentListener,
                                                                                    PartitureTableListener,
                                                                                    AdjustmentListener,
                                                                                    ContributionTextPaneListener,
                                                                                    WindowListener
                                                                                    /*CellEditorListener*/ {
    
        
    public int PARSE_LEVEL = 2; // Minimaltranskript 
    public boolean NORMALIZE_WHITESPACE = true; //whether or not to normalize whitespace before saving
    public boolean UPDATE_PAUSES = true; //whether or not to update pauses before saving
    public boolean AUTO_ASSIGN_SPEAKER = true; //whether or not to automatically assign the previous speaker when appending segments 

    boolean IS_SAVING_IN_BACKGROUND = false;

    int selectedPanelIndex = 0;
    
    ApplicationFrame applicationFrame;
    
    EventListTableModel eventListTableModel;
    ContributionListTableModel contributionListTableModel;
    
    EventListTable eventListTable;
    ContributionListTable contributionListTable;
    public ContributionTextPane contributionTextPane;
    
    public TranscriptionHead transcriptionHead = new TranscriptionHead();

    AbstractParser parser = new GATParser(Constants.getAlphabetLanguage());
    
    JScrollPane timeViewScrollPane;
    //ChangeZoomDialog changeZoomDialog;

    QuickTranscriptionDialog quickTranscriptionDialog;
    MatchListDialog matchListDialog;
    public MaskDialog maskDialog;
    
    VideoPanel videoPanel;
    

    org.exmaralda.folker.actions.fileactions.NewAction newAction;
    org.exmaralda.folker.actions.fileactions.OpenAction openAction;
    org.exmaralda.folker.actions.fileactions.AppendTranscriptionAction appendTranscriptionAction;
    org.exmaralda.folker.actions.fileactions.MergeTranscriptionsAction mergeTranscriptionsAction;
    org.exmaralda.folker.actions.fileactions.SplitTranscriptionAction splitTranscriptionAction;
    org.exmaralda.folker.actions.fileactions.SaveAction saveAction;
    org.exmaralda.folker.actions.fileactions.SaveAsAction saveAsAction;
    org.exmaralda.folker.actions.fileactions.ImportAction importAction;
    org.exmaralda.folker.actions.fileactions.ExportAction exportAction;
    org.exmaralda.folker.actions.fileactions.OutputAction outputAction;
    org.exmaralda.folker.actions.fileactions.ExitAction exitAction;
    // ---------------------------
    org.exmaralda.folker.actions.editactions.CopyAction copyAction;
    org.exmaralda.folker.actions.editactions.SearchAction searchAction;
    org.exmaralda.folker.actions.editactions.ReplaceAction replaceAction;
    org.exmaralda.folker.actions.editactions.EditPreferencesAction editPreferencesAction;
    // ---------------------------
    org.exmaralda.folker.actions.transcriptionactions.EditSpeakersAction editSpeakersAction;
    org.exmaralda.folker.actions.transcriptionactions.EditRecordingAction editRecordingAction;
    // ---------------------------
    org.exmaralda.folker.actions.transcriptionactions.FillGapsAction fillGapsAction;
    org.exmaralda.folker.actions.transcriptionactions.UpdatePausesAction updatePausesAction;
    org.exmaralda.folker.actions.transcriptionactions.RemovePauseSpeakerAssignmentAction removePauseSpeakerAssignmentAction;
    org.exmaralda.folker.actions.transcriptionactions.NormalizeWhiteSpaceAction normalizeWhitespaceAction;
    org.exmaralda.folker.actions.transcriptionactions.MinimizeAction minimizeAction;
    org.exmaralda.folker.actions.transcriptionactions.ModifyTimesAction modifyTimesAction;
    // ---------------------------
    org.exmaralda.folker.actions.transcriptionactions.EditTranscriptionLogAction editTranscriptionLogAction;
    org.exmaralda.folker.actions.transcriptionactions.EditMaskAction editMaskAction;
    org.exmaralda.folker.actions.transcriptionactions.MaskAction maskAction;
    org.exmaralda.folker.actions.transcriptionactions.DeleteMaskAction deleteMaskAction;
    // ---------------------------
    org.exmaralda.folker.actions.playeractions.ToggleLoopStateAction toggleLoopStateAction;
    // ---------------------------
    org.exmaralda.folker.actions.eventviewactions.AddEventAction addEventAction;
    org.exmaralda.folker.actions.eventviewactions.AddMaskAction addMaskAction;
    org.exmaralda.folker.actions.eventviewactions.AppendEventAction appendEventAction;
    org.exmaralda.folker.actions.eventviewactions.RemoveEventAction removeEventAction;
    org.exmaralda.folker.actions.eventviewactions.TimestampEventAction timestampEventAction;
    org.exmaralda.folker.actions.eventviewactions.SplitEventInListAction splitEventInListAction;
    org.exmaralda.folker.actions.eventviewactions.MergeEventsInListAction mergeEventsInListAction;
    org.exmaralda.folker.actions.eventviewactions.InsertPauseAction insertPauseAction;
    org.exmaralda.folker.actions.eventviewactions.NextErrorAction nextErrorAction;
    // ---------------------------
    org.exmaralda.folker.actions.eventviewactions.SplitInContributionAction splitInContributionAction;
    // ---------------------------
    org.exmaralda.folker.actions.helpactions.DisplayKeyboardShortcutsAction displayKeyboardShortcutsAction;
    //org.exmaralda.folker.actions.playeractions.RescueAction rescueAction;

    
    Date editStart = new Date();
    public String currentFilePath = null;
    public String currentMediaPath = null;
    Vector<File> recentFiles = new Vector<File>();

    String CHECK_REGEX_MINIMAL_EVENT = ".*";
    String CHECK_REGEX_MINIMAL_CONTRIBUTION = ".*";
    String CHECK_REGEX_BASIC_EVENT = ".*";
    String CHECK_REGEX_BASIC_CONTRIBUTION = ".*";
    String NO_SPEAKER_CHECK_REGEX_BASIC_CONTRIBUTION = ".*";

    public boolean DOCUMENT_CHANGED = false;

    ActionListener virtualKeyListener;
    private JButton editRecordingToolbarButton;
            
    /** Creates a new instance of ApplicationControl */
    public ApplicationControl(ApplicationFrame af) {
        super(af, new WaveFormViewer(), new PartitureTableWithActions(af, false), null);

        // partitur configurations
        partitur.tablePopupMenu.configureForFolker();
        partitur.eventPopupMenu.configureForFolker();
        partitur.undoEnabled = false;
        ((CopyTextAction)(partitur.copyTextAction)).markOverlaps = true;
        ((DeleteEventAction)(partitur.deleteEventAction)).safetyCheck = true;
        partitur.pausePrefix= "(";
        partitur.pauseSuffix= ") ";
        partitur.pauseDigits = 2;
        partitur.pauseDecimalComma = false;
        partitur.rtfParameters.setPaperMeasures(PageOutputParameters.DINA4_VERTICAL);

        
        try {
            PatternReader pr = new PatternReader("/org/exmaralda/folker/data/Patterns.xml");
            CHECK_REGEX_MINIMAL_EVENT = pr.getPattern(2, "GAT_EVENT", Constants.getAlphabetLanguage());
            System.out.println("CHECK_REGEX " + CHECK_REGEX_MINIMAL_EVENT);
            CHECK_REGEX_MINIMAL_CONTRIBUTION = pr.getPattern(2, "GAT_CONTRIBUTION", Constants.getAlphabetLanguage());
            CHECK_REGEX_BASIC_EVENT = pr.getPattern(3, "GAT_EVENT", Constants.getAlphabetLanguage());
            CHECK_REGEX_BASIC_CONTRIBUTION = pr.getPattern(3, "GAT_CONTRIBUTION", Constants.getAlphabetLanguage());
            NO_SPEAKER_CHECK_REGEX_BASIC_CONTRIBUTION = pr.getPattern(3, "GAT_NO_SPEAKER_CONTRIBUTION", Constants.getAlphabetLanguage());
        } catch (Exception ex) {
            // do nothing
            ex.printStackTrace();
        }


        applicationFrame = af;
        applicationFrame.addComponentListener(this);
        applicationFrame.addWindowListener(this);
        
        //timeViewer = new WaveFormViewer();

        eventListTable = new EventListTable();
        //eventListTable.setCheckRegex(checkRegex1);

        contributionListTable = new ContributionListTable();
        //contributionListTable.setCheckRegex(checkRegex2);
        contributionTextPane = new ContributionTextPane();
        
        //partitur = new PartitureTableWithActions(af, false);
        
        changeZoomDialog = new ChangeZoomDialog(applicationFrame, false);
        changeZoomDialog.zoomLevelSlider.addChangeListener(this);
        changeZoomDialog.magnifyLevelSlider.addChangeListener(this);
        
        timeViewScrollPane = new JScrollPane();
        timeViewScrollPane.setViewportView(timeViewer);  
        timeViewScrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
        
        eventListTable.getSelectionModel().addListSelectionListener(this);   
        eventListTable.getColumnModel().getSelectionModel().addListSelectionListener(this);        
        // new 20-12-2012
        //eventListTable.eventTextCellEditor.addCellEditorListener(this);        
        contributionListTable.getSelectionModel().addListSelectionListener(this);   
        contributionTextPane.addContributionTextPaneListener(this);
        
        initMaskDialog();

        EventListTranscription elt = new EventListTranscription(0,0);
        setTranscription(elt);
        
        initPlayer();      
        player.addPlayableListener(timeViewer);
        player.addPlayableListener(this);

        timeViewer.navigationDialog.configureForFolker();

        quickTranscriptionDialog = new QuickTranscriptionDialog(applicationFrame, false);
        matchListDialog = new MatchListDialog(applicationFrame, false);
        
        initActions();
        setGeneralDocumentActionsEnabled(false);

        quickTranscriptionDialog.pack();
        quickTranscriptionDialog.setLocationRelativeTo(applicationFrame);
        quickTranscriptionDialog.addWindowListener(this);
        
        matchListDialog.pack();
        matchListDialog.setLocationRelativeTo(applicationFrame);
        matchListDialog.addWindowListener(this);
        matchListDialog.setApplicationControl(this);
        
        initSearchDialog();
        setFont();

        virtualKeyListener = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton b = (JButton)(e.getSource());
                insertString(b.getText());
            }
        };
        
        initVideoPanel();


    }

    public TranscriptionHead getTranscriptionHead() {
        return transcriptionHead;
    }


    
    public JFrame getFrame(){
        return applicationFrame;
    }
    
    public void updateInfoPanel(){
        applicationFrame.mainPanel.playerLabel.setText(PreferencesUtilities.getProperty("PlayerType", "???"));
        String alphabetLanguage = Constants.getAlphabetLanguage();
        if ("default".equals(alphabetLanguage)){
            applicationFrame.mainPanel.parseLevelLabel.setText(Integer.toString(PARSE_LEVEL));
        } else {
            applicationFrame.mainPanel.parseLevelLabel.setText(Integer.toString(PARSE_LEVEL) + " (" + alphabetLanguage + ")");            
        }
    }

    public void setFont() {
        String fontName = PreferencesUtilities.getProperty("font", "");
        eventListTable.setFont(fontName);
        contributionListTable.setFont(fontName);
        if (fontName.length()>0){
            Font f = new Font(fontName, Font.PLAIN, 13);
            contributionTextPane.setFont(f);
            if ((applicationFrame!=null) && (applicationFrame.mainPanel!=null)){
                applicationFrame.mainPanel.setVirtualKeyFonts(f);
            }
            partitur.getModel().defaultFontName = fontName;
            if (this.selectedPanelIndex==1){
                TierFormat tf = new TierFormat();
                tf.setFontName(fontName);
                partitur.getModel().setFormats(0, partitur.getModel().getNumRows()-1, tf);
            }
        }
    }

    public void insertString(String s){
        switch(selectedPanelIndex){
            case 0 :
                if (!eventListTable.isEditing()) return;
                JTextField textField = (JTextField)(eventListTable.eventTextCellEditor.getComponent());
                textField.replaceSelection(s);
                textField.requestFocus();
                break;
            case 1 :
                if (!partitur.isEditing) return;
                JTextField textField2 = (JTextField)(partitur.getEditingComponent());
                textField2.replaceSelection(s);
                textField2.requestFocus();
                break;
            case 2 :
                if (contributionTextPane==null) return;
                contributionTextPane.replaceSelection(s);
                contributionTextPane.requestFocus();
                break;
        }
    }
    
    void insertGATComment() {
        // check if something in an editor is selected
        switch(selectedPanelIndex){
            case 0 :
                if (!eventListTable.isEditing()) return;
                JTextField textField = (JTextField)(eventListTable.eventTextCellEditor.getComponent());
                if (textField.getSelectedText()==null || textField.getSelectedText().trim().length()==0) return;
                while (textField.getSelectedText().startsWith(" ")){textField.setSelectionStart(textField.getSelectionStart()+1);}
                while (textField.getSelectedText().endsWith(" ")){textField.setSelectionEnd(textField.getSelectionEnd()-1);}
                String newText = "<<xxx> " + textField.getSelectedText() + " >";
                int position = textField.getSelectionStart();
                textField.requestFocus();
                textField.replaceSelection(newText);
                textField.setSelectionStart(position+2);
                textField.setSelectionEnd(position+5);                   
                break;
            case 1 :
                if (!partitur.isEditing) return;
                JTextField textField2 = (JTextField)(partitur.getEditingComponent());
                if (textField2.getSelectedText()==null || textField2.getSelectedText().trim().length()==0) return;
                while (textField2.getSelectedText().startsWith(" ")){textField2.setSelectionStart(textField2.getSelectionStart()+1);}
                while (textField2.getSelectedText().endsWith(" ")){textField2.setSelectionEnd(textField2.getSelectionEnd()-1);}
                String newText2 = "<<xxx> " + textField2.getSelectedText() + " >";
                int position2 = textField2.getSelectionStart();
                textField2.requestFocus();
                textField2.replaceSelection(newText2);
                textField2.setSelectionStart(position2+2);
                textField2.setSelectionEnd(position2+5);                   
                break;
            case 2 :
                if (contributionTextPane==null) return;
                if (contributionTextPane.getSelectedText()==null || contributionTextPane.getSelectedText().trim().length()==0) return;
                while (contributionTextPane.getSelectedText().startsWith(" ")){contributionTextPane.setSelectionStart(contributionTextPane.getSelectionStart()+1);}
                while (contributionTextPane.getSelectedText().endsWith(" ")){contributionTextPane.setSelectionEnd(contributionTextPane.getSelectionEnd()-1);}
                String newText3 = "<<xxx> " + contributionTextPane.getSelectedText() + " >";
                int position3 = contributionTextPane.getSelectionStart();
                contributionTextPane.requestFocus();
                contributionTextPane.replaceSelection(newText3);
                contributionTextPane.setSelectionStart(position3+2);
                contributionTextPane.setSelectionEnd(position3+5);                   
                break;
        }
        
        // ask for the comment string
        // or not?
                
    }
    
    void insertAccent() {
        switch(selectedPanelIndex){
            case 0 :
                if (!eventListTable.isEditing()) return;
                JTextField textField = (JTextField)(eventListTable.eventTextCellEditor.getComponent());
                if (textField.getSelectedText()==null || textField.getSelectedText().trim().length()==0) return;
                String newText = textField.getSelectedText().toUpperCase();
                textField.requestFocus();
                textField.replaceSelection(newText);
                break;
            case 1 :
                if (!partitur.isEditing) return;
                JTextField textField2 = (JTextField)(partitur.getEditingComponent());
                if (textField2.getSelectedText()==null || textField2.getSelectedText().trim().length()==0) return;
                String newText2 = textField2.getSelectedText().toUpperCase();
                textField2.requestFocus();
                textField2.replaceSelection(newText2);
                break;
            case 2 :
                if (contributionTextPane==null) return;
                if (contributionTextPane.getSelectedText()==null || contributionTextPane.getSelectedText().trim().length()==0) return;
                String newText3 = contributionTextPane.getSelectedText().toUpperCase();
                contributionTextPane.requestFocus();
                contributionTextPane.replaceSelection(newText3);
                break;
        }
    }

    void insertStrongAccent() {
        switch(selectedPanelIndex){
            case 0 :
                if (!eventListTable.isEditing()) return;
                JTextField textField = (JTextField)(eventListTable.eventTextCellEditor.getComponent());
                if (textField.getSelectedText()==null || textField.getSelectedText().trim().length()==0) return;
                String newText = "!" + textField.getSelectedText().toUpperCase().replaceAll("\\!", "") + "!";;
                textField.requestFocus();
                textField.replaceSelection(newText);
                break;
            case 1 :
                if (!partitur.isEditing) return;
                JTextField textField2 = (JTextField)(partitur.getEditingComponent());
                if (textField2.getSelectedText()==null || textField2.getSelectedText().trim().length()==0) return;
                String newText2 = "!" + textField2.getSelectedText().toUpperCase().replaceAll("\\!", "") + "!";
                textField2.requestFocus();
                textField2.replaceSelection(newText2);
                break;
            case 2 :
                if (contributionTextPane==null) return;
                if (contributionTextPane.getSelectedText()==null || contributionTextPane.getSelectedText().trim().length()==0) return;
                String newText3 = "!" + contributionTextPane.getSelectedText().toUpperCase().replaceAll("\\!", "") + "!";
                contributionTextPane.requestFocus();
                contributionTextPane.replaceSelection(newText3);
                break;
        }
    }


    void removeAccent() {
        switch(selectedPanelIndex){
            case 0 :
                if (!eventListTable.isEditing()) return;
                JTextField textField = (JTextField)(eventListTable.eventTextCellEditor.getComponent());
                if (textField.getSelectedText()==null || textField.getSelectedText().trim().length()==0) return;
                String newText = textField.getSelectedText().toLowerCase().replaceAll("\\!", "");
                textField.requestFocus();
                textField.replaceSelection(newText);
                break;
            case 1 :
                if (!partitur.isEditing) return;
                JTextField textField2 = (JTextField)(partitur.getEditingComponent());
                if (textField2.getSelectedText()==null || textField2.getSelectedText().trim().length()==0) return;
                String newText2 = textField2.getSelectedText().toLowerCase().replaceAll("\\!", "");
                textField2.requestFocus();
                textField2.replaceSelection(newText2);
                break;
            case 2 :
                if (contributionTextPane==null) return;
                if (contributionTextPane.getSelectedText()==null || contributionTextPane.getSelectedText().trim().length()==0) return;
                String newText3 = contributionTextPane.getSelectedText().toLowerCase().replaceAll("\\!", "");
                contributionTextPane.requestFocus();
                contributionTextPane.replaceSelection(newText3);
                break;
        }
    }
    
    



    
/***************** INITIALISATION ******************************/
    
    public void setParseLevel(int level){
        PARSE_LEVEL = level;
        if (level<2){
            eventListTable.setCheckRegex(".*");
            eventListTableModel.setCheckRegex(".*");
            contributionListTable.setCheckRegex(".*");
            contributionListTableModel.setCheckRegex(".*");
            contributionListTableModel.setNoSpeakerCheckRegex(".*");
        } else if (level==2){
            eventListTable.setCheckRegex(CHECK_REGEX_MINIMAL_EVENT);
            eventListTableModel.setCheckRegex(CHECK_REGEX_MINIMAL_EVENT);
            contributionListTable.setCheckRegex(CHECK_REGEX_MINIMAL_CONTRIBUTION);
            contributionListTableModel.setCheckRegex(CHECK_REGEX_MINIMAL_CONTRIBUTION);
            contributionListTableModel.setNoSpeakerCheckRegex(CHECK_REGEX_MINIMAL_CONTRIBUTION);
        } else if (level==3){
            eventListTable.setCheckRegex(CHECK_REGEX_BASIC_EVENT);
            eventListTableModel.setCheckRegex(CHECK_REGEX_BASIC_EVENT);
            contributionListTable.setCheckRegex(CHECK_REGEX_BASIC_CONTRIBUTION);
            contributionListTableModel.setCheckRegex(CHECK_REGEX_BASIC_CONTRIBUTION);
            contributionListTableModel.setNoSpeakerCheckRegex(NO_SPEAKER_CHECK_REGEX_BASIC_CONTRIBUTION);
        }

        eventListTable.setTimeControlActivated(level>0);
        contributionListTable.setTimeControlActivated(level>0);

        eventListTable.setSyntaxControlActivated(level>1);
        contributionListTable.setSyntaxControlActivated(level>1);

    }



    @Override
    void initActions(){
        super.initActions();

        org.exmaralda.folker.utilities.Constants c = new org.exmaralda.folker.utilities.Constants();
        
        newAction = new org.exmaralda.folker.actions.fileactions.NewAction(this, FOLKERInternationalizer.getString("file_menu.new"), c.getIcon(Constants.NEW_ICON));
        openAction = new org.exmaralda.folker.actions.fileactions.OpenAction(this, FOLKERInternationalizer.getString("file_menu.open"), c.getIcon(Constants.OPEN_ICON));
        // added 07-09-2009
        appendTranscriptionAction = new org.exmaralda.folker.actions.fileactions.AppendTranscriptionAction(this, FOLKERInternationalizer.getString("file_menu.multipart_transcription.continue"), null);
        mergeTranscriptionsAction = new org.exmaralda.folker.actions.fileactions.MergeTranscriptionsAction(this, FOLKERInternationalizer.getString("file_menu.multipart_transcription.merge"), null);
        splitTranscriptionAction = new org.exmaralda.folker.actions.fileactions.SplitTranscriptionAction(this, FOLKERInternationalizer.getString("file_menu.multipart_transcription.split"), null);
        // ****************
        saveAction = new org.exmaralda.folker.actions.fileactions.SaveAction(this, FOLKERInternationalizer.getString("file_menu.save"), c.getIcon(Constants.SAVE_ICON));
        saveAsAction = new org.exmaralda.folker.actions.fileactions.SaveAsAction(this, FOLKERInternationalizer.getString("file_menu.saveAs"), c.getIcon(Constants.SAVE_AS_ICON));
        importAction = new org.exmaralda.folker.actions.fileactions.ImportAction(this, FOLKERInternationalizer.getString("file_menu.import"), c.getIcon(Constants.IMPORT_ICON));
        exportAction = new org.exmaralda.folker.actions.fileactions.ExportAction(this, FOLKERInternationalizer.getString("file_menu.export"), c.getIcon(Constants.EXPORT_ICON));
        outputAction = new org.exmaralda.folker.actions.fileactions.OutputAction(this, FOLKERInternationalizer.getString("file_menu.output"), c.getIcon(Constants.OUTPUT_ICON));
        exitAction = new org.exmaralda.folker.actions.fileactions.ExitAction(this, FOLKERInternationalizer.getString("file_menu.exit"), null);
        // -----------------
        copyAction = new org.exmaralda.folker.actions.editactions.CopyAction(this, FOLKERInternationalizer.getString("edit_menu.copy"), c.getIcon(Constants.COPY_ICON));
        searchAction = new org.exmaralda.folker.actions.editactions.SearchAction(this, FOLKERInternationalizer.getString("edit_menu.search"), c.getIcon(Constants.SEARCH_ICON));
        replaceAction = new org.exmaralda.folker.actions.editactions.ReplaceAction(this, FOLKERInternationalizer.getString("edit_menu.replace"), c.getIcon(Constants.REPLACE_ICON));
        editPreferencesAction = new org.exmaralda.folker.actions.editactions.EditPreferencesAction(this, FOLKERInternationalizer.getString("edit_menu.preferences"), c.getIcon(Constants.EDIT_PREFERENCES_ICON));
        // -----------------
        editSpeakersAction = new org.exmaralda.folker.actions.transcriptionactions.EditSpeakersAction(this, FOLKERInternationalizer.getString("transcription_menu.speakers"), c.getIcon(Constants.EDIT_SPEAKERS_ICON));
        editRecordingAction = new org.exmaralda.folker.actions.transcriptionactions.EditRecordingAction(this, FOLKERInternationalizer.getString("transcription_menu.recording"), c.getIcon(Constants.EDIT_RECORDING_ICON));
        // -----------------
        editTranscriptionLogAction = new org.exmaralda.folker.actions.transcriptionactions.EditTranscriptionLogAction(this, FOLKERInternationalizer.getString("transcription_menu.transcriptionlog"), null);
        editMaskAction = new org.exmaralda.folker.actions.transcriptionactions.EditMaskAction(this, FOLKERInternationalizer.getString("transcription_menu.editmask"), null);
        maskAction = new org.exmaralda.folker.actions.transcriptionactions.MaskAction(this, FOLKERInternationalizer.getString("transcription_menu.mask"), null);
        deleteMaskAction = new org.exmaralda.folker.actions.transcriptionactions.DeleteMaskAction(this, FOLKERInternationalizer.getString("transcription_menu.deletemask"), null);
        // -----------------
        fillGapsAction = new org.exmaralda.folker.actions.transcriptionactions.FillGapsAction(this, FOLKERInternationalizer.getString("transcription_menu.fillGaps"), c.getIcon(Constants.FILL_GAPS_ICON));
        normalizeWhitespaceAction = new org.exmaralda.folker.actions.transcriptionactions.NormalizeWhiteSpaceAction(this,FOLKERInternationalizer.getString("transcription_menu.whiteSpace"), null);
        minimizeAction = new org.exmaralda.folker.actions.transcriptionactions.MinimizeAction(this,FOLKERInternationalizer.getString("transcription_menu.minimize"), null);
        updatePausesAction = new org.exmaralda.folker.actions.transcriptionactions.UpdatePausesAction(this,FOLKERInternationalizer.getString("transcription_menu.pauses"), null);
        removePauseSpeakerAssignmentAction = new org.exmaralda.folker.actions.transcriptionactions.RemovePauseSpeakerAssignmentAction(this,FOLKERInternationalizer.getString("transcription_menu.removePauseSpeaker"), null);
        modifyTimesAction = new org.exmaralda.folker.actions.transcriptionactions.ModifyTimesAction(this,FOLKERInternationalizer.getString("transcription_menu.modifyTimes"), null);
        // -----------------
        toggleLoopStateAction = new org.exmaralda.folker.actions.playeractions.ToggleLoopStateAction(this, "Loop", null);
        // -----------------
        addEventAction = new org.exmaralda.folker.actions.eventviewactions.AddEventAction(this,"",c.getIcon(Constants.ADD_EVENT_ICON));
        addMaskAction = new org.exmaralda.folker.actions.eventviewactions.AddMaskAction(this,"",c.getIcon(Constants.ADD_MASK_ICON));
        appendEventAction = new org.exmaralda.folker.actions.eventviewactions.AppendEventAction(this,"",c.getIcon(Constants.APPEND_EVENT_ICON));
        removeEventAction = new org.exmaralda.folker.actions.eventviewactions.RemoveEventAction(this,"",c.getIcon(Constants.REMOVE_EVENT_ICON));
        timestampEventAction = new org.exmaralda.folker.actions.eventviewactions.TimestampEventAction(this,"",c.getIcon(Constants.TIMESTAMP_EVENT_ICON));
        splitEventInListAction = new org.exmaralda.folker.actions.eventviewactions.SplitEventInListAction(this,"",c.getIcon(Constants.SPLIT_EVENT_ICON));
        mergeEventsInListAction = new org.exmaralda.folker.actions.eventviewactions.MergeEventsInListAction(this,"",c.getIcon(Constants.MERGE_EVENTS_ICON));
        insertPauseAction = new org.exmaralda.folker.actions.eventviewactions.InsertPauseAction(this,"",c.getIcon(Constants.INSERT_PAUSE_ICON));
        nextErrorAction = new org.exmaralda.folker.actions.eventviewactions.NextErrorAction(this,"",c.getIcon(Constants.NEXT_ERROR_ICON));
        // -----------------
        splitInContributionAction = new org.exmaralda.folker.actions.eventviewactions.SplitInContributionAction(this,"",c.getIcon(Constants.SPLIT_EVENT_ICON));
        // -----------------
        displayKeyboardShortcutsAction  = new org.exmaralda.folker.actions.helpactions.DisplayKeyboardShortcutsAction(this, FOLKERInternationalizer.getString("help_menu.keyboard"), null);

    }
    
    void assignActions(){
        JMenuItem jmi1 = applicationFrame.fileMenu.add(newAction);
        jmi1.setAccelerator(KeyStroke.getKeyStroke("control N"));
        JMenuItem jmi2 = applicationFrame.fileMenu.add(openAction);
        jmi2.setAccelerator(KeyStroke.getKeyStroke("control O"));
        JMenuItem jmi3 = applicationFrame.fileMenu.add(saveAction);
        jmi3.setAccelerator(KeyStroke.getKeyStroke("control S"));
        applicationFrame.fileMenu.add(saveAsAction);
        applicationFrame.fileMenu.addSeparator();

        JMenu multiPartMenu = new JMenu(FOLKERInternationalizer.getString("file_menu.multipart_transcription"));
        multiPartMenu.add(appendTranscriptionAction);
        multiPartMenu.add(mergeTranscriptionsAction);
        multiPartMenu.add(splitTranscriptionAction);
        applicationFrame.fileMenu.add(multiPartMenu);
        applicationFrame.fileMenu.addSeparator();

        applicationFrame.fileMenu.add(importAction);
        applicationFrame.fileMenu.add(exportAction);
        applicationFrame.fileMenu.addSeparator();
        applicationFrame.fileMenu.add(outputAction);
        applicationFrame.fileMenu.addSeparator();
        // after this separator go the actions for opening recent files
        
        applicationFrame.editMenu.add(copyAction);
        applicationFrame.editMenu.addSeparator();
        applicationFrame.editMenu.add(searchAction);
        applicationFrame.editMenu.add(replaceAction);
        applicationFrame.editMenu.addSeparator();
        applicationFrame.editMenu.add(editPreferencesAction);
        
        applicationFrame.viewMenu.addSeparator();
        applicationFrame.viewMenu.add(editMaskAction);
        if (Constants.isFolkContext()){
            applicationFrame.viewMenu.add(editTranscriptionLogAction);           
        }

        
        applicationFrame.transcriptionMenu.add(editSpeakersAction);
        applicationFrame.transcriptionMenu.add(editRecordingAction);
        applicationFrame.transcriptionMenu.addSeparator();
        applicationFrame.transcriptionMenu.add(fillGapsAction);
        applicationFrame.transcriptionMenu.add(normalizeWhitespaceAction);
        applicationFrame.transcriptionMenu.add(minimizeAction);
        applicationFrame.transcriptionMenu.add(updatePausesAction);
        applicationFrame.transcriptionMenu.add(removePauseSpeakerAssignmentAction);
        applicationFrame.transcriptionMenu.add(modifyTimesAction);
        modifyTimesAction.setEnabled(false);
        //----------------
        applicationFrame.transcriptionMenu.addSeparator();
        applicationFrame.transcriptionMenu.add(maskAction);           
        applicationFrame.transcriptionMenu.add(deleteMaskAction);           
        
        applicationFrame.helpMenu.add(displayKeyboardShortcutsAction);
        //applicationFrame.helpMenu.addSeparator();
        //applicationFrame.helpMenu.add(rescueAction);

        // added 19-05-2009
        boolean useControl = PreferencesUtilities.getBooleanProperty("use-control", false);
        String modifier = "";
        if (useControl) modifier = FOLKERInternationalizer.getString("misc.ctrl");

        playSelectionAction.setEnabled(false);
        applicationFrame.mainPanel.playSelectionButton.setAction(playSelectionAction);
        applicationFrame.mainPanel.playSelectionButton.setToolTipText(FOLKERInternationalizer.getString("player.playSelection") + modifier + "F3)");
        loopSelectionAction.setEnabled(false);
        applicationFrame.mainPanel.loopSelectionButton.setAction(loopSelectionAction);
        applicationFrame.mainPanel.loopSelectionButton.setToolTipText(FOLKERInternationalizer.getString("player.loopSelection"));

        playFirstSecondBeforeSelectionAction.setEnabled(false);
        applicationFrame.mainPanel.playLastSecondBeforeSelectionButton.setAction(playFirstSecondBeforeSelectionAction);
        applicationFrame.mainPanel.playLastSecondBeforeSelectionButton.setToolTipText(FOLKERInternationalizer.getString("player.lastSecondBefore"));

        playFirstSecondOfSelectionAction.setEnabled(false);
        applicationFrame.mainPanel.playFirstSecondOfSelectionButton.setAction(playFirstSecondOfSelectionAction);
        applicationFrame.mainPanel.playFirstSecondOfSelectionButton.setToolTipText(FOLKERInternationalizer.getString("player.firstSecond"));

        playLastSecondOfSelectionAction.setEnabled(false);
        applicationFrame.mainPanel.playLastSecondOfSelectionButton.setAction(playLastSecondOfSelectionAction);
        applicationFrame.mainPanel.playLastSecondOfSelectionButton.setToolTipText(FOLKERInternationalizer.getString("player.lastSecond"));

        playFirstSecondAfterSelectionAction.setEnabled(false);
        applicationFrame.mainPanel.playFirstSecondAfterSelectionButton.setAction(playFirstSecondAfterSelectionAction);
        applicationFrame.mainPanel.playFirstSecondAfterSelectionButton.setToolTipText(FOLKERInternationalizer.getString("player.firstSecondAfter"));

        playAction.setEnabled(false);
        applicationFrame.mainPanel.playButton.setAction(playAction);
        applicationFrame.mainPanel.playButton.setToolTipText(FOLKERInternationalizer.getString("player.play") + modifier + "F4)");
        pauseAction.setEnabled(false);
        applicationFrame.mainPanel.pauseButton.setAction(pauseAction);
        applicationFrame.mainPanel.pauseButton.setToolTipText(FOLKERInternationalizer.getString("player.pause") + modifier + "F5)");
        stopAction.setEnabled(false);
        applicationFrame.mainPanel.stopButton.setAction(stopAction);
        applicationFrame.mainPanel.stopButton.setToolTipText(FOLKERInternationalizer.getString("player.stop") + modifier + "F6)");

        playNextSegmentAction.setEnabled(false);
        applicationFrame.mainPanel.playNextSegmentButton.setAction(playNextSegmentAction);
        applicationFrame.mainPanel.playNextSegmentButton.setToolTipText(FOLKERInternationalizer.getString("player.playNextSegment"));
        
        //applicationFrame.mainPanel.loopCheckBox.setAction(toggleLoopStateAction);
        //applicationFrame.mainPanel.loopCheckBox.setToolTipText("Abspielschleife an-/ausschalten");

        
        applicationFrame.mainPanel.zoomToggleButton.setAction(changeZoomAction);
        applicationFrame.mainPanel.zoomToggleButton.setToolTipText(FOLKERInternationalizer.getString("waveform.zoom"));
        shiftSelectionAction.setEnabled(false);
        applicationFrame.mainPanel.shiftSelectionButton.setAction(shiftSelectionAction);
        applicationFrame.mainPanel.shiftSelectionButton.setToolTipText(FOLKERInternationalizer.getString("waveform.shiftSelection"));
        detachSelectionAction.setEnabled(false);
        applicationFrame.mainPanel.detachSelectionButton.setAction(detachSelectionAction);
        applicationFrame.mainPanel.detachSelectionButton.setToolTipText(FOLKERInternationalizer.getString("waveform.detachSelection"));
        
        applicationFrame.mainPanel.navigateButton.setAction(navigateAction);
        applicationFrame.mainPanel.navigateButton.setToolTipText(FOLKERInternationalizer.getString("waveform.navigation"));

        decreaseSelectionStartAction.setEnabled(false);
        increaseSelectionStartAction.setEnabled(false);
        decreaseSelectionEndAction.setEnabled(false);
        increaseSelectionEndAction.setEnabled(false);
                
        applicationFrame.applicationToolBar.add(newAction).setToolTipText(FOLKERInternationalizer.getString("file_menu.new"));
        applicationFrame.applicationToolBar.add(openAction).setToolTipText(FOLKERInternationalizer.getString("file_menu.open"));
        applicationFrame.applicationToolBar.add(saveAction).setToolTipText(FOLKERInternationalizer.getString("file_menu.save"));
        applicationFrame.applicationToolBar.add(saveAsAction).setToolTipText(FOLKERInternationalizer.getString("file_menu.saveAs"));
        applicationFrame.applicationToolBar.add(new JToolBar.Separator());
        applicationFrame.applicationToolBar.add(outputAction).setToolTipText(FOLKERInternationalizer.getString("file_menu.output"));
        applicationFrame.applicationToolBar.add(new JToolBar.Separator());
        applicationFrame.applicationToolBar.add(copyAction).setToolTipText(FOLKERInternationalizer.getString("edit_menu.copy"));
        applicationFrame.applicationToolBar.add(new JToolBar.Separator());
        applicationFrame.applicationToolBar.add(searchAction).setToolTipText(FOLKERInternationalizer.getString("edit_menu.search"));
        applicationFrame.applicationToolBar.add(replaceAction).setToolTipText(FOLKERInternationalizer.getString("edit_menu.replace"));
        applicationFrame.applicationToolBar.add(new JToolBar.Separator());
        applicationFrame.applicationToolBar.add(editPreferencesAction).setToolTipText(FOLKERInternationalizer.getString("edit_menu.preferences"));
        applicationFrame.applicationToolBar.add(new JToolBar.Separator());
        applicationFrame.applicationToolBar.add(editSpeakersAction).setToolTipText(FOLKERInternationalizer.getString("transcription_menu.speakers"));
        
        editRecordingToolbarButton = applicationFrame.applicationToolBar.add(editRecordingAction);
        editRecordingToolbarButton.setToolTipText(FOLKERInternationalizer.getString("transcription_menu.recording"));
        editRecordingToolbarButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        editRecordingToolbarButton.setBorder(new EtchedBorder());
        
        addEventAction.setEnabled(false);
        applicationFrame.mainPanel.addEventButton.setAction(addEventAction);
        applicationFrame.mainPanel.addEventButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.newSegment"));
        
        addMaskAction.setEnabled(false);
        applicationFrame.mainPanel.addMaskButton.setAction(addMaskAction);
        applicationFrame.mainPanel.addMaskButton.setToolTipText(FOLKERInternationalizer.getString("dialog.maskEntry"));

        appendEventAction.setEnabled(false);
        applicationFrame.mainPanel.appendEventButton.setAction(appendEventAction);
        applicationFrame.mainPanel.appendEventButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.appendSegment"));

        removeEventAction.setEnabled(false);
        applicationFrame.mainPanel.removeEventButton.setAction(removeEventAction);
        applicationFrame.mainPanel.removeEventButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.removeSegment"));
        
        timestampEventAction.setEnabled(false);
        applicationFrame.mainPanel.timestampEventButton.setAction(timestampEventAction);
        applicationFrame.mainPanel.timestampEventButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.assignTime"));
        
        splitEventInListAction.setEnabled(false);
        applicationFrame.mainPanel.splitEventInListButton.setAction(splitEventInListAction);
        applicationFrame.mainPanel.splitEventInListButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.splitSegment"));

        mergeEventsInListAction.setEnabled(false);
        applicationFrame.mainPanel.mergeEventsInListButton.setAction(mergeEventsInListAction);
        applicationFrame.mainPanel.mergeEventsInListButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.mergeSegments"));
                
        //fillGapsAction.setEnabled(false);
        //applicationFrame.mainPanel.fillGapsButton.setAction(fillGapsAction);

        insertPauseAction.setEnabled(false);
        applicationFrame.mainPanel.insertPauseButton.setAction(insertPauseAction);
        applicationFrame.mainPanel.insertPauseButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.insertPause"));
        
        nextErrorAction.setEnabled(false);
        applicationFrame.mainPanel.nextErrorButton.setAction(nextErrorAction);
        applicationFrame.mainPanel.nextErrorButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.nextError"));
        applicationFrame.mainPanel.nextErrorButton2.setAction(nextErrorAction);
        applicationFrame.mainPanel.nextErrorButton2.setToolTipText(FOLKERInternationalizer.getString("segmentactions.nextError"));

        applicationFrame.mainPanel.splitInContributionButton.setAction(splitInContributionAction);
        applicationFrame.mainPanel.splitInContributionButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.splitSegment"));
        
        applicationFrame.mainPanel.splitEventButton.setAction(partitur.splitAction);
        applicationFrame.mainPanel.splitEventButton.setText(null);        
        applicationFrame.mainPanel.splitEventButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.splitSegment"));
        applicationFrame.mainPanel.mergeEventsButton.setAction(partitur.mergeAction);
        applicationFrame.mainPanel.mergeEventsButton.setText(null);        
        applicationFrame.mainPanel.mergeEventsButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.mergeSegments"));
        applicationFrame.mainPanel.shiftLeftButton.setAction(partitur.shiftLeftAction);
        applicationFrame.mainPanel.shiftLeftButton.setText(null);
        applicationFrame.mainPanel.shiftLeftButton.setToolTipText(FOLKERInternationalizer.getString("partituractions.shiftLeft"));
        applicationFrame.mainPanel.shiftRightButton.setAction(partitur.shiftRightAction);
        applicationFrame.mainPanel.shiftRightButton.setText(null);
        applicationFrame.mainPanel.shiftRightButton.setToolTipText(FOLKERInternationalizer.getString("partituractions.shiftRight"));
        applicationFrame.mainPanel.shrinkLeftButton.setAction(partitur.shrinkLeftAction);
        applicationFrame.mainPanel.shrinkLeftButton.setText(null);
        applicationFrame.mainPanel.shrinkLeftButton.setToolTipText(FOLKERInternationalizer.getString("partituractions.shrinkLeft"));
        applicationFrame.mainPanel.shrinkRightButton.setAction(partitur.shrinkRightAction);
        applicationFrame.mainPanel.shrinkRightButton.setText(null);
        applicationFrame.mainPanel.shrinkRightButton.setToolTipText(FOLKERInternationalizer.getString("partituractions.shrinkRight"));

        applicationFrame.mainPanel.extendLeftButton.setAction(partitur.extendLeftAction);
        applicationFrame.mainPanel.extendLeftButton.setText(null);
        applicationFrame.mainPanel.extendLeftButton.setToolTipText(FOLKERInternationalizer.getString("partituractions.extendLeft"));
        applicationFrame.mainPanel.extendRightButton.setAction(partitur.extendRightAction);
        applicationFrame.mainPanel.extendRightButton.setText(null);
        applicationFrame.mainPanel.extendRightButton.setToolTipText(FOLKERInternationalizer.getString("partituractions.extendRight"));

        // added 07-09-2009
        applicationFrame.mainPanel.removeEventInPartiturButton.setAction(partitur.deleteEventAction);
        applicationFrame.mainPanel.removeEventInPartiturButton.setText(null);
        applicationFrame.mainPanel.removeEventInPartiturButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.removeSegment"));
        applicationFrame.mainPanel.removeEventInPartiturButton.setIcon(new Constants().getIcon(Constants.REMOVE_EVENT_ICON));

        // removed 04-03-2010 - this functionality is probably not needed in FOLKER
        // it causes confusion
        //applicationFrame.mainPanel.assignTimesInPartiturButton.setAction(assignTimesAction);
        //applicationFrame.mainPanel.assignTimesInPartiturButton.setText(null);
        //applicationFrame.mainPanel.assignTimesInPartiturButton.setToolTipText("Zeit neu zuweisen");

        // added 26-02-2010
        applicationFrame.mainPanel.insertPauseInPartiturButton.setAction(partitur.insertPauseAction);
        applicationFrame.mainPanel.insertPauseInPartiturButton.setText(null);
        applicationFrame.mainPanel.insertPauseInPartiturButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.insertPause"));
        applicationFrame.mainPanel.insertPauseInPartiturButton.setIcon(new Constants().getIcon(Constants.INSERT_PAUSE_ICON));

        // added 04-07-2014
        applicationFrame.mainPanel.maskInPartiturButton.setAction(addMaskAction);
        applicationFrame.mainPanel.maskInPartiturButton.setText(null);
        applicationFrame.mainPanel.maskInPartiturButton.setToolTipText(FOLKERInternationalizer.getString("dialog.maskEntry"));
        applicationFrame.mainPanel.maskInPartiturButton.setIcon(new Constants().getIcon(Constants.ADD_MASK_ICON));

        //------
        applicationFrame.mainPanel.addEventInPartiturButton.setAction(addEventAction);
        applicationFrame.mainPanel.addEventInPartiturButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.newSegment"));

        //------
        quickTranscriptionDialog.playSelectionButton.setAction(playSelectionAction);
        quickTranscriptionDialog.playSelectionButton.setToolTipText(FOLKERInternationalizer.getString("player.playSelection2"));
        quickTranscriptionDialog.shiftSelectionButton.setAction(shiftSelectionAction);
        quickTranscriptionDialog.shiftSelectionButton.setToolTipText(FOLKERInternationalizer.getString("waveform.shiftSelection"));
        quickTranscriptionDialog.addEventButton.setAction(addEventAction);
        quickTranscriptionDialog.addEventButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.newSegment"));
        quickTranscriptionDialog.insertPauseButton.setAction(insertPauseAction);
        quickTranscriptionDialog.insertPauseButton.setToolTipText(FOLKERInternationalizer.getString("segmentactions.insertPause"));

        for (Component c : applicationFrame.mainPanel.virtualKeyboardPanel.getComponents()){
            if (c instanceof VirtualKey){
                ((VirtualKey)c).addActionListener(virtualKeyListener);
            }
        }

    }
    
    void assignKeyboardShortcuts(){
        // changed 19-05-2009
        boolean useControl = PreferencesUtilities.getBooleanProperty("use-control", false);
        String modifier = "";
        if (useControl) modifier = "control ";

        Object[][] generalAssignments = {
            {modifier + "F3", "playSelection", playSelectionAction},
            {"shift F3", "playLastSecondOfSelection", playLastSecondOfSelectionAction},
            {"alt F3", "loopSelection", loopSelectionAction},
            {modifier + "F4", "play", playAction},
            // added 23-12-2011
            {"shift F4", "playNextSegment", playNextSegmentAction},
            {modifier + "F5", "pause", pauseAction},
            {modifier + "F6", "stop", stopAction},

            {"alt shift LEFT", "decreaseSelectionStart", decreaseSelectionStartAction},
            {"alt shift RIGHT", "increaseSelectionStart", increaseSelectionStartAction},
            {"alt LEFT", "decreaseSelectionEnd", decreaseSelectionEndAction},
            {"alt RIGHT", "increaseSelectionEnd", increaseSelectionEndAction},

            {"control ENTER", "addEvent", addEventAction},
            {"alt DELETE", "removeEvent", removeEventAction},
            {"alt ENTER", "insertPause", insertPauseAction},
            {"shift ENTER", "appendEvent", appendEventAction},
            {"control SPACE", "shiftSelection", shiftSelectionAction},

            // shortcuts for the virtual keyboard
            // removed for version 1.1.
            /*{"alt 1", "insertBreathe", new InsertStringAction(this, "", null, " ")},
            {"alt 2", "insertGlottalStop", new InsertStringAction(this, "", null, "\u0294")},
            {"alt 3", "insertSegmentSeparator", new InsertStringAction(this, "", null, "|")},
            {"alt 4", "insertDash", new InsertStringAction(this, "", null, "\u2013")},
            {"alt 5", "insertTHSUp", new InsertStringAction(this, "", null, "\u2191")},
            {"alt shift 5", "insertTHSDown", new InsertStringAction(this, "", null, "\u2193")},
            {"alt 6", "insertATHBUp", new InsertStringAction(this, "", null, "\u00B4")},
            {"alt shift 6", "insertATHBDown", new InsertStringAction(this, "", null, "\u0060")},
            {"alt 7", "insertATHBLevel", new InsertStringAction(this, "", null, "\u00AF")},
            {"alt 8", "insertATHBUpDown", new InsertStringAction(this, "", null, "\u02C6")},
            {"alt shift 8", "insertATHBDownUp", new InsertStringAction(this, "", null, "\u02C7")}*/
        };
        processAssignments(generalAssignments, applicationFrame.mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        processAssignments(generalAssignments, applicationFrame.mainPanel, JComponent.WHEN_IN_FOCUSED_WINDOW);
        processAssignments(generalAssignments, eventListTable, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        
        Object[][] eventViewAssignments = {
            {"control 1", "mergeEvent", mergeEventsInListAction},
            {"control 2", "splitEvent", splitEventInListAction},
            {"control E", "nextError", nextErrorAction},
            {"control M", "newMask", addMaskAction}
        };
        processAssignments(eventViewAssignments, applicationFrame.mainPanel.eventViewPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        Object[][] partiturViewAssignments = {
            {"control 1", "mergeEvent", partitur.mergeAction},
            {"control 2", "splitEvent", partitur.splitAction},
            {"control 3", "doubleSplitEvent", partitur.doubleSplitAction},
            {"control shift R", "shiftRight", partitur.shiftRightAction},
            {"control shift L", "shiftLeft", partitur.shiftLeftAction},
            // added 23-12-2011
            //{"PAGE_DOWN", "scrollForward", partitur.scrollForwardsAction},
            //{"control shift RIGHT", "extendRight", partitur.extendRightAction},
            //{"control shift LEFT", "extendLeft", partitur.extendLeftAction},
            {"control alt RIGHT", "shrinkRight", partitur.shrinkRightAction},
            {"control alt LEFT", "shrinkLeft", partitur.shrinkLeftAction}//,            
            //{"control RIGHT", "moveRight", partitur.moveRightAction},
            //{"control LEFT", "moveLeft", partitur.moveLeftAction},
            
        };

        processAssignments(partiturViewAssignments, applicationFrame.mainPanel.partiturViewPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        Object[][] tableAssignments = {
            {"control C", "copy", copyAction}
        };
        processAssignments(tableAssignments, eventListTable, JComponent.WHEN_FOCUSED);
        processAssignments(tableAssignments, contributionListTable, JComponent.WHEN_FOCUSED);

    
    }

    void toggleQuickTranscriptionDialog(boolean showOrDont) {
        quickTranscriptionDialog.setVisible(showOrDont);
        applicationFrame.showQuickTranscriptionCheckBoxMenuItem.setSelected(quickTranscriptionDialog.isShowing());
    }
    
    
    void toggleMatchList(boolean showOrDont) {
        matchListDialog.setVisible(showOrDont);
        applicationFrame.showMatchListCheckBoxMenuItem.setSelected(matchListDialog.isShowing());
    }
    
    void toggleVideoPanel(boolean showOrDont) {
        videoPanel.setVisible(showOrDont);
        applicationFrame.showVideoPanelCheckBoxMenuItem.setSelected(videoPanel.isShowing());
    }
    

    void toggleVirtualKeyboard(boolean showOrDont) {
        applicationFrame.mainPanel.virtualKeyboardPanel.setVisible(showOrDont);
        applicationFrame.showVirtualKeyboardCheckBoxMenuItem.setSelected(applicationFrame.mainPanel.virtualKeyboardPanel.isShowing());
    }

    
    public void setGeneralDocumentActionsEnabled(boolean enabled){
        saveAction.setEnabled(enabled);
        saveAsAction.setEnabled(enabled);
        appendTranscriptionAction.setEnabled(enabled);
        mergeTranscriptionsAction.setEnabled(enabled);
        splitTranscriptionAction.setEnabled(enabled);
        exportAction.setEnabled(enabled);
        outputAction.setEnabled(enabled);
        editSpeakersAction.setEnabled(enabled);                
        editRecordingAction.setEnabled(enabled);
        editTranscriptionLogAction.setEnabled(enabled);
        copyAction.setEnabled(enabled);
        searchAction.setEnabled(enabled);
        replaceAction.setEnabled(enabled);
        changeZoomAction.setEnabled(enabled);
        navigateAction.setEnabled(enabled);
        fillGapsAction.setEnabled(enabled);
        updatePausesAction.setEnabled(enabled);
        nextErrorAction.setEnabled(enabled);
        removePauseSpeakerAssignmentAction.setEnabled(enabled && selectedPanelIndex==0);
        normalizeWhitespaceAction.setEnabled(enabled);
        appendEventAction.setEnabled(enabled);
        eventListTable.setEnabled(enabled);                              
        contributionListTable.setEnabled(enabled);
        
        toggleLoopStateAction.setEnabled(enabled);
        //rescueAction.setEnabled(enabled);
        
        playNextSegmentAction.setEnabled(enabled);
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


    public void initVideoPanel(){
        videoPanel = new VideoPanel(applicationFrame, false);
        timeViewer.addTimeSelectionListener(videoPanel);
        videoPanel.setLocationRelativeTo(applicationFrame);
    }
    
    public void initPlayer(){
        
        // set the default player according to os
        String defaultPlayer = getDefaultPlayer();
        
        // read preferred player from preferences
        String playerType = java.util.prefs.Preferences.userRoot().node(applicationFrame.getPreferencesNode()).get("PlayerType", defaultPlayer);
        System.out.println("Player: " + playerType);
        // make sure that there is no contradiction between preferred player and os
        String os = System.getProperty("os.name").substring(0,3);
        if (playerType.equals("DirectShow-Player") && os.equalsIgnoreCase("mac")){
            playerType = "ELAN-Quicktime-Player";
        }
        if (playerType.equals("ELAN-Quicktime-Player") && os.equalsIgnoreCase("win")){
            playerType = "DirectShow-Player";
        }

        if (playerType.equals("JMF-Player")) {
            player = new JMFPlayer();
        } else if (playerType.equals("DirectShow-Player")) {
            player = new ELANDSPlayer();
        } else if (playerType.equals("JDS-Player")) {
            player = new JDSPlayer();
        } else if (playerType.equals("BAS-Audio-Player")) {
            player = new BASAudioPlayer();
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
        }
        java.util.prefs.Preferences.userRoot().node(applicationFrame.getPreferencesNode()).put("PlayerType", playerType);
    }

    public void rescuePlayer() throws IOException {
        // the player is blocking
        // do anything you can think of to unblock it
        // save women and children

        final ApplicationControl myself = this;

        Thread rescueThread = new Thread(new Runnable(){

            public void run() {
                player.stopPlayback();
                ((AbstractPlayer)player).reset();
                timeViewer.removeSelection();
                timeViewer.reset();
                initPlayer();
                player.addPlayableListener(myself);
                player.addPlayableListener(timeViewer);
                try {
                    player.setSoundFile(currentMediaPath);
                } catch (IOException ex) {
                    myself.displayException(ex);
                }
                timeViewScrollPane.revalidate();
                playerState=PLAYER_IDLE;

                playAction.setEnabled(true);
                stopAction.setEnabled(false);
                pauseAction.setEnabled(false);
                playSelectionAction.setEnabled(false);
                playLastSecondOfSelectionAction.setEnabled(false);
                loopPlay = false;
                timeViewer.setCursorTime(0.0);


                status("Player zur�ckgesetzt");
            }            
        });

        rescueThread.start();
    }

 
    /***************** GETTING TRANSCRIPTION AND MEDIA ******************************/ 
 
    public EventListTranscription getTranscription(){
        return eventListTableModel.getTranscription();
    }
    
    public BasicTranscription getBasicTranscription(){
        if (selectedPanelIndex==1){
            partitur.commitEdit(true);
            return partitur.getModel().getTranscription();
        } else {
            return org.exmaralda.folker.io.EventListTranscriptionConverter.exportBasicTranscription(getTranscription());   
        }
    }
    
    public EventListTableModel getEventListTableModel(){
        return eventListTableModel;
    }
    
    /***************** SETTING TRANSCRIPTION AND MEDIA ******************************/ 
    
    public void newTranscriptionFile(File f){
        applicationFrame.mainPanel.textViewsTabbedPane.setSelectedIndex(0);
        applicationFrame.mainPanel.textViewsTabbedPane.setEnabledAt(1,true);        
        applicationFrame.mainPanel.textViewsTabbedPane.setEnabledAt(2,true);        
        
        String mediaPath = f.getAbsolutePath();
        try {
            setMedia(mediaPath);
        } catch (IOException ex) {
            displayException(ex);            
            return;
        }
        EventListTranscription elt = new EventListTranscription(0,player.getTotalLength()*1000.0);
        // added 05-10-2009
        elt.setMediaPath(mediaPath);
        setTranscription(elt);
        setCurrentFilePath(null);
        
        transcriptionHead = new TranscriptionHead();
        maskDialog.setData();
        
        setGeneralDocumentActionsEnabled(true);   
        timeViewScrollPane.getHorizontalScrollBar().setValue(0);
        adjustVisibleArea();

        resetSelection();

        status(FOLKERInternationalizer.getString("status.new1") + f.getAbsolutePath() + FOLKERInternationalizer.getString("status.new2"));
        
        editStart = new Date();
        
    }

    public void appendTranscription() {
        applicationFrame.mainPanel.textViewsTabbedPane.setSelectedIndex(0);
        applicationFrame.mainPanel.textViewsTabbedPane.setEnabledAt(1,true);
        applicationFrame.mainPanel.textViewsTabbedPane.setEnabledAt(2,true);
        if (getTranscription().getEventlist().getEvents().size()<1){
            displayException(new Exception(FOLKERInternationalizer.getString("error.zerosegments")));
            return;
        }
        try {
            EventListTranscription elt = EventListTranscription.AppendTranscription(getTranscription());

            setTranscription(elt);
            setCurrentFilePath(null);

            setGeneralDocumentActionsEnabled(true);
            timeViewScrollPane.getHorizontalScrollBar().setValue(0);
            adjustVisibleArea();

            java.awt.Rectangle bounds = eventListTable.getCellRect(0+eventListTableModel.ADDITIONAL_ROWS,0,true);
            eventListTable.scrollRectToVisible(bounds);
            eventListTable.getSelectionModel().clearSelection();
            eventListTable.requestFocus();
            eventListTable.getSelectionModel().setSelectionInterval(0, 0);
            eventListTable.editCellAt(0, 4);
            eventListTable.getEditorComponent().requestFocus();

            DOCUMENT_CHANGED = true;

            status(FOLKERInternationalizer.getString("status.new1") + elt.getMediaPath() + FOLKERInternationalizer.getString("status.new2"));
        } catch (IOException ex) {
            displayException(ex);
            return;
        }
    }

    public void mergeTranscriptions(File f) {
        setView(0);

        EventListTranscription elt;
        try {
            elt = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXML(f);
        } catch (Exception ex) {
            displayException(ex);
            ex.printStackTrace();
            status(FOLKERInternationalizer.getString("error.reading") + f.getAbsolutePath() + ". ");
            return;
        }

        getTranscription().mergeTranscriptions(elt);

        reset();
        // added 05-10-2009
        DOCUMENT_CHANGED = true;
        status(FOLKERInternationalizer.getString("status.merge1") + f.getAbsolutePath() + FOLKERInternationalizer.getString("status.merge2"));

    }

    public void splitTranscription(File f) {
        double splitTime = 0.0;
        switch (selectedPanelIndex){
            case 0 :
                int i1 = eventListTable.getSelectedRow();
                if ((i1<0)||(i1>=getTranscription().getNumberOfEvents())){
                    displayException(new Exception(FOLKERInternationalizer.getString("error.nosegment")));
                    return;
                }
                splitTime = getTranscription().getEventAt(i1).getEndpoint().getTime();
                break;
            case 1:
                int i2 = partitur.selectionStartCol;
                if (i2<0){
                    displayException(new Exception(FOLKERInternationalizer.getString("error.notimepoint")));
                    return;
                }
                org.exmaralda.partitureditor.jexmaralda.Timeline tl = partitur.getModel().getTranscription().getBody().getCommonTimeline();
                splitTime = tl.getTimelineItemAt(Math.min(i2+1, tl.getNumberOfTimelineItems()-1)).getTime() * 1000.0;
                break;
            case 2 :
                int i3 = contributionListTable.getSelectedRow();
                if ((i3<0)||(i3>=getTranscription().getNumberOfContributions())){
                    displayException(new Exception(FOLKERInternationalizer.getString("error.nocontribution")));
                    return;
                }
                splitTime = getTranscription().getContributionAt(i3).getEndpoint().getTime();
                break;
        }

        // added 18-09-2012
        commitEdit();
        applicationFrame.mainPanel.textViewsTabbedPane.setSelectedIndex(0);
        applicationFrame.mainPanel.textViewsTabbedPane.setEnabledAt(1,true);
        applicationFrame.mainPanel.textViewsTabbedPane.setEnabledAt(2,true);
        try {
            EventListTranscription part2 = getTranscription().splitTranscription(splitTime);
            org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.writeXML(part2, f, parser, PARSE_LEVEL);
            this.eventListTableModel.fireTableDataChanged();
            DOCUMENT_CHANGED = true;
            status(FOLKERInternationalizer.getString("status.split1") + f.getAbsolutePath() + FOLKERInternationalizer.getString("status.split2"));

        } catch (SAXException ex) {
            displayException(ex);
            return;
        } catch (JDOMException ex) {
            displayException(ex);
            return;
        } catch (ParserConfigurationException ex) {
            displayException(ex);
            return;
        } catch (TransformerConfigurationException ex) {
            displayException(ex);
            return;
        } catch (TransformerException ex) {
            displayException(ex);
            return;
        } catch (IOException ex) {
            displayException(ex);
            return;
        }

    }

    
    
    public void openTranscriptionFile(File f){
        setView(0);

        EventListTranscription elt;
        String mediaPath;
        try {
            // changed 02-08-2012: zero timeline tolerance, otherwise word aligned DGD transcripts will have time errors
            // elt = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXML(f);
            elt = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXML(f, 0);
            transcriptionHead = new TranscriptionHead(f);
            int howMany = getTranscriptionHead().extractMaskSegments(elt);
            if (howMany>0){
                JOptionPane.showMessageDialog(applicationFrame, "Es wurden " + Integer.toString(howMany) + " Maskierungssegmente\nverschoben.");
            }
            mediaPath = elt.getMediaPath();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex.getMessage().contains("Content is not allowed in prolog")){
                String message = FOLKERInternationalizer.getString("error.opening1") 
                        + "\n" + f.getAbsolutePath() + "\n" +
                        FOLKERInternationalizer.getString("error.opening2")
                        + "\n" + FOLKERInternationalizer.getString("error.opening3") + "\n"
                        + ex.getLocalizedMessage();
                displayException(message);
                ex.printStackTrace();
                status(FOLKERInternationalizer.getString("error.reading") + f.getAbsolutePath() + ". ");
                return;                
            } else {
                displayException(ex);
                ex.printStackTrace();
                status(FOLKERInternationalizer.getString("error.reading") + f.getAbsolutePath() + ". ");
                return;
            }
        }
        if ((recentFiles.isEmpty()) || (!recentFiles.elementAt(0).equals(f))) {
            recentFiles.remove(f);
            recentFiles.add(0, f);
        }

        boolean mediaSet = false;
        while (!mediaSet){
            try {
                setMedia(mediaPath);
                mediaSet = true;
            } catch (IOException ex) {
                int optionChosen = displayRecordingNotFoundDialog(mediaPath, ex);
                if (optionChosen==JOptionPane.NO_OPTION) return;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.recording"));
                fileChooser.setFileFilter(new org.exmaralda.folker.utilities.WaveFileFilter());
                fileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));
                int retValue = fileChooser.showOpenDialog(getFrame());
                if (retValue==JFileChooser.CANCEL_OPTION) return;
                mediaPath = fileChooser.getSelectedFile().getAbsolutePath();
                // added 05-10-2009
                elt.setMediaPath(mediaPath);
                // added 28-09-2010
                DOCUMENT_CHANGED = true;
            }
        }

        setTranscription(elt);
        // added 07-05-2012
        // not clean...
        if ((PARSE_LEVEL>1)){
            normalizeWhitespace();
        }
        
        setCurrentFilePath(f.getAbsolutePath());

        reset();            
        
        editStart = new Date();
        
        status(FOLKERInternationalizer.getString("status.open1") + f.getAbsolutePath() + FOLKERInternationalizer.getString("status.open2"));
        
        if (f.getName().toLowerCase().endsWith(".fln")){
            String message = FOLKERInternationalizer.getString("misc.FLNWarning1") + System.getProperty("line.separator")
                             + FOLKERInternationalizer.getString("misc.FLNWarning2") + System.getProperty("line.separator")
                             + FOLKERInternationalizer.getString("misc.FLNWarning3") + System.getProperty("line.separator")
                             + FOLKERInternationalizer.getString("misc.FLNWarning4");
            JOptionPane.showMessageDialog(this.applicationFrame, message);
        }
    }
    
    public void saveTranscriptionFileAs(File f, boolean checkOverwrite){
        // check if the file exists
        // if yes, ask for user confirmation
        commitEdit();
        if ((checkOverwrite) && (f.exists())){
            int retVal = JOptionPane.showConfirmDialog( applicationFrame, 
                                                        FOLKERInternationalizer.getString("option.fileexists"),
                                                        FOLKERInternationalizer.getString("option.confirmation"),
                                                        JOptionPane.YES_NO_OPTION);
            if (retVal!=JOptionPane.YES_OPTION) return; 
        }  
        
        // add the file to the list of recently used files
        if ((recentFiles.isEmpty()) || (!recentFiles.elementAt(0).equals(f))){
            recentFiles.remove(f);
            recentFiles.add(0, f);        
        }
        
        // get the event list transcription
        EventListTranscription transcription = null;
        if (selectedPanelIndex==1){
            partitur.commitEdit(true);
            transcription = org.exmaralda.folker.io.EventListTranscriptionConverter.
                            importExmaraldaBasicTranscription(partitur.getModel().getTranscription(), 
                            null, null);            
        } else {
           transcription = getTranscription();
        }
        //added 12-05-2009
        if ((PARSE_LEVEL>1) && (UPDATE_PAUSES)){
            updatePauses();
        }
        if ((PARSE_LEVEL>1) && (NORMALIZE_WHITESPACE)){
            normalizeWhitespace();
        }
        transcription.setMediaPath(currentMediaPath);


        // changed 12-05-2009: do the saving in a thread
        // changed 29-05-2009: take care not to close the application while the save thread is running
        final EventListTranscription finalTranscription = transcription;
        final TranscriptionHead finalTranscriptionHead = transcriptionHead;
        final File finalFile = f;
        Thread saveThread = new Thread(new Runnable(){

            public void run() {
                // now this is really weird: the writeXML method first transforms the event list transcription
                // into an EXMARaLDA basic transcription. Then this basic transcription is transformed via a stylesheet
                // into a FOLKER transcription
                try {
                    System.out.println("Start saving...");
                    IS_SAVING_IN_BACKGROUND = true;
                    applicationFrame.mainPanel.progressBar.setVisible(true);
                    saveAction.setEnabled(false);
                    saveAsAction.setEnabled(false);
                    openAction.setEnabled(false);
                    importAction.setEnabled(false);
                    exportAction.setEnabled(false);
                    exitAction.setEnabled(false);
                    status(FOLKERInternationalizer.getString("status.saving") + finalFile.getAbsolutePath() + "...");
                    org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.writeXML(finalTranscription, finalFile, parser, PARSE_LEVEL, finalTranscriptionHead);
                    setCurrentFilePath(finalFile.getAbsolutePath());
                    status(FOLKERInternationalizer.getString("status.saved1") 
                            + finalFile.getAbsolutePath() 
                            + FOLKERInternationalizer.getString("status.saved2"));
                    DOCUMENT_CHANGED = false;
                    eventListTableModel.DOCUMENT_CHANGED = false;
                    contributionListTableModel.DOCUMENT_CHANGED = false;
                    partitur.transcriptionChanged = false;
                    System.out.println("Done saving...");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    displayException(ex);
                    status(FOLKERInternationalizer.getString("status.saveerror") + finalFile.getAbsolutePath() + ".");
                } finally {
                    exitAction.setEnabled(true);
                    openAction.setEnabled(true);
                    importAction.setEnabled(true);
                    exportAction.setEnabled(true);
                    saveAction.setEnabled(true);
                    saveAsAction.setEnabled(true);
                    applicationFrame.mainPanel.progressBar.setVisible(false);
                    IS_SAVING_IN_BACKGROUND = false;
                }
            }

        });

        saveThread.start();
                
    }
    
    
    public void setMedia(String path) throws IOException{
        File tryFile = new File(path);
        String defaultAudioPath = PreferencesUtilities.getProperty("default-audio-path", "");
        String tryPath = path;
        if ((!(tryFile.exists())) && defaultAudioPath!=null && defaultAudioPath.length()>0){
            String name = tryFile.getName();
            File otherTryFile = new File(new File(defaultAudioPath), name);
            if (otherTryFile.exists()){
                tryPath = otherTryFile.getAbsolutePath();
            }
        } 
        ((WaveFormViewer)timeViewer).setSoundFile(tryPath);
        //timeViewer.setPixelsPerSecond(10.0);
        player.setSoundFile(tryPath);            
        timeViewScrollPane.revalidate();
        playerState=PLAYER_IDLE;         
        currentMediaPath = tryPath;
        
        videoPanel.setPreferredPath(currentMediaPath);
        

        editRecordingToolbarButton.setText(new File(path).getName());
        editRecordingToolbarButton.setToolTipText(FOLKERInternationalizer.getString("transcription_menu.recording") + " [" + path +"]");
    }
    
    public void setTranscription(EventListTranscription elt){
        eventListTableModel = new EventListTableModel(elt);
        //eventListTableModel.setCheckRegex(checkRegex1);
        eventListTable.setModel(eventListTableModel);                        
        eventListTableModel.addTableModelListener(this);

        elt.updateContributions();
        contributionListTableModel = new ContributionListTableModel(elt);
        //contributionListTableModel.setCheckRegex(checkRegex2);
        contributionListTable.setModel(contributionListTableModel);                        
        contributionListTableModel.addTableModelListener(this);
        
        setParseLevel(PARSE_LEVEL);
        
        maskDialog.setData();

    }
    
    public void reset(){
        applicationFrame.mainPanel.textViewsTabbedPane.setEnabledAt(1, true);
        applicationFrame.mainPanel.textViewsTabbedPane.setEnabledAt(2, true);
        setGeneralDocumentActionsEnabled(true);        
        applicationFrame.mainPanel.eventViewScrollPane.getVerticalScrollBar().setValue(0);
        timeViewScrollPane.getHorizontalScrollBar().setValue(0);
        setParseLevel(PARSE_LEVEL);
        adjustVisibleArea();

        resetSelection();
    }
    
    public void resetSelection(){
        // ADDED 29-06-2010
        selectionStart = -1;
        selectionEnd = -1;
        addEventAction.setEnabled(selectionStart!=selectionEnd);
        insertPauseAction.setEnabled(selectionStart!=selectionEnd);
        applicationFrame.mainPanel.startTimeLabel.setText("-");
        applicationFrame.mainPanel.stopTimeLabel.setText("-");
        applicationFrame.mainPanel.selectionLengthLabel.setText("-");

    }
    
/***************** WAVEFORM ACTIONS ******************************/ 

    @Override
    public void changeZoom(){
        if (!(applicationFrame.mainPanel.zoomToggleButton.isSelected())){
            changeZoomDialog.setVisible(false);
            return;
        }
        java.awt.Point p = applicationFrame.mainPanel.zoomToggleButton.getLocationOnScreen();
        changeZoomDialog.setLocation(p.x - changeZoomDialog.getWidth(), p.y + applicationFrame.mainPanel.zoomToggleButton.getHeight());
        int spp = (int)Math.round(timeViewer.getSecondsPerPixel()*1000);
        changeZoomDialog.zoomLevelSlider.setValue(spp);

        int mag = (int) Math.round(timeViewer.getVerticalMagnify() * 10);
        changeZoomDialog.magnifyLevelSlider.setValue(mag);
        
        changeZoomDialog.setVisible(true);
    }
    
    @Override
    public void shiftSelection(){
        commitEdit();
        super.shiftSelection();
        eventListTable.getSelectionModel().clearSelection();
        contributionListTable.getSelectionModel().clearSelection();
    }
    
    @Override
    public void detachSelection(){
        super.detachSelection();
        unselectTimepoints();
        detachSelectionAction.setEnabled(false);
        if (selectedPanelIndex==0){
            timestampEventAction.setEnabled(eventListTable.getSelectedRowCount()==1);
        }
    }

    @Override
    public void playNextSegment() {
        switch (selectedPanelIndex){
            case 0 : // segment view
                stop();
                commitEdit();
                eventListTable.selectNextRow();
                playSelection();
                break;
            case 1 : // partitur view
                super.playNextSegment();
                break;
            case 2 : // contribution view
                stop();
                commitEdit();
                contributionListTable.selectNextRow();
                playSelection();
                break;                
        }
    }
    
    
    
/******************* EVENT VIEW ACTIONS ****************************/
    
    @Override
    public void addMask(){
        //if (selectedPanelIndex!=0) return;
        //JTextArea textArea = new JTextArea();
        //textArea.setLineWrap(true);
        //textArea.setRows(10);
        //JScrollPane scrollPane = new JScrollPane(textArea);
        //int ok = JOptionPane.showConfirmDialog(applicationFrame, 
        //       scrollPane, 
        //        "Maskierungstext", 
        //        JOptionPane.OK_CANCEL_OPTION, 
        //        JOptionPane.PLAIN_MESSAGE, 
        //        new Constants().getIcon(Constants.ADD_MASK_ICON));
        MaskEntryDialog maskEntryDialog = new MaskEntryDialog(applicationFrame, true, transcriptionHead.getKeyElement());
        maskEntryDialog.setLocationRelativeTo(applicationFrame);
        maskEntryDialog.setVisible(true);
        
        if (!maskEntryDialog.approved) return;
        String maskText = maskEntryDialog.getText();
        getTranscriptionHead().addMaskSegment(selectionStart, selectionEnd, maskText);  
        maskDialog.getModel().fireTableDataChanged();
        DOCUMENT_CHANGED = true;
    }
    
    
    public void deleteMask(){
        String message = FOLKERInternationalizer.getString("masker.delete.confirm1") + "\n" + FOLKERInternationalizer.getString("option.sure");
        int userchoice = JOptionPane.showConfirmDialog(applicationFrame, message, 
                FOLKERInternationalizer.getString("transcription_menu.deletemask"), JOptionPane.YES_NO_OPTION);
        if (userchoice!=JOptionPane.YES_OPTION) return;
        getTranscriptionHead().deleteMask();
        maskDialog.getModel().fireTableDataChanged();
        DOCUMENT_CHANGED = true;
        
    }
    
    
    @Override
    public void addEvent(){
        //int selectedPanelIndex = applicationFrame.mainPanel.textViewsTabbedPane.getSelectedIndex();
        switch(selectedPanelIndex){
            case 0 : // eventListTable
                commitEdit();
                int row = eventListTableModel.getTranscription().addEvent(selectionStart, selectionEnd);
                eventListTableModel.fireTableRowsInserted(row, row);

                org.exmaralda.folker.data.Event addedEvent = eventListTableModel.getTranscription().getEventAt(row);
                setSelectedTimepoints(addedEvent.getStartpoint(), addedEvent.getEndpoint());                
                
                //java.awt.Rectangle bounds = eventListTable.getCellRect(row+eventListTableModel.ADDITIONAL_ROWS,0,true);
                // changed 06-04-2009
                java.awt.Rectangle bounds = eventListTable.getCellRect(row,0,true);
                eventListTable.scrollRectToVisible(bounds);
                eventListTable.getSelectionModel().clearSelection();
                eventListTable.requestFocus();
                eventListTable.getSelectionModel().setSelectionInterval(row, row);
                eventListTable.editCellAt(row, 4);            
                eventListTable.getEditorComponent().requestFocus();
                DOCUMENT_CHANGED = true;
                break;
            case 1 : // partitur
                super.addEvent();
                DOCUMENT_CHANGED = true;
                break;
            case 2 : // contribution list
                break;
        }
    }

    public void appendEvent(){
        commitEdit();
        // new 30-06-2014
        boolean restartLoop = getLoopMode();
        if (restartLoop){
            stopLoop();
        } else {
            stop();
        }
        // end new
        
        
        org.exmaralda.folker.data.Timeline tl = eventListTableModel.getTranscription().getTimeline();
        double lastTime = tl.getMaximumTime();
        if (lastTime==player.getTotalLength()*1000.0){
            lastTime = tl.getTimepointAt(tl.getTimepoints().size()-2).getTime();
        }
        double newTime = Math.min(lastTime + 2000.0, this.player.getTotalLength() * 1000.0);
        org.exmaralda.folker.data.Speaker speaker = null;
        
        if (AUTO_ASSIGN_SPEAKER){
            int lastEventIndex = eventListTableModel.getTranscription().getNumberOfEvents()-1;
            if (lastEventIndex>=0){
                speaker = eventListTableModel.getTranscription().getEventAt(lastEventIndex).getSpeaker();
            }
        }
        
        int row = eventListTableModel.getTranscription().addEvent(lastTime, newTime, speaker);
        eventListTableModel.fireTableRowsInserted(row, row);

        org.exmaralda.folker.data.Event addedEvent = eventListTableModel.getTranscription().getEventAt(row);
        setSelectedTimepoints(addedEvent.getStartpoint(), addedEvent.getEndpoint());

        java.awt.Rectangle bounds = eventListTable.getCellRect(row+eventListTableModel.ADDITIONAL_ROWS,0,true);
        eventListTable.scrollRectToVisible(bounds);
        eventListTable.getSelectionModel().clearSelection();
        eventListTable.requestFocus();
        eventListTable.getSelectionModel().setSelectionInterval(row, row);
        eventListTable.editCellAt(row, 4);
        eventListTable.getEditorComponent().requestFocus();

        DOCUMENT_CHANGED = true;

        if (restartLoop){
            startLoop();
        } else {
            playSelection();
        }
    }
    
    public void timestampEvent(){
        //int selectedPanelIndex = applicationFrame.mainPanel.textViewsTabbedPane.getSelectedIndex();
        switch(selectedPanelIndex){
            case 0 : // eventListTable
                commitEdit();
                int row = eventListTable.getSelectedRow();
                if (row<0) return;                
                int newrow = eventListTableModel.getTranscription().setTimestamps(row, selectionStart, selectionEnd);
                eventListTableModel.fireTableDataChanged();
                
                java.awt.Rectangle bounds = eventListTable.getCellRect(newrow+eventListTableModel.ADDITIONAL_ROWS,0,true);
                eventListTable.scrollRectToVisible(bounds);
                eventListTable.getSelectionModel().setSelectionInterval(newrow, newrow);
                eventListTable.requestFocus();
                DOCUMENT_CHANGED = true;
                break;
            case 1 : // partitur                
                // TODO
                break;
            case 2 : // contribution list
                break;
        }        
    }
    
    public void removeEvent(){
        int retValue = JOptionPane.showConfirmDialog(applicationFrame, FOLKERInternationalizer.getString("option.sure"),
                FOLKERInternationalizer.getString("segmentactions.removeSegment"), JOptionPane.YES_NO_OPTION);
        if (retValue!=JOptionPane.YES_OPTION) return;
        commitEdit();
        int[] selRows = eventListTable.getSelectedRows();
        eventListTableModel.getTranscription().removeEvents(selRows);
        eventListTableModel.fireTableRowsDeleted(selRows[0], selRows[selRows.length-1]);
        eventListTable.getSelectionModel().clearSelection();
        status(Integer.toString(selRows.length) + FOLKERInternationalizer.getString("status.segmentsremoved"));
        detachSelection();
        DOCUMENT_CHANGED = true;
    }
    
    public void mergeEventsInList(){
        int[] rows = eventListTable.getSelectedRows();
        if (rows.length<2) return;
        commitEdit();
        eventListTableModel.getTranscription().mergeEvents(rows);
        eventListTableModel.fireTableDataChanged();
        eventListTable.getSelectionModel().setSelectionInterval(rows[0], rows[0]);
        DOCUMENT_CHANGED = true;
        status(FOLKERInternationalizer.getString("status.mergesegments1") + Integer.toString(rows[0]+1) + FOLKERInternationalizer.getString("status.mergesegments2"));
    }
    
   
    public void splitEventInList(){
        if (eventListTable.getEditingColumn()!=4) return;
        int row = eventListTable.getEditingRow();        
        JTextField jt = (JTextField)(eventListTable.getEditorComponent());
        int splitPosition = jt.getCaretPosition();
        eventListTable.eventTextCellEditor.stopCellEditing();
        
        int newRow = eventListTableModel.getTranscription().splitEvent(row, splitPosition);
        if (newRow==-1){
            String text = FOLKERInternationalizer.getString("error.shortsegment");
            JOptionPane.showMessageDialog(applicationFrame, text);
            status(FOLKERInternationalizer.getString("status.nosplitsegment"));
            return;
        }
        status(FOLKERInternationalizer.getString("status.splitsegment1") + Integer.toString(row+1) + FOLKERInternationalizer.getString("status.splitsegment2"));
        DOCUMENT_CHANGED = true;
        eventListTableModel.fireTableRowsUpdated(row,row);
        eventListTableModel.fireTableRowsInserted(newRow, newRow);
        eventListTable.getSelectionModel().setSelectionInterval(newRow,newRow);
    }
    
    public void splitInContribution() {        
        int rememberRow = contributionListTable.getSelectedRow();
        contributionTextPane.split(contributionListTableModel);
        contributionListTable.getSelectionModel().setSelectionInterval(rememberRow, rememberRow);
    }
    
    
    public void fillGaps(){
        switch (selectedPanelIndex){
            case 0 : //segment list
                int howMany = eventListTableModel.getTranscription().fillGaps();
                eventListTableModel.fireTableDataChanged();
                status(FOLKERInternationalizer.getString("status.fillgaps1") + howMany + FOLKERInternationalizer.getString("status.fillgaps2"));
                DOCUMENT_CHANGED = true;
                break;
            case 1 : //partitur
                // action is disabled in partitur view
                break;
            case 2 : //contribution list
                int howMany2 = contributionListTableModel.getTranscription().fillGaps();
                contributionListTableModel.fireTableDataChanged();
                status(FOLKERInternationalizer.getString("status.fillgaps1")  + howMany2 + FOLKERInternationalizer.getString("status.fillgaps2"));
                DOCUMENT_CHANGED = true;
                break;            
        }

    }

    public void updatePauses() {
        commitEdit();
        switch (selectedPanelIndex){
            case 0 : //segment list
                int howMany = eventListTableModel.getTranscription().updatePauses();
                eventListTableModel.fireTableRowsUpdated(0, eventListTableModel.getRowCount());
                status(FOLKERInternationalizer.getString("status.updatepauses1") + howMany + FOLKERInternationalizer.getString("status.updatepauses2"));
                DOCUMENT_CHANGED = true;
                break;
            case 1 : //partitur
                int howMany3 = partitur.getModel().updatePauses();
                status(FOLKERInternationalizer.getString("status.updatepauses1") + howMany3 + FOLKERInternationalizer.getString("status.updatepauses2"));
                DOCUMENT_CHANGED = true;
                break;
            case 2 : //contribution list
                int howMany2 = contributionListTableModel.getTranscription().updatePauses();
                contributionListTableModel.fireTableRowsUpdated(0, contributionListTableModel.getRowCount());
                status(FOLKERInternationalizer.getString("status.updatepauses1") + howMany2 + FOLKERInternationalizer.getString("status.updatepauses2"));
                DOCUMENT_CHANGED = true;
                break;
        }
    }
    
    public void modifyTimes() {
        commitEdit();
        switch(selectedPanelIndex){
            case 1 : //partitur
                org.exmaralda.partitureditor.jexmaralda.Timeline tl = partitur.getModel().getTranscription().getBody().getCommonTimeline();
                ModifyAbsoluteTimesDialog dialog = new ModifyAbsoluteTimesDialog(tl, partitur.selectionStartCol, partitur.parent, true);
                dialog.setLocationRelativeTo(partitur);
                dialog.setVisible(true);
                if (dialog.approved){
                    if (dialog.getModificationType()==ModifyAbsoluteTimesDialog.SHIFT_MODIFICATION){
                        double shift = dialog.getShiftAmount();
                        System.out.println("Shifting by " + shift);
                        partitur.getModel().shiftTimes(shift);             
                    } else if (dialog.getModificationType()==ModifyAbsoluteTimesDialog.SCALE_MODIFICATION){
                        double scale = dialog.getScaleAmount();
                        partitur.getModel().scaleTimes(scale);             
                    }
                }
                status(FOLKERInternationalizer.getString("dialog.times.updated"));
                break;                
        }
        
    }

    

    public void removePauseSpeakerAssignment() {
        int retValue = JOptionPane.showConfirmDialog(applicationFrame, FOLKERInternationalizer.getString("option.sure"),
                FOLKERInternationalizer.getString("segmentactions.removeSpeakers"), JOptionPane.YES_NO_OPTION);
        if (retValue==JOptionPane.NO_OPTION) return;
        commitEdit();
        switch (selectedPanelIndex){
            case 0 : //segment list
                int howMany = eventListTableModel.getTranscription().removePauseSpeakerAssignment();
                eventListTableModel.fireTableRowsUpdated(0, eventListTableModel.getRowCount());
                status(FOLKERInternationalizer.getString("status.updatepauses1") + howMany + FOLKERInternationalizer.getString("status.updatepauses2"));
                DOCUMENT_CHANGED = true;
                break;
            case 1 : //partitur
                //Option not available in the partitur view
                break;
            case 2 : //contribution list
                //Option not available in the contribution view
        }
    }

    public void normalizeWhitespace() {
        switch (selectedPanelIndex){
            case 0 : //segment list
                int howMany = eventListTableModel.getTranscription().normalizeWhitespace();
                eventListTableModel.fireTableRowsUpdated(0, eventListTableModel.getRowCount());
                status(FOLKERInternationalizer.getString("status.whitespace") + howMany + FOLKERInternationalizer.getString("status.updatepauses2"));
                DOCUMENT_CHANGED = true;
                break;
            case 1 : //partitur
                int howMany3 = partitur.getModel().normalizeWhitespace();
                status(FOLKERInternationalizer.getString("status.whitespace") + howMany3 + FOLKERInternationalizer.getString("status.updatepauses2"));
                DOCUMENT_CHANGED = true;
                break;
            case 2 : //contribution list
                int howMany2 = contributionListTableModel.getTranscription().normalizeWhitespace();
                contributionListTableModel.fireTableRowsUpdated(0, contributionListTableModel.getRowCount());
                status(FOLKERInternationalizer.getString("status.whitespace") + howMany2 + FOLKERInternationalizer.getString("status.updatepauses2"));
                DOCUMENT_CHANGED = true;
                break;
        }
    }
    
    public void minimize() {
        eventListTableModel.getTranscription().lowerCaseAll();
        //eventListTableModel.getTranscription().removePuncutation();
        eventListTableModel.fireTableRowsUpdated(0, eventListTableModel.getRowCount());
        status(FOLKERInternationalizer.getString("status.minimize"));
        DOCUMENT_CHANGED = true;
    }
    
    
    public void insertPause(){
        double pauseLength = Math.round((selectionEnd - selectionStart)/10.0)/100.0;
        String pauseString = "(" + Double.toString(pauseLength) + ") ";
        //int row1 = eventListTable.getSelectedRow();
        
        if (selectedPanelIndex==0){
            commitEdit();
            addEvent();
            //int row = eventListTable.getEditingRow();
            JTextField jt = (JTextField)(eventListTable.getEditorComponent());
            int caretPosition = jt.getCaretPosition();
            try {
                jt.getDocument().insertString(caretPosition, pauseString, null);
                status(FOLKERInternationalizer.getString("status.insertpause1") + Integer.toString(eventListTable.getSelectedRow()+ 1) + FOLKERInternationalizer.getString("status.insertpause2"));
                DOCUMENT_CHANGED = true;
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void nextError(){
        if (selectedPanelIndex==0){
            int startIndex = Math.max(-1,eventListTable.getSelectedRow());
            int nextIndex = -1;
            for (int index = startIndex+1; index<getTranscription().getNumberOfEvents(); index++){
                boolean hasError =
                        (!((Boolean) (eventListTableModel.getValueAt(index, 5))).booleanValue())
                        || (!((Boolean) (eventListTableModel.getValueAt(index, 6))).booleanValue());
                if (hasError){
                    nextIndex = index;
                    break;
                }
            }
            if (nextIndex!=-1){
                eventListTable.getSelectionModel().setSelectionInterval(nextIndex, nextIndex);
                eventListTable.scrollRectToVisible(eventListTable.getCellRect(Math.min(nextIndex+2, eventListTableModel.getRowCount()-1), 0, true));                        
            } else {
                JOptionPane.showMessageDialog(applicationFrame, FOLKERInternationalizer.getString("misc.noMoreErrors"));
            }
        } else if (selectedPanelIndex==2){
            int startIndex = Math.max(-1,contributionListTable.getSelectedRow());
            int nextIndex = -1;
            for (int index = startIndex+1; index<getTranscription().getNumberOfContributions(); index++){
                boolean hasError =
                        (!((Boolean) (contributionListTable.getValueAt(index, 5))).booleanValue())
                        || (!((Boolean) (contributionListTable.getValueAt(index, 6))).booleanValue());
                if (hasError){
                    nextIndex = index;
                    break;
                }
            }
            if (nextIndex!=-1){
                contributionListTable.getSelectionModel().setSelectionInterval(nextIndex, nextIndex);
                contributionListTable.scrollRectToVisible(contributionListTable.getCellRect(Math.min(nextIndex+2, contributionListTableModel.getRowCount()-1), 0, true));                        
            } else {
                JOptionPane.showMessageDialog(applicationFrame, FOLKERInternationalizer.getString("misc.noMoreErrors"));
            }            
        }
    }

    public void checkLog(){
        if (!Constants.isFolkContext()) return;
        if (!Constants.isLoggingEnabled()) return;
        if (getTranscription()==null) return;
        if (getTranscription().getEventlist().getEvents().isEmpty()) return;
        if (transcriptionHead==null) return;
        Date editEnd = new Date();
        String user = System.getProperty("user.name");
        System.out.println("Checklog: " + editStart + " until " + editEnd);
        NewLogEntryDialog dialog = new NewLogEntryDialog(applicationFrame, true, editStart, editEnd, user, "");
        dialog.setLocationRelativeTo(applicationFrame);
        dialog.setVisible(true);
        if (dialog.approved){
            Element newLog = dialog.getLogEntry();
            transcriptionHead.appendLog(newLog);
            DOCUMENT_CHANGED = true;
        }
    }
    
    public boolean checkSave(){
        // TODO: make a REAL check whether or not the transcription has actually changed
        boolean thereWereChanges = DOCUMENT_CHANGED || eventListTableModel.DOCUMENT_CHANGED || contributionListTableModel.DOCUMENT_CHANGED || partitur.transcriptionChanged;
        if ((getTranscription().getNumberOfEvents()>0) && thereWereChanges){
            int retValue = JOptionPane.showConfirmDialog(applicationFrame, 
                    FOLKERInternationalizer.getString("option.checksave1"),
                    FOLKERInternationalizer.getString("option.checksave2"), JOptionPane.YES_NO_CANCEL_OPTION);
            if (retValue==JOptionPane.CANCEL_OPTION){
                return false;
            }
            if (retValue==JOptionPane.YES_OPTION){
                IS_SAVING_IN_BACKGROUND = true;
                System.out.println("HERE IS CHECK SAVE!");
                saveAction.actionPerformed(null);
                waitWhileSaving();
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
        // divider location
        PreferencesUtilities.setProperty("divider-location", Integer.toString(applicationFrame.mainPanel.timeTextViewsSplitPane.getDividerLocation()));        
        // pixels per second in time viewer
        PreferencesUtilities.setProperty("pixels-per-second", Double.toString(timeViewer.getPixelsPerSecond()));
        // recent files
        String recentFileString = "";
        for (int pos=0; pos<Math.min(4,recentFiles.size()); pos++){
            recentFileString+=recentFiles.elementAt(pos).getAbsolutePath()+"#";
        }
        PreferencesUtilities.setProperty("recent-files", recentFileString);
        PreferencesUtilities.setProperty("parse-level", Integer.toString(PARSE_LEVEL));

        PreferencesUtilities.setProperty("show-virtual-keyboard", Boolean.toString(applicationFrame.showVirtualKeyboardCheckBoxMenuItem.isSelected()));
        
        matchListDialog.storeSettings();
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
        applicationFrame.mainPanel.timeTextViewsSplitPane.setDividerLocation(dividerLocation);
        // pixels per second in time viewer
        double pixelsPerSecond = Double.parseDouble(PreferencesUtilities.getProperty("pixels-per-second", "10.0"));
        timeViewer.setPixelsPerSecond(Math.max(10,Math.min(pixelsPerSecond,1000.0)));
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

        int parseLevel = Integer.parseInt(PreferencesUtilities.getProperty("parse-level", "2"));
        setParseLevel(parseLevel);
        contributionTextPane.MAKE_LINE_BREAK_AFTER_BOUNDARY_SYMBOLS=(parseLevel==3);

        NORMALIZE_WHITESPACE = PreferencesUtilities.getBooleanProperty("normalize-whitespace", true);
        UPDATE_PAUSES = PreferencesUtilities.getBooleanProperty("update-pauses", true);
        TIME_BETWEEN_LOOPS = Integer.parseInt(PreferencesUtilities.getProperty("loop-time", "500"));

        boolean showVirtualKeyboard = PreferencesUtilities.getBooleanProperty("show-virtual-keyboard", true);
        applicationFrame.mainPanel.virtualKeyboardPanel.setVisible(showVirtualKeyboard);
        applicationFrame.showVirtualKeyboardCheckBoxMenuItem.setSelected(showVirtualKeyboard);

        matchListDialog.retrieveSettings();
        
        applicationFrame.fileMenu.addSeparator();
        applicationFrame.fileMenu.add(exitAction); 
        
        try {
            String ipAddress = Inet4Address.getLocalHost().getHostAddress();
            if (ipAddress.startsWith("10.")){
                if (PreferencesUtilities.getProperty("logging-enabled", null)==null){ 
                    PreferencesUtilities.setProperty("logging-enabled", Boolean.toString(true));
                }
            }
        } catch (UnknownHostException ex) {
            System.out.println("Could not get IP-Address");
        }

        status(FOLKERInternationalizer.getString("status.start") + Integer.toString(parseLevel) + " / Player: " + PreferencesUtilities.getProperty("PlayerType", getDefaultPlayer()));
        updateInfoPanel();
    }

    
    void waitWhileSaving(){
        while (IS_SAVING_IN_BACKGROUND){
            // added 29-05-2009
            // don't do anything while saving is in progress
        }
    }

    public boolean exitApplication(){
        waitWhileSaving();
        System.out.println("Waited once");
        checkLog();
        System.out.println("Log checked");
        if (!checkSave()) return false;
        System.out.println("Save checked");
        waitWhileSaving();
        System.out.println("Waited twice");
        System.out.println("Application terminated");
        System.out.println("-----------------------");
        storeSettings();
        System.exit(0);
        return true;
    }
    
    
/***************** OTHER ******************************/ 


    private void initMaskDialog(){
        maskDialog = new MaskDialog(applicationFrame, false, this);           
        maskDialog.setTitle(FOLKERInternationalizer.getString("dialog.mask.editMask"));        
        //AWTUtilities.setWindowOpacity(maskDialog, .9f); 
        //timeViewer.addTimeSelectionListener(maskDialog);        
    }

    public void copySelectionToClipboard() throws SAXException, JDOMException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        switch (selectedPanelIndex){
            case 0 :
                int[] selectedSegments = eventListTable.getSelectedRows();
                EventListTranscription elt = getTranscription().partForEvents(selectedSegments);
                elt.updateContributions();
                String STYLESHEET = OutputAction.SEGMENTS2HTML_STYLESHEET;
                // changed 18-08-2010
                // caused a bug when file was not yet saved
                File theFile = null;
                if (currentFilePath!=null){
                    theFile = new File(currentFilePath);                    
                }
                Document transcriptionDoc = EventListTranscriptionXMLReaderWriter.toJDOMDocument(elt, theFile);
                transcriptionDoc.getRootElement().setAttribute("count-start", Integer.toString(eventListTable.getSelectedRow()));
                String docString = org.exmaralda.common.jdomutilities.IOUtilities.documentToString(transcriptionDoc);

                StylesheetFactory sf = new StylesheetFactory(true);
                String resultString = sf.applyInternalStylesheetToString(STYLESHEET, docString);
                org.exmaralda.exakt.utilities.HTMLSelection html = new org.exmaralda.exakt.utilities.HTMLSelection(resultString);
                this.getFrame().getToolkit().getSystemClipboard().setContents(html,null);
                break;
            case 1 :
                partitur.copyTextAction.actionPerformed(null);
                break;
            case 2 :
                int[] selectedContributions = contributionListTable.getSelectedRows();
                EventListTranscription elt2 = getTranscription().partForContributions(selectedContributions);
                String STYLESHEET2 = OutputAction.CONTRIBUTIONS2HTML_STYLESHEET;
                // changed 18-08-2010
                // caused a bug when file was not yet saved
                File theFile2 = null;
                if (currentFilePath!=null){
                    theFile2 = new File(currentFilePath);
                }
                Document transcriptionDoc2 = EventListTranscriptionXMLReaderWriter.toJDOMDocument(elt2, theFile2);
                transcriptionDoc2.getRootElement().setAttribute("count-start", Integer.toString(contributionListTable.getSelectedRow()));
                String docString2 = org.exmaralda.common.jdomutilities.IOUtilities.documentToString(transcriptionDoc2);
                //System.out.println(docString2);
                StylesheetFactory sf2 = new StylesheetFactory(true);
                String resultString2 = sf2.applyInternalStylesheetToString(STYLESHEET2, docString2);
                org.exmaralda.exakt.utilities.HTMLSelection html2 = new org.exmaralda.exakt.utilities.HTMLSelection(resultString2);
                this.getFrame().getToolkit().getSystemClipboard().setContents(html2,null);
                break;
        }
    }


    public void commitEdit(){
        if (eventListTable.isEditing()){            
            eventListTable.eventTextCellEditor.stopCellEditing();
            eventListTable.speakerCellEditor.stopCellEditing();            
        } else if (partitur.isEditing){
            partitur.commitEdit(true);
        } else if ((selectedPanelIndex==2) && (contributionTextPane.isEditable())){
            contributionTextPane.validateContribution();
            validateContribution();
        }
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
        applicationFrame.mainPanel.statusMessageLabel.setText(message);
    }
        
    public void displayException(String message){
        String title = FOLKERInternationalizer.getString("misc.error");       
        JOptionPane.showMessageDialog(getFrame(), message, title, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void displayException(Exception e){
        String title = FOLKERInternationalizer.getString("misc.error");
        String message = e.getLocalizedMessage();
        JOptionPane.showMessageDialog(getFrame(), message, title, JOptionPane.ERROR_MESSAGE);
        status(title + ": " + message);
        e.printStackTrace();
    }

    public void toggleLoopState(){
        //loopPlay = applicationFrame.mainPanel.loopCheckBox.isSelected();
    }

    public int displayRecordingNotFoundDialog(String mediaPath, Exception ex){
        //String message = "Fehler beim Lesen der Aufnahme" + " \n" + mediaPath + ".\n" + "Fehlermeldung" + ":\n" + ex.getLocalizedMessage();
        String message = FOLKERInternationalizer.getString("error.readingrecording") + " \n" + mediaPath + ".\n" + FOLKERInternationalizer.getString("error.message") + ":\n" + ex.getLocalizedMessage();
        //String[] options = {"Aufnahme neu zuordnen", "Abbrechen"};
        String[] options = {FOLKERInternationalizer.getString("dialog.recording"), FOLKERInternationalizer.getString("error.cancel")};
        //  Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue
        int optionChosen = JOptionPane.showOptionDialog(applicationFrame, 
                    message,
                    FOLKERInternationalizer.getString("error.readingrecording"),
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE, 
                    new Constants().getIcon(Constants.RECORDING_WARNING_ICON),
                    options, 
                    FOLKERInternationalizer.getString("dialog.recording"));
        return optionChosen;
    }
    
    
    void adjustVisibleArea(){
        // do nothing because something's not working
        /*double left = timeViewer.getLeftBoundaryTime();
        double right = timeViewer.getRightBoundaryTime();
        
        eventListTable.timeCellRenderer.setVisibleTimes(left, right);  */
        /* contributionListTable.timeCellRenderer.setVisibleTimes(left, right);  */

        /*eventListTableModel.fireSelectionChanged();   
        contributionListTableModel.fireSelectionChanged();*/
    }
    
    public void setMaskTimepoints(Timepoint minTimepoint, Timepoint maxTimepoint){
        timeViewer.setCursorTime(minTimepoint.getTime());
        timeViewer.setSelectionInterval(minTimepoint.getTime(), maxTimepoint.getTime(), false);               
        timeViewer.resetDragBoundaries();
        scrollToTime(minTimepoint.getTime(), null, false);
    }

    
    
    public void setSelectedTimepoints(Timepoint minTimepoint, Timepoint maxTimepoint){
       
        
        // added 20-05-2009
        timeViewer.setCursorTime(minTimepoint.getTime());
        timeViewer.setSelectionInterval(minTimepoint.getTime(), maxTimepoint.getTime(), true);               
        // find the previous and following point in the timeline and
        // set the drag boundaries accordingly
        Timepoint previousTimepoint = eventListTableModel.getTranscription().getTimeline().getPreviousTimepoint(minTimepoint);
        if (previousTimepoint==null){timeViewer.setLeftDragBoundary(0.0);}
        else {timeViewer.setLeftDragBoundary(previousTimepoint.getTime());}
        Timepoint nextTimepoint = eventListTableModel.getTranscription().getTimeline().getNextTimepoint(maxTimepoint);
        if (nextTimepoint==null){timeViewer.setRightDragBoundary(player.getTotalLength()*1000.0);}
        else {timeViewer.setRightDragBoundary(nextTimepoint.getTime());}    
        
        eventListTable.timeCellRenderer.setSelectedTimepoints(minTimepoint, maxTimepoint);
        contributionListTable.timeCellRenderer.setSelectedTimepoints(minTimepoint, maxTimepoint);
        startPoint = minTimepoint;
        endPoint = maxTimepoint;
        
        adjustVisibleArea();
        
        detachSelectionAction.setEnabled(true);
        
    }
    
    @Override
    public void unselectTimepoints(){
        eventListTable.timeCellRenderer.removeSelectedTimepoints();
        eventListTableModel.fireSelectionChanged();            
        contributionListTable.timeCellRenderer.removeSelectedTimepoints();
        contributionListTableModel.fireSelectionChanged();
        super.unselectTimepoints();
    }

    private void checkPauseLength() {
        if (eventListTable.getSelectedRowCount()!=1) return;
        if (this.PARSE_LEVEL<=1) return;
        int selectedRow = eventListTable.getSelectedRow();
        org.exmaralda.folker.data.Event e = getTranscription().getEventAt(selectedRow);
        if (e.getText().matches("\\(\\d{1,2}\\.\\d{1,2}\\) ?")){
            double pauseLength = e.getEndpoint().getTime() - e.getStartpoint().getTime();
            String newPauseText = "(" + Double.toString(Math.round(pauseLength/10.0)/100.0) + ")";
            e.setText(newPauseText);
            this.getEventListTableModel().fireTableCellUpdated(selectedRow, 4);
        }
    }

    
    Object getSelectedEvent(int panelIndex){
        Object selectedEvent = null;
        switch (panelIndex){
            case 0 :
                int row1 = eventListTable.getSelectedRow();
                if (row1>=0){
                    selectedEvent = eventListTableModel.getTranscription().getEventAt(row1);
                }
                break;
            case 1 :
                selectedEvent = partitur.getSelectedEvent();
                break;
            case 2 :
                int row2 = contributionListTable.getSelectedRow();
                if (row2>=0){
                    selectedEvent = eventListTableModel.getTranscription().getContributionAt(row2).eventlist.getEventAt(0);
                }
                break;
        }
        return selectedEvent;
    }
    
    public void setView(int whichPanel){
        applicationFrame.mainPanel.textViewsTabbedPane.setSelectedIndex(whichPanel);        
    }
    
    
    void switchViews(){
            int newSelectedPanelIndex = applicationFrame.mainPanel.textViewsTabbedPane.getSelectedIndex();
            
            double visibleTime = timeViewer.getLeftBoundaryTime();
            Object selection = getSelectedEvent(selectedPanelIndex);
            EventWrapper correspondingEvent = new EventWrapper();
            
            // take care of switching between / updating the transcription data models
            switch (selectedPanelIndex){
                case 1 : // i.e. we are coming from the partitur view
                    System.out.println("[*** Coming from the partitur view ***]");
                    partitur.commitEdit(true);
                    EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionConverter.
                            importExmaraldaBasicTranscription(partitur.getModel().getTranscription(), 
                            (org.exmaralda.partitureditor.jexmaralda.Event)selection, correspondingEvent);
                    setTranscription(elt);      
                    break;
                case 0 :
                case 2 : // i.e. we are NOT coming from the partitur view                    
                    commitEdit();
                    if (newSelectedPanelIndex==0){
                        // i.e. we are going (from the contribution view) to the event view
                        System.out.println("[*** Going from the contribution to the event view ***]");
                        correspondingEvent.setFolkerEvent((org.exmaralda.folker.data.Event)selection);
                        eventListTableModel.fireTableDataChanged();
                    } else if (newSelectedPanelIndex==1){
                        // i.e. we are going to the partiturview
                        System.out.println("[*** Going to the partitur view ***]");
                        applicationFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        BasicTranscription bt = org.exmaralda.folker.io.EventListTranscriptionConverter
                                .exportBasicTranscription(getTranscription(), 
                                (org.exmaralda.folker.data.Event)selection, correspondingEvent);
                        bt.getHead().getMetaInformation().setReferencedFile(currentMediaPath);
                        // stratify the transcription
                        bt.getBody().stratify(Tier.STRATIFY_BY_DISTRIBUTION);
                        
                        partitur.getModel().setTranscription(bt);

                        // added 06-04-2009
                        partitur.protectLastColumn = true;
                        // added 19-01-2010
                        partitur.getModel().protectLastColumn = true;

                        partitur.getModel().anchorTimeline(0.0, player.getTotalLength());
                        applicationFrame.setCursor(Cursor.getDefaultCursor());
                    } else if (newSelectedPanelIndex==2){
                        // i.e. we are going (from the event view) to the contribution view
                        System.out.println("[*** Going from the event to the contribution view ***]");
                        correspondingEvent.setFolkerEvent((org.exmaralda.folker.data.Event)selection);
                        getTranscription().updateContributions();
                        contributionListTableModel.fireTableDataChanged();                                                
                    }
                    break;
            }
                      
            timeViewer.removeSelection();
            
            if (newSelectedPanelIndex==2){
                applicationFrame.mainPanel.textViewPanel.remove(applicationFrame.mainPanel.virtualKeyboardPanel);
                applicationFrame.mainPanel.contributionTextPanel.add(applicationFrame.mainPanel.virtualKeyboardPanel, BorderLayout.NORTH);
            } else if (selectedPanelIndex==2){
                applicationFrame.mainPanel.contributionTextPanel.remove(applicationFrame.mainPanel.virtualKeyboardPanel);
                applicationFrame.mainPanel.textViewPanel.add(applicationFrame.mainPanel.virtualKeyboardPanel, BorderLayout.NORTH);                
            }
            
            if (selectedPanelIndex==2){
                contributionTextPane.setContribution(null);
            }
            
            
            selectedPanelIndex = newSelectedPanelIndex;
            
            switch(selectedPanelIndex){
                case 0 : // eventListTable
                    // find the right position
                    if (correspondingEvent.getFolkerEvent()!=null){
                        org.exmaralda.folker.data.Event ce = correspondingEvent.getFolkerEvent();
                        eventListTable.makeVisible(ce);
                    } else {
                        eventListTable.makeVisible(visibleTime);
                    }                    
                    adjustVisibleArea();
                    break;
                case 1 : // partitur
                    toggleQuickTranscriptionDialog(false);
                    timeViewer.removeSelection();
                    // find the right position
                    if (correspondingEvent.getExmaraldaEvent()!=null){
                        org.exmaralda.partitureditor.jexmaralda.Event ce = correspondingEvent.getExmaraldaEvent();
                        partitur.makeVisible(ce);
                    } else {
                        partitur.makeVisible(visibleTime/1000.0);
                    }
                    Timepoint tp = eventListTable.getVisiblePosition();                    
                    break;                    
                case 2 : // contribution list                    
                    toggleQuickTranscriptionDialog(false);
                    // find the right position
                    if (correspondingEvent.getFolkerEvent()!=null){
                        org.exmaralda.folker.data.Event ce = correspondingEvent.getFolkerEvent();
                        contributionListTable.makeVisible(ce);
                    } else {
                        contributionListTable.makeVisible(visibleTime);
                    }
                    adjustVisibleArea();
                    break;                    
            }

            editSpeakersAction.setEnabled(selectedPanelIndex!=1);
            editRecordingAction.setEnabled(selectedPanelIndex!=1);
            fillGapsAction.setEnabled(selectedPanelIndex!=1);
            removePauseSpeakerAssignmentAction.setEnabled(selectedPanelIndex==0);
            modifyTimesAction.setEnabled(selectedPanelIndex==1);
            minimizeAction.setEnabled(selectedPanelIndex==0);
            //updatePausesAction.setEnabled(selectedPanelIndex!=1);
            //normalizeWhitespaceAction.setEnabled(selectedPanelIndex!=1);
    }
       
/******************** LISTENER METHODS *********************/    

    public void speakersChanged(){
        DOCUMENT_CHANGED = true;
        switch (selectedPanelIndex){
            case 0 :
                getEventListTableModel().fireSpeakersChanged();
                break;
            case 2 :
                contributionListTableModel.getTranscription().updateContributions();
                contributionListTableModel.fireTableDataChanged();
                break;
            case 1 :
                break;
        } 
    }
    
    /** process changes in the selection of either the event view or
     * the contribution view */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        if (e.getSource()==eventListTable.getSelectionModel()){
            //System.out.println("Value changed!");
            // the selected event(s) in the event view have changed
            // determine selection
            int firstSelectedRow = eventListTable.getSelectedRow();
            boolean oneOrSeveralEventsAreSelected = (firstSelectedRow>=0) && (firstSelectedRow<eventListTableModel.getRowCount()-10);
            
            // enable/disable actions
            if (removeEventAction!=null){removeEventAction.setEnabled(oneOrSeveralEventsAreSelected);}
            if (splitEventInListAction!=null){splitEventInListAction.setEnabled(eventListTable.getEditingColumn()==4);}
            if (mergeEventsInListAction!=null) {mergeEventsInListAction.setEnabled(eventListTable.getSelectedRowCount()>1);}
            if (!oneOrSeveralEventsAreSelected) return;
            
            // determine the selected time interval
            Timepoint minTimepoint = eventListTableModel.getTranscription().getEventAt(firstSelectedRow).getStartpoint();
            Timepoint maxTimepoint = eventListTableModel.getMaxTimepoint(eventListTable.getSelectedRows());

            setSelectedTimepoints(minTimepoint, maxTimepoint); 
            timestampEventAction.setEnabled(false);
            
        } else if (e.getSource()==contributionListTable.getSelectionModel()){
            // the selected contribution(s) in the contribution view have changed
            int firstSelectedRow = contributionListTable.getSelectedRow();
            if (firstSelectedRow<0) return;
            
            Contribution contribution = getTranscription().getContributionAt(firstSelectedRow);
            if (contribution==null) return;

            if (contributionListTable.getSelectedRowCount()==1){
                contributionTextPane.setContribution(contribution);
            } else {
                contributionTextPane.setContribution(null);
            }

            // determine the selected time interval
            Timepoint minTimepoint = contribution.getStartpoint();
            //Timepoint maxTimepoint = contribution.getEndpoint();
            Timepoint maxTimepoint = contributionListTableModel.getMaxTimepoint(contributionListTable.getSelectedRows());
            
            setSelectedTimepoints(minTimepoint, maxTimepoint);            
        } else if (e.getSource()==eventListTable.getColumnModel().getSelectionModel()){
            //System.out.println("Column model!");
            if (splitEventInListAction!=null){splitEventInListAction.setEnabled(eventListTable.getEditingColumn()==4);}            
        } else {
            if (splitEventInListAction!=null){splitEventInListAction.setEnabled(eventListTable.getEditingColumn()==4);}                        
        }
    }

    /** process changes in the selection of the time view */
    @Override
    public void processTimeSelectionEvent(TimeSelectionEvent event) {        
        
        super.processTimeSelectionEvent(event);                
        
        if (event.getType()!=TimeSelectionEvent.ZOOM_CHANGED){
            double cursor = timeViewer.getCursorTime();
            applicationFrame.mainPanel.cursorTimeLabel.setText("   " + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(cursor,2) + "   ");

            adjustVisibleArea();

            addEventAction.setEnabled(selectionStart!=selectionEnd);
            addMaskAction.setEnabled(selectionStart!=selectionEnd);
            insertPauseAction.setEnabled(selectionStart!=selectionEnd);
            if (selectionStart!=selectionEnd){
                applicationFrame.mainPanel.startTimeLabel.setText(org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(selectionStart,2));
                applicationFrame.mainPanel.stopTimeLabel.setText(org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(selectionEnd,2));
                double selectionLength = Math.round((selectionEnd-selectionStart)*100)/100;
                applicationFrame.mainPanel.selectionLengthLabel.setText(" " + Double.toString(selectionLength/1000) + "s ");
            } else {
                applicationFrame.mainPanel.startTimeLabel.setText("-");
                applicationFrame.mainPanel.stopTimeLabel.setText("-");
                applicationFrame.mainPanel.selectionLengthLabel.setText("-");
            }
        }
        
        if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED) && (startPoint!=null)){
            //int selectedPanelIndex = applicationFrame.mainPanel.textViewsTabbedPane.getSelectedIndex();
            switch(selectedPanelIndex){
                case 0 : // event view or contribution view
                case 2 :
                    AbstractListTranscriptionTableModel tm = (selectedPanelIndex==0) ? eventListTableModel : contributionListTableModel;
                    Timepoint tp = (Timepoint)startPoint;
                    // ADDED 08-06-2011: check if the change leads to contradictory orders of the timeline
                    boolean timeOK = tm.getTranscription().getTimeline().checkOrder(tp, event.getStartTime());
                    if (!(timeOK)){
                        //displayException(new Exception("Not possible - contradictory time structure"));
                        //return;
                    }
                    tp.setTime(event.getStartTime());
                    tm.fireSelectionChanged();
                    if (!timeOK){
                        tm.reorderTimeline();
                    }
                    break;
                case 1 : // partitur
                    super.moveTimepoints(event);
                    break; 
            }
        } else if ((event.getType()==TimeSelectionEvent.END_TIME_CHANGED) && (endPoint!=null)){
            //int selectedPanelIndex = applicationFrame.mainPanel.textViewsTabbedPane.getSelectedIndex();
            switch(selectedPanelIndex){
                case 0 : // event view or contribution view
                case 2 :
                    AbstractListTranscriptionTableModel tm = (selectedPanelIndex==0) ? eventListTableModel : contributionListTableModel;
                    Timepoint tp = (Timepoint)endPoint;
                    // ADDED 08-06-2011: check if the change leads to contradictory orders of the timeline
                    boolean timeOK = tm.getTranscription().getTimeline().checkOrder(tp, event.getEndTime());
                    if (!(timeOK)){
                        //displayException(new Exception("Not possible - contradictory time structure"));
                        //return;
                    }
                    tp.setTime(event.getEndTime());
                    tm.fireSelectionChanged();
                    if (!timeOK){
                        tm.reorderTimeline();
                    }
                    break;
                case 1 : // partitur
                    super.moveTimepoints(event);
                    break;      
            }
        } else if (event.getType()==TimeSelectionEvent.ZOOM_CHANGED){
            String spp = Double.toString(Math.round(timeViewer.getSecondsPerPixel()*10000.0)/10.0);
            spp = "1px\u2259" + spp + "ms ";
            applicationFrame.mainPanel.secondsPerPixelLabel.setText(spp);
        } else if (event.getType()==TimeSelectionEvent.MARK_SET){
                applicationFrame.mainPanel.markTimeLabel.setText(org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(timeViewer.getMarkTime(),2));
        } else if (event.getType()==TimeSelectionEvent.MARK_REMOVED){
                applicationFrame.mainPanel.markTimeLabel.setText("-");
        }
        
        
    }

    /** process change events fired by either the event or the contribution view */
    @Override
    public void tableChanged(TableModelEvent e) {
        if ((e.getType()==e.UPDATE) && (e.getColumn()==4)){
            if (selectedPanelIndex==0){
                eventListTable.getSelectionModel().setSelectionInterval(e.getFirstRow(), e.getLastRow());
            }
        }
        
    }
    
    /*void updateNextErrorButtons(TableModelEvent e){
        if (e.getSource()==eventListTableModel){
            boolean segmentsHaveErrors = false;
            for (int index = 0; index<getTranscription().getNumberOfEvents(); index++){
                segmentsHaveErrors = (!((Boolean) (eventListTableModel.getValueAt(index, 5))).booleanValue())
                        || (!((Boolean) (eventListTableModel.getValueAt(index, 6))).booleanValue());
                if (segmentsHaveErrors) break;
            }
            applicationFrame.mainPanel.nextErrorButton.setEnabled(segmentsHaveErrors);
        }
        
    }*/

    @Override
    public void processPlayableEvent(PlayableEvent e) {
        super.processPlayableEvent(e);
        int type = e.getType();
        switch (type){
            case PlayableEvent.SOUNDFILE_SET :
                status(FOLKERInternationalizer.getString("status.audioloaded"));
                applicationFrame.mainPanel.pauseButton.setSelected(false);
                break;                
            case PlayableEvent.PLAYBACK_STARTED :                 
                status(FOLKERInternationalizer.getString("status.playbackstarted"));
                applicationFrame.mainPanel.pauseButton.setSelected(false);
                break;
            case PlayableEvent.PLAYBACK_STOPPED : 
                status(FOLKERInternationalizer.getString("status.playbackstopped"));
                applicationFrame.mainPanel.pauseButton.setSelected(false);
                break;
            case PlayableEvent.PLAYBACK_HALTED :
                status(FOLKERInternationalizer.getString("status.playbackhalted"));
                break;                
            case PlayableEvent.PLAYBACK_RESUMED :
                status(FOLKERInternationalizer.getString("status.playbackcontinued"));
                break;
            case PlayableEvent.POSITION_UPDATE :
                double pos = e.getPosition();
                applicationFrame.mainPanel.cursorTimeLabel.setText("   " + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(pos*1000.0,2) + "   ");
                break;
            default: break;
        }
    }    

    @Override
    public void stateChanged(ChangeEvent e) {        
        if (e.getSource()==changeZoomDialog.zoomLevelSlider){
            if (changeZoomDialog.zoomLevelSlider.getValueIsAdjusting()) return;
            int v = changeZoomDialog.zoomLevelSlider.getValue();
            double pixelsPerSecond = 1000.0 / v;
            timeViewer.setPixelsPerSecond(pixelsPerSecond);
            adjustVisibleArea();
        } else if (e.getSource()==changeZoomDialog.magnifyLevelSlider){
            if (changeZoomDialog.magnifyLevelSlider.getValueIsAdjusting()) return;
            int v = changeZoomDialog.magnifyLevelSlider.getValue();
            double magnification = v / 10.0;
            timeViewer.setVerticalMagnify(magnification);
        }
        else if (e.getSource()==applicationFrame.mainPanel.textViewsTabbedPane){
            switchViews();
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
        java.awt.Point p = applicationFrame.mainPanel.zoomToggleButton.getLocationOnScreen();
        changeZoomDialog.setLocation(p.x, p.y + applicationFrame.mainPanel.zoomToggleButton.getHeight());    
        adjustVisibleArea();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        java.awt.Point p = applicationFrame.mainPanel.zoomToggleButton.getLocationOnScreen();
        changeZoomDialog.setLocation(p.x, p.y + applicationFrame.mainPanel.zoomToggleButton.getHeight());        
    }


    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getSource()==timeViewScrollPane.getHorizontalScrollBar()){
            if (timeViewScrollPane.getHorizontalScrollBar().getValueIsAdjusting()) return;
            adjustVisibleArea();
        }                
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
        timeViewer.setCursorTime(time);
        play();
    }
    
    @Override
    public void processTimepoint(Timepoint tp, MouseEvent e) {
        if (playerState!=ApplicationControl.PLAYER_IDLE){
            stop();
        }
        double start = tp.getTime();
        if (!(e.isShiftDown())){
            timeViewer.setCursorTime(start);
            play();            
        } else {
            timeViewer.setCursorTime(start);
            Timepoint tpEnd = tp.getTimeline().getNextTimepoint(tp);
            double end = this.player.getTotalLength();
            if (tpEnd!=null){
                end = tpEnd.getTime();
            }
            timeViewer.setSelectionInterval(start, end, false);
            this.playSelection();
        }
    }
    
    
    
    // WindowListener interface methods
    public void windowOpened(WindowEvent e) {
        // TODO
    }

    public void windowClosing(WindowEvent e) {
        if (e.getSource()==this.applicationFrame){
            toggleQuickTranscriptionDialog(false);
            exitApplication();
        }
        if (e.getSource()==this.quickTranscriptionDialog){
            applicationFrame.showQuickTranscriptionCheckBoxMenuItem.setSelected(false);
        }
        if (e.getSource()==this.matchListDialog){
            applicationFrame.showMatchListCheckBoxMenuItem.setSelected(false);
        }
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
        if (e.getSource()==this.applicationFrame){
            toggleQuickTranscriptionDialog(false);
            toggleMatchList(false);
        }
    }

    public void windowDeiconified(WindowEvent e) {
        // TODO
    }

    public void windowActivated(WindowEvent e) {
        // TODO
    }

    public void windowDeactivated(WindowEvent e) {
        // TODO
    }

    //***************************************************
    //***** SEARCH AND REPLACE FUNCTIONALITY ************
    //***************************************************

    public JDialog searchDialog;
    public org.exmaralda.folker.gui.SearchPanel searchPanel = new org.exmaralda.folker.gui.SearchPanel();

    private void initSearchDialog() {
        searchDialog = new JDialog(getFrame(), false);
        searchDialog.setTitle(FOLKERInternationalizer.getString("dialog.search"));
        searchDialog.add(searchPanel);
        searchDialog.pack();
        searchDialog.setLocationRelativeTo(getFrame());
        searchDialog.getRootPane().setDefaultButton(searchPanel.searchButton);
        searchPanel.setApplicationControl(this);
    }
    

    
    public void search(String searchExpression) {
        System.out.println("[Searching " + searchExpression + "]");
        getFrame().requestFocus();
        Pattern p;
        try {
            p = Pattern.compile(searchExpression);
        } catch (PatternSyntaxException pse){
            pse.printStackTrace();
            JOptionPane.showMessageDialog(getFrame(), pse.getLocalizedMessage());
            return;
        }
        switch (selectedPanelIndex){
            case 0 : // segment view
                eventListTable.requestFocusInWindow();
                int startRow = eventListTable.getSelectedRow();
                startRow = Math.max(0, startRow);
                int startPos = 0;
                if (eventListTable.isEditing()){
                    startPos = ((JTextField)(eventListTable.getEditorComponent())).getCaretPosition();
                    commitEdit();
                }
                //System.out.println("startRow=" + startRow + " startPos=" + startPos);
                int[] found = eventListTableModel.getTranscription().findInSegments(p, startRow, startPos);
                if (found==null){
                    JOptionPane.showMessageDialog(getFrame(), FOLKERInternationalizer.getString("option.endofdocument"));
                    status("'" + searchExpression + "'" + FOLKERInternationalizer.getString("status.notfound"));
                    return;
                }
                //System.out.println("Row=" + found[0] + " start=" + found[1] + " end=" + found[2]);

                eventListTable.getSelectionModel().clearSelection();
                eventListTable.getSelectionModel().setSelectionInterval(found[0], found[0]);
                eventListTable.makeVisible(eventListTable.getEventListTableModel().getTranscription().getEventAt(found[0]));
                eventListTable.editCellAt(found[0], 4);
                JTextField editorTextField = ((JTextField)(eventListTable.getEditorComponent()));
                editorTextField.requestFocusInWindow();
                editorTextField.setSelectionStart(found[1]);
                editorTextField.setSelectionEnd(found[2]);
                status(FOLKERInternationalizer.getString("status.found1") + editorTextField.getSelectedText() + FOLKERInternationalizer.getString("status.found2") + (found[0]+1) + FOLKERInternationalizer.getString("status.found3"));
                break;
            case 1 : // partitur view
                partitur.requestFocusInWindow();
                int startTier = partitur.selectionStartRow;
                startTier = Math.max(0, startTier);
                int startTLI = partitur.selectionStartCol;
                startTLI = Math.max(0, startTLI);
                int startPos2 = 0;
                if (partitur.isEditing){
                    startPos2 = ((JTextField)(partitur.getEditingComponent())).getCaretPosition();
                    commitEdit();
                }
                //System.out.println("startTier=" + startTier + " startTLI=" + startTLI + " startPos2=" + startPos2);

                int[] found2 = partitur.getModel().findInEvents(p, startTier, startTLI, startPos2);
                if (found2==null){
                    JOptionPane.showMessageDialog(getFrame(), FOLKERInternationalizer.getString("option.endofdocument"));
                    status("'" + searchExpression  + "'" + FOLKERInternationalizer.getString("status.notfound"));
                    return;
                }
                partitur.clearSelection();
                partitur.setNewSelection(found2[0], found2[1], true);
                JTextField editorTextField2 = ((JTextField)(partitur.getEditingComponent()));
                editorTextField2.requestFocusInWindow();
                editorTextField2.setSelectionStart(found2[2]);
                editorTextField2.setSelectionEnd(found2[3]);
                status(FOLKERInternationalizer.getString("status.found1") + editorTextField2.getSelectedText() + FOLKERInternationalizer.getString("status.found2") + (found2[0]+1) + FOLKERInternationalizer.getString("status.found3"));
                break;
            case 2 : // contribution view
                contributionListTable.requestFocusInWindow();
                int startRow3 = contributionListTable.getSelectedRow();
                startRow3 = Math.max(0, startRow3);
                int startPos3 = 0;
                if ((contributionTextPane.getText().length()>0) && (contributionTextPane.isEditable())){
                    startPos3 = contributionTextPane.getCaretPosition();
                    commitEdit();
                }
                int[] found3 = contributionListTableModel.getTranscription().findInContributions(p, startRow3, startPos3);
                if (found3==null){
                    JOptionPane.showMessageDialog(getFrame(), FOLKERInternationalizer.getString("option.endofdocument"));
                    status("'" + searchExpression + "'" + FOLKERInternationalizer.getString("status.notfound"));
                    return;
                }

                contributionListTable.getSelectionModel().clearSelection();
                contributionListTable.getSelectionModel().setSelectionInterval(found3[0], found3[0]);
                Contribution c = contributionListTableModel.getTranscription().getContributionAt(found3[0]);
                contributionListTable.makeVisible(c.eventlist.getEventAt(0));
                contributionTextPane.requestFocusInWindow();
                contributionTextPane.setSelectionStart(c.offsetToCaretPosition(found3[1]));
                contributionTextPane.setSelectionEnd(c.offsetToCaretPosition(found3[2]));
                status(FOLKERInternationalizer.getString("status.found1") + contributionTextPane.getSelectedText() + FOLKERInternationalizer.getString("status.found4") + (found3[0]+1) + FOLKERInternationalizer.getString("status.found3"));
                break;

        }
    }

    public void replace(String replaceExpression) {
        System.out.println("[Replacing " + replaceExpression + "]");
        getFrame().requestFocus();
        switch (selectedPanelIndex){
            case 0 : // segment view
                eventListTable.requestFocusInWindow();
                if (!eventListTable.isEditing()) return;
                JTextField textField = ((JTextField)(eventListTable.getEditorComponent()));
                if (textField.getSelectionStart()==textField.getSelectionEnd()) return;
                String before = textField.getSelectedText();
                textField.replaceSelection(replaceExpression);
                status("'" + before + "'" + FOLKERInternationalizer.getString("status.replaced") + "'" + replaceExpression + "'");
                DOCUMENT_CHANGED = true;
                break;
            case 1 : // partitur view
                partitur.requestFocusInWindow();
                if (!partitur.isEditing) return;
                JTextField textField2 = ((JTextField)(partitur.getEditingComponent()));
                if (textField2.getSelectionStart()==textField2.getSelectionEnd()) return;
                String before2 = textField2.getSelectedText();
                textField2.replaceSelection(replaceExpression);
                status("'" + before2 + "'" + FOLKERInternationalizer.getString("status.replaced") + "'" + replaceExpression + "'");
                DOCUMENT_CHANGED = true;
                break;
            case 2 : // contribution view
                eventListTable.requestFocusInWindow();
                if (!contributionTextPane.isEditable()) return;
                if (!(contributionTextPane.getText().length()>0)) return;
                if (contributionTextPane.getSelectionStart()==contributionTextPane.getSelectionEnd()) return;
                //System.out.println("===>" + contributionTextPane.getSelectedText());
                contributionTextPane.replaceSelection(replaceExpression);
                contributionTextPane.validateContribution();
                this.validateContribution();
                break;
        }

    }

    public void replaceAll(String searchExpression, String replaceExpression) {
        commitEdit();
        switch (selectedPanelIndex){
            case 0 : // segment view
                int howMany = eventListTableModel.getTranscription().replaceAllInSegments(searchExpression, replaceExpression);
                eventListTableModel.fireTableDataChanged();
                JOptionPane.showMessageDialog(getFrame(), FOLKERInternationalizer.getString("option.replaceall1") + howMany + FOLKERInternationalizer.getString("option.replaceall2"));
                status("'" + searchExpression + "'" + FOLKERInternationalizer.getString("status.replaceall1") + "'" + replaceExpression + FOLKERInternationalizer.getString("status.replaceall2") + howMany + FOLKERInternationalizer.getString("status.replaceall3") );
                break;
            case 1 : // partitur view
                int howMany2 = partitur.getModel().getTranscription().replaceAllInEvents(searchExpression, replaceExpression);
                partitur.getModel().fireFormatReset();
                JOptionPane.showMessageDialog(getFrame(), FOLKERInternationalizer.getString("option.replaceall1") + howMany2 + FOLKERInternationalizer.getString("option.replaceall2"));
                status("'" + searchExpression + "'" + FOLKERInternationalizer.getString("status.replaceall1") + "'" + replaceExpression + FOLKERInternationalizer.getString("status.replaceall2") + howMany2 + FOLKERInternationalizer.getString("status.replaceall3") );
                break;
            case 2 : // contribution view
                JOptionPane.showMessageDialog(getFrame(),
                        FOLKERInternationalizer.getString("option.noreplace1") + "\n" +
                        FOLKERInternationalizer.getString("option.noreplace2") + "\n" +
                        FOLKERInternationalizer.getString("option.noreplace3") + "\n" +
                        FOLKERInternationalizer.getString("option.noreplace4"));
                break;
        }
    }

    @Override
    public JToggleButton getZoomToggleButton() {
        return applicationFrame.mainPanel.zoomToggleButton;
    }

    // processes a match from a match list
    public void processMatch(Element matchElement, File workingDir) {
        String filename = matchElement.getAttributeValue("tra") + ".fln";
        File transcriptionFile = new File(workingDir, filename);
        if ((currentFilePath==null) || (!(currentFilePath.equals(transcriptionFile.getAbsolutePath())))){
            checkSave();
            openTranscriptionFile(transcriptionFile);
        }
        
       //TODO: Scroll to the right position
        double time = Double.parseDouble(matchElement.getAttributeValue("time")) * 1000.0;
        String speakerID = matchElement.getAttributeValue("speaker");
        scrollToTime(time, speakerID);
    }

    private void scrollToTime(double time, String speakerID) {
        scrollToTime(time, speakerID, true);
    }
    private void scrollToTime(double time, String speakerID, boolean select) {
                commitEdit();
        switch (selectedPanelIndex){
            case 0 : // segment view
                int index = getTranscription().findFirstIndexForTime(time);
                if (select){
                    eventListTable.getSelectionModel().setSelectionInterval(index, index);
                }                
                eventListTable.scrollRectToVisible(eventListTable.getCellRect(Math.min(index+7, eventListTableModel.getRowCount()-1), 0, true));
                break;
            case 1 : // partitur view
                int index3 = getBasicTranscription().getBody().getCommonTimeline().getPositionForTime(time / 1000.0);
                partitur.setLeftColumn(index3);
                break;
            case 2 : // contribution view
                int index2 = getTranscription().findFirstContributionIndexForTime(time);
                if (select){
                    contributionListTable.getSelectionModel().setSelectionInterval(index2, index2);                
                }
                contributionListTable.scrollRectToVisible(contributionListTable.getCellRect(Math.min(index2+3, contributionListTableModel.getRowCount()-1), 0, true));
                break;
        }

    }

    public void mask() {
        try {            
            MaskFileDialog maskFileDialog = new MaskFileDialog(applicationFrame, true);
            int initMethod = PreferencesUtilities.getIntegerProperty("mask-method", -1);
            maskFileDialog.setMethod(initMethod);
            maskFileDialog.setFile(getTranscription().getMediaPath());
            maskFileDialog.setLocationRelativeTo(applicationFrame);
            maskFileDialog.setVisible(true);
            if (!maskFileDialog.approved) return;
            
            final File in = maskFileDialog.getSourceFile();
            final File out = maskFileDialog.getTargetFile();
            final int method = maskFileDialog.getMethod();
            
            PreferencesUtilities.setProperty("mask-method", Integer.toString(method));
            
            // TODO: Security checks for both files
            if (!(in.canRead())){
                JOptionPane.showMessageDialog(applicationFrame, 
                        in.getAbsolutePath() + "\n" +
                        FOLKERInternationalizer.getString("option.cannotaccessfile"));
            }            
            if (out.exists()){
                int retVal = JOptionPane.showConfirmDialog( applicationFrame, 
                                                            FOLKERInternationalizer.getString("option.fileexists"),
                                                            FOLKERInternationalizer.getString("option.confirmation"),
                                                            JOptionPane.YES_NO_OPTION);
                if (retVal!=JOptionPane.YES_OPTION) return;                 
                if (!(out.canWrite())){
                    JOptionPane.showMessageDialog(applicationFrame, 
                            in.getAbsolutePath() + "\n" +
                            FOLKERInternationalizer.getString("option.cannotaccessfile"));
                }
            }
            
            // Do the actual masking
            final Masker masker = new Masker(in, out);
            final double[][] maskTimes = MaskTimeCreator.createTimesFromFOLKERTranscriptionHead(transcriptionHead);
            
            
            final MaskerProgressDialog pbd = new MaskerProgressDialog(applicationFrame, false); 
            pbd.setTitle(FOLKERInternationalizer.getString("masker.progressdialogtitle"));
            pbd.setLocationRelativeTo(applicationFrame);
            pbd.setVisible(true);
            masker.addMaskerListener(pbd);
            final Runnable doDisplayMaskDone = new Runnable() {
                    @Override
                    public void run() {
                        maskDone(in, out);
                    }
            };
            Thread maskThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                                masker.mask(method, maskTimes); 
                                pbd.setVisible(false);
                                try {
                                        javax.swing.SwingUtilities.invokeAndWait(doDisplayMaskDone);
                                } catch (Exception ex) {
                                        ex.printStackTrace();
                                }
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                    }
            };
            maskThread.start();

        } catch (IOException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
            displayException(ex);
        } catch (WavFileException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
            displayException(ex);
        } catch (URISyntaxException ex){
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
            displayException(ex);
        } catch (ClassNotFoundException ex){
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);            
            displayException(ex);
        }
        
    }
    
    void maskDone(File in, File out) {
        String text = 
                FOLKERInternationalizer.getString("masker.accomplished") + "\n" 
                + FOLKERInternationalizer.getString("masker.sourcefile") + ": " + in.getAbsolutePath() + "\n"
                + FOLKERInternationalizer.getString("masker.targetfile") + ": " + out.getAbsolutePath() + "\n"
                + FOLKERInternationalizer.getString("masker.switchfile");
        int choice = JOptionPane.showConfirmDialog(applicationFrame, text, FOLKERInternationalizer.getString("masker.accomplished"), JOptionPane.YES_NO_OPTION);
        if (choice==JOptionPane.YES_OPTION){
            try {
                this.setMedia(out.getAbsolutePath());
            } catch (IOException ex) {
                displayException(ex);
            }
        }
    }


    
    
    
}
