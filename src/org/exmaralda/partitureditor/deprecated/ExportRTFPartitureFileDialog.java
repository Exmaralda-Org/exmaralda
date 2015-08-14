/*
 * ExportRTFPartitureFileDialog.java
 *
 * Created on 24. September 2001, 12:19
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.interlinearText.RTFParameters;
import org.exmaralda.partitureditor.interlinearText.InterlinearText;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.exmaralda.partitureditor.interlinearText.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class ExportRTFPartitureFileDialog extends AbstractXMLSaveAsDialog {

    private InterlinearText it;
    private RTFParameters param;
    private javax.swing.JButton editPartiturParametersButton;

    /** Creates new ExportRTFPartitureFileDialog */
    public ExportRTFPartitureFileDialog(InterlinearText i, RTFParameters p) {
        super(false);
        it = i;
        param = p;
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.deprecated.RTFFileFilter());
        setDialogTitle("Set the RTF file for export");              
    }

    /** Creates new ExportRTFPartitureFileDialog */
    public ExportRTFPartitureFileDialog(InterlinearText i, RTFParameters p, String startDirectory) {
        this(i,p);
        try {
            setCurrentDirectory(new File(startDirectory));
        }
        catch (Throwable dummy){}  
    }

    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(getSelectedFile().separatorChar)){
            setFilename(getFilename() + ".rtf");
        }
    }
    
    public boolean saveRTF(java.awt.Component parent){
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
            System.out.println("Beginning trim");
            it.trim(param);
            if (param.makePageBreaks){it.calculatePageBreaks(param);}
            if (param.glueAdjacent){
                it.glueAdjacentItChunks(param.glueEmpty, param.criticalSizePercentage);
            }
            it.writeTestRTF(getFilename(),param);
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
    }
    
}