/*
 * PartitureTableWithActions.java
 *
 * Created on 20. August 2001, 15:21
 */

package org.exmaralda.partitureditor.partiture;

import org.exmaralda.partitureditor.partiture.legacyActions.ExSyncEventShrinkerAction;
import org.exmaralda.partitureditor.partiture.legacyActions.ImportActionInel;
import org.exmaralda.partitureditor.partiture.legacyActions.StadtspracheWordSegmentationAction;
import org.exmaralda.partitureditor.partiture.legacyActions.StadtspracheTierSegmentationAction;
import org.exmaralda.partitureditor.partiture.legacyActions.MakeSyllableStructureTierAction;
import org.exmaralda.partitureditor.partiture.legacyActions.AppendSpaceAction;
import org.exmaralda.partitureditor.partiture.legacyActions.ExSyncCleanupAction;
import org.exmaralda.partitureditor.partiture.legacyActions.K8MysteryConverterAction;
import org.exmaralda.folker.timeview.TimeSelectionEvent;
import org.exmaralda.partitureditor.interlinearText.PrintParameters;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.SaveBasicTranscriptionAsDialog;

import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.linkPanel.*;
import org.exmaralda.partitureditor.praatPanel.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.sound.*;

import org.exmaralda.partitureditor.partiture.formatActions.*;
import org.exmaralda.partitureditor.partiture.fileActions.*;
import org.exmaralda.partitureditor.partiture.editActions.*;
import org.exmaralda.partitureditor.partiture.viewActions.*;
import org.exmaralda.partitureditor.partiture.transcriptionActions.*;
import org.exmaralda.partitureditor.partiture.tierActions.*;
import org.exmaralda.partitureditor.partiture.timelineActions.*;
import org.exmaralda.partitureditor.partiture.eventActions.*;


import org.exmaralda.partitureditor.partiture.menus.EventPopupMenu;
import org.exmaralda.partitureditor.partiture.menus.TablePopupMenu;

import com.klg.jclass.table.*;
import java.awt.Desktop;
import java.awt.Font;

import javax.swing.*;

import java.util.*;
import java.io.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.xml.parsers.ParserConfigurationException;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.helpers.Internationalizer;
import org.exmaralda.folker.timeview.TimeSelectionListener;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.partiture.legacyActions.LegacyExportAction;
import org.exmaralda.partitureditor.partiture.legacyActions.LegacyImportAction;
import org.exmaralda.partitureditor.partiture.legacyActions.LegacyOutputAction;
import org.exmaralda.partitureditor.partiture.webServicesActions.WebLichtAction;
import org.exmaralda.partitureditor.partiture.webServicesActions.WebMAUSAction;
import org.exmaralda.partitureditor.partiture.webServicesActions.WebMAUSFineAlignmentAction;
import org.exmaralda.partitureditor.partiture.undo.RestoreCellInfo;
import org.exmaralda.partitureditor.partiture.undo.UndoHandler;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;
import org.exmaralda.partitureditor.partiture.webServicesActions.DeepLAction;
import org.exmaralda.partitureditor.partiture.webServicesActions.G2PAction;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;


/**
 * provides javax.swing.AbstractActions for all possible operations on a partiture-table / Basic transcription
 * implements listeners to table events that take care of enableing / disableing possible actions
 * contains the popup menus of the application 
 * takes care of storing and loading most of the user preferences
 * @author  Thomas Schmidt, thomas.schmidt@uni-hamburg.de
 * @version 1.0
 */

