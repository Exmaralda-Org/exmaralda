/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.convert.test;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.TabularConverter;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class TabularTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TabularTest().doit();
        } catch (SAXException | JexmaraldaException ex) {
            Logger.getLogger(TabularTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws SAXException, JexmaraldaException {
        BasicTranscription bt = new BasicTranscription("C:\\Users\\bernd\\Dropbox\\EXMARaLDA-Demokorpus\\RudiVoellerWutausbruch\\RudiVoellerWutausbruch.exb");
        TabularConverter tc = new TabularConverter();
        String[] depIDs = {"TIE1"};
        List<String[]> result = tc.BasicTranscriptionToTabular(bt, "TIE3", depIDs);
        for (String[] r : result){
            System.out.println(String.join(" // ", r));
        }
    }
    
}
