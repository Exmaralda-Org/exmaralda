/*
 * PlayableListener.java
 *
 * Created on 28. Juli 2004, 17:26
 */

package org.exmaralda.partitureditor.sound;

/**
 *
 * @author  thomas
 */
public interface PlayableListener {
    
    /** this method should called by a Playable implementation
     * whenever playback is started, halted, resumed or stopped
     * and whenever the current position in the media file has changed.
     * The method 'getType()' on the PlayableEvent can be used
     * to determine which type of event has occured */
    public void processPlayableEvent(PlayableEvent e);
    
}
