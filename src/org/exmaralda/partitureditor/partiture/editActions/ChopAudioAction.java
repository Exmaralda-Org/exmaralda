/*
 * ChopAudioAction.java
 *
 * Created on 22. April 2005, 14:38
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.exmaralda.partitureditor.sound.*;
/**
 *
 * @author  thomas
 */
public class ChopAudioAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ChopAudioAction */
    public ChopAudioAction(PartitureTableWithActions t) {
        super("Chop audio...", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("ChopAudioAction");
        table.commitEdit(true);
        chopAudio();        
    }
    
    private void chopAudio(){
        BasicTranscription t = table.getModel().getTranscription();
        ChopAudioFileDialog dialog = new ChopAudioFileDialog(table.parent, true, t, t.getHead().getMetaInformation().getReferencedFile("wav"));
        dialog.show();
        if (!dialog.change) return;
        if (dialog.newTierCreated){
            table.getModel().addTier(dialog.newTier);
        } else if (dialog.existingTierUpdated){
            table.resetData();
        }
    }
    
}
