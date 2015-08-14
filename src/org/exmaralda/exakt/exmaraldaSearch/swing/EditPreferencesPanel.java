/*
 * EditPreferencesPanel.java
 *
 * Created on 19. Februar 2007, 09:53
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.util.prefs.*;
import javax.swing.JFileChooser;
import java.io.*;

/**
 *
 * @author  thomas
 */
public class EditPreferencesPanel extends javax.swing.JPanel {
    
    private String partiturInToolStylesheet;
    private String segmentedXSLStylesheet;
    private String partiturOutputStylesheet;
    private String concordanceOutputStylesheet;
    private String kwicTableFont;
    private int kwicTableFontSize;
    private int maxSearchResults;
    private int kwicContextLimit;
    private int fullDisplayContextLimit;
    
    private String[] FONTS;
    
    /** Creates new form EditPreferencesPanel */
    public EditPreferencesPanel() {
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        FONTS = ge.getAvailableFontFamilyNames();
        initComponents();
        maxSearchResultsPanel.add(javax.swing.Box.createHorizontalGlue());
        KWICContextLimitPanel.add(javax.swing.Box.createHorizontalGlue());
        fullDisplayContextLimitPanel.add(javax.swing.Box.createHorizontalGlue());
        readSettings();
    }
    
    private void readSettings(){
        Preferences prefs = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT");
        partiturInToolStylesheet = prefs.get("xsl-partitur-tool", "");
        partiturInToolStylesheetTextField.setText(partiturInToolStylesheet);        
        
        partiturOutputStylesheet = prefs.get("xsl-partitur-output", "");
        partiturOutputStylesheetTextField.setText(partiturOutputStylesheet);
        
        concordanceOutputStylesheet = prefs.get("xsl-concordance-output", "");
        concordanceOutputStylesheetTextField.setText(concordanceOutputStylesheet);        
        
        segmentedXSLStylesheet = prefs.get("xsl-segmented-tool", "");
        segmentedOutputStylesheetTextField.setText(segmentedXSLStylesheet);
        
        kwicTableFont = prefs.get("kwic-table-font-name", "Times New Roman");
        kwicTableFontComboBox.setSelectedItem(kwicTableFont);
        
        kwicTableFontSize = prefs.getInt("kwic-table-font-size", 10);
        kwicTableFontSizeSpinner.setValue(kwicTableFontSize);
        
        maxSearchResults = prefs.getInt("max-search-results", 10000);
        if (maxSearchResults==-1){
            maxSearchResultsCheckBox.setSelected(true);
            maxSearchResultsSpinner.setValue(10000);
            maxSearchResultsSpinner.setEnabled(false);
        } else {
            maxSearchResultsSpinner.setValue(maxSearchResults);        
        }

        kwicContextLimit = prefs.getInt("kwic-context-limit", 100);;
        if (kwicContextLimit==-1){
            kwicContextLimitCheckBox.setSelected(true);
            kwicContextLimitSpinner.setValue(100);
            kwicContextLimitSpinner.setEnabled(false);
        } else {
            kwicContextLimitSpinner.setValue(kwicContextLimit);
        }

        fullDisplayContextLimit = prefs.getInt("full-display-limit", 50);;
        if (fullDisplayContextLimit==-1){
            fullDisplayContextLimitCheckBox.setSelected(true);
            fullDisplayContextLimitSpinner.setValue(100);
            fullDisplayContextLimitSpinner.setEnabled(false);
        } else {
            fullDisplayContextLimitSpinner.setValue(fullDisplayContextLimit);
        }
                
    }

    public int getKwicTableFontSize() {
        return ((Integer)(kwicTableFontSizeSpinner.getValue())).intValue();
    }
    
    public String getPartiturInToolStylesheet() {
        return partiturInToolStylesheetTextField.getText();
    }

    public void setPartiturInToolStylesheet(String partiturInToolStylesheet) {
        this.partiturInToolStylesheet = partiturInToolStylesheet;
    }

    public String getPartiturOutputStylesheet() {
        return partiturOutputStylesheetTextField.getText();
    }

