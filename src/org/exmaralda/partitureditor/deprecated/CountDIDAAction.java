/*
 * CountDIDAAction.java
 *
 * Created on 12. Maerz 2004, 10:20
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.fsm.*;
import java.util.*;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class CountDIDAAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of CountDIDAAction */
    public CountDIDAAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Count segments", icon, t); 
    }

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("countDIDAAction!");
        table.commitEdit(true);
        countDIDA();          
    }
    
    private void countDIDA(){
         BasicTranscription bt = table.getModel().getTranscription().makeCopy();
         try{
             SegmentedTranscription st = new org.exmaralda.partitureditor.jexmaralda.segment.DIDASegmentation(table.didaFSM).BasicToSegmented(bt);
             String countResult = st.getBody().countSegments(st.getHead().getSpeakertable());
             org.exmaralda.partitureditor.exSync.swing.MessageDialog md = new org.exmaralda.partitureditor.exSync.swing.MessageDialog((javax.swing.JFrame)table.parent, true, new StringBuffer(countResult));
             md.setTitle(org.exmaralda.common.helpers.Internationalizer.getString("Count result"));
             md.show();
         } catch (SAXException se){
             //processSAXException(se);
         } catch (FSMException fsme){
             //processFSMException(fsme);
         }                 
    }
    
}
