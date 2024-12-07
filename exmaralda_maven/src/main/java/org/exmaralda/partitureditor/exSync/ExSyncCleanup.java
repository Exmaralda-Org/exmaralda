/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.exSync;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;

/**
 *
 * @author thomas
 */
public class ExSyncCleanup {

    BasicTranscription transcription;

    public ExSyncCleanup(BasicTranscription transcription) {
        this.transcription = transcription;
    }

    public BasicTranscription getTranscription() {
        return transcription;
    }

    public int replaceNonBreakingSpace(){
        int count = 0;
        for (int pos=0; pos<transcription.getBody().getNumberOfTiers(); pos++){
            Tier t = transcription.getBody().getTierAt(pos);
            for (int i=0; i<t.getNumberOfEvents(); i++){
                Event e = t.getEventAt(i);
                String newDescription = e.getDescription().replaceAll("\\u00A0", "\u02D9");
                if (!e.getDescription().equals(newDescription)){
                    e.setDescription(newDescription);
                    count++;
                }
            }
        }
        return count;
    }

    public int replaceEllipsisDots(){
        int count = 0;
        for (int pos=0; pos<transcription.getBody().getNumberOfTiers(); pos++){
            Tier t = transcription.getBody().getTierAt(pos);
            for (int i=0; i<t.getNumberOfEvents(); i++){
                Event e = t.getEventAt(i);
                String newDescription = e.getDescription().replaceAll("(\\.){3,3}", "\u2026");
                if (!e.getDescription().equals(newDescription)){
                    e.setDescription(newDescription);
                    count++;
                }
            }
        }
        return count;
    }


