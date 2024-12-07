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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author  thomas
 */
public class EventSearcher {
    
    /** Creates a new instance of EventSearcher */
    public EventSearcher() {
    }
    
    // changed for issue #399
    public static Vector search(String searchString, BasicTranscription transcription, 
                                String[] tierIDs, String startTLI, String endTLI, 
                                boolean caseSensitive) throws JexmaraldaException {
        return search(searchString, transcription, tierIDs, startTLI, endTLI, caseSensitive, false);
    }
    
    
    // changed for issue #399
    public static Vector search(String searchString, BasicTranscription transcription, 
                                String[] tierIDs, String startTLI, String endTLI, 
                                boolean caseSensitive,
                                boolean regex
    ) throws JexmaraldaException {
        Vector result = new Vector();        
        if (!regex){
            if (!caseSensitive){
                searchString = searchString.toLowerCase();
            }
            Timeline timeline = transcription.getBody().getCommonTimeline();
            int start = timeline.lookupID(startTLI);
            int end = timeline.lookupID(endTLI);
            for (String tierID : tierIDs) {
                Tier tier = transcription.getBody().getTierWithID(tierID);
                for (int pos2 = 0; pos2<tier.getNumberOfEvents(); pos2++) {
                    Event event = (Event)(tier.elementAt(pos2));
                    int position = timeline.lookupID(event.getStart());
                    if ((position >= start) && (position<=end)) {
                        int startSearch = 0;
                        String desc = event.getDescription();
                        if (!caseSensitive){
                            desc = desc.toLowerCase();
                        }
                        while (desc.indexOf(searchString, startSearch)>-1) {
                            EventSearchResult esr = new EventSearchResult();
                            esr.tierID = tierID;
                            esr.event = event;
                            esr.offset = desc.indexOf(searchString, startSearch);
                            esr.length = searchString.length();
                            esr.tierName = tier.getDescription(transcription.getHead().getSpeakertable());
                            result.addElement(esr);
                            startSearch = esr.offset + 1;
                        }
                    }
                }
            }
        } else {
            // new for issue #399
            Timeline timeline = transcription.getBody().getCommonTimeline();
            int start = timeline.lookupID(startTLI);
            int end = timeline.lookupID(endTLI);
            Pattern pattern = Pattern.compile(searchString);
            for (String tierID : tierIDs) {
                Tier tier = transcription.getBody().getTierWithID(tierID);
                for (int pos2 = 0; pos2<tier.getNumberOfEvents(); pos2++) {
                    Event event = (Event)(tier.elementAt(pos2));
                    int position = timeline.lookupID(event.getStart());
                    if ((position >= start) && (position<=end)) {
                        String desc = event.getDescription();
                        Matcher matcher = pattern.matcher(desc);
                        while (matcher.find()) {
                            EventSearchResult esr = new EventSearchResult();
                            esr.tierID = tierID;
                            esr.event = event;
                            esr.offset = matcher.start();
                            esr.length = matcher.end() - matcher.start();
                            esr.tierName = tier.getDescription(transcription.getHead().getSpeakertable());
                            result.addElement(esr);
                        }
                    }
                }
            }
        }
        return result;
    }

    // changed for issue #399
    public static Vector search(String searchString, BasicTranscription transcription, String[] tierIDs, boolean caseSensitive) throws JexmaraldaException {
        return search(searchString, transcription, tierIDs, caseSensitive, false);
    }
    
    // changed for issue #399
    public static Vector search(String searchString, BasicTranscription transcription, String[] tierIDs, boolean caseSensitive, boolean regex) throws JexmaraldaException {
        Timeline timeline = transcription.getBody().getCommonTimeline();
        String startTLI = timeline.getTimelineItemAt(0).getID();
        String endTLI = timeline.getTimelineItemAt(timeline.getNumberOfTimelineItems()-1).getID();
        
        return search(searchString, transcription, tierIDs, startTLI, endTLI, caseSensitive, regex);
    }

    public static Vector search(String searchString, BasicTranscription transcription, boolean caseSensitive)throws JexmaraldaException {
        String[] tierIDs = transcription.getBody().getAllTierIDs();
        Timeline timeline = transcription.getBody().getCommonTimeline();
        String startTLI = timeline.getTimelineItemAt(0).getID();
        String endTLI = timeline.getTimelineItemAt(timeline.getNumberOfTimelineItems()-1).getID();
        
        return search(searchString, transcription, tierIDs, startTLI, endTLI, caseSensitive);
    }
    
}
