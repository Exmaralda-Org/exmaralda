/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.cGATMinimalSegmentation;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class TestAutoNormalizer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestAutoNormalizer().doit();
        } catch (IOException | SAXException | JexmaraldaException | FSMException | JDOMException | LexiconException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(TestAutoNormalizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    String in = "C:\\Users\\bernd\\Dropbox\\work\\2023_Maribor\\2025_04_30_NORMALISATION\\TEST_DEBUG_SCHEME.exb";

    private void doit() throws IOException, SAXException, JexmaraldaException, FSMException, JDOMException, LexiconException, ParserConfigurationException, TransformerException {
        XMLLexicon lexicon = new XMLLexicon();
        lexicon.read("/org/exmaralda/orthonormal/lexicon/GOS_Normalization_Lexicon_MAY_2025.xml");
        //lexicon.read("/org/exmaralda/orthonormal/lexicon/FOLK_Normalization_Lexicon_NOV_2024.xml");
        AutoNormalizer autoNormalizer = new AutoNormalizer(lexicon);
        BasicTranscription bt = new BasicTranscription(in);
        //BasicTranscription bt = EventListTranscriptionXMLReaderWriter.readXMLAsBasicTranscription(new File("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2025_05_07_FOLK\\Hausaufgabe250502.flk"));
        AbstractSegmentation segmentationAlgo = new GenericSegmentation();
        //AbstractSegmentation segmentationAlgo = new cGATMinimalSegmentation();
        Tier normalizeBasicTranscriptionTier = autoNormalizer.normalizeBasicTranscriptionTier(bt, "TIE2", segmentationAlgo, "SpeakerContribution_Word");
        System.out.println(normalizeBasicTranscriptionTier.toXML());
        
        
        
    }
    
}
