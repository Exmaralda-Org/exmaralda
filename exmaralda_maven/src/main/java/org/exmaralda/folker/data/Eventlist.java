/*
 * Eventlist.java
 *
 * Created on 7. Mai 2008, 15:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import java.util.*;

/**
 *
 * @author thomas
 */
public class Eventlist {
    
    EventListTranscription transcription;
    Vector<Event> events;
    TimeAssignedComparator eventComparator = new TimeAssignedComparator();
    
    /** Creates a new instance of Eventlist */
    public Eventlist(EventListTranscription t) {
        transcription = t;
        events = new Vector<Event>();
    }
    
    public Vector<Event> getEvents(){
        return events;
    }
    
    public int addEvent(Event e){
        events.addElement(e);
        sort();
        return events.indexOf(e);
    }
    
    public Event getEventAt(int index){
        return events.elementAt(index);
    }
    
    public void sort(){
        Collections.sort(events, eventComparator);
    }

    
    
}
