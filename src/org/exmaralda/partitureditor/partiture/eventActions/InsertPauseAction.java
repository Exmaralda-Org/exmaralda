/*
 * EditEventAction.java
 *
 * Created on 18. Juni 2003, 11:38
 */

package org.exmaralda.partitureditor.partiture.eventActions;

import org.exmaralda.folker.application.AbstractTimeviewPartiturPlayerControl;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;


/**
 *
 * @author  thomas
 */
public class InsertPauseAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {



    /** Creates a new instance of EditEventAction */
    public InsertPauseAction(PartitureTableWithActions t, String text, javax.swing.ImageIcon icon) {
        super(text, icon, t);
        //this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
    
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("insertPauseAction!");
        insertPause();
        table.transcriptionChanged = true;        
    }
    
    private void insertPause(){
        table.insertPause();
    }
    
    
}
