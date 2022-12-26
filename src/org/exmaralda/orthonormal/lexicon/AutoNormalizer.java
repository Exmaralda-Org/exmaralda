/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class AutoNormalizer {

    public int MIN_AUTO_FREQUENCY = 5;
    
    public boolean OVERWRITE_EXISTING = false;

    LexiconInterface lexicon;
    
    private final List<SearchListenerInterface> listenerList = new ArrayList<>();
    
    static final Namespace teiNamespace = Namespace.getNamespace("tei", "http://www.tei-c.org/ns/1.0");
    
    
    public AutoNormalizer(LexiconInterface l) {
        lexicon = l;
    }

    public void addSearchListener(SearchListenerInterface sli) {
        listenerList.add(sli);
    }

    
    public void normalize(Element contribution, HashMap<String,String> normalizationMap) {
        String name = contribution.getName();
        String attributeName = "n";
        ElementFilter elementFilter = new ElementFilter("w");
        if (!(name.equals("contribution"))){
            elementFilter = new ElementFilter("tei:w", teiNamespace);
            attributeName = "norm";
        }
        
        
        Iterator i = contribution.getDescendants(elementFilter);
        
        while (i.hasNext()){
            Element word = (Element)(i.next());
            String wordText = WordUtilities.getWordText(word);
            if (normalizationMap.containsKey(wordText)){
                word.setAttribute(attributeName, normalizationMap.get(wordText));
            }
        }
        
    }

    public int normalize(Element contribution) throws LexiconException {
        String name = contribution.getName();
        String attributeName = "n";
        ElementFilter elementFilter = new ElementFilter("w");
        if (!(name.equals("contribution"))){
            elementFilter = new ElementFilter("w", teiNamespace);
            attributeName = "norm";
        }

        Iterator i = contribution.getDescendants(elementFilter);
        int count=0;
        while (i.hasNext()){
            Element word = (Element)(i.next());
            String wordText = WordUtilities.getWordText(word);
            //System.out.println("Looking up " + wordText);
            //System.out.println("??? " + wordText);
            if (word.getAttribute(attributeName)==null || OVERWRITE_EXISTING){
                if (OVERWRITE_EXISTING){
                    word.removeAttribute(attributeName);
                }
                boolean lookupGotResult = false;
                // 1. lookup the form in the lexicon
                List<String> forms = lexicon.getCandidateForms(wordText);
                if (!forms.isEmpty()){
                    String form = forms.get(0);
                    //System.out.println(form + " " + lexicon.getFrequency(wordText, form));
                    if (!(form.equals(word.getText()))){
                       if ((!lexicon.hasFrequencyInformation()) 
                               || ((lexicon.hasFrequencyInformation()) 
                               && (lexicon.getFrequency(wordText, form)>=MIN_AUTO_FREQUENCY))){
                            word.setAttribute(attributeName, form);
                            //System.out.println("Set " + form);
                            lookupGotResult = true;
                            count++;
                        }
                    }
                }

                // 2. if you can, try the capitals only list
                if (!(lookupGotResult) && lexicon.hasCapitalInformation() && wordText.matches("[a-zäöüß].+")){
                    try {
                        String capitalWord = wordText.substring(0,1).toUpperCase() + wordText.substring(1);
                        if (lexicon.isCapitalOnly(capitalWord)){
                            word.setAttribute(attributeName, capitalWord);
                            lookupGotResult = true;
                            count++;                        
                        }
                    } catch (LexiconException ex) {
                        // should not get here...
                        System.out.println(ex.getLocalizedMessage());
                    }
                }
            }
        }
        return count;        
    }




    public int normalize(Document document) throws LexiconException, JDOMException{
        String name = document.getRootElement().getName();
        
        int all = 0;
        List l;
        if (name.equals("folker-transcription")){
            l = document.getRootElement().getChildren("contribution");       
        } else {
            XPath xp = XPath.newInstance("descendant::tei:u");
            xp.addNamespace(teiNamespace);
            l = xp.selectNodes(document);
        }
        int count = 0;
        for (Object o : l){
            fireProgress((double)count / (double)l.size(), "Autonormalizing contribution " + Integer.toString(count+1) + " of " + Integer.toString(l.size()));
            Element contribution = (Element)o;
            all+=normalize(contribution);
            count++;
        }
        fireProgress(1.0, "Auto normalization completed");
        return all;
    }
    
    
    protected void fireProgress(double progress, String message) {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size() - 1; i >= 0; i -= 1) {
            SearchEvent se = new SearchEvent(SearchEvent.SEARCH_PROGRESS_CHANGED, progress, message);
            listenerList.get(i).processSearchEvent(se);
        }
    }
    
    
}
