/*
 * ExportTASXDialog.java
 *
 * Created on 23. November 2001, 13:52
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class ExportAGDialog extends SaveBasicTranscriptionAsDialog {

    /** Creates new ExportAGDialog */
    public ExportAGDialog(String startDirectory, BasicTranscription t) {
        super(startDirectory, t, false);
        setDialogTitle("Export an AG file");        
    }

    public boolean exportAG(java.awt.Component parent) {
        boolean proceed = false;
        while (!proceed){
            int returnVal = showSaveDialog(parent);
            if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                checkExtension();
                proceed=checkOverwrite(parent);
            }
            else {success = false; return success;}
        }
        try {
            org.exmaralda.partitureditor.jexmaralda.convert.AIFConverter.writeAIFToFile(getTranscription(), getFilename());
            //getTranscription().writeAGToFile(getFilename());
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
        
    }
    
}
