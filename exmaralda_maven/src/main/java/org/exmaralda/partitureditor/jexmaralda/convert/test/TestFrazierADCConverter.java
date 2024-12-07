/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert.test;

import java.io.File;
import java.io.IOException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.FrazierADCConverter;

/**
 *
 * @author thomas.schmidt
 */
public class TestFrazierADCConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, JexmaraldaException {
        new TestFrazierADCConverter().doit();
    }
    
    String inFile = "D:\\Dropbox\\IDS\\Veranstaltungen\\EXMARaLDA-Schulung_Paris_November_2021\\MATERIAL\\test_frazier.json";

    private void doit() throws IOException, JexmaraldaException {
        FrazierADCConverter converter = new FrazierADCConverter();
        BasicTranscription bt = converter.readFrazierADCFromFile(new File(inFile)); 
        bt.writeXMLToFile(inFile.replaceAll("\\.[a-z]+", ".exb"), "none");
    }
    
    
}
