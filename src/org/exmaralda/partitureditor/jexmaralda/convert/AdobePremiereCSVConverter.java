/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
public class AdobePremiereCSVConverter {
    
    
    
    public static BasicTranscription readAdobePremiereCSV(File csvFile) throws IOException, JexmaraldaException{
        // read json file via Jackson
        List<String> lines = new ArrayList<>();
        FileInputStream fis = new FileInputStream(csvFile);
        InputStreamReader isr = new InputStreamReader(fis, "utf-8");
        try (BufferedReader br = new BufferedReader(isr)) {
            String nextLine;
            System.out.println("Started reading document");
            int lineCount=0;
            while ((nextLine = br.readLine()) != null){
                // remove komisches Zeichen am Anfang von Unicode-kodierten Dateien
                if (lineCount==0){
                    lineCount++;
                    continue;
                }
                lineCount++;
                if (nextLine.trim().length()==0){
                    continue;
                }
                lines.add(nextLine);
            }
        }
        
        
        BasicTranscription result = new BasicTranscription();
        
        // "Sprecher 1","00:00:00:12","00:00:11:10","What comes through is your determination at all costs to actually succeed. 
        
        Map<String,String> speaker2ID = new HashMap<>(); 
        for (String line : lines){
            if (!(line.contains(","))) continue;
            String speakerName = line.substring(0, line.indexOf(",")).replace("\"", "");
            if(!(speaker2ID.containsKey(speakerName))){
                Speaker speaker = new Speaker();
                speaker.setID(result.getHead().getSpeakertable().getFreeID());
                speaker.setAbbreviation(speakerName);
                result.getHead().getSpeakertable().addSpeaker(speaker);
                speaker2ID.put(speakerName, speaker.getID());
                
                Tier textTier = new Tier("TIE_" + speaker.getID(), speaker.getID(), "v", "t", speakerName + " [v]");
                result.getBody().addTier(textTier);                
            }
        }
        
        Timeline timeline = result.getBody().getCommonTimeline();
        
        for (String line : lines){
            String[] bits = line.split("\\\",\\\"");
            if (bits.length!=4) continue;
            String speakerName = bits[0].substring(1);
            String startTimeString = bits[1];
            String endTimeString = bits[2];
            String text = bits[3].replaceAll("\"$", "");
            
            double startTime = timeCodeToSeconds(startTimeString);
            int i1 = timeline.findTimelineItem(startTime, 0.01);
            TimelineItem startTLI;
            if (i1>0){
                startTLI = timeline.getTimelineItemAt(i1);
            } else {
                startTLI = new TimelineItem();
                startTLI.setID(timeline.getFreeID());
                startTLI.setTime(startTime);
                timeline.insertAccordingToTime(startTLI);
            }
            
            double endTime = timeCodeToSeconds(endTimeString);
            int i2 = timeline.findTimelineItem(endTime, 0.01);
            TimelineItem endTLI;
            if (i2>0){
                endTLI = timeline.getTimelineItemAt(i2);
            } else {
                endTLI = new TimelineItem();
                endTLI.setID(timeline.getFreeID());
                endTLI.setTime(endTime);
                timeline.insertAccordingToTime(endTLI);
            }
            
            String speakerID = speaker2ID.get(speakerName);
            Tier tier = result.getBody().getTierWithID("TIE_" + speakerID);
            Event event = new Event(startTLI.getID(), endTLI.getID(), text);
            tier.addEvent(event);
            
        }
        
       
        return result;
        
        
    }
    
    private static double timeCodeToSeconds(String timeCodeString){
        String[] parts = timeCodeString.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        double seconds = Double.parseDouble(parts[2] + "." + parts[3]);
        return 60*60*hours + 60*minutes + seconds;
    }
    
    
}
