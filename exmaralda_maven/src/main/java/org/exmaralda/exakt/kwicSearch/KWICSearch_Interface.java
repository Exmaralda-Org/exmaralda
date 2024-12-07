package org.exmaralda.exakt.kwicSearch;

public interface KWICSearch_Interface {
    /**
     * Set the corpus on which the KWIC search is to be carried out
     */
    public void setCorpus(Corpus c);

    public void setSearchExpression(SearchExpression se);

    public void search();

    public SearchResultList_Interface getSearchResultList();

    public SearchExpression getSearchExpression();

    public Corpus getCorpus();

    public void addKWICSearchProgressListener(KWICSearchProgressListener l);
}
