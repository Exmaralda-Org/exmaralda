/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.partiture.undo;

import java.io.File;
import java.util.Vector;
import javax.swing.SwingUtilities;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;

/**
 *
 * @author thomas
 */
public class UndoHandler {

    int MAX_UNDO = 20;

    Vector<UndoInformation> undoInformation = new Vector<UndoInformation>();

    public void addUndoInformation(UndoInformation undo){
        // Do not add if this is just another time change in the same item as before
        if ((undoInformation.size()>0) && (undo.restoreType==UndoInformation.RESTORE_TIME) && (undoInformation.elementAt(0).restoreType==UndoInformation.RESTORE_TIME)){
            RestoreTimeInfo rti1 = (RestoreTimeInfo)(undoInformation.elementAt(0).restoreObject);
            RestoreTimeInfo rti2 = (RestoreTimeInfo)(undo.restoreObject);
            if (rti1.timelineID.equals(rti2.timelineID)){
                return;
            }            
        }
        if ((undoInformation.size()>0) && (undo.equals(undoInformation.firstElement()))){
            return;
        }
        undoInformation.insertElementAt(undo, 0);
        while (undoInformation.size()>MAX_UNDO){
            undoInformation.removeElementAt(MAX_UNDO);
        }
    }

    public String getCurrentDescription(){
        if (isEmpty()) return null;
        return undoInformation.elementAt(0).description;
    }

    public void undo(final PartitureTableWithActions partitur){
        try {
            final UndoInformation info = undoInformation.firstElement();
            switch (info.restoreType) {
                case UndoInformation.RESTORE_TRANSCRIPTION:
                    System.out.println("UNDO: RESTORE TRANSCRIPTION");
                    File file = (File) (info.restoreObject);
                    BasicTranscription bt = new BasicTranscription(file.getAbsolutePath());
                    partitur.getModel().setTranscription(bt);
                    break;
                case UndoInformation.RESTORE_REGION:
                    System.out.println("UNDO: RESTORE REGION");
                    BasicTranscription region = (BasicTranscription) (info.restoreObject);
                    partitur.getModel().replaceRegion(region);
                    break;
                case UndoInformation.RESTORE_CELL:
                    System.out.println("UNDO: RESTORE CELL");
                    RestoreCellInfo restoreCellInfo = (RestoreCellInfo) (info.restoreObject);
                    if (restoreCellInfo.text!=null){
                        partitur.getModel().setTableDataItem(restoreCellInfo.text, restoreCellInfo.row, restoreCellInfo.col);
                    } else {
                        partitur.getModel().deleteEvent(restoreCellInfo.row, restoreCellInfo.col);
                    }
                    break;
                case UndoInformation.RESTORE_TIME:
                    System.out.println("UNDO: RESTORE TIME");
                    RestoreTimeInfo restoreTimeInfo = (RestoreTimeInfo) (info.restoreObject);
                    int col = partitur.getModel().getTranscription().getBody().getCommonTimeline().lookupID(restoreTimeInfo.timelineID);
                    partitur.getModel().getTimelineItem(col).setTime(restoreTimeInfo.time);
                    partitur.getModel().fireColumnLabelChanged(col);
                    break;
                case UndoInformation.REMOVE_TIER:
                    System.out.println("UNDO: REMOVE TIER");
                    String id = (String)(info.restoreObject);
                    int row = partitur.getModel().getTranscription().getBody().lookupID(id);
                    partitur.getModel().removeTier(row);
                    break;
                case UndoInformation.RESTORE_TIER_PROPERTIES:
                    System.out.println("UNDO: RESTORE TIER PROPERTIES");
                    Tier restoreTier = (Tier)(info.restoreObject);
                    Tier tier = partitur.getModel().getTranscription().getBody().getTierWithID(restoreTier.getID());
                    tier.setSpeaker(restoreTier.getSpeaker());
                    tier.setCategory(restoreTier.getCategory());
                    tier.setType(restoreTier.getType());
                    tier.setDisplayName(restoreTier.getDisplayName());
                    partitur.getModel().fireRowLabelsChanged();
                    break;
                case UndoInformation.RESTORE_TIER_ORDER:
                    System.out.println("UNDO: RESTORE TIER ORDER");
                    String[] tierIDs = (String[])(info.restoreObject);
                    partitur.getModel().changeTierOrder(tierIDs);
                    break;
            }
            if (info.editingRow>=0){
                partitur.setNewSelection(info.editingRow, info.editingColumn, true);
            } else {
                partitur.setNewSelection(info.selectionStartRow, info.selectionEndRow, info.selectionStartCol, info.selectionEndCol);
            }
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    partitur.scrollRectToVisible(info.visibleRectangle);                    
                }                
            });
            undoInformation.removeElementAt(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isEmpty() {
        return (undoInformation.size()<=0);
    }
}
