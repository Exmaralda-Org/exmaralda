/*
 * HIATDOSReader.java
 *
 * Created on 21. Oktober 2002, 11:09
 */

package org.exmaralda.partitureditor.exHIATDOS;

import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Speakertable;
import org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Speaker;
import org.exmaralda.partitureditor.jexmaralda.MetaInformation;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import java.io.*;
import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * reads in a set of files that make up a HIAT-DOS-transcription
 * and converts it to an EXMARaLDA Basic-Transcription
 * Menu: File --> Import --> Import HIAT-DOS
 * @author  Thomas Schmidt
 */
public class HIATDOSReader extends Vector {
    
    static int MAX_HIAT_DOS_LINE_LENGTH = 56;
    /** the characters to be replaced by default 
     * (as used in project A6 "Scandinavian Semi-Communication") */
    char[][] CHAR_REPLACE = {
                              {'\u017D', '\u00C4'},     // großes Ä
                              {'\u201E', '\u00E4'},     // kleines ä
                              {'\u2122', '\u00D6'},     // großes Ö
                              {'\u201D', '\u00F6'},     // kleines ö
                              {'\u0161', '\u00DC'},     // großes Ü
                              {'\u0081', '\u00FC'},     // kleines ü
                              {'\u00E1', '\u00DF'},     // sz (ß)   
                              {'&', '\u00C5'},     // großes A mit Kringel                                    
                              {'*', '\u00E5'},     // kleines a mit Kringel
                              {'$', '\u00D8'},    // großes O mit Strich durch
                              {'#', '\u00F8'},          // kleines o mit Strich durch
                              {'+', '\u00C6'},         // großes AE Ligatur
                              {'%', '\u00E6'},         // kleines ae Ligatur
                              {'\uFFFD', '\u00FC'},     // auch kleines ü
                              {'\u201A', '\u00E9'}      // kleines e mit accent aigue
                             };
    
    private Hashtable tiers;
    private Hashtable maxLengthOfPartiturFlaeche;
    private Vector syncPoints;
    private MetaInformation meta;
    private Speakertable speakertable;

    
    /** Creates a new instance of HIATDOSReader 
     * by reading in the specified HIAT-DOS "*.dat" file
     * into memory */
    public HIATDOSReader(String filename) throws IOException {
        String fullFilename = filename;
        FileReader fr = new FileReader(fullFilename);
        BufferedReader br = new BufferedReader(fr);
        String nextLine = new String();
        System.out.println("Started reading document");
        while ((nextLine = br.readLine()) != null){
            addElement(nextLine);
        }
        br.close();
        System.out.println("Document read.");        
        meta = new MetaInformation();
        speakertable = new Speakertable();
    }
    
