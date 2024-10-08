/*
 * SpeakerTableDialog.java
 *
 * Created on 6. August 2001, 12:36
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

import org.exmaralda.common.helpers.Internationalizer;
import java.util.*;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class EditSpeakerTableDialog extends JEscapeDialog {

    private static final String[] sex = {"(unknown / n.a.)","female","male"};
    private org.exmaralda.partitureditor.jexmaralda.Speakertable speakertable;
    private javax.swing.DefaultListModel speakerListModel;
    //private UDInformationTableModel tableModel;
    private org.exmaralda.partitureditor.udInformationTable.UDInformationPanel tablePanel;
    private org.exmaralda.partitureditor.jexmaralda.Speaker currentSpeaker;
    private javax.swing.JButton collectAttButton;
    private javax.swing.JButton templateButton;
    private Vector speakerIDs = new Vector();
    
    /** Creates new form SpeakerTableDialog
     * @param parent
     * @param modal
     * @param st */
    public EditSpeakerTableDialog(java.awt.Frame parent,boolean modal,org.exmaralda.partitureditor.jexmaralda.Speakertable st) {
        super (parent, modal);
        collectAttButton = new javax.swing.JButton();
        collectAttButton.setPreferredSize (new java.awt.Dimension(170, 27));
        collectAttButton.setMaximumSize (new java.awt.Dimension(170, 27));
        collectAttButton.setMinimumSize (new java.awt.Dimension(150, 27));
        collectAttButton.setText ("Collect attributes");
        collectAttButton.addActionListener (new java.awt.event.ActionListener () {
            @Override
            public void actionPerformed (java.awt.event.ActionEvent evt) {
                collectAttButtonActionPerformed (evt);
            }
        }
        );        
        templateButton = new javax.swing.JButton();
        templateButton.setPreferredSize (new java.awt.Dimension(170, 27));
        templateButton.setMaximumSize (new java.awt.Dimension(170, 27));
        templateButton.setMinimumSize (new java.awt.Dimension(150, 27));
        templateButton.setText ("Template...");
        templateButton.addActionListener (new java.awt.event.ActionListener () {
            @Override
            public void actionPerformed (java.awt.event.ActionEvent evt) {
                templateButtonActionPerformed (evt);
            }
        }
        );        
        tablePanel = new org.exmaralda.partitureditor.udInformationTable.UDInformationPanel();
        change=false;
        speakertable = st.makeCopy();
        speakerListModel = new javax.swing.DefaultListModel();
        String[] ids = speakertable.getAllSpeakerIDs();
        for (int pos=0; pos<ids.length; pos++){
            try{
                String listEntry = ids[pos] + " [" + speakertable.getSpeakerWithID(ids[pos]).getAbbreviation() + "]";
                speakerListModel.addElement(listEntry);
                speakerIDs.addElement(ids[pos]);
            } catch (org.exmaralda.partitureditor.jexmaralda.JexmaraldaException je){};
        }
        initComponents ();
        this.getRootPane().setDefaultButton(okButton);
        tablePanel.getButtonPanel().add(collectAttButton);
        tablePanel.getButtonPanel().add(templateButton);
        propertiesPanel.add(tablePanel);
        pack ();
        speakerList.setSelectedIndex(0);
        Internationalizer.internationalizeDialogToolTips(this);
    }

    public org.exmaralda.partitureditor.jexmaralda.Speakertable getSpeakertable(){
        return speakertable;
    }
    
    public boolean getAutoAdd(){
        return autoAddSpeakerTiersCheckBox.isSelected();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        speakersPanel = new javax.swing.JPanel();
        speakerListScrollPane = new javax.swing.JScrollPane();
        speakerList = new javax.swing.JList(speakerListModel);
        speakersButtonPanel = new javax.swing.JPanel();
        addSpeakerButton = new javax.swing.JButton();
        addSpeakerButton.setMnemonic('A');
        removeSpeakerButton = new javax.swing.JButton();
        removeSpeakerButton.setMnemonic('R');
        autoAddSpeakerTiersCheckBox = new javax.swing.JCheckBox();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        okButton.setMnemonic('O');
        cancelButton = new javax.swing.JButton();
        cancelButton.setMnemonic('C');
        propertiesPanel = new javax.swing.JPanel();
        fixedAttributesPanel = new javax.swing.JPanel();
        abbreviationPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        abbreviationTextField = new javax.swing.JTextField();
        sexPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        sexComboBox = new javax.swing.JComboBox(sex);
        languagesPanel = new javax.swing.JPanel();
        lUsedPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        luLabel = new javax.swing.JLabel();
        l1Panel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        l1Label = new javax.swing.JLabel();
        l2Panel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        l2Label = new javax.swing.JLabel();
        languageButtonPanel = new javax.swing.JPanel();
        editLangButton = new javax.swing.JButton();
        editLangButton.setMnemonic('E');
        commentScrollPane = new javax.swing.JScrollPane();
        commentTextArea = new javax.swing.JTextArea();

        setTitle("Edit speakertable");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        speakersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Speakers"));
        speakersPanel.setToolTipText("");
        speakersPanel.setMaximumSize(new java.awt.Dimension(600, 200));
        speakersPanel.setMinimumSize(new java.awt.Dimension(200, 110));
        speakersPanel.setPreferredSize(new java.awt.Dimension(250, 110));
        speakersPanel.setLayout(new java.awt.BorderLayout());

        speakerListScrollPane.setMaximumSize(new java.awt.Dimension(400, 200));
        speakerListScrollPane.setMinimumSize(new java.awt.Dimension(110, 110));
        speakerListScrollPane.setPreferredSize(new java.awt.Dimension(200, 110));

        speakerList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        speakerList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                speakerListValueChanged(evt);
            }
        });
        speakerListScrollPane.setViewportView(speakerList);

        speakersPanel.add(speakerListScrollPane, java.awt.BorderLayout.CENTER);

        speakersButtonPanel.setMaximumSize(new java.awt.Dimension(200, 200));
        speakersButtonPanel.setMinimumSize(new java.awt.Dimension(160, 60));
        speakersButtonPanel.setPreferredSize(new java.awt.Dimension(160, 80));
        speakersButtonPanel.setLayout(new javax.swing.BoxLayout(speakersButtonPanel, javax.swing.BoxLayout.Y_AXIS));

        addSpeakerButton.setText("Add speaker");
        addSpeakerButton.setToolTipText("");
        addSpeakerButton.setMaximumSize(new java.awt.Dimension(160, 27));
        addSpeakerButton.setMinimumSize(new java.awt.Dimension(160, 27));
        addSpeakerButton.setPreferredSize(new java.awt.Dimension(160, 27));
        addSpeakerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSpeakerButtonActionPerformed(evt);
            }
        });
        speakersButtonPanel.add(addSpeakerButton);

        removeSpeakerButton.setText("Remove speaker");
        removeSpeakerButton.setToolTipText("");
        removeSpeakerButton.setMaximumSize(new java.awt.Dimension(160, 27));
        removeSpeakerButton.setMinimumSize(new java.awt.Dimension(160, 27));
        removeSpeakerButton.setPreferredSize(new java.awt.Dimension(160, 27));
        removeSpeakerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSpeakerButtonActionPerformed(evt);
            }
        });
        speakersButtonPanel.add(removeSpeakerButton);

        speakersPanel.add(speakersButtonPanel, java.awt.BorderLayout.SOUTH);

        autoAddSpeakerTiersCheckBox.setSelected(true);
        autoAddSpeakerTiersCheckBox.setText("Auto add one T tier for new speakers");
        speakersPanel.add(autoAddSpeakerTiersCheckBox, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(speakersPanel, java.awt.BorderLayout.WEST);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.setMaximumSize(new java.awt.Dimension(600, 40));
        buttonPanel.setPreferredSize(new java.awt.Dimension(360, 40));

        okButton.setText("OK");
        okButton.setMaximumSize(new java.awt.Dimension(110, 27));
        okButton.setMinimumSize(new java.awt.Dimension(80, 27));
        okButton.setPreferredSize(new java.awt.Dimension(110, 27));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.setMaximumSize(new java.awt.Dimension(110, 27));
        cancelButton.setMinimumSize(new java.awt.Dimension(80, 27));
        cancelButton.setPreferredSize(new java.awt.Dimension(110, 27));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        propertiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Speaker properties"));
        propertiesPanel.setToolTipText("");
        propertiesPanel.setMinimumSize(new java.awt.Dimension(332, 420));
        propertiesPanel.setLayout(new javax.swing.BoxLayout(propertiesPanel, javax.swing.BoxLayout.Y_AXIS));

        fixedAttributesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Fixed attributes"));
        fixedAttributesPanel.setToolTipText("");
        fixedAttributesPanel.setLayout(new java.awt.GridLayout(2, 1));

        abbreviationPanel.setMaximumSize(new java.awt.Dimension(600, 30));
        abbreviationPanel.setPreferredSize(new java.awt.Dimension(240, 30));
        abbreviationPanel.setLayout(new javax.swing.BoxLayout(abbreviationPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setText("Abbreviation: ");
        jLabel2.setToolTipText("");
        jLabel2.setMaximumSize(new java.awt.Dimension(140, 16));
        jLabel2.setMinimumSize(new java.awt.Dimension(140, 16));
        jLabel2.setPreferredSize(new java.awt.Dimension(140, 16));
        abbreviationPanel.add(jLabel2);

        abbreviationTextField.setMaximumSize(new java.awt.Dimension(200, 30));
        abbreviationTextField.setMinimumSize(new java.awt.Dimension(20, 20));
        abbreviationTextField.setPreferredSize(new java.awt.Dimension(100, 30));
        abbreviationTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abbreviationTextFieldActionPerformed(evt);
            }
        });
        abbreviationTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                abbreviationTextFieldFocusLost(evt);
            }
        });
        abbreviationPanel.add(abbreviationTextField);

        fixedAttributesPanel.add(abbreviationPanel);

        sexPanel.setMaximumSize(new java.awt.Dimension(600, 30));
        sexPanel.setLayout(new javax.swing.BoxLayout(sexPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setText("Sex: ");
        jLabel3.setToolTipText("");
        jLabel3.setMaximumSize(new java.awt.Dimension(140, 16));
        jLabel3.setMinimumSize(new java.awt.Dimension(140, 16));
        jLabel3.setPreferredSize(new java.awt.Dimension(140, 16));
        sexPanel.add(jLabel3);

        sexComboBox.setMaximumSize(new java.awt.Dimension(200, 30));
        sexComboBox.setMinimumSize(new java.awt.Dimension(20, 20));
        sexComboBox.setPreferredSize(new java.awt.Dimension(55, 30));
        sexComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sexComboBoxActionPerformed(evt);
            }
        });
        sexPanel.add(sexComboBox);

        fixedAttributesPanel.add(sexPanel);

        propertiesPanel.add(fixedAttributesPanel);

        languagesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Languages"));
        languagesPanel.setToolTipText("");
        languagesPanel.setMaximumSize(new java.awt.Dimension(600, 100));
        languagesPanel.setMinimumSize(new java.awt.Dimension(50, 100));
        languagesPanel.setPreferredSize(new java.awt.Dimension(320, 130));
        languagesPanel.setLayout(new java.awt.GridLayout(4, 1));

        lUsedPanel.setMaximumSize(new java.awt.Dimension(600, 20));
        lUsedPanel.setLayout(new javax.swing.BoxLayout(lUsedPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel4.setText("Language(s) used: ");
        jLabel4.setToolTipText("");
        jLabel4.setMaximumSize(new java.awt.Dimension(140, 16));
        jLabel4.setMinimumSize(new java.awt.Dimension(140, 16));
        jLabel4.setPreferredSize(new java.awt.Dimension(140, 16));
        lUsedPanel.add(jLabel4);

        luLabel.setMaximumSize(new java.awt.Dimension(460, 16));
        luLabel.setMinimumSize(new java.awt.Dimension(100, 16));
        luLabel.setPreferredSize(new java.awt.Dimension(130, 16));
        lUsedPanel.add(luLabel);

        languagesPanel.add(lUsedPanel);

        l1Panel.setMaximumSize(new java.awt.Dimension(600, 20));
        l1Panel.setLayout(new javax.swing.BoxLayout(l1Panel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel6.setText("First language(s):");
        jLabel6.setToolTipText("");
        jLabel6.setMaximumSize(new java.awt.Dimension(140, 16));
        jLabel6.setMinimumSize(new java.awt.Dimension(140, 16));
        jLabel6.setPreferredSize(new java.awt.Dimension(140, 16));
        l1Panel.add(jLabel6);

        l1Label.setMaximumSize(new java.awt.Dimension(460, 16));
        l1Label.setMinimumSize(new java.awt.Dimension(100, 16));
        l1Label.setPreferredSize(new java.awt.Dimension(130, 16));
        l1Panel.add(l1Label);

        languagesPanel.add(l1Panel);

        l2Panel.setMaximumSize(new java.awt.Dimension(600, 20));
        l2Panel.setLayout(new javax.swing.BoxLayout(l2Panel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel8.setText("Second language(s):");
        jLabel8.setToolTipText("");
        jLabel8.setMaximumSize(new java.awt.Dimension(140, 16));
        jLabel8.setMinimumSize(new java.awt.Dimension(140, 16));
        jLabel8.setPreferredSize(new java.awt.Dimension(140, 16));
        l2Panel.add(jLabel8);

        l2Label.setMaximumSize(new java.awt.Dimension(460, 16));
        l2Label.setMinimumSize(new java.awt.Dimension(100, 16));
        l2Label.setPreferredSize(new java.awt.Dimension(130, 16));
        l2Panel.add(l2Label);

        languagesPanel.add(l2Panel);

        languageButtonPanel.setMaximumSize(new java.awt.Dimension(600, 30));
        languageButtonPanel.setLayout(new javax.swing.BoxLayout(languageButtonPanel, javax.swing.BoxLayout.LINE_AXIS));

        editLangButton.setText("Edit languages...");
        editLangButton.setToolTipText("");
        editLangButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLangButtonActionPerformed(evt);
            }
        });
        languageButtonPanel.add(editLangButton);

        languagesPanel.add(languageButtonPanel);

        propertiesPanel.add(languagesPanel);

        commentScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Comment"));
        commentScrollPane.setToolTipText("");
        commentScrollPane.setMaximumSize(new java.awt.Dimension(10000, 10000));
        commentScrollPane.setMinimumSize(new java.awt.Dimension(320, 100));
        commentScrollPane.setPreferredSize(new java.awt.Dimension(320, 150));

        commentTextArea.setLineWrap(true);
        commentTextArea.setWrapStyleWord(true);
        commentTextArea.setMaximumSize(new java.awt.Dimension(600, 10000));
        commentTextArea.setMinimumSize(new java.awt.Dimension(0, 0));
        commentTextArea.setPreferredSize(new java.awt.Dimension(400, 2000));
        commentScrollPane.setViewportView(commentTextArea);

        propertiesPanel.add(commentScrollPane);

        getContentPane().add(propertiesPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

  private void abbreviationTextFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_abbreviationTextFieldFocusLost
// Add your handling code here:
    currentSpeaker.setAbbreviation(abbreviationTextField.getText());
    String newListEntry = currentSpeaker.getID() + " [" + currentSpeaker.getAbbreviation() + "]";
    speakerListModel.setElementAt(newListEntry, speakerList.getSelectedIndex());
  }//GEN-LAST:event_abbreviationTextFieldFocusLost

  private void collectAttButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_collectAttButtonActionPerformed
// Add your handling code here:
        if ((currentSpeaker!=null) && (speakertable.containsSpeakerWithID(currentSpeaker.getID()))){
            try {
                speakertable.collectAttributes(currentSpeaker.getID());
            } catch (org.exmaralda.partitureditor.jexmaralda.JexmaraldaException je) {}
            tablePanel.setUDInformation(currentSpeaker.getUDSpeakerInformation());
    }
  }//GEN-LAST:event_collectAttButtonActionPerformed

  private void templateButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_templateButtonActionPerformed
// Add your handling code here:
        OpenBasicTranscriptionDialog dialog = new OpenBasicTranscriptionDialog();
        if (dialog.openTranscription(this)){
            org.exmaralda.partitureditor.jexmaralda.BasicTranscription templateTranscription = dialog.getTranscription();
            speakertable.makeUDAttributesFromTemplate(templateTranscription.getHead().getSpeakertable());
            if (currentSpeaker!=null){
                tablePanel.setUDInformation(currentSpeaker.getUDSpeakerInformation());
            }            
        }      
        
  }//GEN-LAST:event_templateButtonActionPerformed

  private void removeAttButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAttButtonActionPerformed
// Add your handling code here:
  }//GEN-LAST:event_removeAttButtonActionPerformed

  private void addAttButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAttButtonActionPerformed
