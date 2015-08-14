/*
 * SimpleExmaraldaReader.java
 *
 * Created on 25. Mai 2001, 14:43
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de)
 * @version 
 */
public class MoDiKoReader extends ArrayList<String> {

    
    public char openOverlap = '<';
    public char closeOverlap = '>';
    public char openComment = '[';
    public char closeComment = ']';
    public char openAnnotation = '{';
    public char closeAnnotation = '}';
    public char speakerSep = ':';
    int lineCount;
    
    private Hashtable overlapIndexes;
    private String source;

    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new SimpleExmaraldaReader, i.e. opens the specified file and reads it into the vector (one line per element) */
    public MoDiKoReader(String filename) throws IOException {
        source = filename;
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        overlapIndexes = new Hashtable();
        String nextLine = new String();
        System.out.println("Started reading document");
        while ((nextLine = br.readLine()) != null){
            add(nextLine);
        }
        br.close();
        System.out.println("Document read.");        
        normalize();
    }
           
    /** Creates new SimpleExmaraldaReader, i.e. opens the specified file and reads it into the vector (one line per element) */
    public MoDiKoReader(String filename, String encoding) throws IOException, UnsupportedEncodingException {
        source = filename;
        FileInputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        BufferedReader br = new BufferedReader(isr);
        overlapIndexes = new Hashtable();
        String nextLine = new String();
        System.out.println("Started reading document");
        lineCount=0;
        while ((nextLine = br.readLine()) != null){
            // remove komisches Zeichen am Anfang von Unicode-kodierten Dateien
            if (lineCount==0 && encoding.startsWith("UTF") && nextLine.charAt(0)=='\uFEFF'){
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
            String comment = parts[1];
            String main = parts[2];
            String annotation = parts[3];
            
            if (!speakertable.containsSpeakerWithID(speaker)){
                Speaker newSpeaker = new Speaker();
                newSpeaker.setID(speaker);
                newSpeaker.setAbbreviation(speaker);
                speakertable.addSpeaker(newSpeaker);

                Tier commentTier = new Tier();
                commentTier.setID(t.getBody().getFreeID());
                commentTier.setSpeaker(speaker);
                commentTier.setType("d");
                commentTier.setCategory("c");
                t.getBody().addTier(commentTier);

                Tier mainTier = new Tier();
                mainTier.setID(t.getBody().getFreeID());
                mainTier.setSpeaker(speaker);
                mainTier.setType("t");
                mainTier.setCategory("v");
                t.getBody().addTier(mainTier);
            
                Tier annTier = new Tier();
                annTier.setID(t.getBody().getFreeID());
                annTier.setSpeaker(speaker);
                annTier.setType("a");
                annTier.setCategory("a");
                t.getBody().addTier(annTier);
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
            if (comment.length()>0){
                String[] ids2 = t.getBody().getTiersWithProperties(speaker, "d");
                Tier commentTier = t.getBody().getTierWithID(ids2[0]);
                Event event = new Event();
                event.setStart(start);
                event.setEnd(end);
                event.setDescription(comment);
                commentTier.addEvent(event);
            }
            
            if (annotation.length()>0){
                String[] ids3 = t.getBody().getTiersWithProperties(speaker, "a");
                Tier annotationTier = t.getBody().getTierWithID(ids3[0]);
                Event event = new Event();
                event.setStart(start);
                event.setEnd(end);
                event.setDescription(annotation);
                annotationTier.addEvent(event);
            }
            
        }

        t.getBody().removeEmptyTiers();
        t.makeDisplayNames();

        // added for 1.2.6.: check if events in one tier overlap
        for (int pos=0; pos<t.getBody().getNumberOfTiers(); pos++){
            Tier tier = t.getBody().getTierAt(pos);
            Event offendingEvent = new Event();
            if (!tier.isStratified(t.getBody().getCommonTimeline(), offendingEvent)){
                //tier.stratify(t.getBody().getCommonTimeline(), AbstractEventTier.STRATIFY_BY_DISTRIBUTION);                
                String text = "Tier " + tier.getDisplayName() + " has illegal temporal structure" + System.getProperty("line.separator");
                text+="The offending event has the start point " + offendingEvent.getStart() + System.getProperty("line.separator");
                text+=" and the description " + offendingEvent.getDescription();
                throw new JexmaraldaException(0, text);
            }
        }
        
        return t;
    }
    
    /** normalizes the document, i.e. gets rid of white space, empty lines, etc. */
    private void normalize(){
        System.out.println("Started normalizing document");
        // merge consecutive lines when the second one does not start with "#"
        for (int pos=0; pos<size(); pos++){
            String line = this.get(pos);
            if ((!(line.startsWith("#"))) && pos>0){
                String previousLine = this.get(pos-1);
                String mergedLine = previousLine + " " + line;
                set(pos-1, mergedLine);
                remove(pos);
                pos--;
            }
        }
        
        for (int pos=0; pos<size(); pos++){
            String line = this.get(pos);
            line = line.trim();
            line = line.replaceAll(" {2,}", " ");
            line = line.replaceAll("\\t{2,}", "\t");
          
            // replace the original string with the normalized one (supposing there's something left...)
            if (line.length()<=0){
                remove(pos);
                pos--;
            } else {
                set(pos, line);
            }
        }
        System.out.println("Document normalized");
    }
    
    private void setHeadParameters(Head h){
        h.getMetaInformation().setComment("Imported from a Simple EXMARaLDA file");
        h.getMetaInformation().getUDMetaInformation().setAttribute("Source file name", source);
    }
    
    /** dissect the current line into speaker, comment, main and annotation part */
    private String[] dissectLine(int pos) throws JexmaraldaException {

        String line = get(pos);
        
        String speaker = "";
        int speakerSepPos = line.indexOf(speakerSep);
        if (speakerSepPos < 1) {
            throw new JexmaraldaException(0, "Error at line " + Integer.toString(pos+1) + ". \n" + "No speaker separator. \n" + line);
        } else {
            speaker = line.substring(0, speakerSepPos);
        }

        String comment = "";
        int openCommentPos = line.indexOf(openComment);
        int closeCommentPos = line.indexOf(closeComment);
        if (openCommentPos<0 && closeCommentPos<0){ // i.e. if there is a no comment at all
        } else if (((openCommentPos<0 || closeCommentPos<0) || // i.e. if there is an opening but no closing bracket or vice versa
             (openCommentPos >= closeCommentPos) || // i.e. if the opening bracket comes AFTER the closing bracket
             (openCommentPos <= speakerSepPos))){  // i.e. if the opening bracket comes BEFORE the speaker separator
            throw new JexmaraldaException(0, "Error at line " + Integer.toString(pos) + ". \n" + "Illegal comment bracketing. \n" + line);
        } else {
            comment = line.substring(openCommentPos+1, closeCommentPos);
        }
        
        String annotation = "";
        int openAnnotationPos = line.indexOf(openAnnotation);
        int closeAnnotationPos = line.indexOf(closeAnnotation);
        if (openAnnotationPos<0 && closeAnnotationPos<0){ // i.e. if there is a no comment at all
        } else if (((openAnnotationPos<0 || closeAnnotationPos<0) || // i.e. if there is an opening but no closing bracket or vice versa
             (openAnnotationPos >= closeAnnotationPos) || // i.e. if the opening bracket comes AFTER the closing bracket
             (openAnnotationPos <= closeCommentPos))){  // i.e. if the opening bracket comes BEFORE the bracket closing the comment
            throw new JexmaraldaException(0, "Error at line " + Integer.toString(pos) + ". \n" + "Illegal annotation bracketing. \n" + line);
        } else {
            annotation = line.substring(openAnnotationPos+1, closeAnnotationPos);
        }

        String main = "";
        int mainStart = Math.max(speakerSepPos+1, closeCommentPos+1);
        if (openAnnotationPos<1){
            main = line.substring(mainStart);           
        } else {
            main = line.substring(mainStart, openAnnotationPos);
        }
        // strip leading whitespace
        while (main.length()>0 && Character.isWhitespace(main.charAt(0))){
            main = main.substring(1);
        }
        // strip trailing whitespace
        while (main.length()>1 && Character.isWhitespace(main.charAt(main.length()-1)) && Character.isWhitespace(main.charAt(main.length()-2))){
            main = main.substring(0,main.length()-1);
        }
        // if the line ends with an overlap, throw out the last space also
        if (main.endsWith("> ")){
            main = main.substring(0,main.length()-1);
        }
        
        String[] result = new String[4];
        result[0] = speaker;
        result[1] = comment;
        result[2] = main;
        result[3] = annotation;
        return result;
    }

            
        
    
    
// ****************************************************************************************************************
// ****************************************************************************************************************
    
    Event[] getEvents(String mainDescription, Timeline timeline) throws JexmaraldaException{
        Vector resultVector = new Vector();
        String lastEnd = timeline.getTimelineItemAt(timeline.getNumberOfTimelineItems()-1).getID();
        if (mainDescription.length()==0){
            String end = timeline.getFreeID();
            timeline.addTimelineItem(new TimelineItem(end));
            Event newEvent = new Event(lastEnd, end, mainDescription);
            resultVector.addElement(newEvent);            
        }
        while (mainDescription.length()>0){
            if (mainDescription.charAt(0)==openOverlap){
                int firstClosingBracket = mainDescription.indexOf(closeOverlap);
                if (firstClosingBracket<0) {throw new JexmaraldaException(44,"Simple Exmaralda Parse Error - " + mainDescription);}
                int secondClosingBracket = mainDescription.indexOf(closeOverlap, firstClosingBracket+1);
                if (secondClosingBracket<0) {throw new JexmaraldaException(44,"Simple Exmaralda Parse Error - " + mainDescription);}
                if (firstClosingBracket==secondClosingBracket) {throw new JexmaraldaException(44,"Simple Exmaralda Parse Error - " + mainDescription);}
                String overlapIndex = mainDescription.substring(firstClosingBracket, secondClosingBracket);
                String overlapStart = new String();
                String overlapEnd = new String();
                String overlapText = new String();
                if (overlapIndexes.containsKey(overlapIndex)){
                    String entry = (String)overlapIndexes.get(overlapIndex);
                    overlapStart = entry.substring(0,entry.indexOf('#'));
                    overlapEnd = entry.substring(entry.indexOf('#')+1);
                    overlapText = mainDescription.substring(1,firstClosingBracket);
                }
                else{
                    overlapStart = new String(lastEnd);
                    overlapEnd = timeline.getFreeID();
                    overlapText = mainDescription.substring(1,firstClosingBracket);
                    timeline.addTimelineItem(new TimelineItem(overlapEnd));
                    String entry = new String(overlapStart + "#" + overlapEnd);
                    overlapIndexes.put(overlapIndex, entry);                    
                }
                Event newEvent = new Event(overlapStart, overlapEnd, overlapText);
                resultVector.addElement(newEvent);
                lastEnd = new String(overlapEnd);
                mainDescription = mainDescription.substring(secondClosingBracket+1);                
            }
            else {
                if (mainDescription.indexOf('<')==-1){
                    String end = timeline.getFreeID();
                    timeline.addTimelineItem(new TimelineItem(end));
                    Event newEvent = new Event(lastEnd, end, mainDescription);
                    resultVector.addElement(newEvent);
                    mainDescription = new String();
                }
                else {
                    int openingBracket = mainDescription.indexOf('<');
                    String end = timeline.getFreeID();
                    timeline.addTimelineItem(new TimelineItem(end));
                    String eventText = mainDescription.substring(0,openingBracket);
                    Event newEvent = new Event(lastEnd, end, eventText);
                    resultVector.addElement(newEvent);
                    lastEnd = new String(end);
                    mainDescription = mainDescription.substring(openingBracket);
                }                    
            }
        }
        Event[] result = new Event[resultVector.size()];   
        for (int pos=0; pos<resultVector.size(); pos++){
            result[pos]=(Event)resultVector.elementAt(pos);
        }
        return result;
    }
    

// ****************************************************************************************************************
    
    
    public ArrayList<Integer> checkLines(){
        // a well behaved line looks like this:
        // #00:00:01-9# PM1:	so (4) gut oki doki
        ArrayList<Integer> result = new ArrayList<Integer>();
        String REGEX = "^#\\d{2}\\:\\d{2}\\:\\d{2}\\-\\d# [A-Z]{2}\\d\\:.+$";
        int pos=1;
        for (String line : this){
            System.out.print("Line #" + pos);
            if (line.matches(REGEX)){
                System.out.println(" matches.");
            } else {
                System.out.println(" does not match: " + line);
                result.add(pos);
            }
            pos++;
        }
        return result;
    }
    
    public void writeLines(File file) throws IOException{
        FileOutputStream fos = new FileOutputStream(file);
        for (String line : this){
            fos.write((line+ "\n").getBytes("UTF-8"));
        }
        fos.close();
        
    }
    
    public static void main(String[] args){
        try {
            String FILE="C:\\Users\\Schmidt\\ownCloud\\Shared\\ModiKo\\Datengrundlage\\Trainingskorpus_Wareneingang\\"
                    + "TEST_THOMAS\\06+6a_Prozessmodellierung_Lagerist_Transkript_2012-11-12_Er_corrected.txt";
            MoDiKoReader reader = new MoDiKoReader(FILE, "UTF-8");
            ArrayList<Integer> result = reader.checkLines();
            if (!result.isEmpty()){
                File theFile = new File(FILE);
                File outFile = new File(theFile.getParentFile(), theFile.getName().substring(0, theFile.getName().lastIndexOf(".")) + "_normalized.txt");
                reader.writeLines(outFile);
                System.out.println(outFile.getAbsolutePath() + " written.");
                System.exit(0);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(MoDiKoReader.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
}