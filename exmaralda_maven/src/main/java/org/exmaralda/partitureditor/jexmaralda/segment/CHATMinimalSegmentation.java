/*
 * CHATSegmentation.java
 *
 * Created on 23. Juli 2003, 12:59
 */

package org.exmaralda.partitureditor.jexmaralda.segment;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.fsm.*;
import java.util.*;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class CHATMinimalSegmentation extends AbstractSegmentation {
    
    //private final String utteranceWordFSM = "/org/exmaralda/partitureditor/fsm/xml/HIAT_UtteranceWord.xml";
    private final String utteranceFSM = "/org/exmaralda/partitureditor/fsm/xml/CHAT_UtteranceWord.xml";
    
    
    /** Creates a new instance of HIATSegmentation */
    public CHATMinimalSegmentation() {
    }
    
    public CHATMinimalSegmentation(String ptef){
        super(ptef);
    }

    public SegmentedTranscription BasicToSegmented(BasicTranscription bt) throws SAXException, FSMException {
         SegmentedTranscription st = bt.toSegmentedTranscription();
         FSMSaxReader sr = new FSMSaxReader();

         FiniteStateMachine fsm = null;
         
         if (pathToExternalFSM.length()<=0){
            java.io.InputStream is2 = getClass().getResourceAsStream(utteranceFSM);
            fsm = sr.readFromStream(is2);
         } else {
             fsm = sr.readFromFile(pathToExternalFSM);
         }
         
         
         for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
             SegmentedTier t = (SegmentedTier)(st.getBody().elementAt(pos));
             if (t.hasSegmentationWithName("SpeakerContribution_Event")){
                 Segmentation seg = t.getSegmentationWithName("SpeakerContribution_Event");
                 Segmentation seg2 = new Segmentation();
                 seg2.setName("SpeakerContribution_Utterance_Word");
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
    
    public ListTranscription BasicToUtteranceList(BasicTranscription bt) throws FSMException, SAXException, JexmaraldaException {
         SegmentedTranscription st = BasicToSegmented(bt);
         String[] flatMergeSegmentations = {"SpeakerContribution_Utterance_Word", "SpeakerContribution_Event"};
         String[] flatMergeSegments = {"e", "CHAT:u"};
         for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
             SegmentedTier t = (SegmentedTier)(st.getBody().elementAt(pos));
              if (t.hasSegmentationWithName("SpeakerContribution_Event")){
                 t.flatMerge(flatMergeSegmentations, flatMergeSegments, "Utterance_Event_Merged", "e.u");
                 t.hierarchicalMerge("SpeakerContribution_Utterance_Word",
                                     "Utterance_Event_Merged", 
                                     "SpeakerContribution_Utterance_Event", 
                                     "CHAT:u",
                                     "e.u");
                 
              }
         }
         
         beforeAugment = st.getBody().makeTLIHashtable();
         st.getBody().augmentCommonTimeline();
         
         SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.CHAT_UTTERANCE_WORD_SEGMENTATION);
         ListTranscription lt = st.toListTranscription(info);
         lt.getBody().sort(true);
         return lt;
    }


    public Vector getSegmentationErrors(BasicTranscription bt) throws SAXException {
         Vector result = new Vector();
         SegmentedTranscription st = bt.toSegmentedTranscription();
         FSMSaxReader sr = new FSMSaxReader();

         FiniteStateMachine fsm = null;
       
         if (pathToExternalFSM.length()<=0){
            java.io.InputStream is2 = getClass().getResourceAsStream(utteranceFSM);
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
