/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.convert.test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.CHATConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.SAXException;

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
        } catch (IOException | JexmaraldaException | SAXException | FSMException | JDOMException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(CHATTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private void doit() throws IOException, JexmaraldaException, SAXException, FSMException, JDOMException, ParserConfigurationException, XSLTransformException, TransformerException {
        CHATConverter cc = new CHATConverter(new File("C:\\Users\\bernd\\Dropbox\\work\\2025_GriffithCorpus\\transcripts\\0-CHA\\07.cha"));
        BasicTranscription exb = cc.convert();
        exb.writeXMLToFile("C:\\Users\\bernd\\Dropbox\\work\\2025_GriffithCorpus\\transcripts\\1-EXB\\07.exb", "none");
        
        TEIConverter teiConverter = new TEIConverter();
        String customFSM = "C:\\exmaralda\\src\\org\\exmaralda\\partitureditor\\fsm\\xml\\CHAT_CA_UtteranceWord.xml";
        teiConverter.writeChatMinimalSegmentedISOTEIToFile(exb, "C:\\Users\\bernd\\Dropbox\\work\\2025_GriffithCorpus\\transcripts\\1-EXB\\07.xml" , customFSM);
    }
    
}
