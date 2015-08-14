/*
 * SaveTierFormatTableAsDialog.java
 *
 * Created on 9. August 2001, 15:45
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.net.*;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.xml.sax.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class SaveTierFormatTableAsDialog extends AbstractXMLSaveAsDialog {

    private TierFormatTable tierFormatTable;
    
    /** Creates new SaveTierFormatTableAsDialog */
    public SaveTierFormatTableAsDialog(TierFormatTable t) {
        super();
        tierFormatTable = t;
        setDialogTitle("Save a format table to file");
        changeFileFilter();
    }

    
    /** Creates new SaveTierFormatTableAsDialog and sets start directory*/
    public SaveTierFormatTableAsDialog(String startDirectory, TierFormatTable t) {
        super(startDirectory);
        tierFormatTable = t;
        setDialogTitle("Save a format table to file");
        changeFileFilter();
    }
    
    public void setTierFormatTable(TierFormatTable t){
        tierFormatTable=t;
    }
    
    public boolean saveTierFormatTableAs(java.awt.Component parent){
        boolean proceed = false;
        while (!proceed){
            int returnVal = showSaveDialog(parent);
            if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                checkExtension();
                proceed=checkOverwrite(parent);
            }
            else {success = false; return success;}
        }
        // REMOVED FOR VERSION 1.2.1. (21-Oct-2001)
        /*String DTDType = getDTDType();
        String DTDPath = getDTDPath();*/
        try {
             /*if (DTDType.equals("none")){*/
                tierFormatTable.writeXMLToFile(getFilename(),"none");                 
             /*} else if (DTDType.equals("system")){
                tierFormatTable.writeXMLToFile(getFilename(),DTDPath);
             } else {
                tierFormatTable.writeXMLToFile(getFilename(),"public");
             }*/
             success=true;
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
    }

    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(File.separatorChar)){
            filename = filename + ".exf";
        }
    }

    private void changeFileFilter() {
        String[] suff = {"exf", "xml"};
        ParameterFileFilter formatFileFilter = new ParameterFileFilter(suff, "EXMARaLDA Format Table (*.exf, *.xml)");
        this.setFileFilter(formatFileFilter);
    }

     
}