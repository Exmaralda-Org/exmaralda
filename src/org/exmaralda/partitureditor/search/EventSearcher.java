/*
 * EventSearcher.java
 *
 * Created on 13. Juni 2003, 12:49
 */

package org.exmaralda.partitureditor.search;

import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;

/**
 *
 * @author  thomas
 */
public class EventSearcher {
    
    /** Creates a new instance of EventSearcher */
    public EventSearcher() {
    }
    
    public static Vector search(String searchString, BasicTranscription transcription, 
                                String[] tierIDs, String startTLI, String endTLI, 
                                boolean caseSensitive) throws JexmaraldaException {
        Vector result = new Vector();
        if (!caseSensitive){
            searchString = searchString.toLowerCase();
        }
        Timeline timeline = transcription.getBody().getCommonTimeline();
        int start = timeline.lookupID(startTLI);
        int end = timeline.lookupID(endTLI);
        for (int pos=0; pos<tierIDs.length; pos++){
            Tier tier = transcription.getBody().getTierWithID(tierIDs[pos]);
            for (int pos2=0; pos2<tier.getNumberOfEvents(); pos2++){
                Event event = (Event)(tier.elementAt(pos2));
                int position = timeline.lookupID(event.getStart());
                if ((position >= start) && (position<=end)){
                    int startSearch = 0;
                    String desc = event.getDescription();
                    if (!caseSensitive){
                        desc = desc.toLowerCase();
                    }
                    while (desc.indexOf(searchString, startSearch)>-1){
                        EventSearchResult esr = new EventSearchResult();
                        esr.tierID = tierIDs[pos];
                        esr.event = event;
                        esr.offset = desc.indexOf(searchString, startSearch);
                        esr.length = searchString.length();
                        esr.tierName = tier.getDescription(transcription.getHead().getSpeakertable());
                        result.addElement(esr);
                        //startSearch+=1;
                        //startSearch+=esr.offset + searchString.length();
                        startSearch = esr.offset + 1;
                    }
                }
            }
        }
        return result;
    }
    
    public static Vector search(String searchString, BasicTranscription transcription, String[] tierIDs, boolean caseSensitive) throws JexmaraldaException {
        Timeline timeline = transcription.getBody().getCommonTimeline();
        String startTLI = timeline.getTimelineItemAt(0).getID();
        String endTLI = timeline.getTimelineItemAt(timeline.getNumberOfTimelineItems()-1).getID();
        
        return search(searchString, transcription, tierIDs, startTLI, endTLI, caseSensitive);
    }

    public static Vector search(String searchString, BasicTranscription transcription, boolean caseSensitive)throws JexmaraldaException {
        String[] tierIDs = transcription.getBody().getAllTierIDs();
        Timeline timeline = transcription.getBody().getCommonTimeline();
        String startTLI = timeline.getTimelineItemAt(0).getID();
        String endTLI = timeline.getTimelineItemAt(timeline.getNumberOfTimelineItems()-1).getID();
        
        return search(searchString, transcription, tierIDs, startTLI, endTLI, caseSensitive);
    }
    
}
