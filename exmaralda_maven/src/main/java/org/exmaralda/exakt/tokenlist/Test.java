/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.tokenlist;

import java.io.File;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpus;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;

/**
 *
 * @author thomas
 */
public class Test implements SearchListenerInterface {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Test().foit();

    }

    public void processSearchEvent(SearchEvent se) {
        System.out.println(se.getProgress() + " " + se.getData());
    }

    private void foit() {
        try {
            COMACorpus c = new COMACorpus();
            //c.readCorpus(new File("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\EXMARaLDA_DemoKorpus\\EXMARaLDA_DemoKorpus.coma"));
            c.addSearchListener(this);
            //c.readCorpus(new File("T:\\TP-E5\\0.9_ohne_Audio\\ENDFAS_SKOBI.coma"));
            c.readCorpus(new File("S:\\TP-Z2\\DATEN\\MAPTASK\\0.3\\MAPTASK.coma"));

            //c.index();
            System.out.println("Unique Speaker Identifier: " + c.getUniqueSpeakerIdentifier());
            String[] sigles={"Dav"};
            HashtableTokenList tl = new HashtableTokenList();
            tl.addSearchListener(this);
            tl.readWordsFromExmaraldaCorpus(c, sigles);
            //tl.readWordsFromExmaraldaCorpus(c);
            tl.write(new File("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\MAPTASK_tokens.xml"));
            AbstractTokenList etokens = tl.getTokensWithPrefix("e");
            System.out.println(tl.getTotalTokenCount() + " TOKENS!!!");
            System.out.println("der " + tl.getTokenCount("der"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
