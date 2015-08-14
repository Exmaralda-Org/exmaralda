/*
 * Timepoint.java
 *
 * Created on 7. Mai 2008, 11:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class Timepoint {
    
    private Timeline timeline;
    private double time;
    
    /** Creates a new instance of Timepoint */
    public Timepoint(Timeline t, double time) {
        timeline = t;
        this.time = time;
    }

    // pseudo constructor, used by quick aligner
    public Timepoint(double time) {
        this.time = time;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return TimeStringFormatter.formatMiliseconds(time, 2);
    }

    Element toJDOMElement() {
        Element result = new Element("timepoint");
        result.setAttribute("timepoint-id", "TLI_" + Integer.toString(timeline.getTimepoints().indexOf(this)));
        result.setAttribute("absolute-time", Double.toString(getTime()/1000.0));
        return result;
    }
    

    
}
