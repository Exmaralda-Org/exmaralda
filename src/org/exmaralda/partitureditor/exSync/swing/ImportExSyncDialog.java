/*
 * ImportTASXDialog.java
 *
 * Created on 23. November 2001, 15:04
 */

package org.exmaralda.partitureditor.exSync.swing;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.OpenBasicTranscriptionDialog;
import org.xml.sax.*;

import org.exmaralda.partitureditor.exSync.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ImportExSyncDialog extends OpenBasicTranscriptionDialog {

    /** Creates new ImportTASXDialog */
    public ImportExSyncDialog(String startDirectory) {
        super(startDirectory);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter("xml", "Extensible Markup Language"));
        setDialogTitle("Import an ExSync file");        
    }
    
    public boolean importExSync(java.awt.Component parent){
        int returnVal = showOpenDialog(parent);
        if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            setFilename(getSelectedFile().toString());
            try {
                ExSyncDocument esd = new ExSyncDocument(getSelectedFile().toString());
                StringBuffer messages = new StringBuffer();
                esd.messages = messages;
                setTranscription(esd.toBasicTranscription());   
                success=true;
                MessageDialog md = new MessageDialog((javax.swing.JFrame)parent, true, messages);
                md.show();
            }
            catch (SAXException se){
                showSAXErrorDialog(se, parent);
            }
            catch (org.exmaralda.partitureditor.jexmaralda.JexmaraldaException je){
                showJexmaraldaErrorDialog(je, parent);
            }
        }
        else {
            success=false;
        }         
        return success;
    }
    

    void showErrorDialog(String message){
        javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
        errorDialog.showMessageDialog(  getParent(),
                                "File could not be read.\n" + "Error message was:\n" + message,
                                "I/O  error or Transformer (configuration) error",
                                javax.swing.JOptionPane.ERROR_MESSAGE);
        success=false;                        
    }
}
