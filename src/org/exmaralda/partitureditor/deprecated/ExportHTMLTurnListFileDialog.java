/*
 * ExportHTMLTurnListDialog.java
 *
 * Created on 12. September 2002, 17:50
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;

/**
 *
 * @author  Thomas
 */
public class ExportHTMLTurnListFileDialog extends AbstractXMLSaveAsDialog {
    
    ListTranscription lt;
    TierFormatTable tft;
    
    /** Creates a new instance of ExportHTMLTurnListDialog */
    public ExportHTMLTurnListFileDialog(ListTranscription l, TierFormatTable t, String startDirectory) {
        super(false);
        lt = l;
        tft = t;
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.HTMLFileFilter());        
        setDialogTitle("Choose a HTML file for export");        
        try {
            setCurrentDirectory(new File(startDirectory));
        }
        catch (Throwable dummy){}  
        
    }
    
    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(getSelectedFile().separatorChar)){
            setFilename(getFilename() + ".html");
        }
    }
    
    public boolean saveHTML(java.awt.Component parent){
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
              lt.writeHTMLToFile(getFilename(),tft);
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
    }
    
    public boolean saveHTMLWithStylesheet(java.awt.Component parent, String pathToStylesheet){
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
            boolean externalStylesheetCouldBeUsed = lt.writeHTMLToFile(getFilename(), pathToStylesheet);  
            if (!externalStylesheetCouldBeUsed){
                javax.swing.JOptionPane dialog = new javax.swing.JOptionPane();
                String text = new String("There was a problem with " + System.getProperty("line.separator"));
                text+=pathToStylesheet + " : " + System.getProperty("line.separator");
                text+="Using internal stylesheet instead.";                    
                dialog.showMessageDialog(parent, text);                           
            }
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
        
    }
        
    
}
