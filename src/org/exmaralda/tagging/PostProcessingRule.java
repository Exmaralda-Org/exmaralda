/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.IOException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
class PostProcessingRule {
 
    
/*    <rule>
        <match field="word">[A-ZÄÖÜa-zäöüß]{0,2}\%</match>
        <set field="pos">XY</set>
    </rule> */
    
    String matchField;
    String matchRegex;
    String setField;
    String setString;

    public PostProcessingRule(Element ruleElement) {
        matchField = ruleElement.getChild("match").getAttributeValue("field");
        matchRegex = ruleElement.getChildText("match");
        setField = ruleElement.getChild("set").getAttributeValue("field");
        setString = ruleElement.getChildText("set");
    }
    
    public int apply(Element word){
        String textToBeMatched = "";
        if ("word".equals(matchField)){
            textToBeMatched = WordUtilities.getWordText(word);
            if (textToBeMatched!=null && textToBeMatched.matches(matchRegex)){
                word.setAttribute(setField, setString);
                return 1;
            } else {
                return 0;
            }  
        } else {
            // get a normalisation value anyway
            if ("n".equals(matchField)){
                if (word.getAttribute("n")!=null){
                    textToBeMatched = word.getAttributeValue(matchField);     
                } else {
                    textToBeMatched = WordUtilities.getWordText(word);                
                }
            } else {
                textToBeMatched = word.getAttributeValue(matchField);            
            } 
            
            
            if (textToBeMatched==null){
                return 0;
            }
            if (!(textToBeMatched.contains(" "))){
                if (textToBeMatched.matches(matchRegex)){
                    word.setAttribute(setField, setString);
                    return 1;
                } else {
                    return 0;
                }
            } else {
                // and this is the Kasus Knaxus!
                // several items in @n, @pos or @lemma
                String[] itemsToBeMatched = textToBeMatched.split(" ");
                String[] correspondingItems = word.getAttributeValue(setField).split(" ");
                String newValue="";
                int count=0;
                int index=0;
                for (String item : itemsToBeMatched){
                    if (item.matches(matchRegex)){
                        newValue+=setString + " ";
                        count++;
                    } else {
                        //newValue+=item + " ";
                        if (index<correspondingItems.length){
                            newValue+=correspondingItems[index] + " ";
                        } else {
                            newValue+="??? ";                            
                        }
                    }
                    index++;
                }
                word.setAttribute(setField, newValue.trim());
                return count;
            }
        }
    }
    
    
    public int applyELAN(Element token) throws JDOMException, IOException {
        /*
            <ANNOTATION>
                <REF_ANNOTATION ANNOTATION_ID="a2" ANNOTATION_REF="a1">
                    <ANNOTATION_VALUE>also</ANNOTATION_VALUE>
                </REF_ANNOTATION>
            </ANNOTATION>
        
        
            <ANNOTATION>
                <REF_ANNOTATION ANNOTATION_ID="a2_p" ANNOTATION_REF="a2">
                    <ANNOTATION_VALUE>SEDM</ANNOTATION_VALUE>
                </REF_ANNOTATION>
            </ANNOTATION>        
        */

        String textToBeMatched = token.getChild("REF_ANNOTATION").getChildText("ANNOTATION_VALUE");
        String id = token.getChild("REF_ANNOTATION").getAttributeValue("ANNOTATION_ID");
        if (textToBeMatched.matches(matchRegex)){       
            String xpath = "ancestor::ANNOTATION_DOCUMENT/descendant::TIER[@LINGUISTIC_TYPE_REF='" 
                    + this.setField
                    + "']/descendant::REF_ANNOTATION[@ANNOTATION_REF='" + id + "']/parent::ANNOTATION";
            Element elementToBeSet = (Element) XPath.selectSingleNode(token,xpath); 
            if (elementToBeSet==null){
                throw new IOException("No matching element for " + IOUtilities.elementToString(token) + " using XPath " + xpath +  "!");
            }
            elementToBeSet.getChild("REF_ANNOTATION").getChild("ANNOTATION_VALUE").setText(this.setString);
            return 1;
        }
        return 0;
    }
    
    
    
    
}
