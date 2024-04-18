/*
 * InsertTierAction.java
 *
 * Created on 17. Juni 2003, 14:43
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.jexmaraldaswing.NewTierDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class InsertTierAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of InsertTierAction
     * @param t
     * @param icon */
    public InsertTierAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Insert tier...", icon, t);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                        
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("insertTierAction!");
        table.commitEdit(true);
        insertTier();
        table.transcriptionChanged = true;        
    }
    
    private void insertTier(){
        int row = table.selectionStartRow;
        NewTierDialog dialog = new NewTierDialog(table.parent, true, table.getModel().getTranscription());
        // new 15-12-2021, issue #300
        // 04-11-2023, there is a bug here
        if (row>=0){
            dialog.guessNewTier(row);
        }
        if (dialog.makeNewTier()){
            Tier newTier = dialog.getNewTier();
            if (dialog.speakertableChanged){
                table.getModel().getTranscription().getHead().setSpeakertable(dialog.getSpeakertable());
                if (table.undoEnabled){
                    UndoInformation undoInfo = new UndoInformation(table, "Insert tier");
                    undoInfo.memorizeTranscription(table);
                    table.addUndo(undoInfo);
                }
            } else {
                if (table.undoEnabled){
                    UndoInformation undoInfo = new UndoInformation(table, "Insert tier");
                    undoInfo.restoreType = UndoInformation.REMOVE_TIER;
                    undoInfo.restoreObject = newTier.getID();
                    table.addUndo(undoInfo);
                }
            }
            table.getModel().insertTier(newTier, row);
            table.status("Inserted tier: " + newTier.getDisplayName());            
            if (row<=table.getFrameEndPosition()){
                table.setFrameEndPosition(table.getFrameEndPosition()+1);
            }
        }
    }
    
    
}
