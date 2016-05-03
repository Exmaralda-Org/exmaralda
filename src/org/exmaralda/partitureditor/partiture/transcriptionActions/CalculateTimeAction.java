/*
 * CountDIDAAction.java
 *
 * Created on 12. Maerz 2004, 10:20
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;

import java.util.Map;
import java.util.HashMap;


/**
 *
 * @author  thomas
 * @author  tpirinen
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
         Timeline commonTimeline = bt.getBody().getCommonTimeline();
         commonTimeline.completeTimes();
         String html = "<html><head></head><body>\n";
         // calculate per tier
         html += "<h1>Tiers</h1>\n";
         html += "<table>\n";
         html+="<tr><th>Tier</th><th>Time</th></tr>\n";
         for (int i = 0; i < bt.getBody().getNumberOfTiers(); i++){
             Tier tier = bt.getBody().getTierAt(i);
             double time = 
                 tier.calculateEventTime(commonTimeline);
             html+= "<tr><td>" + 
                 tier.getDescription(bt.getHead().getSpeakertable()) + "</td>";
             html+= "<td>" + 
                 TimeStringFormatter.formatMiliseconds(time*1000.0, 2)  + 
                 "</td></tr>\n";
         }
         // calculate per label etc.
         html += "</table>\n";
         html += "<h2>Labels (per Tier)</h2>\n";
         html += "<table>\n";
         html += "<tr><th>Label</th><th>Time</th></tr>\n";
         Map<String, Double> totalLabelTimes = new HashMap<String, Double>();
         for (int i = 0; i < bt.getBody().getNumberOfTiers(); i++) {
            Tier tier = bt.getBody().getTierAt(i);
            System.out.println(tier.getType());
            if (tier.getType().equals("a")) {
                html += "<tr><th colspan='2'>" + 
                    tier.getDescription(bt.getHead().getSpeakertable()) + 
                    "</th></tr>\n";
                Map<String, Double> labelTimes = new HashMap<String, Double>();
                for (int j = 0; j < tier.getNumberOfEvents(); j++) {
                    Event e = tier.getEventAt(j);
                    try {
                        double timestart = commonTimeline.getTimelineItemWithID(
                                e.getStart()).getTime();
                        double timeend = commonTimeline.getTimelineItemWithID(
                                e.getEnd()).getTime();
                        double time = timeend - timestart;
                        String descr = e.getDescription().trim();
                        if (labelTimes.containsKey(descr)) {
                            double newtime = labelTimes.get(descr) + time;
                            labelTimes.put(descr, newtime);
                        } else {
                            labelTimes.put(descr, time);
                        }
                        if (totalLabelTimes.containsKey(descr)) {
                            double newtime = totalLabelTimes.get(descr) + time;
                            totalLabelTimes.put(descr, newtime);
                        } else {
                            totalLabelTimes.put(descr, time);
                        }
                    } catch (JexmaraldaException je) {
                        je.printStackTrace();
                    }
                }
                for (Map.Entry<String, Double> entry : labelTimes.entrySet()) {
                    if (entry.getKey().length() < 32) {
                        html += "<tr><td>" + entry.getKey() + "</td>\n<td>" + 
                            TimeStringFormatter.formatMiliseconds(
                                    entry.getValue() * 1000.0, 2) + "</td></tr>\n";
                    } else {
                        html += "<tr><td>" + entry.getKey().substring(0, 32) + 
                            "<span style='color:red'>... [[truncated]]</span>" +
                            "</td>\n<td>" + TimeStringFormatter.formatMiliseconds(
                                    entry.getValue() * 1000.0, 2) +
                            "</td></tr>\n";
                    }
                }
            } else {
                // I guess for non-annotation tiers just do something
                html += "<tr><th colspan='2'>" + 
                    tier.getDescription(bt.getHead().getSpeakertable()) + 
                    "</th></tr>\n";
                html += "<tr><td><em>text-type tier</em></td>\n";
                html += "<td>" +
                    TimeStringFormatter.formatMiliseconds(
                            tier.calculateEventTime(commonTimeline) * 1000.0,
                            2) + "</td></tr>\n";
            }
         }
         html += "<tr><th colspan='2'><strong>Totals</strong></th></tr>\n";
         for (Map.Entry<String, Double> entry : totalLabelTimes.entrySet()) {
            if (entry.getKey().length() < 32) {
                html += "<tr><td>" + entry.getKey() + "</td>\n<td>" + 
                    TimeStringFormatter.formatMiliseconds(
                            entry.getValue() * 1000.0, 2) + "</td></tr>\n";
            } else {
                html += "<tr><td>" + entry.getKey().substring(0, 32) + 
                    "<span style='color: red'>... [[truncated]]</span>" +
                    "</td>\n<td>" + 
                    TimeStringFormatter.formatMiliseconds(
                            entry.getValue() * 1000.0, 2) +
                    "</td></tr>\n";
            }
         }
         html+="</table>\n";
         html+="</html>\n";
         org.exmaralda.partitureditor.exSync.swing.MessageDialog md =
                 new org.exmaralda.partitureditor.exSync.swing.MessageDialog(
                         (javax.swing.JFrame)table.parent, true,
                         new StringBuffer(html));
         md.setTitle(org.exmaralda.common.helpers.Internationalizer.getString(
                     "Annotated time"));
         md.show();

    }

}