    public int moveIsolatedPunctuation() throws JexmaraldaException{
        int count = 0;
        String PUNCTUATION = "[\\.!\\?\\u2026\\u02D9\\u0022\\u005B\\u005D: ]+";
        for (int pos=0; pos<transcription.getBody().getNumberOfTiers(); pos++){
            Tier t = transcription.getBody().getTierAt(pos);
            if (!("t".equals(t.getType()))) continue;
            Timeline tl = transcription.getBody().getCommonTimeline();
            for (int i=0; i<tl.getNumberOfTimelineItems(); i++){
                TimelineItem tli = tl.getTimelineItemAt(i);
                if (t.containsEventAtStartPoint(tli.getID())){
                    Event event = t.getEventAtStartPoint(tli.getID());
                    if (event.getDescription().matches(PUNCTUATION)){
                        Event previousEvent = t.getFirstEventBeforeStartPoint(tl, tli.getID());
                        if ((previousEvent!=null) && (previousEvent.getEnd().equals(event.getStart()))){
                            previousEvent.setDescription(previousEvent.getDescription() + event.getDescription());
                            t.removeEventAtStartPoint(tli.getID());
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    public int moveLigature() throws JexmaraldaException{
        int count = 0;
        for (int pos=0; pos<transcription.getBody().getNumberOfTiers(); pos++){
            Tier t = transcription.getBody().getTierAt(pos);
            if (!("t".equals(t.getType()))) continue;
            Timeline tl = transcription.getBody().getCommonTimeline();
            for (int i=0; i<tl.getNumberOfTimelineItems(); i++){
                TimelineItem tli = tl.getTimelineItemAt(i);
                if (t.containsEventAtStartPoint(tli.getID())){
                    Event event = t.getEventAtStartPoint(tli.getID());
                    if (event.getDescription().endsWith("\u203F")){
                        Event followingEvent = t.getFirstEventAfterStartPoint(tl, tli.getID());
                        if ((followingEvent!=null) && (followingEvent.getStart().equals(event.getEnd()))){
                            followingEvent.setDescription("\u203F" + followingEvent.getDescription());
                            event.setDescription(event.getDescription().substring(0, event.getDescription().length()-1));
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    public int moveInitialColons() throws JexmaraldaException{
        int count = 0;
        for (int pos=0; pos<transcription.getBody().getNumberOfTiers(); pos++){
            Tier t = transcription.getBody().getTierAt(pos);
            if (!("t".equals(t.getType()))) continue;
            Timeline tl = transcription.getBody().getCommonTimeline();
            for (int i=0; i<tl.getNumberOfTimelineItems(); i++){
                TimelineItem tli = tl.getTimelineItemAt(i);
                if (t.containsEventAtStartPoint(tli.getID())){
                    Event event = t.getEventAtStartPoint(tli.getID());
                    if (event.getDescription().startsWith(":")){
                        Event previousEvent = t.getFirstEventBeforeStartPoint(tl, tli.getID());
                        if ((previousEvent!=null) && (previousEvent.getEnd().equals(event.getStart()))){
                            previousEvent.setDescription(previousEvent.getDescription() + " ");
                            event.setDescription(event.getDescription().substring(1));
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }


    public int moveInitialSpaces() throws JexmaraldaException{
        int count = 0;
        for (int pos=0; pos<transcription.getBody().getNumberOfTiers(); pos++){
            Tier t = transcription.getBody().getTierAt(pos);
            if (!("t".equals(t.getType()))) continue;
            Timeline tl = transcription.getBody().getCommonTimeline();
            for (int i=0; i<tl.getNumberOfTimelineItems(); i++){
                TimelineItem tli = tl.getTimelineItemAt(i);
                if (t.containsEventAtStartPoint(tli.getID())){
                    Event event = t.getEventAtStartPoint(tli.getID());
                    if (event.getDescription().startsWith(" ")){
                        Event previousEvent = t.getFirstEventBeforeStartPoint(tl, tli.getID());
                        if ((previousEvent!=null) && (previousEvent.getEnd().equals(event.getStart()))){
                            previousEvent.setDescription(previousEvent.getDescription() + " ");
                            int index = 0;
                            for (char c : event.getDescription().toCharArray()){
                                if (c!=' ') break;
                                index++;
                            }
                            event.setDescription(event.getDescription().substring(index));
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    public int replaceIncomprehensible() throws JexmaraldaException{
        int count = 0;
        for (int pos=0; pos<transcription.getBody().getNumberOfTiers(); pos++){
            Tier t = transcription.getBody().getTierAt(pos);
            if (!("t".equals(t.getType()))) continue;
            Timeline tl = transcription.getBody().getCommonTimeline();
            for (int i=0; i<tl.getNumberOfTimelineItems(); i++){
                TimelineItem tli = tl.getTimelineItemAt(i);
                if (t.containsEventAtStartPoint(tli.getID())){
                    Event event = t.getEventAtStartPoint(tli.getID());
                    if (event.getDescription().trim().startsWith(")")){
                        Event previousEvent = t.getFirstEventBeforeStartPoint(tl, tli.getID());
                        if ((previousEvent!=null) && (previousEvent.getEnd().equals(event.getStart()))){
                            if (previousEvent.getDescription().trim().endsWith("(")){
                                Event combinedEvent = new Event();
                                combinedEvent.setDescription(previousEvent.getDescription() + event.getDescription());
                                combinedEvent.setStart(previousEvent.getStart());
                                combinedEvent.setEnd(event.getEnd());
                                t.removeEventAtStartPoint(event.getStart());
                                t.removeEventAtStartPoint(previousEvent.getStart());
                                t.addEvent(combinedEvent);
                            }
                        }
                    }
                }
            }
        }

        for (int pos=0; pos<transcription.getBody().getNumberOfTiers(); pos++){
            Tier t = transcription.getBody().getTierAt(pos);
            for (int i=0; i<t.getNumberOfEvents(); i++){
                Event e = t.getEventAt(i);
                String newDescription = e.getDescription().replaceAll("(?<!\\()\\( +\\)(?!\\))", "((unverständlich))");
                if (!e.getDescription().equals(newDescription)){
                    e.setDescription(newDescription);
                    count++;
                }
            }
        }
        return count;
    }

    public int splitAtUtteranceEndSymbols() throws JexmaraldaException{
        int count=0;
        String PUNCTUATION = "[\\.!\\?\\u2026\\u02D9](\\u0022[\\.!\\?\\u2026\\u02D9]?)? *";
        Pattern p = Pattern.compile(PUNCTUATION);

        for (int pos=0; pos<transcription.getBody().getNumberOfTiers(); pos++){
            Tier t = transcription.getBody().getTierAt(pos);
            Timeline tl = transcription.getBody().getCommonTimeline();
            for (int i=0; i<t.getNumberOfEvents(); i++){
                Event e = t.getEventAt(i);
                if ((tl.lookupID(e.getEnd()) - tl.lookupID(e.getStart()))>1) continue;
                Matcher m = p.matcher(e.getDescription());
                if (m.find() && m.end()<e.getDescription().length()){
                    String newTLI = tl.insertTimelineItemAfter(e.getStart());
                    Event e1 = new Event();
                    e1.setStart(e.getStart());
                    e1.setEnd(newTLI);
                    e1.setDescription(e.getDescription().substring(0,m.end()));
                    Event e2 = new Event();
                    e2.setStart(newTLI);
                    e2.setEnd(e.getEnd());
                    e2.setDescription(e.getDescription().substring(m.end()));
                    t.removeEventAtStartPoint(e.getStart());
                    t.addEvent(e1);
                    t.addEvent(e2);
                    t.sort(tl);
                    count++;
                }
            }
        }

        return count;
    }

    public int normalizeWhitespace(){
        int count = 0;
        for (int pos=0; pos<transcription.getBody().getNumberOfTiers(); pos++){
            Tier t = transcription.getBody().getTierAt(pos);
            for (int i=0; i<t.getNumberOfEvents(); i++){
                Event e = t.getEventAt(i);
                String newDescription = e.getDescription().replaceAll("\\s", " ");
                if (!e.getDescription().equals(newDescription)){
                    e.setDescription(newDescription);
                    count++;
                }
            }
        }
        return count;

    }

}
