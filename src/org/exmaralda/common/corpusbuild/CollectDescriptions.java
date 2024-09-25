/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.common.corpusbuild;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author bernd
 */
public class CollectDescriptions {
    
    Document comaDocument;
    
    public CollectDescriptions(Document comaDocument) {
        this.comaDocument = comaDocument;
    }
    
    public List<String> collectCommunicationKeyNames(){
        return collectKeyNames("//Communication/Description/Key");
    }

    public List<String> collectSpeakerKeyNames(){
        return collectKeyNames("//Speaker/Description/Key");
    }
    
    public List<String> collectCommunicationKeyValues(String keyName){
        return collectKeyValues("//Communication/Description/Key[@Name='" + keyName + "']");
    }
    
    private List<String> collectKeyNames(String xpath){
        List<String> result = new ArrayList<>();
        try {
            Set<String> map = new HashSet<>();
            List l = XPath.selectNodes(comaDocument, xpath);
            for (Object o : l){
                Element e = (Element)o;
                String keyName = e.getAttributeValue("Name");
                map.add(keyName);
            }
            for (String key : map){
                result.add(key);
            }
        } catch (JDOMException ex) {
            Logger.getLogger(CollectDescriptions.class.getName()).log(Level.SEVERE, null, ex);
        }
        Collections.sort(result);
        return result;            
    }
    
    private List<String> collectKeyValues(String xpath){
        List<String> result = new ArrayList<>();
        try {
            Set<String> map = new HashSet<>();
            List l = XPath.selectNodes(comaDocument, xpath);
            for (Object o : l){
                Element e = (Element)o;
                String keyValue = e.getText();
                map.add(keyValue);
            }
            for (String key : map){
                result.add(key);
            }
        } catch (JDOMException ex) {
            Logger.getLogger(CollectDescriptions.class.getName()).log(Level.SEVERE, null, ex);
        }
        Collections.sort(result);
        return result;            
        
    }
    
    
}
