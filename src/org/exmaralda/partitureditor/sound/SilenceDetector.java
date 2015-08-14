/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.masker.WavFile;
import org.exmaralda.masker.WavFileException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;

/**
 *
 * @author Schmidt
 */
public class SilenceDetector {
    
    WavFile wavFileIn;
    int numChannels;
    long numFrames;
    
    long totalFramesRead = 0;
    
    int windowSize = 1024;
    
    ArrayList<SilenceDetectorListener> listeners;

    /* initialises the silence detector with a wave file 
     * memorizes the number of channels and the number of frames
     */
    public SilenceDetector(File wavFile) throws IOException, WavFileException{
        wavFileIn = WavFile.openWavFile(wavFile);
        numChannels = wavFileIn.getNumChannels(); 
        numFrames = wavFileIn.getNumFrames();        
    }
    
    public void addSilenceDetectorListener(SilenceDetectorListener listener){
        if (listeners==null){
            listeners = new ArrayList<SilenceDetectorListener>();
        }
        listeners.add(listener);        
    }
    
    /* returns a list of pairs [start,end] where silences are detected
     * a silence is defined as a stretech of the sound file whose average 
     * energy is below the threshhold value and which is at least of minLength
     * typical values are threshhold=0.005 and minLength=0.3
     */
    public ArrayList<double[]> detectSilences(double threshhold, double minLength) throws IOException, WavFileException{        
        ArrayList<long[]> resultFramePairs = new ArrayList<long[]>();
        
        int framesRead;
        // Create a buffer of windowSize frames
        double[] buffer = new double[windowSize * numChannels];
        boolean inSilence = false;
        long lastSilenceStart = -1;
        
        // 1. in a first step, make a list of pairs [startFrame,endFrame]
        // where windowSize samples have a total volume below the threshhold
        do {
             // Read frames into buffer                    
             framesRead = wavFileIn.readFrames(buffer, windowSize);
             double volume = getVolume(buffer);
             if (!inSilence && (volume<threshhold)){
                 // silence starts
                 lastSilenceStart = totalFramesRead;                 
             } else if (inSilence && (volume>=threshhold)){
                 // silence ends
                 // add the pair lastSilence / totalFramesRead to the result
                 long[] pair = {lastSilenceStart, totalFramesRead + framesRead};
                 resultFramePairs.add(pair);
             }             
             totalFramesRead+=framesRead;
             inSilence = (volume<threshhold);
             //System.out.println(totalFramesRead + " / " + volume);
        } while (wavFileIn.getFramesRemaining() > 0);
        
        // 2. in a second step, retain only those pairs
        // whose length is at least of the minum length
        // calculate the times in seconds corresponding
        // to the [startFrame,endFrame] pair
        // and put those into the result list
        ArrayList<double[]> result = new ArrayList<double[]>();
        for (long[] pair : resultFramePairs){
            double duration = pair[1] - pair[0];
            double durationInSeconds = duration / (double)(wavFileIn.getSampleRate() * wavFileIn.getNumChannels());
            if (durationInSeconds >= minLength){
                double[] pairInSeconds = {
                    //(double)pair[0]/(double)(wavFileIn.getSampleRate() * wavFileIn.getNumChannels()),
                    //(double)pair[1]/(double)(wavFileIn.getSampleRate() * wavFileIn.getNumChannels()),
                    (double)pair[0]/(double)(wavFileIn.getSampleRate()),
                    (double)pair[1]/(double)(wavFileIn.getSampleRate()),
                };
                //System.out.println(pairInSeconds[0] + " / " + pairInSeconds[1]);
                result.add(pairInSeconds);
            }
        }
        return result;
    }

    
    private double getVolume(double[] buffer) {
        // add up all the absolute frame values
        // and divide them by the size of the buffer
        // to obtain the volume of this buffer
        double total = 0;
        for (double d : buffer){
            total+=Math.abs(d);
        }
        return (total / buffer.length);
    }
    
    
    /* go through the list of [startTime, endTime] pairs
     * (typically the result of detectSilences())
     * and check the time that lies between endTime(n) and startTime(n+1)
     * if that time is less than minLengthBetween, merge the two pairs
     * return the list of pairs thus made
     * a typical value is minLengthBetween=0.3
     */
    public ArrayList<double[]> clean(ArrayList<double[]> in, double minLengthBetween){
        ArrayList<double[]> result = new ArrayList<double[]>();
        for (int i=0; i<in.size()-1; i++){
            double[] d1 = in.get(i);
            double[] d2 = in.get(i+1);
            if (d2[0]-d1[1]<minLengthBetween){
                double[] join = {d1[0], d1[1]};
                result.add(join);
                i++;
            } else {
                result.add(d1);
            }
        }
        return result;
    }
    
