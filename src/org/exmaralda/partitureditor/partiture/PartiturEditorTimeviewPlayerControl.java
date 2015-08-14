/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.partiture;

import javax.swing.JFrame;
import javax.swing.JToggleButton;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.folker.application.AbstractTimeviewPartiturPlayerControl;
import org.exmaralda.folker.timeview.AbstractTimeProportionalViewer;
import org.exmaralda.folker.timeview.ChangeZoomDialog;
import org.exmaralda.folker.timeview.TimeSelectionEvent;
import org.exmaralda.partitureditor.sound.CocoaQTPlayer;
import org.exmaralda.partitureditor.sound.ELANDSPlayer;
import org.exmaralda.partitureditor.sound.ELANQTPlayer;
import org.exmaralda.partitureditor.sound.JDSPlayer;
import org.exmaralda.partitureditor.sound.JMFPlayer;
import org.exmaralda.partitureditor.sound.MMFPlayer;
import org.exmaralda.partitureditor.sound.Playable;
import org.exmaralda.partitureditor.sound.PlayableEvent;

/**
 *
 * @author thomas
 */
public class PartiturEditorTimeviewPlayerControl extends AbstractTimeviewPartiturPlayerControl {

    JToggleButton zoomToggleButton;

    public PartiturEditorTimeviewPlayerControl(ExmaraldaApplication ac, AbstractTimeProportionalViewer tv, PartitureTableWithActions pt, Playable p) {
        super(ac, tv, pt, p);

        changeZoomDialog = new ChangeZoomDialog((JFrame)ac, false);
        changeZoomDialog.zoomLevelSlider.addChangeListener(this);
        changeZoomDialog.magnifyLevelSlider.addChangeListener(this);

    }

    @Override
    public void displayException(Exception ex) {
        // TODO?
        ex.printStackTrace();
    }

    @Override
    public void processPlayableEvent(PlayableEvent e) {
        // TODO?
        super.processPlayableEvent(e);
    }

    @Override
    public void processTimeSelectionEvent(TimeSelectionEvent event) {
        // TODO?
        super.processTimeSelectionEvent(event);
        super.moveTimepoints(event);
        if ((playerState==PLAYER_IDLE)){
            if (player instanceof JMFPlayer){
                JMFPlayer jmfp = (JMFPlayer)player;
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    jmfp.updateVideo(selectionStart/1000.0);
                } else {
                    jmfp.updateVideo(selectionEnd/1000.0);
                }
            } else if (player instanceof ELANDSPlayer){
                ELANDSPlayer jmfp = (ELANDSPlayer)player;
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    jmfp.updateVideo(selectionStart/1000.0);
                } else {
                    jmfp.updateVideo(selectionEnd/1000.0);
                }
            } else if (player instanceof JDSPlayer){
                JDSPlayer jmfp = (JDSPlayer)player;
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    jmfp.updateVideo(selectionStart/1000.0);
                } else {
                    jmfp.updateVideo(selectionEnd/1000.0);
                }
            } else if (player instanceof ELANQTPlayer){
                ELANQTPlayer jmfp = (ELANQTPlayer)player;
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    jmfp.updateVideo(selectionStart/1000.0);
                } else {
                    jmfp.updateVideo(selectionEnd/1000.0);
                }
            } else if (player instanceof CocoaQTPlayer){
                CocoaQTPlayer jmfp = (CocoaQTPlayer)player;
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    jmfp.updateVideo(selectionStart/1000.0);
                } else {
                    jmfp.updateVideo(selectionEnd/1000.0);
                }
            } else if (player instanceof MMFPlayer){
                MMFPlayer jmfp = (MMFPlayer)player;
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    jmfp.updateVideo(selectionStart/1000.0);
                } else {
                    jmfp.updateVideo(selectionEnd/1000.0);
                }
            } 
        }

    }

    @Override
    public void detachSelection(){
        super.detachSelection();
        unselectTimepoints();
        detachSelectionAction.setEnabled(false);
    }

    @Override
    public JToggleButton getZoomToggleButton() {
        return this.zoomToggleButton;
    }








}
