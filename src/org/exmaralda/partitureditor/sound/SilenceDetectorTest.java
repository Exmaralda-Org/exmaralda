/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.sound;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.exmaralda.masker.WavFileException;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;

/**
 *
 * @author Schmidt
 */
public class SilenceDetectorTest implements SilenceDetectorListener {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new SilenceDetectorTest().doit();
        } catch (IOException ex) {
            Logger.getLogger(SilenceDetectorTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WavFileException ex) {
            Logger.getLogger(SilenceDetectorTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(SilenceDetectorTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(SilenceDetectorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processProgress(int countIterations, int totalIterations, long startTime, long time) {
        System.out.println(countIterations + " (" + totalIterations + ") / " + time + " " + startTime);
    }

    private void doit() throws IOException, WavFileException, JexmaraldaException, UnsupportedAudioFileException {
        File audioFile = new File("c:\\users\\Schmidt\\Dropbox\\HAMATAC\\Korpus\\Ali_Dimitri\\MT_091209_Ali.wav");
        SilenceDetector sd = new SilenceDetector(audioFile);
        sd.addSilenceDetectorListener(this);
        sd.performSilenceDetection(audioFile);
    }
    
}
