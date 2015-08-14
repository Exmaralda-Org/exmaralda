/*
 * AbstractSearch.java
 *
 * Created on 5. Januar 2007, 17:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import java.util.Vector;

/**
 *
 * @author thomas
 */
public class Search {
    
    private SearchResultList searchResult = new SearchResultList();
    private Vector<SearchListenerInterface> listenerList = new Vector<SearchListenerInterface>();    
    private SearchParametersInterface searchParameters;
    private CorpusInterface corpus;
    private int MAX_NUMBER_OF_SEARCH_RESULTS = 10000;
    double timeForLastSearch;
    
    
    /** Creates a new instance of AbstractSearch */
    public Search(CorpusInterface c, SearchParametersInterface sp) {
        corpus = c;
        searchParameters = sp;
    }
    
    public void setMaxNumberOfSearchResults(int n){
        MAX_NUMBER_OF_SEARCH_RESULTS = n;
    }
    
    public CorpusInterface getCorpus(){
        return corpus;        
    }
    
    public SearchParametersInterface getSearchParameters(){
        return searchParameters;        
    }
    
    public void addSearchListener(SearchListenerInterface sli){
         listenerList.addElement(sli);        
    }
    
    public SearchResultList getSearchResult(){
        return searchResult;
    }
    
    public double getTimeForLastSearch(){
        return timeForLastSearch;
    }
    
    
    public void doSearch(){
        long start = new java.util.Date().getTime();
        CorpusInterface c = getCorpus();
        int allSegments = c.getNumberOfSearchableSegments();
        int countSegments = 0;        
        int countMatches = 0;
        int countComponents = 0;
        c.reset();

        // !!!!!!!!!!!!!
        searchResult = new SearchResultList();
        while(c.hasNext()){
            CorpusComponentInterface cc = c.next();
            String id = cc.getIdentifier();
            //System.out.println("Searching... " + id);
            final SearchResultList thisResult = cc.search(getSearchParameters());
            //System.out.println("Found " + thisResult.size());
            countSegments+=cc.getNumberOfSearchableSegments();
            int matches = thisResult.size();
            countMatches+=matches;
            countComponents++;
            searchResult.addSearchResults(thisResult);
            fireSearchProgress((double)countComponents/(double)c.getNumberOfCorpusComponents(),  countMatches + " matches", id);
            if ((MAX_NUMBER_OF_SEARCH_RESULTS>0) && (countMatches>MAX_NUMBER_OF_SEARCH_RESULTS)){
                fireSearchStopped((double)countSegments/(double)allSegments,  "Search stopped at " + countMatches + " results.");
                break;
            }
        }
        long end = new java.util.Date().getTime();
        this.timeForLastSearch = (end-start)/1000.0;
        fireSearchCompleted();
    }
    
    protected void fireSearchProgress(double progress, String message, String id){
        String[] data = {message,id};
        SearchEvent se = new SearchEvent(SearchEvent.SEARCH_PROGRESS_CHANGED, progress, data);
        fireSearchEvent(se);
    }
    
    protected void fireSearchStopped(double progress, String message){
        // Process the listeners last to first, notifying
        // those that are interested in this event
        SearchEvent se = new SearchEvent(SearchEvent.SEARCH_STOPPED, progress, message);
        fireSearchEvent(se);
    }
    
    protected void fireSearchCompleted(){
        SearchEvent se = new SearchEvent(SearchEvent.SEARCH_COMPLETED, 1.0, searchResult);        
    }
    
    protected void fireSearchEvent(SearchEvent se){
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size()-1; i>=0; i-=1) {
            listenerList.elementAt(i).processSearchEvent(se);
         }                        
    }
}
