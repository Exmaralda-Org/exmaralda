/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.common.corpusbuild;

import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class TestTEIMerger {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestTEIMerger().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(TestTEIMerger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestTEIMerger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TestTEIMerger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String SEG = "C:\\Users\\Schmidt\\Desktop\\EXMARaLDA-Demokorpus\\Arbeitsamt\\Helge_Schneider_Arbeitsamt_s.exs";
    String FLAT = "SpeakerContribution_Event";
    String DEEP = "SpeakerContribution_Utterance_Word";
    
    private void doit() throws JDOMException, IOException, Exception {
        Document segmentedTranscription = FileIO.readDocumentFromLocalFile(SEG);
        Vector v = TEIMerger.TEIMerge(segmentedTranscription, DEEP, FLAT, true);
        String outDocString = "<x>";
        for (Object o : v){
            outDocString+=IOUtilities.elementToString((Element)o);
        }
        outDocString+="</x>";
        System.out.println(outDocString);
    }
    
}
