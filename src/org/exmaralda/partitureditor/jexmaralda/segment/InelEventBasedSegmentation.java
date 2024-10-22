/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.segment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.tree.MutableTreeNode;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.Annotation;
import org.exmaralda.partitureditor.jexmaralda.AtomicTimedSegment;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.NonTimedSegment;
import org.exmaralda.partitureditor.jexmaralda.SegmentList;
import org.exmaralda.partitureditor.jexmaralda.Segmentation;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTier;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.Tier;
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

        Vector<FSMException> result = new Vector<>();

        SegmentedTranscription plainSegmented = bt.toSegmentedTranscription();
        for (int i=0; i<plainSegmented.getBody().getNumberOfTiers(); i++){
            SegmentedTier segmentedTier = plainSegmented.getBody().getSegmentedTierAt(i);           
            Annotation refAnnotation = segmentedTier.getAnnotationWithName("ref");
            if (refAnnotation==null){
                FSMException ex = new FSMException("No reference tier for " + segmentedTier.getDisplayName(), "", bt.getBody().getCommonTimeline().getTimelineItemAt(0).getID(), bt.getBody().getTierAt(0).getID());
                result.add(ex);                
            }
        }        



        String[] tTierIDs = bt.getBody().getTiersOfType("t");
        String wordRegEx = "^([" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]+)?[^" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]+([" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]+)?$";
        // changed 21-08-2024
        //String wordRegEx = "^([" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]+ ?)?[^" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]+( ?[" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]+)?$";
        
        
        
        
        for (String tierID : tTierIDs){
            try {
                Tier tier = bt.getBody().getTierWithID(tierID);
                boolean openSingleParenth = false;
                
                for (int pos=0; pos<tier.getNumberOfEvents(); pos++){
                    Event event = tier.getEventAt(pos);
                    //if (event.getDescription().startsWith("((")){
                    if (event.getDescription().contains("((")){
                        if (!event.getDescription().contains("))")){
                            FSMException ex = new FSMException("Unclosed double round brackets", event.getDescription(), event.getStart(), tierID);
                            result.add(ex);
                        } else { 
                            int i1 = event.getDescription().indexOf("((");
                            if (i1>0){
                                String precedingText = event.getDescription().substring(0, i1);
                                if (!(precedingText.matches("[" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]*"))){
                                    FSMException ex = new FSMException("Word characters before double opening round parentheses", event.getDescription().substring(0, i1), event.getStart(), tierID);
                                    result.add(ex);
                                }
                            }


                            int i2 = event.getDescription().lastIndexOf("))");
                            String remainingText = event.getDescription().substring(i2);
                            if (!(remainingText.matches("[" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]*"))){
                                FSMException ex = new FSMException("Word characters after double closing round parentheses", event.getDescription().substring(0, i2), event.getStart(), tierID);
                                result.add(ex);
                            }
                            
                        }
                    } else {
                        if (!(event.getDescription().matches(wordRegEx))){
                            //FSMException ex = new FSMException("Punctutation between word characters", event.getDescription(), event.getStart(), tierID);                            
                            FSMException ex = new FSMException("Non-permissible sequence of word and punctuation characters", event.getDescription(), event.getStart(), tierID);                            
                            result.add(ex);
                        }      
                        if (event.getDescription().startsWith("(")){
                            if (openSingleParenth){
                                FSMException ex = new FSMException("Non-matching parenthesis", event.getDescription(), event.getStart(), tierID);                            
                                result.add(ex);                                
                            }
                            openSingleParenth = true;
                        }
                        openSingleParenth = openSingleParenth && (!(event.getDescription().contains(")")));
                    }
                }
                if (openSingleParenth){
                    FSMException ex = new FSMException("Non-matching parenthesis", tier.getEventAt(tier.getNumberOfEvents()-1).getDescription(), tier.getEventAt(tier.getNumberOfEvents()-1).getStart(), tierID);                            
                    result.add(ex);                    
                }
            } catch (JexmaraldaException ex) {
                Logger.getLogger(InelEventBasedSegmentation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return result;
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
                            //checkParentheses(currentUtterance);
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
                } else {
                    //checkParentheses(currentUtterance);                   
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
        
        // ****************************
        //if (text.startsWith("((")){
        if (text.contains("((")){
            int startIndex = text.indexOf("((");
            String precedingText = text.substring(0,startIndex+2);
            //result.addAll(makeNonTimedSegments("((", event.getID() + ".1"));
            result.addAll(makeNonTimedSegments(precedingText, event.getID() + ".1"));
            int endIndex = text.lastIndexOf("))");
            if (endIndex<0){
                FSMException ex = new FSMException("Unclosed double round brackets", text, event.getStart(), null);
                throw ex;
            }
            AtomicTimedSegment ats = new AtomicTimedSegment();
            ats.setStart(event.getStart());
            ats.setEnd(event.getEnd());        
            ats.setName(("INEL:non-pho"));
            //ats.setDescription(text.substring(2, endIndex));
            ats.setDescription(text.substring(startIndex+2, endIndex));
            ats.setID(event.getID() + ".ats");
            result.add(ats);
            String remainingText = text.substring(endIndex);
            if (!(remainingText.matches("[" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]*"))){
                FSMException ex = new FSMException("Word characters after double closing round parentheses", text, event.getStart(), null);
                throw ex;
            }
            result.addAll(makeNonTimedSegments(text.substring(endIndex), event.getID() + ".2")); 
            return result;
        }
        // ****************************
        Pattern p = Pattern.compile("^[" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]+"); // puncutation at the beginning
        Matcher m = p.matcher(text);
        if (m.find()){
            int end = m.end();
            String punctuation = text.substring(0, end);
            text = text.substring(end);
            result.addAll(makeNonTimedSegments(punctuation, event.getID() + ".1"));            
        }
        p = Pattern.compile("[" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]+$"); // puncutation at the end
        m = p.matcher(text);
        if (m.find()){
            int start = m.start();
            String word = text.substring(0, start);
            p = Pattern.compile("[" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]"); // puncutation anywhere
            m = p.matcher(word);
            if (m.find()){
                String parsedText = text.substring(0, m.start());
                FSMException ex = new FSMException("Punctutation between word characters", parsedText, event.getStart(), null);
                throw ex;
            }
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
            // now there shouldn't be any puncutation inside text
            // if there is, it is a segmentation error
            p = Pattern.compile("[" + WORD_EXTERNAL_PUNCUTATION_REGEX + "]"); // puncutation anywhere
            m = p.matcher(text);
            if (m.find()){
                String parsedText = text.substring(0, m.start());
                FSMException ex = new FSMException("Punctutation between word characters", parsedText, event.getStart(), null);
                throw ex;
            }
            
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

    private List<NonTimedSegment> makeNonTimedSegments(String text, String baseID) throws FSMException {
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

    private void checkParentheses(TimedSegment currentUtterance) throws FSMException {
        Vector allParentheses = currentUtterance.getAllSegmentsWithName("INEL:ip");
        boolean parenthOpen = false;
        for (Object o : allParentheses){
            NonTimedSegment pSegment = (NonTimedSegment)o;
            if (pSegment.getDescription().equals("(")){
                if (parenthOpen){
                    // don't care where the error is?
                    FSMException ex = new FSMException("Non-matching parenthesis", null, null, null);                            
                    throw ex;                          
                }
                parenthOpen = true;
            }
            parenthOpen = parenthOpen && (!(pSegment.getDescription().equals(")")));
            System.out.println(pSegment.getDescription() + " " + Boolean.toString(parenthOpen));
        }
        if (parenthOpen){
            FSMException ex = new FSMException("Non-matching parenthesis", null, null, null);                            
            throw ex;                  
        }
    }
    
}
