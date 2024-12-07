package org.exmaralda.exakt.kwicSearch;

public interface SearchResult {
    public String getLeftContextAsString();

    public String getMatchTextAsString();

    public String getRightContextAsString();

    public String getCorpusComponentID();

    public String getKWICSearchableComponentID();

    public String[] getPrecedingAdditionalAttributeValues();

    public String[] getFollowingAdditionalAttributeValues();
}
