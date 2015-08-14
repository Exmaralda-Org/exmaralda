/*
 * EditPreferencesDialog.java
 *
 * Created on 6. Oktober 2003, 10:31
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

import java.util.prefs.BackingStoreException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import org.exmaralda.common.helpers.Internationalizer;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.ChooseStylesheetDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.AbstractXMLOpenDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;

/**
 *
 * @author  thomas
 */
public class EditPreferencesDialog extends javax.swing.JDialog {
    
    boolean changed = false;
    public boolean reset = false;
    org.exmaralda.common.ExmaraldaApplication application;
    
    /** Creates new form EditPreferencesDialog */
    public EditPreferencesDialog(java.awt.Frame parent, boolean modal, org.exmaralda.common.ExmaraldaApplication app) {
        super(parent, modal);
        application = app;
        initComponents();
        
        Preferences settings = java.util.prefs.Preferences.userRoot().node(application.getPreferencesNode());
        logDirectoryLabel.setText(settings.get("LOG-Directory", System.getProperty("user.home")));
        String pd = settings.get("PRAAT-Directory", "");
        if (pd.length()>0){
            praatDirectoryLabel.setText(pd);
        }
        
        Internationalizer.internationalizeDialogToolTips(this);
        
        
        // coded live from Ankara, 19-03-2009
        String os = System.getProperty("os.name").toLowerCase();
        
        elanDSPlayerRadioButton.setVisible(os.startsWith("win"));
        jdsPlayerRadioButton.setVisible(os.startsWith("win"));
        mmfPlayerRadioButton.setVisible(os.startsWith("win"));
        elanQuicktimeRadioButton.setVisible(os.startsWith("mac") || (os.startsWith("win") && org.exmaralda.partitureditor.sound.QuicktimePlayer.isQuicktimeAvailable()));
        cocoaQuicktimePlayerRadioButton.setVisible(os.startsWith("mac"));
        
        digitsSpinner.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {updatePauseExample();}
        });
        DocumentListener dl = new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {updatePauseExample();}
            @Override
            public void removeUpdate(DocumentEvent e) {updatePauseExample();}
            @Override
            public void changedUpdate(DocumentEvent e) {updatePauseExample(); }
        };
        pausePrefixTextField.getDocument().addDocumentListener(dl);
        pauseSuffixTextField.getDocument().addDocumentListener(dl);
    }
    
    public String[] getValues(){
        String[] result = new String[31];
        result[0] = tierFontLabel.getText();
        result[1] = generalPurposeFontLabel.getText();
        result[2] = head2HTMLTextField.getText();
        result[3] = speakertable2TranscriptionTextField.getText();
        result[4] = transcription2FormattableTextField.getText();
        result[5] = freeStylesheetVisualisationTextField.getText();
        result[6] = HIATUtteranceList2HTMLTextField.getText();
        result[7] = Boolean.toString(enableAutoSaveCheckBox.isSelected());
        result[8] = autoSaveFilenameTextField.getText();
        result[9] = autoSavePathTextField.getText();
        result[10] = Integer.toString(autoSaveIntervalSlider.getValue());
        result[11] = hiatTextField.getText();
        result[12] = "";
        result[13] = gatTextField.getText();
        result[14] = chatTextField.getText();
        result[15] = Internationalizer.getLanguageForIndex(languagesComboBox.getSelectedIndex());
        //result[16] = (String)(mediaPlayerComboBox.getSelectedItem());
        result[16] = getSelectedMediaPlayer();
        result[17] = Boolean.toString(underlineCharRadioButton.isSelected());
        result[18] = underlineCategoryTextField.getText();
        result[19] = (String)(preferredSegmentationComboBox.getSelectedItem());
        result[20] = Boolean.toString(sfb538MenuCheckBox.isSelected());
        result[21] = Boolean.toString(sinMenuCheckBox.isSelected());
        result[22] = Boolean.toString(odtstdMenuCheckBox.isSelected());
        result[23] = Boolean.toString(autoAnchorCheckBox.isSelected());
        result[24] = Boolean.toString(autoRemoveTLICheckBox.isSelected());
        // pause notation
        result[25] = pausePrefixTextField.getText();
        result[26] = pauseSuffixTextField.getText();
        result[27] = digitsSpinner.getValue().toString();
        result[28] = Boolean.toString(decimalCommaRadioButton.isSelected());
        result[29] = Boolean.toString(enableUndoCheckBox.isSelected());
        result[30] = Boolean.toString(autoInterpolateCheckBox.isSelected());
        return result;        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        underlineButtonGroup = new javax.swing.ButtonGroup();
        pauseNotationButtonGroup = new javax.swing.ButtonGroup();
        mediaPlayersButtonGroup = new javax.swing.ButtonGroup();
        bottomPanel = new javax.swing.JPanel();
        okCancelPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        fontsPanel = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        tierFontPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tierFontLabel = new javax.swing.JLabel();
        changeTierFontButton = new javax.swing.JButton();
        generalPurposeFontPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        generalPurposeFontLabel = new javax.swing.JLabel();
        changeGeneralPurposeFontButton = new javax.swing.JButton();
        underlineMethodPanel = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        underlineTierRadioButton = new javax.swing.JRadioButton();
        underlineCategoryTextField = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        underlineCharRadioButton = new javax.swing.JRadioButton();
        stylesheetsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        head2HTMLTextField = new javax.swing.JTextField();
        changeHead2HTMLButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        speakertable2TranscriptionTextField = new javax.swing.JTextField();
        changeSpeakertable2TranscriptionButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        transcription2FormattableTextField = new javax.swing.JTextField();
        changeTranscription2FormattableButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        freeStylesheetVisualisationTextField = new javax.swing.JTextField();
        changeFreeStylesheetVisualisationButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        HIATUtteranceList2HTMLTextField = new javax.swing.JTextField();
        changeHIATUtteranceList2HTMLButton = new javax.swing.JButton();
        segmentationPanel = new javax.swing.JPanel();
        fsmPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        hiatTextField = new javax.swing.JTextField();
        changeHIATButton = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        gatTextField = new javax.swing.JTextField();
        changeGATButton = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        chatTextField = new javax.swing.JTextField();
        changeCHATButton = new javax.swing.JButton();
        preferredSegmentationPanel = new javax.swing.JPanel();
        preferredSegmentationComboBox = new javax.swing.JComboBox();
        pauseNotationPanel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        pausePrefixTextField = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        pauseSuffixTextField = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        decimalPointRadioButton = new javax.swing.JRadioButton();
        decimalCommaRadioButton = new javax.swing.JRadioButton();
        jLabel27 = new javax.swing.JLabel();
        digitsSpinner = new javax.swing.JSpinner();
        jLabel28 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        testLabel = new javax.swing.JLabel();
        autoSavePanel = new javax.swing.JPanel();
        enableAutoSavePanel = new javax.swing.JPanel();
        enableAutoSaveCheckBox = new javax.swing.JCheckBox();
        autoSaveFilenamePanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        autoSaveFilenameTextField = new javax.swing.JTextField();
        autoSavePathPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        autoSavePathTextField = new javax.swing.JTextField();
        browseForAutoSavePathButton = new javax.swing.JButton();
        autoSaveIntervalPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        autoSaveIntervalSlider = new javax.swing.JSlider();
        jLabel11 = new javax.swing.JLabel();
        enableUndoPanel = new javax.swing.JPanel();
        enableUndoCheckBox = new javax.swing.JCheckBox();
        languagePanel = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        languagesComboBox = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        mediaPanel = new javax.swing.JPanel();
        playerSelectionPanel = new javax.swing.JPanel();
        basPlayerRadioButton = new javax.swing.JRadioButton();
        jdsPlayerRadioButton = new javax.swing.JRadioButton();
        mmfPlayerRadioButton = new javax.swing.JRadioButton();
        elanDSPlayerRadioButton = new javax.swing.JRadioButton();
        cocoaQuicktimePlayerRadioButton = new javax.swing.JRadioButton();
        elanQuicktimeRadioButton = new javax.swing.JRadioButton();
        jmfPlayerRadioButton = new javax.swing.JRadioButton();
        otherOptionsPanel = new javax.swing.JPanel();
        autoAnchorCheckBox = new javax.swing.JCheckBox();
        autoRemoveTLICheckBox = new javax.swing.JCheckBox();
        autoInterpolateCheckBox = new javax.swing.JCheckBox();
        topPanel = new javax.swing.JPanel();
        pathsPanel = new javax.swing.JPanel();
        logPanel = new javax.swing.JPanel();
        logDirectoryLabel = new javax.swing.JLabel();
        setLogDirectoryButton = new javax.swing.JButton();
        praatPanel = new javax.swing.JPanel();
        praatDirectoryLabel = new javax.swing.JLabel();
        changePraatDirectoryButton = new javax.swing.JButton();
        menusPanel = new javax.swing.JPanel();
        sfb538MenuCheckBox = new javax.swing.JCheckBox();
        sinMenuCheckBox = new javax.swing.JCheckBox();
        odtstdMenuCheckBox = new javax.swing.JCheckBox();
        jPanel16 = new javax.swing.JPanel();
        resetButton = new javax.swing.JButton();

        setTitle("Preferences");
        setPreferredSize(new java.awt.Dimension(600, 450));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        bottomPanel.setLayout(new java.awt.BorderLayout());

        okButton.setText("OK");
        okButton.setMaximumSize(new java.awt.Dimension(110, 27));
        okButton.setMinimumSize(new java.awt.Dimension(73, 26));
        okButton.setPreferredSize(new java.awt.Dimension(110, 27));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okCancelPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.setMaximumSize(new java.awt.Dimension(110, 27));
        cancelButton.setPreferredSize(new java.awt.Dimension(110, 27));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        okCancelPanel.add(cancelButton);

        bottomPanel.add(okCancelPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        mainPanel.setLayout(new java.awt.BorderLayout());

        tabbedPane.setMaximumSize(new java.awt.Dimension(800, 600));
        tabbedPane.setMinimumSize(new java.awt.Dimension(120, 22));
        tabbedPane.setPreferredSize(new java.awt.Dimension(600, 300));

        fontsPanel.setLayout(new javax.swing.BoxLayout(fontsPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Fonts"));

        tierFontPanel.setLayout(new javax.swing.BoxLayout(tierFontPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText("Default tier font: ");
        jLabel1.setMaximumSize(new java.awt.Dimension(180, 20));
        jLabel1.setMinimumSize(new java.awt.Dimension(180, 20));
        jLabel1.setPreferredSize(new java.awt.Dimension(180, 20));
        tierFontPanel.add(jLabel1);

        tierFontLabel.setForeground(new java.awt.Color(51, 102, 255));
        tierFontLabel.setText("Times New Roman");
        tierFontLabel.setMaximumSize(new java.awt.Dimension(180, 20));
        tierFontLabel.setMinimumSize(new java.awt.Dimension(180, 20));
        tierFontLabel.setPreferredSize(new java.awt.Dimension(180, 20));
        tierFontPanel.add(tierFontLabel);

        changeTierFontButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        changeTierFontButton.setText("Change...");
        changeTierFontButton.setMaximumSize(new java.awt.Dimension(100, 20));
        changeTierFontButton.setMinimumSize(new java.awt.Dimension(80, 20));
        changeTierFontButton.setPreferredSize(new java.awt.Dimension(100, 20));
        changeTierFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeTierFontButtonActionPerformed(evt);
            }
        });
        tierFontPanel.add(changeTierFontButton);

        jPanel15.add(tierFontPanel);

        generalPurposeFontPanel.setLayout(new javax.swing.BoxLayout(generalPurposeFontPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setText("Default general purpose font: ");
        jLabel3.setMaximumSize(new java.awt.Dimension(180, 20));
        jLabel3.setMinimumSize(new java.awt.Dimension(180, 20));
        jLabel3.setPreferredSize(new java.awt.Dimension(180, 20));
        generalPurposeFontPanel.add(jLabel3);

        generalPurposeFontLabel.setForeground(new java.awt.Color(51, 51, 255));
        generalPurposeFontLabel.setText("Times New Roman");
        generalPurposeFontLabel.setToolTipText("");
        generalPurposeFontLabel.setMaximumSize(new java.awt.Dimension(180, 20));
        generalPurposeFontLabel.setMinimumSize(new java.awt.Dimension(180, 20));
        generalPurposeFontLabel.setPreferredSize(new java.awt.Dimension(180, 20));
        generalPurposeFontPanel.add(generalPurposeFontLabel);

        changeGeneralPurposeFontButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        changeGeneralPurposeFontButton.setText("Change...");
        changeGeneralPurposeFontButton.setMaximumSize(new java.awt.Dimension(100, 20));
        changeGeneralPurposeFontButton.setMinimumSize(new java.awt.Dimension(80, 20));
        changeGeneralPurposeFontButton.setPreferredSize(new java.awt.Dimension(100, 20));
        changeGeneralPurposeFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeGeneralPurposeFontButtonActionPerformed(evt);
            }
        });
        generalPurposeFontPanel.add(changeGeneralPurposeFontButton);

        jPanel15.add(generalPurposeFontPanel);

        fontsPanel.add(jPanel15);

        underlineMethodPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Underline Method"));

        underlineButtonGroup.add(underlineTierRadioButton);
        underlineTierRadioButton.setSelected(true);
        underlineTierRadioButton.setText("Underline in a separate tier of category ");
        underlineTierRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        underlineTierRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanel10.add(underlineTierRadioButton);

        underlineCategoryTextField.setText("akz");
        underlineCategoryTextField.setMaximumSize(new java.awt.Dimension(100, 19));
        underlineCategoryTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        underlineCategoryTextField.setPreferredSize(new java.awt.Dimension(50, 19));
        jPanel10.add(underlineCategoryTextField);

        underlineMethodPanel.add(jPanel10);

        underlineButtonGroup.add(underlineCharRadioButton);
        underlineCharRadioButton.setText("Underline in the same tier (using a diacritic) ");
        underlineCharRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        underlineCharRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanel11.add(underlineCharRadioButton);

        underlineMethodPanel.add(jPanel11);

        fontsPanel.add(underlineMethodPanel);

        tabbedPane.addTab("Fonts", fontsPanel);

        stylesheetsPanel.setLayout(new javax.swing.BoxLayout(stylesheetsPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Head to HTML"));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        head2HTMLTextField.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        head2HTMLTextField.setMinimumSize(new java.awt.Dimension(180, 20));
        head2HTMLTextField.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel1.add(head2HTMLTextField);

        changeHead2HTMLButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        changeHead2HTMLButton.setText("Change...");
        changeHead2HTMLButton.setMaximumSize(new java.awt.Dimension(100, 20));
        changeHead2HTMLButton.setMinimumSize(new java.awt.Dimension(80, 20));
        changeHead2HTMLButton.setPreferredSize(new java.awt.Dimension(100, 20));
        changeHead2HTMLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeHead2HTMLButtonActionPerformed(evt);
            }
        });
        jPanel1.add(changeHead2HTMLButton);

        stylesheetsPanel.add(jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Speakertable to transcription"));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        speakertable2TranscriptionTextField.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        speakertable2TranscriptionTextField.setMinimumSize(new java.awt.Dimension(180, 20));
        speakertable2TranscriptionTextField.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel2.add(speakertable2TranscriptionTextField);

        changeSpeakertable2TranscriptionButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        changeSpeakertable2TranscriptionButton.setText("Change...");
        changeSpeakertable2TranscriptionButton.setMaximumSize(new java.awt.Dimension(100, 20));
        changeSpeakertable2TranscriptionButton.setMinimumSize(new java.awt.Dimension(80, 20));
        changeSpeakertable2TranscriptionButton.setPreferredSize(new java.awt.Dimension(100, 20));
        changeSpeakertable2TranscriptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeSpeakertable2TranscriptionButtonActionPerformed(evt);
            }
        });
        jPanel2.add(changeSpeakertable2TranscriptionButton);

        stylesheetsPanel.add(jPanel2);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Transcription to format table"));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        transcription2FormattableTextField.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        transcription2FormattableTextField.setMinimumSize(new java.awt.Dimension(180, 20));
        transcription2FormattableTextField.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel3.add(transcription2FormattableTextField);

        changeTranscription2FormattableButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        changeTranscription2FormattableButton.setText("Change...");
        changeTranscription2FormattableButton.setMaximumSize(new java.awt.Dimension(100, 20));
        changeTranscription2FormattableButton.setMinimumSize(new java.awt.Dimension(80, 20));
        changeTranscription2FormattableButton.setPreferredSize(new java.awt.Dimension(100, 20));
        changeTranscription2FormattableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeTranscription2FormattableButtonActionPerformed(evt);
            }
        });
        jPanel3.add(changeTranscription2FormattableButton);

        stylesheetsPanel.add(jPanel3);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Free stylesheet transformation"));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        freeStylesheetVisualisationTextField.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        freeStylesheetVisualisationTextField.setMinimumSize(new java.awt.Dimension(180, 20));
        freeStylesheetVisualisationTextField.setPreferredSize(new java.awt.Dimension(180, 20));
        jPanel4.add(freeStylesheetVisualisationTextField);

        changeFreeStylesheetVisualisationButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        changeFreeStylesheetVisualisationButton.setText("Change...");
        changeFreeStylesheetVisualisationButton.setMaximumSize(new java.awt.Dimension(100, 20));
        changeFreeStylesheetVisualisationButton.setMinimumSize(new java.awt.Dimension(80, 20));
        changeFreeStylesheetVisualisationButton.setPreferredSize(new java.awt.Dimension(100, 20));
        changeFreeStylesheetVisualisationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeFreeStylesheetVisualisationButtonActionPerformed(evt);
            }
        });
        jPanel4.add(changeFreeStylesheetVisualisationButton);

        stylesheetsPanel.add(jPanel4);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("HIAT utterance list to HTML"));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        HIATUtteranceList2HTMLTextField.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        HIATUtteranceList2HTMLTextField.setMinimumSize(new java.awt.Dimension(180, 20));
        HIATUtteranceList2HTMLTextField.setPreferredSize(new java.awt.Dimension(180, 20));
        jPanel5.add(HIATUtteranceList2HTMLTextField);

        changeHIATUtteranceList2HTMLButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        changeHIATUtteranceList2HTMLButton.setText("Change...");
        changeHIATUtteranceList2HTMLButton.setMaximumSize(new java.awt.Dimension(100, 20));
        changeHIATUtteranceList2HTMLButton.setMinimumSize(new java.awt.Dimension(80, 20));
        changeHIATUtteranceList2HTMLButton.setPreferredSize(new java.awt.Dimension(100, 20));
        changeHIATUtteranceList2HTMLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeHIATUtteranceList2HTMLButtonActionPerformed(evt);
            }
        });
        jPanel5.add(changeHIATUtteranceList2HTMLButton);

        stylesheetsPanel.add(jPanel5);

        tabbedPane.addTab("Stylesheets", stylesheetsPanel);

        segmentationPanel.setLayout(new java.awt.BorderLayout());

        fsmPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Finite State Machines"));
        fsmPanel.setLayout(new javax.swing.BoxLayout(fsmPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        jLabel12.setText("HIAT:");
        jLabel12.setMaximumSize(new java.awt.Dimension(90, 20));
        jLabel12.setMinimumSize(new java.awt.Dimension(180, 20));
        jLabel12.setPreferredSize(new java.awt.Dimension(90, 20));
        jPanel6.add(jLabel12);

        hiatTextField.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        hiatTextField.setMinimumSize(new java.awt.Dimension(180, 20));
        hiatTextField.setPreferredSize(new java.awt.Dimension(180, 20));
        jPanel6.add(hiatTextField);

        changeHIATButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        changeHIATButton.setText("Change...");
        changeHIATButton.setMaximumSize(new java.awt.Dimension(100, 20));
        changeHIATButton.setMinimumSize(new java.awt.Dimension(80, 20));
        changeHIATButton.setPreferredSize(new java.awt.Dimension(100, 20));
        changeHIATButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeHIATButtonActionPerformed(evt);
            }
        });
        jPanel6.add(changeHIATButton);

        fsmPanel.add(jPanel6);

        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

        jLabel14.setText("GAT:");
        jLabel14.setMaximumSize(new java.awt.Dimension(90, 20));
        jLabel14.setMinimumSize(new java.awt.Dimension(180, 20));
        jLabel14.setPreferredSize(new java.awt.Dimension(90, 20));
        jPanel8.add(jLabel14);

        gatTextField.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        gatTextField.setMinimumSize(new java.awt.Dimension(180, 20));
        gatTextField.setPreferredSize(new java.awt.Dimension(180, 20));
        jPanel8.add(gatTextField);

        changeGATButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        changeGATButton.setText("Change...");
        changeGATButton.setMaximumSize(new java.awt.Dimension(100, 20));
        changeGATButton.setMinimumSize(new java.awt.Dimension(80, 20));
        changeGATButton.setPreferredSize(new java.awt.Dimension(100, 20));
        changeGATButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeGATButtonActionPerformed(evt);
            }
        });
        jPanel8.add(changeGATButton);

        fsmPanel.add(jPanel8);

        jPanel9.setLayout(new javax.swing.BoxLayout(jPanel9, javax.swing.BoxLayout.LINE_AXIS));

        jLabel15.setText("CHAT: ");
        jLabel15.setMaximumSize(new java.awt.Dimension(90, 20));
        jLabel15.setMinimumSize(new java.awt.Dimension(180, 20));
        jLabel15.setPreferredSize(new java.awt.Dimension(90, 20));
        jPanel9.add(jLabel15);

        chatTextField.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        chatTextField.setMinimumSize(new java.awt.Dimension(180, 20));
        chatTextField.setPreferredSize(new java.awt.Dimension(180, 20));
        jPanel9.add(chatTextField);

        changeCHATButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        changeCHATButton.setText("Change...");
        changeCHATButton.setMaximumSize(new java.awt.Dimension(100, 20));
        changeCHATButton.setMinimumSize(new java.awt.Dimension(80, 20));
        changeCHATButton.setPreferredSize(new java.awt.Dimension(100, 20));
        changeCHATButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeCHATButtonActionPerformed(evt);
            }
        });
        jPanel9.add(changeCHATButton);

        fsmPanel.add(jPanel9);

        segmentationPanel.add(fsmPanel, java.awt.BorderLayout.CENTER);

        preferredSegmentationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Preferred Segmentation"));

        preferredSegmentationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GENERIC", "HIAT", "DIDA", "GAT", "cGAT_MINIMAL", "CHAT", "CHAT_MINIMAL", "IPA" }));
        preferredSegmentationComboBox.setMinimumSize(new java.awt.Dimension(100, 18));
        preferredSegmentationComboBox.setPreferredSize(new java.awt.Dimension(130, 27));
        preferredSegmentationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferredSegmentationComboBoxActionPerformed(evt);
            }
        });
        preferredSegmentationPanel.add(preferredSegmentationComboBox);

        segmentationPanel.add(preferredSegmentationPanel, java.awt.BorderLayout.PAGE_START);

        pauseNotationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Pause Notation"));
        pauseNotationPanel.setLayout(new java.awt.BorderLayout());

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));

        jLabel13.setText("Prefix: ");
        jPanel7.add(jLabel13);

        pausePrefixTextField.setText("((");
        pausePrefixTextField.setMaximumSize(new java.awt.Dimension(50, 20));
        pausePrefixTextField.setPreferredSize(new java.awt.Dimension(40, 20));
        pausePrefixTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pausePrefixTextFieldActionPerformed(evt);
            }
        });
        jPanel7.add(pausePrefixTextField);

        pauseNotationPanel.add(jPanel7, java.awt.BorderLayout.WEST);

        jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.LINE_AXIS));

        jLabel24.setText(" Suffix: ");
        jPanel12.add(jLabel24);

        pauseSuffixTextField.setText("s))");
        pauseSuffixTextField.setMaximumSize(new java.awt.Dimension(50, 20));
        pauseSuffixTextField.setPreferredSize(new java.awt.Dimension(40, 20));
        pauseSuffixTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseSuffixTextFieldActionPerformed(evt);
            }
        });
        jPanel12.add(pauseSuffixTextField);

        pauseNotationPanel.add(jPanel12, java.awt.BorderLayout.EAST);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel26.setText("Decimal...");
        jPanel13.add(jLabel26);

        pauseNotationButtonGroup.add(decimalPointRadioButton);
        decimalPointRadioButton.setText("...point");
        decimalPointRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decimalPointRadioButtonActionPerformed(evt);
            }
        });
        jPanel13.add(decimalPointRadioButton);

        pauseNotationButtonGroup.add(decimalCommaRadioButton);
        decimalCommaRadioButton.setSelected(true);
        decimalCommaRadioButton.setText("...comma");
        decimalCommaRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decimalCommaRadioButtonActionPerformed(evt);
            }
        });
        jPanel13.add(decimalCommaRadioButton);

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel27.setText("Round to ");
        jPanel13.add(jLabel27);

        digitsSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 3, 1));
        digitsSpinner.setMinimumSize(new java.awt.Dimension(40, 20));
        digitsSpinner.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel13.add(digitsSpinner);

        jLabel28.setText(" digits");
        jPanel13.add(jLabel28);

        pauseNotationPanel.add(jPanel13, java.awt.BorderLayout.NORTH);

        jPanel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        testLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        testLabel.setText("1.6543 = ((1,7s))");
        jPanel14.add(testLabel);

        pauseNotationPanel.add(jPanel14, java.awt.BorderLayout.CENTER);

        segmentationPanel.add(pauseNotationPanel, java.awt.BorderLayout.PAGE_END);

        tabbedPane.addTab("Segmentation", segmentationPanel);

        autoSavePanel.setLayout(new javax.swing.BoxLayout(autoSavePanel, javax.swing.BoxLayout.Y_AXIS));

        enableAutoSavePanel.setLayout(new javax.swing.BoxLayout(enableAutoSavePanel, javax.swing.BoxLayout.LINE_AXIS));

        enableAutoSaveCheckBox.setText("Enable auto save");
        enableAutoSavePanel.add(enableAutoSaveCheckBox);

        autoSavePanel.add(enableAutoSavePanel);

        autoSaveFilenamePanel.setLayout(new javax.swing.BoxLayout(autoSaveFilenamePanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel8.setText("Auto save filename: ");
        jLabel8.setMaximumSize(new java.awt.Dimension(117, 16));
        jLabel8.setPreferredSize(new java.awt.Dimension(117, 16));
        autoSaveFilenamePanel.add(jLabel8);

        autoSaveFilenameTextField.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        autoSaveFilenameTextField.setMinimumSize(new java.awt.Dimension(180, 20));
        autoSaveFilenameTextField.setPreferredSize(new java.awt.Dimension(180, 20));
        autoSaveFilenamePanel.add(autoSaveFilenameTextField);

        autoSavePanel.add(autoSaveFilenamePanel);

        autoSavePathPanel.setLayout(new javax.swing.BoxLayout(autoSavePathPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel9.setText("Auto save path: ");
        jLabel9.setMaximumSize(new java.awt.Dimension(117, 16));
        jLabel9.setMinimumSize(new java.awt.Dimension(117, 16));
        jLabel9.setPreferredSize(new java.awt.Dimension(117, 16));
        autoSavePathPanel.add(jLabel9);

        autoSavePathTextField.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        autoSavePathTextField.setMinimumSize(new java.awt.Dimension(180, 20));
        autoSavePathTextField.setPreferredSize(new java.awt.Dimension(180, 20));
        autoSavePathPanel.add(autoSavePathTextField);

        browseForAutoSavePathButton.setText("Browse...");
        browseForAutoSavePathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseForAutoSavePathButtonActionPerformed(evt);
            }
        });
        autoSavePathPanel.add(browseForAutoSavePathButton);

        autoSavePanel.add(autoSavePathPanel);

        autoSaveIntervalPanel.setLayout(new javax.swing.BoxLayout(autoSaveIntervalPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel10.setText("Auto save interval: ");
        jLabel10.setMaximumSize(new java.awt.Dimension(117, 16));
        jLabel10.setMinimumSize(new java.awt.Dimension(117, 16));
        jLabel10.setPreferredSize(new java.awt.Dimension(117, 16));
        autoSaveIntervalPanel.add(jLabel10);

        autoSaveIntervalSlider.setMajorTickSpacing(10);
        autoSaveIntervalSlider.setMaximum(61);
        autoSaveIntervalSlider.setMinimum(1);
        autoSaveIntervalSlider.setMinorTickSpacing(2);
        autoSaveIntervalSlider.setPaintLabels(true);
        autoSaveIntervalSlider.setPaintTicks(true);
        autoSaveIntervalSlider.setToolTipText("Set the value for the auto save interval - e.g.: a value of '10' means an automatic backup will be made every ten seconds");
        autoSaveIntervalSlider.setValue(45);
        autoSaveIntervalPanel.add(autoSaveIntervalSlider);

        jLabel11.setText("min");
        autoSaveIntervalPanel.add(jLabel11);

        autoSavePanel.add(autoSaveIntervalPanel);

        enableUndoPanel.setLayout(new javax.swing.BoxLayout(enableUndoPanel, javax.swing.BoxLayout.LINE_AXIS));

        enableUndoCheckBox.setSelected(true);
        enableUndoCheckBox.setText("Enable Undo");
        enableUndoPanel.add(enableUndoCheckBox);

        autoSavePanel.add(enableUndoPanel);

        tabbedPane.addTab("Auto save", null, autoSavePanel, "");

        languagePanel.setLayout(new javax.swing.BoxLayout(languagePanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel16.setText("Choose a language: ");
        languagePanel.add(jLabel16);

        languagesComboBox.setModel(Internationalizer.getLanguagesForComboBox());
        languagesComboBox.setAlignmentX(0.0F);
        languagesComboBox.setMaximumSize(new java.awt.Dimension(800, 27));
        languagesComboBox.setPreferredSize(new java.awt.Dimension(200, 27));
        languagePanel.add(languagesComboBox);

        jLabel17.setText(" ");
        languagePanel.add(jLabel17);

        jLabel18.setText("Choose your language, then restart the Partitur-Editor.");
        languagePanel.add(jLabel18);

        jLabel19.setText("Waehlen Sie Ihre Sprache, und starten Sie dann den Partitur-Editor neu.");
        languagePanel.add(jLabel19);

        jLabel20.setText("Choisissez votre langue, puis redémarrez le Partitur-Editor.");
        languagePanel.add(jLabel20);

        tabbedPane.addTab("Language", languagePanel);

        mediaPanel.setLayout(new java.awt.BorderLayout());

        playerSelectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Choose a media player"));
        playerSelectionPanel.setLayout(new javax.swing.BoxLayout(playerSelectionPanel, javax.swing.BoxLayout.Y_AXIS));

        mediaPlayersButtonGroup.add(basPlayerRadioButton);
        basPlayerRadioButton.setText("<html><b>BAS Audio Player:</b> A player provided by the Bavarian Archive for Speech Signals (BAS). Can only be used to play WAV audio files.</html>\n");
        playerSelectionPanel.add(basPlayerRadioButton);

        mediaPlayersButtonGroup.add(jdsPlayerRadioButton);
        jdsPlayerRadioButton.setText("<html><b>JDS Player:</b> A player provided by the Language Archive at the MPI Nijmegen, also used inside ELAN. Uses Window's native Direct Show framework to playback audio and video files. Recommended for video files other than MPEG-4.</html>");
        playerSelectionPanel.add(jdsPlayerRadioButton);

        mediaPlayersButtonGroup.add(mmfPlayerRadioButton);
        mmfPlayerRadioButton.setText("<html><b>MMF Player:</b> A player provided by the Language Archive at the MPI Nijmegen, also used inside ELAN. Uses Window's native Microsoft Media Foundation framework to playback audio and video files. <i>Not working yet!</i> </html>");
        playerSelectionPanel.add(mmfPlayerRadioButton);

        mediaPlayersButtonGroup.add(elanDSPlayerRadioButton);
        elanDSPlayerRadioButton.setForeground(new java.awt.Color(64, 64, 64));
        elanDSPlayerRadioButton.setText("<html><b>ELAN DS Player:</b> A player provided by the language Archive at the MPI Nijmegen, also used in older versions of ELAN. Uses Window's native Direct Show framework to playback audio and video files. Not used anymore. </html>");
        elanDSPlayerRadioButton.setEnabled(false);
        playerSelectionPanel.add(elanDSPlayerRadioButton);

        mediaPlayersButtonGroup.add(cocoaQuicktimePlayerRadioButton);
        cocoaQuicktimePlayerRadioButton.setText("<html><b>Cocoa Quicktime Player:</b> A player provided by the Language Archive at the MPI Nijmegen, also used inside ELAN. Uses the Quicktime framework to playback audio and video files. Recommended for video files on the MAC.</html>");
        playerSelectionPanel.add(cocoaQuicktimePlayerRadioButton);

        mediaPlayersButtonGroup.add(elanQuicktimeRadioButton);
        elanQuicktimeRadioButton.setText("<html><b>ELAN Quicktime Player:</b> A player provided by the Language Archive at the MPI Nijmegen, also used inside ELAN. Uses the Quicktime framework to playback audio and video files. Requires Quicktime for Java.</html>");
        playerSelectionPanel.add(elanQuicktimeRadioButton);

        mediaPlayersButtonGroup.add(jmfPlayerRadioButton);
        jmfPlayerRadioButton.setText("<html><b>JMF Player:</b> Uses the Java Media framework to playback audio and video files. Recommended for video files on Linuxes.</html>");
        playerSelectionPanel.add(jmfPlayerRadioButton);

        mediaPanel.add(playerSelectionPanel, java.awt.BorderLayout.CENTER);

        otherOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Additional Options"));
        otherOptionsPanel.setLayout(new javax.swing.BoxLayout(otherOptionsPanel, javax.swing.BoxLayout.Y_AXIS));

        autoAnchorCheckBox.setSelected(true);
        autoAnchorCheckBox.setText("Auto anchor transcription to media");
        otherOptionsPanel.add(autoAnchorCheckBox);

        autoRemoveTLICheckBox.setText("Auto remove unused timeline items after merge");
        otherOptionsPanel.add(autoRemoveTLICheckBox);

        autoInterpolateCheckBox.setSelected(true);
        autoInterpolateCheckBox.setText("Auto interpolate when splitting");
        otherOptionsPanel.add(autoInterpolateCheckBox);

        mediaPanel.add(otherOptionsPanel, java.awt.BorderLayout.SOUTH);

        topPanel.setLayout(new java.awt.BorderLayout());
        mediaPanel.add(topPanel, java.awt.BorderLayout.NORTH);

        tabbedPane.addTab("Media", mediaPanel);

        pathsPanel.setLayout(new javax.swing.BoxLayout(pathsPanel, javax.swing.BoxLayout.Y_AXIS));

        logPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Log file directory"));

        logDirectoryLabel.setForeground(new java.awt.Color(0, 0, 153));
        logDirectoryLabel.setText("<Not set> ");
        logPanel.add(logDirectoryLabel);

        setLogDirectoryButton.setAction(new org.exmaralda.common.application.ChangeLoggingDirectoryAction("Change logging directory...", application));
        setLogDirectoryButton.setText("Change log file directory...");
        logPanel.add(setLogDirectoryButton);

        pathsPanel.add(logPanel);

        praatPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Praat directory"));

        praatDirectoryLabel.setForeground(new java.awt.Color(0, 0, 153));
        praatDirectoryLabel.setText("<Not set> ");
        praatPanel.add(praatDirectoryLabel);

        changePraatDirectoryButton.setText("Change Praat directory...");
        changePraatDirectoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePraatDirectoryButtonActionPerformed(evt);
            }
        });
        praatPanel.add(changePraatDirectoryButton);

        pathsPanel.add(praatPanel);

        tabbedPane.addTab("Paths", pathsPanel);

        menusPanel.setLayout(new javax.swing.BoxLayout(menusPanel, javax.swing.BoxLayout.Y_AXIS));

        sfb538MenuCheckBox.setText("SFB 538/632 Menu");
        menusPanel.add(sfb538MenuCheckBox);

        sinMenuCheckBox.setText("SiN Menu");
        menusPanel.add(sinMenuCheckBox);

        odtstdMenuCheckBox.setText("ODT/STD Menu");
        menusPanel.add(odtstdMenuCheckBox);

        tabbedPane.addTab("Menus", menusPanel);

        mainPanel.add(tabbedPane, java.awt.BorderLayout.CENTER);

        jPanel16.setLayout(new java.awt.BorderLayout());

        resetButton.setBackground(new java.awt.Color(204, 0, 51));
        resetButton.setText("Reset...");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        jPanel16.add(resetButton, java.awt.BorderLayout.SOUTH);

        mainPanel.add(jPanel16, java.awt.BorderLayout.NORTH);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changeCHATButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeCHATButtonActionPerformed
        AbstractXMLOpenDialog dialog = new AbstractXMLOpenDialog(chatTextField.getText());
        dialog.setFileFilter(new ParameterFileFilter("xml", "Finite State Machine (*.xml"));
        if (dialog.showDialog(this,"Choose")==javax.swing.JFileChooser.APPROVE_OPTION){
            chatTextField.setText(dialog.getSelectedFile().getPath());
        }                                
        // TODO add your handling code here:
    }//GEN-LAST:event_changeCHATButtonActionPerformed

    private void changeGATButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeGATButtonActionPerformed
        AbstractXMLOpenDialog dialog = new AbstractXMLOpenDialog(gatTextField.getText());
        dialog.setFileFilter(new ParameterFileFilter("xml", "Finite State Machine (*.xml)"));
        if (dialog.showDialog(this,"Choose")==javax.swing.JFileChooser.APPROVE_OPTION){
            gatTextField.setText(dialog.getSelectedFile().getPath());
        }                                
        // TODO add your handling code here:
    }//GEN-LAST:event_changeGATButtonActionPerformed

    private void changeHIATButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeHIATButtonActionPerformed
        // TODO add your handling code here:
        AbstractXMLOpenDialog dialog = new AbstractXMLOpenDialog(hiatTextField.getText());
        dialog.setFileFilter(new ParameterFileFilter("xml", "Finite State Machine (*.xml"));
        if (dialog.showDialog(this,"Choose")==javax.swing.JFileChooser.APPROVE_OPTION){
            hiatTextField.setText(dialog.getSelectedFile().getPath());
        }                                
    }//GEN-LAST:event_changeHIATButtonActionPerformed

    private void browseForAutoSavePathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseForAutoSavePathButtonActionPerformed
        // Add your handling code here:
        javax.swing.JFileChooser dialog = new javax.swing.JFileChooser();
        dialog.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        dialog.setDialogTitle("Choose a directory");
        if (dialog.showOpenDialog(this)==javax.swing.JFileChooser.APPROVE_OPTION){
            autoSavePathTextField.setText(dialog.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_browseForAutoSavePathButtonActionPerformed

    private void changeHIATUtteranceList2HTMLButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeHIATUtteranceList2HTMLButtonActionPerformed
        // Add your handling code here:
        ChooseStylesheetDialog dialog = new ChooseStylesheetDialog(HIATUtteranceList2HTMLTextField.getText());
        if (dialog.showOpenDialog(this)==javax.swing.JFileChooser.APPROVE_OPTION){
            HIATUtteranceList2HTMLTextField.setText(dialog.getSelectedFile().getPath());
        }                        
    }//GEN-LAST:event_changeHIATUtteranceList2HTMLButtonActionPerformed

    private void changeFreeStylesheetVisualisationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeFreeStylesheetVisualisationButtonActionPerformed
        // Add your handling code here:
        ChooseStylesheetDialog dialog = new ChooseStylesheetDialog(freeStylesheetVisualisationTextField.getText());
        if (dialog.showOpenDialog(this)==javax.swing.JFileChooser.APPROVE_OPTION){
            freeStylesheetVisualisationTextField.setText(dialog.getSelectedFile().getPath());
        }                
        
    }//GEN-LAST:event_changeFreeStylesheetVisualisationButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // Add your handling code here:
        changed = false;
        setVisible(false);
        dispose();        
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // Add your handling code here:
        changed = true;
        setVisible(false);
        dispose();        
    }//GEN-LAST:event_okButtonActionPerformed

    private void changeTranscription2FormattableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeTranscription2FormattableButtonActionPerformed
        // Add your handling code here:
        ChooseStylesheetDialog dialog = new ChooseStylesheetDialog(transcription2FormattableTextField.getText());
        if (dialog.showOpenDialog(this)==javax.swing.JFileChooser.APPROVE_OPTION){
            transcription2FormattableTextField.setText(dialog.getSelectedFile().getPath());
        }                
    }//GEN-LAST:event_changeTranscription2FormattableButtonActionPerformed

    private void changeSpeakertable2TranscriptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeSpeakertable2TranscriptionButtonActionPerformed
        // Add your handling code here:
        ChooseStylesheetDialog dialog = new ChooseStylesheetDialog(speakertable2TranscriptionTextField.getText());
        if (dialog.showOpenDialog(this)==javax.swing.JFileChooser.APPROVE_OPTION){
            speakertable2TranscriptionTextField.setText(dialog.getSelectedFile().getPath());
        }        
    }//GEN-LAST:event_changeSpeakertable2TranscriptionButtonActionPerformed

    private void changeHead2HTMLButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeHead2HTMLButtonActionPerformed
        // Add your handling code here:
        ChooseStylesheetDialog dialog = new ChooseStylesheetDialog(head2HTMLTextField.getText());
        if (dialog.showOpenDialog(this)==javax.swing.JFileChooser.APPROVE_OPTION){
            head2HTMLTextField.setText(dialog.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_changeHead2HTMLButtonActionPerformed

    private void changeGeneralPurposeFontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeGeneralPurposeFontButtonActionPerformed
        // Add your handling code here:
        ChooseFontDialog dialog = new ChooseFontDialog(new javax.swing.JFrame(), true, generalPurposeFontLabel.getText());
        dialog.setTitle("Set the general purpose font");
        dialog.show();
        generalPurposeFontLabel.setText(dialog.getFontname());
        generalPurposeFontLabel.setFont(new java.awt.Font(dialog.getFontname(), java.awt.Font.PLAIN, 12));                
    }//GEN-LAST:event_changeGeneralPurposeFontButtonActionPerformed

    private void changeTierFontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeTierFontButtonActionPerformed
        // Add your handling code here:
        ChooseFontDialog dialog = new ChooseFontDialog(new javax.swing.JFrame(), true, tierFontLabel.getText());
        dialog.setTitle("Set the default tier font");
        dialog.show();
        tierFontLabel.setText(dialog.getFontname());
        tierFontLabel.setFont(new java.awt.Font(dialog.getFontname(), java.awt.Font.PLAIN, 12));                        
    }//GEN-LAST:event_changeTierFontButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        changed = false;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void changePraatDirectoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePraatDirectoryButtonActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Choose the directory with the Praat and Sendpraat binaries");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retValue = jfc.showOpenDialog(this);
        if (retValue==JFileChooser.APPROVE_OPTION){
            Preferences settings = java.util.prefs.Preferences.userRoot().node(application.getPreferencesNode());
            String newPD = jfc.getSelectedFile().getAbsolutePath();
            settings.put("PRAAT-Directory", newPD);
            praatDirectoryLabel.setText(newPD);
            JOptionPane.showMessageDialog(this, "Praat directory set to " + newPD + ".\nChanges will take effect after restart.");
        }
    }//GEN-LAST:event_changePraatDirectoryButtonActionPerformed

    private void pausePrefixTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pausePrefixTextFieldActionPerformed
        updatePauseExample();
    }//GEN-LAST:event_pausePrefixTextFieldActionPerformed

    private void pauseSuffixTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseSuffixTextFieldActionPerformed
        updatePauseExample();
    }//GEN-LAST:event_pauseSuffixTextFieldActionPerformed

    private void decimalPointRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decimalPointRadioButtonActionPerformed
        updatePauseExample();
    }//GEN-LAST:event_decimalPointRadioButtonActionPerformed

    private void decimalCommaRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decimalCommaRadioButtonActionPerformed
        updatePauseExample();
    }//GEN-LAST:event_decimalCommaRadioButtonActionPerformed

    private void preferredSegmentationComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferredSegmentationComboBoxActionPerformed
        String seg = (String)(preferredSegmentationComboBox.getSelectedItem());
        //GENERIC, HIAT, DIDA, GAT, cGAT_MINIMAL, CHAT, IPA
        if (seg.equals("HIAT")){
            pausePrefixTextField.setText("((");
            pauseSuffixTextField.setText("s))");
            digitsSpinner.setValue(new Integer(1));
            decimalCommaRadioButton.setSelected(true);
        } else if (seg.equals("DIDA")){
            pausePrefixTextField.setText("*");
            pauseSuffixTextField.setText("*");
            digitsSpinner.setValue(new Integer(1));
            decimalCommaRadioButton.setSelected(true);
        } else if ((seg.equals("GAT")) || (seg.equals("cGAT_MINIMAL"))){
            pausePrefixTextField.setText("(");
            pauseSuffixTextField.setText(")");
            digitsSpinner.setValue(new Integer(1));
            decimalPointRadioButton.setSelected(true);
        } else if ((seg.equals("CHAT")) || (seg.equals("CHAT_MINIMAL"))){
            pausePrefixTextField.setText("(");
            pauseSuffixTextField.setText(")");
            digitsSpinner.setValue(new Integer(2));
            decimalPointRadioButton.setSelected(true);
        }

        updatePauseExample();
    }//GEN-LAST:event_preferredSegmentationComboBoxActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        int ret = JOptionPane.showConfirmDialog(rootPane, "This will reset all preferences to their initial values.\n"
                + "Changes will take full effect after a restart of the editor.\n"
                + "Are you sure?", "Reset preferences", JOptionPane.YES_NO_OPTION);
        if (ret==JOptionPane.YES_OPTION){
            changed=false;
            reset=true; 
            setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_resetButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //new EditPreferencesDialog(new javax.swing.JFrame(), true).show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField HIATUtteranceList2HTMLTextField;
    private javax.swing.JCheckBox autoAnchorCheckBox;
    private javax.swing.JCheckBox autoInterpolateCheckBox;
    private javax.swing.JCheckBox autoRemoveTLICheckBox;
    private javax.swing.JPanel autoSaveFilenamePanel;
    private javax.swing.JTextField autoSaveFilenameTextField;
    private javax.swing.JPanel autoSaveIntervalPanel;
    private javax.swing.JSlider autoSaveIntervalSlider;
    private javax.swing.JPanel autoSavePanel;
    private javax.swing.JPanel autoSavePathPanel;
    private javax.swing.JTextField autoSavePathTextField;
    private javax.swing.JRadioButton basPlayerRadioButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton browseForAutoSavePathButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton changeCHATButton;
    private javax.swing.JButton changeFreeStylesheetVisualisationButton;
    private javax.swing.JButton changeGATButton;
    private javax.swing.JButton changeGeneralPurposeFontButton;
    private javax.swing.JButton changeHIATButton;
    private javax.swing.JButton changeHIATUtteranceList2HTMLButton;
    private javax.swing.JButton changeHead2HTMLButton;
    private javax.swing.JButton changePraatDirectoryButton;
    private javax.swing.JButton changeSpeakertable2TranscriptionButton;
    private javax.swing.JButton changeTierFontButton;
    private javax.swing.JButton changeTranscription2FormattableButton;
    private javax.swing.JTextField chatTextField;
    private javax.swing.JRadioButton cocoaQuicktimePlayerRadioButton;
    private javax.swing.JRadioButton decimalCommaRadioButton;
    private javax.swing.JRadioButton decimalPointRadioButton;
    private javax.swing.JSpinner digitsSpinner;
    private javax.swing.JRadioButton elanDSPlayerRadioButton;
    private javax.swing.JRadioButton elanQuicktimeRadioButton;
    private javax.swing.JCheckBox enableAutoSaveCheckBox;
    private javax.swing.JPanel enableAutoSavePanel;
    private javax.swing.JCheckBox enableUndoCheckBox;
    private javax.swing.JPanel enableUndoPanel;
    private javax.swing.JPanel fontsPanel;
    private javax.swing.JTextField freeStylesheetVisualisationTextField;
    private javax.swing.JPanel fsmPanel;
    private javax.swing.JTextField gatTextField;
    private javax.swing.JLabel generalPurposeFontLabel;
    private javax.swing.JPanel generalPurposeFontPanel;
    private javax.swing.JTextField head2HTMLTextField;
    private javax.swing.JTextField hiatTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jdsPlayerRadioButton;
    private javax.swing.JRadioButton jmfPlayerRadioButton;
    private javax.swing.JPanel languagePanel;
    private javax.swing.JComboBox languagesComboBox;
    private javax.swing.JLabel logDirectoryLabel;
    private javax.swing.JPanel logPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mediaPanel;
    private javax.swing.ButtonGroup mediaPlayersButtonGroup;
    private javax.swing.JPanel menusPanel;
    private javax.swing.JRadioButton mmfPlayerRadioButton;
    private javax.swing.JCheckBox odtstdMenuCheckBox;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okCancelPanel;
    private javax.swing.JPanel otherOptionsPanel;
    private javax.swing.JPanel pathsPanel;
    private javax.swing.ButtonGroup pauseNotationButtonGroup;
    private javax.swing.JPanel pauseNotationPanel;
    private javax.swing.JTextField pausePrefixTextField;
    private javax.swing.JTextField pauseSuffixTextField;
    private javax.swing.JPanel playerSelectionPanel;
    private javax.swing.JLabel praatDirectoryLabel;
    private javax.swing.JPanel praatPanel;
    private javax.swing.JComboBox preferredSegmentationComboBox;
    private javax.swing.JPanel preferredSegmentationPanel;
    private javax.swing.JButton resetButton;
    private javax.swing.JPanel segmentationPanel;
    private javax.swing.JButton setLogDirectoryButton;
    private javax.swing.JCheckBox sfb538MenuCheckBox;
    private javax.swing.JCheckBox sinMenuCheckBox;
    private javax.swing.JTextField speakertable2TranscriptionTextField;
    private javax.swing.JPanel stylesheetsPanel;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JLabel testLabel;
    private javax.swing.JLabel tierFontLabel;
    private javax.swing.JPanel tierFontPanel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField transcription2FormattableTextField;
    private javax.swing.ButtonGroup underlineButtonGroup;
    private javax.swing.JTextField underlineCategoryTextField;
    private javax.swing.JRadioButton underlineCharRadioButton;
    private javax.swing.JPanel underlineMethodPanel;
    private javax.swing.JRadioButton underlineTierRadioButton;
    // End of variables declaration//GEN-END:variables
    
    public void show(){
        java.awt.Dimension dialogSize = this.getPreferredSize();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width/2 - dialogSize.width/2, screenSize.height/2 - dialogSize.height/2);
        super.show();
    }

    void updatePauseExample(){
        String example = "1.6543 = "
                + org.exmaralda.partitureditor.partiture.StringUtilities.makePause(1.6543,
                    pausePrefixTextField.getText(),
                    pauseSuffixTextField.getText(),
                    ((Integer)digitsSpinner.getValue()).intValue(),
                    decimalCommaRadioButton.isSelected()
                    );
        testLabel.setText(example);
    }
    
    public boolean editPreferences(String[] values){
        tierFontLabel.setText(values[0]);
        tierFontLabel.setFont(new java.awt.Font(values[0], java.awt.Font.PLAIN, 12));
        generalPurposeFontLabel.setText(values[1]);
        generalPurposeFontLabel.setFont(new java.awt.Font(values[1], java.awt.Font.PLAIN, 12));
        head2HTMLTextField.setText(values[2]);
        speakertable2TranscriptionTextField.setText(values[3]);
        transcription2FormattableTextField.setText(values[4]);
        freeStylesheetVisualisationTextField.setText(values[5]);
        HIATUtteranceList2HTMLTextField.setText(values[6]);
        enableAutoSaveCheckBox.setSelected(new Boolean(values[7]).booleanValue());
        autoSaveFilenameTextField.setText(values[8]);
        autoSavePathTextField.setText(values[9]);
        try {
            autoSaveIntervalSlider.setValue(Integer.parseInt(values[10]));
        } catch (NumberFormatException nfe){
            autoSaveIntervalSlider.setValue(45);
        }
        hiatTextField.setText(values[11]);
        //didaTextField.setText(values[12]);
        gatTextField.setText(values[13]);
        chatTextField.setText(values[14]);
        languagesComboBox.setSelectedIndex(Internationalizer.getIndexForLanguage(values[15]));
        //mediaPlayerComboBox.setSelectedItem(values[16]);
        
        String mp = values[16];
        elanDSPlayerRadioButton.setSelected("DirectShow-Player".equals(mp));
        jdsPlayerRadioButton.setSelected("JDS-Player".equals(mp));
        mmfPlayerRadioButton.setSelected("MMF-Player".equals(mp));
        elanQuicktimeRadioButton.setSelected("ELAN-Quicktime-Player".equals(mp));
        cocoaQuicktimePlayerRadioButton.setSelected("CocoaQT-Player".equals(mp));
        jmfPlayerRadioButton.setSelected("JMF-Player".equals(mp));
        basPlayerRadioButton.setSelected("BAS-Audio-Player".equals(mp));
        

        underlineCharRadioButton.setSelected(new Boolean(values[17]).booleanValue());
        underlineTierRadioButton.setSelected(!(new Boolean(values[17]).booleanValue()));
        underlineCategoryTextField.setText(values[18]);

        preferredSegmentationComboBox.setSelectedItem(values[19]);

        sfb538MenuCheckBox.setSelected(new Boolean(values[20]).booleanValue());
        sinMenuCheckBox.setSelected(new Boolean(values[21]).booleanValue());
        odtstdMenuCheckBox.setSelected(new Boolean(values[22]).booleanValue());
        autoAnchorCheckBox.setSelected(new Boolean(values[23]).booleanValue());
        autoRemoveTLICheckBox.setSelected(new Boolean(values[24]).booleanValue());

        // pause notation
        pausePrefixTextField.setText(values[25]);
        pauseSuffixTextField.setText(values[26]);
        digitsSpinner.setValue(new Integer(Integer.parseInt(values[27])));
        decimalCommaRadioButton.setSelected(new Boolean(values[28]).booleanValue());
        enableAutoSaveCheckBox.setSelected(new Boolean(values[29]).booleanValue());
        autoInterpolateCheckBox.setSelected(new Boolean(values[30]).booleanValue());

        show();
        return changed;
    }

    public void changeToSegmentationTab() {
        tabbedPane.setSelectedIndex(2);
    }

    private String getSelectedMediaPlayer() {
        if (basPlayerRadioButton.isSelected()){
            return "BAS-Audio-Player";
        }
        if (jdsPlayerRadioButton.isSelected()){
            return "JDS-Player";
        }
        if (mmfPlayerRadioButton.isSelected()){
            return "MMF-Player";
        }
        if (elanDSPlayerRadioButton.isSelected()){
            return "DirectShow-Player";
        }
        if (elanQuicktimeRadioButton.isSelected()){
            return "ELAN-Quicktime-Player";
        }
        if (cocoaQuicktimePlayerRadioButton.isSelected()){
            return "CocoaQT-Player";
        }
        return "JMF-Player";
    }
    
}