    /** reads in the head infornation of a HIAT-DOS "*.inf" file */
    public void readHead(String filename) throws IOException {
        String fullFilename = filename;
        FileReader fr = new FileReader(fullFilename);
        BufferedReader br = new BufferedReader(fr);
        String nextLine = new String();
        String fullText = new String();
        System.out.println("Started reading document");
        while ((nextLine = br.readLine()) != null){
            fullText+=nextLine;
        }
        br.close();
        System.out.println("Document read.");        
        fullText = this.replace(fullText).toString();
        // ============== process the file content ==========================

        String projectName = fullText.substring(0,30).trim();
        String transcriptionName = fullText.substring(31,81).trim();
        String recordingName = fullText.substring(82,112).trim();
        String recordingDate = fullText.substring(113,121).trim();
        String recordingDuration = fullText.substring(122,137).trim();
        String zaehlwerkStart = fullText.substring(138,142).trim();
        String zaehlwerkEnd = fullText.substring(143,147).trim();
        String aufnahmeGeraet = fullText.substring(148,168).trim();
        String transkribent = fullText.substring(169,187).trim();
        String transVerh = fullText.substring(188,193).trim();
        String transDatum = fullText.substring(194,202).trim();
        String korrektor = fullText.substring(203,221).trim();
        String korrVerh = fullText.substring(222,227).trim();
        String korrDatum = fullText.substring(228,236).trim();
        String copyright = fullText.substring(237,262).trim();
        String inhalt = fullText.substring(263,323).trim();

        meta.setTranscriptionConvention("HIAT (HIAT-DOS)");
        if (projectName.length()>0) meta.setProjectName(projectName);
        if (transcriptionName.length()>0) meta.setTranscriptionName(transcriptionName);
        if (inhalt.length()>0) meta.setComment(inhalt);

        UDInformationHashtable udinfo = meta.getUDMetaInformation();
        if (recordingName.length()>0) udinfo.setAttribute("Aufnahme", recordingName);
        if (recordingDate.length()>0) udinfo.setAttribute("Aufnahme:Datum", recordingDate);
        if (recordingDuration.length()>0) udinfo.setAttribute("Aufnahme:Dauer", recordingDuration);
        if (zaehlwerkStart.length()>0) udinfo.setAttribute("Aufnahme:Zaehlwerk:Start", zaehlwerkStart);
        if (zaehlwerkEnd.length()>0) udinfo.setAttribute("Aufnahme:Zaehlwerk:End", zaehlwerkEnd);
        if (aufnahmeGeraet.length()>0) udinfo.setAttribute("Aufnahme:Ger�t", aufnahmeGeraet);
        if (transkribent.length()>0) udinfo.setAttribute("Transkribent", transkribent);
        if (transVerh.length()>0) udinfo.setAttribute("Transkribent:Verh�ltnis", transVerh);
        if (transDatum.length()>0) udinfo.setAttribute("Transkribent:Datum", transDatum);
        if (korrektor.length()>0) udinfo.setAttribute("Korrektor", korrektor);
        if (korrVerh.length()>0) udinfo.setAttribute("Korrektor:Verh�ltnis", korrVerh);
        if (korrDatum.length()>0) udinfo.setAttribute("Korrektor:Datum", korrDatum);
        if (copyright.length()>0) udinfo.setAttribute("Copyright", copyright);
        
    }
    
    /** reads in the head infornation of a HIAT-DOS "*.sig" file */
    public void readSpeakers(String filename) throws IOException {
        String fullFilename = filename;
        FileReader fr = new FileReader(fullFilename);
        BufferedReader br = new BufferedReader(fr);
        String nextLine = new String();
        String fullText = new String();
        System.out.println("Started reading document");
        while ((nextLine = br.readLine()) != null){
            fullText+=nextLine;
        }
        br.close();
        System.out.println("Document read.");        
        fullText = this.replace(fullText).toString();
        // ============== process the file content ==========================
        // first character gives number of speakers
        int numberOfSpeakers = Integer.parseInt(fullText.substring(0,1));
        int start = 1;
        for (int pos=0; pos<numberOfSpeakers; pos++){
            String name = fullText.substring(start+1,start+20).trim();
            String abb = fullText.substring(191+pos*3, 193+pos*3);
            start+=21;
            Speaker speaker = new Speaker();
            speaker.setID("S" + Integer.toString(pos));
            speaker.setAbbreviation(abb);
            speaker.getUDSpeakerInformation().setAttribute("Name", name);
            try{
                speakertable.addSpeaker(speaker);
            } catch (JexmaraldaException je) {}
        }
        
    }
    
    
    public void parse(){
        maxLengthOfPartiturFlaeche = new Hashtable();
        for (int pos=0; pos<size(); pos++){
            String line = (String)(elementAt(pos));
            if (line.length()>1){
                Integer partiturFlaechenNummer = new Integer(line.substring(3,7).trim());
                Integer length = new Integer(line.substring(7,9).trim());
                if (!maxLengthOfPartiturFlaeche.containsKey(partiturFlaechenNummer)){
                    maxLengthOfPartiturFlaeche.put(partiturFlaechenNummer, length);
                } else{
                    Integer currentMax = (Integer)(maxLengthOfPartiturFlaeche.get(partiturFlaechenNummer));
                    if (length.compareTo(currentMax)>0){
                        maxLengthOfPartiturFlaeche.put(partiturFlaechenNummer, length);
                    }
                }                        
            }
        }

        tiers = new Hashtable();
        for (int pos=0; pos<size(); pos++){
            String line = (String)(elementAt(pos));
            if (line.length()>1){
                String id = line.substring(0,3);
                Integer partiturFlaechenNummer = new Integer(line.substring(3,7).trim());
                String content = line.substring(24);
                if (!tiers.containsKey(id)){
                    tiers.put(id, new StringBuffer());
                }
                StringBuffer oldValue = (StringBuffer)(tiers.get(id));
                oldValue.append(content);
                int maxLineLength = ((Integer)(maxLengthOfPartiturFlaeche.get(partiturFlaechenNummer))).intValue();
                for (int i=0; i < maxLineLength - content.length(); i++){
                    oldValue.append(" ");
                }
            }
        }
    }
    
