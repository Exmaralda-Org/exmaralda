/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.partiture.undo;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import javax.swing.JTextField;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;

/**
 *
 * @author thomas
 */
public class UndoInformation {


    public static final int RESTORE_TRANSCRIPTION = 0;
    public static final int RESTORE_REGION = 1;
    public static final int RESTORE_CELL = 2;
    public static final int RESTORE_TIME = 3;
    public static final int REMOVE_TIER = 4;
    public static final int RESTORE_TIER_PROPERTIES = 5;
    public static final int RESTORE_TIER_ORDER = 6;


    Rectangle visibleRectangle;
    int editingRow = -1;
    int editingColumn = -1;
    int cursorPosition = -1;

    int selectionStartRow;
    int selectionEndRow;
    int selectionStartCol;
    int selectionEndCol;

    int action;

    public Object restoreObject;
    public int restoreType;

    public String description;

    public UndoInformation(PartitureTableWithActions partitur, String text) {
        description = text;
        visibleRectangle = partitur.getVisibleRect();
        if (partitur.isEditing){
            editingRow = partitur.selectionStartRow;
            editingColumn = partitur.selectionStartCol;
            JTextField editingComponent = ((JTextField)(partitur.getEditingComponent()));
            if (editingComponent!=null){
                cursorPosition = editingComponent.getCaretPosition();
            } else {
                cursorPosition=0;
            }
        } else {
            selectionStartRow = partitur.selectionStartRow;
            selectionEndRow = partitur.selectionEndRow;
            selectionStartCol = partitur.selectionStartCol;
            selectionEndCol = partitur.selectionEndCol;
        }
    }

    public void memorizeRegion(PartitureTableWithActions partitur, int lower, int upper) {
        BasicTranscription bt = partitur.getModel().getTranscription();
        Timeline tl = bt.getBody().getCommonTimeline();
        if (tl.getNumberOfTimelineItems()<5){
            memorizeTranscription(partitur);
            return;
        }
        restoreType = RESTORE_REGION;
        restoreObject = bt.getPartOfTranscription( bt.getBody().getAllTierIDs(),
                                            tl.getTimelineItemAt(Math.max(0, lower)).getID(),
                                            tl.getTimelineItemAt(Math.min(upper, tl.getNumberOfTimelineItems()-1)).getID()).makeCopy();
        /*try {
            ((BasicTranscription) restoreObject).writeXMLToFile("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\UNDO_BUG\\restoreRegion.exb", "none");
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
    }

    public void memorizeCell(PartitureTableWithActions partitur) {
        int row = partitur.selectionStartRow;
        int col = partitur.selectionStartCol;
        restoreType = RESTORE_CELL;
        String text = null;
        try {
            Event e = partitur.getModel().getEvent(row, col);
            text = e.getDescription();
        } catch (JexmaraldaException ex) {
            //ex.printStackTrace();
        }
        restoreObject = new RestoreCellInfo(row,col,text);
    }

    public void memorizeTime(TimelineItem tli, double oldTime) {
        restoreType = RESTORE_TIME;
        restoreObject = new RestoreTimeInfo(tli.getID(), oldTime);
    }

    public void memorizeTranscription(PartitureTableWithActions table) {
        try {
            restoreType = RESTORE_TRANSCRIPTION;
            File tempFile = File.createTempFile("EXMARaLDA_Restore", ".exb");
            tempFile.deleteOnExit();
            table.getModel().getTranscription().makeCopy().writeXMLToFile(tempFile.getAbsolutePath(), "none");
            restoreObject = tempFile;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