// Add your handling code here:
  }//GEN-LAST:event_addAttButtonActionPerformed

  private void editLangButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLangButtonActionPerformed
// Add your handling code here:
    EditLanguagesDialog dialog = new EditLanguagesDialog((javax.swing.JFrame)this.getParent(), true, 
                                                         currentSpeaker.getLanguagesUsed(),
                                                         currentSpeaker.getL1(),
                                                         currentSpeaker.getL2());
    if (dialog.editLanguages()){
        currentSpeaker.setLanguagesUsed(dialog.getLanguagesUsed());
        currentSpeaker.setL1(dialog.getL1());
        currentSpeaker.setL2(dialog.getL2());
        luLabel.setText(currentSpeaker.getLanguagesUsed().getLanguagesString());
        l1Label.setText(currentSpeaker.getL1().getLanguagesString());
        l2Label.setText(currentSpeaker.getL2().getLanguagesString());
    }        
  }//GEN-LAST:event_editLangButtonActionPerformed

  private void abbreviationTextFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abbreviationTextFieldActionPerformed
// Add your handling code here:
    currentSpeaker.setAbbreviation(abbreviationTextField.getText());
    String newListEntry = currentSpeaker.getID() + " [" + currentSpeaker.getAbbreviation() + "]";
    speakerListModel.setElementAt(newListEntry, speakerList.getSelectedIndex());
  }//GEN-LAST:event_abbreviationTextFieldActionPerformed

  private void sexComboBoxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sexComboBoxActionPerformed
