/*
 * MakeSyllableStructureTierAction.java
 *
 * Created on 22. April 2004, 11:48
 */

package org.exmaralda.partitureditor.partiture.sfb538Actions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
/**
 *
 * @author  thomas
 */
public class MakeSyllableStructureTierAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of MakeSyllableStructureTierAction */
    public MakeSyllableStructureTierAction(PartitureTableWithActions t) {
        super("[SFB 538] E3: Insert Syllable Structure tier", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("makeSyllableStructureTierAction!");
        table.commitEdit(true);
        makeSyllableStructureTier();
        table.transcriptionChanged = true;        
    }
    
    private void makeSyllableStructureTier(){
        int row = table.selectionStartRow;
        if ((row<0) || (row>=table.getModel().getTranscription().getBody().getNumberOfTiers())) return;
        Tier phoneticTier = table.getModel().getTier(row);
        try {
            Tier newTier = new SyllableStructure().makeSyllableStructureTier(phoneticTier);
            String speakerAbb = "";
            if (newTier.getSpeaker()!=null){
                speakerAbb = table.getModel().getTranscription().getHead()
                                .getSpeakertable().getSpeakerWithID(newTier.getSpeaker()).getAbbreviation();
            }
            String display = speakerAbb + " [syll]";
            newTier.setDisplayName(display);
            String id = table.getModel().getTranscription().getBody().getFreeID();
            newTier.setID(id);
            if (row+1<table.getModel().getNumRows()){
                table.getModel().insertTier(newTier, row+1);
            } else {
                table.getModel().addTier(newTier);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
