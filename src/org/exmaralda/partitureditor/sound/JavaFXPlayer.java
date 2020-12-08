/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.sound;

import java.awt.Component;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mpi.eudico.client.annotator.player.NoPlayerException;
import mpi.eudico.client.mediacontrol.ControllerEvent;
import mpi.eudico.client.mediacontrol.ControllerListener;
import mpi.eudico.client.mediacontrol.PeriodicUpdateController;
import mpi.eudico.server.corpora.clomimpl.abstr.MediaDescriptor;

/**
 *
 * @author thomas
 */
public class JavaFXPlayer extends AbstractPlayer implements ControllerListener {

    mpi.eudico.client.annotator.player.JFXMediaPlayer wrappedPlayer;
    //Thread playThread;
    private double haltTime = 0;
    //private boolean halted = false;

    @Override
    public void setSoundFile(String pathToSoundFile) throws IOException {
        String urlString = pathToSoundFile;
        System.out.println("This is the JavaFXPlayer setting media file to " + pathToSoundFile);
        if ((pathToSoundFile==null) && (wrappedPlayer!=null)){
            wrappedPlayer.stop();
        }
        if ((pathToSoundFile==null)){
            return;
        }
        

        if (!(pathToSoundFile.startsWith("http://") || (pathToSoundFile.startsWith("https://")))){
            //urlString = "file:///" + URLEncoder.encode(pathToSoundFile, StandardCharsets.ISO_8859_1.toString());
            // only this version seems to do it -- this is for issue #
            urlString = "file:///" + pathToSoundFile.replaceAll(" ", "%20");
            soundFilePath = pathToSoundFile;
        }
        
        System.out.println("******* URLString=" + urlString);

        MediaDescriptor mediaDescriptor =
                mpi.eudico.client.annotator.linkedmedia.MediaDescriptorUtil.createMediaDescriptor(urlString);
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
            wrappedPlayer = new mpi.eudico.client.annotator.player.JFXMediaPlayer(mediaDescriptor);
            PeriodicUpdateController puc = new PeriodicUpdateController(1);
            wrappedPlayer.addController(puc);
            puc.addControllerListener(this);
            //puc.start();
            // new 16-02-2020
            // give the player a second to initialise
            // changed 30-06-2020
            // no: rather give it sixty seconds
            // no, don't. That's not the point
            try {
                Thread.sleep(1000); 
            } catch (InterruptedException ex) {
                Logger.getLogger(JavaFXPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
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




    
    


