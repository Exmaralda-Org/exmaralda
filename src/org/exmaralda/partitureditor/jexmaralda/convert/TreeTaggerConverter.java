/*
 * TextConverter.java
 *
 * Created on 8. Juni 2007, 09:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
/**
 *
 * @author thomas
 */
public class TreeTaggerConverter {
    
    ArrayList<String> lines = new ArrayList<String>();
    public boolean appendSpaces = true;
    
    /** Creates a new instance of TextConverter */
    public TreeTaggerConverter() {
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
            TreeTaggerConverter tc = new TreeTaggerConverter();
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
    
    public String exportBasicTranscription(BasicTranscription bt) throws IOException{
        
        // determine main (token) tier
        Tier mainTier = null;
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            Tier thisTier = bt.getBody().getTierAt(pos);
            if ((thisTier.getSpeaker()!=null) && (thisTier.getType().equals("t"))){
                mainTier = thisTier;
                break;
            }
        }
        
        if (mainTier==null){
            throw new IOException("No main tier found for this transcription");
        }
        
        // determine dependent (lemma, pos, etc.) tiers
        Vector<Tier> dependentTiers = new Vector<Tier>();
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            Tier thisTier = bt.getBody().getTierAt(pos);
            if ((mainTier.getSpeaker().equals(thisTier.getSpeaker())) && (thisTier.getType().equals("a"))){
                dependentTiers.addElement(thisTier);
            }
        }
        
        StringBuffer result = new StringBuffer();
        
        for (int pos=0; pos<bt.getBody().getCommonTimeline().getNumberOfTimelineItems(); pos++){
            TimelineItem tli = bt.getBody().getCommonTimeline().getTimelineItemAt(pos);
            try {
                Event e = mainTier.getEventAtStartPoint(tli.getID());
                result.append(e.getDescription().trim());
                for (Tier tier : dependentTiers){
                    if (tier.containsEventAtStartPoint(tli.getID())){
                        Event a = tier.getEventAtStartPoint(tli.getID());
                        result.append("\t");
                        result.append(a.getDescription());
                    }
                    //if (!(dependentTiers.indexOf(tier)==dependentTiers.size()-1)){}
                }
                result.append(System.getProperty("line.separator"));
            } catch (JexmaraldaException ex) { /*do nothing - we simply ignore this non-existing event */}
        }        
        return result.toString();
    }
    
    public void writeText(BasicTranscription bt, File file) throws IOException {
        String text = exportBasicTranscription(bt);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(text.getBytes("UTF-8"));
        fos.close();
        
    }
    
    
}
