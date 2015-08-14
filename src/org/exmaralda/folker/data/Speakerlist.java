/*
 * Speakerlist.java
 *
 * Created on 7. Mai 2008, 11:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import java.util.*;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class Speakerlist {
    
    EventListTranscription transcription;
    Vector<Speaker> speakers;
    HashSet<String> ids;
    
    /** Creates a new instance of Speakerlist */
    public Speakerlist(EventListTranscription t) {
        transcription = t;
        speakers = new Vector<Speaker>();
        ids = new HashSet<String>();
    }
    
    public String getFreeID(String suggestedID){
        String testID = suggestedID;
        int count=1;
        while (ids.contains(testID)){
            count++;
            testID = suggestedID + "-" + Integer.toString(count);
        }
        return testID;        
    }
    
    public String addSpeaker(String id){
        String testID = getFreeID(id);
        Speaker newSpeaker = new Speaker(testID);
        speakers.add(newSpeaker);
        ids.add(testID);
        return testID;
    }
    
    public void removeSpeaker(Speaker s){
        ids.remove(s.getIdentifier());
        speakers.remove(s);
        for (int pos=0; pos<transcription.getNumberOfEvents(); pos++){
            Event e = transcription.getEventAt(pos);
            if (e.getSpeaker()==s){
                e.setSpeaker(null);
            }
        }
    }
    
    public void setSpeakerID(Speaker s, String id){
        ids.remove(s.getIdentifier());
        s.setIdentifier(id);
        ids.add(id);
    }
    
    public Vector<Speaker> getSpeakers(){
        return speakers;
    }
    
    public boolean hasSpeakerID(String id){
        return ids.contains(id);
    }
    
    public String[] getSpeakerIDs(){
        String[] retValue = new String[speakers.size()];
        int pos=0;
        for (Speaker s : speakers){
            retValue[pos] = s.getIdentifier();
            pos++;        
        }
        return retValue;
    }

    public Element toJDOMElement() {
        Element speakersElement = new Element("speakers");
        for (Speaker s : speakers){
            Element speaker = s.toJDOMElement();
            speakersElement.addContent(speaker);
        }

        return speakersElement;
    }

    /** merges this speakerlist with another apeakerlist */
    public void merge(Speakerlist speakerlist) {
        for (Speaker speaker : speakerlist.getSpeakers()){
            if (this.hasSpeakerID(speaker.getIdentifier())) continue;
            this.addSpeaker(speaker.getIdentifier());
        }
    }
    
    
}
