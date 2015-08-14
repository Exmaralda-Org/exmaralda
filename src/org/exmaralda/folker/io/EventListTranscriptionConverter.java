/*
 * EventListTranscriptionConverter.java
 *
 * Created on 9. Mai 2008, 15:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.io;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.EventWrapper;
import org.exmaralda.folker.data.Timepoint;

/**
 *
 * @author thomas
 */
public class EventListTranscriptionConverter {
    
    /** Creates a new instance of EventListTranscriptionConverter */
    public EventListTranscriptionConverter() {
    }
    
    public static EventListTranscription importExmaraldaBasicTranscription(BasicTranscription bt){
        return importExmaraldaBasicTranscription(bt, null, new EventWrapper(), 50, false);
    }

    public static EventListTranscription importExmaraldaBasicTranscription(BasicTranscription bt, boolean removeEmptyEvents){
        return importExmaraldaBasicTranscription(bt, null, new EventWrapper(), 50, removeEmptyEvents);
    }

    public static EventListTranscription importExmaraldaBasicTranscription(BasicTranscription bt, int timelineTolerance){
        return importExmaraldaBasicTranscription(bt, null, new EventWrapper(), timelineTolerance, false);
    }
            
    public static EventListTranscription importExmaraldaBasicTranscription(
            BasicTranscription bt,
            org.exmaralda.partitureditor.jexmaralda.Event selectedEvent,
            EventWrapper correspondingEvent
            ){
        return importExmaraldaBasicTranscription(bt, selectedEvent, correspondingEvent, 50, false);
    }

    public static EventListTranscription importExmaraldaBasicTranscription(
            BasicTranscription bt,
            org.exmaralda.partitureditor.jexmaralda.Event selectedEvent,
            EventWrapper correspondingEvent,
            int timelineTolerance,            
            boolean removeEmptyEvents
            ){
        
        org.exmaralda.partitureditor.jexmaralda.Timeline tl = bt.getBody().getCommonTimeline();
        tl.completeTimes(false, bt, false);
        
        // new 01-12-2014
        // changed 05-01-2015
        if (removeEmptyEvents){
            for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
                bt.getBody().getTierAt(pos).removeEmptyEvents();
            }
        }
        
        EventListTranscription result = new EventListTranscription(
                tl.getTimelineItemAt(0).getTime()*1000.0,
                tl.getMaxTime()*1000.0,
                timelineTolerance);
        
