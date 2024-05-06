/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.AbstractSegment;
import org.exmaralda.partitureditor.jexmaralda.Annotation;
import org.exmaralda.partitureditor.jexmaralda.AtomicTimedSegment;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Identifiable;
import org.exmaralda.partitureditor.jexmaralda.NonTimedSegment;
import org.exmaralda.partitureditor.jexmaralda.SegmentList;
import org.exmaralda.partitureditor.jexmaralda.Segmentation;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTier;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.TimedAnnotation;
import org.exmaralda.partitureditor.jexmaralda.TimedSegment;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class InelEventBasedSegmentation extends AbstractSegmentation {

    String XSL = "/org/exmaralda/partitureditor/jexmaralda/xsl/InelEventBasedSegmentation.xsl";
    String[] WORD_EXTERNAL_PUNCUTATION = {
            "\\s", // white space 
            "\\(", // opening round parenthesis 
            "\\)", // closing round parenthesis 
            "\\[", // opening square bracket 
            "\\]", // closing square bracket 
            "\\.", // period 
            "\\?", // question mark 
            "!", // exclamation mark 
            "…", // ellipsis (one symbol) 
            ",", // comma 
            "–", // n-dash 
            "—", // m-dash 
            "‐", // U+2010 HYPHEN 
            "‑", // U+2011 NON-BREAKING HYPHEN 
            "=", // equals 
            "\"", // straight double quotation mark 
            "“", // left double quotation mark 
            "”", // right double quotation mark 
            "«", // left double angle quotation mark 
            "»", // right double angle quotation mark 
            ";", // semicolon 
            ":", // colon         
    };
    
    String WORD_EXTERNAL_PUNCUTATION_REGEX = String.join("", WORD_EXTERNAL_PUNCUTATION);
    
    @Override
    public Vector getSegmentationErrors(BasicTranscription bt) throws SAXException {
        // just return empty for now
        return new Vector();
    }

    @Override
    public SegmentedTranscription BasicToSegmented(BasicTranscription bt) throws SAXException, FSMException {
        SegmentedTranscription plainSegmented = bt.toSegmentedTranscription();
        for (int i=0; i<plainSegmented.getBody().getNumberOfTiers(); i++){
            SegmentedTier segmentedTier = plainSegmented.getBody().getSegmentedTierAt(i);
            String tierID = segmentedTier.getID();
            Segmentation sourceSegmentation = segmentedTier.getSegmentationWithName("SpeakerContribution_Event");
            Segmentation targetSegmentation = new Segmentation();
            targetSegmentation.setName("SpeakerContribution_Utterance_Word");
            targetSegmentation.setTierReference(sourceSegmentation.getTierReference());
            segmentedTier.addSegmentation(targetSegmentation);
            
            Annotation refAnnotation = segmentedTier.getAnnotationWithName("ref");
            Set<String> utteranceEndPoints = new HashSet<>();
            for (int j=0; j<refAnnotation.getNumberOfSegments(); j++){
                utteranceEndPoints.add(((TimedAnnotation)(refAnnotation.get(j))).getEnd());
            }
            
            SegmentList allSegmentChains = sourceSegmentation.getAllSegmentsWithName("sc");
            int uttCount = 1;
            for (int j=0; j<allSegmentChains.size(); j++){
                TimedSegment segmentChain = (TimedSegment)(allSegmentChains.get(j));
                
                TimedSegment targetSegmentChain = new TimedSegment();
                targetSegmentChain.setStart(segmentChain.getStart());
                targetSegmentChain.setEnd(segmentChain.getEnd());
                targetSegmentChain.setName("sc");
                targetSegmentation.addSegment(targetSegmentChain);
                
                TimedSegment currentUtterance = new TimedSegment();
                currentUtterance.setID(tierID + ".u" + Integer.toString(uttCount));
                currentUtterance.setName("INEL:u");
                uttCount++;
                currentUtterance.setStart(segmentChain.getStart());
                targetSegmentChain.add(currentUtterance);
                Vector sourceEvents = segmentChain.getAllSegmentsWithName("e");
                
                for (int k=0; k<sourceEvents.size(); k++){
                    TimedSegment event = (TimedSegment) sourceEvents.elementAt(k);
                    try {
                        List<MutableTreeNode> parsedSegments = parseEvent(event);
                        for (MutableTreeNode as : parsedSegments){
                            currentUtterance.add(as);
                        }
                        if (utteranceEndPoints.contains(event.getEnd())){                    
                            currentUtterance.setEnd(event.getEnd());
                            currentUtterance = new TimedSegment();
                            currentUtterance.setID(tierID + ".u" + Integer.toString(uttCount));
                            currentUtterance.setName("INEL:u");
                            uttCount++;
                            currentUtterance.setStart(event.getEnd());
                            targetSegmentChain.add(currentUtterance);
                        }
                    } catch (FSMException ex){
                        ex.setTierID(tierID);
                        throw(ex);
                    }
                }
                if (!currentUtterance.children().hasMoreElements()){
                    targetSegmentChain.remove(currentUtterance);
                }
            }
        }
        return plainSegmented;
        
        //*******************************
        // old solution, using XSL transformation
        // works, but too slow
        /*String plainSegmentedXML = plainSegmented.toXML();
        StylesheetFactory stylesheetFactory = new StylesheetFactory(true);
        String inelSegmentedXML = stylesheetFactory.applyInternalStylesheetToString(XSL, plainSegmentedXML);
        Document inelSegmentedDoc = IOUtilities.readDocumentFromString(inelSegmentedXML);
        File tempFile = File.createTempFile("INEL_SEGMENTED", ".exs");
        tempFile.deleteOnExit();
        FileIO.writeDocumentToLocalFile(tempFile, inelSegmentedDoc);
        SegmentedTranscription result = new SegmentedTranscriptionSaxReader().readFromFile(tempFile.getAbsolutePath());
        tempFile.delete();
        return result;*/
    }

    private List<MutableTreeNode> parseEvent(TimedSegment event) throws FSMException {
        List<MutableTreeNode> result = new ArrayList<>();
        String text = event.getDescription();
        if (text.startsWith("((")){
            result.addAll(makeNonTimedSegments("((", event.getID() + ".1"));
            int endIndex = text.lastIndexOf("))");
            if (endIndex<0){
                FSMException ex = new FSMException("Unclosed double round brackets", text, event.getStart(), null);
                throw(ex);
            }
            AtomicTimedSegment ats = new AtomicTimedSegment();
            ats.setStart(event.getStart());
            ats.setEnd(event.getEnd());        
            ats.setName(("INEL:non-pho"));
            ats.setDescription(text.substring(2, endIndex));
            ats.setID(event.getID() + ".ats");
            result.add(ats);
            result.addAll(makeNonTimedSegments(text.substring(endIndex), event.getID() + ".2"));
            return result;
        }
        Pattern p = Pattern.compile("^[" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]+");
        Matcher m = p.matcher(text);
        if (m.find()){
            int end = m.end();
            String punctuation = text.substring(0, end);
            text = text.substring(end);
            result.addAll(makeNonTimedSegments(punctuation, event.getID() + ".1"));            
        }
        p = Pattern.compile("[" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]+$");
        m = p.matcher(text);
        if (m.find()){
            int start = m.start();
            String word = text.substring(0, start);
            TimedSegment ts = new TimedSegment();
            ts.setName("INEL:w");
            ts.setStart(event.getStart());
            ts.setEnd(event.getEnd());        
            ts.setDescription(word);
            ts.setID(event.getID() + ".w");
            result.add(ts);
            String punctuation = text.substring(start);
            result.addAll(makeNonTimedSegments(punctuation, event.getID() + ".2"));            
        } else {
            TimedSegment ts = new TimedSegment();
            ts.setName("INEL:w");
            ts.setStart(event.getStart());
            ts.setEnd(event.getEnd());        
            ts.setDescription(text);
            ts.setID(event.getID() + ".w");
            result.add(ts);            
        }
        
        return result;
    }

    private List<NonTimedSegment> makeNonTimedSegments(String text, String baseID) {
        List<NonTimedSegment> result = new ArrayList<>();
        int i=1;
        for (char c : text.toCharArray()){
            NonTimedSegment nts = new NonTimedSegment();
            nts.setName("INEL:ip");
            nts.setID(baseID + "." + Integer.toString(i));
            i++;
            nts.setDescription(Character.toString(c));
            result.add(nts);
        }
        return result;
    }
    
}
