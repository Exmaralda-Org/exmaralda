/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices.swing;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;

/**
 *
 * @author Schmidt
 */
public class DeepLParameterDialog extends javax.swing.JDialog {

     
    /* "BG" - Bulgarian
        "CS" - Czech
        "DA" - Danish
        "DE" - German
        "EL" - Greek
        "EN" - English
        "ES" - Spanish
        "ET" - Estonian
        "FI" - Finnish
        "FR" - French
        "HU" - Hungarian
        "IT" - Italian
        "JA" - Japanese
        "LT" - Lithuanian
        "LV" - Latvian
        "NL" - Dutch
        "PL" - Polish
        "PT" - Portuguese (all Portuguese varieties mixed)
        "RO" - Romanian
        "RU" - Russian
        "SK" - Slovak
        "SL" - Slovenian
        "SV" - Swedish
        "ZH" - Chinese */
    
    static String[][] SOURCE_LANGUAGES = {
        {"auto_detect", "Auto detect"},
        {"BG","Bulgarian"},
        {"CS","Czech"},
        {"DA","Danish"},
        {"DE","German"},
        {"EL","Greek"},
        {"EN","English"},
        {"ES","Spanish"},
        {"ET","Estonian"},
        {"FI","Finnish"},
        {"FR","French"},
        {"HU","Hungarian"},
        {"IT","Italian"},
        {"JA","Japanese"},
        {"LT","Lithuanian"},
        {"LV","Latvian"},
        {"NL","Dutch"},
        {"PL","Polish"},
        {"PT","Portuguese"},
        {"RO","Romanian"},
        {"RU","Russian"},
        {"SK","Slovak"},
        {"SL","Slovenian"},
        {"SV","Swedish"},
        {"ZH","Chinese"}        
    };   


    static String[][] TARGET_LANGUAGES = {
        {"BG","Bulgarian"},
        {"CS","Czech"},
        {"DA","Danish"},
        {"DE","German"},
        {"EL","Greek"},
        {"EN-GB","English(British)"},
        {"EN-US","English(American)"},
        {"ES","Spanish"},
        {"ET","Estonian"},
        {"FI","Finnish"},
        {"FR","French"},
        {"HU","Hungarian"},
        {"IT","Italian"},
        {"JA","Japanese"},
        {"LT","Lithuanian"},
        {"LV","Latvian"},
        {"NL","Dutch"},
        {"PL","Polish"},
        {"PT-PT","Portuguese(all excluding Brazilian)"},
        {"PT-BR","Portuguese(Brazilian)"},
        {"RO","Romanian"},
        {"RU","Russian"},
        {"SK","Slovak"},
        {"SL","Slovenian"},
        {"SV","Swedish"},
        {"ZH","Chinese"}
    };   
    
    String[] FORMALITY_SUPPORTED = {
        "DE", "FR", "IT", "ES", "NL", "PL", "PT-PT", "PT-BR", "RU"
    };



    public boolean approved = false;
    