public class PartitureTableWithActions extends PartitureTable 
                implements  JCEditCellListener,
                            JCSelectListener, 
                            DocumentListener, 
                            PopupMenuListener, 
                            /* SegmentationPanelListener, */
                            PraatPanelListener,
                            org.exmaralda.partitureditor.search.SearchResultListener, 
                            org.exmaralda.partitureditor.jexmaralda.errorChecker.ErrorCheckerListener,
                            org.exmaralda.partitureditor.svgPanel.XPointerListener, 
                            java.awt.event.MouseWheelListener,
                            java.awt.event.MouseListener,
                            org.exmaralda.partitureditor.sound.PlayableListener,
                            TimeSelectionListener {
    

    /** added 04-02-2010: holds the current selection length in seconds */
    public double currentSelectionLength = 0.0;
    public double currentSelectionStart = -1.0;
    public double currentSelectionEnd = -1.0;

    /** determines whether certain actions auto-generate absolute timestamps
     * when the transcription is associated with a media file
     */
    public boolean AUTO_ANCHOR = true;
    /** determines whether unused timeline items are removed automatically
     * after merge
     */
    public boolean AUTO_REMOVE_UNUSED_TLI = true;


    /** true if the user is currently editing an event in the partitur */
    public boolean isEditing = false;
    /** true iff the currently selected cell has span one */
    public boolean currentCellHasSpanOne;
    /** true iff a single cell is selected */
    public boolean aSingleCellIsSelected;
    /** true iff more than one cell in a single tier is selected */
    public boolean aSeriesOfCellsIsSelected;
    /** true iff the current user selection spans multiple columns */
    public boolean multipleColumnsSelected = false;
    public boolean aWholeColumnIsSelected;
    public boolean aSeriesOfColumnsIsSelected;
    public boolean aRectangleOfEventsIsSelected;    


    /** true iff the grid in the partitur is hidden */
    public boolean gridIsHidden = false;
    
    /** the last registered caret position in a cell editor */
    public int lastRegisteredCharPosition;
    
    /** the current transcription file name */
    public String filename = "";
    /** the default directory for opening and saving transcriptions */
    public String homeDirectory = "";
    /** the default directory for RTF export */
    public String rtfDirectory = "";
    /** the default directory for HTML exports */
    public String htmlDirectory = "";
    /** the default directory for Simple EXMARaLDA imports */
    public String txtDirectory = "";
    /** the default directory for CHAT exports */
    public String chatDirectory = "";
    /** the default name for the HTML file created by the "Send to Browser" action */
    public String standardHTMLName = "";
    /** list of recently used files */
    public java.util.Vector<String> recentFiles = new java.util.Vector<String>();
    /** paths for user defined keyboards */
    public String[] externalKeyboardPaths = new String[0];
    
    /** the name of the default font for new tiers */
    public String defaultFontName = "Times New Roman";
    /** path to and name of the stylesheet used for HTML output of meta-information and speakertable */
    public String head2HTMLStylesheet = "";
    /** path to and name of the stylesheet used for the "Speakertable to New" action */
    public String speakertable2TranscriptionStylesheet = "";
    /** path to and name of the stylesheet used for the "Format --> Apply stylesheet" action */
    public String transcription2FormattableStylesheet = "";
    public String freeStylesheetVisualisationStylesheet = "";
    public String HIATUtteranceList2HTMLStylesheet = "";
    
    public String preferredSegmentation = "";
    /** path to the FSM used for HIAT segmentation (optional) */
    public String hiatFSM = "";
    /** path to the FSM used for DIDA segmentation (optional) */
    public String didaFSM = "";
    /** path to the FSM used for GAT segmentation (optional) */
    public String gatFSM = "";
    /** path to the FSM used for CHAT segmentation (optional) */
    public String chatFSM = "";
    /** path to the FSM used for IPA segmentation (optional) */
    public String ipaFSM = "";
    /** path to the FSM used for generic segmentation (optional) */
    public String genericFSM = "";
    /** path to the FSM used for generic segmentation (optional) */
    public String cGATMinimalFSM = "";
    
    /** The separate thread in which auto saving takes place */
    public AutoSave autoSaveThread;
    /** Switches auto save on/off */
    public boolean autoSave = false;
    /** determines whether to use diacritics for underlining */
    public boolean underlineWithDiacritics = false;
    /** determines the category to use for underlining tiers */
    public String underlineCategory = "akz";
    /** determines whether or not to save the tier format table with the transcriptio */
    public boolean saveTierFormatTable = false;
    public boolean prettyPrint = false;

    /** the page format used for printing */
    public java.awt.print.PageFormat pageFormat = new java.awt.print.PageFormat();
    /** the parameters used for printing */
    public org.exmaralda.partitureditor.interlinearText.PrintParameters printParameters = new org.exmaralda.partitureditor.interlinearText.PrintParameters();
    /** the parameters used for RTF export */
    public org.exmaralda.partitureditor.interlinearText.RTFParameters rtfParameters = new org.exmaralda.partitureditor.interlinearText.RTFParameters();
    /** the parameters used for HTML export */
    public org.exmaralda.partitureditor.interlinearText.HTMLParameters htmlParameters = new org.exmaralda.partitureditor.interlinearText.HTMLParameters();
    /** the parameters used for SVG export */
    public org.exmaralda.partitureditor.interlinearText.SVGParameters svgParameters = new org.exmaralda.partitureditor.interlinearText.SVGParameters();
    
    public String language = "en";
    
    Vector<PartitureTableListener> thisListenerList = new Vector<>();
    
    public java.awt.Font currentFont;
    public javax.swing.JTextField dummyTextField = new javax.swing.JTextField();
    PartitureCellStringEditor editor;
    
    /** a reference to the container (i.e. the JFrame extension that is the partitur editor */
    public java.awt.Frame parent;

    public StatusPanel statusPanel = null;
    PartiturEditorInfoPanel infoPanel = null;
    
    
    /** the popup menu used for editing events */
    public EventPopupMenu eventPopupMenu;
    
    /** the popup menu used for editing the partitur */
    public TablePopupMenu tablePopupMenu;
    
    // new 11-01-2019, issue #176
    public JMenu moveMenu = new JMenu(Internationalizer.getString("Move to tier"));
    
    public java.awt.Color defaultSelectionBg;
    public java.awt.Color defaultSelectionColor;

    public org.exmaralda.partitureditor.sound.Playable player;

    boolean cancelEdit = false;

    // pause notation
    public String pausePrefix = "((";
    public String pauseSuffix = "s))";
    public int pauseDigits = 1;
    public boolean pauseDecimalComma = true;
    
    // ************************************************************
    // ***********     Action Declaration *************************
    // ************************************************************
    
    /** Action for creating a new transcription file (Menu File --> New) */
    public javax.swing.AbstractAction newAction;
    /** Action for creating a new transcription file from a speakertable (Menu File --> New from speakertable)*/
    public javax.swing.AbstractAction newFromWizardAction;
    /** Action for creating a new transcription file from a speakertable (Menu File --> New from speakertable)*/
    public javax.swing.AbstractAction newFromSpeakertableAction;
    /** Action for creating a new transcription file from a timeline (Menu File --> New from timeline)*/
    public javax.swing.AbstractAction newFromTimelineAction;
    /** Action for creating a new transcription file from a silence detection (Menu File --> New from silence detection)*/
    public javax.swing.AbstractAction newFromSilenceDetectionAction;
    /** Action for opening an existing transcription file (Menu File --> Open)*/
    public javax.swing.AbstractAction openAction;
    /** Action for restoring the last saved version of the current transcription file (Menu File --> Restore)*/    
    public javax.swing.AbstractAction restoreAutoBackupAction;
    /** Action for restoring the last saved version of the current transcription file (Menu File --> Restore)*/    
    public javax.swing.AbstractAction restoreAction;
    /** Action for saving the current transcription file under its current name (Menu File --> Save)*/
    public javax.swing.AbstractAction saveAction;
    /** Action for saving the current transcription file under a new name (Menu File --> Save As)*/
    public javax.swing.AbstractAction saveAsAction;
       
    /** Action for editing the page setup for the current transcription */
    public javax.swing.AbstractAction pageSetupAction;
    /** Action for sending the current transcription to a printer */
    public javax.swing.AbstractAction printAction;
    /** Action for editing the partitur parameters */
    public javax.swing.AbstractAction editPartiturParametersAction;
    
    public javax.swing.AbstractAction sendHTMLPartitureToBrowserAction;
    /** Action for outputting transcription into some visualisation format */
    public javax.swing.AbstractAction outputAction;
    /** Action for importing some 3rd party format to EXMARaLDA */
    public javax.swing.AbstractAction importAction;
    public javax.swing.AbstractAction importActionInel;
    /** Action for exporting the transcription to some 3rd party format */
    public javax.swing.AbstractAction exportAction;
    //*******************************************************************

    /* Edit menu actions */
    public javax.swing.AbstractAction undoAction;
    /** Action for copying currently selected text to the clipboard */
    public javax.swing.AbstractAction copyTextAction;
    /** Action for copying currently selected text to the clipboard as HTML (#338)*/
    public javax.swing.AbstractAction copyHTMLAction;
    /** Action for pasting the clipboard at the current cursor position */
    public javax.swing.AbstractAction pasteAction;
    /** Action for cutting currently selected text to the clipboard */
    public javax.swing.AbstractAction cutAction;

    public javax.swing.AbstractAction searchInEventsAction;
    public javax.swing.AbstractAction findNextAction;
    public javax.swing.AbstractAction replaceInEventsAction;
    public javax.swing.AbstractAction gotoAction;
    // #274
    public javax.swing.AbstractAction lowerUpperCaseAction;

    public javax.swing.AbstractAction exaktSearchAction;    
    
    public javax.swing.AbstractAction selectionToNewAction;
    public javax.swing.AbstractAction leftPartToNewAction;
    public javax.swing.AbstractAction rightPartToNewAction;
    /*public javax.swing.AbstractAction selectionToHTMLAction;
    public javax.swing.AbstractAction selectionToRTFAction;
    public javax.swing.AbstractAction printSelectionAction;*/
    
    public javax.swing.AbstractAction glueTranscriptionsAction;
    public javax.swing.AbstractAction mergeTranscriptionsAction;
    public javax.swing.AbstractAction chopTranscriptionAction;
    public javax.swing.AbstractAction cleanupAction;
    public javax.swing.AbstractAction editErrorsAction;

    public javax.swing.AbstractAction chopAudioAction;
    public javax.swing.AbstractAction maskAudioAction;

    public javax.swing.AbstractAction editPreferencesAction;

    //*******************************************************************

    /* View menu actions */
    public javax.swing.AbstractAction showHideGridAction;
    public javax.swing.AbstractAction changeScaleConstantAction;
    public javax.swing.AbstractAction showSpecialCharactersAction;

    //*******************************************************************

    /* Transcription menu actions */

    /** Action for editing the meta information of the current transcription (Menu File --> Meta information)*/
    public javax.swing.AbstractAction editMetaInformationAction;
    /** Action for editing the speakertable of the current transcription (Menu File --> Speakertable) */
    public javax.swing.AbstractAction editSpeakertableAction;
    /** Action for editing the recordings of the current transcription (Menu File --> Recordings) */
    public javax.swing.AbstractAction editRecordingsAction;

    public javax.swing.AbstractAction getStructureErrorsAction;
    public javax.swing.AbstractAction calculateTimeAction;
    public javax.swing.AbstractAction getSegmentationErrorsAction;
    public javax.swing.AbstractAction segmentAndSaveTranscriptionAction;
    public javax.swing.AbstractAction countAction;
    public javax.swing.AbstractAction wordListAction;
    public javax.swing.AbstractAction transformationAction;
    public javax.swing.AbstractAction autoAnnotationAction;
    public javax.swing.AbstractAction insertUtteranceNumbersAction;
    public javax.swing.AbstractAction addTokenLayerAction;

    
    //*******************************************************************

    /* Tier menu actions */
    
    public javax.swing.AbstractAction editTierAction;
    public javax.swing.AbstractAction addTierAction;
    public javax.swing.AbstractAction insertTierAction;
    public javax.swing.AbstractAction removeTierAction;
    public javax.swing.AbstractAction moveTierUpAction;
    public javax.swing.AbstractAction changeTierOrderAction;
    public javax.swing.AbstractAction hideTierAction;
    public javax.swing.AbstractAction showAllTiersAction;
    public javax.swing.AbstractAction removeEmptyEventsAction;
    public javax.swing.AbstractAction editTiersAction;
    public javax.swing.AbstractAction listEventsAction; // issue #316
    public javax.swing.AbstractAction typesAction; // issue #295

    //*******************************************************************

    /* Event menu actions */
    
    public javax.swing.AbstractAction deleteEventAction;
    public javax.swing.AbstractAction deleteEventsAction;

    public javax.swing.AbstractAction extendRightAction;
    public javax.swing.AbstractAction extendLeftAction;
    public javax.swing.AbstractAction shrinkRightAction;
    public javax.swing.AbstractAction shrinkLeftAction;
    public javax.swing.AbstractAction moveRightAction;
    public javax.swing.AbstractAction moveLeftAction;
    public javax.swing.AbstractAction shiftRightAction;
    public javax.swing.AbstractAction shiftLeftAction;
    public javax.swing.AbstractAction mergeAction;
    public javax.swing.AbstractAction splitAction;
    public javax.swing.AbstractAction doubleSplitAction;
    public javax.swing.AbstractAction editEventAction;
    public javax.swing.AbstractAction findNextEventAction;
    public javax.swing.AbstractAction splitLongEventAction;

    public javax.swing.AbstractAction insertPauseAction;
    
    // 24-06-2016 MuM-Multi new 
    public javax.swing.AbstractAction moveDownLeftAction;
    public javax.swing.AbstractAction moveDownRightAction;
    


    //*******************************************************************

    /* Timeline menu actions */
    
    public javax.swing.AbstractAction editTimelineItemAction;
    public javax.swing.AbstractAction insertTimelineItemAction;
    public javax.swing.AbstractAction anchorTimelineItemAction;
    public javax.swing.AbstractAction smoothTimelineAction;
    public javax.swing.AbstractAction removeGapAction;
    public javax.swing.AbstractAction removeAllGapsAction;
    public javax.swing.AbstractAction removeUnusedTimelineItemsAction;
    public javax.swing.AbstractAction makeTimelineConsistentAction;
    public javax.swing.AbstractAction completeTimelineAction;
    public javax.swing.AbstractAction removeInterpolatedTimesAction;
    public javax.swing.AbstractAction removeTimesAction;
    public javax.swing.AbstractAction confirmTimelineItemAction;
    public javax.swing.AbstractAction shiftAbsoluteTimesAction;
    public javax.swing.AbstractAction addBookmarkAction;
    public javax.swing.AbstractAction bookmarksAction;
    public javax.swing.AbstractAction easyAlignmentAction;

    //*******************************************************************

    /* Format menu actions */

    public javax.swing.AbstractAction reformatAction;

    public javax.swing.AbstractAction openTierFormatTableAction;
    public javax.swing.AbstractAction saveTierFormatTableAction;
    public javax.swing.AbstractAction applyFormatStylesheetAction;    
    public javax.swing.AbstractAction editTierFormatTableAction;
    public javax.swing.AbstractAction editTierFormatAction;
    public javax.swing.AbstractAction editRowLabelFormatAction;
    public javax.swing.AbstractAction editColumnLabelFormatAction;
    public javax.swing.AbstractAction editTimelineItemFormatAction;

    public javax.swing.AbstractAction setFrameEndAction;

    public javax.swing.AbstractAction underlineAction;
    
    //*******************************************************************
    //*******************************************************************
    //*******************************************************************
    /* Legacy menu actions */
    
    /* File */
    public javax.swing.AbstractAction legacyImportAction;
    public javax.swing.AbstractAction legacyExportAction;
    public javax.swing.AbstractAction legacyOutputAction;

    /* SFB 538 */
    public javax.swing.AbstractAction syllableStructureAction;
    public javax.swing.AbstractAction k8MysteryConverterAction;
    public javax.swing.AbstractAction exSyncEventShrinkerAction;
    public javax.swing.AbstractAction exSyncCleanupAction;
    /* SFB 632 */
    public javax.swing.AbstractAction appendSpaceAction;
    
    /* SiN */
    public javax.swing.AbstractAction stadtspracheWordSegmentationAction;
    public javax.swing.AbstractAction stadtspracheTierSegmentationAction;
    
    // ************************************************************
       
    /* Web Services */
    public javax.swing.AbstractAction webMAUSAction;
    public javax.swing.AbstractAction webMAUSFineAlignmentAction;
    public javax.swing.AbstractAction g2pAction;
    public javax.swing.AbstractAction webLichtAction; 
    public javax.swing.AbstractAction deepLAction; 
    
    //*******************************************************************

    /* Navigation actions */
    public javax.swing.AbstractAction jumpToStartAction;
    public javax.swing.AbstractAction jumpToEndAction;
    public javax.swing.AbstractAction scrollForwardsAction;
    public javax.swing.AbstractAction scrollBackwardsAction;
    
    
    // ************************************************************
    // ************************************************************
    

    public PartitureTableWithActions(java.awt.Frame p){
        this(p, true);
    }
    
    /** Creates new PartitureTableWithActions
     *  p specifies the parent component
     * @param p
     * @param initKeystrokes */
    public PartitureTableWithActions(java.awt.Frame p, boolean initKeystrokes){
        super();
        defaultSelectionBg = this.getSelectedBackground();
        defaultSelectionColor = this.getSelectedForeground();
        parent = p;        

        player = makePlayer();

        initActions();
        addSelectListener(this);
        addEditCellListener(this);        
        setFilename("untitled.exb");
        initDialogs();
        if (initKeystrokes){
            initKeystrokes();
        }

        eventPopupMenu = new EventPopupMenu(this);
        eventPopupMenu.addPopupMenuListener(this);
        
        tablePopupMenu = new org.exmaralda.partitureditor.partiture.menus.TablePopupMenu(this);
        tablePopupMenu.addPopupMenuListener(this);
                
        pageFormat = java.awt.print.PrinterJob.getPrinterJob().defaultPage();
        homeDirectory = new String();
        rtfDirectory = new String();
        htmlDirectory = new String();
        txtDirectory = new String();
        chatDirectory = new String();
        standardHTMLName = System.getProperty("user.home") + System.getProperty("file.separator") + "EXMARaLDA_HTML_OUTPUT.HTML";
        printParameters.setPaperMeasures(pageFormat);
        rtfParameters.setPaperMeasures(pageFormat);             
        
        autoSaveThread = new AutoSave(getModel().getTranscription());
        try {            
            // set the cell renderer for timeline items
            timelineItemTableCellRenderer = new org.exmaralda.partitureditor.partiture.TimelineItemTableCellRenderer(this);
            setCellRenderer(Class.forName("org.exmaralda.partitureditor.jexmaralda.TimelineItem"), timelineItemTableCellRenderer);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }


    public String getFSMForPreferredSegmentation() {
        if (preferredSegmentation.equals("HIAT")) return hiatFSM;
        if (preferredSegmentation.equals("GAT")) return gatFSM;
        if (preferredSegmentation.equals("CHAT")) return chatFSM;
        if (preferredSegmentation.equals("DIDA")) return didaFSM;
        if (preferredSegmentation.equals("GENERIC")) return genericFSM;
        if (preferredSegmentation.equals("cGAT_MINIMAL")) return cGATMinimalFSM;
        return "";
    }

    public AbstractSegmentation getAbstractSegmentation() {
        return getAbstractSegmentation(preferredSegmentation);
    }

    public AbstractSegmentation getAbstractSegmentation(String preferredSegmentation) {
        if (preferredSegmentation.equals("HIAT"))
            return new org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation(hiatFSM);
        if (preferredSegmentation.equals("GAT"))
            return new org.exmaralda.partitureditor.jexmaralda.segment.GATSegmentation(gatFSM);
        if (preferredSegmentation.equals("cGAT_MINIMAL"))
            return new org.exmaralda.partitureditor.jexmaralda.segment.cGATMinimalSegmentation(cGATMinimalFSM);
        if (preferredSegmentation.equals("CHAT"))
            return new org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation(chatFSM);
        if (preferredSegmentation.equals("CHAT_MINIMAL"))
            return new org.exmaralda.partitureditor.jexmaralda.segment.CHATMinimalSegmentation();
        if (preferredSegmentation.equals("DIDA"))
            return new org.exmaralda.partitureditor.jexmaralda.segment.DIDASegmentation(didaFSM);
        if (preferredSegmentation.equals("IPA"))
            return new org.exmaralda.partitureditor.jexmaralda.segment.IPASegmentation();
        if (preferredSegmentation.equals("GENERIC"))
            return new org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation(genericFSM);
        return new org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation();
    }


    // *********************************************************************************** //
    // ************************ ACTION INITIALISATIONS *********************************** //
    // *********************************************************************************** //
    
    
    /** intialises the actions associated with menu items and toolbar buttons */
    private void initActions(){
        
        //*********************************************************************************************
        //*************************************** FILE ACTIONS ****************************************
        //*********************************************************************************************

        newAction = new NewAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/New.gif")));
        newFromWizardAction = new NewFromWizardAction(this, null);
        newFromSpeakertableAction = new NewFromSpeakertableAction(this, null);
        newFromTimelineAction = new NewFromTimelineAction(this);
        newFromSilenceDetectionAction = new NewFromSilenceDetectionAction(this);
        openAction = new OpenAction(this,new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Open.gif")));               
        restoreAction = new RestoreAction(this);
        restoreAutoBackupAction = new RestoreAutoBackupAction(this);
        saveAction = new SaveAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Save.gif")));
        saveAsAction = new SaveAsAction (this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/SaveAs.gif")));

        editMetaInformationAction = new EditMetaInformationAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/EditMetaInformation.gif")));
        editSpeakertableAction = new EditSpeakertableAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/speakertable.png")));
        editRecordingsAction = new EditRecordingsAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/22x22/mimetypes/video-x-generic.png")));

        pageSetupAction = new PageSetupAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/PageSetup.gif")));
        editPartiturParametersAction = new EditPartiturParametersAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/PageSetup.gif")));
        printAction = new PrintAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Print.gif")));
        
        sendHTMLPartitureToBrowserAction = new SendHTMLPartitureToBrowserAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/22x22/apps/internet-web-browser.png")));
        
        outputAction = new OutputAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/22x22/mimetypes/x-office-presentation.png")));
        importAction = new ImportAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Import.gif")));
        exportAction = new ExportAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Export.gif")));
        
        //*********************************************************************************************
        //*************************************** EDIT ACTIONS ****************************************
        //*********************************************************************************************
        
        undoAction = new UndoAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/actions/edit-undo.png")));

        copyTextAction = new CopyTextAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Copy.gif")));
        // new for #338
        copyHTMLAction = new CopyHTMLAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Copy.gif")));
        pasteAction = new PasteAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Paste.gif")));
        cutAction = new CutAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Cut.gif")));

        searchInEventsAction = new SearchInEventsAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Search.gif")));
        findNextAction = new FindNextAction(this);
        replaceInEventsAction = new ReplaceInEventsAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Replace.gif")));
        gotoAction = new GotoAction(this);
        // <div>Icons made by <a href="https://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
        lowerUpperCaseAction = new LowerUpperCaseAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/upper-and-lower-case-a-letter.png")));

        exaktSearchAction = new ExaktSearchAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/exakt_small.png")));

        selectionToNewAction = new SelectionToNewAction(this, null);
        leftPartToNewAction = new LeftPartToNewAction(this, null);
        rightPartToNewAction = new RightPartToNewAction(this, null);
        
        editPreferencesAction = new EditPreferencesAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Settings.gif")));

        mergeTranscriptionsAction = new MergeTranscriptionsAction(this, null);
        glueTranscriptionsAction = new GlueTranscriptionsAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Pattex.gif")));
        chopTranscriptionAction = new ChopTranscriptionAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Chop.gif")));
        cleanupAction = new CleanupAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Cleanup.png")));
        editErrorsAction = new EditErrorsAction(this);        
        
        chopAudioAction = new ChopAudioAction(this);
        maskAudioAction = new MaskAudioAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/othericons/mask_black.png")));
        
        //*********************************************************************************************
        //*************************************** VIEW ACTIONS ****************************************
        //*********************************************************************************************

        changeScaleConstantAction = new ChangeScaleConstantAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/EditScaleConstant.gif")));        
        showSpecialCharactersAction = new ShowSpecialCharactersAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/ShowSpecialCharacters.gif")));

        // All other view actions are implemented in menu classes, because
        // I'm too stupid to connect a JCheckBoxMenuItem with an action
        
        //*********************************************************************************************
        //*********************************** TRANSCRIPTION ACTIONS ***********************************
        //*********************************************************************************************

        getStructureErrorsAction = new GetStructureErrorsAction(this);
        calculateTimeAction = new CalculateTimeAction(this);
        getSegmentationErrorsAction = new GetSegmentationErrorsAction(this);
        segmentAndSaveTranscriptionAction = new SegmentAndSaveTranscriptionAction(this);
        countAction = new CountAction(this);
        wordListAction = new WordlistAction(this);
        //transformationAction = new TransformationAction(this);
        transformationAction = new TransformationAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Transform.gif")));
        insertUtteranceNumbersAction = new InsertHIATUtteranceNumbersAction(this);
        autoAnnotationAction = new AutoAnnotationAction(this);
        addTokenLayerAction = new AddTokenLayerAction(this);
        
        //*********************************************************************************************
        //*************************************** TIER ACTIONS ****************************************
        //*********************************************************************************************

        editTierAction = new EditTierAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/EditTier.png")));
        addTierAction = new AddTierAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/AddTier.png")));
        insertTierAction = new InsertTierAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/InsertTier.png")));
        removeTierAction = new RemoveTierAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/RemoveTier.png")));        
        moveTierUpAction = new MoveTierUpAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/MoveTierUp.png")));
        changeTierOrderAction = new ChangeTierOrderAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/ChangeTierOrder.png")));        
        hideTierAction = new HideTierAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/HideTier.png")));
        showAllTiersAction = new ShowAllTiersAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/ShowAllTiers.png")));        
        removeEmptyEventsAction = new RemoveEmptyEventsAction(this);
        editTiersAction = new EditTiersAction(this);
        listEventsAction = new ListEventsAction(this);
        typesAction = new TypesAction(this);
        listEventsAction = new ListEventsAction(this);

        //*********************************************************************************************
        //***************************************** EVENT ACTIONS *************************************
        //*********************************************************************************************

        shiftRightAction = new ShiftRightAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/ShiftRight.png")));
        shiftLeftAction = new ShiftLeftAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/ShiftLeft.png")));
        mergeAction = new MergeAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Merge.png")));
        splitAction = new SplitAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Split.png")));
        doubleSplitAction = new DoubleSplitAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/DoubleSplit.png")));
        deleteEventAction = new DeleteEventAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/DeleteEvent.png")));
        deleteEventsAction = new DeleteEventsAction(this);
        extendRightAction = new ExtendRightAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/ExtendRight.png")));
        extendLeftAction = new ExtendLeftAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/ExtendLeft.png")));
        shrinkRightAction = new ShrinkRightAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/ShrinkRight.png")));
        shrinkLeftAction = new ShrinkLeftAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/ShrinkLeft.png")));
        moveRightAction = new MoveRightAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/MoveRight.png")));
        moveLeftAction = new MoveLeftAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/MoveLeft.png")));
        editEventAction = new EditEventAction(this);
        findNextEventAction = new FindNextEventAction(this);
        splitLongEventAction = new SplitLongEventAction(this);

        insertPauseAction = new org.exmaralda.partitureditor.partiture.eventActions.InsertPauseAction(this, "Insert pause", new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/QuarterRestSmall.png")));

        // 24-06-2016 MuM-Multi new 
        moveDownLeftAction = new MoveDownLeftAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/arrow-lower-left.png")));
        moveDownRightAction = new MoveDownRightAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/arrow-lower-right.png")));
        
        //*********************************************************************************************
        //***************************************** TIMELINE ACTIONS **********************************
        //*********************************************************************************************

        editTimelineItemAction = new EditTimelineItemAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/EditTLI.png")));
        insertTimelineItemAction = new InsertTimelineItemAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/InsertTLI.png")));
        anchorTimelineItemAction = new AnchorTimelineAction(this);
        smoothTimelineAction = new SmoothTimelineAction(this);
        removeGapAction = new RemoveGapAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/RemoveGap.gif")));
        removeAllGapsAction = new RemoveAllGapsAction(this);
        removeUnusedTimelineItemsAction = new RemoveUnusedTimelineItemsAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/RemoveUnusedTLI.png")));
        makeTimelineConsistentAction = new MakeTimelineConsistentAction(this);
        completeTimelineAction = new CompleteTimelineAction(this);
        removeInterpolatedTimesAction = new RemoveInterpolatedTimesAction(this);
        removeTimesAction = new RemoveTimesAction(this);
        confirmTimelineItemAction = new ConfirmTimelineItemAction(this);
        shiftAbsoluteTimesAction = new ShiftAbsoluteTimesAction(this);
        addBookmarkAction = new AddBookmarkAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Bookmark.gif")));
        bookmarksAction = new BookmarksAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Bookmark.gif")));
        easyAlignmentAction = new EasyAlignmentAction(this);
        
        //*********************************************************************************************
        //*************************************** FORMAT ACTIONS **************************************
        //*********************************************************************************************

        reformatAction = new ReformatAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Reformat.gif")));

        openTierFormatTableAction = new OpenTierFormatTableAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/OpenTierFormatTable.png")));
        saveTierFormatTableAction = new SaveTierFormatTableAsAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/SaveTierFormatTableAs.png")));
        applyFormatStylesheetAction = new ApplyFormatStylesheetAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/XSL.png")));
        editTierFormatTableAction = new EditTierFormatTableAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/EditTierFormatTable.png")));
        editTierFormatAction = new EditTierFormatAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/EditTierFormat.png")));
        editRowLabelFormatAction = new EditRowLabelFormatAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/EditRowLabelFormat.png")));
        editColumnLabelFormatAction = new EditColumnLabelFormatAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/EditColumnLabelFormat.png")));
        editTimelineItemFormatAction = new EditTimelineItemFormatAction(this);

        setFrameEndAction = new SetFrameEndAction(this);

        underlineAction = new UnderlineAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Underline.gif")));
        
        
        //*********************************************************************************************
        //*************************************  LEGACY ACTIONS **************************************
        //*********************************************************************************************

        legacyImportAction = new LegacyImportAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Import.gif")));
        legacyExportAction = new LegacyExportAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Export.gif")));
        legacyOutputAction = new LegacyOutputAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/22x22/mimetypes/x-office-presentation.png")));

        syllableStructureAction = new MakeSyllableStructureTierAction(this);
        k8MysteryConverterAction = new K8MysteryConverterAction(this);
        appendSpaceAction = new AppendSpaceAction(this);
        exSyncCleanupAction = new ExSyncCleanupAction(this);
        exSyncEventShrinkerAction = new ExSyncEventShrinkerAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/ExSyncEventShrinker.gif")));
        
        importActionInel = new ImportActionInel(this, null);

        stadtspracheWordSegmentationAction = new StadtspracheWordSegmentationAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/hamburg.png")));
        stadtspracheTierSegmentationAction = new StadtspracheTierSegmentationAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/hamburg.png")));


        //*********************************************************************************************
        //*************************************  WEBSERVICE ACTIONS ***************************************
        //*********************************************************************************************

        webMAUSAction = new WebMAUSAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/maus_24x24.png")));
        webMAUSFineAlignmentAction = new WebMAUSFineAlignmentAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/maus_24x24.png")));
        g2pAction = new G2PAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Schwa.png")));
        webLichtAction = new WebLichtAction(this);
        deepLAction = new DeepLAction(this, new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/deepl_logo_icon_170284.png")));

        //-------------------------------- NAVIGATION ACTIONS --------------------------------------------------

        jumpToStartAction = new javax.swing.AbstractAction("Jump to start"){
            {putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control HOME"));}
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("JumpToStartAction!");
                commitEdit(true);
                jumpToStart();
            }
        };
        
        jumpToEndAction = new javax.swing.AbstractAction("Jump to end"){
            {putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control END"));}
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("JumpToEndAction!");
                commitEdit(true);
                jumpToEnd();
            }
        };
        
        scrollForwardsAction = new javax.swing.AbstractAction("Scroll forwards"){
            {putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt PAGE_DOWN"));}
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("ScrollForwardsAction!");
                commitEdit(true);
                scrollForwards();
            }
        };
        
        scrollBackwardsAction = new javax.swing.AbstractAction("Scroll backwards"){
            {putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt PAGE_DOWN"));}
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("ScrollBackwardsAction!");
                commitEdit(true);
                scrollBackwards();
            }
        };

        
        // initial enabling of actions
        selectionToNewAction.setEnabled(true);
        
        undoAction.setEnabled(false);
        pasteAction.setEnabled(false);
        cutAction.setEnabled(false);
        chopAudioAction.setEnabled(false);
        
        deleteEventAction.setEnabled(false);
        deleteEventsAction.setEnabled(false);
        extendRightAction.setEnabled(false);
        extendLeftAction.setEnabled(false);
        shrinkRightAction.setEnabled(false);
        shrinkLeftAction.setEnabled(false);
        moveRightAction.setEnabled(false);
        moveLeftAction.setEnabled(false);
        shiftRightAction.setEnabled(false);
        shiftLeftAction.setEnabled(false);
        mergeAction.setEnabled(false);
        splitAction.setEnabled(false);
        doubleSplitAction.setEnabled(false);
        editEventAction.setEnabled(false);
        insertPauseAction.setEnabled(false);
        splitLongEventAction.setEnabled(false);
        
        // 24-06-2016 MuM-Multi new 
        moveDownLeftAction.setEnabled(false);
        moveDownRightAction.setEnabled(false);
        
        
        insertTimelineItemAction.setEnabled(false);
        makeTimelineConsistentAction.setEnabled(true);
        removeUnusedTimelineItemsAction.setEnabled(true);
        editTimelineItemAction.setEnabled(false);
        removeGapAction.setEnabled(false);
        
        reformatAction.setEnabled(true);
        editTierAction.setEnabled(false);
        removeEmptyEventsAction.setEnabled(false);
        insertTierAction.setEnabled(false);
        removeTierAction.setEnabled(false);
        moveTierUpAction.setEnabled(false);
        hideTierAction.setEnabled(false);
        showAllTiersAction.setEnabled(true);
        editTierFormatAction.setEnabled(false);
        setFrameEndAction.setEnabled(false);
        underlineAction.setEnabled(false);
        typesAction.setEnabled(false);
    }
    
    /** initialises Keystrokes for navigation commands */
    private void initKeystrokes(){

        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();        
        
        // for jump to Start Action (Pos1 key)
        im.put(KeyStroke.getKeyStroke("control HOME"), "jumpToStartAction");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("control HOME"), "jumpToStartAction");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control HOME"), "jumpToStartAction");
        am.put("jumpToStartAction", jumpToStartAction);
        
        // for jump to End Action (End key)
        im.put(KeyStroke.getKeyStroke("control END"), "jumpToEndAction");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("control END"), "jumpToEndAction");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control END"), "jumpToEndAction");
        am.put("jumpToEndAction", jumpToEndAction);       
                       
    }
    
    
    
    /** initialises the virtual keyboard, the link panel, 
     *  the segmentation panel and the media panel */
    public void initDialogs(){
        linkPanelDialog = new LinkPanelDialog(parent,false);
        linkPanelDialog.getLinkPanel().addLinkPanelListener(this);
        
        keyboardDialog = new org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardDialog(parent, false, externalKeyboardPaths, generalPurposeFontName);
        
        mediaPanelDialog = new org.exmaralda.partitureditor.sound.AudioPanel(parent, false, player);
        mediaPanelDialog.addPraatPanelListener(this);
        mediaPanelDialog.addPlayableListener(this);
        
        praatPanel = new org.exmaralda.partitureditor.praatPanel.PraatPanel(parent, false);
        praatPanel.addPraatPanelListener(this);
        praatPanel.setLocationRelativeTo(this);
        
        annotationDialog = new org.exmaralda.partitureditor.annotation.AnnotationDialog(parent, false);
        annotationDialog.setPartitur(this);

        ipaPanel = new org.exmaralda.partitureditor.ipapanel.IPADialog(parent, false);
        
        if (parent instanceof PartiturEditor){
            multimodalDialog = new org.exmaralda.partitureditor.multimodalPanel.MultimodalDialog(parent, false);
            multimodalDialog.setLocationRelativeTo(this);
        }

        if (parent instanceof PartiturEditor){
            svgDialog = new org.exmaralda.partitureditor.svgPanel.SVGDialog(parent, false);
            svgDialog.setLocationRelativeTo(this);
            svgDialog.svgPanel.addXPointerListener(this);
        }
        
        
    }

    public void insertPause() {
        String pauseText = StringUtilities.makePause(currentSelectionLength, pausePrefix, pauseSuffix, pauseDigits, pauseDecimalComma);
        if (isEditing){
            editor = (PartitureCellStringEditor)(getEditingComponent());
            editor.replaceSelection(pauseText);
            // new 17-11-2017: issue #122
            editor.requestFocus();
        } else {
            // 1. find the nn tier
            Tier nn_t = null;
            for (int i=0; i<getModel().getTranscription().getBody().getNumberOfTiers(); i++){
                Tier tryTier = getModel().getTranscription().getBody().getTierAt(i);
                if (tryTier.getCategory().equals("v") && tryTier.getType().equals("t") && tryTier.getSpeaker()==null){
                    nn_t = tryTier;
                    break;
                }
            }
            boolean isOccupied = true;
            // 2. create the nn tier if there isn't one
            if (nn_t==null){
                nn_t = new Tier();
                nn_t.setCategory("v");
                nn_t.setType("t");
                nn_t.setSpeaker(null);
                nn_t.setID("TIE_");
                nn_t.setDisplayName("");
                getModel().addTier(nn_t);
                isOccupied = false;
            }
            
            // 3. check if the nn_tier is occupied
            if (isOccupied){
                isOccupied = false;
                for (int j=0; j<nn_t.getNumberOfEvents(); j++){
                    try {
                        Event e = nn_t.getEventAt(j);
                        double start = getModel().getTranscription().getBody().getCommonTimeline().getTimelineItemWithID(e.getStart()).getTime();
                        double end = getModel().getTranscription().getBody().getCommonTimeline().getTimelineItemWithID(e.getEnd()).getTime();
                        if (((start>=currentSelectionStart) && (start<=currentSelectionEnd))
                                || ((end>=currentSelectionStart) && (end<=currentSelectionEnd))){
                            isOccupied = true;
                            break;                    
                        }
                    } catch (JexmaraldaException ex) {
                        Logger.getLogger(PartitureTableWithActions.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
                        return;
                    }                    
                }
            }
            
            if (isOccupied){
                JOptionPane.showMessageDialog(this, "Cannot insert pause: position in nn tier is occupied. ");
                return;
            }
            
            int[] cols = getModel().insertInterval(currentSelectionStart, currentSelectionEnd, 0.01);
            int col1 = cols[0];
            int col2 = cols[1];

            TimelineItem tli1 = getModel().getTimelineItem(col1);
            TimelineItem tli2 = getModel().getTimelineItem(col2);
            Event newEvent = new Event(tli1.getID(), tli2.getID(), pauseText);            
            nn_t.addEvent(newEvent);
            int row = getModel().getTranscription().getBody().lookupID(nn_t.getID());

            getModel().fireEventAdded(row, col1, col2);            
            makeColumnVisible(col1+1);
            setNewSelection(row, col1, true);
            
        }
    }

    
    // *********************************************************************************** //
    // ************************ GET & SET METHODS ***************************************** //
    // *********************************************************************************** //
    
    /** sets the filename of the current transcription
     * @param f */
    public void setFilename(String f){
        filename = f;
        fireFilenameChanged();
        if (autoSaveThread!=null){
            autoSaveThread.setFilePath(filename);
        }
    }
    
    /** returns the filename of the current transcription
     * @return  */
    public String getFilename(){
        return filename;
    }
            
    // *********************************************************************************** //
    // ************************ LISTENER METHODS ***************************************** //
    // *********************************************************************************** //
    
    
    /** checks if the event is a popup trigger
     *  if so, displays the table's popup menu
     * @param event */
    @Override
    public void processMouseEvent(MouseEvent event) {        
       //super.processMouseEvent(event);
       if (event.isPopupTrigger()) {
           tablePopupMenu.show(this, event.getX(), event.getY());
       } /*else if (event.getClickCount()==2 && !(event.isConsumed()) && this.selectionStartCol==-1 && this.selectionStartRow>=0){
            // new 27-05-2023: double click with selected row will bring up list events
            listEventsAction.actionPerformed(null);
           //System.out.println("DOUBLE");
           event.consume();
       } */ else  {
            try {
                super.processMouseEvent(event);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                 System.out.println(e.getMessage());
                // do nothing
                // catches the (harmless) array index out of bounds exception
            } catch (Exception e){
                 System.out.println(e.getMessage());
            }
       }
       
        //System.out.println("CHECKING");
       
    }
    
    public boolean waitForSelection = false;

    /** called before the user selection changes
     * @param evt */
    @Override
    public void beforeSelect(final com.klg.jclass.table.JCSelectEvent evt) {
        waitForSelection = true;
        //System.out.println("beforeSelect");
        selectionStartRow = Math.min(evt.getStartRow(), evt.getEndRow());
        selectionEndRow = Math.max(evt.getStartRow(), evt.getEndRow());
        selectionStartCol = Math.min(evt.getStartColumn(), evt.getEndColumn());
        selectionEndCol = Math.max(evt.getStartColumn(), evt.getEndColumn());
        if ((evt.getStartRow()>=0) && (evt.getStartColumn()>=0)){
            traverse(evt.getStartRow(), evt.getStartColumn(), false, false);
        } else if (evt.getStartColumn()>=0){
            // changed 28-04-2009
            traverse(0, evt.getStartColumn(), false, false);
            //traverse(-1, evt.getStartColumn(), false, false);
        }
    }
    
    /** called when the user selection changes
     * @param evt */
    @Override
    public void select(final com.klg.jclass.table.JCSelectEvent evt) {
        //System.out.println("select");
        selectionStartRow = Math.min(evt.getStartRow(), evt.getEndRow());
        selectionEndRow = Math.max(evt.getStartRow(), evt.getEndRow());
        selectionStartCol = Math.min(evt.getStartColumn(), evt.getEndColumn());
        selectionEndCol = Math.max(evt.getStartColumn(), evt.getEndColumn());

        // added 06-04-2009: extending the selection by
        // clicking on CTRL has no effect otherwise
        if (evt.getType()==JCSelectEvent.EXTEND){
            selectionChanged();
        }
        waitForSelection = false;
    }
    
    /** called after the user selection changes
     * @param evt */
    @Override
    public void afterSelect(final com.klg.jclass.table.JCSelectEvent evt) {
        //System.out.println("afterSelect");
        selectionStartRow = Math.min(evt.getStartRow(), evt.getEndRow());
        selectionEndRow = Math.max(evt.getStartRow(), evt.getEndRow());
        selectionStartCol = Math.min(evt.getStartColumn(), evt.getEndColumn());
        selectionEndCol = Math.max(evt.getStartColumn(), evt.getEndColumn());

        selectionChanged();
    }
    
    /** called before editing of an event starts
     * @param evt */
    @Override
    public void beforeEditCell(final com.klg.jclass.table.JCEditCellEvent evt) {
        //System.out.println("beforeEditCell");
        // added 08-04-2009: don't edit when the control key was pressed
        if (cancelEdit){
            evt.setCancelled(true);
            return;
        }

        selectionStartRow = evt.getRow();
        selectionEndRow = selectionStartRow;
        selectionStartCol = evt.getColumn();
        selectionEndCol = selectionStartCol;


        if (undoEnabled){
            // Undo information
            undoEditCellInfo = new UndoInformation(this, "Edit event");
            if (selectionStartCol<getModel().getNumColumns()-2){
                undoEditCellInfo.memorizeCell(this);
            } else {
                undoEditCellInfo.memorizeRegion(this, getModel().lower(selectionStartCol), getNumColumns());
            }
            // end undo information
        }



        currentCellHasSpanOne = (getModel().getCellSpan(selectionStartRow, selectionStartCol)==1);
        selectionChanged();
        // 10-12-2010 Moved this code to own method
        setupLinkPanel();
    }
    
    /** called when editing of an event starts
     * @param evt */
    @Override
    public void editCell(final com.klg.jclass.table.JCEditCellEvent evt) {
        //System.out.println("editCell");
        // added for 1.3. to really avoid editing when is not editable
        if (!isEditable(evt.getRow(), evt.getColumn())){
            commitEdit(true);
            return;
        }
        editor = (PartitureCellStringEditor)(evt.getEditingComponent());
        editor.setPopupMenu(eventPopupMenu);
        keyboardDialog.getKeyboardPanel().addListener(editor);
        // changed 09-05-2017 - issue #87
        if (multimodalDialog!=null && (multimodalDialog.multimodalPanel!=null)){
            multimodalDialog.multimodalPanel.addUnicodeKeyboardListener(editor);
        }
        ipaPanel.ipaPanel.addUnicodeListener(editor);
        editor.positionCursor();
        editor.getDocument().addDocumentListener(this);        
        currentFont = editor.getFont();
        largeTextField.setDocument(editor.getDocument());
        largeTextField.setEditable(true);
        isEditing = true;
        pasteAction.setEnabled(true);
        cutAction.setEnabled(true);

    }
    
    /** called after editing of an event has finished
     * @param evt */
    @Override
    public void afterEditCell(final com.klg.jclass.table.JCEditCellEvent evt) {
        if (cancelEdit){
            cancelEdit = false;
            return;
        }
        if (undoEnabled && getModel().valueHasChanged()){
            addUndo(undoEditCellInfo);
        }
        //System.out.println("afterEditCell");
        largeTextField.setEditable(false);
        // changed 19-02-2009
        largeTextField.setDocument(new javax.swing.text.PlainDocument());
        keyboardDialog.getKeyboardPanel().removeAllListeners();        
        // changed 09-05-2017 - issue #87
        if (multimodalDialog!=null && (multimodalDialog.multimodalPanel!=null)){
            multimodalDialog.multimodalPanel.removeAllListeners();        
        }
        ipaPanel.ipaPanel.removeAllListeners();
        selectionChanged();
        isEditing = false;
        pasteAction.setEnabled(false);
        cutAction.setEnabled(false);
    }
    
    // *********************************************************************************** //
    // ************************ METHODS FOR HANDLING AND REACTING TO SELECTIONS ********** //
    // *********************************************************************************** //
    
    /** programatically sets the user selection to the specified
     * row and column. beginEdit determines whether or not editing
     * of the corresponding event is to be started
     * @param row
     * @param col
     * @param beginEdit */
    @Override
    public void setNewSelection(int row, int col, boolean beginEdit){
        super.setNewSelection(row, col, beginEdit);
        selectionStartRow=row;
        selectionEndRow=row;
        selectionStartCol=col;
        selectionEndCol=col;
        selectionChanged();
    }
    
    public void setNewSelection(int row1, int row2, int col1, int col2){
        clearSelection();
        if (!(this.isCellVisible(row1, col1))){
            makeVisible(row1,col1);
        }
        setSelection(row1,col1,row2,col2);        
        selectionStartRow=row1;
        selectionEndRow=row2;
        selectionStartCol=col1;
        selectionEndCol=col2;        
        selectionChanged();        
    }

    void setupLinkPanel(){
        if (getModel().containsEvent(selectionStartRow, selectionStartCol)){
            try{
                linkPanelDialog.getLinkPanel().setEvent(getModel().getEvent(selectionStartRow, selectionStartCol), selectionStartRow, selectionStartCol);
            } catch(JexmaraldaException je){
                System.out.println(je.getMessage());
            }
            linkPanelDialog.getLinkPanel().setEnabled(true);
        } else {
            linkPanelDialog.getLinkPanel().setEnabled(false);
            linkPanelDialog.getLinkPanel().emptyContents();
        }
    }
    
    /** called after the user selection has changed 
     * takes care of enabling and diabling actions according
     * to the new selection */
    private void selectionChanged(){
        //System.out.println("selectionChanged");
        //System.out.println("Row= " + selectionStartRow);
        //System.out.println("Col= " + selectionStartCol);
        //System.out.println("**************");
        if ((selectionStartCol>=getModel().getNumColumns()) || (selectionStartRow>=getModel().getNumRows())){
            // illegal value, can happen during data reset, don't understand---
            return;
        }
        if (selectionStartRow!=-1){setHorizSBTrackRow(selectionStartRow);}
        
        aSingleCellIsSelected = ((selectionStartRow == selectionEndRow) && (selectionStartCol == selectionEndCol) && (selectionStartRow!=-1) && (selectionStartCol!=-1));
        aSeriesOfCellsIsSelected = ((selectionStartRow == selectionEndRow) && (selectionStartCol < selectionEndCol) && (selectionStartRow!=-1) && (selectionStartCol!=-1));
        boolean aWholeRowIsSelected = ((selectionStartRow == selectionEndRow) && (selectionStartRow!=-1) && (selectionStartCol==-1));
        boolean aSeriesOfRowsIsSelected = ((selectionStartRow != selectionEndRow) && (selectionStartRow != -1) && (selectionEndRow != -1));
        aWholeColumnIsSelected = ((selectionStartCol == selectionEndCol) && (selectionStartCol != -1) && (selectionStartRow == -1));
        aSeriesOfColumnsIsSelected = ((selectionStartCol != selectionEndCol) && (selectionStartCol != -1) && (selectionEndCol != -1));
        boolean cellContainsEvent = (aSingleCellIsSelected && (getModel().containsEvent(selectionStartRow, selectionStartCol)));
        boolean cellToTheLeftIsFree = (aSingleCellIsSelected && getModel().cellToTheLeftIsFree(selectionStartRow, selectionStartCol));
        boolean cellToTheRightIsFree = (aSingleCellIsSelected && getModel().cellToTheRightIsFree(selectionStartRow, selectionStartCol));
        boolean cellSpanIsGreaterThanOne = (aSingleCellIsSelected && getModel().getCellSpan(selectionStartRow,selectionStartCol)>1);
        boolean columnIsGap = (aWholeColumnIsSelected && getModel().isGap(selectionStartCol));
        boolean isTranscriptionTypeTier = ((selectionStartRow>=0) && (selectionStartRow == selectionEndRow) && getModel().getTier(selectionStartRow).getType().equals("t"));
        aRectangleOfEventsIsSelected = (selectionStartCol!=selectionEndCol) && (selectionStartRow>=0) && (selectionEndRow>=0) && (selectionStartCol>=0);
        boolean aVerticalSeriesOfEventsIsSelected = (selectionStartCol==selectionEndCol) && (selectionStartRow>=0) && !(selectionStartRow == selectionEndRow);
                
        multipleColumnsSelected = aSeriesOfColumnsIsSelected;
        
        leftPartToNewAction.setEnabled(selectionStartCol!=-1);
        rightPartToNewAction.setEnabled(selectionStartCol!=-1);
        
        // 0. LinkPanel
        //setupLinkPanel();

        // 1. Tier actions
        addTierAction.setEnabled(!locked);
        typesAction.setEnabled(aWholeRowIsSelected);
        listEventsAction.setEnabled(aWholeRowIsSelected);
        removeEmptyEventsAction.setEnabled(aWholeRowIsSelected && !locked);
        moveTierUpAction.setEnabled(aWholeRowIsSelected && selectionStartRow!=0 && !locked);
        insertTierAction.setEnabled(aWholeRowIsSelected && !locked);
        editTierAction.setEnabled(aWholeRowIsSelected && !locked);
        removeTierAction.setEnabled(aWholeRowIsSelected || aSeriesOfRowsIsSelected && !locked);
        hideTierAction.setEnabled(aWholeRowIsSelected || aSeriesOfRowsIsSelected);
        
        // 2. Event actions
        editEventAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && !locked);
        deleteEventAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && !locked);
        deleteEventsAction.setEnabled((aRectangleOfEventsIsSelected || aVerticalSeriesOfEventsIsSelected) && !locked);
        extendRightAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && cellToTheRightIsFree && !locked);
        extendLeftAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && cellToTheLeftIsFree && !locked);
        moveRightAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && cellToTheRightIsFree && !locked);
        moveLeftAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && cellToTheLeftIsFree && !locked);
        shrinkRightAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && cellSpanIsGreaterThanOne && !locked);
        shrinkLeftAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && cellSpanIsGreaterThanOne && !locked);
        shiftRightAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && !locked);
        shiftLeftAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && !locked);
        //mergeAction.setEnabled(aSeriesOfCellsIsSelected && !locked);
        mergeAction.setEnabled((aSeriesOfCellsIsSelected || aRectangleOfEventsIsSelected) && !locked);
        splitAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && !locked);
        doubleSplitAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && !cellSpanIsGreaterThanOne && !locked);
        findNextEventAction.setEnabled(aSingleCellIsSelected || aSeriesOfCellsIsSelected);
        splitLongEventAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && !locked);

        
        // 24-06-2016 MuM-Multi new 
        moveDownLeftAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && !locked);
        moveDownRightAction.setEnabled(aSingleCellIsSelected && cellContainsEvent && !locked);
        
        
        
        // 3. Timeline actions
        editTimelineItemAction.setEnabled(aWholeColumnIsSelected && !locked);
        addBookmarkAction.setEnabled(aWholeColumnIsSelected && !locked);
        insertTimelineItemAction.setEnabled(aWholeColumnIsSelected && !locked);
        confirmTimelineItemAction.setEnabled((aWholeColumnIsSelected || aSeriesOfColumnsIsSelected) && !locked);
        removeGapAction.setEnabled(aWholeColumnIsSelected && columnIsGap && !locked);
        praatPanel.getGetButton().setEnabled(aWholeColumnIsSelected);
        //mediaPanelDialog.enableGetButtons(aWholeColumnIsSelected);
        
        // 4. Format Actions
        editTierFormatAction.setEnabled((aWholeRowIsSelected || aSeriesOfRowsIsSelected) && !locked);
        setFrameEndAction.setEnabled(aWholeRowIsSelected);
        underlineAction.setEnabled(isTranscriptionTypeTier && aSingleCellIsSelected && cellContainsEvent && !cellSpanIsGreaterThanOne && !locked);
        
        // 4a. Segmentation Actions
        stadtspracheTierSegmentationAction.setEnabled(aWholeRowIsSelected);
        
        // CLARIN actions
        webMAUSAction.setEnabled(aSeriesOfCellsIsSelected || aSingleCellIsSelected);

        // 5. Set the times of the media panel
        Timeline tl = getModel().getTranscription().getBody().getCommonTimeline();
        double startTime = 0;
        double endTime = tl.getMaxTime();
        
        if (aSingleCellIsSelected && cellContainsEvent){
            try{
                Event ev = getModel().getEvent(selectionStartRow, selectionStartCol);
                String start = ev.getStart();
                String end = ev.getEnd();
                startTime = tl.getPreviousTime(start);
                endTime = tl.getNextTime(end);
                largeTextField.setText(ev.getDescription());
                
                // NEW - 02-11-2016
                // SVG PANEL COMMUNICATION
                if ((svgDialog!=null) && (svgDialog.isVisible())){
                    if (getModel().getTranscription().getBody().getTierAt(selectionStartRow).getCategory().equals("ref")){
                        String xpointerRef = getModel().getEvent(selectionStartRow, selectionStartCol).getDescription();
                        svgDialog.svgPanel.setXPointer(xpointerRef, new File(filename).getParentFile());
                    }
                }
                
            } catch (JexmaraldaException je){
            }
            
        } else if (selectionStartCol >=0 && selectionEndCol>=0){
            String start = tl.getTimelineItemAt(selectionStartCol).getID();
            // ADDED 27-01-2009
            int add = 1;
            if ((aSingleCellIsSelected)||(aSeriesOfCellsIsSelected)){
                // make sure to add the cell span to the selectionEndCol
                add = getModel().getCellSpan(selectionStartRow, selectionEndCol);
            }
            String end = tl.getTimelineItemAt(Math.min(getModel().getNumColumns()-1,selectionEndCol+add)).getID();
            startTime = tl.getPreviousTime(start);
            endTime = tl.getNextTime(end);
        }            
        if ((startTime>=0) && (endTime>startTime)){
            mediaPanelDialog.setStartTime(startTime);
            mediaPanelDialog.setEndTime(endTime);
            mediaPanelDialog.snapshotWouldBeLinkeable = aSingleCellIsSelected && cellContainsEvent;
            praatPanel.setStartAndEndTime(startTime, endTime);
            fireMediaTimeChanged(startTime, endTime);
        }

        //added 01-04-2009
        if (aSeriesOfCellsIsSelected){
            String allText = "";
            for (int pos=selectionStartCol; pos<=selectionEndCol; pos++){
                try {
                    Event e = getModel().getEvent(selectionStartRow, pos);
                    if (e != null) {
                        allText += e.getDescription();
                    }
                } catch (JexmaraldaException ex) {
                    //ex.printStackTrace();
                }
            }
            largeTextField.setText(allText);
        }

        // added 08-04-2009: set the cell focus according to the selection
        /*if (aSingleCellIsSelected || aSeriesOfCellsIsSelected){
           traverse(selectionStartRow, selectionStartCol, false, false);
        } else if (aWholeColumnIsSelected || aSeriesOfColumnsIsSelected ){
           traverse(0, selectionStartCol, false, false);
        }*/
    }
    
    /** if an event is selected: returns this event, writing the tier ID into its UD data
     * @return  */
    public Event getSelectedEvent(){
        if ((selectionStartRow<0) || (selectionStartCol<0)) return null;
        try {
            Event e = getModel().getEvent(selectionStartRow, selectionStartCol);
            e.getUDEventInformation().setAttribute("Tier-ID", getModel().getTier(selectionStartRow).getID());
            return e;
        } catch (JexmaraldaException ex) {
            System.out.println("No event selected.");
            return null;
        }
    }
    
    
    
    // *********************************************************************************** //
    // ************************ METHODS FOR LISTENERS  *********************************** //
    // *********************************************************************************** //
    
    /** add a listener that is to be informed of changes to the table
     * @param l */
    public void addPartitureTableListener(PartitureTableListener l) {
        //listenerList.add(PartitureTableListener.class, l);
        thisListenerList.addElement(l);
    }
    
    /** remove all listeners */
    public void removeAllListeners(){
        //listenerList = new javax.swing.event.EventListenerList();
        thisListenerList.removeAllElements();
    }
    
    protected void fireEvent(PartitureTableEvent e){
        for (PartitureTableListener l : thisListenerList){
            l.partitureTablePropertyChanged(e);
        }
    }

    /** informs the listeners that the filename of the transcription has changed
     *  by calling their partitureTablePropertyChanged method with the
     * appropriate parameter (PartitureTableEvent.FILENAME_CHANGED) */
    protected void fireFilenameChanged() {
        PartitureTableEvent event = new PartitureTableEvent(this, PartitureTableEvent.FILENAME_CHANGED, getFilename());
        fireEvent(event);
    }

    protected void fireUndoChanged(String text){
        PartitureTableEvent event = new PartitureTableEvent(this, PartitureTableEvent.UNDO_CHANGED, text);
        fireEvent(event);
    }
    
    /** informs the listeners that the current media time of the transcription has changed 
     *  by calling their partitureTablePropertyChanged method with the
     * appropriate parameter (PartitureTableEvent.MEDIA_TIME_CHANGED)
     * @param startTime
     * @param endTime */
    protected void fireMediaTimeChanged(double startTime, double endTime) {
        Double[] times = new Double[2];
        times[0] = startTime;
        times[1] = endTime;
        PartitureTableEvent event = new PartitureTableEvent(this, PartitureTableEvent.MEDIA_TIME_CHANGED, times);
        fireEvent(event);
    }
    
 
        
        
    
    // *********************************************************************************** //
    // ************************ ACTION METHODS ******************************************* //
    // *********************************************************************************** //
     
    /** saves the current transcription under its current name */
    private void save(){
        if (!getFilename().equals("untitled.exb")){
            try{
                //getModel().getTranscription().writeXMLToFile(getFilename(),"none");
                // 02-06-2023 issue #398
                getModel().getTranscription().writeXMLToFile(getFilename(), "none", this.getModel().getTierFormatTable());
            } catch (IOException t){
                saveTranscription();
            }
        }
        else {
            saveTranscription();
        }
    }

    /** saves the current transcription under a new name */
    private void saveTranscription(){
        SaveBasicTranscriptionAsDialog dialog = new SaveBasicTranscriptionAsDialog(homeDirectory, getModel().getTranscription());
        boolean result = dialog.saveTranscriptionAs(parent);
        if (result){
            restoreAction.setEnabled(true);
            setFilename(dialog.getFilename());
            homeDirectory = dialog.getFilename();
        }
    }
    
                         
    
    void checkUpdate(){
        try{
            //String url = "http://aboutness.org";
              String url = "http://www.exmaralda.org/update/index.php"
			  + "?program=partitureditor" + "&version=" 
                          + java.net.URLEncoder.encode(((PartiturEditor)this.getTopLevelAncestor()).getVersion(), "UTF-8")
                          + "&java="
                          + java.net.URLEncoder.encode(System.getProperty("java.version"), "UTF-8")
		          + "&os=" + java.net.URLEncoder.encode( System.getProperty("os.name"))
		          + "&osversion=" + java.net.URLEncoder.encode( System.getProperty("os.version"));            
            //BrowserLauncher.openURL(url);
            Desktop.getDesktop().browse(new URI(url));
            
        } catch (IOException ioe){
            javax.swing.JOptionPane.showMessageDialog(
                    this,ioe.getLocalizedMessage(),"IO Error",javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (URISyntaxException ex) {
            Logger.getLogger(PartitureTableWithActions.class.getName()).log(Level.SEVERE, null, ex);
        }                        
    }
    
    /** opens the EXMARaLDA website */
    void exmaraldaOnTheWeb(){
        try{
            String url = "http://www.exmaralda.org";
            Desktop.getDesktop().browse(new URI(url));
            
        } catch (IOException ioe){
            javax.swing.JOptionPane.showMessageDialog(  this,
            ioe.getLocalizedMessage(),
            "IO Error",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);                           
        } catch (URISyntaxException ex) {
            Logger.getLogger(PartitureTableWithActions.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    /** scrolls to the beginning of the partitur */
    private void jumpToStart(){
        this.makeColumnVisible(0);
    }
    
    /** scrolls to the end of the partitur */
    private void jumpToEnd(){
        this.makeColumnVisible(this.getNumColumns()-1);
    }
    
    /** from the current position: finds the next event in the current tier*/
    public void findEvent(){
        int col = this.getModel().findNextEvent(selectionStartRow, selectionStartCol);
        //System.out.println("From " + selectionStartCol + " the next event is in " + col);
        if (col>=0){
            makeColumnVisible(col);
            this.setNewSelection(selectionStartRow, col, true);
            //setSelectedCells(new JCCellRange(selectionStartRow, col, selectionStartRow, col));
        } else {
            // issue #316
            if (selectionStartRow>0){
                int continueSearch = JOptionPane.showConfirmDialog(this, "No events found after cursor position. \n"
                        + "Continue at start?", "Continue search", JOptionPane.YES_NO_OPTION);
                if (continueSearch==JOptionPane.OK_OPTION){
                    makeColumnVisible(col);
                    setNewSelection(selectionStartRow, 0, true);                    
                    findEvent();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Nothing found. ");
            }
        }
    }

    /** goes to the next search result from the Find dialog */
    public void findNext() {
        ((SearchInEventsAction)searchInEventsAction).dialog.gotoNextSearchResult();
    }

    
    private void scrollForwards(){
        System.out.println("Left col " + this.getLeftColumn());
        System.out.println("Last visible col " + getLastVisibleColumn());
        int col = getLastVisibleColumn();
        setLeftColumn(col);
    }
    
    private void scrollBackwards(){
        int previousCol = Math.max(0, this.getLeftColumn()-2);
        this.makeColumnVisible(previousCol);
    }

    /** makes the partitur ready for exit, i.e.
     * checks if the user wants to save changes
     * and returns true if this check has completed */
    boolean exit(){
        boolean proceed = true;
        if (transcriptionChanged){
            proceed = checkSave();
        }
        if (!proceed) return false;
        return true;
    }
    
    // *********************************************************************************** //
    // ************************ CONVENIENCE METHODS ************************************** //
    // *********************************************************************************** //
    
    public void status(String message){
        if (statusPanel!=null){
            statusPanel.setStatus(message);
        }
    }
    
    /** makes spaces appear as dots 
     *  partitur will not be editable as long
     *  as this is activated */
    public void showSpecialCharacters(){
        commitEdit(true);
        getModel().setShowSpecialCharacters(!getModel().getShowSpecialCharacters());
    }
    
    /** changes the background color for empty cells
     *  necessary for better performance */
    public void useDifferentEmptyColor(){
       this.diffBgCol = !this.diffBgCol;
       resetFormat(true);
    }
    
    public void toggleTimelineMode(){
        getModel().timelineMode = !(getModel().timelineMode);
        getModel().fireColumnLabelsChanged();     
        if (getModel().timelineMode){
            addTableMouseWheelListener(this);
            //mediaPanelDialog.activateTimelineMode();
        } else {
            addTableMouseWheelListener(null);        
        }        
    }
    
    public void setPlaybackMode(boolean pm){
        playbackMode = pm;
        if (playbackMode){
            setSelectedBackground(java.awt.Color.YELLOW);
            setSelectedForeground(java.awt.Color.BLACK);  
            //getModel().completeTimeline();
        } else {
            setSelectedForeground(this.defaultSelectionColor);                        
            setSelectedBackground(this.defaultSelectionBg);
        }        
    }
    
    public void togglePlaybackMode(){
        setPlaybackMode(!playbackMode);
    }

    /** checks if the user wants to save changes to the transcription
     * @return  */
    public boolean checkSave(){
        String text = "The transcription " + getFilename() + " has been changed. \n Do you want to save changes?";
        int returnval = javax.swing.JOptionPane.showConfirmDialog(parent, text, "Save changes to the transcription", javax.swing.JOptionPane.YES_NO_CANCEL_OPTION);
        if (returnval==javax.swing.JOptionPane.YES_OPTION){
            save();
        } else if (returnval==javax.swing.JOptionPane.CANCEL_OPTION){
            return false;
        }
        return true;
    }
            
    
    /** opens a dialog for clean up (called by TASX Import)
     * @param t */
    public void cleanup(BasicTranscription t){
        CleanupDialog dialog = new CleanupDialog(parent, true);
        if (dialog.editCleanupParameters()){
            if (dialog.smoothTimeline()){
                System.out.println("SMOOTHING: " + dialog.getThreshhold() / 1000.0);
                //getModel().smoothTimeline(dialog.getThreshhold() / 1000.0);
                t.getBody().smoothTimeline(dialog.getThreshhold() / 1000.0);
            }
            for (int pos=0; pos<t.getBody().getNumberOfTiers(); pos++){
                if (dialog.removeEmptyEvents()){
                    System.out.println("REMOVING EMPTY EVENTS");
                    t.getBody().getTierAt(pos).removeEmptyEvents();
                }
                if (dialog.bridgeGaps()){
                    System.out.println("BRIDGING GAPS");
                    t.getBody().getTierAt(pos).bridgeGaps(dialog.getMaxDiff()/1000, t.getBody().getCommonTimeline());
                }
            }
            if (dialog.removeUnusedTLI()){
                System.out.println("REMOVING UNUSED TLI");
                t.getBody().removeUnusedTimelineItems();
            }
            if (dialog.removeGaps()){
                System.out.println("REMOVING GAPS");
                t.getBody().removeAllGaps();
            }
            if (dialog.normalizeIDs()){
                System.out.println("NORMALIZING");
                t.normalize();
            }
        }
    }
    
    /** checks if tiers are stratified. If they are not,
      * opens an appropriate dialog (used by TASX Import)
     * @param t */
    public void stratify(BasicTranscription t){
        Timeline tl = t.getBody().getCommonTimeline();
        for (int pos=0; pos<t.getBody().getNumberOfTiers(); pos++){
            Tier tier = t.getBody().getTierAt(pos);
            if (!tier.isStratified(tl)){
                StratifyTierDialog dialog = new StratifyTierDialog(parent, true, tier, tl);
                dialog.stratifyTier();
                if (dialog.getAdditionalTiers()!=null){
                    for (int i=0; i<dialog.getAdditionalTiers().length; i++){
                        Tier newTier = dialog.getAdditionalTiers()[i];
                        newTier.setID(t.getBody().getFreeID());
                        pos++;
                        try{
                            t.getBody().insertTierAt(newTier,pos);
                        } catch (JexmaraldaException je){}                                                    
                    }
                }
            }
        }
    }        

    /** returns the cursor Position in the editing component
     * @return  */
    public int getCharPos(){
        return getSelectionStartPosition();
    }
    
    /** returns the start position of the selection in the
     * current editing component
     * @return  */
    public int getSelectionStartPosition(){
        javax.swing.JTextField editingComponent = (javax.swing.JTextField)getEditingComponent();
        if (editingComponent!=null){
            int textPosition = editingComponent.getSelectionStart();
            return textPosition;
        }
        return lastRegisteredCharPosition;
    }
    
    /** returns the end position of the selection in the
     * current editing component
     * @return  */
    public int getSelectionEndPosition(){
        javax.swing.JTextField editingComponent = (javax.swing.JTextField)getEditingComponent();
        if (editingComponent!=null){
            int textPosition = editingComponent.getSelectionEnd();
            return textPosition;
        }
        return lastRegisteredCharPosition;
    }

    // returns true if selection start and selection end have absolute time values
    public boolean selectionIsAnchored() {
        //return true;
        if ((selectionStartCol<0) || (selectionEndCol<0) || (selectionEndCol==getModel().getNumColumns()-1)){
            return false;
        }
        TimelineItem tli1 = getModel().getTimelineItem(selectionStartCol);
        TimelineItem tli2 = getModel().getTimelineItem(selectionEndCol+1);
        //System.out.println("****" + tli1.getTimeAsString() + " " + tli2.getTime());
        return ((tli1.getTime()>=0) && (tli1.getTime()<tli2.getTime()));
    }

    
    /** if one or more events are selected: returns the start point 
     * of the first of these events, otherwise: returns the timeline
     * item corresponding to the first visible column
     * @return  */
    public TimelineItem getVisiblePosition(){
        if (selectionStartCol>=0){
            return getModel().getTranscription().getBody().getCommonTimeline().getTimelineItemAt(selectionStartCol);
        } 
        int firstCol = getLeftColumn();
        return getModel().getTranscription().getBody().getCommonTimeline().getTimelineItemAt(firstCol);
    }
    
    
    /** returns the first column that is fully visible
     * @return  */
    public int getFirstVisibleColumn(){
        return 0;
    }
    
    /** returns the last column that is fully visible
     * @return  */
    public int getLastVisibleColumn(){
        JCCellRange visibleCells = getVisibleCells();
        int lastVisibleColumn = visibleCells.end_column;
        return lastVisibleColumn;
    }
    
    
    /** locks or unlocks the partitur 
     *  i.e. makes it uneditable or editable */
    @Override
    public void setLocked (boolean l){
        super.setLocked(l);
        tablePopupMenu.configure(l);
    }
    
    
    /** sets the user preferences
     * @param usernode */
    //void setSettings(java.util.Properties settings){
    public void setSettings(String usernode){
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(usernode);

        // language
        language = settings.get("LANGUAGE", "en");

        // Directories
        homeDirectory = settings.get("HOME-Dir","");
        htmlDirectory = settings.get("HTML-Dir",homeDirectory);
        rtfDirectory = settings.get("RTF-Dir",homeDirectory);
        txtDirectory = settings.get("TXT-Dir",homeDirectory);
        chatDirectory = settings.get("CHAT-Dir",homeDirectory);
        
        // recently used files
        String recentFilesList = settings.get("RecentFiles", "");
        java.util.StringTokenizer st = new java.util.StringTokenizer(recentFilesList,"###***###");
        while (st.hasMoreTokens()){
            String fn = st.nextToken();
            recentFiles.addElement(fn);
        }            

        // AutoSave
        if (settings.get("AutoSave", "TRUE").equalsIgnoreCase("TRUE")){
            autoSave = true;
            autoSaveThread.FILENAME = settings.get("AutoSave-Filename", "EXMARaLDA_AutoBackup");
            autoSaveThread.PATH = settings.get("AutoSave-Path", System.getProperty("user.home"));
            try {
                int interval = 60*Integer.parseInt(settings.get("AutoSave-Interval", "600"));
                interval = Math.max(60, interval);
                autoSaveThread.setSaveInterval(interval);
            } catch (NumberFormatException nfe){
                autoSaveThread.setSaveInterval(600);
            }
            startAutoSaveThread();
        }
        
        prettyPrint = settings.getBoolean("pretty-print-enabled-partitureditor", false);
        
        //saveTierFormatTable = settings.get("SaveTierFormatTable", "TRUE").equalsIgnoreCase("TRUE");        

        // View options
        scaleConstant = Integer.parseInt(settings.get("Scale-Constant", "2"));
        diffBgCol = settings.get("Diff-Bg-Color","TRUE").equalsIgnoreCase("TRUE");
        if (settings.get("SHOW-Keyboard","TRUE").equalsIgnoreCase("TRUE")){keyboardDialog.show();}
        if (settings.get("SHOW-LinkPanel","FALSE").equalsIgnoreCase("TRUE")){linkPanelDialog.show();}
        if (settings.get("SHOW-MediaPanel","FALSE").equalsIgnoreCase("TRUE")){mediaPanelDialog.show();}
        if (settings.get("SHOW-PraatPanel","FALSE").equalsIgnoreCase("TRUE")){praatPanel.setVisible(true);}
        if (settings.get("SHOW-AnnotationPanel","FALSE").equalsIgnoreCase("TRUE")){annotationDialog.setVisible(true);}
        if (settings.get("SHOW-IPAPanel","FALSE").equalsIgnoreCase("TRUE")){ipaPanel.setVisible(true);}
        if (settings.get("SHOW-MultimodalPanel","FALSE").equalsIgnoreCase("TRUE") && multimodalDialog!=null){multimodalDialog.setVisible(true);}
        if (settings.get("SHOW-SVGPanel","FALSE").equalsIgnoreCase("TRUE") && svgDialog!=null){svgDialog.setVisible(true);}


        // default font and general purpose font
        defaultFontName = settings.get("Default-Font","Times New Roman");
        getModel().defaultFontName = this.defaultFontName;
        generalPurposeFontName = settings.get("General-Purpose-Font","Arial Unicode MS");
        keyboardDialog.getKeyboardPanel().setGeneralPurposeFontName(this.generalPurposeFontName);        

        //Preferred virtual keyboard
        String virtKeyboard = settings.get("VIRTUAL-KEYBOARD", "HIAT");
        keyboardDialog.getKeyboardPanel().setKeyboard(virtKeyboard);
        int w = settings.getInt("VIRTUAL-KEYBOARD-WIDTH", keyboardDialog.getWidth());
        int h = settings.getInt("VIRTUAL-KEYBOARD-HEIGHT", keyboardDialog.getHeight());
        keyboardDialog.setPreferredSize(new java.awt.Dimension(w,h));
        keyboardDialog.setSize(new java.awt.Dimension(w,h));
        int x = settings.getInt("VIRTUAL-KEYBOARD-X", keyboardDialog.getX());
        int y = settings.getInt("VIRTUAL-KEYBOARD-Y", keyboardDialog.getY());        
        // 04-02-2023: issue #72 
        x = correctXForScreenSize(x);
        y = correctYForScreenSize(y);        
        keyboardDialog.setLocation(x, y);
        keyboardDialog.getKeyboardPanel().setKeySize(settings.getInt("VIRTUAL-KEYBOARD-KEYSIZE", 100));
        //keyboardDialog.pack();

        // Annotation Panel
        int w2 = settings.getInt("ANNOTATION-PANEL-WIDTH", annotationDialog.getWidth());
        int h2 = settings.getInt("ANNOTATION-PANEL-HEIGHT", annotationDialog.getHeight());
        annotationDialog.setPreferredSize(new java.awt.Dimension(w2,h2));
        annotationDialog.setSize(new java.awt.Dimension(w2,h2));
        int x2 = settings.getInt("ANNOTATION-PANEL-X", annotationDialog.getX());
        int y2 = settings.getInt("ANNOTATION-PANEL-Y", annotationDialog.getY());
        x2 = Math.min(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width - w2, x2);
        annotationDialog.setLocation(x2, y2);

        // IPA Panel
        int x3 = settings.getInt("IPA-PANEL-X", keyboardDialog.getX());
        int y3 = settings.getInt("IPA-PANEL-Y", keyboardDialog.getY());
        // 04-02-2023: issue #72 
        x3 = correctXForScreenSize(x3);
        y3 = correctYForScreenSize(y3);        
        ipaPanel.setLocation(x3, y3);



        // Stylesheets
        head2HTMLStylesheet = settings.get("Head-To-HTML-Stylesheet","");
        speakertable2TranscriptionStylesheet = settings.get("Speakertable-To-Transcription-Stylesheet","");        
        transcription2FormattableStylesheet = settings.get("Transcription-To-Formattable-Stylesheet","");
        freeStylesheetVisualisationStylesheet = settings.get("Free-Stylesheet-Visualisation-Stylesheet", "");
        HIATUtteranceList2HTMLStylesheet = settings.get("HIAT-UtteranceList-To-HTML-Stylesheet", "");

        // FSMs
        preferredSegmentation = settings.get("Preferred-Segmentation", "GENERIC");
        if (infoPanel!=null){
            infoPanel.segmentationLabel.setText(preferredSegmentation);
        }
        hiatFSM = settings.get("HIAT-FSM","");
        didaFSM = settings.get("DIDA-FSM","");
        gatFSM = settings.get("GAT-FSM","");
        chatFSM = settings.get("CHAT-FSM","");
        ipaFSM = settings.get("IPA-FSM","");
        genericFSM = settings.get("GENERIC-FSM","");
        cGATMinimalFSM = settings.get("CGAT-MINIMAL-FSM","");

        // Partitur Parameters
        rtfParameters.setSettings(usernode);
        htmlParameters.setSettings(usernode);
        svgParameters.setSettings(usernode);
        printParameters.setSettings(usernode);           
        pageFormat.setPaper(org.exmaralda.partitureditor.interlinearText.PageOutputParameters.makePaperFromParameters(rtfParameters));        
        
        //parameters for the media panel
        mediaPanelDialog.lastSnapshotFilename = settings.get("Snapshot-Filename", System.getProperty("user.home") + System.getProperty("file.separator") + "snapshot1.png");
        mediaPanelDialog.lastAudioPartFilename = settings.get("AudioPart-Filename", System.getProperty("user.home") + System.getProperty("file.separator") + "snippet1.wav");
        
        // underlining
        underlineWithDiacritics = settings.getBoolean("UNDERLINE-Diacritics", false);
        underlineCategory = settings.get("UNDERLINE-Category", "akz");

        //auto anchoring
        AUTO_ANCHOR = settings.getBoolean("AUTO-ANCHOR", true);
        AUTO_REMOVE_UNUSED_TLI = settings.getBoolean("AUTO-REMOVE-TLI", false);
        getModel().INTERPOLATE_WHEN_SPLITTING = settings.getBoolean("AUTO-INTERPOLATE", true);

        // pause notation
        pausePrefix = settings.get("PAUSE-PREFIX", "((");
        pauseSuffix = settings.get("PAUSE-SUFFIX", "s))");
        pauseDigits = settings.getInt("PAUSE-DIGITS", 1);
        pauseDecimalComma = settings.getBoolean("PAUSE-COMMA", true);

        //undo
        undoEnabled = settings.getBoolean("ENABLE-UNDO", true);
        
        // player
        String playerType = settings.get("PlayerType", "JavaFX-Player");
        if (infoPanel!=null){
            infoPanel.playerLabel.setText(playerType);
        }
        

    }
    
    /** returns the user settings
     * @param usernode */
    public void getSettings(String usernode){
        //java.util.Properties result = new java.util.Properties();
        //changed for version 1.3.5.        
        //java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor");
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(usernode);
        
        // Language
        settings.put("LANGUAGE", language);
        // Directories
        settings.put("HOME-Dir", homeDirectory);
        settings.put("HTML-Dir", htmlDirectory);
        settings.put("RTF-Dir", rtfDirectory);
        settings.put("TXT-Dir", txtDirectory);
        settings.put("CHAT-Dir", chatDirectory);
        // View Options
        settings.put("Scale-Constant", Integer.toString(this.scaleConstant));
        settings.put("Diff-Bg-Color", Boolean.toString(diffBgCol)); 
        settings.put("SHOW-Keyboard", Boolean.toString(keyboardDialog.isShowing()));
        settings.put("SHOW-LinkPanel", Boolean.toString(linkPanelDialog.isShowing()));
        settings.put("SHOW-MediaPanel", Boolean.toString(mediaPanelDialog.isShowing()));
        //settings.put("SHOW-SegmentationPanel", new Boolean(segmentationPanel.isShowing()).toString());
        settings.put("SHOW-PraatPanel", Boolean.toString(praatPanel.isShowing()));
        settings.put("SHOW-AnnotationPanel", Boolean.toString(annotationDialog.isShowing()));
        settings.put("SHOW-IPAPanel", Boolean.toString(ipaPanel.isShowing()));
        if (multimodalDialog!=null){
            settings.put("SHOW-MultimodalPanel", Boolean.toString(multimodalDialog.isShowing()));
        }
        if (svgDialog!=null){
            settings.put("SHOW-SVGPanel", Boolean.toString(svgDialog.isShowing()));
        }
        // Default font
        settings.put("Default-Font", defaultFontName);
        settings.put("General-Purpose-Font", generalPurposeFontName);
        // Stylesheets
        settings.put("Head-To-HTML-Stylesheet",head2HTMLStylesheet);
        settings.put("Speakertable-To-Transcription-Stylesheet", speakertable2TranscriptionStylesheet);
        settings.put("Transcription-To-Formattable-Stylesheet", transcription2FormattableStylesheet);
        settings.put("Free-Stylesheet-Visualisation-Stylesheet", freeStylesheetVisualisationStylesheet);
        settings.put("HIAT-UtteranceList-To-HTML-Stylesheet", HIATUtteranceList2HTMLStylesheet);

        settings.put("VIRTUAL-KEYBOARD", keyboardDialog.getKeyboardPanel().getKeyboard());
        settings.put("VIRTUAL-KEYBOARD-WIDTH", Integer.toString(keyboardDialog.getWidth()));
        settings.put("VIRTUAL-KEYBOARD-HEIGHT", Integer.toString(keyboardDialog.getHeight()));
        settings.put("VIRTUAL-KEYBOARD-X", Integer.toString(keyboardDialog.getX()));
        settings.put("VIRTUAL-KEYBOARD-Y", Integer.toString(keyboardDialog.getY()));
        settings.put("VIRTUAL-KEYBOARD-KEYSIZE", Integer.toString(keyboardDialog.getKeyboardPanel().getKeySize()));

        settings.put("ANNOTATION-PANEL-WIDTH", Integer.toString(annotationDialog.getWidth()));
        settings.put("ANNOTATION-PANEL-HEIGHT", Integer.toString(annotationDialog.getHeight()));
        settings.put("ANNOTATION-PANEL-X", Integer.toString(annotationDialog.getX()));
        settings.put("ANNOTATION-PANEL-Y", Integer.toString(annotationDialog.getY()));

        settings.put("IPA-PANEL-X", Integer.toString(ipaPanel.getX()));
        settings.put("IPA-PANEL-Y", Integer.toString(ipaPanel.getY()));

        settings.put("Preferred-Segmentation", this.preferredSegmentation);

        // FSMs
        settings.put("HIAT-FSM", this.hiatFSM);
        settings.put("DIDA-FSM", this.didaFSM);
        settings.put("GAT-FSM", this.gatFSM);
        settings.put("CHAT-FSM", this.chatFSM);
        settings.put("IPA-FSM", this.ipaFSM);
        settings.put("GENERIC-FSM", this.genericFSM);
        settings.put("CGAT-MINIMAL-FSM", this.cGATMinimalFSM);
        // Break Parameters
        settings.put("Break-RespectWordBoundaries", Boolean.toString(rtfParameters.respectWordBoundaries[0]));

        // new 05-03-2012
        String wordBoundaries = "";
        for (char c : rtfParameters.getWordEndChars()){
            wordBoundaries+=c;
        }
        settings.put("Break-WordBoundaries", wordBoundaries);

        settings.put("Break-HorizontalTolerance", Integer.toString(rtfParameters.tolerance));
        settings.put("Break-VerticalTolerance", Integer.toString(rtfParameters.pageBreakTolerance));
        settings.put("Break-AdditionalLabelSpace", Integer.toString(rtfParameters.additionalLabelSpace));
        settings.put("Break-RemoveEmptyLines", Boolean.toString(rtfParameters.removeEmptyLines));
        settings.put("Break-NumberPartiturAreas", Boolean.toString(rtfParameters.numberItBundles));
        settings.put("Break-SmoothRightBoundaries", Boolean.toString(rtfParameters.smoothRightBoundary));        
        settings.put("Break-SaveSpace", Boolean.toString(rtfParameters.saveSpace));        
        // General Partitur Parameters
        settings.put("General-IncludeTimeline", Boolean.toString(rtfParameters.includeSyncPoints));
        settings.put("General-PutTimelineOutside", Boolean.toString(rtfParameters.putSyncPointsOutside));
        settings.put("General-PrependMetaInformation", Boolean.toString(rtfParameters.prependAdditionalInformation));
        // RTF parameters
        settings.put("RTF-Glue-Adjacent", Boolean.toString(rtfParameters.glueAdjacent));
        settings.put("RTF-Glue-Empty", Boolean.toString(rtfParameters.glueEmpty));      
        settings.put("RTF-Use-ClFitText", Boolean.toString(rtfParameters.useClFitText));      
        settings.put("RTF-CriticalSizePercentage", Double.toString(rtfParameters.criticalSizePercentage));
        settings.put("RTF-RightMarginBuffer", Integer.toString(rtfParameters.rightMarginBuffer));
        settings.put("RTF-MakePageBreaks", Boolean.toString(rtfParameters.makePageBreaks));      
        // HTML parameters
        settings.put("HTML-Width", Double.toString(this.htmlParameters.getWidth()));
        // SVG parameters
        settings.put("SVG-Width", Double.toString(this.svgParameters.getWidth()));
        settings.put("SVG-ScaleFactor", Double.toString(this.svgParameters.scaleFactor));
        
        // page setup
        
        short CM = PrintParameters.CM_UNIT;
        settings.put("PAGE-Width", Double.toString(printParameters.getPaperMeasure("paper:width", CM)));
        settings.put("PAGE-Height", Double.toString(printParameters.getPaperMeasure("paper:height", CM)));
        settings.put("PAGE-Margin-Left", Double.toString(printParameters.getPaperMeasure("margin:left", CM)));
        settings.put("PAGE-Margin-Right", Double.toString(printParameters.getPaperMeasure("margin:right", CM)));
        settings.put("PAGE-Margin-Top", Double.toString(printParameters.getPaperMeasure("margin:top", CM)));
        settings.put("PAGE-Margin-Bottom", Double.toString(printParameters.getPaperMeasure("margin:bottom", CM)));

        // recentFiles
        int fileCount = 0;
        int pos=0;
        String recentFilesList = new String();
        java.util.HashSet hs = new java.util.HashSet();        
        while ((fileCount<10) && (pos<recentFiles.size())){
            String thisFilename = (String)(recentFiles.elementAt(pos));
            if (!hs.contains(thisFilename)){
                if (fileCount>0) {recentFilesList+="###***###";}
                recentFilesList+=thisFilename;
                hs.add(thisFilename);
                fileCount++;
            }
            pos++;
        }
        settings.put("RecentFiles", recentFilesList);
        settings.put("Snapshot-Filename", mediaPanelDialog.lastSnapshotFilename);
        settings.put("AudioPart-Filename", mediaPanelDialog.lastAudioPartFilename);
        // auto save
        settings.put("AutoSave", Boolean.toString(autoSave));
        settings.put("AutoSave-Filename", autoSaveThread.FILENAME);
        settings.put("AutoSave-Path", autoSaveThread.PATH);
        // changed 09-03-2009
        settings.put("AutoSave-Interval", Integer.toString(Math.max(60000,autoSaveThread.SAVE_INTERVAL)/60000));
        //settings.put("SaveTierFormatTable", new Boolean(this.saveTierFormatTable).toString());

        settings.put("pretty-print-enabled-partitureditor", Boolean.toString(prettyPrint));
        
        // underline preferences
        settings.put("UNDERLINE-Diacritics", Boolean.toString(underlineWithDiacritics));      
        settings.put("UNDERLINE-Category", underlineCategory);

        // auto anchoring
        settings.put("AUTO-ANCHOR", Boolean.toString(AUTO_ANCHOR));
        settings.put("AUTO-REMOVE-TLI", Boolean.toString(AUTO_REMOVE_UNUSED_TLI));
        settings.put("AUTO-INTERPOLATE", Boolean.toString(getModel().INTERPOLATE_WHEN_SPLITTING));
        //return result;

        // pause notation
        settings.put("PAUSE-PREFIX", pausePrefix);
        settings.put("PAUSE-SUFFIX", pauseSuffix);
        settings.put("PAUSE-DIGITS", Integer.toString(pauseDigits));
        settings.put("PAUSE-COMMA", Boolean.toString(pauseDecimalComma));

        settings.put("ENABLE-UNDO", Boolean.toString(undoEnabled));
    }
    
    /** launches the system's default browser with the standard HTML file
     * @param file */
    public void launchBrowser(File file){
        try{
            String url = file.toURI().toString();
            //BrowserLauncher.openURL(url);
            Desktop.getDesktop().browse(new URI(url));
            
        } catch (IOException ioe){
            javax.swing.JOptionPane.showMessageDialog(  this,
            ioe.getLocalizedMessage(),
            "IO Error",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);                           
        } catch (URISyntaxException ex) {
            Logger.getLogger(PartitureTableWithActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** does nothing */
    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent documentEvent) {
    }    
    
    /** adjusts the width of the current cell when the user enters text */
    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent documentEvent) {
        if ((getModel().timeProportional) || (!currentCellHasSpanOne)) return;
        javax.swing.text.Document document = documentEvent.getDocument();
        try{
            String text = document.getText(0,document.getLength());
            int width = dummyTextField.getFontMetrics(currentFont).stringWidth(text)+5;
            int currentWidth = this.getPixelWidth(selectionStartCol);
            if (currentWidth < width){
                this.setPixelWidth(selectionStartCol, width);
            }
        } catch (javax.swing.text.BadLocationException ble){
            ble.printStackTrace();
        }
    }
    
    /** does nothing
     * @param documentEvent */
    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent documentEvent) {
    }
    
    /** sets up the media panel to play the referenced file (if possible)
     * @return  */
    public boolean setupMedia(){
        
        System.out.println("(1a) PartiturEditor: setupMedia");
        // the partitur editor must handle the setup of the time viewer
        // this is awkward but I'm only human
        if (getTopLevelAncestor() instanceof PartiturEditor){
            PartiturEditor pe = (PartiturEditor)(getTopLevelAncestor());
            boolean goon = pe.setupMedia();
            if (!goon){     
                if (player instanceof AbstractPlayer) ((AbstractPlayer)player).reset();
                mediaPanelDialog.reset();
                protectLastColumn = false;
                // added 19-01-2010
                getModel().protectLastColumn = false;

                getModel().fireColumnLabelsChanged();
                return false;                
            }
        }
        // end of awkward, human-only part
        // code is immaculate from here on

        System.out.println("(1b) PartiturTableWithActions: setupMedia");
        // changed 14-04-2009
        String soundFile = getModel().getTranscription().getHead().getMetaInformation().getReferencedFile();
        String waveFile = getModel().getTranscription().getHead().getMetaInformation().getReferencedFile("wav");
        chopAudioAction.setEnabled((waveFile!=null) && (org.exmaralda.partitureditor.sound.AudioProcessor.isCuttable(waveFile)));

        // added 28-06-2011
        if (getTopLevelAncestor() instanceof org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT){
           // soundFile = getModel().getTranscription().getHead().getMetaInformation().getReferencedFile("ogg");
           // TODO: must be even more sophisticated!
        }

        if (soundFile!=null && soundFile.length()>0){
            try {
                player.setSoundFile(soundFile);
                System.out.println("(1b - alpha) - Media file is now set for player of PartiturTableWithActions");
                System.out.println("   Player says that media file has length " + Double.toString(player.getTotalLength()));
                //getModel().getTranscription().getBody().getCommonTimeline().anchorTimeline(0.0, player.getTotalLength());
                mediaPanelDialog.setAvailableSoundFiles(getModel().getTranscription().getHead().getMetaInformation().getReferencedFiles());
                System.out.println("(1c) PartiturTableWithActions: handing the media file to the audio/video panel");
                boolean success = mediaPanelDialog.setSoundFile(soundFile);
                
                if ((mediaPanelDialog.isVideo()) && (getTopLevelAncestor() instanceof PartiturEditor)){
                    
                    System.out.println("(1d) PartiturTableWithActions: handling a video");
                    mediaPanelDialog.setVisible(true);
                    PartiturEditor pe = (PartiturEditor)(getTopLevelAncestor());
                    pe.menuBar.viewMenu.showMediaPanelCheckBoxMenuItem.setSelected(true);        
                    
                    //new 06-12-2016: prevent closing of dialog, may cause trouble
                    //because CocoaQT always has a visible component
                    if (player instanceof AVFPlayer){
                        mediaPanelDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);                        
                    }
                }
                //anchoring the timeline may require relayout
                //moved here 24-04-2017 issue #73
                if (AUTO_ANCHOR){
                    protectLastColumn = true;
                    // added 19-01-2010
                    getModel().protectLastColumn = true;
                    getModel().anchorTimeline(0.0, player.getTotalLength());
                }
                return success;
            } catch (IOException ex) {
                //ex.printStackTrace();
                System.out.println("Message: " + ex.getLocalizedMessage());
                String[] options = {"Edit recordings", "Ignore"};
                
                // changed this for issue #220
                Object messagePane = new RecordingErrorMessagePanel(soundFile, ex, ((ExmaraldaApplication)getTopLevelAncestor()).getPreferencesNode());
                int optionChosen = JOptionPane.showOptionDialog(this, messagePane , "Player: Problem opening Media",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/22x22/mimetypes/video-x-generic.png")),
                        options, "Edit recordings");
                
                if (optionChosen==JOptionPane.YES_OPTION){
                    editRecordingsAction.actionPerformed(null);
                    return true;
                } else {
                    if (player instanceof AbstractPlayer) ((AbstractPlayer)player).reset();
                    mediaPanelDialog.reset();
                    protectLastColumn = false;
                    // added 19-01-2010
                    getModel().protectLastColumn = false;

                    getModel().fireColumnLabelsChanged();
                    return false;
                }
            }
        } else {
            if (player instanceof AbstractPlayer) ((AbstractPlayer)player).reset();
            protectLastColumn = false;
            // added 19-01-2010
            getModel().protectLastColumn = false;

            getModel().fireColumnLabelsChanged();
            return mediaPanelDialog.reset();
        }           


    }
        
    /** sets up the praat panel to play the referenced file (if possible) */
    public void setupPraatPanel(){
        String soundFile = getModel().getTranscription().getHead().getMetaInformation().getReferencedFile("wav");
        if (soundFile!=null && soundFile.length()>0){
            praatPanel.setAudioFilename(soundFile);
        }            
    }

    public BasicTranscription getVisibleTiersAsNewTranscription(){
            return getModel().getPartOfTranscription(getIndicesOfVisibleRows(),0,getModel().getNumColumns()-1);        
    }

    /** returns the currently selected portion of non-hidden tiers as a new transcription
     * @return  */
    public BasicTranscription getCurrentSelectionAsNewTranscription(){
        if ((selectionStartCol>=0) && (selectionStartRow==-1)){
            // a set of colums is selected
            int selectionEnd=Math.min(selectionEndCol+1, getModel().getNumColumns()-1);
            return getModel().getPartOfTranscription(getIndicesOfVisibleRows(),selectionStartCol,selectionEnd);
        } else if ((selectionStartCol>=0) && selectionStartRow>=0){
            // a real selection inside the partitur, i.e. not of rows and not of columns
            // not of the flesh, but over flesh
            List<Integer> rowIndices = new ArrayList<>();
            for (int rowIndex : getIndicesOfVisibleRows()){
                if (rowIndex>=selectionStartRow && rowIndex<=selectionEndRow){
                    rowIndices.add(rowIndex);
                }
            }
           int[] rowIndicesArray = new int[rowIndices.size()];
           for (int i=0; i<rowIndices.size(); i++) rowIndicesArray[i] = rowIndices.get(i);
           int selectionEnd=Math.min(selectionEndCol+1, getModel().getNumColumns()-1);
           return getModel().getPartOfTranscription(rowIndicesArray, selectionStartCol,selectionEnd);
        } else if ((selectionStartCol==-1) && (selectionStartRow>=0)){
            // a set of rows is selected
            List<Integer> rowIndices = new ArrayList<>();
            for (int rowIndex : getIndicesOfVisibleRows()){
                if (rowIndex>=selectionStartRow && rowIndex<=selectionEndRow){
                    rowIndices.add(rowIndex);
                }
            }
           int[] rowIndicesArray = new int[rowIndices.size()];
           for (int i=0; i<rowIndices.size(); i++) rowIndicesArray[i] = rowIndices.get(i);
           return getModel().getPartOfTranscription(rowIndicesArray, 0,getModel().getNumColumns()-1);
            
        }       
        return getModel().getPartOfTranscription(getIndicesOfVisibleRows(),0,getModel().getNumColumns()-1);
    }
    
    /** sets the speaker contribution of the segmentation panel if
     * the user has thus requested */
    /*public void getTurnRequested(SegmentationPanelEvent event) {
        commitEdit(true);
        if (selectionStartRow<0 || selectionStartCol<0){
            segmentationPanel.setTurn(false, "Faulty selection!");
        } else if (!getModel().getTier(selectionStartRow).getType().equals("t")){
            segmentationPanel.setTurn(false, "Selection is not in tier of type 't'");
        } else if (!getModel().containsEvent(selectionStartRow, selectionStartCol)){
            segmentationPanel.setTurn(false, "Selection is on empty event.");
        } else {
            String turnText = getModel().getTurnText(selectionStartRow, selectionStartCol);
            segmentationPanel.setTurn(true, turnText);
        }
    }*/
    
    /** processes a time sent from the praat panel
     * @param event */
    @Override
    public void processTime(PraatPanelEvent event) {
        if (this.selectionStartCol<0) return;
        commitEdit(true);
        if (event.getTime()==PraatPanelEvent.SELECT_NEXT_TLI){
            // this is not really a time, but the command to
            // select the next point in the timeline
            int selectedColumn = Math.min(selectionStartCol+1, getModel().getNumColumns()-1);
            this.setNewSelection(-1, selectedColumn, false);
            traverse(JCTableEnum.TRAVERSE_RIGHT);
            return;
        } else if (event.getTime()==PraatPanelEvent.SELECT_PREVIOUS_TLI){
            // this is not really a time, but the command to
            // select the previous point in the timeline
            int selectedColumn = Math.max(selectionStartCol-1, 0);
            this.setNewSelection(-1, selectedColumn, false);
            traverse(JCTableEnum.TRAVERSE_LEFT);
            return;
        } else if (event.getTime()==PraatPanelEvent.LINK_SNAPSHOT){
            // this is not really a time, but the command to
            // link a snapshot to the current event
            try{
                Event linkevent = getModel().getEvent(selectionStartRow, selectionStartCol);
                linkevent.setMedium("img");
                linkevent.setURL(mediaPanelDialog.lastSnapshotFilename);
                getModel().fireCellFormatChanged(selectionStartRow, selectionStartCol);
                linkPanelDialog.getLinkPanel().setEvent(linkevent, selectionStartRow, selectionStartCol);
                return;
            } catch (JexmaraldaException je){
                System.out.println("Could not link");
                return;
            }
        } else if (event.getTime()==PraatPanelEvent.LINK_AUDIO_SNIPPET){
            // this is not really a time, but the command to
            // link an audio snippet to the current event
            try{
                Event linkevent = getModel().getEvent(selectionStartRow, selectionStartCol);
                linkevent.setMedium("aud");
                linkevent.setURL(mediaPanelDialog.lastAudioPartFilename);
                getModel().fireCellFormatChanged(selectionStartRow, selectionStartCol);
                linkPanelDialog.getLinkPanel().setEvent(linkevent, selectionStartRow, selectionStartCol);
                return;
            } catch (JexmaraldaException je){
                System.out.println("Could not link");
                return;
            }
        } else if (event.getTime()==PraatPanelEvent.PLAYBACK_MODE_ON){
            setPlaybackMode(true);
            return;
        } else if (event.getTime()==PraatPanelEvent.PLAYBACK_MODE_OFF){
            setPlaybackMode(false);
            return;
        } else if (event.getTime()==PraatPanelEvent.SOUND_FILE_SELECTION_CHANGED){
            int index = event.getIndex();
            String path = getModel().getTranscription().getHead().getMetaInformation().getReferencedFiles().elementAt(index);
            getModel().getTranscription().getHead().getMetaInformation().getReferencedFiles().removeElementAt(index);
            getModel().getTranscription().getHead().getMetaInformation().getReferencedFiles().insertElementAt(path, 0);
            setupMedia();
            return;
        }
        // NOW we're talking about time!
        double time = event.getTime();
        TimelineItem tli = new TimelineItem();
        tli.setTime(time);
        getModel().editTimelineItem(selectionStartCol, tli);
        mediaPanelDialog.setStartTime(time);
        //System.out.println("Set time of TLI " + selectionStartCol + " to " + tli.getTimeAsString());
    }

    public void assignTimes(double t1, double t2) {
        TimelineItem tli1 = new TimelineItem();
        tli1.setTime(t1);
        if (undoEnabled){
            // Undo information
            TimelineItem timelineItem = getModel().getTimelineItem(selectionStartCol);
            UndoInformation undoInfo = new UndoInformation(this, "Edit timeline item");
            undoInfo.memorizeTime(timelineItem, timelineItem.getTime());
            addUndo(undoInfo);
            // end undo information

        }
        getModel().editTimelineItem(selectionStartCol, tli1);

        if (t2!=t1){
            TimelineItem tli2 = new TimelineItem();
            tli2.setTime(t2);
            if (undoEnabled){
                // Undo information
                TimelineItem timelineItem = getModel().getTimelineItem(selectionEndCol+1);
                UndoInformation undoInfo = new UndoInformation(this, "Edit timeline item");
                undoInfo.memorizeTime(timelineItem, timelineItem.getTime());
                addUndo(undoInfo);
                // end undo information

            }
            getModel().editTimelineItem(selectionEndCol+1, tli2);
        }

        fireMediaTimeChanged(t1, t2);
    }

    
    
    /** *  processes a search result from the search dialog, i.e.scrolls the partitur to the appropriate position
     * @param esr 
     */
    @Override
    public void processSearchResult(org.exmaralda.partitureditor.search.EventSearchResult esr) {
        System.out.println("processing search result");
        commitEdit(true);
        if ((esr.tierName!=null) && (esr.tierName.equals("###TIMELINE###"))){
            String tliID = esr.tierID;
            int col = Math.max(0, getModel().getColumnNumber(tliID));
            this.makeVisible(0,col);
            return;
        }        
        int colNo = Math.max(0, getModel().getColumnNumber(esr.event.getStart()));
        int rowNo = Math.max(0, getModel().getRowNumber(esr.tierID));
        this.makeVisible(rowNo, colNo);
        // this.setSelection(rowNo, colNo, rowNo, colNo);
        // added 08-10-2009 to ensure that the event is question
        // is not at the right edge of the screen
        // but rather towards the centre
        // (user request from Marc Reznicek, HU Berlin)
        if ((colNo>0) && getColumnPixelWidth(colNo-1)<300){
            setLeftColumn(colNo-1);
        }
        setSelectedCells(new JCCellRange(rowNo, colNo, rowNo, colNo));
    }
    
    /** processes a replace result from the replace dialog
     * @param esr
     * @param replaceString */
    @Override
    public void processReplaceResult(org.exmaralda.partitureditor.search.EventSearchResult esr, String replaceString) {
        try{
            commitEdit(true);
            Tier tier = getModel().getTranscription().getBody().getTierWithID(esr.tierID);
            Event event = tier.getEventAtStartPoint(esr.event.getStart());
            String old = event.getDescription();
            int colNo = Math.max(0, getModel().getColumnNumber(esr.event.getStart()));
            int rowNo = Math.max(0, getModel().getRowNumber(esr.tierID));
            if (undoEnabled){
                UndoInformation undoInfo = new UndoInformation(this, "Replace");
                undoInfo.restoreType = UndoInformation.RESTORE_CELL;
                undoInfo.restoreObject = new RestoreCellInfo(rowNo, colNo, old);
                addUndo(undoInfo);
            }
            String newDescription = old.substring(0, esr.offset) + replaceString + old.substring(esr.offset + esr.length);
            getModel().setTableDataItem(newDescription, rowNo, colNo);
            this.makeVisible(rowNo, colNo);
            this.setSelectedCells(new JCCellRange(rowNo, colNo, rowNo, colNo));            
        } catch (JexmaraldaException je){
            // do nothing
        }
    }
    
    /** processes a request from the replace dialog to replace all found instances
     * @param resultVector
     * @param searchString
     * @param replaceString */
    @Override
    public void processReplaceAll(Vector resultVector, String searchString, String replaceString) {
        if (undoEnabled){
            UndoInformation undoInfo = new UndoInformation(this, "Replace all");
            undoInfo.memorizeTranscription(this);
            addUndo(undoInfo);
        }
        String tierID = "";
        String startID = "";
        // make sure that every event only occurs once in the list
        for (int pos=0; pos<resultVector.size(); pos++){
            org.exmaralda.partitureditor.search.EventSearchResult esr = (org.exmaralda.partitureditor.search.EventSearchResult)(resultVector.elementAt(pos));
            if ((esr.tierID.equals(tierID) && (esr.event.getStart().equals(startID)))){
                resultVector.removeElementAt(pos);
                pos--;
            } else {
                tierID = esr.tierID;
                startID = esr.event.getStart();
            }            
        }
                
        // replace
        for (int pos=0; pos<resultVector.size(); pos++){
            org.exmaralda.partitureditor.search.EventSearchResult esr = (org.exmaralda.partitureditor.search.EventSearchResult)(resultVector.elementAt(pos));
            try{
                //System.out.println(esr.tierID + " " + esr.event.getStart());
                Tier tier = getModel().getTranscription().getBody().getTierWithID(esr.tierID);
                Event event = tier.getEventAtStartPoint(esr.event.getStart());
                String desc = event.getDescription();
                String newDesc = desc.replaceAll("\\Q" + searchString + "\\E", replaceString);
                event.setDescription(newDesc);
                /*int index = 0;
                while (desc.indexOf(searchString, index)!=-1){
                    index = desc.indexOf(searchString, index);
                    String before = desc.substring(0,index);
                    String after = desc.substring(index + searchString.length());
                    String newDescription = before + replaceString + after;
                    index+=replaceString.length();      
                    event.setDescription(newDescription);
                    desc = newDescription;
                }*/
            } catch (JexmaraldaException je){
            }
        }
        getModel().fireFormatReset();
    }
    
    /** starts the auto save thread */
    public void startAutoSaveThread(){
        if (autoSave){
            autoSaveThread.start();
        }
    }
    
    /** stops the auto save thread */
    public void stopAutoSaveThread(){
        autoSaveThread.stop();
    }
    
    /** reconfigures (i.e. stops and starts again) the auto save thread */
    public void reconfigureAutoSaveThread(){
        stopAutoSaveThread();
        autoSaveThread.setTranscription(getModel().getTranscription());
        startAutoSaveThread();
    }

    @Override
    public void processError(String file, String tier, String start) {
        //System.out.println(file + " " + tier + " " + start);
        try {
            if (!(new File(file).toURI().equals(new File(filename).toURI()))){
                if (transcriptionChanged){
                    checkSave();
                }
                BasicTranscription bt = new BasicTranscription(file);
                // # issue #315
                stratify(bt);
                
                getModel().setTranscription(bt);
                this.setFilename(file);
                this.setupMedia();
            }
        } catch (JexmaraldaException | SAXException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
        }
        // added 09-04-2010
        commitEdit(true);
        int colNo = -1;
        if (start.length()>0){
            colNo = Math.max(0, getModel().getColumnNumber(start));
        }
        int rowNo = Math.max(-1, getModel().getRowNumber(tier));
        if (colNo>=0){
            makeVisible(rowNo, colNo);
            setSelectedCells(new JCCellRange(rowNo, colNo, rowNo, colNo));
        } else {
            this.setNewSelection(rowNo, -1, false);
        }
    }
    
    @Override
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {        
        if (!getModel().timelineMode) return;
        if (selectionStartCol<0) return;
        if (selectionStartCol!=selectionEndCol) return;
        TimelineItem tli = getModel().getTimelineItem(selectionStartCol);
        //System.out.println(tli.getDescription(selectionStartCol));
        if (tli.getTime()<0) return;
        double newTime = tli.getTime() + e.getWheelRotation() * -0.1;
        tli.setTime(newTime);
        getModel().fireColumnLabelChanged(selectionStartCol);
        this.mediaPanelDialog.setStartTime(newTime);
        this.mediaPanelDialog.setEndTime(newTime+1.0);
        //this.mediaPanelDialog.doPlay();
    }
    
    public boolean playbackMode = false;
    
    @Override
    public void processPlayableEvent(org.exmaralda.partitureditor.sound.PlayableEvent e){
        if (!playbackMode) return;
        if (!(e.getType()==org.exmaralda.partitureditor.sound.PlayableEvent.POSITION_UPDATE)) return;
        //System.out.println("processPlayableEvent in PartitureTableWithActions - " + e.getPosition());
        final int col = getModel().getTranscription().getBody().getCommonTimeline().getPositionForTime(e.getPosition());
        //System.out.println("JENES! " + col);        
        // changed 18-06-2008
        /*if (!this.isColumnVisible(col)){
            this.makeColumnVisible(col);
        }*/
        //issue #98: put that in a separate thread?
        // but who understands threading?
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                setLeftColumn(col);
                if (selectionStartCol!=col){
                    setSelection(-1,col,-1,col);
                }
            }
            
        });
    }

    public final Playable makePlayer(){

        /*if (true){
            return new VLCPlayer();
        }*/
        
        java.util.prefs.Preferences settings =
                java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor");

        String os = System.getProperty("os.name").substring(0,3);
        //String jreVersion = System.getProperty("java.version");
        String defaultPlayer = "BAS-Audio-Player";
        if (os.equalsIgnoreCase("mac")){
            defaultPlayer = "AVF-Player";
        } else if (os.equalsIgnoreCase("win")){
            defaultPlayer = "JDS-Player";
        }

        System.out.println("Default player: " + defaultPlayer);
        
        String playerType = settings.get("PlayerType", defaultPlayer);
        settings.put("PlayerType", playerType);
        
        System.out.println("User's player: " + playerType);

        // changed 11-10-2011: make sure that default player
        // is always used under EXAKT
        // why??? 02-12-2015
        // take this away, 28-11-2020
        /*if (parent instanceof EXAKT){
        //playerType = defaultPlayer;
        //playerType = "BAS-Audio-Player";
        //}*/
        switch (playerType) {
            /******************************************/
            /******************************************/
            /******************************************/
            // These are the real players...
            case "JDS-Player":
                return new JDSPlayer();
            case "JavaFX-Player":
                return new JavaFXPlayer();
            case "AVF-Player":
                return new AVFPlayer();
            case "MMF-Player":
                MMFPlayer thePlayer = new MMFPlayer();
                return thePlayer;
            case "BAS-Audio-Player":
                return new BASAudioPlayer();
            /******************************************/
            /******************************************/
            /******************************************/
            // And these are outdated players, must be overridden
            case "JMF-Player":
                {
                    settings.put("PlayerType", "BAS-Audio-Player");
                    String message = "The JMF Player is no longer used. \n"
                            + ".\nCreating BAS Audio player instead.\n"
                            + "Changed settings accordingly.";
                    javax.swing.JOptionPane.showMessageDialog(this, message);
                    return new BASAudioPlayer();
                }
            case "DirectShow-Player":
                {
                    settings.put("PlayerType", "JDS-Player");
                    String message = "The ELAN DS Player is no longer used. \n"
                            + ".\nCreating JDS player instead.\n"
                            + "Changed settings accordingly.";
                    javax.swing.JOptionPane.showMessageDialog(this, message);
                    return new JDSPlayer();
                }
            case "ELAN-Quicktime-Player":
                {
                    settings.put("PlayerType", "BAS-Audio-Player");
                    String message = "The ELAN-Quicktime Player is no longer used. \n"
                            + ".\nCreating BAS Audio player instead.\n"
                            + "Changed settings accordingly.";
                    javax.swing.JOptionPane.showMessageDialog(this, message);
                    return new BASAudioPlayer();
                }
            case "CocoaQT-Player":
                {
                    settings.put("PlayerType", "BAS-Audio-Player");
                    String message = "The CocoaQT-Player is no longer used. \n"
                            + ".\nCreating AVF player instead.\n"
                            + "Changed settings accordingly.";
                    javax.swing.JOptionPane.showMessageDialog(this, message);
                    return new AVFPlayer();
                }
            case "Quicktime-Player":
                {
                    settings.put("PlayerType", "ELAN-Quicktime-Player");
                    String message = "Do not want to create old Quicktime player. \n"
                            + ".\nCreating AVF player instead.\n"
                            + "Changed settings accordingly.";
                    javax.swing.JOptionPane.showMessageDialog(this, message);
                    return new AVFPlayer();
                }
            default:
                break;
        }
        return new BASAudioPlayer();
    }

    @Override
    protected void processComponentKeyEvent(KeyEvent e) {
        // added 08-04-2009: don't edit when control is pressed
        cancelEdit = e.isControlDown();
        super.processComponentKeyEvent(e);
    }

    @Override
    public void processTimeSelectionEvent(TimeSelectionEvent event) {
        currentSelectionStart = event.getStartTime() / 1000.0;
        currentSelectionEnd = event.getEndTime() / 1000.0;
        currentSelectionLength = (event.getEndTime()-event.getStartTime())/1000.0;
        insertPauseAction.setEnabled(currentSelectionLength>0.0);
        mediaPanelDialog.setTimeSelection(event);
    }
    
    


    /*********************************************
     * UNDO HANDLING
     **********************************************/


    public boolean undoEnabled = true;
    public UndoHandler undoHandler = new UndoHandler();
    UndoInformation undoEditCellInfo;

    public void addUndo(UndoInformation info){        
        undoHandler.addUndoInformation(info);
        undoAction.setEnabled(true);
        fireUndoChanged(undoHandler.getCurrentDescription());
    }

    public void undo() {
        // added 22-09-2011: disable until undo is complete
        undoAction.setEnabled(false);
        undoHandler.undo(this);
        undoAction.setEnabled(!(undoHandler.isEmpty()));
        fireUndoChanged(undoHandler.getCurrentDescription());
    }

    public void clearUndo() {
        undoHandler = new UndoHandler();
        undoAction.setEnabled(false);
        fireUndoChanged(null);
    }

    public void clearSearchResult() {
        ((SearchInEventsAction)(searchInEventsAction)).dialog.clearSearchResult();
    }

    void setMediaPlaybackRate(double newRate) {
        if (player instanceof JDSPlayer){
            ((JDSPlayer)player).setPlaybackRate(newRate); 
        } else if (player instanceof AVFPlayer){
            ((AVFPlayer)player).setPlaybackRate(newRate);             
        } else if (player instanceof JavaFXPlayer){
            ((JavaFXPlayer)player).setPlaybackRate(newRate);             
        }
    }

    @Override
    public void processXPointer(String xPointer) {
        //JOptionPane.showMessageDialog(parent, xPointer);
        if (isEditing){
            editor = (PartitureCellStringEditor)(getEditingComponent());
            editor.replaceSelection(xPointer);
        }        
    }

    // changed 11-01-2019 for issue #176
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        if (!(e.getSource() instanceof EventPopupMenu)) return;
        ((JPopupMenu)(e.getSource())).add(moveMenu, 6);
        moveMenu.removeAll();
        if (!(selectionStartRow==selectionEndRow && getModel().containsEvent(selectionStartRow, selectionStartCol) && getModel().containsEvent(selectionStartRow, selectionEndCol))){
            moveMenu.setEnabled(false);
            return;
        } 
        Event firstEvent = null;
        Event lastEvent = null;
        try {
            firstEvent = getModel().getEvent(selectionStartRow, selectionStartCol);
            lastEvent = getModel().getEvent(selectionStartRow, selectionEndCol);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(PartitureTableWithActions.class.getName()).log(Level.SEVERE, null, ex);
        }
        String startID = firstEvent.getStart();
        String endID = lastEvent.getEnd();
        
        //System.out.println(startID + " --- " + endID);
        String sourceTierID = getModel().getTranscription().getBody().getTierAt(selectionStartRow).getID();
        for (int i=0; i<getModel().getTranscription().getBody().getNumberOfTiers(); i++){
            if (i==selectionStartRow) continue;
            Tier tier = getModel().getTranscription().getBody().getTierAt(i);
            if (tier.getEventsIntersecting(getModel().getTranscription().getBody().getCommonTimeline(), startID, endID).isEmpty()){
                try {
                    // this means the tier is free in this place
                    //MoveEventAction thisAction = new MoveEventAction(this, sourceTierID, tier.getID(), startID);
                    MoveEventAction thisAction = new MoveEventAction(this, sourceTierID, tier.getID(), startID, endID);
                    JMenuItem thisMenuItem = moveMenu.add(thisAction);
                    if (tier.getType().equals("t")){
                        thisMenuItem.setFont(thisMenuItem.getFont().deriveFont(Font.BOLD));
                    }
                    moveMenu.setEnabled(true);
                } catch (JexmaraldaException ex) {
                    Logger.getLogger(PartitureTableWithActions.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
    }

    /*@Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        eventPopupMenu.moveMenu.removeAll();
        if (!getModel().containsEvent(selectionStartRow, selectionStartCol)){
            eventPopupMenu.moveMenu.setEnabled(false);
            return;
        } 
        Event event = null;
        try {
            event = getModel().getEvent(selectionStartRow, selectionStartCol);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(PartitureTableWithActions.class.getName()).log(Level.SEVERE, null, ex);
        }
        String startID = event.getStart();
        String endID = event.getEnd();
        String sourceTierID = getModel().getTranscription().getBody().getTierAt(selectionStartRow).getID();
        for (int i=0; i<getModel().getTranscription().getBody().getNumberOfTiers(); i++){
            if (i==selectionStartRow) continue;
            Tier tier = getModel().getTranscription().getBody().getTierAt(i);
            if (tier.getEventsIntersecting(getModel().getTranscription().getBody().getCommonTimeline(), startID, endID).isEmpty()){
                try {
                    // this means the tier is free in this place
                    MoveEventAction thisAction = new MoveEventAction(this, sourceTierID, tier.getID(), startID);
                    JMenuItem thisMenuItem = eventPopupMenu.moveMenu.add(thisAction);
                    if (tier.getType().equals("t")){
                        thisMenuItem.setFont(thisMenuItem.getFont().deriveFont(Font.BOLD));
                    }
                    eventPopupMenu.moveMenu.setEnabled(true);
                } catch (JexmaraldaException ex) {
                    Logger.getLogger(PartitureTableWithActions.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
    }*/

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // do nothing
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        // do nothing
    }

    
    public void hideRow(int row) {
        setRowHidden(row, true);  
        //getModel().getTier(row).getUDTierInformation().setAttribute("exmaralda:hidden", "true");
        getModel().getTranscription().getBody().getTierAt(row).getUDTierInformation().setAttribute("exmaralda:hidden", "true");
    }

    @Override
    public void resetData() {
        super.resetData(); //To change body of generated methods, choose Tools | Templates.
        // new 30-11-2020: issue #232
        for (int i=0; i<this.getModel().getTranscription().getBody().getNumberOfTiers(); i++){
            Tier tier = this.getModel().getTranscription().getBody().getTierAt(i);
            if ("true".equals(tier.getUDTierInformation().getValueOfAttribute("exmaralda:hidden"))){
                hideRow(i);
            }
        }        
    }

    public void applyTransformationScenario(Element transformationScenario) {        
        String[] parameters = new String[]{
            transformationScenario.getChild("input").getAttributeValue("type"),
            transformationScenario.getChild("segmentation").getAttributeValue("type"),
            transformationScenario.getChildText("list-unit"),
            transformationScenario.getChildText("stylesheet"),
            transformationScenario.getChild("output").getAttributeValue("suffix")
        };
        
        String[][] xslParameters = null;
        Element p = transformationScenario.getChild("parameters");
        if (p!=null){
            List l = p.getChildren("parameter");
            int i=0;
            for (Object o : l){
                Element e = (Element)o;
                xslParameters[i][0] = e.getAttributeValue("name");
                xslParameters[i][1] = e.getText();
                i++;                
            }
        }
        
        try {
            ((TransformationAction)transformationAction).transform(getModel().getTranscription().makeCopy(), getAbstractSegmentation(preferredSegmentation), parameters, xslParameters);
        } catch (JDOMException | IOException | SAXException | FSMException | ParserConfigurationException | JexmaraldaException ex) {
            Logger.getLogger(PartitureTableWithActions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
        
    }

    // new 12-02-2021, issue #221
    public void cleanupAutoSave() {
        File[] existingFiles = new File(autoSaveThread.PATH).listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.startsWith(autoSaveThread.FILENAME) && name.endsWith(".exb"));
            }            
        });
        if (existingFiles==null){
            System.out.println("List files returned null for " + autoSaveThread.FILENAME);
            return;
        }
        System.out.println("Found " + existingFiles.length + " auto save files");
        if (existingFiles.length < 10) return;
        String message = "<html><p>Found " + existingFiles.length + " auto backup files in folder </p>"
                + "<p><b>" + autoSaveThread.PATH + "</b></p>"
                + "<p>That's a lot. What do you want to do?</p></html>";
        CleanupAutoSavePanel casp = new CleanupAutoSavePanel();
        casp.setMessage(message);
        
        JOptionPane.showMessageDialog(this, casp, "Cleanup auto backup files", JOptionPane.OK_CANCEL_OPTION, 
            new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/32x32/emblems/emblem-important.png"))
        );
        
        List<File> toBeDeleted = new ArrayList<>();
        if (casp.deleteOlderRadioButton.isSelected()){
            long days = (int) casp.deleteOlderSpinner.getValue();
            long now = System.currentTimeMillis();
            System.out.println("Now: " + now);
            System.out.println("Days: " + (days * 24 * 60 * 60 * 1000));
            for (File f : existingFiles){
                long lastModified = f.lastModified();
                if ((now - lastModified) > (days * 24 * 60 * 60 * 1000)){
                    toBeDeleted.add(f);
                }
            }
            
        } else if (casp.keepRecentRadioButton.isSelected()){
            int from = (int) casp.keepRecentSpinner.getValue();
            Arrays.sort(existingFiles, new Comparator<File>(){
                @Override
                public int compare(File o1, File o2) {
                    return Long.compare(o2.lastModified(), o1.lastModified());
                }                
            });
            for (int i = from; i<existingFiles.length; i++){
                toBeDeleted.add(existingFiles[i]);
            }            
        }
        
        if (toBeDeleted.isEmpty()){
            this.status("No auto backup files deleted");
            //JOptionPane.showMessageDialog(this, "No auto backup files will be deleted");
            return;
        }
        
        // 31-05-2023 changed for #396
        /*int really = JOptionPane.showConfirmDialog(this, 
                toBeDeleted.size() + " files will be deleted from\n " + autoSaveThread.PATH + "\nAre you sure?", 
                "Confirm delete of auto backup files",
                JOptionPane.YES_NO_OPTION);
        
        if (really==JOptionPane.YES_OPTION){*/
            for (File f : toBeDeleted){
                f.delete();
            }
            this.status(toBeDeleted.size() + " files deleted from\n " + autoSaveThread.PATH);
            /*JOptionPane.showMessageDialog(this, toBeDeleted.size() + " files deleted from\n " + autoSaveThread.PATH );            
        } else {
            JOptionPane.showMessageDialog(this, "No auto backup files will be deleted");            
        }*/
        
        
    }

    private int correctXForScreenSize(int x) {
        int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        if (x>=screenWidth-10){
            return screenWidth - 50;
        }
        return x;
    }

    private int correctYForScreenSize(int y) {
        int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        if (y>=screenHeight){
            return screenHeight - 50;
        }
        return y;
    }
    
    



    

   
    

    
    
    
}
