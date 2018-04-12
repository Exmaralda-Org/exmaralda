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
 * @author tommi
 *
 * Some TSV format for INEL. HEre's an email describing:
 * <blockquote>
 * more than one transcription tier.
 * I'd suggest an extra header line with a list of transcription tier names
 * if more than one. If that line is absent, the first column is taken as the
 * only transcription tier.
 * <pre>
 * ### Transcription tiers ### TXT-A TXT-B
 * TXT-A   Lemma-A  POS-A   TXT-B   Lemma-B  POS-B   Start    End
 * [...]
 * xxxx    yyyyy    zzzz                             1.2907   5.3719
 *                         xxxx    yyyyy    zzzz    7.6353   10.7190
 * xxxx    yyyyy    zzzz    aaaa    bbbb     cccc    12.2907  15.371
 * </pre>
 * lternatively, in the row-per-annotation setup I'd use a separate column
 * for tier type (transcription/annot./desc.) and/or for the speaker.
 * <pre>
 * (Tiername   Tiertype  Speaker    Annotation    Start    End)
 * TXT-A       t         A          xxxx          1.2907   5.3719
 * Lemma-A     a         A          yyyy          1.2907   5.3719
 * POS-A       a         A          zzzz          1.2907   5.3719
 * TXT-B       t         B          xxxx          1.2907   5.3719
 * </pre>
 * </blockquote>
 */
public class InelTsvConverter {

    ArrayList<String> lines = new ArrayList<String>();
    public boolean appendSpaces = true;

    /** Creates a new instance of TextConverter */
    public InelTsvConverter() {
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
        Set<String> speakers = new HashSet<String>();
        Set<String> tiers = new HashSet<String>();
        for (String line : lines) {
            String[] fields = line.split("\\t");
            tiers.add(fields[0] + "\t" + fields[1] + "\t" + fields[2]);
            speakers.add(fields[1]);
        }
        int n_speakers = 0;
        Map<String, String> speakermap = new HashMap<String, String>();
        for (String s : speakers) {
            String sid = "SPK" + n_speakers;
            Speaker speaker = new Speaker();
            speaker.setID(sid);
            speaker.setAbbreviation(s);
            try {
                st.addSpeaker(speaker);
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
            speakermap.put(s, sid);
            n_speakers++;
        }
        Timeline timeline = bt.getBody().getCommonTimeline();
        String tli_start = timeline.addTimelineItem();
        String tli_end = tli_start;
        int n_tiers  = 0;
        Map<String, Tier> tiermap = new HashMap<String, Tier>();
        for (String s : tiers) {
            String tid = "TIE" + n_tiers;
            Tier tier = new Tier();
            String[] fields = s.split("\\t");
            tier.setID(tid);
            tier.setType(fields[1]);
            tier.setDisplayName(fields[0]);
            tier.setSpeaker(speakermap.get(fields[2]));
            try {
                bt.getBody().addTier(tier);
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
            tiermap.put(fields[0], tier);
            n_tiers++;
        }
        // map from TSV time presenetaiton to timeline id's
        Map<String, String> tlimap = new HashMap<String, String>();
        tlimap.put("0", tli_start);
        for (String line : lines) {
            String[] fields = line.split("\\t");
            Event e = new Event();
            e.setDescription(fields[3]);
            String e_start = "";
            String e_end = "";
            try {
                if (tlimap.containsKey(fields[4])) {
                    e_start = tlimap.get(fields[4]);
                } else {
                    e_start = timeline.getFreeID();
                    TimelineItem tli = new TimelineItem(e_start,
                            timestampToDouble(fields[4]));
                    timeline.addTimelineItem(tli);
                    tlimap.put(fields[4], e_start);
                }
                if (tlimap.containsKey(fields[5])) {
                    e_end = tlimap.get(fields[5]);
                } else {
                    e_end = timeline.getFreeID();
                    TimelineItem tli = new TimelineItem(e_end,
                            timestampToDouble(fields[5]));
                    timeline.addTimelineItem(tli);
                    tlimap.put(fields[5], e_end);
                }
                e.setStart(e_start);
                e.setEnd(e_end);
                Tier tier = tiermap.get(fields[0]);
                tier.addEvent(e);
                tli_end = e_end;
            } catch (JexmaraldaException je) {
                je.printStackTrace();
            }
        }
        return bt;
    }

    private static double timestampToDouble(String timestamp) {
        return Double.parseDouble(timestamp);
    }

    public static void main(String[] args){
        try {
            File f = new File("test.tsv");
            InelTsvConverter tc = new InelTsvConverter();
            tc.readText(f);
            BasicTranscription bt = tc.importText();
            bt.writeXMLToFile("test.exb", "none");
            System.out.println(tc.importText().toXML());
        } catch (PatternSyntaxException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String exportBasicTranscription(BasicTranscription bt) throws IOException{
        return "FIXME";
    }

    public void writeText(BasicTranscription bt, File file) throws IOException {
        String text = exportBasicTranscription(bt);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(text.getBytes("UTF-8"));
        fos.close();

    }


}
