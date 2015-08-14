/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.io.*;
import javax.swing.*;

/**
 *
 * @author thomas
 */
public class OutputFileDialog extends AbstractFileFilterDialog implements java.beans.PropertyChangeListener {

    
    public SVGAccessoryPanel svgAccessory = new SVGAccessoryPanel();   
    public HTMLFramesOptionPanel framesAccessory = new HTMLFramesOptionPanel();
    public org.exmaralda.partitureditor.interlinearText.swing.ChooseSettingsForXMLExportPanel chooseSettingsForXMLExportPanel
            = new org.exmaralda.partitureditor.interlinearText.swing.ChooseSettingsForXMLExportPanel();

    public JComboBox encodingComboBox;
    private JPanel encodingPanel;
    public String[] encodingNames = {"[System-Default]", "7-Bit-ASCII", "ISO-8859-1 (Standard Latin)", "UTF-8", "UTF-16 (Big Endian)", "UTF-16 (Little Endian)"};
    public String[] encodings = {"", "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE"};

    SelectionAccessory selectionPanel;
    
      
    
    /** Creates new ExportTASXDialog */
    public OutputFileDialog(String startDirectory) {
        super();
        boolean thisIsAMac = System.getProperty("os.name").substring(0,3).equalsIgnoreCase("mac");
        if (thisIsAMac){
            setPreferredSize(new java.awt.Dimension(800, 600));
        }
        setCurrentDirectory(new File(startDirectory).getParentFile());
        setDialogTitle("Output file");       
        setAcceptAllFileFilterUsed(false);
        addChoosableFileFilter(HTMLPartiturFileFilter);
        addChoosableFileFilter(RTFPartiturFileFilter);
        addChoosableFileFilter(SVGPartiturFileFilter);
        addChoosableFileFilter(XMLPartiturFileFilter);
        addChoosableFileFilter(HTMLSegmentChainFileFilter);
        addChoosableFileFilter(FreeStylesheetFileFilter);
        addChoosableFileFilter(HTMLPartiturWithHTML5AudioFileFilter);
        addChoosableFileFilter(HTMLPartiturWithFlashFileFilter);
        addChoosableFileFilter(HTMLSegmentChainWithHTML5AudioFileFilter);
        addChoosableFileFilter(HTMLSegmentChainWithFlashFileFilter);
        addChoosableFileFilter(GATTranscriptFileFilter);
        addChoosableFileFilter(SimpleTextTranscriptFileFilter);
        setFileFilter(HTMLPartiturFileFilter);
        setMultiSelectionEnabled(false);
        addPropertyChangeListener("fileFilterChanged", this);
        initAccessory();
        setAccessory(framesAccessory);
    }
    
    private void initAccessory(){
        encodingPanel = new JPanel();
        encodingPanel.setLayout(new BoxLayout(encodingPanel,BoxLayout.Y_AXIS));
        encodingComboBox = new JComboBox(encodingNames);
        encodingComboBox.setPreferredSize(new java.awt.Dimension(150,25));
        encodingComboBox.setMaximumSize(new java.awt.Dimension(150,25));
        encodingPanel.setBorder(new javax.swing.border.TitledBorder("Char encoding"));
        encodingPanel.add(encodingComboBox);
        org.exmaralda.common.helpers.Internationalizer.internationalizeComponentToolTips(encodingPanel);

        selectionPanel = new SelectionAccessory();
    }
    

    public void propertyChange(PropertyChangeEvent evt) {
        if (!(evt.getPropertyName().equals("fileFilterChanged"))) return;
        if (getFileFilter()==SVGPartiturFileFilter){
            setAccessory(svgAccessory);
        } else if (getFileFilter()==HTMLPartiturFileFilter){
            setAccessory(framesAccessory);
        } else if (getFileFilter()==XMLPartiturFileFilter){
            setAccessory(chooseSettingsForXMLExportPanel);
        } else if (getFileFilter()==this.FreeStylesheetFileFilter){
            setAccessory(encodingPanel);
        } else if (getFileFilter()==GATTranscriptFileFilter){
            setAccessory(encodingPanel);
        } else if (getFileFilter()==GATTranscriptFileFilter){
            setAccessory(encodingPanel);
        } else {
            setAccessory(null);
        }
        revalidate();
    }

    @Override
    public void setAccessory(JComponent newAccessory) {
        JPanel accessory = new JPanel();
        String os = System.getProperty("os.name").substring(0,3);
        if (!(os.equalsIgnoreCase("mac"))){
            accessory.setLayout(new BoxLayout(accessory, BoxLayout.Y_AXIS));
        }
        selectionPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        accessory.add(this.selectionPanel);
        if (newAccessory!=null){
            newAccessory.setAlignmentX(Component.RIGHT_ALIGNMENT);
            accessory.add(Box.createVerticalGlue());
            accessory.add(newAccessory);
        }
        super.setAccessory(accessory);
    }

    public int getSelectionChoice(){
        return selectionPanel.getSelectionChoice();
    }



}