// Add your handling code here:
    switch (sexComboBox.getSelectedIndex()){
        case 0 : {currentSpeaker.setSex('u'); break;}
        case 1 : {currentSpeaker.setSex('f'); break;}
        case 2 : {currentSpeaker.setSex('m'); break;}
    }
  }//GEN-LAST:event_sexComboBoxActionPerformed

  private void cancelButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
// Add your handling code here:
    change=false;
    setVisible (false);
    dispose ();    
  }//GEN-LAST:event_cancelButtonActionPerformed

  private void okButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
// Add your handling code here:
    if ((currentSpeaker!=null) && (speakertable.containsSpeakerWithID(currentSpeaker.getID()))){
        tablePanel.table.commitCellEdit();
        currentSpeaker.setUDSpeakerInformation(tablePanel.getUDInformation());
        currentSpeaker.setComment(commentTextArea.getText());
    }
    change = true;
    setVisible (false);
    dispose ();    
  }//GEN-LAST:event_okButtonActionPerformed

  private void removeSpeakerButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSpeakerButtonActionPerformed
// Add your handling code here:
    int selection = speakerList.getSelectedIndex();    
    if (selection!=-1){
        //String selectedValue = (String)speakerList.getSelectedValue();
        //String speakerID = selectedValue.substring(0, selectedValue.indexOf(" [")); 
        String speakerID = (String)(speakerIDs.elementAt(selection));
        speakerListModel.removeElementAt(selection);
        speakerIDs.removeElementAt(selection);
        speakerList.setSelectedIndex(-1);
        enablePropertiesPanel(false);
        try{
            speakertable.removeSpeakerWithID(speakerID);
        } catch (org.exmaralda.partitureditor.jexmaralda.JexmaraldaException je) {}                
    }
  }//GEN-LAST:event_removeSpeakerButtonActionPerformed

  private void addSpeakerButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSpeakerButtonActionPerformed
