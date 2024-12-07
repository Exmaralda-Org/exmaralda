/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.exmaralda.common.corpusbuild.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class PostProcessingRules {
    
    public static final String FOLK_RULES = "/org/exmaralda/tagging/PostProcessingRulesFOLK.xml";
    
    Document xmlDocument;
    ArrayList<PostProcessingRule> rules = new ArrayList<PostProcessingRule>();
    
    public void read(String path) throws JDOMException, IOException{
        // changed 12-07-2022 : issue #324
        //if (path.startsWith("/")){
        if (org.exmaralda.exakt.utilities.FileIO.isInternalResource(path)){
            java.io.InputStream is = getClass().getResourceAsStream(path);
            xmlDocument = FileIO.readDocumentFromInputStream(is, false);
        } else {
            xmlDocument = FileIO.readDocumentFromLocalFile(path);
        }
        for (Object o : xmlDocument.getRootElement().getChildren("rule")){
            Element e = (Element)o;
            rules.add(new PostProcessingRule(e));
        }
    }
    
    
    public int apply(Document taggedDocument) throws JDOMException{
        List l = XPath.newInstance("//w").selectNodes(taggedDocument);
        int count = 0;
        for (Object o : l){
            Element word = (Element)o;
            for (PostProcessingRule r : rules){
                count+=r.apply(word);
            }
        }
        return count;
    }
    
    public int applyISOTEI(Document taggedDocument) throws JDOMException{
        XPath xpath = XPath.newInstance("//tei:w");
        xpath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");        
        List l = xpath.selectNodes(taggedDocument);
        int count = 0;
        for (Object o : l){
            Element word = (Element)o;
            for (PostProcessingRule r : rules){
                count+=r.apply(word);
            }
        }
        return count;
    }

    public int applyELAN(Document taggedELANDocument) throws JDOMException, IOException{
        List l = XPath.newInstance("//TIER[@LINGUISTIC_TYPE_REF='Tokenization']/descendant::ANNOTATION").selectNodes(taggedELANDocument);
        int count = 0;
        System.out.println(l.size() + " tokens to post-process");
        for (Object o : l){
            Element token = (Element)o;
            for (PostProcessingRule r : rules){
                count+=r.applyELAN(token);
            }
        }
        return count;
    }
    
    
    
}
