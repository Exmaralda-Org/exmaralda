/*
 * MergeTest.java
 *
 * Created on 18. Maerz 2004, 11:14
 */

package org.exmaralda.partitureditor.jexmaralda;

import org.exmaralda.partitureditor.fsm.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  thomas
 */
public class MergeTest {
    
    /** Creates a new instance of MergeTest */
    public MergeTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        /*SegmentedTranscriptionSaxReader str = new SegmentedTranscriptionSaxReader();
        SegmentedTranscription st = str.readFromFile("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\GAT_SEG.xml");
        st.getBody().augmentCommonTimeline();
        //SegmentedTier tier = (SegmentedTier)(st.getBody().elementAt(0));
        String[] nos = {"SpeakerContribution_IntonationUnit", "SpeakerContribution_Event"};
        String[] noss = {"e", "GAT:pe"};
        for (int pos=0; pos<st.getBody().size(); pos++){
            SegmentedTier tier = (SegmentedTier)(st.getBody().elementAt(pos));
            try{
                tier.flatMerge(nos, noss, "IntonationUnit_Event_Merged", "e.pe");
            } catch (JexmaraldaException je){            
                System.out.println("Falscher Fehler bei flatMerge: " + je.getMessage());
            }
            try{
                tier.hierarchicalMerge( "SpeakerContribution_IntonationUnit", 
                                        "IntonationUnit_Event_Merged", 
                                        "Speaker_Contribution_Utterance_Event", 
                                        "GAT:pe",
                                        "e.pe");
            } catch (JexmaraldaException je){            
                System.out.println("Falscher Fehler bei hierarchicalMerge: " + je.getMessage());
            }
        }
        st.writeXMLToFile("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\gat_seg_merge.xml", "none");*/
        BasicTranscription bt = new BasicTranscription("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\GAT.xml");
        //ListTranscription lt = new GATSegmentation().BasicToUtteranceList(bt);
        //lt.writeXMLToFile("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\GAT_list.xml", "none");
        //GATSegmentation.writeText("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\GAT.txt", lt);
    }
    
}
