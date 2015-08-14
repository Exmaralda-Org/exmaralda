/*
 * AIFConverter.java
 *
 * Created on 18. Juni 2002, 11:15
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.*;
import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public final class PraatConverter {

    static final short STARTED_READING = 0;
    static final short IN_ITEM = 1;
    static final short IN_INTERVAL = 2;
    
    static final short FILE_TYPE = 20;
    static final short OBJECT_CLASS = 21;
    static final short XMIN = 22;
    static final short XMAX = 23;
    static final short TIERS = 24;
    static final short SIZE = 25;
    static final short ITEM_HEADER = 26;
    static final short ITEM = 27;
    static final short CLASS = 28;
    static final short NAME = 29;
    static final short INTERVALS_SIZE = 30;
    static final short INTERVALS = 31;
    static final short TEXT = 32;
    static final short BROKEN_TEXT = 33;
    static final short OTHER = 100;
    
    PraatUnicodeMapping pum;
    
    /** Creates new PraatConverter */
    public PraatConverter() {
        pum = new PraatUnicodeMapping();
        try {
            pum.instantiate();
        } catch (JexmaraldaException je){
            je.printStackTrace();
        }
    }
    
    public String BasicTranscriptionToPraat(BasicTranscription t){
        StringBuilder sb = new StringBuilder();
        sb.append("File type = \"ooTextFile\"\n");
        sb.append("Object class = \"TextGrid\"\n");
        sb.append("\n");
        Timeline tl = t.getBody().getCommonTimeline();
        tl.completeTimes();
        sb.append("xmin = " + Double.toString(tl.getTimelineItemAt(0).getTime()) + " \n");
        sb.append("xmax = " + Double.toString(tl.getTimelineItemAt(tl.getNumberOfTimelineItems()-1).getTime()) + " \n");
        sb.append("tiers? <exists> \n");
        sb.append("size = " + Integer.toString(t.getBody().getNumberOfTiers()) + " \n" );
        sb.append("item []: \n");
        for (int pos=0; pos<t.getBody().getNumberOfTiers(); pos++){
            Tier tier = t.getBody().getTierAt(pos);
            tier.stratify(tl, AbstractEventTier.STRATIFY_BY_DELETION);
            tier.fillWithEmptyEvents(tl);
            tier.sort(tl);
            sb.append("    "); // 4 spaces
            sb.append("item [" + Integer.toString(pos+1) + "]: \n");
            sb.append("        "); // 8 spaces
            sb.append("class = \"IntervalTier\" \n");
            // changed in Version 1.2.5.
            /*String speakerID = tier.getSpeaker();
            String category = tier.getCategory();
            String speakerAbbrev = new String();
            try {
                speakerAbbrev = t.getHead().getSpeakertable().getSpeakerWithID(speakerID).getAbbreviation();
            } catch (JexmaraldaException je){}                                    */
            sb.append("        "); // 8 spaces
            // changed in Version 1.2.5.
            //sb.append("name = \"" + speakerAbbrev + " [" + category + "]\" \n");
            //sb.append("name = \"" + tier.getDisplayName() + "\" \n");
            sb.append("name = \"" + tier.getPraatName() + "\" \n");
            sb.append("        "); // 8 spaces
            sb.append("xmin = " + Double.toString(tl.getTimelineItemAt(0).getTime()) + " \n");
            sb.append("        "); // 8 spaces
            sb.append("xmax = " + Double.toString(tl.getTimelineItemAt(tl.getNumberOfTimelineItems()-1).getTime()) + " \n");
            sb.append("        "); // 8 spaces
            sb.append("intervals: size = " + Integer.toString(tier.getNumberOfEvents()) + " \n");
            
            for (int i=0; i<tier.getNumberOfEvents(); i++){
                Event e = tier.getEventAt(i);
                sb.append("        "); // 8 spaces
                sb.append("intervals [" + Integer.toString(i+1) + "]: \n");
                try {
                    sb.append("            "); // 12 spaces
                    sb.append("xmin = " + Double.toString(tl.getTimelineItemWithID(e.getStart()).getTime()) + " \n");
                    sb.append("            "); // 12 spaces
                    sb.append("xmax = " + Double.toString(tl.getTimelineItemWithID(e.getEnd()).getTime()) + " \n");
                } catch (JexmaraldaException je) {
                    //should not get here
                    System.out.println(je.getMessage());
                }
                sb.append("            "); // 12 spaces
                sb.append("text = \"" + toPraatString(e.getDescription()) + "\" \n");
            }
        }
        tl.removeInterpolatedTimes();
        return sb.toString();
    }
    
    public void writePraatToFile(BasicTranscription t, String filename) throws IOException {
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        fos.write(BasicTranscriptionToPraat(t).getBytes("UTF-16"));
        fos.close();
        System.out.println("document written.");
    }
    
    public BasicTranscription readPraatFromFile(String filename) throws IOException{
        String encoding = EncodingDetector.detectEncoding(new File(filename));
        System.out.println("Encoding " + encoding);
        if (encoding.length()==0){
            encoding="UTF-8";
        }
        return readPraatFromFile(filename, encoding);
    }
    
    public BasicTranscription readPraatFromFile(String filename, String encoding) throws IOException{
       BasicTranscription t = new BasicTranscription();
       Timeline tl = t.getBody().getCommonTimeline();
       t.getHead().getMetaInformation().getUDMetaInformation().setAttribute("source", filename);
       
       // set up the buffered reader
       // Changed in version 1.3.4.
       // because Praat has changed the file encoding
       //FileReader fr =  new FileReader(filename);
       FileInputStream fis = new FileInputStream(filename);
       InputStreamReader isr = new InputStreamReader(fis, encoding);       
       // Changed in version 1.3.4.
       // because Praat has changed the file encoding
       //BufferedReader myInput = new BufferedReader(fr);
       BufferedReader myInput = new BufferedReader(isr);
       String nextLine = new String();
       
       short currentState = STARTED_READING;
       System.out.println("Started reading document...");
       // read in the document line for line
       int tiers=0;
       Tier currentTier = new Tier();
       Event currentEvent = new Event();
       Hashtable timesHashtable = new Hashtable();
       try {
           while ((nextLine = myInput.readLine()) != null) {
                nextLine = nextLine.trim();
                //System.out.println(nextLine);
                switch (recognizeLine(nextLine)){
                    case ITEM       :   if (tiers>0) {t.getBody().addTier(currentTier);}
                                        tiers++;
                                        currentTier = new Tier();
                                        currentTier.setType("t");
                                        currentTier.setCategory("v");
                                        currentTier.setID("TIE" + Integer.toString(tiers-1));
                                        currentState = IN_ITEM;
                                        break;
                    case NAME       :   String name = (String)getLineValue(nextLine, NAME);
                                        if (name.contains("|")){
                                            // parse the name
                                            String speakerID = name.substring(0, name.indexOf("|"));
                                            if (!(t.getHead().getSpeakertable().containsSpeakerWithID(speakerID) || speakerID.length()==0 || "NO_SPEAKER".equals(speakerID))){
                                                Speaker newSpeaker = new Speaker();
                                                newSpeaker.setID(speakerID);
                                                newSpeaker.setAbbreviation(speakerID);
                                                t.getHead().getSpeakertable().addSpeaker(newSpeaker);
                                            }
                                            if (!("NO_SPEAKER".equals(speakerID) || speakerID.length()==0)){
                                                currentTier.setSpeaker(speakerID);
                                            } else {
                                                currentTier.setSpeaker(null);
                                            }                                               
                                            if (name.length()>name.indexOf("|")){
                                                String rest = name.substring(name.indexOf("|")+1);
                                                if (rest.contains("|")){
                                                    String type = rest.substring(0, rest.indexOf("|"));
                                                    if (rest.length()>rest.indexOf("|")){
                                                        String category = rest.substring(rest.indexOf("|")+1);
                                                        currentTier.setCategory(category);                                                        
                                                    }
                                                    //System.out.println("Type: " + type);
                                                    if ("t".equals(type) || "d".equals(type) || "a".equals(type)){
                                                        currentTier.setType(type);
                                                    } 
                                                }                                                
                                            }                                             
                                        } else {
                                            String originalName = name; 
                                            int count=0;
                                            // check for duplicate names, added 11-Oct-2002
                                            while (t.getHead().getSpeakertable().containsSpeakerWithID(name)){
                                                name = originalName + "-" + Integer.toString(count);
                                                count++;
                                            }
                                            Speaker newSpeaker = new Speaker();
                                            newSpeaker.setID(name);
                                            newSpeaker.setAbbreviation(name);
                                            t.getHead().getSpeakertable().addSpeaker(newSpeaker);
                                            currentTier.setSpeaker(name);
                                        }
                                        break;
                    case INTERVALS  :   currentState=IN_INTERVAL;
                                        break;
                    case XMIN       :   if (currentState==IN_INTERVAL){
                                            Double time = (Double)getLineValue(nextLine,XMIN);
                                            if (!timesHashtable.containsKey(time)){
                                                TimelineItem tli = new TimelineItem();
                                                String id = tl.getFreeID();
                                                tli.setID(id);
                                                tli.setTime(time.doubleValue());
                                                tl.insertAccordingToTime(tli);
                                                timesHashtable.put(time, id);
                                            }
                                            currentEvent.setStart((String)timesHashtable.get(time));                                            
                                        }
                                        break;
                    case XMAX       :   if (currentState==IN_INTERVAL){
                                            Double time = (Double)getLineValue(nextLine,XMAX);
                                            if (!timesHashtable.containsKey(time)){
                                                TimelineItem tli = new TimelineItem();
                                                String id = tl.getFreeID();
                                                tli.setID(id);
                                                tli.setTime(time.doubleValue());
                                                tl.insertAccordingToTime(tli);
                                                timesHashtable.put(time, id);
                                            }
                                            currentEvent.setEnd((String)timesHashtable.get(time));                                            
                                        }
                                        break;
                    case TEXT       :   String text = ""; 
                                        if (nextLine.indexOf("\"")==nextLine.lastIndexOf("\"")){
                                            // added 02-Nov-2007, take care of line breaks within text
                                            text = (String)getLineValue(nextLine,BROKEN_TEXT);                                            
                                            while ((nextLine = myInput.readLine()) != null){
                                                if (!(nextLine.trim().endsWith("\""))){
                                                    text+=nextLine;
                                                } else {
                                                    text+=nextLine.substring(0,nextLine.lastIndexOf("\""));
                                                    break;
                                                }
                                            }
                                        } else {
                                            text = (String)getLineValue(nextLine,TEXT);
                                        }
                                        if (text.length()>0) {
                                            String escaped = stripEscapeChars(text);
                                            String replaced = replaceEscapeChars(escaped);
                                            replaced = org.exmaralda.partitureditor.jexmaralda.StringUtilities.replaceControlCharacters(replaced);
                                            currentEvent.setDescription(replaced);
                                            currentTier.addEvent(currentEvent);
                                        }
                                        currentEvent = new Event();
                                        break;
                }
           }
           t.getBody().addTier(currentTier);
           t.makeDisplayNames();
       } catch (JexmaraldaException je) {
           je.printStackTrace();
           throw new IOException(je.getMessage());
       }       
       System.out.println("Document read.");    
       return t;
    }
    
    
    private short recognizeLine(String line){
        if (line.startsWith("File type")) return FILE_TYPE;
        else if (line.startsWith("Object class")) return OBJECT_CLASS;
        else if (line.startsWith("xmin")) return XMIN;
        else if (line.startsWith("xmax")) return XMAX;
        else if (line.startsWith("tiers?")) return TIERS;
        else if (line.startsWith("size")) return SIZE;
        else if (line.startsWith("item []:")) return ITEM_HEADER;
        else if (line.startsWith("item")) return ITEM;
        else if (line.startsWith("class")) return CLASS;
        else if (line.startsWith("name")) return NAME;
        else if (line.startsWith("intervals: size")) return INTERVALS_SIZE;
        else if (line.startsWith("intervals")) return INTERVALS;
        else if (line.startsWith("text")) return TEXT;
        else return OTHER;
    }
    
    private Object getLineValue(String line, short type) throws IOException{
        try {
            switch (type){
                case XMIN : return new Double(Double.parseDouble(line.substring(7)));                
                case XMAX : return new Double(Double.parseDouble(line.substring(7)));
                case NAME : return new String(line.substring(8,line.length()-1));
                case TEXT : return new String(line.substring(8,line.length()-1));
                case BROKEN_TEXT : return new String(line.substring(8,line.length()));
            }
            return null;
        } catch (Throwable t){throw new IOException("Illegal value: " + line);}
    }
    
    private String stripEscapeChars(String text){
        StringBuilder sb = new StringBuilder();
        for (int pos=0; pos<text.length(); pos++){
            char c = text.charAt(pos);
            if (c=='\t'){
                // ignore tabs, added 11-Oct-2002
            } else if (c=='\n'){
                // ignore line breaks, added 02-Nov-2007
            }
            else if (c!='"'){
                sb.append(c);              
            }
            else if ((pos<text.length()-1) && (text.charAt(pos+1)=='"')){
                // ignore the character
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    private String replaceEscapeChars(String text){
        StringBuffer sb= new StringBuffer();
        for (int pos=0; pos<text.length(); pos++){
            char c = text.charAt(pos);
            if ((c=='\\') && (pos+3<=text.length())){                
                String escapeCode = text.substring(pos+1, pos+3);
                String uni = pum.getUnicodeForPraat(escapeCode);
                sb.append(uni);
                pos+=2;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    private String toPraatString(String text){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<text.length(); pos++){
            char c = text.charAt(pos);
            if (c=='"'){
                sb.append("\"\"");              // append two quotes, not one
//            } else if (pum.unicodePraat.containsKey(Character.toString(c))){                
                } else if (pum.unicodePraat.containsKey(new Character(c).toString())){                
                // ACHTUNG!!! replaceAll() scheint eine 1.4. Methode zu sein!
                //sb.append(pum.getPraatForUnicode(Character.toString(c)).replaceAll("\"", "\"\""));
                String replaceString = pum.getPraatForUnicode(new Character(c).toString());
                for (int pos2=0; pos2<replaceString.length(); pos2++){
                    if (replaceString.charAt(pos2)=='\"'){
                        sb.append("\"\"");
                    } else {
                        sb.append(replaceString.charAt(pos2));
                    }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
