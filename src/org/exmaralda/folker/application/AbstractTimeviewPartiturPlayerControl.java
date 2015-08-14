/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.folker.application;

import java.awt.Component;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.helpers.Internationalizer;
import org.exmaralda.folker.actions.waveformactions.FineTuneSelectionAction;
import org.exmaralda.folker.gui.TierSelectionPanel;
import org.exmaralda.folker.timeview.AbstractTimeProportionalViewer;
import org.exmaralda.folker.timeview.ChangeZoomDialog;
import org.exmaralda.folker.timeview.TimeSelectionEvent;
import org.exmaralda.folker.timeview.TimeSelectionListener;
import org.exmaralda.folker.utilities.Constants;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.partiture.PartitureTableEvent;
import org.exmaralda.partitureditor.partiture.PartitureTableListener;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;
import org.exmaralda.partitureditor.sound.Playable;
import org.exmaralda.partitureditor.sound.PlayableEvent;
import org.exmaralda.partitureditor.sound.PlayableListener;

/**
 *
 * @author thomas
 */
public abstract class AbstractTimeviewPartiturPlayerControl 
        implements PartitureTableListener, PlayableListener, TimeSelectionListener, ChangeListener {


    public AbstractTimeProportionalViewer timeViewer;
    public PartitureTableWithActions partitur;
    public Playable player;
    public ChangeZoomDialog changeZoomDialog;

    public static int PLAYER_NO_SOUND = -1;
    public static int PLAYER_IDLE = 0;
    public static int PLAYER_PLAYING = 1;
    public static int PLAYER_HALTED = 2;
    public static int TIME_BETWEEN_LOOPS = 500;

    public int playerState = PLAYER_IDLE;

    public double selectionStart = -1;
    public double selectionEnd = -1;

    public Object startPoint;
    public Object endPoint;

    public boolean stoppedByUser = false;
    public boolean loopPlay = false;
    public boolean isPlayingSelection = false;

    // added 17-08-2009: different loop implementation
    private LoopThread loopThread;



    public org.exmaralda.folker.actions.playeractions.PlayAction playAction;
    public org.exmaralda.folker.actions.playeractions.PlaySelectionAction playSelectionAction;
    public org.exmaralda.folker.actions.playeractions.PlayFirstSecondOfSelectionAction playFirstSecondOfSelectionAction;
    public org.exmaralda.folker.actions.playeractions.PlayLastSecondOfSelectionAction playLastSecondOfSelectionAction;
    public org.exmaralda.folker.actions.playeractions.PlayFirstSecondBeforeSelectionAction playFirstSecondBeforeSelectionAction;
    public org.exmaralda.folker.actions.playeractions.PlayFirstSecondAfterSelectionAction playFirstSecondAfterSelectionAction;
    public org.exmaralda.folker.actions.playeractions.LoopSelectionAction loopSelectionAction;
    public org.exmaralda.folker.actions.playeractions.PauseAction pauseAction;
    public org.exmaralda.folker.actions.playeractions.StopAction stopAction;
    // ---------------------------
    public org.exmaralda.folker.actions.playeractions.PlayNextSegmentAction playNextSegmentAction;
    
    // ---------------------------
    public org.exmaralda.folker.actions.waveformactions.ShiftSelectionAction shiftSelectionAction;
    public org.exmaralda.folker.actions.waveformactions.DetachSelectionAction detachSelectionAction;
    public org.exmaralda.folker.actions.waveformactions.FineTuneSelectionAction decreaseSelectionStartAction;
    public org.exmaralda.folker.actions.waveformactions.FineTuneSelectionAction increaseSelectionStartAction;
    public org.exmaralda.folker.actions.waveformactions.FineTuneSelectionAction decreaseSelectionEndAction;
    public org.exmaralda.folker.actions.waveformactions.FineTuneSelectionAction increaseSelectionEndAction;
    public org.exmaralda.folker.actions.waveformactions.NavigateAction navigateAction;
    // ---------------------------
    public org.exmaralda.folker.actions.partiturviewactions.AddEventInPartiturAction addEventInPartiturAction;
    public org.exmaralda.folker.actions.partiturviewactions.AppendIntervalInPartiturAction appendIntervalInPartiturAction;
    public org.exmaralda.folker.actions.partiturviewactions.AssignTimesAction assignTimesAction;
    // ---------------------------
    public org.exmaralda.folker.actions.waveformactions.ChangeZoomAction changeZoomAction;


    public Component addEventInvoker;


    public AbstractTimeviewPartiturPlayerControl(ExmaraldaApplication ac, AbstractTimeProportionalViewer tv, PartitureTableWithActions pt, Playable p) {

        timeViewer = tv;
        timeViewer.addTimeSelectionListener(this);

        partitur = pt;
        partitur.addPartitureTableListener(this);

        timeViewer.addTimeSelectionListener(pt);

        player = p;
        if (player!=null){
            player.addPlayableListener(timeViewer);
            player.addPlayableListener(this);
        }

        initActions();


    }


    public void fineTuneSelection(int boundary, int amount) {
        if (boundary==FineTuneSelectionAction.LEFT_BOUNDARY){
            timeViewer.fineTuneSelectionStart(amount);
        } else {
            timeViewer.fineTuneSelectionEnd(amount);
        }
    }

    public Component getAddEventInvoker() {
        return addEventInvoker;
    }

    public void setAddEventInvoker(Component addEventInvoker) {
        this.addEventInvoker = addEventInvoker;
    }


    void initActions(){
        org.exmaralda.folker.utilities.Constants c = new org.exmaralda.folker.utilities.Constants();

        playSelectionAction = new org.exmaralda.folker.actions.playeractions.PlaySelectionAction(this, "[*]", c.getIcon(Constants.PLAY_SELECTION_ICON));
        playFirstSecondBeforeSelectionAction = new org.exmaralda.folker.actions.playeractions.PlayFirstSecondBeforeSelectionAction(this, "*[", null);
        playFirstSecondOfSelectionAction = new org.exmaralda.folker.actions.playeractions.PlayFirstSecondOfSelectionAction(this, "[*", null);
        playLastSecondOfSelectionAction = new org.exmaralda.folker.actions.playeractions.PlayLastSecondOfSelectionAction(this, "*]", null);
        playFirstSecondAfterSelectionAction = new org.exmaralda.folker.actions.playeractions.PlayFirstSecondAfterSelectionAction(this, "]*", null);
        loopSelectionAction = new org.exmaralda.folker.actions.playeractions.LoopSelectionAction(this, "", c.getIcon(Constants.LOOP_SELECTION_ICON));
        playAction = new org.exmaralda.folker.actions.playeractions.PlayAction(this, "", c.getIcon(Constants.PLAY_ICON));
        pauseAction = new org.exmaralda.folker.actions.playeractions.PauseAction(this, "", c.getIcon(Constants.PAUSE_ICON));
        stopAction = new org.exmaralda.folker.actions.playeractions.StopAction(this, "", c.getIcon(Constants.STOP_ICON));

        playNextSegmentAction = new org.exmaralda.folker.actions.playeractions.PlayNextSegmentAction(this, "", c.getIcon(Constants.PLAY_NEXT_SEGMENT_ICON));
        
        shiftSelectionAction = new org.exmaralda.folker.actions.waveformactions.ShiftSelectionAction(this, "",  c.getIcon(Constants.SHIFT_SELECTION_ICON));
        detachSelectionAction = new org.exmaralda.folker.actions.waveformactions.DetachSelectionAction(this, "",  c.getIcon(Constants.DETACH_SELECTION_ICON));
        detachSelectionAction.setEnabled(false);

        decreaseSelectionStartAction = new org.exmaralda.folker.actions.waveformactions.FineTuneSelectionAction(this, "", c.getIcon(Constants.REWIND_ICON), FineTuneSelectionAction.LEFT_BOUNDARY, -1);
        increaseSelectionStartAction = new org.exmaralda.folker.actions.waveformactions.FineTuneSelectionAction(this, "", c.getIcon(Constants.FORWARD_ICON), FineTuneSelectionAction.LEFT_BOUNDARY, +1);
        decreaseSelectionEndAction = new org.exmaralda.folker.actions.waveformactions.FineTuneSelectionAction(this, "", c.getIcon(Constants.REWIND_ICON), FineTuneSelectionAction.RIGHT_BOUNDARY, -1);
        increaseSelectionEndAction = new org.exmaralda.folker.actions.waveformactions.FineTuneSelectionAction(this, "", c.getIcon(Constants.FORWARD_ICON), FineTuneSelectionAction.RIGHT_BOUNDARY, +1);
        navigateAction = new org.exmaralda.folker.actions.waveformactions.NavigateAction(this, "", c.getIcon(Constants.NAVIGATE_ICON));

        addEventInPartiturAction = new org.exmaralda.folker.actions.partiturviewactions.AddEventInPartiturAction(this, 
                Internationalizer.getString("Add event..."), c.getIcon(Constants.ADD_EVENT_ICON));
        appendIntervalInPartiturAction = new org.exmaralda.folker.actions.partiturviewactions.AppendIntervalInPartiturAction(this, 
                Internationalizer.getString("Append interval"), c.getIcon(Constants.APPEND_INTERVAL_ICON));
        assignTimesAction = new org.exmaralda.folker.actions.partiturviewactions.AssignTimesAction(this, "", c.getIcon(Constants.TIMESTAMP_EVENT_ICON));
        assignTimesAction.setEnabled(false);

        // -----------------
        changeZoomAction = new org.exmaralda.folker.actions.waveformactions.ChangeZoomAction(this, "",  c.getIcon(Constants.ZOOM_ICON));


    }

 /***************** PLAYING MEDIA ******************************/

    public void playNextSegment(){       
        stop();
        partitur.commitEdit(true);
        int currentSelection = partitur.selectionStartCol;
        int newSelection = 0;
        if (currentSelection>=0 && currentSelection<partitur.getModel().getNumColumns()-1){
            newSelection = currentSelection+1;
        }
        partitur.setNewSelection(-1, -1, newSelection, newSelection);
        playSelection();
    }
    
    /** plays from the cursor position or from the left corner of the visible signal */
    public void play(){
        double cursor = timeViewer.getCursorTime();
        player.setStartTime(cursor/1000.0);
        player.setEndTime(player.getTotalLength());
        //playerState = PLAYER_PLAYING;
        // added 11-05-2009: loop functionality
        isPlayingSelection = false;
        player.startPlayback();
    }

    public void playSelection(){
        if (playerState==PLAYER_PLAYING) return;
        player.setStartTime(selectionStart/1000.0);
        player.setEndTime(selectionEnd/1000.0);
        //playerState = PLAYER_PLAYING;
        // added 11-05-2009: loop functionality
        isPlayingSelection = true;
        stoppedByUser = false;
        player.startPlayback();
    }

    public void playLastSecondOfSelection() {
        if (playerState==PLAYER_PLAYING) return;
        double startTime = Math.max(0.0, selectionEnd-1000.0);
        player.setStartTime(startTime/1000.0);
        player.setEndTime(selectionEnd/1000.0);
        // added 11-05-2009: loop functionality
        isPlayingSelection = false;
        player.startPlayback();
    }

    public void playFirstSecondOfSelection() {
        if (playerState==PLAYER_PLAYING) return;
        player.setStartTime(selectionStart/1000.0);
        double endTime = Math.min(selectionStart+1000.0, player.getTotalLength()*1000.0);
        player.setEndTime(endTime/1000.0);
        isPlayingSelection = false;
        player.startPlayback();
    }

    public void playFirstSecondBeforeSelection() {
        if (playerState==PLAYER_PLAYING) return;
        double startTime = Math.max(0.0, selectionStart-1000.0);
        player.setStartTime(startTime/1000.0);
        player.setEndTime(selectionStart/1000.0);
        // added 11-05-2009: loop functionality
        isPlayingSelection = false;
        player.startPlayback();
    }

    public void playFirstSecondAfterSelection() {
        if (playerState==PLAYER_PLAYING) return;
        player.setStartTime(selectionEnd/1000.0);
        double endTime = Math.min(selectionEnd+1000.0, player.getTotalLength()*1000.0);
        player.setEndTime(endTime/1000.0);
        isPlayingSelection = false;
        player.startPlayback();
    }

    public void pauseOrResume(){
        if (playerState==PLAYER_PLAYING){
            //playerState=PLAYER_HALTED;
            player.haltPlayback();
        } else if (playerState==PLAYER_HALTED){
            //playerState=PLAYER_PLAYING;
            player.resumePlayback();
        }
    }

    /** stops the current playback */
    public void stop(){
        //System.out.println("AbstractTimeviewPartiturPlayerControl: stop");
        //playerState=PLAYER_IDLE;
        // added 11-05-2009: loop functionality
        // changed 17-08-2009
        if (loopPlay){
            loopPlay = false;
            stopLoop();
        } /* else {*/
            stoppedByUser = true;
            player.stopPlayback();
        //}
    }



 /***************** ACTIONS ******************************/

    public void navigate() {
        timeViewer.navigationDialog.setLocationRelativeTo(timeViewer);
        timeViewer.navigationDialog.updateTimes();
        timeViewer.navigationDialog.setVisible(true);
    }

    public void shiftSelection(){
        timeViewer.shiftSelection();
        detachSelectionAction.setEnabled(false);
    }

    public void detachSelection(){
        timeViewer.detachSelection();
        detachSelectionAction.setEnabled(false);
    }

    public void addInterval(){
        partitur.commitEdit(true);
        // Added 08-04-2011
        if ((selectionStart<0) || (selectionEnd<0)) return;
        int[] cols = partitur.getModel().insertInterval(selectionStart/1000.0, selectionEnd/1000.0, 0.001);
        int col1 = cols[0];
        int col2 = cols[1];
        partitur.setNewSelection(-1, -1, col1, col2-1);        
    }

    public void assignTimes() {
        // changed 06-05-2010
        if (selectionStart!=selectionEnd){
            partitur.assignTimes(selectionStart/1000.0, selectionEnd/1000.0);
            timeViewer.fireSelectionDetached();
        } else if (timeViewer.getCursorTime()>=0){
            partitur.assignTimes(timeViewer.getCursorTime()/1000.0, timeViewer.getCursorTime()/1000.0);
            timeViewer.fireSelectionDetached();            
        }
    }

    
    public void appendInterval(){
        partitur.commitEdit(true);

        Timeline tl = partitur.getModel().getTranscription().getBody().getCommonTimeline();

        // changed 09-06-2009: different method for determining the "last" timeline item
        // i.e. the thing onto which shalt be appended ye new intervalle
        // before: String lastID = tl.getTimelineItemAt(tl.getNumberOfTimelineItems()-2).getID();
        String lastID = partitur.getModel().getTranscription().getBody().getLastUsedTimelineItem();

        double time1 = tl.getPreviousTime(lastID);
        double time2 = Math.min(time1 + 2.0, tl.getMaxTime());
        //System.out.println("Interval: " + time1 + " / " + time2);

        // Undo information
        if (partitur.undoEnabled){
            UndoInformation undoInfo = new UndoInformation(partitur, "Append interval");
            int lower = partitur.getModel().lower(partitur.getModel().getTranscription().getBody().getCommonTimeline().lookupID(lastID));
            int upper = partitur.getModel().getTranscription().getBody().getCommonTimeline().size()-1;
            undoInfo.memorizeRegion(partitur,lower,upper);
            partitur.addUndo(undoInfo);
            //System.out.println("Added undo append interval");
        }
        // end undo information

        // Added 08-04-2011
        if ((time1<0) || (time2<0)) return;

        int[] cols = partitur.getModel().insertInterval(time1, time2, 0.001);
        int col1 = cols[0];
        int col2 = cols[1];
        // added 06-04-2009 to make entering text easier        
        partitur.setPixelWidth(col1, 200);
        partitur.makeColumnVisible(col1+1);
        // end add
        
        partitur.setNewSelection(-1, -1, col1, col2-1);
        playSelection();
    }

    public void addMask(){
        //override in subclasses
    }
    
    public void addEvent(){
        // added 08-04-2011
        if ((selectionStart<0) || (selectionEnd<0)) return;

        // Undo information
        if (partitur.undoEnabled){
            UndoInformation undoInfo = new UndoInformation(partitur, "Add event");
            int t0 = partitur.getModel().getTranscription().getBody().getCommonTimeline().findTimelineItem(selectionStart/1000.0, 0.001);
            if (t0<0){
                t0 = partitur.getModel().getTranscription().getBody().getCommonTimeline().getPositionForTime(selectionStart/1000.0);
            }
            int t1 = partitur.getModel().getTranscription().getBody().getCommonTimeline().findTimelineItem(selectionEnd/1000.0, 0.001);
            if (t1<0){
                t1 = partitur.getModel().getTranscription().getBody().getCommonTimeline().getPositionForTime(selectionEnd/1000.0);
            }
            int lower = partitur.getModel().lower(t0-2);
            int upper = partitur.getModel().upper(t1+2);
            undoInfo.memorizeRegion(partitur,lower,upper);
            partitur.addUndo(undoInfo);
            //System.out.println("Added undo add event");
        }
        // end undo information


        // changed 01-04-2009: commiting edit resets the
        // selection, so I have to get it before commiting edit
        double selStart = selectionStart;
        double selEnd = selectionEnd;

        partitur.commitEdit(true);
        //int col1 = partitur.getModel().insertTimelineItem(selectionStart/1000.0, 0.001);
        //int col2 = partitur.getModel().insertTimelineItem(selectionEnd/1000.0, 0.001);
        int[] cols = partitur.getModel().insertInterval(selStart/1000.0, selEnd/1000.0, 0.001);
        int col1 = cols[0];
        int col2 = cols[1];

        partitur.setNewSelection(-1, -1, col1, col2-1);
        TimelineItem tli1 = partitur.getModel().getTimelineItem(col1);
        TimelineItem tli2 = partitur.getModel().getTimelineItem(col2);

        JDialog dialog = new JDialog((JFrame)(partitur.getTopLevelAncestor()), true);
        dialog.setLayout(new java.awt.BorderLayout());
        dialog.setUndecorated(true);
        TierSelectionPanel tsp = new TierSelectionPanel(partitur.getModel().getTranscription(), tli1.getID(), tli2.getID());
        if (tsp.isATierAvailable()){
            dialog.getContentPane().add(tsp);
            dialog.pack();
            dialog.setLocationRelativeTo(addEventInvoker);
            dialog.setVisible(true);
        }
        if (tsp.tierList.getSelectedValue()!=null){
            Tier tier = (Tier)(tsp.tierList.getSelectedValue());
            org.exmaralda.partitureditor.jexmaralda.Event newEvent = new org.exmaralda.partitureditor.jexmaralda.Event();
            newEvent.setStart(tli1.getID());
            newEvent.setEnd(tli2.getID());
            tier.addEvent(newEvent);

            int row = partitur.getModel().getTranscription().getBody().lookupID(tier.getID());
            partitur.getModel().fireEventAdded(row, col1, col2);
            // added 26-05-2009 to make entering text easier
            partitur.setPixelWidth(col1, 200);
            partitur.makeColumnVisible(col1+1);
            // end add
            partitur.setNewSelection(row, col1, true);
        } else {
            partitur.setNewSelection(-1, -1, col1, col2-1);
        }


    }

    /** process changes in the partitur view */
    @Override
    public void partitureTablePropertyChanged(PartitureTableEvent e) {
        //System.out.println("THIS IS AbstractTimeviewPartiturPlayerControl");
        if (e.getID()==PartitureTableEvent.MEDIA_TIME_CHANGED){
            if (partitur.selectionStartCol<0) {
                // don't set a selection in the timeviewer
                // when (e.g.) a whole tier is selected
                detachSelection();
                return;
            }
            if (!(partitur.selectionIsAnchored())){
                // don't set a selection in the timeviewer
                // when there are no absolute time values
                // for the selection start and end
                detachSelection();
                return;
            }
            Object o = e.getInfo();
            Double[] times = (Double[])o;
            double minTime = times[0].doubleValue()*1000.0;
            double maxTime = times[1].doubleValue()*1000.0;
            // added 15-04-2009
            timeViewer.setCursorTime(minTime);
            timeViewer.setSelectionInterval(minTime, maxTime, true, (!partitur.getModel().timeProportional));
            int add = 1;
            if ((partitur.aSingleCellIsSelected)||(partitur.aSeriesOfCellsIsSelected)){
                // make sure to add the cell span to the selectionEndCol
                add = partitur.getModel().getCellSpan(partitur.selectionStartRow, partitur.selectionEndCol);
                //System.out.println("ADD= " + add + " " + partitur.selectionStartRow + " " + partitur.selectionEndCol);
            }


            org.exmaralda.partitureditor.jexmaralda.Timeline tl = partitur.getModel().getTranscription().getBody().getCommonTimeline();

            startPoint = tl.getTimelineItemAt(partitur.selectionStartCol);
            endPoint = tl.getTimelineItemAt(partitur.selectionEndCol+add);

            // find the previous and following point in the timeline and
            // set the drag boundaries accordingly
            double previousTime =  0.0;
            if (partitur.selectionStartCol>0){
                previousTime = tl.getTimelineItemAt(partitur.selectionStartCol-1).getTime();
            }
            double nextTime =  player.getTotalLength();
            if (partitur.selectionEndCol+add<tl.getNumberOfTimelineItems()-1){
                nextTime = tl.getNextTime(tl.getTimelineItemAt(partitur.selectionEndCol+add+1).getID());
            }
            timeViewer.setLeftDragBoundary(previousTime*1000.0);
            timeViewer.setRightDragBoundary(nextTime*1000.0);

            detachSelectionAction.setEnabled(true);            

        }
    }

    @Override
    public void processTimeSelectionEvent(TimeSelectionEvent event) {
        if (event.getType()==TimeSelectionEvent.ZOOM_CHANGED){
            partitur.getModel().setPixelsPerSecond(timeViewer.getPixelsPerSecond());
            return;
        }
        if ((event.getType()!=TimeSelectionEvent.START_TIME_CHANGED) &&
                (event.getType()!=TimeSelectionEvent.END_TIME_CHANGED)){
            unselectTimepoints();
        }
        selectionStart = timeViewer.getSelectionStartTime();
        selectionEnd = timeViewer.getSelectionEndTime();
        if (selectionStart>selectionEnd){
            double mem=selectionStart; selectionStart=selectionEnd; selectionEnd=mem;
        }
        playSelectionAction.setEnabled(selectionStart!=selectionEnd);
        playFirstSecondOfSelectionAction.setEnabled(selectionStart!=selectionEnd);
        playLastSecondOfSelectionAction.setEnabled(selectionStart!=selectionEnd);
        playFirstSecondBeforeSelectionAction.setEnabled(selectionStart!=selectionEnd);
        playFirstSecondAfterSelectionAction.setEnabled(selectionStart!=selectionEnd);
        loopSelectionAction.setEnabled(selectionStart!=selectionEnd);
        shiftSelectionAction.setEnabled(selectionStart!=selectionEnd);
        detachSelectionAction.setEnabled(timeViewer.selectionAttached);
        playAction.setEnabled(timeViewer.getCursorTime()>=0);
        assignTimesAction.setEnabled(
                ((selectionStart!=selectionEnd) && (partitur.aWholeColumnIsSelected || partitur.aSeriesOfColumnsIsSelected))
                || (selectionStart==selectionEnd) && (partitur.aWholeColumnIsSelected));

        decreaseSelectionStartAction.setEnabled(selectionStart!=selectionEnd);
        increaseSelectionStartAction.setEnabled(selectionStart!=selectionEnd);
        decreaseSelectionEndAction.setEnabled(selectionStart!=selectionEnd);
        increaseSelectionEndAction.setEnabled(selectionStart!=selectionEnd);

        addEventInPartiturAction.setEnabled(selectionStart!=selectionEnd);

    }

    public void unselectTimepoints(){
        startPoint = null;
        endPoint = null;
        timeViewer.resetDragBoundaries();        
    }

    public void moveTimepoints(TimeSelectionEvent event){
        TimelineItem tli = null;
        double oldTime = -1;
        if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED) && (startPoint!=null)){
            tli = (TimelineItem)startPoint;
            oldTime = tli.getTime();
            tli.setTime(event.getStartTime()/1000.0);
            tli.setType("");
            partitur.getModel().fireColumnLabelsChanged();
            if (partitur.getModel().timeProportional){
                int col = partitur.getModel().getTranscription().getBody().getCommonTimeline().lookupID(tli.getID());
                if (col>0){
                    partitur.sizeColumnWidth(col-1);                    
                }
                partitur.sizeColumnWidth(col);
            }
        } else if ((event.getType()==TimeSelectionEvent.END_TIME_CHANGED) && (endPoint!=null)){
            tli = (TimelineItem)endPoint;
            oldTime = tli.getTime();
            tli.setTime(event.getEndTime()/1000.0);
            tli.setType("");
            partitur.getModel().fireColumnLabelsChanged();
            if (partitur.getModel().timeProportional){
                int col = partitur.getModel().getTranscription().getBody().getCommonTimeline().lookupID(tli.getID());
                if (col>0){
                    partitur.sizeColumnWidth(col-1);
                }
                partitur.sizeColumnWidth(col);
            }
        }
        if ((partitur.undoEnabled) && (tli!=null) && ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED) || (event.getType()==TimeSelectionEvent.END_TIME_CHANGED))){
            // Undo information
            UndoInformation undoInfo = new UndoInformation(partitur, "Move timepoint");
            undoInfo.memorizeTime(tli, oldTime);
            partitur.addUndo(undoInfo);
            // end undo information
        }
    }

    public abstract void displayException(Exception ex);

    @Override
    public void processPlayableEvent(PlayableEvent e) {
        int type = e.getType();
        switch (type){
            case PlayableEvent.SOUNDFILE_SET :
                playAction.setEnabled(true);
                playSelectionAction.setEnabled(false);
                playFirstSecondOfSelectionAction.setEnabled(false);
                playLastSecondOfSelectionAction.setEnabled(false);
                playFirstSecondBeforeSelectionAction.setEnabled(false);
                playFirstSecondAfterSelectionAction.setEnabled(false);
                loopSelectionAction.setEnabled(false);
                pauseAction.setEnabled(false);
                stopAction.setEnabled(false);
                playerState=PLAYER_IDLE;
                break;
            case PlayableEvent.PLAYBACK_STARTED :
                //System.out.println("Received playback started");
                playAction.setEnabled(false);
                playSelectionAction.setEnabled(false);
                playFirstSecondOfSelectionAction.setEnabled(false);
                playLastSecondOfSelectionAction.setEnabled(false);
                playFirstSecondBeforeSelectionAction.setEnabled(false);
                playFirstSecondAfterSelectionAction.setEnabled(false);
                loopSelectionAction.setEnabled(false);
                // changed 11-05-2009: pause causes trouble when looping
                //pauseAction.setEnabled(true);
                pauseAction.setEnabled(!loopPlay);
                stopAction.setEnabled(true);
                playerState = PLAYER_PLAYING;
                break;
            case PlayableEvent.PLAYBACK_STOPPED :
                //System.out.println("Received playback stopped");
                playerState=PLAYER_IDLE;
                playAction.setEnabled(true && !loopPlay);
                playSelectionAction.setEnabled(selectionStart!=selectionEnd && !loopPlay);
                playFirstSecondOfSelectionAction.setEnabled(selectionStart!=selectionEnd && !loopPlay);
                playLastSecondOfSelectionAction.setEnabled(selectionStart!=selectionEnd && !loopPlay);
                playFirstSecondBeforeSelectionAction.setEnabled(selectionStart!=selectionEnd && !loopPlay);
                playFirstSecondAfterSelectionAction.setEnabled(selectionStart!=selectionEnd && !loopPlay);
                loopSelectionAction.setEnabled(selectionStart!=selectionEnd && !loopPlay);
                pauseAction.setEnabled(false && !loopPlay);
                stopAction.setEnabled(false);
                break;
            case PlayableEvent.PLAYBACK_HALTED :
                playAction.setEnabled(false);
                playSelectionAction.setEnabled(false);
                playFirstSecondOfSelectionAction.setEnabled(false);
                playLastSecondOfSelectionAction.setEnabled(false);
                playFirstSecondBeforeSelectionAction.setEnabled(false);
                playFirstSecondAfterSelectionAction.setEnabled(false);
                loopSelectionAction.setEnabled(false);
                pauseAction.setEnabled(true);
                stopAction.setEnabled(true);
                playerState = PLAYER_HALTED;
                break;
            case PlayableEvent.PLAYBACK_RESUMED :
                playAction.setEnabled(false);
                playSelectionAction.setEnabled(false);
                playFirstSecondOfSelectionAction.setEnabled(false);
                playLastSecondOfSelectionAction.setEnabled(false);
                playFirstSecondBeforeSelectionAction.setEnabled(false);
                playFirstSecondAfterSelectionAction.setEnabled(false);
                loopSelectionAction.setEnabled(false);
                pauseAction.setEnabled(true);
                stopAction.setEnabled(true);
                playerState = PLAYER_PLAYING;
                break;
            case PlayableEvent.POSITION_UPDATE :
                break;
            default: break;
        }
    }
    
    public abstract JToggleButton getZoomToggleButton();

    public void changeZoom(){
        if (!(getZoomToggleButton().isSelected())){
            changeZoomDialog.setVisible(false);
            return;
        }
        java.awt.Point p = getZoomToggleButton().getLocationOnScreen();
        changeZoomDialog.setLocation(p.x - changeZoomDialog.getWidth(), p.y + getZoomToggleButton().getHeight());
        int spp = (int)Math.round(timeViewer.getSecondsPerPixel()*1000);
        changeZoomDialog.zoomLevelSlider.setValue(spp);

        int mag = (int) Math.round(timeViewer.getVerticalMagnify() * 10);
        changeZoomDialog.magnifyLevelSlider.setValue(mag);

        changeZoomDialog.setVisible(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource()==changeZoomDialog.zoomLevelSlider){
            if (changeZoomDialog.zoomLevelSlider.getValueIsAdjusting()) return;
            int v = changeZoomDialog.zoomLevelSlider.getValue();
            double pixelsPerSecond = 1000.0 / v;
            timeViewer.setPixelsPerSecond(pixelsPerSecond);
            partitur.getModel().setPixelsPerSecond(pixelsPerSecond);
        } else if (e.getSource()==changeZoomDialog.magnifyLevelSlider){
            if (changeZoomDialog.magnifyLevelSlider.getValueIsAdjusting()) return;
            int v = changeZoomDialog.magnifyLevelSlider.getValue();
            double magnification = v / 10.0;
            timeViewer.setVerticalMagnify(magnification);
        }
    }


    public void processAssignments(Object[][] assignments, JComponent c, int condition) {
        for (Object[] assignment : assignments) {
            String keyName = (String) (assignment[0]);
            String actionName = (String) (assignment[1]);
            //System.out.println(keyName + " " + actionName);
            AbstractAction action = (AbstractAction) (assignment[2]);
            c.getInputMap(condition).put(KeyStroke.getKeyStroke(keyName), actionName);
            c.getActionMap().put(actionName, action);
        }
    }

    // ********************
    // Convencience methods for communicating with the loop thread
    // added 17-08-2009

    boolean getLoopMode(){
        return loopPlay;
    }

    boolean playerIsPlaying(){
        return (playerState==PLAYER_PLAYING);
    }

    int getUserTimeBetweenLoops(){
        return TIME_BETWEEN_LOOPS;
    }

    //public void startLoop(long begin, long end) {
    public void startLoop(){
        // stop current loop if necessary
        if ((loopThread != null) && loopThread.isAlive()) {
            loopThread.stopLoop();
        }

        //loopThread = new LoopThread(begin, end);
        loopThread = new LoopThread();
        loopThread.start();
    }

    /**
     * Stops the current loop thread, if active.
     */
    public void stopLoop() {
        //setPlaySelectionMode(false);

        if ((loopThread != null) && loopThread.isAlive()) {
            loopThread.stopLoop();
        }
    }


    // ********************


    /**
     * Starts a new playing thread when loopmode is true
     * taken from ELAN class ElanMediaPlayerControl
     * added 17-08-2009: new loop implementation
     */
    private class LoopThread extends Thread {
        //private long beginTime;
        //private long endTime;
        private boolean stopLoop = false;

        /**
         * Creates a new LoopThread instance
         *
         * @param begin the interval begin time
         * @param end the interval endtime
         */
        /*LoopThread(long begin, long end) {
            this.beginTime = begin;
            this.endTime = end;
        }*/
        LoopThread(){
            
        }

        /**
         * Sets the flag that indicates that the loop thread should stop to
         * true.
         */
        public void stopLoop() {
            stopLoop = true;
        }

        /**
         * Restarts the player to play the interval as long as the controller
         * is in  loop mode and the loop is not explicitely stopped.
         */
        public void run() {
            while (!stopLoop && getLoopMode()) {
                if (!playerIsPlaying()) {
                    //playInterval(beginTime, endTime);
                    playSelection();
                }

                while (playerIsPlaying() == true) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception ex) {
                    }
                }

                try {
                    Thread.sleep(getUserTimeBetweenLoops());
                } catch (Exception ex) {
                }
            }
        }
    }
}