    /* take a bit away from each pair of [startTime, endTime] silence candidates
     * the rationale is that it is better to have a pause that is too short
     * than a pause that contains non-silence from the preceding or following interval
     * a typical value is shrinkAmount=0.05
     */
    public ArrayList<double[]> shrink(ArrayList<double[]> in, double shrinkAmount){
        ArrayList<double[]> result = new ArrayList<double[]>();
        for (double[] d : in){
            double[] shrunkD = {d[0] + shrinkAmount, d[1] - shrinkAmount};
            result.add(shrunkD);
        }
        return result;
    }

    
    /* finds the n lists with the best scores
     * and removes the rest from the input
     */
    public void findNBest(int n, ArrayList<ArrayList<double[]>> all, final double[] parameters){
        all.sort(new java.util.Comparator<ArrayList<double[]>>(){

            @Override
            public int compare(ArrayList<double[]> o1, ArrayList<double[]> o2) {
                double score1 = getScore(o1, parameters);
                double score2 = getScore(o2, parameters);
                return Double.compare(score1, score2);
            }
            
        }); 
        
        for (int i=n; i<all.size(); i++){
            all.remove(i);
        }
    }
    
    public ArrayList<ArrayList<double[]>> getNBestList(
                int n,
                double threshHoldStart,
                double threshHoldEnd,
                double threshHoldStep,
                double minLengthStart,
                double minLengthEnd,
                double minLengthStep,
                double zero,
                double muchTooShort,
                double okay,
                double tooLong,
                double muchTooLong,
                double audioLength
                
            ) throws IOException, WavFileException{
            double[] parameters = {zero,muchTooShort,okay,tooLong,muchTooLong, audioLength};
            
            ArrayList<ArrayList<double[]>> all = new ArrayList<ArrayList<double[]>>();
            for (double threshhold = threshHoldStart; threshhold < threshHoldEnd; threshhold+=threshHoldStep){
                for (double minLength=minLengthStart; minLength<=minLengthEnd; minLength+=minLengthStep){
                    //ArrayList<double[]> silenceIntervals = sd.detectSilences(THRESHHOLD, MIN_LENGTH);
                    ArrayList<double[]> silenceIntervals = detectSilences(threshhold, minLength);
                    ArrayList<double[]> cleanedSilenceIntervals = clean(silenceIntervals, 0.3);
                    ArrayList<double[]> shrunkSilenceIntervals = shrink(cleanedSilenceIntervals, 0.05);
                    
                    all.add(shrunkSilenceIntervals);
                    double score = getScore(shrunkSilenceIntervals, parameters);
                    //System.out.println(k + "\t" + threshhold + "\t" + minLength + "\t" + score + "\t" + shrunkSilenceIntervals.size());
                }
            }
            findNBest(n, all, parameters);
            return all;
    }
    
    public ArrayList<double[]> findBest(ArrayList<ArrayList<double[]>> all, double[] parameters){
        double[] scores = new double[all.size()];
        int i=0;
        for (ArrayList<double[]> thisOne : all){
            scores[i] = getScore(thisOne, parameters);
            i++;
        }
        int maxIndex = 0;
        double max = Double.NEGATIVE_INFINITY;
        i = 0;
        for (double s : scores){
            //System.out.println("Current score: " + scores[i]);
            //System.out.println("Max index: " + maxIndex);
            //System.out.println("Max value: " + max);
            if (scores[i]>max){
                maxIndex = i;
                max = scores[i];
            }
            i++;
        }
        return all.get(maxIndex);
    }
    

