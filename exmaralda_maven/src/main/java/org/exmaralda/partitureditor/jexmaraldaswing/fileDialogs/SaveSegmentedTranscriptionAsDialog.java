/*
 * SaveSegmentedTranscriptionAsDialog.java
 *
 * Created on 23. Januar 2003, 15:41
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;

/**
 *
 * @author  Thomas
 * @version 
 */
public class SaveSegmentedTranscriptionAsDialog extends AbstractXMLSaveAsDialog {
 
    private SegmentedTranscription transcription;

    /** Creates new SaveSegmentedTranscriptionAsDialog */
    public SaveSegmentedTranscriptionAsDialog(SegmentedTranscription t) {
        super();
        super.resetChoosableFileFilters();
        String[] seg_suff={"exs", "xml"};
        setFileFilter(new ParameterFileFilter(seg_suff, "EXMARaLDA Segmented Transcription (EXS, XML)"));
        transcription = t;
        setDialogTitle("Save a segmented transcription to file");
    }

    
    /** Creates new SaveSegmentedTranscriptionAsDialog */
    public SaveSegmentedTranscriptionAsDialog(String startDirectory, SegmentedTranscription t) {
        super(startDirectory);
        super.resetChoosableFileFilters();
        String[] seg_suff={"exs", "xml"};
        setFileFilter(new ParameterFileFilter(seg_suff, "EXMARaLDA Segmented Transcription (EXS, XML)"));
        //addChoosableFileFilter(new ParameterFileFilter("xml", "Extensible Markup Language (XML)"));
        transcription = t;
        setDialogTitle("Save a segmented transcription to file");
    }
    

    public void setTranscription(SegmentedTranscription t){
        transcription=t;
    }
    
    SegmentedTranscription getTranscription(){
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
        String name = getSelectedFile().getName();
        if (name.indexOf('.')<0){
            filename = filename + ".exs";
            setFilename(filename);
        }
    }

    
    


}

