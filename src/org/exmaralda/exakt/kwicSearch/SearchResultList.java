/*
 * SearchResultList.java
 *
 * Created on 14. November 2006, 11:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.kwicSearch;

import java.util.*;
import org.jdom.Element;
import org.exmaralda.exakt.kwicSearch.XMLable;

/**
 *
 * @author thomas
 */
public class SearchResultList implements SearchResultList_Interface, XMLable {
    
    java.util.Vector<SearchResult> list;
    
    /** Creates a new instance of SearchResultList */
    public SearchResultList() {
        list = new Vector<SearchResult>();
    }

    public SearchResult getSearchResult(int index) {
        return list.elementAt(index);
    }

    public Iterator<SearchResult> getSearchResultListIterator() {
        return list.iterator();
    }

    public void addSearchResults(SearchResult[] searchResults) {
        for (int pos=0; pos<searchResults.length; pos++){
            list.add(searchResults[pos]);
        }
    }

    public int getSize() {
        return list.size();
    }

    public void addSearchResult(SearchResult searchResult) {
        list.add(searchResult);
    }

    public void addSearchResults(Vector<SearchResult> searchResults) {
        list.addAll(searchResults);
    }

    public Element toXML() {
        Element returnValue = new Element("search-result-list");
        Iterator<SearchResult> i = getSearchResultListIterator();
        while (i.hasNext()){
            SearchResult sr = i.next();
            XMLable xsr = (XMLable)sr;
            returnValue.addContent(xsr.toXML());
        }
        return returnValue;
    }
    
}
