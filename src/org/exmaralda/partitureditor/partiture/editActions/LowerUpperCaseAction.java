/*
 * PasteAction.java
 *
 * Created on 17. Juni 2003, 12:46
 */

package org.exmaralda.partitureditor.partiture.editActions;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaraldaswing.LowerUpperCaseDialog;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class LowerUpperCaseAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    LowerUpperCaseDialog lucd;

    /** Creates a new instance of PasteAction */
    public LowerUpperCaseAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("To Lower/Upper Case...", icon, t);
        lucd = new LowerUpperCaseDialog(table.parent, true);        
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("LowerUpperCaseAction!");
        lowerUpperCase();        
    }
    
    private void lowerUpperCase(){
        lucd.setLocationRelativeTo(table);
        lucd.setVisible(true);
        
        if (!(lucd.change)) return;
        
        boolean toLower = lucd.toLowerCase();
        
        if (table.aSeriesOfCellsIsSelected || table.aRectangleOfEventsIsSelected || (table.aSingleCellIsSelected && (!table.isEditing))){
            int startRow = table.selectionStartRow;
            int endRow = table.selectionEndRow;
            int startCol = table.selectionStartCol;
            int endCol = table.selectionEndCol;
            for (int row=startRow; row<=endRow; row++){
                for (int col=startCol; col<=endCol; col++){
                    try {
                        Event event = table.getModel().getEvent(row, col);
                        if (event!=null){
                            String description = event.getDescription();
                            if (toLower){
                                event.setDescription(description.toLowerCase());
                            } else {
                                event.setDescription(description.toUpperCase());                    
                            }
                            table.getModel().fireValueChanged(row, col);                            
                        }
                    } catch (JexmaraldaException ex) {
                        Logger.getLogger(LowerUpperCaseAction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return;        
        }
        
        
        if (table.aSingleCellIsSelected && (table.isEditing)){
            int textStartPos = table.getSelectionStartPosition();
            int textEndPos = table.getSelectionEndPosition();
            if (textStartPos<0 || textStartPos==textEndPos || textEndPos<0) return;
            try {
                Event event = table.getModel().getEvent(table.selectionStartRow, table.selectionStartCol);
                String leftPart = event.getDescription().substring(0,textStartPos);
                String middlePart = event.getDescription().substring(textStartPos, textEndPos);
                String rightPart = event.getDescription().substring(textEndPos);
                String newMiddlePart = "";
                if (toLower){
                    newMiddlePart = middlePart.toLowerCase();
                } else {
                    newMiddlePart = middlePart.toUpperCase();
                }
                event.setDescription(leftPart + newMiddlePart + rightPart);
                table.getModel().fireValueChanged(table.selectionStartRow, table.selectionStartCol);         
            } catch (JexmaraldaException ex) {
                Logger.getLogger(LowerUpperCaseAction.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return;
        }
        
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
                
        if (toLower){
            table.status("Changed upper to lower case. ");
        } else {
            table.status("Changed lower to upper case. ");
        }
    }
}
