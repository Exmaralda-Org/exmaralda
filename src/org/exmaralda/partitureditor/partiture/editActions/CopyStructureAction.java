/*
 * CopyTextAction.java
 *
 * Created on 17. Juni 2003, 12:39
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;


/**
 *
 * @author  thomas
 * new 18-11-2022 for issue #338
 */
public class CopyStructureAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    

    /** Creates a new instance of CopyTextAction
     * @param t
     * @param icon */
    public CopyStructureAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Copy Structure", icon, t);
        //this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("copyStructureAction!");
        copyStructureAction();        
    }
    
    private void copyStructureAction(){
        BasicTranscription newTranscription = table.getCurrentSelectionAsNewTranscription();
        table.setStructureClipboard(newTranscription);
        
    }
    
   
}
