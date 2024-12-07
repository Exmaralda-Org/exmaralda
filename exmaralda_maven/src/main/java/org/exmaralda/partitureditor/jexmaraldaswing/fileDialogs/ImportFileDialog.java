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
    public JCheckBox splitSentencesCheckBox;
    public String[] encodingNames = {"[System-Default]", "7-Bit-ASCII", "ISO-8859-1 (Standard Latin)", "UTF-8", "UTF-16 (Big Endian)", "UTF-16 (Little Endian)"};
    public String[] encodings = {"", "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE"};


    /** Creates new ImportFileDialog
     * @param startDirectory */
    public ImportFileDialog(String startDirectory) {
        super();
        boolean thisIsAMac = System.getProperty("os.name").substring(0,3).equalsIgnoreCase("mac");
        if (thisIsAMac){
            setPreferredSize(new java.awt.Dimension(800, 600));
        }
        setCurrentDirectory(new File(startDirectory).getParentFile());
        setDialogTitle("Import file");
        setAcceptAllFileFilterUsed(false);
        addChoosableFileFilter(TEIFileFilter);
        addChoosableFileFilter(EAFFileFilter);
        addChoosableFileFilter(PraatFileFilter);
        addChoosableFileFilter(FOLKERTranscriptionFileFilter);
        addChoosableFileFilter(CHATTranscriptFileFilter);
        addChoosableFileFilter(TranscriberFileFilter);
        addChoosableFileFilter(ExmaraldaSegmentedTranscriptionFileFilter);
        // added 27-11-2021: issue #296
        addChoosableFileFilter(FrazierADCFileFilter);
        addChoosableFileFilter(AnvilFileFilter);
        addChoosableFileFilter(SimpleExmaraldaFileFilter);
        // added 17-11-2017: issue #119
        addChoosableFileFilter(VTTFileFilter);
        addChoosableFileFilter(SRTFileFilter);
        // added 07-02-2023: issue #363
        addChoosableFileFilter(AdobePremiereCSVFilter);
        
        
        //added 15-01-2023: issue #357
        addChoosableFileFilter(WhisperJSONFileFilter);
        //added 15-01-2023: issue #358
        addChoosableFileFilter(AmberscriptJSONFileFilter);
        
        addChoosableFileFilter(TextFileFilter);
        addChoosableFileFilter(AudacityLabelFileFilter);
        addChoosableFileFilter(TreeTaggerFilter);
        //addChoosableFileFilter(TEIFileFilter);
        addChoosableFileFilter(XSLStylesheetImportFilter);
        addChoosableFileFilter(HIATDOSFileFilter);
        addChoosableFileFilter(PhonFileFilter);
        addChoosableFileFilter(FlexTextXMLFileFilter);
        addChoosableFileFilter(TCFFileFilter);
        addChoosableFileFilter(TsvFileFilter);
        addChoosableFileFilter(F4TextFileFilter);
        setFileFilter(PraatFileFilter);

        /*addChoosableFileFilter(exSyncFileFilter);
        addChoosableFileFilter(RioDeJaneiroFileFilter);
        addChoosableFileFilter(TASXFileFilter);
        addChoosableFileFilter(WinPitchFileFilter);
        addChoosableFileFilter(AGFileFilter);
        addChoosableFileFilter(TransanaXMLFileFilter);*/



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
        splitSentencesCheckBox = new JCheckBox("Split sentences");
        splitSentencesCheckBox.setSelected(true);
        accessoryPanel.add(splitSentencesCheckBox);
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
        }  else if (getFileFilter()==TsvFileFilter){
            setAccessory(accessoryPanel);
        }  else if (getFileFilter()==F4TextFileFilter){
            setAccessory(accessoryPanel);
            encodingComboBox.setSelectedIndex(3); // set this to UTF-8
        } else {
            setAccessory(null);
        }
        appendSpacesCheckBox.setVisible(getFileFilter()==TreeTaggerFilter);
        splitSentencesCheckBox.setVisible(getFileFilter()==F4TextFileFilter);
        revalidate();
    }


}
