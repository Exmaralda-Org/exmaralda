/*
 * CutAction.java
 *
 * Created on 17. Juni 2003, 12:49
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import org.exmaralda.common.helpers.Internationalizer;

/**
 *
 * @author  thomas
 */
public class UndoAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of CutAction */
    public UndoAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super(Internationalizer.getString("Undo"), icon, t);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("undoAction!");
        undo();
    }    
    
    private void undo(){
        table.undo();
    }
    
}
