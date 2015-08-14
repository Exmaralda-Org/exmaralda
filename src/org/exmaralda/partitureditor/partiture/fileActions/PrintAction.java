/*
 * PrintAction.java
 *
 * Created on 17. Juni 2003, 09:19
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JOptionPane;


/**
 * Sends the transcription to a printer
 * Menu: File --> Print
 * @author  thomas
 */
public class PrintAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of PrintAction */
    public PrintAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Print...", icon, t);        
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                                
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        table.commitEdit(true);
        System.out.println("printAction!");
        printTranscription();
    }

    
    private void printTranscription(){
        org.exmaralda.partitureditor.interlinearText.InterlinearText it =
        ItConverter.BasicTranscriptionToInterlinearText(table.getModel().getTranscription(), table.getModel().getTierFormatTable());
        System.out.println("Transcript converted to interlinear text.");
        if (table.getFrameEndPosition()>=0){
            ((org.exmaralda.partitureditor.interlinearText.ItBundle)it.getItElementAt(0)).frameEndPosition=table.getFrameEndPosition();
        }
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
                table.status("Transcription printed");

            } catch (java.awt.print.PrinterException pe){
                JOptionPane.showMessageDialog(table.parent,
                    "Transcription could not be printed: " + pe.getLocalizedMessage(), "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
        
    }
    
}
