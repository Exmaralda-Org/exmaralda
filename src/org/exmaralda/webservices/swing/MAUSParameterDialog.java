/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices.swing;

import java.util.HashMap;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.exmaralda.common.ExmaraldaApplication;

/**
 *
 * @author Schmidt
 */
public class MAUSParameterDialog extends javax.swing.JDialog {

    // Language of the speech to be processed; we use the RFC5646 sub-structure 'iso639-3 - iso3166-1 [ - iso3166-2], 
    // e.g. 'eng-US' for American English, 'deu-AT-1' for Austrian German spoken in 'Oberoesterreich'; 
    // defines the possible orthographic text language in the input, the text-to-phoneme tranformation and some language specific transformations 
    //within the MAUS process. The code 'gsw-CH' (= Swiss German) denotes orthographic text input in Swiss German 'Dieth' encoding. 
    
     
    // [aus-AU, eus-ES, eus-FR, cat-ES, nld-BE, nld-NL, eng-AU, eng-US, eng-GB, eng-SC, eng-NZ, 
    // ekk-EE, fin-FI, fra-FR, kat-GE, deu-DE, gsw-CH, gsw-CH-BE, gsw-CH-BS, gsw-CH-GR, gsw-CH-SG, 
    // gsw-CH-ZH, hun-HU, ita-IT, jpn-JP, gup-AU, mlt-MT, nor-NO, pol-PL, ron-RO, rus-RU, spa-ES, 
    // guf-AU, cat, deu, eng, fra, hun, ita, mlt, nld, pol, nze, fin, ron, spa] 
    
    public static String[][] LANGUAGES = {
        {"deu", "German"},
        {"eng", "English"},
        {"nld", "Dutch ; Flemish"},
        {"fra", "French"},
        {"spa", "Spanish"},
        {"ita", "Italian"},
        {"cat", "Catalan"}, 	
        {"ron", "Romanian; Moldavian; Moldovan"},
        {"hun", "Hungarian"},
        {"pol", "Polish"},
        {"fin", "Finnish"},
        {"cat-ES", "Catalan (Spain)"},
        {"ekk-EE", "Estonian (Estonia)"},        
        {"eng-AU", "English (Australia)"},
        {"eng-US", "English (USA)"},
        {"eng-GB", "English (Great Britain)"},
        {"eng-NZ", "English (New Zealand)"},
        {"eng-SC", "English (Seychelles)"},
        {"fra-FR", "French (France)"},
        {"eus-ES", "Basque (Spain)"},
        {"eus-FR", "Basque (France)"}, 
        {"guf-AU", "Gupapuyngu (Australia)"},
        {"gup-AU", "Gunwinggu (Australia)"},
        {"ita-IT", "Italian (Italy)"},
        {"jpn-JP", "Japanese (Japan)"},
        {"nld-BE", "Flemish (Belgium)"},
        {"kat-GE", "Georgian (Georgia)"},
        {"nld-NL", "Dutch (Netherlands)"},
        {"nor-NO", "Norwegian (Norway)"},
        {"pol-PL", "Polish (Poland)"},
        {"rus-RU", "Russian (Russia)"},
        {"gsw-CH", "Swiss German, Dieth encoding"},
        {"gsw-CH-BE", "Swiss German, Dieth encoding (Bern)"},
        {"gsw-CH-BS", "Swiss German, Dieth encoding (Basel)"},
        {"gsw-CH-GR", "Swiss German, Dieth encoding (Graubünden)"},
        {"gsw-CH-SG", "Swiss German, Dieth encoding (St. Gallen)"},
        {"gsw-CH-ZH", "Swiss German, Dieth encoding (Zürich)"},
        
        
        // gsw-CH, gsw-CH-BE, gsw-CH-BS, gsw-CH-GR, gsw-CH-SG, gsw-CH-ZH,

        {"aus-AU", "???"}

    };   

    public boolean approved = false;
    java.awt.Frame parent;
    
