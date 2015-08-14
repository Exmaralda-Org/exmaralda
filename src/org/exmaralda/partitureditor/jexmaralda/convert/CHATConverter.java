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
public class CHATConverter extends Vector<String> {

    
    BasicTranscription conversionResult;
    HashSet<String> speakers;
    File inputFile;

    public static final int CHAT_SEGMENTATION_METHOD = 0;
    public static final int HIAT_SEGMENTATION_METHOD = 1;
    public static final int EVENT_METHOD = 2;

    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    public CHATConverter(File file) throws FileNotFoundException, IOException{
        System.out.println("[CHATConverter] Determining encoding");
        inputFile = file;
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String firstLine = br.readLine();
        if (firstLine.toUpperCase().startsWith("@UTF8")){
            System.out.println("[CHATConverter] Switching to UTF-8 encoding");
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
        } else if (firstLine.startsWith("@Font")){
            String secondLine = br.readLine();
            if (secondLine.toUpperCase().startsWith("@UTF8")){
                System.out.println("[CHATConverter] Switching to UTF-8 encoding");
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                br = new BufferedReader(isr);
            }
        }
        String nextLine = new String();
        System.out.println("[CHATConverter] Started reading document");
        while ((nextLine = br.readLine()) != null){
            addElement(nextLine);
        }
        br.close();
        System.out.println("[CHATConverter] Document read.");
        normalize();
    }

    public BasicTranscription convert() throws JexmaraldaException{
        conversionResult = new BasicTranscription();
        conversionResult.getBody().getCommonTimeline().addTimelineItem();
        conversionResult.getBody().getCommonTimeline().addTimelineItem();
        generateSpeakerTable();
        findSpeakerMetadata();
        findRecording();
        generateTiers();
        parse();
        finalCleanup();
        return conversionResult;
    }

    private void generateSpeakerTable() throws JexmaraldaException {
        System.out.println("[CHATConverter] Generating speakertable");
        speakers = new HashSet<String>();
        HashSet<String> abbs = new HashSet<String>();
        String participantsLine = null;
        for (String line : this){
            if (line.startsWith("@Participants")){
                participantsLine = line;
            } else if (line.startsWith("*")){
                String speaker = line.substring(1, line.indexOf(":"));
                speakers.add(speaker);
            }
        }
        if (participantsLine!=null){
            //@Participants:	CHI Ross Target_Child, MAR Mark Target_Child, FAT Brian	Father, MOT Mary Mother
            String participantsValues = participantsLine.substring(participantsLine.indexOf(":")+1).trim();
            String[] participants = participantsValues.split(",");
            for (String partDesc : participants){
                String[] info = partDesc.trim().split(" ");
                Speaker speaker = new Speaker();
                speaker.setID("SPK_" + info[0]);
                speaker.setAbbreviation(info[0]);
                if (info.length>1){
                    speaker.getUDSpeakerInformation().setAttribute("CHAT:Full Name", info[1]);
                    if ((abbs.contains(info[1]) && (!abbs.contains(info[0])))){
                        abbs.add(info[0]);
                        speaker.setAbbreviation(info[0]);
                    } else {
                        abbs.add(info[1]);
                        speaker.setAbbreviation(info[1]);
                    }
                }
                if (info.length>2){
                    speaker.getUDSpeakerInformation().setAttribute("CHAT:Role", info[2]);
                }
                conversionResult.getHead().getSpeakertable().addSpeaker(speaker);
            }
        }
        for (String s : speakers){
            if (!conversionResult.getHead().getSpeakertable().containsSpeakerWithID("SPK_"+s)){
                Speaker speaker = new Speaker();
                speaker.setID("SPK_" + s);
                speaker.setAbbreviation(s);
                conversionResult.getHead().getSpeakertable().addSpeaker(speaker);
            }
        }
        System.out.println("[CHATConverter] " + conversionResult.getHead().getSpeakertable().getNumberOfSpeakers() + " speakers generated.");

    }

