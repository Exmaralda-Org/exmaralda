/*
 * AbstractPlayer.java
 *
 * Created on 2. August 2004, 17:39
 */

package org.exmaralda.partitureditor.sound;

import java.io.IOException;
import java.util.*;

/**
 *
 * @author  thomas
 */
public abstract class AbstractPlayer implements Playable {
    
    public double startTime = 0;
    public double endTime = 0;
    Vector listenerList;

    String soundFilePath;
    
    public double bufferTime = 0.0;
    
    /** Creates a new instance of AbstractPlayer */
    public AbstractPlayer() {
        listenerList = new Vector();        
    }

    @Override
    public String getSoundFile(){
        return soundFilePath;
    }
    
    @Override
    public void setStartTime(double startTimeInSeconds) {
        startTime = startTimeInSeconds;
    }

    @Override
    public void setEndTime(double endTimeInSeconds) {
        endTime = endTimeInSeconds;
        //System.out.println("End time set to " + endTimeInSeconds);
    }
    
    @Override
    public void setBufferTime(double bufferTimeInSeconds) {
        bufferTime = bufferTimeInSeconds;
    }
    
    public void reset(){
        try {
            startTime = 0;
            endTime = 0;
            setSoundFile(null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    
    @Override
    public void addPlayableListener(PlayableListener l) {
        listenerList.addElement(l);
    }

    protected void firePlaybackStarted(){
        PlayableEvent event = new PlayableEvent(PlayableEvent.PLAYBACK_STARTED, startTime);
        firePlayableEvent(event);
    }
    
    protected void firePlaybackStopped(){
        System.out.println("Firing playback stopped");
        PlayableEvent event = new PlayableEvent(PlayableEvent.PLAYBACK_STOPPED, getCurrentPosition());
        firePlayableEvent(event);
    }
    
    protected void firePlaybackHalted(){
        PlayableEvent event = new PlayableEvent(PlayableEvent.PLAYBACK_HALTED, getCurrentPosition());
        firePlayableEvent(event);
    }

    protected void firePlaybackResumed(){
        PlayableEvent event = new PlayableEvent(PlayableEvent.PLAYBACK_RESUMED, getCurrentPosition());
        firePlayableEvent(event);
    }

    protected void firePosition(){
        PlayableEvent event = new PlayableEvent(PlayableEvent.POSITION_UPDATE, getCurrentPosition());
        firePlayableEvent(event);
    }

    protected void fireSoundfileSet(){
        PlayableEvent event = new PlayableEvent(PlayableEvent.SOUNDFILE_SET, 0);
        firePlayableEvent(event);        
    }
    
    private void firePlayableEvent(PlayableEvent e){
        for (int pos=0; pos<listenerList.size(); pos++){
            PlayableListener l = (PlayableListener)(listenerList.elementAt(pos));
            l.processPlayableEvent(e);
        }
    }
        

    
    
}
