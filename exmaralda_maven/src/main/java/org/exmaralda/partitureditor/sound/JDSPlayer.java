/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.sound;

import java.awt.Component;
import java.io.IOException;
import mpi.eudico.client.annotator.player.NoPlayerException;
import mpi.eudico.client.mediacontrol.ControllerEvent;
import mpi.eudico.client.mediacontrol.ControllerListener;
import mpi.eudico.client.mediacontrol.PeriodicUpdateController;
import mpi.eudico.server.corpora.clomimpl.abstr.MediaDescriptor;

/**
 *
 * @author thomas
 */
public class JDSPlayer extends AbstractPlayer implements ControllerListener {

    mpi.eudico.client.annotator.player.JDSMediaPlayer wrappedPlayer;
    //Thread playThread;
    private double haltTime = 0;
    //private boolean halted = false;

    @Override
    public void setSoundFile(String pathToSoundFile) throws IOException {
        System.out.println("This is the JDSPlayer setting media file to " + pathToSoundFile);
        String urlString = pathToSoundFile;
        if ((pathToSoundFile==null) && (wrappedPlayer!=null)){
            wrappedPlayer.stop();
        }
        if ((pathToSoundFile==null)){
            return;
        }
                

        if (!pathToSoundFile.startsWith("http://")){
            urlString = "file:///" + pathToSoundFile;
            soundFilePath = pathToSoundFile;
        }

        MediaDescriptor mediaDescriptor =
                // changed 08-07-2021, issue # 275
                //mpi.eudico.client.annotator.linkedmedia.MediaDescriptorUtil.createMediaDescriptor(urlString);
                new MediaDescriptor(urlString, determineMimeType(urlString));
        try {
            // added 04-06-2009
            if (wrappedPlayer!=null){
                wrappedPlayer.cleanUpOnClose();
                /*try {
                    wrappedPlayer.finalize();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }*/
            }
            wrappedPlayer = new mpi.eudico.client.annotator.player.JDSMediaPlayer(mediaDescriptor);
            PeriodicUpdateController puc = new PeriodicUpdateController(1);
            wrappedPlayer.addController(puc);
            puc.addControllerListener(this);
            //puc.start();
            
            
            
            //wrappedPlayer = new mpi.eudico.client.annotator.player.NativeMediaPlayerWindowsDS(urlString);
            // added 08-02-2010
            fireSoundfileSet();

            // experiment!
            //wrappedPlayer.setRate(0.8f);

        } catch (NoPlayerException ex) {
            throw new IOException(ex);
        }
    }
    
    public void setPlaybackRate(double rate){
        wrappedPlayer.setRate((float)rate);
    }
        

    @Override
    public double getTotalLength() {
        if (wrappedPlayer==null) return 0.0;
        return wrappedPlayer.getMediaDuration()/1000.0;
    }
    
    public int getSourceWidth(){
        if (wrappedPlayer==null) return 0;
        return wrappedPlayer.getSourceWidth();
    }
    
    public int getSourceHeight(){
        if (wrappedPlayer==null) return 0;
        return wrappedPlayer.getSourceHeight();        
    }

    private void play(double startTime, double endTime){
        long thisStartTime = (long)(startTime * 1000);
        long thisEndTime = (long)(endTime * 1000);
        wrappedPlayer.playInterval(thisStartTime, thisEndTime);        
    }

    @Override
    public void startPlayback() {
        play(startTime, endTime);
        //firePlaybackStarted();
        //halted = false;
    }

    @Override
    public void haltPlayback() {
        haltTime = getCurrentPosition();
        //halted = true;
        wrappedPlayer.stop();
        wrappedPlayer.setMediaTime((long)(haltTime*1000));
        firePlaybackHalted();        
    }
    
    @Override
    public void resumePlayback() {
        play(haltTime, endTime);
        firePlaybackResumed();        
        //halted = false;
    }
    

    @Override
    public void stopPlayback() {
        if ((wrappedPlayer==null) || (!wrappedPlayer.isPlaying())) return;
        //halted = false;
        //firePlaybackStopped();
        //playThread.interrupt();
        //playThread=null;
        wrappedPlayer.stop();
    }

    @Override
    public double getCurrentPosition() {
        return wrappedPlayer.getMediaTime()/1000.0;
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
        if (wrappedPlayer!=null){
            return wrappedPlayer.getVisualComponent();
        }
        return null;
    }
    


    // added 26-05-2009
    public void updateVideo(double time){
        if ((wrappedPlayer!=null) && (wrappedPlayer.getVisualComponent()!=null)){
            wrappedPlayer.setMediaTime((long)(time*1000.0));
        }
    }
      

    // added 26-05-2009
    public java.awt.Image grabFrame(){
        return wrappedPlayer.getCurrentFrameImage();
    }

    
    @Override
    public void controllerUpdate(ControllerEvent ce) {        
        if (wrappedPlayer.isPlaying() && (ce instanceof mpi.eudico.client.mediacontrol.TimeEvent)){
            //mpi.eudico.client.mediacontrol.TimeEvent timeEvent = (mpi.eudico.client.mediacontrol.TimeEvent)ce;            
            //System.out.println("Current JDS position: " + getCurrentPosition());
            firePosition();
        } else if (ce instanceof mpi.eudico.client.mediacontrol.StopEvent){
            //System.out.println("Controller Update: Stop event received!");
            firePlaybackStopped();
        } else if (ce instanceof mpi.eudico.client.mediacontrol.StartEvent){    
            //System.out.println("Controller Update: Start event received!");
            firePlaybackStarted();     
        } /*else {
            // added 02-06-2015: additional check if end time has been reached
            
            if (getCurrentPosition()>=(endTime - 0.01)){
                System.out.println("Stopped playback because we got close!");
                stopPlayback();
                firePlaybackStopped();
            }
            
        }*/
    }

}




    
    


