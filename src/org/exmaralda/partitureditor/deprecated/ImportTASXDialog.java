/*
 * ImportTASXDialog.java
 *
 * Created on 23. November 2001, 15:04
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.convert.TASXConverter;
import java.io.*;
import java.net.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
/**
 *
 * @author  Thomas
 * @version 
 */
public class ImportTASXDialog extends OpenBasicTranscriptionDialog {

    /** Creates new ImportTASXDialog */
    public ImportTASXDialog(String startDirectory) {
        super(startDirectory);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter("xml", "Extensible Markup Language"));
        setDialogTitle("Import a TASX file");        
    }
    
    public boolean importTASX(java.awt.Component parent){
        int returnVal = showOpenDialog(parent);
        if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            setFilename(new String(getSelectedFile().toString()));
            try {
                TASXConverter tc = new TASXConverter();
                setTranscription(tc.readTASXFromFile(getFilename()));
                success=true;
            }
            catch (SAXException se){
                showSAXErrorDialog(se, parent);
            }

            catch (org.exmaralda.partitureditor.jexmaralda.JexmaraldaException je){
                showJexmaraldaErrorDialog(je, parent);
            }            catch (IOException ioe){
                showErrorDialog(ioe.getMessage());
            }
            catch (ParserConfigurationException pce){
                showErrorDialog(pce.getMessage());
            }
            catch (TransformerConfigurationException tce){
                showErrorDialog(tce.getMessage());                
            }
            catch (TransformerException te){
                showErrorDialog(te.getMessage());
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
