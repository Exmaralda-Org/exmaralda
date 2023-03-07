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
import org.exmaralda.partitureditor.sound.AVFPlayer;
import org.exmaralda.partitureditor.sound.JDSPlayer;
import org.exmaralda.partitureditor.sound.JavaFXPlayer;
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
        // changed 11-02-2020
        super.processTimeSelectionEvent(event);
        // possible place for issue #377
        super.moveTimepoints(event);
        if ((playerState==PLAYER_IDLE)){
             if (player instanceof JDSPlayer jds){
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    jds.updateVideo(selectionStart/1000.0);
                } else {
                    jds.updateVideo(selectionEnd/1000.0);
                }
            } else if (player instanceof JavaFXPlayer jfx){
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    jfx.updateVideo(selectionStart/1000.0);
                } else {
                    jfx.updateVideo(selectionEnd/1000.0);
                } 
            } else if (player instanceof AVFPlayer avf){
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    avf.updateVideo(selectionStart/1000.0);
                } else {
                    avf.updateVideo(selectionEnd/1000.0);
                }            
            } else if (player instanceof MMFPlayer mmf){
                //HEY HO BERND THE BUILDER!
                 //Timeview cursor update may cause problems?
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    mmf.updateVideo(selectionStart/1000.0);
                } else {
                    mmf.updateVideo(selectionEnd/1000.0);
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