    private void findSpeakerMetadata(){
        for (String line : this){
            if (!(line.startsWith("@ID:"))) continue;
            String content = line.substring(line.indexOf(":")+1).trim();
            String[] fields = content.split("\\|");
            fields = Arrays.copyOf(fields, 9);
            for (int pos=0; pos<fields.length; pos++){
                if (fields[pos]==null) fields[pos]="";
            }
            //@ID: language|corpus|code|age|sex|group|SES|role|education|
            String code = fields[2].trim();
            if (code.length()<1) continue;
            try {
                Speaker s = conversionResult.getHead().getSpeakertable().getSpeakerWithID("SPK_" + code);
                s.getUDSpeakerInformation().setAttribute("CHAT:ID", code);
                s.getUDSpeakerInformation().setAttribute("Language", fields[0].trim());
                s.getUDSpeakerInformation().setAttribute("Corpus", fields[1].trim());
                s.getUDSpeakerInformation().setAttribute("Age", fields[3].trim());
                s.getUDSpeakerInformation().setAttribute("Sex", fields[4].trim());
                if (fields[4].equals("male")) s.setSex('m');
                if (fields[4].equals("female")) s.setSex('f');
                s.getUDSpeakerInformation().setAttribute("Group", fields[5].trim());
                s.getUDSpeakerInformation().setAttribute("SES", fields[6].trim());
                s.getUDSpeakerInformation().setAttribute("Education", fields[8].trim());
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }

        }
    }

    private void findRecording(){
        System.out.println("[CHATConverter] Finding recording");
        HashSet<String> checked = new HashSet<String>();
        for (String line : this){
            if (line.startsWith("@Media:")){
                //@Media:	block, audio
                int index0 = line.indexOf(":");
                int index1 = line.indexOf(",");
                String medianame = line.substring(index0+1,index1).trim();
                String referencedFile = null;
                String[] suffixes = {"wav", "WAV", "mp3", "MP3", "mov", "MOV", ""};
                for (String suffix : suffixes){
                    File testFile = new File(inputFile.getParentFile(), medianame + "." + suffix);
                    System.out.println("Searching " + testFile.getAbsolutePath());
                    if (testFile.exists()){
                        referencedFile = testFile.getAbsolutePath();
                        break;
                    }
                }
                if (referencedFile!=null){
                    conversionResult.getHead().getMetaInformation().addReferencedFile(referencedFile);
                    System.out.println("[CHATConverter] Found recording " + referencedFile);

                }
                break; // look no further: we found a recording!
            }
            if (line.indexOf("%snd:")>=0){
                String sndLine = line;
                //*MAR:	he would like nothing ? %snd:"boys68a1"_0_26331
                String snd = sndLine.substring(sndLine.indexOf("%snd:")+5);
                int index1 = snd.indexOf("\"");
                int index2 = snd.indexOf("\"", index1+1);
                snd = snd.substring(index1+1, index2);
                if (!checked.contains(snd)){
                    String referencedFile = null;
                    String[] suffixes = {"wav", "WAV", "mp3", "MP3", "mov", "MOV", ""};
                    for (String suffix : suffixes){
                        File testFile = new File(inputFile.getParentFile(), snd + "." + suffix);
                        System.out.println("Searching " + testFile.getAbsolutePath());
                        if (testFile.exists()){
                            referencedFile = testFile.getAbsolutePath();
                            break;
                        }
                    }
                    if (referencedFile!=null){
                        conversionResult.getHead().getMetaInformation().addReferencedFile(referencedFile);
                        System.out.println("[CHATConverter] Found recording " + referencedFile);

                    }
                    checked.add(snd);
                }
                
            }
        }
    }

