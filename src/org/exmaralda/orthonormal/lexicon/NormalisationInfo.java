/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.jdom.Element;

/**
 *
 * @author Schmidt
 */
public class NormalisationInfo {
    
    String form;
    HashMap<String, Integer> frequencyMap = new HashMap<String, Integer>();

    public NormalisationInfo(String form) {
        this.form = form;
    }

    NormalisationInfo(Element e) {
        /* <entry form="schätze">
            <n corr="Schätze" freq="5"/>
            <n corr="schätzen" freq="1"/>
            <n corr="schätze" freq="10"/>
        </entry> */
        form = e.getAttributeValue("form");
        for (Object o : e.getChildren("n")){
            Element n = (Element)o;
            frequencyMap.put(n.getAttributeValue("corr"), Integer.parseInt(n.getAttributeValue("freq")));
        }
        
    }
      
    public void put(String correspondingForm){
        if (!frequencyMap.containsKey(correspondingForm)){
            frequencyMap.put(correspondingForm, 0);
        }
        frequencyMap.put(correspondingForm, frequencyMap.get(correspondingForm)+1);
    }

    public Element toJDOMElement() {
        Element entry = new Element("entry");
        entry.setAttribute("form", form);
        for (String correspondingForm : frequencyMap.keySet()){
            Element n = new Element("n");
            n.setAttribute("corr", correspondingForm);
            n.setAttribute("freq", frequencyMap.get(correspondingForm).toString()); 
            entry.addContent(n);
        }        
        return entry;
    }

    public int getFrequency(String correspondingForm) {
        if (frequencyMap.containsKey(correspondingForm)){
            return frequencyMap.get(correspondingForm);
        }
        return 0;
    }

    List<String> getCandidateForms() {
        ArrayList<FormAndFrequency> v = new ArrayList<FormAndFrequency>();
        for (String lemma : frequencyMap.keySet()){
            v.add(new FormAndFrequency(lemma, frequencyMap.get(lemma).intValue()));
        }
        Collections.sort(v, new Comparator<FormAndFrequency>(){
            @Override
            public int compare(FormAndFrequency o1, FormAndFrequency o2) {
                if (o1.frequency>o2.frequency) return -1;
                if (o1.frequency<o2.frequency) return +1;
                return (o1.form.compareTo(o2.form));
            }            
        });

        ArrayList<String> result = new ArrayList<String>();
        for (FormAndFrequency faf : v){
            result.add(faf.form);
        }        
        return result;
    }
    
    
}
