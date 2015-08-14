package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
import java.io.*;
/*
 * AbstractEventTier.java
 *
 * Created on 12. April 2001, 13:01
 * Major change on 12. June 2002 for Version 1.1.1. :
 * it is now possible to have overlapping events
 * --> added methods for stratifiying tiers, i.e.
 * for making sure that events DO NOT overlap
 * (this is a necessary condition for displaying the tier in interlinear text)
 */



/**
 * Abstract Parent Class of Tier, Segmentation and Annotation 
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de)
 * @version 1.1.1. (12-June-2002)
 */

public class AbstractEventTier extends AbstractTier {

    public static final short STRATIFY_BY_DISTRIBUTION = 1;
    public static final short STRATIFY_BY_DELETION = 2;
    
    /** hashtable for faster access to events via start ids */
    private Hashtable positions;
    /** user defined information about this tier */
    private UDInformationHashtable udTierInformation;


    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new AbstractEventTier */
    public AbstractEventTier() {
        super();
        positions = new Hashtable();
        udTierInformation = new UDInformationHashtable();
    }
    
    /** Creates new AbstractEventTier with id i, speaker s, category c and type t*/
    public AbstractEventTier(String i, String s, String c, String t) {
        super(i,s,c,t);
        positions = new Hashtable();
        udTierInformation = new UDInformationHashtable();
    }
          
    /** Creates new AbstractEventTier with id i, speaker s, category c, type t and display-name d*/
    public AbstractEventTier(String i, String s, String c, String t, String d) {
        super(i,s,c,t,d);
        positions = new Hashtable();
        udTierInformation = new UDInformationHashtable();
    }

    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    /** returns the user defined information of this tier */
    public UDInformationHashtable getUDTierInformation(){
        return udTierInformation;
    }
    
    /** sets the user definde information of this tier to the specified value */
    public void setUDTierInformation(UDInformationHashtable i){
        udTierInformation = i;
    }

    // ********************************************
    // ********** BASIC MANIPULATION **************
    // ********************************************

    
    
    // ********** ADDING, REMOVING, GETTING INFO ABOUT EVENTS **************
    
    /** returns number of fragments contained in tier */
    public int getNumberOfEvents(){
        return size();
    }

    /** adds event e */
    public void addEvent(Event e) {
        addElement(e);
        positions.put(e.getStart(),new Integer(getNumberOfEvents()-1));
    }
   
    /** checks if the tier contains at least one event with start id id */
    public boolean containsEventAtStartPoint(String id){
        if (positions.containsKey(id)) { return true; }
        return false;
    }    

   
    /** returns event at position */
    public Event getEventAt(int position){
        return (Event)elementAt(position);
    }
    
    public void removeAllEvents(){
        this.clear();
        positions.clear();
    }


    /** returns event with start id id, null if there is no fragment with start id 
     *  N.B.: if the tier is not stratified, this may return only one event although
     *  there are several events with the specified start id
     *  to make sure to get all events (i.e. if not sure that the tier is stratified)
     *  use getEvent<b>s</b>AtStartPoint(id) */
    public Event getEventAtStartPoint(String id) throws JexmaraldaException {
        if (containsEventAtStartPoint(id)){
            return (Event)elementAt(lookupID(id));
        }
        throw new JexmaraldaException(7, new String ("No event starting at " + id));
    }
    
    /** returns all events starting at the specified start point 
     *  N.B.: this will be slower then getEventAtStartPoint
     *  but will return ALL events, i.e. works fine also for 
     *  non-stratified tiers */
    public Event[] getEventsAtStartPoint(String id){
        Vector resultVector = new Vector();
        for (int pos=0; pos<getNumberOfEvents(); pos++){
            if (getEventAt(pos).getStart().equals(id)){
                resultVector.add(getEventAt(pos));
            }
        }
        Event[] result = new Event[resultVector.size()];
        for (int pos=0; pos<resultVector.size(); pos++){
            result[pos] = (Event)resultVector.elementAt(pos);
        }
        return result;
    }

    /** gets the first event that starts before the start point with the specified id
     *  returns null if there is no such event 
     *  N.B.: if the tier is not stratified, this may return only one event although
     *  there are several events before the specified start id starting at the same point*/
    public Event getFirstEventBeforeStartPoint(Timeline tl, String id){
        for (int pos=tl.lookupID(id)-1; pos>=0; pos--){
            String tli = tl.getTimelineItemAt(pos).getID();
            if (containsEventAtStartPoint(tli)){
                try {return getEventAtStartPoint(tli);}
                catch (JexmaraldaException je) {}
            }
        }
        return null;
    }
    
