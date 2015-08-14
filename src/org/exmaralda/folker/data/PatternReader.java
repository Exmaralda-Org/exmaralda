/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import org.jdom.*;
import org.jdom.xpath.*;

/* Big change 04-12-2012
 * allow language codes in order to differentiate between alphabets
 */

/**
 *
 * @author thomas
 */
public class PatternReader {

    Document document;

    public PatternReader(String internalPath) throws JDOMException, IOException {
        document = new org.exmaralda.common.jdomutilities.IOUtilities().readDocumentFromResource(internalPath);
    }

    public Hashtable<String,String> getAllPatterns(int level) throws JDOMException{
        return getAllPatterns(level, "default");
    }
    
    public Hashtable<String,String> getAllPatterns(int level, String languageCode) throws JDOMException{
        Hashtable<String, String> result = new Hashtable<String, String>();
        String xp = "//level[@level='" + Integer.toString(level) + "']/pattern";
        for (Object o : XPath.newInstance(xp).selectNodes(document)){
            Element e = (Element)o;
            String name = e.getAttributeValue("name");
            String pattern = resolveElement(e.getChild("regex"), languageCode);
            if (!("default".equals(languageCode))){
                String xpath = "descendant::language[@name='" + languageCode + "']/regex";
                Element regexChildOfThisLanguage = (Element) XPath.selectSingleNode(e, xpath);
                if (regexChildOfThisLanguage!=null){
                    pattern = resolveElement(regexChildOfThisLanguage, languageCode);
                }
            }
            result.put(name, pattern);
        }
        return result;
    }

    public String getPattern(int level, String name) throws JDOMException {
        return getPattern(level, name, "default");
    }

    public String getPattern(int level, String name, String languageCode) throws JDOMException{
        String xp = "//level[@level='" + Integer.toString(level) + "']/pattern[@name='" + name + "']";
        System.out.println(xp);
        Element e =  (Element) XPath.newInstance(xp).selectSingleNode(document);
        String pattern = resolveElement(e.getChild("regex"), languageCode);
        if (!("default".equals(languageCode))){
            String xpath = "descendant::language[@name='" + languageCode + "']/regex";
            System.out.println(xpath);
            Element regexChildOfThisLanguage = (Element) XPath.selectSingleNode(e, xpath);
            System.out.println("Null?");
            if (regexChildOfThisLanguage!=null){
                System.out.println("Not null");
                pattern = resolveElement(regexChildOfThisLanguage, languageCode);
            }
        }        
        return pattern;
    }

    public String resolveElement(Element e) throws JDOMException{
        return resolveElement(e, "default");
    }
    
    public String resolveElement(Element e, String languageCode) throws JDOMException{
        String result = "";
        List l = e.getContent();
        for (Object o : l){
            //System.out.println(o.toString());
            if (o instanceof org.jdom.Text){
                result+=((org.jdom.Text)o).getText();
            } else {
                Element patternRef = ((org.jdom.Element)o);
                String refName = patternRef.getAttributeValue("ref");
                //System.out.println("---" + refName);
                String xp2 = "ancestor::level/descendant::pattern[@name='" + refName + "']";
                //System.out.println("+++" + xp2);
                Element refEl = (Element) XPath.newInstance(xp2).selectSingleNode(patternRef);
                Element theRightRegexElement = refEl.getChild("regex");
                if (!("default".equals(languageCode))){
                    String xpath = "descendant::language[@name='" + languageCode + "']/regex";
                    Element regexChildOfThisLanguage = (Element) XPath.selectSingleNode(refEl, xpath);
                    if (regexChildOfThisLanguage!=null){
                        theRightRegexElement = regexChildOfThisLanguage;
                    }
                }        
                result+=resolveElement(theRightRegexElement, languageCode);
            }
        }
        return result;
    }

}
