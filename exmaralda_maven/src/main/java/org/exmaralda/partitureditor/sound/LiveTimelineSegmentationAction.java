/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.sound;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author thomas
 */
public class LiveTimelineSegmentationAction extends AbstractAction {

    LiveTimelineSegmentationDialog dialog;

    public LiveTimelineSegmentationAction(LiveTimelineSegmentationDialog d) {
        dialog = d;
    }



    public void actionPerformed(ActionEvent e) {
        dialog.processAction();
    }

}
