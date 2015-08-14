/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.masker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Schmidt
 */
public class Masker {

    ArrayList<MaskerListener> listeners = new ArrayList<MaskerListener>();
    
    WavFile wavFileIn;
    WavFile wavFileOut;
    
    
    int numChannels;
    long numFrames;
    
    long totalFramesRead = 0;
    
    BrownNoiseGenerator brownNoiseGenerator = new BrownNoiseGenerator(-0.4,0.4);   
    double[] brownNoise = null;
    int brownNoisePointer = 0;
    
    
    public static final int METHOD_SILENCE = 0;
    public static final int METHOD_BROWN_NOISE = 1;
    public static final int METHOD_BROWN_NOISE_COPIED = 2;

    public Masker(File fileIn, File fileOut) throws IOException, WavFileException, URISyntaxException, ClassNotFoundException {
        wavFileIn = WavFile.openWavFile(fileIn);
        numChannels = wavFileIn.getNumChannels(); 
        numFrames = wavFileIn.getNumFrames();
        wavFileOut = WavFile.newWavFile(fileOut, numChannels, numFrames, wavFileIn.getValidBits(), wavFileIn.getSampleRate());
        initBrownNoiseWavFile();
    }

    private double[] initBrownNoiseWavFile() throws URISyntaxException, IOException, WavFileException, ClassNotFoundException{
        //URL resource = getClass().getResource("/org/exmaralda/masker/ImmerzBrownNoise_9db.ser");
        //File brownNoiseFile = new File(resource.toURI());
        // Deserialize the int[]
        //ObjectInputStream in = new ObjectInputStream(new FileInputStream(brownNoiseFile));
        ObjectInputStream in = new ObjectInputStream(getClass().getResourceAsStream("/org/exmaralda/masker/ImmerzBrownNoise_9db.ser"));
        double[] samples = (double[]) in.readObject();
        in.close();    
        return samples;
    }
    
    public void mask(int method, double[][] maskTimes) throws IOException, WavFileException, URISyntaxException, ClassNotFoundException{
        long sampleRate = wavFileIn.getSampleRate();
        for (double[] t : maskTimes){
            // copy to t[0]
            copyFrames((long) (t[0]*((double)sampleRate)));
            // write mask  to [t1]
            maskFrames((long) (t[1]*((double)sampleRate)), method);
        }
        // copy what's left
        copyFrames(wavFileIn.getNumFrames());

        // close the wavFiles
        wavFileIn.close();
        wavFileOut.close();
        fireMaskerEvent(MaskerEvent.DONE);
        
    }
    
        
    private void copyFrames(long end) throws IOException, WavFileException {
        int framesRead;

        // Create a buffer of 100 frames
        double[] buffer = new double[100 * numChannels];
        //long[] buffer = new long[100 * numChannels];

        do {
             // Read frames into buffer                    
             framesRead = wavFileIn.readFrames(buffer, 100);
             totalFramesRead+=framesRead;
             wavFileOut.writeFrames(buffer, framesRead);
             fireMaskerEvent(MaskerEvent.COPY_ACTIVITY);
             

        } while (wavFileIn.getNumFrames() - wavFileIn.getFramesRemaining() + 100 < end);
        
        // remaining frames not fitting into 100 buffer
        int remaining = (int) (end - totalFramesRead);
        System.out.println(remaining + " REMAINING TO COPY");        
        if (remaining>=0){
            buffer = new double[remaining * numChannels];
            //buffer = new long[remaining * numChannels];
            framesRead = wavFileIn.readFrames(buffer, remaining);
            totalFramesRead+=framesRead;
            wavFileOut.writeFrames(buffer, framesRead);
        }
            
            
    }

    private void maskFrames(long end, int method) throws IOException, WavFileException, URISyntaxException, ClassNotFoundException {
        int framesRead;

        // Create a buffer of 100 frames
        double[] buffer = new double[100 * numChannels];
        //long[] buffer = new long[100 * numChannels];

        do {
                // Read frames into buffer                    
                framesRead = wavFileIn.readFrames(buffer, 100);
                totalFramesRead+=framesRead;              
                double[] mask = getMask(buffer, method);                
                wavFileOut.writeFrames(mask, framesRead);
                fireMaskerEvent(MaskerEvent.MASK_ACTIVITY);


        } while (wavFileIn.getNumFrames() - wavFileIn.getFramesRemaining() + 100 < end);

        // remaining frames not fitting into 100 buffer
        int remaining = (int) (end - totalFramesRead);
        //System.out.println(remaining + " REMAINING TO MASK");
        if (remaining>=0){
            //buffer = new long[remaining * numChannels];
            buffer = new double[remaining * numChannels];
            double[] mask = getMask(buffer, method);
            framesRead = wavFileIn.readFrames(buffer, remaining);
            totalFramesRead+=framesRead;            
            wavFileOut.writeFrames(mask, framesRead);
        }
            
    }

    
    //private long[] getMask(long[] originalBuffer, int method) throws IOException, WavFileException, URISyntaxException {
    private double[] getMask(double[] originalBuffer, int method) throws IOException, WavFileException, URISyntaxException, ClassNotFoundException {
        //long[] mask = new long[originalBuffer.length];
        double[] mask = new double[originalBuffer.length];
        if (method==Masker.METHOD_SILENCE){
            Arrays.fill(mask, 0.0);
        } else if (method==Masker.METHOD_BROWN_NOISE){
            for (int c=0; c<numChannels; c++){
                for (int pos=c; pos<mask.length; pos+=numChannels){
                    mask[pos]=brownNoiseGenerator.getNext();
                }
            }
        } else if (method==Masker.METHOD_BROWN_NOISE_COPIED){
            if (brownNoise==null){
                brownNoise = initBrownNoiseWavFile();  
            }
            for (int c=0; c<numChannels; c++){
                for (int pos=c; pos<mask.length; pos+=numChannels){
                    mask[pos] = brownNoise[(brownNoisePointer+pos)%brownNoise.length];
                }
            }
            brownNoisePointer+=mask.length;
        }
        return mask;
    }
    
    
    // ****************** listener methods *********************
    
    public void addMaskerListener(MaskerListener listener){
        listeners.add(listener);
    }
    
    public void fireMaskerEvent(int activity){
        //System.out.println(totalFramesRead + " frames read in total");
        MaskerEvent event = new MaskerEvent(wavFileIn.getFramesRemaining(), numFrames, activity);
        for (MaskerListener l : listeners){
            l.processMaskerEvent(event);
        }
    }
    
}
