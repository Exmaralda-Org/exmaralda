package org.exmaralda.exakt.kwicSearch;

public interface KWICSearchableComponent {
    public String getID();
    public SearchResult[] search(SearchExpression se);
}