    public void setPartiturOutputStylesheet(String partiturOutputStylesheet) {
        this.partiturOutputStylesheet = partiturOutputStylesheet;
    }

    public String getConcordanceOutputStylesheet() {
        return concordanceOutputStylesheetTextField.getText();
    }

    public String getSegmentedOutputStylesheet() {
        return segmentedOutputStylesheetTextField.getText();
    }

    public void setConcordanceOutputStylesheet(String concordanceOutputStylesheet) {
        this.concordanceOutputStylesheet = concordanceOutputStylesheet;
    }
    
    public String browseForStylesheet(String startDirectory){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(startDirectory));
        fileChooser.setDialogTitle("Choose a stylesheet");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                String name = f.getAbsolutePath();
                return (f.isDirectory() || name.substring(Math.max(0,name.length()-3)).equalsIgnoreCase("XSL"));
            }
            public String getDescription() {
                return "XSL files (*.xsl)";
            }
        });
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue==JFileChooser.APPROVE_OPTION){
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    public String getKwicTableFont() {
        return (String)(kwicTableFontComboBox.getSelectedItem());
    }

    public int getMaxSearchResults() {
        if (maxSearchResultsCheckBox.isSelected()) return -1;
        return ((Integer)(maxSearchResultsSpinner.getValue())).intValue();
    }
    public int getKWICContextLimit() {
        if (kwicContextLimitCheckBox.isSelected()) return -1;
        return ((Integer)(kwicContextLimitSpinner.getValue())).intValue();
    }
    public int getFullDisplayContextLimit() {
        if (fullDisplayContextLimitCheckBox.isSelected()) return -1;
        return ((Integer)(fullDisplayContextLimitSpinner.getValue())).intValue();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        stylesheetsPanel = new javax.swing.JPanel();
        partiturInToolStylesheetPanel = new javax.swing.JPanel();
        partiturInToolSSLabel = new javax.swing.JLabel();
        partiturInToolStylesheetTextField = new javax.swing.JTextField();
        partiturInToolStylesheetButton = new javax.swing.JButton();
        segmentedXSLPanel = new javax.swing.JPanel();
        segmentedOutputSSLabel = new javax.swing.JLabel();
        segmentedOutputStylesheetTextField = new javax.swing.JTextField();
        segmentedOutputStylesheetButton = new javax.swing.JButton();
        partiturOutputStylesheetPanel = new javax.swing.JPanel();
        partiturOutputSSLabel = new javax.swing.JLabel();
        partiturOutputStylesheetTextField = new javax.swing.JTextField();
        partiturOutputStylesheetButton = new javax.swing.JButton();
        concordanceOutputStylesheetPanel = new javax.swing.JPanel();
        concordanceOutputSSLabel = new javax.swing.JLabel();
        concordanceOutputStylesheetTextField = new javax.swing.JTextField();
        concordanceOutputStylesheetButton = new javax.swing.JButton();
        fontsPanel = new javax.swing.JPanel();
        kwicTableFontPanel = new javax.swing.JPanel();
        kwicTableFontLabel = new javax.swing.JLabel();
        kwicTableFontComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        kwicTableFontSizeSpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        performancePanel = new javax.swing.JPanel();
        maxSearchResultsPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        maxSearchResultsSpinner = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        maxSearchResultsCheckBox = new javax.swing.JCheckBox();
        KWICContextLimitPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        kwicContextLimitSpinner = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        kwicContextLimitCheckBox = new javax.swing.JCheckBox();
        fullDisplayContextLimitPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        fullDisplayContextLimitSpinner = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        fullDisplayContextLimitCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        stylesheetsPanel.setLayout(new javax.swing.BoxLayout(stylesheetsPanel, javax.swing.BoxLayout.Y_AXIS));

        partiturInToolStylesheetPanel.setLayout(new javax.swing.BoxLayout(partiturInToolStylesheetPanel, javax.swing.BoxLayout.LINE_AXIS));

        partiturInToolSSLabel.setText("Partitur display in EXAKT: ");
        partiturInToolSSLabel.setMaximumSize(new java.awt.Dimension(150, 14));
        partiturInToolSSLabel.setMinimumSize(new java.awt.Dimension(130, 14));
        partiturInToolSSLabel.setPreferredSize(new java.awt.Dimension(150, 14));
        partiturInToolStylesheetPanel.add(partiturInToolSSLabel);

        partiturInToolStylesheetTextField.setMaximumSize(new java.awt.Dimension(700, 25));
        partiturInToolStylesheetTextField.setPreferredSize(new java.awt.Dimension(400, 25));
        partiturInToolStylesheetPanel.add(partiturInToolStylesheetTextField);

        partiturInToolStylesheetButton.setText("Browse...");
        partiturInToolStylesheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                partiturInToolStylesheetButtonActionPerformed(evt);
            }
        });
        partiturInToolStylesheetPanel.add(partiturInToolStylesheetButton);

        stylesheetsPanel.add(partiturInToolStylesheetPanel);

        segmentedXSLPanel.setLayout(new javax.swing.BoxLayout(segmentedXSLPanel, javax.swing.BoxLayout.LINE_AXIS));

        segmentedOutputSSLabel.setText("HTML display in EXAKT: ");
        segmentedOutputSSLabel.setMaximumSize(new java.awt.Dimension(150, 14));
        segmentedOutputSSLabel.setMinimumSize(new java.awt.Dimension(130, 14));
        segmentedOutputSSLabel.setPreferredSize(new java.awt.Dimension(150, 14));
        segmentedXSLPanel.add(segmentedOutputSSLabel);

        segmentedOutputStylesheetTextField.setMaximumSize(new java.awt.Dimension(700, 25));
        segmentedOutputStylesheetTextField.setPreferredSize(new java.awt.Dimension(400, 25));
        segmentedXSLPanel.add(segmentedOutputStylesheetTextField);

        segmentedOutputStylesheetButton.setText("Browse...");
        segmentedOutputStylesheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segmentedOutputStylesheetButtonActionPerformed(evt);
            }
        });
        segmentedXSLPanel.add(segmentedOutputStylesheetButton);

        stylesheetsPanel.add(segmentedXSLPanel);

        partiturOutputStylesheetPanel.setLayout(new javax.swing.BoxLayout(partiturOutputStylesheetPanel, javax.swing.BoxLayout.LINE_AXIS));

        partiturOutputSSLabel.setText("Partitur output: ");
        partiturOutputSSLabel.setMaximumSize(new java.awt.Dimension(150, 14));
        partiturOutputSSLabel.setMinimumSize(new java.awt.Dimension(130, 14));
        partiturOutputSSLabel.setPreferredSize(new java.awt.Dimension(150, 14));
        partiturOutputStylesheetPanel.add(partiturOutputSSLabel);

        partiturOutputStylesheetTextField.setMaximumSize(new java.awt.Dimension(700, 25));
        partiturOutputStylesheetTextField.setPreferredSize(new java.awt.Dimension(400, 25));
        partiturOutputStylesheetPanel.add(partiturOutputStylesheetTextField);

        partiturOutputStylesheetButton.setText("Browse...");
        partiturOutputStylesheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                partiturOutputStylesheetButtonActionPerformed(evt);
            }
        });
        partiturOutputStylesheetPanel.add(partiturOutputStylesheetButton);

        stylesheetsPanel.add(partiturOutputStylesheetPanel);

        concordanceOutputStylesheetPanel.setLayout(new javax.swing.BoxLayout(concordanceOutputStylesheetPanel, javax.swing.BoxLayout.LINE_AXIS));

        concordanceOutputSSLabel.setText("Concordance output: ");
        concordanceOutputSSLabel.setMaximumSize(new java.awt.Dimension(150, 14));
        concordanceOutputSSLabel.setMinimumSize(new java.awt.Dimension(130, 14));
        concordanceOutputSSLabel.setPreferredSize(new java.awt.Dimension(150, 14));
        concordanceOutputStylesheetPanel.add(concordanceOutputSSLabel);

        concordanceOutputStylesheetTextField.setMaximumSize(new java.awt.Dimension(700, 25));
        concordanceOutputStylesheetTextField.setPreferredSize(new java.awt.Dimension(400, 25));
        concordanceOutputStylesheetPanel.add(concordanceOutputStylesheetTextField);

        concordanceOutputStylesheetButton.setText("Browse...");
        concordanceOutputStylesheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                concordanceOutputStylesheetButtonActionPerformed(evt);
            }
        });
        concordanceOutputStylesheetButton.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                concordanceOutputStylesheetButtonAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        concordanceOutputStylesheetPanel.add(concordanceOutputStylesheetButton);

        stylesheetsPanel.add(concordanceOutputStylesheetPanel);

        tabbedPane.addTab("Stylesheets", stylesheetsPanel);

        fontsPanel.setLayout(new javax.swing.BoxLayout(fontsPanel, javax.swing.BoxLayout.LINE_AXIS));

        kwicTableFontPanel.setLayout(new javax.swing.BoxLayout(kwicTableFontPanel, javax.swing.BoxLayout.LINE_AXIS));

        kwicTableFontLabel.setText("KWIC Table: ");
        kwicTableFontPanel.add(kwicTableFontLabel);

        kwicTableFontComboBox.setModel(new javax.swing.DefaultComboBoxModel(FONTS));
        kwicTableFontComboBox.setMaximumSize(new java.awt.Dimension(700, 22));
        kwicTableFontComboBox.setMinimumSize(new java.awt.Dimension(100, 22));
        kwicTableFontComboBox.setPreferredSize(new java.awt.Dimension(300, 22));
        kwicTableFontPanel.add(kwicTableFontComboBox);

        jLabel2.setMaximumSize(new java.awt.Dimension(30, 1));
        jLabel2.setMinimumSize(new java.awt.Dimension(30, 1));
        jLabel2.setPreferredSize(new java.awt.Dimension(30, 1));
        kwicTableFontPanel.add(jLabel2);

        kwicTableFontSizeSpinner.setMaximumSize(new java.awt.Dimension(70, 22));
        kwicTableFontSizeSpinner.setMinimumSize(new java.awt.Dimension(30, 22));
        kwicTableFontSizeSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
        kwicTableFontPanel.add(kwicTableFontSizeSpinner);

        jLabel1.setText("pt");
        kwicTableFontPanel.add(jLabel1);

        fontsPanel.add(kwicTableFontPanel);

        tabbedPane.addTab("Fonts", fontsPanel);

        performancePanel.setMaximumSize(new java.awt.Dimension(500, 70));
        performancePanel.setPreferredSize(new java.awt.Dimension(500, 70));
        performancePanel.setLayout(new javax.swing.BoxLayout(performancePanel, javax.swing.BoxLayout.Y_AXIS));

        maxSearchResultsPanel.setMaximumSize(new java.awt.Dimension(500, 18));
        maxSearchResultsPanel.setPreferredSize(new java.awt.Dimension(500, 18));
        maxSearchResultsPanel.setLayout(new javax.swing.BoxLayout(maxSearchResultsPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setText("Maximum number of search results: ");
        jLabel3.setMaximumSize(new java.awt.Dimension(190, 14));
        jLabel3.setPreferredSize(new java.awt.Dimension(190, 14));
        maxSearchResultsPanel.add(jLabel3);

        maxSearchResultsSpinner.setMaximumSize(new java.awt.Dimension(100, 18));
        maxSearchResultsSpinner.setPreferredSize(new java.awt.Dimension(100, 18));
        maxSearchResultsPanel.add(maxSearchResultsSpinner);

        jLabel5.setText(" results       ");
        jLabel5.setMaximumSize(new java.awt.Dimension(100, 14));
        jLabel5.setMinimumSize(new java.awt.Dimension(100, 14));
        jLabel5.setPreferredSize(new java.awt.Dimension(100, 14));
        maxSearchResultsPanel.add(jLabel5);

        maxSearchResultsCheckBox.setText("Unlimited");
        maxSearchResultsCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        maxSearchResultsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        maxSearchResultsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxSearchResultsCheckBoxActionPerformed(evt);
            }
        });
        maxSearchResultsPanel.add(maxSearchResultsCheckBox);

        performancePanel.add(maxSearchResultsPanel);

        KWICContextLimitPanel.setMaximumSize(new java.awt.Dimension(500, 18));
        KWICContextLimitPanel.setPreferredSize(new java.awt.Dimension(500, 18));
        KWICContextLimitPanel.setLayout(new javax.swing.BoxLayout(KWICContextLimitPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel4.setText("KWIC context limit: ");
        jLabel4.setMaximumSize(new java.awt.Dimension(190, 14));
        jLabel4.setMinimumSize(new java.awt.Dimension(173, 14));
        jLabel4.setPreferredSize(new java.awt.Dimension(190, 14));
        KWICContextLimitPanel.add(jLabel4);

        kwicContextLimitSpinner.setMaximumSize(new java.awt.Dimension(100, 18));
        kwicContextLimitSpinner.setPreferredSize(new java.awt.Dimension(100, 18));
        KWICContextLimitPanel.add(kwicContextLimitSpinner);

        jLabel6.setText(" characters       ");
        jLabel6.setMaximumSize(new java.awt.Dimension(100, 14));
        jLabel6.setMinimumSize(new java.awt.Dimension(100, 14));
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 14));
        KWICContextLimitPanel.add(jLabel6);

        kwicContextLimitCheckBox.setText("Unlimited");
        kwicContextLimitCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        kwicContextLimitCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kwicContextLimitCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kwicContextLimitCheckBoxActionPerformed(evt);
            }
        });
        KWICContextLimitPanel.add(kwicContextLimitCheckBox);

        performancePanel.add(KWICContextLimitPanel);

        fullDisplayContextLimitPanel.setMaximumSize(new java.awt.Dimension(500, 18));
        fullDisplayContextLimitPanel.setPreferredSize(new java.awt.Dimension(500, 18));
        fullDisplayContextLimitPanel.setLayout(new javax.swing.BoxLayout(fullDisplayContextLimitPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel7.setText("Full display context limit: ");
        jLabel7.setMaximumSize(new java.awt.Dimension(190, 14));
        jLabel7.setMinimumSize(new java.awt.Dimension(173, 14));
        jLabel7.setPreferredSize(new java.awt.Dimension(190, 14));
        fullDisplayContextLimitPanel.add(jLabel7);

        fullDisplayContextLimitSpinner.setMaximumSize(new java.awt.Dimension(100, 18));
        fullDisplayContextLimitSpinner.setPreferredSize(new java.awt.Dimension(100, 18));
        fullDisplayContextLimitPanel.add(fullDisplayContextLimitSpinner);

        jLabel8.setText(" timeline items       ");
        jLabel8.setMaximumSize(new java.awt.Dimension(100, 14));
        jLabel8.setMinimumSize(new java.awt.Dimension(100, 14));
        jLabel8.setPreferredSize(new java.awt.Dimension(100, 14));
        fullDisplayContextLimitPanel.add(jLabel8);

        fullDisplayContextLimitCheckBox.setText("Unlimited");
        fullDisplayContextLimitCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fullDisplayContextLimitCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fullDisplayContextLimitCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullDisplayContextLimitCheckBoxActionPerformed(evt);
            }
        });
        fullDisplayContextLimitPanel.add(fullDisplayContextLimitCheckBox);

        performancePanel.add(fullDisplayContextLimitPanel);

        tabbedPane.addTab("Performance", null, performancePanel, "Set size limitations to improve performance");

        add(tabbedPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void fullDisplayContextLimitCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullDisplayContextLimitCheckBoxActionPerformed
    fullDisplayContextLimitSpinner.setEnabled(!fullDisplayContextLimitCheckBox.isSelected());
    }//GEN-LAST:event_fullDisplayContextLimitCheckBoxActionPerformed

    private void kwicContextLimitCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kwicContextLimitCheckBoxActionPerformed
    kwicContextLimitSpinner.setEnabled(!kwicContextLimitCheckBox.isSelected());
    }//GEN-LAST:event_kwicContextLimitCheckBoxActionPerformed

    private void maxSearchResultsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxSearchResultsCheckBoxActionPerformed
    maxSearchResultsSpinner.setEnabled(!maxSearchResultsCheckBox.isSelected());
    }//GEN-LAST:event_maxSearchResultsCheckBoxActionPerformed

    private void segmentedOutputStylesheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentedOutputStylesheetButtonActionPerformed
        String newXSL = browseForStylesheet(getConcordanceOutputStylesheet());
        if (newXSL!=null) segmentedOutputStylesheetTextField.setText(newXSL);
    }//GEN-LAST:event_segmentedOutputStylesheetButtonActionPerformed

    private void concordanceOutputStylesheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_concordanceOutputStylesheetButtonActionPerformed
        String newXSL = browseForStylesheet(getConcordanceOutputStylesheet());
        if (newXSL!=null) concordanceOutputStylesheetTextField.setText(newXSL);
    }//GEN-LAST:event_concordanceOutputStylesheetButtonActionPerformed

    private void concordanceOutputStylesheetButtonAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_concordanceOutputStylesheetButtonAncestorAdded
