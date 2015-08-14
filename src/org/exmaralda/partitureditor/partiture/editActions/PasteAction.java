/*
 * PasteAction.java
 *
 * Created on 17. Juni 2003, 12:46
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

/**
 *
 * @author  thomas
 */
public class PasteAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of PasteAction */
    public PasteAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Paste", icon, t);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("pasteAction!");
        paste();        
    }
    
    private void paste(){
        if (!table.isEditing) return;
        javax.swing.JTextField editingComponent = (javax.swing.JTextField)(table.getEditingComponent());
        if (editingComponent!=null){
            java.awt.datatransfer.Transferable trans = table.getToolkit().getSystemClipboard().getContents(this);
            if (trans.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)){
                try{
                    Object data = trans.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
                    String text = (String)data;
                    editingComponent.replaceSelection(text);
                } catch (java.awt.datatransfer.UnsupportedFlavorException ufe){
                } catch (java.io.IOException ioe){
                }                
            }
        }
    }
    
}
