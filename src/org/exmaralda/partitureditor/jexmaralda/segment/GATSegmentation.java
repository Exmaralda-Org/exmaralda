/*
 * GATSegmentation.java
 *
 * Created on 19. Maerz 2004, 14:07
 */

package org.exmaralda.partitureditor.jexmaralda.segment;

import org.exmaralda.partitureditor.fsm.FSMSaxReader;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.fsm.FiniteStateMachine;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class GATSegmentation extends AbstractSegmentation {
    
    private final String intonationUnitFSM = "/org/exmaralda/partitureditor/fsm/xml/GAT_Phrasierungseinheit.xml";
    
    /** Creates a new instance of GATSegmentation */
    public GATSegmentation() {
    }
    
    public GATSegmentation(String ptef){
        super(ptef);
    }

    public SegmentedTranscription BasicToSegmented(BasicTranscription bt) throws SAXException, FSMException {
         SegmentedTranscription st = bt.toSegmentedTranscription();
         FSMSaxReader sr = new FSMSaxReader();

         FiniteStateMachine fsm = null;
         
         if (pathToExternalFSM.length()<=0){
            java.io.InputStream is2 = getClass().getResourceAsStream(intonationUnitFSM);
            fsm = sr.readFromStream(is2);
         } else {
             fsm = sr.readFromFile(pathToExternalFSM);
         }
         
         for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
             SegmentedTier t = (SegmentedTier)(st.getBody().elementAt(pos));
             if (t.hasSegmentationWithName("SpeakerContribution_Event")){
                 Segmentation seg = t.getSegmentationWithName("SpeakerContribution_Event");
                 Segmentation seg2 = new Segmentation();
                 seg2.setName("SpeakerContribution_IntonationUnit");
                 seg2.setTierReference(seg.getTierReference());                 
                 Vector tlfs = FSMSegmentor.segment(seg,seg2,fsm,t.getID());
                 t.insertElementAt(seg2,0);
                 for (int i=0; i<tlfs.size(); i++){
                     t.addTimelineFork((TimelineFork)(tlfs.elementAt(i)));
                 }
             }
         }
         st.getBody().makeIDs();        
         return st;
    }    
    
    public ListTranscription BasicToIntonationUnitList(BasicTranscription bt) throws FSMException, SAXException, JexmaraldaException {
         SegmentedTranscription st = BasicToSegmented(bt);
         String[] flatMergeSegmentations = {"SpeakerContribution_IntonationUnit", "SpeakerContribution_Event"};
         String[] flatMergeSegments = {"e", "GAT:pe"};
         for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
             SegmentedTier t = (SegmentedTier)(st.getBody().elementAt(pos));
              if (t.hasSegmentationWithName("SpeakerContribution_Event")){
                 t.flatMerge(flatMergeSegmentations, flatMergeSegments, "IntonationUnit_Event_Merged", "e.pe");
                 t.hierarchicalMerge("SpeakerContribution_IntonationUnit", 
                                     "IntonationUnit_Event_Merged", 
                                     "SpeakerContribution_IntonationUnit_Event", 
                                     "GAT:pe",
                                     "e.pe");
                 
              }
         }
         
         beforeAugment = st.getBody().makeTLIHashtable();
         st.getBody().augmentCommonTimeline();
         
         SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.GAT_INTONATIONUNIT_SEGMENTATION);
         ListTranscription lt = st.toListTranscription(info);
         lt.getBody().sort();
         return lt;
    }
    
    public static String toText(ListTranscription lt) throws JexmaraldaException {
        Speakertable speakertable = lt.getHead().getSpeakertable();
        // determine the longest speaker abbreviation
        int longestAbbLength = 0;
        for (int pos=0; pos<speakertable.getNumberOfSpeakers(); pos++){
            longestAbbLength = Math.max(longestAbbLength, speakertable.getSpeakerAt(pos).getAbbreviation().length());
        }
        ListBody body = lt.getBody();
        Hashtable overlaps = new Hashtable();
        // count the frequency of start/end combinations
        for (int pos=0; pos<body.getNumberOfSpeakerContributions(); pos++){
            SpeakerContribution sc = body.getSpeakerContributionAt(pos);
            TimedSegment ts = sc.getMain();
            Vector v = ts.getAllSegmentsWithName("e.pe");
            for (int e=0; e<v.size(); e++){
                TimedSegment epe = (TimedSegment)(v.elementAt(e));
                String key = epe.getStart() + "#" + epe.getEnd();
                if (overlaps.containsKey(key)){
                    Integer i = (Integer)(overlaps.get(key));
                    Integer i_plus_one = new Integer(i.intValue()+1);
                    overlaps.put(key, i_plus_one);
                } else {
                    overlaps.put(key, new Integer(1));
                }
            }
        }
        
        StringBuffer output = new StringBuffer();
        String lastSpeaker = "";
        for (int pos=0; pos<body.getNumberOfSpeakerContributions(); pos++){
            // Zeilennummerierung
            if (pos+1<10) output.append("0");
            if (pos+1<100) output.append("0");
            output.append(Integer.toString(pos+1));
            output.append("  ");
            
            // Sprecherkuerzel
            SpeakerContribution sc = body.getSpeakerContributionAt(pos);
            String thisSpeaker = sc.getSpeaker();
            if (thisSpeaker==null){
                
            } else if (!thisSpeaker.equals(lastSpeaker)){
                output.append(speakertable.getSpeakerWithID(thisSpeaker).getAbbreviation());
                output.append(":");
            } else {
                for (int s=0; s<=speakertable.getSpeakerWithID(thisSpeaker).getAbbreviation().length(); s++){
                    output.append(" ");
                }
            }
            // Sprecherkuerzelausgleich
            if (thisSpeaker!=null){
                for (int i=0; i<longestAbbLength-speakertable.getSpeakerWithID(thisSpeaker).getAbbreviation().length(); i++){
                    output.append(" ");
                }
            }
            output.append("  ");
            
            // Text
            TimedSegment ts = sc.getMain();
            Vector v = ts.getAllSegmentsWithName("e.pe");
            for (int e=0; e<v.size(); e++){
                TimedSegment epe = (TimedSegment)(v.elementAt(e));
                String key = epe.getStart() + "#" + epe.getEnd();
                int howOften = ((Integer)(overlaps.get(key))).intValue();
                if (howOften>1) output.append("[");
                output.append(epe.getDescription());
                if (howOften>1) output.append("]");                
            }
            
            output.append(System.getProperty("line.separator"));
            lastSpeaker = thisSpeaker;
        }

        return output.toString();
    }
    
    public Vector getSegmentationErrors(BasicTranscription bt) throws SAXException {
         Vector result = new Vector();
         SegmentedTranscription st = bt.toSegmentedTranscription();
         FSMSaxReader sr = new FSMSaxReader();

         FiniteStateMachine fsm = null;
       
         if (pathToExternalFSM.length()<=0){
            java.io.InputStream is2 = getClass().getResourceAsStream(intonationUnitFSM);
            fsm = sr.readFromStream(is2);
         } else {
             fsm = sr.readFromFile(pathToExternalFSM);
         }

         
         for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
             SegmentedTier t = (SegmentedTier)(st.getBody().elementAt(pos));
             if (t.hasSegmentationWithName("SpeakerContribution_Event")){
                 Segmentation seg = t.getSegmentationWithName("SpeakerContribution_Event");
                 Vector errors = FSMSegmentor.getSegmentationErrors(seg,fsm);
                 result.addAll(errors);
             }
         }
         return result;
    }
    
    
}
