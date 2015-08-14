/*
 * ExtendRightAction.java
 *
 * Created on 18. Juni 2003, 11:35
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
public class ExtendRightAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExtendRightAction */
    public ExtendRightAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Extend to the right", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift RIGHT"));          
    }
    
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("extendRightAction!");
        extendRight();
        table.transcriptionChanged = true;        
    }
    
    private void extendRight(){
        table.commitEdit(true);
        if (table.undoEnabled){
            // Undo information
            int lower = table.getModel().lower(table.selectionStartCol);
            int upper = table.getModel().upper(table.selectionStartCol + table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol)+1);
            UndoInformation undoInfo = new UndoInformation(table, "Extend right");
            undoInfo.memorizeRegion(table, lower, upper);
            table.addUndo(undoInfo);
            //System.out.println("Added undo extend right");
            // end undo information
        }
        table.getModel().extendRight(table.selectionStartRow, table.selectionStartCol);
    }
    
    
}
