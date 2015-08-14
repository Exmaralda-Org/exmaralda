/*
 * DoubleSplitAction.java
 *
 * Created on 27. Oktober 2003, 17:46
 */

package org.exmaralda.partitureditor.partiture.eventActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;

/**
 *
 * @author  thomas
 */
public class DoubleSplitAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of DoubleSplitAction */
     public DoubleSplitAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Double split", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 3"));            
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("doubleSplitAction!");
        doubleSplit();
        table.transcriptionChanged = true;        
    }
    
    private void doubleSplit(){
        int pos1 = table.getSelectionStartPosition();
        int pos2 = table.getSelectionEndPosition();
        table.commitEdit(true);
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(table.selectionStartCol);
            int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol));
            //System.out.println("upper " + upper + " lower " + lower);
            UndoInformation undoInfo = new UndoInformation(table, "Double split");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo double split");
            // end undo information
        }
        table.getModel().doubleSplit(table.selectionStartRow, table.selectionStartCol, pos1, pos2);
    }
    
    
}
