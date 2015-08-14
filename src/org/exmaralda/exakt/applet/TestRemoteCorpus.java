/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.applet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.exmaralda.exakt.exmaraldaSearch.COMARemoteCorpus;
import org.exmaralda.exakt.kwicSearch.SearchResult;
import org.exmaralda.exakt.search.RegularExpressionSearchParameters;
import org.exmaralda.exakt.search.Search;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.exakt.search.SearchParametersInterface;
import org.exmaralda.exakt.search.SearchResultInterface;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class TestRemoteCorpus implements SearchListenerInterface {

    String url = "http://www1.uni-hamburg.de/exmaralda/files/demokorpus/EXMARaLDA_DemoKorpus.coma";
    String SEARCH_EXPRESSION = "das";
    String[][] ADDITIONAL_DATA_LOCATORS = {{"tier-id", "../../@id"},{"speaker", "../../@speaker"},{"start", "@s"}};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestRemoteCorpus().doit();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    private void doit() throws JDOMException, IOException, URISyntaxException {
        COMARemoteCorpus corpus = new COMARemoteCorpus();
        corpus.readCorpus(new URL(url));
        String segmentationName = "SpeakerContribution_Event";
        String xpath = "//segmentation[@name='" + segmentationName + "']/ts";
        String searchExpression = SEARCH_EXPRESSION;
        SearchParametersInterface parameters = new RegularExpressionSearchParameters(searchExpression, ADDITIONAL_DATA_LOCATORS);

        Search search = new Search(corpus, parameters);
        search.addSearchListener(this);
        search.doSearch();

        for (SearchResultInterface sr : search.getSearchResult()){
            System.out.println(sr.getLeftContextAsString() + " /// " + sr.getMatchTextAsString());
        }
    }

    public void processSearchEvent(SearchEvent se) {
        System.out.println("" + se.toString());
    }

}
