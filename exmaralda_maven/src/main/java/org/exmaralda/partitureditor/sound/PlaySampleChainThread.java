/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.sound;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bernd
 */
public class PlaySampleChainThread extends Thread {
    
    Playable player;
    List<double[]> startEndPairs;
    long waitTimeInMiliseconds;

    public PlaySampleChainThread(Playable player, List<double[]> startEndPairs, long waitTimeInMiliseconds) {
        this.player = player;
        this.startEndPairs = startEndPairs;
        this.waitTimeInMiliseconds = waitTimeInMiliseconds;
    }

    @Override
    public void run() {
        player.stopPlayback();
        for (double[] startEndPair : startEndPairs){
            try {
                double start = startEndPair[0];
                double end = startEndPair[1];
                player.setStartTime(start);
                player.setEndTime(end);
                System.out.println("Playing from " + start + " to " + end);
                player.startPlayback();
                wait((long) ((end-start)*1000));
                wait(waitTimeInMiliseconds);
            } catch (InterruptedException ex) {
                Logger.getLogger(PlaySampleChainThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    
    
    
}
