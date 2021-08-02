/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.masker;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
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
    
    // construct brownNoiseGenerator furhter below where it is used
    //BrownNoiseGenerator brownNoiseGenerator = new BrownNoiseGenerator(-0.4,0.4);   
    double[] brownNoise = null;
    int brownNoisePointer = 0;
    
    
    public static final int METHOD_SILENCE = 0;
    public static final int METHOD_BROWN_NOISE = 1;
    public static final int METHOD_BROWN_NOISE_COPIED = 2;
    public static final int METHOD_SAMPLE = 3;
    private double[] lastBufferValues;

    public Masker(File fileIn, File fileOut) throws IOException, WavFileException, URISyntaxException, ClassNotFoundException {
        wavFileIn = WavFile.openWavFile(fileIn);
        numChannels = wavFileIn.getNumChannels(); 
        numFrames = wavFileIn.getNumFrames();
        wavFileOut = WavFile.newWavFile(fileOut, numChannels, numFrames, wavFileIn.getValidBits(), wavFileIn.getSampleRate());
        initBrownNoiseWavFile();
        
        // issue #269
        // Create a buffer of 1 frame
        lastBufferValues = new double[1 * numChannels];
        // Read frames into buffer
        wavFileIn.readFrames(lastBufferValues, 1);
        
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

        //double lastStart = 0.0;
        double maskStart;
        double lastEnd = 0.0;
        double maskEnd;
        for (double[] t : maskTimes){
            maskStart = t[0]*((double)sampleRate);
            maskEnd = t[1]*((double)sampleRate);
            if (maskStart != lastEnd){
                // copy to t[0]
                copyFrames((long) maskStart);
                // mask to t[1]
                maskFrames((long) maskEnd, method);
            }
            else {
                // write mask  to t[1]
                maskFrames((long) maskEnd, method);
            }
            lastEnd = maskEnd;
        }
        // copy what's left
        //System.out.println("copying what's left ");
        copyFrames(wavFileIn.getNumFrames());

        // close the wavFiles
        wavFileIn.close();
        wavFileOut.close();
        fireMaskerEvent(MaskerEvent.DONE);
        
    }
    
        
    private void copyFrames(long end) throws IOException, WavFileException {
        int framesRead;

        // Create a buffer of 100 frames
        int bufferSizeCopy = 1000;
        double[] buffer = new double[bufferSizeCopy * numChannels];
        //long[] buffer = new long[100 * numChannels];
        //lastBufferValues = Arrays.copyOfRange(buffer, buffer.length-numChannels, buffer.length); // in case there are no "remaining"
        do {
             // Read frames into buffer                    
             framesRead = wavFileIn.readFrames(buffer, bufferSizeCopy);
             totalFramesRead+=framesRead;
             lastBufferValues = Arrays.copyOfRange(buffer, buffer.length-numChannels, buffer.length);
             wavFileOut.writeFrames(buffer, framesRead);
             fireMaskerEvent(MaskerEvent.COPY_ACTIVITY);
             

        } while (wavFileIn.getNumFrames() - wavFileIn.getFramesRemaining() + bufferSizeCopy < end);
        
        // remaining frames not fitting into 100 buffer
        int remaining = (int) (end - totalFramesRead);
        System.out.println(remaining + " REMAINING TO COPY");        
        if (remaining>=0){
            buffer = new double[remaining * numChannels];
            //buffer = new long[remaining * numChannels];
            framesRead = wavFileIn.readFrames(buffer, remaining);
            lastBufferValues = Arrays.copyOfRange(buffer, buffer.length-numChannels, buffer.length);
            totalFramesRead+=framesRead;
            wavFileOut.writeFrames(buffer, framesRead);
        }
            
            
    }

    private void maskFrames(long end, int method) throws IOException, WavFileException, URISyntaxException, ClassNotFoundException {
        int framesRead;

        // Create a buffer of 100 frames
        // This was changed to 4000 frames to avoid repetitive impulses at a high rate
        int bufferSizeMask = 4000;
        double[] buffer = new double[bufferSizeMask * numChannels];
        //long[] buffer = new long[100 * numChannels];
        // in order to give the last sample value to the beginning of the mask
        
        do {
                // Read frames into buffer                    
                framesRead = wavFileIn.readFrames(buffer, bufferSizeMask);
                totalFramesRead+=framesRead;              
                double[] mask = getMask(buffer, lastBufferValues, method);                
                // give the previous last sample value to the beginning of the new mask
                lastBufferValues = Arrays.copyOfRange(mask, mask.length-numChannels, mask.length);
                wavFileOut.writeFrames(mask, framesRead);
                fireMaskerEvent(MaskerEvent.MASK_ACTIVITY);


        } while (wavFileIn.getNumFrames() - wavFileIn.getFramesRemaining() + bufferSizeMask < end);

        // remaining frames not fitting into 4000 buffer
        int remaining = (int) (end - totalFramesRead);
        //System.out.println(remaining + " REMAINING TO MASK");
        if (remaining>=0){
            System.out.println(remaining + " REMAINING TO MASK");
            //buffer = new long[remaining * numChannels];
            buffer = new double[remaining * numChannels];
            framesRead = wavFileIn.readFrames(buffer, remaining);
            totalFramesRead+=framesRead;            
            double[] mask = getMask(buffer, lastBufferValues, method);                
            lastBufferValues = Arrays.copyOfRange(mask, mask.length-numChannels, mask.length);
            wavFileOut.writeFrames(mask, framesRead);
        }
            
    }

    
    //private long[] getMask(long[] originalBuffer, int method) throws IOException, WavFileException, URISyntaxException {
    private double[] getMask(double[] originalBuffer, double[] lastMaskValues, int method) throws IOException, WavFileException, URISyntaxException, ClassNotFoundException {
        //long[] mask = new long[originalBuffer.length];
        double[] mask = new double[originalBuffer.length];
        switch (method) {
            case Masker.METHOD_SILENCE:
                Arrays.fill(mask, 0.0);
                break;
            case Masker.METHOD_BROWN_NOISE:
                for (int c=0; c<numChannels; c++){
                    double sum = 0.0;
                    double rms = 0.0;
                    double currentValue=lastMaskValues[c];
                    // Calculate the Root-Mean-Square (Energy) of the Signal
                    // in order to balance the brown noise accordingly
                    for (int i=0; i<originalBuffer.length/numChannels; i++){
                        // select values in current channel
                        double numInChannel=originalBuffer[i*numChannels+c];
                        sum += numInChannel * numInChannel; // sum up the squares
                    }
                    rms = Math.sqrt(sum / originalBuffer.length/numChannels);
                    double lowerLimit = 0.0-rms;
                    double upperLimit = rms;
                    //BrownNoiseGenerator brownNoiseGenerator = new BrownNoiseGenerator(lowerLimit,upperLimit);
                    // issue #269
                    if (lowerLimit>=upperLimit){
                        lowerLimit = upperLimit - 0.001;
                    }
                    BrownNoiseGenerator brownNoiseGenerator = new BrownNoiseGenerator(lowerLimit,upperLimit,(upperLimit - lowerLimit) / 20,0.02,currentValue);
                    for (int pos=c; pos<mask.length; pos+=numChannels){
                        mask[pos]=brownNoiseGenerator.getNext();
                    }
                }   break;
            case Masker.METHOD_BROWN_NOISE_COPIED:
                if (brownNoise==null){
                    brownNoise = initBrownNoiseWavFile();
                }   
                for (int c=0; c<numChannels; c++){
                    for (int pos=c; pos<mask.length; pos+=numChannels){
                        mask[pos] = brownNoise[(brownNoisePointer+pos)%brownNoise.length];
                    }
                }   
                brownNoisePointer+=mask.length;
                break;
            case Masker.METHOD_SAMPLE:
                // TO DO
                break;
            default:
                break;
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