/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
 * @author Thomas_Schmidt
 */
class CombinedPostProcessingRules {
    
    public static final String FOLK_RULES = "/org/exmaralda/tagging/PostProcessingRulesCombiFOLK.xml";
    
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
        // "Combined" Post Processing Rules : n:n
        List l2 = XPath.selectNodes(xmlDocument, "//rule");
        int count=0;
        for (Object o2 : l2){
            Element ruleElement = (Element)o2;
            Element matchToken1 = (Element) ruleElement.getChild("match").getChildren("token").get(0);
            Element matchToken2 = (Element) ruleElement.getChild("match").getChildren("token").get(1);
            Element setToken1 = (Element) ruleElement.getChild("set").getChildren("token").get(0);
            Element setToken2 = (Element) ruleElement.getChild("set").getChildren("token").get(1);

            // <w n="noch"/><w n="mal"/>
            String xp3 = "//w[@n='" + matchToken1.getText() + "' and following-sibling::w[1]/@n='" + matchToken2.getText() + "']";
            List l3 = XPath.selectNodes(taggedDocument, xp3);
            for (Object o3 : l3){
                Element wElement1 = (Element)o3;
                wElement1.setAttribute("pos", setToken1.getText());

                Element wElement2 = (Element) XPath.selectSingleNode(wElement1, "following-sibling::w[1]");
                wElement2.setAttribute("pos", setToken2.getText());
                count++;
            }

            // <w n="noch mal"/>
            String xp4 = "//w[@n='" + matchToken1.getText() + " " + matchToken2.getText() + "']";
            List l4 = XPath.selectNodes(taggedDocument, xp4);
            for (Object o4 : l4){
                Element wElement = (Element)o4;
                wElement.setAttribute("pos", setToken1.getText() + " " + setToken2.getText());                        
                count++;
            }
        }
        return count;
    }
    
    
    
}