    private void generateTiers() throws JexmaraldaException{
        // collect the dependent tier categories
        Vector<String> dependentCategories = new Vector<String>();
        for (String line: this){
            if (line.startsWith("%")){
                String category = line.substring(1, line.indexOf(":"));
                if (!(dependentCategories.contains(category))){
                    dependentCategories.addElement(category);
                    System.out.println("[CHATConverter] Found dependent tier " + category);
                }
                    
            }
        }
        for (int pos=0; pos<conversionResult.getHead().getSpeakertable().getNumberOfSpeakers(); pos++){
            Speaker s = conversionResult.getHead().getSpeakertable().getSpeakerAt(pos);
            Tier mainTier = new Tier();
            mainTier.setID("TIER_" + s.getID() + "_MAIN");
            mainTier.setSpeaker(s.getID());
            mainTier.setCategory("main");
            mainTier.setType("t");
            mainTier.setDisplayName(s.getAbbreviation());
            conversionResult.getBody().addTier(mainTier);
            for (String cat : dependentCategories){
                Tier depTier = new Tier();
                depTier.setID("TIER_" + s.getID() + "_" + cat);
                depTier.setSpeaker(s.getID());
                depTier.setCategory(cat);
                depTier.setType("a");
                depTier.setDisplayName("%" + cat);
                conversionResult.getBody().addTier(depTier);
            }
        }
    }

    private void parse() throws JexmaraldaException{
        System.out.println("[CHATConverter] Started parsing document");
        Timeline tl = conversionResult.getBody().getCommonTimeline();
        String lastEnd = tl.insertTimelineItemAfter(tl.getTimelineItemAt(0).getID());
        for (int pos=0; pos<this.size(); pos++){
            String line = elementAt(pos);
            //System.out.println("[CHATConverter] Parsing line " + line);
            if (line.startsWith("*")){
                // TODO: parse the main line
                // create timeline items
                // create an event on the main line
                int colonIndex = line.indexOf(":");
                int sndIndex = line.indexOf("%snd");
                // added 28-06-2010: new CHAT files don't have the %snd tag
                // they only have times between two unicode symbols #0015
                if (sndIndex<0){
                    sndIndex = line.indexOf("\u0015");
                }
                String speaker = line.substring(1,colonIndex);
                String start = "";
                String end = "";
                String text = "";
                if (sndIndex>=0){
                    int index1=-1;
                    int index2=-1;
                    if (line.indexOf("%snd")>=0){
                        index1 = line.indexOf("_", sndIndex);
                        index2 = line.lastIndexOf("_");
                    } else {
                        index1 = sndIndex;
                        index2 = line.indexOf("_", sndIndex);
                    }
                    String startTime = line.substring(index1+1, index2).trim();
                    String endTime = line.substring(index2+1).trim();
                    startTime = startTime.replaceAll("[^\\d]", "");
                    endTime = endTime.replaceAll("[^\\d]", "");
                    start = "TLI_TIME_" + startTime;
                    end = "TLI_TIME_" + endTime;
                    if (!(tl.containsTimelineItemWithID(start))){
                        TimelineItem tli1 = new TimelineItem(start, Double.parseDouble(startTime)/1000.0);
                        tl.insertAccordingToTime(tli1);
                    }
                    if (!(tl.containsTimelineItemWithID(end))){
                        TimelineItem tli2 = new TimelineItem(end, Double.parseDouble(endTime)/1000.0);
                        tl.insertAccordingToTime(tli2);
                    }
                    text = line.substring(colonIndex+1,sndIndex).trim() + " ";
                } else {
                    start = lastEnd;
                    //end = tl.insertTimelineItemBefore(tl.getTimelineItemAt(tl.getNumberOfTimelineItems()-1).getID());
                    end = tl.insertTimelineItemAfter(tl.getTimelineItemAt(tl.getNumberOfTimelineItems()-1).getID());
                    text = line.substring(colonIndex+1).trim() + " ";
                }

                if (tl.lookupID(start)>=tl.lookupID(end)){
                    throw new JexmaraldaException("Illegal temoral structure at line " + Integer.toString(pos) + ": " + line);
                }

                // added 22-11-2010: don't add events with identical start and end
                if (start.equals(end)){
                    System.out.println("[CHAT Converter] Skipping line " + Integer.toString(pos) + " - start=end");
                    continue;
                }
                Event newEvent = new Event();
                newEvent.setDescription(text);
                newEvent.setStart(start);
                newEvent.setEnd(end);
                //TIER_SPK_CHI_MAIN
                Tier tier = conversionResult.getBody().getTierWithID("TIER_SPK_" + speaker + "_MAIN");
                tier.addEvent(newEvent);
                while (pos<size()-1 && elementAt(pos+1).startsWith("%")){
                    pos++;
                    String dependentLine = elementAt(pos);
                    int colonIndex2 = dependentLine.indexOf(":");
                    String category = dependentLine.substring(1,colonIndex2);
                    String annotationText = dependentLine.substring(colonIndex2+1).trim() + " ";
                    Event annotationEvent = new Event();
                    annotationEvent.setDescription(annotationText);
                    annotationEvent.setStart(start);
                    annotationEvent.setEnd(end);
                    //TIER_SPK_CHI_MAIN
                    Tier annotationTier = conversionResult.getBody().getTierWithID("TIER_SPK_" + speaker + "_" + category);
                    annotationTier.addEvent(annotationEvent);
                }
                lastEnd = conversionResult.getBody().getLastUsedTimelineItem();
            }
        }
        System.out.println("[CHATConverter] Document parsed");
    }

