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
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
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
        Segmentation segmentation = st.getBody().getSegmentedTierWithID(tierID).getSegmentationWithName(segmentationName);
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
        
        
        for (int i=0; i<segmentation.size(); i++){
            TimedSegment segmentChain = (TimedSegment) segmentation.get(i);
            String xml = segmentChain.toXML();
            Element segmentChainElement = IOUtilities.readElementFromString(xml);
            System.out.println(xml);
            List leavesL = XPath.selectNodes(segmentChainElement, "descendant::*[not(*)]");

            /*
                <ts n="GEN:w" id="Seg_1948" s="T0.TIE2.18" e="T8-414">generacij</ts>
                <nts n="GEN:ip" id="Seg_1949">.</nts>
                <ts n="GEN:w" id="Seg_1951" s="T8-414" e="T8-414.TIE2.1">Danes</ts>
            */

            boolean waitingForNewStart = true;
            Event lastEvent = null;
            Event currentEvent = null;
            String danglingText = "";
            String preText = "";
            for (Object o : leavesL){                
                Element leafE = (Element)o;
                //System.out.println(leafE.getText());
                if (leafE.getAttributeValue("s")!=null && timeline.containsTimelineItemWithID(leafE.getAttributeValue("s"))){
                    if (lastEvent!=null){
                        lastEvent.setDescription(lastEvent.getDescription() + danglingText);
                    }
                    lastEvent = currentEvent;
                    currentEvent = new Event();
                    //System.out.println("NEW EVENT " + leafE.getAttributeValue("s"));
                    currentEvent.setDescription(preText);
                    preText="";
                    currentEvent.setStart(leafE.getAttributeValue("s"));      
                    waitingForNewStart = false;
                }
                switch (leafE.getName()){
                    case "ts" : 
                        List<String> candidateForms = lexicon.getCandidateForms(leafE.getText());
                        if (candidateForms.isEmpty()){
                            currentEvent.setDescription(currentEvent.getDescription() + leafE.getText());
                        } else {
                            currentEvent.setDescription(currentEvent.getDescription() + candidateForms.get(0));
                        }
                        break;
                    case "ats" :
                    case "nts" : 
                        if (currentEvent==null){
                            preText+=leafE.getText();
                        } else {
                            currentEvent.setDescription(currentEvent.getDescription() + leafE.getText());   
                        }
                        if (waitingForNewStart){
                            danglingText+=leafE.getText();
                        }
                        break;                        
                }                
                
                if (leafE.getAttributeValue("e")!=null && timeline.containsTimelineItemWithID(leafE.getAttributeValue("e"))){
                    currentEvent.setEnd(leafE.getAttributeValue("e"));
                    resultTier.add(currentEvent); 
                    lastEvent = currentEvent;
                    currentEvent = new Event();
                    danglingText = "";
                    waitingForNewStart = true;
                }
            }
            if (lastEvent!=null){
                lastEvent.setDescription(lastEvent.getDescription() + danglingText);
            }
            
        }
        
        /*
            <segmentation name="SpeakerContribution_Word" tierref="TIE2">
                <ts n="sc" id="Seg_1891" s="T0" e="T16-330">
                    <ts n="GEN:w" id="Seg_1893" s="T0" e="T0.TIE2.1">Slovenci</ts>
                    <nts n="GEN:ip" id="Seg_1894"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1896" s="T0.TIE2.1" e="T0.TIE2.2">imamo</ts>
                    <nts n="GEN:ip" id="Seg_1897"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1899" s="T0.TIE2.2" e="T0.TIE2.3">duhovnika</ts>
                    <nts n="GEN:ip" id="Seg_1900">,</nts>
                    <nts n="GEN:ip" id="Seg_1901"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1903" s="T0.TIE2.3" e="T0.TIE2.4">ki</ts>
                    <nts n="GEN:ip" id="Seg_1904"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1906" s="T0.TIE2.4" e="T0.TIE2.5">se</ts>
                    <nts n="GEN:ip" id="Seg_1907"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1909" s="T0.TIE2.5" e="T0.TIE2.6">je</ts>
                    <nts n="GEN:ip" id="Seg_1910"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1912" s="T0.TIE2.6" e="T0.TIE2.7">z</ts>
                    <nts n="GEN:ip" id="Seg_1913"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1915" s="T0.TIE2.7" e="T0.TIE2.8">izvirnim</ts>
                    <nts n="GEN:ip" id="Seg_1916"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1918" s="T0.TIE2.8" e="T0.TIE2.9">pristopom</ts>
                    <nts n="GEN:ip" id="Seg_1919"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1921" s="T0.TIE2.9" e="T0.TIE2.10">k</ts>
                    <nts n="GEN:ip" id="Seg_1922"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1924" s="T0.TIE2.10" e="T0.TIE2.11">bogoslužju</ts>
                    <nts n="GEN:ip" id="Seg_1925"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1927" s="T0.TIE2.11" e="T0.TIE2.12">in</ts>
                    <nts n="GEN:ip" id="Seg_1928"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1930" s="T0.TIE2.12" e="T0.TIE2.13">s</ts>
                    <nts n="GEN:ip" id="Seg_1931"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1933" s="T0.TIE2.13" e="T0.TIE2.14">pozitivno</ts>
                    <nts n="GEN:ip" id="Seg_1934"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1936" s="T0.TIE2.14" e="T0.TIE2.15">naravnanostjo</ts>
                    <nts n="GEN:ip" id="Seg_1937"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1939" s="T0.TIE2.15" e="T0.TIE2.16">prikupil</ts>
                    <nts n="GEN:ip" id="Seg_1940"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1942" s="T0.TIE2.16" e="T0.TIE2.17">ljudem</ts>
                    <nts n="GEN:ip" id="Seg_1943"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1945" s="T0.TIE2.17" e="T0.TIE2.18">vseh</ts>
                    <nts n="GEN:ip" id="Seg_1946"><![CDATA[ ]]></nts>
                    <ts n="GEN:w" id="Seg_1948" s="T0.TIE2.18" e="T8-414">generacij</ts>
                    <nts n="GEN:ip" id="Seg_1949">.</nts>
                    <ts n="GEN:w" id="Seg_1951" s="T8-414" e="T8-414.TIE2.1">Danes</ts>
                    <nts n="GEN:ip" id="Seg_1952">,</nts>
                    <nts n="GEN:ip" id="Seg_1953"><![CDATA[ ]]></nts>
                    [...]
                    <nts n="GEN:ip" id="Seg_2003">.</nts>
                </ts>
        
        */
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
    
    
}