        // import speakers and memorize the id mappings
        Hashtable<String,String> speakerIDMappings = new Hashtable<String,String>();
        for (int pos=0; pos<bt.getHead().getSpeakertable().getNumberOfSpeakers(); pos++){
            org.exmaralda.partitureditor.jexmaralda.Speaker importedSpeaker 
                    = bt.getHead().getSpeakertable().getSpeakerAt(pos);
            String newID = result.addSpeaker(importedSpeaker.getAbbreviation());
            speakerIDMappings.put(importedSpeaker.getID(), newID);
            if (importedSpeaker!=null){
                if (importedSpeaker.getUDSpeakerInformation().containsAttribute("Name")){
                    result.getSpeakerWithID(newID).setName(importedSpeaker.getUDSpeakerInformation().getValueOfAttribute("Name"));
                }
            }
        }
        
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            org.exmaralda.partitureditor.jexmaralda.Tier tier = bt.getBody().getTierAt(pos);
            if (!(tier.getType().equals("t"))) continue;
            org.exmaralda.folker.data.Speaker speaker = null;
            if (tier.getSpeaker()!=null){
                speaker = result.getSpeakerWithID(speakerIDMappings.get(tier.getSpeaker()));
            }
            for (int i=0; i<tier.getNumberOfEvents(); i++){
                org.exmaralda.partitureditor.jexmaralda.Event e = tier.getEventAt(i);
                try {
                    double start = tl.getTimelineItemWithID(e.getStart()).getTime()*1000.0;
                    double end = tl.getTimelineItemWithID(e.getEnd()).getTime()*1000.0;
                    String text = e.getDescription();                    
                    int index = result.addEvent(start, end, text, speaker);
                    if (e==selectedEvent){
                        correspondingEvent.setFolkerEvent(result.getEventAt(index));
                    }
                } catch (JexmaraldaException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        String audio = bt.getHead().getMetaInformation().getReferencedFile("wav");
        if (audio==null){
            audio = bt.getHead().getMetaInformation().getReferencedFile();
        }
        result.setMediaPath(audio);
        
        System.out.println("************" + result.getMediaPath());

        result.updateContributions();

        return result;
    }
    
    public static BasicTranscription exportBasicTranscription(EventListTranscription elt){
        return exportBasicTranscription(elt, null, new EventWrapper());
    }

    public static BasicTranscription exportBasicTranscription(
            EventListTranscription elt,
            org.exmaralda.folker.data.Event selectedEvent,
            EventWrapper correspondingEvent){
        BasicTranscription result = new BasicTranscription();
        
        result.getHead().getMetaInformation().setReferencedFile(elt.getMediaPath());
        
        // make timeline
        org.exmaralda.partitureditor.jexmaralda.Timeline timeline = result.getBody().getCommonTimeline();
        int count = 0;
        for (Timepoint tp : elt.getTimeline().getTimepoints()){
            TimelineItem tli = new TimelineItem();
            tli.setID("TLI_" + Integer.toString(count));
            tli.setTime(tp.getTime()/1000.0);
            try {
                timeline.addTimelineItem(tli);
                count++;
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
        }

        // make speakertable and tiers 
        Speakertable st = result.getHead().getSpeakertable();
        for (org.exmaralda.folker.data.Speaker s : elt.getSpeakerlist().getSpeakers()){
            org.exmaralda.partitureditor.jexmaralda.Speaker sp 
                    = new org.exmaralda.partitureditor.jexmaralda.Speaker();
            sp.setID(s.getIdentifier());
            sp.setAbbreviation(s.getIdentifier());
            sp.getUDSpeakerInformation().setAttribute("Name", s.getName());
            try {
                st.addSpeaker(sp);
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
            
            Tier t = new Tier();
            t.setCategory("v");
            t.setType("t");
            t.setSpeaker(s.getIdentifier());
            t.setID("TIE_" + s.getIdentifier());
            t.setDisplayName(s.getIdentifier());
            try {
                result.getBody().addTier(t);
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
        }
        
        // make a tier for events which are not speaker assigned
        Tier nn_t = new Tier();
        nn_t.setCategory("v");
        nn_t.setType("t");
        nn_t.setSpeaker(null);
        nn_t.setID("TIE_");
        nn_t.setDisplayName("");
        try {
            result.getBody().addTier(nn_t);
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }

        // put events into tiers
        org.exmaralda.folker.data.Timeline tl = elt.getTimeline();
        for (org.exmaralda.folker.data.Event ev : elt.getEventlist().getEvents()){
            org.exmaralda.partitureditor.jexmaralda.Event evt 
                    = new org.exmaralda.partitureditor.jexmaralda.Event();
            evt.setDescription(ev.getText());
            evt.setStart("TLI_" +  tl.getTimepoints().indexOf(ev.getStartpoint()));
            evt.setEnd("TLI_" +  tl.getTimepoints().indexOf(ev.getEndpoint()));
            try {
                String tierID = "";
                if (ev.getSpeaker()!=null){
                    tierID = ev.getSpeaker().getIdentifier();
                }
                Tier t = result.getBody().getTierWithID("TIE_" + tierID);
                t.addEvent(evt);
                if (ev==selectedEvent){
                    correspondingEvent.setExmaraldaEvent(evt);
                    evt.getUDEventInformation().setAttribute("Tier-ID", t.getID());
                }
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
        }
                
        
        return result;
    }
    
}
