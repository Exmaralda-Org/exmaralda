/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Element;

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
    
    
    
    
}
