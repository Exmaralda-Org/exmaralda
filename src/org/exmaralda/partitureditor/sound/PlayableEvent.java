/*
 * PlayableEvent.java
 *
 * Created on 28. Juli 2004, 17:27
 */

package org.exmaralda.partitureditor.sound;

/**
 *
 * @author  thomas
 */
public class PlayableEvent {
    
    public static final int PLAYBACK_STARTED = 0;
    public static final int PLAYBACK_HALTED = 1;
    public static final int PLAYBACK_RESUMED = 2;
    public static final int PLAYBACK_STOPPED = 4;
    public static final int POSITION_UPDATE = 8;
    public static final int SOUNDFILE_SET = 16;
    
    /** the type of event which has occured */
    protected int eventType;
    
    /** the position (in seconds) in the sound file at which the event occured */
    protected double eventPosition;
    
    /** Creates a new instance of PlayableEvent */
    public PlayableEvent(int type, double position) {
        eventType = type;
        eventPosition = position;
    }
    
    /** returns the type of the event that has occured*/
    public int getType(){
        return eventType;
    }
    
    /** returns the position (in seconds) in the sound file at which the event occured */
    public double getPosition(){
        return eventPosition;
    }
    
}
