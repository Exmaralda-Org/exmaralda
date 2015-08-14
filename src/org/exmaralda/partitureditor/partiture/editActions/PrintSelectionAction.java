/*
 * SelectionToRTFAction.java
 *
 * Created on 17. Juni 2003, 13:16
 */

package org.exmaralda.partitureditor.partiture.editActions;

import java.io.File;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  thomas
 */
public class PrintSelectionAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SelectionToRTFAction */
    public PrintSelectionAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Print selection...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("printSelectionAction!");
        table.commitEdit(true);
        printSelection();        
    }
    
    private void printSelection(){
        BasicTranscription newTranscription = table.getCurrentSelectionAsNewTranscription();
        int timelineStart = table.selectionStartCol;
        org.exmaralda.partitureditor.interlinearText.InterlinearText it =
            ItConverter.BasicTranscriptionToInterlinearText(newTranscription, table.getModel().getTierFormatTable(), timelineStart);
        if (table.getFrameEndPosition()>=0){((org.exmaralda.partitureditor.interlinearText.ItBundle)it.getItElementAt(0)).frameEndPosition=table.getFrameEndPosition();}
        System.out.println("Transcript converted to interlinear text.");
        java.awt.print.PrinterJob printerJob = java.awt.print.PrinterJob.getPrinterJob();
        printerJob.setJobName(new File(table.getFilename()).getName());
        if (printerJob.printDialog()){
            it.setPrintParameters(table.printParameters);
            it.trim(table.printParameters);
            it.calculatePageBreaks(table.printParameters);
            printerJob.setPrintable(it, table.pageFormat);
            try{
                System.out.println("NOW PRINTING...");
                printerJob.print();
            } catch (java.awt.print.PrinterException pe){
                JOptionPane.showMessageDialog(table.parent,
                    "Transcription could not be printed: " + pe.getLocalizedMessage(), "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
}
