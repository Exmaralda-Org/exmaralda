/*
 * SaveBasicTranscriptionAsDialog.java
 *
 * Created on 1. August 2001, 17:25
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import javax.swing.JCheckBox;
import javax.swing.JPanel;


/**
 *
 * @author  Thomas
 * @version 
 */
public class SaveBasicTranscriptionAsDialog extends AbstractXMLSaveAsDialog {

    private BasicTranscription transcription;
    private TierFormatTable tierFormatTable;
    
    JCheckBox saveTierFormatTableCheckBox = new JCheckBox(
            org.exmaralda.common.helpers.Internationalizer.getString("Save formats")
            );
    JPanel accessory = new JPanel(new java.awt.FlowLayout());
    
    /** Creates new SaveBasicTranscriptionAsDialog */
    public SaveBasicTranscriptionAsDialog(BasicTranscription t) {
        super();
        transcription = t;
        setDialogTitle("Save basic transcription as");
    }
    
    /** Creates new SaveBasicTranscriptionAsDialog */
    public SaveBasicTranscriptionAsDialog(String startDirectory, BasicTranscription t) {
        super(startDirectory);
        transcription = t;
        setDialogTitle("Save basic transcription as");
    }
    
    /** Creates new SaveBasicTranscriptionAsDialog */
    public SaveBasicTranscriptionAsDialog(String startDirectory, BasicTranscription t, TierFormatTable tft) {
        super(startDirectory);
                
        accessory.add(saveTierFormatTableCheckBox);
        setAccessory(accessory);        
        
        transcription = t;
        tierFormatTable = tft;
        setDialogTitle("Save basic transcription as");
    }
    
    
    /** Creates new SaveBasicTranscriptionAsDialog */
    public SaveBasicTranscriptionAsDialog(String startDirectory, BasicTranscription t, boolean showDTDPanel) {
        super(startDirectory, showDTDPanel);
        transcription = t;
        setDialogTitle("Save a basic transcription to file");
    }

    public void setSaveTierFormatTable(boolean b){
        saveTierFormatTableCheckBox.setSelected(b);        
    }
    
    public boolean isSaveTierFormatTable(){
        return saveTierFormatTableCheckBox.isSelected();
    }
    
    public void setTranscription(BasicTranscription t){
        transcription=t;
    }
    
    public BasicTranscription getTranscription(){
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
            if (saveTierFormatTableCheckBox.isSelected()){ 
                transcription.writeXMLToFile(getFilename(),"none", tierFormatTable);                 
            } else {
                transcription.writeXMLToFile(getFilename(),"none");                 
            }
            success=true;
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
    }

    @Override
    public void checkExtension(){
        // added 14-04-2009
        String currentFilename = getSelectedFile().getAbsolutePath();
        if (!(getSelectedFile().getName().contains("."))){
            currentFilename+=".exb";
        }
        setFilename(currentFilename);
    }

    
    


}