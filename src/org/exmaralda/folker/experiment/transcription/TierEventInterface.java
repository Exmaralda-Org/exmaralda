/*
 * TierEventInterface.java
 *
 * Created on 20. Maerz 2008, 11:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.experiment.transcription;

import java.util.*;

/**
 *
 * @author thomas
 */
public interface TierEventInterface {
    
    public int getNumberOfTiers();
    
    public int getNumberOfEvents();
    
    public EventInterface getEvent(int index);

    public Vector<EventInterface[]> getTierEventData(double startTime, double endTime);
    
    /* returns the index of the first event which contains the given miliseconds value
     * returns -1 if there is no such event */
    public int findEvent(double miliseconds);
    
    public int addEvent(EventInterface e);
    
}
