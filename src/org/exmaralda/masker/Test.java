/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.masker;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.folker.data.TranscriptionHead;

/**
 *
 * @author Schmidt
 */
public class Test implements MaskerListener {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Test().doit();
    }

    private void doit() {
        try {
            /*double[][] mask = {
                {1.0,2.0},
                {3.0,4.0},
                {5.0,6.0},
                {7.0,8.0},
                {9.0,10.0},
                {11.0,12.0}
            };*/
            
            File in = new File("C:\\Users\\Schmidt\\Desktop\\TEST\\Rudi_Voeller_Wutausbruch.WAV");
            File out = new File("C:\\Users\\Schmidt\\Desktop\\TEST\\Rudi_Voeller_Wutausbruch_MASK.WAV");
            
            double[][] mask = MaskTimeCreator.createTimesFromFOLKERTranscriptionHead(new TranscriptionHead(new File("C:\\Users\\Schmidt\\Desktop\\TEST\\Rudi_Voeller_Wutausbruch.flk")));
            for (double[] d : mask){
                System.out.println(d[0] + " / " + d[1]);
            }
            Masker m = new Masker(in, out);
            m.addMaskerListener(this);
            //m.mask(Masker.METHOD_SILENCE, mask);
            m.mask(Masker.METHOD_BROWN_NOISE_COPIED, mask);
            //m.mask(Masker.METHOD_WHITE_NOISE, mask);

        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processMaskerEvent(MaskerEvent maskerEvent) {
        if (true) return;
        double progress = 1.0 - ((double)maskerEvent.framesRemaining / (double)maskerEvent.framesTotal);
        System.out.print(progress);
        switch (maskerEvent.activity){
            case MaskerEvent.COPY_ACTIVITY : System.out.println(" Copying"); break;
            case MaskerEvent.MASK_ACTIVITY : System.out.println(" Masking"); break;
            case MaskerEvent.DONE : System.out.println(" Done"); break;
        }
    }

}
