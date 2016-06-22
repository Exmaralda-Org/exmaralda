/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.common.corpusbuild;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
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

    String TRANS = "C:\\Users\\Schmidt\\Desktop\\EXMARaLDA-Demokorpus\\Arbeitsamt\\Helge_Schneider_Arbeitsamt.exb";
    String FLAT = "SpeakerContribution_Event";
    String DEEP = "SpeakerContribution_Utterance_Word";
    String OUT = "C:\\Users\\Schmidt\\Desktop\\TEI_OUT.xml";
    
    private void doit() throws JDOMException, IOException, Exception {
        BasicTranscription bt = new BasicTranscription(TRANS);
        TEIConverter converter = new TEIConverter();
        converter.writeHIATISOTEIToFile(bt, OUT, true);
    }
    
}
