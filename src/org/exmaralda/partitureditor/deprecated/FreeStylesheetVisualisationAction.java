/*
 * FreeStylesheetVisualisationAction.java
 *
 * Created on 19. Maerz 2004, 15:12
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ExportFreeStylesheetVisualisationDialog;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  thomas
 */
public class FreeStylesheetVisualisationAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of FreeStylesheetVisualisationAction */
    public FreeStylesheetVisualisationAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Free stylesheet visualization...", icon, t);   
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("freeStylesheetVisualisationAction!");
        table.commitEdit(true);
        freeStylesheetVisualisation();        
    }
    
    private void freeStylesheetVisualisation(){
        if (table.freeStylesheetVisualisationStylesheet.length()==0){
            javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
            errorDialog.showMessageDialog(  table.parent,
                                            "No stylesheet defined in preferences", 
                                            "Error",
                                            javax.swing.JOptionPane.ERROR_MESSAGE);
            return;            
        }
        
        try {
            StylesheetFactory sf = new StylesheetFactory();
            String resultText = sf.applyExternalStylesheetToString( table.freeStylesheetVisualisationStylesheet, 
                                                                    table.getModel().getTranscription().toXML());
             ExportFreeStylesheetVisualisationDialog dialog = new ExportFreeStylesheetVisualisationDialog(resultText);
             boolean success = dialog.save(table.parent);            
        } catch (Exception e){
            javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
            errorDialog.showMessageDialog(table,
                e.getMessage(),
                "Error",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);         
            
        }
    }
    
}
