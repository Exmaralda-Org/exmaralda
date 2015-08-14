/*
 * SelectionToHTMLAction.java
 *
 * Created on 17. Juni 2003, 13:13
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.deprecated.ExportHTMLPartitureFileDialog;
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;


/**
 *
 * @author  thomas
 */
public class SelectionToHTMLAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SelectionToHTMLAction */
    public SelectionToHTMLAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Selection to HTML...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("selectionToHTMLAction!");
        table.commitEdit(true);
        selectionToHTML();        
    }
    
    private void selectionToHTML(){
        BasicTranscription newTranscription = table.getCurrentSelectionAsNewTranscription();
        int timelineStart = table.selectionStartCol;
        org.exmaralda.partitureditor.interlinearText.InterlinearText it =
            ItConverter.BasicTranscriptionToInterlinearText(newTranscription, table.getModel().getTierFormatTable(), timelineStart);
        if (table.getFrameEndPosition()>=0){((org.exmaralda.partitureditor.interlinearText.ItBundle)it.getItElementAt(0)).frameEndPosition=table.getFrameEndPosition();}
        System.out.println("Transcript converted to interlinear text.");
        table.htmlParameters.additionalStuff="";
        ExportHTMLPartitureFileDialog dialog = new ExportHTMLPartitureFileDialog(it, table.htmlParameters, table.htmlDirectory);
        boolean success = dialog.saveHTML(table);
        if (success){
            table.htmlDirectory = dialog.getFilename();
        }
    }
    
    
}
