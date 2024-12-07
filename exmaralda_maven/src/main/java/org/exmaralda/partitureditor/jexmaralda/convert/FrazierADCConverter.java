/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Speaker;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;

/**
 *
 * @author thomas.schmidt
 */
public class FrazierADCConverter {
    
    // issue #296
    
    public BasicTranscription readFrazierADCFromFile(File inFile) throws IOException, JexmaraldaException{
        // read json file via Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonRoot = objectMapper.readValue(inFile, JsonNode.class);        
        
        /*{
          "title": "ADC Sample DE",
          "language": "de",
          "created_at": "2019-11-25T09:16:18.949Z",
          "updated_at": "2019-11-25T09:20:51.130Z",
          "cue_points": [
            {
              "start": 2760,
              "end": 5633,
              "text": "Hellrosa Himmel über einer hügeligen Wiese am Rande eines Waldes.",
              "voice": "Hans",
              "speed": 1.0,
              "people": "",
              "locations": "Himmel",
              "things": "",
              "created_at": "2019-11-25T09:16:18.977Z",
              "updated_at": "2019-11-25T09:20:29.753Z"
            }
          ]
        }*/
        JsonNode cuePointsNode = jsonRoot.findValue("cue_points");
        Iterator<JsonNode> iterator = cuePointsNode.elements();
        List<FrazierADCCuePoint> cuePoints = new ArrayList<>();
        while (iterator.hasNext()){
            JsonNode cuePointNode = iterator.next();
            FrazierADCCuePoint cuePoint = new FrazierADCCuePoint(cuePointNode);
            cuePoints.add(cuePoint);            
        }
        
        BasicTranscription result = new BasicTranscription();
        
        Speaker speaker = new Speaker();
        speaker.setID("SPK_0");
        speaker.setAbbreviation("X");
        result.getHead().getSpeakertable().addSpeaker(speaker);
        
        Timeline timeline = new Timeline();
        result.getBody().setCommonTimeline(timeline);
        
        Tier textTier = new Tier("TIE_0", "SPK_0", "v", "t", "[text]");
        result.getBody().addTier(textTier);
        Tier locationTier = new Tier("TIE_1", "SPK_0", "loc", "a", "[locations]");
        result.getBody().addTier(locationTier);
        Tier peopleTier = new Tier("TIE_2", "SPK_0", "peo", "a", "[people]");
        result.getBody().addTier(peopleTier);
        Tier thingsTier = new Tier("TIE_3", "SPK_0", "thi", "a", "[things]");
        result.getBody().addTier(thingsTier);
        
        for (FrazierADCCuePoint cuePoint : cuePoints){
            String id1 = "T_" + Integer.toString(cuePoint.start);
            if (!(timeline.containsTimelineItemWithID(id1))){
                TimelineItem tli1 = new TimelineItem();
                tli1.setID(id1);
                tli1.setTime(cuePoint.start / 1000.0);
                timeline.addTimelineItem(tli1);
            }
            String id2 = "T_" + Integer.toString(cuePoint.end);
            if (!(timeline.containsTimelineItemWithID(id2))){
                TimelineItem tli2 = new TimelineItem();
                tli2.setID(id2);
                tli2.setTime(cuePoint.end / 1000.0);
                timeline.addTimelineItem(tli2);
            }
            
            Event textEvent = new Event(id1, id2, cuePoint.text);
            textTier.addEvent(textEvent);
            
            if (cuePoint.locations!=null && cuePoint.locations.length()>0){
                Event event = new Event(id1, id2, cuePoint.locations);
                locationTier.addEvent(event);
            }
            
            if (cuePoint.people!=null && cuePoint.people.length()>0){
                Event event = new Event(id1, id2, cuePoint.people);
                peopleTier.addEvent(event);
            }

            if (cuePoint.things!=null && cuePoint.things.length()>0){
                Event event = new Event(id1, id2, cuePoint.things);
                thingsTier.addEvent(event);
            }

            
        }
        
        return result;
    }

    
}