    private void finalCleanup(){
        conversionResult.getBody().getCommonTimeline().removeInterpolatedTimes();
        conversionResult.getBody().removeEmptyTiers();
        conversionResult.getBody().smoothTimeline(0.1);
        System.out.println("[CHATConverter] Final cleanup performed");
    }




    

// ************************************************************************************

    /** normalizes the document, i.e. gets rid of white space, empty lines, etc. */
    private void normalize(){
        System.out.println("[CHATConverter] Started normalizing document");
        boolean CA = false;

        for (int pos=0; pos<size(); pos++){
            String line = elementAt(pos);
            if ((!CA) && (line.startsWith("@Options:\tCA"))){
                CA = true;
            }

            // replace all left white space with ordinary space
            StringBuffer clean = new StringBuffer();
            for (int pos2=0; pos2<line.length(); pos2++){
                char c = line.charAt(pos2);
                if (Character.isWhitespace(c)){
                    clean.append(" ");
                } 
                else {
                    clean.append(c);
                }
            }
            line = clean.toString();

            if (CA && line.matches("^\\s*\\(\\d{1,2}\\.\\d{0,2}\\)\\s*")){
                line = "*xPx: " + line;
            }
            
            // replace the original string with the normalized one (supposing there's something left...)
            if (line.length()<=0){
                removeElementAt(pos);
                pos--;
            } else {
                setElementAt(line, pos);
            }
        }

        // glue lines together that were broken up because of line wrapping
        for (int pos=1; pos<size(); pos++){
            String line = elementAt(pos);
            char firstChar = line.charAt(0);
            if ((!(firstChar=='@' || firstChar=='*' || firstChar=='%')) && (!(elementAt(pos-1).contains("\u0015")))){
                setElementAt(elementAt(pos-1) + line, pos-1);
                removeElementAt(pos);
                pos--;
            }
            if (CA) {
                // CA has identical speaker abbs sometimes:
                //*C:	                        ??hhhh?
                //*C:	+" ?I didn't expect you to be ho:me?.
                //*C:	?or ?something like?? [!] 34759_37479
                if (firstChar=='*'){
                    int i1 = elementAt(pos-1).indexOf(":");
                    int i2 = line.indexOf(":");
                    int i3 = elementAt(pos-1).indexOf("\u0015");
                    if (i1>0 && i2>0 && i3<0){
                        String previousSpeaker = elementAt(pos-1).substring(0,i1);
                        String thisSpeaker = line.substring(0,i2);
                        if (thisSpeaker.equals(previousSpeaker)){
                            String combinedLine = elementAt(pos-1) + line.substring(i2+1);
                            // replace all left white space with ordinary space
                            StringBuffer clean = new StringBuffer();
                            for (int pos2=0; pos2<combinedLine.length(); pos2++){
                                char c = combinedLine.charAt(pos2);
                                if (Character.isWhitespace(c)){
                                    clean.append(" ");
                                }
                                else {
                                    clean.append(c);
                                }
                            }
                            combinedLine = clean.toString();
                            setElementAt(elementAt(pos-1) + line, pos-1);
                            removeElementAt(pos);
                            pos--;
                        }
                }
                }

            }
        }
        System.out.println("[CHATConverter] Document normalized");
    }

    


