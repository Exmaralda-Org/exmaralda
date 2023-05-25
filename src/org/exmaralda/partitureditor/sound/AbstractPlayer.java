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
    List<PlayableListener> listenerList;

    String soundFilePath;
    
    public double bufferTime = 0.0;
    
    /** Creates a new instance of AbstractPlayer */
    public AbstractPlayer() {
        listenerList = new ArrayList<>();        
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
            System.out.println(ex.getMessage());
        }
    }
    
    
    
    @Override
    public void addPlayableListener(PlayableListener l) {
        listenerList.add(l);
    }

    protected void firePlaybackStarted(){
        PlayableEvent event = new PlayableEvent(PlayableEvent.PLAYBACK_STARTED, startTime);
        firePlayableEvent(event);
    }
    
    protected void firePlaybackStopped(){
        //System.out.println("Firing playback stopped");
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
            listenerList.get(pos)
             .processPlayableEvent(e);
        }
    }
    
    public String determineMimeType(String filePath){
        int index = filePath.lastIndexOf(".");
        if (index<0) return "audio/wav";
        if (index>filePath.length()-2) return "audio/wav";
        String suffix = filePath.substring(index+1).toLowerCase();
        switch (suffix){
            case "wav" : return "audio/wav";
            case "mp4" : return "video/mp4";
            case "mp3" : return "audio/mpeg";
            case "mpg" : 
            case "mpeg" : return "video/mpeg";
            default : return "audio/wav";
        }
    }
        

    
    
}
