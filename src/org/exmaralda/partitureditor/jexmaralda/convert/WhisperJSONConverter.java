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
import org.exmaralda.common.helpers.Rounder;
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
// issue #357
public class WhisperJSONConverter {
    
    
    
    public static BasicTranscription readWhisperJSON(File jsonFile) throws IOException, JexmaraldaException{
        // read json file via Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonRoot = objectMapper.readValue(jsonFile, JsonNode.class);        
        
        BasicTranscription result = new BasicTranscription();
        Speaker speaker = new Speaker();
        speaker.setID("SPK0");
        speaker.setAbbreviation("X");
        result.getHead().getSpeakertable().addSpeaker(speaker);
        Tier textTier = new Tier("TIE0", "SPK0", "v", "t", "X [text]");
        Tier temperatureTier = new Tier("TIE1", "SPK0", "temp", "a", "X [temperature]");
        Tier avgLogProbTier = new Tier("TIE2", "SPK0", "avg", "a", "X [avg_logprob]");
        Tier compressionRatioTier = new Tier("TIE3", "SPK0", "cr", "a", "X [compression_ratio]");
        Tier noSpeechProbTier = new Tier("TIE4", "SPK0", "nsp", "a", "X [no_speech_prob]");
        result.getBody().addTier(textTier);
        result.getBody().addTier(temperatureTier);
        result.getBody().addTier(avgLogProbTier);
        result.getBody().addTier(compressionRatioTier);
        result.getBody().addTier(noSpeechProbTier);
        
        
        /*
            {
                "text": 
                " Abscheu und Zorn über folternde GIs im Irak, die US-Armee soll schon länger Bescheid gewusst",
                "segments": [
                    {
                        "id": 0,
                        "seek": 0,
                        "start": 0.0,
                        "end": 13.44,
                        "text": " Abscheu und Zorn über folternde GIs im Irak, die US-Armee soll schon länger Bescheid gewusst",
                        "tokens": [
                            5813,
                            1876,
                            ...
                            327,
                            6906,
                            26340
                        ],
                        "temperature": 0.0,
                        "avg_logprob": -0.20706645302150561,
                        "compression_ratio": 1.2619047619047619,
                        "no_speech_prob": 0.2456832379102707
                    }
                ]
            }        
        
        */
        
        JsonNode segmentsNode = jsonRoot.findValue("segments");
        Iterator<JsonNode> iterator = segmentsNode.elements();
        Timeline timeline = result.getBody().getCommonTimeline();
        while (iterator.hasNext()){
            JsonNode segmentNode = iterator.next();
            if (segmentNode.get("start")==null){
                throw new IOException("Error in format.");
            }
            // round to miliseconds to avoid overlaps which aren't overlaps
            double startTimeInSeconds = Rounder.round(segmentNode.get("start").asDouble(),3);
            double endTimeInSeconds = Rounder.round(segmentNode.get("end").asDouble(),3);
            String text = segmentNode.get("text").asText();
            String temperature = segmentNode.get("temperature").asText();
            String avg_logprob = segmentNode.get("avg_logprob").asText();
            String compression_ratio = segmentNode.get("compression_ratio").asText();
            String no_speech_prob = segmentNode.get("no_speech_prob").asText();
            
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
            
            textTier.addEvent(new Event(startID, endID, text));
            temperatureTier.addEvent(new Event(startID, endID, temperature));
            avgLogProbTier.addEvent(new Event(startID, endID, avg_logprob));
            compressionRatioTier.addEvent(new Event(startID, endID, compression_ratio));
            noSpeechProbTier.addEvent(new Event(startID, endID, no_speech_prob));
            
        }
        
        
        
        
        return result;
        
        
    }
    
}
