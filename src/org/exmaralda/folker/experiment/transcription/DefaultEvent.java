/*
 * Event.java
 *
 * Created on 20. Maerz 2008, 11:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.experiment.transcription;

import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class DefaultEvent implements EventInterface {
    
    String description;
    double startTime;
    double endTime;
    String startID;
    String endID;
    String tierID;
    String speakerID;
    
    /** Creates a new instance of Event */
    public DefaultEvent(String d, double s, double e,
                        String sID, String eID, String tID, String spID) {
        description = d;
        startTime = s;
        endTime = e;
        startID = sID;
        endID = eID;
        tierID = tID;
        speakerID = spID;
    }

    public String getDescription() {
        return description;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public String getStartID() {
        return startID;
    }

    public String getEndID() {
        return endID;
    }

    public String getTierID() {
        return tierID;
    }

    public String getSpeakerID() {
        return speakerID;
    }

    public void setDescription(String d) {
        description = d;
    }

    public void setSpeakerID(String sID) {
        speakerID = sID;
    }

    public boolean contains(double seconds) {
        return ((seconds>=startTime)&&(seconds<=endTime));
    }

    public Element toXMLElement() {
        Element e = new Element("event");
        e.setAttribute("start-time", Double.toString(startTime));
        e.setAttribute("end-time", Double.toString(endTime));
        e.setAttribute("start-ID", startID);
        e.setAttribute("end-ID", endID);
        e.setAttribute("speaker-ID", speakerID);
        e.setAttribute("tier-ID", tierID);
        e.setText(description);
        return e;
    }
    
}
