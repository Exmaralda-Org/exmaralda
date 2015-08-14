/*
 * NewFromSpeakertableAction.java
 *
 * Created on 6. Oktober 2003, 16:40
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.sound.LiveTimelineSegmentationDialog;

/**
 * creates a new file from a speakertable (which the user is prompted to create and edit)
 * and a stylesheet (which is either specified in the preferences or a built-in stylesheet is used)
 * Menu: File --> New from speakertable
 * @author  thomas
 */
public class NewFromTimelineAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of NewFromSpeakertableAction */
    public NewFromTimelineAction(PartitureTableWithActions t) {
        super("New from timeline...", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            table.commitEdit(true);
            System.out.println("newFromTimelineAction!");
            newTranscription();
            table.transcriptionChanged = true;
            table.clearUndo();
            table.clearSearchResult();
            table.setFrameEndPosition(-2);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());
        }
    }
    
    private void newTranscription() throws IOException{
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;

        BasicTranscription newTranscription = new BasicTranscription();

        Speaker speaker = new Speaker();
        speaker.setID("SPK0");
        speaker.setAbbreviation("X");
        try {
            newTranscription.getHead().getSpeakertable().addSpeaker(speaker);
        } catch (JexmaraldaException ex) {
            // do nothing
        }
        newTranscription.getBody().getCommonTimeline().addTimelineItem();
        newTranscription.getBody().getCommonTimeline().addTimelineItem();
        Tier tier = new Tier("TIE0","SPK0","v","t");
        try {
            newTranscription.getBody().addTier(tier);
        } catch (JexmaraldaException ex) {
            // do nothing
        }
        newTranscription.makeDisplayNames();



        EditReferencedFilesDialog dialog = new EditReferencedFilesDialog(null, true, newTranscription.getHead().getMetaInformation().getReferencedFiles());
        dialog.defaultDirectory = table.getFilename();
        dialog.setLocationRelativeTo(this.table);
        dialog.setVisible(true);

        if ((dialog.getReferencedFiles().size()<1) || (dialog.getReferencedFiles().elementAt(0).length()<1)) return;

        newTranscription.getHead().getMetaInformation().setReferencedFiles(dialog.getReferencedFiles());

        LiveTimelineSegmentationDialog ltsd = new LiveTimelineSegmentationDialog(table.parent, true);
        ltsd.setLocationRelativeTo(table);
        ltsd.setPlayer(table.makePlayer());
        ltsd.setMedia(newTranscription.getHead().getMetaInformation().getReferencedFile());
        ltsd.setTitle("Live Timeline Segmentation");
        ltsd.setVisible(true);

        Timeline tl = newTranscription.getBody().getCommonTimeline();
        tl.anchorTimeline(0.0, ltsd.getPlayer().getTotalLength());
        Vector<Double> times = ltsd.getTimes();
        for (Double time : times){
            //System.out.println("[" + time + "]");
            TimelineItem tli = new TimelineItem();
            tli.setID(tl.getFreeID());
            tli.setTime(time.doubleValue());
            tl.insertAccordingToTime(tli);
        }

        table.getModel().setTranscription(newTranscription);
        table.showAllTiers();
        table.setFilename("untitled.exb");
        table.linkPanelDialog.getLinkPanel().emptyContents();
        table.largeTextField.setText("");
        //table.reexportHTMLAction.setEnabled(false);
        table.restoreAction.setEnabled(false);
        table.reconfigureAutoSaveThread();
        table.setupMedia();

        table.status("New transcription");
    }
    
    
}
