/*
 * SimpleExmaraldaReader.java
 *
 * Created on 25. Mai 2001, 14:43
 */

package org.exmaralda.dgd.folk;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de)
 * @version 
 */
public class POLI2FLK extends ArrayList<String> {

    
    public char openOverlap = '<';
    public char closeOverlap = '>';
    public char openComment = '[';
    public char closeComment = ']';
    public char openAnnotation = '{';
    public char closeAnnotation = '}';
    public char speakerSep = ':';
    int lineCount;
    
    private String source;

    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    
    /** Creates new SimpleExmaraldaReader, i.e. opens the specified file and reads it into the vector (one line per element) */
    public POLI2FLK(String filename) throws IOException, UnsupportedEncodingException {
        source = filename;
        FileInputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String nextLine = new String();
        System.out.println("Started reading document");
        lineCount=0;
        while ((nextLine = br.readLine()) != null){
            // remove komisches Zeichen am Anfang von Unicode-kodierten Dateien
            if (lineCount==0 && nextLine.charAt(0)=='\uFEFF'){
                nextLine = nextLine.substring(1);
            }
            add(nextLine);
            lineCount++;
        }
        br.close();
        System.out.println("Document read.");        
        normalize();
    }
    
           

    public BasicTranscription parseBasicTranscription() throws JexmaraldaException{
        BasicTranscription t = new BasicTranscription();
        setHeadParameters(t.getHead());
        Timeline timeline = t.getBody().getCommonTimeline();
        timeline.addTimelineItem(new TimelineItem("T0"));
        Speakertable speakertable = t.getHead().getSpeakertable();
        for (int pos=0; pos<size(); pos++){
            String[] parts = dissectLine(pos);

            String speaker = parts[0];
            String main = parts[1];
            
            if (!speakertable.containsSpeakerWithID(speaker)){
                Speaker newSpeaker = new Speaker();
                newSpeaker.setID(speaker);
                newSpeaker.setAbbreviation(speaker);
                speakertable.addSpeaker(newSpeaker);

                Tier mainTier = new Tier();
                mainTier.setID(t.getBody().getFreeID());
                mainTier.setSpeaker(speaker);
                mainTier.setType("t");
                mainTier.setCategory("v");
                t.getBody().addTier(mainTier);
            
            }
            
            Event[] events = getEvents(main, timeline);
            //System.out.println("Processing " + main);
            String[] ids = t.getBody().getTiersWithProperties(speaker, "t");
            Tier mainTier = t.getBody().getTierWithID(ids[0]);
            for (int pos2=0; pos2<events.length; pos2++){
                mainTier.addEvent(events[pos2]);
            }

            String start = events[0].getStart();
            String end = events[events.length-1].getEnd();
        }

        t.getBody().removeEmptyTiers();
        t.makeDisplayNames();
        
        return t;
    }
    
    /** normalizes the document, i.e. gets rid of white space, empty lines, etc. */
    private void normalize(){
        System.out.println("Started normalizing document");
        String lastSpeaker = "";
        for (int pos=0; pos<size(); pos++){
            String line = get(pos);
            line = line.trim();
            if (line.length()>0){
                line+=" ";
            }
            line = line.replaceAll("\\s{2,}", " ");
            line = line.trim();

            // replace the original string with the normalized one (supposing there's something left...)
            if (line.length()<=0){                
                remove(pos);
                pos--;
            } else {
                if (!(line.matches("^[A-Za-z0-9\\/]+\\:.*"))){
                    // i.e. no speaker...
                    line =  lastSpeaker + ": " + line;
                } else {
                    int index = line.indexOf(":");
                    lastSpeaker = line.substring(0,index);
                }
                set(pos, line);
                System.out.println("[" + pos + "] " + line);
            }
        }
        System.out.println("Document normalized");
    }
    
    private void setHeadParameters(Head h){
        h.getMetaInformation().setComment("Imported from a Rio de Janeiro text transcription file");
        h.getMetaInformation().getUDMetaInformation().setAttribute("Source file name", source);
    }
    
    /** dissect the current line into speaker, comment, main and annotation part */
    private String[] dissectLine(int pos) throws JexmaraldaException {

        String line = get(pos);        
        System.out.println("Dissecting " + line);
        int index = line.indexOf(":");
        String speaker = line.substring(0, index);
        String main = line.substring(index+1).trim() + " ";        
        String[] result = new String[2];
        result[0] = speaker;
        result[1] = main;
        return result;
    }

            
        
    
    
// ****************************************************************************************************************
// ****************************************************************************************************************
    
    Event[] getEvents(String mainDescription, Timeline timeline) throws JexmaraldaException{
        Vector resultVector = new Vector();
        String lastEnd = timeline.getTimelineItemAt(timeline.getNumberOfTimelineItems()-1).getID();
        
        String remainder = mainDescription;
        
        /*if (true){
            String end = timeline.getFreeID();
            timeline.addTimelineItem(new TimelineItem(end));
            Event newEvent = new Event(lastEnd, end, mainDescription);
            resultVector.addElement(newEvent);            
        }*/
        while (remainder.matches(".*[\\.\\?\\!] .+")){
            Matcher m = Pattern.compile("[\\.\\?\\!] .+").matcher(remainder);
            m.find();
            String desc = remainder.substring(0, m.start() + 2 );
            remainder = remainder.substring(m.start() +2 );
            String end = timeline.getFreeID();
            timeline.addTimelineItem(new TimelineItem(end));
            Event newEvent = new Event(lastEnd, end, desc);
            resultVector.addElement(newEvent);            
            lastEnd = end;
        }
        
        String end = timeline.getFreeID();
        timeline.addTimelineItem(new TimelineItem(end));
        Event newEvent = new Event(lastEnd, end, remainder);
        resultVector.addElement(newEvent);            

        Event[] result = new Event[resultVector.size()];   
        for (int pos=0; pos<resultVector.size(); pos++){
            result[pos]=(Event)resultVector.elementAt(pos);
        }
        return result;
    }
    

// ****************************************************************************************************************
    
    public static void main(String[] args){
        try {
            POLI2FLK rdjtc = new POLI2FLK("Z:\\POLI\\Transkripte\\TXT\\FOLK_POLI_01_A01b.txt");
            BasicTranscription bt = rdjtc.parseBasicTranscription();
            bt.writeXMLToFile("Z:\\POLI\\Transkripte\\EXB\\FOLK_POLI_01_A01b.exb", "none");
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

