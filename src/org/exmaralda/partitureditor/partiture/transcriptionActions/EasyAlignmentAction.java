/*
 * EditMetaInformationAction.java
 *
 * Created on 17. Juni 2003, 09:44
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import javax.swing.JDialog;
import javax.swing.JFrame;
import org.exmaralda.partitureditor.alignmentPanel.AlignmentPanel;
import org.exmaralda.partitureditor.partiture.*;
/**
 *
 * opens a dialog for editing the meta information 
 * Menu: File --> Edit meta information
 * @author  thomas
 */
public class EasyAlignmentAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditMetaInformationAction */
    public EasyAlignmentAction(PartitureTableWithActions t) {
        super("Easy alignment...", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("easyAlignmentAction!");
        table.commitEdit(true);
        easyAlignment();
    }
    
    private void easyAlignment(){
        table.setSelectedBackground(java.awt.Color.YELLOW);
        table.setSelectedForeground(java.awt.Color.BLACK);  
        JDialog alignmentDialog = new JDialog((JFrame)(table.getTopLevelAncestor()), true);
        AlignmentPanel alignmentPanel = new AlignmentPanel(table, table.player);
        alignmentDialog.add(alignmentPanel);
        alignmentDialog.pack();
        alignmentDialog.setLocationRelativeTo(table);
        alignmentDialog.setTitle("Easy Alignment");
        alignmentDialog.setVisible(true);                

        table.setSelectedForeground(table.defaultSelectionColor);                        
        table.setSelectedBackground(table.defaultSelectionBg);        
        table.transcriptionChanged=true;
        
    }
    
    
}
