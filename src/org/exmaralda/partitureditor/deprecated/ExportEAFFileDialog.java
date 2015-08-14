/*
 * ExportEAFFileDialog.java
 *
 * Created on 13. November 2003, 12:30
 */

package org.exmaralda.partitureditor.deprecated;

import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.convert.ELANConverter;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class ExportEAFFileDialog extends SaveBasicTranscriptionAsDialog {
    
    /** Creates a new instance of ExportEAFFileDialog */
    public ExportEAFFileDialog(String startDirectory, BasicTranscription t) {
        super(startDirectory, t, false);
        ParameterFileFilter eafFilter = new ParameterFileFilter("eaf", "ELAN Annotation File");
        setFileFilter(eafFilter);
        setDialogTitle("Export an ELAN  file");        
    }

    public boolean exportELAN(java.awt.Component parent) {
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
            ELANConverter ec = new ELANConverter();
            ec.writeELANToFile(getTranscription(), getFilename());
        }
        catch (SAXException se) {
                JOptionPane.showMessageDialog(  parent,
                                        "File could not be written.\n" + "Error message was:\n" + se.getMessage(),
                                        "SAX error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
                success=false;                                    
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        catch (javax.xml.parsers.ParserConfigurationException pce){
                JOptionPane.showMessageDialog(  parent,
                                        "File could not be written.\n" + "Error message was:\n" + pce.getMessage(),
                                        "Parser configuration error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
                success=false;                        
        }
        catch (javax.xml.transform.TransformerConfigurationException tce){
                JOptionPane.showMessageDialog(  parent,
                                        "File could not be written.\n" + "Error message was:\n" + tce.getMessage(),
                                        "Transformer configuration error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
                success=false;                        
        }
        catch (javax.xml.transform.TransformerException te){
                JOptionPane.showMessageDialog(  parent,
                                        "File could not be written.\n" + "Error message was:\n" + te.getMessage(),
                                        "Transformer  error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
                success=false;                        
        }
        return success;
        
    }
    
    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(getSelectedFile().separatorChar)){
            filename = filename + ".eaf";
        }
    }    
    
}
