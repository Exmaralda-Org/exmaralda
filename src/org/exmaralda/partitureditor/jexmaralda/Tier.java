package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
import java.io.*;
import org.exmaralda.partitureditor.jexmaralda.*;
/*
 * Tier.java
 *
 * Created on 6. Februar 2001, 13:03
 */


/* Revision History
 *  0   06-Feb-2001 Creation according to revision 1.1 (!!) of 'exmaralda-time-transcription.dtd'
 *                  and 'exmaralda-segment-transcription.dtd'
 *      12-Feb-2001 Started segmentation method
 *      01-Aug-2002 Get and set methods transferred to new superclass "AbstractTier"
 */

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */


public class Tier extends AbstractEventTier {

   
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new empty Tier */
    public Tier() {
        super();
    }

    /** Creates new Tier with id i, speaker id s, category c, type t*/
    public Tier(String i, String s, String c, String t) {
        super(i,s,c,t);
    }
    
    /** Creates new Tier with id i, speaker id s, category c, type t, display-name d*/
    public Tier(String i, String s, String c, String t, String d) {
        super(i,s,c,t,d);
    }

    /** if this is a tier of type 'a': returns the start points of all events for which no
     * corresponding (chain of) event(s) in a parent tier exists
     * returns null if the tier is not of type 'a' or if there is no parent tier */
    public String[] getAnnotationMismatches(BasicTranscription bt){
        if (!getType().equals("a")) return null;
        Tier parentTier = null;
        for (int pos2=0; pos2<bt.getBody().getNumberOfTiers(); pos2++){
            Tier t2 = bt.getBody().getTierAt(pos2);
            if ((t2.getType()!=null) && (t2.getType().equals("t")) && (t2.getSpeaker()!=null) && (t2.getSpeaker().equals(getSpeaker()))){
                parentTier = t2;
                break;
            }
        }
        if (parentTier==null) return null;
        
        Vector<String> startPoints = new Vector<String>();
        for (int pos2=0; pos2<getNumberOfEvents(); pos2++){
            Event annotation = getEventAt(pos2);
            String s = annotation.getStart();
            String e = annotation.getEnd();
            Event correspondingEvent = null;
            try {
                correspondingEvent = parentTier.getEventAtStartPoint(s);
            } catch (JexmaraldaException ex) {
                 startPoints.addElement(s);               
            }
            while ((correspondingEvent!=null) && (!correspondingEvent.getEnd().equals(e))){
                try {
                    correspondingEvent = parentTier.getEventAtStartPoint(correspondingEvent.getEnd());
                } catch (JexmaraldaException ex) {
                    startPoints.addElement(s);
                    break;
                }
            }
        }        
        String[] retValue = new String[startPoints.size()];
        int pos=0;
        for (String sp : startPoints){
            retValue[pos] = sp;
            pos++;
        }
        
        return retValue;
    }
    
    /** returns a copy of this tier */
    public Tier makeCopy(){
        Tier result=new Tier(this.getID(), this.getSpeaker(), this.getCategory(), this.getType());
        // changed in Version 1.2.5.
        result.setDisplayName(this.getDisplayName());
        result.setUDTierInformation(this.getUDTierInformation().makeCopy());
        for (int pos=0; pos<this.getNumberOfEvents(); pos++){
            result.addEvent(this.getEventAt(pos).makeCopy());
        }
        return result;
    }
    
    /** returns a copy of this tier where all events have empty descriptions */
    public Tier makeEmptyCopy(){
        Tier result=new Tier(this.getID(), this.getSpeaker(), this.getCategory(), this.getType());
        for (int pos=0; pos<this.getNumberOfEvents(); pos++){
            Event oldEvent = this.getEventAt(pos); 
            Event newEvent = new Event(oldEvent.getStart(), oldEvent.getEnd(), new String());
            result.addEvent(newEvent);
        }
        return result;
    }
        
    
    /** returns a tier containing all events of this tier that start after startTLI (inclusive)
     *  and end before endTLI (inclusive) */
    Tier getPartOfTier(Timeline timeline, String startTLI, String endTLI){
        Tier result = new Tier(this.getID(), this.getSpeaker(), this.getCategory(), this.getType());
        result.setDisplayName(this.getDisplayName());
        result.setUDTierInformation(this.getUDTierInformation());
        for (int pos=0; pos<getNumberOfEvents(); pos++){
            Event event = getEventAt(pos);
            if ((!timeline.before(event.getStart(),startTLI)) && (!timeline.before(endTLI, event.getEnd()))){
                result.addEvent(event);
            }
        }
        return result;
    }

