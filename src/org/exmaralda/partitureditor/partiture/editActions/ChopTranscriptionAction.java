/*
 * ChopAction.java
 *
 * Created on 4. November 2004, 12:51
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
/**
 *
 * @author  thomas
 */
public class ChopTranscriptionAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction{
    

    /** Creates a new instance of ChopAction */
    public ChopTranscriptionAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Chop transcription...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("ChopTranscriptionAction");
        table.commitEdit(true);
        chopTranscription();        
    }
    
    private void chopTranscription(){
        BasicTranscription t = table.getModel().getTranscription();
        ChopDialog dialog = new ChopDialog(table.parent, true, 
            t.getBody().getCommonTimeline().getNumberOfTimelineItems());
        dialog.show();
        if (!dialog.changed) return;
        BasicTranscription[] parts = t.chop(dialog.getMaxValue());
        try {
            for (int pos=0; pos<parts.length; pos++){
                // changed 01-10-2012
                String filename =   dialog.getTargetDirectory() + System.getProperty("file.separator") 
                                    + dialog.getBaseFilename() + Integer.toString(pos+1) + ".exb";
                parts[pos].writeXMLToFile(filename, "none");
            }
            String message = Integer.toString(parts.length) + " files written to " + dialog.getTargetDirectory();
            javax.swing.JOptionPane.showMessageDialog(table.parent, message);
        } catch (IOException ioe){
            javax.swing.JOptionPane.showMessageDialog(table.parent, ioe.getLocalizedMessage());
        }
    }
}
