/*
 * ReexportHTMLAction.java
 *
 * Created on 17. Juni 2003, 11:53
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;
import java.io.*;

/**
 *
 * @author  thomas
 */
public class ReexportHTMLAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ReexportHTMLAction */
    public ReexportHTMLAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Reexport HTML partiture", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("reexportHTMLAction!");
        table.commitEdit(true);
        reexportHTML();        
    }
    
    private void reexportHTML(){
        org.exmaralda.partitureditor.interlinearText.InterlinearText it =
            ItConverter.BasicTranscriptionToInterlinearText(table.getModel().getTranscription(), table.getModel().getTierFormatTable());
        System.out.println("Transcript converted to interlinear text.");
        if (table.htmlParameters.getWidth()>0) {
            it.trim(table.htmlParameters);
        }
        if (table.htmlParameters.prependAdditionalInformation){
            table.htmlParameters.additionalStuff = table.getModel().getTranscription().getHead().toHTML();
        }
        try{
            it.writeHTMLToFile(table.htmlDirectory,table.htmlParameters);
        } catch (java.io.IOException ioe){
            javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
                errorDialog.showMessageDialog(  table,
                "File could not be written.\n" + "Error message was:\n" + ioe.getLocalizedMessage(),
                "IO error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        table.htmlParameters.additionalStuff="";
    }
    
    
}
