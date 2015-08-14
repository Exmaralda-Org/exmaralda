/*
 * EditPreferencesPanel.java
 *
 * Created on 20. Juni 2008, 16:10
 */

package org.exmaralda.folker.gui;

import java.awt.Font;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;

/**
 *
 * @author  thomas
 */
public class EditPreferencesPanel extends javax.swing.JPanel {
    
    //****************
    
    org.exmaralda.common.ExmaraldaApplication application;

    String[] FONTS;
    String TEST_STRING = "\u00B0 \u0294 \u007C \u2013 \u2191 \u2193 \u0060 \u00B4 \u00AF \u02C6 \u02C7";
    String[] LANGUAGE_CODES = {"default", "ger", "GER", "eng", "fre", "por", "hun", "tur", "dan", "spa", "ita", "cze", "rus_tran", "universal"};


    /** Creates new form EditPreferencesPanel */
    public EditPreferencesPanel(org.exmaralda.common.ExmaraldaApplication app) {
        initComponents();
        useControlCheckBoxPanel.add(javax.swing.Box.createHorizontalGlue());

        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        FONTS = ge.getAvailableFontFamilyNames();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        boolean arialIsThere = false;
        boolean timesIsThere = false;
        for (String fontname : FONTS){
            model.addElement(fontname);
            System.out.println(fontname);
            if (fontname.equals("Arial")){ arialIsThere = true;}
            if (fontname.equals("Times New Roman")){ timesIsThere = true;}
        }
        fontsComboBox.setModel(model);
        if (arialIsThere){
            fontsComboBox.setSelectedItem("Arial");
        } else if (timesIsThere){
            fontsComboBox.setSelectedItem("Times New Roman");            
        }
        fontTestTextField.setText(TEST_STRING);


        application = app;
        initValues();
    }
    
    public void initValues(){
        Preferences prefs = Preferences.userRoot().node(application.getPreferencesNode());
        String playerType = prefs.get("PlayerType", "JMF-Player");
        jmfRadioButton.setSelected(playerType.equals("JMF-Player"));
        elanQuicktimeRadioButton.setSelected(playerType.equals("ELAN-Quicktime-Player"));
        quicktimeRadioButton.setSelected(playerType.equals("Quicktime-Player"));
        directShowRadioButton.setSelected(playerType.equals("DirectShow-Player"));
        basRadioButton.setSelected(playerType.equals("BAS-Audio-Player"));

        String os = System.getProperty("os.name").toLowerCase();
        directShowRadioButton.setVisible(os.toLowerCase().startsWith("win"));
        elanQuicktimeRadioButton.setVisible(os.toLowerCase().startsWith("mac"));

        String parseLevel = prefs.get("parse-level", "2");
        System.out.println("Parse level is " + parseLevel);
        level0RadioButton.setSelected(parseLevel.equals("0"));
        level1RadioButton.setSelected(parseLevel.equals("1"));
        level2RadioButton.setSelected(parseLevel.equals("2"));
        level3RadioButton.setSelected(parseLevel.equals("3"));
        
        String alphabetLanguage = prefs.get("alphabet-language", "default");
        int index = 0;        
        for (int i=0; i<LANGUAGE_CODES.length; i++){
            if (LANGUAGE_CODES[i].equals(alphabetLanguage)){
                index = i;
                break;
            }
        }
        alphabetComboBox.setSelectedIndex(index);

        normalizeWhiteSpaceCheckBox.setSelected(prefs.getBoolean("normalize-whitespace", true));
        recalculatePausesCheckBox.setSelected(prefs.getBoolean("update-pauses", true));

        normalizeWhiteSpaceCheckBox.setEnabled(parseLevel.equals("2") || parseLevel.equals("3"));
        recalculatePausesCheckBox.setEnabled(parseLevel.equals("2") || parseLevel.equals("3"));

        useControlCheckBox.setSelected(prefs.getBoolean("use-control", false));

        loopTimeSlider.setValue(prefs.getInt("loop-time", 500));

        String fontName = prefs.get("font", "");
        if (fontName.length()>0){
            fontsComboBox.setSelectedItem(fontName);
            fontsComboBoxActionPerformed(null);
            setFontCheckBox.setSelected(true);
            fontsComboBox.setEnabled(true);
        } else {
            setFontCheckBox.setSelected(false);
            fontsComboBox.setEnabled(false);
        }

        String language = prefs.get("language", "de");
        germanRadioButton.setSelected(language.equals("de"));
        englishRadioButton.setSelected(language.equals("en"));
        frenchRadioButton.setSelected(language.equals("fr"));
        
        boolean logging = prefs.getBoolean("logging-enabled", false);
        enableLoggingCheckBox.setSelected(logging);
        
        String defaultAudio = prefs.get("default-audio-path", "");
        defaultAudioTextField.setText(defaultAudio);
    }
    
