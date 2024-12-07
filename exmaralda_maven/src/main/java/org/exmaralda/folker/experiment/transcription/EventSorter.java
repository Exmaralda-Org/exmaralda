/*
 * EventSorter.java
 *
 * Created on 26. Maerz 2008, 14:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.experiment.transcription;

/**
 *
 * @author thomas
 */
public class EventSorter implements java.util.Comparator<EventInterface> {
    
    /** Creates a new instance of EventSorter */
    public EventSorter() {
    }

    public int compare(EventInterface o1, EventInterface o2) {
        if (o1.getStartTime()<o2.getStartTime()) return -1;
        if (o1.getStartTime()>o2.getStartTime()) return 1;
        // same start times --> compare end times
        if (o1.getEndTime()>o2.getEndTime()) return -1;
        if (o1.getEndTime()<o2.getEndTime()) return 1;
        // same start and end times --> compare speakers
        if ((o1.getSpeakerID()!=null) && (o2.getSpeakerID()!=null)){
            return o1.getSpeakerID().compareTo(o2.getSpeakerID());
        }
        return 0;
    }
    
}