// Add your handling code here:
    String newID = speakertable.getFreeID();
    try {
        speakertable.addSpeaker(new org.exmaralda.partitureditor.jexmaralda.Speaker(newID, newID, 'u', 
                                 new org.exmaralda.partitureditor.jexmaralda.Languages(), new org.exmaralda.partitureditor.jexmaralda.Languages(), new org.exmaralda.partitureditor.jexmaralda.Languages(), 
                                 new org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable(), new String()));
        // removed for version 1.2.2., 18/11/2002
        //speakertable.getSpeakerWithID(newID).getLanguagesUsed().addLanguage("de");
    } catch (org.exmaralda.partitureditor.jexmaralda.JexmaraldaException je) {}
    speakerListModel.addElement((newID + " [" + newID + "]"));
    speakerIDs.addElement(newID);
    speakerList.setSelectedIndex(speakerIDs.size()-1);
  }//GEN-LAST:event_addSpeakerButtonActionPerformed

  private void speakerListValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_speakerListValueChanged
// Add your handling code here:
    if (speakerList.getSelectedIndex()!=-1){
        if ((currentSpeaker!=null) && (speakertable.containsSpeakerWithID(currentSpeaker.getID()))){
            currentSpeaker.setUDSpeakerInformation(tablePanel.getUDInformation());
            currentSpeaker.setComment(commentTextArea.getText());
        }
        //String selectedValue = (String)speakerList.getSelectedValue();
        //String speakerID = selectedValue.substring(0, selectedValue.lastIndexOf(" ["));
        String speakerID = (String)(speakerIDs.elementAt(speakerList.getSelectedIndex()));
        try {
            currentSpeaker = speakertable.getSpeakerWithID(speakerID);
        } catch (org.exmaralda.partitureditor.jexmaralda.JexmaraldaException je){
            
        }
        enablePropertiesPanel(true);
        tablePanel.setUDInformation(currentSpeaker.getUDSpeakerInformation());         
        abbreviationTextField.setText(currentSpeaker.getAbbreviation());
        switch (currentSpeaker.getSex()){
            case 'u' : {sexComboBox.setSelectedIndex(0); break;}
            case 'f' : {sexComboBox.setSelectedIndex(1); break;}
            case 'm' : {sexComboBox.setSelectedIndex(2); break;}
        }
        commentTextArea.setText(currentSpeaker.getComment());
        luLabel.setText(currentSpeaker.getLanguagesUsed().getLanguagesString());
        l1Label.setText(currentSpeaker.getL1().getLanguagesString());
        l2Label.setText(currentSpeaker.getL2().getLanguagesString());            
    }
    else {
        enablePropertiesPanel(false);
    }
  }//GEN-LAST:event_speakerListValueChanged

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        change=false;
        setVisible (false);
        dispose ();
    }//GEN-LAST:event_closeDialog

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        //new SpeakerTableDialog (new javax.swing.JFrame (), true).show ();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel abbreviationPanel;
    private javax.swing.JTextField abbreviationTextField;
    private javax.swing.JButton addSpeakerButton;
    private javax.swing.JCheckBox autoAddSpeakerTiersCheckBox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane commentScrollPane;
    private javax.swing.JTextArea commentTextArea;
    private javax.swing.JButton editLangButton;
    private javax.swing.JPanel fixedAttributesPanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel l1Label;
    private javax.swing.JPanel l1Panel;
    private javax.swing.JLabel l2Label;
    private javax.swing.JPanel l2Panel;
    private javax.swing.JPanel lUsedPanel;
    private javax.swing.JPanel languageButtonPanel;
    private javax.swing.JPanel languagesPanel;
    private javax.swing.JLabel luLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.JButton removeSpeakerButton;
    private javax.swing.JComboBox sexComboBox;
    private javax.swing.JPanel sexPanel;
    private javax.swing.JList speakerList;
    private javax.swing.JScrollPane speakerListScrollPane;
    private javax.swing.JPanel speakersButtonPanel;
    private javax.swing.JPanel speakersPanel;
    // End of variables declaration//GEN-END:variables
    
    private void enablePropertiesPanel(boolean enabled){
        propertiesPanel.setEnabled(enabled);
        editLangButton.setEnabled(enabled);
        collectAttButton.setEnabled(enabled);
        commentTextArea.setEnabled(enabled);
        sexComboBox.setEnabled(enabled);
        abbreviationTextField.setEnabled(enabled);
        commentScrollPane.setEnabled(enabled);
        fixedAttributesPanel.setEnabled(enabled);
        languagesPanel.setEnabled(enabled);
    }
    
    public boolean editSpeakertable(){
         java.awt.Dimension dialogSize = this.getPreferredSize();
         java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
         this.setLocation(screenSize.width/2 - dialogSize.width/2, screenSize.height/2 - dialogSize.height/2);
         //this.show();
         this.setVisible(true);
         return change;
    }
}