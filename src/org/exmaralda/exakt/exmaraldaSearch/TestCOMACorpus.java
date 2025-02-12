/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.exakt.exmaraldaSearch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.tokenlist.AbstractTokenList;
import org.jdom.JDOMException;

/**
 *
 * @author bernd
 */
public class TestCOMACorpus {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestCOMACorpus().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(TestCOMACorpus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestCOMACorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        COMACorpus c = new COMACorpus();
        c.readCorpus(new File("C:\\exmaralda-demo-corpus\\src\\main\\java\\data\\corpora\\EXMARaLDA-DemoKorpus\\EXMARaLDA-DemoKorpus.coma"));
        Set<String> speakerAttributes = c.getSpeakerAttributes();
        System.out.println(String.join("\n", speakerAttributes));
        System.out.println("\n---------------\n");
        
        AbstractTokenList availableValuesForSpeakerAttribute = c.getAvailableValuesForSpeakerAttribute("Sex*");
        System.out.println(String.join("\n", availableValuesForSpeakerAttribute.getTokens(AbstractTokenList.ALPHABETICALLY_SORTED)));
    }
    
}
