/*
 * AbstractKWICSearch.java
 *
 * Created on 13. November 2006, 15:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.kwicSearch;

import java.util.*;

public class KWICSearch implements KWICSearch_Interface {
    
    private Corpus corpus;
    private SearchExpression searchExpression;
    private SearchResultList_Interface searchResultList;
    private Vector<KWICSearchProgressListener> listenerList = new Vector<KWICSearchProgressListener>();
    private int corpusSize = 0;
    private long timeForLastSearch = 0;
    
    
    /** Creates a new instance of AbstractKWICSearch */
    public KWICSearch() {
        searchResultList = new SearchResultList();
    }

    public void setCorpus(Corpus c) {
        corpus = c;
        corpusSize = c.getSize();
    }

    public void setSearchExpression(SearchExpression se) {
        searchExpression = se;
    }

    public SearchResultList_Interface getSearchResultList() {
        return searchResultList;
    }

    public SearchExpression getSearchExpression() {
        return searchExpression;
    }

    public Corpus getCorpus() {
        return corpus;
    }

    public void addKWICSearchProgressListener(KWICSearchProgressListener l) {
         listenerList.addElement(l);        
    }
    
    protected void fireSearchProgress(double percent){        
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size()-1; i>=0; i-=1) {
            listenerList.elementAt(i).searchProgressChanged(percent);
         }                
    }
    
    public long getTimeForLastSearch(){
        return timeForLastSearch;
    }

    public void search() {
        CorpusIterator ci = this.getCorpus().getCorpusIterator();
        int count = 0;
        long before = System.currentTimeMillis();
        while (ci.hasNext()){
            CorpusComponent cc = ci.next();
            KWICSearchableIterator ksi = cc.getKWICSearchableIterator();
            while (ksi.hasNext()){
                KWICSearchableComponent ksc = ksi.next();                
                SearchResult[] searchResults = ksc.search(this.getSearchExpression());
                this.getSearchResultList().addSearchResults(searchResults);
            }
            count++;
            this.fireSearchProgress((double)count/(double)corpusSize);
        }
        long after = System.currentTimeMillis();
        timeForLastSearch = after - before;
    }
    
    
}
