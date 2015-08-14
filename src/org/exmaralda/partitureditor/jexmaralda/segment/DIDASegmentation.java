/*
 * DIDASegmentation.java
 *
 * Created on 12. Maerz 2004, 10:15
 */

package org.exmaralda.partitureditor.jexmaralda.segment;

import org.exmaralda.partitureditor.fsm.FSMSaxReader;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.fsm.FiniteStateMachine;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.fsm.*;
import java.util.*;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */

public class DIDASegmentation extends AbstractSegmentation {
    
    private final String wordFSM = "/org/exmaralda/partitureditor/fsm/xml/DIDA_Word.xml";
    private static String WORD_INTERNAL_CHAR_PATTERN = "[*\\u0022:]";
    private static char[] WORD_INTERNAL_CHARS = {'*','"', ':'};

    
    public DIDASegmentation() {
        super();
    }
    
    public DIDASegmentation(String ptef){
        super(ptef);
    }

    public SegmentedTranscription BasicToSegmented(BasicTranscription bt) throws SAXException, FSMException {
         SegmentedTranscription st = bt.toSegmentedTranscription();
         FSMSaxReader sr = new FSMSaxReader();

         FiniteStateMachine fsm = null;
         
         if (pathToExternalFSM.length()<=0){
            java.io.InputStream is2 = getClass().getResourceAsStream(wordFSM);
            fsm = sr.readFromStream(is2);
         } else {
             fsm = sr.readFromFile(pathToExternalFSM);
         }
         
         for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
             SegmentedTier t = (SegmentedTier)(st.getBody().elementAt(pos));
             if (t.hasSegmentationWithName("SpeakerContribution_Event")){
                 Segmentation seg = t.getSegmentationWithName("SpeakerContribution_Event");
                 Segmentation seg2 = new Segmentation();
                 seg2.setName("SpeakerContribution_Word");
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
    
    public static void cleanWordList(SegmentList wordlist){
        for (int pos=0; pos<wordlist.size(); pos++){
            Describable word = (Describable)(wordlist.elementAt(pos));
            String text = word.getDescription();
            text = text.replaceAll(WORD_INTERNAL_CHAR_PATTERN, "");
            word.setDescription(text);
        }
    }
    
    public Vector getSegmentationErrors(BasicTranscription bt) throws SAXException {
         Vector result = new Vector();
         SegmentedTranscription st = bt.toSegmentedTranscription();
         FSMSaxReader sr = new FSMSaxReader();

         FiniteStateMachine fsm = null;
       
         if (pathToExternalFSM.length()<=0){
            java.io.InputStream is2 = getClass().getResourceAsStream(wordFSM);
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
