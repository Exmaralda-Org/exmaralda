package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
import java.io.*;
/*
 * Timeline.java
 *
 * Created on 6. Februar 2001, 12:18
 */

/* Revision History
 *  0   17-Apr-2001 first reference implementation
 */

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */

public class Timeline extends Vector {

    private Hashtable positions;

    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new empty Timeline */
    public Timeline() {
        super();
        positions = new Hashtable();
    }

    
    /** returns a copy of this timeline */
    public Timeline makeCopy(){
        Timeline result = new Timeline();
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            TimelineItem tli = getTimelineItemAt(pos);
            result.addElement(tli.makeCopy());
        }
        result.updatePositions();
        return result;
    }

    // ********************************************
    // ********** BASIC MANIPULATION **************
    // ********************************************

    /** returns the timelineitem at the specified position */
    public TimelineItem getTimelineItemAt(int position){
        if (position>=size()) return null;
        return (TimelineItem)elementAt(position);
    }
    
    /** returns true if the timeline contains a timeline item with the specified id, false otherwise */
    public boolean containsTimelineItemWithID(String id){
        if (lookupID(id)==-1) {return false;}
        return true;
    }
    
    /** returns the timeline item with the specified id */
    public TimelineItem getTimelineItemWithID (String id) throws JexmaraldaException{
        if (lookupID(id)==-1) {throw new JexmaraldaException(3, new String("No such timeline-item: " + id));}
        return getTimelineItemAt(lookupID(id));
    }
    
    /** returns the position of the timeline item with the specified id, if it is
    in the timeline. returns -1 otherwise */
    public int lookupID(String id){
        if (positions.containsKey(id)){ 
            return ((Integer)positions.get(id)).intValue(); 
        }
        return -1;
    }
    
    /** returns a timeline consisting of the time line items between positions pos1 and pos2 (exclusively) */
    Timeline getTimelineBetween(int pos1, int pos2){
        Timeline t = new Timeline ();
        for (int i=pos1+1; i<pos2; i++){
            t.addElement(getTimelineItemAt(i));
        }
        t.updatePositions();
        return t;
    }
    
    /** returns a timeline consisting of the time line items between the items with id1 and id2 (inclusively).
    throws a JexmaraldaException if either of the ids does not exist or if the item with id2 is before the item with id 1*/
    public Timeline getTimelineBetween(String id1, String id2) throws JexmaraldaException {
        if (lookupID(id1)==-1) { throw new JexmaraldaException(3, new String("No such timeline item : " + id1)); }
        if (lookupID(id2)==-1) { throw new JexmaraldaException(3, new String("No such timeline item : " + id2)); }
        if (lookupID(id2)<lookupID(id1)){throw new JexmaraldaException(4, new String("Wrong order of timeline-items: " + id1 + " " + id2)); }
        Timeline t = new Timeline ();
        for (int i=lookupID(id1); i<=lookupID(id2); i++){
            t.addElement(getTimelineItemAt(i));
        }
        t.updatePositions();
        return t;
    }

    public void removeTimelineInterval(int start, int end){
        removeRange(start, end);
        updatePositions();
    }

    boolean eventsOverlap(Event event1, Event event2){
        return intervalsOverlap(event1.getStart(), event1.getEnd(), event2.getStart(), event2.getEnd());
    }
    
    boolean intervalsOverlap(String a1, String b1, String a2, String b2){
        int start1 = lookupID(a1); 
        int start2 = lookupID(a2); 
        int end1 = lookupID(b1); 
        int end2 = lookupID(b2);
        boolean oneTwo = (start1<start2) && (end1<=start2); 
        boolean twoOne = (start2<start1) && (end2<=start1); 
        return !(oneTwo || twoOne);        
    }
    
    /** returns a free id */
    public String getFreeID(){
        if (getNumberOfTimelineItems()>0) {
            int i=0;
            while (positions.containsKey((String)("T" + new Integer(i).toString()))){i++;}
            return (String)("T" + new Integer(i));
        }
        return "T0";
    }
    
    public boolean anchorTimeline(double first, double last){
        boolean itemAdded = false;
        if (getTimelineItemAt(0).getTime()<0) {
            getTimelineItemAt(0).setTime(first);
        } 
        if ((getTimelineItemAt(size()-1).getTime()<0)) {
            getTimelineItemAt(size()-1).setTime(last);
        } else if (getTimelineItemAt(size()-1).getTime()<last - 0.01){
            TimelineItem newTLI = new TimelineItem();
            newTLI.setID(getFreeID());
            newTLI.setTime(last);
            try {
                addTimelineItem(newTLI);
                itemAdded = true;
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
        }
        return itemAdded;
    }
    
    
    /** returns the number of timeline items in the time line */
    public int getNumberOfTimelineItems(){
        return size();
    }
       

    public boolean isLastTimelineItem(String id){
        if (lookupID(id)==getNumberOfTimelineItems()-1){
            return true;
        }
        return false;
    }
    
    /** adds the time line item tli to the end of the time line */
    public String addTimelineItem() {
        String id = getFreeID();
        TimelineItem tli = new TimelineItem(id);
        try {addTimelineItem(tli);}
        catch (JexmaraldaException je) {}
        return id;
    }

    /** adds the time line item tli to the end of the time line */
    public void addTimelineItem(TimelineItem tli) throws JexmaraldaException {
        String newID = tli.getID();
        if (positions.containsKey(newID)){throw new JexmaraldaException(2, new String("ID " + newID + " already exists in this timeline. "));}
        addElement(tli);
        positions.put(newID, new Integer(getNumberOfTimelineItems()-1));
    }

    void updatePositions(){
        positions.clear();
        for (int i=0; i<getNumberOfTimelineItems(); i++){
            positions.put(getTimelineItemAt(i).getID(), new Integer(i));
        }       
    }
        
    /** inserts a new time line item after the one with the specified id */
    public void insertTimelineItemAfter(String id, TimelineItem tli) throws JexmaraldaException {
        if (lookupID(id)==-1) {throw new JexmaraldaException(3, new String("No such timeline item : " + id)); }
        if (lookupID(tli.getID())!=-1) {throw new JexmaraldaException(2, new String("ID " + tli.getID() + " already exists in this timeline. ")); }       
        insertElementAt(tli, lookupID(id)+1);
        updatePositions();
    }

    /** inserts a new time line item after the one with the specified id */
    public String insertTimelineItemAfter(String id) throws JexmaraldaException {
        if (lookupID(id)==-1) {throw new JexmaraldaException(3, new String("No such timeline item : " + id)); }
        String newID = getFreeID();
        TimelineItem tli = new TimelineItem(newID);
        insertElementAt(tli, lookupID(id)+1);
        updatePositions();
        return newID;
    }

    /** inserts a new time line item after the one with the specified id */
    public void insertTimelineItemBefore(String id, TimelineItem tli) throws JexmaraldaException {
        if (lookupID(id)==-1) {throw new JexmaraldaException(3, new String("No such timeline item : " + id)); }
        if (lookupID(tli.getID())!=-1) {throw new JexmaraldaException(2, new String("ID " + tli.getID() + " already exists in this timeline. ")); }       
        insertElementAt(tli, lookupID(id));
        updatePositions();
    }

    /** inserts a new time line item after the one with the specified id */
    public String insertTimelineItemBefore(String id) throws JexmaraldaException {
        if (lookupID(id)==-1) {throw new JexmaraldaException(3, new String("No such timeline item : " + id)); }
        String newID = getFreeID();
        TimelineItem tli = new TimelineItem(newID);
        insertElementAt(tli, lookupID(id));
        updatePositions();
        return newID;
    }

    public void insertTimelineItemAt(TimelineItem tli, int position) {
        insertElementAt(tli, position);
        updatePositions();
    }



    /** removes the timeline item at the specified position */
    public void removeTimelineItemAt(int position){
        removeElementAt(position);
        updatePositions();
    }
    
    /** removes the timeline item with the specified ID */
    void removeTimelineItemWithID(String id){
        removeElementAt(lookupID(id));
        updatePositions();
    }
    
    /** returns the ID of the next timeline item after the one specified */
    String getNextID(String id){
        if (isLastTimelineItem(id)) {return null;}
        return (getTimelineItemAt(lookupID(id)+1).getID());
    }

    /** returns true if id1 comes before id2 in this timeline */
    public boolean before(String id1, String id2){
        return (lookupID(id1) < lookupID(id2));
    }
    
    /** returns the span between the two specified timeline items */
    public int calculateSpan(String start, String end){
        return (lookupID(end)-lookupID(start));
    }

    /** returns true if absolute time values are monotonously increasing 
    *  false otherwise */
    public boolean isConsistent(){
        double minTime = 0;
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            double currentTime=getTimelineItemAt(pos).getTime();
            if (currentTime>=0){
                if (currentTime<minTime) {return false;}
                else {minTime = currentTime;}
            }
        }
        return true;
    }
    
    /** kicks out all absolute time values that would make the timeline non-consistent 
    * returns true if changes to the timeline had to be made, false otherwise */
    public boolean makeConsistent(){
        double minTime = 0;
        boolean result=false;        
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            double currentTime=getTimelineItemAt(pos).getTime();
            if (currentTime>=0){
                // changed 28-04-2009: don't allow identical timestamps
                // changed again 22-06-2009: don't mistreat the first TLI!
                if ((pos>0) && (currentTime<=minTime)) {
                    getTimelineItemAt(pos).setTime(-0.1);
                    result=true;
                } else {minTime = currentTime;}
            }
        }
        return result;
    }

    public String[] getInconsistencies(){
        double minTime = 0;
        Vector<String> result = new Vector<String>();
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            double currentTime=getTimelineItemAt(pos).getTime();
            if (currentTime>=0){
                // changed 28-04-2009: don't allow identical timestamps
                // changed again 22-06-2009: don't mistreat the first TLI!
                if ((pos>0) && (currentTime<=minTime)) {
                    result.addElement(getTimelineItemAt(pos).getID());
                } else {
                    minTime = currentTime;
                }
            }
        }
        return result.toArray(new String[0]);
    }
    
    public void shiftAbsoluteTimes(double amount){
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            double currentTime=getTimelineItemAt(pos).getTime();
            if (currentTime>=0){
                getTimelineItemAt(pos).setTime(currentTime + amount);
            }
        }
    }
    
    public void scaleAbsoluteTimes(double factor) {
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            double currentTime=getTimelineItemAt(pos).getTime();
            if (currentTime>=0){
                getTimelineItemAt(pos).setTime(currentTime * factor);
            }
        }
    }
    

    public void completeTimes(){
        completeTimes(true, null);
    }
    
    /** interpolates the timeline */
    public void completeTimes(boolean linear, BasicTranscription bt){
        completeTimes(linear, bt, true);
    }
    /** interpolates the timeline */
    public void completeTimes(boolean linear, BasicTranscription bt, boolean reinterpolate){

        if (getNumberOfTimelineItems()==0) {return;}
        
        // added 12-03-2015: force linear if we do not have tiers of type t
        linear = linear || bt.getBody().getTiersOfType("t").length==0;
        
        if (reinterpolate){
            // changed 16-12-2011: make this an option only (because of FOLKER)
            // changed 28-10-2009: interpolated times can be reinterpolated
            removeInterpolatedTimes();
        }
        
        // Make sure the absolute times are consistent
        makeConsistent();

        // Make sure first and last timeline items have absolute time value
        TimelineItem tli = getTimelineItemAt(getNumberOfTimelineItems()-1);
        if (tli.getTime()<0){
            String id = getMaxTimeID();
            if (id==null){  // i.e. there are no absolute time values in this timeline
                tli.setTime(new Double(getNumberOfTimelineItems()-1).doubleValue());
                tli.setType("intp");
            }
            else {
                double time = getTimelineItemAt(lookupID(id)).getTime()/lookupID(id)*(getNumberOfTimelineItems()-1);
                tli.setTime(time);
                tli.setType("intp");
            }
        }
        tli = getTimelineItemAt(0);
        if (tli.getTime()<0){
            tli.setTime(0);
            tli.setType("intp");
        }
        
        
        // Now do the actual interpolation
        if (linear){
            // make a linear interpolation
            for (int pos=1; pos<getNumberOfTimelineItems()-1; pos++){
                tli = getTimelineItemAt(pos);
                if (tli.getTime()<0) {
                    String id1 = getPreviousTimeID(tli.getID());
                    String id2 = getNextTimeID(tli.getID());
                    TimelineItem tli1 = getTimelineItemAt(lookupID(id1));
                    TimelineItem tli2 = getTimelineItemAt(lookupID(id2));
                    tli.setTime(tli1.getTime()+(tli2.getTime()-tli1.getTime())/(lookupID(id2)-lookupID(id1)));
                    tli.setType("intp");
                }
            }
        } else {
            // make an interpolation based on character counts
            double[] characterWidths = new double[getNumberOfTimelineItems()];
            for (int j=0; j<getNumberOfTimelineItems(); j++){
                TimelineItem tliN = getTimelineItemAt(j);
                int totalWidth = 0;
                int totalCount = 0;
                for (int i=0; i<bt.getBody().getNumberOfTiers(); i++){
                    Tier tier = bt.getBody().getTierAt(i);
                    if (!(tier.getType().equals("t"))) continue;
                    if (tier.containsEventAtStartPoint(tliN.getID())){
                        try {
                            Event e = tier.getEventAtStartPoint(tliN.getID());
                            totalWidth+=e.getDescription().length();
                            totalCount++;
                        } catch (JexmaraldaException ex) {
                            // should never get here
                        }
                    }
                }
                if (totalCount>0){
                    characterWidths[j] = (double)((double)totalWidth/(double)totalCount);
                } else {
                    characterWidths[j] = 0;
                }

            }

            for (int pos=1; pos<getNumberOfTimelineItems()-1; pos++){
                tli = getTimelineItemAt(pos);
                if (tli.getTime()<0) {
                    String id1 = getPreviousTimeID(tli.getID());
                    String id2 = getNextTimeID(tli.getID());

                    TimelineItem tli1 = getTimelineItemAt(lookupID(id1));
                    TimelineItem tli2 = getTimelineItemAt(lookupID(id2));

                    int index1 = lookupID(id1);
                    int index2 = lookupID(id2);

                    double total = 0;
                    for (int i=index1; i<index2; i++){
                        total+=characterWidths[i];
                    }

                    //System.out.println("TOTAL: " + total);

                    double proportion = 0;
                    for (int i=index1+1; i<index2; i++){
                        tli = getTimelineItemAt(i);
                        proportion += characterWidths[i-1]/total;
                        //System.out.println(characterWidths[i-1] + "-->" + proportion);
                        double time = tli1.getTime()+(tli2.getTime()-tli1.getTime())*proportion;
                        tli.setTime(time);
                        tli.setType("intp");
                    }

                    //System.out.println("=================");

                    pos = index2;
                }
            }
        }
    }
    

    /** remove absolute times that have been interpolated */
    public void removeInterpolatedTimes(){
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            TimelineItem tli = getTimelineItemAt(pos);
            if ((tli.getTime()>=0) && (tli.getType().equals("intp"))){
                tli.setTime(-1);
            }
        }
    }
    
    public void removeTimes() {
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            TimelineItem tli = getTimelineItemAt(pos);
            tli.setTime(-1);
        }
    }
    
    /** gets the nearest timeline item to the left of the one with the specified ID that has an absolute time 
     * returns null if no such item exists */
    String getPreviousTimeID(String id){
        for (int pos=lookupID(id); pos>=0; pos--){
            if (getTimelineItemAt(pos).getTime()>=0){
                return getTimelineItemAt(pos).getID();
            }
        }
        return null;
    }
    
    public double getPreviousTime(String id){
        for (int pos=lookupID(id); pos>=0; pos--){
            double time = getTimelineItemAt(pos).getTime();
            if (time>=0){
                return time;
            }
        }
        return 0;
    }
    
    String getPreviousTimeID(double time){
        for (int pos=getNumberOfTimelineItems()-1; pos>=0; pos--){
            if ((getTimelineItemAt(pos).getTime()>=0) && (getTimelineItemAt(pos).getTime()<=time)){
                return getTimelineItemAt(pos).getID();
            }
        }
        return null;
    }        
    
    String getNextTimeID(String id){
        for (int pos=lookupID(id); pos<getNumberOfTimelineItems(); pos++){
            if (getTimelineItemAt(pos).getTime()>=0){
                return getTimelineItemAt(pos).getID();
            }
        }
        return null;
    }

    public double getNextTime(String id){
        for (int pos=lookupID(id); pos<getNumberOfTimelineItems(); pos++){
            double time = getTimelineItemAt(pos).getTime();
            if (time>=0){
                return time;
            }
        }
        return getMaxTime();
    }

    String getNextTimeID(double time){
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            if ((getTimelineItemAt(pos).getTime()>=0) && (getTimelineItemAt(pos).getTime()>=time)){
                return getTimelineItemAt(pos).getID();
            }
        }
        return null;
    }
    
    public int findTimelineItem(double time, double tolerance){
        // TODO: this would be faster if it used intervalschachtelung
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            double thisTime = getTimelineItemAt(pos).getTime();
            if (Math.abs(thisTime-time)<tolerance) return pos;
        }
        return -1;
        
    }
    
    /** returns the index of the last timeline item whose absolute time value is *
     * not greater than the specified value */
    public int getPositionForTime(double time){
        int a = 0;
        int b = getNumberOfTimelineItems()-1;
        if (time<getTimelineItemAt(0).getTime()) return a;
        if (time>getMaxTime()) return b;
        
        int m = (int)(Math.floor((a+b)/2));
        if (m<0 || m>=getNumberOfTimelineItems()-1) return -1;
        double t1;
        double t2;
        while (!(((t1=getTimelineItemAt(m).getTime())<=time) && (((t2=getTimelineItemAt(m+1).getTime())>=time)))){
            if (t1>time){
                b=m;
            } else{
                a=m;
            }
            m = (int)(Math.floor((a+b)/2));
            if (m<0 || m>=getNumberOfTimelineItems()-1) return -1;            
        }
        return m;
    }

    public String getMaxTimeID(){
        for (int pos=getNumberOfTimelineItems()-1; pos>=0; pos--){
            if (getTimelineItemAt(pos).getTime()>=0){
                return getTimelineItemAt(pos).getID();
            }
        }
        return null;
    }
    
    public double getMaxTime(){
        for (int pos=getNumberOfTimelineItems()-1; pos>=0; pos--){
            if (getTimelineItemAt(pos).getTime()>=0){
                return getTimelineItemAt(pos).getTime();
            }
        }
        return 0;
    }

    public String getMinTimeID(){
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            if (getTimelineItemAt(pos).getTime()>=0){
                return getTimelineItemAt(pos).getID();
            }
        }
        return null;
    }

    // returns true if there is at least one absolute time
    public boolean isVirgin() {
        return (getMinTimeID()==null);
    }


    public void insertAccordingToTime(TimelineItem tli){
        //System.out.println("Trying to insert " + tli.toXML());
        if (tli.getTime()<0) {return;}
        if (getNumberOfTimelineItems()==0){
            try {addTimelineItem(tli);}
            catch (JexmaraldaException je){je.printStackTrace();}
            return;
        }
        String id2 = getNextTimeID(tli.getTime());
        if (id2==null){ // i.e. no tli after
            try {addTimelineItem(tli);}
            catch (JexmaraldaException je){je.printStackTrace();}
            return;
        } else {
            try {insertTimelineItemBefore(id2,tli);}
            catch (JexmaraldaException je){je.printStackTrace();}
        }                
    }
    
    public Vector getAllBookmarks(){
        Vector result = new Vector();
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            TimelineItem tli = getTimelineItemAt(pos);
            if (tli.getBookmark()!=null){
                result.add(tli);
            }
        }
        return result;
    }
    
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns a sequence of XML-Elements lt;tli&lt; as specified in corresponding dtds (without any top level element!)*/
    public String toXML(){
       StringBuffer sb = new StringBuffer();
       for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            sb.append(getTimelineItemAt(pos).toXML());
        }
        return sb.toString();
    } 

    /** returns a sequence of XML-Elements lt;tpr&lt; as specified in corresponding dtds (without any top level element!)*/
    public String toTimepointReferenceXML(){
        String result = new String();
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            result+=getTimelineItemAt(pos).toTimepointReferenceXML();
        }
        return result;
    } 

    
    /** writes a sequence of XML-Elements lt;tpr&lt; to the specified file output stream (without any top level element!)*/
    public void writeTimepointReferenceXML(FileOutputStream fos) throws IOException {
        fos.write(toTimepointReferenceXML().getBytes("UTF-8"));
    }

    /** makes uniform timeline IDs */
    Hashtable normalize(){
        Hashtable mappings = new Hashtable();
        for (int pos=0; pos<getNumberOfTimelineItems(); pos++){
            TimelineItem tli = getTimelineItemAt(pos);
            String oldID = tli.getID();
            String newID = "T" + Integer.toString(pos);
            tli.setID(newID);
            mappings.put(oldID, newID);
        }
        this.updatePositions();
        return mappings;
    }






}