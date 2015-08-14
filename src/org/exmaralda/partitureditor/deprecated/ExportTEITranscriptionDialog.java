/*
 * ExportTEITranscriptionDialog.java
 *
 * Created on 12. August 2004, 18:07
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.jdom.JDOMException;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class ExportTEITranscriptionDialog extends SaveBasicTranscriptionAsDialog {
    
    /** Creates a new instance of ExportTEITranscriptionDialog */
    public ExportTEITranscriptionDialog(String startDirectory, BasicTranscription t) {
        super(startDirectory, t, false);
        setFileFilter(new ParameterFileFilter("xml", "Extensible Markup Language"));
        setDialogTitle("Export a TEI transcription");        
    }
    
    public boolean exportTEI(java.awt.Component parent) {
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
            TEIConverter tc = new TEIConverter();
            try {
                tc.writeTEIToFile(getTranscription(), getFilename());
            } catch (JDOMException ex) {
                ex.printStackTrace();
            }
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

    @Override
    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(File.separatorChar)){
            setFilename(getFilename() + ".xml");
        }
    }

    
}
