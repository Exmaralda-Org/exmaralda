/*
 * EditMetaInformationAction.java
 *
 * Created on 17. Juni 2003, 09:44
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.alignmentPanel.AlignmentPanel;
import org.exmaralda.partitureditor.partiture.*;
/**
 *
 * opens a dialog for editing the meta information 
 * Menu: File --> Edit meta information
 * @author  thomas
 */
public class EasyAlignmentAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditMetaInformationAction
     * @param t */
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
        if (table.getModel().getTranscription().getHead().getMetaInformation().getReferencedFiles().isEmpty()
                || table.getModel().getTranscription().getHead().getMetaInformation().getReferencedFile().length()==0){
            JOptionPane.showMessageDialog(table, "No media file associated.\nUse 'Transcription > Recordings...' to associate a media file.");
            return;
        }
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