    /**
     * Creates new form MAUSParameterDialog
     * @param parent
     * @param modal
     */
    public DeepLParameterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        sourceLanguageComboBox.setRenderer(new MAUSLanguagesComboBoxRenderer());        
        targetLanguageComboBox.setRenderer(new MAUSLanguagesComboBoxRenderer());
        sourceLanguageComboBox.setSelectedIndex(4); // German
        targetLanguageComboBox.setSelectedIndex(5); // British English
        formalityPanel.setVisible(false);
    }
    
    public HashMap<String, Object> getDeepLParameters() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("SOURCE-LANGUAGE", ((String[])sourceLanguageComboBox.getSelectedItem())[0]);
        result.put("TARGET-LANGUAGE", ((String[])targetLanguageComboBox.getSelectedItem())[0]);
        
        result.put("SELECTED-TIER", selectedTierRadioButton.isSelected());
        result.put("TIER-CATEGORY", tierCategoriesComboBox.getSelectedItem());
                
        result.put("EVENT-BY-EVENT", eventRadioButton.isSelected());        
        result.put("USE-SEGMENTATION", segmentCheckBox.isSelected());
        result.put("SEGMENTATION-ALGORITHM", segmentationComboBox.getSelectedItem());
        
        String formalityLevel = "default";
        if (lessFormalityRadioButton.isSelected()) {formalityLevel = "less";}
        if (moreFormalityRadioButton.isSelected()) {formalityLevel = "more";}
        String selectedTargetLanguage = ((String[])targetLanguageComboBox.getSelectedItem())[0];
        boolean supported = Arrays.asList(FORMALITY_SUPPORTED).contains(selectedTargetLanguage);
        if (!supported){
            formalityLevel = "not_supported";
        }
        result.put("FORMALITY-LEVEL", formalityLevel);
        
        result.put("USE-PRO", proAPIRadioButton.isSelected());
        result.put("API-KEY", apiKeyTextField.getText());
        result.put("LANGUAGE-TIER", detectedLanguageCheckBox.isSelected());
        return result;
    }
    
    public void setParameters(String apiKey, String segmentationAlgorithm, BasicTranscription transcription, int selectedRow){
        apiKeyTextField.setText(apiKey);
        updateOK();
        segmentationComboBox.setSelectedItem(segmentationAlgorithm);
        if (selectedRow>=0){
            selectedTierRadioButton.setSelected(true);
            selectedTierRadioButton.setEnabled(true);
        } else {
            tiersWithCategoryRadioButton.setSelected(true);
            selectedTierRadioButton.setEnabled(false);            
        }
        String[] categories = transcription.getBody().getCategories();
        tierCategoriesComboBox.setModel(new DefaultComboBoxModel(categories));        
    }
    
    private void updateOK() {
        boolean parametersSuffice = 
                apiKeyTextField.getText().length()>0;
        okButton.setEnabled(parametersSuffice);
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
        apiTypeButtonGroup = new javax.swing.ButtonGroup();
        formalityButtonGroup = new javax.swing.ButtonGroup();
        mainPanel = new javax.swing.JPanel();
        sourceLanguagePanel = new javax.swing.JPanel();
        sourceLanguageLabel = new javax.swing.JLabel();
        sourceLanguageComboBox = new javax.swing.JComboBox();
        detectedLanguagePanel = new javax.swing.JPanel();
        detectedLanguageCheckBox = new javax.swing.JCheckBox();
        targetLanguagePanel = new javax.swing.JPanel();
        targetLanguageLabel = new javax.swing.JLabel();
        targetLanguageComboBox = new javax.swing.JComboBox();
        formalityPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        defaultFormalityRadioButton = new javax.swing.JRadioButton();
        moreFormalityRadioButton = new javax.swing.JRadioButton();
        lessFormalityRadioButton = new javax.swing.JRadioButton();
        selectionPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        selectedTierRadioButton = new javax.swing.JRadioButton();
        tiersWithCategoryPanel = new javax.swing.JPanel();
        tiersWithCategoryRadioButton = new javax.swing.JRadioButton();
        tierCategoriesComboBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        segmentationPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        eventRadioButton = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        segmentChainRadioButton = new javax.swing.JRadioButton();
        segmentCheckBox = new javax.swing.JCheckBox();
        segmentationComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        apikeyPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        apiKeyTextField = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        freeAPIRadioButton = new javax.swing.JRadioButton();
        proAPIRadioButton = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        getAPIKeyButton = new javax.swing.JButton();
        okCancelPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("MAUS Parameters");

        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

        sourceLanguagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Source Language"));
        sourceLanguagePanel.setLayout(new java.awt.BorderLayout());

        sourceLanguageLabel.setText("What is the source language?");
        sourceLanguagePanel.add(sourceLanguageLabel, java.awt.BorderLayout.NORTH);

        sourceLanguageComboBox.setModel(new javax.swing.DefaultComboBoxModel(SOURCE_LANGUAGES));
        sourceLanguagePanel.add(sourceLanguageComboBox, java.awt.BorderLayout.CENTER);

        detectedLanguageCheckBox.setText("Make an additional tier for the detected language");
        detectedLanguagePanel.add(detectedLanguageCheckBox);

        sourceLanguagePanel.add(detectedLanguagePanel, java.awt.BorderLayout.SOUTH);

        mainPanel.add(sourceLanguagePanel);

        targetLanguagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Target Language"));
        targetLanguagePanel.setLayout(new java.awt.BorderLayout());

        targetLanguageLabel.setText("What is the target language?");
        targetLanguagePanel.add(targetLanguageLabel, java.awt.BorderLayout.NORTH);

        targetLanguageComboBox.setModel(new javax.swing.DefaultComboBoxModel(TARGET_LANGUAGES));
        targetLanguageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetLanguageComboBoxActionPerformed(evt);
            }
        });
        targetLanguagePanel.add(targetLanguageComboBox, java.awt.BorderLayout.CENTER);

        formalityPanel.setLayout(new java.awt.BorderLayout());

        jLabel4.setText("Which formality level do you want DeepL to use? ");
        formalityPanel.add(jLabel4, java.awt.BorderLayout.NORTH);

        formalityButtonGroup.add(defaultFormalityRadioButton);
        defaultFormalityRadioButton.setText("Default");
        jPanel4.add(defaultFormalityRadioButton);

        formalityButtonGroup.add(moreFormalityRadioButton);
        moreFormalityRadioButton.setText("More");
        jPanel4.add(moreFormalityRadioButton);

        formalityButtonGroup.add(lessFormalityRadioButton);
        lessFormalityRadioButton.setText("Less");
        jPanel4.add(lessFormalityRadioButton);

        formalityPanel.add(jPanel4, java.awt.BorderLayout.CENTER);

        targetLanguagePanel.add(formalityPanel, java.awt.BorderLayout.SOUTH);

        mainPanel.add(targetLanguagePanel);

        selectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Selection"));
        selectionPanel.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.GridLayout(2, 1));

        selectionButtonGroup.add(selectedTierRadioButton);
        selectedTierRadioButton.setSelected(true);
        selectedTierRadioButton.setText("Currently selected tier");
        jPanel5.add(selectedTierRadioButton);

        tiersWithCategoryPanel.setLayout(new javax.swing.BoxLayout(tiersWithCategoryPanel, javax.swing.BoxLayout.LINE_AXIS));

        selectionButtonGroup.add(tiersWithCategoryRadioButton);
        tiersWithCategoryRadioButton.setText("All tiers with category: ");
        tiersWithCategoryPanel.add(tiersWithCategoryRadioButton);

        tierCategoriesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        tiersWithCategoryPanel.add(tierCategoriesComboBox);

        jPanel5.add(tiersWithCategoryPanel);

        selectionPanel.add(jPanel5, java.awt.BorderLayout.CENTER);

        jLabel5.setText("Which text do you want to pass to DeepL?");
        selectionPanel.add(jLabel5, java.awt.BorderLayout.NORTH);

        mainPanel.add(selectionPanel);

        segmentationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Segmentation"));
        segmentationPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridLayout(2, 1));

        segmentationButtonGroup.add(eventRadioButton);
        eventRadioButton.setSelected(true);
        eventRadioButton.setText("Event by event");
        eventRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventRadioButtonActionPerformed(evt);
            }
        });
        jPanel1.add(eventRadioButton);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        segmentationButtonGroup.add(segmentChainRadioButton);
        segmentChainRadioButton.setText("Full segment chains");
        segmentChainRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segmentChainRadioButtonActionPerformed(evt);
            }
        });
        jPanel2.add(segmentChainRadioButton);

        segmentCheckBox.setText("Use segmentation: ");
        segmentCheckBox.setEnabled(false);
        segmentCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segmentCheckBoxActionPerformed(evt);
            }
        });
        jPanel2.add(segmentCheckBox);

        segmentationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "HIAT", "GENERIC", "cGAT Minimal" }));
        segmentationComboBox.setEnabled(false);
        jPanel2.add(segmentationComboBox);

        jPanel1.add(jPanel2);

        segmentationPanel.add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabel2.setText("How do you want to pass text to DeepL?");
        segmentationPanel.add(jLabel2, java.awt.BorderLayout.NORTH);

        mainPanel.add(segmentationPanel);

        apikeyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("API key"));
        apikeyPanel.setLayout(new java.awt.BorderLayout());

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        apiKeyTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apiKeyTextFieldActionPerformed(evt);
            }
        });
        jPanel6.add(apiKeyTextField);

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));

        apiTypeButtonGroup.add(freeAPIRadioButton);
        freeAPIRadioButton.setSelected(true);
        freeAPIRadioButton.setText("Free");
        jPanel7.add(freeAPIRadioButton);

        apiTypeButtonGroup.add(proAPIRadioButton);
        proAPIRadioButton.setText("Pro Account");
        jPanel7.add(proAPIRadioButton);

        jPanel6.add(jPanel7);

        apikeyPanel.add(jPanel6, java.awt.BorderLayout.CENTER);

        jLabel6.setText("Please provide a valid API key for DeepL");
        apikeyPanel.add(jLabel6, java.awt.BorderLayout.PAGE_START);

        getAPIKeyButton.setBackground(java.awt.Color.blue);
        getAPIKeyButton.setText("Get an API key from the DeepL website...");
        getAPIKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getAPIKeyButtonActionPerformed(evt);
            }
        });
        apikeyPanel.add(getAPIKeyButton, java.awt.BorderLayout.PAGE_END);

        mainPanel.add(apikeyPanel);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        okButton.setText("OK");
        okButton.setEnabled(false);
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
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        approved = false;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void apiKeyTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_apiKeyTextFieldActionPerformed
        updateOK();
    }//GEN-LAST:event_apiKeyTextFieldActionPerformed

    private void getAPIKeyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getAPIKeyButtonActionPerformed
        try {
            Desktop.getDesktop().browse(new URI("https://www.deepl.com/de/pro#developer"));
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(WebLichtParameterDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_getAPIKeyButtonActionPerformed

    private void segmentChainRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentChainRadioButtonActionPerformed
        updateSegmentationPanel();
    }//GEN-LAST:event_segmentChainRadioButtonActionPerformed

    private void eventRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventRadioButtonActionPerformed
        updateSegmentationPanel();
    }//GEN-LAST:event_eventRadioButtonActionPerformed

    private void segmentCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentCheckBoxActionPerformed
        updateSegmentationPanel();
    }//GEN-LAST:event_segmentCheckBoxActionPerformed

    private void targetLanguageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetLanguageComboBoxActionPerformed
        String selectedTargetLanguage = ((String[])targetLanguageComboBox.getSelectedItem())[0];
        formalityPanel.setVisible(Arrays.asList(FORMALITY_SUPPORTED).contains(selectedTargetLanguage));
    }//GEN-LAST:event_targetLanguageComboBoxActionPerformed

    
    private void updateSegmentationPanel(){
        segmentCheckBox.setEnabled(segmentChainRadioButton.isSelected());
        segmentationComboBox.setEnabled(segmentChainRadioButton.isSelected() && segmentCheckBox.isSelected());        
    }
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
            java.util.logging.Logger.getLogger(DeepLParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DeepLParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DeepLParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DeepLParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DeepLParameterDialog dialog = new DeepLParameterDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JTextField apiKeyTextField;
    private javax.swing.ButtonGroup apiTypeButtonGroup;
    private javax.swing.JPanel apikeyPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton defaultFormalityRadioButton;
    private javax.swing.JCheckBox detectedLanguageCheckBox;
    private javax.swing.JPanel detectedLanguagePanel;
    private javax.swing.JRadioButton eventRadioButton;
    private javax.swing.ButtonGroup formalityButtonGroup;
    private javax.swing.JPanel formalityPanel;
    private javax.swing.JRadioButton freeAPIRadioButton;
    private javax.swing.JButton getAPIKeyButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JRadioButton lessFormalityRadioButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JRadioButton moreFormalityRadioButton;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okCancelPanel;
    private javax.swing.ButtonGroup outputButtonGroup;
    private javax.swing.JRadioButton proAPIRadioButton;
    private javax.swing.JRadioButton segmentChainRadioButton;
    private javax.swing.JCheckBox segmentCheckBox;
    private javax.swing.ButtonGroup segmentationButtonGroup;
    private javax.swing.JComboBox segmentationComboBox;
    private javax.swing.JPanel segmentationPanel;
    private javax.swing.JRadioButton selectedTierRadioButton;
    private javax.swing.ButtonGroup selectionButtonGroup;
    private javax.swing.JPanel selectionPanel;
    private javax.swing.JComboBox sourceLanguageComboBox;
    private javax.swing.JLabel sourceLanguageLabel;
    private javax.swing.JPanel sourceLanguagePanel;
    private javax.swing.JComboBox targetLanguageComboBox;
    private javax.swing.JLabel targetLanguageLabel;
    private javax.swing.JPanel targetLanguagePanel;
    private javax.swing.JComboBox<String> tierCategoriesComboBox;
    private javax.swing.JPanel tiersWithCategoryPanel;
    private javax.swing.JRadioButton tiersWithCategoryRadioButton;
    // End of variables declaration//GEN-END:variables

}
