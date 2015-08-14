/*
 * HIATSegmentation.java
 *
 * Created on 2. Juli 2003, 11:24
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
public class HIATSegmentation extends AbstractSegmentation {
    
    
    public static String utteranceWordFSM = "/org/exmaralda/partitureditor/fsm/xml/HIAT_UtteranceWord.xml";
    public String utteranceFSM = "/org/exmaralda/partitureditor/fsm/xml/HIAT_Utterance.xml";
    
   
    /** Creates a new instance of HIATSegmentation */
    public HIATSegmentation() {        
    }
    
    public HIATSegmentation(String ptef){
        super(ptef);
    }
        
    
    public SegmentedTranscription BasicToSegmented(BasicTranscription bt) throws SAXException, FSMException {
         SegmentedTranscription st = bt.toSegmentedTranscription();
         FSMSaxReader sr = new FSMSaxReader();

         FiniteStateMachine fsm = null;
         
         if (pathToExternalFSM.length()<=0){
            java.io.InputStream is2 = getClass().getResourceAsStream(utteranceWordFSM);
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
    
    @Override
    public Vector getSegmentationErrors(BasicTranscription bt) throws SAXException {
         Vector result = new Vector();
         SegmentedTranscription st = bt.toSegmentedTranscription();
         FSMSaxReader sr = new FSMSaxReader();

         FiniteStateMachine fsm = null;
       
         if (pathToExternalFSM.length()<=0){
            java.io.InputStream is2 = getClass().getResourceAsStream(utteranceWordFSM);
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
    
    public ListTranscription BasicToUtteranceList(BasicTranscription bt) throws SAXException, FSMException {
         SegmentedTranscription st = BasicToSegmented(bt);
         beforeAugment = st.getBody().makeTLIHashtable();
         st.getBody().augmentCommonTimeline();
         
         SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.HIAT_UTTERANCE_SEGMENTATION);
         ListTranscription lt = st.toListTranscription(info);
         lt.getBody().sort();
         return lt;
    }
        
    
}