    public double calculateEventTime(Timeline timeline){
        double totalTime = 0.0;
        for (int pos=0; pos<getNumberOfEvents(); pos++){
            try {
                Event event = getEventAt(pos);
                double time1 = timeline.getTimelineItemWithID(event.getStart()).getTime();
                double time2 = timeline.getTimelineItemWithID(event.getEnd()).getTime();
                totalTime += (time2-time1);
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
        }
        return totalTime;

    }


    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns the tier as an XML element &lt;tier&gt; as
     *  specified in the corresponding dtd */
    public String toXML(){
        StringBuilder sb = new StringBuilder();
        String [][] atts = {{"id", getID()},
                            {"speaker", getSpeaker()},
                            {"category", getCategory()},
                            {"type", getType()},
                            {"display-name", getDisplayName()}};
        sb.append(StringUtilities.makeXMLOpenElement("tier", atts));
        sb.append(udInformationToXML());
        sb.append(eventsToXML());
        sb.append(StringUtilities.makeXMLCloseElement("tier"));
        return sb.toString();               
    }
    
    /** writes an XML element &lt;tier&gt; to the specified
     *  file output stream */
    public void writeXML(FileOutputStream fos) throws IOException {
        fos.write(toXML().getBytes("UTF-8"));
    }

    // ********************************************
    // ********** EXMARALDA CONVERSIONS ***********
    // ********************************************

    
    public AbstractSegmentVector toSegmentVector(Timeline t){
        sort(t);
        if (getType().equals("t")){
            Segmentation s = new Segmentation();
            s.setTierReference(this.getID());
            s.setName("SpeakerContribution_Event");        
            int turncount = 0;
            for (int pos=0; pos<getNumberOfEvents(); pos++){
                TimedSegment speakerContribution = new TimedSegment();
                speakerContribution.setName("sc");
                speakerContribution.setID(getID() + ".sc" + Integer.toString(turncount));
                Event event = getEventAt(pos);
                speakerContribution.setStart(event.getStart());
                TimedSegment evt = event.toTimedSegment();
                evt.setID(getID() + ".e" + Integer.toString(pos));
                speakerContribution.add(evt);
                while ((pos+1<getNumberOfEvents()) && ((getEventAt(pos+1).getStart()).equals(event.getEnd()))){
                    pos++;
                    event = getEventAt(pos);
                    evt = event.toTimedSegment();
                    evt.setID(getID() + ".e" + Integer.toString(pos));
                    speakerContribution.add(evt);                    
                }
                speakerContribution.setEnd(event.getEnd());
                s.addSegment(speakerContribution);
                turncount++;
            } 
            return s;
        } else if (!getType().equals("a")){
            Segmentation s = new Segmentation();
            s.setTierReference(this.getID());
            s.setName("Event");
            for (int pos=0; pos<getNumberOfEvents();pos++){
                Event event = getEventAt(pos);
                AtomicTimedSegment evt = event.toAtomicTimedSegment();
                evt.setID(getID() + ".e" + Integer.toString(pos));
                s.addSegment(evt);
            }            
            return s;
        } else { // i.e. type is 'a'
            Annotation a = new Annotation();
            a.setTierReference(this.getID());
            a.setName(getCategory());
            for (int pos=0; pos<getNumberOfEvents();pos++){
                Event event = getEventAt(pos);
                TimedAnnotation ta = event.toTimedAnnotation();
                ta.setID(getID() + ".a" + Integer.toString(pos));
                a.addSegment(ta);
            }                        
            return a;        
        }
    }

    


}