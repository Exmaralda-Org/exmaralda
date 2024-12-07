/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        return readWhisperJSON(jsonFile, true);
    }

    // 24-06-2024, revised for WhisperX
    public static BasicTranscription readWhisperJSON(File jsonFile, boolean wantsWords) throws IOException, JexmaraldaException{
        
        Map<String, List<Tier>> speakersToTiers = new HashMap<>();    

        // read json file via Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonRoot = objectMapper.readValue(jsonFile, JsonNode.class);        
        
        BasicTranscription result = new BasicTranscription();
        
        Speaker dummySpeaker = new Speaker();
        dummySpeaker.setID("SPK0");
        dummySpeaker.setAbbreviation("X");
        result.getHead().getSpeakertable().addSpeaker(dummySpeaker);
        
        boolean hasWordLevel = (jsonRoot.findValue("words")!=null);

        speakersToTiers.put(null, makeTiersForSpeaker(dummySpeaker, result, hasWordLevel, wantsWords));
        
        
        
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
        if (segmentsNode==null){
            String message = "Expected segments node not found. Format may be wrong.";
            throw new IOException(message);            
        }
        Iterator<JsonNode> iterator = segmentsNode.elements();
        Timeline timeline = result.getBody().getCommonTimeline();
        while (iterator.hasNext()){
            JsonNode segmentNode = iterator.next();
            JsonNode textNode = segmentNode.get("text");
            if (textNode==null){
                String message = "Expected text node not found. Format may be wrong.";
                throw new IOException(message);
            }
            String text = textNode.asText();
            
            if (text.trim().length()==0){
                // ignore empty text nodes
                continue;
            }
            
            if (segmentNode.get("start")==null){
                throw new IOException("Error in format : start node missing");
            }
            // round to miliseconds to avoid overlaps which aren't overlaps
            double startTimeInSeconds = Rounder.round(segmentNode.get("start").asDouble(),3);
            double endTimeInSeconds = Rounder.round(segmentNode.get("end").asDouble(),3);
            
            // this can happen, although it shouldn't
            if (startTimeInSeconds > endTimeInSeconds){
                double reminder = startTimeInSeconds;
                startTimeInSeconds = endTimeInSeconds;
                endTimeInSeconds = reminder;
            }

            // this can happen, although it shouldn't
            if (startTimeInSeconds == endTimeInSeconds){
                endTimeInSeconds += 0.001;
            }
            
            
            // 24-06-2024 : changed for WhisperX
            JsonNode temperatureNode = segmentNode.get("temperature");
            String temperature = "";
            if (temperatureNode!=null){
                temperature = temperatureNode.asText();
            }
            JsonNode avg_logprobNode = segmentNode.get("avg_logprob");
            String avg_logprob = "";
            if (avg_logprobNode!=null){
                avg_logprob = avg_logprobNode.asText();
            }
            JsonNode compression_ratioNode = segmentNode.get("compression_ratio");
            String compression_ratio = "";
            if (compression_ratioNode!=null){
                compression_ratio = compression_ratioNode.asText();
            }
            JsonNode no_speech_probNode = segmentNode.get("no_speech_prob");
            String no_speech_prob = "";
            if (no_speech_probNode!=null){
                no_speech_prob = no_speech_probNode.asText();
            }


            
            
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
            
            String segmentSpeaker = null;
            JsonNode speakerNode = segmentNode.get("speaker");
            if (speakerNode!=null){
                segmentSpeaker = speakerNode.asText();
            }
            
            if(!(speakersToTiers.containsKey(segmentSpeaker))){
                Speaker speaker = new Speaker();
                speaker.setID(result.getHead().getSpeakertable().getFreeID());
                speaker.setAbbreviation(segmentSpeaker);
                result.getHead().getSpeakertable().addSpeaker(speaker);
                speakersToTiers.put(segmentSpeaker, makeTiersForSpeaker(speaker, result, hasWordLevel, wantsWords));
            }
            
            List<Tier> tiersForThisSpeaker = speakersToTiers.get(segmentSpeaker);
            
            Tier textTier = tiersForThisSpeaker.get(0);
            Tier temperatureTier = tiersForThisSpeaker.get(1);
            Tier avgLogProbTier = tiersForThisSpeaker.get(2);
            Tier compressionRatioTier = tiersForThisSpeaker.get(3);
            Tier noSpeechProbTier = tiersForThisSpeaker.get(4);
            Tier scoreTier = tiersForThisSpeaker.get(5);
            Tier wordTier = tiersForThisSpeaker.get(6);
            
            
            textTier.addEvent(new Event(startID, endID, text));
            temperatureTier.addEvent(new Event(startID, endID, temperature));
            avgLogProbTier.addEvent(new Event(startID, endID, avg_logprob));
            compressionRatioTier.addEvent(new Event(startID, endID, compression_ratio));
            noSpeechProbTier.addEvent(new Event(startID, endID, no_speech_prob));
            
            if (hasWordLevel){
                JsonNode wordsNode = segmentNode.findValue("words");
                Iterator<JsonNode> wordIterator = wordsNode.elements();
                while (wordIterator.hasNext()){
                    JsonNode wordNode = wordIterator.next();
                    if (wordNode.get("start")==null){
                        throw new IOException("Error in format.");
                    }

                    // round to miliseconds to avoid overlaps which aren't overlaps
                    double wStartTimeInSeconds = Rounder.round(wordNode.get("start").asDouble(),3);
                    double wEndTimeInSeconds = Rounder.round(wordNode.get("end").asDouble(),3);
                    String wText = wordNode.get("word").asText();
                    
                    // this can happen, although it shouldn't
                    if (wStartTimeInSeconds > wEndTimeInSeconds){
                        double reminder = wStartTimeInSeconds;
                        wStartTimeInSeconds = wEndTimeInSeconds;
                        wEndTimeInSeconds = reminder;
                    }

                    // this can happen, although it shouldn't
                    if (wStartTimeInSeconds == wEndTimeInSeconds){
                        wEndTimeInSeconds += 0.001;
                    }
                    

                    String wStartID = "TLI_" + Double.toString(wStartTimeInSeconds).replace('.', '_');
                    if (!(timeline.containsTimelineItemWithID(wStartID))){
                        TimelineItem tli = new TimelineItem();
                        tli.setID(wStartID);
                        tli.setTime(wStartTimeInSeconds);
                        timeline.insertAccordingToTime(tli);
                    }

                    String wEndID = "TLI_" + Double.toString(wEndTimeInSeconds).replace('.', '_');
                    if (!(timeline.containsTimelineItemWithID(wEndID))){
                        TimelineItem tli = new TimelineItem();
                        tli.setID(wEndID);
                        tli.setTime(wEndTimeInSeconds);
                        timeline.insertAccordingToTime(tli);
                    }
                    
                    wordTier.addEvent(new Event(wStartID, wEndID, wText));
                    
                    // 24-06-2024 : added for WhisperX
                    JsonNode scoreNode = wordNode.get("score");
                    String score = "";
                    if (scoreNode!=null){
                        score = scoreNode.asText();
                    }
                    scoreTier.addEvent(new Event(wStartID, wEndID, score));
                    

                    
                }
            }
            
        }
        
        return result;
        
        
    }

    private static List<Tier> makeTiersForSpeaker(Speaker speaker, BasicTranscription transcription, boolean hasWordLevel, boolean wantsWords) throws JexmaraldaException {
        String speakerID = speaker.getID();    
        String speakerAbb = speaker.getAbbreviation();
        List<Tier> resultList = new ArrayList<>();
        
        Tier textTier = new Tier(transcription.getBody().getFreeID(), speakerID, "v", "t", speakerAbb + " [text]");
        transcription.getBody().addTier(textTier);

        Tier temperatureTier = new Tier(transcription.getBody().getFreeID(), speakerID, "temp", "a", speakerAbb + " [temperature]");
        transcription.getBody().addTier(temperatureTier);

        Tier avgLogProbTier = new Tier(transcription.getBody().getFreeID(), speakerID, "avg", "a", speakerAbb + " [avg_logprob]");
        transcription.getBody().addTier(avgLogProbTier);

        Tier compressionRatioTier = new Tier(transcription.getBody().getFreeID(), speakerID, "cr", "a", speakerAbb + " [compression_ratio]");
        transcription.getBody().addTier(compressionRatioTier);

        Tier noSpeechProbTier = new Tier(transcription.getBody().getFreeID(), speakerID, "nsp", "a", speakerAbb + " [no_speech_prob]");
        transcription.getBody().addTier(noSpeechProbTier);
        
        Tier scoreTier = new Tier(transcription.getBody().getFreeID(), speakerID, "score", "a", speakerAbb + " [score]");
        transcription.getBody().addTier(scoreTier);


        resultList.add(textTier);
        resultList.add(temperatureTier);
        resultList.add(avgLogProbTier);
        resultList.add(compressionRatioTier);
        resultList.add(noSpeechProbTier);
        resultList.add(scoreTier);
        
        Tier wordTier = new Tier(transcription.getBody().getFreeID(), speakerID, "w", "t", speakerAbb + " [words]");
        resultList.add(wordTier);
        if (hasWordLevel && wantsWords){
            textTier.setType("a");
            transcription.getBody().insertTierAt(wordTier, transcription.getBody().lookupID(textTier.getID()));            
        }
        
        return resultList;
        
    }

    public static void postProcess(BasicTranscription importedTranscription, Map<String, Boolean> parameters) {
        boolean empty = parameters.getOrDefault("EMPTY", Boolean.FALSE);
        for (int pos=0; pos<importedTranscription.getBody().getNumberOfTiers(); pos++){
            Tier tier = importedTranscription.getBody().getTierAt(pos);

            boolean select = parameters.getOrDefault(tier.getCategory(), Boolean.FALSE);
            if (!select || (tier.getNumberOfEvents()==0 && !empty)){
                Tier removalCandidate = importedTranscription.getBody().getTierAt(pos);
                if (removalCandidate.getType().equals("t") && removalCandidate.getCategory().equals("w")){
                    // if a t-(word)-tier is removed, the text tier has to become t-tier
                    String[] otherTiers = importedTranscription.getBody().getTiersOfSpeakerAndCategory(removalCandidate.getSpeaker(), "v");
                    if (otherTiers.length==1){
                        try {
                            importedTranscription.getBody().getTierWithID(otherTiers[0]).setType("t");
                        } catch (JexmaraldaException ex) {
                            Logger.getLogger(WhisperJSONConverter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                importedTranscription.getBody().removeTierAt(pos);                
                pos--;
            }
        }
        
        importedTranscription.getBody().removeUnusedTimelineItems();
    }
    
}
