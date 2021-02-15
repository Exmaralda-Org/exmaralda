/*
 * TextConverter.java
 *
 * Created on 8. Juni 2007, 09:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import org.xml.sax.SAXException;
/**
 *
 * @author thomas
 */
public class F4Converter {
    
    ArrayList<String> lines = new ArrayList<String>();
    public boolean appendSpaces = true;
    
    /** Creates a new instance of TextConverter */
    public F4Converter() {
    }
    
    public void readText(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String nextLine = new String();
        System.out.println("Started reading document");
        while ((nextLine = br.readLine()) != null){
            lines.add(nextLine);
        }
        br.close();
    }

    public void readText(File file, String encoding) throws 
        FileNotFoundException, IOException, UnsupportedEncodingException{
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        BufferedReader br = new BufferedReader(isr);
        String nextLine = new String();
        System.out.println("Started reading document " + file.getAbsolutePath());
        int lineCount=0;
        while ((nextLine = br.readLine()) != null){
            if (nextLine.trim().length()==0) continue;
            // remove komisches Zeichen am Anfang von Unicode-kodierten Dateien
            if (lineCount==0 && encoding.startsWith("UTF") && nextLine.charAt(0)=='\uFEFF'){
                nextLine = nextLine.substring(1);
            }
            lines.add(nextLine);
            lineCount++;
        }
        br.close();
        System.out.println("Document read.");                
    }
    
