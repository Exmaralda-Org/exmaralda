/*
 * ExportSVGPartitureFileDialog.java
 *
 * Created on 7. Februar 2005, 15:22
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.interlinearText.InterlinearText;
import org.exmaralda.partitureditor.interlinearText.SVGParameters;
import org.exmaralda.partitureditor.interlinearText.*;
import java.io.*;

/**
 *
 * @author  thomas
 */
public class ExportSVGPartitureFileDialog extends AbstractXMLSaveAsDialog {
    
    
    SVGAccessoryPanel accessory;    
    InterlinearText it;    
    SVGParameters param;
    
    
    /** Creates a new instance of ExportSVGPartitureFileDialog */
    public ExportSVGPartitureFileDialog(InterlinearText i, SVGParameters p) {
        super(false);
        accessory = new SVGAccessoryPanel();
        this.setAccessory(accessory);        
        it = i;
        param = p;
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.HTMLFileFilter());        
        setDialogTitle("Set the HTML file for export");              
    }
    
    /** Creates new ExportSVGPartitureFileDialog */
    public ExportSVGPartitureFileDialog(InterlinearText i, SVGParameters p, String startDirectory) {
        this(i,p);
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
    
    public boolean saveSVG(java.awt.Component parent){
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
            it.trim(param);
            it.writeSVGToFile(param,getFilename(),accessory.getSubdirectory(), accessory.getBasename());
        } catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }        
        
        return success;
    }
    
    
}
