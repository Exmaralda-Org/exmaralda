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
public class MMFPlayer extends AbstractPlayer implements ControllerListener, StrippedJMMFMediaPlayerListener {

    //mpi.eudico.client.annotator.player.JMMFMediaPlayer wrappedPlayer;
    StrippedJMMFMediaPlayer wrappedPlayer;
    //Thread playThread;
    private double haltTime = 0;
    //private boolean halted = false;

    @Override
    public void setSoundFile(String pathToSoundFile) throws IOException {
        System.out.println("This is the MMFPlayer setting media file to " + pathToSoundFile);
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
            //wrappedPlayer = new mpi.eudico.client.annotator.player.JMMFMediaPlayer(mediaDescriptor);
            wrappedPlayer = new StrippedJMMFMediaPlayer(mediaDescriptor);
            wrappedPlayer.addStrippedJMMFMediaPlayerListener(this);
            wrappedPlayer.getVisualComponent();
            
            // not having a layout manager will cause a null pointer exception...
            //wrappedPlayer.setLayoutManager(new ElanLayoutManager(null, null));
            // but initializing it with null values will also cause one...
            
            PeriodicUpdateController puc = new PeriodicUpdateController(10);
            wrappedPlayer.addController(puc);
            puc.addControllerListener(this);
            puc.start();
            
            //wrappedPlayer = new mpi.eudico.client.annotator.player.NativeMediaPlayerWindowsDS(urlString);
            // added 08-02-2010
            fireSoundfileSet();

            // experiment!
            //wrappedPlayer.setRate(0.5f);

        } catch (NoPlayerException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public double getTotalLength() {
        if (wrappedPlayer==null) return 0.0;
        return wrappedPlayer.getMediaDuration()/1000.0;
    }
    
    public int getSourceWidth(){
        return wrappedPlayer.getSourceWidth();
    }
    
    public int getSourceHeight(){
        return wrappedPlayer.getSourceHeight();        
    }
    

    private void play(double startTime, double endTime){
        long thisStartTime = (long)(startTime * 1000);
        long thisEndTime = (long)(endTime * 1000);
        
        System.out.println(System.currentTimeMillis() + ": Instructing MMF player to play from " + thisStartTime + " to " + thisEndTime);

        wrappedPlayer.playInterval(thisStartTime, thisEndTime);
        
    }

    @Override
    public void startPlayback() {
        play(startTime, endTime);
        firePlaybackStarted();
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
            //HEY HO BERND THE BUILDER!
            // Can I wait here until init has finished?
            return wrappedPlayer.getVisualComponent();
        }
        return null;
    }


    // added 26-05-2009
    public void updateVideo(double time){
        //System.out.println(System.currentTimeMillis() + ": update video to " + time);
        //HEY HO BERND THE BUILDER!        
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
        if (ce instanceof mpi.eudico.client.mediacontrol.TimeEvent){
            //mpi.eudico.client.mediacontrol.TimeEvent timeEvent = (mpi.eudico.client.mediacontrol.TimeEvent)ce;            
            //System.out.println("Current JDS position: " + getCurrentPosition());
            firePosition();
        } else if (ce instanceof mpi.eudico.client.mediacontrol.StopEvent){
            firePlaybackStopped();
        }
    }
    
    @Override
    public void addPlayableListener(PlayableListener l) {
        super.addPlayableListener(l);
    }

    @Override
    public void jmmfPlayerInitialised() {
        System.out.println("The (EXMARaLDA) MMF Player now knows that the (ELAN) MMF player has been initialised.");
        //setStartTime(0.1);
        //super.fireSoundfileSet();
    }
    




    
    

}
