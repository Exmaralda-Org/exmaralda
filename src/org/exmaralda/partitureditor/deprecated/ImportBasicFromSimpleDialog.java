/*
 * ImportBasicFromSimpleDialog.java
 *
 * Created on 28. August 2001, 09:51
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.SimpleExmaraldaReader;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import javax.swing.*;

import java.io.*;
import java.net.*;
import org.xml.sax.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ImportBasicFromSimpleDialog extends AbstractXMLOpenDialog {

    private BasicTranscription transcription;
    private JComboBox encodingComboBox;
    private JPanel accessoryPanel;
    private String[] encodingNames = {"[System-Default]", "7-Bit-ASCII", "ISO-8859-1 (Standard Latin)", "UTF-8", "UTF-16 (Big Endian)", "UTF-16 (Little Endian)"};
    private String[] encodings = {"", "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE"};

    /** Creates new ImportBasicFromSimpleDialog */
    public ImportBasicFromSimpleDialog() {
        super();
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.TextFileFilter());
        setDialogTitle("Import a basic transcription from a Simple EXMARaLDA text file");       
        initAccessory();
    }

    /** Creates new ImportBasicFromSimpleDialog */
    public ImportBasicFromSimpleDialog(String startDirectory) {
        super(startDirectory);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.TextFileFilter());
        setDialogTitle("Import a Simple EXMARaLDA file");        
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
        
    
    public boolean importTranscription(java.awt.Component parent, javax.swing.JProgressBar progressBar){
        int returnVal = showOpenDialog(parent);
        if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            setFilename(new String(getSelectedFile().toString()));
            try {
                progressBar.setMinimum(0);
                progressBar.setMaximum(50);
                progressBar.setValue(10);
                progressBar.setString("Reading text file");
                SimpleExmaraldaReader ser = null;
                if (encodingComboBox.getSelectedIndex()==0){
                    ser = new SimpleExmaraldaReader(getFilename());
                } else {
                    ser = new SimpleExmaraldaReader(getFilename(), encodings[encodingComboBox.getSelectedIndex()]);
                }
                progressBar.setValue(10);
                progressBar.setValue(40);
                progressBar.setString("Making basic transcription");
                transcription = ser.parseBasicTranscription();               
                success=true;
            }
            catch (IOException ioe) {
                SAXException se = new SAXException(ioe);
                showSAXErrorDialog(se, parent);
                progressBar.setString("Import failed");
                progressBar.setValue(progressBar.getMaximum());
            }
            catch (JexmaraldaException je){
                showJexmaraldaErrorDialog(je, parent);
                progressBar.setString("Import failed");
                progressBar.setValue(progressBar.getMaximum());
            }
        }
        else {
            success=false;
        }         
        return success;
    }
    

    public boolean importTranscription(java.awt.Component parent){
        return importTranscription(parent, new javax.swing.JProgressBar());
    }

}