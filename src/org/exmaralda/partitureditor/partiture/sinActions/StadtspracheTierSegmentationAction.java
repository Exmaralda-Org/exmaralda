/*
 * ExportHTMLUtteranceListAction.java
 *
 * Created on 17. Juni 2003, 16:00
 */

package org.exmaralda.partitureditor.partiture.sinActions;

import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;


/**
 *
 * @author  thomas
 */
public class StadtspracheTierSegmentationAction extends AbstractFSMSegmentationAction {
    
    
    /** Creates a new instance of ExportHTMLUtteranceListAction */
    public StadtspracheTierSegmentationAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Spur-Segmentierung", icon, t);
        setEnabled(false);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("StadtsprachTierSegmentation!");
        table.commitEdit(true);
        tierSegmentation();
    }
    
    private void tierSegmentation() {
        org.exmaralda.partitureditor.jexmaralda.segment.StadtspracheSegmentation
                segmentor = new org.exmaralda.partitureditor.jexmaralda.segment.StadtspracheSegmentation();
        Tier selectedTier = table.getModel().getTier(table.selectionStartRow);
        String[][] tierIDMappings = new String[table.getModel().getTranscription().getBody().getNumberOfTiers()][2];
        int count=0;
        for (String[] mapping : tierIDMappings){
            mapping[0] = table.getModel().getTranscription().getBody().getTierAt(count).getID();
            mapping[1] = table.getModel().getTranscription().getBody().getTierAt(count).getID();
            count++;
        }
        Timeline tl = table.getModel().getTranscription().getBody().getCommonTimeline();
        selectedTier.sort(tl);
        String[] tierIDs = table.getModel().getTranscription().getBody().getAllTierIDs();
        if (selectedTier.getNumberOfEvents()==0) return;
        String tierID = selectedTier.getID();
        Event firstEvent = selectedTier.getEventAt(0);
        //BasicTranscription resultTranscription = null

        BasicTranscription resultTranscription =
                table.getModel().getTranscription().getPartOfTranscription(
                tierIDs, tl.getTimelineItemAt(0).getID(), firstEvent.getStart());

        Event lastEvent = firstEvent;
        if (selectedTier.getNumberOfEvents()>0){
            for (int pos=0; pos<selectedTier.getNumberOfEvents(); pos++){
                try {
                    Event event = selectedTier.getEventAt(pos);
                    BasicTranscription middlePart = table.getModel().getTranscription().getPartOfTranscription(
                            tierIDs,
                            lastEvent.getEnd(), event.getStart());
                    //System.out.println("Middle part: " + middlePart.getBody().toXML());
                    if (middlePart.getBody().getCommonTimeline().getNumberOfTimelineItems()>0){
                        resultTranscription.glue(middlePart, tierIDMappings);
                    }
                    BasicTranscription thisSegmentation = 
                            segmentor.wordSegmentation(table.getModel().getTranscription(), event.getStart(), event.getEnd(), tierID, false);
                    //System.out.println("Geschredderte part: " + thisSegmentation.getBody().toXML());
                    resultTranscription.glue(thisSegmentation, tierIDMappings);

                    lastEvent = event;
                } catch (JexmaraldaException ex) {
                    ex.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(table.parent, ex.getLocalizedMessage());
                    return;
                }
            }
        }

        BasicTranscription lastPart =
                table.getModel().getTranscription().getPartOfTranscription(
                tierIDs, lastEvent.getEnd(), tl.getTimelineItemAt(tl.getNumberOfTimelineItems()-1).getID());
        if (lastPart.getBody().getCommonTimeline().getNumberOfTimelineItems()>0){
            try {
                resultTranscription.glue(lastPart, tierIDMappings);
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(table.parent, ex.getLocalizedMessage());
                return;
            }
        }

        table.showAllTiers();
        table.getModel().setTranscription(resultTranscription);
        table.setFilename("untitled.exb");
        table.linkPanelDialog.getLinkPanel().emptyContents();
        table.largeTextField.setText("");
        //table.reexportHTMLAction.setEnabled(false);
        table.restoreAction.setEnabled(false);        
        
        
    }
    
    
}
