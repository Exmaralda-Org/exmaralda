/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.sound;

import ipsk.audio.AudioSourceException;
import ipsk.audio.FileAudioSource;
import ipsk.audio.player.PlayerException;
import ipsk.audio.player.event.PlayerCloseEvent;
import ipsk.audio.player.event.PlayerErrorEvent;
import ipsk.audio.player.event.PlayerEvent;
import ipsk.audio.player.event.PlayerStopEvent; 
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author thomas
 */
public class BASAudioPlayer extends AbstractPlayer implements ipsk.audio.player.PlayerListener { 

    ipsk.audio.player.Player wrappedPlayer; 
    private double haltTime = 0;
    private boolean halted = false;
    Thread playThread; 

    // new 19-01-2016
    public BASAudioPlayer() {
        wrappedPlayer = new ipsk.audio.player.Player();
    }
    
    // don't really know how to do this...
    public void muteChannels(boolean muteRight, boolean muteLeft) throws PlayerException, AudioSourceException{
        /*if (wrappedPlayer.getAudioSource().getAudioInputStream().getFormat().getChannels()!=2) return;
        Mixer newPlaybackMixer = AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]);
        newPlaybackMixer.getLine(Port.Info.LINE_IN).
        wrappedPlayer.setMixer(newPlaybackMixer);*/
    }
    
    
    @Override
    public void setSoundFile(String pathToSoundFile) throws IOException {
        try {
            System.out.println("This is the BASAudioPlayer setting media file to " + pathToSoundFile);
            if ((pathToSoundFile==null)){
                return;
            }
            if (!(pathToSoundFile.toUpperCase().endsWith(".WAV"))){
                String message = "BASAudioPlayer can only be used with *.wav files.";
                throw new IOException(message);
            }
            //if ((pathToSoundFile==null) && (wrappedPlayer!=null)){
            if (wrappedPlayer!=null){
                wrappedPlayer.stop();
            }
            
            // 25-03-2023 completely new for #321, getting rid of all the URI/URL stuff
            System.out.println("BASPlayer : "  + pathToSoundFile);
            soundFilePath = pathToSoundFile;
            
            FileAudioSource fileAudioSource = new FileAudioSource(new File(pathToSoundFile));
            wrappedPlayer.setAudioSource(fileAudioSource);
            wrappedPlayer.addPlayerListener(this);
            wrappedPlayer.open();
            fireSoundfileSet();

        } catch (PlayerException ex) {
            Logger.getLogger(BASAudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }
    }

    @Override
    public double getTotalLength() {
        if (wrappedPlayer==null) return 0.0;
        double seconds = wrappedPlayer.getFrameLength() / wrappedPlayer.getAudioFormat().getFrameRate();        
        return seconds;
    }

    private void play(double startTime, double endTime) throws PlayerException{
        long startFrame = Math.round(wrappedPlayer.getAudioFormat().getFrameRate() * startTime);
        long endFrame = Math.round(wrappedPlayer.getAudioFormat().getFrameRate() * endTime);
        wrappedPlayer.setStartFramePosition(startFrame);
        wrappedPlayer.setStopFramePosition(endFrame);
        wrappedPlayer.play();
    }

    @Override
    public void startPlayback() {
        playThread = new Thread(new Runnable(){
            @Override
            public void run(){
                while (playThread!=null){
                    if (playThread.isInterrupted()) break;
                    if (!wrappedPlayer.isPlaying()) {
                        if (!halted){
                            // i.e. this is a real stop caused by stop button
                            // or by end of interval
                            firePlaybackStopped();
                        }
                        playThread.interrupt();
                    }
                    firePosition();
                    try{
                        Thread.sleep(1);
                    } catch (InterruptedException ie){
                        if (playThread!=null){
                            playThread.interrupt();
                        }
                    }
                }
            }
        });
        try {
            play(startTime, endTime);
            if (!playThread.isAlive()){
                playThread.start();
            }
            firePlaybackStarted();
            halted = false;
        } catch (PlayerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void haltPlayback() {
        haltTime = getCurrentPosition();
        halted = true;

        // new 15-12-2017 - trying to address a part of issue #112
        playThread.interrupt();
        playThread=null;

        wrappedPlayer.stop();
        long framePosition = Math.round(wrappedPlayer.getAudioFormat().getFrameRate() * haltTime);
        try {
            wrappedPlayer.setFramePosition(framePosition);
        } catch (PlayerException ex) {
            ex.printStackTrace();
        }
        firePlaybackHalted();        
    }
    
    @Override
    public void resumePlayback() {
        // changed 15-12-2017 - for issue #112 and the cursor problem
        /*try {
            play(haltTime, endTime);
        } catch (PlayerException ex) {
            ex.printStackTrace();
        }*/

        startTime = haltTime;
        startPlayback();

        firePlaybackResumed();        
        halted = false;
    }


    @Override
    public void stopPlayback() {
        if ((wrappedPlayer==null) || (!wrappedPlayer.isPlaying())) return;
        halted = false;
        playThread.interrupt();
        playThread=null;
        firePlaybackStopped();
        wrappedPlayer.stop();
    }

    @Override
    public double getCurrentPosition() {
        double time = wrappedPlayer.getFramePosition() / wrappedPlayer.getAudioFormat().getFrameRate();
        return time;
    }

    @Override
    public void decreaseCurrentPosition(double time) {
        // do nothing
    }



    @Override
    public void increaseCurrentPosition(double time) {
        // do nothing
    }

    @Override
    public Component getVisibleComponent(){        
        return null;
    }

    @Override
    public void update(PlayerEvent playerEvent) {
        firePosition();
        
        // added 19-01-2016 - not sure what to do with this yet
        if (playerEvent instanceof PlayerStopEvent) {
              // wrappedPlayer.close();
         } else if (playerEvent instanceof PlayerCloseEvent) {
             // player is ready again
         } else if (playerEvent instanceof PlayerErrorEvent) {
             // show error
         }        
    }
    
    public void destroy(){
        //playThread.interrupt();
        playThread=null;
    }







    
    

}
