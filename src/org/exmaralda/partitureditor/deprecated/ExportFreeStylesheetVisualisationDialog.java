/*
 * ExportFreeStylesheetVisualisationDialog.java
 *
 * Created on 19. Maerz 2004, 15:27
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;

/**
 *
 * @author  thomas
 */
public class ExportFreeStylesheetVisualisationDialog extends AbstractTextSaveAsDialog {
    
    String text = "";
    /** Creates a new instance of ExportFreeStylesheetVisualisationDialog */
    public ExportFreeStylesheetVisualisationDialog(String t) {
        text = t;
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.HTMLFileFilter());        
    }
    

    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(getSelectedFile().separatorChar)){
            setFilename(getFilename() + ".html");
        }
    }
    
    public boolean save(java.awt.Component parent){
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
            writeText(text);
        } catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
}
    
}