    /*
     * this evaluates a list of silence candidates, i.e. pairs [startTime, endTime],
     * using the vector passed in parameters, where
     * parameters[0]=0.0 (is not used)
     * parameters[1] is the maximum length of an inter-silence-interval considered much too short, typical value is 1.0 (seconds)
     * parameters[2] is the minimum length of an inter-silence-interval considered okay, typical value is 3.0 (seconds)
     * parameters[3] is the maximum length of an inter-silence-interval considered okay, typical value is 7.0 (seconds)
     * parameters[4] is the maximum length of an inter-silence-interval considered too long, typical value is 12.0 (seconds)
     * parameters[5] is the total length of the audio file
     * so we have x intervals which will be penalized or rewarded as follows
     * [zero/muchTooShort] : the difference to the minimum okay length to the cube will be subtracted from the score
     * [muchTooShort/okay] : the difference to the minimum okay length squared will be subtracted from the score
     * [okay/tooLong] : one will be added to the score
     * [tooLong/muchTooLong] : the difference to the maximum okay length (=tooLong) squared will be subtracted from the score
     * [muchTooLong/...] : the difference to the maximum okay length (=tooLong) to the cube will be subtracted from the score
     * ,1.0,3.0,7.0,12.0, player.getTotalLength()}
     */
    public double getScore(ArrayList<double[]> thisOne, double[] parameters) {
        double zero = parameters[0];
        double muchTooShort = parameters[1];
        double okay = parameters[2];
        double tooLong = parameters[3];
        double muchTooLong = parameters[4];
        double audioLength = parameters[5];
        ArrayList<double[]> complement = getComplement(thisOne, audioLength);
        double totalScore = 0;
        for (double[] d : complement){
            double length = d[1] - d[0];
            if (length<muchTooShort) totalScore-=Math.abs((okay-length)*(okay-length)*(okay-length)); // - delta^3
            else if (muchTooShort<=length && length<okay) totalScore-=(okay-length)*(okay-length); // - delta^2
            else if (okay<=length && length<tooLong) totalScore+=1; // +1
            else if (tooLong<=length && length<=muchTooLong) totalScore-=(length-tooLong)*(length-tooLong); // - delta^2
            else if (muchTooLong<=length) totalScore-=(length-tooLong)*(length-tooLong)*(length-tooLong); // - delta^3
        }
        return totalScore;
    }

    /* returns the list of intervals that complements
     * the list of intervals passed as thisOne, i.e.
     * both lists together cover [0, audioLength]
     */
    private ArrayList<double[]> getComplement(ArrayList<double[]> thisOne, double audioLength) {
        ArrayList<double[]> result = new ArrayList<double[]>();
        double lastStart = 0.0;
        for (double[] d : thisOne){
            double[] complementPair = {lastStart, d[0]};
            result.add(complementPair);
            lastStart = d[1];
        }
        double[] complementPair = {lastStart, audioLength};
        result.add(complementPair);
        return result;
    }
    
    public static void main (String[] args){
        try {
            
            String audio = "Y:\\media\\audio\\SW\\SW--_E_00016_SE_01_A_01_DF_01.WAV";
            BASAudioPlayer player = new BASAudioPlayer();
            player.setSoundFile(audio);
            double audioLength = player.getTotalLength();

            double[] parameters = {0.0,1.0,3.0,7.0,10.0, audioLength};
            
            BasicTranscription bt = new BasicTranscription();
            ArrayList<ArrayList<double[]>> all = new ArrayList<ArrayList<double[]>>();
            for (double threshhold = 0.01; threshhold < 0.02; threshhold+=0.002){
                //double minLength=0.1;
                for (double minLength=0.1; minLength<=0.2; minLength+=0.1){
                    SilenceDetector sd = new SilenceDetector(new File(audio));
                    ArrayList<double[]> silenceIntervals = sd.detectSilences(threshhold, minLength);
                    ArrayList<double[]> cleanedSilenceIntervals = sd.clean(silenceIntervals, 0.3);
                    ArrayList<double[]> shrunkSilenceIntervals = sd.clean(cleanedSilenceIntervals, 0.05);
                    all.add(shrunkSilenceIntervals);
                    double score = sd.getScore(shrunkSilenceIntervals, parameters);
                    System.out.println(threshhold + "\t" + minLength + "\t" + score + "\t" + shrunkSilenceIntervals.size());
                }
            }
            
            
            SilenceDetector sd = new SilenceDetector(new File(audio));
            ArrayList<double[]> bestIntervals = sd.findBest(all, parameters);
            double score = sd.getScore(bestIntervals, parameters);
            String id = bt.getBody().makeTierFromTimes(bestIntervals, 0.00001);
            Tier tier = bt.getBody().getTierWithID(id);
            //tier.getUDTierInformation().setAttribute("threshhold", Double.toString(threshhold));
            //tier.getUDTierInformation().setAttribute("minimum length", Double.toString(minLength));
            //tier.setDisplayName(Double.toString(threshhold) + " / " + Double.toString(minLength));
            
            bt.writeXMLToFile("C:\\Users\\Schmidt\\Dropbox\\IDS\\AGD\\Peters\\BEISPIEL\\NEW_TEST.exb", "none");
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(SilenceDetector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WavFileException ex) {
            Logger.getLogger(SilenceDetector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(SilenceDetector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
