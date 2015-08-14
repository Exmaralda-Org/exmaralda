/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.masker;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Schmidt
 */
public class AnalyseBrownNoise {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new AnalyseBrownNoise().doit();
        } catch (IOException ex) {
            Logger.getLogger(AnalyseBrownNoise.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WavFileException ex) {
            Logger.getLogger(AnalyseBrownNoise.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, WavFileException {
        File fileIn = new File("C:\\Users\\Schmidt\\Dropbox\\IDS\\FOLK\\BrownNoise.wav");
        WavFile wavFileIn = WavFile.openWavFile(fileIn);
        wavFileIn.display();
        long[] buffer = new long[10000 * wavFileIn.getNumChannels()];

        do {
             // Read frames into buffer                    
             int framesRead = wavFileIn.readFrames(buffer, 10000);
             //for (long l : buffer){
             //    System.out.println(l);
             //}
             long l_even = buffer[0];
             long l_odd = buffer[1];
             long max = 0;
             long max2 = 0;
             long all = 0;
             long all2 = 0;
             for (int i=2; i<buffer.length-1; i+=2){
                 System.out.println(l_even-buffer[i]);
                 max = Math.max(max, Math.abs(l_even-buffer[i]));
                 all+=Math.abs(l_even-buffer[i]);
                 all2+=Math.abs(buffer[i]);
                 max2 = Math.max(max2, Math.abs(buffer[i]));
                 l_even = buffer[i];
                 l_odd = buffer[i+1];
             }
             
             System.out.println("MAX: " + max);
             System.out.println("MAX2: " + max2);
             System.out.println("MEAN DELTA: " + all/(buffer.length/2));
             System.out.println("MEAN: " + all2/(buffer.length/2));

        } while (false);
        
    }
}
