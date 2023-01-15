/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Speaker;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;

/**
 *
 * @author bernd
 */
// issue #358
public class AmberscriptJSONConverter {
    
    
    
    public static BasicTranscription readAmberscriptJSON(File jsonFile) throws IOException, JexmaraldaException{
        // read json file via Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonRoot = objectMapper.readValue(jsonFile, JsonNode.class);        
        
        BasicTranscription result = new BasicTranscription();
        
        JsonNode speakersNode = jsonRoot.findValue("speakers");
        if (speakersNode==null){
            throw new IOException("Error in format. ");
        }
        Iterator<JsonNode> iterator = speakersNode.elements();
        while (iterator.hasNext()){
            JsonNode speakerNode = iterator.next();
            String speakerID = speakerNode.get("spkid").asText();
            String name = speakerNode.get("name").asText();
            Speaker speaker = new Speaker();
            speaker.setID(speakerID);
            speaker.setAbbreviation(name);
            result.getHead().getSpeakertable().addSpeaker(speaker);
            Tier textTier = new Tier("TIE_" + speakerID + "_TEXT", speakerID, "v", "t", name + " [text]");
            Tier confidenceTier = new Tier("TIE_" + speakerID + "_CONF", speakerID, "conf", "a", name + " [confidence]");
            Tier pristineTier = new Tier("TIE_" + speakerID + "_PRIST", speakerID, "prist", "a", name +" [pristine]");
            result.getBody().addTier(textTier);
            result.getBody().addTier(confidenceTier);
            result.getBody().addTier(pristineTier);
        }
        
        
        /*
            {
              "id": "5c9e45057103e464a4c6f477",
              "recordId": "RECORD_ID",
              "filename": "FILENAME",
              "startTimeOffset": 0.0,
              "speakers": [
                {
                  "spkid": "spk1",
                  "name": "Speaker 1"
                }
              ],
              "segments": [
                {
                  "speaker": "spk1",
                  "words": [
                    {
                      "start": 0.45,
                      "end": 1.08,
                      "duration": 0.63000005,
                      "text": "Hi,",
                      "conf": 1.0,
                      "pristine": true
                    },
                    {
                      "start": 1.11,
                      "end": 1.65,
                      "duration": 0.53999996,
                      "text": "welcome",
                      "conf": 1.0,
                      "pristine": true
                    },
                    {
                      "start": 1.65,
                      "end": 1.8,
                      "duration": 0.14999998,
                      "text": "to",
                      "conf": 1.0,
                      "pristine": true
                    },
                    {
                      "start": 1.8,
                      "end": 2.1,
                      "duration": 0.29999995,
                      "text": "Amberscript",
                      "conf": 0.65,
                      "pristine": true
                    }
                  ]
                }
              ],
              "highlights": []
            }        
        */
        
        JsonNode segmentsNode = jsonRoot.findValue("segments");
        Iterator<JsonNode> iterator2 = segmentsNode.elements();
        Timeline timeline = result.getBody().getCommonTimeline();
        while (iterator2.hasNext()){
            JsonNode segmentNode = iterator2.next();
            String speakerID = segmentNode.get("speaker").asText();
            
            Iterator<JsonNode> iterator3 = segmentNode.get("words").elements();
            while (iterator3.hasNext()){
                JsonNode wordNode = iterator3.next();
                double startTimeInSeconds = wordNode.get("start").asDouble();
                double endTimeInSeconds = wordNode.get("end").asDouble();
                String text = wordNode.get("text").asText();
                String confidence = wordNode.get("conf").asText();
                String pristine = wordNode.get("pristine").asText();

                String startID = "TLI_" + Double.toString(startTimeInSeconds).replace('.', '_');
                if (!(timeline.containsTimelineItemWithID(startID))){
                    TimelineItem tli = new TimelineItem();
                    tli.setID(startID);
                    tli.setTime(startTimeInSeconds);
                    timeline.insertAccordingToTime(tli);
                }
            
                String endID = "TLI_" + Double.toString(endTimeInSeconds).replace('.', '_');
                if (!(timeline.containsTimelineItemWithID(endID))){
                    TimelineItem tli = new TimelineItem();
                    tli.setID(endID);
                    tli.setTime(endTimeInSeconds);
                    timeline.insertAccordingToTime(tli);
                }
            
                String textTierID = "TIE_" + speakerID + "_TEXT";
                String confTierID = "TIE_" + speakerID + "_CONF";
                String pristTierID = "TIE_" + speakerID + "_PRIST";
                result.getBody().getTierWithID(textTierID).addEvent(new Event(startID, endID, text + " "));
                result.getBody().getTierWithID(confTierID).addEvent(new Event(startID, endID, confidence));
                result.getBody().getTierWithID(pristTierID).addEvent(new Event(startID, endID, pristine));
            }            
        }
        
        
        
        
        return result;
        
        
    }
    
}
