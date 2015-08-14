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

    /** Creates a new instance of AbstractSegmentation */
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
        return NO_SEGMENTATION;
    }
    
}
