/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import java.io.IOException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;

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
    
    String inFile = "D:\\Dropbox\\IDS\\Veranstaltungen\\EXMARaLDA-Schulung_Paris_November_2021\\MATERIAL\\in-den-gaengen-de-fr-in-den-gaengen-ad-fps_25.adc";

    private void doit() throws IOException, JexmaraldaException {
        FrazierADCConverter converter = new FrazierADCConverter();
        BasicTranscription bt = converter.readFrazierADCFromFile(new File(inFile)); 
        bt.writeXMLToFile(inFile.replaceAll("\\.adc", ".exb"), "none");
    }
    
    
}
