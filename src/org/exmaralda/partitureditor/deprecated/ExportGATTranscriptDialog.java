/*
 * ExportGATTranscriptDialog.java
 *
 * Created on 22. Maerz 2004, 15:39
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import java.io.*;
/**
 *
 * @author  thomas
 */
public class ExportGATTranscriptDialog extends AbstractTextSaveAsDialog{
    
    String transcriptText = ""; 
    
    /** Creates a new instance of ExportGATTranscriptDialog */
    public ExportGATTranscriptDialog(String tt) {
        transcriptText = tt;
        setDialogTitle("Set the text file for export");
    }
    
    public boolean saveTranscript(java.awt.Component parent){
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
            writeText(transcriptText);
        } catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
    }
    
}
