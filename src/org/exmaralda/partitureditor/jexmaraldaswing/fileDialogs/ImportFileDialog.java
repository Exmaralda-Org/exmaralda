/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.io.*;
import javax.swing.*;
/**
 *
 * @author thomas
 */
public class ImportFileDialog extends AbstractFileFilterDialog implements java.beans.PropertyChangeListener {
    
    public JComboBox encodingComboBox;
    private JPanel accessoryPanel;
    public JCheckBox appendSpacesCheckBox;
    public String[] encodingNames = {"[System-Default]", "7-Bit-ASCII", "ISO-8859-1 (Standard Latin)", "UTF-8", "UTF-16 (Big Endian)", "UTF-16 (Little Endian)"};
    public String[] encodings = {"", "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE"};
    
    
    /** Creates new ImportFileDialog */
    public ImportFileDialog(String startDirectory) {
        super();
        boolean thisIsAMac = System.getProperty("os.name").substring(0,3).equalsIgnoreCase("mac");
        if (thisIsAMac){
            setPreferredSize(new java.awt.Dimension(800, 600));
        }
        setCurrentDirectory(new File(startDirectory).getParentFile());
        setDialogTitle("Import file");       
        setAcceptAllFileFilterUsed(false);
        addChoosableFileFilter(EAFFileFilter);
        addChoosableFileFilter(PraatFileFilter);
        addChoosableFileFilter(FOLKERTranscriptionFileFilter);
        addChoosableFileFilter(CHATTranscriptFileFilter);
        addChoosableFileFilter(TranscriberFileFilter);
        addChoosableFileFilter(WinPitchFileFilter);
        addChoosableFileFilter(AnvilFileFilter);
        addChoosableFileFilter(TASXFileFilter);
        addChoosableFileFilter(AGFileFilter);
        addChoosableFileFilter(SimpleExmaraldaFileFilter);
        addChoosableFileFilter(RioDeJaneiroFileFilter);
        addChoosableFileFilter(TextFileFilter);
        addChoosableFileFilter(AudacityLabelFileFilter);
        addChoosableFileFilter(TreeTaggerFilter);
        addChoosableFileFilter(TEIFileFilter);
        addChoosableFileFilter(XSLStylesheetImportFilter);
        addChoosableFileFilter(HIATDOSFileFilter);
        addChoosableFileFilter(PhonFileFilter);
        addChoosableFileFilter(exSyncFileFilter);
        setFileFilter(PraatFileFilter);
        setMultiSelectionEnabled(false);
        initAccessory();
        addPropertyChangeListener("fileFilterChanged", this);
    }
    
    private void initAccessory(){
        accessoryPanel = new JPanel();
        accessoryPanel.setLayout(new BoxLayout(accessoryPanel, BoxLayout.Y_AXIS));
        encodingComboBox = new JComboBox(encodingNames);
        encodingComboBox.setPreferredSize(new Dimension(encodingComboBox.getPreferredSize().width, 22));
        encodingComboBox.setMaximumSize(new Dimension(encodingComboBox.getMaximumSize().width, 22));
        accessoryPanel.setBorder(new javax.swing.border.TitledBorder("Char encoding"));
        accessoryPanel.add(encodingComboBox);
        appendSpacesCheckBox = new JCheckBox("Append spaces");
        appendSpacesCheckBox.setSelected(true);
        accessoryPanel.add(appendSpacesCheckBox);
        org.exmaralda.common.helpers.Internationalizer.internationalizeComponentToolTips(accessoryPanel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!(evt.getPropertyName().equals("fileFilterChanged"))) return;
        if (getFileFilter()==SimpleExmaraldaFileFilter){
            setAccessory(accessoryPanel);
        } else if (getFileFilter()==TextFileFilter){
            setAccessory(accessoryPanel);
        } else if (getFileFilter()==RioDeJaneiroFileFilter){
            setAccessory(accessoryPanel);
        }  else if (getFileFilter()==TreeTaggerFilter){
            setAccessory(accessoryPanel);
        } else {
            setAccessory(null);
        }
        appendSpacesCheckBox.setVisible(getFileFilter()==TreeTaggerFilter);
        revalidate();
    }
    

}
