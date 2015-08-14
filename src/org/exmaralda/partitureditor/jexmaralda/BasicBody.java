package org.exmaralda.partitureditor.jexmaralda;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Document;
import org.jdom.Element;
/*
 * BasicBody.java
 *
 * Created on 12. April 2001, 11:22
 */



/**
 * The body of a basic transcription 
 * @author  Thomas
 * @version 
 */
public class BasicBody extends AbstractTierBody {


    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new BasicBody */
    public BasicBody() {
    }

    
    /** returns a copy of this BasicBody */
    public BasicBody makeCopy(){
        BasicBody result = new BasicBody();
        result.setCommonTimeline(this.getCommonTimeline().makeCopy());
        for (int pos=0; pos<this.getNumberOfTiers(); pos++){
            try {result.addTier(this.getTierAt(pos).makeCopy());} catch (JexmaraldaException je) {}
        }
        return result;
    }

    public void smoothTimeline() {
        smoothTimeline(0.1);
    }

    public void smoothTimeline(double THRESHHOLD) {
        //double THRESHHOLD = 0.1;
        Hashtable<String,String> tliMappings = new Hashtable<String, String>();
        Timeline tl = getCommonTimeline();
        tl.completeTimes();
        double lastTime = tl.getTimelineItemAt(0).getTime();
        for (int pos=1; pos<tl.getNumberOfTimelineItems(); pos++){
            TimelineItem tli = tl.getTimelineItemAt(pos);
            if ((tli.getTime()-lastTime)<THRESHHOLD){
                lastTime = (tli.getTime()+lastTime)/2.0;
                tli.setTime(lastTime);
                tliMappings.put(tl.getTimelineItemAt(pos-1).getID(), tli.getID());
            } else {
                lastTime = tli.getTime();
            }
        }
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            Tier tier = getTierAt(pos);
            for (int i=0; i<tier.getNumberOfEvents(); i++){
                Event e = tier.getEventAt(i);
                String newStart = e.getStart();
                String newEnd = e.getEnd();
                if (tliMappings.containsKey(newStart)){
                    newStart = tliMappings.get(newStart);
                }
                if (tliMappings.containsKey(newEnd)){
                    newEnd = tliMappings.get(newEnd);
                }
                if (!(newStart.equals(newEnd))){
                    e.setStart(newStart);
                    e.setEnd(newEnd);
                } else {
                    tliMappings.remove(newStart);
                }
            }
        }
        for (String id : tliMappings.keySet()){
            tl.removeTimelineItemWithID(id);
        }
    }


    BasicBody makeEmptyCopy() {
        BasicBody result = new BasicBody();
        result.setCommonTimeline(new Timeline());
        for (int pos=0; pos<this.getNumberOfTiers(); pos++){
            try {result.addTier(
                    getTierAt(pos).makeEmptyCopy());
            } catch (JexmaraldaException je) {}
        }
        return result;
    }

    
    // ********************************************
    // ********** BASIC MANIPULATION **************
    // ********************************************

    /** checks the well-formedness of this body, i.e.
     * everything that is not covered by the DTD */
    public void check() throws JexmaraldaException {
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            getTierAt(pos).check(getCommonTimeline());
        }
    }
                 
    public String repair() {
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            sb.append(getTierAt(pos).repair(getCommonTimeline()));
        }
        return sb.toString();
    }
    
    public void glue(BasicBody otherBody, String[][] tierIDMappings, boolean merge) throws JexmaraldaException {
        Hashtable tliMappings = new Hashtable();
        int start = 1;
        if ((!merge) && (getCommonTimeline().size()>0) && (otherBody.getCommonTimeline().size()>0)){
            // first tli of new is mapped to last tli of old
            tliMappings.put(otherBody.getCommonTimeline().getTimelineItemAt(0).getID(),
                            getCommonTimeline().getTimelineItemAt(getCommonTimeline().size()-1).getID());
        } else {
            start = 0;
        }
        Timeline tl = otherBody.getCommonTimeline();
        if (merge){
            getCommonTimeline().completeTimes();
            tl.completeTimes();
        }
        for (int pos=start; pos<tl.getNumberOfTimelineItems(); pos++){
            TimelineItem oldTLI = tl.getTimelineItemAt(pos);
            TimelineItem newTLI = new TimelineItem();
            String newID = getCommonTimeline().getFreeID();
            newTLI.setID(newID);
            newTLI.setTime(oldTLI.getTime());
            newTLI.setType(oldTLI.getType());
            if (merge){
                getCommonTimeline().insertAccordingToTime(newTLI);
            } else {
                getCommonTimeline().addTimelineItem(newTLI);
            }
            tliMappings.put(oldTLI.getID(), newID);
        }
        for (int pos=0; pos<tierIDMappings.length; pos++){
            String tierID1 = tierIDMappings[pos][0];
            String tierID2 = tierIDMappings[pos][1];
            //System.out.println("Trying to get " + tierID1 + " and " + tierID2);
            Tier tier1 = getTierWithID(tierID1);
            Tier tier2 = otherBody.getTierWithID(tierID2);
            for (int ev=0; ev<tier2.getNumberOfEvents(); ev++){
                Event oldEvent = tier2.getEventAt(ev);
                String newStartID = (String)(tliMappings.get(oldEvent.getStart()));
                String newEndID = (String)(tliMappings.get(oldEvent.getEnd()));
                Event newEvent = new Event( newStartID, newEndID, 
                                            oldEvent.getDescription(),
                                            oldEvent.getMedium(),
                                            oldEvent.getURL());
                newEvent.setUDEventInformation(oldEvent.getUDEventInformation());
                tier1.addEvent(newEvent);
            }
        }
        if (merge){
        }

    }
    
    public BasicBody[] chop (int minNumberOfTimelineItems){
        int[] clearCuts = getClearCuts();
        int lastChop = 0;
        Vector whereToCut = new Vector();
        whereToCut.addElement(new Integer(0));
        for (int pos=0; pos<clearCuts.length; pos++){
            if ((clearCuts[pos]-lastChop)>minNumberOfTimelineItems){
                whereToCut.addElement(new Integer(clearCuts[pos]));
                lastChop = clearCuts[pos];
            }
        }
        whereToCut.addElement(new Integer(getCommonTimeline().getNumberOfTimelineItems()-1));
        BasicBody[] result = new BasicBody[whereToCut.size()-1];
        for (int pos=0; pos<whereToCut.size()-1; pos++){
            int from = ((Integer)(whereToCut.elementAt(pos))).intValue();
            int to = ((Integer)(whereToCut.elementAt(pos+1))).intValue();
            String fromID = getCommonTimeline().getTimelineItemAt(from).getID();
            String toID = getCommonTimeline().getTimelineItemAt(to).getID();
            try{
                result[pos] = getPartOfBody(getAllTierIDs(), fromID, toID);
            } catch (JexmaraldaException je){}
        }
        return result;
    }
    
    BasicBody getPartOfBody(String[] tierIDs, String startTLI, String endTLI) throws JexmaraldaException{
        return getPartOfBody(tierIDs, startTLI, endTLI, false);
    }

    BasicBody getPartOfBody(String[] tierIDs, String startTLI, String endTLI, boolean anchor) throws JexmaraldaException{
        BasicBody result = new BasicBody();
        Timeline timeline = getCommonTimeline();
        result.setCommonTimeline(timeline.getTimelineBetween(startTLI, endTLI));

        if (anchor){
            // changed 09-03-2011: transfer start and end anchor also
            TimelineItem firstTLI = timeline.getTimelineItemAt(0);
            if ((!(firstTLI.getID().equals(startTLI))) && (firstTLI.getTime()>=0)){
                TimelineItem anchorTLI = new TimelineItem();
                anchorTLI.setID(firstTLI.getID());
                anchorTLI.setTime(firstTLI.getTime());
                result.getCommonTimeline().insertTimelineItemAt(firstTLI, 0);
            }
            TimelineItem lastTLI = timeline.getTimelineItemAt(timeline.getNumberOfTimelineItems()-1);
            if ((!(lastTLI.getID().equals(endTLI))) && (lastTLI.getTime()>=0)){
                TimelineItem anchorTLI = new TimelineItem();
                anchorTLI.setID(lastTLI.getID());
                anchorTLI.setTime(lastTLI.getTime());
                result.getCommonTimeline().addTimelineItem(lastTLI);
            }
            // end change
        }

        for (int i=0; i<tierIDs.length; i++){
            Tier newTier = getTierWithID(tierIDs[i]).getPartOfTier(timeline, startTLI, endTLI);
            result.addTier(newTier);
        }
        return result;        
    }
    
    public int[] getClearCuts(){
        Vector resultVector = new Vector();
        resultVector.addElement(new Integer(0));
        Timeline tl = this.getCommonTimeline();
        for (int pos=1; pos<tl.getNumberOfTimelineItems(); pos++){
            String tli = tl.getTimelineItemAt(pos).getID();
            boolean isClearCut = true;
            for (int pos2=0; pos2<getNumberOfTiers(); pos2++){
                Tier t = getTierAt(pos2);
                if (t.containsEventAtStartPoint(tli)){
                    continue;
                } else {
                    Event e = t.getFirstEventBeforeStartPoint(tl, tli);
                    if (e==null) {
                        continue;
                    } else if (tl.lookupID(e.getEnd())>tl.lookupID(tli)){
                        isClearCut = false;
                        break;
                    }
                }
            }
            if (isClearCut) {
                resultVector.addElement(new Integer(pos));
            }
        }
        int[] result = new int[resultVector.size()];
        for (int pos=0; pos<resultVector.size(); pos++){
            result[pos] = ((Integer)(resultVector.elementAt(pos))).intValue();
        }
        return result;
    }
    
    /** returns the tier at the specified position */
    public Tier getTierAt(int position){
        return (Tier)elementAt(position);
    }
    
    
    /** returns the tier with the specified id */
    public Tier getTierWithID(String id) throws JexmaraldaException {
        return (Tier)getAbstractTierWithID(id);
    }


    /** swaps the tiers at the specified positions */
    public void swapTiers(int position1, int position2){
        Tier tier1 = getTierAt(position1).makeCopy();
        Tier tier2 = getTierAt(position2).makeCopy();
        removeTierAt(position1);
        insertElementAt(tier2, position1);
        removeTierAt(position2);
        insertElementAt(tier1, position2);
        updatePositions();
    }

    
    /** returns a canonical tier order, i.e. all tiers ordered acoording to the order of
     * speaker IDs, then according to their types (order: t - a - d - l - ud */
    public String[] makeCanonicalTierOrder(String[] speakerIDs){
        Vector result = new Vector();
        for (int pos=0; pos<speakerIDs.length; pos++){
            String[] tiersOfTypeT = getTiersWithProperties(speakerIDs[pos],"t");
            StringUtilities.addStringArrayToVector(tiersOfTypeT, result);
            String[] tiersOfTypeA = getTiersWithProperties(speakerIDs[pos],"a");
            StringUtilities.addStringArrayToVector(tiersOfTypeA, result);
            String[] tiersOfTypeD = getTiersWithProperties(speakerIDs[pos],"d");
            StringUtilities.addStringArrayToVector(tiersOfTypeD, result);
            String[] tiersOfTypeL = getTiersWithProperties(speakerIDs[pos],"l");            
            StringUtilities.addStringArrayToVector(tiersOfTypeL, result);
            String[] tiersOfTypeUD = getTiersWithProperties(speakerIDs[pos],"ud");            
            StringUtilities.addStringArrayToVector(tiersOfTypeUD, result);
        }
        return StringUtilities.stringVectorToArray(result);
    }
    
    /** reorders the tiers according to the order in the specified array */
    public void reorderTiers(String[] tierIDs) throws JexmaraldaException{
        BasicBody result = new BasicBody();
        result.setCommonTimeline(this.getCommonTimeline());
        for (int pos=0; pos<tierIDs.length; pos++){
//            System.out.println("reordering " + tierIDs[pos]);
            result.addTier(this.getTierWithID(tierIDs[pos]));
        }
        clear();
        positions.clear();
        for (int pos=0; pos<result.getNumberOfTiers(); pos++){
            addTier(result.getTierAt(pos));
        }
    }
    
    /** gets the latest timeline item at which some event ends
     *  this is important for the 'Append interval' action
     * @return
     */
    public String getLastUsedTimelineItem() {
        HashSet<String> allEndIDs = new HashSet<String>();
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            Tier tier = getTierAt(pos);
            for (String id : tier.getAllEndIDs()){
                allEndIDs.add(id);
            }
        }
        for (int i=getCommonTimeline().getNumberOfTimelineItems()-1; i>=0; i--){
            TimelineItem tli = getCommonTimeline().getTimelineItemAt(i);
            if (allEndIDs.contains(tli.getID())){
                return tli.getID();
            }
        }
        // this is the borderkäse: return the very first timeline item
        return getCommonTimeline().getTimelineItemAt(0).getID();
    }

    // added 05-11-2009
    public void removeUnusedTimelineItems(int selectionStartCol, int selectionEndCol) {
        String[] allIDs = {""};
        for (int pos=0; pos<this.getNumberOfTiers(); pos++){
            Tier tier = getTierAt(pos);
            allIDs = StringUtilities.mergeStringArrays(allIDs, tier.getAllStartIDs());
            allIDs = StringUtilities.mergeStringArrays(allIDs, tier.getAllEndIDs());
        }
        Hashtable usedIDs = new Hashtable();
        for (int pos=0; pos<allIDs.length; pos++){
            usedIDs.put(allIDs[pos],"");
        }
        for (int pos=selectionStartCol; pos<Math.min(selectionEndCol+1, getCommonTimeline().getNumberOfTimelineItems()-1); pos++){
            String id = getCommonTimeline().getTimelineItemAt(pos).getID();
            if (!usedIDs.containsKey(id)){
                getCommonTimeline().removeTimelineItemAt(pos);
                pos--;
            }
        }
        if (getCommonTimeline().getNumberOfTimelineItems()==0){
            getCommonTimeline().addTimelineItem();
        }
    }

    /** removes all timeline items that are not the start or end point of
     * at least one event */
    public void removeUnusedTimelineItems(){
        String[] allIDs = {""};
        for (int pos=0; pos<this.getNumberOfTiers(); pos++){            
            Tier tier = getTierAt(pos);
            allIDs = StringUtilities.mergeStringArrays(allIDs, tier.getAllStartIDs());
            allIDs = StringUtilities.mergeStringArrays(allIDs, tier.getAllEndIDs());
        }
        Hashtable usedIDs = new Hashtable();
        for (int pos=0; pos<allIDs.length; pos++){
            usedIDs.put(allIDs[pos],"");
        }
        // changed 17-02-2009: never remove the last timeline item!
        // changed again 19-07-2010: never remove the first timeline item either!
        for (int pos=1; pos<getCommonTimeline().getNumberOfTimelineItems()-1; pos++){
            String id = getCommonTimeline().getTimelineItemAt(pos).getID();
            if (!usedIDs.containsKey(id)){
                //System.out.println("Removing tli " + id);
                getCommonTimeline().removeTimelineItemAt(pos);
                pos--;
            }
        }     
        if (getCommonTimeline().getNumberOfTimelineItems()==0){
            getCommonTimeline().addTimelineItem();
        }
    }
    
    /** returns true if [a) the specified timeline item does not have an absolute time assigned] and
     * b) no event in the transcription starts with or extends over the specified timeline item */
    public boolean isGap(String tli){
        // Removed for version 1.2.2.
        //try{            
        //if (getCommonTimeline().getTimelineItemWithID(tli).getTime()>=0) {return false;}
        if (getCommonTimeline().isLastTimelineItem(tli)){return false;}
        //} catch (JexmaraldaException je) {return false;}
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            Tier tier = getTierAt(pos);
            if (tier.containsEventAtStartPoint(tli)) { return false; }
            Event event = tier.getFirstEventBeforeStartPoint(getCommonTimeline(), tli);
            if (event==null) {continue;}
            if (getCommonTimeline().before(tli,event.getEnd())) { return false; }
        }
        return true;
    }
    
    /** if the specified timeline item is really a gap, remove it */
    public void removeGap(String tli){
        if (!isGap(tli)){return;}
        //System.out.println("Removing gap : " + tli);
        String nextTLI = getCommonTimeline().getNextID(tli);
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            Tier tier = getTierAt(pos);
            Event event = tier.getFirstEventBeforeStartPoint(getCommonTimeline(), tli);
            if (event==null) {continue;}
            if (event.getEnd().equals(tli)) {
                event.setEnd(nextTLI);
            }
        }
        getCommonTimeline().removeTimelineItemWithID(tli);
    }
    
    /** remove all gaps in the transcription */
    public void removeAllGaps(){
        for (int pos=0; pos<getCommonTimeline().getNumberOfTimelineItems(); pos++){
            TimelineItem tli = getCommonTimeline().getTimelineItemAt(pos);
            if (isGap(tli.getID())) {
                removeGap(tli.getID());
                pos--;
            }
        }
    }
    
    public boolean areAllEventsOneIntervalLong(Vector eventsThatAreNot){
        boolean result = true;
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            Tier tier = getTierAt(pos);
            if (!tier.getType().equals("t")) continue;
            for (int pos2=0; pos2<tier.getNumberOfEvents(); pos2++){
                Event event = tier.getEventAt(pos2);
                String start = event.getStart();
                String end = event.getEnd();
                if (getCommonTimeline().lookupID(end) - getCommonTimeline().lookupID(start) != 1){
                    result = false;
                    Object[] o = new Object[2];
                    o[0] = event;
                    o[1] = tier.getID();
                    eventsThatAreNot.addElement(o);
                }
            }
        }
        return result;
    }

    /**
     * looks for a tier with the same speaker as the one in row which has the specified category
     * then returns the Event at col if there is such an event
     * returns null otherwise
     *
     */
    public Event findCorrespondingAnnotation(int row, int col, String category){
        Tier sourceTier = getTierAt(row);
        if (sourceTier.getSpeaker()==null) return null;
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            Tier candidateTier = getTierAt(pos);
            String sp = candidateTier.getSpeaker();
            if ((sp!=null) && (candidateTier.getSpeaker().equals(sourceTier.getSpeaker()) && (candidateTier.getCategory().equals(category)))){
                try {
                    // means: we found a matching tier
                    Event matchingEvent = candidateTier.getEventAtStartPoint(getCommonTimeline().getTimelineItemAt(col).getID());
                    return matchingEvent;
                } catch (JexmaraldaException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns the body as an XML element &lt;basic-body&gt; as
     *  specified in the corresponding dtd */
    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<basic-body>\n");
        sb.append("<common-timeline>\n");
        sb.append(getCommonTimeline().toXML());
        sb.append("</common-timeline>\n");
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            //System.out.println("Sorting tier " + getTierAt(pos).getID());
            getTierAt(pos).sort(getCommonTimeline());
            sb.append(getTierAt(pos).toXML());
        }
        sb.append("</basic-body>\n");
        return sb.toString();
    }



    
    // ********************************************
    // ********** EXMARALDA CONVERSIONS ***********
    // ********************************************

    /** converts the BasicBody to a SegmentedBody by converting all events into segments of a segmentation "event" */
    SegmentedBody toSegmentedBody(){
        SegmentedBody result = new SegmentedBody();
        result.setCommonTimeline(this.getCommonTimeline());
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            Tier t = getTierAt(pos);
            SegmentedTier st = new SegmentedTier();            
            if ((t.getSpeaker()!=null) && (t.getType().equals("t"))){
                Segmentation seg = (Segmentation)t.toSegmentVector(getCommonTimeline());
                st.addElement(seg);
                for (int pos2=0; pos2<getNumberOfTiers(); pos2++){
                    Tier t2 = getTierAt(pos2);
                    if ((t.getSpeaker().equals(t2.getSpeaker())) && (t2.getType().equals("a"))){
                        Annotation an = (Annotation)t2.toSegmentVector(getCommonTimeline());
                        st.addElement(an);
                    }
                }                
            } else if ((t.getType().equals("a")) && (t.getSpeaker()==null)){
                Annotation an = (Annotation)t.toSegmentVector(getCommonTimeline());
                st.addElement(an);                
            } else if (!t.getType().equals("a")) {
                Segmentation seg = (Segmentation)t.toSegmentVector(getCommonTimeline());
                st.addElement(seg);
            } else {
                continue;
            }
            try {
                st.setID(t.getID());
                st.setSpeaker(t.getSpeaker());
                st.setType(t.getType());
                st.setCategory(t.getCategory());
                st.setDisplayName(t.getDisplayName());
                result.addTier(st);
            } catch (JexmaraldaException je){
                je.printStackTrace();
            }
        }
        return result;
    }
    
    
    /** makes uniform tier and timeline IDs */
    void normalize(Hashtable speakerMappings){
        Hashtable timelineMappings  = getCommonTimeline().normalize();
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            Tier tier = getTierAt(pos);
            String oldID = tier.getID();
            String newID = "TIE" + Integer.toString(pos);
            tier.setID(newID);
            String oldSpeaker = tier.getSpeaker();
            if (oldSpeaker!=null){
                String newSpeaker = (String)(speakerMappings.get(oldSpeaker));
                tier.setSpeaker(newSpeaker);
            }
            tier.normalize(timelineMappings);
        }
        this.updatePositions();
    }
    

    public void stratify(short method){
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            Tier t = getTierAt(pos);
            if (!t.isStratified(getCommonTimeline())){
                Tier[] at = t.stratify(getCommonTimeline(), method);
                for (Tier nt : at){
                    nt.setCategory(t.getCategory());
                    nt.setSpeaker(t.getSpeaker());
                    nt.setType(t.getType());
                    nt.setDisplayName(t.getDisplayName());
                    nt.setID(getFreeID());
                    pos++;
                    try {
                        insertTierAt(nt, pos);
                    } catch (JexmaraldaException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        
    }

    public double getSecondsPerCharacter(int row) {
        Tier tier = getTierAt(row);
        String allText = "";
        double allTime = 0.0;
        for (int pos=0; pos<tier.getNumberOfEvents(); pos++){
            try {
                Event e = tier.getEventAt(pos);
                TimelineItem tli1 = getCommonTimeline().getTimelineItemWithID(e.getStart());
                TimelineItem tli2 = getCommonTimeline().getTimelineItemWithID(e.getEnd());
                if ((tli1.getTime()>=0) && (tli2.getTime()>=0)){
                    allText+=e.getDescription();
                    allTime+=tli2.getTime() - tli1.getTime();
                }
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
        }
        if (allText.length()>0){
            return allTime / ((double)allText.length());
        }
        return -1.0;
    }
    
    /** new 12-11-2013 */
    public Document autoAnnotate(String[] tierIDs, String regex, String annotationCategory, String annotationValue, boolean delete, Speakertable speakertable) throws JexmaraldaException{
        Document problemsDocument = org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.makeEmptyDocument();
        Element errors = problemsDocument.getRootElement().getChild("errors");

        for (String id : tierIDs){
            // STEP 1 : determine source and target tiers
            Tier sourceTier = getTierWithID(id);
            String speaker = sourceTier.getSpeaker();
            if (speaker==null) continue;
            Tier targetTier = null;
            for (int pos=0; pos<getNumberOfTiers(); pos++){
                Tier t = getTierAt(pos);
                if (speaker.equals(t.getSpeaker()) && "a".equals(t.getType()) && annotationCategory.equals(t.getCategory())){
                    targetTier = t;
                }
            }
            if (targetTier==null){
                targetTier = new Tier(getFreeID(), speaker, annotationCategory, "a");
                targetTier.setDisplayName(targetTier.getDescription(speakertable));
                insertTierAt(targetTier, lookupID(id)+1);
            } else if (delete){
                targetTier.removeAllEvents();
            }
            
            ArrayList<Event> addMeLater = new ArrayList<Event>();
            int pos=0;
            //for (int pos=0; pos<sourceTier.getNumberOfEvents(); pos++){
            while (pos<sourceTier.getNumberOfEvents()){
                Event event = sourceTier.getEventAt(pos);
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(event.getDescription());
                if (m.find()){
                    int start = m.start();
                    int end = m.end();
                    boolean canAnnotate = (getCommonTimeline().lookupID(event.getEnd()) - getCommonTimeline().lookupID(event.getStart()) == 1)
                            || (start==0 && end==event.getDescription().length());
                    if (!canAnnotate){
                        // insert into problem file
                        //<error file="Hayat_Liang/MT_031109_Hayat.exb" tier="TIE7" start="T401" done="no">Annotation mismatch</error>                        
                        Element errorElement = new Element("error");
                        errorElement.setAttribute("done", "no");
                        errorElement.setAttribute("tier", id);
                        errorElement.setAttribute("start", event.getStart());
                        String text=    event.getDescription().substring(Math.max(0, start-10), start)
                                        + " | " + event.getDescription().substring(start,end)
                                        + " | " + event.getDescription().substring(end,Math.min(end+10, event.getDescription().length()));                        
                        errorElement.setText(text);
                        errors.addContent(errorElement);                        
                    } else {
                        //System.out.println("Matched " + event.getDescription() + ": " + start + "-" + end);
                        String tli1 = event.getStart();
                        if (start>0){
                            tli1 = getCommonTimeline().insertTimelineItemAfter(event.getStart());
                        }
                        String tli2 = event.getEnd();
                        if (end<event.getDescription().length()){
                            tli2 = getCommonTimeline().insertTimelineItemBefore(event.getEnd());
                        }
                        String memorizeEnd = event.getEnd();
                        String memorizeDescription = event.getDescription();
                        if (start>0){
                            String before = memorizeDescription.substring(0, start);
                            event.setDescription(before);
                            event.setEnd(tli1);
                        } else {
                            sourceTier.removeEventAtStartPoint(event.getStart());
                            pos--;
                        }
                        String match = memorizeDescription.substring(start, end);
                        Event annotatedEvent = new Event(tli1, tli2, match);
                        Event annotationEvent = new Event(tli1, tli2, annotationValue);
                        addMeLater.add(annotatedEvent);
                        targetTier.addEvent(annotationEvent);

                        if (end<memorizeDescription.length()){
                            String after = memorizeDescription.substring(end);                                        
                            Event remainingEvent = new Event(tli2, memorizeEnd, after);                    
                            sourceTier.addEvent(remainingEvent);
                        }
                    }
                }
                //System.out.println("pos=" + pos + " / Number of events: " + sourceTier.getNumberOfEvents());
                pos++;
            }
            for (Event e : addMeLater){
                sourceTier.addEvent(e);
            }
            sourceTier.sort(commonTimeline);
            targetTier.sort(commonTimeline);
        }
        return problemsDocument;
    }
    
    public String makeTierFromTimes(ArrayList<double[]> times, double timelineTolerance) throws JexmaraldaException{
        Tier result = new Tier();
        result.setID(getFreeID());
        result.setCategory("sil");
        result.setType("d");
        result.setDisplayName("[sil]");
        
        DecimalFormat nf = new DecimalFormat("00.00");
        
        
        for (double[] timePair : times){
            
            int startIndex = commonTimeline.findTimelineItem(timePair[0], timelineTolerance);            
            String startID = null;
            if (startIndex>=0){
                startID = commonTimeline.getTimelineItemAt(startIndex).getID();
            } else {
                TimelineItem tli = new TimelineItem();
                tli.setID(commonTimeline.getFreeID());
                tli.setTime(timePair[0]);
                commonTimeline.insertAccordingToTime(tli);
                startID = tli.getID();
                //System.out.println("Added " + startID + " / " + timePair[0]);
            }
            
            int endIndex = commonTimeline.findTimelineItem(timePair[1], timelineTolerance);            
            String endID = null;
            if (endIndex>=0){
                endID = commonTimeline.getTimelineItemAt(endIndex).getID();
            } else {
                TimelineItem tli = new TimelineItem();
                tli.setID(commonTimeline.getFreeID());
                tli.setTime(timePair[1]);
                commonTimeline.insertAccordingToTime(tli);
                endID = tli.getID();
            }
            
            Event event = new Event(startID, endID, nf.format(timePair[1]- timePair[0]).replaceAll(",", "."));
            result.addEvent(event);
        }
        
        addTier(result);
        return result.getID();
    }

      
}