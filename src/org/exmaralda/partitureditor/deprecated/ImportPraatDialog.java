/*
 * ImportTASXDialog.java
 *
 * Created on 23. November 2001, 15:04
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.EncodingDetector;
import java.awt.HeadlessException;
import java.io.*;
import java.net.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.jexmaralda.*;


/**
 *
 * @author  Thomas
 * @version 
 */
public class ImportPraatDialog extends OpenBasicTranscriptionDialog {

    private String[] encodings = {"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE"};

    /** Creates new ImportPraatDialog */
    public ImportPraatDialog(String startDirectory) {
        super(startDirectory);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter("textgrid", "Praat TextGrid"));
        setDialogTitle("Import a Praat TextGrid");        
    }
    
    /*public boolean importPraat(java.awt.Component parent){
        int returnVal = showOpenDialog(parent);
        if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            setFilename(new String(getSelectedFile().toString()));
            try {
                setTranscription(new PraatConverter().readPraatFromFile(getFilename()));
                success=true;
            }
            catch (IOException ioe){
                javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
                errorDialog.showMessageDialog(  parent,
                                        "File could not be read.\n" + "Error message was:\n" + ioe.getMessage(),
                                        "IO error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
                success=false;            
            }
        }
        else {
            success=false;
        }         
        return success;
    }*/
    
    // changed for version 1.3.4 because Praat now uses variable encodings
    public boolean importPraat(java.awt.Component parent){
        int returnVal = showOpenDialog(parent);
        if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            try {
                setFilename(getSelectedFile().toString());
                String encoding = "";
                encoding = EncodingDetector.detectEncoding(getSelectedFile());
                if (encoding.length()==0){
                    Object o = javax.swing.JOptionPane.showInputDialog(this, 
                                "Cannot detect the file encoding. Please select", 
                                "Choose a file encoding",
                                javax.swing.JOptionPane.PLAIN_MESSAGE,
                                null,
                                encodings,
                                "ISO-8859-1");
                    encoding = (String)o;
                }
                setTranscription(new PraatConverter().readPraatFromFile(getFilename(), encoding));
                success=true;
            } catch (HeadlessException ex) {
                ex.printStackTrace();
            } catch (IOException ioe) {
                javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
                errorDialog.showMessageDialog(  parent,
                                        "File could not be read.\n" + "Error message was:\n" + ioe.getMessage(),
                                        "IO error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
                success=false;            
            }
        }
        else {
            success=false;
        }         
        return success;
    }    
    

}
