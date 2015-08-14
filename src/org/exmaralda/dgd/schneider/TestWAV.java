/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.sound.BASAudioPlayer;

/**
 *
 * @author Schmidt
 */
public class TestWAV {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new TestWAV().doit();
    }

    String PF1 = "W:\\TOENE\\PF\\PF3\\PF399AW1.WAV";

    public void doit() {
        try {
            BASAudioPlayer bap = new BASAudioPlayer();
            bap.setSoundFile(PF1);
            System.out.println(bap.getTotalLength());
        } catch (IOException ex) {
            Logger.getLogger(TestWAV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
