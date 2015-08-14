/*
 * ImportTASXDialog.java
 *
 * Created on 23. November 2001, 15:04
 */

package org.exmaralda.partitureditor.deprecated;

import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.convert.AIFConverter;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ImportAGDialog extends OpenBasicTranscriptionDialog {

    /** Creates new ImportTASXDialog */
    public ImportAGDialog(String startDirectory) {
        super(startDirectory);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter("xml", "Extensible Markup Language"));
        setDialogTitle("Import an AG file");        
    }
    
    public boolean importAG(java.awt.Component parent){
        int returnVal = showOpenDialog(parent);
        if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            setFilename(new String(getSelectedFile().toString()));
            AIFConverter tc = new AIFConverter();
            try {
                setTranscription(tc.readAIFFromFile(getFilename()));
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorDialog(ex.getMessage());
                return false;
            }
            success=true;                                                                          
        }
        else {
            success=false;
        }         
        return success;
    }
    

    void showErrorDialog(String message){
        JOptionPane.showMessageDialog(  getParent(),
                                "File could not be read.\n" + "Error message was:\n" + message,
                                "I/O  error or Transformer (configuration) error",
                                javax.swing.JOptionPane.ERROR_MESSAGE);
        success=false;                        
    }
}
