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
import org.jdom.Document;
import org.jdom.Element;
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

    @Override
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
    
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    

    private static ArrayList<GATLine> toGATLines(ListTranscription lt) throws JexmaraldaException {
        Timeline timeline = lt.getBody().getCommonTimeline();
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
                    Integer i_plus_one = i+1;
                    overlaps.put(key, i_plus_one);
                } else {
                    overlaps.put(key, 1);
                }
            }
        }
        
        //StringBuilder output = new StringBuilder();
        String lastSpeaker = "";
        HashMap<String,Integer> openingBracketPositions = new HashMap<String,Integer>();
        HashMap<String,Integer> closingBracketPositions = new HashMap<String,Integer>();
        ArrayList<GATLine> result = new ArrayList<GATLine>();
        for (int pos=0; pos<body.getNumberOfSpeakerContributions(); pos++){
            GATLine thisGATLine = new GATLine();
            
            SpeakerContribution sc = body.getSpeakerContributionAt(pos);
            
            String startID = sc.getMain().getStart();
            String endID = sc.getMain().getEnd();
            double startTime = timeline.getTimelineItemWithID(startID).getTime();
            double endTime = timeline.getTimelineItemWithID(endID).getTime();
            thisGATLine.start = startTime;
            thisGATLine.end = endTime;
            
            
            // flag for recording problems in overlap alignment
            boolean isOverlapProblem = false;

            // Zeilennummerierung
            String lineNumber = "";
            if (pos+1<10) lineNumber+="0";
            if (pos+1<100) lineNumber+="0";
            if (body.getNumberOfSpeakerContributions()>999 && pos+1<1000) lineNumber+="0";;
            lineNumber+=Integer.toString(pos+1);
            thisGATLine.lineNumber = lineNumber;
            
            // Sprecherkuerzel
            String speaker = "";
            String thisSpeaker = sc.getSpeaker();
            if (thisSpeaker==null){                
            } else if (!thisSpeaker.equals(lastSpeaker)){
                speaker+=speakertable.getSpeakerWithID(thisSpeaker).getAbbreviation();
                speaker+=":";
            } else {
                for (int s=0; s<=speakertable.getSpeakerWithID(thisSpeaker).getAbbreviation().length(); s++){
                    speaker+=" ";
                }
            }
            // Sprecherkuerzelausgleich
            if (thisSpeaker!=null){
                for (int i=0; i<longestAbbLength-speakertable.getSpeakerWithID(thisSpeaker).getAbbreviation().length(); i++){
                    speaker+=" ";
                }
            }
            thisGATLine.speakerAbb = speaker;
            
            // Text
            StringBuilder lineText = new StringBuilder();
            TimedSegment ts = sc.getMain();
            Vector v = ts.getAllSegmentsWithName("e.pe");
            int textPosition = 0;
            for (int e=0; e<v.size(); e++){
                TimedSegment epe = (TimedSegment)(v.elementAt(e));
                String key = epe.getStart() + "#" + epe.getEnd();
                int howOften = ((Integer)(overlaps.get(key)));
                
                
                // opening bracket if appropriate
                if (howOften>1) {
                    // not in Java 1.6!
                    //int bracketPosition = openingBracketPositions.getOrDefault(epe.getStart(), 0);
                    int bracketPosition = 0;
                    if (openingBracketPositions.containsKey(epe.getStart())){
                        bracketPosition = openingBracketPositions.get(epe.getStart());
                    }                    
                    if (bracketPosition>=textPosition){
                        // this is the place where the magic happens
                        int numberOfSpaces = bracketPosition-textPosition;
                        for (int j=0; j<numberOfSpaces; j++){
                            lineText.append(" ");
                            textPosition++;
                        }
                        //textPosition=bracketPosition;
                    } else if (openingBracketPositions.containsKey(epe.getStart())) {
                        // the bracket should have been moved but couldn't because
                        // text position is already beyond the place where the bracket should appear
                        isOverlapProblem = true;                        
                    }
                    lineText.append("[");
                    if (!(openingBracketPositions.containsKey(epe.getStart()))){
                        openingBracketPositions.put(epe.getStart(), textPosition);
                    }
                    textPosition++;
                }
                
                // the text
                lineText.append(epe.getDescription());
                textPosition+=epe.getDescription().length();
                
                // closing bracket if appropriate
                if (howOften>1) {
                    // not in Java 1.6!
                    // int bracketPosition = closingBracketPositions.getOrDefault(epe.getEnd(), 0);
                    int bracketPosition = 0;
                    if (closingBracketPositions.containsKey(epe.getStart())){
                        bracketPosition = closingBracketPositions.get(epe.getStart());
                    }                    

                    if (bracketPosition>=textPosition){
                        // this is the place where the magic happens
                        int numberOfSpaces = bracketPosition-textPosition;                        
                        for (int j=0; j<numberOfSpaces; j++){
                            lineText.append(" ");
                            textPosition++;
                        }
                        //textPosition=bracketPosition;
                    } else if (closingBracketPositions.containsKey(epe.getEnd())) {
                        // the bracket should have been moved but couldn't because
                        // text position is already beyond the place where the bracket should appear
                        isOverlapProblem = true;                        
                    }
                    lineText.append("]");
                    if (!(closingBracketPositions.containsKey(epe.getEnd()))){
                        closingBracketPositions.put(epe.getEnd(), textPosition);
                    }
                    textPosition++;
                }                
            }
            
            if (isOverlapProblem){
                //output.append("\t").append("{*** OVERLAP MANUELL BEARBEITEN! ***}");
            }
            
            thisGATLine.text = lineText.toString();
            
            result.add(thisGATLine);
            
            lastSpeaker = thisSpeaker;
            
            
        } 
        
        return result;
    }
    
    
    public static String toText(ListTranscription lt) throws JexmaraldaException {
        ArrayList<GATLine> gatLines = toGATLines(lt);
        StringBuilder output = new StringBuilder();
        for (GATLine gl : gatLines){
            output.append(gl.lineNumber).append(" ");
            output.append(gl.speakerAbb).append("  ");
            output.append(gl.text).append(System.getProperty("line.separator"));
        }
        return output.toString();
    }
    
    public static Document toXML(ListTranscription lt) throws JexmaraldaException {
        ArrayList<GATLine> gatLines = toGATLines(lt);
        Document result = new Document(new Element("gat-transcript"));
        String wavAudio = lt.getHead().getMetaInformation().getReferencedFile("wav");
        if (wavAudio!=null){
            result.getRootElement().setAttribute("audio", wavAudio);
        }
        
        for (GATLine gl : gatLines){
            Element lineElement = new Element("gat-line");            
            lineElement.setAttribute("start", Double.toString(gl.start));
            lineElement.setAttribute("end", Double.toString(gl.end));
            lineElement.setAttribute("number", gl.lineNumber);
            result.getRootElement().addContent(lineElement);
            
            Element speakerElement = new Element("speaker");
            speakerElement.addContent(new org.jdom.CDATA(gl.speakerAbb));
            lineElement.addContent(speakerElement);
            
            Element textElement = new Element("text");
            textElement.addContent(new org.jdom.CDATA(gl.text));
            lineElement.addContent(textElement);
        }
        return result;
    }
    
    
    @Override
    public Vector getSegmentationErrors(BasicTranscription bt) throws SAXException {
         Vector result = new Vector();
         SegmentedTranscription st = bt.toSegmentedTranscription();
         FSMSaxReader sr = new FSMSaxReader();

         FiniteStateMachine fsm;
       
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

    private static class GATLine {

        String lineNumber;
        String speakerAbb;
        String text;
        double start;
        double end;
        
        public GATLine() {
        }
    }
    
    
}
