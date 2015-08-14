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
public class CHATSegmentation extends AbstractSegmentation {
    
    //private final String utteranceWordFSM = "/org/exmaralda/partitureditor/fsm/xml/HIAT_UtteranceWord.xml";
    private final String utteranceFSM = "/org/exmaralda/partitureditor/fsm/xml/CHAT_Utterance.xml";
    
    final static char SOUND_BULLET_CHARACTER='\u0015';
    
    public boolean lastSortWasComplete = false;
    
    /** Creates a new instance of HIATSegmentation */
    public CHATSegmentation() {
    }
    
    public CHATSegmentation(String ptef){
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
                 seg2.setName("SpeakerContribution_Utterance");
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
         String[] flatMergeSegmentations = {"SpeakerContribution_Utterance", "SpeakerContribution_Event"};
         String[] flatMergeSegments = {"e", "CHAT:u"};
         for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
             SegmentedTier t = (SegmentedTier)(st.getBody().elementAt(pos));
              if (t.hasSegmentationWithName("SpeakerContribution_Event")){
                 t.flatMerge(flatMergeSegmentations, flatMergeSegments, "Utterance_Event_Merged", "e.u");
                 t.hierarchicalMerge("SpeakerContribution_Utterance", 
                                     "Utterance_Event_Merged", 
                                     "SpeakerContribution_Utterance_Event", 
                                     "CHAT:u",
                                     "e.u");
                 
              }
         }
         
         beforeAugment = st.getBody().makeTLIHashtable();
         st.getBody().augmentCommonTimeline();
         
         SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.CHAT_UTTERANCE_SEGMENTATION);
         ListTranscription lt = st.toListTranscription(info);
         lt.getBody().sort(true);
         return lt;
    }

    public static String toText(ListTranscription lt) throws JexmaraldaException {
        return toText(lt,"e.u");
    }
    
    public static String toText(ListTranscription lt, String unitName) throws JexmaraldaException{

        boolean basedOnTurns = unitName.equals("e");

        if (!(lt.getBody().getCommonTimeline().isVirgin())){
            lt.getBody().getCommonTimeline().completeTimes();
        }

        UDInformationHashtable metaUDInfo = lt.getHead().getMetaInformation().getUDMetaInformation();
        Speakertable speakertable = lt.getHead().getSpeakertable();
        ListBody body = lt.getBody();
        Hashtable overlaps = new Hashtable();
        // count the frequency of start/end combinations
        for (int pos=0; pos<body.getNumberOfSpeakerContributions(); pos++){
            SpeakerContribution sc = body.getSpeakerContributionAt(pos);
            TimedSegment ts = sc.getMain();
            Vector v = ts.getAllSegmentsWithName(unitName);
            for (int e=0; e<v.size(); e++){
                //TimedSegment epe = (TimedSegment)(v.elementAt(e));
                Timeable epe = (Timeable)(v.elementAt(e));
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
        /*********************/
        /* Header data */
        // added 06-May-2008
        output.append("@UTF8" + System.getProperty("line.separator"));
        output.append("@Begin" + System.getProperty("line.separator"));

        // added 24-April-2008
        if (metaUDInfo.containsAttribute("CHAT:@Languages")){
            output.append("@Languages:\t" + metaUDInfo.getValueOfAttribute("CHAT:@Languages"));
        } else {
            output.append("@Languages:\t");
        }
        output.append(System.getProperty("line.separator"));
        
        output.append("@Participants:\t");
        for (int pos=0; pos<speakertable.getNumberOfSpeakers(); pos++){
            output.append(speakertable.getSpeakerAt(pos).getAbbreviation().replaceAll(" ", "_"));
            // added 14-April-2008
            UDInformationHashtable udinfo = speakertable.getSpeakerAt(pos).getUDSpeakerInformation();
            if (udinfo.containsAttribute("CHAT:Full Name")){
                output.append(" " + udinfo.getValueOfAttribute("CHAT:Full Name").replaceAll(" ", "_"));
            } else {
                output.append(" " + speakertable.getSpeakerAt(pos).getAbbreviation().replaceAll(" ", "_"));
            }
            if (udinfo.containsAttribute("CHAT:Role")){
                output.append(" " + udinfo.getValueOfAttribute("CHAT:Role"));
            } else {
                output.append(" " + "Participant");
            }
            if (pos<speakertable.getNumberOfSpeakers()-1){output.append(", ");}
        }
        output.append(System.getProperty("line.separator"));
        

        // added 14-April-2008
        // changed 03-May-2010
        for (int pos=0; pos<speakertable.getNumberOfSpeakers(); pos++){
            Speaker thisSpeaker = speakertable.getSpeakerAt(pos);
            // changed 24-April-2008
            // changed 03-May-2010
            UDInformationHashtable udinfo = speakertable.getSpeakerAt(pos).getUDSpeakerInformation();
            if (udinfo.containsAttribute("CHAT:ID")){
                output.append("@ID:\t" + udinfo.getValueOfAttribute("CHAT:ID"));
            } else {
                //@ID: language|corpus|code|age|sex|group|SES|role|education|
                //|change_me_later|Christliebe|||||Participant||
                String language = "en";
                if (thisSpeaker.getLanguagesUsed().getAllLanguages().length>0){
                    language = thisSpeaker.getLanguagesUsed().getAllLanguages()[0];
                }
                String role = "Participant";
                if (udinfo.containsAttribute("CHAT:Role")){
                    role = udinfo.getValueOfAttribute("CHAT:Role");
                }
                String corpus = "change_me_later";
                if (lt.getHead().getMetaInformation().getUDMetaInformation().containsAttribute("CHAT:@Corpus")){
                    corpus = lt.getHead().getMetaInformation().getUDMetaInformation().getValueOfAttribute("CHAT:@Corpus");
                }
                String age = "";
                if (udinfo.containsAttribute("CHAT:Age")){
                    age = udinfo.getValueOfAttribute("CHAT:Age");
                }
                output.append("@ID:\t" + language + "|" + corpus + "|" + thisSpeaker.getAbbreviation().replaceAll(" ", "_") + "|" + age + "||||" + role + "||");
            }

            output.append(System.getProperty("line.separator"));
        }
        
        // added 14-April-2008
        // changed 24-April-2008
        // changed 06-May-2008
        // changed 27-Nov-2008
        // changed 25-Mar-2009
        if ((metaUDInfo!=null) && (metaUDInfo.getAllAttributes()!=null)){
            for (String att: metaUDInfo.getAllAttributes()){
                if (att.startsWith("CHAT:@") && /*(!(att.equals("CHAT:@Date"))) &&*/ (!(att.equals("CHAT:@Languages")))){
                    output.append(att.substring(5) + ":\t");
                    output.append(metaUDInfo.getValueOfAttribute(att));
                    output.append(System.getProperty("line.separator"));
                }
            }
        }
        
        // added 24-April-2008
        /*if (metaUDInfo.containsAttribute("CHAT:@Date")){
            output.append("@Date:\t" + metaUDInfo.getValueOfAttribute("CHAT:@Date"));
            output.append(System.getProperty("line.separator"));
        }*/
        

        
        /*********************/
        
        // added 14-April-2008 : synch info
        // changed 10-June-2009
        // changed 20-July-2010
        String fullMediaName = lt.getHead().getMetaInformation().getReferencedFile();
        int i1 = fullMediaName.lastIndexOf(System.getProperty("file.separator"));
        int i2 = fullMediaName.lastIndexOf(".");
        String nakedMediaName = "";
        if ((i1>=0) && (i2>i1)){
            nakedMediaName = fullMediaName.substring(i1+1, i2);        
        }
        String mediaType = "video";
        if (fullMediaName.toUpperCase().endsWith("WAV")||fullMediaName.toUpperCase().endsWith("MP3")){
            mediaType = "audio";
        }
        output.append("@Media:\t" + nakedMediaName + ", " + mediaType);
        output.append(System.getProperty("line.separator"));

        
        int overlapCount = 0;
        String lastOverlap = "";
        HashSet<String> tlisDone = new HashSet<String>();
        for (int pos=0; pos<body.getNumberOfSpeakerContributions(); pos++){
            SpeakerContribution sc = body.getSpeakerContributionAt(pos);

            // added 06-May-2008
            // internal header inside bookmark?
            String tli = sc.getMain().getStart();
            if ((!tlisDone.contains(tli))){
                TimelineItem timelineItem = lt.getBody().getCommonTimeline().getTimelineItemWithID(tli);
                String bookmark = timelineItem.getBookmark();
                if ((bookmark!=null) && bookmark.startsWith("CHAT:")){
                    String bm = bookmark.substring(5);
                    int index = bm.indexOf(":");
                    output.append(bm.substring(0,index+1));
                    output.append("\t");
                    output.append(bm.substring(index+1));
                    output.append(System.getProperty("line.separator"));                    
                    tlisDone.add(tli);
                }
            }
            
            // speaker code
            // changed 07-09-2010
            // new: scs without speaker
            // become @Situation tags in CHAT
            if (sc.getSpeaker()==null){
                // @Situation
                output.append("@Situation:\t");
                output.append(sc.getMain().getDescription());
                output.append(System.getProperty("line.separator"));
                continue;
            }
            String thisSpeaker = sc.getSpeaker();
            output.append("*");
            output.append(speakertable.getSpeakerWithID(thisSpeaker).getAbbreviation());
            output.append(":");
            output.append("\t");

            // Text
            TimedSegment ts = sc.getMain();
            Vector v = ts.getAllSegmentsWithName(unitName);
            // added 10-06-2009
            if (ts.getName().equals(unitName)){
                v = new Vector();
                v.addElement(ts);
            }
            for (int e=0; e<v.size(); e++){
                TimedSegment epe = (TimedSegment)(v.elementAt(e));
                //System.out.println("***** " + epe.getName() + "    -     " + epe.getDescription());
                String key = epe.getStart() + "#" + epe.getEnd();
                int howOften = 0;
                if (overlaps.get(key)!=null){
                    howOften = ((Integer)(overlaps.get(key))).intValue();
                }
                // added 20-May-2008 : 2dn, 3rd, ... line starts with a tab
                if ((!basedOnTurns) && (e>0)) {
                    output.append("\t");
                }
                if (howOften>1) {
                    output.append("<");
                }


                output.append(epe.getDescription());
                if (howOften>1) {
                    output.append(">");
                    if (!key.equals(lastOverlap)){
                        overlapCount=1;
                        output.append(" [>] ");
                    } else {
                        overlapCount++;
                        if (overlapCount==howOften){
                            output.append(" [<] ");
                        } else {
                            output.append(" [<>] ");
                        }
                    }
                    lastOverlap = key;
                }
                // added 14-April-2008 : synch info
                // changed 20-May-2008 : there can be multiple line utterances
                // changed 20-July-2009 : new method of referring to sound file
                String startID = epe.getStart();
                String endID = epe.getEnd();
                double startTime = lt.getBody().getCommonTimeline().getTimelineItemWithID(startID).getTime();
                double endTime = lt.getBody().getCommonTimeline().getTimelineItemWithID(endID).getTime();
                if ((startTime>=0) && (endTime>=0)){
                    output.append(  " " + SOUND_BULLET_CHARACTER + 
                                    //"%snd:" +
                                    //"\"" + nakedMediaName + "\"" +
                                    //"_" +
                                    Long.toString(Math.round(startTime*1000)) + 
                                    "_" +
                                    Long.toString(Math.round(endTime*1000)) + 
                                    SOUND_BULLET_CHARACTER);
                }

                if ((ts.getName().equals(unitName)) || (e==v.size()-1) || (!basedOnTurns)){
                    //System.out.println("Voila une!");
                    output.append(System.getProperty("line.separator"));
                }

            }            

            for (int pos3=0; pos3<sc.getNumberOfAnnotations(); pos3++){
                AbstractSegmentVector asv = sc.getAnnotationAt(pos3);
                if (!(asv.getName().startsWith("%"))) {output.append("%");}
                // changed 20-July-2010
                output.append("x" + asv.getName() + ":" + "\t");
                for (int pos2=0; pos2<asv.size(); pos2++){
                    output.append(((Describable)(asv.elementAt(pos2))).getDescription());
                }
                output.append(System.getProperty("line.separator"));
            }
            
            
        }

        output.append("@End" + System.getProperty("line.separator"));
        return output.toString();
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
