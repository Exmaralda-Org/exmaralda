/*
 * SegmentedToListInfo.java
 *
 * Created on 27. August 2002, 15:55
 */

package org.exmaralda.partitureditor.jexmaralda.segment;

import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  Thomas
 */
public class SegmentedToListInfo {
    
    public static final int TURN_SEGMENTATION = 0;
    public static final int HIAT_UTTERANCE_SEGMENTATION = 1;
    public static final int CHAT_UTTERANCE_SEGMENTATION = 2;
    public static final int GAT_INTONATIONUNIT_SEGMENTATION = 3;
    public static final int EVENT_SEGMENTATION = 4;
    public static final int cGAT_MINIMAL_SEGMENTATION = 5;
    public static final int CHAT_UTTERANCE_WORD_SEGMENTATION = 6;
    
    
    private Hashtable main;
    private Hashtable dependents;
    private Hashtable annotations;
    
    /** Creates a new instance of SegmentedToListInfo */
    public SegmentedToListInfo() {
        main = new Hashtable();
        dependents = new Hashtable();
        annotations = new Hashtable();
    }
    
    /** creates a default conversion info of the given type */
    public SegmentedToListInfo(SegmentedTranscription st, int type){
        this();
        switch (type){
            case TURN_SEGMENTATION :
                for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
                    SegmentedTier tier = (SegmentedTier)(st.getBody().elementAt(pos));
                    for (int pos2=0; pos2<tier.size(); pos2++){
                        AbstractSegmentVector asv = (AbstractSegmentVector)(tier.elementAt(pos2));
                        if ((!(asv instanceof Annotation)) &&
                            (asv.getName().equals("SpeakerContribution_Event")) &&
                            (tier.getSpeaker()!=null)){
                               addMain(tier.getSpeaker(), tier.getID(), "SpeakerContribution_Event", "sc");
                        }
                    }
                 }
                 for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
                     SegmentedTier tier = (SegmentedTier)(st.getBody().elementAt(pos));
                     for (int pos2=0; pos2<tier.size(); pos2++){
                         AbstractSegmentVector asv = (AbstractSegmentVector)(tier.elementAt(pos2));
                         if (!(asv instanceof Annotation)){
                             if (((asv.getName().equals("Event")) &&
                                 (tier.getSpeaker()!=null)) && (main.containsKey(tier.getSpeaker()))){
                             addDependent(tier.getSpeaker(),tier.getID(),"Event");
                         }
                     } else if (tier.getSpeaker()!=null){
                         addAnnotation(tier.getSpeaker(), tier.getID(), asv.getName());
                     }
                  }
                 }
                 break;
            case HIAT_UTTERANCE_SEGMENTATION :
                for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
                    SegmentedTier tier = (SegmentedTier)(st.getBody().elementAt(pos));
                    for (int pos2=0; pos2<tier.size(); pos2++){
                        AbstractSegmentVector asv = (AbstractSegmentVector)(tier.elementAt(pos2));
                        if ((!(asv instanceof Annotation)) &&
                         (asv.getName().equals("SpeakerContribution_Utterance_Word")) &&
                         (tier.getSpeaker()!=null)){
                                //addMain(tier.getSpeaker(), tier.getID(), "SpeakerContribution_Utterance", "HIAT:u");
                                addMain(tier.getSpeaker(), tier.getID(), "SpeakerContribution_Utterance_Word", "HIAT:u");
                         }
                    }
                   }
                   for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
                        SegmentedTier tier = (SegmentedTier)(st.getBody().elementAt(pos));
                        for (int pos2=0; pos2<tier.size(); pos2++){
                            AbstractSegmentVector asv = (AbstractSegmentVector)(tier.elementAt(pos2));
                            if (!(asv instanceof Annotation)){
                                if (((asv.getName().equals("Event")) &&
                                    (tier.getSpeaker()!=null)) && (main.containsKey(tier.getSpeaker()))){
                                    addDependent(tier.getSpeaker(),tier.getID(),"Event");
                                }
                            } else if (tier.getSpeaker()!=null){
                                addAnnotation(tier.getSpeaker(), tier.getID(), asv.getName());
                            }
                        }
                    }
                    break;
        case CHAT_UTTERANCE_SEGMENTATION :
            for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
                SegmentedTier tier = (SegmentedTier)(st.getBody().elementAt(pos));
                for (int pos2=0; pos2<tier.size(); pos2++){
                    AbstractSegmentVector asv = (AbstractSegmentVector)(tier.elementAt(pos2));
                    if ((!(asv instanceof Annotation)) &&
                     (asv.getName().equals("SpeakerContribution_Utterance")) &&
                     (tier.getSpeaker()!=null)){
                            addMain(tier.getSpeaker(), tier.getID(), "SpeakerContribution_Utterance_Event", "CHAT:u");
                    }
                }
           }
           for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
                SegmentedTier tier = (SegmentedTier)(st.getBody().elementAt(pos));
                for (int pos2=0; pos2<tier.size(); pos2++){
                    AbstractSegmentVector asv = (AbstractSegmentVector)(tier.elementAt(pos2));
                    if (!(asv instanceof Annotation)){
                        if (((asv.getName().equals("Event")) &&
                            (tier.getSpeaker()!=null)) && (main.containsKey(tier.getSpeaker()))){
                            addDependent(tier.getSpeaker(),tier.getID(),"Event");
                        }
                    } else if (tier.getSpeaker()!=null){
                        addAnnotation(tier.getSpeaker(), tier.getID(), asv.getName());
                    }
                }
            }
            break;
            case GAT_INTONATIONUNIT_SEGMENTATION :
                for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
                   SegmentedTier tier = (SegmentedTier)(st.getBody().elementAt(pos));
                   for (int pos2=0; pos2<tier.size(); pos2++){
                     AbstractSegmentVector asv = (AbstractSegmentVector)(tier.elementAt(pos2));
                     if ((!(asv instanceof Annotation))
                         && (asv.getName().equals("SpeakerContribution_IntonationUnit_Event"))
                         && (tier.getSpeaker()!=null)){
                            addMain(tier.getSpeaker(), tier.getID(), "SpeakerContribution_IntonationUnit_Event", "GAT:pe");
                        }
                    }
                }
                break;
           case EVENT_SEGMENTATION :
                    for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
                        SegmentedTier tier = (SegmentedTier)(st.getBody().elementAt(pos));
                        for (int pos2=0; pos2<tier.size(); pos2++){
                            AbstractSegmentVector asv = (AbstractSegmentVector)(tier.elementAt(pos2));
                            if ((!(asv instanceof Annotation)) &&
                             (asv.getName().equals("SpeakerContribution_Event")) &&
                             (tier.getSpeaker()!=null)){
                                    addMain(tier.getSpeaker(), tier.getID(), "SpeakerContribution_Event", "e");
                            }
                        }
                   }
                   for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
                        SegmentedTier tier = (SegmentedTier)(st.getBody().elementAt(pos));
                        for (int pos2=0; pos2<tier.size(); pos2++){
                            AbstractSegmentVector asv = (AbstractSegmentVector)(tier.elementAt(pos2));
                            if (!(asv instanceof Annotation)){
                                if (((asv.getName().equals("Event")) &&
                                    (tier.getSpeaker()!=null)) && (main.containsKey(tier.getSpeaker()))){
                                    addDependent(tier.getSpeaker(),tier.getID(),"Event");
                                }
                            } else if (tier.getSpeaker()!=null){
                                addAnnotation(tier.getSpeaker(), tier.getID(), asv.getName());
                            }
                        }
                    }
                    break;
        }
    }
    
    public void addMain(String speakerID, String tierID, String segmentationName, String segmentName){
        String[] value = {tierID, segmentationName, segmentName};
        main.put(speakerID, value);
        dependents.put(speakerID, new Vector());
        annotations.put(speakerID,new Vector());
    }
    
    public void addAnnotation(String speakerID, String tierID, String segmentationName){
        String[] value = {tierID, segmentationName};
        ((Vector)(annotations.get(speakerID))).addElement(value);
    }
    
    public void addDependent(String speakerID, String tierID, String segmentationName){
        String[] value = {tierID, segmentationName};
        ((Vector)(dependents.get(speakerID))).addElement(value);
    }
    
    public String[] getMainForSpeaker(String speakerID){
        return (String[])(main.get(speakerID));
    }
    
    public Vector getDependentsForSpeaker(String speakerID){
        return (Vector)(dependents.get(speakerID));
    }
    
    public Vector getAnnotationsForSpeaker(String speakerID){
        return (Vector)(annotations.get(speakerID));
    }
}
