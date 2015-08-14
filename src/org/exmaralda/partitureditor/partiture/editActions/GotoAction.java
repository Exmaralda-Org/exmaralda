/*
 * ChopAudioAction.java
 *
 * Created on 22. April 2005, 14:38
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.GotoDialog;
/**
 *
 * @author  thomas
 */
public class GotoAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    GotoDialog dialog;

    /** Creates a new instance of ChopAudioAction */
    public GotoAction(PartitureTableWithActions t) {
        super("Go to...", t);
        dialog = new GotoDialog(t.parent, true, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("GotoAction");
        table.commitEdit(true);

        goTo();
    }
    
    private void goTo(){
        dialog.setVisible(true);
    }
    
}
