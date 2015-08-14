/*
 * AbstractTierEventTranscription.java
 *
 * Created on 26. Maerz 2008, 14:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.experiment.transcription;

import java.util.Vector;
import org.jdom.*;

/**
 *
 * @author thomas
 */
public abstract class AbstractTierEventTranscription implements TierEventInterface {
    
    protected Vector<EventInterface> events;

    /** Creates a new instance of AbstractTierEventTranscription */
    public AbstractTierEventTranscription() {
    }
    
    public EventInterface getEvent(int index) {
        return events.elementAt(index);
    }

    public int getNumberOfEvents() {
        return events.size();
    }

    public int findEvent(double miliseconds) {
        double seconds = miliseconds/1000.0;
        if ((events==null) || events.size()<1) return -1;
        int a = 0;
        int b = getNumberOfEvents();
        int index = (a+b)/2;
        while (!(getEvent(index).contains(seconds))){
            int newIndex = -1;
            if (getEvent(index).getStartTime()>seconds){
                b = index;
            } 
            else if (getEvent(index).getEndTime()<seconds) {
                a = index;
            }
            else return index;
            newIndex = (a+b)/2;
            if (index==newIndex) return -1;
            index = newIndex;            
        }
        return index;
    }

    public int addEvent(EventInterface e) {
        events.add(e);
        java.util.Collections.sort(events, new EventSorter());        
        return events.indexOf(e);
    }
    
    public Document toXMLDocument(){
        Element root = new Element("transcription");
        Document d = new Document(root);
        for (EventInterface ei : events){
            root.addContent(ei.toXMLElement());
        }
        return d;
    }
    
}
