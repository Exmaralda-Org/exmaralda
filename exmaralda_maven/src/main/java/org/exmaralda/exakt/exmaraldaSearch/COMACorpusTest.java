/*
 * COMACorpusTest.java
 *
 * Created on 8. Januar 2007, 12:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch;

import java.io.*;
import org.jdom.JDOMException;
import java.util.*;
import org.jdom.*;
import org.exmaralda.exakt.search.AbstractXMLFileListCorpus;
import org.exmaralda.exakt.search.Search;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.exakt.search.SearchResultInterface;
import org.exmaralda.exakt.search.SearchResultList;
import org.exmaralda.exakt.search.SimpleSearchResult;
import org.exmaralda.exakt.search.XPathSearchParameters;

/**
 *
 * @author thomas
 */
public class COMACorpusTest implements SearchListenerInterface {
    
    //String CORPUS_FILE = "S:\\TP-Z2\\DATEN\\K5\\0.5\\K5_COMA_Corpus.xml";
    //String CORPUS_FILE = "D:\\KICKTIONARY\\corpus\\EXAKT_Corpus_de.txt";
    //String CORPUS_FILE = "S:\\TP-Z2\\DATEN\\K2\\0.5\\K2_COMA_Corpus.xml";
    String CORPUS_FILE = "D:\\Zecke\\DemoKorpus\\Coma_Corpus.xml";
    //String SEARCH_EXPRESSION = "fick";
    //String SEARCH_EXPRESSION = "[Bb]all";
    //String SEARCH_EXPRESSION = "das";
    //String SEARCH_EXPRESSION = "//ts[@n='HIAT:w' and string-length(text())<3]";
    String SEARCH_EXPRESSION = "//ts[@n='HIAT:u' and count(ts[@n='HIAT:w'])<3]";
    String[][] ADDITIONAL_DATA_LOCATORS = 
        {
          {"tier-id", "../../@id"}, 
          {"speaker", "../../@speaker"},
          {"display", "../../@display-name"},
          {"start", "@s"},
        };
    /*String[][] ADDITIONAL_DATA_LOCATORS = 
        {
          {"parent", ".."}, 
        };*/
    /** Creates a new instance of COMACorpusTest */
    public COMACorpusTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        COMACorpusTest cct = new COMACorpusTest();
        cct.doit();
    }

    public void doit(){
        try {
            // READ CORPUS
            AbstractXMLFileListCorpus cc = new COMACorpus();
            //AbstractXMLFileListCorpus cc = new SimpleXMLFileListCorpus("//p");
            //AbstractXMLFileListCorpus cc = new SimpleXMLFileListCorpus("//w");
            //cc.setXPathToSearchableSegment("//ts[@n='HIAT:u']");
            cc.addSearchListener(this);
            File file = new File(CORPUS_FILE);
            cc.readCorpus(file);
            cc.setXPathToSearchableSegment("//segmentation[@name='SpeakerContribution_Utterance_Word']/ts");
            System.out.println(cc.getNumberOfSearchableSegments() + " searchable segments");
            System.out.println(cc.getNumberOfCorpusComponents() + " transcriptions");
            
            // INIT SEARCH PARAMETERS
            //RegularExpressionSearchParameters resp = new RegularExpressionSearchParameters(SEARCH_EXPRESSION, ADDITIONAL_DATA_LOCATORS);
            //resp.setContextLimit(100);
            XPathSearchParameters resp = new XPathSearchParameters(SEARCH_EXPRESSION, ADDITIONAL_DATA_LOCATORS);
            
            // DO SEARCH
            Search search = new Search(cc,resp);
            search.addSearchListener(this);
            System.out.println("Searching " + resp.getSearchExpressionAsString());
            search.doSearch();
            System.out.println("Search time: " + search.getTimeForLastSearch());

            SearchResultList v = search.getSearchResult();
            for (SearchResultInterface sri : v){
                SimpleSearchResult result = (SimpleSearchResult)sri;
                String transcriptionLocator = (String)(result.getSearchableSegmentLocator().getCorpusComponentLocator());
                String speakerID = result.getAdditionalData()[1];
                //System.out.println(speakerID + "+++" + transcriptionLocator);
                String name = ((COMACorpus)cc).getCommunicationData(transcriptionLocator, "[Name]");                
                String trname = ((COMACorpus)cc).getSpeakerData(transcriptionLocator, speakerID, "transcription-name");                
                String age = ((COMACorpus)cc).getSpeakerData(transcriptionLocator, speakerID, "Age");                
                String sigle = ((COMACorpus)cc).getSpeakerData(transcriptionLocator, speakerID, "[Sigle]");                
                System.out.println(name + " / " + trname + " / " + age + " / " + sigle);
            }


            // WRITE RESULT
            Document d = new Document();
            d.setRootElement(v.toXML());
            org.exmaralda.exakt.utilities.FileIO.writeDocumentToLocalFile("d:\\out.xml",d);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }        
    }

    public void processSearchEvent(SearchEvent se) {
        //System.out.println((String)(se.getData()) + " " + se.getProgress()*100 +"%");
    }
}
