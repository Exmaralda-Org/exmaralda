/*
 * Created on 09.02.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 * coma2/org.sfb538.coma2/ComaDocument.java
 * 
 * @author woerner
 * 
 */
public class ComaDocument extends Document {


    public ComaDocument() {
        super();
    }
    
    public ComaDocument(Element rootElement) {
        super(rootElement);
    }
    
    // for #545
    public List<String> getCommunicationIDs() throws JDOMException{
        return getIDs("//Communication");
    }
    
    // for #545
    public List<String> getSpeakerIDs() throws JDOMException{
        return getIDs("//Speaker");
    }
    
    // for #545
    public List<String> getTranscriptionIDs() throws JDOMException{
        return getIDs("//Transcription");
    }
    
    // for #545: Sad that it is somehow duplicating the ZuMult implementation, but so what...
    public List<String> getTranscriptionIDsForCommunication(String communicationID) throws JDOMException{
        return getIDs("//Communication[@Id='" + communicationID + "']/descendant::Transcription");        
    }

    public List<String> getSpeakerIDsForCommunication(String communicationID) throws JDOMException{
        List<String> result = new ArrayList<>();
        List l = XPath.selectNodes(this, "//Communication[@Id='" + communicationID + "']/descendant::Person");
        for (Object o : l){
            Element e = (Element)o;
            String id = e.getText();
            result.add(id);
        }
        return result;                
    }

    public List<String> getCommunicationIDsForSpeaker(String speakerID) throws JDOMException {
        return getIDs("//Communication[descendant::Person='" + speakerID + "']");                
    }
    
    public String getCommunicationIDForTranscription(String transcriptionID) throws JDOMException{
        Element commE = (Element) XPath.selectSingleNode(this, "//Transcription[@Id='" + transcriptionID + "']/ancestor::Communication[1]");
        return commE.getAttributeValue("Id");
    }
    
    Map<String, Map<String, String>> communicationMetadata;
    Map<String, Map<String, String>> speakerMetadata;
    Map<String, Map<String, String>> transcriptionMetadata;
    
    public Map<String, Map<String, String>> getCommunicationMetadataAsMap() throws JDOMException{
        if (communicationMetadata==null){
            // it is not there, so lets calculate it
            communicationMetadata = getMap("//Communication|//Setting");
        }
        return communicationMetadata;
    }
    
    public Map<String, Map<String, String>> getSpeakerMetadataAsMap() throws JDOMException{
        if (speakerMetadata==null){
            // it is not there, so lets calculate it
            speakerMetadata = getMap("//Speaker");
        }
        return speakerMetadata;
    }
    

    public Map<String, Map<String, String>> getTranscriptionMetadataAsMap() throws JDOMException{
        if (transcriptionMetadata==null){
            // it is not there, so lets calculate it
            transcriptionMetadata = getMap("//Transcription");
        }
        return transcriptionMetadata;
    }


    private Map<String, Map<String, String>> getMap(String xpathToParentElement) throws JDOMException{
        Map<String, Map<String, String>> result = new HashMap<>();
        List l = XPath.selectNodes(this, xpathToParentElement);
        for (Object o : l){
            Element e = (Element)o;
            String id = e.getAttributeValue("Id");
            List l2 = XPath.selectNodes(e, "child::Description/child::Key");
            Map<String, String> thisMap = new HashMap<>();
            for (Object o2 : l2){
                Element e2 = (Element)o2;
                String key = e2.getAttributeValue("Name");
                String value = e2.getText();
                thisMap.put(key, value);
            }
            if (e.getName().equals("Speaker")){
                String sex = e.getChildText("Sex");
                thisMap.put("Sex", sex);
            }
            result.put(id, thisMap);
        }
        return result;        
        
    }
    
    private List<String> getIDs(String xpathToIDElement) throws JDOMException{
        List<String> result = new ArrayList<>();
        List l = XPath.selectNodes(this, xpathToIDElement);
        for (Object o : l){
            Element e = (Element)o;
            String id = e.getAttributeValue("Id");
            result.add(id);
        }
        return result;        
    }
    
    
}
