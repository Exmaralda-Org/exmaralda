/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.data;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.folker.data.Speaker;
import org.exmaralda.folker.data.Timepoint;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class NormalizedFolkerTranscription {

    Document xmlDocument;
    String documentId;
    ArrayList<Element> contributions;
    String mediaPath;
    HashMap<String, Timepoint> timeMappings;
    HashMap<String, Speaker> speakerMappings;
    HashMap<Element, ArrayList<Element>> wordMappings;
    HashSet<String> tokenIDs;
    List<Element> words;
    int highestParseLevel = 0;


    public NormalizedFolkerTranscription(Document xmlDocument) {
        this(xmlDocument, false);
    }
    
    public NormalizedFolkerTranscription(Document xmlDocument, boolean removeIdentical) {
        this.xmlDocument = xmlDocument;
        documentId = xmlDocument.getRootElement().getAttributeValue("id");
        index();
        // issue 
        if (removeIdentical){
            removeIdentical();
        }
    }

    public int getHighestParseLevel(){
        return highestParseLevel;
    }
    
    public List<Element> getWords(){
        ArrayList<Element> result = new ArrayList<Element>();
        Iterator i = xmlDocument.getRootElement().getDescendants(new ElementFilter("w"));
        while (i.hasNext()){
            result.add((Element)(i.next()));
        }
        return result;
    }

    public List<Element> getWordsForContribution(Element contribution){
        return this.wordMappings.get(contribution);
    }

    public String getID(){
        return documentId;
    }

    public Document getDocument(){
        return xmlDocument;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String path){
        mediaPath = path;
        File file = new File(path);
        try {
            String mp = file.toURI().toURL().toString();
            System.out.println("MP is now = " + mp);
            if (file.toURI().isAbsolute()) {
                mp = file.getAbsolutePath();
                System.out.println("And MP is now = " + mp);
            }
            xmlDocument.getRootElement().getChild("recording").setAttribute("path", mp);
            System.out.println("************** Path set from " + path + " to " + mp);
        } catch (MalformedURLException | IllegalArgumentException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    public int getNumberOfContributions() {
        return contributions.size();
    }

    public Timepoint getTimeForId(String id) {
        return timeMappings.get(id);
    }

    public Speaker getSpeakerForId(String id) {
        if (id==null){
            return null;
        }
        Speaker returnValue = speakerMappings.get(id);
        return returnValue;
    }

    final void index(){
        contributions = new ArrayList<>();
        List l = xmlDocument.getRootElement().getChildren("contribution");
        for (Object o : l){
            Element contribution = (Element)o;
            contributions.add(contribution);
            highestParseLevel = Math.max(highestParseLevel, Integer.parseInt(contribution.getAttributeValue("parse-level")));
        }

        timeMappings  = new HashMap<>();
        List l2 = xmlDocument.getRootElement().getChild("timeline").getChildren("timepoint");
        for (Object o : l2){
            Element timepoint = (Element)o;
            String id = timepoint.getAttributeValue("timepoint-id");
            Double absoluteTime = Double.parseDouble(timepoint.getAttributeValue("absolute-time"));
            Timepoint tp = new Timepoint(null, absoluteTime.doubleValue()* 1000.0);
            timeMappings.put(id, tp);
        }

        indexSpeakers();

        wordMappings = new HashMap<>();
        tokenIDs = new HashSet<>();
        for (Element contribution : contributions){
            ArrayList<Element> wordVector = new ArrayList<>();
            Iterator i = contribution.getDescendants(new ElementFilter());
            while (i.hasNext()){
                Element token = (Element)(i.next());
                if (token.getName().equals("w")){
                    wordVector.add(token);
                }                
                tokenIDs.add(token.getAttributeValue("id"));
            }
            wordMappings.put(contribution, wordVector);
        }
    }
    
    public void reindexContribution(Element contribution){
            ArrayList<Element> wordVector = new ArrayList<>();
            Iterator i = contribution.getDescendants(new ElementFilter());
            while (i.hasNext()){
                Element token = (Element)(i.next());
                if (token.getName().equals("w")){
                    wordVector.add(token);
                }
                tokenIDs.add(token.getAttributeValue("id"));
            }
            wordMappings.put(contribution, wordVector);        
    }
    
    public void unindexContribution(Element contribution){
            Iterator i = contribution.getDescendants(new ElementFilter());
            while (i.hasNext()){
                Element word = (Element)(i.next());
                tokenIDs.remove(word.getAttributeValue("id"));
            }
            wordMappings.remove(contribution);
    }
    
    private void removeIdentical() {
        List<Element> l = this.getWords();
        for (Element w : l){
            Attribute n = w.getAttribute("n");
            // 01-03-2023, issue #340
            if ((n!=null) && (n.getValue().equals(WordUtilities.getWordText(w, true)))){
                w.removeAttribute(n);
            }
        }
    }
    

    public Element getContributionAt(int index){
        return contributions.get(index);
    }


    public int getContributionIndex(Element thisWordElement) {
        Element currentElement = thisWordElement;
        while ((!currentElement.getName().equals("contribution"))){
            currentElement = currentElement.getParentElement();
        }
        return contributions.indexOf(currentElement);
    }

    public boolean hasTokenID(String testID) {
        //try {
            // #322
            // return wordIDs.contains(testID);
            // #483 : this is a performance catastrophe
            //Object o = XPath.selectSingleNode(xmlDocument, "//*[@id='" + testID + "']");
            //return (o!=null);     
            return tokenIDs.contains(testID);
        /*} catch (JDOMException ex) {
            Logger.getLogger(NormalizedFolkerTranscription.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }*/
    }

    public double getNormalisationRatio() {
        try {
            long allWords = XPath.selectNodes(xmlDocument, "//w").size();
            long nWords = XPath.selectNodes(xmlDocument, "//w[@n]").size();
            return (double)nWords / (double)allWords;
        } catch (JDOMException ex) {
            return -1.0;
        }
    }

    public void indexSpeakers() {
        speakerMappings  = new HashMap<>();
        List l3 = xmlDocument.getRootElement().getChild("speakers").getChildren("speaker");
        for (Object o : l3){
            Element speakerElement = (Element)o;
            String id = speakerElement.getAttributeValue("speaker-id");
            String name = speakerElement.getChildText("name");
            Speaker speaker = new Speaker(id);
            speaker.setName(name);
            speakerMappings.put(id, speaker);
        }
    }
    
    String CONTRIBUTION2TIMEDSEGMENT_XSL = "/org/exmaralda/orthonormal/data/FLNContribution2EXSTimedSegmentAndAnnotations.xsl";
    
    public SegmentedTranscription toSegmentedTranscription() throws JDOMException, SAXException, ParserConfigurationException, IOException, TransformerException, TransformerConfigurationException, JexmaraldaException{
        SegmentedTranscription st = EventListTranscriptionXMLReaderWriter.readXMLAsBasicTranscription(getDocument()).toSegmentedTranscription();
        
        Document segmentedDoc = IOUtilities.readDocumentFromString(st.toXML());
        
        //System.out.println(st.toXML());
        
        Map<String, Element> segTiers = new HashMap<>();
        Map<String, Element> segmentations = new HashMap<>();
        Map<String, Element> normAnnotations = new HashMap<>();
        Map<String, Element> lemmaAnnotations = new HashMap<>();
        Map<String, Element> posAnnotations = new HashMap<>();
        for (Object o : XPath.selectNodes(segmentedDoc, "//segmented-tier")){
            Element segTier = (Element)o;
            segTiers.put(segTier.getAttributeValue("speaker"), segTier);
            
            // <segmentation name="SpeakerContribution_Word" tierref="TIE_V_HM">
            Element segmentationElement = new Element("segmentation")
                    .setAttribute("name", "SpeakerContribution_Word")
                    .setAttribute("tierref", segTier.getAttributeValue("id")); 


            // <annotation name="norm" tierref="TIE_norm_HM">
            Element normAnnotationElement = new Element("annotation")
                    .setAttribute("name", "norm")
                    .setAttribute("tierref", segTier.getAttributeValue("id"));

            Element lemmaAnnotationElement = new Element("annotation")
                    .setAttribute("name", "lemma")
                    .setAttribute("tierref", segTier.getAttributeValue("id"));

            Element posAnnotationElement = new Element("annotation")
                    .setAttribute("name", "pos")
                    .setAttribute("tierref", segTier.getAttributeValue("id"));
            
            // /segmented-transcription/segmented-body[1]/segmented-tier[1]/annotation[2]
            

            segTier.addContent(segmentationElement);
            segTier.addContent(normAnnotationElement);
            segTier.addContent(lemmaAnnotationElement);
            segTier.addContent(posAnnotationElement);
            
            segmentations.put(segTier.getAttributeValue("speaker"), segmentationElement);
            normAnnotations.put(segTier.getAttributeValue("speaker"), normAnnotationElement);
            lemmaAnnotations.put(segTier.getAttributeValue("speaker"), lemmaAnnotationElement);
            posAnnotations.put(segTier.getAttributeValue("speaker"), posAnnotationElement);
        }
        
        
        StylesheetFactory ssf = new StylesheetFactory(true);
        List contributionList = XPath.selectNodes(getDocument(), "//contribution");
        
        for (Object o : contributionList){
            //System.out.println("Working!");
            Element contributionElement = (Element)o;
            String speaker = contributionElement.getAttributeValue("speaker-reference");
            if (speaker==null){
                speaker = "SPK_NOSPK";
            }
            Element segmentedTierElement = segTiers.get(speaker);
            Element segmentationElement = segmentations.get(speaker);
            int index = segmentedTierElement.indexOf(segmentationElement);
            String transformResult = ssf.applyInternalStylesheetToString(CONTRIBUTION2TIMEDSEGMENT_XSL, IOUtilities.elementToString(contributionElement));
            /*System.out.println(IOUtilities.elementToString(contributionElement));
            System.out.println("----------");
            System.out.println(transformResult);*/
            Document transformResultDocument = IOUtilities.readDocumentFromString(transformResult);
            List timelineForks = XPath.selectNodes(transformResultDocument, "//timeline-fork");
            for (Object o2 : timelineForks){
                if (!((Element)o2).getChildren().isEmpty()){
                    Element tlfElement = (Element)((Element)o2).clone();
                    segmentedTierElement.addContent(index - 1, tlfElement);
                    index++;                
                }
            }
            
            Element tsElement = (Element) XPath.selectSingleNode(transformResultDocument, "//ts[@n='sc']");
            segmentationElement.addContent(tsElement.detach());
            
            Element normElement = (Element) XPath.selectSingleNode(transformResultDocument, "//annotation[@name='norm']");
            if (normElement!=null){
                normAnnotations.get(speaker).addContent(normElement.removeContent());
            }
            Element lemmaElement = (Element) XPath.selectSingleNode(transformResultDocument, "//annotation[@name='lemma']");
            if (lemmaElement!=null){
                lemmaAnnotations.get(speaker).addContent(lemmaElement.removeContent());
            }
            Element posElement = (Element) XPath.selectSingleNode(transformResultDocument, "//annotation[@name='pos']");
            if (posElement!=null){
                posAnnotations.get(speaker).addContent(posElement.removeContent());
            }
        }
        
        for (String key : posAnnotations.keySet()){
            Element e = posAnnotations.get(key);
            if (e.getChildren().isEmpty()) e.detach();
        }

        for (String key : lemmaAnnotations.keySet()){
            Element e = lemmaAnnotations.get(key);
            if (e.getChildren().isEmpty()) e.detach();
        }

        
        File tempFile = File.createTempFile("Seg", ".exs");
        tempFile.deleteOnExit();
        FileIO.writeDocumentToLocalFile(tempFile, segmentedDoc);
        //FileIO.writeDocumentToLocalFile(new File("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_24_ISSUE_313\\FOLK_MEET_03_A01_MASK_TAGGED_TEST_INTERM.exs"), segmentedDoc);
        SegmentedTranscriptionSaxReader reader = new org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader();
        SegmentedTranscription result = reader.readFromFile(tempFile.getAbsolutePath());    
        return result;
    }
    
    


}
