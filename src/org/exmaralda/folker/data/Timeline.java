/*
 * Timeline.java
 *
 * Created on 7. Mai 2008, 11:10
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
public class Timeline {
    
    public double tolerance = 50;
    //double tolerance = 0;
    
    EventListTranscription transcription;
    Vector<Timepoint> timepoints;    
    TimepointComparator timepointComparator = new TimepointComparator(tolerance);

    double maxTime;
    
    /** Creates a new instance of Timeline */
    public Timeline(EventListTranscription t, double minimumTime, double maximumTime) {        
        transcription = t;
        timepoints = new Vector<Timepoint>();
        timepoints.addElement(new Timepoint(this, minimumTime));
        timepoints.addElement(new Timepoint(this, maximumTime));
        maxTime = maximumTime;
    }
    
    /** Creates a new instance of Timeline */
    public Timeline(EventListTranscription t, double minimumTime, double maximumTime, int timelineTolerance) {        
        tolerance = timelineTolerance;
        timepointComparator = new TimepointComparator(tolerance);
        transcription = t;
        timepoints = new Vector<Timepoint>();
        timepoints.addElement(new Timepoint(this, minimumTime));
        timepoints.addElement(new Timepoint(this, maximumTime));
        maxTime = maximumTime;
    }
    

    public Vector<Timepoint> getTimepoints(){
        return timepoints;
    }
    
    
    public Timepoint getTimepointAt(int index){
        return timepoints.elementAt(index);
    }
    
    /** returns the absolute time of the first point in the timeline */
    public double getMinimumTime(){
        if (timepoints.size()>0){
            return timepoints.elementAt(0).getTime();
        }
        return 0.0;
    }
    
    /** returns the absolute time of the first point in the timeline */
    public double getMaximumTime(){
        if (timepoints.size()>0){
            return timepoints.elementAt(timepoints.size()-1).getTime();
        }
        return 0.0;
    }
        
    /** Searches through the timeline for a timepoint with the specified absolute time, 
     *  using the tolerance value for this timeline. If such a timepoint is found,
     *  it is returned, otherwise null is returned */
    public Timepoint findTimepoint(double time){
        
        // check if the time is inside this timeline's range
        if (time<getMinimumTime()){
            if (time>getMinimumTime()-tolerance) return getTimepointAt(0);
            return null;
        }
        if (time>getMaximumTime()){
            if (time<getMaximumTime()+tolerance) return getTimepointAt(timepoints.size()-1);
            return null;
        }
        
        int index = Collections.binarySearch(timepoints, new Timepoint(null, time), timepointComparator);
        if (index<0) return null;
        return timepoints.elementAt(index);

    }
    
    public Timepoint getPreviousTimepoint(Timepoint tp){
        int index = this.timepoints.indexOf(tp);
        if (index<1) return null;
        return getTimepointAt(index-1);
    }
    
    public Timepoint getNextTimepoint(Timepoint tp){
        int index = this.timepoints.indexOf(tp);
        if (index>this.getTimepoints().size()-2) return null;
        return getTimepointAt(index+1);
    }

    public Timepoint addTimepoint(double time){
        Timepoint tp = findTimepoint(time);
        if (tp==null){
            tp = new Timepoint(this, time);
            timepoints.addElement(tp);
            Collections.sort(timepoints, timepointComparator);            
        }
        return tp;
    }

    void removeUnusedTimepoints(HashSet<Timepoint> usedTimepoints) {
        timepoints.retainAll(usedTimepoints);
    }

    Element toJDOMElement() {
        Element result = new Element("timeline");
        for (Timepoint tp : getTimepoints()){
            Element tpElement = tp.toJDOMElement();
            result.addContent(tpElement);
        }
        return result;
    }

    // returns true if the timepoint is in order with the preceding and the following one
    public boolean checkOrder(Timepoint tp, double time) {
        int index = timepoints.indexOf(tp);
        boolean itHasBeenFound = (index>=0);
        boolean itIsTheFirst = (index==0);
        boolean itIsTheLast = (index==timepoints.size()-1);
        boolean thePrecedingIsEarlier = (itHasBeenFound && (itIsTheFirst || (timepoints.elementAt(index-1).getTime()<time)));
        boolean theFollowingIsLater = (itHasBeenFound && (itIsTheLast || (timepoints.elementAt(index+1).getTime()>time)));
        return (thePrecedingIsEarlier && theFollowingIsLater);
    }

    public void reorder() {
        Collections.sort(timepoints, timepointComparator);
    }


    
}
