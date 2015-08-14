/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.sound;

import java.awt.Component;
import java.io.IOException;
import mpi.eudico.server.corpora.clomimpl.abstr.MediaDescriptor;

/**
 *
 * @author thomas
 */
@Deprecated 
public class ELANDSPlayer extends AbstractPlayer implements PlayableListener {

    //27-05-2015 goodbye ELAN DS Player!
    //mpi.eudico.client.annotator.player.NativeMediaPlayerWindowsDS wrappedPlayer; 
    mpi.eudico.client.annotator.player.ElanMediaPlayer wrappedPlayer;
    Thread playThread;
    private double haltTime = 0;
    private boolean halted = false;

    @Override
    public void setSoundFile(String pathToSoundFile) throws IOException {
        System.out.println("This is the ELANDSPlayer setting media file to " + pathToSoundFile);
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
        //27-05-2015 goodbye ELAN DS Player!
        //try {
            // added 04-06-2009
            if (wrappedPlayer!=null){
                wrappedPlayer.cleanUpOnClose();
                try {
                    //27-05-2015 goodbye ELAN DS Player!
                    //wrappedPlayer.finalize();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
            //27-05-2015 goodbye ELAN DS Player!
            //wrappedPlayer = new mpi.eudico.client.annotator.player.NativeMediaPlayerWindowsDS(mediaDescriptor);
            //wrappedPlayer = new mpi.eudico.client.annotator.player.NativeMediaPlayerWindowsDS(urlString);
            // added 08-02-2010
            this.fireSoundfileSet();
            
            addPlayableListener(this);

            // experiment!
            //wrappedPlayer.setRate(0.5f);

            //27-05-2015 goodbye ELAN DS Player!
            //} catch (NoPlayerException ex) {
            //throw new IOException(ex);
            //}
    }

    @Override
    public double getTotalLength() {
        if (wrappedPlayer==null) return 0.0;
        return wrappedPlayer.getMediaDuration()/1000.0;
    }

    private void play(double startTime, double endTime){
        playThread = new Thread(new Runnable(){
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
        
        long thisStartTime = (long)(startTime * 1000);
        long thisEndTime = (long)(endTime * 1000);

        wrappedPlayer.playInterval(thisStartTime, thisEndTime);
        // added 11-05-2009 to avoid IllegalThreadStateException
        if (!playThread.isAlive()){
            playThread.start();
        }
        
    }

    @Override
    public void startPlayback() {
        play(startTime, endTime);
        firePlaybackStarted();
        halted = false;
    }

    @Override
    public void haltPlayback() {
        haltTime = getCurrentPosition();
        halted = true;
        wrappedPlayer.stop();
        wrappedPlayer.setMediaTime((long)(haltTime*1000));
        firePlaybackHalted();        
    }
    
    @Override
    public void resumePlayback() {
        play(haltTime, endTime);
        firePlaybackResumed();        
        halted = false;
    }


    @Override
    public void stopPlayback() {
        if ((wrappedPlayer==null) || (!wrappedPlayer.isPlaying())) return;
        halted = false;
        firePlaybackStopped();
        playThread.interrupt();
        playThread=null;
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
        //27-05-2015 goodbye ELAN DS Player!
        //return wrappedPlayer.getCurrentFrameImage();
        return null;
    }

    @Override
    public void processPlayableEvent(PlayableEvent e) {
        if (e.getType()==PlayableEvent.POSITION_UPDATE){
            if (getCurrentPosition()>endTime){
                stopPlayback();
            }
        }
    }




    
    

}
