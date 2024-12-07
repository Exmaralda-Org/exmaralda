/*
 * DoubleSplitAction.java
 *
 * Created on 27. Oktober 2003, 17:46
 */

package org.exmaralda.partitureditor.partiture.formatActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

/**
 *
 * @author  thomas
 */
public class UnderlineAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of DoubleSplitAction */
     public UnderlineAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Underline", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control U"));            
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("underlineAction!");
        underline();
        table.transcriptionChanged = true;        
    }
    
    private void underline(){
        int pos1 = table.getSelectionStartPosition();
        int pos2 = table.getSelectionEndPosition();
        table.commitEdit(true);
        table.getModel().underline(table.selectionStartRow, table.selectionStartCol, 
                                    pos1, pos2,
                                   table.underlineWithDiacritics, table.underlineCategory);
    }
    
    
}
