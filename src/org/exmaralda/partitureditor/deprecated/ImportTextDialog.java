/*
 * ImportBasicFromSimpleDialog.java
 *
 * Created on 28. August 2001, 09:51
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.convert.TextConverter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import javax.swing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

import java.io.*;
import java.net.*;
import org.xml.sax.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ImportTextDialog extends AbstractXMLOpenDialog {

    private BasicTranscription transcription;
    private JComboBox encodingComboBox;
    private JPanel accessoryPanel;
    private String[] encodingNames = {"[System-Default]", "7-Bit-ASCII", "ISO-8859-1 (Standard Latin)", "UTF-8", "UTF-16 (Big Endian)", "UTF-16 (Little Endian)"};
    private String[] encodings = {"", "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE"};

    /** Creates new ImportBasicFromSimpleDialog */
    public ImportTextDialog() {
        super();
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.TextFileFilter());
        setDialogTitle("Import a text file");       
        initAccessory();
    }

    /** Creates new ImportBasicFromSimpleDialog */
    public ImportTextDialog(String startDirectory) {
        super(startDirectory);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.TextFileFilter());
        setDialogTitle("Import a text file");        
        initAccessory();
    }

    public BasicTranscription getTranscription(){
        return transcription;
    }
    
    private void initAccessory(){
        accessoryPanel = new JPanel();
        encodingComboBox = new JComboBox(encodingNames);
        accessoryPanel.setBorder(new javax.swing.border.TitledBorder("Char encoding"));
        accessoryPanel.add(encodingComboBox);
        org.exmaralda.common.helpers.Internationalizer.internationalizeComponentToolTips(accessoryPanel);
        this.setAccessory(accessoryPanel);        
    }
        
    
    public boolean importTranscription(java.awt.Component parent){
        int returnVal = showOpenDialog(parent);
        if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            setFilename(new String(getSelectedFile().toString()));
            try {
                ChooseTextSplitterDialog ctsd = new ChooseTextSplitterDialog(null, true);
                ctsd.setVisible(true);
                String regex = ctsd.getRegex();
                TextConverter tc = new TextConverter(regex);
                if (encodingComboBox.getSelectedIndex()==0){
                    tc.readText(getSelectedFile());
                } else {
                    tc.readText(getSelectedFile(),encodings[encodingComboBox.getSelectedIndex()]);
                }
                transcription = tc.convert();               
                success=true;
            }
            catch (IOException ioe) {
                SAXException se = new SAXException(ioe);
                showSAXErrorDialog(se, parent);
            }
        }
        else {
            success=false;
        }         
        return success;
    }
    


}