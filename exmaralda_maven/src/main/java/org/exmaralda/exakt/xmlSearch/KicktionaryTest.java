/*
 * Test.java
 *
 * Created on 13. November 2006, 17:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.xmlSearch;

import org.exmaralda.exakt.kwicSearch.KWICSearch;
import org.exmaralda.exakt.kwicSearch.RegularSearchExpression;
import org.exmaralda.exakt.kwicSearch.SearchResultList;
import org.exmaralda.exakt.utilities.FileIO;

/**
 *
 * @author thomas
 */
public class KicktionaryTest implements org.exmaralda.exakt.kwicSearch.KWICSearchProgressListener {
    
    /** Creates a new instance of Test */
    public KicktionaryTest() {
    }
    
    void doit(){
        try {
            java.net.Authenticator.setDefault(new org.exmaralda.exakt.utilities.KicktionaryAuthenticator());
            XMLURLListCorpus flc = new XMLURLListCorpus("//s", "../@pc-id");
            flc.readFromLocalFile("D:\\KICKTIONARY\\corpus\\EXAKT_Remote_Corpus_de.txt","//TEI.2/@pc-key");
            KWICSearch ks = new KWICSearch();  
            ks.addKWICSearchProgressListener(this);
            ks.setCorpus(flc);
            RegularSearchExpression se = new RegularSearchExpression("[Bb]all");
            ks.setSearchExpression(se);
            ks.search();
            System.out.println(ks.getTimeForLastSearch() + " miliseconds is what it took me.");
            org.exmaralda.exakt.kwicSearch.SearchResultList_Interface l = ks.getSearchResultList();
            org.jdom.Document out = new org.jdom.Document();
            out.setRootElement(((SearchResultList)l).toXML());
            FileIO.writeDocumentToLocalFile("d:\\out2.xml",out);
        } catch (XMLSearchException ex) {
            ex.printStackTrace();
        } catch (java.io.IOException ex){
            ex.printStackTrace();
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         KicktionaryTest test = new KicktionaryTest();
         test.doit();
    }

    public void searchProgressChanged(double progressPercentage) {
        System.out.println("PROGRESS: " + progressPercentage);
    }
    
}
