/*
 * AbstractXMLSaveAsDialog.java
 *
 * Created on 9. August 2001, 15:20
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import java.io.*;
import javax.swing.JOptionPane;

/**
 *
 * @author  Thomas
 * @version 
 */
public abstract class AbstractXMLSaveAsDialog extends javax.swing.JFileChooser {

    public String filename;
    public boolean success;

    /** Creates new AbstractXMLSaveAsDialog */
    public AbstractXMLSaveAsDialog() {
        this(true);
    }
    
    /** Creates new AbstractXMLSaveAsDialog */
    public AbstractXMLSaveAsDialog(boolean showDTDPanel) {
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ExmaraldaFileFilter());
        setMinimumSize(new java.awt.Dimension(400, 350));
        setPreferredSize(new java.awt.Dimension(600, 400));
        setMaximumSize(new java.awt.Dimension(800, 600));
        success=false;
    }
    /** Creates new AbstractXMLSaveAsDialog and sets start directory*/
    public AbstractXMLSaveAsDialog(String startDirectory) {
        this();
        try {
            setCurrentDirectory(new File(startDirectory));
        }
        catch (Throwable dummy){}  
    }

    public AbstractXMLSaveAsDialog(String startDirectory, boolean showDTDPanel) {
        this(showDTDPanel);
        try {
            setCurrentDirectory(new File(startDirectory));
        }
        catch (Throwable dummy){}  
    }

    public String getFilename(){
        return filename;
    }
    
    public void setFilename(String fn){
        filename = fn;
    }

    public boolean checkOverwrite(java.awt.Component parent){
        if (getSelectedFile().canRead()){
            int returnval = JOptionPane.showConfirmDialog(parent,
                                                          "File " + filename + " already exists. \n Overwrite?",
                                                          "Warning",
                                                           javax.swing.JOptionPane.OK_CANCEL_OPTION);
            if (returnval!=javax.swing.JOptionPane.OK_OPTION){return false;}                    
        }
        return true;
    }
    
    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(File.separatorChar)){
            filename = filename + ".exb";
        }
    }
    
    public void showIOErrorDialog(IOException ioe, java.awt.Component parent){
        JOptionPane.showMessageDialog(  parent,
                                        "File could not be written." + System.getProperty("line.separator")
                                        + "Error message was:" + System.getProperty("line.separator")
                                        + ioe.getLocalizedMessage(),
                                        "IO error",
                                        javax.swing.JOptionPane.ERROR_MESSAGE);
        success=false;
    }

    public void setDialogTitle(String dialogTitle) {
        String dt = org.exmaralda.common.helpers.Internationalizer.getString(dialogTitle);
        super.setDialogTitle(dt);
    }
    
    
    
    

}