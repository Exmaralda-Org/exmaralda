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

/**
 *
 * @author  thomas
 */
public class CutAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of CutAction */
    public CutAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Cut", icon, t);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("cutAction!");
        cut();        
    }    
    
    private void cut(){
        if (!table.isEditing) return;
        javax.swing.JTextField editingComponent = (javax.swing.JTextField)(table.getEditingComponent());
        if (editingComponent!=null){
            String text = editingComponent.getSelectedText();
            java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection(text);
            table.getToolkit().getSystemClipboard().setContents(ss,ss);
            int start = editingComponent.getSelectionStart();
            int end = editingComponent.getSelectionEnd();
            String oldText = editingComponent.getText();
            String newText = oldText.substring(0,start) + oldText.substring(end);
            editingComponent.setText(newText);
            editingComponent.setCaretPosition(start);
        }        
    }
    
}
