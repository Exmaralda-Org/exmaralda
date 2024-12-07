/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.tokenlist;

import java.io.IOException;
import java.util.List;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpusInterface;
import org.exmaralda.exakt.search.CorpusInterface;

/**
 *
 * @author thomas
 */
public abstract class AbstractTokenList {

    public static final int UNSORTED = 0;
    public static final int ALPHABETICALLY_SORTED = 1;
    public static final int ALPHABETICALLY_REVERSE_SORTED = 2;
    public static final int FREQUENCY_SORTED = 3;

    String name;
    CorpusInterface corpus;

    public CorpusInterface getCorpus(){
        return corpus;
    }

    abstract public int getNumberOfTokens();

    abstract public AbstractTokenList getTokensWithPrefix(String prefix);

    abstract public AbstractTokenList getTokensBetweenPrefixes(String prefix1, String prefix2);

    abstract public void read(Object source) throws IOException;

    abstract public void write(Object target) throws IOException;

    abstract public boolean addToken(String token);

    abstract public int removeToken(String token);

    abstract public int getTokenCount(String token);

    abstract public void setTokenCount(String token, int count);

    abstract public List<String> getTokens(int howtosort);

    abstract public boolean readWordsFromExmaraldaCorpus(COMACorpusInterface finalCorpus) throws Exception;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalTokenCount() {
        int count = 0;
        for (String s : getTokens(AbstractTokenList.UNSORTED)){
            count+=getTokenCount(s);
        }
        return count;
    }









}
