/*
 * Event.java
 *
 * Created on 7. Mai 2008, 11:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class Event implements TimeAssigned, SpeakerAssigned {
    
    private Timepoint startpoint;
    private Timepoint endpoint;
    private String text;
    private Speaker speaker;
    
    public Event(){        
    }
    
    /** Creates a new instance of Event */
    public Event(Timepoint sp, Timepoint ep, String t, Speaker s) {
        startpoint = sp;
        endpoint = ep;
        text = t;
        speaker = s;
    }

    public Timepoint getStartpoint() {
        return startpoint;
    }

    public void setStartpoint(Timepoint startpoint) {
        this.startpoint = startpoint;
    }

    public Timepoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Timepoint endpoint) {
        this.endpoint = endpoint;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public void setSpeaker(Speaker s) {
        speaker = s;
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public boolean overlaps(Event otherEvent){
        double s1 = otherEvent.getStartpoint().getTime();
        double e1 = otherEvent.getEndpoint().getTime();
        double s2 = this.getStartpoint().getTime();
        double e2 = this.getEndpoint().getTime();


        //   <s1>-------<e1>
        //         <s2>--------<e2>
        boolean b1 = (s1<=s2) && (s2<e1);
        //          <s1>-------<e1>
        //   <s2>--------<e2>
        boolean b2 = (s1<e2) && (e2<=e1);
        //   <s1>--------<e1>
        //   <s2>--------<e2>
        boolean b3 = (s1==s2) && (e1==e2);
        //      <s1>-------<e1>
        // <s2>-------------------<e2>
        boolean b4 = (s2<s1) && (e1<e2);

        return (b1||b2||b3||b4);

    }

    public Element toJDOMElement(Timeline timeline){
        Element eventElement = new Element("segment");
        eventElement.setAttribute("start-reference", "TLI_" + Integer.toString(timeline.getTimepoints().indexOf(getStartpoint())));
        eventElement.setAttribute("end-reference", "TLI_" + Integer.toString(timeline.getTimepoints().indexOf(getEndpoint())));
        //eventElement.setAttribute("start-reference", "T" + Integer.toString(timeline.getTimepoints().indexOf(getStartpoint())));
        //eventElement.setAttribute("end-reference", "T" + Integer.toString(timeline.getTimepoints().indexOf(getEndpoint())));
        eventElement.setText(getText());
        return eventElement;
    }

    
}
