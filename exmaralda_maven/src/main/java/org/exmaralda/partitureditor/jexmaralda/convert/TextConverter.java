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
public class TextConverter {
    
    Vector<String> lines = new Vector<String>();
    Pattern splitPattern;
    boolean noSplit = false;
    
    /** Creates a new instance of TextConverter */
    public TextConverter(String sr) throws PatternSyntaxException {
        if (sr.length()==0){
            noSplit = true;
        } else {
            splitPattern = Pattern.compile(sr);            
        }
    }
    
    public void readText(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String nextLine = new String();
        System.out.println("Started reading document");
        while ((nextLine = br.readLine()) != null){
            lines.addElement(nextLine);
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
            lines.addElement(nextLine);
            lineCount++;
        }
        br.close();
        System.out.println("Document read.");                
    }
    
    public BasicTranscription convert(){
        BasicTranscription bt = new BasicTranscription();
        Timeline timeline = bt.getBody().getCommonTimeline();
        String tli = timeline.addTimelineItem();
        Tier tier = new Tier();
        tier.setID("TIE0");
        tier.setCategory("txt");
        tier.setType("t");
        tier.setDisplayName("TXT");
        // added 30-03-2010
        tier.setSpeaker("SPK0");
        try {
            // added 30-04-2010
            Speaker newSpeaker = new Speaker();
            newSpeaker.setID("SPK0");
            newSpeaker.setAbbreviation("AUT");
            newSpeaker.setSex('u');
            bt.getHead().getSpeakertable().addSpeaker(newSpeaker);
            // end add (and Edd)
            bt.getBody().addTier(tier);
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
        for (String line : lines){
            Vector<Event> events = processLine(line);
            for (Event event : events){
                event.setStart(tli);
                tli = timeline.addTimelineItem();
                event.setEnd(tli);
                tier.addEvent(event);
            }
        }        
        return bt;        
    }
    
    public Vector<Event> processLine(String line){
        Vector<Event> result = new Vector<Event>();
        if (noSplit){
            Event e = new Event();
            e.setDescription(line);
            result.add(e);
            return result;
        }
        int lastStart = 0;
        Matcher m = splitPattern.matcher(line);
        while (m.find()){
            String s1 = line.substring(lastStart, m.start());
            String s2 = m.group();
            lastStart = m.end();
            Event e = new Event();
            e.setDescription(s1+s2);
            result.add(e);
        }
        if (lastStart<line.length()){
            Event e = new Event();
            e.setDescription(line.substring(lastStart));
            result.add(e);
        }
        return result;
    }
    
    public static void main(String[] args){
        try {
            File f = new File("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\test.txt");
            TextConverter tc = new TextConverter("a");
            tc.readText(f);
            System.out.println(tc.convert().toXML());
        } catch (PatternSyntaxException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
