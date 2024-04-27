/*
 * CopyTextAction.java
 *
 * Created on 17. Juni 2003, 12:39
 */

package org.exmaralda.partitureditor.partiture.editActions;

import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.partiture.*;


/**
 *
 * @author  thomas
 * new 18-11-2022 for issue #338
 */
public class PasteStructureAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    

    /** Creates a new instance of CopyTextAction
     * @param t
     * @param icon */
    public PasteStructureAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Paste Structure", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("pasteStructureAction!");
        pasteStructureAction();        
    }
    
    private void pasteStructureAction(){
        if (table.getStructureClipboard()==null){
            JOptionPane.showMessageDialog(table, "No structure in clipboard");
            return;
        }
        table.pasteStructureClipboard();
        
    }
    
   
}
