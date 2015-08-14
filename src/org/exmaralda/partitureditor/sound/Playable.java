/*
 * Playable.java
 *
 * Created on 28. Juli 2004, 17:15
 */

package org.exmaralda.partitureditor.sound;

/**
 *
 * @author  thomas
 */
public interface Playable {
    
    /** sets the sound file to be played */
    public void setSoundFile(String pathToSoundFile) throws java.io.IOException;
    
    /** sets the sound file to be played */
    public String getSoundFile();


    /** sets the start time at which the next playback is to begin */
    public void setStartTime(double startTimeInSeconds);
    
    /** sets the end time at which the next playback is to end */
    public void setEndTime(double endTimeInSeconds);
    
    public void setBufferTime(double bufferTimeInSeconds);
    
    /** returns the total length of the sound file in seconds */
    public double getTotalLength();
    
    /** plays the sound file from beginning to end */
    public void startPlayback();
    
    /** halts the playback and leaves the current position where it is*/
    public void haltPlayback();
    
    /** resumes playback at the position where it was last halted */
    public void resumePlayback();
    
    /** stops playback and resets the current position to the start position */
    public void stopPlayback();

    /** plays from beginning to end repeatedly */
    //public void loop();
    
    /** returns the current position in the sound file in seconds */
    public double getCurrentPosition();
    
    /** adds a listener */
    public void addPlayableListener(PlayableListener l);      
    
    public void decreaseCurrentPosition(double time);
    
    public void increaseCurrentPosition(double time);
}