    /**
     * Creates new form MAUSParameterDialog
     * @param parent
     * @param modal
     */
    public MAUSParameterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.languageComboBox.setRenderer(new MAUSLanguagesComboBoxRenderer());
        // #441
        if (parent instanceof ExmaraldaApplication){
            ExmaraldaApplication exmaraldaApplication = (ExmaraldaApplication)parent;
            Preferences prefNode = java.util.prefs.Preferences.userRoot().node(exmaraldaApplication.getPreferencesNode());
            getPreferences(prefNode);
        }
    }
    
    // #441
    private void getPreferences(Preferences prefNode) {
        languageComboBox.setSelectedIndex(prefNode.getInt("MAUS-LANGUAGE-INDEX", 0));
        segmentChainSelectionRadioButton.setSelected(prefNode.getBoolean("MAUS-SEGMENT-CHAIN-SELECTION", false));
        segmentRadioButton.setSelected(prefNode.getBoolean("MAUS-USE-SEGMENTATION", false));
        segmentationComboBox.setSelectedItem(prefNode.get("MAUS-SEGMENTATION-ALGORITHM", "HIAT"));
        wordsOrthoCheckBox.setSelected(prefNode.getBoolean("MAUS-WORDS-ORTHOGRAPHIC", true));
        wordsSAMPACheckBox.setSelected(prefNode.getBoolean("MAUS-WORDS-SAMPA", true));
        phonemesCheckBox.setSelected(prefNode.getBoolean("MAUS-PHONEMES", false));
        mergeWithExistingRadioButton.setSelected(prefNode.getBoolean("MAUS-MERGE", false));
        toleranceSpinner.setValue(prefNode.getDouble("MAUS-TOLERANCE", 0.01));
        forceCheckBox.setSelected(prefNode.getBoolean("MAUS-FORCE", false));
    }
    
    // #441
    private void setPreferences(Preferences prefNode) {
        prefNode.putInt("MAUS-LANGUAGE-INDEX", languageComboBox.getSelectedIndex());
        prefNode.putBoolean("MAUS-SEGMENT-CHAIN-SELECTION", segmentChainSelectionRadioButton.isSelected());
        prefNode.putBoolean("MAUS-USE-SEGMENTATION", segmentRadioButton.isSelected());
        prefNode.put("MAUS-SEGMENTATION-ALGORITHM", (String) segmentationComboBox.getSelectedItem());
        prefNode.putBoolean("MAUS-WORDS-ORTHOGRAPHIC", wordsOrthoCheckBox.isSelected());
        prefNode.putBoolean("MAUS-WORDS-SAMPA", wordsSAMPACheckBox.isSelected());
        prefNode.putBoolean("MAUS-PHONEMES", phonemesCheckBox.isSelected());
        prefNode.putBoolean("MAUS-MERGE", mergeWithExistingRadioButton.isSelected());
        prefNode.putDouble("MAUS-TOLERANCE", (double) toleranceSpinner.getValue());
        prefNode.putBoolean("MAUS-FORCE", forceCheckBox.isSelected());
    }
    
    public HashMap<String, Object> getMAUSParameters() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("LANGUAGE", ((String[])languageComboBox.getSelectedItem())[0]);
        result.put("SEGMENT-CHAIN-SELECTION", segmentChainSelectionRadioButton.isSelected());
        result.put("USE-SEGMENTATION", segmentRadioButton.isSelected());
        result.put("SEGMENTATION-ALGORITHM", segmentationComboBox.getSelectedItem());
        result.put("WORDS-ORTHOGRAPHIC", wordsOrthoCheckBox.isSelected());
        result.put("WORDS-SAMPA", wordsSAMPACheckBox.isSelected());
        result.put("PHONEMES", phonemesCheckBox.isSelected());
        result.put("MERGE", mergeWithExistingRadioButton.isSelected());
        result.put("TOLERANCE", toleranceSpinner.getValue());
        result.put("FORCE", forceCheckBox.isSelected());
        
        return result;
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        segmentationButtonGroup = new javax.swing.ButtonGroup();
        outputButtonGroup = new javax.swing.ButtonGroup();
        selectionButtonGroup = new javax.swing.ButtonGroup();
        mainPanel = new javax.swing.JPanel();
        warningPanel = new javax.swing.JPanel();
        showWarningButton = new javax.swing.JButton();
        languagePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        languageComboBox = new javax.swing.JComboBox();
        selectionPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        eventsSelectionRadioButton = new javax.swing.JRadioButton();
        segmentChainSelectionRadioButton = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        segmentationPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        textAsIsRadioButton = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        segmentRadioButton = new javax.swing.JRadioButton();
        segmentationComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        annotationLevelsPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        wordsOrthoCheckBox = new javax.swing.JCheckBox();
        wordsSAMPACheckBox = new javax.swing.JCheckBox();
        phonemesCheckBox = new javax.swing.JCheckBox();
        outputPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        openAsNewRadioButton = new javax.swing.JRadioButton();
        mergeWithExistingRadioButton = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        toleranceLabel = new javax.swing.JLabel();
        toleranceSpinner = new javax.swing.JSpinner();
        secondsLabel = new javax.swing.JLabel();
        forceCheckBox = new javax.swing.JCheckBox();
        okCancelPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("MAUS Parameters");

        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

        showWarningButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/webservices/swing/hexagon-exclamation-solid.png"))); // NOI18N
        showWarningButton.setText("Information about BAS webservices");
        showWarningButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showWarningButtonActionPerformed(evt);
            }
        });
        warningPanel.add(showWarningButton);

        mainPanel.add(warningPanel);

        languagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Language"));
        languagePanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("What language is spoken in the excerpt?");
        languagePanel.add(jLabel1, java.awt.BorderLayout.NORTH);

        languageComboBox.setModel(new javax.swing.DefaultComboBoxModel(LANGUAGES));
        languagePanel.add(languageComboBox, java.awt.BorderLayout.SOUTH);

        mainPanel.add(languagePanel);

        selectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Selection"));
        selectionPanel.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.GridLayout(2, 1));

        selectionButtonGroup.add(eventsSelectionRadioButton);
        eventsSelectionRadioButton.setSelected(true);
        eventsSelectionRadioButton.setText("Events of the current selection");
        jPanel5.add(eventsSelectionRadioButton);

        selectionButtonGroup.add(segmentChainSelectionRadioButton);
        segmentChainSelectionRadioButton.setText("Segment chain around the current selection");
        jPanel5.add(segmentChainSelectionRadioButton);

        selectionPanel.add(jPanel5, java.awt.BorderLayout.CENTER);

        jLabel5.setText("Which transcription text do you want to pass to MAUS?");
        selectionPanel.add(jLabel5, java.awt.BorderLayout.NORTH);

        mainPanel.add(selectionPanel);

        segmentationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Segmentation"));
        segmentationPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridLayout(2, 1));

        segmentationButtonGroup.add(textAsIsRadioButton);
        textAsIsRadioButton.setSelected(true);
        textAsIsRadioButton.setText("Use the transcription text as is");
        jPanel1.add(textAsIsRadioButton);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        segmentationButtonGroup.add(segmentRadioButton);
        segmentRadioButton.setText("Use a segmentation algorithm: ");
        jPanel2.add(segmentRadioButton);

        segmentationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "HIAT", "GENERIC", "cGAT Minimal" }));
        jPanel2.add(segmentationComboBox);

        jPanel1.add(jPanel2);

        segmentationPanel.add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabel2.setText("How do you want to pass transcription text to MAUS?");
        segmentationPanel.add(jLabel2, java.awt.BorderLayout.NORTH);

        mainPanel.add(segmentationPanel);

        annotationLevelsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Annotation Levels"));
        annotationLevelsPanel.setLayout(new java.awt.BorderLayout());

        jLabel4.setText("Which annotation levels of the MAUS output do you want to use?");
        annotationLevelsPanel.add(jLabel4, java.awt.BorderLayout.NORTH);

        wordsOrthoCheckBox.setSelected(true);
        wordsOrthoCheckBox.setText("Words (orthographic)");
        jPanel4.add(wordsOrthoCheckBox);

        wordsSAMPACheckBox.setSelected(true);
        wordsSAMPACheckBox.setText("Words (SAMPA)");
        jPanel4.add(wordsSAMPACheckBox);

        phonemesCheckBox.setText("Phonemes");
        jPanel4.add(phonemesCheckBox);

        annotationLevelsPanel.add(jPanel4, java.awt.BorderLayout.CENTER);

        mainPanel.add(annotationLevelsPanel);

        outputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Output"));
        outputPanel.setLayout(new java.awt.BorderLayout());

        jLabel3.setText("What do you want to do with the MAUS output?");
        outputPanel.add(jLabel3, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.GridLayout(2, 1));

        outputButtonGroup.add(openAsNewRadioButton);
        openAsNewRadioButton.setSelected(true);
        openAsNewRadioButton.setText("Open it as a new file");
        openAsNewRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAsNewRadioButtonActionPerformed(evt);
            }
        });
        jPanel3.add(openAsNewRadioButton);

        outputButtonGroup.add(mergeWithExistingRadioButton);
        mergeWithExistingRadioButton.setText("Merge it with the existing transcription");
        mergeWithExistingRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mergeWithExistingRadioButtonActionPerformed(evt);
            }
        });
        jPanel3.add(mergeWithExistingRadioButton);

        outputPanel.add(jPanel3, java.awt.BorderLayout.CENTER);

        toleranceLabel.setText("Timeline tolerance: ");
        toleranceLabel.setToolTipText("Timeline items with difference below the tolerance will be treated as identical");
        toleranceLabel.setEnabled(false);
        jPanel6.add(toleranceLabel);

        toleranceSpinner.setModel(new javax.swing.SpinnerNumberModel(0.01d, 0.0d, 0.2d, 0.01d));
        toleranceSpinner.setEnabled(false);
        jPanel6.add(toleranceSpinner);

        secondsLabel.setText("s");
        secondsLabel.setEnabled(false);
        jPanel6.add(secondsLabel);

        forceCheckBox.setText("Force start and end points");
        forceCheckBox.setEnabled(false);
        jPanel6.add(forceCheckBox);

        outputPanel.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        mainPanel.add(outputPanel);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okCancelPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        okCancelPanel.add(cancelButton);

        getContentPane().add(okCancelPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        approved = true;
        
        // #441
        if (this.parent instanceof ExmaraldaApplication){
            ExmaraldaApplication exmaraldaApplication = (ExmaraldaApplication)parent;
            Preferences prefNode = java.util.prefs.Preferences.userRoot().node(exmaraldaApplication.getPreferencesNode());
            setPreferences(prefNode);
        }
        
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        approved = false;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void showWarningButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showWarningButtonActionPerformed
        String html = "<html>"
        + "The  webservices for automatic alignment and grapheme to phoneme conversion "
        + "are kindly provided by <br/> the <b>Bavarian Archive for Speech Signals (BAS)</b> "
        + "at the Ludwig-Maximilians-University (LMU) of Munich. <br/>"
        + "Free usage of the services is permissible for academic research. "
        + "Please refer to the BAS website for details on usage and how to cite the services. <br/>"
        + "Please note that your data (audio and text from the transcription) is sent to an <b>external server</b>. <br/>"
        + "It is your responsibility to ensure that this conforms to <b>data protection</b> regulations "
        + "which may apply to your data. "
        + "</html>";

        JOptionPane.showMessageDialog(this, html, "Information about BAS webservices", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_showWarningButtonActionPerformed

    private void openAsNewRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAsNewRadioButtonActionPerformed
      adjustToleranceControls();
    }//GEN-LAST:event_openAsNewRadioButtonActionPerformed

    private void mergeWithExistingRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mergeWithExistingRadioButtonActionPerformed
      adjustToleranceControls();
    }//GEN-LAST:event_mergeWithExistingRadioButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MAUSParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MAUSParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MAUSParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MAUSParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MAUSParameterDialog dialog = new MAUSParameterDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel annotationLevelsPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton eventsSelectionRadioButton;
    private javax.swing.JCheckBox forceCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JComboBox languageComboBox;
    private javax.swing.JPanel languagePanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JRadioButton mergeWithExistingRadioButton;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okCancelPanel;
    private javax.swing.JRadioButton openAsNewRadioButton;
    private javax.swing.ButtonGroup outputButtonGroup;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JCheckBox phonemesCheckBox;
    private javax.swing.JLabel secondsLabel;
    private javax.swing.JRadioButton segmentChainSelectionRadioButton;
    private javax.swing.JRadioButton segmentRadioButton;
    private javax.swing.ButtonGroup segmentationButtonGroup;
    private javax.swing.JComboBox segmentationComboBox;
    private javax.swing.JPanel segmentationPanel;
    private javax.swing.ButtonGroup selectionButtonGroup;
    private javax.swing.JPanel selectionPanel;
    private javax.swing.JButton showWarningButton;
    private javax.swing.JRadioButton textAsIsRadioButton;
    private javax.swing.JLabel toleranceLabel;
    private javax.swing.JSpinner toleranceSpinner;
    private javax.swing.JPanel warningPanel;
    private javax.swing.JCheckBox wordsOrthoCheckBox;
    private javax.swing.JCheckBox wordsSAMPACheckBox;
    // End of variables declaration//GEN-END:variables

    private void adjustToleranceControls() {
        boolean merge = mergeWithExistingRadioButton.isSelected();
        this.toleranceLabel.setEnabled(merge);
        this.toleranceSpinner.setEnabled(merge);
        this.secondsLabel.setEnabled(merge);
        this.forceCheckBox.setEnabled(merge);
    }    


}