    public void replaceChars(){
        for (Enumeration e = tiers.keys(); e.hasMoreElements(); ){
            String id = (String)(e.nextElement());
            String tier = ((StringBuffer)(tiers.get(id))).toString();
            StringBuffer result = replace(tier);
            tiers.put(id, result);
        }
    }
    
    public void select(String[] ids){
        Hashtable selected = new Hashtable();
        for (int pos=0; pos<ids.length; pos++){
            String id = ids[pos];
            selected.put(id, tiers.get(id));
        }
        tiers.clear();
        tiers=selected;
    }
    
    public String[] getTiersAsArray(Vector v){
        String[] result = new String[tiers.size()];
        int pos=0;
        for (Enumeration e = tiers.keys(); e.hasMoreElements(); ){
            String id = (String)(e.nextElement());
            v.addElement(id);
            String tier = ((StringBuffer)(tiers.get(id))).toString();
            result[pos]=tier;
            pos++;
        }
        return result;
    }
    
    public void removeHoles(){
        Vector v = new Vector();
        String[] allTiers = getTiersAsArray(v);
        for (int pos=0; pos<allTiers[0].length()-1; pos++){
            boolean onlySpaces = true;
            for (int pos2=0; pos2<allTiers.length; pos2++){
                String check = allTiers[pos2].substring(pos,pos+2);
                if (!check.equals("  ")){
                    onlySpaces = false;
                    break;
                }
            }
            if (onlySpaces){
                for (int pos2=0; pos2<allTiers.length; pos2++){
                    String firstPart = allTiers[pos2].substring(0, pos+1);
                    String secondPart = allTiers[pos2].substring(pos+2);
                    allTiers[pos2] =  firstPart + secondPart;
                }
                pos = pos-2;                    
            }
        }
        tiers.clear();
        for (int pos=0; pos<v.size(); pos++){
            tiers.put(((String)(v.elementAt(pos))), new StringBuffer(allTiers[pos]));
        }
    }
    
    public void calculateSyncPoints(boolean startANDend){
        syncPoints = new Vector();
        syncPoints.addElement(new Integer(0));
        Vector v = new Vector();
        String[] allTiers = getTiersAsArray(v);
        for (int pos=0; pos<allTiers.length; pos++){
            String tier = allTiers[pos];
            for (int index=0; index<tier.length()-2;index++){
                String check = tier.substring(index,index+3);
                if (check.charAt(0)==' ' && check.charAt(1)==' ' && check.charAt(2)!=' '){
                    syncPoints.addElement(new Integer(index+2));
                } else if (startANDend && check.charAt(0)!=' ' && check.charAt(1)==' ' && check.charAt(2)==' '){
                    syncPoints.addElement(new Integer(index+1));
                }
            }
        }
        syncPoints.addElement(new Integer(allTiers[0].length()));
        Collections.sort(syncPoints);
        for (int pos=0; pos<syncPoints.size()-1; pos++){
            Integer i1 = (Integer)(syncPoints.elementAt(pos));
            Integer i2 = (Integer)(syncPoints.elementAt(pos+1));
            if (i1.equals(i2)){
                syncPoints.removeElementAt(pos);
                pos--;
            }
        }
    }
    
