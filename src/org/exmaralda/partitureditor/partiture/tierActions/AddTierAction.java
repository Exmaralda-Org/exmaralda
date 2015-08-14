/*
 * AddTierAction.java
 *
 * Created on 17. Juni 2003, 14:38
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;
/**
 *
 * @author  thomas
 */
public class AddTierAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of AddTierAction */
    public AddTierAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Add tier...", icon, t);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                        
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("addTierAction!");
        table.commitEdit(true);
        addTier();
        table.transcriptionChanged = true;        
    }    

    private void addTier(){
        NewTierDialog dialog = new NewTierDialog(table.parent, true, table.getModel().getTranscription());
        if (dialog.makeNewTier()){
            Tier newTier = dialog.getNewTier();
            if (dialog.speakertableChanged){
                table.getModel().getTranscription().getHead().setSpeakertable(dialog.getSpeakertable());
                if (table.undoEnabled){
                    UndoInformation undoInfo = new UndoInformation(table, "Add tier");
                    undoInfo.memorizeTranscription(table);
                    table.addUndo(undoInfo);
                }
            } else {
                if (table.undoEnabled){
                    UndoInformation undoInfo = new UndoInformation(table, "Add tier");
                    undoInfo.restoreType = UndoInformation.REMOVE_TIER;
                    undoInfo.restoreObject = newTier.getID();
                    table.addUndo(undoInfo);
                }
            }
            table.getModel().addTier(newTier);
        }
    }
    
    
    
}
