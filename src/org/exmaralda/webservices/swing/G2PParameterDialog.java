/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices.swing;

import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;

/**
 *
 * @author Schmidt
 */
public class G2PParameterDialog extends javax.swing.JDialog {

     
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
        {"deu-DE","German (DE)"},
        {"gsw-CH-BE","German Dieth (CH), Bern dialect"},
        {"gsw-CH-BS","German Dieth (CH), Basel dialect"},
        {"gsw-CH-GR","German Dieth (CH), Graubunden dialect"},
        {"gsw-CH-SG","German Dieth (CH), St. Gallen dialect"},
        {"gsw-CH-ZH","German Dieth (CH), Zurich dialect"},
        {"gsw-CH","German Dieth (CH)"},

        {"eng-US","English (US)"},
        {"eng-AU","English (AU)"},
        {"eng-GB","English (GB)"},
        {"eng-NZ","English (NZ)"},

        {"fra-FR","French (FR)"},
        {"ita-IT","Italian (IT)"},
        {"spa-ES","Spanish (ES)"},

        {"aus-AU","Aboriginal Languages (AU)"},
        {"arb","Arabic (macro)"},
        {"cze-CZ","Czech (CZ)"},
        {"sqi-AL","Albanian (AL)"},
        {"eus-ES","Basque (ES)"},
        {"eus-FR","Basque (FR)"},
        {"cat-ES","Catalan (ES)"},
        {"nld-NL","Dutch (NL)"},
        {"ekk-EE","Estonian (EE)"},
        {"fin-FI","Finnish (FI)"},
        {"kat-GE","Georgian (GE)"},
        {"hat-HT","Haitian Creole (HT)"},
        {"hun-HU","Hungarian (HU)"},
        {"isl-IS","Icelandic (IS)"},
        {"jpn-JP","Japanese (JP)"},
        {"gup-AU","Kunwinjku, Western and Central Arnhem Land (AU)"},
        {"ltz-LU","Luxembourgish (LU)"},
        {"mlt-MT","Maltese (MT)"},
        {"nan-TW","Min Nan (TW)"},
        {"nor-NO","Norwegian (NO)"},
        {"fas-IR","Persian (IR)"},
        {"pol-PL","Polish (PL)"},
        {"ron-RO","Romanian (RO)"},
        {"rus-RU","Russian (RU)"},
        {"slk-SK","Slovak (SK)"},
        {"swe-SE","Swedish (SE)"},
        {"tha-TH","Thai (TH)"},
        {"guf-AU","Yolŋu Matha, Gupapuyngu, Eastern Arnhem Land (AU)"}

    };   
    
    String sampaExample = "[' { n]  [e g . z ' A: m . p @ l ]";
    String ipaExample = "[ˈ æ n]  [e ɡ . z ˈ ɑː m . p ə l]";
    String arpaExample = "[Q a1 NX]  [Q EH K . S a1 M . P L AX-H]";

    

    public boolean approved = false;
    
    /**
     * Creates new form MAUSParameterDialog
     * @param parent
     * @param modal
     */
    public G2PParameterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        sourceLanguageComboBox.setRenderer(new MAUSLanguagesComboBoxRenderer());        
        sourceLanguageComboBox.setSelectedIndex(0); // German
    }
    
    public HashMap<String, Object> getG2PParameters() {
        /*String[][] fixedParameters = {
            {"lng","eng"},
            {"outsym","x-sampa"},
            {"syl","yes"},
            {"stress","yes"}
        };*/

        HashMap<String, Object> result = new HashMap<>();
        
        result.put("lng", ((String[])sourceLanguageComboBox.getSelectedItem())[0]);
        result.put("outsym", ((String)symbolInventoryComboBox.getSelectedItem()).toLowerCase());
        if (syllabificationCheckBox.isSelected()){
            result.put("syl", "yes");
        } else {
            result.put("syl", "no");
        }
        if (wordStressCheckBox.isSelected()){
            result.put("stress", "yes");
        } else {
            result.put("stress", "no");
        }
        
        result.put("SELECTED-TIER", selectedTierRadioButton.isSelected());
        result.put("TIER-CATEGORY", tierCategoriesComboBox.getSelectedItem());
                
        result.put("EVENT-BY-EVENT", eventRadioButton.isSelected());        
        result.put("USE-SEGMENTATION", segmentCheckBox.isSelected());
        result.put("SEGMENTATION-ALGORITHM", segmentationComboBox.getSelectedItem());
        
        result.put("USE-SPACES", spacesCheckBox.isSelected());
        result.put("USE-BRACKETS", bracketsCheckBox.isSelected());
        
        
        return result;
    }
    
    public void setParameters(String apiKey, String segmentationAlgorithm, BasicTranscription transcription, int selectedRow){
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
        boolean parametersSuffice = true;                
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
        selectionButtonGroup = new javax.swing.ButtonGroup();
        mainPanel = new javax.swing.JPanel();
        sourceLanguagePanel = new javax.swing.JPanel();
        sourceLanguageLabel = new javax.swing.JLabel();
        sourceLanguageComboBox = new javax.swing.JComboBox();
        detectedLanguagePanel = new javax.swing.JPanel();
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
        outputOptionsPanel = new javax.swing.JPanel();
        examplePanel = new javax.swing.JPanel();
        exampleOrthLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        examplePhoLabel = new javax.swing.JLabel();
        symbolInventoryPanel = new javax.swing.JPanel();
        symbolInventoryLabel = new javax.swing.JLabel();
        symbolInventoryComboBox = new javax.swing.JComboBox<>();
        stressSyllabificationPanel = new javax.swing.JPanel();
        syllabificationCheckBox = new javax.swing.JCheckBox();
        wordStressCheckBox = new javax.swing.JCheckBox();
        PostProcessPanel = new javax.swing.JPanel();
        spacesCheckBox = new javax.swing.JCheckBox();
        bracketsCheckBox = new javax.swing.JCheckBox();
        okCancelPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("G2P Parameters");

        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

        sourceLanguagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Source Language"));
        sourceLanguagePanel.setLayout(new java.awt.BorderLayout());

        sourceLanguageLabel.setText("What is the source language?");
        sourceLanguagePanel.add(sourceLanguageLabel, java.awt.BorderLayout.NORTH);

        sourceLanguageComboBox.setModel(new javax.swing.DefaultComboBoxModel(SOURCE_LANGUAGES));
        sourceLanguagePanel.add(sourceLanguageComboBox, java.awt.BorderLayout.CENTER);
        sourceLanguagePanel.add(detectedLanguagePanel, java.awt.BorderLayout.SOUTH);

        mainPanel.add(sourceLanguagePanel);

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

        jLabel5.setText("Which text do you want to pass to G2P?");
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
        segmentCheckBox.setToolTipText("Not yet implemented!");
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

        jLabel2.setText("How do you want to pass text to G2P?");
        segmentationPanel.add(jLabel2, java.awt.BorderLayout.NORTH);

        mainPanel.add(segmentationPanel);

        outputOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Output options"));
        outputOptionsPanel.setLayout(new javax.swing.BoxLayout(outputOptionsPanel, javax.swing.BoxLayout.Y_AXIS));

        exampleOrthLabel.setBackground(new java.awt.Color(204, 204, 204));
        exampleOrthLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        exampleOrthLabel.setText("an example");
        exampleOrthLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        exampleOrthLabel.setOpaque(true);
        examplePanel.add(exampleOrthLabel);

        filler1.setBackground(java.awt.Color.lightGray);
        examplePanel.add(filler1);

        examplePhoLabel.setBackground(new java.awt.Color(255, 255, 255));
        examplePhoLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        examplePhoLabel.setText("[' { n] [e g . z ' A: m . p @ l ]");
        examplePhoLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        examplePhoLabel.setOpaque(true);
        examplePanel.add(examplePhoLabel);

        outputOptionsPanel.add(examplePanel);

        symbolInventoryLabel.setText("Output Symbol Inventory: ");
        symbolInventoryPanel.add(symbolInventoryLabel);

        symbolInventoryComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SAMPA", "IPA", "X-SAMPA", "MAUS-SAMPA", "ARPABET" }));
        symbolInventoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                symbolInventoryComboBoxActionPerformed(evt);
            }
        });
        symbolInventoryPanel.add(symbolInventoryComboBox);

        outputOptionsPanel.add(symbolInventoryPanel);

        syllabificationCheckBox.setSelected(true);
        syllabificationCheckBox.setText("Syllabification");
        syllabificationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syllabificationCheckBoxActionPerformed(evt);
            }
        });
        stressSyllabificationPanel.add(syllabificationCheckBox);

        wordStressCheckBox.setSelected(true);
        wordStressCheckBox.setText("Word stress");
        wordStressCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordStressCheckBoxActionPerformed(evt);
            }
        });
        stressSyllabificationPanel.add(wordStressCheckBox);

        outputOptionsPanel.add(stressSyllabificationPanel);

        spacesCheckBox.setSelected(true);
        spacesCheckBox.setText("Spaces between phonemes");
        spacesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spacesCheckBoxActionPerformed(evt);
            }
        });
        PostProcessPanel.add(spacesCheckBox);

        bracketsCheckBox.setSelected(true);
        bracketsCheckBox.setText("Square brackets around words");
        bracketsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bracketsCheckBoxActionPerformed(evt);
            }
        });
        PostProcessPanel.add(bracketsCheckBox);

        outputOptionsPanel.add(PostProcessPanel);

        mainPanel.add(outputOptionsPanel);

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

    private void segmentChainRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentChainRadioButtonActionPerformed
        updateSegmentationPanel();
    }//GEN-LAST:event_segmentChainRadioButtonActionPerformed

    private void eventRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventRadioButtonActionPerformed
        updateSegmentationPanel();
    }//GEN-LAST:event_eventRadioButtonActionPerformed

    private void segmentCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentCheckBoxActionPerformed
        updateSegmentationPanel();
    }//GEN-LAST:event_segmentCheckBoxActionPerformed

    private void symbolInventoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_symbolInventoryComboBoxActionPerformed
        outputOptionsChanged();
    }//GEN-LAST:event_symbolInventoryComboBoxActionPerformed

    private void syllabificationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syllabificationCheckBoxActionPerformed
        outputOptionsChanged();
    }//GEN-LAST:event_syllabificationCheckBoxActionPerformed

    private void wordStressCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordStressCheckBoxActionPerformed
        outputOptionsChanged();
    }//GEN-LAST:event_wordStressCheckBoxActionPerformed

    private void spacesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spacesCheckBoxActionPerformed
        outputOptionsChanged();
    }//GEN-LAST:event_spacesCheckBoxActionPerformed

    private void bracketsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bracketsCheckBoxActionPerformed
        outputOptionsChanged();
    }//GEN-LAST:event_bracketsCheckBoxActionPerformed

    
    private void updateSegmentationPanel(){
        // need to uncomment the following two lines when and iff segmentation is implemented
        //segmentCheckBox.setEnabled(segmentChainRadioButton.isSelected());
        //segmentationComboBox.setEnabled(segmentChainRadioButton.isSelected() && segmentCheckBox.isSelected());        
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
            java.util.logging.Logger.getLogger(G2PParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(G2PParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(G2PParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(G2PParameterDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                G2PParameterDialog dialog = new G2PParameterDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JPanel PostProcessPanel;
    private javax.swing.JCheckBox bracketsCheckBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel detectedLanguagePanel;
    private javax.swing.JRadioButton eventRadioButton;
    private javax.swing.JLabel exampleOrthLabel;
    private javax.swing.JPanel examplePanel;
    private javax.swing.JLabel examplePhoLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okCancelPanel;
    private javax.swing.JPanel outputOptionsPanel;
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
    private javax.swing.JCheckBox spacesCheckBox;
    private javax.swing.JPanel stressSyllabificationPanel;
    private javax.swing.JCheckBox syllabificationCheckBox;
    private javax.swing.JComboBox<String> symbolInventoryComboBox;
    private javax.swing.JLabel symbolInventoryLabel;
    private javax.swing.JPanel symbolInventoryPanel;
    private javax.swing.JComboBox<String> tierCategoriesComboBox;
    private javax.swing.JPanel tiersWithCategoryPanel;
    private javax.swing.JRadioButton tiersWithCategoryRadioButton;
    private javax.swing.JCheckBox wordStressCheckBox;
    // End of variables declaration//GEN-END:variables

    private void outputOptionsChanged() {
        String startString = sampaExample;
        if (symbolInventoryComboBox.getSelectedIndex()==1){
            startString = ipaExample;
        }
        if (symbolInventoryComboBox.getSelectedIndex()==4){
            startString = arpaExample;
        }
        if (!wordStressCheckBox.isSelected()){
            startString = startString.replace("'", "");
            startString = startString.replace("ˈ", "");            
        }
        if (!syllabificationCheckBox.isSelected()){
            startString = startString.replace(".", "");
        }
        if (!bracketsCheckBox.isSelected()){
            startString = startString.replace("[", "");
            startString = startString.replace("]", "");
        }
        if (!spacesCheckBox.isSelected()){
            startString = startString.replaceAll("  ", "_");
            startString = startString.replace(" ", "");
            startString = startString.replaceAll("_", "  ");
        }
        examplePhoLabel.setText(startString);
        
    }

}
