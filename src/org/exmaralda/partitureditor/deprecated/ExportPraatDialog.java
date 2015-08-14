/*
 * ExportTASXDialog.java
 *
 * Created on 23. November 2001, 13:52
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ExportPraatDialog extends SaveBasicTranscriptionAsDialog {

    /** Creates new ExportPraatDialog */
    public ExportPraatDialog(String startDirectory, BasicTranscription t) {
        super(startDirectory, t, false);
        this.setFileFilter(new org.exmaralda.partitureditor.deprecated.PraatFileFilter());
        setDialogTitle("Export a Praat TextGrid");        
    }

    public boolean exportPraat(java.awt.Component parent) {
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
            new PraatConverter().writePraatToFile(getTranscription(), getFilename());
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
        
    }
    
    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(getSelectedFile().separatorChar)){
            setFilename(getFilename() + ".textGrid");
        }
    }    
    
}
