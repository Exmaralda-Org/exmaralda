/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.alignment;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Schmidt
 */
public class TestAlignment {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new TestAlignment().doit();
    }

    private void doit() {
        try {
            File txtDir = new File("C:\\Users\\Schmidt\\Desktop\\Alignment\\FIX\\TXT");
            File wavDir = new File("C:\\Users\\Schmidt\\Desktop\\Alignment\\FIX\\WAV");
            File praatDir = new File("C:\\Users\\Schmidt\\Desktop\\Alignment\\FIX\\PRAAT");
            
            Aligner aligner = new Aligner(wavDir, praatDir);
            aligner.doAlignment(wavDir, txtDir, praatDir);
        } catch (IOException ex) {
            Logger.getLogger(TestAlignment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
