/*
 * SelectionToRTFAction.java
 *
 * Created on 17. Juni 2003, 13:16
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.deprecated.ExportRTFPartitureFileDialog;
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
public class SelectionToRTFAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SelectionToRTFAction */
    public SelectionToRTFAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Selection to RTF...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("selectionToRTFAction!");
        table.commitEdit(true);
        selectionToRTF();        
    }
    
    private void selectionToRTF(){
        BasicTranscription newTranscription = table.getCurrentSelectionAsNewTranscription();
        int timelineStart = table.selectionStartCol;
        org.exmaralda.partitureditor.interlinearText.InterlinearText it =
            ItConverter.BasicTranscriptionToInterlinearText(newTranscription, table.getModel().getTierFormatTable(), timelineStart);
        if (table.getFrameEndPosition()>=0){((org.exmaralda.partitureditor.interlinearText.ItBundle)it.getItElementAt(0)).frameEndPosition=table.getFrameEndPosition();}
        System.out.println("Transcript converted to interlinear text.");
        table.rtfParameters.additionalStuff = "";
        ExportRTFPartitureFileDialog dialog = new ExportRTFPartitureFileDialog(it, table.rtfParameters, table.rtfDirectory);
        boolean success = dialog.saveRTF(table);
        if (success){
            table.rtfDirectory = dialog.getFilename();
        }
        table.rtfParameters.clearMappings();
    }
    
    
}