    public void commitValues(){
        Preferences prefs = Preferences.userRoot().node(application.getPreferencesNode());
        String playerType = "JMF-Player";
        if (quicktimeRadioButton.isSelected()) playerType = "Quicktime-Player"; 
        if (directShowRadioButton.isSelected()) playerType = "DirectShow-Player"; 
        if (elanQuicktimeRadioButton.isSelected()) playerType = "ELAN-Quicktime-Player";
        if (basRadioButton.isSelected()) playerType = "BAS-Audio-Player";
        if (jdsRadioButton.isSelected()) playerType = "JDS-Player";
        
        prefs.put("PlayerType", playerType);

        String parseLevel = "2";
        if (level0RadioButton.isSelected()) parseLevel="0";
        else if (level1RadioButton.isSelected()) parseLevel="1";
        else if (level3RadioButton.isSelected()) parseLevel="3";
        prefs.put("parse-level", parseLevel);
        
        prefs.put("alphabet-language", LANGUAGE_CODES[alphabetComboBox.getSelectedIndex()]);

        prefs.put("normalize-whitespace", Boolean.toString(normalizeWhiteSpaceCheckBox.isSelected()));
        prefs.put("update-pauses", Boolean.toString(recalculatePausesCheckBox.isSelected()));
        prefs.put("use-control", Boolean.toString(useControlCheckBox.isSelected()));
        prefs.put("loop-time", Integer.toString(loopTimeSlider.getValue()));

        if (setFontCheckBox.isSelected()){
            prefs.put("font", (String)(fontsComboBox.getSelectedItem()));
        } else {
            prefs.put("font", "");
        }

        if (germanRadioButton.isSelected()) prefs.put("language", "de");
        if (englishRadioButton.isSelected()) prefs.put("language", "en");
        if (frenchRadioButton.isSelected()) prefs.put("language", "fr");
        
        prefs.put("logging-enabled", Boolean.toString(enableLoggingCheckBox.isSelected()));
        
        prefs.put("default-audio-path", defaultAudioTextField.getText());

    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playerButtonGroup = new javax.swing.ButtonGroup();
        parseLevelButtonGroup = new javax.swing.ButtonGroup();
        languagesButtonGroup = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        parsePanel = new javax.swing.JPanel();
        parseOptionsPanel = new javax.swing.JPanel();
        normalizeWhiteSpaceCheckBox = new javax.swing.JCheckBox();
        recalculatePausesCheckBox = new javax.swing.JCheckBox();
        middlePanel = new javax.swing.JPanel();
        parseLevelPanel = new javax.swing.JPanel();
        level0RadioButton = new javax.swing.JRadioButton();
        level1RadioButton = new javax.swing.JRadioButton();
        level2RadioButton = new javax.swing.JRadioButton();
        level3RadioButton = new javax.swing.JRadioButton();
        alphabetPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        alphabetComboBox = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        playerOptionsPanel = new javax.swing.JPanel();
        playerPanel = new javax.swing.JPanel();
        basRadioButton = new javax.swing.JRadioButton();
        jdsRadioButton = new javax.swing.JRadioButton();
        directShowRadioButton = new javax.swing.JRadioButton();
        elanQuicktimeRadioButton = new javax.swing.JRadioButton();
        quicktimeRadioButton = new javax.swing.JRadioButton();
        jmfRadioButton = new javax.swing.JRadioButton();
        restartHintPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        restartHintTextArea = new javax.swing.JTextArea();
        optionsPanel = new javax.swing.JPanel();
        useControlCheckBoxPanel = new javax.swing.JPanel();
        useControlCheckBox = new javax.swing.JCheckBox();
        loopTimePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        loopTimeSlider = new javax.swing.JSlider();
        defaultAudioPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        defaultAudioTextField = new javax.swing.JTextField();
        defaultAudioBrowseButton = new javax.swing.JButton();
        fontOptionsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        setFontCheckBox = new javax.swing.JCheckBox();
        fontsComboBox = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        fontTestTextField = new javax.swing.JTextField();
        languageOptionsPanel = new javax.swing.JPanel();
        languagePanel = new javax.swing.JPanel();
        germanRadioButton = new javax.swing.JRadioButton();
        englishRadioButton = new javax.swing.JRadioButton();
        frenchRadioButton = new javax.swing.JRadioButton();
        restartHintPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        restartHintTextArea1 = new javax.swing.JTextArea();
        advancedOptionsPanel = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        enableLoggingCheckBox = new javax.swing.JCheckBox();
        okPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        parsePanel.setLayout(new java.awt.BorderLayout());

        parseOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(FOLKERInternationalizer.getString("dialog.preferences.options")));
        parseOptionsPanel.setLayout(new javax.swing.BoxLayout(parseOptionsPanel, javax.swing.BoxLayout.Y_AXIS));

