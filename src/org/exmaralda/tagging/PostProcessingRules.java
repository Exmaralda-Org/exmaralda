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
        if (path.startsWith("/")){
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
    
    
    
}