    public static void writeHIATSegmentedCHATFile(BasicTranscription bt, File chatFile) throws SAXException, FSMException, JexmaraldaException, FileNotFoundException, UnsupportedEncodingException, IOException{
        HIATSegmentation segmentor = new HIATSegmentation();
        SegmentedTranscription st = segmentor.BasicToSegmented(bt);
        ListTranscription lt = st.toListTranscription(new SegmentedToListInfo(st, SegmentedToListInfo.HIAT_UTTERANCE_SEGMENTATION));
        lt.getBody().sort();
        // replace pause bullets: they cause trouble
        for (int pos=0; pos<lt.getBody().getNumberOfSpeakerContributions(); pos++){
            SpeakerContribution c = lt.getBody().getSpeakerContributionAt(pos);
            Vector v = c.getMain().getAllSegmentsWithName("HIAT:non-pho");
            for (Object o : v){
                AtomicTimedSegment ats = (AtomicTimedSegment)o;
                ats.setDescription(ats.getDescription().replace('\u2022', '#'));
            }
            for (int pos2=0; pos2<c.getNumberOfAnnotations(); pos2++){
                AbstractSegmentVector asv = c.getAnnotationAt(pos2);
                for (Object o2 : asv){
                    TimedAnnotation ta = (TimedAnnotation)o2;
                    ta.setDescription(ta.getDescription().replace('\u2022', '#'));
                }
            }
        }
        /*try {
            // Added 20-07-2010: Additional replacements HIAT > CHAT
            StylesheetFactory ssf = new StylesheetFactory(true);
            String result = ssf.applyInternalStylesheetToString("/org/exmaralda/partitureditor/jexmaralda/xsl/HIAT2CHAT.xsl", lt.toXML());
            // HAHAHA! Das geht nicht, weil ich keine List Transcriptions lesen kann
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        } */

        String chatText = CHATSegmentation.toText(lt, "HIAT:u");

        // Added 20-07-2010: Additional replacements HIAT > CHAT
        chatText = chatText.replaceAll("\\(\\(unv[^\\)]+\\)\\)", "xxx");
        System.out.println("started writing document " + chatFile.getAbsolutePath() + "...");
        FileOutputStream fos = new FileOutputStream(chatFile);
        fos.write(chatText.getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
    }

    public static void writeEventSegmentedCHATFile(BasicTranscription bt, File chatFile) throws SAXException, FSMException, JexmaraldaException, FileNotFoundException, UnsupportedEncodingException, IOException{
        SegmentedTranscription st = bt.toSegmentedTranscription();
        ListTranscription lt = st.toListTranscription(new SegmentedToListInfo(st, SegmentedToListInfo.EVENT_SEGMENTATION));
        lt.getBody().sort();
        String chatText = CHATSegmentation.toText(lt, "e");
        System.out.println("started writing document " + chatFile.getAbsolutePath() + "...");
        FileOutputStream fos = new FileOutputStream(chatFile);
        fos.write(chatText.getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
    }


        
    
    
   
}