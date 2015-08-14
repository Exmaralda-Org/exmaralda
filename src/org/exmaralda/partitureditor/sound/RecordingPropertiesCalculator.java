/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.sound;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author thomas
 */
public class RecordingPropertiesCalculator {

    public static double getRecordingDuration(File recordingFile){
        if (!(recordingFile.exists())) return -1.0;
        if (!(recordingFile.canRead())) return -1.0;
        AbstractPlayer player;
        player = new JMFPlayer();
        double result = -1.0;
        try {
            player.setSoundFile(recordingFile.getAbsolutePath());
            result = player.getTotalLength();
            player = null;
        } catch (Exception ex) {
            String os = System.getProperty("os.name").substring(0,3);
            if (os.equalsIgnoreCase("mac")){
                try {
                    player = new ELANQTPlayer();
                    player.setSoundFile(recordingFile.getAbsolutePath());
                    result = player.getTotalLength();
                    ((ELANQTPlayer)(player)).wrappedPlayer.cleanUpOnClose();
                    player = null;
                } catch (IOException ex1) {
                    try {
                        player = new QuicktimePlayer();
                        player.setSoundFile(recordingFile.getAbsolutePath());
                        result = player.getTotalLength();
                        player = null;
                    } catch (Exception ex2) {
                        // give up
                        return -1.0;
                    }
                }
            } else if (os.equalsIgnoreCase("win")){
                try {
                    player = new JDSPlayer();
                    player.setSoundFile(recordingFile.getAbsolutePath());
                    result = player.getTotalLength();
                    ((JDSPlayer)(player)).wrappedPlayer.cleanUpOnClose();
                    player = null;
                } catch (IOException ex1) {
                    // give up
                    return -1.0;
                }
            } else {
                return -1.0;
            }
        }
        return result;
    }

}
