package org.exmaralda.exakt.kwicSearch;

public interface SearchResultList_Interface {
    
    public java.util.Iterator<SearchResult> getSearchResultListIterator();

    public SearchResult getSearchResult(int index);

    public int getSize();

    public void addSearchResults(SearchResult[] searchResults);
    
    public void addSearchResult(SearchResult searchResult);
    
    public void addSearchResults(java.util.Vector<SearchResult> searchResults);
}
