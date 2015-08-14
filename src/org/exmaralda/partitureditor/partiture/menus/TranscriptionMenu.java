/*
 * this.java
 *
 * Created on 1. Juli 2003, 14:36
 */

package org.exmaralda.partitureditor.partiture.menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class TranscriptionMenu extends AbstractTableMenu implements MouseListener {
    
    public JLabel segmentationLabel = new JLabel(" Segmentation");
    public JMenuItem insertHIATUtteranceNumbersMenuItem;

    /** Creates a new instance of FileMenu */
    public TranscriptionMenu(PartitureTableWithActions t) {
        super(t);
        segmentationLabel.setFont(segmentationLabel.getFont().deriveFont(10.0f).deriveFont(Font.BOLD));
        segmentationLabel.setForeground(Color.GRAY);
        segmentationLabel.addMouseListener(this);
        //segmentationLabel.setHorizontalAlignment(JLabel.TRAILING);
        //segmentationLabel.setAlignmentX(JComponent.RIGHT_ALIGNMENT);

        this.setText("Transcription");
        
        
        add(table.editMetaInformationAction);
        add(table.editSpeakertableAction);
        add(table.editRecordingsAction);

        addSeparator();
        //-------------------------------------------------
        add(table.getStructureErrorsAction);
        add(table.calculateTimeAction);

        addSeparator();
        add(table.autoAnnotationAction);
        
        addSeparator();
        //-------------------------------------------------
        add(segmentationLabel);
        add(table.getSegmentationErrorsAction);
        add(table.segmentAndSaveTranscriptionAction);
        add(table.countAction);
        add(table.wordListAction);
        insertHIATUtteranceNumbersMenuItem = add(table.insertUtteranceNumbersAction);

        addSeparator();
        //-------------------------------------------------
        add(table.transformationAction);
        addSeparator();
        //-------------------------------------------------
        add(table.cleanupAction);
        addSeparator();
        //-------------------------------------------------
        add(table.glueTranscriptionsAction);
        add(table.mergeTranscriptionsAction);
        add(table.chopTranscriptionAction);
        addSeparator();
        add(table.maskAudioAction);
        add(table.chopAudioAction);
        addSeparator();
        add(table.exSyncEventShrinkerAction);

        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        table.editPreferencesAction.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "ChangeSegmentation", e.getWhen(), e.getModifiers()));
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}
