/*
 * SaveMessageDialog.java
 *
 * Created on 21. Maerz 2003, 13:10
 */

package org.exmaralda.partitureditor.exSync.swing;

import java.io.*;
/**
 *
 * @author  thomas
 */
public class SaveMessageDialog extends org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.AbstractXMLSaveAsDialog{
    
    StringBuffer message;
    
    /** Creates a new instance of SaveMessageDialog */
    public SaveMessageDialog(StringBuffer sb) {
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.TextFileFilter());        
        message=sb;
    }
    
    
    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(getSelectedFile().separatorChar)){
            setFilename(getFilename() + ".txt");
        }
    }    
    
    public boolean saveMessage(java.awt.Component parent){
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
            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(new File(getFilename()));
            fos.write(message.toString().getBytes("UTF-16"));
            fos.close();
            System.out.println("document written.");            
            success=true;
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
    }
    
}