// TODO add your handling code here:
    }//GEN-LAST:event_concordanceOutputStylesheetButtonAncestorAdded

    private void partiturOutputStylesheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_partiturOutputStylesheetButtonActionPerformed
        String newXSL = browseForStylesheet(getPartiturOutputStylesheet());
        if (newXSL!=null) partiturOutputStylesheetTextField.setText(newXSL);
    }//GEN-LAST:event_partiturOutputStylesheetButtonActionPerformed

    private void partiturInToolStylesheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_partiturInToolStylesheetButtonActionPerformed
        String newXSL = browseForStylesheet(getPartiturInToolStylesheet());
        if (newXSL!=null) partiturInToolStylesheetTextField.setText(newXSL);
    }//GEN-LAST:event_partiturInToolStylesheetButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel KWICContextLimitPanel;
    private javax.swing.JLabel concordanceOutputSSLabel;
    private javax.swing.JButton concordanceOutputStylesheetButton;
    private javax.swing.JPanel concordanceOutputStylesheetPanel;
    private javax.swing.JTextField concordanceOutputStylesheetTextField;
    private javax.swing.JPanel fontsPanel;
    private javax.swing.JCheckBox fullDisplayContextLimitCheckBox;
    private javax.swing.JPanel fullDisplayContextLimitPanel;
    private javax.swing.JSpinner fullDisplayContextLimitSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JCheckBox kwicContextLimitCheckBox;
    private javax.swing.JSpinner kwicContextLimitSpinner;
    private javax.swing.JComboBox kwicTableFontComboBox;
    private javax.swing.JLabel kwicTableFontLabel;
    private javax.swing.JPanel kwicTableFontPanel;
    private javax.swing.JSpinner kwicTableFontSizeSpinner;
    private javax.swing.JCheckBox maxSearchResultsCheckBox;
    private javax.swing.JPanel maxSearchResultsPanel;
    private javax.swing.JSpinner maxSearchResultsSpinner;
    private javax.swing.JLabel partiturInToolSSLabel;
    private javax.swing.JButton partiturInToolStylesheetButton;
    private javax.swing.JPanel partiturInToolStylesheetPanel;
    private javax.swing.JTextField partiturInToolStylesheetTextField;
    private javax.swing.JLabel partiturOutputSSLabel;
    private javax.swing.JButton partiturOutputStylesheetButton;
    private javax.swing.JPanel partiturOutputStylesheetPanel;
    private javax.swing.JTextField partiturOutputStylesheetTextField;
    private javax.swing.JPanel performancePanel;
    private javax.swing.JLabel segmentedOutputSSLabel;
    private javax.swing.JButton segmentedOutputStylesheetButton;
    private javax.swing.JTextField segmentedOutputStylesheetTextField;
    private javax.swing.JPanel segmentedXSLPanel;
    private javax.swing.JPanel stylesheetsPanel;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
    
}
