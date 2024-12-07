/*
 * EXMARaLDABasicTranscription.java
 *
 * Created on 20. Maerz 2008, 11:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.experiment.transcription;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;
/**
 *
 * @author thomas
 */
public class EXMARaLDABasicTranscription extends AbstractTierEventTranscription {
    
    BasicTranscription basicTranscription;
    
    /** Creates a new instance of EXMARaLDABasicTranscription */
    public EXMARaLDABasicTranscription(BasicTranscription bt) {
        basicTranscription = bt; 
        Timeline tl = basicTranscription.getBody().getCommonTimeline();
        tl.completeTimes();
        events = new Vector<EventInterface>();
        for (int pos=0; pos<basicTranscription.getBody().getNumberOfTiers(); pos++){
            Tier t = basicTranscription.getBody().getTierAt(pos);
            if (t.getType().equals("t")){
                for (int pos2=0; pos2<t.getNumberOfEvents(); pos2++){
                    org.exmaralda.partitureditor.jexmaralda.Event event = t.getEventAt(pos2);
                    String d = event.getDescription();
                    try {
                        double s = tl.getTimelineItemWithID(event.getStart()).getTime();
                        double e = tl.getTimelineItemWithID(event.getEnd()).getTime();
                        EventInterface ei = new DefaultEvent(d,s,e,
                                            event.getStart(), event.getEnd(), t.getID(), t.getSpeaker());                        
                        events.add(ei);
                    } catch (JexmaraldaException ex) {}                    
                }
            }
        }
        java.util.Collections.sort(events, new EventSorter());
    }

    public int getNumberOfTiers() {
        int count=0;
        for (int pos=0; pos<basicTranscription.getBody().getNumberOfTiers(); pos++){
            Tier t = basicTranscription.getBody().getTierAt(pos);
            if (t.getType().equals("t")) count++;
        }
        return count;
    }

    
    

    public Vector<EventInterface[]> getTierEventData(double startTime, double endTime) {
        Timeline tl = basicTranscription.getBody().getCommonTimeline();
        int i1 = tl.getPositionForTime(startTime);
        int i2 = tl.getPositionForTime(endTime);
        i2 = Math.min(i2+1, tl.getNumberOfTimelineItems()-1);
               
        TimelineItem tli1 = tl.getTimelineItemAt(i1);
        TimelineItem tli2 = tl.getTimelineItemAt(i2);
        
        String[] allTierIDs = basicTranscription.getBody().getAllTierIDs();
        BasicTranscription excerpt = basicTranscription.getPartOfTranscription(allTierIDs, tli1.getID(), tli2.getID());
        
        Vector<EventInterface[]> returnValue = new Vector<EventInterface[]>();
        for (int pos=0; pos<excerpt.getBody().getNumberOfTiers(); pos++){
            Tier t = excerpt.getBody().getTierAt(pos);
            if (!(t.getType().equals("t"))) continue;
            EventInterface[] eventsForThisTier = new EventInterface[t.getNumberOfEvents()];
            for (int pos2=0; pos2<t.getNumberOfEvents(); pos2++){
                org.exmaralda.partitureditor.jexmaralda.Event event = t.getEventAt(pos2);
                String d = event.getDescription();
                try {
                    double s = tl.getTimelineItemWithID(event.getStart()).getTime();
                    double e = tl.getTimelineItemWithID(event.getEnd()).getTime();
                    eventsForThisTier[pos2] = new DefaultEvent(d,s,e,
                            event.getStart(), event.getEnd(), t.getID(), t.getSpeaker());
                } catch (JexmaraldaException ex) {
                    eventsForThisTier[pos2] = null;
                }
            }
            returnValue.addElement(eventsForThisTier);
        }
        
        return returnValue;
    }


    
    
    
}
