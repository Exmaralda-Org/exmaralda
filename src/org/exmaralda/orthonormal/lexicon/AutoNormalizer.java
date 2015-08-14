/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

/**
 *
 * @author thomas
 */
public class AutoNormalizer {

    public int MIN_AUTO_FREQUENCY = 5;

    LexiconInterface lexicon;
    
    private Vector<SearchListenerInterface> listenerList = new Vector<SearchListenerInterface>();
    
    
    public AutoNormalizer(LexiconInterface l) {
        lexicon = l;
    }

    public void addSearchListener(SearchListenerInterface sli) {
        listenerList.addElement(sli);
    }

    
    public void normalize(Element contribution, HashMap<String,String> normalizationMap) {
        Iterator i = contribution.getDescendants(new ElementFilter("w"));
        while (i.hasNext()){
            Element word = (Element)(i.next());
            String wordText = WordUtilities.getWordText(word);
            if (normalizationMap.containsKey(wordText)){
                word.setAttribute("n", normalizationMap.get(wordText));
            }
        }
        
    }

    public int normalize(Document document) throws LexiconException{
        List l = document.getRootElement().getChildren("contribution");       
        int all = 0;
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
    
    public int normalize(Element contribution) throws LexiconException {
        Iterator i = contribution.getDescendants(new ElementFilter("w"));
        int count=0;
        while (i.hasNext()){
            Element word = (Element)(i.next());
            String wordText = WordUtilities.getWordText(word);
            if (word.getAttribute("n")==null){
                boolean lookupGotResult = false;

                // 1. lookup the form in the lexicon
                List<String> forms = lexicon.getCandidateForms(wordText);
                if (forms.size()>0){
                    String form = forms.get(0);
                    if (!(form.equals(word.getText()))){
                       if ((!lexicon.hasFrequencyInformation()) 
                               || ((lexicon.hasFrequencyInformation()) 
                               && (lexicon.getFrequency(wordText, form)>=MIN_AUTO_FREQUENCY))){
                            word.setAttribute("n", form);
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
                            word.setAttribute("n", capitalWord);
                            lookupGotResult = true;
                            count++;                        
                        }
                    } catch (LexiconException ex) {
                        // should not get here...
                        ex.printStackTrace();
                    }
                }
            }
        }
        return count;        
    }
    
    protected void fireProgress(double progress, String message) {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size() - 1; i >= 0; i -= 1) {
            SearchEvent se = new SearchEvent(SearchEvent.SEARCH_PROGRESS_CHANGED, progress, message);
            listenerList.elementAt(i).processSearchEvent(se);
        }
    }
    
    
}
