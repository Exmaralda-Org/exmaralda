/*
 * AbstractXMLOpenDialog.java
 *
 * Created on 9. August 2001, 14:52
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.xml.sax.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class AbstractXMLOpenDialog extends javax.swing.JFileChooser {

    public boolean success;
    private String filename;
    
    /** Creates new AbstractXMLOpenDialog */
    public AbstractXMLOpenDialog() {
        super();
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ExmaraldaFileFilter());
        setMinimumSize(new java.awt.Dimension(400, 350));
        setPreferredSize(new java.awt.Dimension(600, 400));
        setMaximumSize(new java.awt.Dimension(800, 600));
        success=false;
    }

    /** Creates new AbstractXMLOpenDialog and sets start directory*/
    public AbstractXMLOpenDialog(String startDirectory) {
        this();
        try {
            setCurrentDirectory(new File(startDirectory));
        }
        catch (Throwable t){}        
    }

    public void setFilename(String fn){
        filename = fn;
    }
    
    public String getFilename(){
        return filename;
    }
    
    public void showSAXErrorDialog (SAXException se, java.awt.Component parent){
        javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
        JOptionPane.showMessageDialog(  parent,
                                        "File could not be read." + System.getProperty("line.separator")
                                        + "Error message was:" + System.getProperty("line.separator")
                                        + se.getLocalizedMessage(),
                                        "SAX error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
        success=false;    
    }
    
    public void showJexmaraldaErrorDialog(JexmaraldaException je, java.awt.Component parent){
        javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
        errorDialog.showMessageDialog(  parent,
                                        "File does not seem to be valid: " + je.getMessage(),
                                        "jexmaralda error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
        success=false;    
    }

    public void setDialogTitle(String dialogTitle) {
        String dt = org.exmaralda.common.helpers.Internationalizer.getString(dialogTitle);
        super.setDialogTitle(dt);
    }
    
}