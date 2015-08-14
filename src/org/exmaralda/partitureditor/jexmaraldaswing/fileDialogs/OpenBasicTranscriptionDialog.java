/*
 * OpenBasicTranscriptionDialog.java
 *
 * Created on 1. August 2001, 14:02
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;

import java.io.*;
import java.net.*;
import org.xml.sax.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class OpenBasicTranscriptionDialog extends AbstractXMLOpenDialog {

    private BasicTranscription transcription;
    TranscriptionPreviewPanel tpp = new TranscriptionPreviewPanel();
    
    /** Creates new OpenBasicTranscriptionDialog */
    public OpenBasicTranscriptionDialog() {
        super();
        setDialogTitle("Open transcription from file");
        addPropertyChangeListener(tpp);
        setAccessory(tpp);
    }

    /** Creates new OpenBasicTranscriptionDialog and sets start directory */
    public OpenBasicTranscriptionDialog(String startDirectory){
        super(startDirectory);
        setDialogTitle("Open transcription from file");
        addPropertyChangeListener(tpp);
        setAccessory(tpp);
    }
    
    public BasicTranscription getTranscription(){
        return transcription;
    }
    
    public void setTranscription(BasicTranscription t){
        transcription = t.makeCopy();
    }
    
    
    public boolean openTranscription(java.awt.Component parent){
        boolean thisIsAMac = System.getProperty("os.name").substring(0,3).equalsIgnoreCase("mac");
        if (thisIsAMac){
            setPreferredSize(new java.awt.Dimension(800, 600));
        }
        int returnVal = showOpenDialog(parent);
        if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            setFilename(getSelectedFile().toString());
            try {
                transcription = new BasicTranscription(getFilename());
                success=true;
            }
            catch (SAXException se){
                showSAXErrorDialog(se, parent);
            }
            catch (JexmaraldaException je){
                showJexmaraldaErrorDialog(je, parent);
            }
        }
        else {
            success=false;
        }         
        return success;
    }
    
    
}