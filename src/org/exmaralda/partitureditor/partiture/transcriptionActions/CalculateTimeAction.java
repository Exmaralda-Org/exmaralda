/*
 * CountDIDAAction.java
 *
 * Created on 12. Maerz 2004, 10:20
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  thomas
 */
public class CalculateTimeAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of CountDIDAAction */
    public CalculateTimeAction(PartitureTableWithActions t) {
        super("Calculate annotated time...", t);
    }

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("calculateTimeAction!");
        table.commitEdit(true);
        calculateTime();
    }
    
    private void calculateTime(){
         BasicTranscription bt = table.getModel().getTranscription().makeCopy();
         bt.getBody().getCommonTimeline().completeTimes();
         String html = "<html><head></head><body><table>";
         html+="<tr><th>Tier</th><th>Time</th></tr>";
         for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
             Tier tier = bt.getBody().getTierAt(pos);
             double time = tier.calculateEventTime(bt.getBody().getCommonTimeline());
             html+= "<tr><td>" + tier.getDescription(bt.getHead().getSpeakertable()) + "</td>";
             html+= "<td>" + TimeStringFormatter.formatMiliseconds(time*1000.0, 2)  + "</td></tr>";
         }
         html+="</table></html>";
         org.exmaralda.partitureditor.exSync.swing.MessageDialog md =
                 new org.exmaralda.partitureditor.exSync.swing.MessageDialog((javax.swing.JFrame)table.parent, true, new StringBuffer(html));
         md.setTitle(org.exmaralda.common.helpers.Internationalizer.getString("Annotated time"));
         md.show();

    }
    
}
