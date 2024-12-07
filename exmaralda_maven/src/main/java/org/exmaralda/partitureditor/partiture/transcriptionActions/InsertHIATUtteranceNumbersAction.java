/*
 * ExportSegmentedTranscriptionAction.java
 *
 * Created on 17. Juni 2003, 17:00
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.exmaralda.partitureditor.jexmaraldaswing.SpeakerContributionNavigationDialog;

/**
 *
 * @author  thomas
 */
public class InsertHIATUtteranceNumbersAction extends AbstractFSMSegmentationAction {
    
    /** Creates a new instance of ExportSegmentedTranscriptionAction */
    public InsertHIATUtteranceNumbersAction(PartitureTableWithActions t) {
        super("Insert utterance numbers", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("insertUtteranceNumbersAction!");
        table.commitEdit(true);
        insertUtteranceNumbers();
    }
    
    private void insertUtteranceNumbers(){
        String offendingTierID = "";
        String offendingTLI = "";
        try {
            table.commitEdit(true);
            if (!table.checkSave()){
                return;
            }
            BasicTranscription bt = table.getModel().getTranscription().makeCopy();
            TierFormatTable tft = table.getModel().getTierFormatTable();
            BasicTranscription copyBT = table.getModel().getTranscription().makeCopy();
            AbstractSegmentation as = table.getAbstractSegmentation();
            SegmentedTranscription st = as.BasicToSegmented(copyBT);
            //SegmentedTranscription st2 = as.BasicToSegmented(copyBT.makeCopy());
            ListTranscription lt = st.toListTranscription(new SegmentedToListInfo(st, SegmentedToListInfo.HIAT_UTTERANCE_SEGMENTATION), false);
            lt.getBody().sort();

            Vector scsThatAreNot = new Vector();
            boolean everythingOK = lt.getBody().areAllSpeakerContributionsInTheCommonTimeline(scsThatAreNot);
            if (!everythingOK){
                JOptionPane.showMessageDialog(table, "Not all utterance starts or ends are in the common timeline" + ".\nClick OK to show a list of these utterances.");
                SpeakerContributionNavigationDialog dialog =
                   new SpeakerContributionNavigationDialog(table.parent, false, scsThatAreNot, st.getBody().makeTLIHashtable(), table);
                dialog.show();
                return;
            }


            for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
                Tier tier = bt.getBody().getTierAt(pos);
                if (tier.getCategory().equals("no")){
                    bt.getBody().removeTierAt(pos);
                    pos--;
                }
            }
            Hashtable<String, Tier> tiersForSpeakers = new Hashtable<String,Tier>();
            for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
                Tier tier = bt.getBody().getTierAt(pos);
                if ((tier.getType().equals("t")) && (tier.getSpeaker()!=null)){
                    Tier newTier = new Tier();
                    newTier.setSpeaker(tier.getSpeaker());
                    newTier.setType("a");
                    newTier.setCategory("no");
                    newTier.setID(bt.getBody().getFreeID());
                    newTier.setDisplayName("");
                    bt.getBody().insertTierAt(newTier, pos);
                    pos++;
                    tiersForSpeakers.put(tier.getSpeaker(), newTier);
                }
            }
            int count=0;
            for (int pos=0; pos<lt.getBody().getNumberOfSpeakerContributions(); pos++){
                SpeakerContribution sc = lt.getBody().getSpeakerContributionAt(pos);
                if (sc.getSpeaker()==null) continue;
                count++;
                String start = sc.getMain().getStart();
                String end = sc.getMain().getEnd();
                /*if ((!bt.getBody().getCommonTimeline().containsTimelineItemWithID(start)) || (!bt.getBody().getCommonTimeline().containsTimelineItemWithID(end))){
                    offendingTierID = sc.getTierReference();
                    if (!(bt.getBody().getCommonTimeline().containsTimelineItemWithID(start))){
                        offendingTLI = st2.getBody().getCommonTimelineMatch(start);
                        if ((offendingTLI==null) || (offendingTLI.length()==0)){
                            offendingTLI = st2.getBody().getCommonTimelineMatch(end);
                        }
                    } else {
                        offendingTLI = st2.getBody().getCommonTimelineMatch(end);
                        if ((offendingTLI==null) || (offendingTLI.length()==0)){
                            offendingTLI = st2.getBody().getCommonTimelineMatch(start);
                        }
                    }
                    throw new JexmaraldaException(912, "Not all utterance starts or ends are in the common timeline");
                }*/
                Event newEvent = new Event();
                newEvent.setStart(start);
                newEvent.setEnd(end);
                newEvent.setDescription("/" + Integer.toString(count) + "/");
                tiersForSpeakers.get(sc.getSpeaker()).addEvent(newEvent);
            }
            //table.getModel().setTranscription(bt);
            table.getModel().setTranscriptionAndTierFormatTable(bt, tft);
            //table.getModel().resetTranscription();
            table.showAllTiers();
            table.setFilename("untitled.exb");
            table.linkPanelDialog.getLinkPanel().emptyContents();
            table.largeTextField.setText("");
            //table.reexportHTMLAction.setEnabled(false);
            table.restoreAction.setEnabled(false);
            table.reconfigureAutoSaveThread();
            table.setupMedia();
            table.status("New transcription");
            table.transcriptionChanged = true;

        } catch (Exception ex) {
            if (ex instanceof JexmaraldaException && ((JexmaraldaException)ex).exceptionCode==912){
                //System.out.println("THE OFFENDERS: " + offendingTierID + " / " + offendingTLI);
                JOptionPane.showMessageDialog(table, ex.getMessage() + ".\nClick OK to go near the first of these utterances.");
                int row = table.getModel().getTranscription().getBody().lookupID(offendingTierID);
                int col = table.getModel().getTranscription().getBody().getCommonTimeline().lookupID(offendingTLI);
                table.setNewSelection(row, col, false);
            } else {
                JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());
            }
            ex.printStackTrace();
        }
    }
    
    
}
