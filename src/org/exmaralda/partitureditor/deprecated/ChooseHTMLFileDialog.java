/*
 * ChooseHTMLFileDialog.java
 *
 * Created on 24. September 2001, 11:48
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ChooseHTMLFileDialog extends AbstractXMLSaveAsDialog {

    /** Creates new ChooseHTMLFileDialog */
    public ChooseHTMLFileDialog() {
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.HTMLFileFilter());        
        setDialogTitle("Choose a HTML file for export");              
    }

    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(getSelectedFile().separatorChar)){
            setFilename(getFilename() + ".html");
        }
    }
    

}