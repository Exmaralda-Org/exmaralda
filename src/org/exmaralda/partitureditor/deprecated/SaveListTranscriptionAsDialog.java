/*
 * SaveListTranscriptionAsDialog.java
 *
 * Created on 9. Juli 2003, 10:57
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;

import java.io.*;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;

/**
 *
 * @author  thomas
 */
public class SaveListTranscriptionAsDialog extends AbstractXMLSaveAsDialog {
    
    private ListTranscription transcription;
    
    /** Creates a new instance of SaveListTranscriptionAsDialog */
    public SaveListTranscriptionAsDialog(ListTranscription t) {
        super();
        setFileFilter(new ParameterFileFilter("xml", "Extensible Markup Language"));
        transcription = t;
        setDialogTitle("Save a list transcription to file");
    }
    
    /** Creates new SaveBasicTranscriptionAsDialog */
    public SaveListTranscriptionAsDialog(String startDirectory, ListTranscription t) {
        super(startDirectory);
        setFileFilter(new ParameterFileFilter("xml", "Extensible Markup Language"));
        transcription = t;
        setDialogTitle("Save a list transcription to file");
    }
    
    public void setTranscription(ListTranscription t){
        transcription=t;
    }
    
    ListTranscription getTranscription(){
        return transcription;
    }
    
    public boolean saveTranscriptionAs(java.awt.Component parent){
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
             transcription.writeXMLToFile(getFilename(),"none");                 
             success=true;
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
    }
    
    @Override
    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(File.separatorChar)){
            setFilename(getFilename() + ".xml");
        }
    }
    
    
}
