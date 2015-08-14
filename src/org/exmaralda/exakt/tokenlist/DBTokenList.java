/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.exakt.tokenlist;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.jdom.Document;
import org.jdom.Element;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpusInterface;
import org.exmaralda.exakt.exmaraldaSearch.COMADBCorpus;
import org.jdom.JDOMException;

/**
 *
 * @author Z2
 */
public class DBTokenList extends AbstractTokenList {

    private String pre = "ex_";

    HashMap<String, Integer> theTokens = new HashMap<String, Integer>();
    private ArrayList<SearchListenerInterface> listenerList = new ArrayList<SearchListenerInterface>();

    public void addSearchListener(SearchListenerInterface sli) {
        listenerList.add(sli);
    }

    protected void fireCorpusInit(double progress, String message) {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size() - 1; i >= 0; i -= 1) {
            SearchEvent se = new SearchEvent(SearchEvent.CORPUS_INIT_PROGRESS, progress, message);
            listenerList.get(i).processSearchEvent(se);
        }
    }

    public boolean readWordsFromExmaraldaCorpus(COMACorpusInterface c) throws SQLException {

        this.corpus = c;

        boolean result = false;

        String nameOfWordSegment = ((COMADBCorpus)(corpus)).getWordSegmentName();

        if (nameOfWordSegment != null) {

            Connection conn = ((COMADBCorpus)(corpus)).getConnection();

            Statement query = conn.createStatement();

            ResultSet tokensResult = query.executeQuery("SELECT cdata from ((" + pre + "segment INNER JOIN " + pre + "segmented_transcription ON " + pre + "segment.transcription_guid = " + pre + "segmented_transcription.transcription_guid) INNER JOIN " + pre + "communication ON " + pre + "segmented_transcription.communication_guid = " + pre + "communication.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "communication.corpus_guid = " + pre + "corpus.corpus_guid WHERE " + pre + "corpus.name='" + ((COMADBCorpus)(corpus)).getCorpusName() + "' AND " + pre + "segment.name = '" + nameOfWordSegment + "';");

            while (tokensResult.next()) {

                addToken(tokensResult.getString("cdata"));
                
            }

            result = true;
 
        this.setName("Word list for " + ((COMADBCorpus)(corpus)).getCorpusName());
}

        return result;
    }

    @Override
    public int getNumberOfTokens() {
        return theTokens.size();
    }

    @Override
    public AbstractTokenList getTokensWithPrefix(String prefix) {
        AbstractTokenList result = new DBTokenList();
        for (String token : theTokens.keySet()) {
            if (token.toLowerCase().startsWith(prefix.toLowerCase())) {
                result.addToken(token);
                result.setTokenCount(token, getTokenCount(token));
            }
        }
        return result;
    }

    @Override
    public AbstractTokenList getTokensBetweenPrefixes(String prefix1, String prefix2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void read(Object source) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void write(Object target) throws IOException {
        File file = (File) target;
        Document doc = new Document(new Element("token-list"));
        for (String token : getTokens(AbstractTokenList.ALPHABETICALLY_SORTED)) {
            Element tokenElement = new Element("token");
            tokenElement.setText(token);
            tokenElement.setAttribute("count", Integer.toString(getTokenCount(token)));
            doc.getRootElement().addContent(tokenElement);
        }
        IOUtilities.writeDocumentToLocalFile(file.getAbsolutePath(), doc);
    }

    @Override
    public boolean addToken(String token) {
        int newCount = getTokenCount(token) + 1;
        theTokens.put(token, newCount);
        return (newCount == 1);
    }

    @Override
    public int removeToken(String token) {
        return theTokens.remove(token);
    }

    @Override
    public int getTokenCount(String token) {
        int count = 0;
        if (theTokens.containsKey(token)) {
            count = theTokens.get(token).intValue();
        }
        return count;
    }

    @Override
    public void setTokenCount(String token, int count) {
        theTokens.put(token, count);
    }

    @Override
    public List<String> getTokens(int howtosort) {
        ArrayList<String> result = new ArrayList<String>();
        result.addAll(theTokens.keySet());
        if (howtosort == AbstractTokenList.ALPHABETICALLY_SORTED) {
            Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
        }
        return result;
    }
}
