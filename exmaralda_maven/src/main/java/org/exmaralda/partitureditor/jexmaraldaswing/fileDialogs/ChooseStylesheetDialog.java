/*
 * ChooseStylesheetDialog.java
 *
 * Created on 6. Oktober 2003, 11:11
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

/**
 *
 * @author  thomas
 */
public class ChooseStylesheetDialog extends javax.swing.JFileChooser {
    
    
    /** Creates a new instance of ChooseStylesheetDialog */
    public ChooseStylesheetDialog() {
        super();
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.XSLFileFilter());
        setDialogTitle("Choose a stylesheet");
    }
    
    public ChooseStylesheetDialog(String startDirectory) {
        this();
        try {
            this.setCurrentDirectory(new java.io.File(startDirectory));
        } catch (Exception e){
        }
    }
    
    
}
