/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.data;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.folker.data.Speaker;
import org.exmaralda.folker.data.Timepoint;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;

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
    HashSet<String> wordIDs;
    List<Element> words;
    int highestParseLevel = 0;


    public NormalizedFolkerTranscription(Document xmlDocument) {
        this(xmlDocument, false);
    }
    
    public NormalizedFolkerTranscription(Document xmlDocument, boolean removeIdentical) {
        this.xmlDocument = xmlDocument;
        documentId = xmlDocument.getRootElement().getAttributeValue("id");
        index();
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
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex){
            ex.printStackTrace();
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

    void index(){
        contributions = new ArrayList<Element>();
        List l = xmlDocument.getRootElement().getChildren("contribution");
        for (Object o : l){
            Element contribution = (Element)o;
            contributions.add(contribution);
            highestParseLevel = Math.max(highestParseLevel, Integer.parseInt(contribution.getAttributeValue("parse-level")));
        }

        timeMappings  = new HashMap<String, Timepoint>();
        List l2 = xmlDocument.getRootElement().getChild("timeline").getChildren("timepoint");
        for (Object o : l2){
            Element timepoint = (Element)o;
            String id = timepoint.getAttributeValue("timepoint-id");
            Double absoluteTime = Double.parseDouble(timepoint.getAttributeValue("absolute-time"));
            Timepoint tp = new Timepoint(null, absoluteTime.doubleValue()* 1000.0);
            timeMappings.put(id, tp);
        }

        speakerMappings  = new HashMap<String, Speaker>();
        List l3 = xmlDocument.getRootElement().getChild("speakers").getChildren("speaker");
        for (Object o : l3){
            Element speakerElement = (Element)o;
            String id = speakerElement.getAttributeValue("speaker-id");
            String name = speakerElement.getChildText("name");
            Speaker speaker = new Speaker(id);
            speaker.setName(name);
            speakerMappings.put(id, speaker);
        }

        wordMappings = new HashMap<Element, ArrayList<Element>>();
        wordIDs = new HashSet<String>();
        for (Element contribution : contributions){
            ArrayList<Element> wordVector = new ArrayList<Element>();
            Iterator i = contribution.getDescendants(new ElementFilter("w"));
            while (i.hasNext()){
                Element word = (Element)(i.next());
                wordVector.add(word);
                wordIDs.add(word.getAttributeValue("id"));
            }
            wordMappings.put(contribution, wordVector);
        }
    }
    
    public void reindexContribution(Element contribution){
            ArrayList<Element> wordVector = new ArrayList<Element>();
            Iterator i = contribution.getDescendants(new ElementFilter("w"));
            while (i.hasNext()){
                Element word = (Element)(i.next());
                wordVector.add(word);
                wordIDs.add(word.getAttributeValue("id"));
            }
            wordMappings.put(contribution, wordVector);        
    }
    
    public void unindexContribution(Element contribution){
            Iterator i = contribution.getDescendants(new ElementFilter("w"));
            while (i.hasNext()){
                Element word = (Element)(i.next());
                wordIDs.remove(word.getAttributeValue("id"));
            }
            wordMappings.remove(contribution);
    }
    
    private void removeIdentical() {
        List<Element> l = this.getWords();
        for (Element w : l){
            Attribute n = w.getAttribute("n");
            if ((n!=null) && (n.getValue().equals(WordUtilities.getWordText(w)))){
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

    public boolean hasWordID(String testID) {
        return wordIDs.contains(testID);
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


}
