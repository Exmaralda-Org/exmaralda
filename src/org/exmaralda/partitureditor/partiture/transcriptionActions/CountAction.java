/*
 * CountDIDAAction.java
 *
 * Created on 12. Maerz 2004, 10:20
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;

/**
 *
 * @author  thomas
 */
public class CountAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of CountDIDAAction */
    public CountAction(PartitureTableWithActions t) {
        super("Count segments...", t);
    }

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("countAction!");
        table.commitEdit(true);
        count();          
    }
    
    private void count(){
         BasicTranscription bt = table.getModel().getTranscription().makeCopy();
         try{
             SegmentedTranscription st =
                     table.getAbstractSegmentation().BasicToSegmented(bt);
             StylesheetFactory sf = new StylesheetFactory(true);
             String countResult =
                sf.applyInternalStylesheetToString(
                    "/org/exmaralda/partitureditor/jexmaralda/xsl/CountSegmented.xsl", 
                    st.toXML());
             //System.out.println(countResult);
             org.exmaralda.partitureditor.exSync.swing.MessageDialog md =
                     new org.exmaralda.partitureditor.exSync.swing.MessageDialog((javax.swing.JFrame)table.parent, true, new StringBuffer(countResult));
             md.setTitle(org.exmaralda.common.helpers.Internationalizer.getString("Count result"));
             md.show();
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