        normalizeWhiteSpaceCheckBox.setSelected(true);
        normalizeWhiteSpaceCheckBox.setText(FOLKERInternationalizer.getString("dialog.preferences.whitespace"));
        normalizeWhiteSpaceCheckBox.setToolTipText(FOLKERInternationalizer.getString("dialog.preferences.whitespace.tooltip"));
        parseOptionsPanel.add(normalizeWhiteSpaceCheckBox);

        recalculatePausesCheckBox.setSelected(true);
        recalculatePausesCheckBox.setText(FOLKERInternationalizer.getString("dialog.preferences.pauses"));
        recalculatePausesCheckBox.setToolTipText(FOLKERInternationalizer.getString("dialog.preferences.pauses.tooltip"));
        parseOptionsPanel.add(recalculatePausesCheckBox);

        parsePanel.add(parseOptionsPanel, java.awt.BorderLayout.SOUTH);

        middlePanel.setLayout(new java.awt.BorderLayout());

        parseLevelPanel.setLayout(new javax.swing.BoxLayout(parseLevelPanel, javax.swing.BoxLayout.Y_AXIS));

        parseLevelButtonGroup.add(level0RadioButton);
        level0RadioButton.setText(FOLKERInternationalizer.getString("dialog.preferences.level0"));
        level0RadioButton.setToolTipText(FOLKERInternationalizer.getString("dialog.preferences.level0.tooltip"));
        level0RadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                level0RadioButtonActionPerformed(evt);
            }
        });
        parseLevelPanel.add(level0RadioButton);

        parseLevelButtonGroup.add(level1RadioButton);
        level1RadioButton.setText(FOLKERInternationalizer.getString("dialog.preferences.level1"));
        level1RadioButton.setToolTipText(FOLKERInternationalizer.getString("dialog.preferences.level1.tooltip"));
        level1RadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                level1RadioButtonActionPerformed(evt);
            }
        });
        parseLevelPanel.add(level1RadioButton);

        parseLevelButtonGroup.add(level2RadioButton);
        level2RadioButton.setText(FOLKERInternationalizer.getString("dialog.preferences.level2"));
        level2RadioButton.setToolTipText(FOLKERInternationalizer.getString("dialog.preferences.level2.tooltip"));
        level2RadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                level2RadioButtonActionPerformed(evt);
            }
        });
        parseLevelPanel.add(level2RadioButton);

        parseLevelButtonGroup.add(level3RadioButton);
        level3RadioButton.setText(FOLKERInternationalizer.getString("dialog.preferences.level3"));
        level3RadioButton.setToolTipText(FOLKERInternationalizer.getString("dialog.preferences.level3.tooltip"));
        level3RadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                level3RadioButtonActionPerformed(evt);
            }
        });
        parseLevelPanel.add(level3RadioButton);

        middlePanel.add(parseLevelPanel, java.awt.BorderLayout.CENTER);

        alphabetPanel.setLayout(new javax.swing.BoxLayout(alphabetPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setText("Alphabet: ");
        jPanel3.add(jLabel2);

        alphabetComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "German (Deutsch)", "German (Deutsch) + Capitals", "English", "French (Français)", "Portuguese (Portugu?s)", "Hungarian (Magyar)", "Turkish (Türkçe)", "Danish (Dansk)", "Spanish (Espagnol)", "Italian (Italiano)", "Czech (èeština)", "Russian (Latin transliteration)", "Universal" }));
        alphabetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alphabetComboBoxActionPerformed(evt);
            }
        });
        jPanel3.add(alphabetComboBox);

        alphabetPanel.add(jPanel3);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setForeground(new java.awt.Color(255, 51, 51));
        jLabel3.setText(        FOLKERInternationalizer.getString("dialog.preferences.alphabethint"));
        jPanel4.add(jLabel3);

        alphabetPanel.add(jPanel4);

        middlePanel.add(alphabetPanel, java.awt.BorderLayout.SOUTH);

        parsePanel.add(middlePanel, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(FOLKERInternationalizer.getString("dialog.preferences.transcriptlevel"), parsePanel);

        playerOptionsPanel.setLayout(new java.awt.BorderLayout());

        playerPanel.setLayout(new javax.swing.BoxLayout(playerPanel, javax.swing.BoxLayout.Y_AXIS));

        playerButtonGroup.add(basRadioButton);
        basRadioButton.setText("BAS Audio Player");
        playerPanel.add(basRadioButton);

        playerButtonGroup.add(jdsRadioButton);
        jdsRadioButton.setText("JDS Player");
        playerPanel.add(jdsRadioButton);

        playerButtonGroup.add(directShowRadioButton);
        directShowRadioButton.setForeground(new java.awt.Color(128, 128, 128));
        directShowRadioButton.setText(FOLKERInternationalizer.getString("dialog.preferences.directshow"));
        playerPanel.add(directShowRadioButton);

        playerButtonGroup.add(elanQuicktimeRadioButton);
        elanQuicktimeRadioButton.setText(FOLKERInternationalizer.getString("dialog.preferences.quicktime"));
        playerPanel.add(elanQuicktimeRadioButton);

        playerButtonGroup.add(quicktimeRadioButton);
        quicktimeRadioButton.setForeground(new java.awt.Color(128, 128, 128));
        quicktimeRadioButton.setText("QTJ (Quicktime for Java) Player");
        playerPanel.add(quicktimeRadioButton);

        playerButtonGroup.add(jmfRadioButton);
        jmfRadioButton.setForeground(new java.awt.Color(128, 128, 128));
        jmfRadioButton.setText("JMF (Java Media Framework) Player");
        playerPanel.add(jmfRadioButton);

        playerOptionsPanel.add(playerPanel, java.awt.BorderLayout.CENTER);

        restartHintPanel.setLayout(new javax.swing.BoxLayout(restartHintPanel, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        restartHintTextArea.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        restartHintTextArea.setColumns(20);
        restartHintTextArea.setEditable(false);
        restartHintTextArea.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        restartHintTextArea.setForeground(new java.awt.Color(204, 0, 0));
        restartHintTextArea.setLineWrap(true);
        restartHintTextArea.setRows(3);
        restartHintTextArea.setText(FOLKERInternationalizer.getString("dialog.preferences.playerhint"));
        restartHintTextArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(restartHintTextArea);

        restartHintPanel.add(jScrollPane1);

        playerOptionsPanel.add(restartHintPanel, java.awt.BorderLayout.PAGE_START);

        optionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(FOLKERInternationalizer.getString("dialog.preferences.options")));
        optionsPanel.setLayout(new javax.swing.BoxLayout(optionsPanel, javax.swing.BoxLayout.Y_AXIS));

        useControlCheckBoxPanel.setLayout(new javax.swing.BoxLayout(useControlCheckBoxPanel, javax.swing.BoxLayout.LINE_AXIS));

        useControlCheckBox.setText(FOLKERInternationalizer.getString("dialog.preferences.controloption"));
        useControlCheckBoxPanel.add(useControlCheckBox);

        optionsPanel.add(useControlCheckBoxPanel);

        loopTimePanel.setLayout(new javax.swing.BoxLayout(loopTimePanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText(FOLKERInternationalizer.getString("dialog.preferences.waitloop"));
        loopTimePanel.add(jLabel1);

        loopTimeSlider.setMajorTickSpacing(200);
        loopTimeSlider.setMaximum(1000);
        loopTimeSlider.setMinimum(200);
        loopTimeSlider.setMinorTickSpacing(100);
        loopTimeSlider.setPaintLabels(true);
        loopTimeSlider.setPaintTicks(true);
        loopTimeSlider.setPreferredSize(new java.awt.Dimension(250, 47));
        loopTimePanel.add(loopTimeSlider);

        optionsPanel.add(loopTimePanel);

        jLabel4.setText(FOLKERInternationalizer.getString("dialog.preferences.defaultAudioPath"));
        defaultAudioPanel.add(jLabel4);

        defaultAudioTextField.setMinimumSize(new java.awt.Dimension(250, 20));
        defaultAudioTextField.setPreferredSize(new java.awt.Dimension(250, 20));
        defaultAudioPanel.add(defaultAudioTextField);

        defaultAudioBrowseButton.setText(FOLKERInternationalizer.getString("masker.browse"));
        defaultAudioBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultAudioBrowseButtonActionPerformed(evt);
            }
        });
        defaultAudioPanel.add(defaultAudioBrowseButton);

        optionsPanel.add(defaultAudioPanel);

        playerOptionsPanel.add(optionsPanel, java.awt.BorderLayout.PAGE_END);

        jTabbedPane1.addTab("Player", playerOptionsPanel);

        fontOptionsPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        setFontCheckBox.setText(FOLKERInternationalizer.getString("dialog.preferences.imposefont"));
        setFontCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setFontCheckBoxActionPerformed(evt);
            }
        });
        jPanel1.add(setFontCheckBox);

        fontsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        fontsComboBox.setEnabled(false);
        fontsComboBox.setMaximumSize(new java.awt.Dimension(5000, 20));
        fontsComboBox.setPreferredSize(new java.awt.Dimension(200, 20));
        fontsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontsComboBoxActionPerformed(evt);
            }
        });
        jPanel1.add(fontsComboBox);

        fontOptionsPanel.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Test"));

        fontTestTextField.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        fontTestTextField.setForeground(new java.awt.Color(51, 102, 0));
        fontTestTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fontTestTextField.setText("jTextField1");
        fontTestTextField.setMaximumSize(new java.awt.Dimension(5000, 40));
        fontTestTextField.setMinimumSize(new java.awt.Dimension(6, 40));
        fontTestTextField.setPreferredSize(new java.awt.Dimension(300, 40));
        jPanel2.add(fontTestTextField);

        fontOptionsPanel.add(jPanel2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(FOLKERInternationalizer.getString("dialog.preferences.font"), fontOptionsPanel);

        languageOptionsPanel.setLayout(new java.awt.BorderLayout());

        languagePanel.setLayout(new javax.swing.BoxLayout(languagePanel, javax.swing.BoxLayout.Y_AXIS));

        languagesButtonGroup.add(germanRadioButton);
        germanRadioButton.setText("Deutsch (DE)");
        languagePanel.add(germanRadioButton);

        languagesButtonGroup.add(englishRadioButton);
        englishRadioButton.setText("English (EN)");
        languagePanel.add(englishRadioButton);

        languagesButtonGroup.add(frenchRadioButton);
        frenchRadioButton.setText("Francais (FR)");
        languagePanel.add(frenchRadioButton);

        languageOptionsPanel.add(languagePanel, java.awt.BorderLayout.CENTER);

        restartHintPanel1.setLayout(new javax.swing.BoxLayout(restartHintPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        restartHintTextArea1.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        restartHintTextArea1.setColumns(20);
        restartHintTextArea1.setEditable(false);
        restartHintTextArea1.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        restartHintTextArea1.setForeground(new java.awt.Color(204, 0, 0));
        restartHintTextArea1.setLineWrap(true);
        restartHintTextArea1.setRows(3);
        restartHintTextArea1.setText(FOLKERInternationalizer.getString("dialog.preferences.languagehint"));
        restartHintTextArea1.setWrapStyleWord(true);
        jScrollPane2.setViewportView(restartHintTextArea1);

        restartHintPanel1.add(jScrollPane2);

        languageOptionsPanel.add(restartHintPanel1, java.awt.BorderLayout.PAGE_START);

        jTabbedPane1.addTab(FOLKERInternationalizer.getString("dialog.preferences.language"), languageOptionsPanel);

        advancedOptionsPanel.setLayout(new java.awt.BorderLayout());

        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

        enableLoggingCheckBox.setText(FOLKERInternationalizer.getString("dialog.preferences.enableLogging"));
        mainPanel.add(enableLoggingCheckBox);

        advancedOptionsPanel.add(mainPanel, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(FOLKERInternationalizer.getString("dialog.preferences.advanced"), advancedOptionsPanel);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
        jTabbedPane1.getAccessibleContext().setAccessibleName("Player");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okPanel.add(okButton);

        add(okPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        java.awt.Container c = this.getTopLevelAncestor();
        if (c instanceof JDialog){
            JDialog d = (JDialog)c;
            d.setVisible(false);
            d.dispose();
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void level0RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_level0RadioButtonActionPerformed
        updateCheckBoxEnablement();
    }//GEN-LAST:event_level0RadioButtonActionPerformed

    private void level1RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_level1RadioButtonActionPerformed
        updateCheckBoxEnablement();
    }//GEN-LAST:event_level1RadioButtonActionPerformed

    private void level2RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_level2RadioButtonActionPerformed
        updateCheckBoxEnablement();
    }//GEN-LAST:event_level2RadioButtonActionPerformed

    private void setFontCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setFontCheckBoxActionPerformed
        fontsComboBox.setEnabled(setFontCheckBox.isSelected());
        if (setFontCheckBox.isEnabled()){
            fontsComboBoxActionPerformed(evt);
        }
    }//GEN-LAST:event_setFontCheckBoxActionPerformed

    private void fontsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontsComboBoxActionPerformed
        String selectedFont = (String)(fontsComboBox.getSelectedItem());
        Font font = new Font(selectedFont, Font.PLAIN, 16);
        fontTestTextField.setFont(font);
    }//GEN-LAST:event_fontsComboBoxActionPerformed

    private void level3RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_level3RadioButtonActionPerformed
        updateCheckBoxEnablement();
    }//GEN-LAST:event_level3RadioButtonActionPerformed

    private void alphabetComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alphabetComboBoxActionPerformed

    }//GEN-LAST:event_alphabetComboBoxActionPerformed

    private void defaultAudioBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultAudioBrowseButtonActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int approve = jfc.showOpenDialog(defaultAudioBrowseButton);
        if (approve!=JFileChooser.APPROVE_OPTION) return;
        defaultAudioTextField.setText(jfc.getSelectedFile().getAbsolutePath());
    }//GEN-LAST:event_defaultAudioBrowseButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel advancedOptionsPanel;
    private javax.swing.JComboBox alphabetComboBox;
    private javax.swing.JPanel alphabetPanel;
    private javax.swing.JRadioButton basRadioButton;
    private javax.swing.JButton defaultAudioBrowseButton;
    private javax.swing.JPanel defaultAudioPanel;
    private javax.swing.JTextField defaultAudioTextField;
    private javax.swing.JRadioButton directShowRadioButton;
    private javax.swing.JRadioButton elanQuicktimeRadioButton;
    private javax.swing.JCheckBox enableLoggingCheckBox;
    private javax.swing.JRadioButton englishRadioButton;
    private javax.swing.JPanel fontOptionsPanel;
    private javax.swing.JTextField fontTestTextField;
    private javax.swing.JComboBox fontsComboBox;
    private javax.swing.JRadioButton frenchRadioButton;
    private javax.swing.JRadioButton germanRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton jdsRadioButton;
    private javax.swing.JRadioButton jmfRadioButton;
    private javax.swing.JPanel languageOptionsPanel;
    private javax.swing.JPanel languagePanel;
    private javax.swing.ButtonGroup languagesButtonGroup;
    private javax.swing.JRadioButton level0RadioButton;
    private javax.swing.JRadioButton level1RadioButton;
    private javax.swing.JRadioButton level2RadioButton;
    private javax.swing.JRadioButton level3RadioButton;
    private javax.swing.JPanel loopTimePanel;
    private javax.swing.JSlider loopTimeSlider;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel middlePanel;
    private javax.swing.JCheckBox normalizeWhiteSpaceCheckBox;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okPanel;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.ButtonGroup parseLevelButtonGroup;
    private javax.swing.JPanel parseLevelPanel;
    private javax.swing.JPanel parseOptionsPanel;
    private javax.swing.JPanel parsePanel;
    private javax.swing.ButtonGroup playerButtonGroup;
    private javax.swing.JPanel playerOptionsPanel;
    private javax.swing.JPanel playerPanel;
    private javax.swing.JRadioButton quicktimeRadioButton;
    private javax.swing.JCheckBox recalculatePausesCheckBox;
    private javax.swing.JPanel restartHintPanel;
    private javax.swing.JPanel restartHintPanel1;
    private javax.swing.JTextArea restartHintTextArea;
    private javax.swing.JTextArea restartHintTextArea1;
    private javax.swing.JCheckBox setFontCheckBox;
    private javax.swing.JCheckBox useControlCheckBox;
    private javax.swing.JPanel useControlCheckBoxPanel;
    // End of variables declaration//GEN-END:variables

    private void updateCheckBoxEnablement() {
        normalizeWhiteSpaceCheckBox.setEnabled(level2RadioButton.isSelected());
        recalculatePausesCheckBox.setEnabled(level2RadioButton.isSelected());
    }
    
}
