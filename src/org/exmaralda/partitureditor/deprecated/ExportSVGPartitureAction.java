/*
 * exportHTMLPartitureAction.java
 *
 * Created on 17. Juni 2003, 11:19
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.partiture.*;

/**
 * Exports the current transcription as a partiture in HTML
 * Menu: File --> Visualise --> Export HTML partiture
 * @author  thomas
 */
public class ExportSVGPartitureAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of exportHTMLPartitureAction */
    public ExportSVGPartitureAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("SVG partiture...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportSVGAction!");
        table.commitEdit(true);
        exportSVGPartiture();        
    }
    
    private void exportSVGPartiture(){
        org.exmaralda.partitureditor.interlinearText.InterlinearText it =
            ItConverter.BasicTranscriptionToInterlinearText(table.getModel().getTranscription(), table.getModel().getTierFormatTable());
        System.out.println("Transcript converted to interlinear text.");
        if (table.getFrameEndPosition()>=0){((org.exmaralda.partitureditor.interlinearText.ItBundle)it.getItElementAt(0)).frameEndPosition=table.getFrameEndPosition();}
             
        ExportSVGPartitureFileDialog dialog = new ExportSVGPartitureFileDialog(it, table.svgParameters, table.htmlDirectory);
        boolean success = dialog.saveSVG(table);
        table.htmlDirectory = dialog.getFilename();
    }
    
    
}
