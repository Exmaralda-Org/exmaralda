/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.AbstractSegment;
import org.exmaralda.partitureditor.jexmaralda.AtomicTimedSegment;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.NonTimedSegment;
import org.exmaralda.partitureditor.jexmaralda.Segmentation;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.TimedSegment;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class AutoNormalizer {

    public int MIN_AUTO_FREQUENCY = 5;
    
    public boolean OVERWRITE_EXISTING = false;

    LexiconInterface lexicon;
    
    private final List<SearchListenerInterface> listenerList = new ArrayList<>();
    
    static final Namespace teiNamespace = Namespace.getNamespace("tei", "http://www.tei-c.org/ns/1.0");
    
    
    public AutoNormalizer(LexiconInterface l) {
        lexicon = l;
    }

    public void addSearchListener(SearchListenerInterface sli) {
        listenerList.add(sli);
    }

    
    public void normalize(Element contribution, HashMap<String,String> normalizationMap) {
        String name = contribution.getName();
        String attributeName = "n";
        ElementFilter elementFilter = new ElementFilter("w");
        if (!(name.equals("contribution"))){
            elementFilter = new ElementFilter("tei:w", teiNamespace);
            attributeName = "norm";
        }
        
        
        Iterator i = contribution.getDescendants(elementFilter);
        
        while (i.hasNext()){
            Element word = (Element)(i.next());
            // 01-03-2023, issue #340
            String wordText = WordUtilities.getWordText(word, true);
            if (normalizationMap.containsKey(wordText)){
                word.setAttribute(attributeName, normalizationMap.get(wordText));
            }
        }
        
    }

    public int normalize(Element contribution) throws LexiconException {
        String name = contribution.getName();
        String attributeName = "n";
        ElementFilter elementFilter = new ElementFilter("w");
        if (!(name.equals("contribution"))){
            elementFilter = new ElementFilter("w", teiNamespace);
            attributeName = "norm";
        }

        Iterator i = contribution.getDescendants(elementFilter);
        int count=0;
        while (i.hasNext()){
            Element word = (Element)(i.next());
            // 01-03-2023, issue #340
            String wordText = WordUtilities.getWordText(word, true);
            //System.out.println("Looking up " + wordText);
            //System.out.println("??? " + wordText);
            if (word.getAttribute(attributeName)==null || OVERWRITE_EXISTING){
                if (OVERWRITE_EXISTING){
                    word.removeAttribute(attributeName);
                }
                boolean lookupGotResult = false;
                // 1. lookup the form in the lexicon
                List<String> forms = lexicon.getCandidateForms(wordText);
                if (!forms.isEmpty()){
                    String form = forms.get(0);
                    //System.out.println(form + " " + lexicon.getFrequency(wordText, form));
                    if (!(form.equals(word.getText()))){
                       if ((!lexicon.hasFrequencyInformation()) 
                               || ((lexicon.hasFrequencyInformation()) 
                               && (lexicon.getFrequency(wordText, form)>=MIN_AUTO_FREQUENCY))){
                            word.setAttribute(attributeName, form);
                            //System.out.println("Set " + form);
                            lookupGotResult = true;
                            count++;
                        }
                    }
                }

                // 2. if you can, try the capitals only list
                if (!(lookupGotResult) && lexicon.hasCapitalInformation() && wordText.matches("[a-zäöüß].+")){
                    try {
                        String capitalWord = wordText.substring(0,1).toUpperCase() + wordText.substring(1);
                        if (lexicon.isCapitalOnly(capitalWord)){
                            word.setAttribute(attributeName, capitalWord);
                            lookupGotResult = true;
                            count++;                        
                        }
                    } catch (LexiconException ex) {
                        // should not get here...
                        System.out.println(ex.getLocalizedMessage());
                    }
                }
            }
        }
        return count;        
    }




    public int normalize(Document document) throws LexiconException, JDOMException{
        String name = document.getRootElement().getName();
        
        int all = 0;
        List l;
        if (name.equals("folker-transcription")){
            l = document.getRootElement().getChildren("contribution");       
        } else {
            XPath xp = XPath.newInstance("descendant::tei:u");
            xp.addNamespace(teiNamespace);
            l = xp.selectNodes(document);
        }
        int count = 0;
        for (Object o : l){
            fireProgress((double)count / (double)l.size(), "Autonormalizing contribution " + Integer.toString(count+1) + " of " + Integer.toString(l.size()));
            Element contribution = (Element)o;
            all+=normalize(contribution);
            count++;
        }
        fireProgress(1.0, "Auto normalization completed");
        return all;
    }
    
    
    // new 14-05-2025, issue #524
    public Tier normalizeBasicTranscriptionTier(BasicTranscription bt, String tierID, AbstractSegmentation segmentationAlgorithm, String segmentationName) throws SAXException, FSMException, JDOMException, IOException, LexiconException, JexmaraldaException{
        SegmentedTranscription st = segmentationAlgorithm.BasicToSegmented(bt);
        Segmentation wordSegmentation = st.getBody().getSegmentedTierWithID(tierID).getSegmentationWithName(segmentationName);
        Segmentation eventSegmentation = st.getBody().getSegmentedTierWithID(tierID).getSegmentationWithName("SpeakerContribution_Event");
        
        Tier sourceTier = bt.getBody().getTierWithID(tierID);
        Timeline timeline = bt.getBody().getCommonTimeline();
        
        Tier resultTier = new Tier();
        resultTier.setSpeaker(sourceTier.getSpeaker());
        String newTierID = sourceTier.getID()+ "_NORM1";
        int j=1;
        while (bt.getBody().containsTierWithID(newTierID)){
            j++;
            newTierID = sourceTier.getID()+ "_NORM" + Integer.toString(j);
        }
        resultTier.setID(newTierID);
        resultTier.setCategory("norm");
        resultTier.setType("a");
        resultTier.setDisplayName(resultTier.getDescription(bt.getHead().getSpeakertable()));

        for (int i=0; i<wordSegmentation.size(); i++){
            // Those two will have identical text
            TimedSegment wordSegmentChain = (TimedSegment) wordSegmentation.get(i);
            TimedSegment eventSegmentChain = (TimedSegment) eventSegmentation.get(i);
            
            fireProgress((double)i / (double)wordSegmentation.size(), 
                    "Autonormalizing contribution " + Integer.toString(i+1) + " of " + Integer.toString(wordSegmentation.size()) + " in tier " + tierID);
            
            List<Integer> newEventPoints = new ArrayList<>();
            newEventPoints.add(0);
            Vector events = eventSegmentChain.getAllSegmentsWithName("e");
            int characterCount = 0;
            for (int l=0; l<events.size(); l++){
                TimedSegment event = (TimedSegment) events.get(l);
                characterCount+=event.getDescription().length();
                newEventPoints.add(characterCount);
                System.out.println("Added " + characterCount + " to new event points");
            }
            
            Vector leavesL = wordSegmentChain.getLeaves();
            characterCount = 0;
            Event currentEvent = null;
            for (int l=0; l<leavesL.size(); l++){
                if (newEventPoints.contains(characterCount)){
                    if (currentEvent!=null){
                        resultTier.add(currentEvent);
                    }
                    currentEvent = new Event();
                }
                Object leaf = leavesL.get(l);
                if (leaf instanceof TimedSegment){
                    TimedSegment timedSegment = (TimedSegment)leaf;
                    String startID = timedSegment.getStart();
                    if (timeline.containsTimelineItemWithID(startID) && currentEvent.getStart().length()==0){
                        currentEvent.setStart(startID);                        
                    }
                    String endID = timedSegment.getEnd();
                    if (timeline.containsTimelineItemWithID(endID) && currentEvent.getEnd().length()==0){
                        currentEvent.setEnd(endID);                        
                    }
                    characterCount+=timedSegment.getDescription().length();
                    List<String> candidateForms = lexicon.getCandidateForms(timedSegment.getDescription());
                    if (candidateForms.isEmpty()){
                        currentEvent.setDescription(currentEvent.getDescription() + timedSegment.getDescription());
                    } else {
                        currentEvent.setDescription(currentEvent.getDescription() + candidateForms.get(0));
                    }                    
                } else if (leaf instanceof AtomicTimedSegment){
                    AtomicTimedSegment atomicTimedSegment = (AtomicTimedSegment)leaf;
                    String startID = atomicTimedSegment.getStart();
                    if (timeline.containsTimelineItemWithID(startID) && currentEvent.getStart().length()==0){
                        currentEvent.setStart(startID);                        
                    }
                    String endID = atomicTimedSegment.getEnd();
                    if (timeline.containsTimelineItemWithID(endID) && currentEvent.getEnd().length()==0){
                        currentEvent.setEnd(endID);                        
                    }
                    characterCount+=atomicTimedSegment.getDescription().length();
                    currentEvent.setDescription(currentEvent.getDescription() + atomicTimedSegment.getDescription());
                } else {
                    NonTimedSegment nonTimedSegment = (NonTimedSegment)leaf;
                    characterCount+=nonTimedSegment.getDescription().length();
                    currentEvent.setDescription(currentEvent.getDescription() + nonTimedSegment.getDescription());
                }
            }
            resultTier.addEvent(currentEvent);
        }
            
            
        resultTier.updatePositions();
        return resultTier;
        
    }
    
    
    protected void fireProgress(double progress, String message) {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size() - 1; i >= 0; i -= 1) {
            SearchEvent se = new SearchEvent(SearchEvent.SEARCH_PROGRESS_CHANGED, progress, message);
            listenerList.get(i).processSearchEvent(se);
        }
    }

    private void log_debug(boolean waitingForNewStart, Event lastEvent, Event currentEvent, String danglingText, String preText) {
        System.out.print(Boolean.toString(waitingForNewStart)
                + "\t" + danglingText 
                + "\t" + preText
        );
        if (lastEvent!=null){
            System.out.print("\t" + lastEvent.getDescription());
        } else {
            System.out.print("\tNULL");            
        }
        if (currentEvent!=null){
            System.out.print("\t" + currentEvent.getDescription());
        } else {
            System.out.print("\tNULL");            
        }
        System.out.println("");
    }
    
    
}
