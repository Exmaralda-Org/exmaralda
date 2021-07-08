/*
 * PasteAction.java
 *
 * Created on 17. Juni 2003, 12:46
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaraldaswing.LowerUpperCaseDialog;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class LowerUpperCaseAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of PasteAction */
    public LowerUpperCaseAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("To Lower/Upper Case...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("LowerUpperCaseAction!");
        lowerUpperCase();        
    }
    
    private void lowerUpperCase(){
        LowerUpperCaseDialog lucd = new LowerUpperCaseDialog(table.parent, true);
        lucd.setLocationRelativeTo(table);
        lucd.setVisible(true);
        
        if (!(lucd.change)) return;
        
        boolean toLower = lucd.toLowerCase();
        
        int start=0;
        int end=table.getModel().getTranscription().getBody().getNumberOfTiers();
        boolean aSeriesOfRowsIsSelected = (table.selectionStartRow != -1) && (table.selectionEndRow != -1) && (table.selectionStartCol<0);
        if (aSeriesOfRowsIsSelected){
            start = table.selectionStartRow;
            end = table.selectionEndRow + 1;
        }
        for (int row=start; row<end; row++){
            Tier tier = table.getModel().getTranscription().getBody().getTierAt(row);
            for (int pos=0; pos<tier.getNumberOfEvents(); pos++){
                Event event = tier.getEventAt(pos);
                String description = event.getDescription();
                if (toLower){
                    event.setDescription(description.toLowerCase());
                } else {
                    event.setDescription(description.toUpperCase());                    
                }
                table.getModel().fireValueChanged(row, pos);
            }
        }
                
    }
    
}
