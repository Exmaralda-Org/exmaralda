/*
 * CountDIDAAction.java
 *
 * Created on 12. Maerz 2004, 10:20
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaraldaswing.WordListDialog;

/**
 *
 * @author  thomas
 */
public class WordlistAction extends AbstractFSMSegmentationAction {
    
    WordListDialog wordListDialog;

    /** Creates a new instance of CountDIDAAction */
    public WordlistAction(PartitureTableWithActions t) {
        super("Word list...", t);
        wordListDialog = new WordListDialog((JFrame)(table.getTopLevelAncestor()), false);
        wordListDialog.addSearchResultListener(table);
        wordListDialog.refreshButton.setAction(this);
        wordListDialog.refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Reformat.gif"))); // NOI18N
        wordListDialog.refreshButton.setText("Refresh");

    }

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("wordlistAction!");
        table.commitEdit(true);
        wordlist();
    }
    
    private void wordlist(){
         BasicTranscription bt = table.getModel().getTranscription().makeCopy();
         try{
             SegmentedTranscription st =
                     table.getAbstractSegmentation().BasicToSegmented(bt);
             wordListDialog.setTranscription(st);
             java.awt.Dimension dialogSize = wordListDialog.getPreferredSize();
             java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
             wordListDialog.setLocation(screenSize.width - dialogSize.width, 0);
             wordListDialog.setVisible(true);
             wordListDialog.requestFocusInWindow();
         } catch (Exception ex){
            int optionChosen = JOptionPane
                    .showConfirmDialog(table, "Segmentation Error(s):\n " + ex.getLocalizedMessage() + "\nEdit errors?",
                    "Segmentation Error(s)", JOptionPane.OK_CANCEL_OPTION);
            if (optionChosen==JOptionPane.OK_OPTION){
                table.getSegmentationErrorsAction.actionPerformed(null);
            }
         }                 
    }
    
}
