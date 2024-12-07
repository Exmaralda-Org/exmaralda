/*
 * SimpleExmaraldaReader.java
 *
 * Created on 25. Mai 2001, 14:43
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.*;
import java.util.*;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.AbstractSegmentVector;
import org.exmaralda.partitureditor.jexmaralda.AtomicTimedSegment;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.Speaker;
import org.exmaralda.partitureditor.jexmaralda.SpeakerContribution;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.TimedAnnotation;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.xml.sax.SAXException;

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de)
 * @version 
 */
public class K8MysteryConverter extends Vector<String> {

    
    BasicTranscription conversionResult;
    File inputFile;

    Tier interviewerTier;
    Tier faroesePersonTier;




    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    public K8MysteryConverter(File file) throws FileNotFoundException, IOException{
        inputFile = file;
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String nextLine = new String();
        System.out.println("[K8MysteryConverter] Started reading document");
        int count=0;
        // read the document line by line, skip empty lines and the first line
        while ((nextLine = br.readLine()) != null){
            if ((count>0) && (nextLine.trim().length()>0)){
                addElement(nextLine + "\t");
            }
            count++;
        }
        br.close();
        System.out.println("[K8MysteryConverter] Document read.");
    }

    public BasicTranscription convert() throws JexmaraldaException{
        conversionResult = new BasicTranscription();
        generateSpeakerTable();
        generateTiers();
        generateTimeline();
        parse();
        finalCleanup();
        return conversionResult;
    }

    private void generateSpeakerTable() throws JexmaraldaException {
        System.out.println("[K8MysteryConverter] Generating speakertable");
        Speaker interviewer = new Speaker();
        interviewer.setID("INT");
        interviewer.setAbbreviation("INT");
        Speaker faroesePerson = new Speaker();
        faroesePerson.setID("FAR");
        faroesePerson.setAbbreviation("FAR");
        conversionResult.getHead().getSpeakertable().addSpeaker(interviewer);
        conversionResult.getHead().getSpeakertable().addSpeaker(faroesePerson);
    }


    private void generateTiers() throws JexmaraldaException{
        System.out.println("[K8MysteryConverter] Generating tiers");
        interviewerTier = new Tier();
        interviewerTier.setID("TIE_INT");
        interviewerTier.setSpeaker("INT");
        interviewerTier.setType("t");
        interviewerTier.setCategory("v");
        interviewerTier.setDisplayName("INT [v]");
        faroesePersonTier = new Tier();
        faroesePersonTier.setID("TIE_FAR");
        faroesePersonTier.setSpeaker("FAR");
        faroesePersonTier.setType("t");
        faroesePersonTier.setCategory("v");
        faroesePersonTier.setDisplayName("FAR [v]");
        conversionResult.getBody().addTier(interviewerTier);
        conversionResult.getBody().addTier(faroesePersonTier);
    }

    private void generateTimeline() throws JexmaraldaException{
        System.out.println("[K8MysteryConverter] Generating timeline");
        for (String line : this){
            String[] fields = line.split("\\t");
            if (fields.length<5) continue;
            //0002 	0.8033524121013901 	3.8959554547234347 	so búði eg, so búði eg 25 ár í Klaksvík 								q: á ja
            String start = fields[1].trim();
            String end = fields[2].trim();
            String combined = start + "_" + end;
            Timeline tl = conversionResult.getBody().getCommonTimeline();
            if (!(tl.containsTimelineItemWithID("T" + start.replaceAll("\\.", "_")))){
                TimelineItem startTLI = new TimelineItem();
                startTLI.setID("T" + start.replaceAll("\\.", "_"));
                startTLI.setTime(Double.parseDouble(start));
                tl.insertAccordingToTime(startTLI);
            }
            if (!(tl.containsTimelineItemWithID("T" + end.replaceAll("\\.", "_")))){
                TimelineItem endTLI = new TimelineItem();
                endTLI.setID("T" + end.replaceAll("\\.", "_"));
                endTLI.setTime(Double.parseDouble(end));
                tl.insertAccordingToTime(endTLI);
            }
            if (!(tl.containsTimelineItemWithID("T" + combined.replaceAll("\\.", "_")))){
                TimelineItem midTLI = new TimelineItem();
                midTLI.setID("T" + combined.replaceAll("\\.", "_"));
                tl.insertTimelineItemAfter("T" + start.replaceAll("\\.", "_"), midTLI);
            }

        }
    }

    private void parse() throws JexmaraldaException{
        System.out.println("[K8MysteryConverter] Started parsing document");
        for (String line : this){
            String[] fields = line.split("\\t");
            if (fields.length<5) continue;
            //0002 	0.8033524121013901 	3.8959554547234347 	so búði eg, so búði eg 25 ár í Klaksvík 								q: á ja
            String start = fields[1].trim();
            String end = fields[2].trim();
            String combined = start + "_" + end;
            String faroesePersonText = fields[3];
            String interviewerText = fields[fields.length-1];
            if (interviewerText.trim().startsWith("q:")){
                int index = interviewerText.indexOf(":");
                interviewerText = interviewerText.substring(index+1).trim();
            }
            if (faroesePersonText.trim().length()>0){
                Event faroesePersonEvent = new Event();
                faroesePersonEvent.setStart("T" + start.replaceAll("\\.", "_"));
                if (interviewerText.trim().length()>0){
                    faroesePersonEvent.setEnd("T" + combined.replaceAll("\\.", "_"));
                } else {
                    faroesePersonEvent.setEnd("T" + end.replaceAll("\\.", "_"));
                }
                faroesePersonEvent.setDescription(faroesePersonText);
                faroesePersonTier.addEvent(faroesePersonEvent);
            }

            if (interviewerText.trim().length()>0){
                Event interviewerEvent = new Event();
                interviewerEvent.setStart("T" + combined.replaceAll("\\.", "_"));
                interviewerEvent.setEnd("T" + end.replaceAll("\\.", "_"));
                interviewerEvent.setDescription(interviewerText);
                interviewerTier.addEvent(interviewerEvent);
            }

        }

        System.out.println("[K8MysteryConverter] Document parsed");
    }

    private void finalCleanup(){
        conversionResult.getBody().removeUnusedTimelineItems();
        conversionResult.normalize();
        conversionResult.getBody().getCommonTimeline().completeTimes(false, conversionResult);
        conversionResult.getBody().smoothTimeline(0.05);
    }




    

   
}