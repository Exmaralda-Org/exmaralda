/*
 * EventWrapper.java
 *
 * Created on 1. Juli 2008, 15:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

/**
 *
 * @author thomas
 */
public class EventWrapper {
    
    private org.exmaralda.partitureditor.jexmaralda.Event exmaraldaEvent;
    private org.exmaralda.folker.data.Event folkerEvent;
    
    /** Creates a new instance of EventWrapper */
    public EventWrapper() {
    }
        
    public void setExmaraldaEvent(org.exmaralda.partitureditor.jexmaralda.Event e){
        exmaraldaEvent = e;        
    }
    
    public void setFolkerEvent(org.exmaralda.folker.data.Event e){
        folkerEvent = e;        
    }

    public org.exmaralda.partitureditor.jexmaralda.Event getExmaraldaEvent() {
        return exmaraldaEvent;
    }

    public org.exmaralda.folker.data.Event getFolkerEvent() {
        return folkerEvent;
    }

}
