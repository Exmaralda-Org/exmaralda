/*
 * ExportTASXDialog.java
 *
 * Created on 23. November 2001, 13:52
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.convert.TASXConverter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ExportTASXDialog extends SaveBasicTranscriptionAsDialog {

    /** Creates new ExportTASXDialog */
    public ExportTASXDialog(String startDirectory, BasicTranscription t) {
        super(startDirectory, t, false);
        setDialogTitle("Export a TASX file");        
    }

    public boolean exportTASX(java.awt.Component parent) {
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
            TASXConverter tc = new TASXConverter();
            tc.writeTASXToFile(getTranscription(), getFilename());
        }
        catch (SAXException se) {
               javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
                errorDialog.showMessageDialog(  parent,
                                        "File could not be written.\n" + "Error message was:\n" + se.getMessage(),
                                        "SAX error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
                success=false;                                    
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        catch (javax.xml.parsers.ParserConfigurationException pce){
               javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
                errorDialog.showMessageDialog(  parent,
                                        "File could not be written.\n" + "Error message was:\n" + pce.getMessage(),
                                        "Parser configuration error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
                success=false;                        
        }
        catch (javax.xml.transform.TransformerConfigurationException tce){
               javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
                errorDialog.showMessageDialog(  parent,
                                        "File could not be written.\n" + "Error message was:\n" + tce.getMessage(),
                                        "Transformer configuration error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
                success=false;                        
        }
        catch (javax.xml.transform.TransformerException te){
               javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
                errorDialog.showMessageDialog(  parent,
                                        "File could not be written.\n" + "Error message was:\n" + te.getMessage(),
                                        "Transformer  error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
                success=false;                        
        }
        return success;
        
    }
    
}
