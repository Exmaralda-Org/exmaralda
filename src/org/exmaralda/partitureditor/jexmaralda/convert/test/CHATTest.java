/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.convert.test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.CHATConverter;

/**
 *
 * @author bernd
 */
public class CHATTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new CHATTest().doit();
        } catch (IOException | JexmaraldaException ex) {
            Logger.getLogger(CHATTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, JexmaraldaException {
        CHATConverter cc = new CHATConverter(new File("C:\\Users\\bernd\\Dropbox\\work\\2025_GriffithCorpus\\transcripts\\0-CHA\\07.cha"));
        BasicTranscription exb = cc.convert();
        exb.writeXMLToFile("C:\\Users\\bernd\\Dropbox\\work\\2025_GriffithCorpus\\transcripts\\1-EXB\\07.exb", "none");
    }
    
}
