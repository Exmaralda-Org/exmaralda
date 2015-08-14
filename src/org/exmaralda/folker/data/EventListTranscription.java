/*
 * Transcription.java
 *
 * Created on 7. Mai 2008, 11:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class EventListTranscription {
    
    Timeline timeline;
    Speakerlist speakerlist;
    Eventlist eventlist;
    String mediaPath;
    
    Vector<Contribution> contributions = new Vector<Contribution>();
   
    /** Creates a new instance of Transcription */
    public EventListTranscription(double minimumTime, double maximumTime) {
        timeline = new Timeline(this, minimumTime, maximumTime);
        speakerlist = new Speakerlist(this);
        eventlist = new Eventlist(this);
        updateContributions();
    }

    public EventListTranscription(double minimumTime, double maximumTime, int timelineTolerance) {
        timeline = new Timeline(this, minimumTime, maximumTime, timelineTolerance);
        speakerlist = new Speakerlist(this);
        eventlist = new Eventlist(this);
        updateContributions();
    }

    /** creates a new EventListTranscription which continues where the given one ends */
    public static EventListTranscription AppendTranscription(EventListTranscription t) throws IOException{
        t.updateTimeline();
        double lastTime = t.getTimeline().getMaximumTime();
        org.exmaralda.partitureditor.sound.JMFPlayer player =
            new org.exmaralda.partitureditor.sound.JMFPlayer();
        player.setSoundFile(t.getMediaPath());
        double recEnd = player.getTotalLength() *1000.0;
        EventListTranscription elt = new EventListTranscription(lastTime, recEnd);
        elt.speakerlist = t.getSpeakerlist();
        elt.setMediaPath(t.getMediaPath());
        elt.addEvent(lastTime, lastTime + 2000.0);
        return elt;
    }
    
    public Eventlist getEventlist(){
        return eventlist;
    }

    
    public Timeline getTimeline(){
        return timeline;
    }
    
    public Speakerlist getSpeakerlist(){
        return speakerlist;
    }
    
    public String getMediaPath(){
        return mediaPath;
    }


    
    public void setMediaPath(String mp){
        mediaPath = mp;
    }
            
    public Event getEventAt(int index){
        if (index>=eventlist.events.size()){
            return null;
        }
        return eventlist.getEventAt(index);
    }
    
    public Contribution getContributionAt(int index){
        if ((contributions.size()>0) && index<contributions.size()){
            return contributions.elementAt(index);
        }
        return null;
    }
    
    public int getNumberOfEvents(){
        return eventlist.events.size();
    }
    
    public int getNumberOfContributions(){
        return contributions.size();
    }
    
    public Eventlist getEventsForSpeaker(Speaker s){
        Eventlist result = new Eventlist(this);
        for (Event event : getEventlist().getEvents()){
            if (event.getSpeaker()==s){
                result.addEvent(event);
            }
        }
        return result;
    }

    public boolean selfOverlaps(Event event){
        if (event.getSpeaker()==null) return false;
        Eventlist el = getEventsForSpeaker(event.getSpeaker());
        int thisEventIndex = el.events.indexOf(event);
        if (thisEventIndex>0){
            Event previousEvent = el.events.elementAt(thisEventIndex-1);
            if (event.overlaps(previousEvent)) return true;
        }
        if (thisEventIndex<el.events.size()-1){
            Event nextEvent = el.events.elementAt(thisEventIndex+1);
            if (event.overlaps(nextEvent)) return true;
        }
        return false;
    }
    
    /** the standard action in the event view: adds an event with empty text and no speaker assignment
     * to the list of events. Returns the position at which the event was added */
    public int addEvent(double starttime, double endtime){
        return addEvent(starttime, endtime, null);
    }
    
    public int addEvent(Event event){
        return eventlist.addEvent(event);        
    }
    
    public int addEvent(double starttime, double endtime, Speaker speaker){
        Timepoint startpoint = timeline.addTimepoint(starttime);
        Timepoint endpoint = timeline.addTimepoint(endtime);
        Event event = new Event(startpoint, endpoint, "", speaker);
        return eventlist.addEvent(event);
    }    
    
    public int addEvent(double starttime, double endtime, String text, Speaker speaker){
        Timepoint startpoint = timeline.addTimepoint(starttime);
        Timepoint endpoint = timeline.addTimepoint(endtime);
        if (startpoint==endpoint){
            endpoint = timeline.addTimepoint(endtime + timeline.tolerance + 1);
        }
        Event event = new Event(startpoint, endpoint, text, speaker);
        return eventlist.addEvent(event);        
    }
    
    public int setTimestamps(int index, double startTime, double endTime){
        org.exmaralda.folker.data.Event event = getEventAt(index);
        Timepoint startPoint = getTimeline().addTimepoint(startTime);
        Timepoint endPoint = getTimeline().addTimepoint(endTime);
        event.setStartpoint(startPoint);
        event.setEndpoint(endPoint);
        eventlist.sort();                
        return eventlist.events.indexOf(event);
    }
    
    
    public void removeEvents(int[] indexes){
        Vector<Event> toBeRemoved = new Vector<Event>();
        for (int i : indexes){
            if (i >= eventlist.events.size()) continue;
            toBeRemoved.add(eventlist.getEventAt(i));
            //System.out.println("Removing: " + eventlist.getEventAt(i).getText());
        }
        eventlist.getEvents().removeAll(toBeRemoved);
        updateTimeline();
    }
    
    public void mergeEvents(int[] indices){        
        String allText = "";
        for (int index : indices){
            Event e = getEventAt(index);
            String text = e.getText();
            allText+=text;
        }
        Event firstEvent = getEventAt(indices[0]);
        Event lastEvent = getEventAt(indices[indices.length-1]);
        firstEvent.setText(allText);
        firstEvent.setEndpoint(lastEvent.getEndpoint());
        removeEvents(java.util.Arrays.copyOfRange(indices, 1, indices.length));
    }
    
    
    
    public int splitEvent(int index, int splitPosition){
        // modifed 05-02-2009 to avoid events that are shorter than the tolerance
        Event e = getEventAt(index);
        double startTime = e.getStartpoint().getTime();
        double endTime = e.getEndpoint().getTime();
        double tolerance = timeline.tolerance;
        if ((endTime - startTime) <= 2*tolerance){
            System.out.println(startTime + " " + endTime + " " + tolerance);
            return -1;
        }
        String text = e.getText();
        // modified 01-07-2010 to avoid division by zero when there is no text
        double splitRatio = 0.5;
        if (text.length()>0){
            splitRatio = ((double)splitPosition/text.length());
        }
        double splitTime = Math.min(endTime - (timeline.tolerance+1), startTime + Math.max(timeline.tolerance+1, (endTime - startTime) * splitRatio));
        Timepoint oldEndpoint = e.getEndpoint();
        Timepoint splitTimepoint = getTimeline().addTimepoint(splitTime);
        e.setEndpoint(splitTimepoint);
        e.setText(text.substring(0,splitPosition));
        
        Event secondEvent = new Event(splitTimepoint, oldEndpoint, text.substring(splitPosition), e.getSpeaker());
        return eventlist.addEvent(secondEvent);
    }
    
    public String addSpeaker(String id){
        return speakerlist.addSpeaker(id);
    }


    
    public int fillGaps(){
        int count=0;
        for (int pos=0; pos<getTimeline().timepoints.size()-1; pos++){
            Timepoint t0 = getTimeline().getTimepointAt(pos);
            Timepoint t1 = getTimeline().getTimepointAt(pos+1);
            if (findEvents(t0.getTime(),t1.getTime()).size()==0){
                Event newEvent = new Event(t0,t1,"",null);
                eventlist.addEvent(newEvent);
                count++;
            }
        }
        updateContributions();
        return count;
    }

    public int normalizeWhitespace() {
        int count=0;
        for (Event e : getEventlist().events){
            String text = e.getText();
            String changedText = text.replaceAll("\\s{2,}", " ");
            if (!text.equals(changedText)){
                count++;
                e.setText(changedText);
            }
        }
        return count;
    }


    public int removePauseSpeakerAssignment() {
        int count=0;
        for (Event e : getEventlist().events){
            String text = e.getText();
            if ((text.matches("\\(\\d{1,2}\\.\\d{1,2}\\) ?") && (e.getSpeaker()!=null))){
                e.setSpeaker(null);
                count++;
            }
        }
        return count;
    }

    public int updatePauses() {
        int count=0;
        for (Event e : getEventlist().events){
            String text = e.getText();
            if (text.matches("\\(\\d{1,2}\\.\\d{1,2}\\) ?")){
                double pauseLength = e.getEndpoint().getTime() - e.getStartpoint().getTime();
                String newPauseText = "(" + Double.toString(Math.round(pauseLength/10.0)/100.0) + ") ";
                if (!text.trim().equals(newPauseText.trim())){
                    count++;
                    e.setText(newPauseText);
                }
            }
        }
        return count;

    }
    
    public int findFirstIndexForTime(double time){
        if ((getNumberOfEvents()>0) && (time<getEventAt(0).getStartpoint().getTime())) {
            return 0;
        }
        int row=0;        
        for (Event e : eventlist.events){
            if (time<=e.getStartpoint().getTime()){
                return row;
            }
            row++;
        }
        return getNumberOfEvents()-1;
    }
    
    public Vector<Event> findEvents(double t0, double t1){
        Vector<Event> returnvalue = new Vector<Event>();
        for (Event e : getEventlist().events){
            if ((e.getStartpoint().getTime()>=t0) && (e.getEndpoint().getTime()<=t1)){
                returnvalue.add(e);
            } else if ((e.getStartpoint().getTime()>=t0) && (e.getStartpoint().getTime()<t1)){
                returnvalue.add(e);
            } else if ((e.getEndpoint().getTime()>t0) && (e.getEndpoint().getTime()<=t1)){
                returnvalue.add(e);
            } else if ((e.getStartpoint().getTime()<t0) && (e.getEndpoint().getTime()>t1)){
                returnvalue.add(e);
            }
        }
        return returnvalue;
    }

    public EventListTranscription partForEvents(int[] eventIndices){
        double minTime = Double.MAX_VALUE;
        double maxTime = -1;
        for (int index : eventIndices){
            if (index>=getNumberOfEvents()) continue;
            Event e = eventlist.getEventAt(index);
            minTime = Math.min(minTime, e.getStartpoint().getTime());
            maxTime = Math.max(maxTime, e.getEndpoint().getTime());
        }
        EventListTranscription result = new EventListTranscription(minTime, maxTime);
        result.setMediaPath(getMediaPath());
        for (int index : eventIndices){
            if (index>=getNumberOfEvents()) continue;
            Event e = eventlist.getEventAt(index);
            if ((e.getSpeaker()!=null) && (!(result.speakerlist.hasSpeakerID(e.getSpeaker().getIdentifier())))){
                result.addSpeaker(e.getSpeaker().getIdentifier());
            }
            //result.addEvent(e.getStartpoint().getTime(), e.getEndpoint().getTime(), e.getText(), e.getSpeaker());
            Speaker speaker = null;
            if (e.getSpeaker()!=null){
                speaker = result.getSpeakerWithID(e.getSpeaker().getIdentifier());
            }
            result.addEvent(e.getStartpoint().getTime(), e.getEndpoint().getTime(), e.getText(), speaker);
        }
        return result;
    }

    public EventListTranscription partForContributions(int[] contributionIndices) {
        double minTime = Double.MAX_VALUE;
        double maxTime = -1;
        for (int index : contributionIndices){
            if (index>=getNumberOfContributions()) continue;
            Contribution c = getContributionAt(index);
            minTime = Math.min(minTime, c.getStartpoint().getTime());
            maxTime = Math.max(maxTime, c.getEndpoint().getTime());
        }
        EventListTranscription result = new EventListTranscription(minTime, maxTime);
        result.setMediaPath(getMediaPath());
        for (int index : contributionIndices){
            if (index>=getNumberOfContributions()) continue;
            Contribution c = getContributionAt(index);
            if ((c.getSpeaker()!=null) &&  (!(result.speakerlist.hasSpeakerID(c.getSpeaker().getIdentifier())))){
                result.addSpeaker(c.getSpeaker().getIdentifier());
            }
            for (Event e : c.eventlist.getEvents()){
                Speaker speaker2 = null;
                if (e.getSpeaker()!=null){
                    speaker2 = result.getSpeakerWithID(e.getSpeaker().getIdentifier());
                }
                result.addEvent(e.getStartpoint().getTime(), e.getEndpoint().getTime(), e.getText(), speaker2);
            }
        }
        result.updateContributions();
        return result;
    }

    
    public Speaker getSpeakerWithID(String id){
        for (Speaker s : speakerlist.speakers){
            if (s.getIdentifier().equals(id)) return s;
        }
        return null;
    }
    
    
    public void setText(Event e, String t){
        
    }
    
    public void setSpeaker(Event e, Speaker s){
        
    }
    
    //=============== methods concerning the contribution list ===================
    
    public void updateContributions(){
        contributions = new Vector<Contribution>();
        for (Speaker s : getSpeakerlist().getSpeakers()){
            Eventlist el = getEventsForSpeaker(s);
            if (el.getEvents().size()<=0) continue;
            Contribution currentContribution = new Contribution(this);
            Timepoint lastEndpoint=null;
            for (Event e : el.getEvents()){
                if ((lastEndpoint==null) || (e.getStartpoint().getTime()<=lastEndpoint.getTime())){
                    // i.e. this event is part of the current contribution
                    currentContribution.addEvent(e);
                    if ((lastEndpoint==null) || (e.getEndpoint().getTime()>lastEndpoint.getTime())){
                        lastEndpoint = e.getEndpoint();
                    }
                } else {
                    // i.e. a new contribution starts with this event
                    contributions.add(currentContribution);
                    currentContribution = new Contribution(this);
                    currentContribution.addEvent(e);
                    lastEndpoint = e.getEndpoint();
                }
            }
            // add the last contribution
            contributions.add(currentContribution);
        }
        
        // take care of events without speaker assigment
        Eventlist el2 = getEventsForSpeaker(null);
        for (Event e : el2.getEvents()){
            Contribution newContribution = new Contribution(this);
            newContribution.addEvent(e);
            contributions.add(newContribution);           
        }
        
        Collections.sort(contributions, new TimeAssignedComparator());
    }
    
    public int findFirstContributionIndexForTime(double time){
        if ((contributions.size()>0) && (time<getContributionAt(0).getStartpoint().getTime())) {
            return 0;
        }
        int row=0;        
        for (Contribution c : contributions){
            if (time<=c.getStartpoint().getTime()){
                return row;
            }
            row++;
        }
        return contributions.size()-1;
    }
    
    public Contribution findContribution(Event e){
        for (Contribution c : contributions){
            if (c.eventlist.getEvents().contains(e)) return c;
        }
        return null;
    }
    
    public int getIndexOfContribution(Contribution c){
        return contributions.indexOf(c);
    }



    private void updateTimeline() {
        HashSet<Timepoint> usedTimepoints = new HashSet<Timepoint>();
        for (Event ev : eventlist.events){
            usedTimepoints.add(ev.getStartpoint());
            usedTimepoints.add(ev.getEndpoint());
        }
        timeline.removeUnusedTimepoints(usedTimepoints);
    }

    public void mergeTranscriptions(EventListTranscription elt) {
       getSpeakerlist().merge(elt.getSpeakerlist());
       for (Event event : elt.getEventlist().getEvents()){
           if (event.getSpeaker()!=null){
            event.setSpeaker(this.getSpeakerWithID(event.getSpeaker().getIdentifier()));
           }
           addEvent(event.getStartpoint().getTime(), event.getEndpoint().getTime(), event.getText(), event.getSpeaker());
           //this.eventlist.addEvent(event);
       }
    }

    public EventListTranscription splitTranscription(double splitTime) throws IOException {
        org.exmaralda.partitureditor.sound.JMFPlayer player =
            new org.exmaralda.partitureditor.sound.JMFPlayer();
        System.out.println("MEDIA PATH " + getMediaPath());
        player.setSoundFile(getMediaPath());
        double recEnd = player.getTotalLength() *1000.0;
        EventListTranscription elt = new EventListTranscription(splitTime, recEnd);
        elt.speakerlist = getSpeakerlist();
        elt.setMediaPath(getMediaPath());
        Vector<Event> toBeRemoved = new Vector<Event>();
        for (Event event : getEventlist().getEvents()){
           if (event.getEndpoint().getTime()<=splitTime) continue;
           elt.addEvent(event.getStartpoint().getTime(), event.getEndpoint().getTime(), event.getText(), event.getSpeaker());
           //elt.eventlist.addEvent(event);
           toBeRemoved.add(event);
        }
        eventlist.events.removeAll(toBeRemoved);
        updateTimeline();
        return elt;
    }



    // ***************************
    // *** FINDING & REPLACING ***
    // ***************************

    public int[] findInSegments(Pattern p, int startRow, int startPos){
        int[] result = new int[3];
        Event e = getEventAt(startRow);
        String searchString = e.getText();
        Matcher m = p.matcher(searchString);
        if (m.find(startPos)){
            result[0] = startRow;
            result[1] = m.start();
            result[2] = m.end();
            return result;
        }
        if (startRow<eventlist.getEvents().size()-1){
            return findInSegments(p, startRow+1, 0);
        }
        return null;
    }
    
    public int[] findInContributions(Pattern p, int startRow, int startPos){
        int[] result = new int[3];
        Contribution c = this.getContributionAt(startRow);
        String searchString = c.getText();
        Matcher m = p.matcher(searchString);
        if (m.find(Math.min(searchString.length()-1, startPos))){
            result[0] = startRow;
            result[1] = m.start();
            result[2] = m.end();
            return result;
        }
        if (startRow<this.contributions.size()-1){
            return findInContributions(p, startRow+1, 0);
        }
        return null;
    }

    public int replaceAllInSegments(String searchExpression, String replaceExpression) {
        int count = 0;
        for (Event e : getEventlist().getEvents()){
            String original = e.getText();
            String changed = original.replaceAll(searchExpression, replaceExpression);
            if (!(changed.equals(original))){
                count++;
                e.setText(changed);
            }
        }
        return count;
    }

    // ***************************

    public Element toJDOMElement(File file){
        return toJDOMElement(file, null);
    }
    
    public Element toJDOMElement(File file, TranscriptionHead transcriptionHead){
        Element folkertranscription = new Element("folker-transcription");

        if (transcriptionHead==null){
            // the transcription head (is empty)
            Element head = new Element("head");
            folkertranscription.addContent(head);
        } else {
            Element head = transcriptionHead.getHeadElement();
            // just to make sure...
            head.detach();
            folkertranscription.addContent(head);
        }

        // the speakerlist
        Element speakers = getSpeakerlist().toJDOMElement();
        folkertranscription.addContent(speakers);

        // the recording
        Element recording = new Element("recording");
        // changed 18-08-2010: null file is given
        // when this method is used for copying
        if (file!=null){
            // changed 24-01-2011
            /*URI uri1 = new File(getMediaPath()).toURI();
            URI uri2 = file.getParentFile().toURI();
            URI relativeURI = uri2.relativize(uri1);
            String relativePath = relativeURI.toString();*/
            File file1 = new File(getMediaPath());
            File file2 = file.getParentFile();
            String relativePath = org.exmaralda.common.helpers.RelativePath.getRelativePath(file2, file1);            
            
            recording.setAttribute("path", relativePath);
        } else {
            recording.setAttribute("path", getMediaPath());
        }
        folkertranscription.addContent(recording);

        // the timeline
        Element timelineElement = getTimeline().toJDOMElement();
        folkertranscription.addContent(timelineElement);

        // the contributions (unparsed)
        for (Contribution c : contributions){
            folkertranscription.addContent(c.toJDOMElement(getTimeline()));
        }

        return folkertranscription;


    }

    public void lowerCaseAll() {
        for (Event e : getEventlist().getEvents()){
            String original = e.getText();
            String changed = original.toLowerCase();
            if (!(changed.equals(original))){
                e.setText(changed);
            }
        }        
    }
    
    public void removePuncutation() {
        for (Event e : getEventlist().getEvents()){
            String original = e.getText();
            String changed = original.replaceAll("(?<!(\\d|\\p{Punct}))\\p{Punct}(?!(\\d|\\p{Punct}))", "");
                    
            if (!(changed.equals(original))){
                e.setText(changed);
            }
        }                
    }

    


    
    
}
