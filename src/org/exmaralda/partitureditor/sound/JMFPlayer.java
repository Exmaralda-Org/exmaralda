/*
 * JMFPlayer.java
 *
 * Created on 28. Juli 2004, 17:48
 */

package org.exmaralda.partitureditor.sound;

import javax.media.*;
import javax.media.NoPlayerException;
import javax.media.CannotRealizeException;
import java.io.IOException;
import java.io.File;
import java.net.MalformedURLException;

/**
 *
 * @author  thomas
 */
public class JMFPlayer extends AbstractPlayer implements PlayableListener {
    
    Player player;
    Thread playThread;
    private double haltTime = 0;
    public int UPDATE_INTERVAL = 1;
    javax.media.control.FramePositioningControl fpc;
    javax.media.control.FrameGrabbingControl fgc;
    
    /** Creates a new instance of JMFPlayer */
    public JMFPlayer() {
        super();
        //added 10-April-2008 to watch over stop time trespassing
        addPlayableListener(this);
    }
        
    public java.awt.Component getPlayerControlComponent(){
        return player.getControlPanelComponent();
    }
    
    public java.awt.Component getPlayerVisibleComponent(){
        return player.getVisualComponent();
    }
    
    @Override
    public double getCurrentPosition() {
        long timeNanoSeconds = player.getMediaNanoseconds();        
        return (timeNanoSeconds / 1000000000.0);
    }
    
    @Override
    public double getTotalLength() {
        if (player==null) return 0.0;
        return (player.getDuration().getNanoseconds() / 1000000000.0);
    }
    
    @Override
    public void haltPlayback() {
        //System.out.println("Halt playback.");
        playThread.interrupt();
        haltTime = getCurrentPosition();
        player.stop();            
        firePlaybackHalted();        
    }
    
    @Override
    public void resumePlayback() {
        //System.out.println("Resume playback.");        
        play(haltTime, endTime);
        firePlaybackResumed();
    }
    
    
    @Override
    public void setSoundFile(String filename) throws java.io.IOException {
        System.out.println("This is the JMFPlayer setting media file to " + filename);
        soundFilePath = filename;
        if ((filename==null) && (player!=null)){
            player.stop();
            player.close();
        }
        if ((filename==null)){
            return;
        }
        fpc = null;
        fgc = null;
        try{
            player = Manager.createRealizedPlayer((new File(filename)).toURL());     
            fpc = (javax.media.control.FramePositioningControl)player.getControl("javax.media.control.FramePositioningControl");
            fgc = (javax.media.control.FrameGrabbingControl)player.getControl("javax.media.control.FrameGrabbingControl");
            //player = Manager.createRealizedPlayer(new MediaLocator(new File(filename).toURL()));
        } catch (MalformedURLException mue){
            IOException wrappedException = new IOException("Malformed URL: " + mue.getLocalizedMessage());
            //mue.printStackTrace();
            throw wrappedException;
        } catch (NoPlayerException npe){
            IOException wrappedException = new IOException("No player: " + npe.getLocalizedMessage());
            System.out.println("Exception wrapped by JMFPlayer.setSoundFile");
            //npe.printStackTrace();
            throw wrappedException;
        } catch (CannotRealizeException cre){
            IOException wrappedException = new IOException("Cannot realize player: " + cre.getLocalizedMessage());
            //cre.printStackTrace();
            throw wrappedException;
        }
        player.addControllerListener(new ControllerAdapter() {
                    @Override
                    public void endOfMedia(EndOfMediaEvent e) {
                        System.out.println("JMFPlayer: End of media");
                        firePlaybackStopped();
                    }
                    @Override
                    public void stopAtTime(StopAtTimeEvent e) {
                        System.out.println("JMFPlayer: Stop at time");
                        playThread.interrupt();
                        firePlaybackStopped();
                    }
            });                
        this.fireSoundfileSet();
    }
    
    
    @Override
    public void startPlayback() {
        play(startTime, endTime);
        firePlaybackStarted();
    }
    
    @Override
    public void stopPlayback() {
        System.out.println("JMF-Player: Stop playback.");
        if (playThread!=null){
            playThread.interrupt();
        }
        if (player!=null){
            player.stop();
            firePlaybackStopped();
        }
    }
    
    
    private void play(double startTime, double endTime){
        player.setMediaTime(new Time(Math.max(0, startTime-bufferTime)));      
        player.setStopTime(new Time(Math.min(this.getTotalLength(), endTime+bufferTime)));      
        playThread = new Thread(new Runnable(){
            public void run(){
                while (playThread != null) {
                    if (playThread.isInterrupted()) break;
                    firePosition();
                    try{
                        Thread.sleep(UPDATE_INTERVAL);
                    } catch (InterruptedException ie){
                        playThread.interrupt();
                    }
                }
            }
        });
        player.start();
        playThread.start();        
    }
        
    @Override
    public void decreaseCurrentPosition(double time) {
        haltTime = Math.max(startTime, haltTime-time);
        this.firePosition();
    }
    
    @Override
    public void increaseCurrentPosition(double time) {
        haltTime = Math.min(endTime, haltTime+time);
        this.firePosition();
    }
        
    public void updateVideo(double time){
        if (fpc==null) {
            //System.out.println("No control");
            return;
        }
        System.out.println("updateVideo");
        int frameNo = fpc.mapTimeToFrame(new javax.media.Time(time));
        fpc.seek(frameNo);        
    }
    
    public java.awt.Image grabFrame(){
        if (fgc==null){
            return null;
        }
        Buffer buf = fgc.grabFrame();
        javax.media.format.VideoFormat vf = (javax.media.format.VideoFormat)(buf.getFormat());
        javax.media.util.BufferToImage bti = new javax.media.util.BufferToImage(vf);
        java.awt.Image img = bti.createImage(buf);        
        System.out.println("Frame grabbed!");
        return img;
    }

    @Override
    public void processPlayableEvent(PlayableEvent e) {
        if (e.getType()!=PlayableEvent.POSITION_UPDATE) return;
        double p = e.getPosition();
        if (p>Math.min(getTotalLength(),endTime+bufferTime+0.1)) {
            System.out.println("SHOULD HAVE STOPPED: " + p + " > " + endTime+bufferTime);
            stopPlayback();
        }
    }
    
}
