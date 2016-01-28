/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.sound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.Mixer;

/**
 *
 * @author Schmidt
 */
public class CheckMixer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new CheckMixer().doit();
    }

    private void doit() {
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        int count=1;
        for (Mixer.Info mi : mixerInfo){            
            Mixer mixer = AudioSystem.getMixer(mi);
            System.out.println(count + ")" + mixer.getMixerInfo().getDescription());
            count++;
            Control[] controls = mixer.getControls();
            for (Control c : controls){
                System.out.println("-" + c.toString());
            }
        }
    }
    
}