    public Event getFirstEventAfterStartPoint(Timeline tl, String id){
        for (int pos=tl.lookupID(id)+1; pos<tl.getNumberOfTimelineItems(); pos++){
            String tli = tl.getTimelineItemAt(pos).getID();
            if (containsEventAtStartPoint(tli)){
                try {return getEventAtStartPoint(tli);}
                catch (JexmaraldaException je) {}
            }
        }
        return null;
    }
    /** returns all events in this tier that lie fully between the 
     * timeline items specified by the two ids
     * @param tl
     * @param id1
     * @param id2
     * @return
     */
    public Vector<Event> getEventsBetween(Timeline tl, String id1, String id2){
        Vector<Event> result = new Vector<Event>();
        for (int pos=tl.lookupID(id1); pos<=tl.lookupID(id2); pos++){
            String tli = tl.getTimelineItemAt(pos).getID();
            if (containsEventAtStartPoint(tli)){
                try {
                    Event e = getEventAtStartPoint(tli);
                    if (tl.lookupID(e.getEnd())<=tl.lookupID(id2)){
                        result.add(e);
                    }
                } catch (JexmaraldaException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return result;
    }
    
    /** returns all events in this tier that intersect the interval between the 
     * timeline items specified by the two ids
     * @param tl
     * @param id1
     * @param id2
     * @return
     */
    public Vector<Event> getEventsIntersecting(Timeline tl, String id1, String id2){
        Vector<Event> result = new Vector<Event>();
        sort(tl);
        if (this.getNumberOfEvents()==0) return result;
        Event firstEvent = this.getFirstEventBeforeStartPoint(tl, id1);
        int firstIndex = 0;
        if (firstEvent!=null){
            firstIndex = lookupID(firstEvent.getStart());
        }
        for (int pos=firstIndex; pos<getNumberOfEvents(); pos++){
            Event testEvent = getEventAt(pos);
            if (tl.intervalsOverlap(id1, id2, testEvent.getStart(), testEvent.getEnd())){
                result.add(testEvent);
            } else {
                if (tl.before(id2, testEvent.getStart())){
                    break;
                }
            }
            
        }
        return result;
    }


    /** removes the event with start id id, does nothing if there is no fragment with start id 
     *  N.B.: if the tier is not stratified, this may remove only one event although
     *  there are several events with the specified start id
     *  to make sure to remove all events (i.e. if not sure that the tier is stratified)
     *  use removeEvent<b>s</b>AtStartPoint(id) */
    public void removeEventAtStartPoint (String id) throws JexmaraldaException{
        if (!containsEventAtStartPoint(id)){
            throw new JexmaraldaException(7, new String ("No event starting at " + id));
        }
        removeElementAt(lookupID(id));
        updatePositions();
    }
        
    /** removes all events starting at the specified start point 
     *  N.B.: this will be slower then removeEventAtStartPoint
     *  but will remove ALL events, i.e. works fine also for 
     *  non-stratified tiers */
    public void removeEventsAtStartPoint(String id){
        for (int pos=0; pos<getNumberOfEvents(); pos++){
            if (getEventAt(pos).getStart().equals(id)){
                this.removeElementAt(pos);
                pos--;
            }
        }
        updatePositions();
    }
    
    // ******* METHODS TO DO WITH THE TIMELINE *********
    
    /** makes uniform timeline IDs */
    void normalize(Hashtable timelineMappings){
        for (int pos=0; pos<getNumberOfEvents(); pos++){
            Event event = getEventAt(pos);
            String oldStart = event.getStart();
            String newStart = (String)(timelineMappings.get(oldStart));
            String oldEnd = event.getEnd();
            String newEnd = (String)(timelineMappings.get(oldEnd));
            event.setStart(newStart);
            event.setEnd(newEnd);
        }
        this.updatePositions();
    }

    /** checks if this tier respects the specified timeline, i.e. returns true iff it contains no events
     * that use a timeline item not contained in this timeline */
    public boolean respectsTimeline(Timeline tl){
        if (getNumberOfEvents()==0) {return true;}
        String[] startIDs = this.getAllStartIDs();
        for (int pos=0; pos<startIDs.length; pos++){
            if (!tl.containsTimelineItemWithID(startIDs[pos])){
                return false;
            }        
        }
        String[] endIDs = this.getAllEndIDs();
        for (int pos=0; pos<endIDs.length; pos++){
            if (!tl.containsTimelineItemWithID(endIDs[pos])){
                return false;
            }        
        }
        return true;
    }
    
    /** bridges gaps, i.e. intervals (according to the given timeline)
     * that contain no events, if their duration (according to the absolute
     * time values of the given timeline) is smaller than the parameter maxDiff */
    public void bridgeGaps(double maxDiff, Timeline tl){
        tl.makeConsistent();
        sort(tl);
        for (int pos=0; pos<getNumberOfEvents()-1; pos++){
            Event event = getEventAt(pos);
            Event nextEvent = getEventAt(pos+1);
            if (event.getEnd().equals(nextEvent.getStart())) {continue;}
            double endTime;
            double startTime;
            try {
                endTime = tl.getTimelineItemWithID(event.getEnd()).getTime();
                startTime = tl.getTimelineItemWithID(nextEvent.getStart()).getTime();
            } catch (JexmaraldaException je) {return;}
            if (endTime>=0 && startTime>=0 && startTime-endTime<maxDiff){
                event.setEnd(nextEvent.getStart());
            }
            updatePositions();
        }
    }
    
    /** returns the IDs of all start points of the events contained in this tier */
    public String[] getAllStartIDs(){
        Vector result = new Vector();
        for (int pos=0; pos<getNumberOfEvents(); pos++){
            result.addElement(getEventAt(pos).getStart());
        }
        return StringUtilities.stringVectorToArray(result);
    }
    
    /** returns the IDs of all end points of the events contained in this tier */
    public String[] getAllEndIDs(){
        Vector result = new Vector();
        for (int pos=0; pos<getNumberOfEvents(); pos++){
            result.addElement(getEventAt(pos).getEnd());
        }
        return StringUtilities.stringVectorToArray(result);
    }
    
    /** sorts the events according to the specified timeline */
    public void sort(Timeline timeline){
        AbstractEventTier result = new AbstractEventTier();
        for (int pos=0; pos<timeline.getNumberOfTimelineItems(); pos++){
            //System.out.println("Sorting " + timeline.getTimelineItemAt(pos).getID());
            Event[] events = getEventsAtStartPoint(timeline.getTimelineItemAt(pos).getID());
            if (events.length>1){
                // sort the events according to their length, longest event first
                events = this.sortAccordingToLength(timeline, events);
            }
            for (int i=0; i<events.length; i++){
                result.addElement(events[i]);
            }
        }
        clear();
        positions.clear();
        for (int pos=0; pos< result.getNumberOfEvents(); pos++){
                addElement(result.getEventAt(pos));
        }
        updatePositions(); 
    }

    /** fills the tier with empty events so that is one uninterrupted sequence of events */
    public void fillWithEmptyEvents(Timeline timeline){            
        sort(timeline);
        if (getNumberOfEvents()==0){return;}        
        Vector newEvents = new Vector();
        for (int pos=0; pos<getNumberOfEvents()-1; pos++){
            Event event = getEventAt(pos);
            Event nextEvent = getEventAt(pos+1);
            if (!event.getEnd().equals(nextEvent.getStart())){
                Event emptyEvent = new Event();
                emptyEvent.setStart(event.getEnd());
                emptyEvent.setEnd(nextEvent.getStart());
                newEvents.add(emptyEvent);
            }
        }
        Event firstEvent = getEventAt(0);
        if (!firstEvent.getStart().equals(timeline.getTimelineItemAt(0).getID())){
            Event emptyEvent = new Event();
            emptyEvent.setStart(timeline.getTimelineItemAt(0).getID());
            emptyEvent.setEnd(firstEvent.getStart());
            newEvents.add(emptyEvent);
        }
        Event lastEvent = getEventAt(getNumberOfEvents()-1);
        if (!lastEvent.getEnd().equals(timeline.getTimelineItemAt(timeline.getNumberOfTimelineItems()-1).getID())){
            Event emptyEvent = new Event();
            emptyEvent.setStart(lastEvent.getEnd());
            emptyEvent.setEnd(timeline.getTimelineItemAt(timeline.getNumberOfTimelineItems()-1).getID());
            newEvents.add(emptyEvent);
        }
        for (int pos=0; pos< newEvents.size(); pos++){
            Event e = (Event)newEvents.elementAt(pos);
            this.addEvent(e);
        }
    }
    
    //***** PRIVATE METHODS FOR HANDLING THE POSITIONS HASHTABLE ******
    // (The positions hashtable is for quick access to events, it only
    // works properly for stratified tiers. For non-stratified tiers,
    // it does not know about several events starting at the same start point)
    //*****************************************************************
    
    /** updates the position hashtable */
    public void updatePositions(){
        positions.clear();
        for (int pos=0; pos<getNumberOfEvents(); pos++){
            positions.put(getEventAt(pos).getStart(), new Integer(pos));
        }       
    }

    /** returns position of event with start id id, -1 if there is 
    no fragment with start id id*/
    private int lookupID(String id){
        if (positions.containsKey(id))
        {
           return ((Integer)positions.get(id)).intValue();
        }
        return -1;
    }

    /** PRIVATE CONVENIENCE METHOD FOR SORTING AN ARRAY OF EVENTS ACCORDING TO LENGTH 
    ** WITH RESPECT TO A GIVEN TIMELINE */
    private Event[] sortAccordingToLength(Timeline timeline, Event[] unsortedEvents){
        Event[] sortedEvents = new Event[unsortedEvents.length];
        int count = 0;
        int checkLength=0;
        while (count<unsortedEvents.length) {
            for (int i=0; i<unsortedEvents.length; i++){
                String start = unsortedEvents[i].getStart();
                String end = unsortedEvents[i].getEnd();
                if (timeline.calculateSpan(start,end)==checkLength){
                    sortedEvents[count]=unsortedEvents[i].makeCopy();
                    count++;
                }
            }
            checkLength++;
        }
        // reverse the order
        count = 0;
        Event[] result = new Event[unsortedEvents.length];
        for (int i=sortedEvents.length-1; i>=0; i--){
            result[count] = sortedEvents[i];
            count++;
        }
        return result;
    }
    
    /** returns true if this tier, according to the given timeline
     * contains an event that would overlap with the given event */
    boolean positionIsOccupied(Timeline timeline, Event event){
        for (int pos=0; pos<getNumberOfEvents(); pos++){
            Event checkEvent = getEventAt(pos);
            if (timeline.eventsOverlap(event,checkEvent)){
                return true;
            }
         }             
         return false;
    }
    
    // ********** STRATIFICATION **************

    /** checks the well-formedness of this tier, i.e.
     * everything that is not covered by the DTD 
     * deprecated since version 1.1.1. 
     * reintroduced in version 1.3.3., 22-03-2007 */
    public void check (Timeline timeline) throws JexmaraldaException{
        String message = "";
        Vector<Event> badEvents = new Vector<Event>();
        for (int pos=0; pos<getNumberOfEvents();pos++){
            Event e = getEventAt(pos);
            int start = timeline.lookupID(e.getStart());
            int end = timeline.lookupID(e.getEnd());
            if (start>=end){
                badEvents.add(e);
                /*String message = "Bad event in tier " + getID() + ": \n";
                message += "Start " + e.getStart() + ", end " + e.getEnd() + "\n";
                throw new JexmaraldaException(117,message);*/
                removeElementAt(pos);
                pos--;
                updatePositions();
            }
        }
    }

    /** does the same things as check but instead of throwing an exception
     * removes the offending event and writes a message to the return value
     * @param timeline
     */
    public String repair(Timeline timeline){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<getNumberOfEvents();pos++){
            Event e = getEventAt(pos);
            int start = timeline.lookupID(e.getStart());
            int end = timeline.lookupID(e.getEnd());
            if (start>=end){
                removeElementAt(pos);
                updatePositions();
                pos--;
                String message = "Removed event in tier " + getID() + " - ";
                message += "Start " + e.getStart() + ", end " + e.getEnd() + ", text " + e.getDescription() + "\n";
                sb.append(message);
            }
        }
        return sb.toString();
    }
    
 
    /** removes all empty events from this tier, i.e.
     * all events that contain nothing but white space */
    public void removeEmptyEvents(){
        for (int pos=0; pos<getNumberOfEvents(); pos++){
            Event event = getEventAt(pos);
            if (event.getDescription().trim().length()<=0){
                this.removeElementAt(pos);
                pos--;
            }
        }
        updatePositions();
    }

    /** checks whether this tier is stratified, i.e. whether it does not contain any
     * overlappimg events */
    public boolean isStratified(Timeline timeline){
        Event emptyEvent = new Event();
        return isStratified(timeline, emptyEvent);
    }
    
    /** checks whether this tier is stratified, i.e. whether it does not contain any
     * overlappimg events */
    public boolean isStratified(Timeline timeline, Event offendingEvent){
        sort(timeline);
        String lastEnd = new String();
        for (int pos=0; pos<getNumberOfEvents(); pos++){
            Event currentEvent = getEventAt(pos);
            if ((lastEnd.length()>0) && (timeline.before(currentEvent.getStart(), lastEnd)))
                {
                    offendingEvent.setDescription(currentEvent.getDescription());
                    offendingEvent.setStart(currentEvent.getStart());
                    offendingEvent.setEnd(currentEvent.getEnd());
                    return false;
                }
            lastEnd = currentEvent.getEnd();
        }
        return true;
    }

    /** will stratify this tier according to the specified timeline and stratifying method 
    * if the method is 'STRATIFY_BY_DISTRIBUTION', the return value will be zero or more
    * new tiers onto which events that were not in a stratified order have been distributed 
    * if the method is 'STRATIFY_BY_DELETION', the return value will always be null */
    // changed 19-06-2008, now returns an array of tiers instead of abstract tiers 
    public Tier[] stratify(Timeline timeline, short stratifyingMethod){
        if (getNumberOfEvents()==0) {return null;}
        sort(timeline);
        Vector removedEvents = new Vector();
        if ((stratifyingMethod==AbstractEventTier.STRATIFY_BY_DELETION) || (stratifyingMethod==AbstractEventTier.STRATIFY_BY_DISTRIBUTION)){
            // first go through the timeline, if there is more than one event starting
            // at a given timeline item, remove all but the first of these events
            // as the tier has been sorted before the event retained will automatically be the
            // longest one
            for (int pos=0; pos<timeline.getNumberOfTimelineItems(); pos++){
                Event[] events = getEventsAtStartPoint(timeline.getTimelineItemAt(pos).getID());
                if (events.length>1){
                    for (int i=1; i<events.length; i++){
                        removedEvents.addElement(events[i]);
                        this.removeElement(events[i]);
                    }
                }
            }
            // now check for other overlaps, i.e. for cases where there are elements e1 and e2
            // and the start time of e2 is before the end time of e1
            String lastEnd = getEventAt(0).getEnd();
            for (int pos=1; pos<getNumberOfEvents(); pos++){
                String start = getEventAt(pos).getStart();
                if (timeline.before(start, lastEnd)){
                    removedEvents.addElement(getEventAt(pos));
                    this.removeElementAt(pos);                    
                    pos--;
                } else {
                    lastEnd = getEventAt(pos).getEnd();
                }
            }
            // and finally check if some of the removed events can be reinserted          
            // to do this: first sort the removed events by length
            Event[] removedEventsArray = new Event[removedEvents.size()];
            for (int i=0; i<removedEvents.size(); i++) {removedEventsArray[i]=(Event)removedEvents.elementAt(i);}
            removedEventsArray = sortAccordingToLength(timeline, removedEventsArray);
            // then check for each removed event whether the space it would occupy is already taken
            for (int i=0; i<removedEventsArray.length; i++){
                Event currentEvent = removedEventsArray[i];
                // if it is not, reinsert it to the tier and remove it from the removed events
                if (!positionIsOccupied(timeline, currentEvent)){
                    this.addEvent(currentEvent);
                    removedEvents.remove(currentEvent);
                }
            }
            sort(timeline);
        }
        if (stratifyingMethod==this.STRATIFY_BY_DISTRIBUTION){
            // make new tiers and distribute the removed elements onto these
            Vector<Tier> newTiers = new Vector<Tier>();
            while (!removedEvents.isEmpty()){
                Tier newTier = new Tier();
                for (int pos=0; pos<removedEvents.size(); pos++){
                    Event currentEvent = (Event)removedEvents.elementAt(pos);
                    if (!newTier.positionIsOccupied(timeline, currentEvent)){
                        newTier.addEvent(currentEvent);
                        removedEvents.removeElementAt(pos);
                        pos--;
                    } else {
                    }
                }
                newTiers.addElement(newTier);
            }
            Tier[] result = new Tier[newTiers.size()];
            for (int i=0; i<newTiers.size(); i++) {result[i]=newTiers.elementAt(i);}
            return result;
        }
        return null;
    }
    
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns the meta information as an XML-element &lt;ud-tier-information&gt; */
    String udInformationToXML(){
        StringBuffer sb = new StringBuffer();
        if (udTierInformation.getNumberOfAttributes()>0){
            sb.append("<ud-tier-information>");
            sb.append(udTierInformation.toXML());
            sb.append("</ud-tier-information>");
        }
        return sb.toString();
    }
    
    /** returns all events as an XML-string */
    String eventsToXML(){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<getNumberOfEvents(); pos++){
                sb.append(getEventAt(pos).toXML());
        }
        return sb.toString();
    }

    
    /** writes all events as an XML-string to the specified FileOutputStream */
    void writeEventsXML(FileOutputStream fos) throws IOException {
        for (int pos=0; pos<getNumberOfEvents(); pos++){
                getEventAt(pos).writeXML(fos);
        }           
    }        

}