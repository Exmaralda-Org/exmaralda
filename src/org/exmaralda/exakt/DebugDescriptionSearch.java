/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt;

import java.io.File;
import java.io.IOException;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpus;
import org.exmaralda.exakt.search.RegularExpressionSearchParameters;
import org.exmaralda.exakt.search.Search;
import org.exmaralda.exakt.search.SearchResultInterface;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class DebugDescriptionSearch {

    /**
     * @param args the command line arguments
     */
    String[][] ADDITIONAL_DATA_LOCATORS = {{"tier-id", "../../@id"},{"speaker", "../../@speaker"},{"start", "@s"}};
    //String INPUT = "S:\\TP-Z2\\DATEN\\MAPTASK\\0.1\\MAPTASK.coma";
    String INPUT = "S:\\TP-Z2\\DATEN\\EXMARaLDA_DemoKorpus\\EXMARaLDA_DemoKorpus.coma";

    Thread searchThread;
    Search search;
    Thread searchThread2;
    Search search2;
    
    public static void main(String[] args) {
        try {
            new DebugDescriptionSearch().doit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void doit() throws JDOMException, IOException{
        COMACorpus corpus = new COMACorpus();
        corpus.readCorpus(new File(INPUT));
        String xpath = "//segmented-tier[@type='d' and @category='" + "nn" + "']/segmentation/ats";
        corpus.setXPathToSearchableSegment(xpath);
        String searchExpression = ".+";
        RegularExpressionSearchParameters parameters = new RegularExpressionSearchParameters(searchExpression, ADDITIONAL_DATA_LOCATORS);
        search = new Search(corpus,parameters);
        final Runnable doUpdateSearchResult = new Runnable() {
             public void run() {
                 output();
             }
         };
        searchThread = new Thread(){
            @Override
            public void run(){
                search.doSearch();
                javax.swing.SwingUtilities.invokeLater(doUpdateSearchResult);
            }
        };
        
        //search.doSearch();
        searchThread.start();

        /*search2 = new Search(corpus,parameters);
        searchThread2 = new Thread(){
            @Override
            public void run(){
                search2.doSearch();
                javax.swing.SwingUtilities.invokeLater(doUpdateSearchResult);
            }
        };

        //search.doSearch();
        searchThread2.start();*/

    }

    void output(){
        System.out.println("====" + search.getSearchResult().size());
        /*for (SearchResultInterface sri : search.getSearchResult()){
            System.out.println(sri.getMatchTextAsString());
        }*/
        /*for (SearchResultInterface sri : search2.getSearchResult()){
            System.out.println(sri.getMatchTextAsString());
        }*/

    }
}
