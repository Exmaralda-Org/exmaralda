/*
 * Segmented2RDB.java
 *
 * Created on 27. Januar 2003, 11:14
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.io.*;
import java.util.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class Segmented2RDB {

    SegmentedTranscription transcription;   
    FileOutputStream trans_fos;
    FileOutputStream meta_fos;
    FileOutputStream speaker_fos;
    FileOutputStream lang_fos;
    FileOutputStream segments_fos;
    String currentTransName;
    int count = 0;
    String tab = "\t";
    String eol = System.getProperty("line.separator");

    /** Creates new Segmented2RDB */
    public Segmented2RDB() {
    }
    
    public void setTranscription(SegmentedTranscription t) {
        transcription = t;
    }
    
    public void openOutputFiles(String filename_base) throws FileNotFoundException {
        String transcriptionFilename = filename_base + "_trans.txt";
        String metaInfoFilename = filename_base + "_meta.txt";
        String speakerFilename = filename_base + "_speaker.txt";
        String langFilename = filename_base + "_lang.txt";
        String segmentFilename = filename_base + "_segments.txt";
        trans_fos = new FileOutputStream(new File(transcriptionFilename),false);        
        meta_fos = new FileOutputStream(new File(metaInfoFilename),false);        
        speaker_fos = new FileOutputStream(new File(speakerFilename),false);        
        lang_fos = new FileOutputStream(new File(langFilename),false);        
        segments_fos = new FileOutputStream(new File(segmentFilename),false);        
    }
    
    public void closeOutputFiles() throws IOException {
        trans_fos.close();
        meta_fos.close();
        speaker_fos.close();
        lang_fos.close();
        segments_fos.close();
    }
    
    public void add(SegmentedTranscription t) throws IOException {
        transcription = t;
        processTrans();
        processMeta(); 
        processSpeakers();        
        processSegments();
    }
    
    private void processTrans() throws IOException {
        // transcriptName - projectName - convention - comment - referenced file
        currentTransName = transcription.getHead().getMetaInformation().getTranscriptionName();
        if ((currentTransName == null) || (currentTransName.length()==0)){
            count++;
            currentTransName = "Trans" + Integer.toString(count);
        }
        MetaInformation meta = transcription.getHead().getMetaInformation();
        String[] texts = {currentTransName,  meta.getProjectName(), meta.getTranscriptionConvention(), meta.getComment(), meta.getReferencedFile()};
        trans_fos.write(makeTabString(texts).getBytes());
    }
    
    private void processMeta() throws IOException{
        UDInformationHashtable meta = transcription.getHead().getMetaInformation().getUDMetaInformation();
        // transcriptNo - AttributeName - AttributeValue       
        String[] attNames = meta.getAllAttributes();
        for (int pos=0; pos<attNames.length; pos++){
            String value = meta.getValueOfAttribute(attNames[pos]);
            String[] texts = {currentTransName, attNames[pos], value};
            meta_fos.write(makeTabString(texts).getBytes());            
        }        
    }
    
    private void processSpeakers() throws IOException{
        Speakertable spt = transcription.getHead().getSpeakertable();
        for (int pos=0; pos<spt.getNumberOfSpeakers(); pos++){
            Speaker s = spt.getSpeakerAt(pos);
            // transcriptNo - speakerID - Abbreviation - Sex - Comment
            String speakerID = currentTransName + "." + s.getID();
            StringBuffer sb = new StringBuffer();
            sb.append(s.getSex());
            String sex = sb.toString();
            String[] texts = {currentTransName, speakerID, s.getAbbreviation(), sex, s.getComment()};
            speaker_fos.write(makeTabString(texts).getBytes());
            
            String[] allLU = s.getLanguagesUsed().getAllLanguages();
            for (int i=0; i<allLU.length; i++){
                String[] t = {currentTransName, speakerID, "LU", allLU[i]};
                lang_fos.write(makeTabString(t).getBytes());
            }

            String[] allL1 = s.getL1().getAllLanguages();
            for (int i=0; i<allL1.length; i++){
                String[] t = {currentTransName, speakerID, "L1", allL1[i]};
                lang_fos.write(makeTabString(t).getBytes());
            }

            String[] allL2 = s.getL2().getAllLanguages();
            for (int i=0; i<allL2.length; i++){
                String[] t = {currentTransName, speakerID, "L2", allL2[i]};
                lang_fos.write(makeTabString(t).getBytes());
            }
        }
    }
    
    private String makeTabString(String[] texts){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<texts.length; pos++){
            sb.append(texts[pos]);
            if (pos<texts.length-1){
                sb.append(tab);
            }
        }
        sb.append(eol);
        return sb.toString();
    }
    
    private void processSegments() throws IOException{
        SegmentedBody body = transcription.getBody();
        Hashtable tlis = body.makeTLIHashtable();
        for (int pos=0; pos<body.size(); pos++){
            SegmentedTier st = (SegmentedTier)(body.elementAt(pos));
            Vector segmentList = new Vector();
            for (int pos2=0; pos2<st.size(); pos2++){
                Object o = st.elementAt(pos2);
                if (o instanceof Segmentation){
                    Segmentation s = (Segmentation)o;
                    if (s.getName().equals("Turn_Event")) continue;
                    for (int pos3=0; pos3<s.getNumberOfSegments(); pos3++){
                        Object o2 = s.elementAt(pos3);
                        if (o2 instanceof TimedSegment){
                            TimedSegment ts = (TimedSegment)o2;
                            segmentList.addAll(ts.makeSegmentList());
                        }
                        else if (o2 instanceof AtomicTimedSegment){
                            AtomicTimedSegment ats = (AtomicTimedSegment)o2;
                            String[] entry = {ats.getName(), ats.getDescription(), ats.getStart(), ats.getEnd(), ats.getID(), "nil", "0","s"};
                            segmentList.add(entry);
                        }
                    }
                }
                else if (o instanceof Annotation){
                    Annotation a = (Annotation)o;
                    for (int pos3=0; pos3<a.getNumberOfSegments(); pos3++){
                        Object o2 = a.elementAt(pos3);
                        if (o2 instanceof TimedAnnotation){
                            TimedAnnotation ta = (TimedAnnotation)o2;
                            String[] entry = {a.getName(), ta.getDescription(), ta.getStart(), ta.getEnd(), ta.getID(), "nil", "0", "a"};
                            segmentList.add(entry);                            
                        }        
                    }
                }
            }
            for (int i=0; i<segmentList.size(); i++){
                String[] entry = (String[])(segmentList.elementAt(i));
                String[] complete = new String[16];
                complete[0] = currentTransName;
                complete[1] = entry[7];  // segment vs. annotation
                complete[2] = st.getCategory();
                complete[3] = st.getType();
                complete[4] = currentTransName + "." + st.getSpeaker();
                complete[5] = entry[0]; // Name
                complete[6] = entry[1]; // Description
                complete[7] = currentTransName + "." + entry[2]; // start
                complete[8] = currentTransName + "." + (String)(tlis.get(entry[2]));  // common start
                complete[9] = currentTransName + "." + entry[3]; // end
                complete[10] = currentTransName + "." + entry[4]; // id
                if (!entry[5].equals("nil")){
                    complete[11] = currentTransName + "." + entry[5]; // parent id
                } else {
                    complete[11] = "";
                }
                complete[12] = entry[6];   // pos in parent
                if (complete[8]!=null){
                    //complete[13]="<a href=\"" + ".\\" + currentTransName + ".html#" + (String)(tlis.get(entry[2])) + "\">" + "Go!" + "</a>";
                    complete[13] = "Partitur#" + ".\\" + "html\\partitur\\" + currentTransName + ".html#" + (String)(tlis.get(entry[2]));
                    complete[14] = "TurnList#" + ".\\" + "html\\turnlist\\" + currentTransName + ".html#" + (String)(tlis.get(entry[2]));
                    complete[15] = "UtteranceList#" + ".\\" + "html\\utterancelist\\" + currentTransName + ".html#" + (String)(tlis.get(entry[2]));
                    
                } else {
                    complete[13] = "";
                    complete[14] = "";
                    complete[15] = "";
                }
                segments_fos.write(makeTabString(complete).getBytes());                
            }
        }
    }

}
