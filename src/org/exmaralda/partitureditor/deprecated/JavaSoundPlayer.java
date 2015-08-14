/*
 * JavaSoundPlayer.java
 *
 * Created on 2. August 2004, 17:38
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.sound.*;
import javax.sound.sampled.*;
import java.io.*;

/**
 *
 * @author  thomas
 */
public class JavaSoundPlayer extends AbstractPlayer implements LineListener {
    
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat = null;
    private SourceDataLine line = null;
    private File soundFile = null;
    Thread playThread;
    Thread timeThread;
    
    long playbackStartTime = 0;    
    private double haltTime = 0;
    boolean isResumed = false;
    boolean isHalted = false;
    
    int UPDATE_INTERVAL = 100;
    int bufferSize = 128000;    
    int nBytesRead;
    
    /** Creates a new instance of JavaSoundPlayer */
    public JavaSoundPlayer() {
    }
    
    
    public double getCurrentPosition() {
        long currentTime = System.currentTimeMillis();
        if (isResumed){
            return haltTime + (currentTime - playbackStartTime)/1000.0;
        } else {
            return startTime + (currentTime - playbackStartTime)/1000.0;
        }
    }
    
    public double getTotalLength() {
        long frameLength = audioInputStream.getFrameLength();
        float frameRate = audioFormat.getFrameRate();
        double totalLength = frameLength / frameRate;
        return totalLength;        
    }
    
    public void haltPlayback() {
        haltTime = getCurrentPosition();
        stop();
        firePlaybackHalted();        
        isHalted = true;
    }
    
    public void resumePlayback() {
        isHalted = false;
        isResumed = true;
        if (line.isActive()){            
            line.stop();
        }
        line.start();
        try{
            audioInputStream = AudioSystem.getAudioInputStream(soundFile); 
            long bytesBeforeStart = Math.round(haltTime * audioFormat.getFrameRate() * audioFormat.getFrameSize());
            audioInputStream.skip(bytesBeforeStart);
            firePlaybackStarted();
        } catch (Exception e){
            // do nothing
            e.printStackTrace();
            return;
        }        
        playThread = new Thread(new Runnable(){
            public void run(){
                try{
                    int count = 0;
                    nBytesRead = 0;
                    long totalBytesToRead = Math.round((endTime - haltTime) * audioFormat.getFrameRate() * audioFormat.getFrameSize());
                    byte[] abData = new byte[bufferSize];
                    while (nBytesRead!=-1 && totalBytesToRead>0) {
                        if (playThread.isInterrupted()) break;
                        nBytesRead = audioInputStream.read(abData, 0, Math.min(abData.length, (int)totalBytesToRead));
                        totalBytesToRead-=nBytesRead;                                                
                        if ((nBytesRead >=0) && (!playThread.isInterrupted())){
                            int nBytesWritten = line.write(abData, 0, nBytesRead);
                        }
                    }
                    timeThread.interrupt();
                    line.stop();
                    audioInputStream.close();                    
                    if (!playThread.isInterrupted()) {
                        firePlaybackStopped();
                    }
                } catch (Exception e){
                    // do nothing
                    e.printStackTrace();
                }
            }
        });
        timeThread = new JavaSoundTimeThread(this);
        playThread.start();
        timeThread.start();
    }
    
    
    public void setSoundFile(String pathToSoundFile) throws java.io.IOException {
        soundFile = new File(pathToSoundFile);
        try {
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);    
        } catch (UnsupportedAudioFileException uafe){
            IOException wrappedException = new IOException("Unsupported audio file:" + uafe.getLocalizedMessage());
            throw wrappedException;
        }
        audioFormat = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(audioFormat); 
        } catch (LineUnavailableException lue){
            IOException wrappedException = new IOException("Line unavailable:" + lue.getLocalizedMessage());
            throw wrappedException;
        }
        line.addLineListener(this);
        bufferSize = line.getBufferSize();
        //bufferSize = audioFormat.getFrameSize();
        line.addLineListener(this);
        setStartTime(0);
        setEndTime(getTotalLength());
        audioInputStream.close();
    }
    
    
    public void startPlayback() {
        isResumed = false;
        isHalted = false;        
        if (line.isActive()){
            line.drain();
            line.stop();
        }
        line.start();
        try{
            audioInputStream = AudioSystem.getAudioInputStream(soundFile); 
            long bytesBeforeStart = Math.round(startTime * audioFormat.getFrameRate() * audioFormat.getFrameSize());
            audioInputStream.skip(bytesBeforeStart);
            firePlaybackStarted();
        } catch (Exception e){
            // do nothing
            e.printStackTrace();
            return;
        }        
        playThread = new Thread(new Runnable(){
            public void run(){
                try{
                    int count = 0;
                    nBytesRead = 0;
                    long totalBytesToRead = Math.round((endTime - startTime) * audioFormat.getFrameRate() * audioFormat.getFrameSize());
                    byte[] abData = new byte[bufferSize];
                    while (nBytesRead!=-1 && totalBytesToRead>0) {
                        if (playThread.isInterrupted()) break;
                        nBytesRead = audioInputStream.read(abData, 0, Math.min(abData.length, (int)totalBytesToRead));
                        totalBytesToRead-=nBytesRead;                                                
                        if ((nBytesRead >=0) && (!playThread.isInterrupted())){
                            int nBytesWritten = line.write(abData, 0, nBytesRead);
                        }
                    }
                    timeThread.interrupt();
                    line.stop();
                    audioInputStream.close();                    
                    if (!playThread.isInterrupted()) {
                        firePlaybackStopped();
                    }
                } catch (Exception e){
                    // do nothing
                    e.printStackTrace();
                }
            }            
        });
        timeThread = new JavaSoundTimeThread(this);
        playThread.start();
        timeThread.start();
    }
    
    public void stopPlayback() {
        if (!isHalted) {
            stop();
        }
        firePlaybackStopped();   
        isResumed = false;        
        isHalted = false;
    }
    
    public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.START){
        }
        if (event.getType() == LineEvent.Type.STOP){
        }        
    }
    
    private void stop(){
        if (line.isActive()){
            line.drain();
            line.stop();
        }
        timeThread.interrupt();
        while (!timeThread.isInterrupted());
        playThread.interrupt();
        try{
            audioInputStream.close();
        } catch (Exception e){
            // do nothing
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        try {
            JavaSoundPlayer pp = new JavaSoundPlayer();
            pp.setSoundFile("T:\\TP-Z2\\GAT\\Demokorpus\\HART_ABER_FAIR\\HART_ABER_FAIR_02_APRIL_2008.wav");
            pp.setStartTime(4.95);
            pp.setEndTime(8.63);
            pp.startPlayback();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void decreaseCurrentPosition(double time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void increaseCurrentPosition(double time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}

