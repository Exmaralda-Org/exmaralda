/*
 * EventSearchResult.java
 *
 * Created on 13. Juni 2003, 12:46
 */

package org.exmaralda.partitureditor.search;

import org.exmaralda.partitureditor.jexmaralda.*;
/**
 *
 * @author  thomas
 */
public class EventSearchResult {
    
    
    public String tierID;
    public String tierName;
    public int offset;
    public int length;
    public org.exmaralda.partitureditor.jexmaralda.Event event;
    
    /** Creates a new instance of EventSearchResult */
    public EventSearchResult() {
    }
    
    public String toString(){
        String result = tierName + "\t" + tierID + "\t" + Integer.toString(offset) + "\t" + Integer.toString(length) + "\t";
        result+= event.getDescription() + "\t" + event.getStart() + "\t" + event.getEnd();
        return result;
    }
    
}
