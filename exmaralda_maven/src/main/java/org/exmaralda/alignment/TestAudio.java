/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.alignment;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Schmidt
 */
public class TestAudio {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new TestAudio().doit();
    }

    private void doit() {
        File dir = new File("Y:\\media\\audio\\DS");
        File[] wavFiles = dir.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wav");
            }            
        });
        
        for (File wavFile : wavFiles){
            System.out.print(wavFile.getName() + "...");
            try {    
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
                System.out.println("OK");
            } catch (UnsupportedAudioFileException ex) {
                System.out.println("UnsupportedAudioFileException: " + ex.getLocalizedMessage());
            } catch (IOException ex) {
                System.out.println("IOException: " + ex.getLocalizedMessage());
            }
        }
        
    }
    
    
}
