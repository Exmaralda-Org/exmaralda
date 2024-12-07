package org.exmaralda.exakt.kwicSearch;

public interface Corpus {
    public CorpusIterator getCorpusIterator();

    public CorpusComponent getCorpusComponent(String componentID);

    public int getSize();
}
