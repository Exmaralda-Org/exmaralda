/*
 * ImportEAFFileDialog.java
 *
 * Created on 12. November 2003, 17:39
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.convert.ELANConverter;
import java.io.*;
import java.net.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import javax.swing.JProgressBar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;

/**
 *
 * @author  thomas
 */
public class ImportEAFFileDialog extends OpenBasicTranscriptionDialog {
    
    JProgressBar progBar;
    
    /** Creates a new instance of ImportEAFFileDialog */
    public ImportEAFFileDialog(String startDirectory) {
        super(startDirectory);
        ParameterFileFilter eafFilter = new ParameterFileFilter("eaf", "ELAN Annotation File");
        setFileFilter(eafFilter);
        setDialogTitle("Import an ELAN file");           
    }
    
    public void setProgressBar(JProgressBar pb){
        progBar = pb;
    }
    
    public boolean importELAN(java.awt.Component parent){
        int returnVal = showOpenDialog(parent);
        if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            setFilename(new String(getSelectedFile().toString()));
            try {
                ELANConverter ec = new ELANConverter();
                ec.setProgressBar(progBar);
                setTranscription(ec.readELANFromFile(getFilename()));
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
    
    