    /** returns a basic transcription version of the file
     * represented by this HIAT-DOS-reader */
    public BasicTranscription toBasicTranscription(){
        BasicTranscription t = new BasicTranscription();
        t.getHead().setSpeakertable(speakertable);
        t.getHead().setMetaInformation(meta);
        try{
            for (int pos=0; pos<syncPoints.size(); pos++){
                Integer no = ((Integer)(syncPoints.elementAt(pos)));
                String tliID = "T" + no.toString();
                TimelineItem tli = new TimelineItem();
                tli.setID(tliID);
                t.getBody().getCommonTimeline().addTimelineItem(tli);
            }
            for (int i1=0; i1<9; i1++){
                for (int i2=0; i2<5; i2++){
                    String id = "n" + Integer.toString(i1) + Integer.toString(i2);
                    if (!tiers.containsKey(id)) {continue;}
                    String content = ((StringBuffer)tiers.get(id)).toString();
                    if (!t.getHead().getSpeakertable().containsSpeakerWithID("S" + id.charAt(1))){
                        Speaker speaker = new Speaker();
                        speaker.setID("s" + id.charAt(1));
                        t.getHead().getSpeakertable().addSpeaker(speaker);
                    }
                    Tier tier = new Tier();
                    tier.setID(id);
                    if (i2==0){
                        tier.setCategory("v");
                        tier.setType("t");
                    } else {
                        tier.setCategory("");
                        tier.setType("d");
                    }
                    tier.setSpeaker("S"+ id.charAt(1));
                    for (int pos2=0; pos2<syncPoints.size()-1; pos2++){
                        int s1=((Integer)(syncPoints.elementAt(pos2))).intValue();
                        int s2=((Integer)(syncPoints.elementAt(pos2+1))).intValue();
                        String cut = trimTrailingWhitespace(content.substring(s1,s2));
                        if (cut.trim().length()>0){
                            Event e = new Event();
                            e.setStart("T" + Integer.toString(s1));
                            e.setEnd("T" + Integer.toString(s2));
                            e.setDescription(cut);
                            tier.addEvent(e);
                        }
                    }
                    t.getBody().addTier(tier);
                }
            }
        } catch (JexmaraldaException je){
            je.printStackTrace();
        }
        t.makeDisplayNames();
        return t;
    }
    
    public void testOutput(){
        for (Enumeration e = tiers.keys(); e.hasMoreElements(); ){
            String id = (String)(e.nextElement());
            String tier = ((StringBuffer)(tiers.get(id))).toString();
            System.out.println(id + "\t" + tier);
        }
    }

    private StringBuffer replace(String original){
        StringBuffer result = new StringBuffer();
        for (int pos=0; pos<original.length(); pos++){
            boolean doneSomething = false;
            char c = original.charAt(pos);
            for (int pos2=0; pos2<CHAR_REPLACE.length; pos2++){
                char toBeReplaced = CHAR_REPLACE[pos2][0];
                char replacement = CHAR_REPLACE[pos2][1];
                if (c==toBeReplaced){
                    result.append(replacement);
                    doneSomething = true;
                    break;
                }
            }
            if (!doneSomething){
                result.append(c);
            }
        }
        return result;
    }
    
    private String trimTrailingWhitespace(String source){
        int double_space_pos = source.indexOf("  ");
        if ((double_space_pos>=0) && (source.substring(double_space_pos).trim().length()==0)){
            return source.substring(0, double_space_pos+1);
        }
        return source;
    }
}