    public BasicTranscription importF4(boolean splitAtPeriod){
        try {
            BasicTranscription bt = new BasicTranscription();
            Speakertable st = bt.getHead().getSpeakertable();
            Speaker speaker = new Speaker();
            speaker.setID("SPK0");
            speaker.setAbbreviation("X");
            st.addSpeaker(speaker);
            Timeline timeline = bt.getBody().getCommonTimeline();
            String tliID = timeline.addTimelineItem();
            TimelineItem lastTLI = timeline.getTimelineItemWithID(tliID);
            lastTLI.setTime(0.0);
            
            Tier textTier = new Tier();
            textTier.setID("TIE0");
            textTier.setCategory("txt");
            textTier.setType("t");
            textTier.setDisplayName("X [txt]");
            textTier.setSpeaker("SPK0");
            Map<String, Tier> abb2TierMap = new HashMap<>();
            abb2TierMap.put("X", textTier);
            
            bt.getBody().addTier(textTier);
            
            for (String line : lines){
                // #00:00:20-5# Also ich war mit meinem Mann, wir waren mein Mann, ist geboren in Ludwigshafen am
                // Rhein, und die Stadt hat ihn eingeladen, uns eingeladen, und wir fahren dort. Wir waren im Haus, wo er gewohnt hat, und wir sind sehr, sehr gut und Dead akzeptiert, und die waren wirklich fantastisch.
                String restOfLine = line;
                if (line.startsWith("#")){
                    int endIndex = line.indexOf("#", 3);
                    String timeStampString = line.substring(0, endIndex);
                    restOfLine = line.substring(endIndex+1).trim();
                    int h = Integer.parseInt(timeStampString.substring(1,3));
                    int m = Integer.parseInt(timeStampString.substring(4,6));
                    double s = Double.parseDouble(timeStampString.substring(7).replaceAll("\\-", ".").replaceAll("\\#", ""));
                    //System.out.println(timeStampString + " : " + h + " / " + m + " / " + s);
                    double seconds = 3600.0 * h + 60.0 * m + s;
                    lastTLI.setTime(seconds);
                    
                    String sp = "X";
                    if (restOfLine.matches("^[A-ZÄÖÜ][A-Za-zÄÖÜäöüß]{0,10}\\:.+")){
                        int index = restOfLine.indexOf(":");
                        sp = restOfLine.substring(0, index);
                        restOfLine = restOfLine.substring(index+1);
                    }
                    
                    if (!(abb2TierMap.containsKey(sp))){
                        String tierID = bt.getBody().getFreeID();
                        Tier newTier = new Tier(tierID, sp, "v", "t", sp + " [v]");
                        abb2TierMap.put(sp, newTier);
                        String speakerID = bt.getHead().getSpeakertable().getFreeID();
                        Speaker newSpeaker = new Speaker();
                        newSpeaker.setID(speakerID);
                        st.addSpeaker(newSpeaker);
                    }
                    
                    String[] sentences = {restOfLine};
                    if (splitAtPeriod){
                        sentences = restOfLine.split("(?<=\\. +)");
                    }
                    Tier thisTier = abb2TierMap.get(sp);
                    for (String sentence : sentences){
                        Event event = new Event();
                        event.setStart(lastTLI.getID());
                        String newTLIID = timeline.getFreeID();
                        TimelineItem newTLI = new TimelineItem();
                        newTLI.setID(newTLIID);
                        timeline.addTimelineItem(newTLI);
                        event.setEnd(newTLI.getID());
                        lastTLI = newTLI;
                        event.setDescription(sentence);
                        thisTier.addEvent(event);
                    }                    
                }
                
            }
            
            
            return bt;
        } catch (JexmaraldaException ex) {
            Logger.getLogger(F4Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    // I don't quite understand (any more) what this is doing
    // It does not seem to be used anywhere in the real EXMARaLDA code
    // and maybe it is very old, I don't think it is my code
    // I'm getting rid of it (14-02-2021)
    @Deprecated
    public BasicTranscription importText(){
        BasicTranscription bt = new BasicTranscription();
        Speakertable st = bt.getHead().getSpeakertable();
        Speaker speaker = new Speaker();
        speaker.setID("SPK0");
        speaker.setAbbreviation("X");
        try {
            st.addSpeaker(speaker);
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
        Timeline timeline = bt.getBody().getCommonTimeline();
        String tli = timeline.addTimelineItem();

        Tier textTier = new Tier();
        textTier.setID("TIE0");
        textTier.setCategory("txt");
        textTier.setType("t");
        textTier.setDisplayName("X [txt]");
        textTier.setSpeaker("SPK0");

        Tier posTier = new Tier();
        posTier.setID("TIE1");
        posTier.setCategory("pos");
        posTier.setType("a");
        posTier.setDisplayName("X [pos]");
        posTier.setSpeaker("SPK0");

        Tier lemmaTier = new Tier();
        lemmaTier.setID("TIE2");
        lemmaTier.setCategory("lemma");
        lemmaTier.setType("a");
        lemmaTier.setDisplayName("X [lemma]");
        lemmaTier.setSpeaker("SPK0");

        try {
            bt.getBody().addTier(textTier);
            bt.getBody().addTier(posTier);
            bt.getBody().addTier(lemmaTier);
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
        for (String line : lines){
            Event[] events = processLine(line);
            
            for (Event e : events){
                e.setStart(tli);
            }
            tli = timeline.addTimelineItem();
            for (Event e : events){
                e.setEnd(tli);
            }

            textTier.addEvent(events[0]);
            if (events.length>1){
                posTier.addEvent(events[1]);
            }
            if (events.length>2){
                lemmaTier.addEvent(events[2]);
            }
        }        
        return bt;        
    }
    
    public Event[] processLine(String line){
        String[] items = line.split("\\t");
        Event[] result = new Event[items.length];        
        int i = 0;
        for (String item : items){
            result[i] = new Event();
            result[i].setDescription(item);
            i++;
        }
        if (appendSpaces){
            result[0].setDescription(result[0].getDescription() + " ");
        }
        /*Event[] result = new Event[2];
        String word = line;
        String pos = "";
        int index = line.indexOf("\t");
        if (index>0){
            word = line.substring(0,index) + " ";
            pos = line.substring(index+1);
        }
        result[0] = new Event();
        result[0].setDescription(word);
        result[1] = new Event();
        result[1].setDescription(pos);*/
        return result;
    }
    
    public static void main(String[] args){
        try {
            File f = new File("T:\\TP-Z2\\DATEN\\Anne_Siekmeyer\\EXMARALDA\\hlte\\G73\\abschrift_tagged.txt");
            F4Converter tc = new F4Converter();
            tc.readText(f);
            BasicTranscription bt = tc.importText();
            bt.writeXMLToFile("T:\\TP-Z2\\DATEN\\Anne_Siekmeyer\\EXMARALDA\\ausgewhlte\\G73\\abschrift_tagged.exb", "none");
            System.out.println(tc.importText().toXML());
        } catch (PatternSyntaxException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static final int EVENTS_FROM_T_TIERS = 0;
    public static final int SPEAKER_CONTRIBUTIONS = 1;
    

    public String exportBasicTranscription(BasicTranscription bt) throws IOException, SAXException, ParserConfigurationException, TransformerException{
        return exportBasicTranscription(bt, SPEAKER_CONTRIBUTIONS, "TXT");
    }

    public String exportBasicTranscription(BasicTranscription bt, int method, String outputType) throws IOException, SAXException, ParserConfigurationException, TransformerException{
        StylesheetFactory sf = new StylesheetFactory(true);
        String result;
        if (method==EVENTS_FROM_T_TIERS){
            String xsl_path = "/org/exmaralda/partitureditor/jexmaralda/xsl/EXB2F4.xsl";
            String[][] parameters = {{"output-type", outputType}};
            result = sf.applyInternalStylesheetToString(xsl_path, bt.toXML(), parameters);            
        } else {
            String xsl_path = "/org/exmaralda/partitureditor/jexmaralda/xsl/EXS2F4.xsl";            
            String[][] parameters = {{"output-type", outputType}};
            result = sf.applyInternalStylesheetToString(xsl_path, bt.toSegmentedTranscription().toXML(), parameters);
        } 
        return result;
    }
    
    public void writeText(BasicTranscription bt, File file, int method, String outputType) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        String text = exportBasicTranscription(bt, method, outputType);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(text.getBytes());
        fos.close();        
    }
    
    
    
}
