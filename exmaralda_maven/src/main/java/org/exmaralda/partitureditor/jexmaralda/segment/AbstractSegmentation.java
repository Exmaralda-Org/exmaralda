/*
 * AbstractSegmentation.java
 *
 * Created on 1. April 2004, 18:57
 */

package org.exmaralda.partitureditor.jexmaralda.segment;

import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.fsm.*;
/**
 *
 * @author  thomas
 */
public abstract class AbstractSegmentation {

    
    public Hashtable beforeAugment = new Hashtable();
    public Hashtable afterAugment = new Hashtable();
    public String pathToExternalFSM = "";
    
    public static final int NO_SEGMENTATION = 0;
    public static final int HIAT_SEGMENTATION = 1;
    public static final int DIDA_SEGMENTATION = 2;
    public static final int CHAT_SEGMENTATION = 3;
    public static final int GAT_SEGMENTATION = 4;      
    public static final int IPA_SEGMENTATION = 5;      
    public static final int GENERIC_SEGMENTATION = 6;
    public static final int GAT_MINIMAL_SEGMENTATION = 7;
    public static final int CHAT_MINIMAL_SEGMENTATION = 8;
    public static final int INEL_EVENT_BASED = 9;

    /** Creates a new instance of AbstractSegmentation
     * @param ptef */
    public AbstractSegmentation(String ptef) {
        pathToExternalFSM = ptef;        
    }
    
    public AbstractSegmentation(){        
    }
    
    public abstract Vector getSegmentationErrors(BasicTranscription bt) throws SAXException;
    
    public abstract SegmentedTranscription BasicToSegmented(BasicTranscription bt) throws SAXException, FSMException;

    public static int getSegmentationCode(String segName){
        if (segName.equals("HIAT")) return HIAT_SEGMENTATION;
        if (segName.equals("DIDA")) return DIDA_SEGMENTATION;
        if (segName.equals("GAT")) return GAT_SEGMENTATION;
        if (segName.equals("CHAT")) return CHAT_SEGMENTATION;
        if (segName.equals("IPA")) return IPA_SEGMENTATION;
        if (segName.equals("GENERIC")) return GENERIC_SEGMENTATION;
        if (segName.equals("cGAT_MINIMAL")) return GAT_MINIMAL_SEGMENTATION;
        if (segName.equals("CHAT_MINIMAL")) return CHAT_MINIMAL_SEGMENTATION;
        if (segName.equals("INEL_EVENT_BASED")) return INEL_EVENT_BASED;
        return NO_SEGMENTATION;
    }
    
    public static AbstractSegmentation getSegmentationAlgorithm(int segmentationCode){
        switch (segmentationCode){
            case AbstractSegmentation.HIAT_SEGMENTATION : return new HIATSegmentation();
            case AbstractSegmentation.DIDA_SEGMENTATION : return new DIDASegmentation();
            case AbstractSegmentation.GAT_SEGMENTATION : return new GATSegmentation();
            case AbstractSegmentation.CHAT_SEGMENTATION : return new CHATSegmentation();
            case AbstractSegmentation.IPA_SEGMENTATION : return new IPASegmentation();
            case AbstractSegmentation.GENERIC_SEGMENTATION : return new GenericSegmentation();
            case AbstractSegmentation.GAT_MINIMAL_SEGMENTATION : return new GATMinimalSegmentation();
            case AbstractSegmentation.CHAT_MINIMAL_SEGMENTATION : return new CHATMinimalSegmentation();            
            case AbstractSegmentation.INEL_EVENT_BASED : return new InelEventBasedSegmentation();            
        }
        return null;
    }
    
    public static String getWordSegmentationName(int segmentationCode) {
        switch (segmentationCode){
            case AbstractSegmentation.HIAT_SEGMENTATION : return "SpeakerContribution_Utterance_Word";
            case AbstractSegmentation.DIDA_SEGMENTATION : return "SpeakerContribution_Word";
            case AbstractSegmentation.IPA_SEGMENTATION : return "SegmentChain_Word_Syllable";
            case AbstractSegmentation.GENERIC_SEGMENTATION : return "SpeakerContribution_Word";
            case AbstractSegmentation.GAT_MINIMAL_SEGMENTATION : return "SpeakerContribution_Word";
            case AbstractSegmentation.CHAT_MINIMAL_SEGMENTATION : return "SpeakerContribution_Utterance_Word";     
            case AbstractSegmentation.INEL_EVENT_BASED : return "SpeakerContribution_Utterance_Word";     
            
        }
        return null;
    }

    
}
