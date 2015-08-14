/*
 * SaveMessageDialog.java
 *
 * Created on 21. Maerz 2003, 13:10
 */

package org.exmaralda.partitureditor.search;

import java.io.*;
import java.util.*;


/**
 *
 * @author  thomas
 */
public class SaveResultDialog extends org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.AbstractXMLSaveAsDialog{
    
    Vector searchResults;
    
    /** Creates a new instance of SaveMessageDialog */
    public SaveResultDialog(Vector v) {
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.TextFileFilter());         
        searchResults=v;
    }
    
    
    @Override
    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(File.separatorChar)){
            setFilename(getFilename() + ".txt");
        }
    }    
    
    public boolean saveResults(java.awt.Component parent){
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
            for (int pos=0; pos<searchResults.size(); pos++){
                EventSearchResult esr = (EventSearchResult)(searchResults.elementAt(pos));
                String text = esr.toString() + System.getProperty("line.separator");
                fos.write(text.getBytes("UTF-16"));
                
            }
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
