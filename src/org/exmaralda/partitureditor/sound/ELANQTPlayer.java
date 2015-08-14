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
public class ELANQTPlayer extends AbstractPlayer implements ControllerListener {

    mpi.eudico.client.annotator.player.QTMediaPlayer wrappedPlayer;
    //Thread playThread;
    private double haltTime = 0;
    //private boolean halted = false;

            
    
    @Override
    public void setSoundFile(String pathToSoundFile) throws IOException {
        System.out.println("This is the ELANQTPlayer setting media file to " + pathToSoundFile);
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
                    Logger.getLogger(ELANQTPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }*/
            }
            wrappedPlayer = new mpi.eudico.client.annotator.player.QTMediaPlayer(mediaDescriptor);
            PeriodicUpdateController puc = new PeriodicUpdateController(10);
            wrappedPlayer.addController(puc);
            puc.addControllerListener(this);
            puc.start();
            
            // added 08-02-2010
            this.fireSoundfileSet();

            //wrappedPlayer = new mpi.eudico.client.annotator.player.NativeMediaPlayerWindowsDS(urlString);
        } catch (NoPlayerException ex) {
            throw new IOException(ex.getLocalizedMessage());
        }
    }

    @Override
    public double getTotalLength() {
        if (wrappedPlayer==null) return 0.0;
        return wrappedPlayer.getMediaDuration()/1000.0;
    }

    private void play(double startTime, double endTime){
        /*playThread = new Thread(new Runnable(){
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
        });        */
        
        long thisStartTime = (long)(startTime * 1000);
        long thisEndTime = (long)(endTime * 1000);
        wrappedPlayer.playInterval(thisStartTime, thisEndTime);        
        // added 11-05-2009 to avoid IllegalThreadStateException
        /*if (!playThread.isAlive()){
            playThread.start();
        }*/
        
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

    public Component getVisibleComponent(){
        return wrappedPlayer.getVisualComponent();
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


    public static void main(String[] args){
        ELANQTPlayer player = new ELANQTPlayer();
        try {
            player.setSoundFile("/Users/thomasschmidt/Desktop/EnglishTranslator/EnglishTranslator.wav");
            player.play(20.0, 25.0);
        } catch (IOException ex) {
            Logger.getLogger(ELANQTPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void controllerUpdate(ControllerEvent ce) {
        if (ce instanceof mpi.eudico.client.mediacontrol.TimeEvent){
            //mpi.eudico.client.mediacontrol.TimeEvent timeEvent = (mpi.eudico.client.mediacontrol.TimeEvent)ce;            
            //System.out.println("Current JDS position: " + getCurrentPosition());
            firePosition();
        } else if (ce instanceof mpi.eudico.client.mediacontrol.StopEvent){
            this.firePlaybackStopped();
        }
    }

    
    

}
