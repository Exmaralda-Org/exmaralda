/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.tei;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class TestCHATMinimalConversion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestCHATMinimalConversion().doit();
        } catch (SAXException | JexmaraldaException | FSMException | JDOMException | IOException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(TestCHATMinimalConversion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String IN = "C:\\Users\\bernd\\Dropbox\\work\\2025_GriffithCorpus\\transcripts\\1-EXB\\24\\24.exb";
    String OUT = "C:\\Users\\bernd\\Dropbox\\work\\2025_GriffithCorpus\\transcripts\\1-EXB\\24\\24.xml";
    String FSM = "C:\\exmaralda\\src\\org\\exmaralda\\partitureditor\\fsm\\xml\\CHAT_CA_UtteranceWord.xml";
    
    private void doit() throws SAXException, JexmaraldaException, FSMException, JDOMException, XSLTransformException, IOException, ParserConfigurationException, TransformerException {
        BasicTranscription bt = new BasicTranscription(IN);
        TEIConverter teiConverter = new TEIConverter();
        teiConverter.setLanguage("en");
        teiConverter.writeChatMinimalSegmentedISOTEIToFile(bt, OUT, FSM);
    }
    
}
