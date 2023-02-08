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
    HashMap<String, Integer> frequencyMap = new HashMap<>();
    HashMap<FormLanguagePair, Integer> frequencyMapWithLanguage = new HashMap<>();
    
    static String DEFAULT_LANGUAGE = "deu";

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
            String nForm = n.getAttributeValue("corr");
            int freq = Integer.parseInt(n.getAttributeValue("freq"));
            if (!(frequencyMap.containsKey(nForm))){
                frequencyMap.put(nForm, 0);
            }
            frequencyMap.put(nForm, frequencyMap.get(nForm) + freq);

            String lang = n.getAttributeValue("lang");
            if (lang==null){
                lang = DEFAULT_LANGUAGE;
            }
            FormLanguagePair formLanguage = new FormLanguagePair(nForm, lang);
            frequencyMapWithLanguage.put(formLanguage, freq);
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
        ArrayList<FormAndFrequency> v = new ArrayList<>();
        for (String lemma : frequencyMap.keySet()){
            v.add(new FormAndFrequency(lemma, frequencyMap.get(lemma)));
        }
        Collections.sort(v, (FormAndFrequency o1, FormAndFrequency o2) -> {
            //System.out.println(o1.frequency + " --- " + o2.frequency);
            if (o1.frequency>o2.frequency) return -1;
            if (o1.frequency<o2.frequency) return +1;
            return (o1.form.compareTo(o2.form));            
        });

        ArrayList<String> result = new ArrayList<>();
        for (FormAndFrequency faf : v){
            result.add(faf.form);
        }        
        
        return result;
        
    }
    
    List<FormLanguagePair> getCandidateFormsWithLanguage() {
        ArrayList<FormLanguagePairAndFrequency> v = new ArrayList<>();
        for (FormLanguagePair form : frequencyMapWithLanguage.keySet()){
            v.add(new FormLanguagePairAndFrequency(form, frequencyMapWithLanguage.get(form)));
        }
        Collections.sort(v, new Comparator<FormLanguagePairAndFrequency>(){
            @Override
            public int compare(FormLanguagePairAndFrequency o1, FormLanguagePairAndFrequency o2) {
                //System.out.println(o1.frequency + " --- " + o2.frequency);
                if (o1.frequency>o2.frequency) return -1;
                if (o1.frequency<o2.frequency) return +1;
                return (o1.formLanguagePair.compareTo(o2.formLanguagePair)); 
            }            
        });

        ArrayList<FormLanguagePair> result = new ArrayList<>();
        for (FormLanguagePairAndFrequency faf : v){
            result.add(faf.formLanguagePair);
        }                
        return result;
        
    }
    
}
