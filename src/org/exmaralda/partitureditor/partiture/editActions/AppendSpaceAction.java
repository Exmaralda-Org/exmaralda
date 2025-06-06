/*
 * MakeSyllableStructureTierAction.java
 *
 * Created on 22. April 2004, 11:48
 */

package org.exmaralda.partitureditor.partiture.editActions;

import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class AppendSpaceAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of MakeSyllableStructureTierAction */
    public AppendSpaceAction(PartitureTableWithActions t) {
        super("Append spaces", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("appendSpaceAction!");
        table.commitEdit(true);
        appendSpace();
        table.transcriptionChanged = true;        
    }
    
    private void appendSpace(){
        BasicTranscription bt = table.getModel().getTranscription();
        int countReplacements = 0;
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            Tier tier = bt.getBody().getTierAt(pos);
            for (int i=0; i<tier.getNumberOfEvents(); i++){
                Event event = tier.getEventAt(i);
                if (!event.getDescription().endsWith(" ")){
                    event.setDescription(event.getDescription() + " ");
                    countReplacements++;
                }
            }
        }
        JOptionPane.showMessageDialog(table, "<html><b>" + Integer.toString(countReplacements) + "</b> spaces appended at event boundaries.</html>");
        table.getModel().fireFormatReset();
    }

}
